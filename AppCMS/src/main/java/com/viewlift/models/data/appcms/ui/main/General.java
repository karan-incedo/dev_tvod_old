
package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class General {

    @SerializedName("textColor")
    @Expose
    private String textColor;
    @SerializedName("pageTitleColor")
    @Expose
    private String pageTitleColor;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("blockTitleColor")
    @Expose
    private String blockTitleColor;

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getPageTitleColor() {
        return pageTitleColor;
    }

    public void setPageTitleColor(String pageTitleColor) {
        this.pageTitleColor = pageTitleColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBlockTitleColor() {
        return blockTitleColor;
    }

    public void setBlockTitleColor(String blockTitleColor) {
        this.blockTitleColor = blockTitleColor;
    }

}
