package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Bundles_library implements Serializable {
    @SerializedName("site")
    @Expose
    private String site;

    @SerializedName("addedDate")
    @Expose
    private String addedDate;

    @SerializedName("platform")
    @Expose
    private String platform;

    @SerializedName("scurrencyCodeite")
    @Expose
    private String currencyCode;

    @SerializedName("purchaseType")
    @Expose
    private String purchaseType;

    @SerializedName("countryCode")
    @Expose
    private String countryCode;

    @SerializedName("transactionStartDate")
    @Expose
    private String transactionStartDate;

    @SerializedName("purchaseStatus")
    @Expose
    private String purchaseStatus;

    @SerializedName("contentType")
    @Expose
    private String contentType;

    @SerializedName("updateDate")
    @Expose
    private String updateDate;

    @SerializedName("contentId")
    @Expose
    private String contentId;

    @SerializedName("videoQuality")
    @Expose
    private String videoQuality;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("transactionDateEpoch")
    @Expose
    private String transactionDateEpoch;

    @SerializedName("siteId")
    @Expose
    private String siteId;

    @SerializedName("videoId")
    @Expose
    private String videoId;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("seasonId")
    @Expose
    private String seasonId;

    @SerializedName("siteOwner")
    @Expose
    private String siteOwner;

    @SerializedName("paymentHandler")
    @Expose
    private String paymentHandler;

    @SerializedName("seriesId")
    @Expose
    private String seriesId;

    @SerializedName("gatewayChargeId")
    @Expose
    private String gatewayChargeId;

    public String getSite ()
    {
        return site;
    }

    public void setSite (String site)
    {
        this.site = site;
    }

    public String getAddedDate ()
    {
        return addedDate;
    }

    public void setAddedDate (String addedDate)
    {
        this.addedDate = addedDate;
    }

    public String getPlatform ()
    {
        return platform;
    }

    public void setPlatform (String platform)
    {
        this.platform = platform;
    }

    public String getCurrencyCode ()
    {
        return currencyCode;
    }

    public void setCurrencyCode (String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public String getPurchaseType ()
    {
        return purchaseType;
    }

    public void setPurchaseType (String purchaseType)
    {
        this.purchaseType = purchaseType;
    }

    public String getCountryCode ()
    {
        return countryCode;
    }

    public void setCountryCode (String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getTransactionStartDate ()
    {
        return transactionStartDate;
    }

    public void setTransactionStartDate (String transactionStartDate)
    {
        this.transactionStartDate = transactionStartDate;
    }

    public String getPurchaseStatus ()
    {
        return purchaseStatus;
    }

    public void setPurchaseStatus (String purchaseStatus)
    {
        this.purchaseStatus = purchaseStatus;
    }

    public String getContentType ()
    {
        return contentType;
    }

    public void setContentType (String contentType)
    {
        this.contentType = contentType;
    }

    public String getUpdateDate ()
    {
        return updateDate;
    }

    public void setUpdateDate (String updateDate)
    {
        this.updateDate = updateDate;
    }

    public String getContentId ()
    {
        return contentId;
    }

    public void setContentId (String contentId)
    {
        this.contentId = contentId;
    }

    public String getVideoQuality ()
    {
        return videoQuality;
    }

    public void setVideoQuality (String videoQuality)
    {
        this.videoQuality = videoQuality;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getTransactionDateEpoch ()
    {
        return transactionDateEpoch;
    }

    public void setTransactionDateEpoch (String transactionDateEpoch)
    {
        this.transactionDateEpoch = transactionDateEpoch;
    }

    public String getSiteId ()
    {
        return siteId;
    }

    public void setSiteId (String siteId)
    {
        this.siteId = siteId;
    }

    public String getVideoId ()
    {
        return videoId;
    }

    public void setVideoId (String videoId)
    {
        this.videoId = videoId;
    }

    public String getUserId ()
    {
        return userId;
    }

    public void setUserId (String userId)
    {
        this.userId = userId;
    }

    public String getSeasonId ()
    {
        return seasonId;
    }

    public void setSeasonId (String seasonId)
    {
        this.seasonId = seasonId;
    }

    public String getSiteOwner ()
    {
        return siteOwner;
    }

    public void setSiteOwner (String siteOwner)
    {
        this.siteOwner = siteOwner;
    }

    public String getPaymentHandler ()
    {
        return paymentHandler;
    }

    public void setPaymentHandler (String paymentHandler)
    {
        this.paymentHandler = paymentHandler;
    }

    public String getSeriesId ()
    {
        return seriesId;
    }

    public void setSeriesId (String seriesId)
    {
        this.seriesId = seriesId;
    }

    public String getGatewayChargeId ()
    {
        return gatewayChargeId;
    }

    public void setGatewayChargeId (String gatewayChargeId)
    {
        this.gatewayChargeId = gatewayChargeId;
    }


}

