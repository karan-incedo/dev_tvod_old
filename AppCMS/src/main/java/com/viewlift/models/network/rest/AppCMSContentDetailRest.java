package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.api.AppCMSContentDetail;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Url;

/**
 * Created by anas.azeem on 3/12/2018.
 * Owned by ViewLift, NYC
 */

public interface AppCMSContentDetailRest {
    @GET
    Call<AppCMSContentDetail> get(@Url String url, @HeaderMap Map<String, String> authHeaders);
}