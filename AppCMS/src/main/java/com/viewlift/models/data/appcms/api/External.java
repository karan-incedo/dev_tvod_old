package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class External {

    @SerializedName("brightCoveId")
    @Expose
    Object brightCoveId;

    @SerializedName("yTVideoId")
    @Expose
    Object yTVideoId;

    @SerializedName("tmsId")
    @Expose
    Object tmsId;

    @SerializedName("fBVideoId")
    @Expose
    Object fBVideoId;

    @SerializedName("externalId")
    @Expose
    Object externalId;

    @SerializedName("imdbId")
    @Expose
    String imdbId;

    @SerializedName("tmdbId")
    @Expose
    int tmdbId;

    public Object getBrightCoveId() {
        return brightCoveId;
    }

    public void setBrightCoveId(Object brightCoveId) {
        this.brightCoveId = brightCoveId;
    }

    public Object getYTVideoId() {
        return yTVideoId;
    }

    public void setYTVideoId(Object yTVideoId) {
        this.yTVideoId = yTVideoId;
    }

    public Object getTmsId() {
        return tmsId;
    }

    public void setTmsId(Object tmsId) {
        this.tmsId = tmsId;
    }

    public Object getFBVideoId() {
        return fBVideoId;
    }

    public void setFBVideoId(Object fBVideoId) {
        this.fBVideoId = fBVideoId;
    }

    public Object getExternalId() {
        return externalId;
    }

    public void setExternalId(Object externalId) {
        this.externalId = externalId;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }
}
