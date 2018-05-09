package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.photogallery.AppCMSPhotoGalleryResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSPhotoGalleryCall {

    private static final String TAG = AppCMSPhotoGalleryCall.class.getSimpleName() + "TAG";
    private final AppCMSPhotoGalleryRest appCMSPhotoGalleryResult;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSPhotoGalleryCall(AppCMSPhotoGalleryRest appCMSArticleRest, Gson gson) {
        this.appCMSPhotoGalleryResult = appCMSArticleRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url, String xApiKey,
                     final Action1<AppCMSPhotoGalleryResult> photoGalleryResultAction) throws IOException {
        Log.d(TAG,"PhotoGallery URL : "+url);
        try {

            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("x-api-key", xApiKey);

            appCMSPhotoGalleryResult.get(url, authTokenMap).enqueue(new Callback<AppCMSPhotoGalleryResult>() {
                @Override
                public void onResponse(Call<AppCMSPhotoGalleryResult> call, Response<AppCMSPhotoGalleryResult> response) {
                    Observable.just(response.body()).subscribe(photoGalleryResultAction);
                }

                @Override
                public void onFailure(Call<AppCMSPhotoGalleryResult> call, Throwable t) {
                    photoGalleryResultAction.call(null);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
    }
}
