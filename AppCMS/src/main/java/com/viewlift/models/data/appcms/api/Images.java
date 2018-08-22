package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

@UseStag
public class Images implements Serializable {

    @SerializedName("_1x1")
    @Expose
    Image_1x1 _1x1;

    public Image_1x1 get_1x1() {
        return _1x1;
    }

    public void set_1x1(Image_1x1 _1x1) {
        this._1x1 = _1x1;
    }

    public Image_3x4 get_3x4() {
        return _3x4;
    }

    public void set_3x4(Image_3x4 _3x4) {
        this._3x4 = _3x4;
    }

    @SerializedName("_3x4")
    @Expose
    Image_3x4 _3x4;

    @SerializedName("_16x9")
    @Expose
    Image_16x9 _16x9;

    public Image_16x9 get_16x9Image() {
        return _16x9Image;
    }

    public void set_16x9Image(Image_16x9 _16x9Image) {
        this._16x9Image = _16x9Image;
    }

    @SerializedName("_16x9Image")
    @Expose
    Image_16x9 _16x9Image;

}
