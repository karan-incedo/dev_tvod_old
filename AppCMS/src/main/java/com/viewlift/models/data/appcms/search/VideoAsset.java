
package com.viewlift.models.data.appcms.search;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoAsset {

    @SerializedName("mpeg")
    @Expose
    private List<Mpeg> mpeg = null;
    @SerializedName("hls")
    @Expose
    private String hls;

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
