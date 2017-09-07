package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 9/6/17.
 */

@UseStag
public class Features {
    @SerializedName("mobile_app_downloads")
    @Expose
    boolean mobileAppDonwloads;

    @SerializedName("user_content_rating")
    @Expose
    boolean userContentRating;

    @SerializedName("free_preview")
    @Expose
    FreePreview freePreview;

    @SerializedName("auto_play")
    @Expose
    boolean autoPlay;

    @SerializedName("mute_sound")
    @Expose
    boolean muteSound;

    @SerializedName("casting")
    @Expose
    boolean casting;

    @SerializedName("trick_play")
    @Expose
    boolean trickPlay;

    public boolean isMobileAppDonwloads() {
        return mobileAppDonwloads;
    }

    public void setMobileAppDonwloads(boolean mobileAppDonwloads) {
        this.mobileAppDonwloads = mobileAppDonwloads;
    }

    public boolean isUserContentRating() {
        return userContentRating;
    }

    public void setUserContentRating(boolean userContentRating) {
        this.userContentRating = userContentRating;
    }

    public FreePreview getFreePreview() {
        return freePreview;
    }

    public void setFreePreview(FreePreview freePreview) {
        this.freePreview = freePreview;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isMuteSound() {
        return muteSound;
    }

    public void setMuteSound(boolean muteSound) {
        this.muteSound = muteSound;
    }

    public boolean isCasting() {
        return casting;
    }

    public void setCasting(boolean casting) {
        this.casting = casting;
    }

    public boolean isTrickPlay() {
        return trickPlay;
    }

    public void setTrickPlay(boolean trickPlay) {
        this.trickPlay = trickPlay;
    }
}
