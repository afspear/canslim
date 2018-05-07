package com.spear.canslim;

import com.jayway.jsonpath.JsonPath;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.IOException;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class EarningsAnalyzer {


  private String ticker;
  private String earningsJson;

  public EarningsAnalyzer(String ticker) throws IOException {
    this.ticker = ticker;
    earningsJson = getEarnings();
  }

  String getEarnings() throws IOException {

    String url = String.format("https://api.iextrading.com/1.0/stock/%s/earnings", ticker);

    Request request = new Request.Builder()
      .url(url)
      .build();

    Response response = Util.httpClient.newCall(request).execute();
    return response.body().string();
  }



  /**
   * Earnings per share in the latest quarter should be up at least 25 percent
   * versus the same quarter a year ago, and preferably much more.
   */
  public Double epsPercentageGainFromAYearAgo() throws IOException {

    Double actualEPs = JsonPath.read(earningsJson, "$.earnings[1].actualEPS");
    Double yearAgo = JsonPath.read(earningsJson, "$.earnings[1].yearAgo");

    double percentageUp = 100 * ((actualEPs - yearAgo) / yearAgo);

    return percentageUp ;

  }

  public Double getSlopeOfEps() {

    TreeMap<Integer, Double> epsList = new TreeMap<>();

    IntStream
      .range(0, 4)
      .forEach(value -> {
        Double actualEPs = JsonPath.read(earningsJson, String.format("$.earnings[%d].actualEPS", value));
        Double yearAgo = JsonPath.read(earningsJson, String.format("$.earnings[%d].yearAgo", value));

        epsList.put(value, actualEPs);
        epsList.put(value + 4, yearAgo);

    });
    ArrayList<Double> arrayList = new ArrayList();
    epsList.descendingMap().forEach((integer, aDouble) -> {
      arrayList.add(aDouble);
    });

    SimpleRegression regression = new SimpleRegression();

    for (int i = 0; i < arrayList.size(); i++) {
      regression.addData((double) i, arrayList.get(i));
      System.out.println(i + " " + arrayList.get(i));
    }

    double slope = regression.getSlope();

    System.out.println("slope of " + ticker + " eps: " + slope);

    return slope;
  }

}
