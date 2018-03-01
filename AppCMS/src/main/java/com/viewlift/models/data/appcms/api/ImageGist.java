package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class ImageGist implements Serializable {

    @SerializedName("_32x9")
    @Expose
    String _32x9;

    @SerializedName("_16x9")
    @Expose
    String _16x9;

    @SerializedName("_4x3")
    @Expose
    String _4x3;

    @SerializedName("_3x4")
    @Expose
    String _3x4;

    @SerializedName("_1x1")
    @Expose
    String _1x1;

    public String get_32x9() {
        return _32x9;
    }

    public void set_32x9(String _32x9) {
        this._32x9 = _32x9;
    }

    public String get_16x9() {
        return _16x9;
    }

    public void set_16x9(String _16x9) {
        this._16x9 = _16x9;
    }

    public String get_4x3() {
        return _4x3;
    }

    public void set_4x3(String _4x3) {
        this._4x3 = _4x3;
    }

    public String get_3x4() {
        return _3x4;
    }

    public void set_3x4(String _3x4) {
        this._3x4 = _3x4;
    }

    public String get_1x1() {
        return _1x1;
    }

    public void set_1x1(String _1x1) {
        this._1x1 = _1x1;
    }
}
