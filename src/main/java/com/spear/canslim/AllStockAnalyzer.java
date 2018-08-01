package com.spear.canslim;


import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.util.concurrent.RateLimiter;
import com.jayway.jsonpath.JsonPath;
import com.spear.canslim.database.DataAdder;
import com.spear.canslim.database.Database;
import com.spear.canslim.web.Client;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AllStockAnalyzer {

    private List<String> allStocks = new ArrayList();
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    RateLimiter rateLimiter = RateLimiter.create(20);
    List<ScheduledFuture> futures = new ArrayList<>();
    private static AllStockAnalyzer allStockAnalyzer = new AllStockAnalyzer();
    Future future;


    private AllStockAnalyzer () {}

    public static AllStockAnalyzer getInstance() {
        return allStockAnalyzer;
    }

    public List<String> getAllStocks() {

        String symbolsJson = Client.getJson(StockFundamentalAnalyzer.IEXTRADINGURL + "/ref-data/symbols");

        Integer numberOfSymobls = JsonPath.read(symbolsJson, "$.length()");


        List<Map<String, Object>> commonStocks = JsonPath
                .parse(symbolsJson)
                .read("$..[?(@.type == 'cs')]", List.class);

        return commonStocks
                .stream()
                .map(stringObjectMap -> stringObjectMap.get("symbol"))
                .map(o -> o.toString())
                .collect(Collectors.toList());

        }

        public void retryList() {
            try {
                List<String> stocksToRetry = Files.readLines(new File("errors.csv"), Charset.defaultCharset())
                        .stream()
                        .map(s -> s.split(";")[0])
                        .collect(Collectors.toList());
                analyzeAllStocks(stocksToRetry);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void analyzeAllStocks(List<String> stocks) {

        for (Iterator<String> iterator = stocks.iterator(); iterator.hasNext();) {

            String stock = iterator.next();

                // Remove the current element from the iterator and the list.
                iterator.remove();


            annalyzeandSave(stock);
        }

    }

    private void annalyzeandSave(String stock) {
        rateLimiter.acquire();
        try {
            Map<String, Object> stockData = StockFundamentalGenerator.genearteStockData(stock);
            DataAdder.newInstance().collection("stocks").document(stock).data(stockData).mergeData();
        } catch (Exception e) {
            System.out.println("Stock " + stock + " isn't looking good. Not saving.");
            try {
                Files.append((stock + ";" +e.getMessage() + ";" + e.toString()) + System.lineSeparator(), new File("errors.csv"), Charset.defaultCharset());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }


    public void populateStockWithIndsutrySlope() {
        Map<String, Map<String, Double>> industriesWithComapnySlopes = IndustryGroupService.getIndustriesWithComapnySlopes();

        industriesWithComapnySlopes
                .entrySet()
                .forEach(stringMapEntry -> {
                    String industry = stringMapEntry.getKey();
                    Map<String, Double> companies = stringMapEntry.getValue();
                    IndustryGroupService.getIndustryGroupSlope(stringMapEntry.getValue())
                            .ifPresent(inudstrySlope -> {
                                DataAdder.newInstance().collection("industry").document(industry).data(ImmutableMap.of("slope", inudstrySlope)).mergeData();
                                companies
                                        .keySet()
                                        .forEach(ticker -> {
                                            DataAdder.newInstance().collection("stocks").document(ticker).data(ImmutableMap.of("industrySlope", inudstrySlope)).mergeData();
                                        });
                            });
                    });
    }


    public void rankStocks() {

        List<Map<String, Object>> stocks = Database.getDatabase().getCollection("stocks");
            Stream.of(StockFundamentalGenerator.KEYS.values())
                    .forEach(keyEnum -> {
                        String key = keyEnum.string();



                            List<Pair<String, Double>> sortedMaps = stocks
                                    .stream()
                                    .map(stringObjectMap -> {
                                        Pair<String, Double> pair = new MutablePair<>();

                                        Object keyValue = stringObjectMap.get(key);

                                        String ticker = stringObjectMap.get("_id").toString();

                                        if (keyValue instanceof Double)
                                           pair = MutablePair.of(ticker, (Double) keyValue);
                                        else if (keyValue instanceof Map) {
                                            Object mapValue = ((Map) keyValue).get("value");
                                            if (mapValue instanceof Double)
                                                pair = MutablePair.of(ticker, (Double) mapValue);
                                        }
                                        return pair;


                                    })
                                    .filter(stringDoublePair -> stringDoublePair.getValue() != null)
                                    .sorted(
                                            Comparator
                                                    .comparing(Pair::getValue)

                                    )
                                    .collect(Collectors.toList());


                            for (int i = 0; i < sortedMaps.size(); i++) {

                                Double rank = (Double.valueOf(i) / Double.valueOf(stocks.size()));


                                Pair<String, Double> pair = sortedMaps.get(i);

                                Double value = pair.getValue();
                                String ticker = pair.getKey();

                                Map map = ImmutableMap.of(key, ImmutableMap.of("rank", rank, "value", value));





                                DataAdder.newInstance().collection("stocks").document(ticker).data(map).mergeData();

                            }


                    });


    }

    public void saveStockScore() {

        Map<String, Double> tickerScoreMap = new HashMap<>();

            Database.getDatabase().consumeCollection("stocks", (stringObjectMap) -> {
                OptionalDouble score = stringObjectMap
                        .values()
                        .stream()
                        .mapToDouble(o -> {
                            if (o instanceof Map) {
                                return Double.valueOf(((Map) o).get("rank").toString());

                            } else {
                                return 0;
                            }
                        })
                        .average();
                score.ifPresent(v -> tickerScoreMap.put(stringObjectMap.get("_id").toString(), v));


            });


            tickerScoreMap
                    .forEach((key, value) -> DataAdder.newInstance()
                            .collection("stocks")
                            .document(key)
                            .data(ImmutableMap.of("score", value))
                            .mergeData());


    }

    private Stream<ImmutablePair<String, Double>> getSortedStocks() {
        List<Map<String, Object>> stocks = Database.getDatabase().getCollection("stocks");


        Stream<ImmutablePair<String, Double>> sortedStocks = stocks
                .stream()
                .map(stringObjectMap -> ImmutablePair.of(stringObjectMap.get("_id").toString(), Double.valueOf(stringObjectMap.get("score").toString())))
                .sorted((stringDoubleImmutablePair, t1) -> Double.compare(t1.right, stringDoubleImmutablePair.right));

        return sortedStocks;
    }

    public void findBestStocks () {


        Map<String, Object> bestStocks = getSortedStocks()
                .limit(10)
                .collect(Collectors.toMap(o -> o.left, o -> o.right));

        DataAdder.newInstance().collection("bestStocks").document(LocalDate.now().toString()).data(bestStocks).setData();


    }

    public void findWorstStocks () {

        List<ImmutablePair<String, Double>> sortedStocks = getSortedStocks().collect(Collectors.toList());

        List<ImmutablePair<String, Double>> lastTen = sortedStocks.subList(sortedStocks.size() - 10, sortedStocks.size());

        Map<String, Object> worstStocks = lastTen.stream()
                .collect(Collectors.toMap(o -> o.left, o -> o.right));

        DataAdder.newInstance().collection("worstStocks").document(LocalDate.now().toString()).data(worstStocks).setData();


    }

    public Optional<Future> analyzeAllStocks() {

        if(future == null || future.isDone()) {
            future = executor.submit(() -> {
                List<String> stocks = getAllStocks();
                analyzeAllStocks(stocks);
                populateStockWithIndsutrySlope();
                rankStocks();
                saveStockScore();
                findBestStocks();
            });
            return Optional.of(future);

        }
        return Optional.empty();
    }





}
