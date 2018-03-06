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

import com.google.gson.Gson;
import com.viewlift.AppCMSApplication;
import com.viewlift.BuildConfig;
import com.viewlift.R;
import com.viewlift.models.data.appcms.search.AppCMSSearchResult;
import com.viewlift.models.network.modules.AppCMSSearchUrlData;
import com.viewlift.models.network.rest.AppCMSSearchCall;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

import static android.app.SearchManager.SUGGEST_URI_PATH_QUERY;

/*
 * Created by viewlift on 6/12/17.
 */

public class AppCMSSearchableContentProvider extends ContentProvider {
    public static final String URI_AUTHORITY = BuildConfig.AUTHORITY;
    //private static final String TAG = "SearchableProvider";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String[] SUGGESTION_COLUMN_NAMES = {BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_DURATION,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,
    SearchManager.SUGGEST_COLUMN_CONTENT_TYPE};

    static {
        uriMatcher.addURI(URI_AUTHORITY, SUGGEST_URI_PATH_QUERY, 1);
        uriMatcher.addURI(URI_AUTHORITY, null, 2);
    }

    @Inject
    AppCMSSearchUrlData appCMSSearchUrlData;

    @Inject
    AppCMSSearchCall appCMSSearchCall;
    private Gson gson;
    private OkHttpClient client;

    @Override
    public boolean onCreate() {
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
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
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
                //Log.d(TAG, "Performing a search of Viewlift films");
                if (selectionArgs != null &&
                        selectionArgs.length > 0 &&
                        !TextUtils.isEmpty(appCMSSearchUrlData.getBaseUrl()) &&
                        !TextUtils.isEmpty(appCMSSearchUrlData.getSiteName())) {
                    String url = getContext().getString(R.string.app_cms_search_api_url,
                            appCMSSearchUrlData.getBaseUrl(),
                            appCMSSearchUrlData.getSiteName(),
                            selectionArgs[0]);
                    //Log.d(TAG, "Search URL: " + url);
                    try {
                        List<AppCMSSearchResult> searchResultList = appCMSSearchCall.call(appCMSSearchUrlData.getApiKey(), url);

                        if (searchResultList != null) {
                            //Log.d(TAG, "Search results received (" + searchResultList.size() + "): ");
                            cursor = new MatrixCursor(SUGGESTION_COLUMN_NAMES, searchResultList.size());

                            for (int i = 0; i < searchResultList.size(); i++) {
                                Uri permalinkUri = Uri.parse(searchResultList.get(i).getGist().getPermalink());
                                String filmUri = permalinkUri.getLastPathSegment();
                                String title = searchResultList.get(i).getGist().getTitle();
                                String runtime = String.valueOf(searchResultList.get(i).getGist().getRuntime());
                                String contentType = searchResultList.get(i).getGist().getContentType();

                                String searchHintResult = searchResultList.get(i).getGist().getTitle() +
                                        "," +
                                        runtime +
                                        "," +
                                        filmUri +
                                        "," +
                                        permalinkUri +
                                        "," +
                                        contentType;
                                Object[] rowResult = {i, title, runtime, searchHintResult, contentType};

                                cursor.addRow(rowResult);
                                //Log.d(TAG, searchResultList.get(i).getGist().getTitle());
                                //Log.d(TAG, String.valueOf(searchResultList.get(i).getGist().getRuntime())
//                                        + " seconds");
                            }
                        } else {
                            //Log.d(TAG, "No search results found");
                        }
                    } catch (Exception e) {
                        //Log.e(TAG, "Received exception: " + e.getMessage());
                    }
                } else {
                    //Log.d(TAG, "Could not retrieved results - search content provider has not been injected");
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
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

    private boolean needInjection() {
        return appCMSSearchCall == null || appCMSSearchUrlData == null;
    }
}
