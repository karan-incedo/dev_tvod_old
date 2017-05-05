
package air.com.snagfilms.models.data.appcms.android;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notifications {

    @SerializedName("urbanAirship")
    @Expose
    private UrbanAirship urbanAirship;

    public UrbanAirship getUrbanAirship() {
        return urbanAirship;
    }

    public void setUrbanAirship(UrbanAirship urbanAirship) {
        this.urbanAirship = urbanAirship;
    }

}
