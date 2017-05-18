
package air.com.snagfilms.models.data.appcms.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Desktop {

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
    @SerializedName("xAxis")
    @Expose
    private Integer xAxis;

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

    public Integer getXAxis() {
        return xAxis;
    }

    public void setXAxis(Integer xAxis) {
        this.xAxis = xAxis;
    }

}
