package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.history.UserVideoStatusResponse;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 7/7/17.
 */

public class AppCMSUserVideoStatusCall {
    private final AppCMSUserVideoStatusRest appCMSUserVideoStatusRest;
    private Map<String, String> authHeaders;

    @Inject
    public AppCMSUserVideoStatusCall(AppCMSUserVideoStatusRest appCMSUserVideoStatusRest) {
        this.appCMSUserVideoStatusRest = appCMSUserVideoStatusRest;
        this.authHeaders = new HashMap<>();
    }

    public void call(String url,
                     String authToken,
                     final Action1<UserVideoStatusResponse> readyAction1) {
        authHeaders.put("Authorization", authToken);
        appCMSUserVideoStatusRest.get(url, authHeaders).enqueue(new Callback<UserVideoStatusResponse>() {
            @Override
            public void onResponse(Call<UserVideoStatusResponse> call, Response<UserVideoStatusResponse> response) {
                Observable.just(response.body()).subscribe(readyAction1);
            }

            @Override
            public void onFailure(Call<UserVideoStatusResponse> call, Throwable t) {
                Observable.just((UserVideoStatusResponse) null).subscribe(readyAction1);
            }
        });
    }
}
