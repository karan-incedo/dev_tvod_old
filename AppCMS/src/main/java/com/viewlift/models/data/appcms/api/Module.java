
package com.viewlift.models.data.appcms.api;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Module {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("ad")
    @Expose
    private Object ad;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("settings")
    @Expose
    private Settings settings;
    @SerializedName("filters")
    @Expose
    private Filters filters;
    @SerializedName("contentData")
    @Expose
    private List<ContentDatum> contentData = null;
    @SerializedName("moduleType")
    @Expose
    private String moduleType;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("title")
    @Expose
    private Object title;
    @SerializedName("metadataMap")
    @Expose
    private Object metadataMap;
    @SerializedName("viewType")
    @Expose
    private Object viewType;
    @SerializedName("menuLinks")
    @Expose
    private Object menuLinks;
    @SerializedName("supportedDeviceLinks")
    @Expose
    private Object supportedDeviceLinks;
    @SerializedName("searchText")
    @Expose
    private Object searchText;
    @SerializedName("navigation")
    @Expose
    private Object navigation;

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

    public Object getAd() {
        return ad;
    }

    public void setAd(Object ad) {
        this.ad = ad;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
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

    public Object getTitle() {
        return title;
    }

    public void setTitle(Object title) {
        this.title = title;
    }

    public Object getMetadataMap() {
        return metadataMap;
    }

    public void setMetadataMap(Object metadataMap) {
        this.metadataMap = metadataMap;
    }

    public Object getViewType() {
        return viewType;
    }

    public void setViewType(Object viewType) {
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

}
