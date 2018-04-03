package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.audio.AudioAssets;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class StreamingInfo implements Serializable {

    @SerializedName("isLiveStream")
    @Expose
    boolean isLiveStream;

    @SerializedName("videoAssets")
    @Expose
    VideoAssets videoAssets;

    @SerializedName("audioAssets")
    @Expose
    AudioAssets audioAssets;

    @SerializedName("cuePoints")
    @Expose
    String cuePoints;

    public boolean getIsLiveStream() {
        return isLiveStream;
    }

    public void setIsLiveStream(boolean isLiveStream) {
        this.isLiveStream = isLiveStream;
    }

    public VideoAssets getVideoAssets() {
        return videoAssets;
    }

    public void setVideoAssets(VideoAssets videoAssets) {
        this.videoAssets = videoAssets;
    }

    public AudioAssets  getAudioAssets() {
        return audioAssets;
    }

    public void setAudioAssets(AudioAssets  audioAssets) {
        this.audioAssets = audioAssets;
    }

    public String getCuePoints() {
        return cuePoints;
    }

    public void setCuePoints(String cuePoints) {
        this.cuePoints = cuePoints;
    }
}
