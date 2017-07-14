package com.viewlift.models.data.appcms.watchlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Credit {

    @SerializedName("objectKey")
    @Expose
    String objectKey;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("url")
    @Expose
    String url;

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
