package com.viewlift.models.data.appcms.beacon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;

/**
 * Created by sandeep.singh on 8/21/2017.
 */

@UseStag
public class AppCMSBeaconRequest {

    @Expose
    ArrayList<BeaconRequest> beaconRequest;

    public ArrayList<BeaconRequest> getBeaconRequest() {
        return beaconRequest;
    }

    public void setBeaconRequest(ArrayList<BeaconRequest> beaconRequest) {
        this.beaconRequest = beaconRequest;
    }

}
