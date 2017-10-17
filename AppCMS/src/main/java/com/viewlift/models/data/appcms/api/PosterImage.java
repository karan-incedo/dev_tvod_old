package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class PosterImage implements Serializable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("publishDate")
    @Expose
    long publishDate;

    @SerializedName("updateDate")
    @Expose
    long updateDate;

    @SerializedName("addedDate")
    @Expose
    long addedDate;

    @SerializedName("siteOwner")
    @Expose
    String siteOwner;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("url")
    @Expose
    String url;

    @SerializedName("secureUrl")
    @Expose
    String secureUrl;

    @SerializedName("imageTag")
    @Expose
    String imageTag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public String getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecureUrl() {
        return secureUrl;
    }

    public void setSecureUrl(String secureUrl) {
        this.secureUrl = secureUrl;
    }

    public String getImageTag() {
        return imageTag;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }
}
