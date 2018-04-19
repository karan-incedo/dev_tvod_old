package com.viewlift.models.network.rest;

/*
 * Created by Viewlift on 6/28/2017.
 */

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.sslcommerz.SSLCredential;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

public class AppCMSSSLCommerzConfigCall {

    private static final String TAG = AppCMSSSLCommerzConfigCall.class.getSimpleName() + "TAG";
    private final AppCMSSSLCommerzConfigRest appCMSSSLCommerzConfigRest;

    @SuppressWarnings({"unused, FieldCanBeLocal"})
    private final Gson gson;
    private Map<String, String> authHeaders;
    @Inject
    public AppCMSSSLCommerzConfigCall(AppCMSSSLCommerzConfigRest appCMSSSLCommerzConfigRest, Gson gson) {
        this.appCMSSSLCommerzConfigRest = appCMSSSLCommerzConfigRest;
        this.gson = gson;
        this.authHeaders = new HashMap<>();
    }

    @WorkerThread
    public void call(String url, String authToken,
                     final Action1<SSLCredential> sslConfigAction) throws IOException {
        authHeaders.clear();
        if (!TextUtils.isEmpty(authToken)) {
            authHeaders.put("Authorization", authToken);
        }
        try {
            appCMSSSLCommerzConfigRest.getConfig(url,authHeaders).enqueue(new Callback<SSLCredential>() {
                @Override
                public void onResponse(@NonNull Call<SSLCredential> call,
                                       @NonNull Response<SSLCredential> response) {
                    if (response.body() == null)
                        sslConfigAction.call(null);
                    else
                        Observable.just(response.body()).subscribe(sslConfigAction);

                }

                @Override
                public void onFailure(@NonNull Call<SSLCredential> call,
                                      @NonNull Throwable t) {
                    //Log.e(TAG, "onFailure: " + t.getMessage());
                    sslConfigAction.call(null);
                }
            });
        } catch (Exception e) {
            //Log.e(TAG, "Failed to execute watchlist " + url + ": " + e.toString());
        }
    }
}
