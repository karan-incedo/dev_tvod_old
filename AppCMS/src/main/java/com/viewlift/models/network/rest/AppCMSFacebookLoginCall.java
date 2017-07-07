package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.ui.authentication.FacebookLoginResponse;

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

    @Inject
    public AppCMSFacebookLoginCall(AppCMSFacebookLoginRest appCMSFacebookLoginRest) {
        this.appCMSFacebookLoginRest = appCMSFacebookLoginRest;
    }

    public void call(String url, final Action1<FacebookLoginResponse> readyAction) {
        appCMSFacebookLoginRest.login(url).enqueue(new Callback<FacebookLoginResponse>() {
            @Override
            public void onResponse(Call<FacebookLoginResponse> call, Response<FacebookLoginResponse> response) {
                Observable.just(response.body()).subscribe(readyAction);
            }

            @Override
            public void onFailure(Call<FacebookLoginResponse> call, Throwable t) {
                Observable.just((FacebookLoginResponse) null).subscribe(readyAction);
            }
        });
    }
}
