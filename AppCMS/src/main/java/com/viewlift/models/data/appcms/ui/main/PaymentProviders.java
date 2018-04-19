package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by viewlift on 9/12/17.
 */

@UseStag
public class PaymentProviders implements Serializable {

    @SerializedName("stripe")
    @Expose
    Stripe stripe;

    @SerializedName("ccavenue")
    @Expose
    CCAv ccav;

    public SSLCommerz getSslCommerz() {
        return sslCommerz;
    }

    @SerializedName("sslcommerz")
    @Expose
    SSLCommerz sslCommerz;

    public CCAv getCcav() {
        return ccav;
    }

    public void setCcav(CCAv ccav) {
        this.ccav = ccav;
    }
}
