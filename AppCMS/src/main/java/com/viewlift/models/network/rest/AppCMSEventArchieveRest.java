package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.viewlift.models.data.appcms.api.AppCMSEventArchieveResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Url;

public interface AppCMSEventArchieveRest {
    @GET
    Call<AppCMSEventArchieveResult> get(@Url String url, @HeaderMap Map<String, String> headers);
}
