package com.viewlift.mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.viewlift.AppCMSApplication;
import com.viewlift.casting.CastHelper;
import com.viewlift.presenters.AppCMSPresenter;

import com.viewlift.views.activity.AppCMSPageActivity;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.components.AppCMSPresenterComponent;

import com.viewlift.R;
import com.viewlift.views.customviews.BaseView;

public class AppCMSLaunchActivity extends AppCompatActivity {
    private static final String TAG = "AppCMSLaunchActivity";

    private Uri searchQuery;
    private CastHelper mCastHelper;
    private BroadcastReceiver presenterCloseActionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        handleIntent(getIntent());

        Log.d(TAG, "Launching application from main.json");
        Log.d(TAG, "Search query (optional): " + searchQuery);
        AppCMSPresenterComponent appCMSPresenterComponent =
                ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent();
        appCMSPresenterComponent.appCMSPresenter().getAppCMSMain(this,
                getString(R.string.app_cms_app_name),
                searchQuery,
                AppCMSPresenter.PlatformType.ANDROID);
        if (!BaseView.isTablet(this)) {
            appCMSPresenterComponent.appCMSPresenter().restrictPortraitOnly();
        }

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

        Log.d(TAG, "onCreate()");
        setCasting();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(presenterCloseActionReceiver);
    }

    private void setCasting() {
        try {
            mCastHelper = CastHelper.getInstance(getApplicationContext());
            mCastHelper.initCastingObj();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing casting: " + e.getMessage());
        }
    }

    public void handleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            final Uri data = intent.getData();
            Log.i(TAG, "Received intent action: " + action);
            if (data != null) {
                Log.i(TAG, "Received intent data: " + data.toString());
                searchQuery = data;
                AppCMSPresenterComponent appCMSPresenterComponent =
                        ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent();
                appCMSPresenterComponent.appCMSPresenter().sendDeepLinkAction(searchQuery);
            }
        }
    }
}
