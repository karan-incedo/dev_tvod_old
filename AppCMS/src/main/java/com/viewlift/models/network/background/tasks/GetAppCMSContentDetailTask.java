package com.viewlift.models.network.background.tasks;

import com.viewlift.models.data.appcms.api.AppCMSContentDetail;
import com.viewlift.models.network.rest.AppCMSContentDetailCall;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by anas.azeem on 3/12/2018.
 * Owned by ViewLift, NYC
 */

public class GetAppCMSContentDetailTask {
    private static final String TAG = "GetAppCMSContentDetailTask";

    private final AppCMSContentDetailCall call;
    private final Action1<AppCMSContentDetail> readyAction;

    public GetAppCMSContentDetailTask(AppCMSContentDetailCall call,
                                      Action1<AppCMSContentDetail> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    public void execute(Params params) {
        Observable
                .fromCallable(() -> {
                    if (params != null) {
                        try {
                            return call.call(params.url, params.authToken);
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
                    if (readyAction != null) {
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