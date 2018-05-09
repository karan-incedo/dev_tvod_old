package com.viewlift.models.network.rest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Url;

/**
 * Created by viewlift on 5/4/17.
 */

public interface AppCMSFloodLightRest {

    @GET
    @Headers("Cache-Control: no-cache, no-store")
    Call<String> get(@Url String url, @HeaderMap Map<String, String> headers);
}
