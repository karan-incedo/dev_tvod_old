
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Beacon {

    @SerializedName("apiBaseUrl")
    @Expose
    private String apiBaseUrl;
    @SerializedName("clientId")
    @Expose
    private String clientId;
    @SerializedName("pfm")
    @Expose
    private String pfm;
    @SerializedName("siteName")
    @Expose
    private String siteName;

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPfm() {
        return pfm;
    }

    public void setPfm(String pfm) {
        this.pfm = pfm;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

}
