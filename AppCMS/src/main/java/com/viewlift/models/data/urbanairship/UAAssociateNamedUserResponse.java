package com.viewlift.models.data.urbanairship;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 1/9/18.
 */

@UseStag
public class UAAssociateNamedUserResponse {
    @SerializedName("ok")
    @Expose
    boolean ok;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
