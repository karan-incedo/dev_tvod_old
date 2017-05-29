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
import android.widget.LinearLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.Primary;
import com.viewlift.models.data.appcms.ui.android.User;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.NavBarItemView;
import com.viewlift.views.fragments.AppCMSPageFragment;

import java.util.List;
import java.util.Stack;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSPageActivity extends AppCompatActivity implements AppCMSPageFragment.OnPageCreationError {
    private static final String TAG = "AppCMSPageActivity";

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
        handleAppCMSBinder((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key)),
                true);

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

    private void handleAppCMSBinder(AppCMSBinder appCMSBinder, boolean firstFragment) {
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

        LinearLayout appCMSTabNavContainer =
                (LinearLayout) findViewById(R.id.app_cms_tab_nav_container);
        Navigation navigation = appCMSBinder.getNavigation();
        if (navigation.getPrimary().size() == 0) {
            appCMSTabNavContainer.setVisibility(View.GONE);
        } else {
            int totalNavItemCnt = 0;
            for (int i = 0;
                 i < navigation.getPrimary().size() && totalNavItemCnt < appCMSTabNavContainer.getChildCount() - 1;
                 i++, totalNavItemCnt++) {
                Primary primary = navigation.getPrimary().get(i);
                StringBuffer iconName = new StringBuffer();
                iconName.append(primary.getDisplayedPath().toLowerCase().replaceAll(" ", "_"));
                iconName.append(primary.getUrl().replace("/", "_"));

                NavBarItemView navBarItemView = (NavBarItemView) appCMSTabNavContainer.getChildAt(i);
                navBarItemView.setImage(iconName.toString());
                navBarItemView.setLabel(primary.getTitle());
            }
            if (appCMSBinder.isUserLoggedIn()) {
                for (int i = 0;
                     i < navigation.getUser().size() && totalNavItemCnt < appCMSTabNavContainer.getChildCount()  - 1;
                     i++, totalNavItemCnt++) {
                    User user = navigation.getUser().get(i);
                    StringBuffer iconName = new StringBuffer();
                    iconName.append(user.getDisplayedName().toLowerCase().replaceAll(" ", "_"));
                    iconName.append(user.getUrl().replaceAll("/", "_"));

                    NavBarItemView navBarItemView = (NavBarItemView) appCMSTabNavContainer.getChildAt(i);
                    navBarItemView.setImage(iconName.toString());
                    navBarItemView.setLabel(user.getTitle());
                }
            }
            NavBarItemView menuNavBarItemView =
                    (NavBarItemView) appCMSTabNavContainer.getChildAt(appCMSTabNavContainer.getChildCount() - 1);
            menuNavBarItemView.setImage(getString(R.string.app_cms_menu_icon_name));
            menuNavBarItemView.setLabel(getString(R.string.app_cms_menu_label));
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment appCMSPageFragment = AppCMSPageFragment.newInstance(this, appCMSBinder);
        if (firstFragment) {
            fragmentTransaction.replace(R.id.app_cms_fragment,
                    appCMSPageFragment,
                    String.valueOf(appCMSBinderStack.size()));
        } else {
            fragmentTransaction.replace(R.id.app_cms_fragment,
                    appCMSPageFragment,
                    appCMSBinder.getPageId());
            fragmentTransaction.addToBackStack(String.valueOf(appCMSBinderStack.size()));
        }
        fragmentTransaction.commit();
    }

    private void handlePresenterAction(Intent intent) {
        Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
        handleAppCMSBinder((AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key)),
                false);
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
        AppCMSBinder currentAppCMSBinder = appCMSBinderStack.pop();
        super.onBackPressed();
    }
}
