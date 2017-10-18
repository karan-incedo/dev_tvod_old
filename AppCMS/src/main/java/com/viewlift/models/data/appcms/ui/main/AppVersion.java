package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by viewlift on 10/4/17.
 */

@UseStag
public class AppVersion implements Serializable {
    @SerializedName("minimum")
    @Expose
    String minimum;

    @SerializedName("latest")
    @Expose
    String latest;

    @SerializedName("updateUrl")
    @Expose
    String updateUrl;

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }
}
