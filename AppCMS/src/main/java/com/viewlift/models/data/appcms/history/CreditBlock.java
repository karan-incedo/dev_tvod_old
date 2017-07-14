package com.viewlift.models.data.appcms.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreditBlock {

    @SerializedName("credits")
    @Expose
    private List<Credit> credits = null;

    @SerializedName("containsHollywoodCelebrities")
    @Expose
    private Boolean containsHollywoodCelebrities;

    @SerializedName("containsTVCelebrities")
    @Expose
    private Boolean containsTVCelebrities;

    @SerializedName("title")
    @Expose
    private String title;

    public List<Credit> getCredits() {
        return credits;
    }

    public void setCredits(List<Credit> credits) {
        this.credits = credits;
    }

    public Boolean getContainsHollywoodCelebrities() {
        return containsHollywoodCelebrities;
    }

    public void setContainsHollywoodCelebrities(Boolean containsHollywoodCelebrities) {
        this.containsHollywoodCelebrities = containsHollywoodCelebrities;
    }

    public Boolean getContainsTVCelebrities() {
        return containsTVCelebrities;
    }

    public void setContainsTVCelebrities(Boolean containsTVCelebrities) {
        this.containsTVCelebrities = containsTVCelebrities;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
