package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.subscriptions.AppCMSValidatePlaySubRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by viewlift on 2/22/18.
 */

public class AppCMSValidatePlaySubCall {
    private static final String TAG = "AppCMSWatchlistCallTAG_";
    private final AppCMSValidatePlaySubRest appCMSValidatePlaySubRest;

    private Map<String, String> authHeaders;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;

    @Inject
    public AppCMSValidatePlaySubCall(AppCMSValidatePlaySubRest appCMSValidatePlaySubRest, Gson gson) {
        this.appCMSValidatePlaySubRest = appCMSValidatePlaySubRest;
        this.gson = gson;
        this.authHeaders = new HashMap<>();
    }

    @WorkerThread
    public void call(String url,
                     String authToken,
                     String xApiKey,
                     AppCMSValidatePlaySubRequest appCMSValidatePlaySubRequest) throws IOException {
        try {
            //Log.d(TAG, "Attempting to read Video Detail JSON: " + url);
            authHeaders.clear();
            authHeaders.put("Authorization", authToken);
            authHeaders.put("x-api-key", xApiKey);

            appCMSValidatePlaySubRest.validate(url,
                    authHeaders,
                    appCMSValidatePlaySubRequest).execute().body();
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "DialogType parsing input JSON - " + url + ": " + e.toString());
        } catch (Exception e) {
            // e.printStackTrace();
            Log.e(TAG, "Network error retrieving site data - " + url + ": " + e.toString());
        }
        return;
    }
}
