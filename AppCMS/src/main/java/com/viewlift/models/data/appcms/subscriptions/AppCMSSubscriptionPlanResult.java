package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/19/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class AppCMSSubscriptionPlanResult {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("identifier")
    @Expose
    String identifier;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("renewalCyclePeriodMultiplier")
    @Expose
    int renewalCyclePeriodMultiplier;

    @SerializedName("renewalCycleType")
    @Expose
    String renewalCycleType;

    @SerializedName("planDetails")
    @Expose
    List<PlanDetail> planDetails;

    @SerializedName("siteOwner")
    @Expose
    String siteOwner;

    @SerializedName("addedDate")
    @Expose
    String addedDate;

    @SerializedName("updateDate")
    @Expose
    String updateDate;

    @SerializedName("si_status")
    @Expose
    String siStatus ;

    @SerializedName("code")
    @Expose
    String code;

    @SerializedName("error")
    @Expose
    String error;

    @SerializedName("message")
    @Expose
    String message;

    @SerializedName("subscriptionStatus")
    @Expose
    String subscriptionStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRenewalCyclePeriodMultiplier() {
        return renewalCyclePeriodMultiplier;
    }

    public void setRenewalCyclePeriodMultiplier(int renewalCyclePeriodMultiplier) {
        this.renewalCyclePeriodMultiplier = renewalCyclePeriodMultiplier;
    }

    public String getRenewalCycleType() {
        return renewalCycleType;
    }

    public void setRenewalCycleType(String renewalCycleType) {
        this.renewalCycleType = renewalCycleType;
    }

    public List<PlanDetail> getPlanDetails() {
        return planDetails;
    }

    public void setPlanDetails(List<PlanDetail> planDetails) {
        this.planDetails = planDetails;
    }

    public String getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getSiStatus() {
        return siStatus;
    }

    public void setSiStatus(String siStatus) {
        this.siStatus = siStatus;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }
}
