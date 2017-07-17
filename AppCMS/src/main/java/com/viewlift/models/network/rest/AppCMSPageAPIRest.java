package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by viewlift on 5/9/17.
 */

public interface AppCMSPageAPIRest {
    @GET
    Call<AppCMSPageAPI> get(@Url String url);
}
