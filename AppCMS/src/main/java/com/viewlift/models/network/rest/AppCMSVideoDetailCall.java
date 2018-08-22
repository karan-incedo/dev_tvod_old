package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.api.AppCMSEntitlementResponse;
import com.viewlift.models.data.appcms.api.AppCMSRentalAPIResponse;
import com.viewlift.models.data.appcms.api.AppCMSSignedURLResult;
import com.viewlift.models.data.appcms.api.AppCMSTransactionDataValue;
import com.viewlift.models.data.appcms.api.AppCMSVideoDetail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.Headers;
import retrofit2.Call;
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
    public AppCMSVideoDetail call(String url, String authToken, String xApi) throws IOException {
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
    public AppCMSRentalAPIResponse callRentalApiData(String url, String authToken, String xApi) throws IOException {
        try {

            url = "https://release-api.viewlift.com/transaction/changeStatus?userId=e94c0540-942e-11e8-9176-2de6c1a4d094&videoId=cb89adef-b261-4d20-8875-b7f0848849b3";
            authHeaders.clear();
            authHeaders.put("Authorization", authToken);
//            authHeaders.put("x-api-key", xApi);
            Call<AppCMSRentalAPIResponse> rentalVideoRespose = appCMSVideoDetailRest.getRentalVideoRespose(url, authHeaders);
            Response<AppCMSRentalAPIResponse> execute = rentalVideoRespose.execute();
            AppCMSRentalAPIResponse body = execute.body();
            return body;
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "DialogType parsing input JSON - " + url + ": " + e.toString());
        } catch (Exception e) {
            // e.printStackTrace();
            Log.e(TAG, "Network error retrieving site data - " + url + ": " + e.toString());
        }
        return null;
    }

    @WorkerThread
    public List<Map<String, AppCMSTransactionDataValue>> callTransactionalData(String url, String authToken, String xApi) throws IOException {
        try {

            authHeaders.clear();
            authHeaders.put("Authorization", authToken);
//            authHeaders.put("x-api-key", xApi);
            return appCMSVideoDetailRest.getTransactionDataResponse(url, authHeaders).execute().body();
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
            Response<AppCMSEntitlementResponse> response = appCMSVideoDetailRest.getEntitlementVideo(url, authHeaders).execute();
            Headers headers = response.headers();

            if (response.isSuccessful()) {
                AppCMSEntitlementResponse appCMSEntitlementResponse = response.body();

                if (headers != null) {
                    AppCMSSignedURLResult appCMSSignedURLResult = new AppCMSSignedURLResult();
                    for (String cookie : headers.values("Set-Cookie")) {
                        if (cookie.contains("CloudFront-Key-Pair-Id=")) {
                            appCMSSignedURLResult.setKeyPairId(cookie.substring("CloudFront-Key-Pair-Id=".length()));
                        } else if (cookie.contains("CloudFront-Signature=")) {
                            appCMSSignedURLResult.setSignature(cookie.substring("CloudFront-Signature=".length()));
                        } else if (cookie.contains("CloudFront-Policy=")) {
                            appCMSSignedURLResult.setPolicy(cookie.substring("CloudFront-Policy=".length()));
                        }
                    }
                    appCMSEntitlementResponse.setAppCMSSignedURLResult(appCMSSignedURLResult);
                }

                return appCMSEntitlementResponse;
            } else if (response.code() != 200) {
                try {
                    AppCMSEntitlementResponse appCMSEntitlementResponse =
                            new Gson().fromJson(response.errorBody().string(),
                                    AppCMSEntitlementResponse.class);
                    appCMSEntitlementResponse.setCode(response.code());
                    return appCMSEntitlementResponse;
                } catch (Exception e) {
                    AppCMSEntitlementResponse statusResponse = new AppCMSEntitlementResponse();
                    statusResponse.setCode(response.code());
                    statusResponse.setSuccess(false);
                    return statusResponse;
                }
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