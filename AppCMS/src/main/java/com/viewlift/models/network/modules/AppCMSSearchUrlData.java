package com.viewlift.models.network.modules;

/**
 * Created by viewlift on 6/13/17.
 */

public class AppCMSSearchUrlData {
    private String baseUrl;
    private String siteName;
    private String apiKey;

    public AppCMSSearchUrlData(String baseUrl,
                               String siteName,
                               String apiKey) {
        this.baseUrl = baseUrl;
        this.siteName = siteName;
        this.apiKey = apiKey;
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

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
