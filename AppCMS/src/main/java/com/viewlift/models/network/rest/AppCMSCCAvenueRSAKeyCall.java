package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.viewlift.models.data.appcms.ccavenue.RSAKeyBody;
import com.viewlift.models.data.appcms.ccavenue.RSAKeyResponse;
import com.viewlift.models.data.appcms.sslcommerz.SSLInitiateBody;
import com.viewlift.models.data.appcms.sslcommerz.SSLInitiateResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSCCAvenueRSAKeyCall {

    private static final String TAG = AppCMSCCAvenueRSAKeyCall.class.getSimpleName() + "TAG";
    private final AppCMSCCAvenueRSAKeyRest appCMSCCAvenueRSAKeyRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;
    private Map<String, String> headersMap;

    @Inject
    public AppCMSCCAvenueRSAKeyCall(AppCMSCCAvenueRSAKeyRest appCMSCCAvenueRSAKeyRest, Gson gson) {
        this.appCMSCCAvenueRSAKeyRest = appCMSCCAvenueRSAKeyRest;
        this.gson = gson;
        this.headersMap = new HashMap<>();
    }

    @WorkerThread
    public void call(String url,
                     final Action1<RSAKeyResponse> rsaKeyResponseAction1,
                     String apiKey, String authToken,
                     String planId, String site, String userId) {
        try {
            headersMap.clear();
            headersMap.put("x-api-key", apiKey);
            headersMap.put("Authorization", authToken);

            RSAKeyBody rsaKeyBody = new RSAKeyBody();
            rsaKeyBody.setSite(site);
            rsaKeyBody.setUserId(userId);
            rsaKeyBody.setDevice("android_phone");
            rsaKeyBody.setPlanId(planId);
            appCMSCCAvenueRSAKeyRest.obtainRSAKey(url, rsaKeyBody, headersMap).enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    RSAKeyResponse rsaKeyResponse = null;
                    JsonElement signInResponse = response.body();
                    rsaKeyResponse = gson.fromJson(signInResponse, RSAKeyResponse.class);
                    Observable.just(rsaKeyResponse).subscribe(rsaKeyResponseAction1);
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {

                }
            });


        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
    }
}
