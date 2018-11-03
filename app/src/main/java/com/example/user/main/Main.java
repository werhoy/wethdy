package com.example.user.main;

import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    float temp;
    @SerializedName("humidity")
    int humidity;
    @SerializedName("pressure")
    int pressure;
    @SerializedName("temp_min")
    float temp_min;
    @SerializedName("temp_max")
    float temp_max;
}
