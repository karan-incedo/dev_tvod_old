package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class LiveEvents implements Serializable {

    public List<com.viewlift.models.data.appcms.api.Fights> getFights() {
        return Fights;
    }

    public void setFights(List<com.viewlift.models.data.appcms.api.Fights> fights) {
        Fights = fights;
    }

    public String getEventDescription() {
        return EventDescription;
    }

    public void setEventDescription(String eventDescription) {
        EventDescription = eventDescription;
    }

    public String getEventName() {
        return EventName;
    }

    public void setEventName(String eventName) {
        EventName = eventName;
    }

    public String getEventState() {
        return EventState;
    }

    public void setEventState(String eventState) {
        EventState = eventState;
    }

    public String getOrganization() {
        return Organization;
    }

    public void setOrganization(String organization) {
        Organization = organization;
    }

    public String getEventId() {
        return EventId;
    }

    public void setEventId(String eventId) {
        EventId = eventId;
    }

    public String getIsLiveEvent() {
        return IsLiveEvent;
    }

    public void setIsLiveEvent(String isLiveEvent) {
        IsLiveEvent = isLiveEvent;
    }

    public String getEventCity() {
        return EventCity;
    }

    public void setEventCity(String eventCity) {
        EventCity = eventCity;
    }

    public String getEventDate() {
        return EventDate;
    }

    public void setEventDate(String eventDate) {
        EventDate = eventDate;
    }

    @SerializedName("Fights")
    @Expose
    public List<Fights> Fights;

    @SerializedName("EventDescription")
    @Expose
    public String EventDescription;

    @SerializedName("EventName")
    @Expose
    public String EventName;

    @SerializedName("EventState")
    @Expose
    public String EventState;

    @SerializedName("Organization")
    @Expose
    public String Organization;

    @SerializedName("EventId")
    @Expose
    public String EventId;

    @SerializedName("IsLiveEvent")
    @Expose
    public String IsLiveEvent;

    @SerializedName("EventCity")
    @Expose
    public String EventCity;

    @SerializedName("standings")
    @Expose
    public String EventDate;
}

