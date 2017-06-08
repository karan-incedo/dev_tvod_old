
package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PosterImage {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("publishDate")
    @Expose
    private Integer publishDate;
    @SerializedName("updateDate")
    @Expose
    private Integer updateDate;
    @SerializedName("addedDate")
    @Expose
    private Integer addedDate;
    @SerializedName("siteOwner")
    @Expose
    private String siteOwner;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("secureUrl")
    @Expose
    private String secureUrl;
    @SerializedName("imageTag")
    @Expose
    private String imageTag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Integer publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Integer updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Integer addedDate) {
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
