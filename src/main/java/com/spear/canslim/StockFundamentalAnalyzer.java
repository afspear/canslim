package com.spear.canslim;

import com.jayway.jsonpath.JsonPath;
import com.spear.canslim.math.Statistics;
import com.spear.canslim.web.Client;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StockFundamentalAnalyzer {


  private String ticker;
  private String earningsJson;
  private String financialsJson;
  private String statsJson;
  private String companyJson;

  private String earningsUrl = "/earnings";
  private String financialsUrl = "/financials";
  private String statsUrl = "/stats";
  private String companyUrl = "/company";
  private String industry;

  public static String IEXTRADINGURL = "https://api.iextrading.com/1.0";
  public static String STOCKURL = IEXTRADINGURL + "/stock/%s";
  public StockFundamentalAnalyzer(String ticker) throws IOException {
    this.ticker = ticker;

    earningsJson = getJson(earningsUrl);
    financialsJson = getJson(financialsUrl);
    statsJson = getJson(statsUrl);
    companyJson = getJson(companyUrl);
    industry = JsonPath.read(companyJson, "$.industry");

  }

  private String getJson(String endingUrl) {
    String url = String.format(STOCKURL, ticker) + endingUrl;
    return Client.getJson(url);
  }



  /**
   * Earnings per share in the latest quarter should be up at least 25 percent
   * versus the same quarter a year ago, and preferably much more.
   */
  public Optional<Double> epsPercentageGainFromAYearAgo() throws IOException {
          Number actualEPs = (Number) Optional.ofNullable(JsonPath.read(earningsJson, "$.earnings[0].actualEPS"))
                  .orElse(JsonPath.read(earningsJson, "$.earnings[0].consensusEPS"));
          Number yearAgo = JsonPath.read(earningsJson, "$.earnings[0].yearAgo");
          double percentageUp = (actualEPs.doubleValue() - yearAgo.doubleValue()) / yearAgo.doubleValue();

          return Optional.of(percentageUp) ;

  }

  //rule 4
  // Sales should be up 25 percent or more in one or more recent quarters, or at least accelerating in their percentage change for the last three quarters.
  public Double getRateOfChangeForRevenueForLastFourQuarters()  {


    System.out.println(financialsJson);

    TreeMap<LocalDate, Double> localDateNumberTreeMap = new TreeMap<>();

    for (int i = 0; i < 4; i++) {
      Number revenue = JsonPath.read(financialsJson, "$.financials[" + i + "].totalRevenue");
      String quarter = JsonPath.read(financialsJson, "$.financials[" + i + "].reportDate");
      System.out.println("iteration:" + i +",quarter: " + quarter + ",revenue:" + revenue);

      localDateNumberTreeMap.put(LocalDate.parse(quarter), revenue.doubleValue());


    }

    List<Double> values = new ArrayList<>(localDateNumberTreeMap.values());

    return Statistics.findSlope(values);

  }

  //rule 5 The after-tax profit margin in the most recent quarter should be either at or at least close to a new high and among the very best in the company’s industry.
  public Double afterTaxProfitMaxDefference() {

    TreeMap<LocalDate, Double> profitMarginByDate = new TreeMap<>();

    IntStream
            .range(0, 4)
            .forEach(value -> {
              Number totalRevenue = JsonPath.read(financialsJson, String.format("$.financials[%d].totalRevenue", value));
              Number netIncome = JsonPath.read(financialsJson, String.format("$.financials[%d].netIncome", value));
              Double afterTaxProfit = Double.valueOf(netIncome.doubleValue() / totalRevenue.doubleValue());
              String quarter = JsonPath.read(financialsJson, String.format("$.financials[%d].reportDate",value));

              profitMarginByDate.put(LocalDate.parse(quarter), afterTaxProfit);





            });

    Double  thisQuartersAfterTaxProfit = profitMarginByDate.firstEntry().getValue();

    Double maxProfitMargin = profitMarginByDate.values().stream().max(Double::compareTo).get();

    return (thisQuartersAfterTaxProfit - maxProfitMargin) / maxProfitMargin;

  }

  //rule 6
  public Number returnOnEquity () {
    return JsonPath.read(statsJson, "$.returnOnEquity");

  }

  //rule 7
  public Optional<Double> cashFlowEpsComparedToEpsIfTechnology() {
    String sector = JsonPath.read(companyJson, "$.sector");

    if(sector.equalsIgnoreCase("technology")) {
      Number sharesOutstanding = JsonPath.read(statsJson, "$.sharesOutstanding");
      Double cashFlow = getCashFlowFromMostRecentQuarter();

      //https://www.investopedia.com/terms/c/casheps.asp
      Number cashEPS = cashFlow / sharesOutstanding.doubleValue();

      Number eps = JsonPath.read(statsJson, "$.latestEPS");
      if (eps.doubleValue() == 0) {
        eps = JsonPath.read(statsJson, "$.consensusEPS");
      }

      return Optional.of(cashEPS.doubleValue() / eps.doubleValue());

    } else
      return Optional.empty();

  }

  //rule 8

  public Double getEpsGrowthOverLastTwoQuarters() {
    TreeMap<LocalDate, Double> epsByDate = new TreeMap<>();

    IntStream
            .range(0, 4)
            .forEach(value -> {

              Number actualEPS = (Number) Optional.ofNullable(JsonPath.read(earningsJson, String.format("$.earnings[%d].actualEPS", value)))
                      .orElse(JsonPath.read(earningsJson, String.format("$.earnings[%d].consensusEPS",value)));
                String EPSReportDate = JsonPath.read(earningsJson, String.format("$.earnings[%d].EPSReportDate", value));


                epsByDate.put(LocalDate.parse(EPSReportDate), actualEPS.doubleValue());


            });

    List<Double> firstTwoEps = epsByDate
            .values()
            .stream()
            .skip(Math.max(0, epsByDate.size() - 2))
            .collect(Collectors.toList());

    Double eps1 = firstTwoEps.get(0);
    Double eps2 = firstTwoEps.get(1);

    Double epsGrowth =  (eps2 - eps1) / eps1;

    return epsGrowth;

  }

  //rule 8
  public Double getRateOfAnualGrowthForLastThreeYears() {

    return  getRateOfIncreaseForEarnings();
  }

  //rule 8
  private Double getStockPricePerformanceOverTime(Interval stockInterval, Calendar timePeriod) {

    Stock stock = null;
    try {
      stock = YahooFinance.get(ticker);
    } catch (IOException e) {
      e.printStackTrace();
    }

    StockQuote todaysQuote = stock.getQuote();


    List<HistoricalQuote> history = new ArrayList<>();
    try {
      history.addAll(stock.getHistory(timePeriod, stockInterval));
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<Double> closeValues = history
            .stream()
            .filter(historicalQuote -> historicalQuote.getClose() != null)
            .map(historicalQuote -> {
              return historicalQuote.getClose().doubleValue();
            }).collect(Collectors.toList());

    Double slope = Statistics.findSlope(closeValues);
    return slope;

  }


  public Double getStockPricePerformanceOverThePastTwelveMonths() {
    Calendar twelveMonthsAgo = Calendar.getInstance();
    twelveMonthsAgo.add(Calendar.MONTH, - 12);
    return getStockPricePerformanceOverTime(Interval.WEEKLY, twelveMonthsAgo);
  }

  public void updateIndustry() {

    Calendar threeMonths = Calendar.getInstance();
    threeMonths.add(Calendar.MONTH, -1);

    Double stockPerformanceLast3Months = getStockPricePerformanceOverTime(Interval.DAILY, threeMonths);

    IndustryGroupService.saveIndustryGroupStock(industry, ticker, stockPerformanceLast3Months);

  }

  
  private Double getCashFlowFromMostRecentQuarter() {

    TreeMap<LocalDate, Double> localDateNumberTreeMap = new TreeMap<>();

    for (int i = 0; i < 4; i++) {
      Number revenue = JsonPath.read(financialsJson, "$.financials[" + i + "].cashFlow");
      String quarter = JsonPath.read(financialsJson, "$.financials[" + i + "].reportDate");
      System.out.println("iteration:" + i +",quarter: " + quarter + ",revenue:" + revenue);

      localDateNumberTreeMap.put(LocalDate.parse(quarter), revenue.doubleValue());


    }

    return localDateNumberTreeMap.firstEntry().getValue();

  }


  public Double getSlopeOfEps() {

    TreeMap<Integer, Double> epsList = new TreeMap<>();

    IntStream
      .range(0, 4)
      .forEach(value -> {
        Number actualEPS = (Number) Optional.ofNullable(JsonPath.read(earningsJson, String.format("$.earnings[%d].actualEPS", value)))
                .orElse(JsonPath.read(earningsJson, String.format("$.earnings[%d].consensusEPS",value)));

                epsList.put(value, actualEPS.doubleValue());

    });
    ArrayList<Double> arrayList = new ArrayList();
    epsList.descendingMap().forEach((integer, aDouble) -> {
      arrayList.add(aDouble);
    });

    return Statistics.findSlope(arrayList);
  }

  //rule 3
  public double getRateOfIncreaseForEarnings() {

    TreeMap<LocalDate, Double> netIncomeByDate = new TreeMap<>();

    IntStream
            .range(0, 4)
            .forEach(value -> {

              Number netIncome = JsonPath.read(financialsJson, String.format("$.financials[%d].netIncome", value));
              String reportDate = JsonPath.read(financialsJson, String.format("$.financials[%d].reportDate", value));

              netIncomeByDate.put(LocalDate.parse(reportDate), netIncome.doubleValue());


            });



      List<Double> earningsList = netIncomeByDate
              .entrySet()
              .stream()

              .map(yearDoubleEntry -> yearDoubleEntry.getValue())
              .collect(Collectors.toList());

      return Statistics.findSlope(earningsList);

    }


}
