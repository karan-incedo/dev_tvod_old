package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.api.AppCMSStreamingInfo;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by viewlift on 6/26/17.
 */

public class AppCMSStreamingInfoCall {
    private static final String TAG = "StreamingInfoCall";

    private final AppCMSStreamingInfoRest appCMSStreamingInfoRest;

    @Inject
    public AppCMSStreamingInfoCall(AppCMSStreamingInfoRest appCMSStreamingInfoRest) {
        this.appCMSStreamingInfoRest = appCMSStreamingInfoRest;
    }

    @WorkerThread
    public AppCMSStreamingInfo call(String url) throws IOException {
        try {
            //Log.d(TAG, "Attempting to read Streaming Info JSON: " + url);
            return appCMSStreamingInfoRest.get(url).execute().body();
        } catch (JsonSyntaxException e) {
            //Log.e(TAG, "DialogType parsing input JSON - " + url + ": " + e.toString());
        } catch (Exception e) {
            //Log.e(TAG, "Network error retrieving site data - " + url + ": " + e.toString());
        }
        return null;
    }
}
