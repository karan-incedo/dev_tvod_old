package com.viewlift.models.data.urbanairship;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by viewlift on 12/21/17.
 */

@UseStag
public class UANamedUserRequest {
    @SerializedName("audience")
    @Expose
    UAAudience uaAudience;

    @SerializedName("add")
    @Expose
    Map<String, List<String>> uaAdd;

    @SerializedName("remove")
    @Expose
    Map<String, List<String>> uaRemove;

    public UAAudience getUaAudience() {
        return uaAudience;
    }

    public void setUaAudience(UAAudience uaAudience) {
        this.uaAudience = uaAudience;
    }

    public Map<String, List<String>> getUaAdd() {
        return uaAdd;
    }

    public void setUaAdd(Map<String, List<String>> uaAdd) {
        this.uaAdd = uaAdd;
    }

    public Map<String, List<String>> getUaRemove() {
        return uaRemove;
    }

    public void setUaRemove(Map<String, List<String>> uaRemove) {
        this.uaRemove = uaRemove;
    }
}
