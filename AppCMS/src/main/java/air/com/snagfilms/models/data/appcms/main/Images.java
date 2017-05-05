
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Images {

    @SerializedName("placeholderCover")
    @Expose
    private String placeholderCover;
    @SerializedName("placeholderPoster")
    @Expose
    private String placeholderPoster;

    public String getPlaceholderCover() {
        return placeholderCover;
    }

    public void setPlaceholderCover(String placeholderCover) {
        this.placeholderCover = placeholderCover;
    }

    public String getPlaceholderPoster() {
        return placeholderPoster;
    }

    public void setPlaceholderPoster(String placeholderPoster) {
        this.placeholderPoster = placeholderPoster;
    }

}
