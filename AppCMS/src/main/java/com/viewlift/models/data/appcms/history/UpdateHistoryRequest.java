package com.viewlift.models.data.appcms.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 7/7/17.
 */

@UseStag
public class UpdateHistoryRequest {

    @SerializedName("userId")
    @Expose
    String userId;

    @SerializedName("videoId")
    @Expose
    String videoId;

    @SerializedName("watchedTime")
    @Expose
    long watchedTime;

    @SerializedName("siteOwner")
    @Expose
    String siteOwner;

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

    public long getWatchedTime() {
        return watchedTime;
    }

    public void setWatchedTime(long watchedTime) {
        this.watchedTime = watchedTime;
    }

    public String getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }
}
