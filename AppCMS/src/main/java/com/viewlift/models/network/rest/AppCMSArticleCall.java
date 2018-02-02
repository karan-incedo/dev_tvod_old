package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.article.AppCMSArticleResult;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSArticleCall {

    private static final String TAG = AppCMSArticleCall.class.getSimpleName() + "TAG";
    private final AppCMSArticleRest appCMSArticleRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSArticleCall(AppCMSArticleRest appCMSArticleRest, Gson gson) {
        this.appCMSArticleRest = appCMSArticleRest;
        this.gson = gson;
    }

    @WorkerThread
    public void call(String url,
                     final Action1<AppCMSArticleResult> articleResultAction) throws IOException {


        try {
            appCMSArticleRest.get(url).enqueue(new Callback<AppCMSArticleResult>() {
                @Override
                public void onResponse(Call<AppCMSArticleResult> call, Response<AppCMSArticleResult> response) {
                    Observable.just(response.body()).subscribe(articleResultAction);
                }

                @Override
                public void onFailure(Call<AppCMSArticleResult> call, Throwable t) {
                    articleResultAction.call(null);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
    }
}
