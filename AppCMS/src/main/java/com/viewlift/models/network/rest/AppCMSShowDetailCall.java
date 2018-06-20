package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.api.AppCMSShowDetail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by anasazeem on 15/06/18.
 */

public class AppCMSShowDetailCall {
    private static final String TAG = "AppCMSShowDetailCall";

    private final AppCMSShowDetailRest appCMSShowDetailRest;
    private Map<String, String> authHeaders;

    @Inject
    public AppCMSShowDetailCall(AppCMSShowDetailRest appCMSShowDetailRest) {
        this.appCMSShowDetailRest = appCMSShowDetailRest;
        this.authHeaders = new HashMap<>();
    }

    @WorkerThread
    public AppCMSShowDetail call(String url, String authToken, String xApi) throws IOException {
        try {

            authHeaders.clear();
            authHeaders.put("Authorization", authToken);
            authHeaders.put("x-api-key", xApi);
            return appCMSShowDetailRest.get(url, authHeaders).execute().body();
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "DialogType parsing input JSON - " + url + ": " + e.toString());
        } catch (Exception e) {
            // e.printStackTrace();
            Log.e(TAG, "Network error retrieving site data - " + url + ": " + e.toString());
        }
        return null;
    }

}
