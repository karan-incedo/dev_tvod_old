package com.viewlift.models.network.background.tasks;

import android.content.Context;

import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.network.rest.AppCMSMainUICall;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSMainUIAsyncTask {
    private static final String TAG = "GetAppCMSMainAsyncTask";

    private final AppCMSMainUICall call;
    private final Action1<AppCMSMain> readyAction;

    public GetAppCMSMainUIAsyncTask(AppCMSMainUICall call,
                                    Action1<AppCMSMain> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    public void execute(Params params) {
        Observable
                .fromCallable(() -> {
                    if (params != null) {
                        try {
                            return call.call(params.context,
                                    params.siteId,
                                    0,
                                    params.bustCache,
                                    params.networkDisconnected);
                        } catch (Exception e) {
                            //Log.e(TAG, "DialogType retrieving page API data: " + e.getMessage());
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(RxJavaInterop.toV1Scheduler(AndroidSchedulers.mainThread()))
                .onErrorResumeNext(throwable -> Observable.empty())
                .subscribe((result) -> Observable.just(result).subscribe(readyAction));
    }

    public static class Params {
        Context context;
        String siteId;
        boolean bustCache;
        boolean networkDisconnected;

        public static class Builder {
            Params params;

            public Builder() {
                params = new Params();
            }

            public Builder context(Context context) {
                params.context = context;
                return this;
            }

            public Builder siteId(String siteId) {
                params.siteId = siteId;
                return this;
            }

            public Builder bustCache(boolean bustCache) {
                params.bustCache = bustCache;
                return this;
            }

            public Builder networkDisconnected(boolean networkDisconnected) {
                params.networkDisconnected = networkDisconnected;
                return this;
            }

            public Params build() {
                return params;
            }
        }
    }
}
