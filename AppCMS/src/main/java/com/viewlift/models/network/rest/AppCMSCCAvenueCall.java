package com.viewlift.models.network.rest;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;
import rx.Observable;

/**
 * Created by viewlift on 8/18/17.
 */

public class AppCMSCCAvenueCall {
    private final AppCMSCCAvenueRest appCMSCCAvenueRest;

    private Map<String, String> headers;

    @Inject
    public AppCMSCCAvenueCall(AppCMSCCAvenueRest appCMSCCAvenueRest) {
        this.appCMSCCAvenueRest = appCMSCCAvenueRest;
        this.headers = new HashMap<>();
    }

    public void call(String url,
                     String authToken,
                     String apiKey,
                     Map<String, Object> body,
                     boolean useAuthHeaders,
                     Action1<String> rasReadyAction) {
        if (useAuthHeaders) {
            headers.put("Authorization", authToken);
            headers.put("x-api-token", apiKey);
        }

        appCMSCCAvenueRest.getRsaKey(url, headers, body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.body() != null) {
                    Observable.just(response.body())
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(rasReadyAction);
                } else {
                    Observable.just((String) null)
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(rasReadyAction);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Observable.just((String) null)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(rasReadyAction);
            }
        });
    }
}
