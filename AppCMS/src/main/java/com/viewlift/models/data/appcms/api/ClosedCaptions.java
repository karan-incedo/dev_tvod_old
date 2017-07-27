package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by anas.azeem on 7/26/2017.
 * Owned by ViewLift, NYC
 */

@UseStag
public class ClosedCaptions {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("publishDate")
    @Expose
    private String publishDate;

    @SerializedName("updateDate")
    @Expose
    private String updateDate;

    @SerializedName("autoGenerateRelated")
    @Expose
    private String addedDate;

    @SerializedName("permalink")
    @Expose
    private String permalink;

    @SerializedName("siteOwner")
    @Expose
    private String siteOwner;

    @SerializedName("registeredDate")
    @Expose
    private String registeredDate;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("format")
    @Expose
    private String format;

    @SerializedName("language")
    @Expose
    private String language;

    @SerializedName("size")
    @Expose
    private float size;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }
}
