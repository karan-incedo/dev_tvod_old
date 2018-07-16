package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppCMSXAPIKey implements Serializable {

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("apiKey")
    @Expose
    String x_ApiKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getX_ApiKey() {
        return x_ApiKey;
    }

    public void setX_ApiKey(String x_ApiKey) {
        this.x_ApiKey = x_ApiKey;
    }
}
