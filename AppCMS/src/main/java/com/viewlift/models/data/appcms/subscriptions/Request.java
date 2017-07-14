package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Request {

    @SerializedName("request")
    @Expose
    private Request_ request;

    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("success")
    @Expose
    private Boolean success;

    public Request_ getRequest() {
        return request;
    }

    public void setRequest(Request_ request) {
        this.request = request;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
