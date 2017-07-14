package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.ui.authentication.FacebookLoginResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/6/17.
 */

public interface AppCMSFacebookLoginRest {
    @GET
    Call<FacebookLoginResponse> login(@Url String url);
}
