package air.com.snagfilms.models.network.rest;

import android.support.annotation.WorkerThread;

import com.google.gson.JsonElement;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by viewlift on 5/9/17.
 */

public class AppCMSPageAPICall {
    private final AppCMSPageAPI appCMSPageAPI;
    private final String apiKey;

    @Inject
    public AppCMSPageAPICall(AppCMSPageAPI appCMSPageAPI, String apiKey) {
        this.appCMSPageAPI = appCMSPageAPI;
        this.apiKey = apiKey;
    }

    @WorkerThread
    public JsonElement call(String url) throws IOException {
        return appCMSPageAPI.get(apiKey, url).execute().body();
    }
}
