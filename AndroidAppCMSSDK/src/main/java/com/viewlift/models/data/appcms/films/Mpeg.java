
package com.viewlift.models.data.appcms.films;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mpeg {

    @SerializedName("codec")
    @Expose
    private String codec;
    @SerializedName("renditionValue")
    @Expose
    private String renditionValue;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("bitrate")
    @Expose
    private Integer bitrate;
    @SerializedName("fileSize")
    @Expose
    private Object fileSize;

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

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public Object getFileSize() {
        return fileSize;
    }

    public void setFileSize(Object fileSize) {
        this.fileSize = fileSize;
    }

}
