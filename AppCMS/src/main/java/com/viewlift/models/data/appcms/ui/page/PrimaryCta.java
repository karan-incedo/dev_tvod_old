package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by viewlift on 11/2/17.
 */

@UseStag
public class PrimaryCta implements Serializable {
    @SerializedName("ctaText")
    @Expose
    String ctaText;


    @SerializedName("bannerText")
    @Expose
    String bannerText;


    @SerializedName("displayedPath")
    @Expose
    String displayedPath;

    @SerializedName("placement")
    @Expose
    String placement;

    @SerializedName("pageId")
    @Expose
    String pageId;

    @SerializedName("url")
    @Expose
    String url;

    public String getBannerText() {
        return bannerText;
    }

    public void setBannerText(String bannerText) {
        this.bannerText = bannerText;
    }


    public String getCtaText() {
        return ctaText;
    }

    public void setCtaText(String ctaText) {
        this.ctaText = ctaText;
    }

    public String getDisplayedPath() {
        return displayedPath;
    }

    public void setDisplayedPath(String displayedPath) {
        this.displayedPath = displayedPath;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
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
}
