package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by viewlift on 12/22/17.
 */

@UseStag
public class Caching implements Serializable {
    private static final long CACHE_TIME_IN_MINS = 15;
    private static final long CACHE_TIME_IN_MSEC = 1000 * 60 * CACHE_TIME_IN_MINS;

    @SerializedName("isEnabled")
    @Expose
    boolean isEnabled;

    boolean overrideCaching;

    long cachingOverrideTime;

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean shouldOverrideCaching() {
        boolean currentOverrideCaching = overrideCaching;
        long currentTime = new Date().getTime();
        /** Set the override caching value to 10 mins if the time to cache update has expired  */
        if (cachingOverrideTime < currentTime &&
                CACHE_TIME_IN_MSEC <= currentTime - cachingOverrideTime) {
            overrideCaching = false;
        }
        return currentOverrideCaching;
    }

    public void setOverrideCaching(boolean overrideCaching) {
        this.overrideCaching = overrideCaching;
        this.cachingOverrideTime = new Date().getTime();
    }
}
