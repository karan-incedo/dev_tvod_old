package com.viewlift.models.data.appcms.sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class AppCMSSite {

    @SerializedName("assetDetails")
    @Expose
    Object assetDetails;

    @SerializedName("gist")
    @Expose
    Gist gist;

    @SerializedName("settings")
    @Expose
    Object settings;

    @SerializedName("siteDetails")
    @Expose
    SiteDetails siteDetails;

    @SerializedName("notifications")
    @Expose
    Object notifications;

    @SerializedName("readWritePolicy")
    @Expose
    String readWritePolicy;

    @SerializedName("siteInternalName")
    @Expose
    String siteInternalName;

    @SerializedName("appAccess")
    @Expose
    Object appAccess;

    public Object getAssetDetails() {
        return assetDetails;
    }

    public void setAssetDetails(Object assetDetails) {
        this.assetDetails = assetDetails;
    }

    public Gist getGist() {
        return gist;
    }

    public void setGist(Gist gist) {
        this.gist = gist;
    }

    public Object getSettings() {
        return settings;
    }

    public void setSettings(Object settings) {
        this.settings = settings;
    }

    public SiteDetails getSiteDetails() {
        return siteDetails;
    }

    public void setSiteDetails(SiteDetails siteDetails) {
        this.siteDetails = siteDetails;
    }

    public Object getNotifications() {
        return notifications;
    }

    public void setNotifications(Object notifications) {
        this.notifications = notifications;
    }

    public String getReadWritePolicy() {
        return readWritePolicy;
    }

    public void setReadWritePolicy(String readWritePolicy) {
        this.readWritePolicy = readWritePolicy;
    }

    public void setSiteInternalName(String siteInternalName) {
        this.siteInternalName = siteInternalName;
    }

    public Object getAppAccess() {
        return appAccess;
    }

    public void setAppAccess(Object appAccess) {
        this.appAccess = appAccess;
    }
}
