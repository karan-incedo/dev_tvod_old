package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;

/**
 * Created by anas.azeem on 6/13/2018.
 * Owned by ViewLift, NYC
 */

@UseStag
public class Images implements Serializable {

    @SerializedName("_16x9")
    @Expose
    _16x9 _16x9;
}
