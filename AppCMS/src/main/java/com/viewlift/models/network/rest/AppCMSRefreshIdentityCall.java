package com.viewlift.models.network.rest;

import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.ui.authentication.RefreshIdentityResponse;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by viewlift on 7/5/17.
 */

public class AppCMSRefreshIdentityCall {
    private static final String TAG = "AppCMSRefreshIdentity";

    private final AppCMSRefreshIdentityRest appCMSRefreshIdentityRest;

    @Inject
    public AppCMSRefreshIdentityCall(AppCMSRefreshIdentityRest appCMSRefreshIdentityRest) {
        this.appCMSRefreshIdentityRest = appCMSRefreshIdentityRest;
    }

    public RefreshIdentityResponse call(String url) {
        try {
            return appCMSRefreshIdentityRest.get(url).execute().body();
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "JsonSyntaxException retrieving Refresh Identity Response: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "IO error retrieving Refresh Identity Response: " + e.toString());
        }
        return null;
    }
}
