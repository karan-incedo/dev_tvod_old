package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.AppCMSEventArchieveResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSEventArchieveCall {

    private static final String TAG = AppCMSEventArchieveCall.class.getSimpleName() + "TAG";
    private final AppCMSEventArchieveRest appCMSEventArchieveRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSEventArchieveCall(AppCMSEventArchieveRest appCMSeventArchRest, Gson gson) {
        this.appCMSEventArchieveRest = appCMSeventArchRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, String eventId,
                     final Action1<AppCMSEventArchieveResult> eventArchieveResultAction) throws IOException {
        try {

            Map<String, String> authTokenMap = new HashMap<>();
//            authTokenMap.put("apikey", xApiKey);

            appCMSEventArchieveRest.get(url, authTokenMap).enqueue(new Callback<AppCMSEventArchieveResult>() {
                @Override
                public void onResponse(@NonNull Call<AppCMSEventArchieveResult> call,
                                       @NonNull Response<AppCMSEventArchieveResult> response) {
                    Observable.just(response.body()).subscribe(eventArchieveResultAction);
                }

                @Override
                public void onFailure(@NonNull Call<AppCMSEventArchieveResult> call,
                                      @NonNull Throwable t) {
                    //Log.e(TAG, "onFailure: " + t.getMessage());
                    eventArchieveResultAction.call(null);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
    }
}
