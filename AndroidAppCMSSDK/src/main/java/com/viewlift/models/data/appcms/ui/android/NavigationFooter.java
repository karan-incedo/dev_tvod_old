
package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NavigationFooter {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("items")
    @Expose
    private List<Object> items = null;
    @SerializedName("pageId")
    @Expose
    private String pageId;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("anchor")
    @Expose
    private String anchor;
    @SerializedName("displayedName")
    @Expose
    private String displayedName;
    @SerializedName("accessLevels")
    @Expose
    private AccessLevels accessLevels;

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

    public String getDisplayedName() {
        return displayedName;
    }

    public void setDisplayedName(String displayedName) {
        this.displayedName = displayedName;
    }

    public AccessLevels getAccessLevels() {
        return accessLevels;
    }

    public void setAccessLevels(AccessLevels accessLevels) {
        this.accessLevels = accessLevels;
    }
}
