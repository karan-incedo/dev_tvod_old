package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.ui.main.AppCMSMain;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Url;

/**
 * Created by viewlift on 5/4/17.
 */

public interface AppCMSMainUIRest {
    @GET
    @Headers("Cache-Control: max-age=0, no-cache, no-store")
    Call<AppCMSMain> get(@Url String url, @HeaderMap Map<String, String> headers);
}
