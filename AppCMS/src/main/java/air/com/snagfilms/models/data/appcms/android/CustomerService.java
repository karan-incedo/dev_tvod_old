
package air.com.snagfilms.models.data.appcms.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerService {

    @SerializedName("apptentiveApiKey")
    @Expose
    private String apptentiveApiKey;

    public String getApptentiveApiKey() {
        return apptentiveApiKey;
    }

    public void setApptentiveApiKey(String apptentiveApiKey) {
        this.apptentiveApiKey = apptentiveApiKey;
    }

}
