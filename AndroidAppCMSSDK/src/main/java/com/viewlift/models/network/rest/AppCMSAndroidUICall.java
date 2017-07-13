package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidUI;

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

public class AppCMSAndroidUICall {
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
    public AppCMSAndroidUI call(String url, boolean loadFromFile) throws IOException {
        String filename = getResourceFilename(url);
        // TODO: Change this logic to remove the input variable loadFromFile and determine to load from file if there is a network error
        if (loadFromFile) {
            return readAndroidFromFile(filename);
        }
        return writeAndroidToFile(filename, appCMSAndroidUIRest.get(url).execute().body());
    }

    private AppCMSAndroidUI writeAndroidToFile(String outputFilename, AppCMSAndroidUI appCMSAndroidUI) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() + outputFilename));
        String output = gson.toJson(appCMSAndroidUI, AppCMSAndroidUI.class);
        outputStream.write(output.getBytes());
        outputStream.close();
        return appCMSAndroidUI;
    }

    private AppCMSAndroidUI readAndroidFromFile(String inputFilename) throws IOException {
        InputStream inputStream = new FileInputStream(
                new File(storageDirectory.toString() + inputFilename));
        Scanner scanner = new Scanner(inputStream);
        StringBuffer sb = new StringBuffer();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        AppCMSAndroidUI appCMSAndroidUI = gson.fromJson(sb.toString(), AppCMSAndroidUI.class);
        scanner.close();
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
