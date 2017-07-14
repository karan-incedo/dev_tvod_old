package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by viewlift on 6/1/17.
 */

public class Styles {
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("cornerRadius")
    @Expose
    private int cornerRadius;
    @SerializedName("padding")
    @Expose
    private int padding;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("textColor")
    @Expose
    private String textColor;

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
