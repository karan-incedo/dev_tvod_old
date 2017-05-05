
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SecondaryHover {

    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("border")
    @Expose
    private Border___ border;
    @SerializedName("text")
    @Expose
    private Text___ text;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Border___ getBorder() {
        return border;
    }

    public void setBorder(Border___ border) {
        this.border = border;
    }

    public Text___ getText() {
        return text;
    }

    public void setText(Text___ text) {
        this.text = text;
    }

}
