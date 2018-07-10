package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Rounds implements Serializable {
    public String getDominantPositions() {
        return DominantPositions;
    }

    public void setDominantPositions(String dominantPositions) {
        DominantPositions = dominantPositions;
    }

    public String getTakedownAttempts() {
        return TakedownAttempts;
    }

    public void setTakedownAttempts(String takedownAttempts) {
        TakedownAttempts = takedownAttempts;
    }

    public String getNonPowerLegStrikesLanded() {
        return NonPowerLegStrikesLanded;
    }

    public void setNonPowerLegStrikesLanded(String nonPowerLegStrikesLanded) {
        NonPowerLegStrikesLanded = nonPowerLegStrikesLanded;
    }

    public String getPowerLegStrikesLanded() {
        return PowerLegStrikesLanded;
    }

    public void setPowerLegStrikesLanded(String powerLegStrikesLanded) {
        PowerLegStrikesLanded = powerLegStrikesLanded;
    }

    public String getNonPowerLanded() {
        return NonPowerLanded;
    }

    public void setNonPowerLanded(String nonPowerLanded) {
        NonPowerLanded = nonPowerLanded;
    }

    public String getTotalStrikesThrown() {
        return TotalStrikesThrown;
    }

    public void setTotalStrikesThrown(String totalStrikesThrown) {
        TotalStrikesThrown = totalStrikesThrown;
    }

    public String getRound() {
        return Round;
    }

    public void setRound(String round) {
        Round = round;
    }

    public String getPowerArmStrikesLanded() {
        return PowerArmStrikesLanded;
    }

    public void setPowerArmStrikesLanded(String powerArmStrikesLanded) {
        PowerArmStrikesLanded = powerArmStrikesLanded;
    }

    public String getStandingTime() {
        return StandingTime;
    }

    public void setStandingTime(String standingTime) {
        StandingTime = standingTime;
    }

    public String getKnockdowns() {
        return Knockdowns;
    }

    public void setKnockdowns(String knockdowns) {
        Knockdowns = knockdowns;
    }

    public String getNonPowerArmStrikesLanded() {
        return NonPowerArmStrikesLanded;
    }

    public void setNonPowerArmStrikesLanded(String nonPowerArmStrikesLanded) {
        NonPowerArmStrikesLanded = nonPowerArmStrikesLanded;
    }

    public String getTotalArmStrikesThrown() {
        return TotalArmStrikesThrown;
    }

    public void setTotalArmStrikesThrown(String totalArmStrikesThrown) {
        TotalArmStrikesThrown = totalArmStrikesThrown;
    }

    public String getTakedowns() {
        return Takedowns;
    }

    public void setTakedowns(String takedowns) {
        Takedowns = takedowns;
    }

    public String getTotalLegStrikesThrown() {
        return TotalLegStrikesThrown;
    }

    public void setTotalLegStrikesThrown(String totalLegStrikesThrown) {
        TotalLegStrikesThrown = totalLegStrikesThrown;
    }

    public String getFighterId() {
        return FighterId;
    }

    public void setFighterId(String fighterId) {
        FighterId = fighterId;
    }

    public String getNonPowerGroundStrikesLanded() {
        return NonPowerGroundStrikesLanded;
    }

    public void setNonPowerGroundStrikesLanded(String nonPowerGroundStrikesLanded) {
        NonPowerGroundStrikesLanded = nonPowerGroundStrikesLanded;
    }

    public String getPowerLanded() {
        return PowerLanded;
    }

    public void setPowerLanded(String powerLanded) {
        PowerLanded = powerLanded;
    }

    public String getRoundTime() {
        return RoundTime;
    }

    public void setRoundTime(String roundTime) {
        RoundTime = roundTime;
    }

    public String getPowerGroundStrikesLanded() {
        return PowerGroundStrikesLanded;
    }

    public void setPowerGroundStrikesLanded(String powerGroundStrikesLanded) {
        PowerGroundStrikesLanded = powerGroundStrikesLanded;
    }

    public String getGroundTime() {
        return GroundTime;
    }

    public void setGroundTime(String groundTime) {
        GroundTime = groundTime;
    }

    public String getSubmissionAttempts() {
        return SubmissionAttempts;
    }

    public void setSubmissionAttempts(String submissionAttempts) {
        SubmissionAttempts = submissionAttempts;
    }

    public String getGroundStrikesThrown() {
        return GroundStrikesThrown;
    }

    public void setGroundStrikesThrown(String groundStrikesThrown) {
        GroundStrikesThrown = groundStrikesThrown;
    }

    @SerializedName("DominantPositions")
    @Expose
    private String DominantPositions;

    @SerializedName("TakedownAttempts")
    @Expose
    private String TakedownAttempts;

    @SerializedName("NonPowerLegStrikesLanded")
    @Expose
    private String NonPowerLegStrikesLanded;

    @SerializedName("PowerLegStrikesLanded")
    @Expose
    private String PowerLegStrikesLanded;

    @SerializedName("NonPowerLanded")
    @Expose
    private String NonPowerLanded;

    @SerializedName("TotalStrikesThrown")
    @Expose
    private String TotalStrikesThrown;

    @SerializedName("Round")
    @Expose
    private String Round;

    @SerializedName("PowerArmStrikesLanded")
    @Expose
    private String PowerArmStrikesLanded;

    @SerializedName("StandingTime")
    @Expose
    private String StandingTime;

    @SerializedName("Knockdowns")
    @Expose
    private String Knockdowns;

    @SerializedName("NonPowerArmStrikesLanded")
    @Expose
    private String NonPowerArmStrikesLanded;

    @SerializedName("TotalArmStrikesThrown")
    @Expose
    private String TotalArmStrikesThrown;

    @SerializedName("Takedowns")
    @Expose
    private String Takedowns;

    @SerializedName("TotalLegStrikesThrown")
    @Expose
    private String TotalLegStrikesThrown;

    @SerializedName("FighterId")
    @Expose
    private String FighterId;

    @SerializedName("NonPowerGroundStrikesLanded")
    @Expose
    private String NonPowerGroundStrikesLanded;

    @SerializedName("PowerLanded")
    @Expose
    private String PowerLanded;

    @SerializedName("RoundTime")
    @Expose
    private String RoundTime;

    @SerializedName("PowerGroundStrikesLanded")
    @Expose
    private String PowerGroundStrikesLanded;

    @SerializedName("GroundTime")
    @Expose
    private String GroundTime;

    @SerializedName("SubmissionAttempts")
    @Expose
    private String SubmissionAttempts;

    @SerializedName("GroundStrikesThrown")
    @Expose
    private String GroundStrikesThrown;


}

