package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by viewlift on 5/4/17.
 */

public interface AppCMSPageUIRest {
    @GET
    Call<AppCMSPageUI> get(@Url String url);
}
