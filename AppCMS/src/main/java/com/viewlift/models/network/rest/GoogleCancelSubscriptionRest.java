package com.viewlift.models.network.rest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/25/17.
 */

public interface GoogleCancelSubscriptionRest {
    @POST
    Call<Void> sendCancelSubscription(@Url String url, @HeaderMap Map<String, String> headers);
}
