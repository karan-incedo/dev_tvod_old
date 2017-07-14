
package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SecondaryHover {

    @SerializedName("textColor")
    @Expose
    private String textColor;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("border")
    @Expose
    private Border border;

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

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

}
