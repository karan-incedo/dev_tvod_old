
package com.viewlift.models.data.appcms.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrimaryCategory {

    @SerializedName("objectKey")
    @Expose
    private String objectKey;
    @SerializedName("id")
    @Expose
    private String id;

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
