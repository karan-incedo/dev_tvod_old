
package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Settings {

    @SerializedName("lazyLoad")
    @Expose
    private Boolean lazyLoad;
    @SerializedName("hideTitle")
    @Expose
    private Boolean hideTitle;
    @SerializedName("hideDate")
    @Expose
    private Boolean hideDate;
    @SerializedName("displayDevices")
    @Expose
    private Object displayDevices;
    @SerializedName("divClassName")
    @Expose
    private Object divClassName;

    public Boolean getLazyLoad() {
        return lazyLoad;
    }

    public void setLazyLoad(Boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    public Boolean getHideTitle() {
        return hideTitle;
    }

    public void setHideTitle(Boolean hideTitle) {
        this.hideTitle = hideTitle;
    }

    public Boolean getHideDate() {
        return hideDate;
    }

    public void setHideDate(Boolean hideDate) {
        this.hideDate = hideDate;
    }

    public Object getDisplayDevices() {
        return displayDevices;
    }

    public void setDisplayDevices(Object displayDevices) {
        this.displayDevices = displayDevices;
    }

    public Object getDivClassName() {
        return divClassName;
    }

    public void setDivClassName(Object divClassName) {
        this.divClassName = divClassName;
    }

}
