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
public class UAAudience {
    @SerializedName("named_user_id")
    @Expose
    List<String> uaNamedUserIds = new ArrayList<>();

    public List<String> getNamedUserIds() {
        return uaNamedUserIds;
    }

    public void addNamedUserIds(String userId) {
        if (uaNamedUserIds == null) {
            uaNamedUserIds = new ArrayList<>();
        }
        uaNamedUserIds.add(userId);
    }
}
