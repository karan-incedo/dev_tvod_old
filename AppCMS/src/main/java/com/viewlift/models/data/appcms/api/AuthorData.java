package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by ram.kailash on 2/14/2018.
 */
@UseStag
public class AuthorData implements Serializable{

    private String site;
    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("publishDate")
    @Expose
    Long publishDate;

    @SerializedName("name")
    @Expose
    String name;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getId() {
        return id;
    }

    public Long getPublishDate() {
        return publishDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
