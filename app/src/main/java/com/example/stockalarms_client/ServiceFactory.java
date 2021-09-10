package com.example.stockalarms_client;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {

    static String BASE_URL = "http://192.168.100.26:8080/StockAlarms/";

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static ServiceUser getUserService() {
        return retrofit.create(ServiceUser.class);
    }


}