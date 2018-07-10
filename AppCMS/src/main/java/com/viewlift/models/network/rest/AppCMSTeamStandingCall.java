package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.AppCMSStandingResult;
import com.viewlift.models.data.appcms.playlist.AppCMSPlaylistResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSTeamStandingCall {

    private static final String TAG = AppCMSTeamStandingCall.class.getSimpleName() + "TAG";
    private final AppCMSTeamStandingRest appCMSStandingRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSTeamStandingCall(AppCMSTeamStandingRest appCMSPlaylistRest, Gson gson) {
        this.appCMSStandingRest = appCMSPlaylistRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, String xApiKey,
                     final Action1<AppCMSStandingResult> playlistResultAction) throws IOException {
        try {

            Map<String, String> authTokenMap = new HashMap<>();
//            authTokenMap.put("x-api-key", xApiKey);

            appCMSStandingRest.get(url, authTokenMap).enqueue(new Callback<AppCMSStandingResult>() {
                @Override
                public void onResponse(@NonNull Call<AppCMSStandingResult> call,
                                       @NonNull Response<AppCMSStandingResult> response) {
                    Observable.just(response.body()).subscribe(playlistResultAction);
                }

                @Override
                public void onFailure(@NonNull Call<AppCMSStandingResult> call,
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
