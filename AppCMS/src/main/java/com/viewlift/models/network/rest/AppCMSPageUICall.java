package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.inject.Inject;

/**
 * Created by viewlift on 5/9/17.
 */

public class AppCMSPageUICall {
    private static final String TAG = "AppCMSPageUICall";

    private final AppCMSPageUIRest appCMSPageUIRest;
    private final Gson gson;
    private final File storageDirectory;

    @Inject
    public AppCMSPageUICall(AppCMSPageUIRest appCMSPageUIRest, Gson gson, File storageDirectory) {
        this.appCMSPageUIRest = appCMSPageUIRest;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
    }

    @WorkerThread
    public AppCMSPageUI call(String url, boolean loadFromFile) throws IOException {
        String filename = getResourceFilename(url);
        // TODO: Change this logic to remove the input variable loadFromFile and determine to load from file if there is a network error
        if (loadFromFile) {
            return readPageFromFile(filename);
        }
        AppCMSPageUI appCMSPageUI = null;
        try {
            appCMSPageUI = appCMSPageUIRest.get(url).execute().body();
            appCMSPageUI = writePageToFile(filename, appCMSPageUI);
        } catch (JsonSyntaxException e) {
            Log.w(TAG, "Error trying to parse input JSON " + url + ": " + e.toString());
        } catch (Exception e) {
            Log.e(TAG, "A serious network error has occurred: " + e.getMessage());
        }
        return appCMSPageUI;
    }

    private AppCMSPageUI writePageToFile(String outputFilename, AppCMSPageUI appCMSPageUI) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() + outputFilename));
        String output = gson.toJson(appCMSPageUI, AppCMSPageUI.class);
        outputStream.write(output.getBytes());
        outputStream.close();
        return appCMSPageUI;
    }

    private AppCMSPageUI readPageFromFile(String inputFilename) throws IOException {
        InputStream inputStream = new FileInputStream(
                new File(storageDirectory.toString() + inputFilename));
        Scanner scanner = new Scanner(inputStream);
        StringBuffer sb = new StringBuffer();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        AppCMSPageUI appCMSPageUI =
                gson.fromJson(sb.toString(), AppCMSPageUI.class);
        scanner.close();
        inputStream.close();
        return appCMSPageUI;
    }

    private String getResourceFilename(String url) {
        final String PATH_SEP = "/";
        final String JSON_EXT = ".json";
        int startIndex = url.lastIndexOf(PATH_SEP);
        int endIndex = url.indexOf(JSON_EXT) + JSON_EXT.length();
        if (0 <= startIndex && startIndex < endIndex) {
            return url.substring(startIndex+1, endIndex);
        }
        return url;
    }
}
