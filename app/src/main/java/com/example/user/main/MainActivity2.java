package com.example.user.main;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MainActivity2 {
    @GET("data/2.5/weather")
    Call<WeatherItem> weatherItem(@Query("q") String q, @Query("appid") String appid);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}



