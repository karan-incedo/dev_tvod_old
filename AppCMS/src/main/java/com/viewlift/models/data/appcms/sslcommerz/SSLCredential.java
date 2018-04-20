package com.viewlift.models.data.appcms.sslcommerz;

import com.google.gson.annotations.SerializedName;

public class SSLCredential {
    @SerializedName("storeId")
    String storeId;
    @SerializedName("storePassword")
    String storePassword;
    @SerializedName("transactionId")
    String transactionId;

    public String getStoreId() {
        return storeId;
    }

    public String getStorePassword() {
        return storePassword;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
