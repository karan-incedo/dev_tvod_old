
package com.viewlift.models.data.appcms.api;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@UseStag
public class PlanDetail implements Serializable {

    @SerializedName("recurringPaymentAmount")
    @Expose
    double recurringPaymentAmount;
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
    long scheduledFromDate;
    @SerializedName("supportedDevices")
    @Expose
    List<String> supportedDevices = null;
    @SerializedName("visible")
    @Expose
    boolean visible;
    @SerializedName("numberOfAllowedStreams")
    @Expose
    int numberOfAllowedStreams;
    @SerializedName("numberOfAllowedDevices")
    @Expose
    int numberOfAllowedDevices;
    @SerializedName("strikeThroughPrice")
    @Expose
    double strikeThroughPrice;

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

    public double getStrikeThroughPrice() {
        return strikeThroughPrice;
    }

    public void setStrikeThroughPrice(double strikeThroughPrice) {
        this.strikeThroughPrice = strikeThroughPrice;
    }
}
