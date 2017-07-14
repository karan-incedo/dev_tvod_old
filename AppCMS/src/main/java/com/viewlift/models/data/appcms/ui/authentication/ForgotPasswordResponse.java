package com.viewlift.models.data.appcms.ui.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 7/6/17.
 */

@UseStag
public class ForgotPasswordResponse {

    @SerializedName("emailSent")
    @Expose
    boolean emailSent;

    @SerializedName("error")
    @Expose
    String error;

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
