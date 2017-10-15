package com.viewlift.models.network.rest;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.R;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public AppCMSMain call(Context context, String siteId, int tryCount, boolean forceReloadFromNetwork) {
        Date now = new Date();
        final String appCMSMainUrl = context.getString(R.string.app_cms_main_url,
                context.getString(R.string.app_cms_baseurl),
                siteId,
                now.getTime());
        AppCMSMain main = null;
        AppCMSMain mainInStorage = null;
        try {
            //Log.d(TAG, "Attempting to retrieve main.json: " + appCMSMainUrl);

            final String hostName = new URL(appCMSMainUrl).getHost();
            ExecutorService executor = Executors.newCachedThreadPool();
            Future<List<InetAddress>> future = executor.submit(()
                    -> okHttpClient.dns().lookup(hostName));
            try {
                future.get(connectionTimeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                //Log.e(TAG, "Connection timed out: " + e.toString());
                if (tryCount == 0) {
                    return call(context, siteId, tryCount + 1, forceReloadFromNetwork);
                }
                return null;
            } catch (InterruptedException e) {
                //Log.e(TAG, "Connection interrupted: " + e.toString());
                if (tryCount == 0) {
                    return call(context, siteId, tryCount + 1, forceReloadFromNetwork);
                }
                return null;
            } catch (ExecutionException e) {
                //Log.e(TAG, "Execution error: " + e.toString());
                if (tryCount == 0) {
                    return call(context, siteId, tryCount + 1, forceReloadFromNetwork);
                }
                try {
                    return readMainFromFile(getResourceFilename(appCMSMainUrl));
                } catch (Exception e1) {
                    //Log.e(TAG, "Could not retrieve main.json from file: " +
//                        e1.getMessage());
                }
                return null;
            } finally {
                future.cancel(true);
            }

            try {
                main = appCMSMainUIRest.get(appCMSMainUrl).execute().body();
            } catch (Exception e) {
                //Log.w(TAG, "Failed to read main.json from network: " + e.getMessage());
            }

            String filename = getResourceFilename(appCMSMainUrl);
            try {
                mainInStorage = readMainFromFile(filename);
            } catch (IOException exception) {
                //Log.w(TAG, "Previous version of main.json file is not in storage");
            }

            boolean useExistingOldVersion = true;

            if (mainInStorage != null && mainInStorage.getVersion() != null) {
                if (main != null && main.getVersion() != null) {
                    useExistingOldVersion = main.getVersion().equals(mainInStorage.getVersion());
                    main.setLoadFromFile(useExistingOldVersion);
                    main.setOldVersion(main.getVersion());
                } else if (main == null) {
                    main = mainInStorage;
                    main.setOldVersion(mainInStorage.getOldVersion());
                }
            }

            if (main != null && useExistingOldVersion) {
                main.setOldVersion(main.getVersion());
            }

            main = writeMainToFile(filename, main);
        } catch (Exception e) {
            //Log.e(TAG, "A serious network error has occurred: " + e.getMessage());
        }

        if (main == null && tryCount == 0) {
            return call(context, siteId, tryCount + 1, forceReloadFromNetwork);
        }

        return main;
    }

    private AppCMSMain writeMainToFile(String outputFilename, AppCMSMain main) throws IOException {
        OutputStream outputStream = new FileOutputStream(
                new File(storageDirectory.toString() +
                        File.separatorChar +
                        outputFilename));
        String output = gson.toJson(main, AppCMSMain.class);
        outputStream.write(output.getBytes());
        outputStream.close();
        return main;
    }

    private AppCMSMain readMainFromFile(String inputFilename) throws Exception {
        InputStream inputStream = new FileInputStream(storageDirectory.toString() +
                File.separatorChar +
                inputFilename);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        while (0 < bufferedInputStream.available()) {
            int len = bufferedInputStream.read(buffer, 0, buffer.length);
            bytesOut.write(buffer, 0, len);
        }

        AppCMSMain main = gson.fromJson(bytesOut.toString("UTF-8"), AppCMSMain.class);
        bytesOut.close();
        bufferedInputStream.close();
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
