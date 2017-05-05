
package air.com.snagfilms.models.data.appcms.page;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModuleList {

    @SerializedName("Layout")
    @Expose
    private List<Layout> layout = null;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("backgroundSelectedColor")
    @Expose
    private String backgroundSelectedColor;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("textColor")
    @Expose
    private String textColor;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("view")
    @Expose
    private String view;
    @SerializedName("borderColor")
    @Expose
    private String borderColor;
    @SerializedName("borderWidth")
    @Expose
    private int borderWidth;
    @SerializedName("imageName")
    @Expose
    private String imageName;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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
