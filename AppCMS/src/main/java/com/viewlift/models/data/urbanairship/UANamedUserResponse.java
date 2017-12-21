package com.viewlift.models.data.urbanairship;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viewlift on 12/21/17.
 */

@UseStag
public class UANamedUserResponse {
    @SerializedName("ok")
    @Expose
    boolean ok;

    @SerializedName("warnings")
    @Expose
    List<String> warnings = new ArrayList<>();

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
