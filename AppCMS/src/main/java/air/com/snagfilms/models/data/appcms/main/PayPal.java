
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayPal {

    @SerializedName("apiPassword")
    @Expose
    private String apiPassword;
    @SerializedName("apiUsername")
    @Expose
    private String apiUsername;
    @SerializedName("authoriztionUrl")
    @Expose
    private String authoriztionUrl;
    @SerializedName("payerId")
    @Expose
    private String payerId;
    @SerializedName("signature")
    @Expose
    private String signature;

    public String getApiPassword() {
        return apiPassword;
    }

    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getApiUsername() {
        return apiUsername;
    }

    public void setApiUsername(String apiUsername) {
        this.apiUsername = apiUsername;
    }

    public String getAuthoriztionUrl() {
        return authoriztionUrl;
    }

    public void setAuthoriztionUrl(String authoriztionUrl) {
        this.authoriztionUrl = authoriztionUrl;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}
