package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.watchlist.AppCMSWatchlistResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSWatchlistCall {

    private static final String TAG = "AppCMSWatchlistCallTAG_";
    private final AppCMSWatchlistRest appCMSWatchlistRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSWatchlistCall(AppCMSWatchlistRest appCMSWatchlistRest, Gson gson) {
        this.appCMSWatchlistRest = appCMSWatchlistRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, String authToken,String xApi,
                     final Action1<AppCMSWatchlistResult> watchlistResultAction1) throws IOException {
        try {
            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("Authorization", authToken);
            authTokenMap.put("x-api-key", xApi);
            System.out.println("====== "+authTokenMap.toString());
            System.out.println(url);
            appCMSWatchlistRest.get(url, authTokenMap).enqueue(new Callback<AppCMSWatchlistResult>() {
                @Override
                public void onResponse(@NonNull Call<AppCMSWatchlistResult> call,
                                       @NonNull Response<AppCMSWatchlistResult> response) {
                    Observable.just(response.body())
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(watchlistResultAction1);
                }

                @Override
                public void onFailure(@NonNull Call<AppCMSWatchlistResult> call,
                                      @NonNull Throwable t) {
                    //Log.e(TAG, "onFailure: " + t.getMessage());
                    watchlistResultAction1.call(null);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
    }
}
