package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/24/17.
 */

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.ui.authentication.GoogleLoginRequest;
import com.viewlift.models.data.appcms.ui.authentication.GoogleLoginResponse;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSGoogleLoginCall {
    private static final String TAG = "AppCMSGoogleLoginTAG_";

    private AppCMSGoogleLoginRest appCMSGoogleLoginRest;
    private Gson gson;

    @Inject
    public AppCMSGoogleLoginCall(AppCMSGoogleLoginRest appCMSGoogleLoginRest,
                                 Gson gson) {
        this.appCMSGoogleLoginRest = appCMSGoogleLoginRest;
    }

    public void call(String url,
                     String googleAccessToken,
                     final Action1<GoogleLoginResponse> responseAction1) {
        GoogleLoginRequest googleLoginRequest = new GoogleLoginRequest();
        googleLoginRequest.setAccessToken(googleAccessToken);

        appCMSGoogleLoginRest.login(url, googleLoginRequest).enqueue(new Callback<GoogleLoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<GoogleLoginResponse> call,
                                   @NonNull Response<GoogleLoginResponse> response) {
                Log.d(TAG, "Response code: " + response.code());

                if (response.body() != null) {
                    Observable.just(response.body()).subscribe(responseAction1);
                } else if (response.errorBody() != null) {
                    try {
                        GoogleLoginResponse googleLoginResponse = gson.fromJson(response.errorBody().string(),
                                GoogleLoginResponse.class);
                        Observable.just(googleLoginResponse).subscribe(responseAction1);
                    } catch (NullPointerException | IOException e) {
                        Log.e(TAG, "Could not parse Facebook Login Response error body");
                        GoogleLoginResponse googleLoginResponse = new GoogleLoginResponse();
                        googleLoginResponse.setError(response.raw().message());
                        Observable.just(googleLoginResponse).subscribe(responseAction1);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleLoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to retrieve response from Google login: " +
                        t.getMessage());
                Observable.just((GoogleLoginResponse) null).subscribe(responseAction1);
            }
        });
    }
}
