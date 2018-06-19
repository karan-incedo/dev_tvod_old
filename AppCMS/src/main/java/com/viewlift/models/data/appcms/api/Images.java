package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Images implements Serializable {

    @SerializedName("_1x1")
    @Expose
    _1x1 _1x1;

    public com.viewlift.models.data.appcms.api._1x1 get_1x1() {
        return _1x1;
    }

    public void set_1x1(com.viewlift.models.data.appcms.api._1x1 _1x1) {
        this._1x1 = _1x1;
    }

    public com.viewlift.models.data.appcms.api._3x4 get_3x4() {
        return _3x4;
    }

    public void set_3x4(com.viewlift.models.data.appcms.api._3x4 _3x4) {
        this._3x4 = _3x4;
    }

    @SerializedName("_3x4")
    @Expose
    _3x4 _3x4;

}
