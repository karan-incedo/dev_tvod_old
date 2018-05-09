package com.viewlift.models.network.rest;

import com.google.gson.JsonElement;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Url;

/**
 * Created by viewlift on 10/3/17.
 */

public interface AppCMSAndroidModuleRest {
    @GET
    Call<JsonElement> get(@Url String url, @HeaderMap Map<String, String> headers);
}
