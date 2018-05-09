package com.viewlift.models.network.rest;

import android.text.TextUtils;

import com.viewlift.models.data.appcms.api.AppCMSSignedURLResult;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.Headers;
import retrofit2.Response;

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

    public AppCMSSignedURLResult call(String authToken, String xApiKey, String url) {
        authHeaders.put("Authorization", authToken);
        authHeaders.put("x-api-key", xApiKey);

        try {
            //Log.d(TAG, "Auth token: " + authToken);
            //Log.d(TAG, "URL: " + url);
            Response<AppCMSSignedURLResult> appCMSSignedURLResultResponse =
                    appCMSSignedURLRest.get(url, authHeaders).execute();
            Headers headers = appCMSSignedURLResultResponse.headers();
            AppCMSSignedURLResult appCMSSignedURLResult = appCMSSignedURLResultResponse.body();
            if (appCMSSignedURLResult == null || TextUtils.isEmpty(appCMSSignedURLResult.getSigned())) {
                if (appCMSSignedURLResult == null) {
                    appCMSSignedURLResult = new AppCMSSignedURLResult();
                }
                for (String cookie : headers.values("Set-Cookie")) {
                    if (cookie.contains("CloudFront-Key-Pair-Id=")) {
                        appCMSSignedURLResult.setKeyPairId(cookie.substring("CloudFront-Key-Pair-Id=".length()));
                    } else if (cookie.contains("CloudFront-Signature=")) {
                        appCMSSignedURLResult.setSignature(cookie.substring("CloudFront-Signature=".length()));
                    } else if (cookie.contains("CloudFront-Policy=")) {
                        appCMSSignedURLResult.setPolicy(cookie.substring("CloudFront-Policy=".length()));
                    }
                }
            }
            return appCMSSignedURLResult;
        } catch (Exception e) {
            //Log.e(TAG, "Failed to retrieve signed URL response: " +
//                e.getMessage());
        }
        return null;
    }
}
