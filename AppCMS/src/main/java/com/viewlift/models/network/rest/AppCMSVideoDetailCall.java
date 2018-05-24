package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.api.AppCMSEntitlementResponse;
import com.viewlift.models.data.appcms.api.AppCMSVideoDetail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Response;

/**
 * Created by anas.azeem on 7/13/2017.
 * Owned by ViewLift, NYC
 */

public class AppCMSVideoDetailCall {
    private static final String TAG = "VideoDetailCall";

    private final AppCMSVideoDetailRest appCMSVideoDetailRest;
    private Map<String, String> authHeaders;

    @Inject
    public AppCMSVideoDetailCall(AppCMSVideoDetailRest appCMSVideoDetailRest) {
        this.appCMSVideoDetailRest = appCMSVideoDetailRest;
        this.authHeaders = new HashMap<>();
    }

    @WorkerThread
    public AppCMSVideoDetail call(String url, String authToken,String xApi) throws IOException {
        try {

            authHeaders.clear();
            authHeaders.put("Authorization", authToken);
            authHeaders.put("x-api-key", xApi);
            return appCMSVideoDetailRest.get(url, authHeaders).execute().body();
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "DialogType parsing input JSON - " + url + ": " + e.toString());
        } catch (Exception e) {
           // e.printStackTrace();
            Log.e(TAG, "Network error retrieving site data - " + url + ": " + e.toString());
        }
        return null;
    }

    @WorkerThread
    public AppCMSEntitlementResponse callEntitlementVideo(String url, String authToken, String xApi) throws IOException {
        try {
            //Log.d(TAG, "Attempting to read Video Detail JSON: " + url);
            authHeaders.clear();
            authHeaders.put("Authorization", authToken);
        //    authHeaders.put("x-api-key", xApi);
            Response<AppCMSEntitlementResponse> response= appCMSVideoDetailRest.getEntitlementVideo(url, authHeaders).execute();

            if(response.isSuccessful()){
                return response.body();
            }else if (response.code() != 200){
                AppCMSEntitlementResponse statusResponse = new AppCMSEntitlementResponse();
                statusResponse.setCode(response.code());
                statusResponse.setSuccess(false);
                return statusResponse;

            }


        } catch (JsonSyntaxException e) {
            Log.e(TAG, "DialogType parsing input JSON - " + url + ": " + e.toString());
        } catch (Exception e) {
             e.printStackTrace();
            Log.e(TAG, "Network error retrieving site data - " + url + ": " + e.toString());
        }
        return null;
    }
}
