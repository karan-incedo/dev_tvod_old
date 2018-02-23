package com.viewlift.views.activity;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.Utils;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.sites.AppCMSSite;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.MiniPlayerView;
import com.viewlift.views.customviews.NavBarItemView;
import com.viewlift.views.customviews.TabCreator;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.fragments.AppCMSCCAvenueFragment;
import com.viewlift.views.fragments.AppCMSChangePasswordFragment;
import com.viewlift.views.fragments.AppCMSEditProfileFragment;
import com.viewlift.views.fragments.AppCMSMoreFragment;
import com.viewlift.views.fragments.AppCMSNavItemsFragment;
import com.viewlift.views.fragments.AppCMSPageFragment;
import com.viewlift.views.fragments.AppCMSResetPasswordFragment;
import com.viewlift.views.fragments.AppCMSSearchFragment;
import com.viewlift.views.fragments.AppCMSTeamListFragment;

import org.json.JSONException;

import java.io.File;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action0;
import rx.functions.Action1;


/*
 * Created by viewlift on 5/5/17.
 */

public class AppCMSPageActivity extends AppCompatActivity implements
        AppCMSPageFragment.OnPageCreation,
        FragmentManager.OnBackStackChangedListener,
        GoogleApiClient.OnConnectionFailedListener,
        AppCMSSearchFragment.OnSaveSearchQuery,
        TabCreator.OnClickHandler {
    private static final String TAG = "AppCMSPageActivity";

    private static final int DEFAULT_NAV_MENU_PAGE_INDEX = 0;
    private static final int DEFAULT_HOME_PAGE_INDEX = 1;
    private static final int DEFAULT_CATEGORIES_PAGE_INDEX = 2;
    private static final int DEFAULT_SEARCH_INDEX = 3;
    private static final int DEFAULT_NAV_LIVE_PAGE_INDEX = 4;
    private static final int NO_NAV_MENU_PAGE_INDEX = -1;

    private static final String FIREBASE_SCREEN_VIEW_EVENT = "screen_view";
    private final static float CLICK_DRAG_TOLERANCE = 10; // Often, there will be a slight, unintentional, drag when the user taps the view, so we need to account for this.
    private final String FIREBASE_LOGIN_SCREEN_VALUE = "Login Screen";
    private final String LOGIN_STATUS_KEY = "logged_in_status";
    private final String LOGIN_STATUS_LOGGED_IN = "logged_in";
    private final String LOGIN_STATUS_LOGGED_OUT = "not_logged_in";
    @BindView(R.id.app_cms_parent_layout)
    RelativeLayout appCMSParentLayout;
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
    @BindView(R.id.app_cms_cast_conroller)
    FrameLayout appCMSCastController;
    @BindView(R.id.new_version_available_parent)
    FrameLayout newVersionUpgradeAvailable;
    @BindView(R.id.new_version_available_textview)
    TextView newVersionAvailableTextView;
    @BindView(R.id.new_version_available_close_button)
    ImageButton newVersionAvailableCloseButton;
    @BindView(R.id.app_cms_search_button)
    ImageButton mSearchTopButton;
    @BindView(R.id.app_cms_share_button)
    ImageButton mShareTopButton;
    @BindView(R.id.app_cms_profile_btn)
    ImageButton mProfileTopButton;
    @BindView(R.id.app_cms_toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_cms_start_free_trial_tool)
    TextView appCMSNavFreeTrialTool;
    private int currentMenuTabIndex = NO_NAV_MENU_PAGE_INDEX;
    private AppCMSPresenter appCMSPresenter;
    private Stack<String> appCMSBinderStack;
    private Map<String, AppCMSBinder> appCMSBinderMap;
    private BroadcastReceiver presenterActionReceiver;
    private BroadcastReceiver presenterCloseActionReceiver;
    private BroadcastReceiver processDeeplinkReceiver;
    private BroadcastReceiver networkConnectedReceiver;
    private BroadcastReceiver wifiConnectedReceiver;
    private BroadcastReceiver downloadReceiver;
    private BroadcastReceiver notifyUpdateListsReceiver;
    private BroadcastReceiver refreshPageDataReceiver;
    private boolean resumeInternalEvents;
    private boolean isActive;
    private boolean shouldSendCloseOthersAction;
    private AppCMSBinder updatedAppCMSBinder;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private IInAppBillingService inAppBillingService;
    private ServiceConnection inAppBillingServiceConn;
    private boolean handlingClose;
    private boolean castDisabled;
    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;
    private String FIREBASE_SEARCH_SCREEN = "Search Screen";
    private String FIREBASE_MENU_SCREEN = "MENU";
    private String FIREBASE_TEAM_NAVIGATION_SCREEN = "Team Navigation Page";
    private String searchQuery;
    private boolean isDownloadPageOpen = false;
    private boolean loaderWaitingFor3rdPartyLogin = false;
    private boolean isTabCreated = false;
    private LinearLayout appCMSTabNavContainerItems;
    private Uri pendingDeeplinkUri;
    private TabCreator tabCreator;
    private float downRawX, downRawY;
    private float dX, dY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!BaseView.isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_appcms_page);

        ButterKnife.bind(this);
        appCMSPresenter = ((AppCMSApplication) getApplication())
                .getAppCMSPresenterComponent()
                .appCMSPresenter();
        appCMSBinderStack = new Stack<>();
        appCMSBinderMap = new HashMap<>();

        initPageActivity();

        Bundle args = getIntent().getBundleExtra(getString(R.string.app_cms_bundle_key));
        if (args != null) {
            try {
                updatedAppCMSBinder =
                        (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
                if (updatedAppCMSBinder != null) {
                    shouldSendCloseOthersAction = updatedAppCMSBinder.shouldSendCloseAction();
                }
            } catch (ClassCastException e) {
                //Log.e(TAG, "Could not read AppCMSBinder: " + e.toString());
            }
        }

        presenterActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null
                        && intent.getAction().equals(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION)) {

                    Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
                    try {
                        updatedAppCMSBinder =
                                (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
                        if (updatedAppCMSBinder != null) {
                            mergeInputData(updatedAppCMSBinder, updatedAppCMSBinder.getPageId());
                        }
                        if (isActive) {
                            handleLaunchPageAction(updatedAppCMSBinder,
                                    false,
                                    false,
                                    false);
                        } else if (updatedAppCMSBinder != null) {
                            Intent appCMSIntent = new Intent(AppCMSPageActivity.this,
                                    AppCMSPageActivity.class);
                            appCMSIntent.putExtra(AppCMSPageActivity.this.getString(R.string.app_cms_bundle_key), args);
                            appCMSIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            AppCMSPageActivity.this.startActivity(appCMSIntent);
                            if (updatedAppCMSBinder.shouldSendCloseAction()) {
                                shouldSendCloseOthersAction = true;
                            }
                        }
                    } catch (ClassCastException e) {
                        //Log.e(TAG, "Could not read AppCMSBinder: " + e.toString());
                    }
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION)) {
                    loaderWaitingFor3rdPartyLogin = intent.getBooleanExtra(getString(R.string.thrid_party_login_intent_extra_key), false);
                    pageLoading(true);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION)) {
                    loaderWaitingFor3rdPartyLogin = false;
                    pageLoading(false);
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_RESET_NAVIGATION_ITEM_ACTION)) {
//                    Log.d(TAG, "Nav item - Received broadcast to select navigation item with page Id: " +
//                            intent.getStringExtra(getString(R.string.navigation_item_key)));
                    selectNavItem(intent.getStringExtra(getString(R.string.navigation_item_key)));
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_UPDATE_HISTORY_ACTION)) {
                    updateData();
                } else if (intent.getAction().equals(AppCMSPresenter.PRESENTER_REFRESH_PAGE_ACTION)) {
                    if (!appCMSBinderStack.isEmpty()) {
                        AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());

                        handleLaunchPageAction(appCMSBinder,
                                false,
                                false,
                                false);
                    }
                }
            }
        };

        processDeeplinkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null &&
                        intent.getStringExtra(getPackageName()) != null &&
                        !intent.getStringExtra(Utils.getProperty("AppPackageName", AppCMSPageActivity.this)).equals(getPackageName())) {
                    return;
                }

                if (intent == null ||
                        intent.getStringExtra(getPackageName()) == null) {
                    return;
                }

                String deeplinkUrl = intent.getStringExtra(getString(R.string.deeplink_uri_extra_key));
                if (!TextUtils.isEmpty(deeplinkUrl)) {
                    if (!isActive) {
                        if (appCMSPresenter.getCurrentActivity() != null) {
                            try {
                                Intent appCMSIntent = new Intent(appCMSPresenter.getCurrentActivity(),
                                        AppCMSPageActivity.class);
                                appCMSIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                appCMSIntent.putExtra(getString(R.string.deeplink_uri_extra_key), deeplinkUrl);
                                appCMSPresenter.getCurrentActivity().startActivity(appCMSIntent);
                            } catch (Exception e) {

                            }
                        }
                    } else {
                        processDeepLink(Uri.parse(deeplinkUrl));
                    }
                }
            }
        };
        presenterCloseActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getAction() != null
                        && intent.getAction().equals(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION)) {
                    boolean closeSelf = intent.getBooleanExtra(getString(R.string.close_self_key),
                            false);
                    boolean closeOnePage = intent.getBooleanExtra(getString(R.string.close_one_page_key), false);
                    if (closeSelf && !handlingClose && appCMSBinderStack.size() > 1) {
                        handlingClose = true;
                        handleCloseAction(closeOnePage);
                        for (String appCMSBinderKey : appCMSBinderStack) {
                            AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderKey);
                            if (appCMSBinder != null) {
                                RefreshAppCMSBinderAction appCMSBinderAction =
                                        new RefreshAppCMSBinderAction(appCMSPresenter,
                                                appCMSBinder,
                                                appCMSPresenter.isUserLoggedIn());
                                if (appCMSBinder != null) {
                                    appCMSPresenter.refreshPageAPIData(appCMSBinder.getAppCMSPageUI(),
                                            appCMSBinder.getPageId(),
                                            appCMSBinderAction);
                                }
                            }
                        }
                        handlingClose = false;
                    }

                    appCMSPresenter.initiateAfterLoginAction();
                }
            }
        };

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        appCMSPresenter.setNetworkConnected(isConnected, null);
        networkConnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                String pageId = "";
                if (!appCMSBinderStack.isEmpty()) {
                    pageId = appCMSBinderStack.peek();
                    if (appCMSPresenter.getNetworkConnectedState() && !isConnected) {
                        appCMSPresenter.setShowNetworkConnectivity(true);
                        appCMSPresenter.showNoNetworkConnectivityToast();
                    } else {
                        appCMSPresenter.setShowNetworkConnectivity(false);
                    }
                }
                appCMSPresenter.setNetworkConnected(isConnected, pageId);
            }
        };

        if (getApplicationContext() != null) {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiConnectedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    appCMSPresenter.setWifiConnected(wifiManager.isWifiEnabled());
                }
            };
        }

        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    DownloadManager.Query downloadQuery = new DownloadManager.Query();
                    downloadQuery.setFilterById(referenceId);

                    @SuppressWarnings("ConstantConditions")
                    Cursor cursor = downloadManager.query(downloadQuery);

                    if (cursor.moveToFirst()) {
                        try {
                            String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE));
                            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                            if (mimeType.contains("mp4") &&
                                    (status == DownloadManager.STATUS_SUCCESSFUL ||
                                            status == DownloadManager.STATUS_FAILED)) {
                                appCMSPresenter.startNextDownload();
                            }
                        } catch (Exception e) {
                            //
                        }
                    }
                }
            }
        };
        notifyUpdateListsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                for (Fragment fragment : fragmentList) {
                    if (fragment instanceof AppCMSPageFragment) {
                        ((AppCMSPageFragment) fragment).updateDataLists();

                    }
                }
            }
        };


        refreshPageDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshPageData();
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
        registerReceiver(wifiConnectedReceiver,
                new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        registerReceiver(downloadReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(notifyUpdateListsReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_UPDATE_LISTS_ACTION));
        registerReceiver(refreshPageDataReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_REFRESH_PAGE_DATA_ACTION));
        registerReceiver(processDeeplinkReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_DEEPLINK_ACTION));


        resumeInternalEvents = false;

        shouldSendCloseOthersAction = false;

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AppCMSPageActivity.this.accessToken = loginResult.getAccessToken();
                        if (appCMSPresenter != null && AppCMSPageActivity.this.accessToken != null) {
                            GraphRequest request = GraphRequest.newMeRequest(
                                    AppCMSPageActivity.this.accessToken,
                                    (user, response) -> {
                                        String username = null;
                                        String email = null;
                                        try {
                                            username = user.getString("name");
                                            email = user.getString("email");
                                        } catch (JSONException | NullPointerException e) {
                                            //Log.e(TAG, "Error parsing Facebook Graph JSON: " + e.getMessage());
                                        }

                                        if (appCMSPresenter.getLaunchType() == AppCMSPresenter.LaunchType.SUBSCRIBE) {
                                            handleCloseAction(false);
                                        }
                                        appCMSPresenter.setFacebookAccessToken(
                                                AppCMSPageActivity.this.accessToken.getToken(),
                                                AppCMSPageActivity.this.accessToken.getUserId(),
                                                username,
                                                email,
                                                false,
                                                true);
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
//                        Log.e(TAG, "Facebook login was cancelled");
                        loaderWaitingFor3rdPartyLogin = false;
                        pageLoading(false);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
//                        Log.e(TAG, "Facebook login exception: " + exception.getMessage());
                        loaderWaitingFor3rdPartyLogin = false;
                        pageLoading(false);
                    }
                });

        appCMSPresenter.sendCloseOthersAction(null, false, false);

//        Log.d(TAG, "onCreate()");
    }

    private void initPageActivity() {
        AndroidThreeTen.init(this);

        accessToken = AccessToken.getCurrentAccessToken();

        inAppBillingServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                inAppBillingService = null;
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

        //noinspection ConstantConditions
        if (inAppBillingService == null && inAppBillingServiceConn != null) {
            Intent serviceIntent =
                    new Intent("com.android.vending.billing.InAppBillingService.BIND");
            serviceIntent.setPackage("com.android.vending");
            bindService(serviceIntent, inAppBillingServiceConn, Context.BIND_AUTO_CREATE);
        }

        if (appCMSPresenter != null) {
            appCMSPresenter.setInAppBillingServiceConn(inAppBillingServiceConn);
        }

        if (updatedAppCMSBinder != null) {
            try {
                appCMSParentView.setBackgroundColor(Color.parseColor(updatedAppCMSBinder.getAppCMSMain()
                        .getBrand()
                        .getGeneral()
                        .getBackgroundColor()));
            } catch (Exception e) {
//                //Log.w(TAG, "Could not set background color of app based upon AppCMS branding - defaulting to primaryDark: " +
//                        e.getMessage());
                appCMSParentView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        }

        int tabCount = getResources().getInteger(R.integer.number_of_tabs);
        manageTopBar();
        createTabBar();
        startFreeTrialTool();


        //Settings The Firebase Analytics for Android
        FirebaseAnalytics mFireBaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (mFireBaseAnalytics != null && appCMSPresenter != null) {
            appCMSPresenter.setmFireBaseAnalytics(mFireBaseAnalytics);
        }


        setToolItemsUIColor();

        closeButton.setOnClickListener(v -> {
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        //noinspection ConstantConditions
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    appCMSPresenter.sendCloseOthersAction(null, true, false);
                }
        );
        appCMSNavFreeTrialTool.setOnClickListener(v -> {
            if (appCMSPresenter != null) {
                appCMSPresenter.setLaunchType(AppCMSPresenter.LaunchType.SUBSCRIBE);
                appCMSPresenter.navigateToSubscriptionPlansPage(true);
            }
        });

        //ToDo:  dynamically visible/hide search /profile btn as per API response, currently showing for MSE
        mSearchTopButton.setOnClickListener(v -> {
                    appCMSPresenter.launchSearchPage();
                }
        );

        mShareTopButton.setOnClickListener(v -> {
                    AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
                    if (appCMSMain != null &&
                            updatedAppCMSBinder != null &&
                            updatedAppCMSBinder.getAppCMSPageAPI() != null &&
                            updatedAppCMSBinder.getAppCMSPageAPI().getModules() != null &&
                            updatedAppCMSBinder.getAppCMSPageAPI().getModules().get(0) != null &&
                            updatedAppCMSBinder.getAppCMSPageAPI().getModules().get(0).getContentData() != null &&
                            !updatedAppCMSBinder.getAppCMSPageAPI().getModules().get(0).getContentData().isEmpty() &&
                            updatedAppCMSBinder.getAppCMSPageAPI().getModules().get(0).getContentData().get(0) != null &&
                            updatedAppCMSBinder.getAppCMSPageAPI().getModules().get(0).getContentData().get(0).getGist() != null &&
                            updatedAppCMSBinder.getAppCMSPageAPI().getModules().get(0).getContentData().get(0).getGist().getTitle() != null &&
                            updatedAppCMSBinder.getAppCMSPageAPI().getModules().get(0).getContentData().get(0).getGist().getPermalink() != null) {
                        StringBuilder filmUrl = new StringBuilder();
                        filmUrl.append(appCMSMain.getDomainName());
                        filmUrl.append(updatedAppCMSBinder.getAppCMSPageAPI().getModules().get(0).getContentData().get(0).getGist().getPermalink());
                        String[] extraData = new String[1];
                        extraData[0] = filmUrl.toString();
                        appCMSPresenter.launchButtonSelectedAction(updatedAppCMSBinder.getAppCMSPageAPI().getModules().get(0).getContentData().get(0).getGist().getPermalink(),
                                getString(R.string.app_cms_action_share_key),
                                updatedAppCMSBinder.getAppCMSPageAPI().getModules().get(0).getContentData().get(0).getGist().getTitle(),
                                extraData,
                                updatedAppCMSBinder.getAppCMSPageAPI().getModules().get(0).getContentData().get(0),
                                false,
                                0,
                                null);
                    }
                }
        );

        //ToDo:  dynamically visible/hide search /profile btn as per API response, currently showing for MSE
        mProfileTopButton.setOnClickListener(v -> {
                    if (appCMSPresenter.isUserLoggedIn()) {
                        appCMSPresenter.launchNavigationPage();
                    } else {
                        if (appCMSPresenter != null) {
                            appCMSPresenter.setLaunchType(AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP);
                            appCMSPresenter.navigateToLoginPage(false);
                            Bundle bundle = new Bundle();
                            bundle.putString(FIREBASE_SCREEN_VIEW_EVENT, FIREBASE_LOGIN_SCREEN_VALUE);
                            String firebaseEventKey = FirebaseAnalytics.Event.VIEW_ITEM;
                            if (appCMSPresenter.isUserLoggedIn()) {
                                appCMSPresenter.getmFireBaseAnalytics().setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_IN);
                            } else {
                                appCMSPresenter.getmFireBaseAnalytics().setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_OUT);
                            }
                            appCMSPresenter.sendFirebaseSelectedEvents(firebaseEventKey, bundle);
                        }
                    }
                }
        );
        inflateCastMiniController();

        if (loadingProgressBar != null) {
            try {
                loadingProgressBar.getIndeterminateDrawable().setTint(Color.parseColor(appCMSPresenter.getAppCMSMain()
                        .getBrand().getCta().getPrimary().getBackgroundColor()));
            } catch (Exception e) {
//                //Log.w(TAG, "Failed to set color for loader: " + e.getMessage());
                loadingProgressBar.getIndeterminateDrawable().setTint(ContextCompat.getColor(this, R.color.colorAccent));
            }
        }

        if (appCMSPresenter != null) {
            try {
                newVersionUpgradeAvailable.setBackgroundColor(Color.parseColor(
                        appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()));
                newVersionAvailableTextView.setTextColor(Color.parseColor(
                        appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getTextColor()));
            } catch (Exception e) {
//                //Log.w(TAG, "Failed to set AppCMS branding colors for soft upgrade messages: " +
//                        e.getMessage());
            }
        }

        newVersionAvailableTextView.setOnClickListener((v) -> {
            Intent googlePlayStoreUpgradeAppIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.google_play_store_upgrade_app_url,
                            getString(R.string.package_name))));
            startActivity(googlePlayStoreUpgradeAppIntent);
        });

        newVersionAvailableCloseButton.setOnClickListener((v) -> {
            ValueAnimator heightAnimator = ValueAnimator.ofInt(newVersionUpgradeAvailable.getHeight(),
                    0);
            heightAnimator.addUpdateListener((animation) -> {
                Integer value = (Integer) animation.getAnimatedValue();
                newVersionUpgradeAvailable.getLayoutParams().height = value;
                if (value == 0) {
                    newVersionUpgradeAvailable.setVisibility(View.GONE);
                }
                newVersionUpgradeAvailable.requestLayout();
            });

            AnimatorSet set = new AnimatorSet();
            set.play(heightAnimator);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
        });
    }

    private boolean shouldShowSearchInToolbar() {
        if (appCMSPresenter.getNavigation() != null &&
                appCMSPresenter.getNavigation().getRight() != null &&
                !appCMSPresenter.getNavigation().getRight().isEmpty()) {
            List<NavigationPrimary> rightNav = appCMSPresenter.getNavigation().getRight();
            int numNavItems = rightNav.size();
            for (int i = 0; i < numNavItems; i++) {
                NavigationPrimary navItem = rightNav.get(i);
                if (navItem != null &&
                        navItem.getTitle() != null &&
                        navItem.getTitle().equals(getString(R.string.app_cms_search_label))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setToolItemsUIColor() {
        int fillColor = appCMSPresenter.getBrandPrimaryCtaColor();

        //Changing color of SVG image
        mMediaRouteButton.setColorFilter(fillColor, PorterDuff.Mode.SRC_IN);

        mProfileTopButton.getDrawable().setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
        mProfileTopButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        mSearchTopButton.getDrawable().setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
        mSearchTopButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        mShareTopButton.getDrawable().setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
        mShareTopButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        closeButton.getDrawable().setColorFilter(new PorterDuffColorFilter(fillColor, PorterDuff.Mode.MULTIPLY));
        closeButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

    }

    private void inflateCastMiniController() {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) ==
                ConnectionResult.SUCCESS && appCMSPresenter.isNetworkConnected()) {
            try {
                LayoutInflater.from(this).inflate(R.layout.fragment_castminicontroller, appCMSCastController);
                castDisabled = false;
            } catch (Exception e) {
                castDisabled = true;
            }
        } else {
            castDisabled = true;
        }
    }

    @Override
    public void onBackPressed() {

        if (AppCMSPresenter.isFullScreenVisible) {
            appCMSPresenter.exitFullScreenPlayer();
            return;
        }
        appCMSPresenter.setEntitlementPendingVideoData(null);
        if (!handlingClose && !isPageLoading()) {
            if (appCMSPresenter.isAddOnFragmentVisible()) {
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof AppCMSMoreFragment) {
                        ((AppCMSMoreFragment) fragment).sendDismissAction();
                    }
                }
                return;
            }

            handlingClose = true;
            handleCloseAction(false);
            handlingClose = false;
        } else if (isPageLoading()) {
            pageLoading(false);
            appCMSPresenter.setIsLoading(false);
            appCMSPresenter.setNavItemToCurrentAction(this);
        }

        appCMSPresenter.cancelCustomToast();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (resumeInternalEvents) {
            appCMSPresenter.restartInternalEvents();
        }
        appCMSPresenter.setCancelAllLoads(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appCMSPresenter == null) {
            appCMSPresenter = ((AppCMSApplication) getApplication())
                    .getAppCMSPresenterComponent()
                    .appCMSPresenter();
        }
        if (!BaseView.isTablet(this)) {
            appCMSPresenter.restrictPortraitOnly();
        } else {
            appCMSPresenter.unrestrictPortraitOnly();
        }

        if (this.findViewById(R.id.video_player_id) != null &&
                appCMSPresenter.isAutoRotate()) {
            appCMSPresenter.unrestrictPortraitOnly();
        } else if (!BaseView.isTablet(this)) {
            appCMSPresenter.restrictPortraitOnly();
        } else if (BaseView.isTablet(this)) {
            appCMSPresenter.unrestrictPortraitOnly();
        }
        resume();

        try {
            registerReceiver(networkConnectedReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception e) {
            //
        }

        appCMSPresenter.setCancelAllLoads(false);

        appCMSPresenter.setCurrentActivity(this);
//        Log.d(TAG, "onResume()");
        //Log.d(TAG, "checkForExistingSubscription()");

        appCMSPresenter.checkForExistingSubscription(false);

        appCMSPresenter.refreshPages(() -> {
            if (appCMSPresenter.isAppBelowMinVersion()) {
                appCMSPresenter.launchUpgradeAppActivity();
            } else if (appCMSPresenter.isAppUpgradeAvailable()) {
                newVersionUpgradeAvailable.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                newVersionAvailableTextView.setText("");
                newVersionAvailableTextView.setText(getString(R.string.a_new_version_of_the_app_is_available_text,
                        getString(R.string.app_cms_app_version),
                        appCMSPresenter.getGooglePlayAppStoreVersion()));
                newVersionUpgradeAvailable.setVisibility(View.VISIBLE);
                newVersionUpgradeAvailable.requestLayout();
            } else {
                newVersionUpgradeAvailable.setVisibility(View.GONE);
                refreshPageData();
            }
        }, true, 0, 0);

        if (appCMSBinderMap != null && !appCMSBinderMap.isEmpty() && appCMSBinderStack != null && !appCMSBinderStack.isEmpty()) {
            AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
            isDownloadPageOpen = appCMSBinder != null && appCMSBinder.getPageId() != null && appCMSBinder.getPageId().equalsIgnoreCase(appCMSPresenter.getDownloadPageId());
        }

        try {
            if (appCMSPresenter.isDownloadPage(updatedAppCMSBinder.getPageId()) &&
                    !appCMSPresenter.isNetworkConnected() &&
                    appCMSPresenter.shouldShowNetworkContectivity()) {
                appCMSPresenter.showNoNetworkConnectivityToast();
                appCMSPresenter.setShowNetworkConnectivity(false);
            }
        } catch (Exception e) {

        }
        if (pendingDeeplinkUri != null) {
            processDeepLink(pendingDeeplinkUri);
            pendingDeeplinkUri = null;
        }
    }

    private void refreshPageData() {
        boolean cancelLoadingOnFinish = false;
        if (!appCMSPresenter.isPageLoading()) {
            pageLoading(true);
            cancelLoadingOnFinish = true;
        }
        if (appCMSBinderMap != null &&
                appCMSBinderStack != null &&
                !appCMSBinderStack.isEmpty()) {
            AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
            if (appCMSBinder != null) {
                AppCMSPageUI appCMSPageUI = appCMSPresenter.getAppCMSPageUI(appCMSBinder.getScreenName());
                if (appCMSPageUI != null) {
                    appCMSBinder.setAppCMSPageUI(appCMSPageUI);
                } else if (cancelLoadingOnFinish) {
                    pageLoading(false);
                }
                updateData(appCMSBinder, () -> appCMSPresenter.sendRefreshPageAction());
            } else if (cancelLoadingOnFinish) {
                pageLoading(false);
            }
        } else if (cancelLoadingOnFinish) {
            pageLoading(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        pageLoading(false);

        appCMSPresenter.cancelInternalEvents();

        isActive = false;

        appCMSPresenter.closeSoftKeyboard();
        appCMSPresenter.cancelCustomToast();

        if (AppCMSPresenter.isFullScreenVisible) {
            appCMSPresenter.exitFullScreenPlayer();
        }
        if (appCMSPresenter.videoPlayerView != null) {
            appCMSPresenter.videoPlayerView.pausePlayer();
        }
        try {
            unregisterReceiver(networkConnectedReceiver);
        } catch (Exception e) {
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            if (intent != null) {
                Bundle args = intent.getBundleExtra(getString(R.string.app_cms_bundle_key));
                if (args != null) {
                    updatedAppCMSBinder =
                            (AppCMSBinder) args.getBinder(getString(R.string.app_cms_binder_key));
                    if (updatedAppCMSBinder != null) {
                        mergeInputData(updatedAppCMSBinder, updatedAppCMSBinder.getPageId());
                    }
                    if (isActive) {
                        handleLaunchPageAction(updatedAppCMSBinder,
                                false,
                                false,
                                false);
                    }
                }

                String deeplinkUri = intent.getStringExtra(getString(R.string.deeplink_uri_extra_key));
                if (!TextUtils.isEmpty(deeplinkUri)) {
                    pendingDeeplinkUri = Uri.parse(deeplinkUri);
                }
            }
        } catch (Exception e) {
            //
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        appCMSPresenter.cancelInternalEvents();

        appCMSPresenter.setShowNetworkConnectivity(true);

        if (!appCMSBinderStack.isEmpty() &&
                isPageLoading() &&
                appCMSPresenter.isPageLoginPage(appCMSBinderStack.peek()) &&
                appCMSPresenter.isUserLoggedIn()) {
            handleCloseAction(true);
        }
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

        try {
            unregisterReceiver(presenterActionReceiver);
            unregisterReceiver(wifiConnectedReceiver);
            unregisterReceiver(downloadReceiver);
            unregisterReceiver(notifyUpdateListsReceiver);
            unregisterReceiver(refreshPageDataReceiver);
            unregisterReceiver(processDeeplinkReceiver);
            unregisterReceiver(presenterCloseActionReceiver);
        } catch (IllegalArgumentException e) {
//            Log.e(TAG, "receiver not regiestered " + e.getMessage());
//            e.printStackTrace();
        }


        if (inAppBillingServiceConn != null) {
            try {
                unbindService(inAppBillingServiceConn);
                inAppBillingServiceConn = null;
                inAppBillingService = null;
            } catch (Exception e) {
//                //Log.e(TAG, "Unable to unbind Google Play Services connection: " + e.getMessage());
            }
        }

        InputMethodManager imm =
                (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && appCMSParentView != null) {
            imm.hideSoftInputFromWindow(appCMSParentView.getWindowToken(), 0);
        }

        appCMSPresenter.setCancelAllLoads(true);

        appCMSPresenter.dismissPopupWindowPlayer(false);
        appCMSPresenter.setCancelAllLoads(true);

        appCMSPresenter.resetLaunched();
        appCMSPresenter.clearVideoPlayerViewCache();
        appCMSPresenter.clearWebViewCache();
        appCMSPresenter.setMiniPLayerVisibility(true);


    }

    @Override
    public void onSuccess(AppCMSBinder appCMSBinder) {
        appCMSPresenter.restartInternalEvents();
        resumeInternalEvents = true;

        if (appCMSBinder != null && appCMSBinder.getSearchQuery() != null) {
            //Log.d(TAG, "Successfully loaded page " + appCMSBinder.getPageName());
            //Log.d(TAG, "Processing search query for deeplink " +
//                    appCMSBinder.getSearchQuery().toString());
            processDeepLink(appCMSBinder.getSearchQuery());
            appCMSBinder.clearSearchQuery();
        }

        try {
            reportFullyDrawn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Log.d(TAG, "Received other activity result");

        appCMSPresenter.setCurrentActivity(this);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppCMSPresenter.RC_GOOGLE_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result != null && result.isSuccess()) {
                    //Log.d(TAG, "Google Signin Status Message: " + result.getStatus().getStatusMessage());
                    if (appCMSPresenter.getLaunchType() == AppCMSPresenter.LaunchType.SUBSCRIBE) {
                        handleCloseAction(false);
                    }
                    appCMSPresenter.setGoogleAccessToken(result.getSignInAccount().getIdToken(),
                            result.getSignInAccount().getId(),
                            result.getSignInAccount().getDisplayName(),
                            result.getSignInAccount().getEmail(),
                            false,
                            true);
                }
            } else if (requestCode == AppCMSPresenter.ADD_GOOGLE_ACCOUNT_TO_DEVICE_REQUEST_CODE) {
                appCMSPresenter.initiateItemPurchase();
            } else if (requestCode == AppCMSPresenter.CC_AVENUE_REQUEST_CODE) {
                boolean subscriptionSuccess = data.getBooleanExtra(getString(R.string.app_cms_ccavenue_payment_success),
                        false);
                if (subscriptionSuccess) {
                    appCMSPresenter.finalizeSignupAfterCCAvenueSubscription(data);
                }
            } else {
                if (FacebookSdk.isFacebookRequestCode(requestCode)) {
                    pageLoading(true);
                    callbackManager.onActivityResult(requestCode, resultCode, data);
                } else if (requestCode == AppCMSPresenter.RC_PURCHASE_PLAY_STORE_ITEM) {
                    appCMSPresenter.finalizeSignupAfterSubscription(data.getStringExtra("INAPP_PURCHASE_DATA"));
                    //Log.d(TAG, "Finalizing signup after subscription");
                }
            }

        } else if (resultCode == RESULT_CANCELED) {
            loaderWaitingFor3rdPartyLogin = false;
            pageLoading(false);
            if (requestCode == AppCMSPresenter.RC_PURCHASE_PLAY_STORE_ITEM) {
                if (!TextUtils.isEmpty(appCMSPresenter.getActiveSubscriptionSku())) {
                    appCMSPresenter.showConfirmCancelSubscriptionDialog(retry -> {
                        if (retry) {
                            appCMSPresenter.initiateItemPurchase();
                        } else {
                            appCMSPresenter.sendCloseOthersAction(null, true, false);
                        }
                    });
                }
            } else if (requestCode == AppCMSPresenter.RC_GOOGLE_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                String message = null;
                if (result == null || result.getStatus() == null) {
                    message = "Additional resolution is required.";
                } else {
                    int statusCode = result.getStatus().getStatusCode();

                    switch (statusCode) {
                        case CommonStatusCodes.API_NOT_CONNECTED:
                            message = "The API is not connected.";
                            break;

                        case CommonStatusCodes.CANCELED:
                            break;

                        case CommonStatusCodes.DEVELOPER_ERROR:
                            message = "The app is configured incorrectly.";
                            break;

                        case CommonStatusCodes.ERROR:
                            message = "An error has occurred.";
                            break;

                        case CommonStatusCodes.INTERNAL_ERROR:
                            message = "An internal server error has occurred.";
                            break;

                        case CommonStatusCodes.INTERRUPTED:
                            message = "The login attempt was interrupted.";
                            break;

                        case CommonStatusCodes.INVALID_ACCOUNT:
                            message = "An invalid account is being used.";
                            break;

                        case CommonStatusCodes.NETWORK_ERROR:
                            message = "A network error has occurred.";
                            break;

                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            message = "Additional resolution is required.";
                            break;

                        case CommonStatusCodes.SIGN_IN_REQUIRED:
                            message = "Sign In is required.";
                            break;

                        case CommonStatusCodes.TIMEOUT:
                            message = "A timeout has occurred.";
                            break;

                        default:
                            break;
                    }
                }
                if (!TextUtils.isEmpty(message)) {
                    //Log.e(TAG, "Google Signin Status Message: " + message);
                    appCMSPresenter.showDialog(AppCMSPresenter.DialogType.SIGNIN,
                            message,
                            false,
                            null,
                            null);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppCMSPresenter.REQUEST_WRITE_EXTERNAL_STORAGE_FOR_DOWNLOADS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    appCMSPresenter.resumeDownloadAfterPermissionGranted();
                }
                break;

            default:
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onError(AppCMSBinder appCMSBinder) {
        if (appCMSBinder != null && appCMSBinder.getPageId() != null) {
            //Log.e(TAG, "Nav item - DialogType attempting to launch page: "
//                    + appCMSBinder.getPageName() + " - " + appCMSBinder.getPageId());
            if (!appCMSBinderStack.isEmpty() &&
                    !TextUtils.isEmpty(appCMSBinderStack.peek()) &&
                    appCMSBinderStack.peek().equals(appCMSBinder.getPageId())) {
                try {
                    getSupportFragmentManager().popBackStackImmediate();
                } catch (IllegalStateException e) {
                    //Log.e(TAG, "DialogType popping back stack: " + e.getMessage());
                }
                handleBack(true, false, false, true);
            }
        }
        if (!appCMSBinderStack.isEmpty()) {
            handleLaunchPageAction(appCMSBinderMap.get(appCMSBinderStack.peek()),
                    false,
                    false,
                    false);
        } else {
            if (appCMSPresenter.isNetworkConnected()) {
                finish();
            } else {
                appCMSPresenter.launchBlankPage();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && !BaseView.isTablet(this) && appCMSPresenter.isExitFullScreen) {
            appCMSPresenter.restrictPortraitOnly();
            appCMSPresenter.isExitFullScreen = false;
            return;
        }*/
        if (AppCMSPresenter.isFullScreenVisible && appCMSPresenter.videoPlayerView != null) {

            appCMSPresenter.restrictLandscapeOnly();
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
                appCMSPresenter.exitFullScreenPlayer();
            return;
        }

        if (appCMSPresenter != null) {
            appCMSPresenter.cancelInternalEvents();
            appCMSPresenter.onConfigurationChange(true);
            if (appCMSPresenter.isMainFragmentViewVisible()) {
                if (!appCMSPresenter.isMainFragmentTransparent()) {
                    appCMSPresenter.showMainFragmentView(true);
                }
                AppCMSBinder appCMSBinder = !appCMSBinderStack.isEmpty() ?
                        appCMSBinderMap.get(appCMSBinderStack.peek()) :
                        null;
                if (appCMSBinder != null) {
                    appCMSPresenter.pushActionInternalEvents(appCMSBinder.getPageId()
                            + BaseView.isLandscape(this));
                    handleLaunchPageAction(appCMSBinder,
                            true,
                            false,
                            false);
                }
            }
        }
    }

    public void pageLoading(boolean pageLoading) {
        if (pageLoading) {
            appCMSPresenter.setMainFragmentTransparency(0.5f);
            appCMSFragment.setEnabled(false);
            appCMSTabNavContainer.setEnabled(false);
            loadingProgressBar.setVisibility(View.VISIBLE);
            //while progress bar loading disable user interaction
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            for (int i = 0; i < appCMSTabNavContainer.getChildCount(); i++) {
                appCMSTabNavContainer.getChildAt(i).setEnabled(false);
            }
            appCMSPresenter.setPageLoading(true);
        } else if (!loaderWaitingFor3rdPartyLogin) {
            appCMSPresenter.setMainFragmentTransparency(1.0f);
            if (appCMSPresenter.isAddOnFragmentVisible()) {
                appCMSPresenter.showAddOnFragment(true, 0.2f);
            }
            appCMSFragment.setEnabled(true);
            appCMSTabNavContainer.setEnabled(true);
            loadingProgressBar.setVisibility(View.GONE);
            //clear user interaction blocker flag
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            for (int i = 0; i < appCMSTabNavContainer.getChildCount(); i++) {
                appCMSTabNavContainer.getChildAt(i).setEnabled(true);
            }
            appCMSPresenter.setPageLoading(false);
        }
    }

    private boolean isPageLoading() {
        return (loadingProgressBar.getVisibility() == View.VISIBLE);
    }

    private void handleBack(boolean popBinderStack,
                            boolean closeActionPage,
                            boolean recurse,
                            boolean popActionStack) {
        if (popBinderStack && !appCMSBinderStack.isEmpty()) {
            appCMSBinderMap.remove(appCMSBinderStack.peek());
            appCMSBinderStack.pop();
            if (popActionStack) {
                appCMSPresenter.popActionInternalEvents();
            }
        }

        if (!appCMSBinderStack.isEmpty()) {
            updatedAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
            //Log.d(TAG, "Back pressed - handling nav bar");
            handleNavbar(appCMSBinderMap.get(appCMSBinderStack.peek()));
            if (appCMSBinderMap.get(appCMSBinderStack.peek()) != null &&
                    appCMSBinderMap.get(appCMSBinderStack.peek()).getPageName() != null) {
                //Log.d(TAG, "Resetting previous AppCMS data: "
//                        + appCMSBinderMap.get(appCMSBinderStack.peek()).getPageName());
            } else if (appCMSBinderMap.get(appCMSBinderStack.peek()) == null) {
                appCMSBinderStack.pop();
            }
        }

        if (shouldPopStack(null) || closeActionPage) {
            try {
                getSupportFragmentManager().popBackStackImmediate();
            } catch (IllegalStateException e) {
//                //Log.e(TAG, "Failed to pop Fragment from back stack");
            }
            if (recurse) {
                //Log.d(TAG, "Handling back - recursive op");
                handleBack(popBinderStack,
                        closeActionPage && !appCMSBinderStack.isEmpty(),
                        recurse,
                        popActionStack);
            }
        }

        if (updatedAppCMSBinder != null) {
             /*
         * casting button will show only on home page, movie page and player page so check which
         * page will be open
         */
            if (!castDisabled) {
                setMediaRouterButtonVisibility(updatedAppCMSBinder.getPageId());
            }
            handleToolbar(updatedAppCMSBinder);
            handleNavbar(updatedAppCMSBinder);
        }
    }

    private void resume() {
        appCMSPresenter.restartInternalEvents();

        if (appCMSBinderStack != null && !appCMSBinderStack.isEmpty()) {
            //Log.d(TAG, "Activity resumed - resetting nav item");
            selectNavItem(appCMSBinderStack.peek());
        }

        if (!isActive) {
            if (updatedAppCMSBinder != null) {
                if (updatedAppCMSBinder.getExtraScreenType() != AppCMSPresenter.ExtraScreenType.BLANK) {
                    handleLaunchPageAction(updatedAppCMSBinder,
                            appCMSPresenter.getConfigurationChanged(),
                            false,
                            false);
                }
            }
        }

        appCMSPresenter.setVideoPlayerHasStarted();

        isActive = true;

        if (shouldSendCloseOthersAction && appCMSPresenter != null) {
            appCMSPresenter.sendCloseOthersAction(null, false, false);
            shouldSendCloseOthersAction = false;
        }

        setCastingInstance();

        registerReceiver(presenterCloseActionReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION));

        if (updatedAppCMSBinder != null &&
                updatedAppCMSBinder.getExtraScreenType() == AppCMSPresenter.ExtraScreenType.BLANK) {
            pageLoading(true);
        }
    }

    private boolean shouldPopStack(String newPageId) {
        return !isBinderStackEmpty()
                && !isBinderStackTopNull()
                && ((!TextUtils.isEmpty(newPageId) && appCMSPresenter.isPagePrimary(newPageId))
                && !appCMSPresenter.isPagePrimary(appCMSBinderStack.peek())
                && appCMSBinderMap.get(appCMSBinderStack.peek()).getExtraScreenType() != AppCMSPresenter.ExtraScreenType.SEARCH)
                && !waitingForSubscriptionFinalization()
                && !atMostOneUserPageOnTopStack(newPageId);
    }

    private boolean isBinderStackEmpty() {
        return appCMSBinderStack.isEmpty();
    }

    private boolean isBinderStackTopNull() {
        return appCMSBinderMap.get(appCMSBinderStack.peek()) == null;
    }

    private boolean waitingForSubscriptionFinalization() {
        return (appCMSPresenter.isViewPlanPage(appCMSBinderStack.peek()) &&
                !appCMSPresenter.isUserSubscribed());
    }

    private boolean atMostOneUserPageOnTopStack(String newPageId) {
        return (newPageId == null ||
                !appCMSBinderStack.isEmpty() &&
                        ((appCMSPresenter.isPageUser(appCMSBinderStack.peek()) &&
                                !appCMSPresenter.isPageUser(newPageId)) ||
                                (!appCMSPresenter.isPageUser(appCMSBinderStack.peek())) &&
                                        appCMSPresenter.isPageUser(newPageId)));
    }

    private void createScreenFromAppCMSBinder(final AppCMSBinder appCMSBinder) {
        //Log.d(TAG, "Handling new AppCMSBinder: " + appCMSBinder.getPageName());

        pageLoading(false);

        handleOrientation(getResources().getConfiguration().orientation, appCMSBinder);
         /*
         * casting button will show only on home page, movie page and player page so check which
         * page will be open
         */
        if (!castDisabled) {
            setMediaRouterButtonVisibility(appCMSBinder.getPageId());
        }
        createFragment(appCMSBinder);
    }

    private void createFragment(AppCMSBinder appCMSBinder) {
        try {
            getSupportFragmentManager().addOnBackStackChangedListener(this);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment appCMSPageFragment = null;

            switch (appCMSBinder.getExtraScreenType()) {
                case CCAVENUE:
                    try {
                        appCMSPageFragment =
                                AppCMSCCAvenueFragment.newInstance(this,
                                        appCMSBinder,
                                        Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getTextColor()),
                                        Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()),
                                        Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()),
                                        //Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor()));
                                        Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()));
                        //send menu screen event for firebase
                        sendFireBaseMenuScreenEvent(FIREBASE_MENU_SCREEN);
                    } catch (IllegalArgumentException e) {
                        //Log.e(TAG, "Error in parsing color. " + e.getLocalizedMessage());
                    }
                    break;

                case NAVIGATION:
                    try {
                        appCMSPageFragment =
                                AppCMSNavItemsFragment.newInstance(this,
                                        appCMSBinder,
                                        Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getTextColor()),
                                        Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()),
                                        Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()),
                                        // Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor()));
                                        Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()));

                        //send menu screen event for firebase
                        sendFireBaseMenuScreenEvent(FIREBASE_MENU_SCREEN);
                    } catch (IllegalArgumentException e) {
                        //Log.e(TAG, "Error in parsing color. " + e.getLocalizedMessage());
                    }
                    break;
                case TEAM:
                    try {
                        if (appCMSBinder != null &&
                                appCMSBinder.getNavigation().getTabBar() != null &&
                                appCMSPresenter.isPageTeamNavigationPage(appCMSBinder.getNavigation().getTabBar())) {
                            appCMSPageFragment =
                                    AppCMSTeamListFragment.newInstance(this,
                                            appCMSBinder,
                                            Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getTextColor()),
                                            Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()),
                                            Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()),
                                            //Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor()));
                                            Color.parseColor(appCMSBinder.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()));
                            //send menu screen event for firebase
                            sendFireBaseMenuScreenEvent(FIREBASE_TEAM_NAVIGATION_SCREEN);
                        }
                    } catch (IllegalArgumentException e) {
                        //Log.e(TAG, "Error in parsing color. " + e.getLocalizedMessage());
                    }
                    break;
                case SEARCH:
                    try {
                        appCMSPageFragment = AppCMSSearchFragment.newInstance(this,
                                Long.parseLong(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getBackgroundColor()
                                        .replace("#", ""), 16),
                                Long.parseLong(appCMSBinder.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()
                                        .replace("#", ""), 16),
                                Long.parseLong(appCMSBinder.getAppCMSMain().getBrand().getGeneral().getTextColor()
                                        .replace("#", ""), 16));
                        //Need to FireEvents When User click on Search
                        sendFireBaseSearchScreenEvent();

                    } catch (NumberFormatException e) {
                        //Log.e(TAG, "Error in parsing color. " + e.getLocalizedMessage());
                    }
                    break;

                case RESET_PASSWORD:
                    appCMSPageFragment =
                            AppCMSResetPasswordFragment.newInstance(this, appCMSBinder.getPagePath());
                    break;

                case EDIT_PROFILE:
                    appCMSPageFragment =
                            AppCMSEditProfileFragment.newInstance(this,
                                    appCMSPresenter.getLoggedInUserName(),
                                    appCMSPresenter.getLoggedInUserEmail());
                    break;

                case CHANGE_PASSWORD:
                    appCMSPageFragment = AppCMSChangePasswordFragment.newInstance();
                    break;

                case NONE:
                    appCMSPageFragment = AppCMSPageFragment.newInstance(this, appCMSBinder);
                    break;

                default:
                    break;
            }
            if (!(appCMSPageFragment instanceof AppCMSPageFragment) && appCMSPresenter.videoPlayerView != null) {
                appCMSPresenter.videoPlayerView.pausePlayer();
            }
            if (appCMSPageFragment != null) {
                fragmentTransaction.replace(R.id.app_cms_fragment, appCMSPageFragment,
                        appCMSBinder.getPageId() + BaseView.isLandscape(this));
                fragmentTransaction.addToBackStack(appCMSBinder.getPageId() + BaseView.isLandscape(this));
                fragmentTransaction.commit();
                getSupportFragmentManager().executePendingTransactions();
            }
        } catch (IllegalStateException e) {
            //Log.e(TAG, "Failed to add Fragment to back stack");
        }

//        /*
//         * casting button will show only on home page, movie page and player page so check which
//         * page will be open
//         */
//        if (!castDisabled) {
//            setMediaRouterButtonVisibility(appCMSBinder.getPageId());
//        }
    }

    private void sendFireBaseMenuScreenEvent(String eventName) {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SCREEN_VIEW_EVENT, eventName);
        if (appCMSPresenter.getmFireBaseAnalytics() != null)
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    private void sendFireBaseSearchScreenEvent() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SCREEN_VIEW_EVENT, FIREBASE_SEARCH_SCREEN);
        if (appCMSPresenter.getmFireBaseAnalytics() != null)
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    public void selectNavItemAndLaunchPage(NavBarItemView v, String pageId, String pageTitle) {
        if (!appCMSPresenter.navigateToPage(pageId,
                pageTitle,
                null,
                false,
                true,
                false,
                true,
                true,
                null)) {
            //Log.e(TAG, "Could not navigate to page with Title: " +
//                    pageTitle +
//                    " Id: " +
//                    pageId);
        } else {
            selectNavItem(v);
        }
    }

    private void selectNavItem(NavBarItemView v) {
        if (v != null && v.getTag() != null) {
            unselectAllNavItems();
            NavTabTag navigationTabTag = (NavTabTag) v.getTag();
            v.select(true, navigationTabTag);
        }
    }

    private void unselectAllNavItems() {
        for (int i = 0; i < appCMSTabNavContainerItems.getChildCount(); i++) {
            if (appCMSTabNavContainerItems.getChildAt(i) instanceof NavBarItemView) {
                unselectNavItem((NavBarItemView) appCMSTabNavContainerItems.getChildAt(i));
            }
        }
    }

    private void unselectNavItem(NavBarItemView v) {
        NavTabTag navigationTabTag = (NavTabTag) v.getTag();

        v.select(false, navigationTabTag);
    }

    public NavBarItemView getSelectedNavItem() {
        for (int i = 0; i < appCMSTabNavContainerItems.getChildCount(); i++) {
            if (((NavBarItemView) appCMSTabNavContainerItems.getChildAt(i)).isItemSelected()) {
                return (NavBarItemView) appCMSTabNavContainerItems.getChildAt(i);
            }
        }
        return null;
    }

    @Override
    public void setSelectedMenuTabIndex(int selectedMenuTabIndex) {

    }

    private void handleNavbar(AppCMSBinder appCMSBinder) {
        if (appCMSBinder != null) {
            final Navigation navigation = appCMSBinder.getNavigation();
            //final ModuleList moduleFooter = appCMSBinder.getAppCMSPageUI() != null ? appCMSBinder.getAppCMSPageUI().getModuleList().get(appCMSBinder.getAppCMSPageUI().getModuleList().size() - 1) : null;
            final ModuleList moduleFooter = appCMSPresenter.getTabBarUIFooterModule();
            if (navigation != null && navigation.getNavigationPrimary() != null &&
                    navigation.getNavigationPrimary().isEmpty() || !appCMSBinder.isNavbarPresent()) {  // for the pages like Hoichoi it is Used here where we dont getting value in settings
                appCMSTabNavContainer.setVisibility(View.GONE);
            } else {
                appCMSTabNavContainer.setVisibility(View.VISIBLE);
                selectNavItem(appCMSBinder.getPageId());
            }
        }
    }

    private void handleOrientation(int orientation, AppCMSBinder appCMSBinder) {
        if (appCMSBinder != null) {
            if (appCMSBinder.isFullScreenEnabled() &&
                    orientation == Configuration.ORIENTATION_LANDSCAPE) {
                appCMSBinder.setAppbarPresent(false);
                handleToolbar(appCMSBinder);
                hideSystemUI(getWindow().getDecorView());
            } else {
                handleToolbar(appCMSBinder);
                showSystemUI(getWindow().getDecorView());
            }
            handleNavbar(appCMSBinder);
        }
    }

    private void handleToolbar(AppCMSBinder appCMSBinder) {
        if (!appCMSBinder.isAppbarPresent()) {
            appBarLayout.setVisibility(View.GONE);
        } else {
            try {
                toolbar.setTitleTextColor(Color.parseColor(appCMSBinder.getAppCMSMain()
                        .getBrand()
                        .getGeneral()
                        .getTextColor()));
            } catch (IllegalArgumentException e) {
                //Log.e(TAG, "Error in parsing color. " + e.getLocalizedMessage());
            }
            setSupportActionBar(toolbar);
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setDisplayHomeAsUpEnabled(false);
                supportActionBar.setDisplayShowHomeEnabled(false);
                supportActionBar.setHomeButtonEnabled(false);
                supportActionBar.setTitle("");
            }
            appBarLayout.setVisibility(View.VISIBLE);

            if (appCMSPresenter.isPagePrimary(appCMSBinder.getPageId()) &&
                    !appCMSPresenter.isViewPlanPage(appCMSBinder.getPageId())) {
                closeButton.setVisibility(View.GONE);
            } else {
                closeButton.setVisibility(View.VISIBLE);
            }

            if (appCMSBinder.getNavigation().getRight() != null) {
                if (appCMSPresenter.isPageSearch(appCMSBinder.getPageId())) {
                    mSearchTopButton.setVisibility(View.GONE);
                } else {
                    mSearchTopButton.setVisibility(View.VISIBLE);
                }
            } else {
                mSearchTopButton.setVisibility(View.GONE);
            }
            if (appCMSPresenter.isArticlePage(appCMSBinder.getPageId()) ||
                    appCMSPresenter.isPageAVideoPage(appCMSBinder.getPageName())) {
                mShareTopButton.setVisibility(View.VISIBLE);
                mSearchTopButton.setVisibility(View.VISIBLE);
            } else {
                mShareTopButton.setVisibility(View.GONE);
                mSearchTopButton.setVisibility(View.GONE);
            }
//            setMediaRouterButtonVisibility(pageId);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void handleLaunchPageAction(final AppCMSBinder appCMSBinder,
                                        boolean configurationChanged,
                                        boolean leavingExtraPage,
                                        boolean keepPage) {
        //Log.d(TAG, "Launching new page: " + appCMSBinder.getPageName());
        if (!BaseView.isTablet(this)) {
            appCMSPresenter.restrictPortraitOnly();
        } else {
            appCMSPresenter.unrestrictPortraitOnly();
        }

        appCMSPresenter.sendGaScreen(appCMSBinder.getScreenName());
        setVisibilityForStartFreeTrial(appCMSBinder.getPageId());
        int lastBackStackEntry = getSupportFragmentManager().getBackStackEntryCount();
        boolean poppedStack = false;
        if (!appCMSBinder.shouldSendCloseAction() &&
                lastBackStackEntry > 0 &&
                (appCMSBinder.getPageId() + BaseView.isLandscape(this))
                        .equals(getSupportFragmentManager()
                                .getBackStackEntryAt(lastBackStackEntry - 1)
                                .getName()) &&
                getSupportFragmentManager().findFragmentByTag(appCMSBinder.getPageId()
                        + BaseView.isLandscape(this)) instanceof AppCMSPageFragment) {
            ((AppCMSPageFragment) getSupportFragmentManager().findFragmentByTag(appCMSBinder.getPageId()
                    + BaseView.isLandscape(this))).refreshView(appCMSBinder);
            if (appCMSBinder.getAppCMSPageAPI() != null || appCMSBinder.getExtraScreenType()
                    != AppCMSPresenter.ExtraScreenType.NONE) {
                pageLoading(false);
            } else {
                pageLoading(true);
            }

            appCMSBinderMap.put(appCMSBinder.getPageId(), appCMSBinder);
            try {
                handleToolbar(appCMSBinder);
                handleNavbar(appCMSBinder);
                updatedAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
                appCMSPresenter.showMainFragmentView(true);
                appCMSPresenter.restartInternalEvents();
                appCMSPresenter.dismissOpenDialogs(null);
            } catch (EmptyStackException e) {
                //Log.e(TAG, "Error attempting to restart screen: " + appCMSBinder.getScreenName());
            }
        } else {
            boolean createFragment = true;

            int distanceFromStackTop = appCMSBinderStack.search(appCMSBinder.getPageId());
            //Log.d(TAG, "Page distance from top: " + distanceFromStackTop);
            int i = 1;
            while (((i < distanceFromStackTop && !configurationChanged) ||
                    ((i < distanceFromStackTop &&
                            (!isBinderStackEmpty() &&
                                    !isBinderStackTopNull() &&
                                    !atMostOneUserPageOnTopStack(appCMSBinder.getPageId()) &&
                                    !leavingExtraPage)) &&
                            ((!leavingExtraPage && shouldPopStack(appCMSBinder.getPageId())) || configurationChanged)) ||
                    (appCMSBinder.shouldSendCloseAction() &&
                            appCMSBinderStack.size() > 1 &&
                            i < appCMSBinderStack.size()))) {
                //Log.d(TAG, "Popping stack to getList to page item");
                try {
                    getSupportFragmentManager().popBackStackImmediate();
                    createFragment = false;
                } catch (IllegalStateException e) {
                    //Log.e(TAG, "DialogType popping back stack: " + e.getMessage());
                }
                if ((i < distanceFromStackTop - 1) ||
                        (!configurationChanged && !atMostOneUserPageOnTopStack(appCMSBinder.getPageId()))) {
                    handleBack(true,
                            false,
                            false,
                            !appCMSBinder.shouldSendCloseAction());
                    poppedStack = true;
                }
                i++;
            }
            if (!appCMSBinderStack.isEmpty()) {
                AppCMSBinder currentAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
                try {
                    createFragment = currentAppCMSBinder.getExtraScreenType() != AppCMSPresenter.ExtraScreenType.SEARCH;
                } catch (Exception e) {
                    //
                }
            }

            if (!appCMSBinderStack.isEmpty() && appCMSBinderMap.get(appCMSBinderStack.peek()) != null) {
                try {
                    createFragment = !(appCMSBinderMap.get(appCMSBinderStack.peek())
                            .getExtraScreenType() == AppCMSPresenter.ExtraScreenType.SEARCH
                            && updatedAppCMSBinder.getExtraScreenType() == AppCMSPresenter.ExtraScreenType.SEARCH);
                } catch (Exception e) {
                    //
                }
            }

            if (distanceFromStackTop < 0 ||
                    appCMSBinder.shouldSendCloseAction() ||
                    (!configurationChanged && appCMSBinder.getExtraScreenType() !=
                            AppCMSPresenter.ExtraScreenType.NONE)) {
                if (!isBinderStackEmpty() &&
                        !isBinderStackTopNull() &&
                        (appCMSPresenter.isPageNavigationPage(appCMSBinderStack.peek()) ||
                                isDownloadPageOpen) &&
                        appCMSPresenter.isPagePrimary(appCMSBinder.getPageId())) {
                    getSupportFragmentManager().popBackStackImmediate();
                    appCMSBinderMap.remove(appCMSBinderStack.peek());
                    appCMSBinderStack.pop();
                    isDownloadPageOpen = false;
                }

                if (appCMSBinderStack.search(appCMSBinder.getPageId()) < 0) {
                    appCMSBinderStack.push(appCMSBinder.getPageId());
                }
                appCMSBinderMap.put(appCMSBinder.getPageId(), appCMSBinder);
            }

            if (distanceFromStackTop >= 0) {
                try {
                    switch (appCMSBinder.getExtraScreenType()) {
                        case NAVIGATION:
                            createFragment = true;
                            break;
                        case TEAM:
                        case SEARCH:
                            //Log.d(TAG, "Popping stack to getList to page item");
                            try {
                                createFragment = false;
                                if (!isBinderStackEmpty() &&
                                        !isBinderStackTopNull() &&
                                        appCMSBinderStack.peek().equals(appCMSBinder.getPageId()) &&
                                        !keepPage) {
                                    getSupportFragmentManager().popBackStackImmediate();
                                    createFragment = true;
                                }

                                if (poppedStack) {
                                    appCMSBinderStack.push(appCMSBinder.getPageId());
                                    appCMSBinderMap.put(appCMSBinder.getPageId(), appCMSBinder);
                                }

                                if (!createFragment) {
                                    handleToolbar(appCMSBinder);
                                    handleNavbar(appCMSBinder);
                                }
                            } catch (IllegalStateException e) {
                                //Log.e(TAG, "DialogType popping back stack: " + e.getMessage());
                            }
                            break;

                        case NONE:
                            if (poppedStack) {
                                if (appCMSBinderStack.search(appCMSBinder.getPageId()) < 0) {
                                    appCMSBinderStack.push(appCMSBinder.getPageId());
                                }
                                appCMSBinderMap.put(appCMSBinder.getPageId(), appCMSBinder);
                            }
                            break;

                        default:
                            break;
                    }
                } catch (Exception e) {
                }
            }

            appCMSBinder.unsetSendCloseAction();

            updatedAppCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());

            if (appCMSPresenter.isAppBelowMinVersion()) {
                appCMSPresenter.launchUpgradeAppActivity();
            } else if (appCMSPresenter.isAppUpgradeAvailable()) {
                newVersionUpgradeAvailable.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                newVersionAvailableTextView.setText("");
                newVersionAvailableTextView.setText(getString(R.string.a_new_version_of_the_app_is_available_text,
                        getString(R.string.app_cms_app_version),
                        appCMSPresenter.getGooglePlayAppStoreVersion()));
                newVersionUpgradeAvailable.setVisibility(View.VISIBLE);
                newVersionUpgradeAvailable.requestLayout();
            }

            if (createFragment) {
                createScreenFromAppCMSBinder(appCMSBinder);
            } else {
                int lastFragment = getSupportFragmentManager().getFragments().size();
                Fragment fragment = getSupportFragmentManager().getFragments().get(lastFragment - 1);
                if (fragment instanceof AppCMSPageFragment) {
                    ((AppCMSPageFragment) fragment).refreshView(appCMSBinder);
                }
                pageLoading(false);
                handleToolbar(appCMSBinder);
            }
        }

        if (appCMSBinder.getExtraScreenType() == AppCMSPresenter.ExtraScreenType.SEARCH) {
            mSearchTopButton.setVisibility(View.GONE);
        } else if (shouldShowSearchInToolbar()) {
            mSearchTopButton.setVisibility(View.VISIBLE);
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

    private void manageTopBar() {
        if (appCMSPresenter.getNavigation() != null && appCMSPresenter.getNavigation().getLeft() != null && appCMSPresenter.getNavigation().getLeft().size() > 0) {
            for (int i = 0; i < appCMSPresenter.getNavigation().getLeft().size(); i++) {
                if (appCMSPresenter.getNavigation().getLeft().get(i).getDisplayedPath().equalsIgnoreCase("Authentication Screen")) {

                    mProfileTopButton.setVisibility(View.VISIBLE);
                }
            }
        }
        if (appCMSPresenter.getNavigation() != null && appCMSPresenter.getNavigation().getRight() != null && appCMSPresenter.getNavigation().getRight().size() > 0) {
            for (int i = 0; i < appCMSPresenter.getNavigation().getRight().size(); i++) {
                if (appCMSPresenter.getNavigation().getRight().get(i).getDisplayedPath().equalsIgnoreCase("Search Screen")) {

                    mSearchTopButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void createTabBar() {
        ModuleList tabBarModule = appCMSPresenter.getTabBarUIFooterModule();
        if (appCMSPresenter.getNavigation() != null &&
                appCMSPresenter.getNavigation().getTabBar() != null &&
                !isTabCreated && tabBarModule != null) {
            isTabCreated = true;
            int WEIGHT_SUM = getResources().getInteger(R.integer.nav_bar_items_weightsum);
            int weight = WEIGHT_SUM / appCMSPresenter.getNavigation().getTabBar().size();

            appCMSTabNavContainer.removeAllViews();

            //add separator view
            if (tabBarModule.isTabSeparator()) {
                View sepratorView = new View(this);
                sepratorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) BaseView.convertDpToPixel(getResources().getDimension(R.dimen.nav_item_separator_height), this)));
                sepratorView.setBackgroundColor(Color.parseColor(tabBarModule.getTabSeparator_color()));
                appCMSTabNavContainer.addView(sepratorView);
            }

            //add navigation item parent view
            appCMSTabNavContainerItems = new LinearLayout(this);
            LinearLayout.LayoutParams appCMSTabNavContainerItemsParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
           /* if (BaseView.isTablet(this)){

                weight=(weight/2);
            }else{
                appCMSTabNavContainerItems.setWeightSum(WEIGHT_SUM);
            }*/

            appCMSTabNavContainerItemsParam.gravity = Gravity.CENTER;

            appCMSTabNavContainerItems.setLayoutParams(appCMSTabNavContainerItemsParam);
            appCMSTabNavContainerItems.setOrientation(LinearLayout.HORIZONTAL);
            appCMSTabNavContainer.addView(appCMSTabNavContainerItems);


            for (int i = 0; i < appCMSPresenter.getNavigation().getTabBar().size(); i++) {
                NavigationPrimary navigationItem = appCMSPresenter.getNavigation().getTabBar().get(i);

                NavBarItemView navBarItemView = new NavBarItemView(this, tabBarModule, appCMSPresenter, weight);
                int highlightColor = 0;
                if (appCMSPresenter!= null ) {
                    highlightColor = appCMSPresenter.getBrandPrimaryCtaColor();
                } else {
                    highlightColor = ContextCompat.getColor(this, R.color.colorNavBarText);
                }

                String tabLabel = navigationItem.getTitle();
                navBarItemView.setTabImage(navigationItem.getIcon());
                navBarItemView.setLabel(tabLabel);
                navBarItemView.setHighlightColor(highlightColor);

                navBarItemView.setId(i);

                String tagId = null;
                if (navigationItem.getPageId() != null && !TextUtils.isEmpty(navigationItem.getPageId())) {
                    tagId = navigationItem.getPageId();
                } else if (navigationItem.getDisplayedPath() != null) {
                    tagId = navigationItem.getDisplayedPath();
                } else if (navigationItem.getTitle() != null) {
                    tagId = navigationItem.getTitle();
                }

                NavTabTag navigationTag = new NavTabTag();
                navigationTag.setPageId(tagId);
                navigationTag.setNavigationTabBar(navigationItem);
                navigationTag.setNavigationModuleItem(tabBarModule);
                navBarItemView.setTag(navigationTag);

                navBarItemView.setOnClickListener(v -> {
                    if (v.getTag() != null) {
                        NavTabTag navigationTabTag = (NavTabTag) v.getTag();
                        if (navigationTabTag.isTabSelected()) {
                            return;
                        }
                        selectNavItem(navigationTabTag.getPageId());

                        selectNavItem((NavBarItemView) v);
                        appCMSPresenter.showMainFragmentView(true);
                        if (navigationTabTag.getPageId().equals("Menu Screen")) {
                            appCMSPresenter.launchNavigationPage();
                        } else if (navigationTabTag.getPageId().equalsIgnoreCase("TEAMS") ||
                                navigationTabTag.getTabBar().getTitle().equalsIgnoreCase("TEAMS")) {
                            appCMSPresenter.launchTeamNavPage();
                        } else if (navigationTabTag.getPageId().equals("Search Screen") ||
                                navigationTabTag.getTabBar().getDisplayedPath().equals("Search Screen")) {
                            appCMSPresenter.launchSearchPage();
                        } else if (!TextUtils.isEmpty(navigationTabTag.getPageId().toString())) {
                            selectNavItemAndLaunchPage(navBarItemView,
                                    appCMSPresenter.getNavigation().getTabBar().get(v.getId()).getPageId(),
                                    appCMSPresenter.getNavigation().getTabBar().get(v.getId()).getTitle());
                        }
                    }
                });
                appCMSTabNavContainerItems.addView(navBarItemView);
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void closeMenuPageIfHighlighted(NavBarItemView menuNavBarItemView) {
        if (!menuNavBarItemView.isItemSelected()) {
            resumeInternalEvents = true;
            selectNavItem(menuNavBarItemView);
        } else {
            unselectNavItem(menuNavBarItemView);
            appCMSPresenter.sendCloseOthersAction(null, true, false);
        }
    }

    private void selectNavItem(String pageId) {
        boolean foundPage = false;
        if (!TextUtils.isEmpty(pageId) && appCMSTabNavContainerItems != null) {
            for (int i = 0; i < appCMSTabNavContainerItems.getChildCount(); i++) {
                NavTabTag navigationTabTag = null;
                if (appCMSTabNavContainerItems.getChildAt(i).getTag() != null) {
                    navigationTabTag = (NavTabTag) appCMSTabNavContainerItems.getChildAt(i).getTag();
                }

                if (navigationTabTag != null && !TextUtils.isEmpty(navigationTabTag.getPageId()) &&
                        (pageId.contains(navigationTabTag.getPageId()) || pageId.equalsIgnoreCase(navigationTabTag.getPageId()) || pageId.equalsIgnoreCase(getString(R.string.app_cms_menu_screen_tag)) ||
                                (navigationTabTag != null && navigationTabTag.getPageId() != null &&
                                        (pageId.equalsIgnoreCase("navigation") && navigationTabTag.getPageId().equals(getString(R.string.app_cms_menu_screen_tag))) ||
                                        (pageId.equalsIgnoreCase(getString(R.string.app_cms_team_page_tag)) &&
                                                navigationTabTag.getTabBar().getTitle().equalsIgnoreCase(getString(R.string.app_cms_team_page_tag)))))) {
                    selectNavItem(((NavBarItemView) appCMSTabNavContainerItems.getChildAt(i)));
                    navigationTabTag.setTabSelected(true);
                    //Log.d(TAG, "Nav item - Selecting tab item with page Id: " +
//                            pageId +
//                            " index: " +
//                            i);
                    currentMenuTabIndex = i;
                    foundPage = true;
                } else {
                    navigationTabTag.setTabSelected(false);
                }
            }
        }

       /* if (!foundPage) {
            final NavBarItemView menuNavBarItemView =
                    (NavBarItemView) appCMSTabNavContainer.getChildAt(navMenuPageIndex);
            selectNavItem(menuNavBarItemView);
        }*/
    }

    public void processDeepLink(Uri deeplinkUri) {
        String title = deeplinkUri.getLastPathSegment();
        String action = getString(R.string.app_cms_action_detailvideopage_key);
        StringBuffer pagePath = new StringBuffer();

        for (String pathSegment : deeplinkUri.getPathSegments()) {
            pagePath.append(File.separatorChar);
            pagePath.append(pathSegment);
        }
        //Log.d(TAG, "Launching deep link " +
//                deeplinkUri.toString() +
//                " with path: " +
//                pagePath.toString());
        appCMSPresenter.launchButtonSelectedAction(pagePath.toString(),
                action,
                title,
                null,
                null,
                false,
                0,
                null);
        appCMSPresenter.resetDeeplinkQuery();
    }

    private void updateData(AppCMSBinder appCMSBinder, Action0 readyAction) {
        final AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
        final AppCMSSite appCMSSite = appCMSPresenter.getAppCMSSite();

        if (appCMSPresenter.isHistoryPage(appCMSBinder.getPageId())) {
            appCMSPresenter.getHistoryData(appCMSHistoryResult -> {
                if (appCMSHistoryResult != null) {
                    AppCMSPageAPI historyAPI =
                            appCMSHistoryResult.convertToAppCMSPageAPI(appCMSBinder.getPageId());
                    historyAPI.getModules().get(0).setId(appCMSBinder.getPageId());
//                    appCMSPresenter.mergeData(historyAPI, appCMSBinder.getAppCMSPageAPI());
                    appCMSBinder.updateAppCMSPageAPI(historyAPI);

                    //Log.d(TAG, "Updated watched history for loaded displays");

                    if (readyAction != null) {
                        readyAction.call();
                    }
                } else if (readyAction != null) {
                    readyAction.call();
                }
            });
        } else if (appCMSPresenter.isWatchlistPage(appCMSBinder.getPageId())) {
            appCMSPresenter.getWatchlistData(appCMSWatchlistResult -> {
                if (appCMSWatchlistResult != null) {
                    AppCMSPageAPI watchlistAPI =
                            appCMSWatchlistResult.convertToAppCMSPageAPI(appCMSBinder.getPageId());
                    watchlistAPI.getModules().get(0).setId(appCMSBinder.getPageId());
//                    appCMSPresenter.mergeData(watchlistAPI, appCMSBinder.getAppCMSPageAPI());
                    appCMSBinder.updateAppCMSPageAPI(watchlistAPI);

                    //Log.d(TAG, "Updated watched history for loaded displays");

                    if (readyAction != null) {
                        readyAction.call();
                    }
                } else if (readyAction != null) {
                    readyAction.call();
                }
            });
        } else {
            String endPoint = appCMSPresenter.getPageIdToPageAPIUrl(appCMSBinder.getPageId());
            boolean usePageIdQueryParam = true;
            if (appCMSPresenter.isPageAVideoPage(appCMSBinder.getScreenName())) {
                endPoint = appCMSPresenter.getPageNameToPageAPIUrl(appCMSBinder.getScreenName());
                usePageIdQueryParam = false;
            }

            if (!TextUtils.isEmpty(endPoint)) {
                String baseUrl = appCMSMain.getApiBaseUrl();
                String siteId = appCMSSite.getGist().getSiteInternalName();
                boolean viewPlans = appCMSPresenter.isViewPlanPage(endPoint);
                boolean showPage = appCMSPresenter.isShowPage(appCMSBinder.getPageId());
                String apiUrl = appCMSPresenter.getApiUrl(usePageIdQueryParam,
                        viewPlans,
                        showPage,
                        baseUrl,
                        endPoint,
                        siteId,
                        appCMSBinder.getPagePath());
                appCMSPresenter.getPageIdContent(apiUrl,
                        appCMSBinder.getPagePath(),
                        appCMSPageAPI -> {
                            if (appCMSPageAPI != null) {
                                appCMSBinder.updateAppCMSPageAPI(appCMSPageAPI);
                            }
                            if (readyAction != null) {
                                readyAction.call();
                            }
                        });
            } else if (readyAction != null) {
                readyAction.call();
            }
        }

    }

    private void updateData() {
        final AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
        final AppCMSSite appCMSSite = appCMSPresenter.getAppCMSSite();

        if (appCMSPresenter != null) {
            for (Map.Entry<String, AppCMSBinder> appCMSBinderEntry : appCMSBinderMap.entrySet()) {
                final AppCMSBinder appCMSBinder = appCMSBinderEntry.getValue();
                if (appCMSBinder != null) {
                    if (appCMSPresenter.isHistoryPage(appCMSBinder.getPageId())) {
                        appCMSPresenter.getHistoryData(appCMSHistoryResult -> {
                            if (appCMSHistoryResult != null) {
                                AppCMSPageAPI historyAPI =
                                        appCMSHistoryResult.convertToAppCMSPageAPI(appCMSBinder.getPageId());
                                historyAPI.getModules().get(0).setId(appCMSBinder.getPageId());
                                appCMSPresenter.mergeData(historyAPI, appCMSBinder.getAppCMSPageAPI());
                                appCMSBinder.updateAppCMSPageAPI(appCMSBinder.getAppCMSPageAPI());

                                //Log.d(TAG, "Updated watched history for loaded displays");
                            }
                        });
                    } else if (appCMSPresenter.isWatchlistPage(appCMSBinder.getPageId())) {
                        appCMSPresenter.getWatchlistData(appCMSWatchlistResult -> {
                            if (appCMSWatchlistResult != null) {
                                AppCMSPageAPI watchlistAPI =
                                        appCMSWatchlistResult.convertToAppCMSPageAPI(appCMSBinder.getPageId());
                                watchlistAPI.getModules().get(0).setId(appCMSBinder.getPageId());
                                appCMSPresenter.mergeData(watchlistAPI, appCMSBinder.getAppCMSPageAPI());
                                appCMSBinder.updateAppCMSPageAPI(appCMSBinder.getAppCMSPageAPI());
                                //Log.d(TAG, "Updated watched history for loaded displays");
                            }
                        });
                    } else {
                        String endPoint = appCMSPresenter.getPageIdToPageAPIUrl(appCMSBinder.getPageId());
                        boolean usePageIdQueryParam = true;
                        if (appCMSPresenter.isPageAVideoPage(appCMSBinder.getScreenName())) {
                            endPoint = appCMSPresenter.getPageNameToPageAPIUrl(appCMSBinder.getScreenName());
                            usePageIdQueryParam = false;
                        }

                        if (!TextUtils.isEmpty(endPoint)) {
                            String baseUrl = appCMSMain.getApiBaseUrl();
                            String siteId = appCMSSite.getGist().getSiteInternalName();
                            boolean viewPlans = appCMSPresenter.isViewPlanPage(endPoint);
                            boolean showPage = appCMSPresenter.isShowPage(appCMSBinder.getPageId());
                            String apiUrl = appCMSPresenter.getApiUrl(usePageIdQueryParam,
                                    viewPlans,
                                    showPage,
                                    baseUrl,
                                    endPoint,
                                    siteId,
                                    appCMSBinder.getPagePath());

                            appCMSPresenter.getPageIdContent(apiUrl,
                                    appCMSBinder.getPagePath(),
                                    appCMSPageAPI -> {
                                        if (appCMSPageAPI != null) {
                                            if (appCMSPresenter.isUserLoggedIn()) {
                                                if (appCMSPageAPI.getModules() != null) {
                                                    for (Module module : appCMSPageAPI.getModules()) {
                                                        AppCMSUIKeyType moduleType = appCMSPresenter.getJsonValueKeyMap().get(module.getModuleType());
                                                        if (moduleType == AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY ||
                                                                moduleType == AppCMSUIKeyType.PAGE_VIDEO_DETAILS_KEY) {
                                                            appCMSPresenter.getHistoryData(appCMSHistoryResult -> {
                                                                if (appCMSHistoryResult != null) {
                                                                    AppCMSPageAPI historyAPI =
                                                                            appCMSHistoryResult.convertToAppCMSPageAPI(appCMSPageAPI.getId());
                                                                    historyAPI.getModules().get(0).setId(module.getId());
                                                                    appCMSPresenter.mergeData(historyAPI, appCMSPageAPI);
                                                                    appCMSBinder.updateAppCMSPageAPI(appCMSPageAPI);

                                                                    //Log.d(TAG, "Updated watched history for loaded displays");
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        }
    }

    private void mergeInputData(AppCMSBinder updatedAppCMSBinder, String pageId) {
        if (appCMSBinderMap.containsKey(pageId) && appCMSPresenter != null &&
                appCMSPresenter.isPageAVideoPage(updatedAppCMSBinder.getScreenName())) {
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
        appCMSPresenter.cancelInternalEvents();
        appCMSPresenter.restartInternalEvents();
        if (appCMSPresenter.isViewPlanPage(updatedAppCMSBinder.getPageId())) {
            //Log.d(TAG, "checkForExistingSubscription() - 1532");
            appCMSPresenter.checkForExistingSubscription(appCMSPresenter.getLaunchType() == AppCMSPresenter.LaunchType.SUBSCRIBE && !appCMSPresenter.isUserSubscribed());
            appCMSPresenter.refreshSubscriptionData(null, true);
        }

        getSupportFragmentManager().removeOnBackStackChangedListener(this);
        if (updatedAppCMSBinder != null && updatedAppCMSBinder.getSearchQuery() != null) {
            //Log.d(TAG, "Successfully loaded page " + appCMSBinder.getPageName());
            //Log.d(TAG, "Processing search query for deeplink " +
//                    appCMSBinder.getSearchQuery().toString());
            appCMSPresenter.sendDeepLinkAction(updatedAppCMSBinder.getSearchQuery());
            updatedAppCMSBinder.clearSearchQuery();
        }
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Log.e(TAG, "Failed to connect for Google SignIn: " + connectionResult.getErrorMessage());
    }

    private void setMediaRouterButtonVisibility(String pageId) {
        if (!castDisabled) {
            try {
                if ((appCMSPresenter.findHomePageNavItem() != null &&
                        !TextUtils.isEmpty(appCMSPresenter.findHomePageNavItem().getPageId()) &&
                        appCMSPresenter.findHomePageNavItem().getPageId().equalsIgnoreCase(pageId)) ||

                        (appCMSPresenter.findMoviesPageNavItem() != null &&
                                !TextUtils.isEmpty(appCMSPresenter.findMoviesPageNavItem().getPageId()) &&
                                appCMSPresenter.findMoviesPageNavItem().getPageId().equalsIgnoreCase(pageId))) {
                    setCastingVisibility(true);
                    CastServiceProvider.getInstance(this).isHomeScreen(true);
                } else {
                    setCastingVisibility(false);
                    CastServiceProvider.getInstance(this).isHomeScreen(false);
                }

                if (CastServiceProvider.getInstance(this).isOverlayVisible()) {
                    CastServiceProvider.getInstance(this).showIntroOverLay();
                }
            } catch (Exception e) {
                //
            }
        }
    }

    public void setCastingVisibility(boolean isVisible) {
        if (isVisible) {
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
            //Log.e(TAG, "Failed to initialize cast provider: " + e.getMessage());
        }
    }

    private void handleCloseAction(boolean closeOnePage) {
        //Log.d(TAG, "Received Presenter Close Action: fragment count = "
//                + getSupportFragmentManager().getBackStackEntryCount());
        if (!appCMSBinderStack.isEmpty()) {
            try {
                int lastBackStackCount = getSupportFragmentManager().getBackStackEntryCount() - 1;
                if (0 < lastBackStackCount) {
                    String lastBackStackEntryName = getSupportFragmentManager().getBackStackEntryAt(lastBackStackCount)
                            .getName();
                    String lastBackStackEntryWithoutOrientationName = lastBackStackEntryName.substring(0,
                            lastBackStackEntryName.indexOf("true") > 0 ? lastBackStackEntryName.indexOf("true") :
                                    lastBackStackEntryName.indexOf("false") > 0 ? lastBackStackEntryName.indexOf("false") :
                                            lastBackStackEntryName.length());
                    while (lastBackStackCount > 0 &&
                            getSupportFragmentManager().getBackStackEntryAt(lastBackStackCount).getName().contains(lastBackStackEntryWithoutOrientationName)) {
                        getSupportFragmentManager().popBackStackImmediate();
                        lastBackStackCount = getSupportFragmentManager().getBackStackEntryCount() - 1;
                    }
                }
            } catch (Exception e) {
                //Log.e(TAG, "DialogType popping back stack: " + e.getMessage());
            }

            try {
                if (appCMSPresenter.isViewPlanPage(appCMSBinderStack.peek())) {
                    if (appCMSPresenter.getLaunchType() == AppCMSPresenter.LaunchType.SUBSCRIBE) {
                        appCMSPresenter.setLaunchType(AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP);
                    }
                }

                AppCMSBinder appCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());
                boolean leavingExtraPage = false;
                if (appCMSBinder != null) {
                    leavingExtraPage = appCMSBinder.getExtraScreenType() !=
                            AppCMSPresenter.ExtraScreenType.NONE;

                    boolean recurse = !closeOnePage &&
                            appCMSPresenter.isPageAVideoPage(appCMSBinder.getScreenName());

                    handleBack(true,
                            false,
                            recurse,
                            true);

                }

                if (appCMSBinderStack.isEmpty()) {
                    finishAffinity();
                    return;
                }

                appCMSBinder = appCMSBinderMap.get(appCMSBinderStack.peek());

                if (appCMSPresenter != null && appCMSBinder != null) {
                    leavingExtraPage = appCMSBinder.getExtraScreenType() !=
                            AppCMSPresenter.ExtraScreenType.NONE;

                    appCMSPresenter.pushActionInternalEvents(appCMSBinder.getPageId()
                            + BaseView.isLandscape(this));

                    handleLaunchPageAction(appCMSBinder,
                            false,
                            leavingExtraPage,
                            appCMSBinder.getExtraScreenType()
                                    == AppCMSPresenter.ExtraScreenType.SEARCH);
                }
            } catch (Exception e) {
                //
            }
            isActive = true;
        } else {
            isActive = false;
            finishAffinity();
        }

        if (appCMSPresenter != null) {
            appCMSPresenter.restartInternalEvents();
        }
    }

    @Override
    public void saveQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    public String restoreQuery() {
        return searchQuery;
    }

    public void startFreeTrialTool() {
        int buttonColor, textColor;
        if (appCMSPresenter.getAppCMSMain() != null &&
                appCMSPresenter.getAppCMSMain().getBrand() != null &&
                appCMSPresenter.getAppCMSMain().getBrand().getCta() != null &&
                appCMSPresenter.getAppCMSMain().getBrand().getGeneral() != null &&
                appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBackgroundColor() != null &&
                appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary() != null &&
                appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor() != null) {
            buttonColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor());
            textColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor());
        } else {
            buttonColor = Color.parseColor("#F81004");
            textColor = Color.parseColor("#ffffff");
        }

        appCMSNavFreeTrialTool.setTextColor(textColor);
        appCMSNavFreeTrialTool.setBackgroundColor(buttonColor);
        if (appCMSPresenter.getNavigation() != null &&
                appCMSPresenter.getNavigation().getSettings() != null &&
                appCMSPresenter.getNavigation().getSettings().getPrimaryCta() != null &&
                appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getPlacement() != null &&
                appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getPlacement().contains(getString(R.string.navigation_settings_primaryCta_placement))) {
            if (appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getBannerText() != null &&
                    appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getCtaText() != null) {

                SpannableString content = new SpannableString(appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getBannerText() +
                        appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getCtaText());
                content.setSpan(new UnderlineSpan(), appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getBannerText().length(),
                        content.length(), 0);
                appCMSNavFreeTrialTool.setText(content);
            }
        }

    }

    void setVisibilityForStartFreeTrial(String pageId) {
        if (appCMSPresenter.getNavigation() != null &&
                appCMSPresenter.getNavigation().getSettings() != null &&
                appCMSPresenter.getNavigation().getSettings().getPrimaryCta() != null &&
                appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getPlacement() != null &&
                appCMSPresenter.getNavigation().getSettings().getPrimaryCta().getPlacement().contains(getString(R.string.navigation_settings_primaryCta_placement))) {

            if (appCMSPresenter.isViewPlanPage(pageId) || appCMSPresenter.isPageLoginPage(pageId)) {
                appCMSNavFreeTrialTool.setVisibility(View.GONE);
                return;
            }
            if (!appCMSPresenter.isUserSubscribed()) {
                appCMSNavFreeTrialTool.setVisibility(View.VISIBLE);
            } else {
                appCMSNavFreeTrialTool.setVisibility(View.GONE);
            }
        }
    }

    public void setFullScreenFocus() {
        synchronized (this) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            appCMSTabNavContainer.setVisibility(View.GONE);
            appBarLayout.setVisibility(View.GONE);

        }
    }

    public void exitFullScreenFocus() {
        synchronized (this) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            appCMSTabNavContainer.setVisibility(View.VISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);


        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void dragMiniPlayer(final MiniPlayerView relativeLayoutPIP) {
        relativeLayoutPIP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                System.out.println("Touched  ...");
                if (action == MotionEvent.ACTION_DOWN) {
                    downRawX = event.getRawX();
                    downRawY = event.getRawY();
                    dX = view.getX() - downRawX;
                    dY = view.getY() - downRawY;
                    return true;
                } else if (action == MotionEvent.ACTION_MOVE) {
                    int viewWidth = view.getWidth();
                    int viewHeight = view.getHeight();

                    View viewParent = (View) view.getParent();
                    int parentWidth = viewParent.getWidth();
                    int parentHeight = viewParent.getHeight();

                    float newX = event.getRawX() + dX;
                    newX = Math.max(0, newX); // Don't allow the view past the left hand side of screen
                    newX = Math.min(parentWidth - viewWidth, newX); // Don't allow the view past the right hand side of screen

                    float newY = event.getRawY() + dY;
                    newY = Math.max(toolbar.getHeight(), newY); // Don't allow the view past the top of screen including toolbar
                    int bottomHeight = viewHeight + appCMSTabNavContainer.getHeight();
                    newY = Math.min(parentHeight - bottomHeight, newY); // Don't allow the view past the bottom of screen including bottombar

                    view.animate()
                            .x(newX)
                            .y(newY)
                            .setDuration(0)
                            .start();

                    return true;

                } else if (action == MotionEvent.ACTION_UP) {

                    float upRawX = event.getRawX();
                    float upRawY = event.getRawY();

                    float upDX = upRawX - downRawX;
                    float upDY = upRawY - downRawY;

                    if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
                        relativeLayoutPIP.pipClick();
                        return true;
                    } else { // A drag
                        return true;
                    }

                }
                return true;
            }
        });
    }

    private static class RefreshAppCMSBinderAction implements Action1<AppCMSPageAPI> {
        private AppCMSPresenter appCMSPresenter;
        private AppCMSBinder appCMSBinder;
        private boolean userLoggedIn;

        RefreshAppCMSBinderAction(AppCMSPresenter appCMSPresenter,
                                  AppCMSBinder appCMSBinder,
                                  boolean userLoggedIn) {
            this.appCMSPresenter = appCMSPresenter;
            this.appCMSBinder = appCMSBinder;
            this.userLoggedIn = userLoggedIn;
        }

        @Override
        public void call(AppCMSPageAPI appCMSPageAPI) {
            userLoggedIn = appCMSPresenter.isUserLoggedIn();
            if (userLoggedIn && appCMSPageAPI != null && appCMSPageAPI.getModules() != null) {
                for (Module module : appCMSPageAPI.getModules()) {
                    AppCMSUIKeyType moduleType = appCMSPresenter.getJsonValueKeyMap().get(module.getModuleType());
                    if (moduleType == AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY ||
                            moduleType == AppCMSUIKeyType.PAGE_VIDEO_DETAILS_KEY) {
                        if (module.getContentData() != null &&
                                !module.getContentData().isEmpty()) {
                            appCMSPresenter.getHistoryData(appCMSHistoryResult -> {
                                if (appCMSHistoryResult != null) {
                                    AppCMSPageAPI historyAPI =
                                            appCMSHistoryResult.convertToAppCMSPageAPI(appCMSPageAPI.getId());
                                    historyAPI.getModules().get(0).setId(module.getId());
                                    appCMSPresenter.mergeData(historyAPI, appCMSPageAPI);
                                    appCMSBinder.updateAppCMSPageAPI(appCMSPageAPI);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public class NavTabTag {
        private String pageId;
        private NavigationPrimary navigationTabBar;
        private boolean isTabSelected;
        private ModuleList navigationModuleItem;

        public String getPageId() {
            return pageId;
        }

        public void setPageId(String pageId) {
            this.pageId = pageId;
        }

        public NavigationPrimary getTabBar() {
            return navigationTabBar;
        }

        public void setNavigationTabBar(NavigationPrimary navigationTabBar) {
            this.navigationTabBar = navigationTabBar;
        }

        public boolean isTabSelected() {
            return isTabSelected;
        }

        public void setTabSelected(boolean tabSelected) {
            isTabSelected = tabSelected;
        }

        public ModuleList getNavigationModuleItem() {
            return navigationModuleItem;
        }

        public void setNavigationModuleItem(ModuleList navigationModuleItem) {
            this.navigationModuleItem = navigationModuleItem;
        }

    }



}
