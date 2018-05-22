package com.viewlift.views.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

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
    private AppCMSDownloadQualityFragment downloadQualityFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.activity_download_quality);
        appCMSPresenter = ((AppCMSApplication) getApplication()).getAppCMSPresenterComponent().appCMSPresenter();

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(getString(R.string.app_cms_download_setting_bundle_key));
        try {
            binder = (AppCMSDownloadQualityBinder)
                    bundleExtra.getBinder(getString(R.string.app_cms_download_setting_binder_key));
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

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
            //Log.e(TAG, "Failed to add Fragment to back stack");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloadQualityFragment = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
