package com.viewlift.models.network.rest;

import android.support.annotation.NonNull;

import com.viewlift.models.data.appcms.ui.authentication.UserIdentity;
import com.viewlift.models.data.appcms.ui.authentication.UserIdentityPassword;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 7/6/17.
 */

public class AppCMSUserIdentityCall {
    private final AppCMSUserIdentityRest appCMSUserIdentityRest;
    private final Map<String, String> authHeaders;

    @Inject
    public AppCMSUserIdentityCall(AppCMSUserIdentityRest appCMSUserIdentityRest) {
        this.appCMSUserIdentityRest = appCMSUserIdentityRest;
        this.authHeaders = new HashMap<>();
    }

    public void callGet(String url, String authToken, final Action1<UserIdentity> userIdentityAction) {
        authHeaders.put("Authorization", authToken);
        appCMSUserIdentityRest.get(url, authHeaders).enqueue(new Callback<UserIdentity>() {
            @Override
            public void onResponse(@NonNull Call<UserIdentity> call,
                                   @NonNull Response<UserIdentity> response) {
                Observable.just(response.body())
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(userIdentityAction);
            }

            @Override
            public void onFailure(@NonNull Call<UserIdentity> call, @NonNull Throwable t) {
                Observable.just((UserIdentity) null)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(userIdentityAction);
            }
        });
    }

    public void callPost(String url,
                         String authToken,
                         UserIdentity userIdentity,
                         final Action1<UserIdentity> userIdentityAction,
                         final Action1<ResponseBody> userErrorAction) {
        authHeaders.put("Authorization", authToken);
        appCMSUserIdentityRest.post(url, authHeaders, userIdentity).enqueue(new Callback<UserIdentity>() {
            @Override
            public void onResponse(@NonNull Call<UserIdentity> call,
                                   @NonNull Response<UserIdentity> response) {
                if (response.body() != null) {
                    Observable.just(response.body())
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(userIdentityAction);
                } else {
                    Observable.just(response.errorBody()).subscribe(userErrorAction);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserIdentity> call, @NonNull Throwable t) {
                Observable.just((UserIdentity) null)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(userIdentityAction);
            }
        });
    }

    public void passwordPost(String url,
                             String authToken,
                             UserIdentityPassword userIdentityPassword,
                             final Action1<UserIdentityPassword> userIdentityPasswordAction1,
                             final Action1<ResponseBody> userPasswordErrorAction) {
        authHeaders.put("resetToken", authToken);
        appCMSUserIdentityRest.post(url, authHeaders,
                userIdentityPassword).enqueue(new Callback<UserIdentityPassword>() {
            @Override
            public void onResponse(@NonNull Call<UserIdentityPassword> call,
                                   @NonNull Response<UserIdentityPassword> response) {
                if (response.body() != null) {
                    Observable.just(response.body())
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(userIdentityPasswordAction1);
                } else {
                    Observable.just(response.errorBody())
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(userPasswordErrorAction);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserIdentityPassword> call, @NonNull Throwable t) {
                //
            }
        });
    }
}
