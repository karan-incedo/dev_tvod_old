
package com.viewlift.models.data.appcms.api;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContentDetails {

    @SerializedName("autoGenerateRelated")
    @Expose
    private Boolean autoGenerateRelated;
    @SerializedName("partner")
    @Expose
    private Object partner;
    @SerializedName("episode")
    @Expose
    private Integer episode;
    @SerializedName("trailers")
    @Expose
    private List<Object> trailers = null;
    @SerializedName("geoRestriction")
    @Expose
    private String geoRestriction;
    @SerializedName("author")
    @Expose
    private Object author;
    @SerializedName("relatedVideoIds")
    @Expose
    private List<String> relatedVideoIds = null;
    @SerializedName("creditBlocks")
    @Expose
    private Object creditBlocks;
    @SerializedName("posterImage")
    @Expose
    private PosterImage posterImage;
    @SerializedName("videoImage")
    @Expose
    private VideoImage videoImage;
    @SerializedName("widgetImage")
    @Expose
    private WidgetImage widgetImage;
    @SerializedName("androidPosterImage")
    @Expose
    private Object androidPosterImage;
    @SerializedName("startDate")
    @Expose
    private Object startDate;
    @SerializedName("endDate")
    @Expose
    private Object endDate;
    @SerializedName("closedCaptions")
    @Expose
    private Object closedCaptions;
    @SerializedName("deviceControls")
    @Expose
    private List<String> deviceControls = null;
    @SerializedName("status")
    @Expose
    private String status;

    public Boolean getAutoGenerateRelated() {
        return autoGenerateRelated;
    }

    public void setAutoGenerateRelated(Boolean autoGenerateRelated) {
        this.autoGenerateRelated = autoGenerateRelated;
    }

    public Object getPartner() {
        return partner;
    }

    public void setPartner(Object partner) {
        this.partner = partner;
    }

    public Integer getEpisode() {
        return episode;
    }

    public void setEpisode(Integer episode) {
        this.episode = episode;
    }

    public List<Object> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Object> trailers) {
        this.trailers = trailers;
    }

    public String getGeoRestriction() {
        return geoRestriction;
    }

    public void setGeoRestriction(String geoRestriction) {
        this.geoRestriction = geoRestriction;
    }

    public Object getAuthor() {
        return author;
    }

    public void setAuthor(Object author) {
        this.author = author;
    }

    public List<String> getRelatedVideoIds() {
        return relatedVideoIds;
    }

    public void setRelatedVideoIds(List<String> relatedVideoIds) {
        this.relatedVideoIds = relatedVideoIds;
    }

    public Object getCreditBlocks() {
        return creditBlocks;
    }

    public void setCreditBlocks(Object creditBlocks) {
        this.creditBlocks = creditBlocks;
    }

    public PosterImage getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(PosterImage posterImage) {
        this.posterImage = posterImage;
    }

    public VideoImage getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(VideoImage videoImage) {
        this.videoImage = videoImage;
    }

    public WidgetImage getWidgetImage() {
        return widgetImage;
    }

    public void setWidgetImage(WidgetImage widgetImage) {
        this.widgetImage = widgetImage;
    }

    public Object getAndroidPosterImage() {
        return androidPosterImage;
    }

    public void setAndroidPosterImage(Object androidPosterImage) {
        this.androidPosterImage = androidPosterImage;
    }

    public Object getStartDate() {
        return startDate;
    }

    public void setStartDate(Object startDate) {
        this.startDate = startDate;
    }

    public Object getEndDate() {
        return endDate;
    }

    public void setEndDate(Object endDate) {
        this.endDate = endDate;
    }

    public Object getClosedCaptions() {
        return closedCaptions;
    }

    public void setClosedCaptions(Object closedCaptions) {
        this.closedCaptions = closedCaptions;
    }

    public List<String> getDeviceControls() {
        return deviceControls;
    }

    public void setDeviceControls(List<String> deviceControls) {
        this.deviceControls = deviceControls;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
