package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.watchlist.AppCMSWatchlistResult;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

public class AppCMSWatchlistCall {

    private static final String TAG = "AppCMSWatchlistCall";
    private final AppCMSWatchlistRest appCMSWatchlistRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSWatchlistCall(AppCMSWatchlistRest appCMSWatchlistRest, Gson gson) {
        this.appCMSWatchlistRest = appCMSWatchlistRest;
        this.gson = gson;
    }

    @WorkerThread
    public List<AppCMSWatchlistResult> call(String url) throws IOException {
        try {
            return appCMSWatchlistRest.get(url).execute().body();
        } catch (Exception e) {
            Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
        return null;
    }
}
