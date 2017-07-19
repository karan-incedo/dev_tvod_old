package com.viewlift.models.data.appcms.api;

/*
 * Created by Viewlift on 7/13/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class SubscriptionRequest {

    @SerializedName("siteInternalName")
    @Expose
    String siteInternalName;

    @SerializedName("userId")
    @Expose
    String userId;

    @SerializedName("siteId")
    @Expose
    String siteId;

    @SerializedName("subscription")
    @Expose
    String subscription;

    @SerializedName("planId")
    @Expose
    String planId;

    @SerializedName("platform")
    @Expose
    String platform;

    @SerializedName("email")
    @Expose
    String email;

    @SerializedName("stripeToken")
    @Expose
    String stripeToken;

    public String getSiteInternalName() {
        return siteInternalName;
    }

    public void setSiteInternalName(String siteInternalName) {
        this.siteInternalName = siteInternalName;
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

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }
}