package com.viewlift.models.data.appcms.audio;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

public class LastPlayAudioDetail {

    public LastPlayAudioDetail(String id, long pos) {
        this.id = id;
        this.position = pos;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    String id;
    long position;
}
