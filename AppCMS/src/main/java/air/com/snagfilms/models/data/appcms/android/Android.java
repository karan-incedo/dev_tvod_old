
package air.com.snagfilms.models.data.appcms.android;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Android {

    @SerializedName("navigation")
    @Expose
    private Navigation navigation;
    @SerializedName("images")
    @Expose
    private Images images;
    @SerializedName("pages")
    @Expose
    private List<MetaPage> metaPages = null;
    @SerializedName("analytics")
    @Expose
    private Analytics analytics;
    @SerializedName("version")
    @Expose
    private String version;

    public Navigation getNavigation() {
        return navigation;
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public List<MetaPage> getMetaPages() {
        return metaPages;
    }

    public void setMetaPages(List<MetaPage> metaPages) {
        this.metaPages = metaPages;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
