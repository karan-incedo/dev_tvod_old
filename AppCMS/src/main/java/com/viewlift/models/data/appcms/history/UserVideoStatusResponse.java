package com.viewlift.models.data.appcms.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by viewlift on 7/7/17.
 */

public class UserVideoStatusResponse {
    @SerializedName("contentId")
    @Expose
    String contentId;
    @SerializedName("userId")
    @Expose
    String userId;
    @SerializedName("isQueued")
    @Expose
    Boolean isQueued;
    @SerializedName("isWatched")
    @Expose
    Boolean isWatched;
    @SerializedName("watchedPercentage")
    @Expose
    Long watchedPercentage;
    @SerializedName("watchedTime")
    @Expose
    Long watchedTime;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getQueued() {
        return isQueued;
    }

    public void setQueued(Boolean queued) {
        isQueued = queued;
    }

    public Boolean getWatched() {
        return isWatched;
    }

    public void setWatched(Boolean watched) {
        isWatched = watched;
    }

    public Long getWatchedPercentage() {
        return watchedPercentage;
    }

    public void setWatchedPercentage(Long watchedPercentage) {
        this.watchedPercentage = watchedPercentage;
    }

    public Long getWatchedTime() {
        return watchedTime;
    }

    public void setWatchedTime(Long watchedTime) {
        this.watchedTime = watchedTime;
    }
}
