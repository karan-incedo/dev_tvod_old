package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class General implements Serializable {

    @SerializedName("backgroundColor")
    @Expose
    String backgroundColor;
    @SerializedName("blockTitleColor")
    @Expose
    String blockTitleColor;

    @SerializedName("fontFamily")
    @Expose
    String fontFamily;
    @SerializedName("pageTitleColor")
    @Expose
    String pageTitleColor;

    @SerializedName("fontUrl")
    @Expose
    String fontUrl;

    @SerializedName("textColor")
    @Expose

    String textColor;




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
    public String getFontFamily() {
        return fontFamily;
    }
    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }
    public String getPageTitleColor() {
        return pageTitleColor;
    }
    public void setPageTitleColor(String pageTitleColor) {
        this.pageTitleColor = pageTitleColor;
    }
    public String getFontUrl() {
        return fontUrl;
    }
    public void setFontUrl(String fontUrl) {
        this.fontUrl = fontUrl;
    }
    public String getTextColor() {
        return textColor;
    }
    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
}
