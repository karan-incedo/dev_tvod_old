package com.viewlift.models.network.rest;

import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.viewlift.models.data.appcms.sites.AppCMSSite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.inject.Inject;

/**
 * Created by viewlift on 6/15/17.
 */

public class AppCMSSiteCall {
    private static final String TAG = "AppCMSSiteCall";

    private final AppCMSSiteRest appCMSSiteRest;
    private final Gson gson;
    private final File storageDirectory;

    @Inject
    public AppCMSSiteCall(AppCMSSiteRest appCMSSiteRest, Gson gson, File storageDirectory) {
        this.appCMSSiteRest = appCMSSiteRest;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
    }

    @WorkerThread
    public AppCMSSite call(String url, boolean networkDisconnected, int numberOfTries) throws IOException {
        try {
            //Log.d(TAG, "Attempting to retrieve site JSON: " + url);
            AppCMSSite appCMSSite = null;
            if (!networkDisconnected) {
                appCMSSite = appCMSSiteRest.get(url).execute().body();
            }
            if (appCMSSite == null) {
                appCMSSite = readAppCMSSiteFromFile(getResourceFilename());
            } else {
                appCMSSite = writeAppCMSSiteToFile(getResourceFilename(), appCMSSite);
            }

            return appCMSSite;
        } catch (JsonSyntaxException e) {
            //Log.e(TAG, "DialogType parsing input JSON - " + url + ": " + e.toString());
        } catch (Exception e) {
            //Log.e(TAG, "Network error retrieving site data - " + url + ": " + e.toString());
        }

        if (numberOfTries == 0) {
            return call(url, networkDisconnected, numberOfTries + 1);
        } else {
            try {
                return readAppCMSSiteFromFile(getResourceFilename());
            } catch (Exception e) {
                //Log.e(TAG, "Failed to read site.json from file: " + e.getMessage());
            }
        }

        return null;
    }

    private AppCMSSite writeAppCMSSiteToFile(String outputFilename, AppCMSSite appCMSSite) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        outputFilename));
        String output = gson.toJson(appCMSSite, AppCMSSite.class);
        outputStream.write(output.getBytes());
        outputStream.close();
        return appCMSSite;
    }

    private AppCMSSite readAppCMSSiteFromFile(String inputFilename) throws IOException {
        InputStream inputStream = new FileInputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        inputFilename));
        Scanner scanner = new Scanner(inputStream);
        StringBuffer sb = new StringBuffer();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        AppCMSSite appCMSSite = gson.fromJson(sb.toString(), AppCMSSite.class);
        scanner.close();
        inputStream.close();
        return appCMSSite;
    }

    private String getResourceFilename() {
        return "sites.json";
    }
}
