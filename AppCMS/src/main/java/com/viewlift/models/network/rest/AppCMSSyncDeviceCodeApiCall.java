package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.SyncDeviceCode;

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

public class AppCMSSyncDeviceCodeApiCall {

    private static final String TAG = "AppCMSGetSyncCodeApiCall_";
    private final AppCMSSyncDeviceCodeRest appCMSSyncDeviceCodeRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;
    private final String userAgent;

    @Inject
    public AppCMSSyncDeviceCodeApiCall(AppCMSSyncDeviceCodeRest appCMSSyncDeviceCodeRest, Gson gson) {
        this.appCMSSyncDeviceCodeRest = appCMSSyncDeviceCodeRest;
        this.gson = gson;
        this.userAgent = System.getProperty("http.agent");
    }

    @WorkerThread
    public void call(String url, String authToken, String xApiKey, boolean deSyncDevice,
                     final Action1<SyncDeviceCode> getSyncCodeAction1) throws IOException {
        try {
            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("Authorization", authToken);
            authTokenMap.put("Content-Type", "application/json");
            authTokenMap.put("user-agent", userAgent);
            authTokenMap.put("x-api-key", xApiKey);

            if (deSyncDevice) {
                appCMSSyncDeviceCodeRest.deSyncDevice(url,authTokenMap).enqueue(new Callback<SyncDeviceCode>() {
                    @Override
                    public void onResponse(Call<SyncDeviceCode> call, Response<SyncDeviceCode> response) {
                        getSyncCodeAction1.call(response.body());
                    }

                    @Override
                    public void onFailure(Call<SyncDeviceCode> call, Throwable t) {

                    }
                });
            } else{
                appCMSSyncDeviceCodeRest.syncDeviceCode(url, authTokenMap).enqueue(new Callback<SyncDeviceCode>() {
                    @Override
                    public void onResponse(Call<SyncDeviceCode> call, Response<SyncDeviceCode> response) {
                        getSyncCodeAction1.call(response.body());
                    }

                    @Override
                    public void onFailure(Call<SyncDeviceCode> call, Throwable t) {
                    }
                });
               }
            } catch(Exception e){
                Observable.just((SyncDeviceCode) null)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(getSyncCodeAction1);
            }
    }
}

