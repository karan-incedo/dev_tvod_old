
package air.com.snagfilms.models.data.appcms.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Component {

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
    private Layout_ layout;
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

    public Layout_ getLayout() {
        return layout;
    }

    public void setLayout(Layout_ layout) {
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
}
