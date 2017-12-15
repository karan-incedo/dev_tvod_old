package com.viewlift.models.network.rest;

import android.support.annotation.NonNull;

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

    public void call(String url, String authToken,
                     final Action1<UserVideoStatusResponse> readyAction1) {
        authHeaders.put("Authorization", authToken);
        appCMSUserVideoStatusRest.get(url, authHeaders).enqueue(new Callback<UserVideoStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserVideoStatusResponse> call,
                                   @NonNull Response<UserVideoStatusResponse> response) {
                Observable.just(response.body())
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(readyAction1);
            }

            @Override
            public void onFailure(@NonNull Call<UserVideoStatusResponse> call, @NonNull Throwable t) {
                Observable.just((UserVideoStatusResponse) null)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(readyAction1);
            }
        });
    }
}
