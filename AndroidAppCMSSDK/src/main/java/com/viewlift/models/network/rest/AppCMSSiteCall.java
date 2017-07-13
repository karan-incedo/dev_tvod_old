package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.sites.AppCMSSite;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by viewlift on 6/15/17.
 */

public class AppCMSSiteCall {
    private static final String TAG = "AppCMSSiteCall";

    private final AppCMSSiteRest appCMSSiteRest;

    @Inject
    public AppCMSSiteCall(AppCMSSiteRest appCMSSiteRest) {
        this.appCMSSiteRest = appCMSSiteRest;
    }

    @WorkerThread
    public AppCMSSite call(String url) throws IOException {
        try {
            Log.d(TAG, "Attempting to retrieve site JSON: " + url);
            return appCMSSiteRest.get(url).execute().body();
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "DialogType parsing input JSON - " + url + ": " + e.toString());
        } catch (Exception e) {
            Log.e(TAG, "Network error retrieving site data - " + url + ": " + e.toString());
        }
        return null;
    }
}
