package com.viewlift.models.data.appcms.watchlist;

/*
 * Created by Viewlift on 7/10/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppCMSAddToWatchlistResult {

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("contentId")
    @Expose
    private String contentId;

    @SerializedName("contentType")
    @Expose
    private String contentType;

    @SerializedName("siteOwner")
    @Expose
    private String siteOwner;

    @SerializedName("actionId")
    @Expose
    private String actionId;

    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("addedDate")
    @Expose
    private Long addedDate;

    @SerializedName("updateDate")
    @Expose
    private Long updateDate;

    @SerializedName("user")
    @Expose
    private Object user;

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

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }
}
