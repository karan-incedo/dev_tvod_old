package com.viewlift.models.data.appcms.audio;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Mp3 {

    @SerializedName("url")
    @Expose
    String url = null;

    @SerializedName("bitrate")
    @Expose
    long bitrate = 0;

    public String getUrl() {
        return url;
    }

    public long getBitrate() {
        return bitrate;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
