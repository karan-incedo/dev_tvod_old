package com.viewlift.models.data.appcms.history;

/*
 * Created by Viewlift on 7/5/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.ContentDatum;

public class Record {
    @SerializedName("contentResponse")
    @Expose
    private ContentResponse contentResponse;

    @SerializedName("userId")
    @Expose
    private String userID;

    @SerializedName("showQueue")
    @Expose
    private boolean showQueue;

    @SerializedName("addedDate")
    @Expose
    private long addedDate;

    @SerializedName("updateDate")
    @Expose
    private long updateDate;

    public ContentResponse getContentResponse() {
        return contentResponse;
    }

    public void setContentResponse(ContentResponse contentResponse) {
        this.contentResponse = contentResponse;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isShowQueue() {
        return showQueue;
    }

    public void setShowQueue(boolean showQueue) {
        this.showQueue = showQueue;
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

    public ContentDatum convertToContentDatum() {
        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setUserId(this.userID);
        contentDatum.setShowQueue(this.showQueue);
        contentDatum.setAddedDate(this.addedDate);
        contentDatum.setUpdateDate(this.updateDate);
        contentDatum.setGist(this.contentResponse.getGist());
        contentDatum.setGrade(this.contentResponse.getGrade());
        return contentDatum;
    }
}
