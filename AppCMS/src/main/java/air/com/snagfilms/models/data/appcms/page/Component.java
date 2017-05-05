
package air.com.snagfilms.models.data.appcms.page;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Component {

    @SerializedName("Layout")
    @Expose
    private List<Layout_> layout = null;
    @SerializedName("fontFamily")
    @Expose
    private String fontFamily;
    @SerializedName("fontSize")
    @Expose
    private int fontSize;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("numberOfLines")
    @Expose
    private int numberOfLines;
    @SerializedName("textAlignment")
    @Expose
    private String textAlignment;
    @SerializedName("textColor")
    @Expose
    private String textColor;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("backgroundColor")
    @Expose
    private int backgroundColor;
    @SerializedName("collectionGridComponents")
    @Expose
    private List<CollectionGridComponent> collectionGridComponents = null;
    @SerializedName("cornerRadius")
    @Expose
    private int cornerRadius;
    @SerializedName("isHorizontalScroll")
    @Expose
    private boolean isHorizontalScroll;
    @SerializedName("supportPagination")
    @Expose
    private boolean supportPagination;
    @SerializedName("trayClickAction")
    @Expose
    private String trayClickAction;
    @SerializedName("trayPadding")
    @Expose
    private int trayPadding;

    public List<Layout_> getLayout() {
        return layout;
    }

    public void setLayout(List<Layout_> layout) {
        this.layout = layout;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(int numberOfLines) {
        this.numberOfLines = numberOfLines;
    }

    public String getTextAlignment() {
        return textAlignment;
    }

    public void setTextAlignment(String textAlignment) {
        this.textAlignment = textAlignment;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public List<CollectionGridComponent> getCollectionGridComponents() {
        return collectionGridComponents;
    }

    public void setCollectionGridComponents(List<CollectionGridComponent> collectionGridComponents) {
        this.collectionGridComponents = collectionGridComponents;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public boolean isIsHorizontalScroll() {
        return isHorizontalScroll;
    }

    public void setIsHorizontalScroll(boolean isHorizontalScroll) {
        this.isHorizontalScroll = isHorizontalScroll;
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

    public int getTrayPadding() {
        return trayPadding;
    }

    public void setTrayPadding(int trayPadding) {
        this.trayPadding = trayPadding;
    }

}
