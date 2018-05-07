package com.spear.canslim;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;

public enum  FundamentalService {
  FUNDAMENTAL_SERVICE;



  public static FundamentalService getInstance() {
    return FUNDAMENTAL_SERVICE;
  }

  public String getFundamentals(String ticker) throws IOException {

    Optional<String> cik = CIKFileReader.getCIKFromTicker("GOOG");

    cik.ifPresent(s -> {
      String url = String.format("https://api.usfundamentals.com/v1/indicators/xbrl?frequency=q&indicators=Revenues&companies=%s&token=9xJdBwq4s89UHj6MgZf4dw", cik.get());

      Request request = new Request.Builder()
        .url(url)
        .build();

      Response response = null;
      try {
        response = Util.httpClient.newCall(request).execute();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        String bodyString = response.body().string();
        System.out.println(bodyString);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    return null;



  }
}
