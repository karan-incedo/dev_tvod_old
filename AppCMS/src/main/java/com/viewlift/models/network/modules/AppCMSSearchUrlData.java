package com.viewlift.models.network.modules;

/**
 * Created by viewlift on 6/13/17.
 */

public class AppCMSSearchUrlData {
    private String baseUrl;
    private String siteName;

    public AppCMSSearchUrlData(String baseUrl, String siteName) {
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
