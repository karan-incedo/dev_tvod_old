package com.viewlift.models.data.appcms.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 7/7/17.
 */

@UseStag
public class UserVideoStatusResponse {

    @SerializedName("contentId")
    @Expose
    String contentId;

    @SerializedName("userId")
    @Expose
    String userId;

    @SerializedName("isQueued")
    @Expose
    boolean isQueued;

    @SerializedName("isWatched")
    @Expose
    boolean isWatched;

    @SerializedName("watchedPercentage")
    @Expose
    long watchedPercentage;

    @SerializedName("watchedTime")
    @Expose
    long watchedTime;

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

    public boolean getQueued() {
        return isQueued;
    }

    public void setQueued(boolean queued) {
        isQueued = queued;
    }

    public boolean getWatched() {
        return isWatched;
    }

    public void setWatched(boolean watched) {
        isWatched = watched;
    }

    public long getWatchedPercentage() {
        return watchedPercentage;
    }

    public void setWatchedPercentage(long watchedPercentage) {
        this.watchedPercentage = watchedPercentage;
    }

    public long getWatchedTime() {
        return watchedTime;
    }

    public void setWatchedTime(long watchedTime) {
        this.watchedTime = watchedTime;
    }
}
