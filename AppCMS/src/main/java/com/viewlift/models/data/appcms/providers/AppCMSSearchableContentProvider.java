package com.viewlift.models.data.appcms.providers;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.search.AppCMSSearchResult;
import com.viewlift.models.network.modules.AppCMSSearchUrlData;
import com.viewlift.models.network.rest.AppCMSSearchCall;
import com.viewlift.presenters.AppCMSPresenter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import snagfilms.com.air.appcms.BuildConfig;
import snagfilms.com.air.appcms.R;

import static android.app.SearchManager.SUGGEST_URI_PATH_QUERY;

/**
 * Created by viewlift on 6/12/17.
 */

public class AppCMSSearchableContentProvider extends ContentProvider {
    public static final String URI_AUTHORITY = BuildConfig.AUTHORITY;

    private static final String TAG = "SearchableProvider";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String[] SUGGESTION_COLUMN_NAMES = { BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA };

    private Gson gson;
    private OkHttpClient client;

    @Inject
    AppCMSSearchUrlData appCMSSearchUrlData;
    @Inject
    AppCMSSearchCall appCMSSearchCall;

    static {
        uriMatcher.addURI(URI_AUTHORITY, SUGGEST_URI_PATH_QUERY, 1);
        uriMatcher.addURI(URI_AUTHORITY, null, 2);
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "Creating Content Provider");
        gson = new Gson();
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        MatrixCursor cursor = null;

        if (getContext() instanceof AppCMSApplication && needInjection()) {
            AppCMSPresenter appCMSPresenter =
                    ((AppCMSApplication) getContext()).getAppCMSPresenterComponent().appCMSPresenter();
            appCMSPresenter.getAppCMSSearchUrlComponent().inject(this);
            if (needInjection()) {
                return null;
            }
        }

        switch (uriMatcher.match(uri)) {
            case 1:
            case 2:
                Log.d(TAG, "Performing a search of Viewlift films");
                if (selectionArgs != null &&
                        selectionArgs.length > 0 &&
                        !TextUtils.isEmpty(appCMSSearchUrlData.getBaseUrl()) &&
                        !TextUtils.isEmpty(appCMSSearchUrlData.getSiteName())) {
                    String baseUrl = appCMSSearchUrlData.getBaseUrl();
                    String siteName = appCMSSearchUrlData.getSiteName();
                    String url = getContext().getString(R.string.app_cms_search_api_url,
                            baseUrl,
                            siteName,
                            selectionArgs[0]);
                    try {
                        List<AppCMSSearchResult> searchResultList = appCMSSearchCall.call(url);
                        if (searchResultList != null) {
                            Log.d(TAG, "Search results received (" + searchResultList.size() + "): ");
                            cursor = new MatrixCursor(SUGGESTION_COLUMN_NAMES, searchResultList.size());
                            for (int i = 0; i < searchResultList.size(); i++) {
                                Object[] rowResult = { i,
                                    searchResultList.get(i).getTitle(),
                                    searchResultList.get(i).getTitle() };
                                cursor.addRow(rowResult);
                                Log.d(TAG, searchResultList.get(i).getTitle());
                            }
                            cursor.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Received exception: " + e.getMessage());
                    }
                } else {
                    Log.d(TAG, "Could not retrieved results - search content provider has not been injected");
                }
                break;
            default:
        }
        return cursor;
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
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    private boolean needInjection() {
        return appCMSSearchCall == null || appCMSSearchUrlData == null;
    }
}
