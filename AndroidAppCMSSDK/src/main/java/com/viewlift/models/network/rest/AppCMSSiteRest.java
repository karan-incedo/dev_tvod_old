package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.sites.AppCMSSite;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by viewlift on 6/15/17.
 */

public interface AppCMSSiteRest {
    @GET
    Call<AppCMSSite> get(@Url String url);
}
