package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by viewlift on 9/6/17.
 */

@UseStag
public class FreePreview implements Serializable {
    @SerializedName("isFreePreview")
    @Expose
    boolean isFreePreview;

    @SerializedName("length")
    @Expose
    Length length;

    @SerializedName("per_video")
    @Expose
    boolean per_video;

    public boolean isFreePreview() {
        return isFreePreview;
    }

    public void setFreePreview(boolean freePreview) {
        isFreePreview = freePreview;
    }

    public Length getLength() {
        return length;
    }

    public void setLength(Length length) {
        this.length = length;
    }

    public boolean isPerVideo() {
        return per_video;
    }

    public void setPeVideo(boolean per_video) {
        this.per_video = per_video;
    }
}
