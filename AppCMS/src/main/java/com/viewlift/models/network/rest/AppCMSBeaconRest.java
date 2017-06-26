package com.viewlift.models.network.rest;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 6/21/17.
 */

public interface AppCMSBeaconRest {
    @POST
    Call<Void> sendBeaconMessage(@Url String url);
}
