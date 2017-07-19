package com.viewlift.models.data.appcms.watchlist;

/*
 * Created by Viewlift on 7/10/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class AppCMSAddToWatchlistResult {

    @SerializedName("userId")
    @Expose
    String userId;

    @SerializedName("contentId")
    @Expose
    String contentId;

    @SerializedName("contentType")
    @Expose
    String contentType;

    @SerializedName("siteOwner")
    @Expose
    String siteOwner;

    @SerializedName("actionId")
    @Expose
    String actionId;

    @SerializedName("action")
    @Expose
    String action;

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("addedDate")
    @Expose
    long addedDate;

    @SerializedName("updateDate")
    @Expose
    long updateDate;

    @SerializedName("user")
    @Expose
    Object user;

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

    public String getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }
}
