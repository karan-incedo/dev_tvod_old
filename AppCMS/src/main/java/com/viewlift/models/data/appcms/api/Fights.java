package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class Fights implements Serializable {

    @SerializedName("FightStatus")
    @Expose
    public String FightStatus;

    @SerializedName("Fighter2_LastName")
    @Expose
    public String Fighter2_LastName;

    @SerializedName("Fighter2Id")
    @Expose
    public String Fighter2Id;

    @SerializedName("Fighter2_FirstName")
    @Expose
    public String Fighter2_FirstName;

    @SerializedName("ScheduledRounds")
    @Expose
    public String ScheduledRounds;

    @SerializedName("SequenceNumber")
    @Expose
    public String SequenceNumber;

    @SerializedName("Title")
    @Expose
    public String Title;

    public String getFightStatus() {
        return FightStatus;
    }

    public void setFightStatus(String fightStatus) {
        FightStatus = fightStatus;
    }

    public String getFighter2_LastName() {
        return Fighter2_LastName;
    }

    public void setFighter2_LastName(String fighter2_LastName) {
        Fighter2_LastName = fighter2_LastName;
    }

    public String getFighter2Id() {
        return Fighter2Id;
    }

    public void setFighter2Id(String fighter2Id) {
        Fighter2Id = fighter2Id;
    }

    public String getFighter2_FirstName() {
        return Fighter2_FirstName;
    }

    public void setFighter2_FirstName(String fighter2_FirstName) {
        Fighter2_FirstName = fighter2_FirstName;
    }

    public String getScheduledRounds() {
        return ScheduledRounds;
    }

    public void setScheduledRounds(String scheduledRounds) {
        ScheduledRounds = scheduledRounds;
    }

    public String getSequenceNumber() {
        return SequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        SequenceNumber = sequenceNumber;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getFighter1_FirstName() {
        return Fighter1_FirstName;
    }

    public void setFighter1_FirstName(String fighter1_FirstName) {
        Fighter1_FirstName = fighter1_FirstName;
    }

    public String getWeightclass() {
        return weightclass;
    }

    public void setWeightclass(String weightclass) {
        this.weightclass = weightclass;
    }

    public String getDecision() {
        return Decision;
    }

    public void setDecision(String decision) {
        Decision = decision;
    }

    public String getActualRounds() {
        return ActualRounds;
    }

    public void setActualRounds(String actualRounds) {
        ActualRounds = actualRounds;
    }

    public String getFighter1_Id() {
        return Fighter1_Id;
    }

    public void setFighter1_Id(String fighter1_Id) {
        Fighter1_Id = fighter1_Id;
    }

    public List<Rounds> getRounds() {
        return Rounds;
    }

    public void setRounds(List<Rounds> rounds) {
        Rounds = rounds;
    }

    public String getFightId() {
        return FightId;
    }

    public void setFightId(String fightId) {
        FightId = fightId;
    }

    public String getFightNotes() {
        return FightNotes;
    }

    public void setFightNotes(String fightNotes) {
        FightNotes = fightNotes;
    }

    public String getWinnerId() {
        return WinnerId;
    }

    public void setWinnerId(String winnerId) {
        WinnerId = winnerId;
    }

    public String getMinutesPerRound() {
        return MinutesPerRound;
    }

    public void setMinutesPerRound(String minutesPerRound) {
        MinutesPerRound = minutesPerRound;
    }

    public String getFighter1_LastName() {
        return Fighter1_LastName;
    }

    public void setFighter1_LastName(String fighter1_LastName) {
        Fighter1_LastName = fighter1_LastName;
    }

    @SerializedName("Fighter1_FirstName")
    @Expose
    private String Fighter1_FirstName;

    @SerializedName("weightclass")
    @Expose
    public String weightclass;

    @SerializedName("Decision")
    @Expose
    public String Decision;

    @SerializedName("ActualRounds")
    @Expose
    public String ActualRounds;

    @SerializedName("Fighter1_Id")
    @Expose
    private String Fighter1_Id;

    @SerializedName("Rounds")
    @Expose
    public List<Rounds> Rounds;

    @SerializedName("FightId")
    @Expose
    public String FightId;

    @SerializedName("FightNotes")
    @Expose
    public String FightNotes;

    @SerializedName("WinnerId")
    @Expose
    public String WinnerId;

    @SerializedName("MinutesPerRound")
    @Expose
    public String MinutesPerRound;

    @SerializedName("Fighter1_LastName")
    @Expose
    public String Fighter1_LastName;

}

