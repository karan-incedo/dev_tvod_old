package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.beacon.AppCMSBeaconRequest;

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
    Call<Boolean> sendBeaconMessage(@Url String url, @HeaderMap Map<String, String> headers, @Body AppCMSBeaconRequest appCMSBeaconRequest);
}
