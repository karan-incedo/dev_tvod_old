package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Gist implements Serializable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("permalink")
    @Expose
    String permalink;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("logLine")
    @Expose
    String logLine;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("year")
    @Expose
    String year;

    @SerializedName("free")
    @Expose
    boolean free;

    @SerializedName("publishDate")
    @Expose
    long publishDate;

    @SerializedName("runtime")
    @Expose
    long runtime;

    @SerializedName("posterImageUrl")
    @Expose
    String posterImageUrl;

    @SerializedName("videoImageUrl")
    @Expose
    String videoImageUrl;

    @SerializedName("imageGist")
    @Expose
    ImageGist imageGist;

    @SerializedName("badgeImages")
    @Expose
    BadgeImages badgeImages;

    @SerializedName("addedDate")
    @Expose
    long addedDate;

    @SerializedName("updateDate")
    @Expose
    long updateDate;

    @SerializedName("primaryCategory")
    @Expose
    PrimaryCategory primaryCategory;

    @SerializedName("watchedTime")
    @Expose
    long watchedTime;

    @SerializedName("contentType")
    @Expose
    String contentType;

    @SerializedName("averageGrade")
    @Expose
    String averageGrade;

    @SerializedName("averageStarRating")
    @Expose
    float averageStarRating;

    @SerializedName("watchedPercentage")
    @Expose
    int watchedPercentage;

    @SerializedName("kisweEventId")
    @Expose
    String kisweEventId;

    String downloadStatus;
    /**
     * This is to store the url of the downloaded file
     */
    String localFileUrl;

    public String getMediaType() {
        return mediaType;
    }

    @SerializedName("mediaType")
    @Expose
    String mediaType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogLine() {
        return logLine;
    }

    public void setLogLine(String logLine) {
        this.logLine = logLine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean getFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public String getPosterImageUrl() {
        return posterImageUrl;
    }

    public void setPosterImageUrl(String posterImageUrl) {
        this.posterImageUrl = posterImageUrl;
    }

    public String getVideoImageUrl() {
        return videoImageUrl;
    }

    public void setVideoImageUrl(String videoImageUrl) {
        this.videoImageUrl = videoImageUrl;
    }

    public BadgeImages getBadgeImages() {
        return badgeImages;
    }

    public void setBadgeImages(BadgeImages badgeImages) {
        this.badgeImages = badgeImages;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public PrimaryCategory getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(PrimaryCategory primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public long getWatchedTime() {
        return watchedTime;
    }

    public void setWatchedTime(long watchedTime) {
        this.watchedTime = watchedTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(String averageGrade) {
        this.averageGrade = averageGrade;
    }

    public float getAverageStarRating() {
        return averageStarRating;
    }

    public void setAverageStarRating(float averageStarRating) {
        this.averageStarRating = averageStarRating;
    }

    public int getWatchedPercentage() {
        return watchedPercentage;
    }

    public void setWatchedPercentage(int watchedPercentage) {
        this.watchedPercentage = watchedPercentage;
    }

    public DownloadStatus getDownloadStatus() {
        if (downloadStatus != null) {
            return DownloadStatus.valueOf(downloadStatus);
        }
        return DownloadStatus.STATUS_PENDING;
    }

    public void setDownloadStatus(DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus.toString();
    }

    public String getLocalFileUrl() {
        return localFileUrl;
    }

    public void setLocalFileUrl(String localFileUrl) {
        this.localFileUrl = localFileUrl;
    }

    public ImageGist getImageGist() {
        return imageGist;
    }

    public void setImageGist(ImageGist imageGist) {
        this.imageGist = imageGist;
    }

    public String getKisweEventId() {
        return kisweEventId;
    }

    public void setKisweEventId(String kisweEventId) {
        this.kisweEventId = kisweEventId;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    boolean isAudioPlaying;

    public boolean isAudioPlaying() {
        return isAudioPlaying;
    }

    public void setAudioPlaying(boolean audioPlaying) {
        isAudioPlaying = audioPlaying;
    }
}
