package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by viewlift on 9/12/17.
 */

public class PaymentProviders {
    @SerializedName("stripe")
    @Expose
    Stripe stripe;

    @SerializedName("ccav")
    @Expose
    CCAv ccav;

    public CCAv getCcav() {
        return ccav;
    }

    public void setCcav(CCAv ccav) {
        this.ccav = ccav;
    }
}
