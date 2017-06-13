package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.search.AppCMSSearchResult;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by viewlift on 6/12/17.
 */

public class AppCMSSearchCall {
    private final AppCMSSearchRest appCMSSearchRest;
    private final Gson gson;

    @Inject
    public AppCMSSearchCall(AppCMSSearchRest appCMSSearchRest, Gson gson) {
        this.appCMSSearchRest = appCMSSearchRest;
        this.gson = gson;
    }

    @WorkerThread
    public List<AppCMSSearchResult> call(String url) throws IOException {
        return appCMSSearchRest.get(url).execute().body();
    }
}
