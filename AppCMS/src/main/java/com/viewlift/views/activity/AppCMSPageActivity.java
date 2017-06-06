package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.Primary;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.NavBarItemView;
import com.viewlift.views.fragments.AppCMSPageFragment;

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

    private RelativeLayout appCMSParentView;
    private ProgressBar appCMSPageLoading;
    private FrameLayout appCMSFragment;
    private AppBarLayout appBarLayout;
    private LinearLayout appCMSTabNavContainer;
    private ActionMenuView appCMSActionMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appcms_page);

        appCMSParentView = (RelativeLayout) findViewById(R.id.app_cms_parent_view);

        appCMSActionMenu = (ActionMenuView) findViewById(R.id.app_cms_action_menu);

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
                appCMSPresenter.setCurrentActivity(AppCMSPageActivity.this);
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION)) {
                    handlePresenterAction(intent);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION)) {
                    pageLoading(true);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_SET_NAVIGATION_ITEM)) {
                    Log.d(TAG, "Nav item - Received broadcast to select navigation item with page Id: " +
                            intent.getStringExtra(getString(R.string.navigation_item_key)));
                    selectNavItem(intent.getStringExtra(getString(R.string.navigation_item_key)));
                }
            }
        };

        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION));
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_SET_NAVIGATION_ITEM));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int backstackCnt = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backstackCnt; i++) {
            String backstackEntryName =
                    getSupportFragmentManager().getBackStackEntryAt(i).getName();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(backstackEntryName);
            if (fragment != null) {
                getSupportFragmentManager().putFragment(outState, backstackEntryName, fragment);
            }
        }
        int appCMSBinderStackSize = appCMSBinderStack.size();
        outState.putInt(getString(R.string.app_cms_binder_stack_size_key), appCMSBinderStackSize);
        for (int i = appCMSBinderStackSize - 1; i >= 0; i--) {
            outState.putBinder(getString(R.string.app_cms_binder_stack_element_key, i), appCMSBinderStack.get(i));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int backstackCnt = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backstackCnt; i++) {
            String backstackEntryName =
                    getSupportFragmentManager().getBackStackEntryAt(i).getName();
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, backstackEntryName);
            if (fragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.app_cms_fragment,
                        fragment,
                        backstackEntryName);
                fragmentTransaction.commit();
            } else {
                Log.e(TAG, "Instance state fragment " + backstackEntryName + " not found!");
            }
        }
        int appCMSBinderStackSize = savedInstanceState.getInt(getString(R.string.app_cms_binder_stack_size_key));
        for (int i = 0; i < appCMSBinderStackSize; i++) {
            appCMSBinderStack.push((AppCMSBinder) savedInstanceState.getBinder(getString(R.string.app_cms_binder_stack_element_key, i)));
        }
    }

    @Override
    public void onBackPressed() {
        handleBack();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        appCMSPresenter.restartInternalEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(presenterActionReceiver);
    }

    @Override
    public void onError() {
        setFinishResult(RESULT_CANCELED);
        getSupportFragmentManager().popBackStack();
        appCMSTabNavContainer.setVisibility(View.VISIBLE);
        handleBack();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        handleOrientation(newConfig.orientation, appCMSBinderStack.peek());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_main, appCMSActionMenu.getMenu());
        return true;
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

    private void handleBack() {
        if (appCMSBinderStack.size() > 0) {
            appCMSBinderStack.pop();
        }
        if (appCMSBinderStack.size() > 0) {
            handleOrientation(getResources().getConfiguration().orientation,
                    appCMSBinderStack.peek());
            Log.d(TAG, "Resetting previous AppCMS data: " + appCMSBinderStack.peek().getPageName());

        }
        appCMSPresenter.navigateAwayFromPage(this);
    }

    private void handleAppCMSBinder(final AppCMSBinder appCMSBinder, boolean firstFragment) {
        pageLoading(false);

        appCMSParentView.setBackgroundColor(Color.parseColor(appCMSBinder.getAppCMSMain()
                .getBrand()
                .getGeneral()
                .getBackgroundColor()));

        appBarLayout = (AppBarLayout) findViewById(R.id.app_cms_appbarlayout);

        appCMSTabNavContainer = (LinearLayout) findViewById(R.id.app_cms_tab_nav_container);

        final Navigation navigation = appCMSBinder.getNavigation();
        if (navigation.getPrimary().size() == 0 || appCMSBinder.isFullScreenEnabled()) {
            appCMSTabNavContainer.setVisibility(View.GONE);
        } else {
            createMenuNavItem();
            createHomeNavItem(appCMSPresenter.findHomePageNavItem(navigation, appCMSBinder.getJsonValueKeyMap()));
            createMoviesNavItem(appCMSPresenter.findMoviesPageNavItem(navigation, appCMSBinder.getJsonValueKeyMap()));
            createSearchNavItem();
            selectNavItem(appCMSBinder.getPageId());

            handleOrientation(getResources().getConfiguration().orientation, appCMSBinder);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment appCMSPageFragment = AppCMSPageFragment.newInstance(this, appCMSBinder);
        if (firstFragment) {
            fragmentTransaction.replace(R.id.app_cms_fragment, appCMSPageFragment, "0");
        } else {
            fragmentTransaction.replace(R.id.app_cms_fragment, appCMSPageFragment);
            if (fragmentManager.getBackStackEntryCount() == 0) {
                fragmentTransaction.addToBackStack("0");
            }
        }
        fragmentTransaction.commit();
    }

    private void selectNavItemAndLaunchPage(NavBarItemView v, String pageId, String pageTitle) {
        selectNavItem(v);
        if (!appCMSPresenter.navigateToPage(pageId, pageTitle)) {
            Log.e(TAG, "Could not navigate to page with Title: " +
                    pageTitle +
                    " Id: " +
                    pageId);
        }
    }

    private void selectNavItem(NavBarItemView v) {
        for (int i = 0; i < appCMSTabNavContainer.getChildCount(); i++) {
            if (appCMSTabNavContainer.getChildAt(i) instanceof NavBarItemView) {
                ((NavBarItemView) appCMSTabNavContainer.getChildAt(i)).select(false);
            }
        }
        v.select(true);
    }

    private void handleOrientation(int orientation, AppCMSBinder appCMSBinder) {
        if (appCMSBinder.isFullScreenEnabled() &&
                orientation == Configuration.ORIENTATION_LANDSCAPE) {
            handleToolbar(false, appCMSBinder.getAppCMSMain());
            appCMSTabNavContainer.setVisibility(View.GONE);
            hideSystemUI(getWindow().getDecorView());
        } else {
            handleToolbar(appCMSBinder.isAppbarPresent(), appCMSBinder.getAppCMSMain());
            appCMSTabNavContainer.setVisibility(View.VISIBLE);
            showSystemUI(getWindow().getDecorView());
        }
    }

    private void handleToolbar(boolean appbarPresent, AppCMSMain appCMSMain) {
        if (!appbarPresent) {
            appBarLayout.setVisibility(View.GONE);
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.app_cms_toolbar);
            toolbar.setTitleTextColor(Color.parseColor(appCMSMain
                    .getBrand()
                    .getGeneral()
                    .getTextColor()));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setTitle("");
            appBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void handlePresenterAction(Intent intent) {
        Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
        AppCMSBinder appCMSBinder =
                (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
        if (appCMSBinderStack.size() > 1) {
            appCMSBinderStack.pop();
        }
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

    private void hideSystemUI(View decorView) {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI(View decorView) {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void createMenuNavItem() {
        NavBarItemView menuNavBarItemView =
                (NavBarItemView) appCMSTabNavContainer.getChildAt(0);
        menuNavBarItemView.setImage(getString(R.string.app_cms_menu_icon_name));
        menuNavBarItemView.setLabel(getString(R.string.app_cms_menu_label));
        menuNavBarItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavItem((NavBarItemView) v);
                if (!appCMSPresenter.launchNavigationPage()) {
                    Log.e(TAG, "Could not launch navigation page!");
                }
            }
        });
    }

    private void createHomeNavItem(final Primary homePageNav) {
        NavBarItemView homeNavBarItemView =
                (NavBarItemView) appCMSTabNavContainer.getChildAt(1);
        homeNavBarItemView.setImage(getIconName(homePageNav));
        homeNavBarItemView.setLabel(homePageNav.getTitle());
        homeNavBarItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavItemAndLaunchPage((NavBarItemView) v,
                        homePageNav.getPageId(),
                        homePageNav.getDisplayedPath());
            }
        });
        homeNavBarItemView.setTag(homePageNav.getPageId());
    }

    private void createMoviesNavItem(final Primary moviePageNav) {
        NavBarItemView moviesNavBarItemView =
                (NavBarItemView) appCMSTabNavContainer.getChildAt(2);
        moviesNavBarItemView.setImage(getIconName(moviePageNav));
        moviesNavBarItemView.setLabel(moviePageNav.getTitle());
        moviesNavBarItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavItemAndLaunchPage((NavBarItemView) v,
                        moviePageNav.getPageId(),
                        moviePageNav.getTitle());
            }
        });
        moviesNavBarItemView.setTag(moviePageNav.getPageId());
    }

    private void createSearchNavItem() {
        NavBarItemView searchNavBarItemView =
                (NavBarItemView) appCMSTabNavContainer.getChildAt(3);
        searchNavBarItemView.setImage(getString(R.string.app_cms_search_icon_name));
        searchNavBarItemView.hideLabel();
        searchNavBarItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavItem((NavBarItemView) v);
            }
        });
    }

    private String getIconName(Primary navItem) {
        StringBuffer iconName = new StringBuffer();
        iconName.append(navItem.getDisplayedPath().toLowerCase().replaceAll(" ", "_"));
        iconName.append(navItem.getUrl().replaceAll("/", "_"));
        return iconName.toString();
    }

    public void selectNavItem(String pageId) {
        for (int i = 0 ; i < appCMSTabNavContainer.getChildCount(); i++) {
            if (pageId.equals(appCMSTabNavContainer.getChildAt(i).getTag())) {
                selectNavItem(((NavBarItemView) appCMSTabNavContainer.getChildAt(i)));
                Log.d(TAG, "Nav item - Selecting tab item with page Id: " + pageId);
            }
        }
    }
}
