package com.viewlift.models.data.appcms.api;

/*
 * Created by Viewlift on 7/17/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class DeleteHistoryRequest {

    @SerializedName("userId")
    @Expose
    String userId;

    @SerializedName("contentId")
    @Expose
    String contentId;

    @SerializedName("contentType")
    @Expose
    String contentType;

    @SerializedName("position")
    @Expose
    long position;

    @SerializedName("contentIds")
    @Expose
    String contentIds = null;

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public String getContentIds() {
        return contentIds;
    }

    public void setContentIds(String contentIds) {
        this.contentIds = contentIds;
    }
}
