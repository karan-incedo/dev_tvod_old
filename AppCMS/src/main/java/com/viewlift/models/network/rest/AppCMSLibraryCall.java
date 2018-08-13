package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.AppCMSLibraryResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSLibraryCall {

    private static final String TAG = AppCMSLibraryCall.class.getSimpleName() + "TAG";
    private final AppCMSLibraryRest appCMSLibraryRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSLibraryCall(AppCMSLibraryRest appCMSLibraryRest, Gson gson) {
        this.appCMSLibraryRest = appCMSLibraryRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, String AuthToken, String xApi,
                     final Action1<AppCMSLibraryResult> rosterResultAction) throws IOException {


        try {

            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("Authorization", AuthToken);

            authTokenMap.put("x-api-key", xApi);
            appCMSLibraryRest.get(url, authTokenMap).enqueue(new Callback<AppCMSLibraryResult>() {
                @Override
                public void onResponse(Call<AppCMSLibraryResult> call, Response<AppCMSLibraryResult> response) {
                    Observable.just(response.body()).subscribe(rosterResultAction);
                }

                @Override
                public void onFailure(Call<AppCMSLibraryResult> call, Throwable t) {
                    rosterResultAction.call(null);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
    }
}
