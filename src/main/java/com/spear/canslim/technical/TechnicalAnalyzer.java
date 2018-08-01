package com.spear.canslim.technical;


import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseTimeSeries;
import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TechnicalAnalyzer {
    public TechnicalAnalyzer(String ticker) throws IOException {

        Calendar stockInterval = Calendar.getInstance();
        stockInterval.add(Calendar.MONTH, -12);

        Stock stock = null;

        stock = YahooFinance.get(ticker);

        StockQuote todaysQuote = stock.getQuote();


        List<HistoricalQuote> history = new ArrayList<>();

        history.addAll(stock.getHistory(stockInterval, Interval.WEEKLY));


        TimeSeries series = new BaseTimeSeries();

        history.stream().forEach(historicalQuote -> {
            series.addBar(new BaseBar(
                            Duration.ofDays(5),
                            ZonedDateTime.ofInstant(historicalQuote.getDate().toInstant(), ZoneId.systemDefault()),
                            decimal(historicalQuote.getOpen()),
                            decimal(historicalQuote.getHigh()),
                            decimal(historicalQuote.getLow()),
                            decimal(historicalQuote.getClose()),
                            decimal(historicalQuote.getVolume())
                            )
            );
        });
        /*

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        List<IndicatorAnalyzer> oscilatorList = new ArrayList<>();

        oscilatorList.add(new IndicatorAnalyzer( "rsi",);


        oscilatorList.add(new IndicatorAnalyzer("stochastic", ));

        oscilatorList.add(new IndicatorAnalyzer("cci", );

        oscilatorList.add(new IndicatorAnalyzer("awsome", );


        oscilatorList.add(new IndicatorAnalyzer("momentum", new ROCIndicator(closePrice, 10)));

        oscilatorList.add(new IndicatorAnalyzer("macd", new MACDIndicator(closePrice)));


        oscilatorList.add(new IndicatorAnalyzer("stochasticRsi", new StochasticRSIIndicator(series, 14)));
        oscilatorList.add(new IndicatorAnalyzer("williams", new WilliamsRIndicator(series, 14)));

        List<IndicatorAnalyzer> movingAveragesList = new ArrayList<>();


        movingAveragesList.add(new IndicatorAnalyzer("ema10", new EMAIndicator(closePrice, 10)));
        movingAveragesList.add(new IndicatorAnalyzer("ema20", new EMAIndicator(closePrice, 20)));
        movingAveragesList.add(new IndicatorAnalyzer("ema30", new EMAIndicator(closePrice, 30)));
        movingAveragesList.add(new IndicatorAnalyzer("ema50", new EMAIndicator(closePrice, 50)));
        movingAveragesList.add(new IndicatorAnalyzer("ema100", new EMAIndicator(closePrice, 100)));
        movingAveragesList.add(new IndicatorAnalyzer("ema200", new EMAIndicator(closePrice, 200)));

        movingAveragesList.add(new IndicatorAnalyzer("sma10", new SMAIndicator(closePrice, 10)));
        movingAveragesList.add(new IndicatorAnalyzer("sma20", new SMAIndicator(closePrice, 20)));
        movingAveragesList.add(new IndicatorAnalyzer("sma30", new SMAIndicator(closePrice, 30)));
        movingAveragesList.add(new IndicatorAnalyzer("sma50", new SMAIndicator(closePrice, 50)));
        movingAveragesList.add(new IndicatorAnalyzer("sma100", new SMAIndicator(closePrice, 100)));
        movingAveragesList.add(new IndicatorAnalyzer("sma200", new SMAIndicator(closePrice, 200)));


        VWAPIndicator vwapIndicator = new VWAPIndicator(series, 20);
        movingAveragesList.add(new IndicatorAnalyzer("wvap", new MVWAPIndicator(vwapIndicator, 20)));


        Map<String, Double> oscilatorMap = oscilatorList
                .stream()
                .collect(Collectors
                        .toMap(indicatorAnalyzer -> indicatorAnalyzer.getName(),
                                indicatorAnalyzer -> indicatorAnalyzer.getLatestValue()));


        movingAveragesList
                .stream()
                .collect(Collectors
                        .toMap(indicatorAnalyzer -> indicatorAnalyzer.getName(),
                                indicatorAnalyzer -> indicatorAnalyzer.getSlope()));
                                */


}

private Decimal decimal(BigDecimal bigDecimal) {
        return Decimal.valueOf(bigDecimal.doubleValue());
}

    private Decimal decimal(Long bigDecimal) {
        return Decimal.valueOf(bigDecimal.doubleValue());
    }




}
