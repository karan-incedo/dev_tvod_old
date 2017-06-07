
package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppCMSMain {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("accessKey")
    @Expose
    private String accessKey;
    @SerializedName("apiBaseUrl")
    @Expose
    private String apiBaseUrl;
    @SerializedName("pageEndpoint")
    @Expose
    private String pageEndpoint;
    @SerializedName("internalName")
    @Expose
    private String internalName;
    @SerializedName("faqUrl")
    @Expose
    private String faqUrl;
    @SerializedName("beacon")
    @Expose
    private Beacon beacon;
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("serviceType")
    @Expose
    private String serviceType;
    @SerializedName("domainName")
    @Expose
    private String domainName;
    @SerializedName("brand")
    @Expose
    private Brand brand;
    @SerializedName("content")
    @Expose
    private Content content;
    @SerializedName("images")
    @Expose
    private Images images;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("old_version")
    @Expose
    private String oldVersion;
    @SerializedName("Web")
    @Expose
    private String web;
    @SerializedName("iOS")
    @Expose
    private String iOS;
    @SerializedName("Android")
    @Expose
    private String android;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getPageEndpoint() {
        return pageEndpoint;
    }

    public void setPageEndpoint(String pageEndpoint) {
        this.pageEndpoint = pageEndpoint;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getFaqUrl() {
        return faqUrl;
    }

    public void setFaqUrl(String faqUrl) {
        this.faqUrl = faqUrl;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getIOS() {
        return iOS;
    }

    public void setIOS(String iOS) {
        this.iOS = iOS;
    }

    public String getAndroid() {
        return android;
    }

    public void setAndroid(String android) {
        this.android = android;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public String getiOS() {
        return iOS;
    }

    public void setiOS(String iOS) {
        this.iOS = iOS;
    }
}
