package com.spear.canslim.technical.oscilator;

import com.spear.canslim.technical.IndicatorAnalyzer;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.AwesomeOscillatorIndicator;

public class AwsomeAnalyzer extends IndicatorAnalyzer {
    public AwsomeAnalyzer(TimeSeries series) {
        super(series);
        indicator =  new AwesomeOscillatorIndicator(series);
    }

    @Override
    public Action getAction() {
        return null;
    }
}
