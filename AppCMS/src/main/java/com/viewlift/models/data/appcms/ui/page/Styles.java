package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 6/1/17.
 */

@UseStag
public class Styles {

    @SerializedName("backgroundColor")
    @Expose
    String backgroundColor;

    @SerializedName("action")
    @Expose
    String action;

    @SerializedName("cornerRadius")
    @Expose
    int cornerRadius;

    @SerializedName("padding")
    @Expose
    int padding;

    @SerializedName("color")
    @Expose
    String color;

    @SerializedName("textColor")
    @Expose
    String textColor;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
}
