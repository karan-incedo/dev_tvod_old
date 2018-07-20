package com.viewlift.models.data.appcms.ui.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Phone {



    @SerializedName("number")
    @Expose
    String number;

    @SerializedName("country")
    @Expose
    String country;

    public String getNumber() {
        return number;
    }

    public String getCountry() {
        return country;
    }
}
