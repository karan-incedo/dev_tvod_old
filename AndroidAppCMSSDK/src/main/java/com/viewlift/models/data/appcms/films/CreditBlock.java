
package com.viewlift.models.data.appcms.films;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreditBlock {

    @SerializedName("id")
    @Expose
    private Object id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("credits")
    @Expose
    private Object credits;
    @SerializedName("containsHollywoodCelebrities")
    @Expose
    private Boolean containsHollywoodCelebrities;
    @SerializedName("containsTVCelebrities")
    @Expose
    private Boolean containsTVCelebrities;

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

    public Object getCredits() {
        return credits;
    }

    public void setCredits(Object credits) {
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

}
