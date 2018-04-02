package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anas.azeem on 3/12/2018.
 * Owned by ViewLift, NYC
 */

//@UseStag
public class AppCMSContentDetail {

    @SerializedName("gist")
    @Expose
    private Gist gist;
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
    @SerializedName("parentalRating")
    @Expose
    private String parentalRating;

    public Gist getGist() {
        return gist;
    }

    public void setGist(Gist gist) {
        this.gist = gist;
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

    public String getParentalRating() {
        return parentalRating;
    }

    public void setParentalRating(String parentalRating) {
        this.parentalRating = parentalRating;
    }

    public ContentDatum convertToContentDatum(){
        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setGist(this.gist);
        contentDatum.setContentDetails(this.contentDetails);
        contentDatum.setStreamingInfo(this.streamingInfo);
        contentDatum.setCategories(this.categories);
        contentDatum.setTags(this.tags);
        contentDatum.setExternal(this.external);
        contentDatum.setStatistics(this.statistics);
        contentDatum.setChannels(this.channels);
        contentDatum.setParentalRating(this.parentalRating);
        return contentDatum;
    }

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id, String moduleType) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();
        data.add(this.convertToContentDatum());

        module.setContentData(data);
        module.setModuleType(moduleType);
        appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }
}
