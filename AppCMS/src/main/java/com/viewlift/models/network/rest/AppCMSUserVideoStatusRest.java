package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.history.UserVideoStatusResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.Url;

/**
 * Created by viewlift on 7/7/17.
 */

public interface AppCMSUserVideoStatusRest {
    Call<UserVideoStatusResponse> get(@Url String url, @HeaderMap Map<String, String> authHeaders);
}
