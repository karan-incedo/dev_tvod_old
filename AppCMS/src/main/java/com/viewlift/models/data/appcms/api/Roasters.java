package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Roasters implements Serializable {
    public TeamRoaster getTeamroster() {
        return teamroster;
    }

    public void setTeamroster(TeamRoaster teamroster) {
        this.teamroster = teamroster;
    }

    @SerializedName("teamroster")
    @Expose
    TeamRoaster teamroster;
}

