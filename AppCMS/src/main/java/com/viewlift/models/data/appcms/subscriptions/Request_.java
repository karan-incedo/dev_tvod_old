package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Request_ {

    @SerializedName("siteInternalName")
    @Expose
    private String siteInternalName;

    @SerializedName("siteId")
    @Expose
    private Object siteId;

    @SerializedName("userId")
    @Expose
    private Object userId;

    @SerializedName("subscriptionOfferId")
    @Expose
    private Object subscriptionOfferId;

    @SerializedName("onlyVisible")
    @Expose
    private Boolean onlyVisible;

    @SerializedName("includeWebOnlyPlan")
    @Expose
    private Boolean includeWebOnlyPlan;

    @SerializedName("onlySubscribableBasedOnDate")
    @Expose
    private Boolean onlySubscribableBasedOnDate;

    @SerializedName("subscriptionPlanIds")
    @Expose
    private Object subscriptionPlanIds;

    @SerializedName("countryCode")
    @Expose
    private Object countryCode;

    @SerializedName("partnerPortal")
    @Expose
    private Boolean partnerPortal;

    @SerializedName("authentication")
    @Expose
    private Authentication authentication;

    public String getSiteInternalName() {
        return siteInternalName;
    }

    public void setSiteInternalName(String siteInternalName) {
        this.siteInternalName = siteInternalName;
    }

    public Object getSiteId() {
        return siteId;
    }

    public void setSiteId(Object siteId) {
        this.siteId = siteId;
    }

    public Object getUserId() {
        return userId;
    }

    public void setUserId(Object userId) {
        this.userId = userId;
    }

    public Object getSubscriptionOfferId() {
        return subscriptionOfferId;
    }

    public void setSubscriptionOfferId(Object subscriptionOfferId) {
        this.subscriptionOfferId = subscriptionOfferId;
    }

    public Boolean getOnlyVisible() {
        return onlyVisible;
    }

    public void setOnlyVisible(Boolean onlyVisible) {
        this.onlyVisible = onlyVisible;
    }

    public Boolean getIncludeWebOnlyPlan() {
        return includeWebOnlyPlan;
    }

    public void setIncludeWebOnlyPlan(Boolean includeWebOnlyPlan) {
        this.includeWebOnlyPlan = includeWebOnlyPlan;
    }

    public Boolean getOnlySubscribableBasedOnDate() {
        return onlySubscribableBasedOnDate;
    }

    public void setOnlySubscribableBasedOnDate(Boolean onlySubscribableBasedOnDate) {
        this.onlySubscribableBasedOnDate = onlySubscribableBasedOnDate;
    }

    public Object getSubscriptionPlanIds() {
        return subscriptionPlanIds;
    }

    public void setSubscriptionPlanIds(Object subscriptionPlanIds) {
        this.subscriptionPlanIds = subscriptionPlanIds;
    }

    public Object getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Object countryCode) {
        this.countryCode = countryCode;
    }

    public Boolean getPartnerPortal() {
        return partnerPortal;
    }

    public void setPartnerPortal(Boolean partnerPortal) {
        this.partnerPortal = partnerPortal;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}
