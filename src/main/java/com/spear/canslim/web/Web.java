package com.spear.canslim.web;

import static spark.Spark.*;

public class Web {

  public static void init() {
    staticFiles.location("/public");

    get("/callback", (request, response) -> "callback");

    get("/", (request, response) -> "canslim coming soon...");
  }

}
