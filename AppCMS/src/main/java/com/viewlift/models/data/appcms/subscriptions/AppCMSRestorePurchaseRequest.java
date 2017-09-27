package com.viewlift.models.data.appcms.subscriptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 9/27/17.
 */

@UseStag
public class AppCMSRestorePurchaseRequest {
    @SerializedName("paymentUniqueId")
    @Expose
    String paymentUniqueId;

    @SerializedName("site")
    @Expose
    String site;

    public String getPaymentUniqueId() {
        return paymentUniqueId;
    }

    public void setPaymentUniqueId(String paymentUniqueId) {
        this.paymentUniqueId = paymentUniqueId;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
