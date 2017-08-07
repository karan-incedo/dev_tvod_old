package com.viewlift.views.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.vending.billing.IInAppBillingService;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.NavBarItemView;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.fragments.AppCMSEditProfileFragment;
import com.viewlift.views.fragments.AppCMSNavItemsFragment;
import com.viewlift.views.fragments.AppCMSPageFragment;
import com.viewlift.views.fragments.AppCMSResetPasswordFragment;
import com.viewlift.views.fragments.AppCMSSearchFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSPageActivity extends AppCompatActivity implements
        AppCMSPageFragment.OnPageCreation,
        FragmentManager.OnBackStackChangedListener,
        GoogleApiClient.OnConnectionFailedListener {
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

    @BindView(R.id.ll_media_route_button)
    LinearLayout ll_media_route_button;

    @BindView(R.id.media_route_button)
    ImageButton mMediaRouteButton;

    @BindView(R.id.app_cms_close_button)
    ImageButton closeButton;

    CastServiceProvider castProvider;
    private AppCMSPresenter appCMSPresenter;
    private Stack<String> appCMSBinderStack;
    private Map<String, AppCMSBinder> appCMSBinderMap;
    private BroadcastReceiver presenterActionReceiver;
    private BroadcastReceiver presenterCloseActionReceiver;
    private boolean resumeInternalEvents;
    private boolean isActive;
    private boolean shouldSendCloseOthersAction;
    private AppCMSBinder updatedAppCMSBinder;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private IInAppBillingService inAppBillingService;
    private ServiceConnection inAppBillingServiceConn;
    private FirebaseAnalytics mFireBaseAnalytics;

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
                        mergeInputData(updatedAppCMSBinder, updatedAppCMSBinder.getPageId());
                        if (isActive) {
                            handleLaunchPageAction(updatedAppCMSBinder,
                                    false,
                                    false);
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
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_UPDATE_HISTORY_ACTION)) {
                    updateData();
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_REFRESH_PAGE_ACTION)) {
                    AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
                    handleLaunchPageAction(appCMSBinder,
                            false,
                            false);
                }
            }
        };

        presenterCloseActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION)) {
                    handleCloseAction();
                    for (String appCMSBinderKey : appCMSBinderStack) {
                        AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderKey);
                        RefreshAppCMSBinderAction appCMSBinderAction =
                                new RefreshAppCMSBinderAction(appCMSBinder);
                        appCMSPresenter.refreshPageAPIData(appCMSBinder.getAppCMSPageUI(),
                                appCMSBinder.getPageId(),
                                appCMSBinderAction);
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
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_UPDATE_HISTORY_ACTION));
        registerReceiver(presenterActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_REFRESH_PAGE_ACTION));

        resumeInternalEvents = false;

        shouldSendCloseOthersAction = false;

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                AppCMSPageActivity.this.accessToken = currentAccessToken;
                if (appCMSPresenter != null && currentAccessToken != null) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            currentAccessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject user,
                                        GraphResponse response) {
                                    String username = null;
                                    String email = null;
                                    try {
                                        username = user.getString("name");
                                        email = user.getString("email");
                                    } catch (JSONException e) {
                                        Log.e(TAG, "Error parsing Facebook Graph JSON: " + e.getMessage());
                                    }
                                    if (appCMSPresenter.getLaunchType() == AppCMSPresenter.LaunchType.SUBSCRIBE) {
                                        handleCloseAction();
                                    }
                                    appCMSPresenter.setFacebookAccessToken(AppCMSPageActivity.this,
                                            currentAccessToken.getToken(),
                                            currentAccessToken.getUserId(),
                                            username,
                                            email);
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }
        };

        accessToken = AccessToken.getCurrentAccessToken();

        inAppBillingServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                inAppBillingService = null;
                unbindService(this);
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                inAppBillingService = IInAppBillingService.Stub.asInterface(service);
                if (appCMSPresenter != null) {
                    appCMSPresenter.setInAppBillingService(inAppBillingService);
                }
            }
        };

        if (inAppBillingService == null && inAppBillingServiceConn != null) {
            Intent serviceIntent =
                    new Intent("com.android.vending.billing.InAppBillingService.BIND");
            serviceIntent.setPackage("com.android.vending");
            bindService(serviceIntent, inAppBillingServiceConn, Context.BIND_AUTO_CREATE);
        }

        if (appCMSPresenter != null) {
            appCMSPresenter.setInAppBillingServiceConn(inAppBillingServiceConn);
        }

        appCMSParentView.setBackgroundColor(Color.parseColor(updatedAppCMSBinder.getAppCMSMain()
                .getBrand()
                .getGeneral()
                .getBackgroundColor()));

        createMenuNavItem();
        createHomeNavItem(appCMSPresenter.findHomePageNavItem());
        createMoviesNavItem(appCMSPresenter.findMoviesPageNavItem());
        createSearchNavItem(getString(R.string.app_cms_search_page_tag));

        //Settings The Firebase Analytics for Android
        mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (mFireBaseAnalytics != null && appCMSPresenter != null) {
            appCMSPresenter.setmFireBaseAnalytics(mFireBaseAnalytics);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCMSPresenter.sendCloseOthersAction(null, true);
            }
        });

        Log.d(TAG, "onCreate()");
    }

    @Override
    public void onBackPressed() {
        handleCloseAction();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (resumeInternalEvents) {
            appCMSPresenter.restartInternalEvents();
            appCMSPresenter.showMainFragmentView(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
        Log.d(TAG, "onResume()");

    }

    @Override
    protected void onPause() {
        super.onPause();

        appCMSPresenter.cancelInternalEvents();

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
            appCMSPresenter.removeLruCacheItem(this, updatedAppCMSBinder.getPageId());
        }

        unregisterReceiver(presenterActionReceiver);
        accessTokenTracker.stopTracking();

        if (inAppBillingService != null) {
            unbindService(inAppBillingServiceConn);
            inAppBillingServiceConn = null;
            inAppBillingService = null;
        }

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

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

        if (resultCode == Activity.RESULT_OK) {
            if (result != null && result.isSuccess()) {
                if (appCMSPresenter.getLaunchType() == AppCMSPresenter.LaunchType.SUBSCRIBE) {
                    handleCloseAction();
                }
                appCMSPresenter.setGoogleAccessToken(this, result.getSignInAccount().getIdToken(),
                        result.getSignInAccount().getId(),
                        result.getSignInAccount().getDisplayName(),
                        result.getSignInAccount().getEmail());
            }

            if (FacebookSdk.isFacebookRequestCode(requestCode)) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            } else if (requestCode == AppCMSPresenter.RC_PURCHASE_PLAY_STORE_ITEM) {
                appCMSPresenter.finalizeSignupAfterSubscription(data.getStringExtra("INAPP_PURCHASE_DATA"));
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == AppCMSPresenter.RC_PURCHASE_PLAY_STORE_ITEM) {
                if (!TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionSku(this))) {
                    appCMSPresenter.showConfirmCancelSubscriptionDialog(retry -> {
                        if (retry) {
                            appCMSPresenter.initiateItemPurchase();
                        } else {
                            appCMSPresenter.setActiveSubscriptionSku(this, null);
                            handleBack(true, true, false, true);
                        }
                    });
                } else {
                    appCMSPresenter.setActiveSubscriptionSku(this, null);
                    handleBack(true, true, false, true);
                }
            }
        }
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
            handleBack(true, false, false, true);
        }

        if (appCMSBinderStack.size() > 0) {
            handleLaunchPageAction(appCMSBinderMap.get(appCMSBinderStack.peek()),
                    false,
                    false);
        } else {
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (appCMSPresenter != null) {
            appCMSPresenter.cancelInternalEvents();
            appCMSPresenter.onConfigurationChange(true);
            if (appCMSPresenter.isMainFragmentViewVisible()) {
                AppCMSBinder appCMSBinder = appCMSBinderStack.size() > 0 ?
                        appCMSBinderMap.get(appCMSBinderStack.peek()) :
                        null;
                if (appCMSBinder != null) {
                    appCMSPresenter.pushActionInternalEvents(appCMSBinder.getPageId()
                            + BaseView.isLandscape(this));
                    handleLaunchPageAction(appCMSBinder,
                            true,
                            false);
                }
            }
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

    private void handleBack(boolean popBinderStack,
                            boolean closeActionPage,
                            boolean recurse,
                            boolean popActionStack) {
        if (popBinderStack && appCMSBinderStack.size() > 0) {
            appCMSBinderStack.pop();
            if (popActionStack) {
                appCMSPresenter.popActionInternalEvents();
            }
        }

        if (appCMSBinderStack.size() > 0) {
            updatedAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
            Log.d(TAG, "Back pressed - handling nav bar");
            handleNavbar(appCMSBinderMap.get(appCMSBinderStack.peek()));
            Log.d(TAG, "Resetting previous AppCMS data: "
                    + appCMSBinderMap.get(appCMSBinderStack.peek()).getPageName());
        }

        if (shouldPopStack(null) || closeActionPage) {
            try {
                getSupportFragmentManager().popBackStack();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Failed to pop Fragment from back stack");
            }
            if (recurse) {
                Log.d(TAG, "Handling back - recursive op");
                handleBack(popBinderStack,
                        closeActionPage && appCMSBinderStack.size() > 0,
                        recurse,
                        popActionStack);
            }
        }
    }

    private void resume() {
        appCMSPresenter.restartInternalEvents();

        if (appCMSBinderStack != null && appCMSBinderStack.size() > 0) {
            Log.d(TAG, "Activity resumed - resetting nav item");
            selectNavItem(appCMSBinderStack.peek());
        }

        if (!isActive) {
            if (updatedAppCMSBinder != null) {
                handleLaunchPageAction(updatedAppCMSBinder,
                        appCMSPresenter.getConfigurationChanged(),
                        false);
            }
        }

        isActive = true;

        if (shouldSendCloseOthersAction && appCMSPresenter != null) {
            appCMSPresenter.sendCloseOthersAction(null, false);
            shouldSendCloseOthersAction = false;
        }

        setCastingInstance();

        registerReceiver(presenterCloseActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));
    }

    private boolean shouldPopStack(String newPageId) {
        if (!isBinderStackEmpty() && !isBinderStackTopNull()) {
            return !appCMSPresenter.isPagePrimary(appCMSBinderStack.peek()) &&
                    !waitingForSubscriptionFinalization() &&
                    !onlyOneUserPageOnStack(newPageId);
        }
        return false;
    }

    private boolean isBinderStackEmpty() {
        return appCMSBinderStack.size() == 0;
    }

    private boolean isBinderStackTopNull() {
        return appCMSBinderMap.get(appCMSBinderStack.peek()) == null;
    }

    private boolean waitingForSubscriptionFinalization() {
        return (appCMSPresenter.isViewPlanPage(appCMSBinderStack.peek()) &&
                appCMSPresenter.getLaunchType() == AppCMSPresenter.LaunchType.SUBSCRIBE);
    }

    private boolean onlyOneUserPageOnStack(String newPageId) {
        return ((newPageId == null &&
                appCMSPresenter.isPageUser(appCMSBinderStack.peek())) ||
                (newPageId != null &&
                        (!appCMSPresenter.isPageUser(newPageId) ||
                                !appCMSPresenter.isPageUser(appCMSBinderStack.peek()))));
    }

    private void createScreenFromAppCMSBinder(final AppCMSBinder appCMSBinder) {
        Log.d(TAG, "Handling new AppCMSBinder: " + appCMSBinder.getPageName());

        pageLoading(false);

        if (appCMSPresenter.isPagePrimary(appCMSBinder.getPageId()) &&
                !appCMSPresenter.isViewPlanPage(appCMSBinder.getPageId())) {
            closeButton.setVisibility(View.GONE);
        } else {
            closeButton.setVisibility(View.VISIBLE);
        }

        Log.d(TAG, "createScreenFromAppCMSBinder() - Handling Navbar");
        handleNavbar(appCMSBinder);

        handleOrientation(getResources().getConfiguration().orientation, appCMSBinder);
        createFragment(appCMSBinder);
    }

    private void createFragment(AppCMSBinder appCMSBinder) {
        try {
            getSupportFragmentManager().addOnBackStackChangedListener(this);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment appCMSPageFragment = null;

            switch (appCMSBinder.getExtraScreenType()) {
                case NAVIGATION:
                    appCMSPageFragment =
                            AppCMSNavItemsFragment.newInstance(this,
                                   appCMSBinder,
                                    Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getTextColor()),
                                    Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()),
                                    Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()),
                                    Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor()));
                    break;

                case SEARCH:
                    appCMSPageFragment = AppCMSSearchFragment.newInstance(this,
                            Long.parseLong(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()
                                    .replace("#", ""), 16),
                            Long.parseLong(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()
                                    .replace("#", ""), 16),
                            Long.parseLong(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getTextColor()
                                    .replace("#", ""), 16));
                    break;

                case RESET_PASSWORD:
                    appCMSPageFragment =
                            AppCMSResetPasswordFragment.newInstance(this, appCMSBinder.getPagePath());
                    break;

                case EDIT_PROFILE:
                    appCMSPageFragment =
                            AppCMSEditProfileFragment.newInstance(this,
                                    appCMSPresenter.getLoggedInUserName(this),
                                    appCMSPresenter.getLoggedInUserEmail(this));
                    break;

                case NONE:
                    appCMSPageFragment = AppCMSPageFragment.newInstance(this, appCMSBinder);
                    break;

                default:
            }

            if (appCMSPageFragment != null) {
                fragmentTransaction.replace(R.id.app_cms_fragment, appCMSPageFragment,
                        appCMSBinder.getPageId() + BaseView.isLandscape(this));
                fragmentTransaction.addToBackStack(appCMSBinder.getPageId() + BaseView.isLandscape(this));
                fragmentTransaction.commit();
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "Failed to add Fragment to back stack");
        }

        /*
         * casting button will show only on home page, movie page and player page so check which page will be open
         */

        setMediaRouterButtonVisibility(appCMSBinder.getPageId());
    }

    private void selectNavItemAndLaunchPage(NavBarItemView v, String pageId, String pageTitle) {
        if (!appCMSPresenter.navigateToPage(pageId,
                pageTitle,
                null,
                false,
                true,
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

    private void handleLaunchPageAction(final AppCMSBinder appCMSBinder,
                                        boolean configurationChanged,
                                        boolean leavingExtraPage) {
        Log.d(TAG, "Launching new page: " + appCMSBinder.getPageName());
        appCMSPresenter.sendGaScreen(appCMSBinder.getScreenName());
        int lastBackstackEntry = getSupportFragmentManager().getBackStackEntryCount();
        if (!configurationChanged &&
                !appCMSBinder.shouldSendCloseAction() &&
                lastBackstackEntry > 0 &&
                (appCMSBinder.getPageId() + BaseView.isLandscape(this))
                        .equals(getSupportFragmentManager()
                                .getBackStackEntryAt(lastBackstackEntry - 1)
                                .getName()) &&
                getSupportFragmentManager().findFragmentByTag(appCMSBinder.getPageId()
                        + BaseView.isLandscape(this)) instanceof AppCMSPageFragment) {
            ((AppCMSPageFragment) getSupportFragmentManager().findFragmentByTag(appCMSBinder.getPageId()
                    + BaseView.isLandscape(this))).refreshView(appCMSBinder);
            pageLoading(false);
            appCMSBinderMap.put(appCMSBinder.getPageId(), appCMSBinder);
            try {
                updatedAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
                appCMSPresenter.showMainFragmentView(true);
                appCMSPresenter.restartInternalEvents();
                appCMSPresenter.dismissOpenDialogs(null);
            } catch (EmptyStackException e) {
                Log.e(TAG, "Error attempting to restart screen: " + appCMSBinder.getScreenName());
                appCMSPresenter.navigateToLoginPage();
            }
        } else {
            int distanceFromStackTop = appCMSBinderStack.search(appCMSBinder.getPageId());
            Log.d(TAG, "Page distance from top: " + distanceFromStackTop);
            int i = 0;
            while (((((i < distanceFromStackTop ||
                    (!isBinderStackEmpty() &&
                            !isBinderStackTopNull() &&
                            !configurationChanged &&
                            !onlyOneUserPageOnStack(appCMSBinder.getPageId()) &&
                            !leavingExtraPage)) &&
                    (!leavingExtraPage && shouldPopStack(appCMSBinder.getPageId()))) ||
                    (appCMSBinder.shouldSendCloseAction() &&
                            appCMSBinderStack.size() > 1 &&
                            i < appCMSBinderStack.size()))) ||
                    (!isBinderStackEmpty() &&
                        !isBinderStackTopNull() &&
                        appCMSBinderMap.get(appCMSBinderStack.peek()).getExtraScreenType() !=
                                AppCMSPresenter.ExtraScreenType.NONE)) {
                Log.d(TAG, "Popping stack to getList to page item");
                try {
                    getSupportFragmentManager().popBackStack();
                } catch (IllegalStateException e) {
                    Log.e(TAG, "DialogType popping back stack: " + e.getMessage());
                }
                if ((i < distanceFromStackTop - 1) ||
                        (!configurationChanged && !onlyOneUserPageOnStack(appCMSBinder.getPageId())) ||
                        appCMSBinderMap.get(appCMSBinderStack.peek()).getExtraScreenType() !=
                                AppCMSPresenter.ExtraScreenType.NONE) {
                    handleBack(true,
                            false,
                            false,
                            !appCMSBinder.shouldSendCloseAction());
                }
                i++;
            }

            if (distanceFromStackTop < 0 ||
                    appCMSBinder.shouldSendCloseAction() ||
                    (configurationChanged && appCMSBinderMap.get(appCMSBinderStack.peek()).getExtraScreenType() ==
                            AppCMSPresenter.ExtraScreenType.NONE)) {
                appCMSBinderStack.push(appCMSBinder.getPageId());
                appCMSBinderMap.put(appCMSBinder.getPageId(), appCMSBinder);
            }

            if (distanceFromStackTop >= 0) {
                switch (appCMSBinder.getExtraScreenType()) {
                    case NAVIGATION:
                        Log.d(TAG, "Popping stack to getList to page item");
                        try {
                            getSupportFragmentManager().popBackStack();
                        } catch (IllegalStateException e) {
                            Log.e(TAG, "DialogType popping back stack: " + e.getMessage());
                        }
                        break;

                    case NONE:
                        break;

                    default:
                }
            }

            appCMSBinder.unsetSendCloseAction();

            updatedAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());

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
        int highlightColor =
                Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor());
        menuNavBarItemView.setImage(getString(R.string.app_cms_menu_icon_name));
        menuNavBarItemView.setLabel(getString(R.string.app_cms_menu_label));
        menuNavBarItemView.setHighlightColor(highlightColor);
        menuNavBarItemView.setOnClickListener(v -> {
            if (appCMSBinderStack.size() > 0) {
                if (!appCMSPresenter.launchNavigationPage()) {
                    Log.e(TAG, "Could not launch navigation page!");
                } else {
                    resumeInternalEvents = true;
                    selectNavItem(menuNavBarItemView);
                }
            }
        });
    }

    private void createHomeNavItem(final NavigationPrimary homePageNav) {
        if (homePageNav != null) {
            final NavBarItemView homeNavBarItemView =
                    (NavBarItemView) appCMSTabNavContainer.getChildAt(HOME_PAGE_INDEX);
            int highlightColor =
                    Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor());
            homeNavBarItemView.setImage(getString(R.string.app_cms_home_icon_name));
            homeNavBarItemView.setHighlightColor(highlightColor);
            homeNavBarItemView.setLabel(homePageNav.getTitle());
            homeNavBarItemView.setOnClickListener(v -> {
                appCMSPresenter.showMainFragmentView(true);
                selectNavItemAndLaunchPage(homeNavBarItemView,
                        homePageNav.getPageId(),
                        homePageNav.getTitle());
            });
            homeNavBarItemView.setTag(homePageNav.getPageId());
        }
    }

    private void createMoviesNavItem(final NavigationPrimary moviePageNav) {
        if (moviePageNav != null) {
            final NavBarItemView moviesNavBarItemView =
                    (NavBarItemView) appCMSTabNavContainer.getChildAt(MOVIES_PAGE_INDEX);
            int highlightColor =
                    Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor());
            moviesNavBarItemView.setImage(getString(R.string.app_cms_movies_icon_name));
            moviesNavBarItemView.setHighlightColor(highlightColor);
            moviesNavBarItemView.setLabel(moviePageNav.getTitle());
            moviesNavBarItemView.setOnClickListener(v -> {
                appCMSPresenter.showMainFragmentView(true);
                selectNavItemAndLaunchPage(moviesNavBarItemView,
                        moviePageNav.getPageId(),
                        moviePageNav.getTitle());
            });
            moviesNavBarItemView.setTag(moviePageNav.getPageId());
        }
    }

    private void createSearchNavItem(String pageId) {
        NavBarItemView searchNavBarItemView =
                (NavBarItemView) appCMSTabNavContainer.getChildAt(SEARCH_INDEX);
        int highlightColor =
                Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor());
        searchNavBarItemView.setImage(getString(R.string.app_cms_search_icon_name));
        searchNavBarItemView.setHighlightColor(highlightColor);
        searchNavBarItemView.setLabel(getString(R.string.app_cms_search_label));
        searchNavBarItemView.setOnClickListener(v -> {
            selectNavItem(searchNavBarItemView);
            appCMSPresenter.launchSearchPage();
        });
        searchNavBarItemView.setTag(pageId);
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
                false,
                0,
                null);
    }

    private void updateData() {
        final AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
        if (appCMSPresenter != null) {
            for (String appCMSBinderKey : appCMSBinderMap.keySet()) {
                final AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderKey);
                if (appCMSBinder != null) {
                    String endPoint = appCMSPresenter.getPageIdToPageAPIUrl(appCMSBinder.getPageId());
                    boolean usePageIdQueryParam = true;
                    if (appCMSPresenter.isPageAVideoPage(appCMSBinder.getPageName())) {
                        endPoint = appCMSPresenter.getPageNameToPageAPIUrl(appCMSBinder.getPageName());
                        usePageIdQueryParam = false;
                    }
                    appCMSPresenter.getPageIdContent(appCMSMain.getApiBaseUrl(),
                            endPoint,
                            appCMSMain.getInternalName(),
                            usePageIdQueryParam,
                            appCMSBinder.getPagePath(),
                            appCMSPageAPI -> {
                                if (appCMSPageAPI != null) {
                                    appCMSBinder.updateAppCMSPageAPI(appCMSPageAPI);
                                }
                            });
                }
            }
        }
    }

    private void mergeInputData(AppCMSBinder updatedAppCMSBinder, String pageId) {
        if (appCMSBinderMap.containsKey(pageId) && appCMSPresenter != null &&
                appCMSPresenter.isPageAVideoPage(updatedAppCMSBinder.getPageName())) {
            AppCMSBinder appCMSBinder = appCMSBinderMap.get(pageId);
            if (updatedAppCMSBinder.getPagePath().equals(appCMSBinder.getPagePath())) {
                AppCMSPageAPI updatedAppCMSPageAPI = updatedAppCMSBinder.getAppCMSPageAPI();
                AppCMSPageAPI appCMSPageAPI = appCMSBinder.getAppCMSPageAPI();
                appCMSPresenter.mergeData(updatedAppCMSPageAPI, appCMSPageAPI);
            }
        }
    }

    @Override
    public void onBackStackChanged() {
        appCMSPresenter.dismissOpenDialogs(null);
        if (!appCMSPresenter.getConfigurationChanged() &&
                !appCMSPresenter.isMainFragmentTransparent()) {
            appCMSPresenter.showMainFragmentView(true);
        }
        appCMSPresenter.onConfigurationChange(false);
        appCMSPresenter.restartInternalEvents();
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Failed to connect for Google SignIn: " + connectionResult.getErrorMessage());
    }

    private void setMediaRouterButtonVisibility(String pageId) {

        if (CastServiceProvider.getInstance(this).isOverlayVisible()) {
            CastServiceProvider.getInstance(this).showIntroOverLay();
        }

        if (appCMSPresenter.findHomePageNavItem().getPageId().equalsIgnoreCase(pageId) ||
                appCMSPresenter.findMoviesPageNavItem().getPageId().equalsIgnoreCase(pageId)) {
            ll_media_route_button.setVisibility(View.VISIBLE);
        } else {
            ll_media_route_button.setVisibility(View.GONE);
        }

    }

    private void setCastingInstance() {
        try {
            CastServiceProvider.getInstance(this).setActivityInstance(AppCMSPageActivity.this, mMediaRouteButton);
            CastServiceProvider.getInstance(this).onActivityResume();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize cast provider: " + e.getMessage());
        }
    }

    private void handleCloseAction() {
        Log.d(TAG, "Received Presenter Close Action: fragment count = "
                + getSupportFragmentManager().getBackStackEntryCount());
        if (appCMSBinderStack.size() > 0) {
            try {
                getSupportFragmentManager().popBackStack();
            } catch (IllegalStateException e) {
                Log.e(TAG, "DialogType popping back stack: " + e.getMessage());
            }

            boolean leavingExtraPage = appCMSBinderMap.get(appCMSBinderStack.peek()).getExtraScreenType() !=
                    AppCMSPresenter.ExtraScreenType.NONE;

            handleBack(true, false, true, true);

            if (appCMSBinderStack.size() == 0) {
                finishAffinity();
                return;
            }

            AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());

            if (!leavingExtraPage) {
                handleBack(false,
                        appCMSBinderStack.size() < 2,
                        false,
                        true);
            }

            appCMSPresenter.pushActionInternalEvents(appCMSBinder.getPageId()
                    + BaseView.isLandscape(this));
            handleLaunchPageAction(appCMSBinder,
                    false,
                    leavingExtraPage);
            isActive = true;
        } else {
            isActive = false;
            finishAffinity();
        }

        if (appCMSPresenter != null) {
            appCMSPresenter.restartInternalEvents();
        }
    }

    private static class RefreshAppCMSBinderAction implements Action1<AppCMSPageAPI> {
        private AppCMSBinder appCMSBinder;

        public RefreshAppCMSBinderAction(AppCMSBinder appCMSBinder) {
            this.appCMSBinder = appCMSBinder;
        }

        @Override
        public void call(AppCMSPageAPI appCMSPageAPI) {
            appCMSBinder.updateAppCMSPageAPI(appCMSPageAPI);
        }
    }
}
