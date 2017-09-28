package com.viewlift.models.data.appcms.ui.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 9/28/17.
 */

@UseStag
public class SigninError {
    @SerializedName("message")
    @Expose
    String message;

    @SerializedName("code")
    @Expose
    String code;

    @SerializedName("time")
    @Expose
    String time;

    @SerializedName("requestId")
    @Expose
    String requestId;

    @SerializedName("statusCode")
    @Expose
    int statusCode;

    @SerializedName("retryable")
    @Expose
    boolean retryable;

    @SerializedName("retryDelay")
    @Expose
    double retryDelay;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    public double getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(double retryDelay) {
        this.retryDelay = retryDelay;
    }
}
