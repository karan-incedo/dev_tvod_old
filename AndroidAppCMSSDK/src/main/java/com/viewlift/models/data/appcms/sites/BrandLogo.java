
package com.viewlift.models.data.appcms.sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BrandLogo {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("imageTag")
    @Expose
    private String imageTag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageTag() {
        return imageTag;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }

}
