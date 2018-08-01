package com.spear.canslim.technical.oscilator;

import com.spear.canslim.technical.IndicatorAnalyzer;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

public class RsaAnalyzer extends IndicatorAnalyzer {
    public RsaAnalyzer(ClosePriceIndicator indicator) {
        super(indicator);

        this.indicator = new RSIIndicator(indicator, 14);
        this.name = "rsa";

    }


    @Override
    public Action getAction() {

        Double latestValue = this.getLatestValue();
        if (latestValue > 70)
            return Action.SELL;
        if (latestValue < 30)
            return Action.BUY;
        else return Action.NEUTRAL;
    }
}
