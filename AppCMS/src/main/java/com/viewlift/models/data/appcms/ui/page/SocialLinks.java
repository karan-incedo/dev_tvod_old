package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.Columns;
import com.viewlift.models.data.appcms.ui.android.AccessLevels;
import com.viewlift.models.data.appcms.ui.android.Platforms;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class SocialLinks implements Serializable {

    @SerializedName("displayedPath")
    @Expose
    String displayedPath;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("platforms")
    @Expose
    Platforms platforms;

    @SerializedName("accessLevels")
    @Expose
    AccessLevels accessLevels;

    public String getDisplayedPath() {
        return displayedPath;
    }

    public String getTitle() {
        return title;
    }

    public Platforms getPlatforms() {
        return platforms;
    }

    public AccessLevels getAccessLevels() {
        return accessLevels;
    }
}
