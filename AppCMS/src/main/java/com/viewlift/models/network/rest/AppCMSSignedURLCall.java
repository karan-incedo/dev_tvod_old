package com.viewlift.models.network.rest;

import android.util.Log;

import com.viewlift.models.data.appcms.api.AppCMSSignedURLResult;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by viewlift on 10/10/17.
 */

public class AppCMSSignedURLCall {
    private static final String TAG = "SignedURLCall";

    private final AppCMSSignedURLRest appCMSSignedURLRest;

    private Map<String, String> authHeaders;

    @Inject
    public AppCMSSignedURLCall(AppCMSSignedURLRest appCMSSignedURLRest) {
        this.appCMSSignedURLRest = appCMSSignedURLRest;
        this.authHeaders = new HashMap<>();
    }

    public AppCMSSignedURLResult call(String authToken, String url) {
        authHeaders.put("Authorization", authToken);
        try {
            Log.d(TAG, "Auth token: " + authToken);
            Log.d(TAG, "URL: " + url);
            return appCMSSignedURLRest.get(url, authHeaders).execute().body();
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve signed URL response: " +
                e.getMessage());
        }
        return null;
    }
}
