package com.viewlift.models.data.appcms.beacon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

/**
 * Created by sandeep.singh on 9/1/2017.
 */

@UseStag
public class BeaconRequestResponse {

    @SerializedName("RecordId")
    @Expose
    public String recordId;
}
