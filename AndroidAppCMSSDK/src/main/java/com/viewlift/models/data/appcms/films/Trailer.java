
package com.viewlift.models.data.appcms.films;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trailer {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("permalink")
    @Expose
    private Object permalink;
    @SerializedName("mediaType")
    @Expose
    private Object mediaType;
    @SerializedName("videoImageUrl")
    @Expose
    private Object videoImageUrl;
    @SerializedName("posterImageUrl")
    @Expose
    private Object posterImageUrl;
    @SerializedName("videoAssets")
    @Expose
    private Object videoAssets;
    @SerializedName("title")
    @Expose
    private Object title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getPermalink() {
        return permalink;
    }

    public void setPermalink(Object permalink) {
        this.permalink = permalink;
    }

    public Object getMediaType() {
        return mediaType;
    }

    public void setMediaType(Object mediaType) {
        this.mediaType = mediaType;
    }

    public Object getVideoImageUrl() {
        return videoImageUrl;
    }

    public void setVideoImageUrl(Object videoImageUrl) {
        this.videoImageUrl = videoImageUrl;
    }

    public Object getPosterImageUrl() {
        return posterImageUrl;
    }

    public void setPosterImageUrl(Object posterImageUrl) {
        this.posterImageUrl = posterImageUrl;
    }

    public Object getVideoAssets() {
        return videoAssets;
    }

    public void setVideoAssets(Object videoAssets) {
        this.videoAssets = videoAssets;
    }

    public Object getTitle() {
        return title;
    }

    public void setTitle(Object title) {
        this.title = title;
    }

}
