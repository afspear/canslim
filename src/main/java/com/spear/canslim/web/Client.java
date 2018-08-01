package com.spear.canslim.web;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class Client {
    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public static String getJson(String url) {

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();

        return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
