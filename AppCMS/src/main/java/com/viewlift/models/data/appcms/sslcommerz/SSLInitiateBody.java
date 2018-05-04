package com.viewlift.models.data.appcms.sslcommerz;

import com.google.gson.annotations.SerializedName;

public class SSLInitiateBody {
    @SerializedName("planId")
    String planId;
    @SerializedName("tran_id")
    String tran_id;
    @SerializedName("phone")
    String phone;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getTran_id() {
        return tran_id;
    }

    public void setTran_id(String tran_id) {
        this.tran_id = tran_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
