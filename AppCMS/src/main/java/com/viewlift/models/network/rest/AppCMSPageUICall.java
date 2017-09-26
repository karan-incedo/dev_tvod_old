package com.viewlift.models.network.rest;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Scanner;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by viewlift on 5/9/17.
 */

public class AppCMSPageUICall {
    private static final String TAG = "AppCMSPageUICall";

    private final AppCMSPageUIRest appCMSPageUIRest;
    private final Gson gson;
    private final File storageDirectory;

    @Inject
    public AppCMSPageUICall(AppCMSPageUIRest appCMSPageUIRest,
                            Gson gson,
                            File storageDirectory) {
        this.appCMSPageUIRest = appCMSPageUIRest;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
    }

    @WorkerThread
    public AppCMSPageUI call(String url, long timeStamp) throws IOException {
        String filename = getResourceFilename(url);
        AppCMSPageUI appCMSPageUI = null;
        try {
            appCMSPageUI = readPageFromFile(filename);
        } catch (Exception e) {
            Log.e(TAG, "Error reading file AppCMS UI JSON file: " +
                    e.getMessage());
            try {
                StringBuilder urlWithTimestamp = new StringBuilder(url);
                urlWithTimestamp.append("?x=");
                urlWithTimestamp.append(timeStamp);
                appCMSPageUI = writePageToFile(filename, appCMSPageUIRest.get(urlWithTimestamp.toString()).execute().body());
            } catch (Exception e2) {
                Log.e(TAG, "A last ditch effort to download the AppCMS UI JSON did not succeed: " +
                        e2.getMessage());
            }
        }
        try {
            appCMSPageUIRest.get(url).enqueue(new Callback<AppCMSPageUI>() {
                @Override
                public void onResponse(Call<AppCMSPageUI> call, Response<AppCMSPageUI> response) {
                    try {
                        if (response.body() != null) {
                            writePageToFile(filename, response.body());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Could not write AppCMS UI JSON file: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<AppCMSPageUI> call, Throwable t) {
                    Log.e(TAG, "Could not read AppCMS UI JSON file from network: " +
                        t.getMessage());
                }
            });
        } catch (JsonSyntaxException e) {
            Log.w(TAG, "DialogType trying to parse input JSON " + url + ": " + e.toString());
        } catch (Exception e) {
            Log.e(TAG, "A serious network error has occurred: " + e.getMessage());
        }
        return appCMSPageUI;
    }

    private AppCMSPageUI writePageToFile(String outputFilename, AppCMSPageUI appCMSPageUI) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        outputFilename));
        String output = gson.toJson(appCMSPageUI, AppCMSPageUI.class);
        outputStream.write(output.getBytes());
        outputStream.close();
        return appCMSPageUI;
    }

    private AppCMSPageUI readPageFromFile(String inputFilename) throws IOException {
        InputStream inputStream = new FileInputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        inputFilename));
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

    private AppCMSPageUI writePageToFileFromAssets(Context context, String url) throws IOException {
        AppCMSPageUI appCMSPageUI = null;

        String resourceFilename = getResourceFilename(url);

        StringBuilder inputFilename = new StringBuilder();
        inputFilename.append(resourceFilename);

        StringBuilder outputFilename = new StringBuilder();
        outputFilename.append(storageDirectory.toString());
        outputFilename.append(File.separatorChar);
        outputFilename.append(resourceFilename);

        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        OutputStream outputStream = null;

        inputStream = assetManager.open(inputFilename.toString());

        Scanner scanner = new Scanner(inputStream);
        StringBuffer sb = new StringBuffer();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }

        appCMSPageUI = gson.fromJson(sb.toString(), AppCMSPageUI.class);
        scanner.close();
        inputStream.close();

        outputStream = new FileOutputStream(new File(outputFilename.toString()));
        outputStream.write(sb.toString().getBytes(Charset.forName("UTF-8")));
        outputStream.flush();
        outputStream.close();

        return appCMSPageUI;
    }

    private String getResourceFilename(String url) {
        final String JSON_EXT = ".json";
        int startIndex = url.lastIndexOf(File.separatorChar);
        int endIndex = url.indexOf(JSON_EXT) + JSON_EXT.length();
        if (0 <= startIndex && startIndex < endIndex) {
            return url.substring(startIndex+1, endIndex);
        }
        return url;
    }
}
