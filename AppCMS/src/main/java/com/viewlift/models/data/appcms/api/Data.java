package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

@UseStag
public class Data implements Serializable {

    @SerializedName("metadata")
    @Expose
    List<MetaData> metadata = null;

    @SerializedName("images")
    @Expose
    Images images;

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    @SerializedName("permalink")
    @Expose
    String permalink;

    public List<MetaData> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<MetaData> metadata) {
        this.metadata = metadata;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }



}
