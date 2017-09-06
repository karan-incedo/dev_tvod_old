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
    public AppCMSPageAPI call(Context context,
                              String baseUrl,
                              String endpoint,
                              String siteId,
                              String authToken,
                              String userId,
                              boolean usePageIdQueryParam,
                              String pageId,
                              boolean viewPlansPage,
                              int tryCount) throws IOException {
        String urlWithContent;
        if (usePageIdQueryParam) {
            if (viewPlansPage) {
                urlWithContent =
                        context.getString(R.string.app_cms_page_api_view_plans_url,
                                baseUrl,
                                endpoint,
                                siteId,
                                context.getString(R.string.app_cms_subscription_platform_key));
            } else {
                urlWithContent =
                        context.getString(R.string.app_cms_page_api_url,
                                baseUrl,
                                endpoint,
                                siteId,
                                context.getString(R.string.app_cms_page_id_query_parameter),
                                pageId,
                                userId);
            }
        } else {
            urlWithContent =
                    context.getString(R.string.app_cms_page_api_url,
                            baseUrl,
                            endpoint,
                            siteId,
                            context.getString(R.string.app_cms_page_path_query_parameter),
                            pageId,
                            userId);
        }
        Log.d(TAG, "URL: " + urlWithContent);
        String filename = getResourceFilename(pageId);
        AppCMSPageAPI appCMSPageAPI = null;
        try {
            headersMap.clear();
            if (!TextUtils.isEmpty(userId)) {
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
            return call(context,
                    baseUrl,
                    endpoint,
                    siteId,
                    authToken,
                    userId,
                    usePageIdQueryParam,
                    pageId,
                    viewPlansPage,
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
