package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Item {

    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;

    @SerializedName("visible")
    @Expose
    private Boolean visible;

    @SerializedName("plansMetaData")
    @Expose
    private String plansMetaData;

    @SerializedName("countryCode")
    @Expose
    private List<Object> countryCode = null;

    @SerializedName("acceptableSubscriptionOffers")
    @Expose
    private List<AcceptableSubscriptionOffer> acceptableSubscriptionOffers = null;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("billingCyclePeriodType")
    @Expose
    private String billingCyclePeriodType;

    @SerializedName("maxConnectedDevices")
    @Expose
    private Integer maxConnectedDevices;

    @SerializedName("isPlanSubscribableBasedOnDate")
    @Expose
    private Boolean isPlanSubscribableBasedOnDate;

    @SerializedName("preAppliedSubscriptionOffers")
    @Expose
    private List<PreAppliedSubscriptionOffer> preAppliedSubscriptionOffers = null;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("maxSimultaneousStreams")
    @Expose
    private Integer maxSimultaneousStreams;

    @SerializedName("schedule")
    @Expose
    private Schedule schedule;

    @SerializedName("recurringPayment")
    @Expose
    private RecurringPayment recurringPayment;

    @SerializedName("billingFrequencyType")
    @Expose
    private Boolean billingFrequencyType;

    @SerializedName("deviceMetaData")
    @Expose
    private String deviceMetaData;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("billingCyclePeriodTypeFrequencyLabel")
    @Expose
    private String billingCyclePeriodTypeFrequencyLabel;

    @SerializedName("billingCyclePeriodMultiplier")
    @Expose
    private Integer billingCyclePeriodMultiplier;

    @SerializedName("acceptableOffersCount")
    @Expose
    private Integer acceptableOffersCount;

    @SerializedName("identifier")
    @Expose
    private String identifier;

    @SerializedName("preAppliedSubscriptionOffersCount")
    @Expose
    private Integer preAppliedSubscriptionOffersCount;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
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

    public Integer getMaxConnectedDevices() {
        return maxConnectedDevices;
    }

    public void setMaxConnectedDevices(Integer maxConnectedDevices) {
        this.maxConnectedDevices = maxConnectedDevices;
    }

    public Boolean getIsPlanSubscribableBasedOnDate() {
        return isPlanSubscribableBasedOnDate;
    }

    public void setIsPlanSubscribableBasedOnDate(Boolean isPlanSubscribableBasedOnDate) {
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

    public Integer getMaxSimultaneousStreams() {
        return maxSimultaneousStreams;
    }

    public void setMaxSimultaneousStreams(Integer maxSimultaneousStreams) {
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

    public Boolean getBillingFrequencyType() {
        return billingFrequencyType;
    }

    public void setBillingFrequencyType(Boolean billingFrequencyType) {
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

    public Integer getBillingCyclePeriodMultiplier() {
        return billingCyclePeriodMultiplier;
    }

    public void setBillingCyclePeriodMultiplier(Integer billingCyclePeriodMultiplier) {
        this.billingCyclePeriodMultiplier = billingCyclePeriodMultiplier;
    }

    public Integer getAcceptableOffersCount() {
        return acceptableOffersCount;
    }

    public void setAcceptableOffersCount(Integer acceptableOffersCount) {
        this.acceptableOffersCount = acceptableOffersCount;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getPreAppliedSubscriptionOffersCount() {
        return preAppliedSubscriptionOffersCount;
    }

    public void setPreAppliedSubscriptionOffersCount(Integer preAppliedSubscriptionOffersCount) {
        this.preAppliedSubscriptionOffersCount = preAppliedSubscriptionOffersCount;
    }
}
