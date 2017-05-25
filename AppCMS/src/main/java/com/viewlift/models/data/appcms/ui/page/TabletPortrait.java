
package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TabletPortrait {

    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("yAxis")
    @Expose
    private Integer yAxis;
    @SerializedName("rightMargin")
    @Expose
    private Integer rightMargin;
    @SerializedName("leftMargin")
    @Expose
    private Integer leftMargin;
    @SerializedName("topMargin")
    @Expose
    private Float topMargin;
    @SerializedName("bottomMargin")
    @Expose
    private Float bottomMargin;
    @SerializedName("xAxis")
    @Expose
    private Integer xAxis;
    @SerializedName("gridWidth")
    @Expose
    private Integer gridWidth;
    @SerializedName("gridHeight")
    @Expose
    private Integer gridHeight;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getYAxis() {
        return yAxis;
    }

    public void setYAxis(Integer yAxis) {
        this.yAxis = yAxis;
    }

    public Integer getRightMargin() {
        return rightMargin;
    }

    public void setRightMargin(Integer rightMargin) {
        this.rightMargin = rightMargin;
    }

    public Integer getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(Integer leftMargin) {
        this.leftMargin = leftMargin;
    }

    public Float getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(Float topMargin) {
        this.topMargin = topMargin;
    }

    public Float getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(Float bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public Integer getXAxis() {
        return xAxis;
    }

    public void setXAxis(Integer xAxis) {
        this.xAxis = xAxis;
    }

    public Integer getyAxis() {
        return yAxis;
    }

    public void setyAxis(Integer yAxis) {
        this.yAxis = yAxis;
    }

    public Integer getxAxis() {
        return xAxis;
    }

    public void setxAxis(Integer xAxis) {
        this.xAxis = xAxis;
    }

    public Integer getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(Integer gridWidth) {
        this.gridWidth = gridWidth;
    }

    public Integer getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(Integer gridHeight) {
        this.gridHeight = gridHeight;
    }
}
