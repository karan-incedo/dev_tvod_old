package com.viewlift.models.network.background.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidUI;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/4/17.
 */

public class GetAppCMSAndroidUIAsyncTask extends AsyncTask<GetAppCMSAndroidUIAsyncTask.Params, Integer, AppCMSAndroidUI> {
    private static final String TAG = "AndroidAsyncTask";

    private final AppCMSAndroidUICall call;
    private final Action1<AppCMSAndroidUI> readyAction;

    public static class Params {
        String url;
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
            public Builder loadFromFile(boolean loadFromFile) {
                params.loadFromFile = loadFromFile;
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

    @Override
    protected AppCMSAndroidUI doInBackground(Params... params) {
        if (params.length > 0) {
            try {
                return call.call(params[0].url, params[0].loadFromFile, 0);
            } catch (IOException e) {
                Log.e(TAG, "Error retrieving AppCMS Android file with params " +
                        params.toString() + ": " +
                        e.getMessage());
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(AppCMSAndroidUI result) {
        Observable.just(result).subscribe(readyAction);
    }
}
