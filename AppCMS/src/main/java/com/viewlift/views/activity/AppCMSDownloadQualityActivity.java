package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSDownloadQualityBinder;
import com.viewlift.views.fragments.AppCMSDownloadQualityFragment;

/**
 * Created by sandeep.singh on 7/28/2017.
 */

public class AppCMSDownloadQualityActivity extends AppCompatActivity {

    private static final String TAG = AppCMSDownloadQualityActivity.class.getSimpleName();
    private AppCMSPresenter appCMSPresenter;
    private AppCMSDownloadQualityBinder binder;
    private BroadcastReceiver handoffReceiver;
    private AppCMSDownloadQualityFragment downloadQualityFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        handoffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               if (intent.getBooleanExtra(getString(R.string.close_self_key),true)) {
                    Log.d(TAG, "Closing activity");
                    finish();
                }
                //finish();
            }
        };

        registerReceiver(handoffReceiver, new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));
        appCMSPresenter = ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter();

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(getString(R.string.app_cms_download_setting_bundle_key));
        binder = (AppCMSDownloadQualityBinder)
                bundleExtra.getBinder(getString(R.string.app_cms_download_setting_binder_key));
        setContentView(R.layout.activity_download_quality);
        //Restore the fragment's instance
        downloadQualityFragment = (AppCMSDownloadQualityFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG);
        if (downloadQualityFragment == null) {
            createFragment(binder);
        }
    }

    private void createFragment(AppCMSDownloadQualityBinder appCMSBinder) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            downloadQualityFragment = AppCMSDownloadQualityFragment.newInstance(this, appCMSBinder);
            fragmentTransaction.replace(R.id.app_cms_fragment, downloadQualityFragment, TAG);
            fragmentTransaction.addToBackStack(TAG);
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Failed to add Fragment to back stack");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(handoffReceiver);
        downloadQualityFragment = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
