package com.viewlift.models.data.appcms.api;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by viewlift on 8/2/17.
 */

public class UserSubscriptionPlan extends RealmObject {
    @PrimaryKey
    String userId;
    String planReceipt;
    SubscriptionPlan subscriptionPlan;
    RealmList<SubscriptionPlan> availableUpgrades;

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
