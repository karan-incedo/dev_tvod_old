package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Settings {

    @SerializedName("lazyLoad")
    @Expose
    boolean lazyLoad;

    @SerializedName("hideTitle")
    @Expose
    boolean hideTitle;

    @SerializedName("hideDate")
    @Expose
    boolean hideDate;

    @SerializedName("displayDevices")
    @Expose
    Object displayDevices;

    @SerializedName("divClassName")
    @Expose
    Object divClassName;

    public boolean getLazyLoad() {
        return lazyLoad;
    }

    public void setLazyLoad(boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    public boolean getHideTitle() {
        return hideTitle;
    }

    public void setHideTitle(boolean hideTitle) {
        this.hideTitle = hideTitle;
    }

    public boolean getHideDate() {
        return hideDate;
    }

    public void setHideDate(boolean hideDate) {
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
