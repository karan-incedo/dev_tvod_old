package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class GameSchedule implements Serializable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("venue")
    @Expose
    String venue;

    @SerializedName("gameTimeZone")
    @Expose
    String gameTimeZone;

    @SerializedName("gameTime")
    @Expose
    long gameTime;

    @SerializedName("gameDoorTime")
    @Expose
    long gameDoorTime;

    @SerializedName("gameDate")
    @Expose
    long gameDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getGameTimeZone() {
        return gameTimeZone;
    }

    public void setGameTimeZone(String gameTimeZone) {
        this.gameTimeZone = gameTimeZone;
    }

    public long getGameTime() {
        return gameTime;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public long getGameDoorTime() {
        return gameDoorTime;
    }

    public void setGameDoorTime(long gameDoorTime) {
        this.gameDoorTime = gameDoorTime;
    }

    public long getGameDate() {
        return gameDate;
    }

    public void setGameDate(long gameDate) {
        this.gameDate = gameDate;
    }

}
