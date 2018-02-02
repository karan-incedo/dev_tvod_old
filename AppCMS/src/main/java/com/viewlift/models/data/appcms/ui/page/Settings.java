package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.Columns;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Settings implements Serializable {

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("description")
    @Expose
    String description;

    @SerializedName("loop")
    @Expose
    boolean loop;

    @SerializedName("columns")
    @Expose
    Columns columns;

    @SerializedName("primaryCta")
    @Expose
    PrimaryCta primaryCta;

    @SerializedName("showPIP")
    @Expose
    boolean showPIP;
    @SerializedName("isStandaloneVideo")
    @Expose
    boolean standaloneVideo;

    @SerializedName("showPlaybackControls")
    @Expose
    boolean showPlaybackControls;

    @SerializedName("roundedCorners")
    @Expose
    boolean roundedCorners;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public Columns getColumns() {
        return columns;
    }

    public void setColumns(Columns columns) {
        this.columns = columns;
    }

    public boolean isLoop() {
        return loop;
    }

    public PrimaryCta getPrimaryCta() {
        return primaryCta;
    }

    public void setPrimaryCta(PrimaryCta primaryCta) {
        this.primaryCta = primaryCta;
    }

    public void setShowPIP(boolean showPIP) {
        this.showPIP = showPIP;
    }

    public boolean isShowPIP() {
        return showPIP;
    }

    public void setShowPlaybackControls(boolean showPlaybackControls) {
        this.showPlaybackControls = showPlaybackControls;
    }

    public boolean isShowPlaybackControls() {
        return showPlaybackControls;
    }

    public void setStandaloneVideo(boolean standaloneVideo) {
        this.standaloneVideo = standaloneVideo;
    }

    public boolean isStandaloneVideo() {
        return standaloneVideo;
    }

    public boolean isRoundedCorners() {
        return roundedCorners;
    }

    public void setRoundedCorners(boolean roundedCorners) {
        this.roundedCorners = roundedCorners;
    }
}
