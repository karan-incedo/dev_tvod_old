
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DropdownHover {

    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("text")
    @Expose
    private Text______ text;

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Text______ getText() {
        return text;
    }

    public void setText(Text______ text) {
        this.text = text;
    }

}
