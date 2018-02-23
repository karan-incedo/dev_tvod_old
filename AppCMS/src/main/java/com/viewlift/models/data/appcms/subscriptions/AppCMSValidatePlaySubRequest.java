package com.viewlift.models.data.appcms.subscriptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 2/22/18.
 */

@UseStag
public class AppCMSValidatePlaySubRequest {
    @SerializedName("receipt")
    @Expose
    String receipt;

    @SerializedName("planIdentifier")
    @Expose
    String planIdentifier;

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getPlanIdentifier() {
        return planIdentifier;
    }

    public void setPlanIdentifier(String planIdentifier) {
        this.planIdentifier = planIdentifier;
    }
}
