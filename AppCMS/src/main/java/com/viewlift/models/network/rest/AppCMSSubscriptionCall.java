package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/12/17.
 */

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.SubscriptionRequest;
import com.viewlift.models.data.appcms.subscriptions.AppCMSSubscriptionResult;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSSubscriptionCall {
    private static final String TAG = "AppCMSSubscriptionCall";
    private final AppCMSSubscriptionRest appCMSSubscriptionRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSSubscriptionCall(AppCMSSubscriptionRest appCMSSubscriptionRest, Gson gson) {
        this.appCMSSubscriptionRest = appCMSSubscriptionRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, String authToken,
                     final Action1<AppCMSSubscriptionResult> subscriptionResultAction1,
                     SubscriptionRequest request) throws Exception {
        try {
            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("Authorization", authToken);
            appCMSSubscriptionRest.request(url, authTokenMap, request).enqueue(
                    new Callback<AppCMSSubscriptionResult>() {
                        @Override
                        public void onResponse(@NonNull Call<AppCMSSubscriptionResult> call,
                                               @NonNull Response<AppCMSSubscriptionResult> response) {
                            Observable.just(response.body())
                                    .onErrorResumeNext(throwable -> Observable.empty())
                                    .subscribe(subscriptionResultAction1);
                        }

                        @Override
                        public void onFailure(@NonNull Call<AppCMSSubscriptionResult> call,
                                              @NonNull Throwable t) {
                            //Log.e(TAG, "onFailure: " + t.getMessage());
                        }
                    });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute subscription " + url + ": " + e.toString());
        }
    }
}
