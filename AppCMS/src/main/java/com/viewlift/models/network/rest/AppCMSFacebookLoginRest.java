package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.ui.authentication.FacebookLoginRequest;
import com.viewlift.models.data.appcms.ui.authentication.FacebookLoginResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/6/17.
 */

public interface AppCMSFacebookLoginRest {
    @POST
    Call<FacebookLoginResponse> login(@Url String url, @Body FacebookLoginRequest request, @HeaderMap Map<String, String> headers);
}
