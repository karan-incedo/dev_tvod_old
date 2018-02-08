package com.viewlift.models.network.rest;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.R;
import com.viewlift.Utils;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSMainUICall {
    private static final String TAG = "AppCMSMainUICall";

    private final long connectionTimeout;
    private final OkHttpClient okHttpClient;
    private final AppCMSMainUIRest appCMSMainUIRest;
    private final Gson gson;
    private final File storageDirectory;

    @Inject
    public AppCMSMainUICall(long connectionTimeout,
                            OkHttpClient okHttpClient,
                            AppCMSMainUIRest appCMSMainUIRest,
                            Gson gson,
                            File storageDirectory) {
        this.connectionTimeout = connectionTimeout;
        this.okHttpClient = okHttpClient;
        this.appCMSMainUIRest = appCMSMainUIRest;
        this.gson = gson;
        this.storageDirectory = storageDirectory;
    }

    @WorkerThread
    public AppCMSMain call(Context context, String siteId, int tryCount, boolean bustCache) {
        Date now = new Date();

        StringBuilder appCMSMainUrlSb = new StringBuilder(context.getString(R.string.app_cms_main_url,
                context.getString(R.string.app_cms_baseurl),
                siteId));
        if (bustCache) {
            appCMSMainUrlSb.append("?x=");
            appCMSMainUrlSb.append(now.getTime());
        }
        final String appCMSMainUrl = appCMSMainUrlSb.toString();

        AppCMSMain main = null;
        AppCMSMain mainInStorage = null;
        try {
            Log.d(TAG, "Attempting to retrieve main.json: " + appCMSMainUrl);

            final String hostName = new URL(appCMSMainUrlSb.toString()).getHost();
            ExecutorService executor = Executors.newCachedThreadPool();
            Future<List<InetAddress>> future = executor.submit(()
                    -> okHttpClient.dns().lookup(hostName));
            try {
                future.get(connectionTimeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                //Log.e(TAG, "Connection timed out: " + e.toString());
                if (tryCount == 0) {
                    return call(context, siteId, tryCount + 1, bustCache);
                }
                return null;
            } catch (InterruptedException e) {
                //Log.e(TAG, "Connection interrupted: " + e.toString());
                if (tryCount == 0) {
                    return call(context, siteId, tryCount + 1, bustCache);
                }
                return null;
            } catch (ExecutionException e) {
                //Log.e(TAG, "Execution error: " + e.toString());
                if (tryCount == 0) {
                    return call(context, siteId, tryCount + 1, bustCache);
                }
                try {
                    return readMainFromFile(getResourceFilename(appCMSMainUrlSb.toString()));
                } catch (Exception e1) {
                    //Log.e(TAG, "Could not retrieve main.json from file: " +
//                        e1.getMessage());
                }
                return null;
            } finally {
                future.cancel(true);
            }

            try {
//                Log.d(TAG, "Retrieving main.json from URL: " + appCMSMainUrl);
                long start = System.currentTimeMillis();
                Log.d(TAG, "Start main.json request: " + start);
                main = appCMSMainUIRest.get(appCMSMainUrlSb.toString()).execute().body();
                long end = System.currentTimeMillis();
                Log.d(TAG, "End main.json request: " + end);
                Log.d(TAG, "main.json URL: " + appCMSMainUrlSb.toString());
                Log.d(TAG, "Total Time main.json request: " + (end - start));
            } catch (Exception e) {
                Log.w(TAG, "Failed to read main.json from network: " + e.getMessage());
            }

            String filename = getResourceFilename(appCMSMainUrlSb.toString());
            try {
                mainInStorage = readMainFromFile(filename);
            } catch (Exception exception) {
                Log.w(TAG, "Previous version of main.json file is not in storage");
            }

            if (main != null && mainInStorage != null) {
                Log.d(TAG, "Read main.json in storage version: " + mainInStorage.getVersion());
                main.setLoadFromFile(main.getVersion().equals(mainInStorage.getVersion()));
            }

            if (main != null) {
                Log.d(TAG, "Read main.json version: " + main.getVersion());
            }

            main = writeMainToFile(filename, main);
        } catch (Exception e) {
            //Log.e(TAG, "A serious network error has occurred: " + e.getMessage());
        }

        if (main == null && tryCount == 0) {
            return call(context, siteId, tryCount + 1, bustCache);
        }

        return main;
    }

    private AppCMSMain writeMainToFile(String outputFilename, AppCMSMain main) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        outputFilename));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(main);
        outputStream.close();
        return main;
    }

    private AppCMSMain readMainFromFile(String inputFilename) throws Exception {
        InputStream inputStream = new FileInputStream(storageDirectory.toString() +
                File.separatorChar +
                inputFilename);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        AppCMSMain main = (AppCMSMain) objectInputStream.readObject();
        inputStream.close();
        return main;
    }

    private String getResourceFilename(String url) {
        final String PATH_SEP = "/";
        final String JSON_EXT = ".json";
        int endIndex = url.indexOf(JSON_EXT) + JSON_EXT.length();
        int startIndex = url.lastIndexOf(PATH_SEP);
        if (0 <= startIndex && startIndex < endIndex) {
            return url.substring(startIndex + 1, endIndex);
        }
        return url;
    }
}
