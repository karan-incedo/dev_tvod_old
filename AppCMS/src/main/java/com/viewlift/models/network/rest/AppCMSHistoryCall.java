package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 7/5/17.
 */

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.history.AppCMSHistoryResult;

import java.io.IOException;

import javax.inject.Inject;

public class AppCMSHistoryCall {
    private static final String TAG = "AppCMSHistoryCall";
    private final AppCMSHistoryRest appCMSHistoryRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSHistoryCall(AppCMSHistoryRest appCMSHistoryRest, Gson gson) {
        this.appCMSHistoryRest = appCMSHistoryRest;
        this.gson = gson;
    }

    @WorkerThread
    public AppCMSHistoryResult call(String url) throws IOException {
        try {
            return appCMSHistoryRest.get(url).execute().body();
        } catch (Exception e) {
            Log.e(TAG, "Failed to execute history " + url + ": " + e.toString());
        }
        return null;
    }
}
