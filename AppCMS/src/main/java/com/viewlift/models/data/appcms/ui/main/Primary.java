package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Primary {

    @SerializedName("textColor")
    @Expose
    String textColor;

    @SerializedName("backgroundColor")
    @Expose
    String backgroundColor;

    @SerializedName("border")
    @Expose
    Border_ border;

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

    public Border_ getBorder() {
        return border;
    }

    public void setBorder(Border_ border) {
        this.border = border;
    }
}
