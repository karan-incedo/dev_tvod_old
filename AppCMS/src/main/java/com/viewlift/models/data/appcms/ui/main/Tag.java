package com.viewlift.models.data.appcms.ui.main;

/**
 * Created by viewlift on 6/13/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Tag implements Serializable {

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("uuid")
    @Expose
    String uuid;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
