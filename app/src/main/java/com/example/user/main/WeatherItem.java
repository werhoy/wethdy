package com.example.user.main;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherItem {
    @SerializedName("coord")
    CoordList coordLists;
    @SerializedName("sys")
    SysList sysLists;
    @SerializedName("wether")
    ArrayList<Weather> weathers;
    @SerializedName("main")
    Main mains;
    @SerializedName("wind")
    WindList winds;
    @SerializedName("rain")
    ArrayList<Rain> rain;
    @SerializedName("clouds")
    CloudsList clouds;

    @SerializedName("dt")
    float dt;

    @SerializedName("id")
    float id;

    @SerializedName("name")
    String name;

    @SerializedName("cod")
    float cod;



}
