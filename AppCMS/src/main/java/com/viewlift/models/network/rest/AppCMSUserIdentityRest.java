package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.ui.authentication.UserIdentity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/6/17.
 */

public interface AppCMSUserIdentityRest {
    @GET
    Call<UserIdentity> get(@Url String url, @HeaderMap Map<String, String> authHeaders);
    @POST
    Call<UserIdentity> post(@Url String url, @HeaderMap Map<String, String> authHeaders, @Body UserIdentity userIdentity);

}
