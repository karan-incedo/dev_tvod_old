package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.AppCMSStandingResult;
import com.viewlift.models.data.appcms.api.AppCMSTeamRoasterResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSTeamRoasterCall {

    private static final String TAG = AppCMSTeamRoasterCall.class.getSimpleName() + "TAG";
    private final AppCMSTeamRoasterRest appCMSTeamRoasterRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSTeamRoasterCall(AppCMSTeamRoasterRest appCMSPlaylistRest, Gson gson) {
        this.appCMSTeamRoasterRest = appCMSPlaylistRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, String xApiKey,
                     final Action1<AppCMSTeamRoasterResult> playlistResultAction) throws IOException {
        try {

            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("apikey", xApiKey);

            appCMSTeamRoasterRest.get(url, authTokenMap).enqueue(new Callback<AppCMSTeamRoasterResult>() {
                @Override
                public void onResponse(@NonNull Call<AppCMSTeamRoasterResult> call,
                                       @NonNull Response<AppCMSTeamRoasterResult> response) {
                    Observable.just(response.body()).subscribe(playlistResultAction);
                }

                @Override
                public void onFailure(@NonNull Call<AppCMSTeamRoasterResult> call,
                                      @NonNull Throwable t) {
                    //Log.e(TAG, "onFailure: " + t.getMessage());
                    playlistResultAction.call(null);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
    }
}
