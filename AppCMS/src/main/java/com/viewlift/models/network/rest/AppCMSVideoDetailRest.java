package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.api.AppCMSVideoDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by anas.azeem on 7/13/2017.
 * Owned by ViewLift, NYC
 */

public interface AppCMSVideoDetailRest {
    @GET
    Call<AppCMSVideoDetail> get(@Url String url);
}
