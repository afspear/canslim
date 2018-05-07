package com.spear.canslim;

import com.spear.canslim.web.Web;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;


public class Main {
  public static void main(String[] args) throws IOException {
    MarketAnalysis dow =  new MarketAnalysis(MarketAnalysis.DowJonesIndustrialAverage).analyze();

    MarketAnalysis sAndP = new MarketAnalysis(MarketAnalysis.SAndP500).analyze();

    MarketAnalysis nasdaq = new MarketAnalysis(MarketAnalysis.NASDAQComposite).analyze();

    Stream.of(dow, sAndP, nasdaq).forEach(marketAnalysis -> {
      System.out.println(ToStringBuilder.reflectionToString(marketAnalysis, ToStringStyle.MULTI_LINE_STYLE));
    });

    MarketAnalysis apple = new MarketAnalysis("AAPL").analyze();

    Web.init();


  }
}
