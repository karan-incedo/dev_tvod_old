package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.GetLinkCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 7/5/17.
 */

public class AppCMSDeviceCodeApiCall {

    private static final String TAG = "AppCMSGetSyncCodeApiCall_";
    private final AppCMSDeviceCodeRest appCMSGetSyncCodeRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;
    private final String userAgent;

    @Inject
    public AppCMSDeviceCodeApiCall(AppCMSDeviceCodeRest appCMSGetSyncCodeRest, Gson gson) {
        this.appCMSGetSyncCodeRest = appCMSGetSyncCodeRest;
        this.gson = gson;
        this.userAgent = System.getProperty("http.agent");
    }

    @WorkerThread
    public void call(String url, String authToken, String xApiKey,
                     final Action1<GetLinkCode> getSyncCodeAction1) throws IOException {
        try {
            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("Authorization", authToken);
            authTokenMap.put("x-api-key", xApiKey);
            authTokenMap.put("Content-Type", "application/json");
            authTokenMap.put("user-agent", userAgent);

            appCMSGetSyncCodeRest.getSyncCode(url,authTokenMap).enqueue(new Callback<GetLinkCode>() {
                @Override
                public void onResponse(Call<GetLinkCode> call, Response<GetLinkCode> response) {
                    getSyncCodeAction1.call(response.body());
                }

                @Override
                public void onFailure(Call<GetLinkCode> call, Throwable t) {
                    //getSyncCodeAction1.call(call);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute history " + url + ": " + e.toString());
            Observable.just((GetLinkCode) null)
                    .onErrorResumeNext(throwable -> Observable.empty())
                    .subscribe(getSyncCodeAction1);
        }
    }
}

