package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class AppCMSAndroidUI {

    @SerializedName("navigation")
    @Expose
    Navigation navigation;

    @SerializedName("images")
    @Expose
    Images images;

    @SerializedName("pages")
    @Expose
    List<MetaPage> metaPages = null;

    @SerializedName("analytics")
    @Expose
    Analytics analytics;

    @SerializedName("version")
    @Expose
    String version;

    @SerializedName("appName")
    @Expose
    String appName;

    @SerializedName("shortAppName")
    @Expose
    String shortAppName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getShortAppName() {
        return shortAppName;
    }

    public void setShortAppName(String shortAppName) {
        this.shortAppName = shortAppName;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public List<MetaPage> getMetaPages() {
        return metaPages;
    }

    public void setMetaPages(List<MetaPage> metaPages) {
        this.metaPages = metaPages;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
