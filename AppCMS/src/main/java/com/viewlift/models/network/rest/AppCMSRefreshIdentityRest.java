package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.ui.authentication.RefreshIdentityResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/5/17.
 */

public interface AppCMSRefreshIdentityRest {
    @GET
    Call<RefreshIdentityResponse> get(@Url String url);
}
