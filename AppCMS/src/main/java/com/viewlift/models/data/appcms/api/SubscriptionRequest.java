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

    @SerializedName("planIdentifier")
    @Expose
    String planIdentifier;

    @SerializedName("platform")
    @Expose
    String platform;

    @SerializedName("email")
    @Expose
    String email;

    @SerializedName("stripeToken")
    @Expose
    String stripeToken;

    @SerializedName("currencyCode")
    @Expose
    String currencyCode;

    @SerializedName("retryCount")
    @Expose
    int retryCount;

    @SerializedName("nextBillingDate")
    @Expose
    String nextBillingDate;

    @SerializedName("zip")
    @Expose
    String zip;

    @SerializedName("receipt")
    @Expose
    String receipt;

    @SerializedName("addEntitlement")
    @Expose
    boolean addEntitlement = true;

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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getNextBillingDate() {
        return nextBillingDate;
    }

    public void setNextBillingDate(String nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getPlanIdentifier() {
        return planIdentifier;
    }

    public void setPlanIdentifier(String planIdentifier) {
        this.planIdentifier = planIdentifier;
    }

    public boolean isAddEntitlement() {
        return addEntitlement;
    }

    public void setAddEntitlement(boolean addEntitlement) {
        this.addEntitlement = addEntitlement;
    }
}
