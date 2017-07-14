package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Mpeg {

    @SerializedName("codec")
    @Expose
    String codec;

    @SerializedName("renditionValue")
    @Expose
    String renditionValue;

    @SerializedName("url")
    @Expose
    String url;

    @SerializedName("bitrate")
    @Expose
    int bitrate;

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public String getRenditionValue() {
        return renditionValue;
    }

    public void setRenditionValue(String renditionValue) {
        this.renditionValue = renditionValue;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }
}
