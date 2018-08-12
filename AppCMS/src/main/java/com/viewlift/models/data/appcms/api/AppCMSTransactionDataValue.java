package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AppCMSTransactionDataValue {

    @SerializedName("updateDate")
    @Expose
    private String updateDate;

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    @SerializedName("addedDate")
    @Expose
    private String addedDate;

    public float getRentalPeriod() {
        return rentalPeriod;
    }

    public void setRentalPeriod(float rentalPeriod) {
        this.rentalPeriod = rentalPeriod;
    }

    @SerializedName("rentalPeriod")
    @Expose
    private float rentalPeriod;


    public long getTransactionEndDate() {
        return transactionEndDate;
    }

    public void setTransactionEndDate(long transactionEndDate) {
        this.transactionEndDate = transactionEndDate;
    }

    @SerializedName("transactionEndDate")
    @Expose
    private long transactionEndDate;


    public String getTransactionStartDate() {
        return transactionStartDate;
    }

    public void setTransactionStartDate(String transactionStartDate) {
        this.transactionStartDate = transactionStartDate;
    }

    @SerializedName("transactionStartDate")
    @Expose
    private String transactionStartDate;

}
