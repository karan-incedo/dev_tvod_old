package com.viewlift.models.data.appcms.photogallery;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.Category;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.ContentDetails;
import com.viewlift.models.data.appcms.api.CreditBlock;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.PhotoGalleryData;
import com.viewlift.models.data.appcms.api.StreamingInfo;
import com.viewlift.models.data.appcms.api.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ram.kailash on 2/14/2018.
 */

public class AppCMSPhotoGalleryResult {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("gist")
    @Expose
    Gist gist;

    @SerializedName("contentDetails")
    ContentDetails contentDetails;

    @SerializedName("permalink")
    @Expose
    String permalink;

    @SerializedName("tags")
    @Expose
    List<Tag> tags = null;

    @SerializedName("categories")
    @Expose
    List<Category> categories = null;

    @SerializedName("creditBlocks")
    @Expose
    List<CreditBlock> creditBlocks = null;

    @SerializedName("streamingInfo")
    @Expose
    StreamingInfo streamingInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<CreditBlock> getCreditBlocks() {
        return creditBlocks;
    }

    public void setCreditBlocks(List<CreditBlock> creditBlocks) {
        this.creditBlocks = creditBlocks;
    }

    public StreamingInfo getStreamingInfo() {
        return streamingInfo;
    }

    public void setStreamingInfo(StreamingInfo streamingInfo) {
        this.streamingInfo = streamingInfo;
    }

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setGist(this.gist);
        contentDatum.setId(this.id);
        contentDatum.setStreamingInfo(this.streamingInfo);
        contentDatum.setContentDetails(this.contentDetails);
        contentDatum.setCategories(this.categories);
        contentDatum.setTags(this.tags);
        data.add(contentDatum);
        if (getStreamingInfo() != null) {
            for(int i = 0; i < getStreamingInfo().getPhotogalleryAssets().size(); i++){
                PhotoGalleryData p = getStreamingInfo().getPhotogalleryAssets().get(i);
                Gist gist=new Gist();
                gist.setId(p.getId());
                gist.setSelectedPosition(i == 0 ? true : false);
                gist.setVideoImageUrl(p.getUrl() != null ? p.getUrl() : "");
                ContentDatum cd=new ContentDatum();
                cd.setGist(gist);
                data.add(cd);
            }
        }
        module.setContentData(data);
        appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }
}
