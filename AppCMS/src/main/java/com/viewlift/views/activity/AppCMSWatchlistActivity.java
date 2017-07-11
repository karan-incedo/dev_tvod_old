package com.viewlift.views.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.watchlist.AppCMSWatchlistResult;
import com.viewlift.models.network.rest.AppCMSWatchlistCall;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSWatchlistItemAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import snagfilms.com.air.appcms.R;

public class AppCMSWatchlistActivity extends AppCompatActivity {

    private static final String TAG = "WatchlistActivityTAG_";

    @BindView(R.id.watchlist_results_recylerview)
    RecyclerView appCMSWatchlistResultsView;

    @Inject
    AppCMSWatchlistCall appCMSWatchlistCall;

    private AppCMSWatchlistItemAdapter appCMSWatchlistItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        ButterKnife.bind(this);

        appCMSWatchlistItemAdapter = new AppCMSWatchlistItemAdapter(this,
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent()
                        .appCMSPresenter(),
                null);

        appCMSWatchlistResultsView.setAdapter(appCMSWatchlistItemAdapter);

        AppCMSMain appCMSMain =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent()
                        .appCMSPresenter()
                        .getAppCMSMain();

        LinearLayout appCMSWatchListResultsContainer =
                (LinearLayout) findViewById(R.id.app_cms_watchlist_results_container);

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        final AppCMSPresenter appCMSPresenter =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent()
                        .appCMSPresenter();

        Log.d(TAG, "handleIntent-getIntent: " + intent);

        final String url = getString(R.string.app_cms_watchlist_api_url,
                appCMSPresenter.getAppCMSMain().getApiBaseUrl(),
                appCMSPresenter.getAppCMSMain().getSite(),
                null);
        Log.d(TAG, "handleIntent: " + url);

        new WatchlistAsyncTask(new Action1<List<AppCMSWatchlistResult>>() {
            @Override
            public void call(List<AppCMSWatchlistResult> resultsData) {
                appCMSWatchlistItemAdapter.setData(resultsData);
            }
        }, appCMSWatchlistCall).execute(url);
    }

    private static class WatchlistAsyncTask extends AsyncTask<String, Void,
            List<AppCMSWatchlistResult>> {

        final Action1<List<AppCMSWatchlistResult>> watchlistReadySubscriber;
        final AppCMSWatchlistCall appCMSWatchlistCall;

        private WatchlistAsyncTask(Action1<List<AppCMSWatchlistResult>> watchlistReadySubscriber,
                                   AppCMSWatchlistCall appCMSWatchlistCall) {
            this.watchlistReadySubscriber = watchlistReadySubscriber;
            this.appCMSWatchlistCall = appCMSWatchlistCall;
        }

        @Override
        protected List<AppCMSWatchlistResult> doInBackground(String... params) {
            if (params.length > 0) {
//                try {
//                    return appCMSWatchlistCall.call(params[0]);
//                } catch (IOException e) {
//                    Log.e(TAG, "doInBackground: " + params[0]);
//                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<AppCMSWatchlistResult> appCMSWatchlistResults) {
            Observable.just(appCMSWatchlistResults).subscribe(watchlistReadySubscriber);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
