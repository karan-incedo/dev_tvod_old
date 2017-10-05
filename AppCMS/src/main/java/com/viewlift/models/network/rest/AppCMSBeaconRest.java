package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.beacon.BeaconRequest;
import com.viewlift.models.data.appcms.beacon.BeaconResponse;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 6/21/17.
 */

public interface AppCMSBeaconRest {
    @POST
    Call<Void> sendBeaconMessage(@Url String url);

    @POST
    Call<BeaconResponse> sendBeaconMessage(@Url String url, @HeaderMap Map<String, String> headers, @Body ArrayList<BeaconRequest> appCMSBeaconRequest);
}
