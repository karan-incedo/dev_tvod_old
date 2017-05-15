
package air.com.snagfilms.models.data.appcms.page;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Component {

    @SerializedName("Layout")
    @Expose
    private List<Layout> layout = null;
    @SerializedName("action")
    @Expose
    private String action;
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
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("backgroundSelectedColor")
    @Expose
    private String backgroundSelectedColor;
    @SerializedName("collectionGridComponents")
    @Expose
    private List<Component> collectionGridComponents = null;
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
    @SerializedName("imageName")
    @Expose
    private String imageName;
    @SerializedName("progressColor")
    @Expose
    private String progressColor;
    @SerializedName("unprogressColor")
    @Expose
    private String unprogressColor;
    @SerializedName("view")
    @Expose
    private String view;
    @SerializedName("borderColor")
    @Expose
    private String borderColor;
    @SerializedName("borderWidth")
    @Expose
    private int borderWidth;
    @SerializedName("apiUrl")
    @Expose
    private String apiUrl;
    @SerializedName("component")
    @Expose
    private List<Component> component = null;
    @SerializedName("id")
    @Expose
    private String id;

    public List<Layout> getLayout() {
        return layout;
    }

    public void setLayout(List<Layout> layout) {
        this.layout = layout;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundSelectedColor() {
        return backgroundSelectedColor;
    }

    public void setBackgroundSelectedColor(String backgroundSelectedColor) {
        this.backgroundSelectedColor = backgroundSelectedColor;
    }

    public List<Component> getCollectionGridComponents() {
        return collectionGridComponents;
    }

    public void setCollectionGridComponents(List<Component> collectionGridComponents) {
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public List<Component> getComponent() {
        return component;
    }

    public void setComponent(List<Component> component) {
        this.component = component;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
