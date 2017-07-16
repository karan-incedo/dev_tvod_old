package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Request {

    @SerializedName("siteInternalName")
    @Expose
    String siteInternalName;

    @SerializedName("siteId")
    @Expose
    Object siteId;

    @SerializedName("userId")
    @Expose
    Object userId;

    @SerializedName("subscriptionOfferId")
    @Expose
    Object subscriptionOfferId;

    @SerializedName("onlyVisible")
    @Expose
    boolean onlyVisible;

    @SerializedName("includeWebOnlyPlan")
    @Expose
    boolean includeWebOnlyPlan;

    @SerializedName("onlySubscribableBasedOnDate")
    @Expose
    boolean onlySubscribableBasedOnDate;

    @SerializedName("subscriptionPlanIds")
    @Expose
    Object subscriptionPlanIds;

    @SerializedName("countryCode")
    @Expose
    Object countryCode;

    @SerializedName("partnerPortal")
    @Expose
    boolean partnerPortal;

    @SerializedName("authentication")
    @Expose
    Authentication authentication;

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

    public boolean getOnlyVisible() {
        return onlyVisible;
    }

    public void setOnlyVisible(boolean onlyVisible) {
        this.onlyVisible = onlyVisible;
    }

    public boolean getIncludeWebOnlyPlan() {
        return includeWebOnlyPlan;
    }

    public void setIncludeWebOnlyPlan(boolean includeWebOnlyPlan) {
        this.includeWebOnlyPlan = includeWebOnlyPlan;
    }

    public boolean getOnlySubscribableBasedOnDate() {
        return onlySubscribableBasedOnDate;
    }

    public void setOnlySubscribableBasedOnDate(boolean onlySubscribableBasedOnDate) {
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

    public boolean getPartnerPortal() {
        return partnerPortal;
    }

    public void setPartnerPortal(boolean partnerPortal) {
        this.partnerPortal = partnerPortal;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}
