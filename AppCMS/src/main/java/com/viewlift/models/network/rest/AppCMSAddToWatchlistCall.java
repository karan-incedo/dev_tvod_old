package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/10/17.
 */

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.AddToWatchlistRequest;
import com.viewlift.models.data.appcms.watchlist.AppCMSAddToWatchlistResult;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSAddToWatchlistCall {

    private static final String TAG = "AppCMSAddToWatchlistTAG";
    private final AppCMSAddToWatchlistRest appCMSAddToWatchlistRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSAddToWatchlistCall(AppCMSAddToWatchlistRest appCMSAddToWatchlistRest, Gson gson) {
        this.appCMSAddToWatchlistRest = appCMSAddToWatchlistRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, String authToken,String xApi,
                     final Action1<AppCMSAddToWatchlistResult> addToWatchlistResultAction1,
                     AddToWatchlistRequest request, boolean add) throws Exception {
        try {
            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("Authorization", authToken);
            authTokenMap.put("x-api-key", xApi);
            Call<AppCMSAddToWatchlistResult> call;
            if (add) {
                call = appCMSAddToWatchlistRest.add(url, authTokenMap, request);
            } else {
                call = appCMSAddToWatchlistRest.removeSingle(url, authTokenMap, request);
            }

            call.enqueue(new Callback<AppCMSAddToWatchlistResult>() {
                @Override
                public void onResponse(@NonNull Call<AppCMSAddToWatchlistResult> call,
                                       @NonNull Response<AppCMSAddToWatchlistResult> response) {
                    Log.d(TAG, "API Response Code: " + response.code());
                    if (response.body() == null) {
                        addToWatchlistResultAction1.call(null);
                    } else {
                        Observable.just(response.body())
                                .onErrorResumeNext(throwable -> Observable.empty())
                                .subscribe(addToWatchlistResultAction1);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AppCMSAddToWatchlistResult> call,
                                      @NonNull Throwable t) {
//                    Log.e(TAG, "onFailure: " + t.getMessage());
                    addToWatchlistResultAction1.call(null);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute add to watchlist " + url + ": " + e.toString());
        }
    }
}
