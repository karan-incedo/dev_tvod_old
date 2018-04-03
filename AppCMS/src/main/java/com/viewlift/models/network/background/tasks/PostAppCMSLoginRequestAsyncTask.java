package com.viewlift.models.network.background.tasks;

import android.util.Log;

import com.viewlift.models.data.appcms.ui.authentication.SignInResponse;
import com.viewlift.models.network.rest.AppCMSSignInCall;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by viewlift on 7/5/17.
 */

public class PostAppCMSLoginRequestAsyncTask {
    private final AppCMSSignInCall call;
    private final Action1<SignInResponse> readyAction;

    private static final String TAG = "LoginRequestTask";

    public static class Params {
        String url;
        String email;
        String password;
        public static class Builder {
            private Params params;
            public Builder() {
                this.params = new Params();
            }
            public Builder url(String url) {
                params.url = url;
                return this;
            }
            public Builder email(String email) {
                params.email = email;
                return this;
            }
            public Builder password(String password) {
                params.password = password;
                return this;
            }
            public Params build() {
                return params;
            }
        }
    }

    public PostAppCMSLoginRequestAsyncTask(AppCMSSignInCall call,
                                           Action1<SignInResponse> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    public void execute(Params params) {
        Observable
                .fromCallable(() -> {
                    if (params != null) {
                        try {
                            return call.call(params.url, params.email, params.password);
                        } catch (Exception e) {
                            //Log.e(TAG, "DialogType retrieving page API data: " + e.getMessage());
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> Observable.empty())
                .subscribe((result) -> Observable.just(result).subscribe(readyAction));
    }
}
