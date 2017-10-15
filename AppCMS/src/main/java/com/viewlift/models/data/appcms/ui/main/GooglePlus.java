package com.viewlift.models.data.appcms.ui.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 8/11/17.
 */

@UseStag
public class GooglePlus {
    @SerializedName("signin")
    @Expose
    boolean signin;

    public boolean isSignin() {
        return signin;
    }

    public void setSignin(boolean signin) {
        this.signin = signin;
    }
}
