package com.viewlift.models.data.appcms.api;

/**
 * Created by anas.azeem on 5/15/2018.
 * Owned by ViewLift, NYC
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class FeaturedTag {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("publishDate")
    @Expose
    private Object publishDate;
    @SerializedName("updateDate")
    @Expose
    private Object updateDate;
    @SerializedName("addedDate")
    @Expose
    private Object addedDate;
    @SerializedName("permalink")
    @Expose
    private String permalink;
    @SerializedName("siteOwner")
    @Expose
    private Object siteOwner;
    @SerializedName("registeredDate")
    @Expose
    private Object registeredDate;
    @SerializedName("originRegion")
    @Expose
    private String originRegion;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("titleOverride")
    @Expose
    private Object titleOverride;
    @SerializedName("promoImages")
    @Expose
    private Object promoImages;
    @SerializedName("poster")
    @Expose
    private Object poster;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("isVisible")
    @Expose
    private Object isVisible;
    @SerializedName("isOverlay")
    @Expose
    private Object isOverlay;
    @SerializedName("isFreeAccess")
    @Expose
    private Object isFreeAccess;
    @SerializedName("requireLogin")
    @Expose
    private Object requireLogin;
    @SerializedName("images")
    @Expose
    private Object images;
    @SerializedName("badgeImages")
    @Expose
    private Object badgeImages;
    @SerializedName("seo")
    @Expose
    private Object seo;
    @SerializedName("visible")
    @Expose
    private Object visible;

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

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
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

    public String getOriginRegion() {
        return originRegion;
    }

    public void setOriginRegion(String originRegion) {
        this.originRegion = originRegion;
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

    public Object getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Object isVisible) {
        this.isVisible = isVisible;
    }

    public Object getIsOverlay() {
        return isOverlay;
    }

    public void setIsOverlay(Object isOverlay) {
        this.isOverlay = isOverlay;
    }

    public Object getIsFreeAccess() {
        return isFreeAccess;
    }

    public void setIsFreeAccess(Object isFreeAccess) {
        this.isFreeAccess = isFreeAccess;
    }

    public Object getRequireLogin() {
        return requireLogin;
    }

    public void setRequireLogin(Object requireLogin) {
        this.requireLogin = requireLogin;
    }

    public Object getImages() {
        return images;
    }

    public void setImages(Object images) {
        this.images = images;
    }

    public Object getBadgeImages() {
        return badgeImages;
    }

    public void setBadgeImages(Object badgeImages) {
        this.badgeImages = badgeImages;
    }

    public Object getSeo() {
        return seo;
    }

    public void setSeo(Object seo) {
        this.seo = seo;
    }

    public Object getVisible() {
        return visible;
    }

    public void setVisible(Object visible) {
        this.visible = visible;
    }

}