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

}
