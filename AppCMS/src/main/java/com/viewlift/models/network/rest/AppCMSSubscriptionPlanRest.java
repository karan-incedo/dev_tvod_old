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
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface AppCMSSubscriptionPlanRest {
    @GET
    Call<List<AppCMSSubscriptionPlanResult>> getPlanList(@Url String url);

    @GET
    Call<AppCMSSubscriptionPlanResult> getSubscribedPlan(@Url String url);

    @POST
    Call<AppCMSSubscriptionPlanResult> createPlan(@Url String url, @Body SubscriptionRequest request);

    @PUT
    Call<AppCMSSubscriptionPlanResult> updatePlan(@Url String url, @Body SubscriptionRequest request);

    @PUT
    Call<AppCMSSubscriptionPlanResult> cancelPlan(@Url String url, @Body SubscriptionRequest request);
}
