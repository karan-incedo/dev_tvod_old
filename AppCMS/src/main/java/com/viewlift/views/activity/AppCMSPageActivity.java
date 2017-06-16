package com.viewlift.views.activity;

import android.app.SearchManager;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.Primary;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.NavBarItemView;
import com.viewlift.views.fragments.AppCMSPageFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSPageActivity extends AppCompatActivity implements AppCMSPageFragment.OnPageCreation {
    private static final String TAG = "AppCMSPageActivity";

    private static final int NAV_PAGE_INDEX = 0;
    private static final int HOME_PAGE_INDEX = 0;
    private static final int MOVIES_PAGE_INDEX = 1;
    private static final int SEARCH_INDEX = 3;

    private AppCMSPresenter appCMSPresenter;
    private Stack<String> appCMSBinderStack;
    private Map<String, AppCMSBinder> appCMSBinderMap;
    private BroadcastReceiver presenterActionReceiver;

    private RelativeLayout appCMSParentView;
    private FrameLayout appCMSFragment;
    private AppBarLayout appBarLayout;
    private LinearLayout appCMSTabNavContainer;
    private ActionMenuView appCMSActionMenu;
    private RelativeLayout appCMSSearchViewContainer;
    private ImageButton appCMSSearchBackButton;
    private SearchView appCMSSearchView;
    private NavBarItemView pageViewDuringSearch;
    private boolean resumeInternalEvents;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appcms_page);

        appCMSParentView = (RelativeLayout) findViewById(R.id.app_cms_parent_view);
        appCMSFragment = (FrameLayout) findViewById(R.id.app_cms_fragment);
        appCMSActionMenu = (ActionMenuView) findViewById(R.id.app_cms_action_menu);
        appCMSSearchViewContainer = (RelativeLayout) findViewById(R.id.app_cms_search_view_container);
        appCMSSearchBackButton = (ImageButton) findViewById(R.id.app_cms_search_back_button);

        appCMSSearchBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchView(false);
                if (pageViewDuringSearch != null) {
                    selectNavItem(pageViewDuringSearch);
                }
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        appCMSSearchView = (SearchView) findViewById(R.id.app_cms_search_view);
        appCMSSearchView.setQueryHint(getString(R.string.search_films));
        appCMSSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        appCMSSearchView.setIconifiedByDefault(true);

        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        appCMSBinderStack = new Stack<>();
        appCMSBinderMap = new HashMap<>();

        presenterActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION)) {
                    Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
                    try {
                        AppCMSBinder appCMSBinder =
                                (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
                        handleLaunchPageAction(appCMSBinder);
                    } catch (ClassCastException e) {
                        Log.e(TAG, "Could not read AppCMSBinder: " + e.toString());
                    }
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION)) {
                    pageLoading(true);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_RESET_NAVIGATION_ITEM)) {
                    Log.d(TAG, "Nav item - Received broadcast to select navigation item with page Id: " +
                            intent.getStringExtra(getString(R.string.navigation_item_key)));
                    selectNavItem(intent.getStringExtra(getString(R.string.navigation_item_key)));
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION)) {
                    Log.d(TAG, "Received Presenter Close Action: fragment count = " + getSupportFragmentManager().getBackStackEntryCount());
                    if (appCMSBinderStack.size() > 1) {
                        try {
                            getSupportFragmentManager().popBackStack();
                        } catch (IllegalStateException e) {
                            Log.e(TAG, "Error popping back stack: " + e.getMessage());
                        }
                        handleBack(true, false, true);
                    }
                }
            }
        };

        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION));
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_RESET_NAVIGATION_ITEM));
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));

        resumeInternalEvents = false;

        Bundle args = getIntent().getBundleExtra(getString(R.string.app_cms_bundle_key));
        try {
            AppCMSBinder appCMSBinder =
                    (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
            handleLaunchPageAction(appCMSBinder);
        } catch (ClassCastException e) {
            Log.e(TAG, "Could not read AppCMSBinder: " + e.toString());
        }

        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
        AppCMSBinder appCMSBinder =
                (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
        handleLaunchPageAction(appCMSBinder);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int appCMSBinderStackSize = appCMSBinderStack.size();
        outState.putInt(getString(R.string.app_cms_binder_stack_size_key), appCMSBinderStackSize);
        for (int i = appCMSBinderStackSize - 1; i >= 0; i--) {
            outState.putBinder(getString(R.string.app_cms_binder_stack_element_key, i), appCMSBinderMap.get(appCMSBinderStack.get(i)));
        }
        outState.putBoolean(getString(R.string.resume_internal_events_key), resumeInternalEvents);
        Log.d(TAG, "Saving instance state");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int appCMSBinderStackSize = savedInstanceState.getInt(getString(R.string.app_cms_binder_stack_size_key));
        appCMSBinderStack.clear();
        appCMSBinderMap.clear();
        for (int i = 0; i < appCMSBinderStackSize; i++) {
            try {
                AppCMSBinder appCMSBinder =
                        ((AppCMSBinder) savedInstanceState.getBinder(getString(R.string.app_cms_binder_stack_element_key, i)));
                String pageId = appCMSBinder.getPageId();
                appCMSBinderStack.push(pageId);
                appCMSBinderMap.put(pageId, appCMSBinder);
            } catch (ClassCastException e) {
                Log.e(TAG, "Could not attach fragment: " + e.toString());
            }
        }
        resumeInternalEvents = savedInstanceState.getBoolean(getString(R.string.resume_internal_events_key));
        Log.d(TAG, "Restoring instance state");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "Back pressed - Binder stack size: " + appCMSBinderStack.size());
        pageLoading(false);
        handleBack(true, appCMSBinderStack.size() < 2, true);
        if (appCMSBinderStack.size() > 0 && appCMSBinderMap.get(appCMSBinderStack.peek()) != null) {
            AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
            handleBack(true, appCMSBinderStack.size() < 2, false);
            handleLaunchPageAction(appCMSBinder);
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resumeInternalEvents) {
            appCMSPresenter.restartInternalEvents();
            Log.d(TAG, "onResume() - Resuming internal events");
        }
        showSearchView(false);
        if (pageViewDuringSearch != null) {
            selectNavItem(pageViewDuringSearch);
        }
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        showSearchView(false);
        if (pageViewDuringSearch != null) {
            selectNavItem(pageViewDuringSearch);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        appCMSPresenter.cancelInternalEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(presenterActionReceiver);
        appCMSPresenter.navigateAwayFromPage(this);
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onSuccess(AppCMSBinder appCMSBinder) {
        appCMSPresenter.restartInternalEvents();
        resumeInternalEvents = true;
    }

    @Override
    public void onError(AppCMSBinder appCMSBinder) {
        if (appCMSBinder != null) {
            Log.e(TAG, "Nav item - Error attempting to launch page: " + appCMSBinder.getPageName() + " - " + appCMSBinder.getPageId());
        }
        setFinishResult(RESULT_CANCELED);
        getSupportFragmentManager().popBackStack();
        handleBack(true, false, false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AppCMSBinder appCMSBinder = appCMSBinderStack.size() > 0 ?
                appCMSBinderMap.get(appCMSBinderStack.peek()) :
                null;
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.d(TAG, "Orientation changed - handling back");
            handleBack(true, true, false);
        }
        if (appCMSBinder != null) {
            appCMSBinderStack.push(appCMSBinder.getPageId());
            appCMSBinderMap.put(appCMSBinder.getPageId(), appCMSBinder);
            createScreenFromAppCMSBinder(appCMSBinder);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_main, appCMSActionMenu.getMenu());
        return true;
    }

    public void pageLoading(boolean pageLoading) {
        if (pageLoading) {
            appCMSFragment.setAlpha(0.5f);
            appCMSFragment.setEnabled(false);
        } else {
            appCMSFragment.setAlpha(1.0f);
            appCMSFragment.setEnabled(true);
        }
    }

    private void handleBack(boolean popBinderStack, boolean closeActionPage, boolean recurse) {
        if (popBinderStack && appCMSBinderStack.size() > 0) {
            appCMSBinderStack.pop();
        }
        if (appCMSBinderStack.size() > 0) {
            Log.d(TAG, "Back pressed - handling nav bar");
            handleNavbar(appCMSBinderMap.get(appCMSBinderStack.peek()));
            Log.d(TAG, "Resetting previous AppCMS data: " + appCMSBinderMap.get(appCMSBinderStack.peek()).getPageName());
        }
        if (shouldPopStack() || closeActionPage) {
            try {
                getSupportFragmentManager().popBackStack();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Failed to pop Fragment from back stack");
            }
            if (recurse) {
                Log.d(TAG, "Handling back - recursive op");
                handleBack(popBinderStack, closeActionPage && appCMSBinderStack.size() > 0, recurse);
            }
        }
    }

    private boolean shouldPopStack() {
        return appCMSBinderStack.size() > 0 && !appCMSPresenter.isActionAPage(appCMSBinderMap.get(appCMSBinderStack.peek()).getPageId());
    }

    private void createScreenFromAppCMSBinder(final AppCMSBinder appCMSBinder) {
        Log.d(TAG, "Handling new AppCMSBinder: " + appCMSBinder.getPageName());

        pageLoading(false);

        appCMSParentView.setBackgroundColor(Color.parseColor(appCMSBinder.getAppCMSMain()
                .getBrand()
                .getGeneral()
                .getBackgroundColor()));

        appBarLayout = (AppBarLayout) findViewById(R.id.app_cms_appbarlayout);

        appCMSTabNavContainer = (LinearLayout) findViewById(R.id.app_cms_tab_nav_container);

        createHomeNavItem(appCMSPresenter.findHomePageNavItem());
        createMoviesNavItem(appCMSPresenter.findMoviesPageNavItem());
        createSearchNavItem();

        Log.d(TAG, "createScreenFromAppCMSBinder() - Handling Navbar");
        handleNavbar(appCMSBinder);
        handleOrientation(getResources().getConfiguration().orientation, appCMSBinder);
    }

    private void createFragment(AppCMSBinder appCMSBinder) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment appCMSPageFragment = AppCMSPageFragment.newInstance(this, appCMSBinder);
            fragmentTransaction.replace(R.id.app_cms_fragment, appCMSPageFragment, appCMSBinder.getPageId());
            fragmentTransaction.addToBackStack(appCMSBinder.getPageId());
            fragmentTransaction.commit();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Failed to pop Fragment from back stack");
        }
    }

    private void selectNavItemAndLaunchPage(NavBarItemView v, String pageId, String pageTitle) {
        if (!appCMSPresenter.navigateToPage(pageId, pageTitle, false)) {
            Log.e(TAG, "Could not navigate to page with Title: " +
                    pageTitle +
                    " Id: " +
                    pageId);
        } else {
            selectNavItem(v);
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

    private NavBarItemView getSelectedNavItem() {
        for (int i = 0; i < appCMSTabNavContainer.getChildCount(); i++) {
            if (((NavBarItemView) appCMSTabNavContainer.getChildAt(i)).isItemSelected()) {
                return (NavBarItemView) appCMSTabNavContainer.getChildAt(i);
            }
        }
        return null;
    }

    private void handleNavbar(AppCMSBinder appCMSBinder) {
        final Navigation navigation = appCMSBinder.getNavigation();
        if (navigation.getPrimary().size() == 0 || !appCMSBinder.isNavbarPresent()) {
            appCMSTabNavContainer.setVisibility(View.GONE);
        } else {
            appCMSTabNavContainer.setVisibility(View.VISIBLE);
            selectNavItem(appCMSBinder.getPageId());
        }
    }

    private void handleOrientation(int orientation, AppCMSBinder appCMSBinder) {
        if (appCMSBinder.isFullScreenEnabled() &&
                orientation == Configuration.ORIENTATION_LANDSCAPE) {
            handleToolbar(false, appCMSBinder.getAppCMSMain());
            hideSystemUI(getWindow().getDecorView());
        } else {
            handleToolbar(appCMSBinder.isAppbarPresent(), appCMSBinder.getAppCMSMain());
            showSystemUI(getWindow().getDecorView());
        }
        handleNavbar(appCMSBinder);
        createFragment(appCMSBinder);
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

    private void handleLaunchPageAction(AppCMSBinder appCMSBinder) {
        Log.d(TAG, "Launching new page");
        int distanceFromStackTop = appCMSBinderStack.search(appCMSBinder.getPageId());
        Log.d(TAG, "Page existing index: " + distanceFromStackTop);
        if (0 <= distanceFromStackTop) {
            for (int i = 0; i < distanceFromStackTop; i++) {
                Log.d(TAG, "Popping stack to get to page item");
                getSupportFragmentManager().popBackStackImmediate();
                handleBack(true, true, false);
            }
        }
        appCMSBinderStack.push(appCMSBinder.getPageId());
        appCMSBinderMap.put(appCMSBinder.getPageId(), appCMSBinder);
        createScreenFromAppCMSBinder(appCMSBinder);
    }

    private void setFinishResult(int resultCode) {
        Intent resultIntent = new Intent();
        if (appCMSBinderStack.size() > 0) {
            Bundle args = new Bundle();
            args.putBinder(getString(R.string.app_cms_binder_key),
                    appCMSBinderMap.get(appCMSBinderStack.peek()));
            resultIntent.putExtra(getString(R.string.app_cms_bundle_key), args);
        }
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
        final NavBarItemView menuNavBarItemView =
                (NavBarItemView) appCMSTabNavContainer.getChildAt(NAV_PAGE_INDEX);
        menuNavBarItemView.setImage(getString(R.string.app_cms_menu_icon_name));
        menuNavBarItemView.setLabel(getString(R.string.app_cms_menu_label));
        menuNavBarItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavItem(menuNavBarItemView);
                if (!appCMSPresenter.launchNavigationPage()) {
                    Log.e(TAG, "Could not launch navigation page!");
                }
            }
        });
    }

    private void createHomeNavItem(final Primary homePageNav) {
        final NavBarItemView homeNavBarItemView =
                (NavBarItemView) appCMSTabNavContainer.getChildAt(HOME_PAGE_INDEX);
        homeNavBarItemView.setImage(getIconName(homePageNav));
        homeNavBarItemView.setLabel(homePageNav.getTitle());
        homeNavBarItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavItemAndLaunchPage(homeNavBarItemView,
                        homePageNav.getPageId(),
                        homePageNav.getTitle());
            }
        });
        homeNavBarItemView.setTag(homePageNav.getPageId());
    }

    private void createMoviesNavItem(final Primary moviePageNav) {
        final NavBarItemView moviesNavBarItemView =
                (NavBarItemView) appCMSTabNavContainer.getChildAt(MOVIES_PAGE_INDEX);
        moviesNavBarItemView.setImage(getIconName(moviePageNav));
        moviesNavBarItemView.setLabel(moviePageNav.getTitle());
        moviesNavBarItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNavItemAndLaunchPage(moviesNavBarItemView,
                        moviePageNav.getPageId(),
                        moviePageNav.getTitle());
            }
        });
        moviesNavBarItemView.setTag(moviePageNav.getPageId());
    }

    private void createSearchNavItem() {
        NavBarItemView searchNavBarItemView =
                (NavBarItemView) appCMSTabNavContainer.getChildAt(SEARCH_INDEX);
        searchNavBarItemView.setImage(getString(R.string.app_cms_search_icon_name));
        searchNavBarItemView.hideLabel();
        searchNavBarItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageViewDuringSearch = getSelectedNavItem();
                selectNavItem((NavBarItemView) v);
                showSearchView(true);
            }
        });
    }

    private String getIconName(Primary navItem) {
        StringBuffer iconName = new StringBuffer();
        iconName.append(navItem.getDisplayedPath().toLowerCase().replaceAll(" ", "_"));
        iconName.append(navItem.getUrl().replaceAll("/", "_"));
        return iconName.toString();
    }

    private void selectNavItem(String pageId) {
        for (int i = 0 ; i < appCMSTabNavContainer.getChildCount(); i++) {
            if (pageId.equals(appCMSTabNavContainer.getChildAt(i).getTag())) {
                selectNavItem(((NavBarItemView) appCMSTabNavContainer.getChildAt(i)));
                Log.d(TAG, "Nav item - Selecting tab item with page Id: " +
                        pageId +
                        " index: " +
                        i);
            }
        }
    }

    private void showSearchView(boolean shouldShowSearchView) {
        if (shouldShowSearchView) {
            appCMSSearchViewContainer.setVisibility(View.VISIBLE);
            appCMSSearchView.setFocusable(true);
            appCMSSearchView.requestFocus();
            appCMSSearchView.setIconified(false);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            appCMSFragment.setAlpha(0.5f);
            appCMSFragment.setEnabled(false);
        } else {
            appCMSSearchViewContainer.setVisibility(View.GONE);
            appCMSSearchView.setIconified(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(appCMSSearchView.getWindowToken(),0);
            appCMSFragment.setAlpha(1.0f);
            appCMSFragment.setEnabled(true);
        }
    }
}
