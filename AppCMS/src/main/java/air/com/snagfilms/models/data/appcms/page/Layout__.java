
package air.com.snagfilms.models.data.appcms.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Layout__ {

    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("xAxis")
    @Expose
    private int xAxis;
    @SerializedName("yAxis")
    @Expose
    private int yAxis;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getXAxis() {
        return xAxis;
    }

    public void setXAxis(int xAxis) {
        this.xAxis = xAxis;
    }

    public int getYAxis() {
        return yAxis;
    }

    public void setYAxis(int yAxis) {
        this.yAxis = yAxis;
    }

}
