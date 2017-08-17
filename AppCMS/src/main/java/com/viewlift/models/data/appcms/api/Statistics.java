package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Statistics {

    @SerializedName("tmdbRatingCount")
    @Expose
    int tmdbRatingCount;

    @SerializedName("tmdbRatingAvg")
    @Expose
    double tmdbRatingAvg;

    @SerializedName("averageViewerGrade")
    @Expose
    String averageViewerGrade;

    public int getTmdbRatingCount() {
        return tmdbRatingCount;
    }

    public void setTmdbRatingCount(int tmdbRatingCount) {
        this.tmdbRatingCount = tmdbRatingCount;
    }

    public double getTmdbRatingAvg() {
        return tmdbRatingAvg;
    }

    public void setTmdbRatingAvg(double tmdbRatingAvg) {
        this.tmdbRatingAvg = tmdbRatingAvg;
    }

    public String getAverageViewerGrade() {
        return averageViewerGrade;
    }

    public void setAverageViewerGrade(String averageViewerGrade) {
        this.averageViewerGrade = averageViewerGrade;
    }
}
