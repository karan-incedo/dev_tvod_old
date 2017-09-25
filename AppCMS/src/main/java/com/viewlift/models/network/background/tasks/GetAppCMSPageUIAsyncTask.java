package com.viewlift.models.network.background.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;

import java.io.IOException;

import com.viewlift.models.network.rest.AppCMSPageUICall;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/9/17.
 */

public class GetAppCMSPageUIAsyncTask extends AsyncTask<GetAppCMSPageUIAsyncTask.Params, Integer, AppCMSPageUI> {
    private static final String TAG = "";

    private final AppCMSPageUICall call;
    private final Action1<AppCMSPageUI> readyAction;

    public static class Params {
        Context context;
        String url;
        public static class Builder {
            private Params params;
            public Builder() {
                params = new Params();
            }
            public Builder context(Context context) {
                params.context = context;
                return this;
            }
            public Builder url(String url) {
                params.url = url;
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

    @Override
    protected AppCMSPageUI doInBackground(Params... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0].context, params[0].url);
            } catch (IOException e) {
                Log.e(TAG, "Could not retrieve Page UI data - " + params[0] + ": " + e.toString());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(AppCMSPageUI result) {
        Observable.just(result).subscribe(readyAction);
    }
}
