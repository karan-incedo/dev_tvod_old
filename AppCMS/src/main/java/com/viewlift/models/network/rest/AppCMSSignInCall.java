package com.viewlift.models.network.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.ui.authentication.ErrorResponse;
import com.viewlift.models.data.appcms.ui.authentication.SignInRequest;
import com.viewlift.models.data.appcms.ui.authentication.SignInResponse;

import java.io.IOException;

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

    @Inject
    public AppCMSSignInCall(AppCMSSignInRest appCMSSignInRest, Gson gson) {
        this.appCMSSignInRest = appCMSSignInRest;
        this.gson = gson;
    }

    public SignInResponse call(String url, String email, String password) {
        SignInResponse loggedInResponseResponse = null;

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail(email);
        signInRequest.setPassword(password);
        try {
            Call<JsonElement> call = appCMSSignInRest.signin(url, signInRequest);

            Response<JsonElement> response = call.execute();
            if (response.body() != null) {
                JsonElement signInResponse = response.body();
                Log.d(TAG, "Raw response: " + signInResponse.toString());
                loggedInResponseResponse = gson.fromJson(signInResponse, SignInResponse.class);
            } else if (response.errorBody() != null) {
                String errorResponse = response.errorBody().string();
                Log.d(TAG, "Raw response: " + errorResponse);
                loggedInResponseResponse = new SignInResponse();
                loggedInResponseResponse.setErrorResponse(gson.fromJson(errorResponse, ErrorResponse.class));
            }
        } catch (JsonSyntaxException | IOException e) {
            Log.e(TAG, "SignIn error: " + e.toString());
        }

        return loggedInResponseResponse;
    }
}
