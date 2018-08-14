package com.viewlift.models.data.appcms.api;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.billing.utils.Purchase;

public class Pricing {

    @SerializedName("rent")
    @Expose
    private Rent rent;
    @SerializedName("purchase")
    @Expose
    private Purchase purchase;
    @SerializedName("type")
    @Expose
    private String type;


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}