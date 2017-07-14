
package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Component implements Module {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("textColor")
    @Expose
    private String textColor;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("layout")
    @Expose
    private Layout layout;
    @SerializedName("backgroundSelectedColor")
    @Expose
    private String backgroundSelectedColor;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("borderColor")
    @Expose
    private String borderColor;
    @SerializedName("fillColor")
    @Expose
    private String fillColor;
    @SerializedName("borderWidth")
    @Expose
    private Integer borderWidth;
    @SerializedName("imageName")
    @Expose
    private String imageName;
    @SerializedName("textAlignment")
    @Expose
    private String textAlignment;
    @SerializedName("numberOfLines")
    @Expose
    private int numberOfLines;
    @SerializedName("trayPadding")
    @Expose
    private int trayPadding;
    @SerializedName("cornerRadius")
    @Expose
    private int cornerRaidus;
    @SerializedName("isHorizontalScroll")
    @Expose
    private boolean isHorizontalScroll;
    @SerializedName("supportPagination")
    @Expose
    private boolean supportPagination;
    @SerializedName("trayClickAction")
    @Expose
    private String trayClickAction;
    @SerializedName("fontFamily")
    @Expose
    private String fontFamily;
    @SerializedName("fontSize")
    @Expose
    private int fontSize;
    @SerializedName("components")
    @Expose
    private List<Component> components;
    @SerializedName("progressColor")
    @Expose
    private String progressColor;
    @SerializedName("unprogressColor")
    private String unprogressColor;
    @SerializedName("selectedColor")
    @Expose
    private String selectedColor;
    @SerializedName("unSelectedColor")
    @Expose
    private String unSelectedColor;
    @SerializedName("isVisibleForPhone")
    @Expose
    private boolean isVisibleForPhone;
    @SerializedName("isVisibleForTablet")
    @Expose
    private boolean isVisibleForTablet;
    @SerializedName("styles")
    @Expose
    private Styles styles;
    @SerializedName("fontWeight")
    @Expose
    private String fontWeight;
    @SerializedName("fontFamilyKey")
    @Expose
    private String fontFamilyKey;
    @SerializedName("fontFamilyValue")
    @Expose
    private String fontFamilyValue;
    @SerializedName("view")
    @Expose
    private String view;
    @SerializedName("protected")
    @Expose
    private boolean viewProtected;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public String getBackgroundSelectedColor() {
        return backgroundSelectedColor;
    }

    public void setBackgroundSelectedColor(String backgroundSelectedColor) {
        this.backgroundSelectedColor = backgroundSelectedColor;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public Integer getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getTextAlignment() {
        return textAlignment;
    }

    public void setTextAlignment(String textAlignment) {
        this.textAlignment = textAlignment;
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(int numberOfLines) {
        this.numberOfLines = numberOfLines;
    }

    public int getTrayPadding() {
        return trayPadding;
    }

    public void setTrayPadding(int trayPadding) {
        this.trayPadding = trayPadding;
    }

    public int getCornerRaidus() {
        return cornerRaidus;
    }

    public void setCornerRaidus(int cornerRaidus) {
        this.cornerRaidus = cornerRaidus;
    }

    public boolean isHorizontalScroll() {
        return isHorizontalScroll;
    }

    public void setHorizontalScroll(boolean horizontalScroll) {
        isHorizontalScroll = horizontalScroll;
    }

    public boolean isSupportPagination() {
        return supportPagination;
    }

    public void setSupportPagination(boolean supportPagination) {
        this.supportPagination = supportPagination;
    }

    public String getTrayClickAction() {
        return trayClickAction;
    }

    public void setTrayClickAction(String trayClickAction) {
        this.trayClickAction = trayClickAction;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public String getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(String progressColor) {
        this.progressColor = progressColor;
    }

    public String getUnprogressColor() {
        return unprogressColor;
    }

    public void setUnprogressColor(String unprogressColor) {
        this.unprogressColor = unprogressColor;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    public String getUnSelectedColor() {
        return unSelectedColor;
    }

    public void setUnSelectedColor(String unSelectedColor) {
        this.unSelectedColor = unSelectedColor;
    }

    public boolean isVisibleForPhone() {
        return isVisibleForPhone;
    }

    public void setVisibleForPhone(boolean visibleForPhone) {
        isVisibleForPhone = visibleForPhone;
    }

    public boolean isVisibleForTablet() {
        return isVisibleForTablet;
    }

    public void setVisibleForTablet(boolean visibleForTablet) {
        isVisibleForTablet = visibleForTablet;
    }

    public Styles getStyles() {
        return styles;
    }

    public void setStyles(Styles styles) {
        this.styles = styles;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public String getFontFamilyKey() {
        return fontFamilyKey;
    }

    public void setFontFamilyKey(String fontFamilyKey) {
        this.fontFamilyKey = fontFamilyKey;
    }

    public String getFontFamilyValue() {
        return fontFamilyValue;
    }

    public void setFontFamilyValue(String fontFamilyValue) {
        this.fontFamilyValue = fontFamilyValue;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public boolean isViewProtected() {
        return viewProtected;
    }

    public void setViewProtected(boolean viewProtected) {
        this.viewProtected = viewProtected;
    }
}
