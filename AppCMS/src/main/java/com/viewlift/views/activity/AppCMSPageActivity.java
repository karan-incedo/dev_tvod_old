package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.Primary;
import com.viewlift.models.data.appcms.ui.android.User;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.fragments.AppCMSPageFragment;

import java.util.Stack;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSPageActivity extends AppCompatActivity implements AppCMSPageFragment.OnPageCreationError {
    private static final String TAG = "AppCMSPageActivity";

    private static final int MAX_BOTTOM_NAV_ITEMS = 3;

    private AppCMSPresenter appCMSPresenter;
    private Stack<AppCMSBinder> appCMSBinderStack;
    private BroadcastReceiver presenterActionReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appcms_page);

        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        appCMSPresenter.setCurrentActivity(this);
        if (!appCMSPresenter.sendCloseOthersAction()) {
            Log.w(TAG, "Could not close other activities");
        }

        appCMSBinderStack = new Stack<>();

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
        handleAppCMSBinder((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key)));

        presenterActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handlePresenterAction(intent);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(presenterActionReceiver);
    }

    @Override
    public void onError() {
        setFinishResult(RESULT_CANCELED);
        finish();
    }

    private void handleAppCMSBinder(AppCMSBinder appCMSBinder) {
        appCMSBinderStack.push(appCMSBinder);

        if (!appCMSBinder.isAppbarPresent()) {
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_cms_appbarlayout);
            appBarLayout.setVisibility(View.GONE);
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.app_cms_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment appCMSPageFragment = AppCMSPageFragment.newInstance(this, appCMSBinder);
        fragmentTransaction.replace(R.id.app_cms_fragment, appCMSPageFragment, appCMSBinder.getPageId());
        fragmentTransaction.commit();
    }

    private void handlePresenterAction(Intent intent) {
        Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
        handleAppCMSBinder((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key)));
    }

    private void setFinishResult(int resultCode) {
        Intent resultIntent = new Intent();
        Bundle args = new Bundle();
        args.putBinder(getString(R.string.app_cms_binder_key), appCMSBinderStack.pop());
        resultIntent.putExtra(getString(R.string.app_cms_bundle_key), args);
        setResult(resultCode, resultIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        appCMSBinderStack.pop();
    }
}
