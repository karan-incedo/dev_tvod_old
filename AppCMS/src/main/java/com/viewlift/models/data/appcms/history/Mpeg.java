package com.viewlift.models.data.appcms.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mpeg {

    @SerializedName("codec")
    @Expose
    private String codec;

    @SerializedName("renditionValue")
    @Expose
    private String renditionValue;

    @SerializedName("bitrate")
    @Expose
    private Integer bitrate;

    @SerializedName("url")
    @Expose
    private String url;

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

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
