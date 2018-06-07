package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class Module implements Serializable,Cloneable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("ad")
    @Expose
    String ad;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("settings")
    @Expose
    Settings settings;

    @SerializedName("filters")
    @Expose
    Filters filters;

    @SerializedName("contentData")
    @Expose
    List<ContentDatum> contentData = null;

    @SerializedName("moduleType")
    @Expose
    String moduleType;

    @SerializedName("contentType")
    @Expose
    String contentType;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("metadataMap")
    @Expose
    Object metadataMap;

    @SerializedName("viewType")
    @Expose
    String viewType;

    @SerializedName("menuLinks")
    @Expose
    Object menuLinks;

    @SerializedName("supportedDeviceLinks")
    @Expose
    Object supportedDeviceLinks;

    @SerializedName("searchText")
    @Expose
    Object searchText;

    @SerializedName("navigation")
    @Expose
    Object navigation;

    @SerializedName("rawText")
    @Expose
    String rawText;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    public List<ContentDatum> getContentData() {
        return contentData;
    }



    public void setContentData(List<ContentDatum> contentData) {
        this.contentData = contentData;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getMetadataMap() {
        return metadataMap;
    }

    public void setMetadataMap(Object metadataMap) {
        this.metadataMap = metadataMap;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public Object getMenuLinks() {
        return menuLinks;
    }

    public void setMenuLinks(Object menuLinks) {
        this.menuLinks = menuLinks;
    }

    public Object getSupportedDeviceLinks() {
        return supportedDeviceLinks;
    }

    public void setSupportedDeviceLinks(Object supportedDeviceLinks) {
        this.supportedDeviceLinks = supportedDeviceLinks;
    }

    public Object getSearchText() {
        return searchText;
    }

    public void setSearchText(Object searchText) {
        this.searchText = searchText;
    }

    public Object getNavigation() {
        return navigation;
    }

    public void setNavigation(Object navigation) {
        this.navigation = navigation;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }
    public Object clone() {
        //shallow copy
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
