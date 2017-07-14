
package com.viewlift.models.data.appcms.ui.main;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinkActive_ {

    @SerializedName("style")
    @Expose
    private List<String> style = null;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;

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
