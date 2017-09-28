package com.viewlift.models.network.rest;

import com.google.gson.JsonElement;
import com.viewlift.models.data.appcms.subscriptions.AppCMSRestorePurchaseRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by viewlift on 9/27/17.
 */

public interface AppCMSRestorePurchaseRest {
    @POST
    Call<JsonElement> restorePurchase(@HeaderMap Map<String, String> authHeaders,
                                      @Url String url,
                                      @Body AppCMSRestorePurchaseRequest appCMSRestorePurchaseRequest);
}
