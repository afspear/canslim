package com.spear.canslim.technical.oscilator;

import com.spear.canslim.technical.IndicatorAnalyzer;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.CCIIndicator;

public class CciAnalyzer extends IndicatorAnalyzer {
    public CciAnalyzer(TimeSeries series) {
        super(series);

        indicator = new CCIIndicator(series, 20);
    }

    @Override
    public Action getAction() {
        Double latestValue = getLatestValue();
        if(latestValue < -100)
            return Action.BUY;
        if (latestValue > 100)
            return Action.SELL;
        return Action.NEUTRAL;
    }
}
