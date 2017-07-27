package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.api.AppCMSVideoDetail;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by anas.azeem on 7/13/2017.
 * Owned by ViewLift, NYC
 */

public class AppCMSVideoDetailCall {
    private static final String TAG = "VideoDetailCall";

    private final AppCMSVideoDetailRest appCMSVideoDetailRest;

    @Inject
    public AppCMSVideoDetailCall(AppCMSVideoDetailRest appCMSVideoDetailRest) {
        this.appCMSVideoDetailRest = appCMSVideoDetailRest;
    }

    @WorkerThread
    public AppCMSVideoDetail call(String url) throws IOException {
        try {
            Log.d(TAG, "Attempting to read Video Detail JSON: " + url);
            return appCMSVideoDetailRest.get(url).execute().body();
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "DialogType parsing input JSON - " + url + ": " + e.toString());
        } catch (Exception e) {
            Log.e(TAG, "Network error retrieving site data - " + url + ": " + e.toString());
        }
        return null;
    }
}
