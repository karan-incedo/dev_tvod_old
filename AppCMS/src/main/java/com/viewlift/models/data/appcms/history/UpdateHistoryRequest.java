package com.viewlift.models.data.appcms.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.inject.Singleton;

/**
 * Created by viewlift on 7/7/17.
 */

public class UpdateHistoryRequest {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("videoId")
    @Expose
    private String videoId;
    @SerializedName("watchedTime")
    @Expose
    private Long watchedTime;
    @SerializedName("siteOwner")
    @Expose
    private String siteOwner;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public Long getWatchedTime() {
        return watchedTime;
    }

    public void setWatchedTime(Long watchedTime) {
        this.watchedTime = watchedTime;
    }

    public String getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }
}
