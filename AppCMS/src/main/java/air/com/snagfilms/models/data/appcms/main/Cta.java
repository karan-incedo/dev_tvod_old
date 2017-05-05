
package air.com.snagfilms.models.data.appcms.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cta {

    @SerializedName("primary")
    @Expose
    private Primary primary;
    @SerializedName("primary--hover")
    @Expose
    private PrimaryHover primaryHover;
    @SerializedName("secondary")
    @Expose
    private Secondary secondary;
    @SerializedName("secondary--hover")
    @Expose
    private SecondaryHover secondaryHover;

    public Primary getPrimary() {
        return primary;
    }

    public void setPrimary(Primary primary) {
        this.primary = primary;
    }

    public PrimaryHover getPrimaryHover() {
        return primaryHover;
    }

    public void setPrimaryHover(PrimaryHover primaryHover) {
        this.primaryHover = primaryHover;
    }

    public Secondary getSecondary() {
        return secondary;
    }

    public void setSecondary(Secondary secondary) {
        this.secondary = secondary;
    }

    public SecondaryHover getSecondaryHover() {
        return secondaryHover;
    }

    public void setSecondaryHover(SecondaryHover secondaryHover) {
        this.secondaryHover = secondaryHover;
    }

}
