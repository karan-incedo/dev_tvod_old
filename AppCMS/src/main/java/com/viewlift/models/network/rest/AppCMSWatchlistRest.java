package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.viewlift.models.data.appcms.watchlist.AppCMSWatchlistResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface AppCMSWatchlistRest {
    @GET
    Call<List<AppCMSWatchlistResult>> get(@Url String url);
}
