package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.sites.Credentials;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by viewlift on 8/11/17.
 */

@UseStag
public class GooglePlus implements Serializable {
    @SerializedName("signin")
    @Expose
    boolean signin;

    @SerializedName("googlePlusUrl")
    @Expose
    String googlePlusUrl;
    @SerializedName("apiKey")
    @Expose
    String apiKey;
    @SerializedName("authenticate")
    @Expose
    boolean authenticate;
    @SerializedName("credentials")
    @Expose
    Credentials credentials;
    public boolean isSignin() {
        return signin;
    }

    public void setSignin(boolean signin) {
        this.signin = signin;
    }
    public String getGooglePlusUrl() {
        return googlePlusUrl;
    }
    public void setGooglePlusUrl(String googlePlusUrl) {
        this.googlePlusUrl = googlePlusUrl;
    }
    public String getApiKey() {
        return apiKey;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    public boolean isAuthenticate() {
        return authenticate;
    }
    public void setAuthenticate(boolean authenticate) {
        this.authenticate = authenticate;
    }
    public Credentials getCredentials() {
        return credentials;
    }
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
}
