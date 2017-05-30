package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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

    private ProgressBar appCMSPageLoading;
    private FrameLayout appCMSFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appcms_page);

        appCMSPageLoading = (ProgressBar) findViewById(R.id.app_cms_page_loading);
        appCMSFragment = (FrameLayout) findViewById(R.id.app_cms_fragment);

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
        AppCMSBinder appCMSBinder =
                (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
        handleAppCMSBinder(appCMSBinder, true);

        if (savedInstanceState == null) {
            appCMSBinderStack.push(appCMSBinder);
        }

        presenterActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION)) {
                    handlePresenterAction(intent);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION)) {
                    pageLoading(true);
                }
            }
        };
        Log.d(TAG, "onCreate() Binder stack size: " + appCMSBinderStack.size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION));
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
        Log.d(TAG, "onResume() Binder stack size: " + appCMSBinderStack.size());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(presenterActionReceiver);
        Log.d(TAG, "onPause() Binder stack size: " + appCMSBinderStack.size());
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed() " + appCMSBinderStack.size());
        AppCMSBinder currentAppCMSBinder = appCMSBinderStack.pop();
        if (appCMSBinderStack.size() > 0) {
            handleToolbar(appCMSBinderStack.peek());
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving instance state");
        int backstackCnt = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backstackCnt; i++) {
            String backstackEntryName =
                    getSupportFragmentManager().getBackStackEntryAt(i).getName();
            Log.d(TAG, "Instance state backstack entry: " + backstackEntryName);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(backstackEntryName);
            if (fragment != null) {
                Log.d(TAG, "Instance state saving fragment " + backstackEntryName);
                getSupportFragmentManager().putFragment(outState, backstackEntryName, fragment);
            }
        }
        int appCMSBinderStackSize = appCMSBinderStack.size();
        outState.putInt(getString(R.string.app_cms_binder_stack_size_key), appCMSBinderStackSize);
        Log.d(TAG, "Instance state saving " + appCMSBinderStackSize + " binder elements");
        for (int i = appCMSBinderStackSize - 1; i >= 0; i--) {
            outState.putBinder(getString(R.string.app_cms_binder_stack_element_key, i), appCMSBinderStack.get(i));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "Restoring instance state");
        int backstackCnt = getSupportFragmentManager().getBackStackEntryCount();
        Log.d(TAG, "Instance state backstack count: " + backstackCnt);
        for (int i = 0; i < backstackCnt; i++) {
            String backstackEntryName =
                    getSupportFragmentManager().getBackStackEntryAt(i).getName();
            Log.d(TAG, "Instance state backstack entry " +
                    i +
                    ": " +
                    backstackEntryName);
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, backstackEntryName);
            if (fragment != null) {
                Log.d(TAG, "Instance state fragment with tag " + fragment.getTag());
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.app_cms_fragment,
                        fragment,
                        backstackEntryName);
//                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                fragmentTransaction.commit();
            } else {
                Log.e(TAG, "Instance state fragment " + backstackEntryName + " not found!");
            }
        }
        int appCMSBinderStackSize = savedInstanceState.getInt(getString(R.string.app_cms_binder_stack_size_key));
        Log.d(TAG, "Instance state restoring " + appCMSBinderStackSize + " binder elements");
        for (int i = 0; i < appCMSBinderStackSize; i++) {
            appCMSBinderStack.push((AppCMSBinder) savedInstanceState.getBinder(getString(R.string.app_cms_binder_stack_element_key, i)));
        }
    }

    @Override
    public void onError() {
        setFinishResult(RESULT_CANCELED);
        finish();
    }

    private void handleAppCMSBinder(final AppCMSBinder appCMSBinder, boolean firstFragment) {
        pageLoading(false);

        handleToolbar(appCMSBinder);

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
                final Primary primary = navigation.getPrimary().get(i);
                StringBuffer iconName = new StringBuffer();
                iconName.append(primary.getDisplayedPath().toLowerCase().replaceAll(" ", "_"));
                iconName.append(primary.getUrl().replace("/", "_"));

                NavBarItemView navBarItemView = (NavBarItemView) appCMSTabNavContainer.getChildAt(i);
                navBarItemView.setImage(iconName.toString());
                navBarItemView.setLabel(primary.getTitle());
                navBarItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!appCMSPresenter.navigateToPage(primary.getPageId())) {
                            Log.e(TAG, "Could not navigate to page with Title: " +
                                    primary.getTitle() +
                                    " Id: " +
                                    primary.getPageId());
                        }
                    }
                });
            }
            if (appCMSBinder.isUserLoggedIn()) {
                for (int i = 0;
                     i < navigation.getUser().size() && totalNavItemCnt < appCMSTabNavContainer.getChildCount()  - 1;
                     i++, totalNavItemCnt++) {
                    final User user = navigation.getUser().get(i);
                    StringBuffer iconName = new StringBuffer();
                    iconName.append(user.getDisplayedName().toLowerCase().replaceAll(" ", "_"));
                    iconName.append(user.getUrl().replaceAll("/", "_"));

                    NavBarItemView navBarItemView = (NavBarItemView) appCMSTabNavContainer.getChildAt(i);
                    navBarItemView.setImage(iconName.toString());
                    navBarItemView.setLabel(user.getTitle());

                    navBarItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!appCMSPresenter.navigateToPage(user.getPageId())) {
                                Log.e(TAG, "Could not navigate to page with Title: " +
                                        user.getTitle() +
                                        " Id: " +
                                        user.getPageId());
                            }
                        }
                    });
                }
            }
            NavBarItemView menuNavBarItemView =
                    (NavBarItemView) appCMSTabNavContainer.getChildAt(appCMSTabNavContainer.getChildCount() - 1);
            menuNavBarItemView.setImage(getString(R.string.app_cms_menu_icon_name));
            menuNavBarItemView.setLabel(getString(R.string.app_cms_menu_label));
            menuNavBarItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!appCMSPresenter.launchNavigationPage()) {
                        Log.e(TAG, "Could not launch navigation page!");
                    }
                    Log.d(TAG, "Binder stack size; " + appCMSBinderStack.size());
                }
            });
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
                    String.valueOf(appCMSBinderStack.size()));
            fragmentTransaction.addToBackStack(String.valueOf(appCMSBinderStack.size()));
        }
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
            getSupportActionBar().setTitle(appCMSBinder.getPageName());
            getSupportActionBar().setSubtitle(appCMSBinder.getSubpageName());
        }
    }

    private void handlePresenterAction(Intent intent) {
        Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
        AppCMSBinder appCMSBinder =
                (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
        appCMSBinderStack.push(appCMSBinder);
        handleAppCMSBinder(appCMSBinder, false);
    }

    private void setFinishResult(int resultCode) {
        Intent resultIntent = new Intent();
        Bundle args = new Bundle();
        args.putBinder(getString(R.string.app_cms_binder_key), appCMSBinderStack.pop());
        resultIntent.putExtra(getString(R.string.app_cms_bundle_key), args);
        setResult(resultCode, resultIntent);
    }

    public void pageLoading(boolean pageLoading) {
        if (pageLoading) {
            appCMSPageLoading.setVisibility(View.VISIBLE);
            appCMSPageLoading.getParent().bringChildToFront(appCMSPageLoading);
            appCMSFragment.setAlpha(0.5f);
            appCMSFragment.setEnabled(false);
        } else {
            appCMSPageLoading.setVisibility(View.INVISIBLE);
            appCMSFragment.setAlpha(1.0f);
            appCMSFragment.setEnabled(true);
        }
    }
}
