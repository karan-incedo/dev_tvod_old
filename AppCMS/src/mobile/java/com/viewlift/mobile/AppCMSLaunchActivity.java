package com.viewlift.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.appsflyer.AppsFlyerLib;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.urbanairship.UAirship;
import com.viewlift.AppCMSApplication;
import com.viewlift.Utils;
import com.viewlift.casting.CastHelper;
import com.viewlift.mobile.imageutils.FrescoImageLoader;
import com.viewlift.presenters.AppCMSPresenter;

import com.viewlift.views.components.AppCMSPresenterComponent;

import com.viewlift.R;
import com.viewlift.views.customviews.BaseView;

import com.google.android.gms.iid.InstanceID;
import com.viewlift.views.utilities.ImageUtils;

public class AppCMSLaunchActivity extends AppCompatActivity {
    private static final String TAG = "AppCMSLaunchActivity";

    private Uri searchQuery;
    private CastHelper mCastHelper;
    private BroadcastReceiver presenterCloseActionReceiver;

    private ConnectivityManager connectivityManager;
    private BroadcastReceiver networkConnectedReceiver;
    private boolean appStartWithNetworkConnected;
    private boolean forceReloadFromNetwork;

    private AppCMSPresenterComponent appCMSPresenterComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!BaseView.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_launch);

        setFullScreenFocus();

        if (getApplication() instanceof AppCMSApplication) {
            appCMSPresenterComponent =
                    ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent();
        }

        handleIntent(getIntent());

        presenterCloseActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION)) {
                    finish();
                }
            }
        };

        registerReceiver(presenterCloseActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkConnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (!appStartWithNetworkConnected && isConnected && appCMSPresenterComponent != null) {
                    appCMSPresenterComponent.appCMSPresenter().getAppCMSMain(AppCMSLaunchActivity.this,
                            Utils.getProperty("SiteId", getApplicationContext()),
                            searchQuery,
                            AppCMSPresenter.PlatformType.ANDROID,
                            false);
                } else if (!isConnected) {
                    appStartWithNetworkConnected = false;
                }
            }
        };

        setCasting();

        new Thread(() -> {
            if (appCMSPresenterComponent != null) {
                appCMSPresenterComponent.appCMSPresenter().setInstanceId(InstanceID.getInstance(this).getId());
            }

            Fresco.initialize(getApplicationContext());

            ImageUtils.registerImageLoader(new FrescoImageLoader());

            UAirship.shared().getPushManager().setUserNotificationsEnabled(true);

            AppsFlyerLib.getInstance().startTracking(getApplication());
        });
        //Log.i(TAG, "UA Device Channel ID: " + UAirship.shared().getPushManager().getChannelId());

        appCMSPresenterComponent.appCMSPresenter().setAppHomeActivityCreated(true);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(presenterCloseActionReceiver);
        } catch (Exception e) {
            //Log.e(TAG, "Failed to unregister Close Action Receiver");
        }
    }

    private void setCasting() {
        try {
            mCastHelper = CastHelper.getInstance(getApplicationContext());
            mCastHelper.initCastingObj();
        } catch (Exception e) {
            //Log.e(TAG, "Error initializing casting: " + e.getMessage());
        }
    }

    public void handleIntent(Intent intent) {
        if (intent != null) {
            try {
                final Uri data = intent.getData();
                //Log.i(TAG, "Received intent action: " + action);
                if (data != null) {
                    //Log.i(TAG, "Received intent data: " + data.toString());
                    searchQuery = data;
                    if (appCMSPresenterComponent != null) {
                        appCMSPresenterComponent.appCMSPresenter().sendDeepLinkAction(searchQuery);
                    }
                }

                forceReloadFromNetwork = intent.getBooleanExtra(getString(R.string.force_reload_from_network_key), false);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        setFullScreenFocus();
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        appStartWithNetworkConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (appStartWithNetworkConnected) {
            registerReceiver(networkConnectedReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        if (appCMSPresenterComponent != null) {
            try {
                if (appCMSPresenterComponent.appCMSPresenter().isLaunched()) {
                    appCMSPresenterComponent.appCMSPresenter().sendCloseOthersAction(null, true, true);
                } else {
                    appCMSPresenterComponent.appCMSPresenter().getAppCMSMain(this,
                            Utils.getProperty("SiteId", getApplicationContext()),
                            searchQuery,
                            AppCMSPresenter.PlatformType.ANDROID,
                            false);
                }
            } catch (Exception e) {
                //Log.e(TAG, "Caught exception retrieving AppCMS data: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(networkConnectedReceiver);
        } catch (Exception e) {
            //Log.e(TAG, "Failed to unregister network receiver");
        }
    }

    private void setFullScreenFocus() {
        getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter().sendCloseOthersAction("Error Screen", false, false);
        } catch (Exception e) {
            //Log.e(TAG, "Caught exception attempting to send close others action: " + e.getMessage());
        }
        finish();
    }
}
