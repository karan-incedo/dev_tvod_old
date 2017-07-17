package com.viewlift.views.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.RelativeLayout;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.viewlift.AppCMSApplication;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.history.AppCMSHistoryResult;
import com.viewlift.models.data.appcms.history.Record;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.customviews.NavBarItemView;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.fragments.AppCMSPageFragment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;
import snagfilms.com.air.appcms.R;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSPageActivity extends AppCompatActivity implements AppCMSPageFragment.OnPageCreation {
    private static final String TAG = "AppCMSPageActivity";

    private static final int NAV_PAGE_INDEX = 0;
    private static final int HOME_PAGE_INDEX = 1;
    private static final int MOVIES_PAGE_INDEX = 2;
    private static final int SEARCH_INDEX = 3;

    @BindView(R.id.app_cms_page_loading_progressbar)
    ProgressBar loadingProgressBar;

    @BindView(R.id.app_cms_parent_view)
    RelativeLayout appCMSParentView;

    @BindView(R.id.app_cms_fragment)
    FrameLayout appCMSFragment;

    @BindView(R.id.app_cms_appbarlayout)
    AppBarLayout appBarLayout;

    @BindView(R.id.app_cms_tab_nav_container)
    LinearLayout appCMSTabNavContainer;

    private AppCMSPresenter appCMSPresenter;
    private Stack<String> appCMSBinderStack;
    private Map<String, AppCMSBinder> appCMSBinderMap;
    private BroadcastReceiver presenterActionReceiver;
    private BroadcastReceiver presenterCloseActionReceiver;

    private NavBarItemView pageViewDuringSearch;
    private boolean resumeInternalEvents;
    private boolean isActive;
    private boolean shouldSendCloseOthersAction;
    private AppCMSBinder updatedAppCMSBinder;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appcms_page);

        ButterKnife.bind(this);

        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();

        appCMSBinderStack = new Stack<>();
        appCMSBinderMap = new HashMap<>();

        Bundle args = getIntent().getBundleExtra(getString(R.string.app_cms_bundle_key));
        try {
            updatedAppCMSBinder =
                    (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
            appCMSBinderStack.push(updatedAppCMSBinder.getPageId());
            appCMSBinderMap.put(updatedAppCMSBinder.getPageId(), updatedAppCMSBinder);
            shouldSendCloseOthersAction = updatedAppCMSBinder.shouldSendCloseAction();
        } catch (ClassCastException e) {
            Log.e(TAG, "Could not read AppCMSBinder: " + e.toString());
        }

        presenterActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION)) {
                    Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
                    try {
                        updatedAppCMSBinder =
                                (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
                        if (isActive) {
                            handleLaunchPageAction(updatedAppCMSBinder);
                        } else if (updatedAppCMSBinder.shouldSendCloseAction()) {
                            Intent appCMSIntent = new Intent(AppCMSPageActivity.this,
                                    AppCMSPageActivity.class);
                            appCMSIntent.putExtra(AppCMSPageActivity.this.getString(R.string.app_cms_bundle_key), args);
                            appCMSIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            AppCMSPageActivity.this.startActivity(appCMSIntent);
                            shouldSendCloseOthersAction = true;
                        }
                    } catch (ClassCastException e) {
                        Log.e(TAG, "Could not read AppCMSBinder: " + e.toString());
                    }
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION)) {
                    pageLoading(true);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION)) {
                    pageLoading(false);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_RESET_NAVIGATION_ITEM_ACTION)) {
                    Log.d(TAG, "Nav item - Received broadcast to select navigation item with page Id: " +
                            intent.getStringExtra(getString(R.string.navigation_item_key)));
                    selectNavItem(intent.getStringExtra(getString(R.string.navigation_item_key)));
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_DEEPLINK_ACTION)) {
                    if (intent.getData() != null) {
                        processDeepLink(intent.getData());
                    }
                }
            }
        };

        presenterCloseActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION)) {
                    Log.d(TAG, "Received Presenter Close Action: fragment count = "
                            + getSupportFragmentManager().getBackStackEntryCount());
                    if (appCMSBinderStack.size() > 1) {
                        try {
                            getSupportFragmentManager().popBackStack();
                        } catch (IllegalStateException e) {
                            Log.e(TAG, "DialogType popping back stack: " + e.getMessage());
                        }
                        handleBack(true, false, true);
                        if (appCMSBinderStack.size() > 0) {
                            AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
                            handleBack(true, appCMSBinderStack.size() < 2, false);
                            handleLaunchPageAction(appCMSBinder);
                        }
                        isActive = true;
                    }
                }
            }
        };

        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION));
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION));
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_RESET_NAVIGATION_ITEM_ACTION));
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_DEEPLINK_ACTION));

        resumeInternalEvents = false;

        shouldSendCloseOthersAction = false;

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                AppCMSPageActivity.this.accessToken = currentAccessToken;
                if (appCMSPresenter != null) {
                    appCMSPresenter.setFacebookAccessToken(AppCMSPageActivity.this,
                            currentAccessToken.getToken());
                }
            }
        };

        accessToken = AccessToken.getCurrentAccessToken();

        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int appCMSBinderStackSize = appCMSBinderStack.size();
        outState.putInt(getString(R.string.app_cms_binder_stack_size_key), appCMSBinderStackSize);
        for (int i = appCMSBinderStackSize - 1; i >= 0; i--) {
            outState.putBinder(getString(R.string.app_cms_binder_stack_element_key, i),
                    appCMSBinderMap.get(appCMSBinderStack.get(i)));
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
                updatedAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
            } catch (ClassCastException e) {
                Log.e(TAG, "Could not attach fragment: " + e.toString());
            }
        }
        resumeInternalEvents = savedInstanceState.getBoolean(getString(R.string.resume_internal_events_key));
        Log.d(TAG, "Restoring instance state");
    }

    @Override
    public void onBackPressed() {
        if (appCMSPresenter != null && appCMSPresenter.isMainFragmentViewVisible()) {
            super.onBackPressed();
            Log.d(TAG, "Back pressed - Binder stack size: " + appCMSBinderStack.size());
            pageLoading(false);
            handleBack(true, appCMSBinderStack.size() < 2, true);
            if (appCMSBinderStack.size() > 0 && appCMSBinderMap.get(appCMSBinderStack.peek()) != null) {
                AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
                handleBack(true, appCMSBinderStack.size() < 2, false);
                handleLaunchPageAction(appCMSBinder);
            } else {
                isActive = false;
                finishAffinity();
            }
            if (appCMSPresenter != null) {
                appCMSPresenter.popActionInternalEvents();
                appCMSPresenter.restartInternalEvents();
            }
        } else if (appCMSPresenter != null) {
            appCMSPresenter.popActionInternalEvents();
            appCMSPresenter.setNavItemToCurrentAction(this);
            appCMSPresenter.showMainFragmentView(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resumeInternalEvents) {
            appCMSPresenter.restartInternalEvents();
            appCMSPresenter.showMainFragmentView(false);
            Log.d(TAG, "onResume() - Resuming internal events");
        }

        if (pageViewDuringSearch != null) {
            selectNavItem(pageViewDuringSearch);
        } else {
            if (appCMSBinderStack != null && appCMSBinderStack.size() > 0) {
                Log.d(TAG, "Activity resumed - resetting nav item");
                selectNavItem(appCMSBinderStack.peek());
            }
        }

        if (!isActive) {
            if (updatedAppCMSBinder != null) {
                handleLaunchPageAction(updatedAppCMSBinder);
            }
        }

        isActive = true;

        if (shouldSendCloseOthersAction && appCMSPresenter != null) {
            appCMSPresenter.sendCloseOthersAction(null, false);
            shouldSendCloseOthersAction = false;
        }

        registerReceiver(presenterCloseActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));

        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pageViewDuringSearch != null) {
            selectNavItem(pageViewDuringSearch);
        }
        unregisterReceiver(presenterCloseActionReceiver);
        isActive = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        appCMSPresenter.cancelInternalEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewCreator viewCreator = null;
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            Fragment fragment =
                    getSupportFragmentManager().findFragmentById(getSupportFragmentManager()
                            .getBackStackEntryAt(i).getId());
            if (fragment instanceof AppCMSPageFragment) {
                viewCreator = ((AppCMSPageFragment) fragment).getViewCreator();
            }
        }
        if (updatedAppCMSBinder != null && viewCreator != null) {
            viewCreator.removeLruCacheItem(this, updatedAppCMSBinder.getPageId());
        }
        unregisterReceiver(presenterActionReceiver);

        accessTokenTracker.stopTracking();

        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onSuccess(AppCMSBinder appCMSBinder) {
        appCMSPresenter.restartInternalEvents();
        resumeInternalEvents = true;
        Log.d(TAG, "Successfully loaded page " + appCMSBinder.getPageName());
        if (appCMSBinder.getSearchQuery() != null) {
            Log.d(TAG, "Processing search query for deeplink " +
                    appCMSBinder.getSearchQuery().toString());
            processDeepLink(appCMSBinder.getSearchQuery());
            appCMSBinder.clearSearchQuery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onError(AppCMSBinder appCMSBinder) {
        if (appCMSBinder != null) {
            Log.e(TAG, "Nav item - DialogType attempting to launch page: "
                    + appCMSBinder.getPageName() + " - " + appCMSBinder.getPageId());
        }

        if (appCMSBinderStack.size() > 0 && appCMSBinderStack.peek().equals(appCMSBinder.getPageId())) {
            try {
                getSupportFragmentManager().popBackStackImmediate();
            } catch (IllegalStateException e) {
                Log.e(TAG, "DialogType popping back stack: " + e.getMessage());
            }
            handleBack(true, false, false);
        }
        if (appCMSBinderStack.size() > 0) {
            handleLaunchPageAction(appCMSBinderMap.get(appCMSBinderStack.peek()));
        } else {
            finish();
        }
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
            handleLaunchPageAction(appCMSBinder);
        }
    }

    public void pageLoading(boolean pageLoading) {
        if (pageLoading) {
            appCMSFragment.setEnabled(false);
            appCMSTabNavContainer.setEnabled(false);
            loadingProgressBar.setVisibility(View.VISIBLE);

            for (int i = 0; i < appCMSTabNavContainer.getChildCount(); i++) {
                appCMSTabNavContainer.getChildAt(i).setEnabled(false);
            }

        } else {
            appCMSFragment.setEnabled(true);
            appCMSTabNavContainer.setEnabled(true);
            loadingProgressBar.setVisibility(View.GONE);

            for (int i = 0; i < appCMSTabNavContainer.getChildCount(); i++) {
                appCMSTabNavContainer.getChildAt(i).setEnabled(true);
            }
        }
    }

    private void handleBack(boolean popBinderStack, boolean closeActionPage, boolean recurse) {
        if (popBinderStack && appCMSBinderStack.size() > 0) {
            appCMSBinderStack.pop();
        }
        if (appCMSBinderStack.size() > 0) {
            updatedAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
            Log.d(TAG, "Back pressed - handling nav bar");
            handleNavbar(appCMSBinderMap.get(appCMSBinderStack.peek()));
            Log.d(TAG, "Resetting previous AppCMS data: "
                    + appCMSBinderMap.get(appCMSBinderStack.peek()).getPageName());
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
        return appCMSBinderStack.size() > 0 &&
                !appCMSPresenter.isPagePrimary(appCMSBinderMap.get(appCMSBinderStack.peek()).getPageId());
    }

    private void createScreenFromAppCMSBinder(final AppCMSBinder appCMSBinder) {
        Log.d(TAG, "Handling new AppCMSBinder: " + appCMSBinder.getPageName());

        pageLoading(false);

        appCMSParentView.setBackgroundColor(Color.parseColor(appCMSBinder.getAppCMSMain()
                .getBrand()
                .getGeneral()
                .getBackgroundColor()));

        createMenuNavItem();
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
            fragmentTransaction.replace(R.id.app_cms_fragment, appCMSPageFragment,
                    appCMSBinder.getPageId());
            fragmentTransaction.addToBackStack(appCMSBinder.getPageId());
            fragmentTransaction.commit();
            getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    appCMSPresenter.dismissOpenDialogs();
                    appCMSPresenter.showMainFragmentView(true);
                    getSupportFragmentManager().removeOnBackStackChangedListener(this);
                }
            });
        } catch (IllegalStateException e) {
            Log.e(TAG, "Failed to add Fragment to back stack");
        }
    }

    private void selectNavItemAndLaunchPage(NavBarItemView v, String pageId, String pageTitle) {
        if (!appCMSPresenter.navigateToPage(pageId,
                pageTitle,
                null,
                false,
                true,
                true,
                null)) {
            Log.e(TAG, "Could not navigate to page with Title: " +
                    pageTitle +
                    " Id: " +
                    pageId);
        } else {
            selectNavItem(v);
        }
    }

    private void selectNavItem(NavBarItemView v) {
        unselectAllNavItems();
        v.select(true);
    }

    private void unselectAllNavItems() {
        for (int i = 0; i < appCMSTabNavContainer.getChildCount(); i++) {
            if (appCMSTabNavContainer.getChildAt(i) instanceof NavBarItemView) {
                unselectNavItem((NavBarItemView) appCMSTabNavContainer.getChildAt(i));
            }
        }
    }

    private void unselectNavItem(NavBarItemView v) {
        v.select(false);
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
        if (navigation.getNavigationPrimary().size() == 0 || !appCMSBinder.isNavbarPresent()) {
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

    private void handleLaunchPageAction(final AppCMSBinder appCMSBinder) {
        Log.d(TAG, "Launching new page: " + appCMSBinder.getPageName());
        appCMSPresenter.sendGaScreen(appCMSBinder.getScreenName());
        int distanceFromStackTop = appCMSBinderStack.search(appCMSBinder.getPageId());
        Log.d(TAG, "Page distance from top: " + distanceFromStackTop);
        if (0 < distanceFromStackTop) {
            for (int i = 0; i < distanceFromStackTop; i++) {
                Log.d(TAG, "Popping stack to get to page item");
                try {
                    getSupportFragmentManager().popBackStack();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "DialogType popping back stack: " + e.getMessage());
                }
                handleBack(true, false, false);
            }
        }
        appCMSBinderStack.push(appCMSBinder.getPageId());
        appCMSBinderMap.put(appCMSBinder.getPageId(), appCMSBinder);
        updatedAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());

        if (appCMSPresenter.isUserLoggedIn(this)) {
            appCMSPresenter.getHistoryData(new Action1<AppCMSHistoryResult>() {
                @Override
                public void call(AppCMSHistoryResult appCMSHistoryResult) {
                    if (appCMSHistoryResult != null &&
                            appCMSHistoryResult.getRecords() != null) {
                        for (Module module : appCMSBinder.getAppCMSPageAPI().getModules()) {
                            if (appCMSBinder.getJsonValueKeyMap().get(module.getModuleType()) ==
                                    AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY) {
                                AppCMSPageAPI pageAPI =
                                        appCMSHistoryResult.convertToAppCMSPageAPI(module.getId());
                                module.setContentData(pageAPI.getModules().get(0).getContentData());
                            } else if (appCMSBinder.getJsonValueKeyMap().get(module.getModuleType()) ==
                                    AppCMSUIKeyType.PAGE_VIDEO_DETAILS_KEY) {

                                if (module.getContentData() != null &&
                                        module.getContentData().size() > 0 &&
                                        module.getContentData().get(0) != null &&
                                        module.getContentData().get(0).getGist() != null) {
                                    Gist moduleGist =
                                            module.getContentData().get(0).getGist();
                                    for (Record record : appCMSHistoryResult.getRecords()) {
                                        if (record.getContentResponse() != null &&
                                                record.getContentResponse().getGist() != null &&
                                                record.getContentResponse().getGist().getId() != null &&
                                                record.getContentResponse().getGist().getId().equals(moduleGist.getId())) {
                                            Gist recordGist = record.getContentResponse().getGist();
                                            moduleGist.setWatchedTime(recordGist.getWatchedTime());
                                            moduleGist.setWatchedPercentage(recordGist.getWatchedPercentage());
                                        }
                                    }
                                }
                            }
                        }
                        createScreenFromAppCMSBinder(appCMSBinder);
                    } else {
                        createScreenFromAppCMSBinder(appCMSBinder);
                    }
                }
            });
        } else {
            createScreenFromAppCMSBinder(appCMSBinder);
        }
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
                if (appCMSBinderStack.size() > 0) {
                    AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
                    if (!appCMSPresenter.launchNavigationPage(appCMSBinder.getPageId(),
                            appCMSBinder.getPageName())) {
                        Log.e(TAG, "Could not launch navigation page!");
                    } else {
                        resumeInternalEvents = true;
                        selectNavItem(menuNavBarItemView);
                    }
                }
            }
        });
    }

    private void createHomeNavItem(final NavigationPrimary homePageNav) {
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

    private void createMoviesNavItem(final NavigationPrimary moviePageNav) {
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
                appCMSPresenter.launchSearchPage();
            }
        });
    }

    private String getIconName(NavigationPrimary navItem) {
        StringBuffer iconName = new StringBuffer();
        iconName.append(navItem.getDisplayedPath().toLowerCase().replaceAll(" ", "_"));
        iconName.append(navItem.getUrl().replaceAll("/", "_"));
        return iconName.toString();
    }

    private void selectNavItem(String pageId) {
        boolean foundPage = false;
        for (int i = 0; i < appCMSTabNavContainer.getChildCount(); i++) {
            if (appCMSTabNavContainer.getChildAt(i).getTag() != null &&
                    pageId.contains(appCMSTabNavContainer.getChildAt(i).getTag().toString())) {
                selectNavItem(((NavBarItemView) appCMSTabNavContainer.getChildAt(i)));
                Log.d(TAG, "Nav item - Selecting tab item with page Id: " +
                        pageId +
                        " index: " +
                        i);
                foundPage = true;
            }
        }
        if (!foundPage) {
            final NavBarItemView menuNavBarItemView =
                    (NavBarItemView) appCMSTabNavContainer.getChildAt(NAV_PAGE_INDEX);
            selectNavItem(menuNavBarItemView);
        }
    }

    private void processDeepLink(Uri deeplinkUri) {
        String title = deeplinkUri.getLastPathSegment();
        String action = getString(R.string.app_cms_action_videopage_key);
        StringBuffer pagePath = new StringBuffer();
        for (String pathSegment : deeplinkUri.getPathSegments()) {
            pagePath.append(File.separatorChar);
            pagePath.append(pathSegment);
        }
        Log.d(TAG, "Launching deep link " +
                deeplinkUri.toString() +
                " with path: " +
                pagePath.toString());
        appCMSPresenter.launchButtonSelectedAction(pagePath.toString(),
                action,
                title,
                null,
                null,
                false);
    }
}
