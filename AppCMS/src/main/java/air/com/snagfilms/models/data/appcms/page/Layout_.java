
package air.com.snagfilms.models.data.appcms.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Layout_ {

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
    @SerializedName("bottomMargin")
    @Expose
    private int bottomMargin;
    @SerializedName("gridHeight")
    @Expose
    private int gridHeight;
    @SerializedName("gridWidth")
    @Expose
    private int gridWidth;
    @SerializedName("leftMargin")
    @Expose
    private int leftMargin;
    @SerializedName("rightMargin")
    @Expose
    private int rightMargin;
    @SerializedName("topMargin")
    @Expose
    private int topMargin;

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

    public int getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
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

    public int getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

}
