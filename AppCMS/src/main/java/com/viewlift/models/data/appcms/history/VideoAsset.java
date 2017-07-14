package com.viewlift.models.data.appcms.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class VideoAsset {

    @SerializedName("mpeg")
    @Expose
    List<Mpeg> mpeg = null;

    @SerializedName("hls")
    @Expose
    String hls;

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
