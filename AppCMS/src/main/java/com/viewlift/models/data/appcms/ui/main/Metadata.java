package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by anas.azeem on 11/30/2017.
 * Owned by ViewLift, NYC
 */

public class Metadata implements Serializable {

    @SerializedName("displayDuration")
    @Expose
    boolean displayDuration;

    @SerializedName("displayAuthor")
    @Expose
    boolean displayAuthor;

    @SerializedName("displayPublishedDate")
    @Expose
    boolean displayPublishedDate;

    public boolean isDisplayDuration() {
        return displayDuration;
    }

    public void setDisplayDuration(boolean displayDuration) {
        this.displayDuration = displayDuration;
    }

    public boolean isDisplayAuthor() {
        return displayAuthor;
    }

    public void setDisplayAuthor(boolean displayAuthor) {
        this.displayAuthor = displayAuthor;
    }

    public boolean isDisplayPublishedDate() {
        return displayPublishedDate;
    }

    public void setDisplayPublishedDate(boolean displayPublishedDate) {
        this.displayPublishedDate = displayPublishedDate;
    }
}
