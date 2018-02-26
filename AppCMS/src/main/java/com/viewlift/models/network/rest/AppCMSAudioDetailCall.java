package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;
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

public class AppCMSAudioDetailCall {

    private static final String TAG = AppCMSAudioDetailCall.class.getSimpleName() + "TAG";
    private final AppCMSAudioDetailRest appCMSAudioDetailRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSAudioDetailCall(AppCMSAudioDetailRest appCMSAudioDetailRest, Gson gson) {
        this.appCMSAudioDetailRest = appCMSAudioDetailRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url,
                     int tryCount, final Action1<AppCMSAudioDetailResult> audioDetailResultAction) throws IOException {
        try {
            appCMSAudioDetailRest.get(url).enqueue(new Callback<AppCMSAudioDetailResult>() {
                @Override
                public void onResponse(@NonNull Call<AppCMSAudioDetailResult> call,
                                       @NonNull Response<AppCMSAudioDetailResult> response) {
                    if (audioDetailResultAction != null && response.body()!=null) {
                        System.out.println("retry audio details success");

                        Observable.just(response.body()).subscribe(audioDetailResultAction);
                    }else{
                        System.out.println("retry audio details fail tryCount-"+tryCount);
                    }
                    if (audioDetailResultAction == null && tryCount == 0) {
                        System.out.println("retry audio details fail");
                        try {
                            call(url, tryCount + 1, audioDetailResultAction);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AppCMSAudioDetailResult> call,
                                      @NonNull Throwable t) {
                    //Log.e(TAG, "onFailure: " + t.getMessage());
                    audioDetailResultAction.call(null);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
    }
}
