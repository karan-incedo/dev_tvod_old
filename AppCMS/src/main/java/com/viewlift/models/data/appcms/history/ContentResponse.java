package com.viewlift.models.data.appcms.history;

/*
 * Created by Viewlift on 7/5/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.Gist;
import com.vimeo.stag.UseStag;

@UseStag
public class ContentResponse {

    @SerializedName("gist")
    @Expose
    Gist gist;

    @SerializedName("grade")
    @Expose
    String grade;

    public Gist getGist() {
        return gist;
    }

    public void setGist(Gist gist) {
        this.gist = gist;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
