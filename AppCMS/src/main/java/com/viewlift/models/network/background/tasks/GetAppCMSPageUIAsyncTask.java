package com.viewlift.models.network.background.tasks;

import android.text.TextUtils;
import android.util.Log;

import com.viewlift.models.data.appcms.ui.android.MetaPage;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.network.rest.AppCMSPageUICall;

import java.io.IOException;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSPageUIAsyncTask {
    private static final String TAG = "";

    private final AppCMSPageUICall call;
    private final Action1<AppCMSPageUI> readyAction;

    public static class MetaPageUI {
        private AppCMSPageUI appCMSPageUI;
        private MetaPage metaPage;

        public AppCMSPageUI getAppCMSPageUI() {
            return appCMSPageUI;
        }

        public void setAppCMSPageUI(AppCMSPageUI appCMSPageUI) {
            this.appCMSPageUI = appCMSPageUI;
        }

        public MetaPage getMetaPage() {
            return metaPage;
        }

        public void setMetaPage(MetaPage metaPage) {
            this.metaPage = metaPage;
        }
    }

    public static class Params {
        String url;
        boolean loadFromFile;
        boolean bustCache;
        MetaPage metaPage;

        public static class Builder {
            private Params params;

            public Builder() {
                params = new Params();
            }

            public Builder url(String url) {
                params.url = url;
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

            public Builder metaPage(MetaPage metaPage) {
                params.metaPage = metaPage;
                return this;
            }

            public Params build() {
                return params;
            }
        }
    }

    public GetAppCMSPageUIAsyncTask(AppCMSPageUICall call, Action1<AppCMSPageUI> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    public Observable<MetaPageUI> getObservable(Params params) {
        if (params != null) {
            return Observable
                    .fromCallable(() -> {
                        try {
                            MetaPageUI metaPageUI = new MetaPageUI();
                            metaPageUI.setMetaPage(params.metaPage);
                            metaPageUI.setAppCMSPageUI(call.call(params.url,
                                    params.bustCache,
                                    params.loadFromFile));
                            return metaPageUI;
                        } catch (Exception e) {
                            Log.e(TAG, "Could not retrieve Page UI data - " + params.url + ": " + e.toString());
                        }
                        return null;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(RxJavaInterop.toV1Scheduler(AndroidSchedulers.mainThread()))
                    .onErrorResumeNext(throwable -> Observable.empty());
        }
        return null;
    }

    public void execute(Params params) {
        if (params != null) {
            Observable
                    .fromCallable(() -> {
                        try {
                            return call.call(params.url, params.bustCache, params.loadFromFile);
                        } catch (IOException e) {
                            //Log.e(TAG, "Could not retrieve Page UI data - " + params.url + ": " + e.toString());
                        }
                        return null;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(RxJavaInterop.toV1Scheduler(AndroidSchedulers.mainThread()))
                    .onErrorResumeNext(throwable -> Observable.empty())
                    .subscribe((result) -> Observable.just(result).subscribe(readyAction));
        }
    }

    public void writeToFile(AppCMSPageUI appCMSPageUI, String url) {
        if (appCMSPageUI != null && !TextUtils.isEmpty(url)) {
            Observable
                    .fromCallable(() -> call.writeToFile(appCMSPageUI, url))
                    .subscribeOn(Schedulers.io())
                    .observeOn(RxJavaInterop.toV1Scheduler(AndroidSchedulers.mainThread()))
                    .onErrorResumeNext(throwable -> Observable.empty())
                    .subscribe(result -> {});
        }
    }
}
