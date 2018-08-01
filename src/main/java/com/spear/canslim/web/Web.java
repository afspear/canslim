package com.spear.canslim.web;

import com.spear.canslim.AllStockAnalyzer;

import java.util.Optional;

import static spark.Spark.*;

public class Web {

  public static void init() {

    Optional<String> port = Optional.ofNullable(System.getenv("PORT"));
    port.ifPresent(s -> port(Integer.valueOf(s)));


    staticFiles.location("/public/canslim/dist/canslim");

    get("/callback", (request, response) -> "callback");

    get("/analyze", (request, response) -> {
      return AllStockAnalyzer.getInstance().analyzeAllStocks()
              .map(future -> "started")
              .orElse("already started");
    });



  }

}
