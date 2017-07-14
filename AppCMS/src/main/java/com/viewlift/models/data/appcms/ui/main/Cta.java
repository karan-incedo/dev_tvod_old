
package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cta {

    @SerializedName("secondary--hover")
    @Expose
    private SecondaryHover secondaryHover;
    @SerializedName("primary")
    @Expose
    private Primary primary;
    @SerializedName("secondary")
    @Expose
    private Secondary secondary;
    @SerializedName("primary--hover")
    @Expose
    private PrimaryHover primaryHover;

    public SecondaryHover getSecondaryHover() {
        return secondaryHover;
    }

    public void setSecondaryHover(SecondaryHover secondaryHover) {
        this.secondaryHover = secondaryHover;
    }

    public Primary getPrimary() {
        return primary;
    }

    public void setPrimary(Primary primary) {
        this.primary = primary;
    }

    public Secondary getSecondary() {
        return secondary;
    }

    public void setSecondary(Secondary secondary) {
        this.secondary = secondary;
    }

    public PrimaryHover getPrimaryHover() {
        return primaryHover;
    }

    public void setPrimaryHover(PrimaryHover primaryHover) {
        this.primaryHover = primaryHover;
    }

}
