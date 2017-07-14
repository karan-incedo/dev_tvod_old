package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.api.AppCMSStreamingInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by viewlift on 6/26/17.
 */

public interface AppCMSStreamingInfoRest {
    @GET
    Call<AppCMSStreamingInfo> get(@Url String url);
}
