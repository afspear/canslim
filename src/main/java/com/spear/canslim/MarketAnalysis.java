package com.spear.canslim;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MarketAnalysis {


  public static final String NASDAQComposite = "^IXIC";
  public static final String SAndP500 = "^GSPC";
  public static final String DowJonesIndustrialAverage = "^DJI";

  Long averageVolume;
  Long todaysVolume;
  double percentDifferenceOfTodayAndAverageVolume;
  Long yesterdayVolume;
  Long differenceBetweenTodayAndYesterdayVolume;
  String marketTicker;

  public MarketAnalysis(String marketTicker) {
    this.marketTicker = marketTicker;
  }

  public Long getAverageVolume() {
    return averageVolume;
  }

  public Long getTodaysVolume() {
    return todaysVolume;
  }

  public double getPercentDifferenceOfTodayAndAverageVolume() {
    return percentDifferenceOfTodayAndAverageVolume;
  }

  public Long getYesterdayVolume() {
    return yesterdayVolume;
  }

  public Long getDifferenceBetweenTodayAndYesterdayVolume() {
    return differenceBetweenTodayAndYesterdayVolume;
  }

  public String getMarketTicker() {
    return marketTicker;
  }

  public  MarketAnalysis analyze () {
    System.out.println("CAN SLIM!");

    Stock stock = null;
    try {
      stock = YahooFinance.get(marketTicker);
    } catch (IOException e) {
      e.printStackTrace();
    }

    StockQuote todaysQuote = stock.getQuote();
    Calendar sevenDaysAgo = Calendar.getInstance();
    sevenDaysAgo.add(Calendar.DAY_OF_MONTH, - 7);

    List<HistoricalQuote> history = new ArrayList<>();
    try {
      history.addAll(stock.getHistory(sevenDaysAgo, Interval.DAILY));
    } catch (IOException e) {
      e.printStackTrace();
    }

    history
      .stream()
      .mapToLong(HistoricalQuote::getVolume)
      .average()
      .ifPresent(value -> {
        long longValue = Double.valueOf(value).longValue();

        averageVolume =  longValue;
        todaysVolume =  todaysQuote.getVolume();
        percentDifferenceOfTodayAndAverageVolume =  (todaysQuote.getVolume() - longValue) / Double.valueOf(longValue) * 100f;
        history
          .stream()
          .skip(history.size() - 1)
          .findFirst().map(HistoricalQuote::getVolume)
          .ifPresent(aLong -> {

            yesterdayVolume = aLong;
            differenceBetweenTodayAndYesterdayVolume = todaysQuote.getVolume() - aLong;
          });
      });

    return this;
  }

}
