package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class AppCMSSubscriptionResult {

    @SerializedName("subscriptionPlanInfo")
    @Expose
    Object subscriptionPlanInfo;

    @SerializedName("subscriptionInfo")
    @Expose
    Object subscriptionInfo;

    @SerializedName("recurringPaymentAgreementReferenceId")
    @Expose
    long recurringPaymentAgreementReferenceId;

    @SerializedName("recurringPaymentAgreementId")
    @Expose
    long recurringPaymentAgreementId;

    @SerializedName("subscriptionOfferUsage")
    @Expose
    Object subscriptionOfferUsage;

    public Object getSubscriptionPlanInfo() {
        return subscriptionPlanInfo;
    }

    public void setSubscriptionPlanInfo(Object subscriptionPlanInfo) {
        this.subscriptionPlanInfo = subscriptionPlanInfo;
    }

    public Object getSubscriptionInfo() {
        return subscriptionInfo;
    }

    public void setSubscriptionInfo(Object subscriptionInfo) {
        this.subscriptionInfo = subscriptionInfo;
    }

    public long getRecurringPaymentAgreementReferenceId() {
        return recurringPaymentAgreementReferenceId;
    }

    public void setRecurringPaymentAgreementReferenceId(long recurringPaymentAgreementReferenceId) {
        this.recurringPaymentAgreementReferenceId = recurringPaymentAgreementReferenceId;
    }

    public long getRecurringPaymentAgreementId() {
        return recurringPaymentAgreementId;
    }

    public void setRecurringPaymentAgreementId(long recurringPaymentAgreementId) {
        this.recurringPaymentAgreementId = recurringPaymentAgreementId;
    }

    public Object getSubscriptionOfferUsage() {
        return subscriptionOfferUsage;
    }

    public void setSubscriptionOfferUsage(Object subscriptionOfferUsage) {
        this.subscriptionOfferUsage = subscriptionOfferUsage;
    }
}
