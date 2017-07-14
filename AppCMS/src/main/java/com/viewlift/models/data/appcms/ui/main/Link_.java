
package com.viewlift.models.data.appcms.ui.main;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Link_ {

    @SerializedName("textColor")
    @Expose
    private String textColor;
    @SerializedName("style")
    @Expose
    private List<String> style = null;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public List<String> getStyle() {
        return style;
    }

    public void setStyle(List<String> style) {
        this.style = style;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

}
