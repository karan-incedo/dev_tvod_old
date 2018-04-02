package com.viewlift.views.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.viewlift.AppCMSApplication;
import com.viewlift.Audio.MusicService;
import com.viewlift.R;
import com.viewlift.models.data.appcms.search.AppCMSSearchResult;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.network.modules.AppCMSSearchUrlData;
import com.viewlift.models.network.rest.AppCMSSearchCall;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSSearchItemAdapter;
import com.viewlift.views.adapters.SearchSuggestionsAdapter;
import com.viewlift.views.customviews.BaseView;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;

/*
 * Created by viewlift on 6/12/17.
 */

public class AppCMSSearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private final String FIREBASE_SEARCH_EVENT = "search";
    private final String FIREBASE_SEARCH_TERM = "search_term";
    private final String FIREBASE_SCREEN_VIEW_EVENT = "screen_view";
    private final String FIREBASE_SCREEN_NAME = "Search Result Screen";

    @BindView(R.id.app_cms_search_results)
    RecyclerView appCMSSearchResultsView;

    @BindView(R.id.search_page_loading_progressbar)
    ProgressBar progressBar;

    @BindView(R.id.app_cms_searchbar)
    SearchView appCMSSearchView;

    @BindView(R.id.app_cms_search_results_container)
    LinearLayout appCMSSearchResultsContainer;

    @BindView(R.id.no_results_textview)
    TextView noResultsTextview;

    @BindView(R.id.app_cms_close_button)
    ImageButton appCMSCloseButton;

    @Inject
    AppCMSSearchUrlData appCMSSearchUrlData;

    @Inject
    AppCMSSearchCall appCMSSearchCall;
    private MediaBrowserCompat mMediaBrowser;

    private String searchQuery;
    private AppCMSSearchItemAdapter appCMSSearchItemAdapter;
    private BroadcastReceiver handoffReceiver;
    private AppCMSPresenter appCMSPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        appCMSSearchItemAdapter = new AppCMSSearchItemAdapter(this,
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent()
                        .appCMSPresenter(),
                null);
        appCMSSearchResultsView.setAdapter(appCMSSearchItemAdapter);
        sendFirebaseAnalyticsEvents();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        @SuppressWarnings("ConstantConditions")
        SearchSuggestionsAdapter searchSuggestionsAdapter = new SearchSuggestionsAdapter(this,
                null,
                searchManager.getSearchableInfo(getComponentName()),
                true);

        appCMSSearchResultsView.requestFocus();

        AppCMSMain appCMSMain =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent()
                        .appCMSPresenter()
                        .getAppCMSMain();

        if (!BaseView.isTablet(this)) {
            ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter().restrictPortraitOnly();
        }

        handoffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null &&
                        intent.getStringExtra(getString(R.string.app_cms_package_name_key)) != null &&
                        !intent.getStringExtra(getString(R.string.app_cms_package_name_key)).equals(getPackageName())) {
                    return;
                }
                String sendingPage = intent.getStringExtra(getString(R.string.app_cms_closing_page_name));
                if (intent.getBooleanExtra(getString(R.string.close_self_key), true) ||
                        sendingPage == null ||
                        !getString(R.string.app_cms_navigation_page_tag).equals(sendingPage)) {
                    //Log.d(TAG, "Closing activity");
                    finish();
                }
            }
        };
        registerReceiver(handoffReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));

        appCMSSearchView.setQueryHint(getString(R.string.search_films));
        //noinspection ConstantConditions
        appCMSSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        appCMSSearchView.setSuggestionsAdapter(searchSuggestionsAdapter);
        appCMSSearchView.setIconifiedByDefault(false);
        TextView searchText = (TextView) appCMSSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        appCMSPresenter.setCursorDrawableColor((EditText) searchText);

        appCMSSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    appCMSSearchItemAdapter.setData(null);
                    updateNoResultsDisplay(appCMSPresenter, null);
                }
                return false;
            }
        });

        appCMSSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) appCMSSearchView.getSuggestionsAdapter().getItem(position);
                String[] searchHintResult = cursor.getString(cursor.getColumnIndex("suggest_intent_data")).split(",");
                appCMSPresenter.searchSuggestionClick(searchHintResult);
                finish();
                return true;
            }
        });
        if (appCMSMain != null &&
                appCMSMain.getBrand() != null &&
                appCMSMain.getBrand().getGeneral() != null &&
                !TextUtils.isEmpty(appCMSPresenter.getAppBackgroundColor())) {
            appCMSSearchResultsContainer.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppBackgroundColor()));
        }

        appCMSCloseButton.setOnClickListener(v -> finish());

        handleIntent(getIntent());
        appCMSSearchItemAdapter.handleProgress((object) -> progressBar.setVisibility(View.VISIBLE));

        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicService.class), mConnectionCallback, null);

    }

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    try {
                        MediaControllerCompat mediaController = new MediaControllerCompat(
                                AppCMSSearchActivity.this, mMediaBrowser.getSessionToken());
                        MediaControllerCompat.setMediaController(AppCMSSearchActivity.this, mediaController);
                    } catch (RemoteException e) {
                    }
                }
            };

    @Override
    public void onStart() {
        super.onStart();
        if (mMediaBrowser != null) {
            mMediaBrowser.connect();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaBrowser != null) {
            mMediaBrowser.disconnect();
        }

    }

    private void sendFirebaseAnalyticsEvents() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SCREEN_VIEW_EVENT, FIREBASE_SCREEN_NAME);
        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent().appCMSPresenter();
        if (appCMSPresenter.getmFireBaseAnalytics() != null) {
            //Logs an app event.
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
            //Sets whether analytics collection is enabled for this app on this device.
            appCMSPresenter.getmFireBaseAnalytics().setAnalyticsCollectionEnabled(true);
        }
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
        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent().appCMSPresenter();
        appCMSPresenter.setNavItemToCurrentAction(this);
        finish();
    }

    @SuppressWarnings("ConstantConditions")
    private void handleIntent(Intent intent) {
        final AppCMSPresenter appCMSPresenter =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter();
        if ((appCMSSearchUrlData == null || appCMSSearchCall == null) &&
                appCMSPresenter.getAppCMSSearchUrlComponent() != null) {
            appCMSPresenter.getAppCMSSearchUrlComponent().inject(this);
            if (appCMSSearchUrlData == null || appCMSSearchCall == null) {
                return;
            }
        }

        appCMSPresenter.cancelInternalEvents();
        appCMSPresenter.pushActionInternalEvents(getString(R.string.app_cms_action_search_key));

        if (Intent.ACTION_VIEW.equals(intent.getAction()) ||
                Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchTerm;
            String queryTerm;

            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                String[] searchHintResult = intent.getDataString().split(",");
                appCMSPresenter.searchSuggestionClick(searchHintResult);

            } else {
                queryTerm = intent.getStringExtra(SearchManager.QUERY);
                searchTerm = queryTerm;
                if (!TextUtils.isEmpty(searchTerm) && appCMSSearchUrlData != null) {
                    appCMSSearchView.setQuery(queryTerm, false);
                    //Send Search Term in Firebase Analytics Logs
                    Bundle bundle = new Bundle();
                    bundle.putString(FIREBASE_SEARCH_TERM, queryTerm);
                    if (appCMSPresenter.getmFireBaseAnalytics() != null)
                        appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_SEARCH_EVENT, bundle);
                    final String url = getString(R.string.app_cms_search_api_url,
                            appCMSSearchUrlData.getBaseUrl(),
                            appCMSSearchUrlData.getSiteName(),
                            searchTerm);
                    //Log.d(TAG, "Search URL: " + url);
                    new SearchAsyncTask(data -> {
                        if (data != null) {
                            appCMSSearchItemAdapter.setData(data);
                            updateNoResultsDisplay(appCMSPresenter, data);
                        }
                    }, appCMSSearchCall, appCMSPresenter.getApiKey()).execute(url,
                            appCMSPresenter.getApiKey());
                }
            }
        }
    }

    private void updateNoResultsDisplay(AppCMSPresenter appCMSPresenter,
                                        List<AppCMSSearchResult> data) {
        if (data == null || data.isEmpty()) {
            try {
                if (appCMSPresenter.getAppCMSMain().getBrand() != null) {
                    noResultsTextview.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain()
                            .getBrand()
                            .getGeneral()
                            .getTextColor()));
                    noResultsTextview.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                noResultsTextview.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                noResultsTextview.setVisibility(View.VISIBLE);
            }
        } else {
            noResultsTextview.setVisibility(View.GONE);
        }
    }

    private static class SearchAsyncTask extends AsyncTask<String, Void, List<AppCMSSearchResult>> {
        final Action1<List<AppCMSSearchResult>> dataReadySubscriber;
        final AppCMSSearchCall appCMSSearchCall;
        final String apiKey;

        SearchAsyncTask(Action1<List<AppCMSSearchResult>> dataReadySubscriber,
                        AppCMSSearchCall appCMSSearchCall,
                        String apiKey) {
            this.dataReadySubscriber = dataReadySubscriber;
            this.appCMSSearchCall = appCMSSearchCall;
            this.apiKey = apiKey;
        }

        @Override
        protected List<AppCMSSearchResult> doInBackground(String... params) {
            if (params.length > 1) {
                try {
                    return appCMSSearchCall.call(params[1], params[0]);
                } catch (IOException e) {
                    //Log.e(TAG, "I/O DialogType retrieving search data from URL: " + params[0]);
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
