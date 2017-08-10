package com.viewlift.models.data.appcms.ui.tv;

/**
 * Created by nitin.tyagi on 7/5/2017.
 */

public class FireTV {
    private String yAxis;

    private String height;

    private String rightMargin;

    private String width;

    private String xAxis;

    private String leftMargin;

    private String bottomMargin;

    private String fontSizeValue;

    private int fontSize;

    private Float fontSizeKey;

    public String getTopMargin() {
        return topMargin;
    }

    public String getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(String bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public void setTopMargin(String topMargin) {
        this.topMargin = topMargin;
    }

    private String topMargin;



    public String getPadding() {
        return padding;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }

    private String padding;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    private String backgroundColor;

    public String getYAxis ()
    {
        return yAxis;
    }

    public void setYAxis (String yAxis)
    {
        this.yAxis = yAxis;
    }

    public String getHeight ()
    {
        return height;
    }

    public void setHeight (String height)
    {
        this.height = height;
    }

    public String getRightMargin ()
    {
        return rightMargin;
    }

    public void setRightMargin (String rightMargin)
    {
        this.rightMargin = rightMargin;
    }

    public String getWidth ()
    {
        return width;
    }

    public void setWidth (String width)
    {
        this.width = width;
    }

    public String getXAxis ()
    {
        return xAxis;
    }

    public void setXAxis (String xAxis)
    {
        this.xAxis = xAxis;
    }

    public String getLeftMargin ()
    {
        return leftMargin;
    }

    public void setLeftMargin (String leftMargin)
    {
        this.leftMargin = leftMargin;
    }

    public String getFontSizeValue() {
        return fontSizeValue;
    }

    public void setFontSizeValue(String fontSizeValue) {
        this.fontSizeValue = fontSizeValue;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Float getFontSizeKey() {
        return fontSizeKey;
    }

    public void setFontSizeKey(Float fontSizeKey) {
        this.fontSizeKey = fontSizeKey;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [yAxis = "+yAxis+", height = "+height+", rightMargin = "+rightMargin+", width = "+width+", xAxis = "+xAxis+", leftMargin = "+leftMargin+"]";
    }
}
