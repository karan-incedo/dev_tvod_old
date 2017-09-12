package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.search.AppCMSSearchResult;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Url;

/**
 * Created by viewlift on 6/12/17.
 */

public interface AppCMSSearchRest {
    @GET
    Call<List<AppCMSSearchResult>> get(@HeaderMap Map<String, String> authHeaders, @Url String url);
}
