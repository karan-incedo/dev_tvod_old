package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/19/17.
 */

import com.viewlift.models.data.appcms.api.SubscriptionRequest;
import com.viewlift.models.data.appcms.subscriptions.AppCMSSubscriptionPlanResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface AppCMSSubscriptionPlanRest {
    @GET
    Call<List<AppCMSSubscriptionPlanResult>> getList(@Url String url);

    @POST
    Call<AppCMSSubscriptionPlanResult> create(@Url String url, @Body SubscriptionRequest request);

    // TODO: 7/19/17 @PUT - update(@Url String url, ...);
}
