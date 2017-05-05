
package air.com.snagfilms.models.data.appcms.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Analytics {

    @SerializedName("appFlyer")
    @Expose
    private AppFlyer appFlyer;
    @SerializedName("googleAnalyticsId")
    @Expose
    private String googleAnalyticsId;

    public AppFlyer getAppFlyer() {
        return appFlyer;
    }

    public void setAppFlyer(AppFlyer appFlyer) {
        this.appFlyer = appFlyer;
    }

    public String getGoogleAnalyticsId() {
        return googleAnalyticsId;
    }

    public void setGoogleAnalyticsId(String googleAnalyticsId) {
        this.googleAnalyticsId = googleAnalyticsId;
    }

}
