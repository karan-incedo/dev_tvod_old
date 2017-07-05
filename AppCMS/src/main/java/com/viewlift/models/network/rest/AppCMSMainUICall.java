package com.viewlift.models.network.rest;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import snagfilms.com.air.appcms.R;

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
    public AppCMSMain call(Context context, String siteId) throws IOException {
        Date now = new Date();
        final String appCMSMainUrl = context.getString(R.string.app_cms_main_url,
                context.getString(R.string.app_cms_baseurl),
                siteId,
                now.getTime());
        AppCMSMain main = null;
        AppCMSMain mainInStorage = null;
        try {
            Log.d(TAG, "Attempting to retrieve main.json: " + appCMSMainUrl);

            final String hostName = new URL(appCMSMainUrl).getHost();
            ExecutorService executor = Executors.newCachedThreadPool();
            Future<List<InetAddress>> future = executor.submit(new Callable<List<InetAddress>>() {
                @Override
                public List<InetAddress> call() throws Exception {
                    return okHttpClient.dns().lookup(hostName);
                }
            });

            try {
                future.get(connectionTimeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                Log.e(TAG, "Connection timed out: " + e.toString());
                return null;
            } catch (InterruptedException e) {
                Log.e(TAG, "Connection interrupted: "+ e.toString());
                return null;
            } catch (ExecutionException e) {
                Log.e(TAG, "Execution error: " + e.toString());
                return null;
            } finally {
                future.cancel(true);
            }

            main = appCMSMainUIRest.get(appCMSMainUrl).execute().body();
            String filename = getResourceFilename(appCMSMainUrl);
            try {
                mainInStorage = readMainFromFile(filename);
            } catch (IOException exception) {
                Log.w(TAG, "Previous version of main.json file is not in storage");
            }

            boolean useExistingOldVersion = true;

            if (mainInStorage != null) {
                main.setOldVersion(mainInStorage.getOldVersion());
                useExistingOldVersion = false;
            }

            if (useExistingOldVersion) {
                main.setOldVersion(main.getVersion());
            }

            main = writeMainToFile(filename, main);
        } catch (Exception e) {
            Log.e(TAG, "A serious network error has occurred: " + e.getMessage());
        }

        return main;
    }

    private AppCMSMain writeMainToFile(String outputFilename, AppCMSMain main) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() + outputFilename));
        String output = gson.toJson(main, AppCMSMain.class);
        outputStream.write(output.getBytes());
        outputStream.close();
        return main;
    }

    private AppCMSMain readMainFromFile(String inputFilename) throws IOException {
        InputStream inputStream = new FileInputStream(storageDirectory.toString() + inputFilename);
        Scanner scanner = new Scanner(inputStream);
        StringBuffer sb = new StringBuffer();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        AppCMSMain main = gson.fromJson(sb.toString(), AppCMSMain.class);
        scanner.close();
        inputStream.close();
        return main;
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
