package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/5/17.
 */

import com.viewlift.models.data.appcms.history.AppCMSHistoryResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface AppCMSHistoryRest {
    @GET
    Call<AppCMSHistoryResult> get(@Url String url);
}
