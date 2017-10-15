package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 8/11/17.
 */

@UseStag
public class SocialMedia {
    @SerializedName("facebook")
    @Expose
    Facebook facebook;

    @SerializedName("twitter")
    @Expose
    Twitter twitter;

    @SerializedName("googlePlus")
    @Expose
    GooglePlus googlePlus;

    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
        this.facebook = facebook;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    public GooglePlus getGooglePlus() {
        return googlePlus;
    }

    public void setGooglePlus(GooglePlus googlePlus) {
        this.googlePlus = googlePlus;
    }
}
