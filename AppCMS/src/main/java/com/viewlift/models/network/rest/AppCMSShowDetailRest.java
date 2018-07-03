package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.api.AppCMSShowDetail;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Url;

/**
 * Created by anasazeem on 15/06/18.
 */

public interface AppCMSShowDetailRest {
    @GET
    Call<AppCMSShowDetail> get(@Url String url, @HeaderMap Map<String, String> authHeaders);
}
