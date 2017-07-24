package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/24/17.
 */

import com.viewlift.models.data.appcms.ui.authentication.GoogleLoginRequest;
import com.viewlift.models.data.appcms.ui.authentication.GoogleLoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface AppCMSGoogleLoginRest {
    @POST
    Call<GoogleLoginResponse> login(@Url String url, @Body GoogleLoginRequest request);
}
