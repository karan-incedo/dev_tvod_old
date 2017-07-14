
package com.viewlift.models.data.appcms.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Credit {

    @SerializedName("objectKey")
    @Expose
    private String objectKey;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("url")
    @Expose
    private String url;

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
