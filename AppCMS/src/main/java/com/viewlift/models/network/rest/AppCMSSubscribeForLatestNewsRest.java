package com.viewlift.models.network.rest;

import com.google.gson.JsonElement;
import com.viewlift.models.data.appcms.subscribeForLatestNewsPojo.SubscribeGoRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by artanelezaj on 4/1/18.
 */

public interface AppCMSSubscribeForLatestNewsRest {
    @POST
    Call<JsonElement> subscribe(@HeaderMap Map<String, String> authHeaders,
                                @Url String url,
                                @Body SubscribeGoRequest subscribeGoRequest);
}