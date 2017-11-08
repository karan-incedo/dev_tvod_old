package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.ComponentListSerializerDeserializer;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@UseStag
public class NavigationTabBar implements Serializable {

    @SerializedName("title")
    @Expose
    String title;

    public boolean isIsbackgroundSelectable() {
        return isbackgroundSelectable;
    }

    public void setIsbackgroundSelectable(boolean isbackgroundSelectable) {
        this.isbackgroundSelectable = isbackgroundSelectable;
    }

    public boolean isDefaultLaunch() {
        return isDefaultLaunch;
    }

    public void setDefaultLaunch(boolean defaultLaunch) {
        isDefaultLaunch = defaultLaunch;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<Component> components) {
        this.components = components;
    }

    @SerializedName("isbackgroundSelectable")
    @Expose
    boolean isbackgroundSelectable;

    @SerializedName("isDefaultLaunch")
    @Expose
    boolean isDefaultLaunch;

    @SerializedName("components")
    @Expose
    ArrayList<Component> components;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @SerializedName("icon")
    @Expose
    String icon;

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
}
