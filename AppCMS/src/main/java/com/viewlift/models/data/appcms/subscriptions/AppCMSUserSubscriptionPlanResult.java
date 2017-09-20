package com.viewlift.models.data.appcms.subscriptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

/**
 * Created by viewlift on 8/3/17.
 */

@UseStag
public class AppCMSUserSubscriptionPlanResult {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("siteId")
    @Expose
    private String siteId;
    @SerializedName("paymentUniqueId")
    @Expose
    private String paymentUniqueId;
    @SerializedName("subscriptionPlanInfo")
    @Expose
    private AppCMSSubscriptionPlanResult subscriptionPlanInfo;
    @SerializedName("subscriptionInfo")
    @Expose
    AppCMSUserSubscriptionPlanInfoResult subscriptionInfo;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("plans")
    @Expose
    List<AppCMSSubscriptionPlanResult> plans;

    public AppCMSUserSubscriptionPlanResult() {
        subscriptionPlanInfo = new AppCMSSubscriptionPlanResult();
        subscriptionInfo = new AppCMSUserSubscriptionPlanInfoResult();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getPaymentUniqueId() {
        return paymentUniqueId;
    }

    public void setPaymentUniqueId(String paymentUniqueId) {
        this.paymentUniqueId = paymentUniqueId;
    }

    public AppCMSSubscriptionPlanResult getSubscriptionPlanInfo() {
        return subscriptionPlanInfo;
    }

    public void setSubscriptionPlanInfo(AppCMSSubscriptionPlanResult subscriptionPlanInfo) {
        this.subscriptionPlanInfo = subscriptionPlanInfo;
    }

    public AppCMSUserSubscriptionPlanInfoResult getSubscriptionInfo() {
        return subscriptionInfo;
    }

    public void setSubscriptionInfo(AppCMSUserSubscriptionPlanInfoResult subscriptionInfo) {
        this.subscriptionInfo = subscriptionInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AppCMSSubscriptionPlanResult> getPlans() {
        return plans;
    }

    public void setPlans(List<AppCMSSubscriptionPlanResult> plans) {
        this.plans = plans;
    }
}
