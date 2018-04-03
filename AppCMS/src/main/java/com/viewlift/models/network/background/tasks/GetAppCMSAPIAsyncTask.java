package com.viewlift.models.network.background.tasks;

import android.content.Context;
import android.util.LruCache;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.network.rest.AppCMSPageAPICall;

import java.io.IOException;
import java.util.List;

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

    public void executeWithModules(Params params) {
        currentParams = params;
        Observable
                .fromCallable(() -> {
                    if (currentParams != null) {
                        try {
                            return call.callWithModules(currentParams.urlWithContent,
                                    currentParams.authToken);
                        } catch (Exception e) {
                            //Log.e(TAG, "DialogType retrieving page API data: " + e.getMessage());
                            System.out.println("*-======* "+e.getMessage());
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> Observable.empty())
                .subscribe((result) -> {
                    Observable.just(result).subscribe(readyAction);
                });
    }

    public void execute(Params params) {
        currentParams = params;
        Observable
                .fromCallable(() -> {
                    if (currentParams != null) {
                        try {
                            return call.call(currentParams.context,
                                    currentParams.urlWithContent,
                                    currentParams.authToken,
                                    currentParams.pageId,
                                    currentParams.loadFromFile,
                                    0,
                                    currentParams.modules);
                        } catch (IOException e) {
                            //Log.e(TAG, "DialogType retrieving page API data: " + e.getMessage());
                            e.printStackTrace();
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                            try {
                                System.gc();
                                Thread.sleep(1000);
                                execute(params);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> Observable.empty())
                .subscribe((result) -> {
                    if (params.appCMSPageAPILruCache != null ) {
                        if (result != null) {
                            params.appCMSPageAPILruCache.put(params.pageId, result);
                        }
                        if (readyAction != null) {
                            Observable.just(result).subscribe(readyAction);
                        }
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
        Context context;
        String urlWithContent;
        String authToken;
        String pageId;
        boolean loadFromFile;
        List<String> modules;
        LruCache<String, AppCMSPageAPI> appCMSPageAPILruCache;

        public static class Builder {
            private Params params;

            public Builder() {
                params = new Params();
            }

            public Builder context(Context context) {
                params.context = context;
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

            public Builder modules(List<String> modules) {
                params.modules = modules;
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
