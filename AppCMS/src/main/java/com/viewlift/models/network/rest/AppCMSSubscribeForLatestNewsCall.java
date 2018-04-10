package com.viewlift.models.network.rest;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.subscribeForLatestNewsPojo.ResponsePojo;
import com.viewlift.models.data.appcms.subscribeForLatestNewsPojo.SubscribeGoRequest;
import com.viewlift.models.data.appcms.subscribeForLatestNewsPojo.UserExist;
import com.viewlift.models.data.appcms.ui.authentication.ErrorResponse;
import com.viewlift.models.data.appcms.ui.authentication.SignInResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by artanelezaj on 4/1/18.
 */

public class AppCMSSubscribeForLatestNewsCall {
    private static final String TAG = "AppCMSSubscribeForLatestNewsCall";

    private final AppCMSSubscribeForLatestNewsRest appCMSSubscribeForLatestNewsRest;
    private final Gson gson;
    private Map<String, String> authHeaders;

    private String url = "https://us17.api.mailchimp.com/3.0/lists/63bfcdbf39/members/";
    private String apiKey = "73c26e8e956a822c67e19e34ab817e09-us17";

    @Inject
    public AppCMSSubscribeForLatestNewsCall(AppCMSSubscribeForLatestNewsRest appCMSSubscribeForLatestNewsRest, Gson gson) {
        this.appCMSSubscribeForLatestNewsRest = appCMSSubscribeForLatestNewsRest;
        this.gson = gson;
        this.authHeaders = new HashMap<>();
    }

    public ResponsePojo call(String email) {
        String auth = "AnyString:" + apiKey;

        authHeaders.put("Authorization", "Basic " + android.util.Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP));

        SubscribeGoRequest subscribeRequest = new SubscribeGoRequest();
        subscribeRequest.setEmailAddress(email);
        subscribeRequest.setStatus("subscribed");

        ResponsePojo responsePojo = null;
        try {
            Call<JsonElement> call = appCMSSubscribeForLatestNewsRest.subscribe(authHeaders, url, subscribeRequest);

            Response<JsonElement> response = call.execute();
            if (response.body() != null) {
                JsonElement responseData = response.body();
                responsePojo = gson.fromJson(responseData, ResponsePojo.class);
            } else if (response.errorBody() != null) {
                String errorResponse = response.errorBody().string();
                System.out.println("");
                UserExist userExist = gson.fromJson(errorResponse, UserExist.class);
                responsePojo = new ResponsePojo();
                responsePojo.setUserExist(userExist);
            }
        } catch (JsonSyntaxException | IOException e) {
            //Log.e(TAG, "SignIn error: " + e.toString());
        }
        return responsePojo;
    }
}