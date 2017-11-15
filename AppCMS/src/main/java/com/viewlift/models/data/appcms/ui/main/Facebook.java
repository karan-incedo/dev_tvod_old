package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by viewlift on 8/11/17.
 */

@UseStag
public class Facebook implements Serializable {
    @SerializedName("url")
    @Expose
    String url;

    @SerializedName("appId")
    @Expose
    String appId;

    @SerializedName("authenticate")
    @Expose
    boolean authenticate;

    @SerializedName("secretKey")
    @Expose
    String secretKey;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(boolean authenticate) {
        this.authenticate = authenticate;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
