package com.spear.canslim.math;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.Arrays;
import java.util.List;

public class Statistics {


    public static Double[] minMaxNormalization(Double doubles[])
    {

        double max = StatUtils.max(ArrayUtils.toPrimitive(doubles));
        double min = StatUtils.min(ArrayUtils.toPrimitive(doubles));

        Double[] normalized = new Double[doubles.length];
        double new_min = 0;
        double new_max = 1;

        System.out.println("min= "+min+" max= "+max+"\n");

        double v1;

        for(int i=0;i<doubles.length;i++)
        {
            v1=(((doubles[i]-min)/(max-min))*(new_max-new_min))+new_min;
            normalized[i] = v1;
        }

        return normalized;
    }


    public static Double findSlope(List<Double> doubleList) {
        SimpleRegression regression = new SimpleRegression();
        Double[] normalizedRevenuValues = Statistics.minMaxNormalization(doubleList.toArray(new Double[doubleList.size()]));


        for (int i = 0; i < normalizedRevenuValues.length; i++) {

            double normalizedRevenuValue = normalizedRevenuValues[i];

            regression.addData(i, normalizedRevenuValue);
        }
        System.out.println("original list:" + doubleList);
        System.out.println("normalized list:" + Arrays.asList(normalizedRevenuValues));

        regression.regress();




        double slope = regression.getSlope();
        System.out.println("slope:" + slope);
        return slope;

    }
}
