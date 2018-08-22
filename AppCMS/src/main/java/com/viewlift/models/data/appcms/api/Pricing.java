package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.billing.utils.Purchase;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Pricing implements Serializable {

    @SerializedName("rent")
    @Expose
    private Rent rent;

    public Rent getRent() {
        return rent;
    }

    public void setRent(Rent rent) {
        this.rent = rent;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    @SerializedName("purchase")
    @Expose
    private Purchase purchase;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @SerializedName("type")
    @Expose
    String type;


}
