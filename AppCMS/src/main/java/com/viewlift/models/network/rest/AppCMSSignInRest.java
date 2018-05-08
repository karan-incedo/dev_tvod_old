package com.viewlift.models.network.rest;

import com.google.gson.JsonElement;
import com.viewlift.models.data.appcms.ui.authentication.SignInRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/5/17.
 */

public interface AppCMSSignInRest {
    @POST
    Call<JsonElement> signin(@Url String url, @Body SignInRequest body, @HeaderMap Map<String, String> headers);
}
