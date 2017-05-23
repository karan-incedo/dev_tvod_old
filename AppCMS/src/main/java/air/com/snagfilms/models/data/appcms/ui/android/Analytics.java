
package air.com.snagfilms.models.data.appcms.ui.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Analytics {

    @SerializedName("googleTagManagerId")
    @Expose
    private String googleTagManagerId;
    @SerializedName("googleAnalyticsId")
    @Expose
    private String googleAnalyticsId;
    @SerializedName("kochavaAppId")
    @Expose
    private String kochavaAppId;
    @SerializedName("appflyerDevKey")
    @Expose
    private String appflyerDevKey;
    @SerializedName("omnitureAppSDKConfigFile")
    @Expose
    private String omnitureAppSDKConfigFile;

    public String getGoogleTagManagerId() {
        return googleTagManagerId;
    }

    public void setGoogleTagManagerId(String googleTagManagerId) {
        this.googleTagManagerId = googleTagManagerId;
    }

    public String getGoogleAnalyticsId() {
        return googleAnalyticsId;
    }

    public void setGoogleAnalyticsId(String googleAnalyticsId) {
        this.googleAnalyticsId = googleAnalyticsId;
    }

    public String getKochavaAppId() {
        return kochavaAppId;
    }

    public void setKochavaAppId(String kochavaAppId) {
        this.kochavaAppId = kochavaAppId;
    }

    public String getAppflyerDevKey() {
        return appflyerDevKey;
    }

    public void setAppflyerDevKey(String appflyerDevKey) {
        this.appflyerDevKey = appflyerDevKey;
    }

    public String getOmnitureAppSDKConfigFile() {
        return omnitureAppSDKConfigFile;
    }

    public void setOmnitureAppSDKConfigFile(String omnitureAppSDKConfigFile) {
        this.omnitureAppSDKConfigFile = omnitureAppSDKConfigFile;
    }

}
