package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/*
 * Created by viewlift on 9/6/17.
 */

@UseStag
public class Features implements Serializable {

    @SerializedName("mobile_app_downloads")
    @Expose
    boolean mobileAppDownloads;

    @SerializedName("user_content_rating")
    @Expose
    boolean userContentRating;

    @SerializedName("audio_preview")
    @Expose
    AudioPreview audioPreview;


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

    public boolean isMobileAppDownloads() {
        return mobileAppDownloads;
    }

    public void setMobileAppDonwloads(boolean mobileAppDownloads) {
        this.mobileAppDownloads = mobileAppDownloads;
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

    public AudioPreview getAudioPreview() {
        return audioPreview;
    }
}
