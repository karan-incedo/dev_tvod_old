package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppCMSRentalAPIResponse {
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("transactionEndDate")
    @Expose
    private int transactionEndDate;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTransactionEndDate() {
        return transactionEndDate;
    }

    public void setTransactionEndDate(int transactionEndDate) {
        this.transactionEndDate = transactionEndDate;
    }
}
