
package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TabletLandscape {

    @SerializedName("yAxis")
    @Expose
    private Integer yAxis;
    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("height")
    @Expose
    private Integer height;
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
    @SerializedName("gridHeight")
    @Expose
    private Integer gridHeight;
    @SerializedName("gridWidth")
    @Expose
    private Integer gridWidth;

    public Integer getYAxis() {
        return yAxis;
    }

    public void setYAxis(Integer yAxis) {
        this.yAxis = yAxis;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
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

    public Integer getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(Integer gridHeight) {
        this.gridHeight = gridHeight;
    }

    public Integer getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(Integer gridWidth) {
        this.gridWidth = gridWidth;
    }
}
