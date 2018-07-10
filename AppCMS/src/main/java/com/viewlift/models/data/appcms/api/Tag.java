package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Tag implements Serializable {

    @SerializedName("id")
    @Expose
    String id;

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

    @SerializedName("titleOverride")
    @Expose
    Object titleOverride;

    @SerializedName("promoImages")
    @Expose
    Object promoImages;

    @SerializedName("poster")
    @Expose
    Object poster;

    @SerializedName("description")
    @Expose
    Object description;

    @SerializedName("isVisible")
    @Expose
    String isVisible;

    @SerializedName("isOverlay")
    @Expose
    String isOverlay;

    @SerializedName("isFreeAccess")
    @Expose
    String isFreeAccess;

    @SerializedName("requireLogin")
    @Expose
    String requireLogin;

    @SerializedName("images")
    @Expose
    Image_16x9 images;

    @SerializedName("badgeImages")
    @Expose
    String badgeImages;

    @SerializedName("visible")
    @Expose
    String visible;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Object getTitleOverride() {
        return titleOverride;
    }

    public void setTitleOverride(Object titleOverride) {
        this.titleOverride = titleOverride;
    }

    public Object getPromoImages() {
        return promoImages;
    }

    public void setPromoImages(Object promoImages) {
        this.promoImages = promoImages;
    }

    public Object getPoster() {
        return poster;
    }

    public void setPoster(Object poster) {
        this.poster = poster;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }

    public String getIsOverlay() {
        return isOverlay;
    }

    public void setIsOverlay(String isOverlay) {
        this.isOverlay = isOverlay;
    }

    public String getIsFreeAccess() {
        return isFreeAccess;
    }

    public void setIsFreeAccess(String isFreeAccess) {
        this.isFreeAccess = isFreeAccess;
    }

    public String getRequireLogin() {
        return requireLogin;
    }

    public void setRequireLogin(String requireLogin) {
        this.requireLogin = requireLogin;
    }

    public Image_16x9 getImages() {
        return images;
    }

    public void setImages(Image_16x9 images) {
        this.images = images;
    }

    public String getBadgeImages() {
        return badgeImages;
    }

    public void setBadgeImages(String badgeImages) {
        this.badgeImages = badgeImages;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }
}
