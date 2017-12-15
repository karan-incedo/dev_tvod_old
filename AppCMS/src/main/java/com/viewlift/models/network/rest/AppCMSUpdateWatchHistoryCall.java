package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.history.UpdateHistoryRequest;

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

public class AppCMSUpdateWatchHistoryCall {
    private final AppCMSUpdateWatchHistoryRest appCMSUpdateWatchHistoryRest;
    private Map<String, String> authHeaders;

    @Inject
    public AppCMSUpdateWatchHistoryCall(AppCMSUpdateWatchHistoryRest appCMSUpdateWatchHistoryRest) {
        this.appCMSUpdateWatchHistoryRest = appCMSUpdateWatchHistoryRest;
        this.authHeaders = new HashMap<>();
    }

    public void call(String url,
                     String authToken,
                     UpdateHistoryRequest updateHistoryRequest,
                     final Action1<String> readyAction) {
        authHeaders.put("Authorization", authToken);
        appCMSUpdateWatchHistoryRest.post(url,
                authHeaders,
                updateHistoryRequest)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Observable.just(response.toString())
                                .onErrorResumeNext(throwable -> Observable.empty())
                                .subscribe(readyAction);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Observable.just((String) null)
                                .onErrorResumeNext(throwable -> Observable.empty())
                                .subscribe(readyAction);
                    }
                });
    }
}
