package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.history.UpdateHistoryRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/7/17.
 */

public interface AppCMSUpdateWatchHistoryRest {
    @POST
    Call<Void> post(@Url String url, @HeaderMap Map<String, String> authHeaders, @Body UpdateHistoryRequest updateHistoryRequest);
}
