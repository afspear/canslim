package com.spear.canslim.technical.oscilator;

import com.spear.canslim.technical.IndicatorAnalyzer;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;

public class StochasticAnalyzer extends IndicatorAnalyzer {

    public StochasticAnalyzer(TimeSeries timeSeries) {
        super(timeSeries);
        indicator = new StochasticOscillatorKIndicator(timeSeries, 14);
    }

    @Override
    public Action getAction() {
        Double latestValue = getLatestValue();
        if (latestValue < 20 )
            return Action.BUY;
        if (latestValue > 80 ) {
            return Action.SELL;
        }
        else return Action.NEUTRAL;
    }
}
