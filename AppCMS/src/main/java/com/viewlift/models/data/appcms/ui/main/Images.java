package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Images {

    @SerializedName("placeholderPoster")
    @Expose
    String placeholderPoster;

    @SerializedName("placeholderCover")
    @Expose
    String placeholderCover;

    public String getPlaceholderPoster() {
        return placeholderPoster;
    }

    public void setPlaceholderPoster(String placeholderPoster) {
        this.placeholderPoster = placeholderPoster;
    }

    public String getPlaceholderCover() {
        return placeholderCover;
    }

    public void setPlaceholderCover(String placeholderCover) {
        this.placeholderCover = placeholderCover;
    }
}
