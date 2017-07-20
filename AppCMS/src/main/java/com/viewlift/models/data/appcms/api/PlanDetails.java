
package com.viewlift.models.data.appcms.api;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlanDetails {

    @SerializedName("planDetails")
    @Expose
    private List<PlanDetail> planDetails = null;

    public List<PlanDetail> getPlanDetails() {
        return planDetails;
    }

    public void setPlanDetails(List<PlanDetail> planDetails) {
        this.planDetails = planDetails;
    }

}
