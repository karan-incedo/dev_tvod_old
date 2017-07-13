package com.viewlift.models.network.rest;

import com.viewlift.models.data.appcms.ui.authentication.ForgotPasswordRequest;
import com.viewlift.models.data.appcms.ui.authentication.ForgotPasswordResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 7/6/17.
 */

public class AppCMSResetPasswordCall {
    private AppCMSResetPasswordRest appCMSResetPasswordRest;

    @Inject
    public AppCMSResetPasswordCall(AppCMSResetPasswordRest appCMSResetPasswordRest) {
        this.appCMSResetPasswordRest = appCMSResetPasswordRest;
    }

    public void call(String url, String email, final Action1<ForgotPasswordResponse> readyAction) {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail(email);
        appCMSResetPasswordRest.resetPassword(url, forgotPasswordRequest).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                Observable.just(response.body()).subscribe(readyAction);
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                Observable.just((ForgotPasswordResponse) null).subscribe(readyAction);
            }
        });
    }
}
