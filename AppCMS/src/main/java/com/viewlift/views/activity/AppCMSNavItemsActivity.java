package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSNavItemsAdapter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.fragments.AppCMSNavItemsFragment;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/30/17.
 */

public class AppCMSNavItemsActivity extends AppCompatActivity implements AppCMSNavItemsAdapter.OnCloseNavAction {
    private static final String TAG = "AppCMSNavItemsActivity";

    private BroadcastReceiver handoffReceiver;
    private AppCMSPresenter appCMSPresenter;
    private AppCMSBinder appCMSBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_nav);

        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        handoffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra(getString(R.string.close_self_key), true)) {
                    Log.d(TAG, "Closing activity");
                    finish();
                }
            }
        };
        registerReceiver(handoffReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
        handleAppCMSBinder((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(handoffReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        appCMSPresenter.navigateToPage(appCMSBinder.getPageId(),
                appCMSBinder.getPageName(),
                false,
                null);
        finish();
    }

    private void handleAppCMSBinder(AppCMSBinder appCMSBinder) {
        this.appCMSBinder = appCMSBinder;
        handleToolbar(appCMSBinder);
        Fragment appCMSNavigationFragment = AppCMSNavItemsFragment.newInstance(this,
                appCMSBinder);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.app_cms_nav_fragment,
                appCMSNavigationFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private void handleToolbar(AppCMSBinder appCMSBinder) {
        if (!appCMSBinder.isAppbarPresent()) {
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_cms_appbarlayout);
            appBarLayout.setVisibility(View.GONE);
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.app_cms_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setTitle("");
        }
    }

    @Override
    public void closeNavAction() {
        finish();
    }
}
