package com.viewlift.models.data.appcms.beacon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;

/**
 * Created by sandeep.singh on 9/1/2017.
 */

@UseStag
public class BeaconResponse {

    @SerializedName("FailedPutCount")
    @Expose
    public int FailedPutCount;

    @SerializedName("RequestResponses")
    @Expose
    public ArrayList<BeaconRequestResponse> beaconRequestResponse;


}
