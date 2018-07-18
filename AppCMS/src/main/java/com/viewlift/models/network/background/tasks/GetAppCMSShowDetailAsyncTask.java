package com.viewlift.models.network.background.tasks;

import android.util.Log;

import com.viewlift.models.data.appcms.api.AppCMSShowDetail;
import com.viewlift.models.network.rest.AppCMSShowDetailCall;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by anasazeem on 14/06/18.
 */

public class GetAppCMSShowDetailAsyncTask {
    private static final String TAG = "ShowDetailAsyncTask";

    private final AppCMSShowDetailCall call;
    private final Action1<AppCMSShowDetail> readyAction;

    public GetAppCMSShowDetailAsyncTask(AppCMSShowDetailCall call,
                                        Action1<AppCMSShowDetail> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    public void execute(GetAppCMSShowDetailAsyncTask.Params params) {
        Observable
                .fromCallable(() -> {
                    if (params != null) {
                        try {
                            return call.call(params.url/*"https://prod-api.viewlift.com/content/series/?id=dc3ef8ff-d2a8-4037-bd11-f711218845f5&site=snagfilms"*/, params.authToken, params.apiKey);
                        } catch (Exception e) {
                            Log.e(TAG, "DialogType retrieving page API data: " + e.getMessage());
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(RxJavaInterop.toV1Scheduler(AndroidSchedulers.mainThread()))
                .onErrorResumeNext(throwable -> Observable.empty())
                .subscribe((result) -> {
                    if (result != null && readyAction != null) {
                        Observable.just(result).subscribe(readyAction);
                    }
                });
    }

    public static class Params {
        String url;
        String authToken;
        String apiKey;
        boolean loadFromFile;

        public static class Builder {
            private Params params;

            public Builder() {
                this.params = new Params();
            }

            public Params.Builder url(String url) {
                params.url = url;
                return this;
            }

            public Params.Builder authToken(String authToken) {
                params.authToken = authToken;
                return this;
            }

            public Params.Builder loadFromFile(boolean loadFromFile) {
                params.loadFromFile = loadFromFile;
                return this;
            }

            public Params.Builder apiKey(String apiKey) {
                params.apiKey = apiKey;
                return this;
            }

            public Params build() {
                return params;
            }
        }
    }
}
