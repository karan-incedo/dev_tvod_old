package com.viewlift.models.data.appcms.audio;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class AudioAssets implements Serializable {

    @SerializedName("type")
    @Expose
    String type = null;

    @SerializedName("mp3")
    @Expose
    Mp3 mp3 = null;

    public String getType() {
        return type;
    }

    public Mp3 getMp3() {
        return mp3;
    }

    public void setMp3(Mp3 mp3) {
        this.mp3 = mp3;
    }
}
