package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/19/17.
 */

import com.viewlift.models.data.appcms.api.SubscriptionRequest;
import com.viewlift.models.data.appcms.subscriptions.AppCMSSubscriptionPlanResult;
import com.viewlift.models.data.appcms.subscriptions.AppCMSUserSubscriptionPlanResult;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface AppCMSSubscriptionPlanRest {
    @GET
    Call<List<AppCMSSubscriptionPlanResult>> getPlanList(@Url String url, @HeaderMap Map<String, String> authHeaders);

    @GET
    Call<AppCMSUserSubscriptionPlanResult> getSubscribedPlan(@Url String url, @HeaderMap Map<String, String> authHeaders);

    @POST
    Call<AppCMSSubscriptionPlanResult> createPlan(@Url String url, @HeaderMap Map<String, String> authHeaders, @Body SubscriptionRequest request);

    @PUT
    Call<AppCMSSubscriptionPlanResult> updatePlan(@Url String url, @HeaderMap Map<String, String> authHeaders, @Body SubscriptionRequest request);

    @PUT
    Call<AppCMSSubscriptionPlanResult> cancelPlan(@Url String url, @HeaderMap Map<String, String> authHeaders, @Body SubscriptionRequest request);
}
