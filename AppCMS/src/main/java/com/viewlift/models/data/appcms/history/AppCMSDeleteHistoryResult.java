package com.viewlift.models.data.appcms.history;

/*
 * Created by Viewlift on 7/17/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class AppCMSDeleteHistoryResult {

    @SerializedName("addedDate")
    @Expose
    Object addedDate;

    @SerializedName("expirationTime")
    @Expose
    long expirationTime;

    @SerializedName("completed")
    @Expose
    boolean completed;

    @SerializedName("debugInfo")
    @Expose
    Object debugInfo;

    @SerializedName("hide")
    @Expose
    Object hide;

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("siteOwner")
    @Expose
    String siteOwner;

    @SerializedName("updateDate")
    @Expose
    long updateDate;

    @SerializedName("user")
    @Expose
    Object user;

    @SerializedName("userId")
    @Expose
    String userId;

    @SerializedName("video")
    @Expose
    Object video;

    @SerializedName("videoId")
    @Expose
    String videoId;

    @SerializedName("watchPercentage")
    @Expose
    int watchedPercentage;

    @SerializedName("watchedTime")
    @Expose
    int watchedTime;

    public Object getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Object addedDate) {
        this.addedDate = addedDate;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Object getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(Object debugInfo) {
        this.debugInfo = debugInfo;
    }

    public Object getHide() {
        return hide;
    }

    public void setHide(Object hide) {
        this.hide = hide;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Object getVideo() {
        return video;
    }

    public void setVideo(Object video) {
        this.video = video;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getWatchedPercentage() {
        return watchedPercentage;
    }

    public void setWatchedPercentage(int watchedPercentage) {
        this.watchedPercentage = watchedPercentage;
    }

    public int getWatchedTime() {
        return watchedTime;
    }

    public void setWatchedTime(int watchedTime) {
        this.watchedTime = watchedTime;
    }
}
