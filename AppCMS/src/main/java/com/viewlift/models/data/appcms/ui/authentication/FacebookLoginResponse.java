package com.viewlift.models.data.appcms.ui.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 7/6/17.
 */

@UseStag
public class FacebookLoginResponse {

    @SerializedName("authorizationToken")
    @Expose
    String authorizationToken;

    @SerializedName("refreshToken")
    @Expose
    String refreshToken;

    @SerializedName("userId")
    @Expose
    String userId;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("picture")
    @Expose
    String picture;

    @SerializedName("isSubscribed")
    @Expose
    boolean isSubscribed;

    @SerializedName("error")
    @Expose
    String error;

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
