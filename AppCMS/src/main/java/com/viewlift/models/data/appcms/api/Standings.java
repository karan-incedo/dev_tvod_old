package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class Standings implements Serializable {
    @SerializedName("standingsorder")
    @Expose
    String standingsorder;

    @SerializedName("team")
    @Expose
    List<Team> team = null;

    @SerializedName("league")
    @Expose
    TeamLeague teamLeague = null;

    @SerializedName("season")
    @Expose
    TeamSeason teamSeason = null;

    public String getStandingsorder() {
        return standingsorder;
    }

    public void setStandingsorder(String standingsorder) {
        this.standingsorder = standingsorder;
    }

    public List<Team> getTeam() {
        return team;
    }

    public void setTeam(List<Team> team) {
        this.team = team;
    }

    public TeamLeague getTeamLeague() {
        return teamLeague;
    }

    public void setTeamLeague(TeamLeague teamLeague) {
        this.teamLeague = teamLeague;
    }

    public TeamSeason getTeamSeason() {
        return teamSeason;
    }

    public void setTeamSeason(TeamSeason teamSeason) {
        this.teamSeason = teamSeason;
    }


}

