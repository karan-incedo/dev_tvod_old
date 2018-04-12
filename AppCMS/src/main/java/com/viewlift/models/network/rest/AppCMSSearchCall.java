package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.viewlift.models.data.appcms.search.AppCMSSearchResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by viewlift on 6/12/17.
 */

public class AppCMSSearchCall {
    private static final String TAG = "AppCMSSearchCall";

    private final AppCMSSearchRest appCMSSearchRest;

    private Map<String, String> authHeaders;

    @Inject
    public AppCMSSearchCall(AppCMSSearchRest appCMSSearchRest) {
        this.appCMSSearchRest = appCMSSearchRest;
        this.authHeaders = new HashMap<>();
    }

    @WorkerThread
    public List<AppCMSSearchResult> call(String apiKey, String url) throws IOException {
        try {
            authHeaders.clear();
            authHeaders.put("x-api-key", apiKey);
            //Log.e(TAG, "search url -" + url  );
            //Log.e(TAG, "search url api key -" + apiKey  );

            return appCMSSearchRest.get(authHeaders, url).execute().body();
        } catch (Exception e) {
            Log.e(TAG, "Failed to execute search query " + url + ": " + e.toString());
        }
        return null;
    }
}
