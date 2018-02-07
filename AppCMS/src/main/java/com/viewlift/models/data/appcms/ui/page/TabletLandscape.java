package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class TabletLandscape implements Serializable {

    @SerializedName("yAxis")
    @Expose
    float yAxis;

    @SerializedName("width")
    @Expose
    float width;

    @SerializedName("height")
    @Expose
    float height;

    @SerializedName("rightMargin")
    @Expose
    float rightMargin;

    @SerializedName("leftMargin")
    @Expose
    float leftMargin;

    @SerializedName("topMargin")
    @Expose
    float topMargin;

    @SerializedName("bottomMargin")
    @Expose
    float bottomMargin;

    @SerializedName("xAxis")
    @Expose
    float xAxis;

    @SerializedName("gridHeight")
    @Expose
    float gridHeight;

    @SerializedName("gridWidth")
    @Expose
    float gridWidth;

    @SerializedName("fontSize")
    @Expose
    int fontSize;

    @SerializedName("trayPadding")
    @Expose
    float trayPadding;

    @SerializedName("fontSizeKey")
    @Expose
    float fontSizeKey;

    @SerializedName("fontSizeValue")
    @Expose
    float fontSizeValue;

    @SerializedName("maximumWidth")
    @Expose
    float maximumWidth;

    @SerializedName("isHorizontalScroll")
    @Expose
    boolean isHorizontalScroll;

    @SerializedName("thumbnailWidth")
    int thumbnailWidth;

    @SerializedName("thumbnailHeight")
    @Expose
    int thumbnailHeight;

    private float savedWidth;

    public boolean isHorizontalScroll() {
        return isHorizontalScroll;
    }

    public float getYAxis() {
        return yAxis;
    }

    public void setYAxis(float yAxis) {
        this.yAxis = yAxis;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getRightMargin() {
        return rightMargin;
    }

    public void setRightMargin(float rightMargin) {
        this.rightMargin = rightMargin;
    }

    public float getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(float leftMargin) {
        this.leftMargin = leftMargin;
    }

    public float getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(float topMargin) {
        this.topMargin = topMargin;
    }

    public float getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(float bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public float getXAxis() {
        return xAxis;
    }

    public void setXAxis(float xAxis) {
        this.xAxis = xAxis;
    }

    public float getyAxis() {
        return yAxis;
    }

    public void setyAxis(float yAxis) {
        this.yAxis = yAxis;
    }

    public float getxAxis() {
        return xAxis;
    }

    public void setxAxis(float xAxis) {
        this.xAxis = xAxis;
    }

    public float getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(float gridHeight) {
        this.gridHeight = gridHeight;
    }

    public float getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(float gridWidth) {
        this.gridWidth = gridWidth;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public float getTrayPadding() {
        return trayPadding;
    }

    public void setTrayPadding(float trayPadding) {
        this.trayPadding = trayPadding;
    }

    public float getFontSizeKey() {
        return fontSizeKey;
    }

    public void setFontSizeKey(float fontSizeKey) {
        this.fontSizeKey = fontSizeKey;
    }

    public float getFontSizeValue() {
        return fontSizeValue;
    }

    public void setFontSizeValue(float fontSizeValue) {
        this.fontSizeValue = fontSizeValue;
    }

    public float getMaximumWidth() {
        return maximumWidth;
    }

    public void setMaximumWidth(float maximumWidth) {
        this.maximumWidth = maximumWidth;
    }

    public float getSavedWidth() {
        return savedWidth;
    }

    public void setSavedWidth(float savedWidth) {
        this.savedWidth = savedWidth;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
    }
}
