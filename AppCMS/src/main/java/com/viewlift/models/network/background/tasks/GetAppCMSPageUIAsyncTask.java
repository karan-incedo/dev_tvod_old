package com.viewlift.models.network.background.tasks;

import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.network.rest.AppCMSPageUICall;

import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSPageUIAsyncTask {
    private static final String TAG = "";

    private final AppCMSPageUICall call;
    private final Action1<AppCMSPageUI> readyAction;

    public static class Params {
        String url;
        long timeStamp;
        boolean loadFromFile;

        public static class Builder {
            private Params params;

            public Builder() {
                params = new Params();
            }

            public Builder url(String url) {
                params.url = url;
                return this;
            }

            public Builder timeStamp(long timeStamp) {
                params.timeStamp = timeStamp;
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

    public GetAppCMSPageUIAsyncTask(AppCMSPageUICall call, Action1<AppCMSPageUI> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    public void execute(Params params) {
        if (params != null) {
            Observable
                    .fromCallable(() -> {
                        try {
                            return call.call(params.url, params.timeStamp, params.loadFromFile);
                        } catch (IOException e) {
                            //Log.e(TAG, "Could not retrieve Page UI data - " + params.url + ": " + e.toString());
                        }
                        return null;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((result) -> Observable.just(result).subscribe(readyAction));
        }
    }
}
