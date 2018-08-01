package com.spear.canslim;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StockFundamentalGenerator {

    public static enum KEYS {
        epsPercentageGainFromAYearAgo,
        rateOfIncreaseForEarnings,
        epsGrowthOverLastTwoQuarters,
        rateOfChangeForRevenueForLastFourQuarters,
        slopeOfEps,
        cashFlowEpsComparedToEpsIfTechnology,
        afterTaxProfitNewHigh,
        rateOfAnualGrowthForLastThreeYears,
        returnOnEquity,
        stockPricePerformanceOverThePastTwelveMonths,
        industrySlope;


        public String string() {
            return super.toString();
        }
    }

    public static Map<String, Object> genearteStockData(String ticker) {
        StockFundamentalAnalyzer stockFundamentalAnalyzer = null;
        Map<String, Object> stockData = new HashMap<>();
        try {
            stockFundamentalAnalyzer = new StockFundamentalAnalyzer(ticker);
            stockData.put(KEYS.epsPercentageGainFromAYearAgo.string(), stockFundamentalAnalyzer.epsPercentageGainFromAYearAgo().orElse(Double.NaN));
            stockData.put(KEYS.rateOfIncreaseForEarnings.string(), stockFundamentalAnalyzer.getRateOfIncreaseForEarnings());
            stockData.put(KEYS.epsGrowthOverLastTwoQuarters.string(), stockFundamentalAnalyzer.getEpsGrowthOverLastTwoQuarters());
            stockData.put(KEYS.rateOfChangeForRevenueForLastFourQuarters.string(), stockFundamentalAnalyzer.getRateOfChangeForRevenueForLastFourQuarters());
            stockData.put(KEYS.slopeOfEps.string(), stockFundamentalAnalyzer.getSlopeOfEps());
            stockFundamentalAnalyzer.cashFlowEpsComparedToEpsIfTechnology()
                    .ifPresent(aDouble -> {
                        stockData.put(KEYS.cashFlowEpsComparedToEpsIfTechnology.string(), aDouble);

                    });
            stockData.put(KEYS.afterTaxProfitNewHigh.string(), stockFundamentalAnalyzer.afterTaxProfitMaxDefference());
            stockData.put(KEYS.rateOfAnualGrowthForLastThreeYears.string(), stockFundamentalAnalyzer.getRateOfAnualGrowthForLastThreeYears());
            stockData.put(KEYS.returnOnEquity.string(), stockFundamentalAnalyzer.returnOnEquity());
            //stockData.put(KEYS.stockPricePerformanceOverThePastTwelveMonths.string(), stockFundamentalAnalyzer.getStockPricePerformanceOverThePastTwelveMonths());
            stockFundamentalAnalyzer.updateIndustry();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return stockData;
    }
}
