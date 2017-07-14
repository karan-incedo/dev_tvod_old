package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidUI;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

/**
 * Created by viewlift on 5/4/17.
 */

public interface AppCMSAndroidUIRest {
    @GET
    @Headers("Cache-Control: no-cache, no-store")
    Call<AppCMSAndroidUI> get(@Url String url);
}
