package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.beacon.AppCMSBeaconRequest;
import com.viewlift.models.data.appcms.watchlist.AppCMSAddToWatchlistResult;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by sandeep.singh on 8/22/2017.
 */

public class AppCMSBeaconCall {

    private static final String TAG="AppCMSBeaconCall";
    private final AppCMSBeaconRest appCMSBeaconRest;



    @Inject
    public AppCMSBeaconCall(AppCMSBeaconRest appCMSBeaconRest){

        this.appCMSBeaconRest =appCMSBeaconRest;
    }

    @WorkerThread
    public void call(String url, final Action1<Boolean> action1, AppCMSBeaconRequest request){

        try {
            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("Content-Type", "application/json");
            Call<Boolean> call;

            call = appCMSBeaconRest.sendBeaconMessage(url, authTokenMap, request);

            call.enqueue(new Callback<Boolean>() {

                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    Observable.just(response.body()).subscribe(action1);
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}