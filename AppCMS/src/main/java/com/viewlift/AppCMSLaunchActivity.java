package com.viewlift;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.components.AppCMSPresenterComponent;

import snagfilms.com.air.appcms.R;

public class AppCMSLaunchActivity extends AppCompatActivity {
    private static final String TAG = "AppCMSLaunchActivity";

    private AppCMSPresenterComponent appCMSPresenterComponent;

    private BroadcastReceiver handoffReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        handoffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra(getString(R.string.close_self_key), true)) {
                    Log.d(TAG, "Closing activity");
                    finish();
                }
            }
        };

        appCMSPresenterComponent = ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent();
        registerReceiver(handoffReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));
        appCMSPresenterComponent.appCMSPresenter().getAppCMSMain(this, getString(R.string.app_cms_app_name));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(handoffReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppCMSPresenter.PRESENTER_NAVIGATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
