package com.viewlift.models.network.rest;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.ui.authentication.FacebookLoginRequest;
import com.viewlift.models.data.appcms.ui.authentication.FacebookLoginResponse;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 7/6/17.
 */

public class AppCMSFacebookLoginCall {
    private static final String TAG = "AppCMSFacebookLogin";

    private AppCMSFacebookLoginRest appCMSFacebookLoginRest;
    private Gson gson;

    @Inject
    public AppCMSFacebookLoginCall(AppCMSFacebookLoginRest appCMSFacebookLoginRest,
                                   Gson gson) {
        this.appCMSFacebookLoginRest = appCMSFacebookLoginRest;
        this.gson = gson;
    }

    public void call(String url,
                     String facebookAccessToken,
                     String userId,
                     String xApiKey,
                     final Action1<FacebookLoginResponse> readyAction) {
        FacebookLoginRequest facebookLoginRequest = new FacebookLoginRequest();
        facebookLoginRequest.setAccessToken(facebookAccessToken);
        facebookLoginRequest.setUserId(userId);

        Map<String, String> authTokenMap = new HashMap<>();
        authTokenMap.put("x-api-key", xApiKey);

        appCMSFacebookLoginRest.login(url, facebookLoginRequest, authTokenMap).enqueue(new Callback<FacebookLoginResponse>() {
            @Override
            public void onResponse(Call<FacebookLoginResponse> call, Response<FacebookLoginResponse> response) {
                if (response.body() != null) {
                    Observable.just(response.body())
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(readyAction);
                } else if (response.errorBody() != null) {
                    try {
                        FacebookLoginResponse facebookLoginResponse =
                                gson.fromJson(response.errorBody().string(),
                                        FacebookLoginResponse.class);
                        Observable.just(facebookLoginResponse)
                                .onErrorResumeNext(throwable -> Observable.empty())
                                .subscribe(readyAction);
                    } catch (Exception e) {
                        Observable.just((FacebookLoginResponse) null)
                                .onErrorResumeNext(throwable -> Observable.empty())
                                .subscribe(readyAction);
                    }
                } else {
                    Observable.just((FacebookLoginResponse) null)
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(readyAction);
                }
            }

            @Override
            public void onFailure(Call<FacebookLoginResponse> call, Throwable t) {
                Observable.just((FacebookLoginResponse) null)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(readyAction);
            }
        });
    }
}
