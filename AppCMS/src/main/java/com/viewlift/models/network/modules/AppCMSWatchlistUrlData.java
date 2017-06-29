package com.viewlift.models.network.modules;

/*
 * Created by Viewlift on 6/28/2017.
 */

public class AppCMSWatchlistUrlData {
    private String baseUrl;
    private String siteName;

    public AppCMSWatchlistUrlData(String baseUrl, String siteName) {
        this.baseUrl = baseUrl;
        this.siteName = siteName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
