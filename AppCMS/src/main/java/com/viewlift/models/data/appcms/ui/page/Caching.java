package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by viewlift on 12/22/17.
 */

@UseStag
public class Caching implements Serializable {
    @SerializedName("isEnabled")
    @Expose
    boolean isEnabled;

    boolean overrideCaching;

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean shouldOverrideCaching() {
        boolean currentOverrideCaching = overrideCaching;
        overrideCaching = false;
        return currentOverrideCaching;
    }

    public void setOverrideCaching(boolean overrideCaching) {
        this.overrideCaching = overrideCaching;
    }
}
