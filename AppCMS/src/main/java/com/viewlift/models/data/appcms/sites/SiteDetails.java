package com.viewlift.models.data.appcms.sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class SiteDetails {

    @SerializedName("supportedDevices")
    @Expose
    Object supportedDevices;

    @SerializedName("brandLogos")
    @Expose
    List<BrandLogo> brandLogos = null;

    public Object getSupportedDevices() {
        return supportedDevices;
    }

    public void setSupportedDevices(Object supportedDevices) {
        this.supportedDevices = supportedDevices;
    }

    public List<BrandLogo> getBrandLogos() {
        return brandLogos;
    }

    public void setBrandLogos(List<BrandLogo> brandLogos) {
        this.brandLogos = brandLogos;
    }
}
