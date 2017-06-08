
package com.viewlift.models.data.appcms.api;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoAssets {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("mpeg")
    @Expose
    private List<Mpeg> mpeg = null;
    @SerializedName("hls")
    @Expose
    private String hls;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Mpeg> getMpeg() {
        return mpeg;
    }

    public void setMpeg(List<Mpeg> mpeg) {
        this.mpeg = mpeg;
    }

    public String getHls() {
        return hls;
    }

    public void setHls(String hls) {
        this.hls = hls;
    }

}
