package com.viewlift.models.network.background.tasks;

import android.util.LruCache;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.network.rest.AppCMSPageAPICall;

import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/*
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSAPIAsyncTask {
    //private static final String TAG = "GetAppCMSAPIAsyncTask";

    private final AppCMSPageAPICall call;
    private final Action1<AppCMSPageAPI> readyAction;
    private Params currentParams;

    public GetAppCMSAPIAsyncTask(AppCMSPageAPICall call,
                                 Action1<AppCMSPageAPI> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    public void execute(Params params) {
        currentParams = params;
        Observable
                .fromCallable(() -> {
                    if (currentParams != null) {
                        try {
                            return call.call(currentParams.urlWithContent,
                                    currentParams.authToken,
                                    currentParams.pageId,
                                    currentParams.loadFromFile,
                                    0);
                        } catch (IOException e) {
                            //Log.e(TAG, "DialogType retrieving page API data: " + e.getMessage());
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (params.appCMSPageAPILruCache != null &&
                            readyAction != null) {
                        if (result != null) {
                            params.appCMSPageAPILruCache.put(params.pageId, result);
                        }
                        Observable.just(result).subscribe(readyAction);
                    }
                });
    }

    public void deleteAll(Action0 onDelete) {
        Observable
                .fromCallable(() -> {
                    call.deleteAllFiles();
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((t) -> {
                    if (onDelete != null) {
                        onDelete.call();
                    }
                });
    }

    public static class Params {
        String urlWithContent;
        String authToken;
        String pageId;
        boolean loadFromFile;
        LruCache<String, AppCMSPageAPI> appCMSPageAPILruCache;

        public static class Builder {
            private Params params;

            public Builder() {
                params = new Params();
            }

            public Builder context() {
                return this;
            }

            public Builder urlWithContent(String urlWithContent) {
                params.urlWithContent = urlWithContent;
                return this;
            }

            public Builder pageId(String pageId) {
                params.pageId = pageId;
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

            public Builder appCMSPageAPILruCache(LruCache<String, AppCMSPageAPI> appCMSPageAPILruCache) {
                params.appCMSPageAPILruCache = appCMSPageAPILruCache;
                return this;
            }

            public Params build() {
                return params;
            }
        }
    }
}
