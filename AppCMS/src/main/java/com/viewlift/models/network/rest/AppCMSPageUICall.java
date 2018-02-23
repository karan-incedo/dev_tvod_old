package com.viewlift.models.network.rest;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.presenters.AppCMSPresenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Date;
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
    public AppCMSPageUICall(AppCMSPageUIRest appCMSPageUIRest,
                            Gson gson,
                            File storageDirectory) {
        this.appCMSPageUIRest = appCMSPageUIRest;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
    }

    @WorkerThread
    public boolean writeToFile(AppCMSPageUI appCMSPageUI, String url) {
        String filename = getResourceFilename(url);
        try {
            writePageToFile(filename, appCMSPageUI);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to write AppCMSPageUI file: " +
                    url);
        }
        return false;
    }

    @WorkerThread
    public AppCMSPageUI call(String url, boolean bustCache, boolean loadFromFile) throws IOException {
        String filename = getResourceFilename(url);
        AppCMSPageUI appCMSPageUI = null;
        if (loadFromFile) {
            try {
                appCMSPageUI = readPageFromFile(filename);
                appCMSPageUI.setLoadedFromNetwork(false);
            } catch (Exception e) {
                Log.e(TAG, "Error reading file AppCMS UI JSON file: " + e.getMessage());
                try {
                    if (bustCache) {
                        StringBuilder urlWithCacheBuster = new StringBuilder(url);
                        urlWithCacheBuster.append("&x=");
                        urlWithCacheBuster.append(new Date().getTime());
                        appCMSPageUI = loadFromNetwork(urlWithCacheBuster.toString(), filename);
                    } else {
                        appCMSPageUI = loadFromNetwork(url, filename);
                    }
                } catch (Exception e2) {
                    //Log.e(TAG, "A last ditch effort to download the AppCMS UI JSON did not succeed: " +
//                        e2.getMessage());
                }
            }
        } else {
            if (bustCache) {
                StringBuilder urlWithCacheBuster = new StringBuilder(url);
                urlWithCacheBuster.append("&x=");
                urlWithCacheBuster.append(new Date().getTime());
                appCMSPageUI = loadFromNetwork(urlWithCacheBuster.toString(), filename);
            } else {
                appCMSPageUI = loadFromNetwork(url, filename);
            }

            if (appCMSPageUI == null) {
                try {
                    appCMSPageUI = readPageFromFile(filename);
                    appCMSPageUI.setLoadedFromNetwork(false);
                } catch (Exception e) {
                    Log.e(TAG, "Error reading file AppCMS UI JSON file: " + e.getMessage());
                }
            }
        }


        return appCMSPageUI;
    }

    private AppCMSPageUI loadFromNetwork(String url, String filename) {
        AppCMSPageUI appCMSPageUI = null;
        try {
            deletePreviousFiles(url);
            appCMSPageUI = writePageToFile(filename, appCMSPageUIRest.get(url)
                    .execute().body());
            appCMSPageUI.setLoadedFromNetwork(true);
        } catch (Exception e) {
            Log.e(TAG, "Error receiving network data " + url + ": " + e.getMessage());
        }
        return appCMSPageUI;
    }

    private AppCMSPageUI writePageToFile(String outputFilename, AppCMSPageUI appCMSPageUI) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        outputFilename));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(appCMSPageUI);
        outputStream.close();
        return appCMSPageUI;
    }

    private void deletePreviousFiles(String url) {
        String fileToDeleteFilenamePattern = getResourceFilenameWithJsonOnly(url);
        if (storageDirectory.isDirectory()) {
            String[] listExistingFiles = storageDirectory.list();
            for (String existingFilename : listExistingFiles) {
                if (existingFilename.contains(fileToDeleteFilenamePattern)) {
                    File fileToDelete = new File(storageDirectory, existingFilename);
                    try {
                        if (fileToDelete.delete()) {
                            //Log.i(TAG, "Successfully deleted pre-existing file: " + fileToDelete);
                        } else {
                            //Log.e(TAG, "Failed to delete pre-existing file: " + fileToDelete);
                        }
                    } catch (Exception e) {
                        //Log.e(TAG, "Failed to delete pre-existing file: " + fileToDelete);
                    }
                }
            }
        }
    }

    private AppCMSPageUI readPageFromFile(String inputFilename) throws Exception {
        InputStream inputStream = new FileInputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        inputFilename));
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        AppCMSPageUI appCMSPageUI = (AppCMSPageUI) objectInputStream.readObject();
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


    private String getResourceFilenameWithJsonOnly(String url) {
        final String JSON_EXT = ".json";
        int startIndex = url.lastIndexOf(File.separatorChar);
        int endIndex = url.indexOf(JSON_EXT) + JSON_EXT.length();
        if (0 <= startIndex && startIndex < endIndex) {
            return url.substring(startIndex + 1, endIndex);
        }
        return url;
    }

    private String getResourceFilename(String url) {
        int startIndex = url.lastIndexOf(File.separatorChar);
        int endIndex = url.length();
        if (0 <= startIndex && startIndex < endIndex) {
            return url.substring(startIndex + 1, endIndex);
        }
        return url;
    }
}