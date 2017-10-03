package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class Season {

    @SerializedName("seasons")
    @Expose
    List<Season_> seasons = null;

    public List<Season_> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season_> seasons) {
        this.seasons = seasons;
    }
}
