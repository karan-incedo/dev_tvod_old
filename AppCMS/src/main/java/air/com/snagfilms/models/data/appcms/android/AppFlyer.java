
package air.com.snagfilms.models.data.appcms.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppFlyer {

    @SerializedName("key")
    @Expose
    private Key key;

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

}
