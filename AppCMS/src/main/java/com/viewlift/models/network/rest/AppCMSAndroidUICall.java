package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by viewlift on 5/9/17.
 */

public class AppCMSAndroidUICall {
    private static final String TAG = "AndroidUICall";

    private static final String SAVE_PATH = "AppCMSAndroidUIRest/";

    private final AppCMSAndroidUIRest appCMSAndroidUIRest;
    private final Gson gson;
    private final File storageDirectory;

    @Inject
    public AppCMSAndroidUICall(AppCMSAndroidUIRest appCMSAndroidUIRest, Gson gson, File storageDirectory) {
        this.appCMSAndroidUIRest = appCMSAndroidUIRest;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
    }

    @WorkerThread
    public AppCMSAndroidUI call(String url,
                                String xApiKey,
                                boolean loadFromFile,
                                boolean bustCache,
                                int tryCount) throws IOException {
        String filename = getResourceFilename(url);
        AppCMSAndroidUI appCMSAndroidUI = null;
        if (loadFromFile) {
            try {
                appCMSAndroidUI = readAndroidFromFile(filename);
            } catch (Exception e) {
                Log.w(TAG, "Failed to read android.json from file: " + e.getMessage());
            }
        }
        if (appCMSAndroidUI == null) {
            try {

                Map<String, String> authTokenMap = new HashMap<>();
                authTokenMap.put("x-api-key", xApiKey);
                if (bustCache) {
                    StringBuilder urlWithCacheBuster = new StringBuilder(url);
                    urlWithCacheBuster.append("&x=");
                    urlWithCacheBuster.append(new Date().getTime());
                    appCMSAndroidUI = appCMSAndroidUIRest.get(urlWithCacheBuster.toString(), authTokenMap).execute().body();
                } else {
                    long start = System.currentTimeMillis();
                    Log.d(TAG, "Start android.json request: " + start);
                    appCMSAndroidUI = appCMSAndroidUIRest.get(url, authTokenMap).execute().body();
                    long end = System.currentTimeMillis();
                    Log.d(TAG, "End android.json request: " + end);
                    Log.d(TAG, "android.json URL: " + url);
                    Log.d(TAG, "Total Time android.json request: " + (end - start));
                }
            } catch (Exception e) {
                //Log.w(TAG, "Failed to retrieve Android UI JSON file from network: " +
//                    e.getMessage());
            }
        }
        if (appCMSAndroidUI == null && tryCount == 0) {
            return call(url, xApiKey, loadFromFile, bustCache, tryCount + 1);
        } else if (appCMSAndroidUI == null) {
            try {
                appCMSAndroidUI = readAndroidFromFile(filename);
            } catch (Exception e) {
                //Log.e(TAG, "Failed to read Android UI JSON file from file: " +
//                    e.getMessage());
            }
        }
        if (appCMSAndroidUI != null) {
            return writeAndroidToFile(filename, appCMSAndroidUI);
        }
        return null;
    }

    private AppCMSAndroidUI writeAndroidToFile(String outputFilename, AppCMSAndroidUI appCMSAndroidUI) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        outputFilename));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(appCMSAndroidUI);
        outputStream.close();
        return appCMSAndroidUI;
    }

    private AppCMSAndroidUI readAndroidFromFile(String inputFilename) throws Exception {
        InputStream inputStream = new FileInputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        inputFilename));
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        AppCMSAndroidUI appCMSAndroidUI = (AppCMSAndroidUI) objectInputStream.readObject();
        inputStream.close();
        return appCMSAndroidUI;
    }

    private String getResourceFilename(String url) {
        final String PATH_SEP = "/";
        final String JSON_EXT = ".json";
        int endIndex = url.indexOf(JSON_EXT) + JSON_EXT.length();
        int startIndex = url.lastIndexOf(PATH_SEP);
        if (0 <= startIndex && startIndex < endIndex) {
            return url.substring(startIndex+1, endIndex);
        }
        return url;
    }
}
