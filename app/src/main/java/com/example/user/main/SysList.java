package com.example.user.main;

import com.google.gson.annotations.SerializedName;

public class SysList {
    @SerializedName("country")
    String country;

    @SerializedName("sunrise")
    float sunrise;

    @SerializedName("sunset")
    float sunset;
}
