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
    String publishDate;

    @SerializedName("runtime")
    @Expose
    long runtime;

    public boolean isFree() {
        return isFree;
    }

    @SerializedName("isFree")
    @Expose
    boolean isFree;

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
    String addedDate;

    @SerializedName("updateDate")
    @Expose
    String updateDate;

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

    @SerializedName("mediaType")
    @Expose
    String mediaType;

    String artistName;
    String directorName;

    String downloadStatus;
    boolean isAudioPlaying;
    long currentPlayingPosition;
    Boolean isCastingConnected;
    /**
     * This is to store the url of the downloaded file
     */
    String localFileUrl;

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

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
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

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
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

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isAudioPlaying() {
        return isAudioPlaying;
    }

    public void setAudioPlaying(boolean audioPlaying) {
        isAudioPlaying = audioPlaying;
    }

    public long getCurrentPlayingPosition() {
        return currentPlayingPosition;
    }

    public void setCurrentPlayingPosition(long currentPlayingPosition) {
        this.currentPlayingPosition = currentPlayingPosition;
    }

    public Boolean getCastingConnected() {
        return isCastingConnected;
    }

    public void setCastingConnected(Boolean castingConnected) {
        isCastingConnected = castingConnected;
    }


    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }
}
