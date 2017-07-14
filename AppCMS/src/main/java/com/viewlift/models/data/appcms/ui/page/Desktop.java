package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Desktop {

    @SerializedName("height")
    @Expose
    float height;

    @SerializedName("yAxis")
    @Expose
    float yAxis;

    @SerializedName("rightMargin")
    @Expose
    float rightMargin;

    @SerializedName("leftMargin")
    @Expose
    float leftMargin;

    @SerializedName("xAxis")
    @Expose
    float xAxis;

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getYAxis() {
        return yAxis;
    }

    public void setYAxis(float yAxis) {
        this.yAxis = yAxis;
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

    public float getXAxis() {
        return xAxis;
    }

    public void setXAxis(float xAxis) {
        this.xAxis = xAxis;
    }
}
