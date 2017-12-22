package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 12/22/17.
 */

@UseStag
public class TaxProviders {
    @SerializedName("avalara")
    @Expose
    Avalara avalara;

    public Avalara getAvalara() {
        return avalara;
    }

    public void setAvalara(Avalara avalara) {
        this.avalara = avalara;
    }
}
