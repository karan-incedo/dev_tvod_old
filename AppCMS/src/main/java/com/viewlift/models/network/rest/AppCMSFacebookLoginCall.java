package com.viewlift.models.network.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.ui.authentication.FacebookLoginRequest;
import com.viewlift.models.data.appcms.ui.authentication.FacebookLoginResponse;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;
import rx.Observable;

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
    }

    public void call(String url,
                     String facebookAccessToken,
                     String userId,
                     final Action1<FacebookLoginResponse> readyAction) {
        FacebookLoginRequest facebookLoginRequest = new FacebookLoginRequest();
        facebookLoginRequest.setAccessToken(facebookAccessToken);
        facebookLoginRequest.setUserId(userId);
        appCMSFacebookLoginRest.login(url, facebookLoginRequest).enqueue(new Callback<FacebookLoginResponse>() {
            @Override
            public void onResponse(Call<FacebookLoginResponse> call, Response<FacebookLoginResponse> response) {
                Log.d(TAG, "Response code: " + response.code());
                if (response.body() != null) {
                    Observable.just(response.body()).subscribe(readyAction);
                } else if (response.errorBody() != null) {
                    try {
                        FacebookLoginResponse facebookLoginResponse = gson.fromJson(response.errorBody().string(),
                                FacebookLoginResponse.class);
                        Observable.just(facebookLoginResponse).subscribe(readyAction);
                    } catch (NullPointerException | IOException e) {
                        Log.e(TAG, "Could not parse Facebook Login Response error body");
                        FacebookLoginResponse facebookLoginResponse = new FacebookLoginResponse();
                        facebookLoginResponse.setError(response.raw().message());
                        Observable.just(facebookLoginResponse).subscribe(readyAction);
                    }
                }
            }

            @Override
            public void onFailure(Call<FacebookLoginResponse> call, Throwable t) {
                Log.e(TAG, "Failed to retrieve response from Facebook login: " +
                    t.getMessage());
                Observable.just((FacebookLoginResponse) null).subscribe(readyAction);
            }
        });
    }
}
