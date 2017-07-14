
package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Desktop {

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
    @SerializedName("xAxis")
    @Expose
    private Float xAxis;

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
        return rightMargin;
    }

    public void setRightMargin(Float rightMargin) {
        this.rightMargin = rightMargin;
    }

    public Float getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(Float leftMargin) {
        this.leftMargin = leftMargin;
    }

    public Float getXAxis() {
        return xAxis;
    }

    public void setXAxis(Float xAxis) {
        this.xAxis = xAxis;
    }

}
