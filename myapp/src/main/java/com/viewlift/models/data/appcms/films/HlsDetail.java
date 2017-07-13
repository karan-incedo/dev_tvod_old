
package com.viewlift.models.data.appcms.films;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HlsDetail {

    @SerializedName("url")
    @Expose
    private Object url;
    @SerializedName("fileSize")
    @Expose
    private Object fileSize;

    public Object getUrl() {
        return url;
    }

    public void setUrl(Object url) {
        this.url = url;
    }

    public Object getFileSize() {
        return fileSize;
    }

    public void setFileSize(Object fileSize) {
        this.fileSize = fileSize;
    }

}
