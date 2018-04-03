package com.viewlift.models.data.appcms.ui.tv;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by nitin.tyagi on 7/5/2017.
 */

public class FireTV implements Serializable {
    private String yAxis;

    private String height;

    private String rightMargin;

    private String width;

    private String xAxis;

    private String leftMargin;

    private String itemWidth;

    private String itemHeight;

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    private String orientation;

    public Float getFontSizeKey() {
        return fontSizeKey;
    }

    public void setFontSizeKey(Float fontSizeKey) {
        this.fontSizeKey = fontSizeKey;
    }

    public Float getFontSizeValue() {
        return fontSizeValue;
    }

    public void setFontSizeValue(Float fontSizeValue) {
        this.fontSizeValue = fontSizeValue;
    }

    @SerializedName("fontSizeKey")
    @Expose
    private Float fontSizeKey;
    @SerializedName("fontSizeValue")
    @Expose
    private Float fontSizeValue;
    @SerializedName("itemSpacing")
    @Expose
    private String itemSpacing;
    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    private int fontSize;

    public String getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(String bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    private String bottomMargin;

    public String getTopMargin() {
        return topMargin;
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

    public String getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(String itemWidth) {
        this.itemWidth = itemWidth;
    }

    public String getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(String itemHeight) {
        this.itemHeight = itemHeight;
    }

    public String getItemSpacing() {
        return itemSpacing;
    }

    public void setItemSpacing(String itemSpacing) {
        this.itemSpacing = itemSpacing;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [yAxis = "+yAxis+", height = "+height+", rightMargin = "+rightMargin+", width = "+width+", xAxis = "+xAxis+", leftMargin = "+leftMargin+"]";
    }
}