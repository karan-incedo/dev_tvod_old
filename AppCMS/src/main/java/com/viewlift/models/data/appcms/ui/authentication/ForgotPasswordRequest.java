package com.viewlift.models.data.appcms.ui.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 7/6/17.
 */

@UseStag
public class ForgotPasswordRequest {

    @SerializedName("email")
    @Expose
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
