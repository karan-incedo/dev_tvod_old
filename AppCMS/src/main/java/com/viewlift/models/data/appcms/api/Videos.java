package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class Videos implements Serializable {


    @SerializedName("imageGist")
    @Expose
    private ImageGist imageGist;

    @SerializedName("runtime")
    @Expose
    private String runtime;

    @SerializedName("purchaseStatus")
    @Expose
    private String purchaseStatus;

    @SerializedName("contentType")
    @Expose
    private String contentType;

    @SerializedName("contentId")
    @Expose
    private String contentId;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("free")
    @Expose
    private String free;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("transactionDateEpoch")
    @Expose
    private String transactionDateEpoch;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("videoId")
    @Expose
    private String videoId;

    @SerializedName("seasonId")
    @Expose
    private String seasonId;

    @SerializedName("year")
    @Expose
    private String year;

    @SerializedName("paymentHandler")
    @Expose
    private String paymentHandler;

    @SerializedName("siteOwner")
    @Expose
    private String siteOwner;

    @SerializedName("videoImageUrl")
    @Expose
    private String videoImageUrl;

    @SerializedName("publishDate")
    @Expose
    private String publishDate;

    @SerializedName("gatewayChargeId")
    @Expose
    private String gatewayChargeId;

    @SerializedName("site")
    @Expose
    private String site;

    @SerializedName("platform")
    @Expose
    private String platform;

    @SerializedName("addedDate")
    @Expose
    private String addedDate;

    @SerializedName("posterImageUrl")
    @Expose
    private String posterImageUrl;

    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;

    @SerializedName("primaryCategory")
    @Expose
    private PrimaryCategory primaryCategory;

    @SerializedName("isLiveStream")
    @Expose
    private String isLiveStream;

    @SerializedName("purchaseType")
    @Expose
    private String purchaseType;

    @SerializedName("transactionStartDate")
    @Expose
    private String transactionStartDate;

    @SerializedName("countryCode")
    @Expose
    private String countryCode;

    @SerializedName("isTrailer")
    @Expose
    private String isTrailer;

    @SerializedName("logLine")
    @Expose
    private String logLine;

    @SerializedName("videoQuality")
    @Expose
    private String videoQuality;

    @SerializedName("updateDate")
    @Expose
    private String updateDate;

    @SerializedName("siteId")
    @Expose
    private String siteId;

    @SerializedName("permalink")
    @Expose
    private String permalink;

    @SerializedName("averageGrade")
    @Expose
    private String averageGrade;

    @SerializedName("averageStarRating")
    @Expose
    private String averageStarRating;

    @SerializedName("watchedTime")
    @Expose
    private String watchedTime;

    @SerializedName("seriesId")
    @Expose
    private String seriesId;

    public ImageGist getImageGist ()
    {
        return imageGist;
    }

    public void setImageGist (ImageGist imageGist)
    {
        this.imageGist = imageGist;
    }

    public String getRuntime ()
    {
        return runtime;
    }

    public void setRuntime (String runtime)
    {
        this.runtime = runtime;
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

    public String getContentId ()
    {
        return contentId;
    }

    public void setContentId (String contentId)
    {
        this.contentId = contentId;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getFree ()
    {
        return free;
    }

    public void setFree (String free)
    {
        this.free = free;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getTransactionDateEpoch ()
    {
        return transactionDateEpoch;
    }

    public void setTransactionDateEpoch (String transactionDateEpoch)
    {
        this.transactionDateEpoch = transactionDateEpoch;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getUserId ()
    {
        return userId;
    }

    public void setUserId (String userId)
    {
        this.userId = userId;
    }

    public String getVideoId ()
    {
        return videoId;
    }

    public void setVideoId (String videoId)
    {
        this.videoId = videoId;
    }

    public String getSeasonId ()
    {
        return seasonId;
    }

    public void setSeasonId (String seasonId)
    {
        this.seasonId = seasonId;
    }

    public String getYear ()
    {
        return year;
    }

    public void setYear (String year)
    {
        this.year = year;
    }

    public String getPaymentHandler ()
    {
        return paymentHandler;
    }

    public void setPaymentHandler (String paymentHandler)
    {
        this.paymentHandler = paymentHandler;
    }

    public String getSiteOwner ()
    {
        return siteOwner;
    }

    public void setSiteOwner (String siteOwner)
    {
        this.siteOwner = siteOwner;
    }

    public String getVideoImageUrl ()
    {
        return videoImageUrl;
    }

    public void setVideoImageUrl (String videoImageUrl)
    {
        this.videoImageUrl = videoImageUrl;
    }

    public String getPublishDate ()
    {
        return publishDate;
    }

    public void setPublishDate (String publishDate)
    {
        this.publishDate = publishDate;
    }

    public String getGatewayChargeId ()
    {
        return gatewayChargeId;
    }

    public void setGatewayChargeId (String gatewayChargeId)
    {
        this.gatewayChargeId = gatewayChargeId;
    }

    public String getSite ()
    {
        return site;
    }

    public void setSite (String site)
    {
        this.site = site;
    }

    public String getPlatform ()
    {
        return platform;
    }

    public void setPlatform (String platform)
    {
        this.platform = platform;
    }

    public String getAddedDate ()
    {
        return addedDate;
    }

    public void setAddedDate (String addedDate)
    {
        this.addedDate = addedDate;
    }

    public String getPosterImageUrl ()
    {
        return posterImageUrl;
    }

    public void setPosterImageUrl (String posterImageUrl)
    {
        this.posterImageUrl = posterImageUrl;
    }

    public String getCurrencyCode ()
    {
        return currencyCode;
    }

    public void setCurrencyCode (String currencyCode)
    {
        this.currencyCode = currencyCode;
    }

    public PrimaryCategory getPrimaryCategory ()
    {
        return primaryCategory;
    }

    public void setPrimaryCategory (PrimaryCategory primaryCategory)
    {
        this.primaryCategory = primaryCategory;
    }

    public String getIsLiveStream ()
    {
        return isLiveStream;
    }

    public void setIsLiveStream (String isLiveStream)
    {
        this.isLiveStream = isLiveStream;
    }

    public String getPurchaseType ()
    {
        return purchaseType;
    }

    public void setPurchaseType (String purchaseType)
    {
        this.purchaseType = purchaseType;
    }

    public String getTransactionStartDate ()
    {
        return transactionStartDate;
    }

    public void setTransactionStartDate (String transactionStartDate)
    {
        this.transactionStartDate = transactionStartDate;
    }

    public String getCountryCode ()
    {
        return countryCode;
    }

    public void setCountryCode (String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getIsTrailer ()
    {
        return isTrailer;
    }

    public void setIsTrailer (String isTrailer)
    {
        this.isTrailer = isTrailer;
    }

    public String getLogLine ()
    {
        return logLine;
    }

    public void setLogLine (String logLine)
    {
        this.logLine = logLine;
    }

    public String getVideoQuality ()
    {
        return videoQuality;
    }

    public void setVideoQuality (String videoQuality)
    {
        this.videoQuality = videoQuality;
    }

    public String getUpdateDate ()
    {
        return updateDate;
    }

    public void setUpdateDate (String updateDate)
    {
        this.updateDate = updateDate;
    }

    public String getSiteId ()
    {
        return siteId;
    }

    public void setSiteId (String siteId)
    {
        this.siteId = siteId;
    }

    public String getPermalink ()
    {
        return permalink;
    }

    public void setPermalink (String permalink)
    {
        this.permalink = permalink;
    }

    public String getAverageGrade ()
    {
        return averageGrade;
    }

    public void setAverageGrade (String averageGrade)
    {
        this.averageGrade = averageGrade;
    }

    public String getAverageStarRating ()
    {
        return averageStarRating;
    }

    public void setAverageStarRating (String averageStarRating)
    {
        this.averageStarRating = averageStarRating;
    }

    public String getWatchedTime ()
    {
        return watchedTime;
    }

    public void setWatchedTime (String watchedTime)
    {
        this.watchedTime = watchedTime;
    }

    public String getSeriesId ()
    {
        return seriesId;
    }

    public void setSeriesId (String seriesId)
    {
        this.seriesId = seriesId;
    }

}

