package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.subscriptions.AppCMSValidatePlaySubRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 2/22/18.
 */

public interface AppCMSValidatePlaySubRest {
    @POST
    Call<Response> validate(@Url String url,
                            @HeaderMap Map<String, String> authHeaders,
                            @Body AppCMSValidatePlaySubRequest appCMSValidatePlaySubRequest);
}
