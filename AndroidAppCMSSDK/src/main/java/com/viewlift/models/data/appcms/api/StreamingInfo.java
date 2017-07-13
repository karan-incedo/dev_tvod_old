
package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StreamingInfo {

    @SerializedName("isLiveStream")
    @Expose
    private Boolean isLiveStream;
    @SerializedName("videoAssets")
    @Expose
    private VideoAssets videoAssets;
    @SerializedName("audioAssets")
    @Expose
    private Object audioAssets;
    @SerializedName("cuePoints")
    @Expose
    private String cuePoints;

    public Boolean getIsLiveStream() {
        return isLiveStream;
    }

    public void setIsLiveStream(Boolean isLiveStream) {
        this.isLiveStream = isLiveStream;
    }

    public VideoAssets getVideoAssets() {
        return videoAssets;
    }

    public void setVideoAssets(VideoAssets videoAssets) {
        this.videoAssets = videoAssets;
    }

    public Object getAudioAssets() {
        return audioAssets;
    }

    public void setAudioAssets(Object audioAssets) {
        this.audioAssets = audioAssets;
    }

    public String getCuePoints() {
        return cuePoints;
    }

    public void setCuePoints(String cuePoints) {
        this.cuePoints = cuePoints;
    }

}
