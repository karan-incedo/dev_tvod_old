package com.viewlift.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 7/6/17.
 */

@UseStag
public class AccessLevels {

    @SerializedName("loggedOut")
    @Expose
    boolean loggedOut;

    @SerializedName("loggedIn")
    @Expose
    boolean loggedIn;

    @SerializedName("subscribed")
    @Expose
    boolean subscribed;

    public boolean getLoggedOut() {
        return loggedOut;
    }

    public void setLoggedOut(boolean loggedOut) {
        this.loggedOut = loggedOut;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}
