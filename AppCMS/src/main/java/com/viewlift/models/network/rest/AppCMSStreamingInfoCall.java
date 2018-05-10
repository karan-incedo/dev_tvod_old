package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;

import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.api.AppCMSStreamingInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    public AppCMSStreamingInfo call(String url, String xApiKey) throws IOException {
        try {

            Map<String, String> authTokenMap = new HashMap<>();
            authTokenMap.put("x-api-key", xApiKey);

            //Log.d(TAG, "Attempting to read Streaming Info JSON: " + url);
            return appCMSStreamingInfoRest.get(url, authTokenMap).execute().body();
        } catch (JsonSyntaxException e) {
            //Log.e(TAG, "DialogType parsing input JSON - " + url + ": " + e.toString());
        } catch (Exception e) {
            //Log.e(TAG, "Network error retrieving site data - " + url + ": " + e.toString());
        }
        return null;
    }
}
