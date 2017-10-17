package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class Season_ implements Serializable {

    @SerializedName("id")
    @Expose
    Object id;

    @SerializedName("publishDate")
    @Expose
    Object publishDate;

    @SerializedName("updateDate")
    @Expose
    Object updateDate;

    @SerializedName("addedDate")
    @Expose
    Object addedDate;

    @SerializedName("permalink")
    @Expose
    Object permalink;

    @SerializedName("siteOwner")
    @Expose
    Object siteOwner;

    @SerializedName("registeredDate")
    @Expose
    Object registeredDate;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("episodes")
    @Expose
    List<ContentDatum> episodes = null;

    @SerializedName("description")
    @Expose
    Object description;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Object publishDate) {
        this.publishDate = publishDate;
    }

    public Object getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Object updateDate) {
        this.updateDate = updateDate;
    }

    public Object getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Object addedDate) {
        this.addedDate = addedDate;
    }

    public Object getPermalink() {
        return permalink;
    }

    public void setPermalink(Object permalink) {
        this.permalink = permalink;
    }

    public Object getSiteOwner() {
        return siteOwner;
    }

    public void setSiteOwner(Object siteOwner) {
        this.siteOwner = siteOwner;
    }

    public Object getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Object registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ContentDatum> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<ContentDatum> episodes) {
        this.episodes = episodes;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }
}
