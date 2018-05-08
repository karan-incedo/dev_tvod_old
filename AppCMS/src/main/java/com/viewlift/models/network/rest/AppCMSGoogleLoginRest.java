package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/24/17.
 */

import com.viewlift.models.data.appcms.ui.authentication.GoogleLoginRequest;
import com.viewlift.models.data.appcms.ui.authentication.GoogleLoginResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface AppCMSGoogleLoginRest {
    @POST
    Call<GoogleLoginResponse> login(@Url String url, @HeaderMap Map<String, String> headers, @Body GoogleLoginRequest request);
}
