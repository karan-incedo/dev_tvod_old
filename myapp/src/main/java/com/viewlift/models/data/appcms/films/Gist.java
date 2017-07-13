
package com.viewlift.models.data.appcms.films;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gist {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("permalink")
    @Expose
    private String permalink;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("logLine")
    @Expose
    private String logLine;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("free")
    @Expose
    private Boolean free;
    @SerializedName("publishDate")
    @Expose
    private Long publishDate;
    @SerializedName("runtime")
    @Expose
    private Long runtime;
    @SerializedName("posterImageUrl")
    @Expose
    private String posterImageUrl;
    @SerializedName("videoImageUrl")
    @Expose
    private String videoImageUrl;
    @SerializedName("addedDate")
    @Expose
    private Long addedDate;
    @SerializedName("updateDate")
    @Expose
    private Long updateDate;
    @SerializedName("primaryCategory")
    @Expose
    private PrimaryCategory primaryCategory;
    @SerializedName("featuredTag")
    @Expose
    private FeaturedTag featuredTag;
    @SerializedName("watchedTime")
    @Expose
    private Long watchedTime;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("averageGrade")
    @Expose
    private String averageGrade;
    @SerializedName("averageStarRating")
    @Expose
    private Double averageStarRating;

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

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public Long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Long publishDate) {
        this.publishDate = publishDate;
    }

    public Long getRuntime() {
        return runtime;
    }

    public void setRuntime(Long runtime) {
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

    public Long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Long addedDate) {
        this.addedDate = addedDate;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public PrimaryCategory getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(PrimaryCategory primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public FeaturedTag getFeaturedTag() {
        return featuredTag;
    }

    public void setFeaturedTag(FeaturedTag featuredTag) {
        this.featuredTag = featuredTag;
    }

    public Long getWatchedTime() {
        return watchedTime;
    }

    public void setWatchedTime(Long watchedTime) {
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

    public Double getAverageStarRating() {
        return averageStarRating;
    }

    public void setAverageStarRating(Double averageStarRating) {
        this.averageStarRating = averageStarRating;
    }

}
