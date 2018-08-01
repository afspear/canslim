package com.spear.canslim.technical;

import com.spear.canslim.math.Statistics;
import org.ta4j.core.Decimal;
import org.ta4j.core.Indicator;
import org.ta4j.core.TimeSeries;

import java.util.ArrayList;
import java.util.List;

public abstract class IndicatorAnalyzer {
    protected Indicator<Decimal> indicator;
    protected String name;
    private TimeSeries series;

    public enum Action {BUY, SELL, NEUTRAL}

    public IndicatorAnalyzer(String name, Indicator<Decimal> indicator) {
        this.indicator = indicator;
        this.name = name;
    }

    public IndicatorAnalyzer(TimeSeries series) {
        this.series = series;
    }

    public IndicatorAnalyzer(Indicator<Decimal> indicator) {
        this.indicator = indicator;
    }


    public Double getLatestValue() {
       return this.indicator.getValue(indicator.getTimeSeries().getBarCount() -1).doubleValue();

    }

    public Double getSlope() {

        List<Double> values = new ArrayList<>();

        for (int i = 0; i < indicator.getTimeSeries().getBarCount(); i++) {
            values.add(indicator.getValue(i).doubleValue());
        }

        return Statistics.findSlope(values);

    }

    public String getName() {
        return name;
    }

    public abstract Action getAction();
}
