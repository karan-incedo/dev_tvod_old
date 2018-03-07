package com.viewlift.models.data.urbanairship;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by viewlift on 1/9/18.
 */

@UseStag
public class UAAssociateNamedUserRequest {
    @SerializedName("channel_id")
    @Expose
    String channelId;

    @SerializedName("device_type")
    @Expose
    String deviceType;

    @SerializedName("named_user_id")
    @Expose
    String namedUserId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getNamedUserId() {
        return namedUserId;
    }

    public void setNamedUserId(String namedUserId) {
        this.namedUserId = namedUserId;
    }
}
