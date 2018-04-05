package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.api.SyncDeviceCode;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/5/17.
 */

public interface AppCMSSyncDeviceCodeRest {
    @GET
    Call<SyncDeviceCode> syncDeviceCode(@Url String url, @HeaderMap Map<String, String> headers);

    @DELETE
    Call<SyncDeviceCode> deSyncDevice(@Url String url, @HeaderMap Map<String, String> headers);
}
