package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class CreditBlock {

    @SerializedName("id")
    @Expose
    Object id;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("credits")
    @Expose
    List<Credit> credits = null;

    @SerializedName("containsHollywoodCelebrities")
    @Expose
    boolean containsHollywoodCelebrities;

    @SerializedName("containsTVCelebrities")
    @Expose
    boolean containsTVCelebrities;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Credit> getCredits() {
        return credits;
    }

    public void setCredits(List<Credit> credits) {
        this.credits = credits;
    }

    public boolean getContainsHollywoodCelebrities() {
        return containsHollywoodCelebrities;
    }

    public void setContainsHollywoodCelebrities(boolean containsHollywoodCelebrities) {
        this.containsHollywoodCelebrities = containsHollywoodCelebrities;
    }

    public boolean getContainsTVCelebrities() {
        return containsTVCelebrities;
    }

    public void setContainsTVCelebrities(boolean containsTVCelebrities) {
        this.containsTVCelebrities = containsTVCelebrities;
    }
}
