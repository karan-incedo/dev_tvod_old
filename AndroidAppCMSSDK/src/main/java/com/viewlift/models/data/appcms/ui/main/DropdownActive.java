
package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DropdownActive {

    @SerializedName("textColor")
    @Expose
    private String textColor;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;

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

}
