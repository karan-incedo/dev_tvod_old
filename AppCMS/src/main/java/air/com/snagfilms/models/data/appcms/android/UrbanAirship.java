
package air.com.snagfilms.models.data.appcms.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UrbanAirship {

    @SerializedName("key")
    @Expose
    private Key_ key;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("secret")
    @Expose
    private Secret secret;
    @SerializedName("username")
    @Expose
    private String username;

    public Key_ getKey() {
        return key;
    }

    public void setKey(Key_ key) {
        this.key = key;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
