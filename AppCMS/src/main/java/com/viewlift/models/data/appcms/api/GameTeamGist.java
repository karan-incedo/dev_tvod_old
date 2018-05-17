package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/*
 * This Class is used for Away Team
 * Home Team, Leagues and Venues Properties
 */

@UseStag
public class GameTeamGist implements Serializable {

    @SerializedName("gist")
    @Expose
    Gist gist;

    public Gist getGist() {
        return gist;
    }

    public void setGist(Gist gist) {
        this.gist = gist;
    }

}
