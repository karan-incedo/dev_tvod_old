package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;


public class AppCMSTransactionDataResponse {


    public Map<String, AppCMSTransactionDataValue> getGetData() {
        return getData;
    }

    public void setGetData(Map<String, AppCMSTransactionDataValue> getData) {
        this.getData = getData;
    }

    Map<String,AppCMSTransactionDataValue> getData;


}



