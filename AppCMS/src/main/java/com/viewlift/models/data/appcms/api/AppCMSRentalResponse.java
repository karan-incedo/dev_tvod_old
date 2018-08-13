package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AppCMSRentalResponse {

    @SerializedName("Status")
    @Expose
    private String Status;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public long getTransactionEndDate() {
        return transactionEndDate;
    }

    public void setTransactionEndDate(long transactionEndDate) {
        this.transactionEndDate = transactionEndDate;
    }

    @SerializedName("transactionEndDate")
    @Expose
    private long transactionEndDate;

}
