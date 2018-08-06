package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/19/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class PlanDetail {

    @SerializedName("recurringPaymentAmount")
    @Expose
    double recurringPaymentAmount;

    @SerializedName("strikeThroughPrice")
    @Expose
    double strikeThroughPrice;

    @SerializedName("recurringPaymentCurrencyCode")
    @Expose
    String recurringPaymentCurrencyCode;

    @SerializedName("countryCode")
    @Expose
    String countryCode;

    @SerializedName("featureDetails")
    @Expose
    List<FeatureDetail> featureDetails = null;

    @SerializedName("callToAction")
    @Expose
    String callToAction;

    @SerializedName("featurePlanIdentifier")
    @Expose
    String featurePlanIdentifier;

    @SerializedName("discountedPrice")
    @Expose
    double discountedPrice;

    @SerializedName("isDefault")
    @Expose
    boolean isDefault;

    @SerializedName("scheduledFromDate")
    @Expose
    String scheduledFromDate;

    @SerializedName("supportedDevices")
    @Expose
    List<String> supportedDevices = null;

    @SerializedName("visible")
    @Expose
    boolean visible;

    @SerializedName("numberOfAllowedStreams")
    @Expose
    long numberOfAllowedStreams;

    @SerializedName("numberOfAllowedDevices")
    @Expose
    long numberOfAllowedDevices;

    public double getRecurringPaymentAmount() {
        return recurringPaymentAmount;
    }

    public void setRecurringPaymentAmount(double recurringPaymentAmount) {
        this.recurringPaymentAmount = recurringPaymentAmount;
    }

    public String getRecurringPaymentCurrencyCode() {
        return recurringPaymentCurrencyCode;
    }

    public void setRecurringPaymentCurrencyCode(String recurringPaymentCurrencyCode) {
        this.recurringPaymentCurrencyCode = recurringPaymentCurrencyCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<FeatureDetail> getFeatureDetails() {
        return featureDetails;
    }

    public void setFeatureDetails(List<FeatureDetail> featureDetails) {
        this.featureDetails = featureDetails;
    }

    public String getCallToAction() {
        return callToAction;
    }

    public void setCallToAction(String callToAction) {
        this.callToAction = callToAction;
    }

    public String getFeaturePlanIdentifier() {
        return featurePlanIdentifier;
    }

    public void setFeaturePlanIdentifier(String featurePlanIdentifier) {
        this.featurePlanIdentifier = featurePlanIdentifier;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getScheduledFromDate() {
        return scheduledFromDate;
    }

    public void setScheduledFromDate(String scheduledFromDate) {
        this.scheduledFromDate = scheduledFromDate;
    }

    public List<String> getSupportedDevices() {
        return supportedDevices;
    }

    public void setSupportedDevices(List<String> supportedDevices) {
        this.supportedDevices = supportedDevices;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public long getNumberOfAllowedStreams() {
        return numberOfAllowedStreams;
    }

    public void setNumberOfAllowedStreams(long numberOfAllowedStreams) {
        this.numberOfAllowedStreams = numberOfAllowedStreams;
    }

    public long getNumberOfAllowedDevices() {
        return numberOfAllowedDevices;
    }

    public void setNumberOfAllowedDevices(long numberOfAllowedDevices) {
        this.numberOfAllowedDevices = numberOfAllowedDevices;
    }

    public double getStrikeThroughPrice() {
        return strikeThroughPrice;
    }
}
