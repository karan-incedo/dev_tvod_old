
package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class External {

    @SerializedName("brightCoveId")
    @Expose
    private Object brightCoveId;
    @SerializedName("yTVideoId")
    @Expose
    private Object yTVideoId;
    @SerializedName("tmsId")
    @Expose
    private Object tmsId;
    @SerializedName("fBVideoId")
    @Expose
    private Object fBVideoId;
    @SerializedName("externalId")
    @Expose
    private Object externalId;
    @SerializedName("imdbId")
    @Expose
    private String imdbId;
    @SerializedName("tmdbId")
    @Expose
    private Integer tmdbId;

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

    public Integer getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

}
