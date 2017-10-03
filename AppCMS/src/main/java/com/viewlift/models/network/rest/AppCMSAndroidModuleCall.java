package com.viewlift.models.network.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.ModuleList;

import org.json.JSONException;

import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 10/3/17.
 */

public class AppCMSAndroidModuleCall {
    private static final String TAG = "AndroidModuleCall";

    private final Gson gson;
    private final AppCMSAndroidModuleRest appCMSAndroidModuleRest;

    @Inject
    public AppCMSAndroidModuleCall(Gson gson,
                                   AppCMSAndroidModuleRest appCMSAndroidModuleRest) {
        this.gson = gson;
        this.appCMSAndroidModuleRest = appCMSAndroidModuleRest;
    }

    public void call(String url, Action1<AppCMSAndroidModules> readyAction) {
        Log.d(TAG, "Retrieving list of modules at URL: " + url);
        appCMSAndroidModuleRest.get(url).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response != null) {
                    Log.d(TAG, "Received a valid response");
                    AppCMSAndroidModules appCMSAndroidModules = new AppCMSAndroidModules();
                    Map<String, ModuleList> moduleListMap = gson.fromJson(response.body(),
                            new TypeToken<Map<String, ModuleList>>(){}.getType());
                    appCMSAndroidModules.setModuleListMap(moduleListMap);
                    Observable.just(appCMSAndroidModules).subscribe(readyAction);
                } else {
                    Log.e(TAG, "Received an error response");
                    Observable.just((AppCMSAndroidModules) null).subscribe(readyAction);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Log.e(TAG, "Could not retrieve data");
                Observable.just((AppCMSAndroidModules) null).subscribe(readyAction);
            }
        });
    }
}
