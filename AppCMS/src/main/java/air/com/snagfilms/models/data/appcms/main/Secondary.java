
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Secondary {

    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("border")
    @Expose
    private Border__ border;
    @SerializedName("text")
    @Expose
    private Text__ text;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Border__ getBorder() {
        return border;
    }

    public void setBorder(Border__ border) {
        this.border = border;
    }

    public Text__ getText() {
        return text;
    }

    public void setText(Text__ text) {
        this.text = text;
    }

}
