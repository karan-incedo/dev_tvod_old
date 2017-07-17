package com.viewlift.models.data.appcms.subscriptions;

/*
 * Created by Viewlift on 7/12/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Authentication {

    @SerializedName("accessKey")
    @Expose
    String accessKey;

    @SerializedName("signature")
    @Expose
    String signature;

    @SerializedName("timestamp")
    @Expose
    String timestamp;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
