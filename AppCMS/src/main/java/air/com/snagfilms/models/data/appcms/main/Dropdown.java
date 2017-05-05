
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Dropdown {

    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("text")
    @Expose
    private Text____ text;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Text____ getText() {
        return text;
    }

    public void setText(Text____ text) {
        this.text = text;
    }

}
