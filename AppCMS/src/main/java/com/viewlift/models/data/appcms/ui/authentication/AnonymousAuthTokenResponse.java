package com.viewlift.models.data.appcms.ui.authentication;

/*
 * Created by Viewlift on 7/20/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class AnonymousAuthTokenResponse {

    @SerializedName("authorizationToken")
    @Expose
    String authorizationToken;

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }
}
