package com.viewlift.models.data.appcms.downloads;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DownloadClosedCaptionRealm extends RealmObject {

    private String id;
    private String publishDate;
    private String updateDate;
    private String addedDate;
    private String permalink;
    private String siteOwner;
    private String registeredDate;

    private String url;

    private String format;
    private String language;
    private float size;
    private String gistId;

    @PrimaryKey
    private long ccFileEnqueueId;

    public long getCcFileEnqueueId() {
        return ccFileEnqueueId;
    }

    public void setCcFileEnqueueId(long ccFileEnqueueId) {
        this.ccFileEnqueueId = ccFileEnqueueId;
    }

    public String getGistId() {
        return gistId;
    }

    public void setGistId(String gistId) {
        this.gistId = gistId;
    }

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
