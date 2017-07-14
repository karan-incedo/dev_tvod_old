package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by viewlift on 7/6/17.
 */

public class AccessLevels {
    @SerializedName("loggedOut")
    @Expose
    private Boolean loggedOut;

    @SerializedName("loggedIn")
    @Expose
    private Boolean loggedIn;

    @SerializedName("subscribed")
    @Expose
    private Boolean subscribed;

    public Boolean getLoggedOut() {
        return loggedOut;
    }

    public void setLoggedOut(Boolean loggedOut) {
        this.loggedOut = loggedOut;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Boolean subscribed) {
        this.subscribed = subscribed;
    }
}
