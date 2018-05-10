package com.viewlift.models.network.background.tasks;

import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidUI;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;

import java.io.IOException;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by viewlift on 5/4/17.
 */

public class GetAppCMSAndroidUIAsyncTask {
    private static final String TAG = "AndroidAsyncTask";

    private final AppCMSAndroidUICall call;
    private final Action1<AppCMSAndroidUI> readyAction;

    public static class Params {
        String url;
        String xApiKey;
        boolean loadFromFile;
        boolean bustCache;
        public static class Builder {
            private Params params;
            public Builder() {
                params = new Params();
            }
            public Builder url(String url) {
                params.url = url;
                return this;
            }
            public Builder xApiKey(String xApiKey) {
                params.xApiKey = xApiKey;
                return this;
            }
            public Builder loadFromFile(boolean loadFromFile) {
                params.loadFromFile = loadFromFile;
                return this;
            }
            public Builder bustCache(boolean bustCache) {
                params.bustCache = bustCache;
                return this;
            }
            public Params build() {
                return params;
            }
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("url: " + params.url + " loadFromFile: " + params.loadFromFile);
                return sb.toString();
            }
        }
    }

    public GetAppCMSAndroidUIAsyncTask(AppCMSAndroidUICall call, Action1<AppCMSAndroidUI> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    public void execute(Params params) {
        Observable
                .fromCallable(() -> {
                    if (params != null) {
                        try {
                            return call.call(params.url,
                                    params.xApiKey,
                                    params.loadFromFile,
                                    params.bustCache,
                                    0);
                        } catch (IOException e) {
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
                .subscribe((result) -> Observable.just(result).subscribe(readyAction));
    }
}
