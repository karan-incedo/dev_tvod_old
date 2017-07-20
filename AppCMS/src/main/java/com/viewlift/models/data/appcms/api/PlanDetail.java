
package com.viewlift.models.data.appcms.api;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlanDetail {

    @SerializedName("recurringPaymentAmount")
    @Expose
    private double recurringPaymentAmount;
    @SerializedName("recurringPaymentCurrencyCode")
    @Expose
    private String recurringPaymentCurrencyCode;
    @SerializedName("countryCode")
    @Expose
    private String countryCode;
    @SerializedName("featureDetails")
    @Expose
    private List<FeatureDetail> featureDetails = null;
    @SerializedName("callToAction")
    @Expose
    private String callToAction;
    @SerializedName("featurePlanIdentifier")
    @Expose
    private String featurePlanIdentifier;
    @SerializedName("discountedPrice")
    @Expose
    private double discountedPrice;
    @SerializedName("isDefault")
    @Expose
    private boolean isDefault;
    @SerializedName("scheduledFromDate")
    @Expose
    private long scheduledFromDate;
    @SerializedName("supportedDevices")
    @Expose
    private List<String> supportedDevices = null;
    @SerializedName("visible")
    @Expose
    private boolean visible;
    @SerializedName("numberOfAllowedStreams")
    @Expose
    private int numberOfAllowedStreams;
    @SerializedName("numberOfAllowedDevices")
    @Expose
    private int numberOfAllowedDevices;

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

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public long getScheduledFromDate() {
        return scheduledFromDate;
    }

    public void setScheduledFromDate(long scheduledFromDate) {
        this.scheduledFromDate = scheduledFromDate;
    }

    public List<String> getSupportedDevices() {
        return supportedDevices;
    }

    public void setSupportedDevices(List<String> supportedDevices) {
        this.supportedDevices = supportedDevices;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getNumberOfAllowedStreams() {
        return numberOfAllowedStreams;
    }

    public void setNumberOfAllowedStreams(int numberOfAllowedStreams) {
        this.numberOfAllowedStreams = numberOfAllowedStreams;
    }

    public int getNumberOfAllowedDevices() {
        return numberOfAllowedDevices;
    }

    public void setNumberOfAllowedDevices(int numberOfAllowedDevices) {
        this.numberOfAllowedDevices = numberOfAllowedDevices;
    }

}
