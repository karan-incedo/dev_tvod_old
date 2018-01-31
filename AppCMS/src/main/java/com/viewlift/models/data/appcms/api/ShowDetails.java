package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.List;

/**
 * Created by viewlift on 11/30/17.
 */

@UseStag
public class ShowDetails implements Serializable {
    @SerializedName("status")
    @Expose
    String status;

    @SerializedName("trailers")
    @Expose
    List<Trailer> trailers;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
