package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.AppCMSScheduleResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSScheduleCall {

    private static final String TAG = AppCMSScheduleCall.class.getSimpleName() + "TAG";
    private final AppCMSScheduleRest appCMSScheduleRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSScheduleCall(AppCMSScheduleRest appCMSScheduleRest, Gson gson) {
        this.appCMSScheduleRest = appCMSScheduleRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, String xApi,
                     final Action1<List<AppCMSScheduleResult>> scheduleResultAction) throws IOException {


        try {

            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("x-api-key", xApi);
            appCMSScheduleRest.get(url, authTokenMap).enqueue(new Callback<List<AppCMSScheduleResult>>() {
                @Override
                public void onResponse(Call<List<AppCMSScheduleResult>> call, Response<List<AppCMSScheduleResult>> response) {
                    Observable.just(response.body()).subscribe(scheduleResultAction);
                }

                @Override
                public void onFailure(Call<List<AppCMSScheduleResult>> call, Throwable t) {
                    scheduleResultAction.call(null);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
    }
}
