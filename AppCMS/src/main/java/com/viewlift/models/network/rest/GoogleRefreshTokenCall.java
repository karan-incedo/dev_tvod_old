package com.viewlift.models.network.rest;

import com.viewlift.models.billing.appcms.authentication.GoogleRefreshTokenResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 7/25/17.
 */

public class GoogleRefreshTokenCall {
    private static final String TAG = "GoogleRefreshToken";
    private GoogleRefreshTokenRest googleRefreshTokenRest;

    @Inject
    public GoogleRefreshTokenCall(GoogleRefreshTokenRest googleRefreshTokenRest) {
        this.googleRefreshTokenRest = googleRefreshTokenRest;
    }

    public void refreshTokenCall(String url,
                                 String grantType,
                                 String clientId,
                                 String clientSecret,
                                 String refreshToken,
                                 Action1<GoogleRefreshTokenResponse> readyAction) {
        googleRefreshTokenRest.refreshToken(url,
                grantType,
                clientId,
                clientSecret,
                refreshToken)
                .enqueue(new Callback<GoogleRefreshTokenResponse>() {
                    @Override
                    public void onResponse(Call<GoogleRefreshTokenResponse> call, Response<GoogleRefreshTokenResponse> response) {
                        Observable.just(response.body())
                                .onErrorResumeNext(throwable -> Observable.empty())
                                .subscribe(readyAction);
                    }

                    @Override
                    public void onFailure(Call<GoogleRefreshTokenResponse> call, Throwable t) {
                        Observable.just((GoogleRefreshTokenResponse) null)
                                .onErrorResumeNext(throwable -> Observable.empty())
                                .subscribe(readyAction);
                    }
                });
    }
}
