package com.viewlift.models.data.appcms.api;

/*
 * Created by Viewlift on 7/10/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddToWatchlistRequest {

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("contentId")
    @Expose
    private String contentId;

    @SerializedName("contentType")
    @Expose
    private String contentType;

    @SerializedName("position")
    @Expose
    private Long position;

    @SerializedName("contentIds")
    @Expose
    private String contentIds = null;

    public void setContentIds(String contentIds) {
        this.contentIds = contentIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public String getContentIds() {
        return contentIds;
    }
}
