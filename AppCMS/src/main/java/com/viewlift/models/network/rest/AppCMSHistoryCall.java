package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/5/17.
 */

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.history.AppCMSHistoryResult;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSHistoryCall {
    private static final String TAG = "AppCMSHistoryCallTAG_";
    private final AppCMSHistoryRest appCMSHistoryRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSHistoryCall(AppCMSHistoryRest appCMSHistoryRest, Gson gson) {
        this.appCMSHistoryRest = appCMSHistoryRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, final Action1<AppCMSHistoryResult> historyResultAction1) throws IOException {
        try {
            appCMSHistoryRest.get(url).enqueue(new Callback<AppCMSHistoryResult>() {
                @Override
                public void onResponse(@NonNull Call<AppCMSHistoryResult> call, @NonNull Response<AppCMSHistoryResult> response) {
                    Observable.just(response.body()).subscribe(historyResultAction1);
                }

                @Override
                public void onFailure(@NonNull Call<AppCMSHistoryResult> call, @NonNull Throwable t) {
                    //
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to execute history " + url + ": " + e.toString());
        }
    }
}
