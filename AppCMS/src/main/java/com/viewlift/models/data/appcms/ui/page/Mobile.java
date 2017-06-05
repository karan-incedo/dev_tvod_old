
package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mobile {

    @SerializedName("width")
    @Expose
    private Float width;
    @SerializedName("height")
    @Expose
    private Float height;
    @SerializedName("yAxis")
    @Expose
    private Float yAxis;
    @SerializedName("rightMargin")
    @Expose
    private Float rightMargin;
    @SerializedName("leftMargin")
    @Expose
    private Float leftMargin;
    @SerializedName("topMargin")
    @Expose
    private Float topMargin;
    @SerializedName("bottomMargin")
    @Expose
    private Float bottomMargin;
    @SerializedName("xAxis")
    @Expose
    private Float xAxis;
    @SerializedName("gridWidth")
    @Expose
    private Float gridWidth;
    @SerializedName("gridHeight")
    @Expose
    private Float gridHeight;
    @SerializedName("fontSize")
    @Expose
    private int fontSize;
    @SerializedName("marginBottom")
    @Expose
    private Float marginBottom;
    @SerializedName("marginTop")
    @Expose
    private Float marginTop;
    @SerializedName("marginLeft")
    @Expose
    private Float marginLeft;
    @SerializedName("marginRight")
    @Expose
    private Float marginRight;
    @SerializedName("trayPadding")
    @Expose
    private Float trayPadding;

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getYAxis() {
        return yAxis;
    }

    public void setYAxis(Float yAxis) {
        this.yAxis = yAxis;
    }

    public Float getRightMargin() {
        if (marginRight != null) {
            return marginRight;
        }
        return rightMargin;
    }

    public void setRightMargin(Float rightMargin) {
        this.rightMargin = rightMargin;
    }

    public Float getLeftMargin() {
        if (marginLeft != null) {
            return marginLeft;
        }
        return leftMargin;
    }

    public void setLeftMargin(Float leftMargin) {
        this.leftMargin = leftMargin;
    }

    public Float getTopMargin() {
        if (marginTop != null) {
            return marginTop;
        }
        return topMargin;
    }

    public void setTopMargin(Float topMargin) {
        this.topMargin = topMargin;
    }

    public Float getBottomMargin() {
        if (marginBottom != null) {
            return marginBottom;
        }
        return bottomMargin;
    }

    public void setBottomMargin(Float bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public Float getXAxis() {
        return xAxis;
    }

    public void setXAxis(Float xAxis) {
        this.xAxis = xAxis;
    }

    public Float getyAxis() {
        return yAxis;
    }

    public void setyAxis(Float yAxis) {
        this.yAxis = yAxis;
    }

    public Float getxAxis() {
        return xAxis;
    }

    public void setxAxis(Float xAxis) {
        this.xAxis = xAxis;
    }

    public Float getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(Float gridWidth) {
        this.gridWidth = gridWidth;
    }

    public Float getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(Float gridHeight) {
        this.gridHeight = gridHeight;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Float getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(Float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public Float getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(Float marginTop) {
        this.marginTop = marginTop;
    }

    public Float getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(Float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public Float getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(Float marginRight) {
        this.marginRight = marginRight;
    }

    public Float getTrayPadding() {
        return trayPadding;
    }

    public void setTrayPadding(Float trayPadding) {
        this.trayPadding = trayPadding;
    }
}
