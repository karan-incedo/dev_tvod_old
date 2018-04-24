package com.viewlift.models.network.background.tasks;

import com.viewlift.models.data.appcms.sites.AppCMSSite;
import com.viewlift.models.network.rest.AppCMSSiteCall;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by viewlift on 6/15/17.
 */

public class GetAppCMSSiteAsyncTask {
    private static final String TAG = "GetAppCMSSiteAsyncTask";

    private final AppCMSSiteCall call;
    private final Action1<AppCMSSite> readyAction;

    public GetAppCMSSiteAsyncTask(AppCMSSiteCall call,
                                  Action1<AppCMSSite> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    public void execute(String params,String apiKey, boolean networkDisconnected) {
        Observable
                .fromCallable(() -> {
                    if (params != null) {
                        try {
                            return call.call(params,apiKey, networkDisconnected, 0);
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
