package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by viewlift on 9/6/17.
 */

@UseStag
public class AudioPreview implements Serializable {
    @SerializedName("isAudioPreview")
    @Expose
    boolean isAudioPreview;

    @SerializedName("length")
    @Expose
    Length length;

    public boolean isAudioPreview() {
        return isAudioPreview;
    }

    public Length getLength() {
        return length;
    }

    public void setLength(Length length) {
        this.length = length;
    }

}
