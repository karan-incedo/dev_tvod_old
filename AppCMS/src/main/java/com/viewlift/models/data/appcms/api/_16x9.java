package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by anas.azeem on 6/13/2018.
 * Owned by ViewLift, NYC
 */

@UseStag
public class _16x9 implements Serializable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("siteOwner")
    @Expose
    String siteOwner;

    @SerializedName("originRegion")
    @Expose
    String originRegion;

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

    @SerializedName("publishDate")
    @Expose
    long publishDate;

    @SerializedName("updateDate")
    @Expose
    long updateDate;

    @SerializedName("addedDate")
    @Expose
    long addedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(String siteOwner) {
        this.siteOwner = siteOwner;
    }

    public String getOriginRegion() {
        return originRegion;
    }

    public void setOriginRegion(String originRegion) {
        this.originRegion = originRegion;
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
}
