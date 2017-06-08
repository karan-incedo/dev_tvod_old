
package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Statistics {

    @SerializedName("tmdbRatingCount")
    @Expose
    private Integer tmdbRatingCount;
    @SerializedName("tmdbRatingAvg")
    @Expose
    private Integer tmdbRatingAvg;
    @SerializedName("averageViewerGrade")
    @Expose
    private String averageViewerGrade;

    public Integer getTmdbRatingCount() {
        return tmdbRatingCount;
    }

    public void setTmdbRatingCount(Integer tmdbRatingCount) {
        this.tmdbRatingCount = tmdbRatingCount;
    }

    public Integer getTmdbRatingAvg() {
        return tmdbRatingAvg;
    }

    public void setTmdbRatingAvg(Integer tmdbRatingAvg) {
        this.tmdbRatingAvg = tmdbRatingAvg;
    }

    public String getAverageViewerGrade() {
        return averageViewerGrade;
    }

    public void setAverageViewerGrade(String averageViewerGrade) {
        this.averageViewerGrade = averageViewerGrade;
    }

}
