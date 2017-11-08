package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class NavigationPrimary implements Serializable {

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("items")
    @Expose
    List<Object> items = null;

    @SerializedName("pageId")
    @Expose
    String pageId;

    @SerializedName("url")
    @Expose
    String url;

    @SerializedName("anchor")
    @Expose
    String anchor;

    @SerializedName("displayedPath")
    @Expose
    String displayedPath;

    @SerializedName("accessLevels")
    @Expose
    AccessLevels accessLevels;

    @SerializedName("pagePath")
    @Expose
    String pagePath;
    @SerializedName("icon")
    @Expose
    String icon;
    @SerializedName("platforms")
    @Expose
    Platforms platforms;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Object> getItems() {
        return items;
    }

    public void setItems(List<Object> items) {
        this.items = items;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getDisplayedPath() {
        return displayedPath;
    }

    public void setDisplayedPath(String displayedPath) {
        this.displayedPath = displayedPath;
    }

    public AccessLevels getAccessLevels() {
        return accessLevels;
    }

    public void setAccessLevels(AccessLevels accessLevels) {
        this.accessLevels = accessLevels;
    }
    public String getPagePath() {
        return pagePath;
    }
    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public Platforms getPlatforms() {
        return platforms;
    }
    public void setPlatforms(Platforms platforms) {
        this.platforms = platforms;
    }
}
