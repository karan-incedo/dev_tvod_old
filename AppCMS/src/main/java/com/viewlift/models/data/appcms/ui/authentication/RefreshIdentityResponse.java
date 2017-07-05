package com.viewlift.models.data.appcms.ui.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by viewlift on 7/5/17.
 */

public class RefreshIdentityResponse {
    @SerializedName("authorization_token")
    @Expose
    private String authorizationToken;
    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;
    @SerializedName("id")
    @Expose
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
