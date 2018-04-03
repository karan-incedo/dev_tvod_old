package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.api.GetLinkCode;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/5/17.
 */

public interface AppCMSDeviceCodeRest {
    @GET
    Call<GetLinkCode> getSyncCode(@Url String url, @HeaderMap Map<String, String> headers);
}
