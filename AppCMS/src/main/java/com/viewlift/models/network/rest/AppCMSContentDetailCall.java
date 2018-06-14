package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;

import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.api.AppCMSContentDetail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by anas.azeem on 3/12/2018.
 * Owned by ViewLift, NYC
 */

public class AppCMSContentDetailCall {
    private static final String TAG = "AppCMSContentDetailCall";

    private final AppCMSContentDetailRest appCMSContentDetailRest;
    private Map<String, String> authHeaders;

    @Inject
    public AppCMSContentDetailCall(AppCMSContentDetailRest appCMSConentDetailRest) {
        this.appCMSContentDetailRest = appCMSConentDetailRest;
        this.authHeaders = new HashMap<>();
    }

    @WorkerThread
    public AppCMSContentDetail call(String url, String authToken,String xApi) throws IOException {
        try {
            //Log.d(TAG, "Attempting to read Video Detail JSON: " + url);
            authHeaders.clear();
            authHeaders.put("Authorization", authToken);
            authHeaders.put("x-api-key", xApi);
            return appCMSContentDetailRest.get(url, authHeaders).execute().body();
        } catch (JsonSyntaxException e) {
            //Log.e(TAG, "DialogType parsing input JSON - " + url + ": " + e.toString());
            e.printStackTrace();
        } catch (Exception e) {
             e.printStackTrace();
            //Log.e(TAG, "Network error retrieving site data - " + url + ": " + e.toString());
        }
        return null;
    }
}

