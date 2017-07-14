
package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trailer {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("permalink")
    @Expose
    private String permalink;
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
    private VideoAssets videoAssets;
    @SerializedName("title")
    @Expose
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
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

    public VideoAssets getVideoAssets() {
        return videoAssets;
    }

    public void setVideoAssets(VideoAssets videoAssets) {
        this.videoAssets = videoAssets;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
