package com.viewlift.views.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private TextView noResultsTextview;

    private AppCMSSearchItemAdapter appCMSSearchItemAdapter;
    private BroadcastReceiver handoffReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RecyclerView appCMSSearchResultsView = (RecyclerView) findViewById(R.id.app_cms_search_results);
        appCMSSearchItemAdapter =
                new AppCMSSearchItemAdapter(this,
                        ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent()
                                .appCMSPresenter(),
                        null);
        appCMSSearchResultsView.setAdapter(appCMSSearchItemAdapter);
        appCMSSearchResultsView.requestFocus();

        AppCMSMain appCMSMain =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent()
                        .appCMSPresenter()
                        .getAppCMSMain();

        handoffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sendingPage = intent.getStringExtra(getString(R.string.app_cms_closing_page_name));
                if (intent.getBooleanExtra(getString(R.string.close_self_key), true) ||
                        sendingPage == null ||
                        !getString(R.string.app_cms_navigation_page_tag).equals(sendingPage)) {
                    Log.d(TAG, "Closing activity");
                    finish();
                }
            }
        };
        registerReceiver(handoffReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        appCMSSearchView = (SearchView) findViewById(R.id.app_cms_searchbar);
        appCMSSearchView.setQueryHint(getString(R.string.search_films));
        appCMSSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        appCMSSearchView.setIconifiedByDefault(false);

        LinearLayout appCMSSearchResultsContainer =
                (LinearLayout) findViewById(R.id.app_cms_search_results_container);
        if (appCMSMain.getBrand() != null &&
                appCMSMain.getBrand().getGeneral() != null &&
                !TextUtils.isEmpty(appCMSMain.getBrand().getGeneral().getBackgroundColor())) {
            appCMSSearchResultsContainer.setBackgroundColor(Color.parseColor(appCMSMain.getBrand()
                    .getGeneral()
                    .getBackgroundColor()));
        }

        noResultsTextview = (TextView) findViewById(R.id.no_results_textview);

        ImageButton appCMSCloseButton = (ImageButton) findViewById(R.id.app_cms_close_button);
        appCMSCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(handoffReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void handleIntent(Intent intent) {
        final AppCMSPresenter appCMSPresenter =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter();
        if (appCMSSearchUrlData == null || appCMSSearchCall == null) {
            appCMSPresenter.getAppCMSSearchUrlComponent().inject(this);
            if (appCMSSearchUrlData == null || appCMSSearchCall == null) {
                return;
            }
        }

        appCMSPresenter.cancelInternalEvents();
        appCMSPresenter.pushActionInternalEvents(getString(R.string.app_cms_action_search_key));

        if (Intent.ACTION_VIEW.equals(intent.getAction()) ||
                Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchTerm = null;
            String queryTerm = null;

            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                String[] searchHintResult = intent.getDataString().split(",");
                queryTerm = searchHintResult[0];
                searchTerm = searchHintResult[1];
            } else {
                queryTerm = intent.getStringExtra(SearchManager.QUERY);
                searchTerm = queryTerm;
            }
            if (!TextUtils.isEmpty(searchTerm)) {
                appCMSSearchView.setQuery(queryTerm, false);
                final String url = getString(R.string.app_cms_search_api_url,
                        appCMSSearchUrlData.getBaseUrl(),
                        appCMSSearchUrlData.getSiteName(),
                        searchTerm);
                Log.d(TAG, "Search URL: " + url);
                new SearchAsyncTask(new Action1<List<AppCMSSearchResult>>() {
                    @Override
                    public void call(List<AppCMSSearchResult> data) {
                        appCMSSearchItemAdapter.setData(data);
                        updateNoResultsDisplay(appCMSPresenter, data);
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

    private void updateNoResultsDisplay(AppCMSPresenter appCMSPresenter,
                                        List<AppCMSSearchResult> data) {
        if (data == null || data.size() == 0) {
            noResultsTextview.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                    .getBrand()
                    .getGeneral()
                    .getTextColor()));
            noResultsTextview.setVisibility(View.VISIBLE);
        } else {
            noResultsTextview.setVisibility(View.GONE);
        }
    }
}
