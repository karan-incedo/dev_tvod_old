package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/12/17.
 */

import com.viewlift.models.data.appcms.api.SubscriptionRequest;
import com.viewlift.models.data.appcms.subscriptions.AppCMSSubscriptionResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface AppCMSSubscriptionRest {
    @POST
    Call<AppCMSSubscriptionResult> request(@Url String url, @HeaderMap Map<String, String> headers,
                                           @Body SubscriptionRequest request);
}
