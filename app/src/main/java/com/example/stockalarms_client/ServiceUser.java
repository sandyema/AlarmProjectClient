package com.example.stockalarms_client;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ServiceUser {
    @POST("login")
    @Headers("Content-Type: application/json")
    Call<MyResponse> doLogin(@Body HashMap<String, String> user);

    @POST("findID")
    @Headers("Content-Type: application/json")
    Call<MyResponse> findID(@Body HashMap<String, String> user);

    @POST("register")
    @Headers("Content-Type: application/json")
    Call<MyResponse> register(@Body HashMap<String, String> user);

    @POST("editAlarm")
    @Headers("Content-Type: application/json")
    Call<MyResponse> editAlarm(@Body HashMap<String, String> alarm);

    @POST("deleteAlarm")
    @Headers("Content-Type: application/json")
    Call<MyResponse> deleteAlarm(@Body HashMap<String, String> alarm);

    @POST("addAlarm")
    @Headers("Content-Type: application/json")
    Call<MyResponse> addAlarm(@Body HashMap<String, String> alarm);

}
