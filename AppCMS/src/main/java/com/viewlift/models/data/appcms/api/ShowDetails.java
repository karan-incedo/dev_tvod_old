package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by anas.azeem on 1/5/2018.
 * Owned by ViewLift, NYC
 */

public class ShowDetails implements Serializable {

    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("site")
    @Expose
    String site;

    @SerializedName("trailers")
    @Expose
    List<Trailer> trailers = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
