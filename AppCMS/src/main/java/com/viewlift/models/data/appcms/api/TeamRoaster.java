package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class TeamRoaster implements Serializable {
    @SerializedName("timestamp")
    @Expose
    String timestamp;

    @SerializedName("team")
    @Expose
    List<Team> team=null;

    @SerializedName("season")
    @Expose
    TeamSeason teamSeason=null;

    @SerializedName("league")
    @Expose
    TeamLeague teamLeague=null;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Team> getTeam() {
        return team;
    }

    public void setTeam(List<Team> team) {
        this.team = team;
    }

    public TeamSeason getTeamSeason() {
        return teamSeason;
    }

    public void setTeamSeason(TeamSeason teamSeason) {
        this.teamSeason = teamSeason;
    }

    public TeamLeague getTeamLeague() {
        return teamLeague;
    }

    public void setTeamLeague(TeamLeague teamLeague) {
        this.teamLeague = teamLeague;
    }


}

