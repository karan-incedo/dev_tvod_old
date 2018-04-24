package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/20/17.
 */

import com.viewlift.models.data.appcms.ui.authentication.AnonymousAuthTokenResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Url;

public interface AppCMSAnonymousAuthTokenRest {
    @GET
    Call<AnonymousAuthTokenResponse> get(@Url String url, @HeaderMap Map<String, String> headers);
}
