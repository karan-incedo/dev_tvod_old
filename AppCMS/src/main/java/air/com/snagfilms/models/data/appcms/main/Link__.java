
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Link__ {

    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("text")
    @Expose
    private Text_______ text;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Text_______ getText() {
        return text;
    }

    public void setText(Text_______ text) {
        this.text = text;
    }

}
