package com.viewlift.models.network.rest;

/*
 * Created by View on 7/20/17.
 */

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.ui.authentication.AnonymousAuthTokenResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSAnonymousAuthTokenCall {

    private static final String TAG = "AnonymousTokenCallTAG_";
    private final AppCMSAnonymousAuthTokenRest anonymousAuthTokenRest;

    @SuppressWarnings("FieldCanBeLocal, unused")
    private final Gson gson;

    @Inject
    public AppCMSAnonymousAuthTokenCall(AppCMSAnonymousAuthTokenRest anonymousAuthTokenRest,
                                        Gson gson) {
        this.anonymousAuthTokenRest = anonymousAuthTokenRest;
        this.gson = gson;
    }

    public void call(String url, final Action1<AnonymousAuthTokenResponse> responseAction1) {
        anonymousAuthTokenRest.get(url).enqueue(new Callback<AnonymousAuthTokenResponse>() {
            @Override
            public void onResponse(@NonNull Call<AnonymousAuthTokenResponse> call,
                                   @NonNull Response<AnonymousAuthTokenResponse> response) {
                Observable.just(response.body())
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(responseAction1);
            }

            @Override
            public void onFailure(@NonNull Call<AnonymousAuthTokenResponse> call,
                                  @NonNull Throwable t) {
                //Log.e(TAG, "onFailure: " + t.getMessage());
                Observable.just((AnonymousAuthTokenResponse) null)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(responseAction1);
            }
        });
    }
}
