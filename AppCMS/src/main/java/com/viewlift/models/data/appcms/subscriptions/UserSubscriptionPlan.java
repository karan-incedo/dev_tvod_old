package com.viewlift.models.data.appcms.subscriptions;

import com.viewlift.models.data.appcms.api.SubscriptionPlan;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by viewlift on 8/3/17.
 */

public class UserSubscriptionPlan extends RealmObject {
    @PrimaryKey
    private String userId;
    private String planReceipt;
    private String paymentHandler;
    private SubscriptionPlan subscriptionPlan;
    private RealmList<SubscriptionPlan> availableUpgrades;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlanReceipt() {
        return planReceipt;
    }

    public void setPlanReceipt(String planReceipt) {
        this.planReceipt = planReceipt;
    }

    public String getPaymentHandler() {
        return paymentHandler;
    }

    public void setPaymentHandler(String paymentHandler) {
        this.paymentHandler = paymentHandler;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public RealmList<SubscriptionPlan> getAvailableUpgrades() {
        return availableUpgrades;
    }

    public void setAvailableUpgrades(RealmList<SubscriptionPlan> availableUpgrades) {
        this.availableUpgrades = availableUpgrades;
    }
}
