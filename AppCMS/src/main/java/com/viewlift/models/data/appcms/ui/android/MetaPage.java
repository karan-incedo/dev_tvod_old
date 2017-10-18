package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class MetaPage implements Serializable {

    @SerializedName("Page-Name")
    @Expose
    String pageName;

    @SerializedName("Page-Type")
    @Expose
    String pageType;

    @SerializedName("Page-ID")
    @Expose
    String pageId;

    @SerializedName("Page-UI")
    @Expose
    String pageUI;

    @SerializedName("Page-API")
    @Expose
    String pageAPI;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getPageUI() {
        return pageUI;
    }

    public void setPageUI(String pageUI) {
        this.pageUI = pageUI;
    }

    public String getPageAPI() {
        return pageAPI;
    }

    public void setPageAPI(String pageAPI) {
        this.pageAPI = pageAPI;
    }
}
