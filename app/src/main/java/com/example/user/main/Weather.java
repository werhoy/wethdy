package com.example.user.main;

import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("id")
    int id;
    @SerializedName("main")
    String main;
    @SerializedName("description")
    String description;
    @SerializedName("icon")
    String icon;
}
