package com.spear.canslim;

import com.spear.canslim.database.DataAdder;
import com.spear.canslim.database.Database;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class IndustryGroupService {


    public static void saveIndustryGroupStock(String industry, String stock, Double stockPriceOverLast2Months) {

        Map<String, Object> stockData = new HashMap<>();
        stockData.put(stock, stockPriceOverLast2Months);

        DataAdder.newInstance().collection("industry").document(industry).data(stockData).mergeData();

    }

    /**
     * The param is a key value pair for the ticker of the company, and the slope of the latest stock regression
     * @param companySlopeMapPerIndustry
     * @return
     */

    public static OptionalDouble getIndustryGroupSlope(Map<String,Double> companySlopeMapPerIndustry) {

        return companySlopeMapPerIndustry
                .values()
                .stream()
                .mapToDouble(aDouble -> aDouble)
                .average();
    }

    public static Map<String, Map<String, Double>> getIndustriesWithComapnySlopes() {

        Map<String, Map<String, Double>> map = new HashMap();

            Database.getDatabase().consumeCollection("industry", (stringObjectMap) -> {
                Map<String, Double> dataWithDouble = stringObjectMap
                        .entrySet()
                        .stream()
                        .filter(stringObjectEntry -> {
                            return stringObjectEntry.getValue() instanceof Double;
                        })
                        .collect(Collectors.toMap(o -> o.getKey(), o -> Double.valueOf(o.getValue().toString())));

                map.put(stringObjectMap.get("_id").toString(), dataWithDouble);

            });


        return map;


    }
}
