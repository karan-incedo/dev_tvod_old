package com.viewlift.models.data.appcms.api;

/*
 * Created by Viewlift on 7/13/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.subscriptions.Authentication;
import com.vimeo.stag.UseStag;

@UseStag
public class SubscriptionRequest {

    @SerializedName("siteInternalName")
    @Expose
    String siteInternalName;

    @SerializedName("onlyVisible")
    @Expose
    boolean onlyVisible;

    @SerializedName("authentication")
    @Expose
    Authentication authentication;

    public String getSiteInternalName() {
        return siteInternalName;
    }

    public void setSiteInternalName(String siteInternalName) {
        this.siteInternalName = siteInternalName;
    }

    public boolean getOnlyVisible() {
        return onlyVisible;
    }

    public void setOnlyVisible(boolean onlyVisible) {
        this.onlyVisible = onlyVisible;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}
