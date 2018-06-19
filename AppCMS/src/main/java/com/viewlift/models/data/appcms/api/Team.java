package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class Team implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;




    @SerializedName("players")
    @Expose
    List<Players> players;

    public List<ContentDatum> getContentDataPlayers() {
        return contentDataPlayers;
    }

    public void setContentDataPlayers(List<ContentDatum> contentDataPlayers) {
        this.contentDataPlayers = contentDataPlayers;
    }

    List<ContentDatum> contentDataPlayers;

    @SerializedName("teamshortname")
    @Expose
    private String teamshortname;

    @SerializedName("mascot")
    @Expose
    private String mascot;

    @SerializedName("teamid")
    @Expose
    private String teamid;

    @SerializedName("lastten")
    @Expose
    private String lastten;

    @SerializedName("roadrecord")
    @Expose
    private String roadrecord;

    @SerializedName("penaltyminutes")
    @Expose
    private String penaltyminutes;

    @SerializedName("winningpercentage")
    @Expose
    private String winningpercentage;

    @SerializedName("twopointgoalsfor")
    @Expose
    private String twopointgoalsfor;

    @SerializedName("homerecord")
    @Expose
    private String homerecord;

    @SerializedName("goalsagainst")
    @Expose
    private String goalsagainst;

    @SerializedName("gb")
    @Expose
    private String gb;

    @SerializedName("goalsfor")
    @Expose
    private String goalsfor;

    @SerializedName("steamid")
    @Expose
    private String steamid;

    @SerializedName("rank")
    @Expose
    private String rank;

//    @SerializedName("standingsorder")
//    @Expose
//    private Division division;

    @SerializedName("twopointgoalsagainst")
    @Expose
    private String twopointgoalsagainst;

    @SerializedName("teamname")
    @Expose
    private String teamname;

    @SerializedName("teamstreak")
    @Expose
    private String teamstreak;

    @SerializedName("losses")
    @Expose
    private String losses;

    @SerializedName("points")
    @Expose
    private String points;

    @SerializedName("gp")
    @Expose
    private String gp;

    @SerializedName("wins")
    @Expose
    private String wins;

    public String getTeamshortname() {
        return teamshortname;
    }

    public void setTeamshortname(String teamshortname) {
        this.teamshortname = teamshortname;
    }

    public String getMascot() {
        return mascot;
    }

    public void setMascot(String mascot) {
        this.mascot = mascot;
    }

    public String getTeamid() {
        return teamid;
    }

    public void setTeamid(String teamid) {
        this.teamid = teamid;
    }

    public String getLastten() {
        return lastten;
    }

    public void setLastten(String lastten) {
        this.lastten = lastten;
    }

    public String getRoadrecord() {
        return roadrecord;
    }

    public void setRoadrecord(String roadrecord) {
        this.roadrecord = roadrecord;
    }

    public String getPenaltyminutes() {
        return penaltyminutes;
    }

    public void setPenaltyminutes(String penaltyminutes) {
        this.penaltyminutes = penaltyminutes;
    }

    public String getWinningpercentage() {
        return winningpercentage;
    }

    public void setWinningpercentage(String winningpercentage) {
        this.winningpercentage = winningpercentage;
    }
    public List<Players> getPlayers() {
        return players;
    }

    public void setPlayers(List<Players> players) {
        this.players = players;
    }
    public String getTwopointgoalsfor() {
        return twopointgoalsfor;
    }

    public void setTwopointgoalsfor(String twopointgoalsfor) {
        this.twopointgoalsfor = twopointgoalsfor;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomerecord() {
        return homerecord;
    }

    public void setHomerecord(String homerecord) {
        this.homerecord = homerecord;
    }

    public String getGoalsagainst() {
        return goalsagainst;
    }

    public void setGoalsagainst(String goalsagainst) {
        this.goalsagainst = goalsagainst;
    }

    public String getGb() {
        return gb;
    }

    public void setGb(String gb) {
        this.gb = gb;
    }

    public String getGoalsfor() {
        return goalsfor;
    }

    public void setGoalsfor(String goalsfor) {
        this.goalsfor = goalsfor;
    }

    public String getSteamid() {
        return steamid;
    }

    public void setSteamid(String steamid) {
        this.steamid = steamid;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

//    public Division getDivision ()
//    {
//        return division;
//    }
//
//    public void setDivision (Division division)
//    {
//        this.division = division;
//    }

    public String getTwopointgoalsagainst() {
        return twopointgoalsagainst;
    }

    public void setTwopointgoalsagainst(String twopointgoalsagainst) {
        this.twopointgoalsagainst = twopointgoalsagainst;
    }

    public String getTeamname() {
        return teamname;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }

    public String getTeamstreak() {
        return teamstreak;
    }

    public void setTeamstreak(String teamstreak) {
        this.teamstreak = teamstreak;
    }

    public String getLosses() {
        return losses;
    }

    public void setLosses(String losses) {
        this.losses = losses;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getGp() {
        return gp;
    }

    public void setGp(String gp) {
        this.gp = gp;
    }

    public String getWins() {
        return wins;
    }

    public void setWins(String wins) {
        this.wins = wins;
    }


}

