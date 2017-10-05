package com.viewlift.models.network.rest;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;

import com.viewlift.R;

import net.nightwhistler.htmlspanner.TextUtil;

import retrofit2.Response;

/**
 * Created by viewlift on 5/9/17.
 */

public class AppCMSPageAPICall {
    private static final String TAG = "AppCMSPageAPICall";

    private final AppCMSPageAPIRest appCMSPageAPIRest;
    private final String apiKey;
    private final Gson gson;
    private final File storageDirectory;
    private Map<String, String> headersMap;
    private String url;

    @Inject
    public AppCMSPageAPICall(AppCMSPageAPIRest appCMSPageAPIRest,
                             String apiKey,
                             Gson gson,
                             File storageDirectory) {
        this.appCMSPageAPIRest = appCMSPageAPIRest;
        this.apiKey = apiKey;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
        this.headersMap = new HashMap<>();
    }

    @WorkerThread
    public AppCMSPageAPI call(String urlWithContent,
                              String authToken,
                              String pageId,
                              int tryCount) throws IOException {
        Log.d(TAG, "URL: " + urlWithContent);
        String filename = getResourceFilename(pageId);
        AppCMSPageAPI appCMSPageAPI = null;
        try {
            headersMap.clear();
            if (!TextUtils.isEmpty(apiKey)) {
                headersMap.put("x-api-key", apiKey);
            }
            if (!TextUtils.isEmpty(authToken)) {
                headersMap.put("Authorization", authToken);
            }
            Log.d(TAG, "AppCMSPageAPICall Authorization val "+headersMap.toString());
            Response<AppCMSPageAPI> response = appCMSPageAPIRest.get(urlWithContent, headersMap).execute();
            appCMSPageAPI = response.body();

            if (!response.isSuccessful()) {
                Log.e(TAG, "Response error: " + response.errorBody().string());
            }

            if (filename != null) {
                appCMSPageAPI = writePageToFile(filename, appCMSPageAPI);
            }
        } catch (JsonSyntaxException e) {
            Log.w(TAG, "Error trying to parse input JSON " + urlWithContent + ": " + e.toString());
        } catch (Exception e) {
            Log.e(TAG, "A serious network error has occurred: " + e.getMessage());
        }

        if (appCMSPageAPI == null && tryCount == 0) {
            return call(urlWithContent,
                    authToken,
                    pageId,
                    tryCount + 1);
        }

        return appCMSPageAPI;
    }

    private AppCMSPageAPI writePageToFile(String outputFilename,
                                          AppCMSPageAPI appCMSPageAPI) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() + outputFilename));
        String output = gson.toJson(appCMSPageAPI, AppCMSPageAPI.class);
        outputStream.write(output.getBytes());
        outputStream.close();
        return appCMSPageAPI;
    }

    private String getResourceFilename(String pageId) {
        if (!TextUtils.isEmpty(pageId)) {
            final String API_SUFFIX = "_API";
            final String JSON_EXT = ".json";
            int startIndex = pageId.lastIndexOf("/");
            if (startIndex >= 0) {
                startIndex += 1;
            } else {
                startIndex = 0;
            }
            return pageId.substring(startIndex) + API_SUFFIX + JSON_EXT;
        }
        return null;
    }
}
