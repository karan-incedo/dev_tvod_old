package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Url;

/**
 * Created by viewlift on 5/9/17.
 */

public interface AppCMSPageAPIRest {
    @GET
    @Headers("Cache-Control: max-age=120")
    Call<AppCMSPageAPI> get(@Url String url, @HeaderMap Map<String, String> headers);
}
