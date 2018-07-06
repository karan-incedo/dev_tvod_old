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
    Object venue;

    @SerializedName("gameTimeZone")
    @Expose
    String gameTimeZone;

    @SerializedName("gameTime")
    @Expose
    long gameTime;

    @SerializedName("eventTime")
    @Expose
    long eventTime;

    @SerializedName("doorTime")
    @Expose
    String doorTime;

    @SerializedName("eventDate")
    @Expose
    long eventDate;

    @SerializedName("showTime")
    @Expose
    boolean showTime;

    @SerializedName("eventTimeZone")
    @Expose
    String eventTimeZone;

    @SerializedName("gameDoorTime")
    @Expose
    long gameDoorTime;

    @SerializedName("gameDate")
    @Expose
    long gameDate;

    public String getIsLiveEvent() {
        return IsLiveEvent;
    }

    public void setIsLiveEvent(String isLiveEvent) {
        IsLiveEvent = isLiveEvent;
    }

    public String IsLiveEvent;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getVenue() {
        return venue;
    }

    public void setVenue(Object venue) {
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

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getDoorTime() {
        return doorTime;
    }

    public void setDoorTime(String doorTime) {
        this.doorTime = doorTime;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public boolean getShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public String getEventTimeZone() {
        return eventTimeZone;
    }

    public void setEventTimeZone(String eventTimeZone) {
        this.eventTimeZone = eventTimeZone;
    }

}
