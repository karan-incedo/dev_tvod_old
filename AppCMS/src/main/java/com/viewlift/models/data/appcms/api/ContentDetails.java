package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@UseStag
public class ContentDetails implements Serializable {

    @SerializedName("autoGenerateRelated")
    @Expose
    boolean autoGenerateRelated;

    @SerializedName("partner")
    @Expose
    Object partner;

    @SerializedName("episode")
    @Expose
    int episode;

    @SerializedName("trailers")
    @Expose
    List<Trailer> trailers = null;

    @SerializedName("geoRestriction")
    @Expose
    String geoRestriction;

    @SerializedName("author")
    @Expose
    AuthorData author;



    @SerializedName("relatedVideoIds")
    @Expose
    List<String> relatedVideoIds = null;

    @SerializedName("creditBlocks")
    @Expose
    Object creditBlocks;

    @SerializedName("posterImage")
    @Expose
    PosterImage posterImage;

    @SerializedName("videoImage")
    @Expose
    VideoImage videoImage;

    @SerializedName("widgetImage")
    @Expose
    WidgetImage widgetImage;

    @SerializedName("androidPosterImage")
    @Expose
    Object androidPosterImage;

    @SerializedName("startDate")
    @Expose
    long startDate;

    @SerializedName("endDate")
    @Expose
    long endDate;

    @SerializedName("closedCaptions")
    @Expose
    ArrayList<ClosedCaptions> closedCaptions;

    @SerializedName("deviceControls")
    @Expose
    List<String> deviceControls = null;
    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("relatedArticleIds")
    @Expose
    List<String> relatedArticleIds;

    public boolean getAutoGenerateRelated() {
        return autoGenerateRelated;
    }

    public void setAutoGenerateRelated(boolean autoGenerateRelated) {
        this.autoGenerateRelated = autoGenerateRelated;
    }

    public Object getPartner() {
        return partner;
    }

    public void setPartner(Object partner) {
        this.partner = partner;
    }

    public int getEpisode() {
        return episode;
    }

    public void setEpisode(int episode) {
        this.episode = episode;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public String getGeoRestriction() {
        return geoRestriction;
    }

    public void setGeoRestriction(String geoRestriction) {
        this.geoRestriction = geoRestriction;
    }

    public AuthorData getAuthor() {
        return author;
    }

    public void setAuthor(AuthorData author) {
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

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public ArrayList<ClosedCaptions> getClosedCaptions() {
        return closedCaptions;
    }

    public void setClosedCaptions(ArrayList<ClosedCaptions> closedCaptions) {
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

    public boolean isAutoGenerateRelated() {
        return autoGenerateRelated;
    }

    public List<String> getRelatedArticleIds() {
        return relatedArticleIds;
    }

    public void setRelatedArticleIds(List<String> relatedArticleIds) {
        this.relatedArticleIds = relatedArticleIds;
    }


}
