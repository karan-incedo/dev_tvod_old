package com.viewlift.models.data.appcms.api;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by viewlift on 7/31/17.
 */

public class SubscriptionPlan extends RealmObject {
    @PrimaryKey
    private String sku;
    private String planId;
    private double subscriptionPrice;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public double getSubscriptionPrice() {
        return subscriptionPrice;
    }

    public void setSubscriptionPrice(double subscriptionPrice) {
        this.subscriptionPrice = subscriptionPrice;
    }
}
