
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrimaryHover {

    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("border")
    @Expose
    private Border_ border;
    @SerializedName("text")
    @Expose
    private Text_ text;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Border_ getBorder() {
        return border;
    }

    public void setBorder(Border_ border) {
        this.border = border;
    }

    public Text_ getText() {
        return text;
    }

    public void setText(Text_ text) {
        this.text = text;
    }

}
