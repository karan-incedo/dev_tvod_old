package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.search.AppCMSSearchResult;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by viewlift on 6/12/17.
 */

public class AppCMSSearchCall {
    private static final String TAG = "AppCMSSearchCall";

    private final AppCMSSearchRest appCMSSearchRest;

    @Inject
    public AppCMSSearchCall(AppCMSSearchRest appCMSSearchRest) {
        this.appCMSSearchRest = appCMSSearchRest;
    }

    @WorkerThread
    public List<AppCMSSearchResult> call(String url) throws IOException {
        try {
            return appCMSSearchRest.get(url).execute().body();
        } catch (Exception e) {
            Log.e(TAG, "Failed to execute search query " + url + ": " + e.toString());
        }
        return null;
    }
}
