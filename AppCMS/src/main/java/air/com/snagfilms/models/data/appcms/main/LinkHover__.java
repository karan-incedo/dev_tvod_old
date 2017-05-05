
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinkHover__ {

    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("text")
    @Expose
    private Text_________ text;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Text_________ getText() {
        return text;
    }

    public void setText(Text_________ text) {
        this.text = text;
    }

}
