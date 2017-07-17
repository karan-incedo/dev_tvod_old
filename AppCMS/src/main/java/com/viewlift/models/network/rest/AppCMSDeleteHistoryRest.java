package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/17/17.
 */

import com.viewlift.models.data.appcms.api.DeleteHistoryRequest;
import com.viewlift.models.data.appcms.history.AppCMSDeleteHistoryResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface AppCMSDeleteHistoryRest {
    @POST
    Call<AppCMSDeleteHistoryResult> post(@Url String url, @HeaderMap Map<String, String> headers,
                                         @Body DeleteHistoryRequest request);

    @HTTP(method = "DELETE", hasBody = true)
    Call<AppCMSDeleteHistoryResult> removeSingle(@Url String url, @HeaderMap Map<String,
            String> headers, @Body DeleteHistoryRequest request);
}
