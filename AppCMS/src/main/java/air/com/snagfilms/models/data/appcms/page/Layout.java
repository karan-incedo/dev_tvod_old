
package air.com.snagfilms.models.data.appcms.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Layout {

    @SerializedName("bottomMargin")
    @Expose
    private double bottomMargin;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("isVerticalyCentered")
    @Expose
    private boolean isVerticalyCentered;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("width")
    @Expose
    private int width;
    @SerializedName("leftMargin")
    @Expose
    private int leftMargin;
    @SerializedName("rightMargin")
    @Expose
    private int rightMargin;
    @SerializedName("topMargin")
    @Expose
    private double topMargin;
    @SerializedName("xAxis")
    @Expose
    private int xAxis;
    @SerializedName("yAxis")
    @Expose
    private int yAxis;

    public double getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(double bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isIsVerticalyCentered() {
        return isVerticalyCentered;
    }

    public void setIsVerticalyCentered(boolean isVerticalyCentered) {
        this.isVerticalyCentered = isVerticalyCentered;
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

    public int getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    public int getRightMargin() {
        return rightMargin;
    }

    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }

    public double getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(double topMargin) {
        this.topMargin = topMargin;
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
