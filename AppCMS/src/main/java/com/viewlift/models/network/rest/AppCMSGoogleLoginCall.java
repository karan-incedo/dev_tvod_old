package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/24/17.
 */

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.ui.authentication.GoogleLoginRequest;
import com.viewlift.models.data.appcms.ui.authentication.GoogleLoginResponse;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSGoogleLoginCall {
    private static final String TAG = "AppCMSGoogleLoginTAG_";

    private AppCMSGoogleLoginRest appCMSGoogleLoginRest;
    private final Gson gson;

    @Inject
    public AppCMSGoogleLoginCall(AppCMSGoogleLoginRest appCMSGoogleLoginRest, Gson gson) {
        this.appCMSGoogleLoginRest = appCMSGoogleLoginRest;
        this.gson = gson;
    }

    public void call(String url,
                     String googleAccessToken,String xApi,
                     final Action1<GoogleLoginResponse> responseAction1) {
        GoogleLoginRequest googleLoginRequest = new GoogleLoginRequest();
        googleLoginRequest.setAccessToken(googleAccessToken);
        Map<String, String> authTokenMap = new HashMap<>();
        authTokenMap.put("x-api-key", xApi);
        appCMSGoogleLoginRest.login(url, authTokenMap,googleLoginRequest).enqueue(new Callback<GoogleLoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<GoogleLoginResponse> call,
                                   @NonNull Response<GoogleLoginResponse> response) {
                if (response.body() != null) {
                    Observable.just(response.body())
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(responseAction1);
                } else if (response.errorBody() != null) {
                    try {
                        GoogleLoginResponse googleLoginResponse =
                                gson.fromJson(response.errorBody().string(),
                                        GoogleLoginResponse.class);
                        Observable.just(googleLoginResponse)
                                .onErrorResumeNext(throwable -> Observable.empty())
                                .subscribe(responseAction1);
                    } catch (Exception e) {
                        Observable.just((GoogleLoginResponse) null)
                                .onErrorResumeNext(throwable -> Observable.empty())
                                .subscribe(responseAction1);
                    }
                } else {
                    Observable.just((GoogleLoginResponse) null)
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(responseAction1);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleLoginResponse> call, @NonNull Throwable t) {
                Observable.just((GoogleLoginResponse) null)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(responseAction1);
            }
        });
    }
}
