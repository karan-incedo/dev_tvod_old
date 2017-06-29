package com.viewlift.models.data.appcms.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.viewlift.models.network.modules.AppCMSWatchlistUrlData;
import com.viewlift.models.network.rest.AppCMSWatchlistCall;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

/*
 * Created by Viewlift on 6/29/2017.
 */

public class AppCMSWatchlistContentProvider extends ContentProvider {
    private Gson gson;
    private OkHttpClient httpClient;

    @Inject
    AppCMSWatchlistUrlData appCMSWatchlistUrlData;

    @Inject
    AppCMSWatchlistCall appCMSWatchlistCall;

    @Override
    public boolean onCreate() {
        gson = new Gson();
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
