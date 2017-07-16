package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class Item {

    @SerializedName("countryCode")
    @Expose
    List<Object> countryCode = null;

    @SerializedName("acceptableSubscriptionOffers")
    @Expose
    List<AcceptableSubscriptionOffer> acceptableSubscriptionOffers = null;

    @SerializedName("preAppliedSubscriptionOffers")
    @Expose
    List<PreAppliedSubscriptionOffer> preAppliedSubscriptionOffers = null;

    @SerializedName("currencyCode")
    @Expose
    String currencyCode;

    @SerializedName("visible")
    @Expose
    boolean visible;

    @SerializedName("plansMetaData")
    @Expose
    String plansMetaData;

    @SerializedName("type")
    @Expose
    String type;

    @SerializedName("billingCyclePeriodType")
    @Expose
    String billingCyclePeriodType;

    @SerializedName("maxConnectedDevices")
    @Expose
    int maxConnectedDevices;

    @SerializedName("isPlanSubscribableBasedOnDate")
    @Expose
    boolean isPlanSubscribableBasedOnDate;

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("maxSimultaneousStreams")
    @Expose
    int maxSimultaneousStreams;

    @SerializedName("schedule")
    @Expose
    Schedule schedule;

    @SerializedName("recurringPayment")
    @Expose
    RecurringPayment recurringPayment;

    @SerializedName("billingFrequencyType")
    @Expose
    boolean billingFrequencyType;

    @SerializedName("deviceMetaData")
    @Expose
    String deviceMetaData;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("billingCyclePeriodTypeFrequencyLabel")
    @Expose
    String billingCyclePeriodTypeFrequencyLabel;

    @SerializedName("billingCyclePeriodMultiplier")
    @Expose
    int billingCyclePeriodMultiplier;

    @SerializedName("acceptableOffersCount")
    @Expose
    int acceptableOffersCount;

    @SerializedName("identifier")
    @Expose
    String identifier;

    @SerializedName("preAppliedSubscriptionOffersCount")
    @Expose
    int preAppliedSubscriptionOffersCount;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getPlansMetaData() {
        return plansMetaData;
    }

    public void setPlansMetaData(String plansMetaData) {
        this.plansMetaData = plansMetaData;
    }

    public List<Object> getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(List<Object> countryCode) {
        this.countryCode = countryCode;
    }

    public List<AcceptableSubscriptionOffer> getAcceptableSubscriptionOffers() {
        return acceptableSubscriptionOffers;
    }

    public void setAcceptableSubscriptionOffers(List<AcceptableSubscriptionOffer> acceptableSubscriptionOffers) {
        this.acceptableSubscriptionOffers = acceptableSubscriptionOffers;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBillingCyclePeriodType() {
        return billingCyclePeriodType;
    }

    public void setBillingCyclePeriodType(String billingCyclePeriodType) {
        this.billingCyclePeriodType = billingCyclePeriodType;
    }

    public int getMaxConnectedDevices() {
        return maxConnectedDevices;
    }

    public void setMaxConnectedDevices(int maxConnectedDevices) {
        this.maxConnectedDevices = maxConnectedDevices;
    }

    public boolean getIsPlanSubscribableBasedOnDate() {
        return isPlanSubscribableBasedOnDate;
    }

    public void setIsPlanSubscribableBasedOnDate(boolean isPlanSubscribableBasedOnDate) {
        this.isPlanSubscribableBasedOnDate = isPlanSubscribableBasedOnDate;
    }

    public List<PreAppliedSubscriptionOffer> getPreAppliedSubscriptionOffers() {
        return preAppliedSubscriptionOffers;
    }

    public void setPreAppliedSubscriptionOffers(List<PreAppliedSubscriptionOffer> preAppliedSubscriptionOffers) {
        this.preAppliedSubscriptionOffers = preAppliedSubscriptionOffers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMaxSimultaneousStreams() {
        return maxSimultaneousStreams;
    }

    public void setMaxSimultaneousStreams(int maxSimultaneousStreams) {
        this.maxSimultaneousStreams = maxSimultaneousStreams;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public RecurringPayment getRecurringPayment() {
        return recurringPayment;
    }

    public void setRecurringPayment(RecurringPayment recurringPayment) {
        this.recurringPayment = recurringPayment;
    }

    public boolean getBillingFrequencyType() {
        return billingFrequencyType;
    }

    public void setBillingFrequencyType(boolean billingFrequencyType) {
        this.billingFrequencyType = billingFrequencyType;
    }

    public String getDeviceMetaData() {
        return deviceMetaData;
    }

    public void setDeviceMetaData(String deviceMetaData) {
        this.deviceMetaData = deviceMetaData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBillingCyclePeriodTypeFrequencyLabel() {
        return billingCyclePeriodTypeFrequencyLabel;
    }

    public void setBillingCyclePeriodTypeFrequencyLabel(String billingCyclePeriodTypeFrequencyLabel) {
        this.billingCyclePeriodTypeFrequencyLabel = billingCyclePeriodTypeFrequencyLabel;
    }

    public int getBillingCyclePeriodMultiplier() {
        return billingCyclePeriodMultiplier;
    }

    public void setBillingCyclePeriodMultiplier(int billingCyclePeriodMultiplier) {
        this.billingCyclePeriodMultiplier = billingCyclePeriodMultiplier;
    }

    public int getAcceptableOffersCount() {
        return acceptableOffersCount;
    }

    public void setAcceptableOffersCount(int acceptableOffersCount) {
        this.acceptableOffersCount = acceptableOffersCount;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getPreAppliedSubscriptionOffersCount() {
        return preAppliedSubscriptionOffersCount;
    }

    public void setPreAppliedSubscriptionOffersCount(int preAppliedSubscriptionOffersCount) {
        this.preAppliedSubscriptionOffersCount = preAppliedSubscriptionOffersCount;
    }
}
