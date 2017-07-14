
package com.viewlift.models.data.appcms.sites;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SiteDetails {

    @SerializedName("supportedDevices")
    @Expose
    private Object supportedDevices;
    @SerializedName("brandLogos")
    @Expose
    private List<BrandLogo> brandLogos = null;

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
