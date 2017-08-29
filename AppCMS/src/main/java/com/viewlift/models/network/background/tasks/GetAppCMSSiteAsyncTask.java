package com.viewlift.models.network.background.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.viewlift.models.data.appcms.sites.AppCMSSite;
import com.viewlift.models.network.rest.AppCMSSiteCall;

import java.io.IOException;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 6/15/17.
 */

public class GetAppCMSSiteAsyncTask extends AsyncTask<String, Integer, AppCMSSite> {
    private static final String TAG = "GetAppCMSSiteAsyncTask";

    private final AppCMSSiteCall call;
    private final Action1<AppCMSSite> readyAction;

    public GetAppCMSSiteAsyncTask(AppCMSSiteCall call,
                                  Action1<AppCMSSite> readyAction) {
        this.call = call;
        this.readyAction = readyAction;
    }

    @Override
    protected AppCMSSite doInBackground(String... params) {
        if (params.length >= 1) {
            try {
                return call.call(params[0], 0);
            } catch (IOException e) {
                Log.e(TAG, "Could not retrieve Site data - " + params[0] + ": " + e.toString());
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(AppCMSSite result) {
        Observable.just(result).subscribe(readyAction);
    }
}
