
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinkActive_ {

    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("text")
    @Expose
    private Text________ text;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Text________ getText() {
        return text;
    }

    public void setText(Text________ text) {
        this.text = text;
    }

}
