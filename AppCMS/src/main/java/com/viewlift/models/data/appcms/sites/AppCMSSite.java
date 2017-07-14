
package com.viewlift.models.data.appcms.sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppCMSSite {

    @SerializedName("assetDetails")
    @Expose
    private Object assetDetails;
    @SerializedName("gist")
    @Expose
    private Gist gist;
    @SerializedName("settings")
    @Expose
    private Object settings;
    @SerializedName("siteDetails")
    @Expose
    private SiteDetails siteDetails;
    @SerializedName("notifications")
    @Expose
    private Object notifications;
    @SerializedName("readWritePolicy")
    @Expose
    private String readWritePolicy;
    @SerializedName("siteInternalName")
    @Expose
    private Object siteInternalName;
    @SerializedName("appAccess")
    @Expose
    private Object appAccess;

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

    public Object getSiteInternalName() {
        return siteInternalName;
    }

    public void setSiteInternalName(Object siteInternalName) {
        this.siteInternalName = siteInternalName;
    }

    public Object getAppAccess() {
        return appAccess;
    }

    public void setAppAccess(Object appAccess) {
        this.appAccess = appAccess;
    }

}
