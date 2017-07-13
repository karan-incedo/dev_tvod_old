package com.viewlift.models.network.rest;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.appcmssdk.R;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Observable;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSMainUICall {
    private static final String TAG = "AppCMSMainUICall";

    private final long connectionTimeout;
    private final OkHttpClient okHttpClient;
    private final AppCMSMainUIRest appCMSMainUIRest;

    @Inject
    public AppCMSMainUICall(long connectionTimeout,
                            OkHttpClient okHttpClient,
                            AppCMSMainUIRest appCMSMainUIRest) {
        this.connectionTimeout = connectionTimeout;
        this.okHttpClient = okHttpClient;
        this.appCMSMainUIRest = appCMSMainUIRest;
    }

    public void call(Context context,
                     String baseUrl,
                     String siteId,
                     final Action1<AppCMSMain> readyAction) throws MalformedURLException {
        Date now = new Date();
        final String appCMSMainUrl = context.getString(R.string.app_cms_main_url,
                baseUrl,
                siteId,
                now.getTime());

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
            return;
        } catch (InterruptedException e) {
            Log.e(TAG, "Connection interrupted: " + e.toString());
            return;
        } catch (ExecutionException e) {
            Log.e(TAG, "Execution error: " + e.toString());
            return;
        } finally {
            future.cancel(true);
        }

        appCMSMainUIRest.get(appCMSMainUrl).enqueue(new Callback<AppCMSMain>() {
            @Override
            public void onResponse(Call<AppCMSMain> call, Response<AppCMSMain> response) {
                rx.Observable.just(response.body()).subscribe(readyAction);
            }

            @Override
            public void onFailure(Call<AppCMSMain> call, Throwable t) {
                rx.Observable.just((AppCMSMain) null).subscribe(readyAction);
            }
        });
    }
}
