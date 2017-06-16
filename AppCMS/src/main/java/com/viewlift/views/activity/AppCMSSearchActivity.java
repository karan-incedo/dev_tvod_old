package com.viewlift.views.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.search.AppCMSSearchResult;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.network.modules.AppCMSSearchUrlData;
import com.viewlift.models.network.rest.AppCMSSearchCall;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSSearchItemAdapter;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;
import rx.Observable;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 6/12/17.
 */

public class AppCMSSearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    @Inject
    AppCMSSearchUrlData appCMSSearchUrlData;
    @Inject
    AppCMSSearchCall appCMSSearchCall;

    private SearchView appCMSSearchView;
    private AppCMSSearchItemAdapter appCMSSearchItemAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView appCMSSearchResultsView = (RecyclerView) findViewById(R.id.app_cms_search_results);
        appCMSSearchItemAdapter =
                new AppCMSSearchItemAdapter(((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter(),
                        null);
        appCMSSearchResultsView.setAdapter(appCMSSearchItemAdapter);
        appCMSSearchResultsView.requestFocus();

        AppCMSMain appCMSMain =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter().getAppCMSMain();
        appCMSSearchResultsView.setBackgroundColor(Color.parseColor(appCMSMain.getBrand()
                .getGeneral()
                .getBackgroundColor()));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        appCMSSearchView = (SearchView) findViewById(R.id.app_cms_searchbar);
        appCMSSearchView.setQueryHint(getString(R.string.search_films));
        appCMSSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        appCMSSearchView.setIconifiedByDefault(true);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        AppCMSPresenter appCMSPresenter =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter();
        if (appCMSSearchUrlData == null || appCMSSearchCall == null) {
            appCMSPresenter.getAppCMSSearchUrlComponent().inject(this);
            if (appCMSSearchUrlData == null || appCMSSearchCall == null) {
                return;
            }
        }

        if (Intent.ACTION_VIEW.equals(intent.getAction()) ||
                Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchTerm = null;
            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                searchTerm = intent.getStringExtra(SearchManager.QUERY);
            } else {
                searchTerm = intent.getDataString();
            }
            if (!TextUtils.isEmpty(searchTerm)) {
                searchTerm = searchTerm.replace(" ", "_");
                final String url = getString(R.string.app_cms_search_api_url,
                        appCMSSearchUrlData.getBaseUrl(),
                        appCMSSearchUrlData.getSiteName(),
                        searchTerm);
                new SearchAsyncTask(new Action1<List<AppCMSSearchResult>>() {
                    @Override
                    public void call(List<AppCMSSearchResult> data) {
                        appCMSSearchItemAdapter.setData(data);
                    }
                }, appCMSSearchCall).execute(url);
            }
        }
    }

    private static class SearchAsyncTask extends AsyncTask<String, Void, List<AppCMSSearchResult>> {
        final Action1<List<AppCMSSearchResult>> dataReadySubscriber;
        final AppCMSSearchCall appCMSSearchCall;

        SearchAsyncTask(Action1<List<AppCMSSearchResult>> dataReadySubscriber,
                               AppCMSSearchCall appCMSSearchCall) {
            this.dataReadySubscriber = dataReadySubscriber;
            this.appCMSSearchCall = appCMSSearchCall;
        }

        @Override
        protected List<AppCMSSearchResult> doInBackground(String... params) {
            if (params.length > 0) {
                try {
                    return appCMSSearchCall.call(params[0]);
                } catch (IOException e) {
                    Log.e(TAG, "I/O Error retrieving search data from URL: " + params[0]);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<AppCMSSearchResult> result) {
            Observable.just(result).subscribe(dataReadySubscriber);
        }
    }
}
