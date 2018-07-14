package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.AppCMSRosterResult;
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

public class AppCMSRosterCall {

    private static final String TAG = AppCMSRosterCall.class.getSimpleName() + "TAG";
    private final AppCMSRosterRest appCMSRosterRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSRosterCall(AppCMSRosterRest appCMSScheduleRest, Gson gson) {
        this.appCMSRosterRest = appCMSScheduleRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, String xApi,
                     final Action1<List<AppCMSRosterResult>> rosterResultAction) throws IOException {


        try {

            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("x-api-key", xApi);
            appCMSRosterRest.get(url, authTokenMap).enqueue(new Callback<List<AppCMSRosterResult>>() {
                @Override
                public void onResponse(Call<List<AppCMSRosterResult>> call, Response<List<AppCMSRosterResult>> response) {
                    Observable.just(response.body()).subscribe(rosterResultAction);
                }

                @Override
                public void onFailure(Call<List<AppCMSRosterResult>> call, Throwable t) {
                    rosterResultAction.call(null);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
    }
}
