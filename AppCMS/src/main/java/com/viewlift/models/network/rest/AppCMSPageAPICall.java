package com.viewlift.models.network.rest;

import android.content.Context;
import android.support.annotation.WorkerThread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/9/17.
 */

public class AppCMSPageAPICall {
    private final AppCMSPageAPIRest appCMSPageAPIRest;
    private final String apiKey;
    private final Gson gson;
    private final File storageDirectory;

    @Inject
    public AppCMSPageAPICall(AppCMSPageAPIRest appCMSPageAPIRest,
                             String apiKey,
                             Gson gson,
                             File storageDirectory) {
        this.appCMSPageAPIRest = appCMSPageAPIRest;
        this.apiKey = apiKey;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
    }

    @WorkerThread
    public AppCMSPageAPI call(Context context, String url) throws IOException {
        String urlWithContent = context.getString(R.string.app_cms_page_api_url, url);
        String filename = getResourceFilename(url);
        return writeMainToFile(filename,
                appCMSPageAPIRest.get(apiKey, urlWithContent).execute().body());
    }

    private AppCMSPageAPI writeMainToFile(String outputFilename,
                                          AppCMSPageAPI appCMSPageAPI) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() + outputFilename));
        String output = gson.toJson(appCMSPageAPI, AppCMSPageAPI.class);
        outputStream.write(output.getBytes());
        outputStream.close();
        return appCMSPageAPI;
    }

    private String getResourceFilename(String url) {
        final String JSON_EXT = ".json";
        final String PAGE_ID_PARAM = "pageId=";
        int startIndex = url.indexOf(PAGE_ID_PARAM) + PAGE_ID_PARAM.length();
        if (0 <= startIndex) {
            return url.substring(startIndex+1, url.length()) + JSON_EXT;
        }
        return url;
    }
}
