package com.viewlift.models.network.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.urbanairship.UAAssociateNamedUserRequest;
import com.viewlift.models.data.urbanairship.UAAssociateNamedUserResponse;
import com.viewlift.models.data.urbanairship.UANamedUserRequest;
import com.viewlift.models.data.urbanairship.UANamedUserResponse;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.fabric.sdk.android.services.network.HttpRequest;

/**
 * Created by viewlift on 12/21/17.
 */

public class UANamedUserEventCall {
    private static final String TAG = "UANamedUserReq";

    private final UANamedUserEventRest uaNamedUserEventRest;
    private final Gson gson;

    @Inject
    public UANamedUserEventCall(UANamedUserEventRest uaNamedUserEventRest,
                                Gson gson) {
        this.uaNamedUserEventRest = uaNamedUserEventRest;
        this.gson = gson;
    }

    public boolean sendLoginAssociation(String accessKey,
                                        String authKey,
                                        UAAssociateNamedUserRequest uaAssociateNamedUserRequest) {
        String rawJsonRequest = gson.toJson(uaAssociateNamedUserRequest);

        try {
            Map<String, String> headers = getRequestHeaders(accessKey, authKey);

            Log.i(TAG, "Request Headers: " + headers);
            Log.i(TAG, "Request Body: " + rawJsonRequest);

            UAAssociateNamedUserResponse uaAssociateNamedUserResponse =
                    uaNamedUserEventRest.associateLogin(headers, uaAssociateNamedUserRequest).execute().body();

            return uaAssociateNamedUserResponse.isOk();
        } catch (Exception e) {
            Log.e(TAG, "Failed to send UA named user request: " +
                    rawJsonRequest);
            Log.e(TAG, e.getMessage());
        }

        return false;
    }

    public boolean sendLogoutDisassociation(String accessKey,
                                            String authKey,
                                            UAAssociateNamedUserRequest uaAssociateNamedUserRequest) {
        String rawJsonRequest = gson.toJson(uaAssociateNamedUserRequest);

        try {
            Map<String, String> headers = getRequestHeaders(accessKey, authKey);

            Log.i(TAG, "Request Headers: " + headers);
            Log.i(TAG, "Request Body: " + rawJsonRequest);

            UAAssociateNamedUserResponse uaAssociateNamedUserResponse =
                    uaNamedUserEventRest.disassociateLogout(headers, uaAssociateNamedUserRequest).execute().body();

            return uaAssociateNamedUserResponse.isOk();
        } catch (Exception e) {
            Log.e(TAG, "Failed to send UA named user request: " +
                    rawJsonRequest);
            Log.e(TAG, e.getMessage());
        }

        return false;
    }

    public boolean call(String accessKey,
                        String authKey,
                        UANamedUserRequest uaNamedUserRequest) {
        String rawJsonRequest = gson.toJson(uaNamedUserRequest);

        try {
            Map<String, String> headers = getRequestHeaders(accessKey, authKey);

            Log.i(TAG, "Request Headers: " + headers);
            Log.i(TAG, "Request Body: " + rawJsonRequest);

            UANamedUserResponse uaNamedUserResponse =
                    uaNamedUserEventRest.post(headers, uaNamedUserRequest).execute().body();

            if (!uaNamedUserResponse.isOk()) {
                Log.w(TAG, uaNamedUserResponse.getWarnings().toString());
            }

            return uaNamedUserResponse.isOk();
        } catch (Exception e) {
            Log.e(TAG, "Failed to send UA named user request: " +
                    rawJsonRequest);
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    private String getBasicAuthHeaderValue(String accessKey, String authKey) {
        StringBuilder usernamePasswordSb = new StringBuilder();
        usernamePasswordSb.append(accessKey);
        usernamePasswordSb.append(":");
        usernamePasswordSb.append(authKey);
        StringBuilder basicAuthHeaderValueSb = new StringBuilder();
        basicAuthHeaderValueSb.append("Basic ");
        basicAuthHeaderValueSb.append(HttpRequest.Base64.encode(usernamePasswordSb.toString()));
        return basicAuthHeaderValueSb.toString();
    }

    private Map<String, String> getRequestHeaders(String accessKey, String authKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", getBasicAuthHeaderValue(accessKey, authKey));
        headers.put("Accept", "application/vnd.urbanairship+json; version=3;");
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
