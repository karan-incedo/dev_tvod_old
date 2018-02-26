package com.viewlift.models.network.rest;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by viewlift on 5/9/17.
 */

public class AppCMSPageAPICall {
    private static final String TAG = "AppCMSPageAPICall";
    private static final String API_SUFFIX = "_API";
    private static final String JSON_EXT = ".json";

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

    public AppCMSPageAPI callWithModules(String url,
                                         String authToken) {
        AppCMSPageAPI appCMSPageAPI = null;

        try {
            headersMap.clear();
            if (!TextUtils.isEmpty(apiKey)) {
                headersMap.put("x-api-key", apiKey);
            }
            if (!TextUtils.isEmpty(authToken)) {
                headersMap.put("Authorization", authToken);
            }
            Response<ResponseBody> response = appCMSPageAPIRest.get(url, headersMap).execute();
            if (response != null && response.body() != null) {
                appCMSPageAPI = gson.fromJson(response.body().string(), AppCMSPageAPI.class);
            }
        } catch (Exception e) {

        }

        return appCMSPageAPI;
    }

    @WorkerThread
    public AppCMSPageAPI call(Context context,
                              String urlWithContent,
                              String authToken,
                              String pageId,
                              boolean loadFromFile,
                              int tryCount,
                              List<String> modules) throws IOException {
        //Log.d(TAG, "URL: " + urlWithContent);
        String filename = getResourceFilename(pageId);
        AppCMSPageAPI appCMSPageAPI = null;

        if (loadFromFile) {
            try {
                appCMSPageAPI = readPageFromFile(filename);
            } catch (Exception e) {

            }
        }

        if (appCMSPageAPI == null) {
            try {
                headersMap.clear();
                if (!TextUtils.isEmpty(apiKey)) {
                    headersMap.put("x-api-key", apiKey);
                }
                if (!TextUtils.isEmpty(authToken)) {
                    headersMap.put("Authorization", authToken);
                }
                //Log.d(TAG, "AppCMSPageAPICall Authorization val " + headersMap.toString());
                Response<ResponseBody> response = null;

                if (modules != null && !modules.isEmpty()) {
                    StringBuilder urlithContentAndModules = new StringBuilder(urlWithContent);
                    urlithContentAndModules.append("&modules=");
                    int numModules = modules.size();
                    for (int i = 0; i < numModules; i++) {
                        urlithContentAndModules.append(modules.get(i));
                    }

                    response = appCMSPageAPIRest.get(urlithContentAndModules.toString(), headersMap).execute();
                } else {
                    long start = System.currentTimeMillis();

                    Log.d(TAG, "Start Page API request: " + start);

                    response = appCMSPageAPIRest.get(urlWithContent, headersMap).execute();

                    long end = System.currentTimeMillis();
                    Log.d(TAG, "End Page API request: " + end);
                    Log.d(TAG, "Page API URL: " + urlWithContent);
                    Log.d(TAG, "Total Time Page API request: " + (end - start));
                }

                if (response != null && response.body() != null) {
                    appCMSPageAPI = gson.fromJson(response.body().string(), AppCMSPageAPI.class);
                }

                if (!response.isSuccessful()) {
                    Log.e(TAG, "Response error: " + response.errorBody().string());
                }

                if (filename != null && (modules == null || modules.isEmpty())) {
                    appCMSPageAPI = writePageToFile(filename, appCMSPageAPI);
                }
            } catch (JsonSyntaxException e) {
                Log.w(TAG, "Error trying to parse input JSON " + urlWithContent + ": " + e.toString());
            } catch (Exception e) {
                Log.e(TAG, "A serious network error has occurred: " + e.getMessage());
            }
        }

        if (appCMSPageAPI == null && tryCount == 0) {
            return call(context,
                    urlWithContent,
                    authToken,
                    pageId,
                    loadFromFile,
                    tryCount + 1,
                    modules);
        }

        return appCMSPageAPI;
    }

    public void deleteAllFiles() {
        String fileToDeleteFilenamePattern = API_SUFFIX;
        File savedFileDirectory = new File(storageDirectory.toString());
        if (savedFileDirectory.isDirectory()) {
            String[] listExistingFiles = savedFileDirectory.list();
            for (String existingFilename : listExistingFiles) {
                if (existingFilename.contains(fileToDeleteFilenamePattern)) {
                    File fileToDelete = new File(storageDirectory, existingFilename);
                    try {
                        if (fileToDelete.delete()) {
//                            //Log.i(TAG, "Successfully deleted pre-existing file: " + fileToDelete);
                        } else {
                            //Log.e(TAG, "Failed to delete pre-existing file: " + fileToDelete);
                        }
                    } catch (Exception e) {
                        //Log.e(TAG, "Could not delete file: " +
//                                fileToDelete +
//                                " - " +
//                                e.getMessage());
                    }
                }
            }
        }
    }

    private AppCMSPageAPI readPageFromFile(String inputFilename) throws Exception {
        InputStream inputStream = new FileInputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        inputFilename));
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        AppCMSPageAPI appCMSPageAPI = (AppCMSPageAPI) objectInputStream.readObject();
        inputStream.close();
        return appCMSPageAPI;
    }

    private AppCMSPageAPI writePageToFile(String outputFilename,
                                          AppCMSPageAPI appCMSPageAPI) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        outputFilename));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(appCMSPageAPI);
        outputStream.close();
        return appCMSPageAPI;
    }

    private String getResourceFilename(String pageId) {
        if (!TextUtils.isEmpty(pageId)) {
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
