package com.viewlift.models.network.rest;

import android.text.TextUtils;
import android.util.Log;

import com.viewlift.models.data.appcms.api.IPGeoLocatorResponse;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 8/1/17.
 */

public class AppCMSIPGeoLocatorCall {
    private static final String TAG = "IPGeoLocator";

    private final AppCMSIPGeoLocatorRest appCMSIPGeoLocatorRest;

    private Map<String, String> authHeaders;

    @Inject
    public AppCMSIPGeoLocatorCall(AppCMSIPGeoLocatorRest appCMSIPGeoLocatorRest) {
        this.appCMSIPGeoLocatorRest = appCMSIPGeoLocatorRest;
        this.authHeaders = new HashMap<>();
    }

    public void call(String url,
                     String authToken,
                     String apiKey,
                     Action1<IPGeoLocatorResponse> readyAction) {
        authHeaders.clear();
        if (!TextUtils.isEmpty(authToken) && !TextUtils.isEmpty(apiKey)) {
            authHeaders.put("Authorization", authToken);
            authHeaders.put("x-api_key", apiKey);
        }

        try {
            appCMSIPGeoLocatorRest.get(url, authHeaders).enqueue(new Callback<IPGeoLocatorResponse>() {
                @Override
                public void onResponse(Call<IPGeoLocatorResponse> call, Response<IPGeoLocatorResponse> response) {
                    Observable.just(response.body())
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(readyAction);
                }

                @Override
                public void onFailure(Call<IPGeoLocatorResponse> call, Throwable t) {
                    //Log.e(TAG, "Failed to retrieve IP based Geolocation: " + t.getMessage());
                    Observable.just((IPGeoLocatorResponse) null)
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(readyAction);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to retrieve IP based Geolocation: " + e.toString());
            Observable.just((IPGeoLocatorResponse) null)
                    .onErrorResumeNext(throwable -> Observable.empty())
                    .subscribe(readyAction);
        }
    }
}
