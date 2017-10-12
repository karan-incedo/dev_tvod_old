package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.api.AppCMSSignedURLResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Url;

/**
 * Created by viewlift on 10/10/17.
 */

public interface AppCMSSignedURLRest {
    @GET
    Call<AppCMSSignedURLResult> get(@Url String url,
                                    @HeaderMap Map<String, String> authHeaders);
}
