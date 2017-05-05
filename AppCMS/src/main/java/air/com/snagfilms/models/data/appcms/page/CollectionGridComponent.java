
package air.com.snagfilms.models.data.appcms.page;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CollectionGridComponent {

    @SerializedName("Layout")
    @Expose
    private List<Layout__> layout = null;
    @SerializedName("imageName")
    @Expose
    private String imageName;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("fontFamily")
    @Expose
    private String fontFamily;
    @SerializedName("fontSize")
    @Expose
    private int fontSize;
    @SerializedName("numberOfLines")
    @Expose
    private int numberOfLines;
    @SerializedName("selectedTextColor")
    @Expose
    private String selectedTextColor;
    @SerializedName("textAlignment")
    @Expose
    private String textAlignment;
    @SerializedName("textColor")
    @Expose
    private String textColor;
    @SerializedName("progressColor")
    @Expose
    private String progressColor;
    @SerializedName("unprogressColor")
    @Expose
    private String unprogressColor;

    public List<Layout__> getLayout() {
        return layout;
    }

    public void setLayout(List<Layout__> layout) {
        this.layout = layout;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(int numberOfLines) {
        this.numberOfLines = numberOfLines;
    }

    public String getSelectedTextColor() {
        return selectedTextColor;
    }

    public void setSelectedTextColor(String selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
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

}
