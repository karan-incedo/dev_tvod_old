package com.viewlift.models.network.rest;

/*
 * Created by View on 7/20/17.
 */

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.ui.authentication.AnonymousAuthTokenResponse;

import java.util.HashMap;
import java.util.Map;

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
    private Map<String, String> headersMap;

    @Inject
    public AppCMSAnonymousAuthTokenCall(AppCMSAnonymousAuthTokenRest anonymousAuthTokenRest,
                                        Gson gson) {
        this.anonymousAuthTokenRest = anonymousAuthTokenRest;
        this.gson = gson;
        this.headersMap = new HashMap<>();
    }

    public void call(String url, final Action1<AnonymousAuthTokenResponse> responseAction1, String apiKey) {
        headersMap.clear();
        if (!TextUtils.isEmpty(apiKey)) {
            headersMap.put("x-api-key", apiKey);
        }
        anonymousAuthTokenRest.get(url, headersMap).enqueue(new Callback<AnonymousAuthTokenResponse>() {
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
