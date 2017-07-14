package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by viewlift on 6/26/17.
 */

public class AppCMSStreamingInfo {
    @SerializedName("streamingInfo")
    @Expose
    private StreamingInfo streamingInfo;

    public StreamingInfo getStreamingInfo() {
        return streamingInfo;
    }

    public void setStreamingInfo(StreamingInfo streamingInfo) {
        this.streamingInfo = streamingInfo;
    }
}
