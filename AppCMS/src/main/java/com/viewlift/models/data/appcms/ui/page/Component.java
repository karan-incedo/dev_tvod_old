package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@UseStag(UseStag.FieldOption.SERIALIZED_NAME)
public class Component implements ModuleWithComponents, Serializable {

    @SerializedName("text")
    @Expose
    String text;

    @SerializedName("textColor")
    @Expose
    String textColor;

    @SerializedName("backgroundColor")
    @Expose
    String backgroundColor;

    @SerializedName("layout")
    @Expose
    Layout layout;

    @SerializedName("backgroundSelectedColor")
    @Expose
    String backgroundSelectedColor;

    @SerializedName("action")
    @Expose
    String action;

    @SerializedName("type")
    @Expose
    String type;

    @SerializedName("key")
    @Expose
    String key;

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    @SerializedName("opacity")
    @Expose
    int opacity;

    @SerializedName("borderColor")
    @Expose
    String borderColor;

    @SerializedName("fillColor")
    @Expose
    String fillColor;

    @SerializedName("borderWidth")
    @Expose
    int borderWidth;

    @SerializedName("imageName")
    @Expose
    String imageName;

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    @SerializedName("icon_url")
    @Expose
    String icon_url;

    @SerializedName("textAlignment")
    @Expose
    String textAlignment;

    @SerializedName("numberOfLines")
    @Expose
    int numberOfLines;

    @SerializedName("trayPadding")
    @Expose
    int trayPadding;

    @SerializedName("cornerRadius")
    @Expose
    int cornerRadius;

    @SerializedName("isHorizontalScroll")
    @Expose
    boolean isHorizontalScroll;

    @SerializedName("supportPagination")
    @Expose
    boolean supportPagination;

    @SerializedName("headerView")
    @Expose
    boolean headerView;

    @SerializedName("trayClickAction")
    @Expose
    String trayClickAction;

    public String getItemClickAction() {
        return itemClickAction;
    }

    public void setItemClickAction(String itemClickAction) {
        this.itemClickAction = itemClickAction;
    }

    @SerializedName("itemClickAction")
    @Expose
    String itemClickAction;

    @SerializedName("fontFamily")
    @Expose
    String fontFamily;

    @SerializedName("fontSize")
    @Expose
    int fontSize;

    @SerializedName("components")
    @Expose
    ArrayList<Component> components;

    @SerializedName("progressColor")
    @Expose
    String progressColor;

    @SerializedName("unprogressColor")
    @Expose
    String unprogressColor;

    @SerializedName("selectedColor")
    @Expose
    String selectedColor;

    @SerializedName("unSelectedColor")
    @Expose
    String unSelectedColor;

    @SerializedName("isVisibleForPhone")
    @Expose
    boolean isVisibleForPhone;

    @SerializedName("isVisibleForTablet")
    @Expose
    boolean isVisibleForTablet;

    @SerializedName("styles")
    @Expose
    Styles styles;

    @SerializedName("fontWeight")
    @Expose
    String fontWeight;

    @SerializedName("fontFamilyKey")
    @Expose
    String fontFamilyKey;

    @SerializedName("fontFamilyValue")
    @Expose
    String fontFamilyValue;

    @SerializedName("view")
    @Expose
    String view;

    @SerializedName("protected")
    @Expose
    boolean isViewProtected;
	
    @SerializedName("selectedText")
    @Expose
    String selectedText;

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    @SerializedName("padding")
    @Expose
    private int padding;

    @SerializedName("svod")
    @Expose
    boolean svod;

    public String getHintColor() {
        return hintColor;
    }
    public void setHintColor(String hintColor) {
        this.hintColor = hintColor;
    }

    @SerializedName("hintColor")
    @Expose
    String hintColor;

    @SerializedName("blockName")
    @Expose
    String blockName;

    boolean yAxisSetManually;

    boolean widthModified;

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
    @Override
    public void setId(String id) {
    }
    @Override
    public void setSettings(Settings settings) {
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

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
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

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
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

    public ArrayList<Component> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<Component> components) {
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
        this.isVisibleForPhone = visibleForPhone;
    }

    public boolean isVisibleForTablet() {
        return isVisibleForTablet;
    }

    public void setVisibleForTablet(boolean visibleForTablet) {
        this.isVisibleForTablet = visibleForTablet;
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

    @Override
    public Settings getSettings() {
        return null;
    }

    public void setView(String view) {
        this.view = view;
    }

    public boolean isViewProtected() {
        return isViewProtected;
    }

    public void setIsViewProtected(boolean isViewProtected) {
        this.isViewProtected = isViewProtected;
    }

    public boolean isyAxisSetManually() {
        return yAxisSetManually;
    }

    public void setyAxisSetManually(boolean yAxisSetManually) {
        this.yAxisSetManually = yAxisSetManually;
    }

    @Override
    public String getId() {
        return null;
    }

    public String getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(String selectedText) {
        this.selectedText = selectedText;
    }
	
    float letterSpacing;
    public float getLetetrSpacing() {
        return letterSpacing;
    }

    public void setLetetrSpacing(float letetrSpacing) {
        this.letterSpacing = letetrSpacing;
    }

    public boolean isSvod() {
        return svod;
    }

    @Override
    public String getBlockName() {
        return null;
    }

    @Override
    public void setBlockName(String blockName) {

    }

    public void setSvod(boolean svod) {
        this.svod = svod;
    }

    public boolean isWidthModified() {
        return widthModified;
    }

    public void setWidthModified(boolean widthModified) {
        this.widthModified = widthModified;
    }
    public boolean isHeaderView() {
        return headerView;
    }

    public void setHeaderView(boolean headerView) {
        this.headerView = headerView;
    }
}
