package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Trailer implements Serializable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("permalink")
    @Expose
    String permalink;

    @SerializedName("mediaType")
    @Expose
    Object mediaType;

    @SerializedName("videoImageUrl")
    @Expose
    Object videoImageUrl;

    @SerializedName("posterImageUrl")
    @Expose
    Object posterImageUrl;

    @SerializedName("videoAssets")
    @Expose
    VideoAssets videoAssets;

    @SerializedName("title")
    @Expose
    String title;

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
