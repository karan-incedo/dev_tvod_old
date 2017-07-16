package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class PrimaryHover {

    @SerializedName("textColor")
    @Expose
    String textColor;

    @SerializedName("backgroundColor")
    @Expose
    String backgroundColor;

    @SerializedName("border")
    @Expose
    Border___ border;

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Border___ getBorder() {
        return border;
    }

    public void setBorder(Border___ border) {
        this.border = border;
    }
}
