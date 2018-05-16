package com.viewlift.models.network.rest;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.ui.authentication.ErrorResponse;
import com.viewlift.models.data.appcms.ui.authentication.SignInRequest;
import com.viewlift.models.data.appcms.ui.authentication.SignInResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by viewlift on 7/5/17.
 */

public class AppCMSSignInCall {
    private static final String TAG = "AppCMSSignin";

    private final AppCMSSignInRest appCMSSignInRest;
    private final Gson gson;
    private Map<String, String> headersMap;

    @Inject
    public AppCMSSignInCall(AppCMSSignInRest appCMSSignInRest, Gson gson) {
        this.appCMSSignInRest = appCMSSignInRest;
        this.gson = gson;
        this.headersMap = new HashMap<>();
    }

    public SignInResponse call(String url, String email, String password, String apiKey) {
        SignInResponse loggedInResponseResponse = null;

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail(email);
        signInRequest.setPassword(password);
        headersMap.clear();
        if (!TextUtils.isEmpty(apiKey)) {
            headersMap.put("x-api-key", apiKey);
        }

        try {
            Call<JsonElement> call = appCMSSignInRest.signin(url, signInRequest, headersMap);

            Response<JsonElement> response = call.execute();
            if (response.body() != null) {
                JsonElement signInResponse = response.body();
                //Log.d(TAG, "Raw response: " + signInResponse.toString());
                loggedInResponseResponse = gson.fromJson(signInResponse, SignInResponse.class);
            } else if (response.errorBody() != null) {
                String errorResponse = response.errorBody().string();
                //Log.d(TAG, "Raw response: " + errorResponse);
                loggedInResponseResponse = new SignInResponse();
                if (errorResponse != null) {
                    loggedInResponseResponse.setErrorResponse(gson.fromJson(errorResponse, ErrorResponse.class));
                }
            }
        } catch (JsonSyntaxException | IOException e) {
            //Log.e(TAG, "SignIn error: " + e.toString());
        }

        return loggedInResponseResponse;
    }
}
