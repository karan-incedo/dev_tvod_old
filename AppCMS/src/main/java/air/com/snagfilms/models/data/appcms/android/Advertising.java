
package air.com.snagfilms.models.data.appcms.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Advertising {

    @SerializedName("bannerTag")
    @Expose
    private String bannerTag;
    @SerializedName("videoTag")
    @Expose
    private String videoTag;

    public String getBannerTag() {
        return bannerTag;
    }

    public void setBannerTag(String bannerTag) {
        this.bannerTag = bannerTag;
    }

    public String getVideoTag() {
        return videoTag;
    }

    public void setVideoTag(String videoTag) {
        this.videoTag = videoTag;
    }

}
