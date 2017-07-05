package com.viewlift.models.network.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.ui.authentication.SignInError;
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
        JsonElement signInResponse = null;
        try {
            Call<JsonElement> call = appCMSSignInRest.signin(url, signInRequest);
            Response<JsonElement> response = call.execute();
            signInResponse = response.body();
            loggedInResponseResponse = gson.fromJson(signInResponse, SignInResponse.class);
        } catch (JsonSyntaxException | IOException e) {
            Log.e(TAG, "SignIn error: " + e.toString());
            try {
                SignInError signInError = gson.fromJson(signInResponse, SignInError.class);
            } catch (JsonSyntaxException e1) {
                Log.e(TAG, "Json Parsing Exception: " + e1.toString());
            }
        }

        return loggedInResponseResponse;
    }
}
