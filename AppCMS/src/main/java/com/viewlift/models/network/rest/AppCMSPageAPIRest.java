package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Url;

/**
 * Created by viewlift on 5/9/17.
 */

public interface AppCMSPageAPIRest {
    @GET
    @Headers("Cache-Control: no-cache, no-store")
    Call<AppCMSPageAPI> get(@Header("x-api-key") String apiKey, @Url String url);
}
