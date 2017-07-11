package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContentDatum {

    @SerializedName("gist")
    @Expose
    private Gist gist;

    @SerializedName("grade")
    @Expose
    private String grade;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("showQueue")
    @Expose
    private Boolean showQueue;

    @SerializedName("addedDate")
    @Expose
    private Long addedDate;

    @SerializedName("updateDate")
    @Expose
    private Long updateDate;

    @SerializedName("contentDetails")
    @Expose
    private ContentDetails contentDetails;

    @SerializedName("streamingInfo")
    @Expose
    private StreamingInfo streamingInfo;

    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;

    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;

    @SerializedName("external")
    @Expose
    private External external;

    @SerializedName("statistics")
    @Expose
    private Statistics statistics;

    @SerializedName("channels")
    @Expose
    private List<Object> channels = null;

    @SerializedName("creditBlocks")
    @Expose
    private List<CreditBlock> creditBlocks = null;

    @SerializedName("parentalRating")
    @Expose
    private String parentalRating;

    public Gist getGist() {
        return gist;
    }

    public void setGist(Gist gist) {
        this.gist = gist;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void setAddedDate(Long addedDate) {
        this.addedDate = addedDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public ContentDetails getContentDetails() {
        return contentDetails;
    }

    public void setContentDetails(ContentDetails contentDetails) {
        this.contentDetails = contentDetails;
    }

    public StreamingInfo getStreamingInfo() {
        return streamingInfo;
    }

    public void setStreamingInfo(StreamingInfo streamingInfo) {
        this.streamingInfo = streamingInfo;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public External getExternal() {
        return external;
    }

    public void setExternal(External external) {
        this.external = external;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public List<Object> getChannels() {
        return channels;
    }

    public void setChannels(List<Object> channels) {
        this.channels = channels;
    }

    public List<CreditBlock> getCreditBlocks() {
        return creditBlocks;
    }

    public void setCreditBlocks(List<CreditBlock> creditBlocks) {
        this.creditBlocks = creditBlocks;
    }

    public String getParentalRating() {
        return parentalRating;
    }

    public void setParentalRating(String parentalRating) {
        this.parentalRating = parentalRating;
    }
}
