package com.viewlift.models.network.rest;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 8/18/17.
 */

public interface AppCMSCCAvenueRest {
    @POST
    Call<String> getRsaKey(@Url String url, @HeaderMap Map<String, String> headers, @Body Map<String, Object> body);
}
