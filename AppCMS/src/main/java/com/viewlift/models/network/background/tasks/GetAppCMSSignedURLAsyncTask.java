package com.viewlift.models.network.background.tasks;

import android.util.Log;

import com.viewlift.models.data.appcms.api.AppCMSSignedURLResult;
import com.viewlift.models.network.rest.AppCMSSignedURLCall;

import java.io.IOException;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by viewlift on 10/10/17.
 */

public class GetAppCMSSignedURLAsyncTask {
    private static final String TAG = "SignedURLAsyncTask";

    private final AppCMSSignedURLCall appCMSSignedURLCall;
    private final Action1<AppCMSSignedURLResult> appCMSSignedURLResultAction1;

    public static class Params {
        String authToken;
        String url;
        public static class Builder {
            Params params;
            public Builder() {
                params = new Params();
            }
            public Builder authToken(String authToken) {
                params.authToken = authToken;
                return this;
            }
            public Builder url(String url) {
                params.url = url;
                return this;
            }
            public Params build() {
                return params;
            }
        }
    }

    public GetAppCMSSignedURLAsyncTask(AppCMSSignedURLCall appCMSSignedURLCall,
                                       Action1<AppCMSSignedURLResult> appCMSSignedURLResultAction1) {
        this.appCMSSignedURLCall = appCMSSignedURLCall;
        this.appCMSSignedURLResultAction1 = appCMSSignedURLResultAction1;
    }

    public void execute(Params params) {
        Observable
                .fromCallable(() -> {
                    if (params != null) {
                        try {
                            return appCMSSignedURLCall.call(params.authToken, params.url);
                        } catch (Exception e) {
                            //Log.e(TAG, "Error retrieving AppCMS Android file with params " +
//                                    params.toString() + ": " +
//                                    e.getMessage());
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(RxJavaInterop.toV1Scheduler(AndroidSchedulers.mainThread()))
                .onErrorResumeNext(throwable -> Observable.empty())
                .subscribe((result) -> {
                    if (appCMSSignedURLResultAction1 != null && result != null) {
                        Observable.just(result).subscribe(appCMSSignedURLResultAction1);
                    }
                });
    }
}
