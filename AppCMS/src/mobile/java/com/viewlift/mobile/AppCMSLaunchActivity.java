package com.viewlift.mobile;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;

import com.viewlift.views.components.AppCMSPresenterComponent;

import com.viewlift.R;

public class AppCMSLaunchActivity extends AppCompatActivity {
    private static final String TAG = "AppCMSLaunchActivity";

    private Uri searchQuery;

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
        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
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
