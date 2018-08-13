package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AppCMSTransactionDataValue {

    @SerializedName("updateDate")
    @Expose
    private String updateDate;

    @SerializedName("addedDate")
    @Expose
    private String addedDate;

    @SerializedName("gatewayChargeId")
    @Expose
    private String gatewayChargeId;

    @SerializedName("contentId")
    @Expose
    private String contentId;

    @SerializedName("transactionStartDate")
    @Expose
    private String transactionStartDate;

    @SerializedName("videoId")
    @Expose
    private String videoId;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("platform")
    @Expose
    private String platform;

    @SerializedName("purchaseType")
    @Expose
    private String purchaseType;

    @SerializedName("videoQuality")
    @Expose
    private String videoQuality;

    @SerializedName("site")
    @Expose
    private String site;

    @SerializedName("countryCode")
    @Expose
    private String countryCode;

    @SerializedName("paymentHandler")
    @Expose
    private String paymentHandler;

    @SerializedName("siteId")
    @Expose
    private String siteId;

    @SerializedName("siteOwner")
    @Expose
    private String siteOwner;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("contentType")
    @Expose
    private String contentType;

    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getGatewayChargeId() {
        return gatewayChargeId;
    }

    public void setGatewayChargeId(String gatewayChargeId) {
        this.gatewayChargeId = gatewayChargeId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getTransactionStartDate() {
        return transactionStartDate;
    }

    public void setTransactionStartDate(String transactionStartDate) {
        this.transactionStartDate = transactionStartDate;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public String getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(String videoQuality) {
        this.videoQuality = videoQuality;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPaymentHandler() {
        return paymentHandler;
    }

    public void setPaymentHandler(String paymentHandler) {
        this.paymentHandler = paymentHandler;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
