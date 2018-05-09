package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.ui.authentication.ForgotPasswordRequest;
import com.viewlift.models.data.appcms.ui.authentication.ForgotPasswordResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/6/17.
 */

public interface AppCMSResetPasswordRest {
    @POST
    Call<ForgotPasswordResponse> resetPassword(@Url String url, @Body ForgotPasswordRequest body, @HeaderMap Map<String, String> headers);
}
