package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

/**
 * Created by vinay.singh on 1/24/2018.
 */

@UseStag
public class AppCMSLibraryResult {

    public List<Videos> getTeams() {
        return videos;
    }

    public void setTeams(List<Team> teams) {
        this.videos = videos;
    }

    @SerializedName("videos")
    @Expose
    List<Videos> videos = null;


}
