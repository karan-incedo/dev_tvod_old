package com.viewlift.casting.roku;

import java.net.URL;


public class RokuLaunchThreadParams {
    private int action;
    private int play_start;
    private URL url;
    private String userId;
    private String contentId;
    private String contentType;
    private String rokuAppId;

    public static final String CONTENT_TYPE_APP = "launch_app";
    public static final String CONTENT_TYPE_SHOW = "launch_show";
    public static final String CONTENT_TYPE_FILM = "launch_film";

    public RokuLaunchThreadParams() {
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return this.action;
    }

    public void setPlayStart(int play_start) {
        this.play_start = play_start;
    }

    public int getPlayStart() {
        return this.play_start;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return this.url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRokuAppId() {
        return rokuAppId;
    }

    public void setRokuAppId(String rokuAppId) {
        this.rokuAppId = rokuAppId;
    }
}
