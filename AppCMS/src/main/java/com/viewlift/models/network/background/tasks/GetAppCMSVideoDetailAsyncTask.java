package com.viewlift.models.network.background.tasks;

import android.util.Log;

import com.viewlift.models.data.appcms.api.AppCMSVideoDetail;
import com.viewlift.models.network.rest.AppCMSVideoDetailCall;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by anas.azeem on 7/12/2017.
 * Owned by ViewLift, NYC
 */
public class GetAppCMSVideoDetailAsyncTask {
    private static final String TAG = "VideoDetailAsyncTask";

    private final AppCMSVideoDetailCall call;
    private final Action1<AppCMSVideoDetail> readyAction;
    String xApi;

    public GetAppCMSVideoDetailAsyncTask(AppCMSVideoDetailCall call,
                                         Action1<AppCMSVideoDetail> readyAction, String xApi) {
        this.call = call;
        this.readyAction = readyAction;
        this.xApi = xApi;
    }

    public void execute(Params params) {
        Observable
                .fromCallable(() -> {
                    if (params != null) {
                        try {
                            return call.call(params.url, params.authToken,xApi);
                        } catch (Exception e) {
                            //Log.e(TAG, "DialogType retrieving page API data: " + e.getMessage());
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
        boolean loadFromFile;

        public static class Builder {
            private Params params;

            public Builder() {
                this.params = new Params();
            }

            public Builder url(String url) {
                params.url = url;
                return this;
            }

            public Builder authToken(String authToken) {
                params.authToken = authToken;
                return this;
            }

            public Builder loadFromFile(boolean loadFromFile) {
                params.loadFromFile = loadFromFile;
                return this;
            }

            public Params build() {
                return params;
            }
        }
    }
}