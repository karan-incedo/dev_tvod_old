package com.viewlift.models.data.appcms.sslcommerz;

import com.google.gson.annotations.SerializedName;

public class SSLInitiateResponse {
    @SerializedName("success")
    Boolean success;
    @SerializedName("error")
    String error;

    public Boolean getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}
