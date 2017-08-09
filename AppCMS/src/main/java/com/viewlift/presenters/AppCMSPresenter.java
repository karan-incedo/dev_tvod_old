package com.viewlift.presenters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.apptentive.android.sdk.Apptentive;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viewlift.R;
import com.viewlift.analytics.AppsFlyerUtils;
import com.viewlift.casting.CastHelper;
import com.viewlift.models.billing.appcms.authentication.GoogleRefreshTokenResponse;
import com.viewlift.models.data.appcms.api.AddToWatchlistRequest;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.AppCMSVideoDetail;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.DeleteHistoryRequest;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.Mpeg;
import com.viewlift.models.data.appcms.api.Settings;
import com.viewlift.models.data.appcms.api.StreamingInfo;
import com.viewlift.models.data.appcms.api.SubscriptionPlan;
import com.viewlift.models.data.appcms.api.SubscriptionRequest;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.downloads.DownloadVideoRealm;
import com.viewlift.models.data.appcms.downloads.RealmController;
import com.viewlift.models.data.appcms.downloads.UserVideoDownloadStatus;
import com.viewlift.models.data.appcms.history.AppCMSDeleteHistoryResult;
import com.viewlift.models.data.appcms.history.AppCMSHistoryResult;
import com.viewlift.models.data.appcms.history.UpdateHistoryRequest;
import com.viewlift.models.data.appcms.history.UserVideoStatusResponse;
import com.viewlift.models.data.appcms.subscriptions.AppCMSSubscriptionPlanResult;
import com.viewlift.models.data.appcms.subscriptions.AppCMSSubscriptionResult;
import com.viewlift.models.data.appcms.subscriptions.UserSubscriptionPlan;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.MetaPage;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.ui.android.NavigationUser;
import com.viewlift.models.data.appcms.ui.authentication.UserIdentity;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.models.data.appcms.watchlist.AppCMSAddToWatchlistResult;
import com.viewlift.models.data.appcms.watchlist.AppCMSWatchlistResult;
import com.viewlift.models.network.background.tasks.GetAppCMSAPIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSAndroidUIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSMainUIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSPageUIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSRefreshIdentityAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSSiteAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSVideoDetailAsyncTask;
import com.viewlift.models.network.background.tasks.PostAppCMSLoginRequestAsyncTask;
import com.viewlift.models.network.components.AppCMSAPIComponent;
import com.viewlift.models.network.components.AppCMSSearchUrlComponent;
import com.viewlift.models.network.components.DaggerAppCMSAPIComponent;
import com.viewlift.models.network.components.DaggerAppCMSSearchUrlComponent;
import com.viewlift.models.network.modules.AppCMSAPIModule;
import com.viewlift.models.network.modules.AppCMSSearchUrlModule;
import com.viewlift.models.network.rest.AppCMSAddToWatchlistCall;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import com.viewlift.models.network.rest.AppCMSAnonymousAuthTokenCall;
import com.viewlift.models.network.rest.AppCMSBeaconRest;
import com.viewlift.models.network.rest.AppCMSDeleteHistoryCall;
import com.viewlift.models.network.rest.AppCMSFacebookLoginCall;
import com.viewlift.models.network.rest.AppCMSGoogleLoginCall;
import com.viewlift.models.network.rest.AppCMSHistoryCall;
import com.viewlift.models.network.rest.AppCMSMainUICall;
import com.viewlift.models.network.rest.AppCMSPageAPICall;
import com.viewlift.models.network.rest.AppCMSPageUICall;
import com.viewlift.models.network.rest.AppCMSRefreshIdentityCall;
import com.viewlift.models.network.rest.AppCMSResetPasswordCall;
import com.viewlift.models.network.rest.AppCMSSearchCall;
import com.viewlift.models.network.rest.AppCMSSignInCall;
import com.viewlift.models.network.rest.AppCMSSiteCall;
import com.viewlift.models.network.rest.AppCMSStreamingInfoCall;
import com.viewlift.models.network.rest.AppCMSSubscriptionCall;
import com.viewlift.models.network.rest.AppCMSSubscriptionPlanCall;
import com.viewlift.models.network.rest.AppCMSUpdateWatchHistoryCall;
import com.viewlift.models.network.rest.AppCMSUserDownloadVideoStatusCall;
import com.viewlift.models.network.rest.AppCMSUserIdentityCall;
import com.viewlift.models.network.rest.AppCMSUserVideoStatusCall;
import com.viewlift.models.network.rest.AppCMSVideoDetailCall;
import com.viewlift.models.network.rest.AppCMSWatchlistCall;
import com.viewlift.models.network.rest.GoogleCancelSubscriptionCall;
import com.viewlift.models.network.rest.GoogleRefreshTokenCall;
import com.viewlift.models.network.utility.MainUtils;
import com.viewlift.views.activity.AppCMSDownloadQualityActivity;
import com.viewlift.views.activity.AppCMSErrorActivity;
import com.viewlift.views.activity.AppCMSPageActivity;
import com.viewlift.views.activity.AppCMSPlayVideoActivity;
import com.viewlift.views.activity.AppCMSSearchActivity;
import com.viewlift.views.activity.AutoplayActivity;
import com.viewlift.views.adapters.AppCMSViewAdapter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.binders.AppCMSDownloadQualityBinder;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.fragments.AppCMSMoreFragment;
import com.viewlift.views.fragments.AppCMSNavItemsFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Inject;

import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/3/17.
 */

public class AppCMSPresenter {
    public static final String PRESENTER_NAVIGATE_ACTION = "appcms_presenter_navigate_action";
    public static final String PRESENTER_PAGE_LOADING_ACTION = "appcms_presenter_page_loading_action";
    public static final String PRESENTER_STOP_PAGE_LOADING_ACTION = "appcms_presenter_stop_page_loading_action";
    public static final String PRESENTER_CLOSE_SCREEN_ACTION = "appcms_presenter_close_action";
    public static final String PRESENTER_RESET_NAVIGATION_ITEM_ACTION = "appcms_presenter_set_navigation_item_action";
    public static final String PRESENTER_UPDATE_HISTORY_ACTION = "appcms_presenter_update_history_action";
    public static final String PRESENTER_REFRESH_PAGE_ACTION = "appcms_presenter_refresh_page_action";
    public static final String PRESENTER_DEEPLINK_ACTION = "appcms_presenter_deeplink_action";

    public static final int RC_PURCHASE_PLAY_STORE_ITEM = 1002;
    public static final String PRESENTER_DIALOG_ACTION = "appcms_presenter_dialog_action";
    public static final String SEARCH_ACTION = "SEARCH_ACTION";
    private static final String TAG = "AppCMSPresenter";
    private static final String LOGIN_SHARED_PREF_NAME = "login_pref";
    private static final String CASTING_OVERLAY_PREF_NAME = "cast_intro_pref";

    private static final String USER_ID_SHARED_PREF_NAME = "user_id_pref";

    private static final String CAST_SHARED_PREF_NAME = "cast_shown";

    private static final String USER_NAME_SHARED_PREF_NAME = "user_name_pref";
    private static final String USER_EMAIL_SHARED_PREF_NAME = "user_email_pref";
    private static final String REFRESH_TOKEN_SHARED_PREF_NAME = "refresh_token_pref";
    private static final String USER_LOGGED_IN_TIME_PREF_NAME = "user_loggedin_time_pref";
    private static final String USER_SETTINGS_PREF_NAME = "user_settings_pref";
    private static final String USER_CLOSED_CAPTION_PREF_KEY = "user_closed_caption_pref_key";
    private static final String FACEBOOK_ACCESS_TOKEN_SHARED_PREF_NAME = "facebook_access_token_shared_pref_name";
    private static final String GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME = "google_access_token_shared_pref_name";
    private static final String ACTIVE_SUBSCRIPTION_SKU = "active_subscription_sku_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_ID = "active_subscription_id_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_CURRENCY = "active_subscription_currency_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_RECEIPT = "active_subscription_token_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_PLAN_NAME = "active_subscription_plan_name_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_PRICE_NAME = "active_subscription_plan_price_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_PROCESSOR_NAME = "active_subscription_payment_processor_key";

    private static final String USER_DOWNLOAD_QUALITY_SHARED_PREF_NAME = "user_download_quality_pref";

    private static final String AUTH_TOKEN_SHARED_PREF_NAME = "auth_token_pref";
    private static final String ANONYMOUS_AUTH_TOKEN_PREF_NAME = "anonymous_auth_token_pref_key";

    private static final long MILLISECONDS_PER_SECOND = 1000L;
    private static final long SECONDS_PER_MINUTE = 60L;
    private static final long MAX_SESSION_DURATION_IN_MINUTES = 30L;
    private static final String MEDIA_SURFIX_MP4 = ".mp4";
    private static final String MEDIA_SURFIX_PNG = ".png";
    private static final String MEDIA_SURFIX_JPG = ".jpg";
    private static final String MEDIA_SUFFIX_SRT = ".srt";
    private static final int RC_GOOGLE_SIGN_IN = 1001;
    private static int PAGE_LRU_CACHE_SIZE = 10;
    private final Gson gson;
    private final AppCMSMainUICall appCMSMainUICall;
    private final AppCMSAndroidUICall appCMSAndroidUICall;
    private final AppCMSPageUICall appCMSPageUICall;
    private final AppCMSSiteCall appCMSSiteCall;
    private final AppCMSSearchCall appCMSSearchCall;
    private final AppCMSSignInCall appCMSSignInCall;
    private final AppCMSRefreshIdentityCall appCMSRefreshIdentityCall;
    private final AppCMSResetPasswordCall appCMSResetPasswordCall;
    private final AppCMSFacebookLoginCall appCMSFacebookLoginCall;
    private final AppCMSGoogleLoginCall appCMSGoogleLoginCall;
    private final AppCMSUserIdentityCall appCMSUserIdentityCall;
    private final GoogleRefreshTokenCall googleRefreshTokenCall;
    private final GoogleCancelSubscriptionCall googleCancelSubscriptionCall;

    private final AppCMSUpdateWatchHistoryCall appCMSUpdateWatchHistoryCall;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final Map<String, String> pageNameToActionMap;
    private final Map<String, AppCMSPageUI> actionToPageMap;
    private final Map<String, AppCMSPageAPI> actionToPageAPIMap;
    private final Map<String, AppCMSActionType> actionToActionTypeMap;
    private final AppCMSWatchlistCall appCMSWatchlistCall;
    private final AppCMSHistoryCall appCMSHistoryCall;
    private final AppCMSUserDownloadVideoStatusCall appCMSUserDownloadVideoStatusCall;

    private final AppCMSUserVideoStatusCall appCMSUserVideoStatusCall;
    private final AppCMSAddToWatchlistCall appCMSAddToWatchlistCall;
    private final AppCMSDeleteHistoryCall appCMSDeleteHistoryCall;
    private final AppCMSSubscriptionCall appCMSSubscriptionCall;
    private final AppCMSSubscriptionPlanCall appCMSSubscriptionPlanCall;
    private final AppCMSAnonymousAuthTokenCall appCMSAnonymousAuthTokenCall;

    private AppCMSPageAPICall appCMSPageAPICall;
    private AppCMSStreamingInfoCall appCMSStreamingInfoCall;
    private AppCMSVideoDetailCall appCMSVideoDetailCall;
    private Activity currentActivity;
    private Navigation navigation;
    private boolean loadFromFile;
    private boolean loadingPage;
    private AppCMSMain appCMSMain;
    private Queue<MetaPage> pagesToProcess;
    private Map<String, AppCMSPageUI> navigationPages;
    private Map<String, AppCMSPageAPI> navigationPageData;
    private Map<String, String> pageIdToPageAPIUrlMap;
    private Map<String, String> actionToPageAPIUrlMap;
    private Map<String, String> actionToPageNameMap;
    private Map<String, String> pageIdToPageNameMap;
    private Map<AppCMSActionType, MetaPage> actionTypeToMetaPageMap;
    private List<Action1<Boolean>> onOrientationChangeHandlers;
    private Map<String, List<OnInternalEvent>> onActionInternalEvents;
    private Stack<String> currentActions;

    private AppCMSSearchUrlComponent appCMSSearchUrlComponent;
    private DownloadManager downloadManager;
    private RealmController realmController;
    private BeaconRunnable beaconMessageRunnable;
    private Runnable beaconMessageThread;
    private GoogleAnalytics googleAnalytics;
    private Tracker tracker;
    private ServiceConnection inAppBillingServiceConn;
    private String tvHomeScreenPackage = "com.viewlift.tv.views.activity.AppCmsHomeActivity";
    private String tvErrorScreenPackage = "com.viewlift.tv.views.activity.AppCmsTvErrorActivity";
    private String tvVideoPlayerPackage = "com.viewlift.tv.views.activity.AppCMSTVPlayVideoActivity";
    private Uri deeplinkSearchQuery;
    private MetaPage splashPage;
    private MetaPage loginPage;
    private MetaPage downloadQualityPage;
    private MetaPage homePage;
    private MetaPage subscriptionPage;
    private PlatformType platformType;
    private AppCMSNavItemsFragment appCMSNavItemsFragment;
    private LaunchType launchType;
    private IInAppBillingService inAppBillingService;
    private String subscriptionUserEmail;
    private String subscriptionUserPassword;
    private boolean signupFromFacebook;
    private boolean isSignupFromGoogle;
    private String facebookAccessToken;
    private String facebookUserId;
    private String facebookUsername;
    private String facebookEmail;
    private String googleAccessToken;
    private String googleUserId;
    private String googleUsername;
    private String googleEmail;
    private String skuToPurchase;
    private String planToPurchase;
    private String currencyOfPlanToPurchase;
    private String planToPurchaseName;
    private String apikey;
    private float planToPurchasePrice;
    private String planReceipt;
    private GoogleApiClient googleApiClient;
    private long downloaded = 0L;
    private LruCache<String, PageView> pageViewLruCache;
    private EntitlementPendingVideoData entitlementPendingVideoData;
    private List<SubscriptionPlan> subscriptionPlans;
    private boolean navigateToHomeToRefresh;
    private boolean configurationChanged;
    private FirebaseAnalytics mFireBaseAnalytics;
    private boolean runUpdateDownloadIconTimer;
    private Timer updateDownloadIconTimer;

    @Inject
    public AppCMSPresenter(Gson gson,
                           AppCMSMainUICall appCMSMainUICall,
                           AppCMSAndroidUICall appCMSAndroidUICall,
                           AppCMSPageUICall appCMSPageUICall,
                           AppCMSSiteCall appCMSSiteCall,
                           AppCMSSearchCall appCMSSearchCall,

                           AppCMSWatchlistCall appCMSWatchlistCall,
                           AppCMSHistoryCall appCMSHistoryCall,

                           AppCMSDeleteHistoryCall appCMSDeleteHistoryCall,

                           AppCMSSubscriptionCall appCMSSubscriptionCall,
                           AppCMSSubscriptionPlanCall appCMSSubscriptionPlanCall,
                           AppCMSAnonymousAuthTokenCall appCMSAnonymousAuthTokenCall,

                           AppCMSBeaconRest appCMSBeaconRest,
                           AppCMSSignInCall appCMSSignInCall,
                           AppCMSRefreshIdentityCall appCMSRefreshIdentityCall,
                           AppCMSResetPasswordCall appCMSResetPasswordCall,

                           AppCMSFacebookLoginCall appCMSFacebookLoginCall,
                           AppCMSGoogleLoginCall appCMSGoogleLoginCall,

                           AppCMSUserIdentityCall appCMSUserIdentityCall,
                           GoogleRefreshTokenCall googleRefreshTokenCall,
                           GoogleCancelSubscriptionCall googleCancelSubscriptionCall,

                           AppCMSUpdateWatchHistoryCall appCMSUpdateWatchHistoryCall,
                           AppCMSUserVideoStatusCall appCMSUserVideoStatusCall,
                           AppCMSUserDownloadVideoStatusCall appCMSUserDownloadVideoStatusCall,

                           AppCMSAddToWatchlistCall appCMSAddToWatchlistCall,

                           Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                           Map<String, String> pageNameToActionMap,
                           Map<String, AppCMSPageUI> actionToPageMap,
                           Map<String, AppCMSPageAPI> actionToPageAPIMap,
                           Map<String, AppCMSActionType> actionToActionTypeMap) {
        this.gson = gson;

        this.appCMSMainUICall = appCMSMainUICall;
        this.appCMSAndroidUICall = appCMSAndroidUICall;
        this.appCMSPageUICall = appCMSPageUICall;
        this.appCMSSiteCall = appCMSSiteCall;
        this.appCMSSearchCall = appCMSSearchCall;
        this.appCMSSignInCall = appCMSSignInCall;
        this.appCMSRefreshIdentityCall = appCMSRefreshIdentityCall;
        this.appCMSResetPasswordCall = appCMSResetPasswordCall;

        this.appCMSFacebookLoginCall = appCMSFacebookLoginCall;
        this.appCMSGoogleLoginCall = appCMSGoogleLoginCall;

        this.jsonValueKeyMap = jsonValueKeyMap;
        this.pageNameToActionMap = pageNameToActionMap;
        this.actionToPageMap = actionToPageMap;
        this.actionToPageAPIMap = actionToPageAPIMap;
        this.actionToActionTypeMap = actionToActionTypeMap;
        this.appCMSUserIdentityCall = appCMSUserIdentityCall;
        this.googleRefreshTokenCall = googleRefreshTokenCall;
        this.googleCancelSubscriptionCall = googleCancelSubscriptionCall;

        this.appCMSUpdateWatchHistoryCall = appCMSUpdateWatchHistoryCall;
        this.appCMSUserVideoStatusCall = appCMSUserVideoStatusCall;
        this.appCMSUserDownloadVideoStatusCall = appCMSUserDownloadVideoStatusCall;

        this.appCMSAddToWatchlistCall = appCMSAddToWatchlistCall;

        this.appCMSWatchlistCall = appCMSWatchlistCall;
        this.appCMSHistoryCall = appCMSHistoryCall;

        this.appCMSDeleteHistoryCall = appCMSDeleteHistoryCall;

        this.appCMSSubscriptionCall = appCMSSubscriptionCall;
        this.appCMSSubscriptionPlanCall = appCMSSubscriptionPlanCall;
        this.appCMSAnonymousAuthTokenCall = appCMSAnonymousAuthTokenCall;

        this.loadingPage = false;
        this.navigationPages = new HashMap<>();
        this.navigationPageData = new HashMap<>();
        this.pageIdToPageAPIUrlMap = new HashMap<>();
        this.actionToPageAPIUrlMap = new HashMap<>();
        this.actionToPageNameMap = new HashMap<>();
        this.pageIdToPageNameMap = new HashMap<>();
        this.actionTypeToMetaPageMap = new HashMap<>();
        this.onOrientationChangeHandlers = new ArrayList<>();
        this.onActionInternalEvents = new HashMap<>();
        this.currentActions = new Stack<>();
        this.beaconMessageRunnable = new BeaconRunnable(appCMSBeaconRest);
        this.beaconMessageThread = new Thread(this.beaconMessageRunnable);

        this.launchType = LaunchType.LOGIN_AND_SIGNUP;
    }

    public LruCache<String, PageView> getPageViewLruCache() {
        if (pageViewLruCache == null) {
            pageViewLruCache = new LruCache<>(PAGE_LRU_CACHE_SIZE);
        }
        return pageViewLruCache;
    }

    public void removeLruCacheItem(Context context, String pageId) {
        if (getPageViewLruCache().get(pageId + BaseView.isLandscape(context)) != null) {
            getPageViewLruCache().remove(pageId + BaseView.isLandscape(context));
        }
    }

    public void setCurrentActivity(Activity activity) {
        this.currentActivity = activity;
        this.downloadManager = (DownloadManager) currentActivity.getSystemService(Context.DOWNLOAD_SERVICE);
        this.realmController = RealmController.with(currentActivity);
    }

    public void unsetCurrentActivity(Activity closedActivity) {
        if (currentActivity == closedActivity) {
            currentActivity = null;
            this.realmController.closeRealm();
        }
    }

    public void initializeGA(String trackerId) {
        if (this.googleAnalytics == null && currentActivity != null) {
            this.googleAnalytics = GoogleAnalytics.getInstance(currentActivity);
            this.tracker = this.googleAnalytics.newTracker(trackerId);
        }
    }

    public boolean launchVideoPlayer(final ContentDatum contentDatum,
                                     final int currentlyPlayingIndex,
                                     List<String> relateVideoIds,
                                     long watchedTime) {
        boolean result = false;
        if (currentActivity != null &&
                !loadingPage && appCMSMain != null &&
                !TextUtils.isEmpty(appCMSMain.getApiBaseUrl()) &&
                !TextUtils.isEmpty(appCMSMain.getSite())) {

            boolean entitlementActive = true;
            boolean svodServiceType =
                    appCMSMain.getServiceType()
                            .equals(currentActivity.getString(R.string.app_cms_main_svod_service_type_key));
            if (svodServiceType) {
                if (isUserLoggedIn(currentActivity)) {
                    // For now just verify that the anonymous user token is available, but the subscription needs to be verified too.
                    if (TextUtils.isEmpty(getAnonymousUserToken(currentActivity))) {
                        showEntitlementDialog(DialogType.SUBSCRIPTION_REQUIRED);
                        entitlementActive = false;
                    }
                } else {
                    showEntitlementDialog(DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED);
                    entitlementActive = false;
                }
            }

            final String action = currentActivity.getString(R.string.app_cms_action_watchvideo_key);
            result = true;

            /*When content details are null it means video player is launched from somewhere
            * other than video detail fragment*/

            if (contentDatum.getContentDetails() == null) {
                String url = currentActivity.getString(R.string.app_cms_video_detail_api_url,
                        appCMSMain.getApiBaseUrl(),
                        contentDatum.getGist().getId(),
                        appCMSMain.getSite());
                GetAppCMSVideoDetailAsyncTask.Params params =
                        new GetAppCMSVideoDetailAsyncTask.Params.Builder().url(url)
                                .authToken(getAuthToken(currentActivity)).build();

                final boolean resultEntitlementActive = entitlementActive;
                new GetAppCMSVideoDetailAsyncTask(appCMSVideoDetailCall,
                        appCMSVideoDetail -> {
                            if (appCMSVideoDetail != null &&
                                    appCMSVideoDetail.getRecords() != null &&
                                    appCMSVideoDetail.getRecords().get(0) != null &&
                                    appCMSVideoDetail.getRecords().get(0).getContentDetails() != null) {
                                if (resultEntitlementActive) {
                                    if (watchedTime >= 0) {
                                        appCMSVideoDetail.getRecords().get(0).getGist().setWatchedTime(watchedTime);
                                    }
                                    launchButtonSelectedAction(appCMSVideoDetail.getRecords().get(0).getGist().getPermalink(),
                                            action,
                                            appCMSVideoDetail.getRecords().get(0).getGist().getTitle(),
                                            null,
                                            appCMSVideoDetail.getRecords().get(0),
                                            false,
                                            currentlyPlayingIndex,
                                            appCMSVideoDetail.getRecords().get(0).getContentDetails().getRelatedVideoIds());
                                } else {
                                    entitlementPendingVideoData = new EntitlementPendingVideoData();
                                    entitlementPendingVideoData.action = action;
                                    entitlementPendingVideoData.closeLauncher = false;
                                    entitlementPendingVideoData.contentDatum = appCMSVideoDetail.getRecords().get(0);
                                    entitlementPendingVideoData.currentlyPlayingIndex = currentlyPlayingIndex;
                                    entitlementPendingVideoData.pagePath = appCMSVideoDetail.getRecords().get(0).getGist().getPermalink();
                                    entitlementPendingVideoData.filmTitle = appCMSVideoDetail.getRecords().get(0).getGist().getTitle();
                                    entitlementPendingVideoData.extraData = null;
                                    entitlementPendingVideoData.relateVideoIds = appCMSVideoDetail.getRecords().get(0).getContentDetails().getRelatedVideoIds();
                                    navigateToHomeToRefresh = true;
                                }
                            }
                        }).execute(params);
            } else {
                if (entitlementActive) {
                    if (watchedTime >= 0) {
                        contentDatum.getGist().setWatchedTime(watchedTime);
                    }
                    launchButtonSelectedAction(
                            contentDatum.getGist().getPermalink(),
                            action,
                            contentDatum.getGist().getTitle(),
                            null,
                            contentDatum,
                            false,
                            currentlyPlayingIndex,
                            relateVideoIds);
                } else {
                    entitlementPendingVideoData = new EntitlementPendingVideoData();
                    entitlementPendingVideoData.action = action;
                    entitlementPendingVideoData.closeLauncher = false;
                    entitlementPendingVideoData.contentDatum = contentDatum;
                    entitlementPendingVideoData.currentlyPlayingIndex = currentlyPlayingIndex;
                    entitlementPendingVideoData.pagePath = contentDatum.getGist().getPermalink();
                    entitlementPendingVideoData.filmTitle = contentDatum.getGist().getTitle();
                    entitlementPendingVideoData.extraData = null;
                    entitlementPendingVideoData.relateVideoIds = relateVideoIds;
                    navigateToHomeToRefresh = true;
                }
            }
        }
        return result;
    }

    public void updateWatchedTime(String filmId, long watchedTime) {
        if (getLoggedInUser(currentActivity) != null) {
            UpdateHistoryRequest updateHistoryRequest = new UpdateHistoryRequest();
            updateHistoryRequest.setUserId(getLoggedInUser(currentActivity));
            updateHistoryRequest.setWatchedTime(watchedTime);
            updateHistoryRequest.setVideoId(filmId);
            updateHistoryRequest.setSiteOwner(appCMSMain.getInternalName());

            String url = currentActivity.getString(R.string.app_cms_update_watch_history_api_url,
                    appCMSMain.getApiBaseUrl());

            appCMSUpdateWatchHistoryCall.call(url, getAuthToken(currentActivity), updateHistoryRequest,
                    s -> {
                        // Call update history
                        if (currentActivity != null) {
                            sendUpdateHistoryAction();
                        }
                    });
        }
    }

    public void sendUpdateHistoryAction() {
        Intent updateHistoryIntent = new Intent(PRESENTER_UPDATE_HISTORY_ACTION);
        currentActivity.sendBroadcast(updateHistoryIntent);
    }

    public void getUserVideoStatus(String filmId, Action1<UserVideoStatusResponse> responseAction) {
        if (shouldRefreshAuthToken()) {
            refreshIdentity(getRefreshToken(currentActivity),
                    new Action0() {
                        @Override
                        public void call() {
                            String url = currentActivity.getString(R.string.app_cms_video_status_api_url,
                                    appCMSMain.getApiBaseUrl(), filmId, appCMSMain.getInternalName());
                            appCMSUserVideoStatusCall.call(url, getAuthToken(currentActivity), responseAction);
                        }
                    });
        } else {
            String url = currentActivity.getString(R.string.app_cms_video_status_api_url,
                    appCMSMain.getApiBaseUrl(), filmId, appCMSMain.getInternalName());
            appCMSUserVideoStatusCall.call(url, getAuthToken(currentActivity), responseAction);
        }
    }

    public void getUserVideoDownloadStatus(String filmId, Action1<UserVideoDownloadStatus> responseAction, String userId) {
        appCMSUserDownloadVideoStatusCall.call(filmId, this, responseAction, userId);
    }

    public void signinAnonymousUser(final AppCMSMain main) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_anonymous_auth_token_api_url,
                    main.getApiBaseUrl(),
                    main.getInternalName());
            appCMSAnonymousAuthTokenCall.call(url, anonymousAuthTokenResponse -> {
                if (anonymousAuthTokenResponse != null) {
                    setAnonymousUserToken(currentActivity, anonymousAuthTokenResponse.getAuthorizationToken());
                }
            });
        }
    }

    public void signinAnonymousUser(final Activity activity, final AppCMSMain main, int tryCount) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_anonymous_auth_token_api_url,
                    main.getApiBaseUrl(),
                    main.getInternalName());
            appCMSAnonymousAuthTokenCall.call(url, anonymousAuthTokenResponse -> {
                if (anonymousAuthTokenResponse != null) {
                    setAnonymousUserToken(currentActivity, anonymousAuthTokenResponse.getAuthorizationToken());
                    if (tryCount == 0) {
                        getAppCMSAndroid(activity,
                                main,
                                tryCount + 1);
                    } else {
                        showDialog(DialogType.NETWORK, null, false, null);
                    }
                }
            });
        }
    }

    public boolean launchButtonSelectedAction(String pagePath,
                                              String action,
                                              String filmTitle,
                                              String[] extraData,
                                              ContentDatum contentDatum,
                                              final boolean closeLauncher,
                                              int currentlyPlayingIndex,
                                              List<String> relateVideoIds) {
        boolean result = false;
        if (!isNetworkConnected()) {
            showDialog(DialogType.NETWORK, null, false, null);
        } else {
            final AppCMSActionType actionType = actionToActionTypeMap.get(action);
            boolean isVideoOffline = false;
            try {
                isVideoOffline = Boolean.parseBoolean(extraData[3]);
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
            Log.d(TAG, "Attempting to load page " + filmTitle + ": " + pagePath);

        /*This is to enable offline video playback even if Internet is not available*/
            if (!(actionType == AppCMSActionType.PLAY_VIDEO_PAGE && isVideoOffline) && !isNetworkConnected()) {
                showDialog(DialogType.NETWORK, null, false, null);
            } else if (currentActivity != null && !loadingPage) {
                if (actionType == null) {
                    Log.e(TAG, "Action " + action + " not found!");
                    return false;
                }
                result = true;
                boolean isTrailer = actionType == AppCMSActionType.WATCH_TRAILER;
                if (actionType == AppCMSActionType.PLAY_VIDEO_PAGE ||
                        actionType == AppCMSActionType.WATCH_TRAILER) {
                    boolean entitlementActive = true;
                    boolean svodServiceType =
                            appCMSMain.getServiceType()
                                    .equals(currentActivity.getString(R.string.app_cms_main_svod_service_type_key));
                    if (svodServiceType && !isTrailer) {
                        if (isUserLoggedIn(currentActivity)) {
                            if (!isUserSubscribed(currentActivity)) {
                                showEntitlementDialog(DialogType.SUBSCRIPTION_REQUIRED);
                                entitlementActive = false;
                            }
                        } else {
                            showEntitlementDialog(DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED);
                            entitlementActive = false;
                        }
                    }

                    if (entitlementActive) {
                        Intent playVideoIntent = new Intent(currentActivity, AppCMSPlayVideoActivity.class);
                        boolean requestAds = !svodServiceType && actionType != AppCMSActionType.PLAY_VIDEO_PAGE;
                        String adsUrl;
                        if (actionType == AppCMSActionType.PLAY_VIDEO_PAGE) {
                            if (pagePath != null && pagePath.contains(
                                    currentActivity.getString(R.string.app_cms_action_qualifier_watchvideo_key))) {
                                requestAds = false;
                            }
                            playVideoIntent.putExtra(currentActivity.getString(R.string.play_ads_key), requestAds);
                            if (contentDatum != null &&
                                    contentDatum.getGist() != null &&
                                    contentDatum.getGist().getWatchedTime() != 0) {
                                playVideoIntent.putExtra(currentActivity.getString(R.string.watched_time_key),
                                        contentDatum.getGist().getWatchedTime());
                            }
                        } else if (actionType == AppCMSActionType.WATCH_TRAILER) {
                            playVideoIntent.putExtra(currentActivity.getString(R.string.watched_time_key),
                                    0);
                        }
                        if (contentDatum != null &&
                                contentDatum.getGist() != null &&
                                contentDatum.getGist().getVideoImageUrl() != null) {
                            playVideoIntent.putExtra(currentActivity.getString(R.string.played_movie_image_url),
                                    contentDatum.getGist().getVideoImageUrl());
                        } else {
                            playVideoIntent.putExtra(currentActivity.getString(R.string.played_movie_image_url), "");
                        }

                        if (contentDatum != null &&
                                contentDatum.getGist() != null &&
                                contentDatum.getGist().getVideoImageUrl() != null) {
                            playVideoIntent.putExtra(currentActivity.getString(R.string.played_movie_image_url),
                                    contentDatum.getGist().getVideoImageUrl());
                        } else {
                            playVideoIntent.putExtra(currentActivity.getString(R.string.played_movie_image_url), "");
                        }

                        playVideoIntent.putExtra(currentActivity.getString(R.string.video_player_font_color_key),
                                appCMSMain.getBrand().getGeneral().getTextColor());
                        playVideoIntent.putExtra(currentActivity.getString(R.string.video_player_title_key),
                                filmTitle);
                        playVideoIntent.putExtra(currentActivity.getString(R.string.video_player_hls_url_key),
                                extraData);

                        Date now = new Date();
                        adsUrl = currentActivity.getString(R.string.app_cms_ads_api_url,
                                getPermalinkCompletePath(pagePath),
                                now.getTime(),
                                appCMSMain.getSite());

                        String backgroundColor = appCMSMain.getBrand()
                                .getGeneral()
                                .getBackgroundColor();

                        AppCMSVideoPageBinder appCMSVideoPageBinder =
                                getAppCMSVideoPageBinder(currentActivity,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        false,
                                        false,
                                        false,
                                        false,
                                        false,
                                        requestAds,
                                        appCMSMain.getBrand().getGeneral().getTextColor(),
                                        backgroundColor,
                                        adsUrl,
                                        contentDatum,
                                        isTrailer,
                                        relateVideoIds,
                                        currentlyPlayingIndex,
                                        isVideoOffline);
                        if (closeLauncher) {
                            sendCloseOthersAction(null, true);
                        }

                        Bundle bundle = new Bundle();
                        bundle.putBinder(currentActivity.getString(R.string.app_cms_video_player_binder_key),
                                appCMSVideoPageBinder);
                        playVideoIntent.putExtra(currentActivity.getString(R.string.app_cms_video_player_bundle_binder_key), bundle);

                        currentActivity.startActivity(playVideoIntent);
                    } else {
                        entitlementPendingVideoData = new EntitlementPendingVideoData();
                        entitlementPendingVideoData.action = action;
                        entitlementPendingVideoData.closeLauncher = closeLauncher;
                        entitlementPendingVideoData.contentDatum = contentDatum;
                        entitlementPendingVideoData.currentlyPlayingIndex = currentlyPlayingIndex;
                        entitlementPendingVideoData.pagePath = pagePath;
                        entitlementPendingVideoData.filmTitle = filmTitle;
                        entitlementPendingVideoData.extraData = extraData;
                        entitlementPendingVideoData.relateVideoIds = relateVideoIds;
                        navigateToHomeToRefresh = true;
                    }
                } else if (actionType == AppCMSActionType.SHARE) {
                    if (extraData != null && extraData.length > 0) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, extraData[0]);
                        sendIntent.setType(currentActivity.getString(R.string.text_plain_mime_type));
                        currentActivity.startActivity(Intent.createChooser(sendIntent,
                                currentActivity.getResources().getText(R.string.send_to)));
                    }
                } else if (actionType == AppCMSActionType.CLOSE) {
                    sendCloseOthersAction(null, false);
                } else if (actionType == AppCMSActionType.LOGIN) {
                    Log.d(TAG, "Login action selected: " + extraData[0]);
                    closeSoftKeyboard();
                    login(extraData[0], extraData[1]);
                } else if (actionType == AppCMSActionType.FORGOT_PASSWORD) {
                    Log.d(TAG, "Forgot password selected: " + extraData[0]);
                    closeSoftKeyboard();
                    launchResetPasswordPage(extraData[0]);
                } else if (actionType == AppCMSActionType.LOGIN_FACEBOOK) {
                    Log.d(TAG, "Facebook Login selected");
                    loginFacebook();
                } else if (actionType == AppCMSActionType.SIGNUP_FACEBOOK) {
                    Log.d(TAG, "Facebook Signup selected");
                    loginFacebook();
                } else if (actionType == AppCMSActionType.LOGIN_GOOGLE) {
                    Log.d(TAG, "Google Login selected");
                    loginGoogle();
                } else if (actionType == AppCMSActionType.SIGNUP_GOOGLE) {
                    Log.d(TAG, "Google signup selected");
                    loginGoogle();
                } else {
                    if (actionType == AppCMSActionType.SIGNUP) {
                        Log.d(TAG, "Sign-Up selected: " + extraData[0]);
                        closeSoftKeyboard();
                        signup(extraData[0], extraData[1]);
                    } else if (actionType == AppCMSActionType.START_TRIAL) {
                        Log.d(TAG, "Start Trial selected");
                        navigateToSubscriptionPlansPage(null, null);
                    } else if (actionType == AppCMSActionType.EDIT_PROFILE) {
                        launchEditProfilePage();
                    } else if (actionType == AppCMSActionType.MANAGE_SUBSCRIPTION) {
                        if (extraData != null && extraData.length > 0) {
                            String key = extraData[0];
                            if (jsonValueKeyMap.get(key) == AppCMSUIKeyType.PAGE_SETTINGS_CANCEL_PLAN_PROFILE_KEY) {
                                sendSubscriptionCancellation();
                            } else if (jsonValueKeyMap.get(key) == AppCMSUIKeyType.PAGE_SETTINGS_UPGRADE_PLAN_PROFILE_KEY) {
                                navigateToSubscriptionPlansPage(null, null);
                            }
                        }
                    } else if (actionType == AppCMSActionType.HOME_PAGE) {
                        navigateToHomePage();
                    } else if (actionType == AppCMSActionType.SIGNIN) {
                        navigateToLoginPage();
                    } else if (actionType == AppCMSActionType.CHANGE_DOWNLOAD_QUALITY) {
                        showDownloadQualityScreen(contentDatum, userVideoDownloadStatus -> {
                            //
                        });
                    } else {
                        boolean appbarPresent = true;
                        boolean fullscreenEnabled = false;
                        boolean navbarPresent = true;
                        final StringBuffer screenName = new StringBuffer();
                        if (!TextUtils.isEmpty(actionToPageNameMap.get(action))) {
                            screenName.append(actionToPageNameMap.get(action));
                        }
                        loadingPage = true;

                        switch (actionType) {
                            case AUTH_PAGE:
                                appbarPresent = false;
                                fullscreenEnabled = false;
                                navbarPresent = false;
                                break;

                            case VIDEO_PAGE:
                                appbarPresent = false;
                                fullscreenEnabled = false;
                                navbarPresent = false;
                                screenName.append(currentActivity.getString(
                                        R.string.app_cms_template_page_separator));
                                screenName.append(filmTitle);
                                break;

                            case PLAY_VIDEO_PAGE:
                                appbarPresent = false;
                                fullscreenEnabled = false;
                                navbarPresent = false;
                                break;

                            case HOME_PAGE:
                            default:
                                break;
                        }
                        currentActivity.sendBroadcast(new Intent(
                                AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
                        AppCMSPageUI appCMSPageUI = actionToPageMap.get(action);
                        getPageIdContent(appCMSMain.getApiBaseUrl(),
                                actionToPageAPIUrlMap.get(action),
                                appCMSMain.getInternalName(),
                                false,
                                pagePath,
                                new AppCMSPageAPIAction(appbarPresent,
                                        fullscreenEnabled,
                                        navbarPresent,
                                        appCMSPageUI,
                                        action,
                                        getPageId(appCMSPageUI),
                                        filmTitle,
                                        pagePath,
                                        false,
                                        closeLauncher,
                                        null) {

                                    final AppCMSPageAPIAction appCMSPageAPIAction = this;

                                    @Override
                                    public void call(final AppCMSPageAPI appCMSPageAPI) {
                                        if (appCMSPageAPI != null) {

                                            boolean loadingHistory = false;
                                            if (isUserLoggedIn(currentActivity)) {
                                                for (Module module : appCMSPageAPI.getModules()) {
                                                    AppCMSUIKeyType moduleType = jsonValueKeyMap.get(module.getModuleType());
                                                    if (moduleType == AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY ||
                                                            moduleType == AppCMSUIKeyType.PAGE_VIDEO_DETAILS_KEY) {
                                                        if (module.getContentData() != null &&
                                                                module.getContentData().size() > 0) {
                                                            loadingHistory = true;
                                                            getHistoryData(appCMSHistoryResult -> {
                                                                if (appCMSHistoryResult != null) {
                                                                    AppCMSPageAPI historyAPI =
                                                                            appCMSHistoryResult.convertToAppCMSPageAPI(appCMSPageAPI.getId());
                                                                    historyAPI.getModules().get(0).setId(module.getId());
                                                                    mergeData(historyAPI, appCMSPageAPI);

                                                                    cancelInternalEvents();
                                                                    pushActionInternalEvents(pageId
                                                                            + BaseView.isLandscape(currentActivity));
                                                                    navigationPageData.put(pageId, appCMSPageAPI);
                                                                    if (launchActivity) {
                                                                        launchPageActivity(currentActivity,
                                                                                appCMSPageAPIAction.appCMSPageUI,
                                                                                appCMSPageAPI,
                                                                                appCMSPageAPIAction.pageId,
                                                                                appCMSPageAPIAction.pageTitle,
                                                                                appCMSPageAPIAction.pageTitle,
                                                                                pageIdToPageNameMap.get(pageId),
                                                                                loadFromFile,
                                                                                appCMSPageAPIAction.appbarPresent,
                                                                                appCMSPageAPIAction.fullscreenEnabled,
                                                                                appCMSPageAPIAction.navbarPresent,
                                                                                appCMSPageAPIAction.sendCloseAction,
                                                                                appCMSPageAPIAction.searchQuery,
                                                                                ExtraScreenType.NONE);
                                                                    } else {
                                                                        Bundle args = getPageActivityBundle(currentActivity,
                                                                                appCMSPageAPIAction.appCMSPageUI,
                                                                                appCMSPageAPI,
                                                                                appCMSPageAPIAction.pageId,
                                                                                appCMSPageAPIAction.pageTitle,
                                                                                appCMSPageAPIAction.pagePath,
                                                                                pageIdToPageNameMap.get(pageId),
                                                                                loadFromFile,
                                                                                appCMSPageAPIAction.appbarPresent,
                                                                                appCMSPageAPIAction.fullscreenEnabled,
                                                                                appCMSPageAPIAction.navbarPresent,
                                                                                appCMSPageAPIAction.sendCloseAction,
                                                                                appCMSPageAPIAction.searchQuery,
                                                                                ExtraScreenType.NONE);
                                                                        Intent updatePageIntent =
                                                                                new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                                                        updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                                                                args);
                                                                        currentActivity.sendBroadcast(updatePageIntent);
                                                                        dismissOpenDialogs(null);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }

                                            if (!loadingHistory) {
                                                cancelInternalEvents();
                                                pushActionInternalEvents(this.action
                                                        + BaseView.isLandscape(currentActivity));
                                                Bundle args = getPageActivityBundle(currentActivity,
                                                        this.appCMSPageUI,
                                                        appCMSPageAPI,
                                                        this.pageId,
                                                        appCMSPageAPI.getTitle(),
                                                        this.pagePath,
                                                        screenName.toString(),
                                                        loadFromFile,
                                                        this.appbarPresent,
                                                        this.fullscreenEnabled,
                                                        this.navbarPresent,
                                                        this.sendCloseAction,
                                                        this.searchQuery,
                                                        ExtraScreenType.NONE);
                                                Intent updatePageIntent =
                                                        new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                                updatePageIntent.putExtra(
                                                        currentActivity.getString(R.string.app_cms_bundle_key),
                                                        args);
                                                currentActivity.sendBroadcast(updatePageIntent);
                                            }
                                        } else {
                                            sendStopLoadingPageAction();
                                            setNavItemToCurrentAction(currentActivity);
                                        }
                                        loadingPage = false;
                                    }
                                });
                    }
                }
            }
        }
        return result;
    }

    public boolean launchNavigationPage() {
        boolean result = false;

        if (currentActivity != null) {
            cancelInternalEvents();

            Bundle args = getPageActivityBundle(currentActivity,
                    null,
                    null,
                    currentActivity.getString(R.string.app_cms_navigation_page_tag),
                    currentActivity.getString(R.string.app_cms_navigation_page_tag),
                    null,
                    currentActivity.getString(R.string.app_cms_navigation_page_tag),
                    false,
                    true,
                    false,
                    false,
                    false,
                    null,
                    ExtraScreenType.NAVIGATION);
            Intent updatePageIntent =
                    new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
            updatePageIntent.putExtra(
                    currentActivity.getString(R.string.app_cms_bundle_key),
                    args);
            currentActivity.sendBroadcast(updatePageIntent);

            result = true;
        }

        return result;
    }

    public void mergeData(AppCMSPageAPI fromAppCMSPageAPI, AppCMSPageAPI toAppCMSPageAPI) {
        for (Module module : fromAppCMSPageAPI.getModules()) {
            Module updateToModule = null;
            Module updateFromModule = null;
            for (Module module1 : toAppCMSPageAPI.getModules()) {
                if (module.getId() != null && module1 != null &&
                        module.getId().equals(module1.getId())) {
                    updateFromModule = module;
                    updateToModule = module1;
                }
            }
            if (updateToModule != null &&

                    updateToModule.getContentData() != null &&
                    updateFromModule != null &&
                    updateFromModule.getContentData() != null) {

                for (ContentDatum toContentDatum : updateToModule.getContentData()) {
                    for (ContentDatum fromContentDatum : updateFromModule.getContentData()) {
                        if (!TextUtils.isEmpty(toContentDatum.getGist().getDescription()) &&
                                toContentDatum.getGist().getDescription().equals(fromContentDatum.getGist().getDescription())) {
                            toContentDatum.getGist().setWatchedTime(fromContentDatum.getGist().getWatchedTime());
                            toContentDatum.getGist().setWatchedPercentage(fromContentDatum.getGist().getWatchedPercentage());
                        }
                    }
                }
            }
        }
    }

    public void dismissOpenDialogs(AppCMSNavItemsFragment newAppCMSNavItemsFragment) {
        if (appCMSNavItemsFragment != null && appCMSNavItemsFragment.isVisible()) {
            appCMSNavItemsFragment.dismiss();
            appCMSNavItemsFragment = null;
        }
        appCMSNavItemsFragment = newAppCMSNavItemsFragment;
    }

    public void onConfigurationChange(boolean configurationChanged) {
        this.configurationChanged = configurationChanged;
    }

    public boolean getConfigurationChanged() {
        return configurationChanged;
    }

    public boolean isMainFragmentTransparent() {
        if (currentActivity != null) {
            FrameLayout mainFragmentView =
                    (FrameLayout) currentActivity.findViewById(R.id.app_cms_fragment);
            if (mainFragmentView != null) {
                return (mainFragmentView.getAlpha() != 1.0f &&
                        mainFragmentView.getVisibility() == View.VISIBLE);
            }
        }
        return false;
    }

    public boolean isMainFragmentViewVisible() {
        if (currentActivity != null) {
            FrameLayout mainFragmentView =
                    (FrameLayout) currentActivity.findViewById(R.id.app_cms_fragment);
            if (mainFragmentView != null) {
                return (mainFragmentView.getVisibility() == View.VISIBLE);
            }
        }
        return false;
    }

    public void showMainFragmentView(boolean show) {
        if (currentActivity != null) {
            FrameLayout mainFragmentView =
                    (FrameLayout) currentActivity.findViewById(R.id.app_cms_fragment);
            if (mainFragmentView != null) {
                if (show) {
                    mainFragmentView.setVisibility(View.VISIBLE);
                    mainFragmentView.setAlpha(1.0f);
                    FrameLayout addOnFragment =
                            (FrameLayout) currentActivity.findViewById(R.id.app_cms_addon_fragment);
                    if (addOnFragment != null) {
                        addOnFragment.setVisibility(View.GONE);
                    }
                    setMainFragmentEnabled(true);
                } else {
                    mainFragmentView.setAlpha(0.0f);
                    mainFragmentView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void setMainFragmentEnabled(boolean isEnabled) {
        FrameLayout mainFragmentView =
                (FrameLayout) currentActivity.findViewById(R.id.app_cms_fragment);
        if (mainFragmentView != null) {
            setAllChildrenEnabled(isEnabled, mainFragmentView);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setAllChildrenEnabled(boolean isEnabled, ViewGroup viewGroup) {
        viewGroup.setEnabled(isEnabled);
        viewGroup.setClickable(isEnabled);
        viewGroup.setNestedScrollingEnabled(isEnabled);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                if (viewGroup.getChildAt(i) instanceof RecyclerView) {
                    ((RecyclerView) viewGroup.getChildAt(i)).setLayoutFrozen(!isEnabled);
                    if (((RecyclerView) viewGroup.getChildAt(i)).getAdapter() instanceof AppCMSViewAdapter) {
                        AppCMSViewAdapter appCMSViewAdapter =
                                (AppCMSViewAdapter) ((RecyclerView) viewGroup.getChildAt(i)).getAdapter();
                        appCMSViewAdapter.setClickable(isEnabled);
                    }
                } else {
                    setAllChildrenEnabled(isEnabled, (ViewGroup) viewGroup.getChildAt(i));
                }
            } else {
                viewGroup.getChildAt(i).setEnabled(isEnabled);
                viewGroup.getChildAt(i).setClickable(isEnabled);
            }
        }
    }

    public void setMainFragmentTransparency(float transparency) {
        if (currentActivity != null) {
            FrameLayout mainFragmentView =
                    (FrameLayout) currentActivity.findViewById(R.id.app_cms_fragment);
            if (mainFragmentView != null) {
                mainFragmentView.setAlpha(transparency);
            }
        }
    }

    public void showAddOnFragment(boolean showMainFragment, float mainFragmentTransparency) {
        showMainFragmentView(showMainFragment);
        setMainFragmentTransparency(mainFragmentTransparency);
        FrameLayout addOnFragment =
                (FrameLayout) currentActivity.findViewById(R.id.app_cms_addon_fragment);
        if (addOnFragment != null) {
            addOnFragment.setVisibility(View.VISIBLE);
            addOnFragment.bringToFront();
        }
        setMainFragmentEnabled(false);
    }

    public boolean isAdditionalFragmentViewAvailable() {
        if (currentActivity != null) {
            FrameLayout additionalFragmentView =
                    (FrameLayout) currentActivity.findViewById(R.id.app_cms_addon_fragment);
            if (additionalFragmentView != null) {
                return true;
            }
        }
        return false;
    }

    private void clearAdditionalFragment() {
        if (isAdditionalFragmentViewAvailable()) {
            FrameLayout additionalFragmentView =
                    (FrameLayout) currentActivity.findViewById(R.id.app_cms_addon_fragment);
            additionalFragmentView.removeAllViews();
        }
    }

    public void launchSearchPage() {
        if (currentActivity != null) {
            cancelInternalEvents();

            Bundle args = getPageActivityBundle(currentActivity,
                    null,
                    null,
                    currentActivity.getString(R.string.app_cms_search_page_tag),
                    currentActivity.getString(R.string.app_cms_search_page_tag),
                    null,
                    currentActivity.getString(R.string.app_cms_search_page_tag),
                    false,
                    true,
                    false,
                    true,
                    false,
                    null,
                    ExtraScreenType.SEARCH);
            Intent updatePageIntent =
                    new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
            updatePageIntent.putExtra(
                    currentActivity.getString(R.string.app_cms_bundle_key),
                    args);
            currentActivity.sendBroadcast(updatePageIntent);
        }
    }

    public void launchSearchResultsPage(String searchQuery) {
        if (currentActivity != null) {
            Intent searchIntent = new Intent(currentActivity, AppCMSSearchActivity.class);
            searchIntent.setAction(Intent.ACTION_SEARCH);
            searchIntent.putExtra(SearchManager.QUERY, searchQuery);
            currentActivity.startActivity(searchIntent);
        }
    }

    public void launchResetPasswordPage(String email) {
        if (currentActivity != null) {
            cancelInternalEvents();

            Bundle args = getPageActivityBundle(currentActivity,
                    null,
                    null,
                    currentActivity.getString(R.string.app_cms_reset_password_page_tag),
                    currentActivity.getString(R.string.app_cms_reset_password_page_tag),
                    email,
                    currentActivity.getString(R.string.app_cms_reset_password_page_tag),
                    false,
                    true,
                    false,
                    false,
                    false,
                    null,
                    ExtraScreenType.RESET_PASSWORD);
            Intent updatePageIntent =
                    new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
            updatePageIntent.putExtra(
                    currentActivity.getString(R.string.app_cms_bundle_key),
                    args);
            currentActivity.sendBroadcast(updatePageIntent);
        }
    }

    public void launchEditProfilePage() {
        if (currentActivity != null) {
            cancelInternalEvents();

            Bundle args = getPageActivityBundle(currentActivity,
                    null,
                    null,
                    currentActivity.getString(R.string.app_cms_edit_profile_page_tag),
                    currentActivity.getString(R.string.app_cms_edit_profile_page_tag),
                    null,
                    currentActivity.getString(R.string.app_cms_edit_profile_page_tag),
                    false,
                    true,
                    false,
                    false,
                    false,
                    null,
                    ExtraScreenType.EDIT_PROFILE);
            Intent updatePageIntent =
                    new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
            updatePageIntent.putExtra(
                    currentActivity.getString(R.string.app_cms_bundle_key),
                    args);
            currentActivity.sendBroadcast(updatePageIntent);
        }
    }

    public void loginFacebook() {
        if (currentActivity != null) {
            signupFromFacebook = true;
            LoginManager.getInstance().logInWithReadPermissions(currentActivity,
                    Arrays.asList("public_profile", "user_friends"));
        }
    }

    public void loginGoogle() {
        if (currentActivity != null) {
            isSignupFromGoogle = true;

            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(currentActivity.getString(R.string.default_web_client_id))
                    .build();

            Intent googleIntent = Auth.GoogleSignInApi
                    .getSignInIntent(getGoogleApiClient(googleSignInOptions));
            currentActivity.startActivityForResult(googleIntent, RC_GOOGLE_SIGN_IN);
        }
    }

    private GoogleApiClient getGoogleApiClient(GoogleSignInOptions googleSignInOptions) {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(currentActivity)
                    .enableAutoManage((FragmentActivity) currentActivity,
                            (GoogleApiClient.OnConnectionFailedListener) currentActivity)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .build();
        }
        return googleApiClient;
    }

    public void setInAppBillingService(IInAppBillingService inAppBillingService) {
        this.inAppBillingService = inAppBillingService;
    }

    public void initiateSignUpAndSubscription(String sku,
                                              String planId,
                                              String currency,
                                              String planName,
                                              float planPrice) {
        if (currentActivity != null) {
            launchType = LaunchType.SUBSCRIBE;
            skuToPurchase = sku;
            planToPurchase = planId;
            planToPurchaseName = planName;
            currencyOfPlanToPurchase = currency;
            planToPurchasePrice = planPrice;

            if (isUserLoggedIn(currentActivity)) {
                initiateItemPurchase();
            } else {
                navigateToLoginPage();
            }
        }
    }

    public void initiateItemPurchase() {
        if (currentActivity != null &&
                inAppBillingService != null) {
            try {
                Bundle activeSubs = inAppBillingService.getPurchases(3,
                        currentActivity.getPackageName(),
                        "subs",
                        null);
                ArrayList<String> subscribedSkus = activeSubs.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");

                Bundle buyIntentBundle;
                if (subscribedSkus != null && subscribedSkus.size() > 0) {
                    buyIntentBundle = inAppBillingService.getBuyIntentToReplaceSkus(5,
                            currentActivity.getPackageName(),
                            subscribedSkus,
                            skuToPurchase,
                            "subs",
                            null);
                } else {
                    buyIntentBundle = inAppBillingService.getBuyIntent(3,
                            currentActivity.getPackageName(),
                            skuToPurchase,
                            "subs",
                            null);
                }

                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                if (pendingIntent != null) {
                    currentActivity.startIntentSenderForResult(pendingIntent.getIntentSender(),
                            RC_PURCHASE_PLAY_STORE_ITEM,
                            new Intent(),
                            0,
                            0,
                            0);
                } else {
                    showToast(currentActivity.getString(R.string.app_cms_cancel_subscription_subscription_not_valid_message),
                            Toast.LENGTH_LONG);
                }
            } catch (RemoteException | IntentSender.SendIntentException e) {
                Log.e(TAG, "Failed to purchase item with sku: "
                        + getActiveSubscriptionSku(currentActivity));
            }
        } else {
            Log.e(TAG, "InAppBillingService: " + inAppBillingService);
        }
    }

    public void sendSubscriptionCancellation() {
        if (currentActivity != null) {
            String paymentProcessor = getActiveSubscriptionProcessor(currentActivity);
            if (!TextUtils.isEmpty(paymentProcessor) &&
                    paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly))) {
                Intent googlePlayStoreCancelIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(currentActivity.getString(R.string.google_play_store_subscriptions_url)));
                currentActivity.startActivity(googlePlayStoreCancelIntent);

                if (currentActivity != null) {
                    if (!TextUtils.isEmpty(getActiveSubscriptionSku(currentActivity))) {
                        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
                        subscriptionRequest.setPlatform(currentActivity.getString(R.string.app_cms_subscription_platform_key));
                        subscriptionRequest.setSiteId(currentActivity.getString(R.string.app_cms_app_name));
                        subscriptionRequest.setSubscription(currentActivity.getString(R.string.app_cms_subscription_key));
                        subscriptionRequest.setCurrencyCode(getActiveSubscriptionCurrency(currentActivity));
                        subscriptionRequest.setPlanIdentifier(getActiveSubscriptionSku(currentActivity));
                        subscriptionRequest.setPlanId(getActiveSubscriptionId(currentActivity));
                        subscriptionRequest.setUserId(getLoggedInUser(currentActivity));
                        subscriptionRequest.setReceipt(getActiveSubscriptionReceipt(currentActivity));

                        Log.d(TAG, "Subscription request: " + gson.toJson(subscriptionRequest, SubscriptionRequest.class));

                        try {
                            appCMSSubscriptionPlanCall.call(
                                    currentActivity.getString(R.string.app_cms_cancel_subscription_api_url,
                                            appCMSMain.getApiBaseUrl(),
                                            appCMSMain.getInternalName(),
                                            currentActivity.getString(R.string.app_cms_subscription_platform_key)),
                                    R.string.app_cms_subscription_plan_cancel_key,
                                    subscriptionRequest,
                                    apikey,
                                    getAuthToken(currentActivity),
                                    result -> {
                                    },
                                    appCMSSubscriptionPlanResults -> {
                                        sendCloseOthersAction(null, false);

                                        AppsFlyerUtils.subscriptionEvent(currentActivity,
                                                false,
                                                currentActivity.getString(R.string.app_cms_appsflyer_dev_key),
                                                String.valueOf(getActiveSubscriptionPrice(currentActivity)),
                                                subscriptionRequest.getPlanId(),
                                                subscriptionRequest.getCurrencyCode());
                                    },
                                    currentUserPlan -> {

                                    });
                        } catch (IOException e) {
                            Log.e(TAG, "Failed to update user subscription status");
                        }
                    }
                }
            }
        }
    }

    public void onOrientationChange(boolean landscape) {
        for (Action1<Boolean> onOrientationChangeHandler : onOrientationChangeHandlers) {
            Observable.just(landscape).subscribe(onOrientationChangeHandler);
        }
    }

    public void restrictPortraitOnly() {
        if (currentActivity != null) {
            currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void unrestrictPortraitOnly() {
        if (currentActivity != null) {
            currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public void editWatchlist(final String filmId,
                              final Action1<AppCMSAddToWatchlistResult> resultAction1, boolean add) {

        final String url = currentActivity.getString(R.string.app_cms_edit_watchlist_api_url,
                appCMSMain.getApiBaseUrl(),
                appCMSMain.getInternalName(),
                getLoggedInUser(currentActivity),
                filmId);

        try {
            AddToWatchlistRequest request = new AddToWatchlistRequest();
            request.setUserId(getLoggedInUser(currentActivity));
            request.setContentType(currentActivity.getString(R.string.add_to_watchlist_content_type_video));
            request.setPosition(1L);
            if (add) {
                request.setContentId(filmId);
            } else {
                request.setContentIds(filmId);
            }

            appCMSAddToWatchlistCall.call(url, getAuthToken(currentActivity),
                    addToWatchlistResult -> {
                        try {
                            Observable.just(addToWatchlistResult).subscribe(resultAction1);
                        } catch (Exception e) {
                            Log.e(TAG, "addToWatchlistContent: " + e.toString());
                        }
                    }, request, add);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeDownloadedFile(String filmId, final Action1<UserVideoDownloadStatus> resultAction1) {
        removeDownloadedFile(filmId);

        appCMSUserDownloadVideoStatusCall.call(filmId, this, resultAction1,
                getLoggedInUser(currentActivity));

    }

    public void removeDownloadedFile(String filmId) {
        DownloadVideoRealm downloadVideoRealm = realmController.getDownloadById(filmId);
        if (downloadVideoRealm == null)
            return;
        downloadManager.remove(downloadVideoRealm.getVideoId_DM());
        downloadManager.remove(downloadVideoRealm.getVideoThumbId_DM());
        downloadManager.remove(downloadVideoRealm.getPosterThumbId_DM());
        downloadManager.remove(downloadVideoRealm.getSubtitlesId_DM());
        realmController.removeFromDB(downloadVideoRealm);
    }


    /**
     * This function will be called in two cases
     * 1) When 1st time downloading start
     * 2) From settings option any time
     *
     * @param contentDatum  pass null from setting screen button / This value could be usefull
     *                      in future we are going to implement the MPEG rendition quality
     *                      dynamically.
     * @param resultAction1 pass null from setting screen button
     */
    public void showDownloadQualityScreen(final ContentDatum contentDatum,
                                          final Action1<UserVideoDownloadStatus> resultAction1) {

        AppCMSPageAPI apiData = new AppCMSPageAPI();
        List<Module> moduleList = new ArrayList<>();
        Module module = new Module();

        getUserDownloadQualityPref(currentActivity);

        List<ContentDatum> contentData = new ArrayList<>();
        ContentDatum contentDatumLocal = new ContentDatum();
        StreamingInfo streamingInfo = new StreamingInfo();
        VideoAssets videoAssets = new VideoAssets();
        List<Mpeg> mpegs = new ArrayList<>();

        String renditionValueArray[] = {"360p", "720p", "1080p"};
        for (String renditionValue : renditionValueArray) {
            Mpeg mpeg = new Mpeg();
            mpeg.setRenditionValue(renditionValue);
            mpegs.add(mpeg);
        }

        videoAssets.setMpeg(mpegs);
        videoAssets.setType("videoAssets");

        streamingInfo.setVideoAssets(videoAssets);
        contentDatumLocal.setStreamingInfo(streamingInfo);

        contentData.add(contentDatumLocal);
        module.setContentData(contentData);

        moduleList.add(module);
        apiData.setModules(moduleList);

        launchDownloadQualityActivity(currentActivity,
                navigationPages.get(downloadQualityPage.getPageId()),
                apiData,
                downloadQualityPage.getPageId(),
                downloadQualityPage.getPageName(),
                pageIdToPageNameMap.get(downloadQualityPage.getPageId()),
                loadFromFile,
                false,
                true,
                false,
                false,
                getAppCMSDownloadQualityBinder(currentActivity,
                        navigationPages.get(downloadQualityPage.getPageId()),
                        apiData,
                        downloadQualityPage.getPageId(),
                        downloadQualityPage.getPageName(),
                        downloadQualityPage.getPageName(),
                        loadFromFile,
                        true,
                        true,
                        false,
                        contentDatum, resultAction1));
    }

    /**
     * Implementation of download manager gives us facility of Async downloading of multiple video
     * Its pre built feature of the download manager
     * <p>
     * <ul>
     * <li>Implementing pause download will require custominzation in download process </li>
     * <li>Same goes for resume download </li>
     * </ul>
     * <p>
     * Videos will be stored in Downloads folder under our app dir by this way our apps video
     * will not be visible to other media app
     * <p>
     * In Future development we may try to add feature like encryption of the video.
     *
     * @param contentDatum
     * @param resultAction1
     * @param add           In future development this is need to change in Enum as we may perform options Add/Pause/Resume/Delete from here onwards
     */

    public void editDownload(final ContentDatum contentDatum,
                             final Action1<UserVideoDownloadStatus> resultAction1, boolean add) {

        long enqueueId = 0L;
        long thumbEnqueueId = downloadVideoImage(contentDatum.getGist().getVideoImageUrl(),
                contentDatum.getGist().getId());
        long posterEnqueueId = downloadPosterImage(contentDatum.getGist().getPosterImageUrl(),
                contentDatum.getGist().getId());
        long ccEnqueueId = 0L;
        if (contentDatum.getContentDetails() != null &&
                contentDatum.getContentDetails().getClosedCaptions() != null &&
                contentDatum.getContentDetails().getClosedCaptions().size() > 0 &&
                contentDatum.getContentDetails().getClosedCaptions().get(0).getUrl() != null) {
            ccEnqueueId = downloadVideoSubtitles(contentDatum.getContentDetails()
                    .getClosedCaptions().get(0).getUrl(), contentDatum.getGist().getId());
        }

        cancelDownloadIconTimerTask();

        try {
            String downloadURL;

            int bitrate = contentDatum.getStreamingInfo().getVideoAssets().getMpeg().get(0).getBitrate();

            String downloadQualityRendition = getUserDownloadQualityPref(currentActivity);
            Map<String, String> urlRenditionMap = new HashMap<>();
            for (Mpeg mpeg : contentDatum.getStreamingInfo().getVideoAssets().getMpeg()) {
                urlRenditionMap.put(mpeg.getRenditionValue().replace("_", "").trim(),
                        mpeg.getUrl());
            }
            downloadURL = urlRenditionMap.get(downloadQualityRendition);

            if (downloadQualityRendition != null) {
                if (downloadURL == null && downloadQualityRendition.contains("360")) {
                    if (urlRenditionMap.get("720p") != null) {
                        downloadURL = urlRenditionMap.get("720p");
                    } else if (urlRenditionMap.get("1080p") != null) {
                        downloadURL = urlRenditionMap.get("1080p");
                    }
                } else if (downloadURL == null && downloadQualityRendition.contains("720")) {
                    if (urlRenditionMap.get("360p") != null) {
                        downloadURL = urlRenditionMap.get("360p");
                    } else if (urlRenditionMap.get("1080p") != null) {
                        downloadURL = urlRenditionMap.get("1080p");
                    }

                } else if (downloadURL == null && downloadQualityRendition.contains("1080")) {

                    if (urlRenditionMap.get("720p") != null) {
                        downloadURL = urlRenditionMap.get("720p");
                    } else if (urlRenditionMap.get("360p") != null) {
                        downloadURL = urlRenditionMap.get("360p");
                    }
                } else if (downloadURL == null) {
                    //noinspection SuspiciousMethodCalls
                    downloadURL = urlRenditionMap.get(urlRenditionMap.keySet().toArray()[0]);
                }
            } else {
                downloadURL = contentDatum.getStreamingInfo().getVideoAssets().getMpeg().get(0).getUrl();
            }

            downloadURL = downloadURL.replace("https:/", "http:/");

            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadURL.replace(" ", "%20")))
                    .setTitle(contentDatum.getGist().getTitle())
                    .setDescription(contentDatum.getGist().getDescription())
                    .setAllowedOverRoaming(false)
                    .setDestinationInExternalFilesDir(currentActivity, Environment.DIRECTORY_DOWNLOADS,
                            contentDatum.getGist().getId() + MEDIA_SURFIX_MP4)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setVisibleInDownloadsUi(false)
                    .setShowRunningNotification(true);

            enqueueId = downloadManager.enqueue(downloadRequest);

            /*
             * Inserting data in realm data object
             */

            createLocalEntry(
                    enqueueId,
                    thumbEnqueueId,
                    posterEnqueueId,
                    ccEnqueueId,
                    contentDatum,
                    downloadURL);

            showToast(
                    currentActivity.getString(R.string.app_cms_download_started_mesage,
                            contentDatum.getGist().getTitle()), Toast.LENGTH_LONG);

        } catch (Exception e) {
            showDialog(DialogType.DOWNLOAD_INCOMPLETE, e.getMessage(), false, null);
        } finally {
            appCMSUserDownloadVideoStatusCall.call(contentDatum.getGist().getId(), this,
                    resultAction1, getLoggedInUser(currentActivity));
        }
    }

    public void createLocalEntry(long enqueueId,
                                 long thumbEnqueueId,
                                 long posterEnqueueId,
                                 long ccEnqueueId,
                                 ContentDatum contentDatum,
                                 String downloadURL) {
        DownloadVideoRealm downloadVideoRealm = new DownloadVideoRealm();
        downloadVideoRealm.setVideoThumbId_DM(thumbEnqueueId);
        downloadVideoRealm.setPosterThumbId_DM(posterEnqueueId);
        downloadVideoRealm.setVideoId_DM(enqueueId);

        downloadVideoRealm.setVideoId(contentDatum.getGist().getId());
        downloadVideoRealm.setVideoTitle(contentDatum.getGist().getTitle());
        downloadVideoRealm.setVideoDescription(contentDatum.getGist().getDescription());
        downloadVideoRealm.setLocalURI(downloadedMediaLocalURI(enqueueId));
        downloadVideoRealm.setVideoImageUrl(getPngPosterPath(contentDatum.getGist().getId()));
        downloadVideoRealm.setPosterFileURL(getPngPosterPath(contentDatum.getGist().getId()));
        if (ccEnqueueId != 0) {
            downloadVideoRealm.setSubtitlesId_DM(ccEnqueueId);
            downloadVideoRealm.setSubtitlesFileURL(getPngPosterPath(contentDatum.getGist().getId()));
        }

        downloadVideoRealm.setVideoFileURL(contentDatum.getGist().getVideoImageUrl()); //This change has been done due to making thumb image available at time of videos are downloading.
        downloadVideoRealm.setVideoWebURL(downloadURL);
        downloadVideoRealm.setDownloadDate(System.currentTimeMillis());
        downloadVideoRealm.setVideoDuration(contentDatum.getGist().getRuntime());

        downloadVideoRealm.setPermalink(contentDatum.getGist().getPermalink());
        downloadVideoRealm.setDownloadStatus(DownloadStatus.STATUS_PENDING);
        downloadVideoRealm.setUserId(getLoggedInUser(currentActivity));

        realmController.addDownload(downloadVideoRealm);
    }

    public void createSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        realmController.addSubscriptionPlan(subscriptionPlan);
    }

    public List<SubscriptionPlan> getExistingSubscriptionPlans() {
        List<SubscriptionPlan> subscriptionPlans = new ArrayList<>();
        RealmResults<SubscriptionPlan> subscriptionPlanRealmResults = realmController.getAllSubscriptionPlans();
        subscriptionPlans.addAll(subscriptionPlanRealmResults);
        return subscriptionPlans;
    }

    /**
     * Created separate method for initiating downloading images as I was facing trouble in
     * initiating tow downloads in same method
     * <p>
     * By this way our Image will store in app dir under "thumbs" folder and it will not be visible
     * to the other apps
     *
     * @param downloadURL
     * @param filename
     */
    public long downloadVideoImage(String downloadURL, String filename) {

        long enqueueId = 0L;
        try {

            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadURL))
                    .setTitle(filename)
                    .setDescription(filename)
                    .setAllowedOverRoaming(false)
                    .setDestinationInExternalFilesDir(currentActivity, "thumbs", filename + MEDIA_SURFIX_JPG)
                    .setVisibleInDownloadsUi(false)
                    .setShowRunningNotification(true);
            enqueueId = downloadManager.enqueue(downloadRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return enqueueId;
    }

    /**
     * Created separate method for initiating downloading images as I was facing trouble in
     * initiating tow downloads in same method
     * <p>
     * By this way our Image will store in app dir under "thumbs" folder and it will not be visible
     * to the other apps
     *
     * @param downloadURL
     * @param filename
     */
    public long downloadPosterImage(String downloadURL, String filename) {

        long enqueueId = 0L;
        try {

            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadURL))
                    .setTitle(filename)
                    .setDescription(filename)
                    .setAllowedOverRoaming(false)
                    .setDestinationInExternalFilesDir(currentActivity, "posters", filename + MEDIA_SURFIX_JPG)
                    .setVisibleInDownloadsUi(false)
                    .setShowRunningNotification(true);
            enqueueId = downloadManager.enqueue(downloadRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return enqueueId;
    }

    public long downloadVideoSubtitles(String downloadURL, String filename) {

        long enqueueId = 0L;
        try {

            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadURL))
                    .setTitle(filename)
                    .setDescription(filename)
                    .setAllowedOverRoaming(false)
                    .setDestinationInExternalFilesDir(currentActivity, "closedCaptions", filename + MEDIA_SUFFIX_SRT)
                    .setVisibleInDownloadsUi(false)
                    .setShowRunningNotification(true);
            enqueueId = downloadManager.enqueue(downloadRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return enqueueId;
    }

    public String downloadedMediaLocalURI(long enqueueId) {
        String uriLocal = currentActivity.getString(R.string.download_file_prefix);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(enqueueId);
        Cursor cursor = downloadManager.query(query);
        if (enqueueId != 0L && cursor != null && cursor.moveToFirst()) {
            uriLocal = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
        }
        cursor.close();
        return uriLocal == null ? "data" : uriLocal;
    }

    public String getDownloadedFileSize(String filmId) {

        DownloadVideoRealm downloadVideoRealm = realmController.getDownloadById(filmId);
        if (downloadVideoRealm == null)
            return "";
        return getDownloadedFileSize(downloadVideoRealm.getVideoSize());
    }


    public DownloadVideoRealm getDownloadedVideo(String videoId) {
        return realmController.getDownloadByIdBelongstoUser(videoId, getLoggedInUser(currentActivity));
    }
    public boolean isVideoDownloaded(String videoId){
        DownloadVideoRealm downloadVideoRealm= realmController.getDownloadByIdBelongstoUser(videoId,getLoggedInUser(currentActivity));
        if (downloadVideoRealm!=null && downloadVideoRealm.getVideoId().equalsIgnoreCase(videoId)) {
            return true;
        }

        return false;
    }
    public String getDownloadedFileSize(long size) {
        String fileSize;
        DecimalFormat dec = new DecimalFormat("0");

        long sizeKB = (size / 1024);
        double megaByte = sizeKB / 1024.0;
        double gigaByte = sizeKB / 1048576.0;
        double teraByte = sizeKB / 1073741824.0;

        if (teraByte > 1) {
            fileSize = dec.format(teraByte).concat("TB");
        } else if (gigaByte > 1) {
            fileSize = dec.format(gigaByte).concat("GB");
        } else if (megaByte > 1) {
            fileSize = dec.format(megaByte).concat("MB");
        } else {
            fileSize = dec.format(sizeKB).concat("KB");
        }

        return fileSize;
    }

    public synchronized int downloadedPercentage(long videoId) {
        int downloadPercent = 0;
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(videoId);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            downloaded = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            long totalSize = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            long downloaded = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            downloadPercent = (int) (downloaded * 100.0 / totalSize + 0.5);

        }

        c.close();
        return downloadPercent;
    }

    public synchronized void updateDownloadingStatus(String filmId, final ImageView imageView,
                                                     AppCMSPresenter presenter,
                                                     final Action1<UserVideoDownloadStatus> responseAction,
                                                     String userId) {
        long videoId = -1L;
        cancelDownloadIconTimerTask();
        try {
            videoId = realmController.getDownloadByIdBelongstoUser(filmId, userId).getVideoId_DM();
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(videoId);

            /*
             * Timer code can be optimize with RxJava code
             */
            runUpdateDownloadIconTimer = true;
            updateDownloadIconTimer = new Timer();
            updateDownloadIconTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Cursor c = downloadManager.query(query);
                    if (c != null && c.moveToFirst()) {
                        downloaded = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        long totalSize = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        long downloaded = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int downloadPercent = (int) (downloaded * 100.0 / totalSize + 0.5);
                        Log.d(TAG, "download progress =" + downloaded + " total-> " + totalSize + " " + downloadPercent);
                        if (downloaded >= totalSize || downloadPercent > 100) {
                            if (currentActivity != null && isUserLoggedIn(currentActivity))
                                currentActivity.runOnUiThread(() -> appCMSUserDownloadVideoStatusCall
                                        .call(filmId, presenter, responseAction, getLoggedInUser(currentActivity)));
                            this.cancel();
                        } else {
                            if (currentActivity != null && runUpdateDownloadIconTimer)
                                currentActivity.runOnUiThread(() -> circularImageBar(imageView, downloadPercent));
                        }
                        c.close();
                    }
                }
            }, 500, 1000);
        } catch (Exception e) {
            Log.e(TAG, "Could not find video ID in downloads");
        }
    }

    public void cancelDownloadIconTimerTask() {
        if (updateDownloadIconTimer != null) {
            runUpdateDownloadIconTimer = false;
            updateDownloadIconTimer.cancel();
            updateDownloadIconTimer.purge();
        }
    }

    private void circularImageBar(ImageView iv2, int i) {
        if (runUpdateDownloadIconTimer) {
            Bitmap b = Bitmap.createBitmap(iv2.getWidth(), iv2.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b);
            Paint paint = new Paint();

            paint.setColor(Color.DKGRAY);
            paint.setStrokeWidth(iv2.getWidth() / 10);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(iv2.getWidth() / 2, iv2.getHeight() / 2, (iv2.getWidth() / 2) - 2, paint);

            int tintColor = Color.parseColor((this.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()));
            paint.setColor(tintColor);
            paint.setStrokeWidth(iv2.getWidth() / 10);
            paint.setStyle(Paint.Style.FILL);
            final RectF oval = new RectF();
            paint.setStyle(Paint.Style.STROKE);
            oval.set(2, 2, iv2.getWidth() - 2, iv2.getHeight() - 2);
            canvas.drawArc(oval, 270, ((i * 360) / 100), false, paint);


            iv2.setImageBitmap(b);
        }
    }

    public void editHistory(final String filmId,
                            final Action1<AppCMSDeleteHistoryResult> resultAction1, boolean post) {
        final String url = currentActivity.getString(R.string.app_cms_edit_history_api_url,
                appCMSMain.getApiBaseUrl(),
                getLoggedInUser(currentActivity),
                appCMSMain.getInternalName(),
                filmId);

        try {
            DeleteHistoryRequest request = new DeleteHistoryRequest();
            request.setUserId(getLoggedInUser(currentActivity));
            request.setContentType(currentActivity.getString(R.string.delete_history_content_type_video));
            request.setPosition(1L);
            if (post) {
                request.setContentId(filmId);
            } else {
                request.setContentIds(filmId);
            }

            appCMSDeleteHistoryCall.call(url, getAuthToken(currentActivity),
                    appCMSDeleteHistoryResult -> {
                        try {
                            Observable.just(appCMSDeleteHistoryResult).subscribe(resultAction1);
                        } catch (Exception e) {
                            Log.e(TAG, "deleteHistoryContent: " + e.toString());
                        }
                    }, request, post);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearDownload(final Action1<UserVideoDownloadStatus> resultAction1) {
        for (DownloadVideoRealm downloadVideoRealm :
                realmController.getDownloadesByUserId(getLoggedInUser(currentActivity))) {
            removeDownloadedFile(downloadVideoRealm.getVideoId());
        }

        appCMSUserDownloadVideoStatusCall.call("", this, resultAction1, getLoggedInUser(currentActivity));
    }

    public void clearWatchlist(final Action1<AppCMSAddToWatchlistResult> resultAction1) {
        final String url = currentActivity.getString(R.string.app_cms_clear_watchlist_api_url,
                appCMSMain.getApiBaseUrl(),
                appCMSMain.getInternalName(),
                getLoggedInUser(currentActivity));

        try {
            AddToWatchlistRequest request = new AddToWatchlistRequest();
            request.setUserId(getLoggedInUser(currentActivity));
            request.setContentType(currentActivity.getString(R.string.add_to_watchlist_content_type_video));
            request.setPosition(1L);
            appCMSAddToWatchlistCall.call(url, getAuthToken(currentActivity),
                    addToWatchlistResult -> {
                        try {
                            Observable.just(addToWatchlistResult).subscribe(resultAction1);
                        } catch (Exception e) {
                            Log.e(TAG, "clearWatchlistContent: " + e.toString());
                        }
                    }, request, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateToDownloadPage(String pageId, String pageTitle, String url,
                                       boolean launchActivity) {
        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {
            showMainFragmentView(false);

            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
            appCMSPageAPI.setId(pageId);

            List<Module> moduleList = new ArrayList<>();
            Module module = new Module();

            Settings settings = new Settings();

            settings.setHideDate(true);
            settings.setHideTitle(false);
            settings.setLazyLoad(false);

            List<ContentDatum> contentData = new ArrayList<>();
            for (DownloadVideoRealm downloadVideoRealm : realmController.getDownloadesByUserId(getLoggedInUser(currentActivity))) {
                contentData.add(downloadVideoRealm.convertToContentDatum(getLoggedInUser(currentActivity)));
            }
            module.setContentData(contentData);
            module.setTitle(currentActivity.getString(R.string.app_cms_page_download_title));
            moduleList.add(module);
            appCMSPageAPI.setModules(moduleList);

            cancelInternalEvents();
            pushActionInternalEvents(pageId
                    + BaseView.isLandscape(currentActivity));
            navigationPageData.put(pageId, appCMSPageAPI);

            boolean loadingHistory = false;
            if (isUserLoggedIn(currentActivity)) {
                for (Module module1 : appCMSPageAPI.getModules()) {
                    if (jsonValueKeyMap.get(module1.getModuleType()) ==
                            AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY) {
                        if (module1.getContentData() != null &&
                                module1.getContentData().size() > 0) {
                            loadingHistory = true;
                            getHistoryData(appCMSHistoryResult -> {
                                if (appCMSHistoryResult != null) {
                                    AppCMSPageAPI historyAPI =
                                            appCMSHistoryResult.convertToAppCMSPageAPI(appCMSPageAPI.getId());
                                    historyAPI.getModules().get(0).setId(module1.getId());
                                    mergeData(historyAPI, appCMSPageAPI);

                                    cancelInternalEvents();
                                    pushActionInternalEvents(pageId
                                            + BaseView.isLandscape(currentActivity));
                                    navigationPageData.put(pageId, appCMSPageAPI);
                                    if (launchActivity) {
                                        launchPageActivity(currentActivity,
                                                appCMSPageUI,
                                                appCMSPageAPI,
                                                pageId,
                                                pageTitle,
                                                pageTitle,
                                                pageIdToPageNameMap.get(pageId),
                                                loadFromFile,
                                                false,
                                                false,
                                                true,
                                                false,
                                                null,
                                                ExtraScreenType.NONE);
                                    } else {
                                        Bundle args = getPageActivityBundle(currentActivity,
                                                appCMSPageUI,
                                                appCMSPageAPI,
                                                pageId,
                                                pageTitle,
                                                pageTitle,
                                                pageIdToPageNameMap.get(pageId),
                                                loadFromFile,
                                                false,
                                                false,
                                                true,
                                                false,
                                                null,
                                                ExtraScreenType.NONE);
                                        Intent updatePageIntent =
                                                new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                        updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                                args);
                                        currentActivity.sendBroadcast(updatePageIntent);
                                        dismissOpenDialogs(null);
                                    }
                                }
                            });
                        }
                    }
                }
            }

            if (!loadingHistory) {
                if (launchActivity) {
                    launchPageActivity(currentActivity,
                            appCMSPageUI,
                            appCMSPageAPI,
                            pageId,
                            pageTitle,
                            pageIdToPageNameMap.get(pageId),
                            pageTitle,
                            loadFromFile,
                            false,
                            false,
                            true,
                            false,
                            null,
                            ExtraScreenType.NONE);
                } else {
                    Bundle args = getPageActivityBundle(currentActivity,
                            appCMSPageUI,
                            appCMSPageAPI,
                            pageId,
                            "My Downloads",
                            pageIdToPageNameMap.get(pageId),
                            pageTitle,
                            loadFromFile,
                            false,
                            false,
                            true,
                            false,
                            null,
                            ExtraScreenType.NONE);

                    Intent downloadPageIntent =
                            new Intent(AppCMSPresenter
                                    .PRESENTER_NAVIGATE_ACTION);
                    downloadPageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key), args);
                    currentActivity.sendBroadcast(downloadPageIntent);
                }
            }
        }
    }

    public void clearHistory(final Action1<AppCMSDeleteHistoryResult> resultAction1) {
        final String url = currentActivity.getString(R.string.app_cms_clear_history_api_url,
                appCMSMain.getApiBaseUrl(),
                getLoggedInUser(currentActivity),
                appCMSMain.getInternalName());

        try {
            DeleteHistoryRequest request = new DeleteHistoryRequest();
            request.setUserId(getLoggedInUser(currentActivity));
            request.setContentType(currentActivity.getString(R.string.delete_history_content_type_video));
            request.setPosition(1L);
            appCMSDeleteHistoryCall.call(url, getAuthToken(currentActivity),
                    appCMSDeleteHistoryResult -> {
                        try {
                            Observable.just(appCMSDeleteHistoryResult).subscribe(resultAction1);
                        } catch (Exception e) {
                            Log.e(TAG, "clearHistoryContent: " + e.toString());
                        }
                    }, request, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateToWatchlistPage(String pageId, String pageTitle, String url,
                                        boolean launchActivity) {

        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {
            showMainFragmentView(false);

            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            getWatchlistPageContent(appCMSMain.getApiBaseUrl(),
                    pageIdToPageAPIUrlMap.get(pageId),
                    appCMSMain.getInternalName(),
                    true,
                    getPageId(appCMSPageUI), new AppCMSWatchlistAPIAction(false,
                            false,
                            true,
                            appCMSPageUI,
                            pageId,
                            pageId,
                            pageTitle,
                            pageId,
                            launchActivity, null) {
                        @Override
                        public void call(AppCMSWatchlistResult appCMSWatchlistResult) {
                            cancelInternalEvents();
                            pushActionInternalEvents(this.pageId
                                    + BaseView.isLandscape(currentActivity));

                            AppCMSPageAPI pageAPI = null;
                            if (appCMSWatchlistResult != null) {
                                pageAPI = appCMSWatchlistResult.convertToAppCMSPageAPI(this.pageId);
                            } else {
                                pageAPI = new AppCMSPageAPI();
                                pageAPI.setId(this.pageId);
                                List<String> moduleIds = new ArrayList<>();
                                List<Module> apiModules = new ArrayList<>();
                                for (ModuleList module : appCMSPageUI.getModuleList()) {
                                    Module module1 = new Module();
                                    module1.setId(module.getId());
                                    apiModules.add(module1);
                                    moduleIds.add(module.getId());
                                }
                                pageAPI.setModuleIds(moduleIds);
                                pageAPI.setModules(apiModules);
                            }

                            navigationPageData.put(this.pageId, pageAPI);

                            if (this.launchActivity) {
                                launchPageActivity(currentActivity,
                                        this.appCMSPageUI,
                                        pageAPI,
                                        this.pageId,
                                        this.pageTitle,
                                        this.pagePath,
                                        pageIdToPageNameMap.get(this.pageId),
                                        loadFromFile,
                                        this.appbarPresent,
                                        this.fullscreenEnabled,
                                        this.navbarPresent,
                                        false,
                                        this.searchQuery,
                                        ExtraScreenType.NONE);
                            } else {
                                Bundle args = getPageActivityBundle(currentActivity,
                                        this.appCMSPageUI,
                                        pageAPI,
                                        this.pageId,
                                        this.pageTitle,
                                        this.pagePath,
                                        pageIdToPageNameMap.get(this.pageId),
                                        loadFromFile,
                                        this.appbarPresent,
                                        this.fullscreenEnabled,
                                        this.navbarPresent,
                                        false,
                                        null,
                                        ExtraScreenType.NONE);
                                Intent watchlistPageIntent =
                                        new Intent(AppCMSPresenter
                                                .PRESENTER_NAVIGATE_ACTION);
                                watchlistPageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                        args);
                                currentActivity.sendBroadcast(watchlistPageIntent);
                            }
                        }
                    });
        }
    }

    /**
     * Method launches the autoplay screen
     *
     * @param pageId    pageId to get the Page UI from navigationPages
     * @param pageTitle pageTitle
     * @param url       url of the API which gets the VideoDetails
     * @param binder    binder to share data
     */
    public void navigateToAutoplayPage(final String pageId,
                                       final String pageTitle,
                                       String url,
                                       final AppCMSVideoPageBinder binder) {

        if (currentActivity != null) {
            final AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            if (!binder.isOffline()) {
                GetAppCMSVideoDetailAsyncTask.Params params =
                        new GetAppCMSVideoDetailAsyncTask.Params.Builder().url(url)
                                .authToken(getAuthToken(currentActivity)).build();
                new GetAppCMSVideoDetailAsyncTask(appCMSVideoDetailCall,
                        appCMSVideoDetail -> {
                            if (appCMSVideoDetail != null) {
                                binder.setContentData(appCMSVideoDetail.getRecords().get(0));
                                AppCMSPageAPI pageAPI = null;
                                for (ModuleList moduleList :
                                        appCMSPageUI.getModuleList()) {
                                    if (moduleList.getType().equals(currentActivity
                                            .getString(R.string.app_cms_page_autoplay_module_key))) {
                                        pageAPI = appCMSVideoDetail.convertToAppCMSPageAPI(pageId,
                                                moduleList.getType());
                                        break;
                                    }
                                }
                                if (pageAPI != null) {
                                    launchAutoplayActivity(currentActivity,
                                            appCMSPageUI,
                                            pageAPI,
                                            pageId,
                                            pageTitle,
                                            pageIdToPageNameMap.get(pageId),
                                            loadFromFile,
                                            false,
                                            true,
                                            false,
                                            false,
                                            binder);
                                }
                            } else {
                                Log.e(TAG, "API issue in VideoDetail call");
                            }
                        }).execute(params);
            } else {
                AppCMSPageAPI pageAPI = binder.getContentData().convertToAppCMSPageAPI();

                if (pageAPI != null) {
                    launchAutoplayActivity(currentActivity,
                            appCMSPageUI,
                            pageAPI,
                            pageId,
                            pageTitle,
                            pageIdToPageNameMap.get(pageId),
                            loadFromFile,
                            false,
                            true,
                            false,
                            false,
                            binder);
                }
            }
        }
    }

    private void getWatchlistPageContent(final String apiBaseUrl, String endPoint,
                                         final String siteId,
                                         boolean userPageIdQueryParam, String pageId,
                                         final AppCMSWatchlistAPIAction watchlist) {
        String url = currentActivity.getString(R.string.app_cms_refresh_identity_api_url,
                appCMSMain.getApiBaseUrl(),
                getRefreshToken(currentActivity));

        appCMSRefreshIdentityCall.call(url, refreshIdentityResponse -> {
            try {
                appCMSWatchlistCall.call(
                        currentActivity.getString(R.string.app_cms_watchlist_api_url,
                                apiBaseUrl, //getLoggedInUser(currentActivity,
                                siteId,
                                getLoggedInUser(currentActivity)),
                        getAuthToken(currentActivity),
                        watchlist);
            } catch (IOException e) {
                Log.e(TAG, "getWatchlistPageContent: " + e.toString());
            }
        });
    }

    public void getHistoryData(final Action1<AppCMSHistoryResult> appCMSHistoryResultAction) {
        if (currentActivity != null) {
            MetaPage historyMetaPage = actionTypeToMetaPageMap.get(AppCMSActionType.HISTORY_PAGE);
            AppCMSPageUI appCMSPageUI = navigationPages.get(historyMetaPage.getPageId());
            getHistoryPageContent(appCMSMain.getApiBaseUrl(),
                    historyMetaPage.getPageAPI(),
                    appCMSMain.getInternalName(),
                    true,
                    getPageId(appCMSPageUI),
                    new AppCMSHistoryAPIAction(true,
                            false,
                            true,
                            appCMSPageUI,
                            historyMetaPage.getPageId(),
                            historyMetaPage.getPageId(),
                            historyMetaPage.getPageName(),
                            historyMetaPage.getPageId(),
                            false,
                            null) {
                        @Override
                        public void call(AppCMSHistoryResult appCMSHistoryResult) {
                            if (appCMSHistoryResult != null) {
                                Observable.just(appCMSHistoryResult).subscribe(appCMSHistoryResultAction);
                            } else {
                                Observable.just((AppCMSHistoryResult) null).subscribe(appCMSHistoryResultAction);
                            }
                        }
                    });
        }
    }

    public void navigateToHistoryPage(String pageId, String pageTitle, String url,
                                      boolean launchActivity) {

        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {
            showMainFragmentView(false);
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            getHistoryPageContent(appCMSMain.getApiBaseUrl(),
                    pageIdToPageAPIUrlMap.get(pageId),
                    appCMSMain.getInternalName(),
                    true,
                    getPageId(appCMSPageUI), new AppCMSHistoryAPIAction(false,
                            false,
                            true,
                            appCMSPageUI,
                            pageId,
                            pageId,
                            pageTitle,
                            pageId,
                            launchActivity, null) {
                        @Override
                        public void call(AppCMSHistoryResult appCMSHistoryResult) {
                            cancelInternalEvents();
                            pushActionInternalEvents(this.pageId
                                    + BaseView.isLandscape(currentActivity));

                            AppCMSPageAPI pageAPI;
                            if (appCMSHistoryResult != null &&
                                    appCMSHistoryResult.getRecords() != null) {
                                pageAPI = appCMSHistoryResult.convertToAppCMSPageAPI(this.pageId);
                            } else {
                                pageAPI = new AppCMSPageAPI();
                                pageAPI.setId(this.pageId);
                                List<String> moduleIds = new ArrayList<>();
                                List<Module> apiModules = new ArrayList<>();
                                for (ModuleList module : appCMSPageUI.getModuleList()) {
                                    Module module1 = new Module();
                                    module1.setId(module.getId());
                                    apiModules.add(module1);
                                    moduleIds.add(module.getId());
                                }
                                pageAPI.setModuleIds(moduleIds);
                                pageAPI.setModules(apiModules);
                            }

                            navigationPageData.put(this.pageId, pageAPI);

                            if (this.launchActivity) {
                                launchPageActivity(currentActivity,
                                        this.appCMSPageUI,
                                        pageAPI,
                                        this.pageId,
                                        this.pageTitle,
                                        this.pagePath,
                                        pageIdToPageNameMap.get(this.pageId),
                                        loadFromFile,
                                        this.appbarPresent,
                                        this.fullscreenEnabled,
                                        this.navbarPresent,
                                        false,
                                        this.searchQuery,
                                        ExtraScreenType.NONE);
                            } else {
                                Bundle args = getPageActivityBundle(currentActivity,
                                        this.appCMSPageUI,
                                        pageAPI,
                                        this.pageId,
                                        this.pageTitle,
                                        this.pagePath,
                                        pageIdToPageNameMap.get(this.pageId),
                                        loadFromFile,
                                        this.appbarPresent,
                                        this.fullscreenEnabled,
                                        this.navbarPresent,
                                        false,
                                        null,
                                        ExtraScreenType.NONE);

                                Intent historyPageIntent =
                                        new Intent(AppCMSPresenter
                                                .PRESENTER_NAVIGATE_ACTION);
                                historyPageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                        args);
                                currentActivity.sendBroadcast(historyPageIntent);
                            }
                        }
                    });
        }
    }

    private void getHistoryPageContent(final String apiBaseUrl, String endPoint, final String siteiD,
                                       boolean userPageIdQueryParam, String pageId,
                                       final AppCMSHistoryAPIAction history) {

        if (shouldRefreshAuthToken()) {
            callRefreshIdentity(() -> {
                try {
                    appCMSHistoryCall.call(currentActivity.getString(R.string.app_cms_history_api_url,
                            apiBaseUrl, getLoggedInUser(currentActivity), siteiD,
                            getLoggedInUser(currentActivity)),
                            getAuthToken(currentActivity),
                            history);
                } catch (IOException e) {
                    Log.e(TAG, "getHistoryPageContent: " + e.toString());
                }
            });
        } else {

            String url = currentActivity.getString(R.string.app_cms_refresh_identity_api_url,
                    appCMSMain.getApiBaseUrl(),
                    getRefreshToken(currentActivity));

            appCMSRefreshIdentityCall.call(url, refreshIdentityResponse -> {
                try {
                    appCMSHistoryCall.call(currentActivity.getString(R.string.app_cms_history_api_url,
                            apiBaseUrl, getLoggedInUser(currentActivity), siteiD,
                            getLoggedInUser(currentActivity)),
                            getAuthToken(currentActivity),
                            history);
                } catch (IOException e) {
                    Log.e(TAG, "getHistoryPageContent: " + e.toString());
                }
            });
        }
    }

    public void navigateToSubscriptionPlansPage(String previousPageId, String previousPageName) {
        if (subscriptionPage != null) {
            launchType = LaunchType.SUBSCRIBE;
            boolean launchSuccess = navigateToPage(subscriptionPage.getPageId(),
                    subscriptionPage.getPageName(),
                    subscriptionPage.getPageUI(),
                    false,
                    true,
                    false,
                    false,
                    false,
                    deeplinkSearchQuery);
            if (!TextUtils.isEmpty(previousPageId) &&
                    !TextUtils.isEmpty(previousPageName)) {
                checkForExistingSubscription(previousPageId, previousPageName);
            }

            if (!launchSuccess) {
                Log.e(TAG, "Failed to launch page: " + subscriptionPage.getPageName());
                launchErrorActivity(currentActivity, platformType);
            }
        }
    }

    public void checkForExistingSubscription(String previousPageId, String previousPageName) {
        if (currentActivity != null) {
            Bundle activeSubs = null;
            try {
                if (inAppBillingService != null) {
                    activeSubs = inAppBillingService.getPurchases(3,
                            currentActivity.getPackageName(),
                            "subs",
                            null);
                    ArrayList<String> subscribedSkus = activeSubs.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                    if (subscribedSkus != null && subscribedSkus.size() > 0) {
                        launchNavigationPage();
                    }
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to purchase item with sku: "
                        + getActiveSubscriptionSku(currentActivity));
            }
        }
    }

    public void navigateToHomePage() {
        if (homePage != null) {
            restartInternalEvents();
            navigateToPage(homePage.getPageId(),
                    homePage.getPageName(),
                    homePage.getPageUI(),
                    false,
                    true,
                    false,
                    true,
                    true,
                    deeplinkSearchQuery);
        }
    }

    public void navigateToLoginPage() {
        if (loginPage != null) {
            boolean launchSuccess = navigateToPage(loginPage.getPageId(),
                    loginPage.getPageName(),
                    loginPage.getPageUI(),
                    false,
                    true,
                    false,
                    false,
                    false,
                    deeplinkSearchQuery);
            if (!launchSuccess) {
                Log.e(TAG, "Failed to launch page: " + loginPage.getPageName());
                launchErrorActivity(currentActivity, platformType);
            }
        }
    }

    public void resetPassword(final String email) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_forgot_password_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSMain.getInternalName());
            appCMSResetPasswordCall.call(url,
                    email,
                    forgotPasswordResponse -> {
                        if (forgotPasswordResponse != null
                                && TextUtils.isEmpty(forgotPasswordResponse.getError())) {
                            Log.d(TAG, "Successfully reset password for email: " + email);
                            showDialog(DialogType.RESET_PASSWORD,
                                    currentActivity.getString(R.string.app_cms_reset_password_success_description, email),
                                    false,
                                    null);
                        } else if (forgotPasswordResponse != null) {
                            Log.e(TAG, "Failed to reset password for email: " + email);
                            showDialog(DialogType.RESET_PASSWORD,
                                    forgotPasswordResponse.getError(),
                                    false,
                                    null);
                        }
                    });
        }
    }

    public void getUserData(final Action1<UserIdentity> userIdentityAction) {
        if (currentActivity != null) {
            if (shouldRefreshAuthToken()) {
                callRefreshIdentity(() -> {
                    String url = currentActivity.getString(R.string.app_cms_user_identity_api_url,
                            appCMSMain.getApiBaseUrl(),
                            appCMSMain.getInternalName());
                    appCMSUserIdentityCall.callGet(url,
                            getAuthToken(currentActivity),
                            userIdentity -> Observable.just(userIdentity).subscribe(userIdentityAction));
                });
            } else {
                String url = currentActivity.getString(R.string.app_cms_user_identity_api_url,
                        appCMSMain.getApiBaseUrl(),
                        appCMSMain.getInternalName());
                appCMSUserIdentityCall.callGet(url,
                        getAuthToken(currentActivity),
                        userIdentity -> Observable.just(userIdentity).subscribe(userIdentityAction));
            }
        }
    }

    public void updateUserData(final String username,
                               final String email,
                               final Action1<UserIdentity> userIdentityAction) {
        if (currentActivity != null) {
            if (shouldRefreshAuthToken()) {
                callRefreshIdentity(() -> {
                    String url = currentActivity.getString(R.string.app_cms_user_identity_api_url,
                            appCMSMain.getApiBaseUrl(),
                            appCMSMain.getInternalName());
                    UserIdentity userIdentity = new UserIdentity();
                    userIdentity.setName(username);
                    userIdentity.setEmail(email);
                    userIdentity.setId(getLoggedInUser(currentActivity));
                    appCMSUserIdentityCall.callPost(url,
                            getAuthToken(currentActivity),
                            userIdentity,
                            new Action1<UserIdentity>() {
                                @Override
                                public void call(UserIdentity userIdentity) {
                                    if (userIdentity != null) {
                                        setLoggedInUserName(currentActivity,
                                                userIdentity.getName());
                                        setLoggedInUserEmail(currentActivity,
                                                userIdentity.getEmail());
                                    }
                                    sendRefreshPageAction();
                                }
                            });
                });
            } else {
                String url = currentActivity.getString(R.string.app_cms_user_identity_api_url,
                        appCMSMain.getApiBaseUrl(),
                        appCMSMain.getInternalName());
                UserIdentity userIdentity = new UserIdentity();
                userIdentity.setName(username);
                userIdentity.setEmail(email);
                userIdentity.setId(getLoggedInUser(currentActivity));
                appCMSUserIdentityCall.callPost(url,
                        getAuthToken(currentActivity),
                        userIdentity,
                        userIdentity1 -> {
                            if (userIdentity1 != null) {
                                setLoggedInUserName(currentActivity,
                                        userIdentity1.getName());
                                setLoggedInUserEmail(currentActivity,
                                        userIdentity1.getEmail());
                                sendRefreshPageAction();
                            }
                        });
            }
        }
    }

    public ServiceConnection getInAppBillingServiceConn() {
        return inAppBillingServiceConn;
    }

    public void setInAppBillingServiceConn(ServiceConnection inAppBillingServiceConn) {
        this.inAppBillingServiceConn = inAppBillingServiceConn;
    }

    public void showSoftKeyboard(View view) {
        if (currentActivity != null) {
            if (view != null) {
                InputMethodManager imm =
                        (InputMethodManager) currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }

    public void closeSoftKeyboard() {
        if (currentActivity != null) {
            View view = currentActivity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm =
                        (InputMethodManager) currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public boolean navigateToPage(String pageId,
                                  String pageTitle,
                                  String url,
                                  boolean launchActivity,
                                  boolean appbarPresent,
                                  boolean fullscreenEnabled,
                                  boolean navbarPresent,
                                  boolean sendCloseAction,
                                  final Uri searchQuery) {
        boolean result = false;
        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {
            loadingPage = true;
            showMainFragmentView(false);
            Log.d(TAG, "Launching page " + pageTitle + ": " + pageId);
            Log.d(TAG, "Search query (optional): " + searchQuery);
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);
            currentActivity.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
            getPageIdContent(appCMSMain.getApiBaseUrl(),
                    pageIdToPageAPIUrlMap.get(pageId),
                    appCMSMain.getInternalName(),
                    true,
                    getPageId(appCMSPageUI),
                    new AppCMSPageAPIAction(appbarPresent,
                            fullscreenEnabled,
                            navbarPresent,
                            appCMSPageUI,
                            pageId,
                            pageId,
                            pageTitle,
                            pageId,
                            launchActivity,
                            sendCloseAction,
                            searchQuery) {
                        @Override
                        public void call(final AppCMSPageAPI appCMSPageAPI) {
                            final AppCMSPageAPIAction appCMSPageAPIAction = this;
                            if (appCMSPageAPI != null) {
                                boolean loadingHistory = false;
                                if (isUserLoggedIn(currentActivity)) {
                                    for (Module module : appCMSPageAPI.getModules()) {
                                        if (jsonValueKeyMap.get(module.getModuleType()) ==
                                                AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY) {
                                            if (module.getContentData() != null &&
                                                    module.getContentData().size() > 0) {
                                                loadingHistory = true;
                                                getHistoryData(appCMSHistoryResult -> {
                                                    if (appCMSHistoryResult != null) {
                                                        AppCMSPageAPI historyAPI =
                                                                appCMSHistoryResult.convertToAppCMSPageAPI(appCMSPageAPI.getId());
                                                        historyAPI.getModules().get(0).setId(module.getId());
                                                        mergeData(historyAPI, appCMSPageAPI);

                                                        cancelInternalEvents();
                                                        pushActionInternalEvents(appCMSPageAPIAction.pageId
                                                                + BaseView.isLandscape(currentActivity));
                                                        navigationPageData.put(appCMSPageAPIAction.pageId, appCMSPageAPI);
                                                        if (appCMSPageAPIAction.launchActivity) {
                                                            launchPageActivity(currentActivity,
                                                                    appCMSPageAPIAction.appCMSPageUI,
                                                                    appCMSPageAPI,
                                                                    appCMSPageAPIAction.pageId,
                                                                    appCMSPageAPIAction.pageTitle,
                                                                    appCMSPageAPIAction.pagePath,
                                                                    pageIdToPageNameMap.get(appCMSPageAPIAction.pageId),
                                                                    loadFromFile,
                                                                    appCMSPageAPIAction.appbarPresent,
                                                                    appCMSPageAPIAction.fullscreenEnabled,
                                                                    appCMSPageAPIAction.navbarPresent,
                                                                    appCMSPageAPIAction.sendCloseAction,
                                                                    appCMSPageAPIAction.searchQuery,
                                                                    ExtraScreenType.NONE);
                                                        } else {
                                                            Bundle args = getPageActivityBundle(currentActivity,
                                                                    appCMSPageAPIAction.appCMSPageUI,
                                                                    appCMSPageAPI,
                                                                    appCMSPageAPIAction.pageId,
                                                                    appCMSPageAPIAction.pageTitle,
                                                                    appCMSPageAPIAction.pagePath,
                                                                    pageIdToPageNameMap.get(appCMSPageAPIAction.pageId),
                                                                    loadFromFile,
                                                                    appCMSPageAPIAction.appbarPresent,
                                                                    appCMSPageAPIAction.fullscreenEnabled,
                                                                    appCMSPageAPIAction.navbarPresent,
                                                                    appCMSPageAPIAction.sendCloseAction,
                                                                    appCMSPageAPIAction.searchQuery,
                                                                    ExtraScreenType.NONE);
                                                            Intent updatePageIntent =
                                                                    new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                                            updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                                                    args);
                                                            currentActivity.sendBroadcast(updatePageIntent);
                                                            dismissOpenDialogs(null);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                if (!loadingHistory) {
                                    cancelInternalEvents();
                                    pushActionInternalEvents(this.pageId + BaseView.isLandscape(currentActivity));
                                    navigationPageData.put(this.pageId, appCMSPageAPI);
                                    if (this.launchActivity) {
                                        launchPageActivity(currentActivity,
                                                this.appCMSPageUI,
                                                appCMSPageAPI,
                                                this.pageId,
                                                this.pageTitle,
                                                this.pagePath,
                                                pageIdToPageNameMap.get(this.pageId),
                                                loadFromFile,
                                                this.appbarPresent,
                                                this.fullscreenEnabled,
                                                this.navbarPresent,
                                                this.sendCloseAction,
                                                this.searchQuery,
                                                ExtraScreenType.NONE);
                                    } else {
                                        Bundle args = getPageActivityBundle(currentActivity,
                                                this.appCMSPageUI,
                                                appCMSPageAPI,
                                                this.pageId,
                                                this.pageTitle,
                                                this.pagePath,
                                                pageIdToPageNameMap.get(this.pageId),
                                                loadFromFile,
                                                this.appbarPresent,
                                                this.fullscreenEnabled,
                                                this.navbarPresent,
                                                this.sendCloseAction,
                                                this.searchQuery,
                                                ExtraScreenType.NONE);
                                        Intent updatePageIntent =
                                                new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                        updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                                args);
                                        currentActivity.sendBroadcast(updatePageIntent);
                                        dismissOpenDialogs(null);
                                    }
                                }
                            } else {
                                sendStopLoadingPageAction();
                                setNavItemToCurrentAction(currentActivity);
                            }
                            loadingPage = false;
                        }
                    });
            result = true;
        } else if (currentActivity != null &&
                !TextUtils.isEmpty(url) &&
                url.contains(currentActivity.getString(
                        R.string.app_cms_page_navigation_contact_us_key))) {
            if (Apptentive.canShowMessageCenter()) {
                Apptentive.showMessageCenter(currentActivity);
            }
        } else {
            Log.d(TAG, "Resetting page navigation to previous tab");
            setNavItemToCurrentAction(currentActivity);
        }
        return result;
    }

    public void sendRefreshPageAction() {
        if (currentActivity != null) {
            if (navigateToHomeToRefresh) {
                navigateToHomePage();
                navigateToHomeToRefresh = false;
            } else {
                Intent refreshPageIntent = new Intent(AppCMSPresenter.PRESENTER_REFRESH_PAGE_ACTION);
                currentActivity.sendBroadcast(refreshPageIntent);
            }
        }
    }

    public boolean sendCloseOthersAction(String pageName, boolean closeSelf) {
        Log.d(TAG, "Sending close others action");
        boolean result = false;
        if (currentActivity != null) {
            Intent closeOthersIntent = new Intent(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION);
            closeOthersIntent.putExtra(currentActivity.getString(R.string.close_self_key),
                    closeSelf);
            closeOthersIntent.putExtra(currentActivity.getString(R.string.app_cms_closing_page_name),
                    pageName);
            currentActivity.sendBroadcast(closeOthersIntent);

            result = true;
        }
        return result;
    }

    public boolean sendDeepLinkAction(Uri deeplinkUri) {
        Log.d(TAG, "Sending deeplink action");
        boolean result = false;
        if (currentActivity != null) {
            Intent deeplinkIntent = new Intent(AppCMSPresenter.PRESENTER_DEEPLINK_ACTION);
            deeplinkIntent.setData(deeplinkUri);
            currentActivity.sendBroadcast(deeplinkIntent);
            result = true;
        }
        return result;
    }

    public void sendStopLoadingPageAction() {
        if (currentActivity != null) {
            Intent stopLoadingPageIntent =
                    new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
            currentActivity.sendBroadcast(stopLoadingPageIntent);
            showDialog(DialogType.NETWORK,
                    null,
                    false,
                    new Action0() {
                        @Override
                        public void call() {
                            sendCloseOthersAction(null, false);
                        }
                    });
        }
    }

    public void launchErrorActivity(Activity activity, PlatformType platformType) {
        if (platformType == PlatformType.ANDROID) {
            try {
                sendCloseOthersAction(null, true);
                Intent errorIntent = new Intent(activity, AppCMSErrorActivity.class);
                activity.startActivity(errorIntent);
            } catch (Exception e) {
                Log.e(TAG, "DialogType launching Mobile DialogType Activity");
            }
        } else if (platformType == PlatformType.TV) {
            try {
                Intent errorIntent = new Intent(activity, Class.forName(tvErrorScreenPackage));
                activity.startActivity(errorIntent);
            } catch (Exception e) {
                Log.e(TAG, "DialogType launching TV DialogType Activity");
            }
        }
    }

    public void getPageIdContent(String baseUrl,
                                 String endPoint,
                                 String siteId,
                                 boolean usePageIdQueryParam,
                                 String pageId,
                                 Action1<AppCMSPageAPI> readyAction) {
        boolean viewPlans = isViewPlanPage(pageId);

        GetAppCMSAPIAsyncTask.Params params = new GetAppCMSAPIAsyncTask.Params.Builder()
                .context(currentActivity)
                .baseUrl(baseUrl)
                .endpoint(endPoint)
                .siteId(siteId)
                .authToken(getAuthToken(currentActivity))
                .userId(getLoggedInUser(currentActivity))
                .usePageIdQueryParam(usePageIdQueryParam)
                .pageId(pageId)
                .viewPlansPage(viewPlans)
                .build();
        new GetAppCMSAPIAsyncTask(appCMSPageAPICall, readyAction).execute(params);
    }

    public boolean isViewPlanPage(String pageId) {
        String pageName = pageIdToPageNameMap.get(pageId);
        return (!TextUtils.isEmpty(pageName) &&
                pageName.equals(currentActivity.getString(R.string.app_cms_page_subscription_page_name_key)));
    }

    public String getPageIdToPageAPIUrl(String pageId) {
        return pageIdToPageAPIUrlMap.get(pageId);
    }

    public String getPageNameToPageAPIUrl(String pageName) {
        return actionToPageAPIUrlMap.get(pageNameToActionMap.get(pageName));
    }

    public boolean isUserLoggedIn(Context context) {
        return getLoggedInUser(context) != null;
    }

    public boolean isUserSubscribed(Context context) {
        return getActiveSubscriptionSku(context) != null;
    }

    public String getPngPosterPath(String fileName) {
        return currentActivity.getFilesDir().getAbsolutePath() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator + fileName + MEDIA_SURFIX_PNG;
    }

    public String getJpgPosterPath(String fileName) {
        return getBaseDownloadDir() + fileName + MEDIA_SURFIX_JPG;
    }

    public String getMP4VideoPath(String fileName) {
        return getBaseDownloadDir() + fileName + MEDIA_SURFIX_MP4;
    }

    public String getBaseDownloadDir() {
        return currentActivity.getFilesDir().getAbsolutePath() + File.separator
                + Environment.DIRECTORY_DOWNLOADS + File.separator;
    }

    public String getBaseImageDir() {
        return currentActivity.getFilesDir().getAbsolutePath() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator;
    }

    public String getLoggedInUser(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(USER_ID_SHARED_PREF_NAME, null);
        }
        return null;
    }

    public boolean setCastOverLay(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(CASTING_OVERLAY_PREF_NAME, 0);
            return sharedPrefs.edit().putBoolean(CAST_SHARED_PREF_NAME, true).commit();
        }
        return false;
    }

    /**
     * Get The Value of Cast Overlay is shown or not
     *
     * @param context
     * @return
     */
    public boolean isCastOverLayShown(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(CASTING_OVERLAY_PREF_NAME, 0);
            return sharedPrefs.getBoolean(CAST_SHARED_PREF_NAME, false);
        }
        return false;
    }

    /**
     * Set The Value for the Cast Introductory Overlay
     *
     * @param context
     * @param userId
     * @return
     */
    public boolean setLoggedInUser(Context context, String userId) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putString(USER_ID_SHARED_PREF_NAME, userId).commit() &&
                    setLoggedInTime(context);
        }
        return false;
    }

    public String getUserDownloadQualityPref(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_DOWNLOAD_QUALITY_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(USER_DOWNLOAD_QUALITY_SHARED_PREF_NAME, null);
        }
        return null;
    }

    public String getAnonymousUserToken(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ANONYMOUS_AUTH_TOKEN_PREF_NAME, 0);
            return sharedPrefs.getString(ANONYMOUS_AUTH_TOKEN_PREF_NAME, null);
        }
        return null;
    }

    public boolean setUserDownloadQualityPref(Context context, String downloadQuality) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_DOWNLOAD_QUALITY_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putString(USER_DOWNLOAD_QUALITY_SHARED_PREF_NAME,
                    downloadQuality).commit() && setLoggedInTime(context);
        }
        return false;
    }

    public boolean setAnonymousUserToken(Context context, String anonymousAuthToken) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ANONYMOUS_AUTH_TOKEN_PREF_NAME, 0);
            return sharedPrefs.edit().putString(ANONYMOUS_AUTH_TOKEN_PREF_NAME, anonymousAuthToken).commit();
        }
        return false;
    }

    /**
     * Method is used to get the user preference with regard to Closed caption
     *
     * @param context current context of Activity/Application
     * @return true/false based upon the user preference
     */
    public boolean getClosedCaptionPreference(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_SETTINGS_PREF_NAME, 0);
            return sharedPrefs.getBoolean(USER_CLOSED_CAPTION_PREF_KEY, false);
        }
        return false;
    }

    /**
     * Method is used to set the user preference with regard to Closed caption
     *
     * @param context current context of Activity/Application
     * @return true/false if the set operation was successful
     */
    public boolean setClosedCaptionPreference(Context context, boolean ccPref) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_SETTINGS_PREF_NAME, 0);
            return sharedPrefs.edit().putBoolean(USER_CLOSED_CAPTION_PREF_KEY, ccPref).commit();
        }
        return false;
    }

    public String getLoggedInUserName(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_NAME_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(USER_NAME_SHARED_PREF_NAME, null);
        }
        return null;
    }

    public boolean setLoggedInUserName(Context context, String userName) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_NAME_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putString(USER_NAME_SHARED_PREF_NAME, userName).commit();
        }
        return false;
    }

    public String getLoggedInUserEmail(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_EMAIL_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(USER_EMAIL_SHARED_PREF_NAME, null);
        }
        return null;
    }

    public boolean setLoggedInUserEmail(Context context, String userEmail) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_EMAIL_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putString(USER_EMAIL_SHARED_PREF_NAME, userEmail).commit();
        }
        return false;
    }

    public long getLoggedInTime(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_LOGGED_IN_TIME_PREF_NAME, 0);
            return sharedPrefs.getLong(USER_LOGGED_IN_TIME_PREF_NAME, 0L);
        }
        return 0L;
    }

    public boolean setLoggedInTime(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_LOGGED_IN_TIME_PREF_NAME, 0);
            Date now = new Date();
            return sharedPrefs.edit().putLong(USER_LOGGED_IN_TIME_PREF_NAME, now.getTime()).commit();
        }
        return false;
    }

    public String getRefreshToken(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(REFRESH_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(REFRESH_TOKEN_SHARED_PREF_NAME, null);
        }
        return null;
    }

    public boolean setRefreshToken(Context context, String refreshToken) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(REFRESH_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putString(REFRESH_TOKEN_SHARED_PREF_NAME, refreshToken).commit();
        }
        return false;
    }

    public String getAuthToken(Context context) {
        if (context != null) {
            if (isUserLoggedIn(context)) {
                SharedPreferences sharedPrefs = context.getSharedPreferences(AUTH_TOKEN_SHARED_PREF_NAME, 0);
                return sharedPrefs.getString(AUTH_TOKEN_SHARED_PREF_NAME, null);
            } else {
                return getAnonymousUserToken(context);
            }
        }
        return null;
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public RealmController getRealmController() {
        return realmController;
    }

    public boolean setAuthToken(Context context, String authToken) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(AUTH_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPreferences.edit().putString(AUTH_TOKEN_SHARED_PREF_NAME, authToken).commit();
        }
        return false;
    }

    public FirebaseAnalytics getmFireBaseAnalytics() {
        return mFireBaseAnalytics;
    }

    public void setmFireBaseAnalytics(FirebaseAnalytics mFireBaseAnalytics) {
        this.mFireBaseAnalytics = mFireBaseAnalytics;
    }

    public String getFacebookAccessToken(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(FACEBOOK_ACCESS_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(FACEBOOK_ACCESS_TOKEN_SHARED_PREF_NAME, null);
        }
        return null;
    }

    public String getGoogleAccessToken(Context context) {
        if (context != null) {
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPreferences.getString(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME, null);
        }
        return null;
    }

    public boolean setFacebookAccessToken(Context context,
                                          final String facebookAccessToken,
                                          final String facebookUserId,
                                          final String username,
                                          final String email) {
        if (launchType == LaunchType.SUBSCRIBE) {
            this.facebookAccessToken = facebookAccessToken;
            this.facebookUserId = facebookUserId;
            this.facebookUsername = username;
            this.facebookEmail = email;
            initiateItemPurchase();
        } else if (context != null) {
            String url = currentActivity.getString(R.string.app_cms_facebook_login_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSMain.getInternalName());
            appCMSFacebookLoginCall.call(url,
                    facebookAccessToken,
                    facebookUserId,
                    facebookLoginResponse -> {
                        if (facebookLoginResponse != null) {
                            setAuthToken(currentActivity, facebookLoginResponse.getAuthorizationToken());
                            setRefreshToken(currentActivity, facebookLoginResponse.getRefreshToken());
                            setLoggedInUser(currentActivity, facebookUserId);
                            setLoggedInUserName(currentActivity, username);
                            setLoggedInUserEmail(currentActivity, email);

                            refreshSubscriptionData();

                            if (entitlementPendingVideoData != null) {
                                launchButtonSelectedAction(entitlementPendingVideoData.pagePath,
                                        entitlementPendingVideoData.action,
                                        entitlementPendingVideoData.filmTitle,
                                        entitlementPendingVideoData.extraData,
                                        entitlementPendingVideoData.contentDatum,
                                        entitlementPendingVideoData.closeLauncher,
                                        entitlementPendingVideoData.currentlyPlayingIndex,
                                        entitlementPendingVideoData.relateVideoIds);
                            } else {
                                sendCloseOthersAction(null, true);
                                cancelInternalEvents();
                                restartInternalEvents();
                                NavigationPrimary homePageNavItem = findHomePageNavItem();
                                if (homePageNavItem != null) {
                                    navigateToPage(homePageNavItem.getPageId(),
                                            homePageNavItem.getTitle(),
                                            homePageNavItem.getUrl(),
                                            false,
                                            true,
                                            false,
                                            true,
                                            false,
                                            deeplinkSearchQuery);
                                }
                            }
                        }
                    });

            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(FACEBOOK_ACCESS_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPreferences.edit().putString(FACEBOOK_ACCESS_TOKEN_SHARED_PREF_NAME,
                    facebookAccessToken).commit();
        }
        return false;
    }

    public boolean setGoogleAccessToken(Context context,
                                        final String googleAccessToken,
                                        final String googleUserId,
                                        final String googleUsername,
                                        final String googleEmail) {

        if (launchType == LaunchType.SUBSCRIBE) {
            this.googleAccessToken = googleAccessToken;
            this.googleUserId = googleUserId;
            this.googleUsername = googleUsername;
            this.googleEmail = googleEmail;
            initiateItemPurchase();
        } else if (context != null) {
            String url = currentActivity.getString(R.string.app_cms_google_login_api_url,
                    appCMSMain.getApiBaseUrl(), appCMSMain.getInternalName());

            appCMSGoogleLoginCall.call(url, googleAccessToken,
                    googleLoginResponse -> {
                        if (googleLoginResponse != null) {
                            setAuthToken(currentActivity, googleLoginResponse.getAuthorizationToken());
                            setRefreshToken(currentActivity, googleLoginResponse.getRefreshToken());
                            setLoggedInUser(currentActivity, googleUserId);
                            setLoggedInUserName(currentActivity, googleUsername);
                            setLoggedInUserEmail(currentActivity, googleEmail);

                            refreshSubscriptionData();

                            if (entitlementPendingVideoData != null) {
                                launchButtonSelectedAction(entitlementPendingVideoData.pagePath,
                                        entitlementPendingVideoData.action,
                                        entitlementPendingVideoData.filmTitle,
                                        entitlementPendingVideoData.extraData,
                                        entitlementPendingVideoData.contentDatum,
                                        entitlementPendingVideoData.closeLauncher,
                                        entitlementPendingVideoData.currentlyPlayingIndex,
                                        entitlementPendingVideoData.relateVideoIds);
                            } else {
                                sendCloseOthersAction(null, true);
                                cancelInternalEvents();
                                restartInternalEvents();
                                NavigationPrimary homePageNavItem = findHomePageNavItem();
                                if (homePageNavItem != null) {
                                    navigateToPage(homePageNavItem.getPageId(),
                                            homePageNavItem.getTitle(),
                                            homePageNavItem.getUrl(),
                                            false,
                                            true,
                                            false,
                                            true,
                                            false,
                                            deeplinkSearchQuery);
                                }
                            }
                        } else {

                        }
                    });

            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPreferences.edit().putString(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME,
                    googleAccessToken).commit();
        }
        return false;
    }

    public String getActiveSubscriptionSku(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_SKU, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_SKU, null);
        }
        return null;
    }

    public boolean setActiveSubscriptionSku(Context context, String subscriptionSku) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_SKU, 0);
            return sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_SKU, subscriptionSku).commit();
        }
        return false;
    }

    public String getActiveSubscriptionId(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_ID, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_ID, null);
        }
        return null;
    }

    public boolean setActiveSubscriptionId(Context context, String subscriptionId) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_ID, 0);
            return sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_ID, subscriptionId).commit();
        }
        return false;
    }

    public String getActiveSubscriptionCurrency(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_CURRENCY, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_CURRENCY, null);
        }
        return null;
    }

    public boolean setActiveSubscriptionCurrency(Context context, String subscriptionCurrency) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_CURRENCY, 0);
            return sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_CURRENCY, subscriptionCurrency).commit();
        }
        return false;
    }

    public String getActiveSubscriptionPlanName(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_PLAN_NAME, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_PLAN_NAME, null);
        }
        return null;
    }

    public boolean setActiveSubscriptionPlanName(Context context, String subscriptionPlanName) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_PLAN_NAME, 0);
            return sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_PLAN_NAME, subscriptionPlanName).commit();
        }
        return false;
    }

    public float getActiveSubscriptionPrice(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_PRICE_NAME, 0);
            return sharedPrefs.getFloat(ACTIVE_SUBSCRIPTION_PRICE_NAME, 0.0f);
        }
        return 0.0f;
    }

    private boolean setActiveSubscriptionPrice(Context context, float subscriptionPrice) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_PRICE_NAME, 0);
            return sharedPrefs.edit().putFloat(ACTIVE_SUBSCRIPTION_PRICE_NAME, subscriptionPrice).commit();
        }
        return false;
    }

    public String getActiveSubscriptionReceipt(Context context) {
        if (context != null) {
            if (context != null) {
                SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_RECEIPT, 0);
                return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_RECEIPT, null);
            }
        }
        return null;
    }

    public boolean setActiveSubscriptionProcessor(Context context, String paymentProcessor) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_PROCESSOR_NAME, 0);
            return sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_PROCESSOR_NAME, paymentProcessor).commit();
        }
        return false;
    }

    public String getActiveSubscriptionProcessor(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_PROCESSOR_NAME, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_PROCESSOR_NAME, null);
        }
        return null;
    }

    public boolean setActiveSubscriptionReceipt(Context context, String subscriptionToken) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_RECEIPT, 0);
            return sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_RECEIPT, subscriptionToken).commit();
        }
        return false;
    }

    public void logout() {
        if (currentActivity != null) {
            GraphRequest revokePermissions = new GraphRequest(AccessToken.getCurrentAccessToken(),
                    getLoggedInUser(currentActivity) + "/permissions/", null,
                    HttpMethod.DELETE, response -> {
                if (response != null) {
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Log.e(TAG, error.toString());
                    } else {
                        //
                    }
                }
            });

            revokePermissions.executeAsync();
            LoginManager.getInstance().logOut();

            setLoggedInUser(currentActivity, null);
            setLoggedInUserName(currentActivity, null);
            setLoggedInUserEmail(currentActivity, null);
            setActiveSubscriptionPrice(currentActivity, 0.0f);
            setActiveSubscriptionId(currentActivity, null);
            setActiveSubscriptionSku(currentActivity, null);
            setActiveSubscriptionPlanName(currentActivity, null);
            setActiveSubscriptionReceipt(currentActivity, null);
            setRefreshToken(currentActivity, null);
            setAuthToken(currentActivity, null);

            sendUpdateHistoryAction();

            signinAnonymousUser(appCMSMain);

            if (googleApiClient != null && googleApiClient.isConnected()) {
                Auth.GoogleSignInApi.signOut(googleApiClient);
            }

            navigateToHomeToRefresh = true;
            launchNavigationPage();

            CastHelper.getInstance(currentActivity.getApplicationContext()).disconnectChromecastOnLogout();
            AppsFlyerUtils.logoutEvent(currentActivity, getLoggedInUser(currentActivity));
        }
    }

    public void addInternalEvent(OnInternalEvent onInternalEvent) {
        if (currentActions.size() > 0 &&
                !TextUtils.isEmpty(currentActions.peek()) &&
                onActionInternalEvents.get(currentActions.peek()) != null) {
            onActionInternalEvents.get(currentActions.peek()).add(onInternalEvent);
        }
    }

    public void clearOnInternalEvents() {
        if (currentActions.size() > 0 &&
                !TextUtils.isEmpty(currentActions.peek()) &&
                onActionInternalEvents.get(currentActions.peek()) != null) {
            onActionInternalEvents.get(currentActions.peek()).clear();
        }
    }

    public @Nullable
    List<OnInternalEvent> getOnInternalEvents() {
        if (currentActions.size() > 0 &&
                !TextUtils.isEmpty(currentActions.peek()) &&
                onActionInternalEvents.get(currentActions.peek()) != null) {
            return onActionInternalEvents.get(currentActions.peek());
        }
        return null;
    }

    public void restartInternalEvents() {
        if (currentActions.size() > 0) {
            Log.d(TAG, "Restarting internal events");
            List<OnInternalEvent> onInternalEvents = onActionInternalEvents.get(currentActions.peek());
            if (onInternalEvents != null) {
                for (OnInternalEvent onInternalEvent : onInternalEvents) {
                    onInternalEvent.cancel(false);
                    Log.d(TAG, "Restarted internal event");
                }
            }
        }
    }

    public void cancelInternalEvents() {
        if (currentActions.size() > 0) {
            List<OnInternalEvent> onInternalEvents = onActionInternalEvents.get(currentActions.peek());
            if (onInternalEvents != null) {
                for (OnInternalEvent onInternalEvent : onInternalEvents) {
                    onInternalEvent.cancel(true);
                }
            }
        }
    }

    public void popActionInternalEvents() {
        if (currentActions.size() > 0) {
            Log.d(TAG, "Stack size - Popping action internal events: " + currentActions.size());
            currentActions.pop();
            Log.d(TAG, "Stack size - Popped action internal events: " + currentActions.size());
        }
    }

    public NavigationPrimary findHomePageNavItem() {
        if (navigation.getNavigationPrimary().size() >= 1) {
            return navigation.getNavigationPrimary().get(0);
        }
        return null;
    }

    public NavigationPrimary findMoviesPageNavItem() {
        if (navigation.getNavigationPrimary().size() >= 2) {
            return navigation.getNavigationPrimary().get(1);
        }
        return null;
    }

    public void getAppCMSMain(final Activity activity,
                              final String siteId,
                              final Uri searchQuery,
                              final PlatformType platformType) {
        this.deeplinkSearchQuery = searchQuery;
        this.platformType = platformType;
        GetAppCMSMainUIAsyncTask.Params params = new GetAppCMSMainUIAsyncTask.Params.Builder()
                .context(currentActivity)
                .siteId(siteId)
                .build();
        new GetAppCMSMainUIAsyncTask(appCMSMainUICall, main -> {
            if (main == null) {
                Log.e(TAG, "DialogType retrieving main.json");
                launchErrorActivity(activity, platformType);
            } else if (TextUtils.isEmpty(main
                    .getAndroid())) {
                Log.e(TAG, "AppCMS key for main not found");
                launchErrorActivity(activity, platformType);
            } else if (TextUtils.isEmpty(main
                    .getApiBaseUrl())) {
                Log.e(TAG, "AppCMS key for API Base URL not found");
                launchErrorActivity(activity, platformType);
            } else if (TextUtils.isEmpty(main.getInternalName())) {
                Log.e(TAG, "AppCMS key for API Site ID not found");
                launchErrorActivity(activity, platformType);
            } else {
                appCMSMain = main;
                String version = main.getVersion();
                String oldVersion = main.getOldVersion();
                Log.d(TAG, "Version: " + version);
                Log.d(TAG, "OldVersion: " + oldVersion);
                loadFromFile = false;

                appCMSSearchUrlComponent = DaggerAppCMSSearchUrlComponent.builder()
                        .appCMSSearchUrlModule(new AppCMSSearchUrlModule(main.getApiBaseUrl(),
                                main.getInternalName(),
                                appCMSSearchCall))
                        .build();
                getAppCMSSite(activity, main, platformType);
            }
        }).execute(params);
    }

    public AppCMSMain getAppCMSMain() {
        return appCMSMain;
    }

    public boolean isPageAVideoPage(String pageName) {
        if (currentActivity != null) {
            return pageName.contains(currentActivity.getString(R.string.app_cms_video_page_page_name));
        }
        return false;
    }

    public boolean isPagePrimary(String pageId) {
        for (NavigationPrimary navigationPrimary : navigation.getNavigationPrimary()) {
            if (!TextUtils.isEmpty(navigationPrimary.getPageId()) && pageId.contains(navigationPrimary.getPageId())) {
                return true;
            }
        }

        return false;
    }

    public boolean isPageNavigationPage(String pageId) {
        if (currentActivity != null &&
                !TextUtils.isEmpty(pageId) &&
                pageId.equals(currentActivity.getString(R.string.app_cms_navigation_page_tag))) {
            return true;
        }
        return false;
    }

    public boolean isPageUser(String pageId) {
        for (NavigationUser navigationUser : navigation.getNavigationUser()) {
            if (!TextUtils.isEmpty(navigationUser.getPageId()) && pageId.contains(navigationUser.getPageId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isPageSplashPage(String pageId) {
        if (splashPage != null &&
                !TextUtils.isEmpty(pageId) &&
                !TextUtils.isEmpty(splashPage.getPageId())) {
            return splashPage.getPageId().equals(pageId);
        }
        return false;
    }

    public AppCMSSearchUrlComponent getAppCMSSearchUrlComponent() {
        return appCMSSearchUrlComponent;
    }

    public void showMoreDialog(String title, String fullText) {
        if (platformType == PlatformType.ANDROID) {
            if (currentActivity != null &&
                    currentActivity instanceof AppCompatActivity &&
                    isAdditionalFragmentViewAvailable()) {
                pushActionInternalEvents(currentActivity.getString(R.string.more_page_action));

                clearAdditionalFragment();
                FragmentTransaction transaction =
                        ((AppCompatActivity) currentActivity).getSupportFragmentManager().beginTransaction();
                AppCMSMoreFragment appCMSMoreFragment =
                        AppCMSMoreFragment.newInstance(currentActivity,
                                title,
                                fullText);
                transaction.add(R.id.app_cms_addon_fragment,
                        appCMSMoreFragment,
                        currentActivity.getString(R.string.app_cms_more_page_tag)).commit();
                showAddOnFragment(true, 0.2f);
                setNavItemToCurrentAction(currentActivity);
            }
        } else if (platformType == PlatformType.TV) {
            Intent args = new Intent(AppCMSPresenter.PRESENTER_DIALOG_ACTION);
            Bundle bundle = new Bundle();
            bundle.putString(currentActivity.getString(R.string.dialog_item_title_key), title);
            bundle.putString(currentActivity.getString(R.string.dialog_item_description_key), fullText);

            args.putExtra(currentActivity.getString(R.string.dialog_item_key), bundle);
            currentActivity.sendBroadcast(args);
        }
    }

    public boolean shouldRefreshAuthToken() {
        if (currentActivity != null) {
            long lastLoginTime = getLoggedInTime(currentActivity);
            if (lastLoginTime >= 0) {
                long now = new Date().getTime();
                long timeDiff = now - lastLoginTime;
                long minutesSinceLogin = timeDiff / (MILLISECONDS_PER_SECOND * SECONDS_PER_MINUTE);
                if (minutesSinceLogin >= MAX_SESSION_DURATION_IN_MINUTES) {
                    return true;
                }
            }
        }
        return false;
    }

    public void showToast(String messgae, int messageDuration) {
        Toast.makeText(currentActivity, messgae, messageDuration).show();
    }

    public void showEntitlementDialog(DialogType dialogType) {
        if (currentActivity != null) {
            int textColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor());
            String title = currentActivity.getString(R.string.app_cms_login_required_title);
            String message = currentActivity.getString(R.string.app_cms_login_required_message);
            if (dialogType == DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED) {
                title = currentActivity.getString(R.string.app_cms_login_and_subscription_required_title);
                message = currentActivity.getString(R.string.app_cms_login_and_subscription_required_message);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
            builder.setTitle(Html.fromHtml(currentActivity.getString(R.string.text_with_color,
                    Integer.toHexString(textColor).substring(2),
                    title)))
                    .setMessage(Html.fromHtml(currentActivity.getString(R.string.text_with_color,
                            Integer.toHexString(textColor).substring(2),
                            message)));

            if (dialogType == DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED) {
                builder.setPositiveButton(R.string.app_cms_login_button_text,
                        (dialog, which) -> {
                            dialog.dismiss();
                            launchType = LaunchType.LOGIN_AND_SIGNUP;
                            navigateToLoginPage();
                        });
                builder.setNegativeButton(R.string.app_cms_subscription_button_text,
                        (dialog, which) -> {
                            dialog.dismiss();
                            navigateToSubscriptionPlansPage(null, null);
                        });
            } else {
                builder.setPositiveButton(R.string.app_cms_subscription_button_text,
                        (dialog, which) -> {
                            dialog.dismiss();
                            navigateToSubscriptionPlansPage(null, null);
                        });
            }

            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                        Color.parseColor(appCMSMain.getBrand()
                                .getGeneral()
                                .getBackgroundColor())));
                if (currentActivity.getWindow().isActive()) {
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        Log.e(TAG, "An exception has occurred when attempting to show the dialogType dialog: "
                                + e.toString());
                    }
                }
            }
        }
    }

    public void showConfirmCancelSubscriptionDialog(Action1<Boolean> oncConfirmationAction) {
        if (currentActivity != null) {
            int textColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor());
            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
            String title = currentActivity.getString(R.string.app_cms_payment_cancelled_dialog_title);
            String message = currentActivity.getString(R.string.app_cms_payment_canceled_body);
            builder.setTitle(Html.fromHtml(currentActivity.getString(R.string.text_with_color,
                    Integer.toHexString(textColor).substring(2),
                    title)))
                    .setMessage(Html.fromHtml(currentActivity.getString(R.string.text_with_color,
                            Integer.toHexString(textColor).substring(2),
                            message)));
            builder.setPositiveButton(R.string.app_cms_positive_confirmation_button_text,
                    (dialog, which) -> {
                        dialog.dismiss();
                        if (oncConfirmationAction != null) {
                            Observable.just(true).subscribe(oncConfirmationAction);
                        }
                    });
            builder.setNegativeButton(R.string.app_cms_negative_confirmation_button_text,
                    (dialog, which) -> {
                        dialog.dismiss();
                        if (oncConfirmationAction != null) {
                            Observable.just(false).subscribe(oncConfirmationAction);
                        }
                    });
            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                        Color.parseColor(appCMSMain.getBrand()
                                .getGeneral()
                                .getBackgroundColor())));
                if (currentActivity.getWindow().isActive()) {
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        Log.e(TAG, "An exception has occurred when attempting to show the dialogType dialog: "
                                + e.toString());
                    }
                }
            }
        }
    }

    public void showDialog(DialogType dialogType,
                           String optionalMessage,
                           boolean showCancelButton,
                           final Action0 onDismissAction) {
        if (currentActivity != null) {
            int textColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor());
            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
            String title;
            String message;
            switch (dialogType) {
                case SIGNIN:
                    title = currentActivity.getString(R.string.app_cms_signin_error_title);
                    message = optionalMessage;
                    break;

                case SIGNUP:
                    title = currentActivity.getString(R.string.app_cms_signup_error_title);
                    message = optionalMessage;
                    break;

                case RESET_PASSWORD:
                    title = currentActivity.getString(R.string.app_cms_reset_password_title);
                    message = optionalMessage;
                    break;

                case CANCEL_SUBSCRIPTION:
                    title = currentActivity.getString(R.string.app_cms_cancel_subscription_title);
                    message = optionalMessage;
                    break;

                case EXISTING_SUBSCRIPTION:
                    title = currentActivity.getString(R.string.app_cms_existing_subscription_title);
                    message = optionalMessage;
                    break;

                case SUBSCRIBE:
                    title = currentActivity.getString(R.string.app_cms_subscription_title);
                    message = optionalMessage;
                    break;

                case DOWNLOAD_INCOMPLETE:
                    title = currentActivity.getString(R.string.app_cms_download_incomplete_error_title);
                    message = currentActivity.getString(R.string.app_cms_download_incomplete_error_message);
                    break;

                default:
                    title = currentActivity.getString(R.string.app_cms_network_connectivity_error_title);
                    message = currentActivity.getString(R.string.app_cms_network_connectivity_error_message);
                    if (isNetworkConnected()) {
                        title = currentActivity.getString(R.string.app_cms_data_error_title);
                        message = currentActivity.getString(R.string.app_cms_data_error_message);
                    }
            }
            builder.setTitle(Html.fromHtml(currentActivity.getString(R.string.text_with_color,
                    Integer.toHexString(textColor).substring(2),
                    title)))
                    .setMessage(Html.fromHtml(currentActivity.getString(R.string.text_with_color,
                            Integer.toHexString(textColor).substring(2),
                            message)));
            if (showCancelButton) {
                builder.setPositiveButton(R.string.app_cms_confirm_alert_dialog_button_text,
                        (dialog, which) -> {
                            dialog.dismiss();
                            if (onDismissAction != null) {
                                onDismissAction.call();
                            }
                        });
                builder.setNegativeButton(R.string.app_cms_cancel_alert_dialog_button_text,
                        (dialog, which) -> dialog.dismiss());
            } else {
                builder.setNegativeButton(R.string.app_cms_close_alert_dialog_button_text,
                        (dialog, which) -> {
                            dialog.dismiss();
                            if (onDismissAction != null) {
                                onDismissAction.call();
                            }
                        });
            }

            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                        Color.parseColor(appCMSMain.getBrand()
                                .getGeneral()
                                .getBackgroundColor())));
                if (currentActivity.getWindow().isActive()) {
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        Log.e(TAG, "An exception has occurred when attempting to show the dialogType dialog: "
                                + e.toString());
                    }
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) currentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getNetworkInfo(activeNetwork);
                return activeNetworkInfo.isConnectedOrConnecting();
            }
        } else {
            for (NetworkInfo networkInfo : connectivityManager.getAllNetworkInfo()) {
                if (networkInfo.isConnectedOrConnecting()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void pushActionInternalEvents(String action) {
        Log.d(TAG, "Stack size - pushing internal events: " + currentActions.size());
        if (onActionInternalEvents.get(action) == null) {
            onActionInternalEvents.put(action, new ArrayList<>());
        }
        int currentActionPos = currentActions.search(action);
        if (0 < currentActionPos) {
            for (int i = 0; i < currentActionPos; i++) {
                currentActions.pop();
            }
        }
        currentActions.push(action);
    }

    public void sendBeaconAdImpression(String vid, String screenName, String parentScreenName,
                                       long currentPosition) {
        Log.d(TAG, "Sending Beacon Ad Impression");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition,
                BeaconEvent.AD_IMPRESSION, false);
        if (url != null) {
            Log.d(TAG, "Beacon Ad Impression: " + url);
            beaconMessageRunnable.setUrl(url);
            beaconMessageThread.run();
        }
    }

    public void sendBeaconAdRequestMessage(String vid, String screenName, String parentScreenName,
                                           long currentPosition) {
        Log.d(TAG, "Sending Beacon Ad Request");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition,
                BeaconEvent.AD_REQUEST, false);
        if (url != null) {
            Log.d(TAG, "Beacon Ad Request: " + url);
            beaconMessageRunnable.setUrl(url);
            beaconMessageThread.run();
        }
    }

    public void sendBeaconPingMessage(String vid, String screenName, String parentScreenName,
                                      long currentPosition, boolean usingChromecast) {
        Log.d(TAG, "Sending Beacon Ping Message");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition, BeaconEvent.PING, usingChromecast);
        if (url != null) {
            Log.d(TAG, "Beacon Ping: " + url);
            beaconMessageRunnable.setUrl(url);
            beaconMessageThread.run();
        }
    }

    public void sendBeaconPlayMessage(String vid, String screenName, String parentScreenName,
                                      long currentPosition, boolean usingChromecast) {
        Log.d(TAG, "Sending Beacon Play Message");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition, BeaconEvent.PLAY, usingChromecast);
        if (url != null) {
            Log.d(TAG, "Beacon Play: " + url);
            beaconMessageRunnable.setUrl(url);
            beaconMessageThread.run();
        }
    }

    private String getPermalinkCompletePath(String pagePath) {
        StringBuffer permalinkCompletePath = new StringBuffer();
        permalinkCompletePath.append(currentActivity.getString(R.string.https_scheme));
        permalinkCompletePath.append(appCMSMain.getDomainName());
        permalinkCompletePath.append(File.separatorChar);
        permalinkCompletePath.append(pagePath);
        return permalinkCompletePath.toString();
    }

    private String getBeaconUrl(String vid, String screenName, String parentScreenName,
                                long currentPosition, BeaconEvent event, boolean usingChromecast) {
        StringBuilder url = new StringBuilder();
        if (currentActivity != null && appCMSMain != null) {
            final String utfEncoding = currentActivity.getString(R.string.utf8enc);
            String uid = InstanceID.getInstance(currentActivity).getId();
            int currentPositionSecs = (int) (currentPosition / MILLISECONDS_PER_SECOND);
            if (isUserLoggedIn(currentActivity)) {
                uid = getLoggedInUser(currentActivity);
            }
            try {
                url.append(currentActivity.getString(R.string.app_cms_beacon_url,
                        appCMSMain.getBeacon().getApiBaseUrl(),
                        URLEncoder.encode(appCMSMain.getBeacon().getSiteName(), utfEncoding),
                        URLEncoder.encode(appCMSMain.getBeacon().getClientId(), utfEncoding),
                        URLEncoder.encode(currentActivity.getString(R.string.app_cms_beacon_platform),
                                utfEncoding),
                        URLEncoder.encode(currentActivity.getString(R.string.app_cms_beacon_dpm_android),
                                utfEncoding),
                        URLEncoder.encode(vid, utfEncoding),
                        URLEncoder.encode(getPermalinkCompletePath(screenName), utfEncoding),
                        URLEncoder.encode(parentScreenName, utfEncoding),
                        event,
                        currentPositionSecs,
                        URLEncoder.encode(uid, utfEncoding)));
                if (usingChromecast) {
                    url.append(URLEncoder.encode(currentActivity.getString(R.string.app_cms_beacon_chromecast_dp2_url),
                            utfEncoding));
                }
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "DialogType encoding Beacon URL parameters: " + e.toString());
            }
        }
        return url.toString();
    }

    public void sendGaScreen(String screenName) {
        if (tracker != null) {
            Log.d(TAG, "Sending GA screen tracking event: " + screenName);
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public void finalizeSignupAfterSubscription(String receiptData) {
        setActiveSubscriptionReceipt(currentActivity, receiptData);

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setPlatform(currentActivity.getString(R.string.app_cms_subscription_platform_key));
        subscriptionRequest.setSiteId(currentActivity.getString(R.string.app_cms_app_name));
        subscriptionRequest.setSubscription(currentActivity.getString(R.string.app_cms_subscription_key));
        subscriptionRequest.setPlanId(planToPurchase);
        subscriptionRequest.setPlanIdentifier(skuToPurchase);
        subscriptionRequest.setUserId(getLoggedInUser(currentActivity));
        subscriptionRequest.setReceipt(receiptData);

        Log.d(TAG, "Subscription request: " + gson.toJson(subscriptionRequest, SubscriptionRequest.class));

        int subscriptionCallType = R.string.app_cms_subscription_plan_create_key;

        if (getActiveSubscriptionSku(currentActivity) != null) {
            subscriptionCallType = R.string.app_cms_subscription_plan_update_key;
        }

        try {
            appCMSSubscriptionPlanCall.call(
                    currentActivity.getString(R.string.app_cms_register_subscription_api_url,
                            appCMSMain.getApiBaseUrl(),
                            appCMSMain.getInternalName(),
                            currentActivity.getString(R.string.app_cms_subscription_platform_key)),
                    subscriptionCallType,
                    subscriptionRequest,
                    apikey,
                    getAuthToken(currentActivity),
                    result -> {

                    },
                    appCMSSubscriptionPlanResult -> {
                        if (appCMSSubscriptionPlanResult != null) {
                            Log.d(TAG, "Subscription response: " + gson.toJson(appCMSSubscriptionPlanResult, AppCMSSubscriptionPlanResult.class));

                            AppsFlyerUtils.subscriptionEvent(currentActivity,
                                    true,
                                    currentActivity.getString(R.string.app_cms_appsflyer_dev_key),
                                    String.valueOf(planToPurchasePrice),
                                    subscriptionRequest.getPlanId(),
                                    subscriptionRequest.getCurrencyCode());
                        }
                    },
                    planResult -> {

                    });
        } catch (IOException e) {
            Log.e(TAG, "Failed to update user subscription status");
        }

        refreshSubscriptionData();

        setActiveSubscriptionSku(currentActivity, skuToPurchase);
        setActiveSubscriptionId(currentActivity, planToPurchase);
        setActiveSubscriptionCurrency(currentActivity, currencyOfPlanToPurchase);
        setActiveSubscriptionPlanName(currentActivity, planToPurchaseName);
        setActiveSubscriptionPrice(currentActivity, planToPurchasePrice);
        skuToPurchase = null;
        planToPurchase = null;
        currencyOfPlanToPurchase = null;
        planToPurchaseName = null;
        planToPurchasePrice = 0.0f;

        if (launchType == LaunchType.SUBSCRIBE) {
            launchType = LaunchType.LOGIN_AND_SIGNUP;
        }
        if (signupFromFacebook) {
            setFacebookAccessToken(currentActivity,
                    facebookAccessToken,
                    facebookUserId,
                    facebookUsername,
                    facebookEmail);
        } else if (isSignupFromGoogle) {
            setGoogleAccessToken(currentActivity,
                    googleAccessToken,
                    googleUserId,
                    googleUsername,
                    googleEmail);
        }
        subscriptionUserEmail = null;
        subscriptionUserPassword = null;
        facebookAccessToken = null;
        facebookUserId = null;

        googleAccessToken = null;
        googleUserId = null;

        if (entitlementPendingVideoData != null) {
            sendCloseOthersAction(null, true);
            launchButtonSelectedAction(entitlementPendingVideoData.pagePath,
                    entitlementPendingVideoData.action,
                    entitlementPendingVideoData.filmTitle,
                    entitlementPendingVideoData.extraData,
                    entitlementPendingVideoData.contentDatum,
                    entitlementPendingVideoData.closeLauncher,
                    entitlementPendingVideoData.currentlyPlayingIndex,
                    entitlementPendingVideoData.relateVideoIds);
            entitlementPendingVideoData.pagePath = null;
            entitlementPendingVideoData.action = null;
            entitlementPendingVideoData.filmTitle = null;
            entitlementPendingVideoData.extraData = null;
            entitlementPendingVideoData.contentDatum = null;
            entitlementPendingVideoData.closeLauncher = false;
            entitlementPendingVideoData.currentlyPlayingIndex = -1;
            entitlementPendingVideoData.relateVideoIds = null;
            entitlementPendingVideoData = null;
        } else {
            sendCloseOthersAction(null, true);
            cancelInternalEvents();
            restartInternalEvents();
            NavigationPrimary homePageNavItem = findHomePageNavItem();
            if (homePageNavItem != null) {
                navigateToPage(homePageNavItem.getPageId(),
                        homePageNavItem.getTitle(),
                        homePageNavItem.getUrl(),
                        false,
                        true,
                        false,
                        true,
                        false,
                        deeplinkSearchQuery);
            }
        }
    }

    public List<SubscriptionPlan> availableUpgradesForUser(String userId) {
        RealmResults<UserSubscriptionPlan> userSubscriptionPlanResult =
                realmController.getUserSubscriptionPlan(userId);
        if (userSubscriptionPlanResult != null) {
            return userSubscriptionPlanResult.first().getAvailableUpgrades();
        }
        return null;
    }

    public boolean upgradesAvailableForUser(String userId) {
        List<SubscriptionPlan> availableUpgradesForUser =
                availableUpgradesForUser(userId);
        if (availableUpgradesForUser != null) {
            return (availableUpgradesForUser.size() > 0);
        }
        return false;
    }

    public boolean isActionFacebook(String action) {
        if (!TextUtils.isEmpty(action)) {
            if (actionToActionTypeMap.get(action) == AppCMSActionType.LOGIN_FACEBOOK ||
                    actionToActionTypeMap.get(action) == AppCMSActionType.SIGNUP_FACEBOOK) {
                return true;
            }
        }

        return false;
    }

    public boolean isActionGoogle(String action) {
        if (!TextUtils.isEmpty(action)) {
            if (actionToActionTypeMap.get(action) == AppCMSActionType.LOGIN_GOOGLE ||
                    actionToActionTypeMap.get(action) == AppCMSActionType.SIGNUP_GOOGLE) {
                return true;
            }
        }

        return false;
    }

    public void signup(String email, String password) {
        if (currentActivity != null) {
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                showDialog(DialogType.SIGNUP,
                        currentActivity.getString(R.string.app_cms_signup_invalid_email_and_password_message),
                        false,
                        null);
            } else if (TextUtils.isEmpty(email)) {
                showDialog(DialogType.SIGNUP,
                        currentActivity.getString(R.string.app_cms_signup_invalid_email_message),
                        false,
                        null);
            } else if (TextUtils.isEmpty(password)) {
                showDialog(DialogType.SIGNUP,
                        currentActivity.getString(R.string.app_cms_signup_invalid_password_message),
                        false,
                        null);
            } else {
                String url = currentActivity.getString(R.string.app_cms_signup_api_url,
                        appCMSMain.getApiBaseUrl(),
                        appCMSMain.getInternalName());
                startLoginAsyncTask(url,
                        email,
                        password,
                        true,
                        launchType == LaunchType.SUBSCRIBE);
            }
        }
    }

    public void refreshSubscriptionData() {
        if (currentActivity != null && isUserLoggedIn(currentActivity)) {
            if (shouldRefreshAuthToken()) {
                refreshIdentity(getRefreshToken(currentActivity),
                        new Action0() {
                            @Override
                            public void call() {
                                try {
                                    appCMSSubscriptionPlanCall.call(
                                            currentActivity.getString(R.string.app_cms_subscription_plan_list_api_url,
                                                    appCMSMain.getApiBaseUrl(),
                                                    appCMSMain.getInternalName()),
                                            R.string.app_cms_subscription_plan_list_key,
                                            null,
                                            apikey,
                                            getAuthToken(currentActivity),
                                            appCMSSubscriptionPlanResultList -> {
                                                for (AppCMSSubscriptionPlanResult appCMSSubscriptionPlanResult : appCMSSubscriptionPlanResultList) {
                                                    SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
                                                    subscriptionPlan.setSku(appCMSSubscriptionPlanResult.getIdentifier());
                                                    subscriptionPlan.setPlanId(appCMSSubscriptionPlanResult.getId());
                                                    subscriptionPlan.setSubscriptionPrice(
                                                            appCMSSubscriptionPlanResult
                                                                    .getPlanDetails()
                                                                    .get(0)
                                                                    .getRecurringPaymentAmount());
                                                    createSubscriptionPlan(subscriptionPlan);
                                                }
                                                subscriptionPlans = getExistingSubscriptionPlans();
                                            },
                                            appCMSSubscriptionPlanResult -> {

                                            },
                                            appCMSUserSubscriptionResult -> {

                                            });
                                    appCMSSubscriptionPlanCall.call(
                                            currentActivity.getString(R.string.app_cms_get_current_subscription_api_url,
                                                    appCMSMain.getApiBaseUrl(),
                                                    appCMSMain.getInternalName()),
                                            R.string.app_cms_subscription_subscribed_plan_key,
                                            null,
                                            apikey,
                                            getAuthToken(currentActivity),
                                            listResult -> {
                                            },
                                            singleResult -> {
                                            },
                                            appCMSSubscriptionPlanResult -> {
                                                if (appCMSSubscriptionPlanResult != null &&
                                                        appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getPlanDetails() != null) {

                                                    UserSubscriptionPlan userSubscriptionPlan = new UserSubscriptionPlan();
                                                    userSubscriptionPlan.setUserId(getLoggedInUser(currentActivity));
                                                    userSubscriptionPlan.setPlanReceipt(appCMSSubscriptionPlanResult.getSubscriptionInfo().getReceipt());
                                                    userSubscriptionPlan.setPaymentHandler(appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler());
                                                    SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
                                                    subscriptionPlan.setSubscriptionPrice(appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getPlanDetails().get(0).getRecurringPaymentAmount());
                                                    subscriptionPlan.setPlanId(appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getId());
                                                    subscriptionPlan.setSku(appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getIdentifier());
                                                    userSubscriptionPlan.setSubscriptionPlan(subscriptionPlan);

                                                    RealmList<SubscriptionPlan> availableUpgradeSubscriptionPlans =
                                                            new RealmList<>();
                                                    RealmResults<SubscriptionPlan> allAvailableSubscriptionPlans =
                                                            realmController.getAllSubscriptionPlans();
                                                    if (allAvailableSubscriptionPlans != null) {
                                                        availableUpgradeSubscriptionPlans.addAll(allAvailableSubscriptionPlans);
                                                    }
                                                    userSubscriptionPlan.setAvailableUpgrades(availableUpgradeSubscriptionPlans);
                                                    realmController.addUserSubscriptionPlan(userSubscriptionPlan);

                                                    setActiveSubscriptionSku(currentActivity,
                                                            appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getIdentifier());
                                                    setActiveSubscriptionId(currentActivity,
                                                            appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getId());
                                                    setActiveSubscriptionCurrency(currentActivity,
                                                            appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getPlanDetails()
                                                                    .get(0).getRecurringPaymentCurrencyCode());
                                                    setActiveSubscriptionPlanName(currentActivity,
                                                            appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getName());
                                                    setActiveSubscriptionPrice(currentActivity, (float)
                                                            appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getPlanDetails()
                                                                    .get(0).getRecurringPaymentAmount());
                                                    setActiveSubscriptionProcessor(currentActivity,
                                                            appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler());
                                                }
                                            }
                                    );
                                } catch (Exception e) {
                                    Log.e(TAG, "getSubscriptionPageContent: " + e.toString());
                                }
                            }
                        });
            } else {
                try {
                    appCMSSubscriptionPlanCall.call(
                            currentActivity.getString(R.string.app_cms_subscription_plan_list_api_url,
                                    appCMSMain.getApiBaseUrl(),
                                    appCMSMain.getInternalName()),
                            R.string.app_cms_subscription_plan_list_key,
                            null,
                            apikey,
                            getAuthToken(currentActivity),
                            appCMSSubscriptionPlanResultList -> {
                                if (appCMSSubscriptionPlanResultList != null) {
                                    for (AppCMSSubscriptionPlanResult appCMSSubscriptionPlanResult : appCMSSubscriptionPlanResultList) {
                                        SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
                                        subscriptionPlan.setSku(appCMSSubscriptionPlanResult.getIdentifier());
                                        subscriptionPlan.setPlanId(appCMSSubscriptionPlanResult.getId());
                                        subscriptionPlan.setSubscriptionPrice(
                                                appCMSSubscriptionPlanResult
                                                        .getPlanDetails()
                                                        .get(0)
                                                        .getRecurringPaymentAmount());
                                        createSubscriptionPlan(subscriptionPlan);
                                    }
                                    subscriptionPlans = getExistingSubscriptionPlans();
                                }
                            },
                            appCMSSubscriptionPlanResult -> {

                            },
                            appCMSUserSubscriptionResult -> {

                            });
                    appCMSSubscriptionPlanCall.call(
                            currentActivity.getString(R.string.app_cms_get_current_subscription_api_url,
                                    appCMSMain.getApiBaseUrl(),
                                    appCMSMain.getInternalName()),
                            R.string.app_cms_subscription_subscribed_plan_key,
                            null,
                            apikey,
                            getAuthToken(currentActivity),
                            listResult -> {
                            },
                            singleResult -> {
                            },
                            appCMSSubscriptionPlanResult -> {
                                if (appCMSSubscriptionPlanResult != null &&
                                        appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getPlanDetails() != null) {

                                    UserSubscriptionPlan userSubscriptionPlan = new UserSubscriptionPlan();
                                    userSubscriptionPlan.setUserId(getLoggedInUser(currentActivity));
                                    userSubscriptionPlan.setPlanReceipt(appCMSSubscriptionPlanResult.getSubscriptionInfo().getReceipt());
                                    userSubscriptionPlan.setPaymentHandler(appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler());
                                    SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
                                    subscriptionPlan.setSubscriptionPrice(appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getPlanDetails().get(0).getRecurringPaymentAmount());
                                    subscriptionPlan.setPlanId(appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getId());
                                    subscriptionPlan.setSku(appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getIdentifier());
                                    userSubscriptionPlan.setSubscriptionPlan(subscriptionPlan);

                                    RealmList<SubscriptionPlan> availableUpgradeSubscriptionPlans =
                                            new RealmList<>();
                                    RealmResults<SubscriptionPlan> allAvailableSubscriptionPlans =
                                            realmController.getAllSubscriptionPlans();
                                    if (allAvailableSubscriptionPlans != null) {
                                        availableUpgradeSubscriptionPlans.addAll(allAvailableSubscriptionPlans);
                                    }
                                    userSubscriptionPlan.setAvailableUpgrades(availableUpgradeSubscriptionPlans);
                                    realmController.addUserSubscriptionPlan(userSubscriptionPlan);

                                    setActiveSubscriptionSku(currentActivity,
                                            appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getIdentifier());
                                    setActiveSubscriptionId(currentActivity,
                                            appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getId());
                                    setActiveSubscriptionCurrency(currentActivity,
                                            appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getPlanDetails()
                                                    .get(0).getRecurringPaymentCurrencyCode());
                                    setActiveSubscriptionPlanName(currentActivity,
                                            appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getName());
                                    setActiveSubscriptionPrice(currentActivity, (float)
                                            appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getPlanDetails()
                                                    .get(0).getRecurringPaymentAmount());
                                    setActiveSubscriptionProcessor(currentActivity,
                                            appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler());
                                }
                            });
                } catch (Exception e) {
                    Log.e(TAG, "getSubscriptionPageContent: " + e.toString());
                }
            }
        }
    }

    public void refreshPageAPIData(AppCMSPageUI appCMSPageUI,
                                   String pageId,
                                   Action1<AppCMSPageAPI> appCMSPageAPIReadyAction) {
        getPageIdContent(appCMSMain.getApiBaseUrl(),
                pageIdToPageAPIUrlMap.get(pageId),
                appCMSMain.getInternalName(),
                true,
                getPageId(appCMSPageUI),
                appCMSPageAPIReadyAction);
    }

    public void login(String email, String password) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_signin_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSMain.getInternalName());
            startLoginAsyncTask(url,
                    email,
                    password,
                    false,
                    false);
        }
    }

    public void callRefreshIdentity(Action0 onReadyAction) {
        if (currentActivity != null) {
            refreshIdentity(getRefreshToken(currentActivity), onReadyAction);
        }
    }

    public LaunchType getLaunchType() {
        return launchType;
    }

    public void setLaunchType(LaunchType launchType) {
        this.launchType = launchType;
    }

    private void refreshGoogleAccessToken(Action1<GoogleRefreshTokenResponse> readyAction) {
        if (currentActivity != null) {
            googleRefreshTokenCall.refreshTokenCall(currentActivity.getString(R.string.google_authentication_refresh_token_api),
                    currentActivity.getString(R.string.google_authentication_refresh_token_api_grant_type),
                    currentActivity.getString(R.string.google_authentication_refresh_token_api_client_id),
                    currentActivity.getString(R.string.google_authentication_refresh_token_api_client_secret),
                    currentActivity.getString(R.string.google_authentication_refresh_token_api_refresh_token),
                    readyAction);
        }
    }

    private void startLoginAsyncTask(String url,
                                     String email,
                                     String password,
                                     boolean signup,
                                     boolean followWithSubscription) {
        PostAppCMSLoginRequestAsyncTask.Params params = new PostAppCMSLoginRequestAsyncTask.Params
                .Builder()
                .url(url)
                .email(email)
                .password(password)
                .build();
        new PostAppCMSLoginRequestAsyncTask(appCMSSignInCall,
                signInResponse -> {
                    if (signInResponse == null) {
                        // Show log error
                        Log.e(TAG, "Email and password are not valid.");
                        if (signup) {
                            showDialog(DialogType.SIGNUP, currentActivity.getString(
                                    R.string.app_cms_error_user_already_exists), false, null);
                        } else {
                            showDialog(DialogType.SIGNIN, currentActivity.getString(
                                    R.string.app_cms_error_email_password), false, null);
                        }
                    } else {
                        setRefreshToken(currentActivity, signInResponse.getRefreshToken());
                        setAuthToken(currentActivity, signInResponse.getAuthorizationToken());
                        setLoggedInUser(currentActivity, signInResponse.getUserId());
                        setLoggedInUserName(currentActivity, signInResponse.getName());
                        setLoggedInUserEmail(currentActivity, signInResponse.getEmail());

                        if (signup) {
                            AppsFlyerUtils.registrationEvent(currentActivity, signInResponse.getUserId(),
                                    currentActivity.getString(R.string.app_cms_appsflyer_dev_key));
                        } else {
                            AppsFlyerUtils.loginEvent(currentActivity, signInResponse.getUserId());
                        }

                        if (followWithSubscription) {
                            signupFromFacebook = false;
                            isSignupFromGoogle = false;
                            subscriptionUserEmail = email;
                            subscriptionUserPassword = password;
                            sendCloseOthersAction(null, true);
                            initiateItemPurchase();
                        } else {
                            refreshSubscriptionData();
                            if (entitlementPendingVideoData != null) {
                                sendCloseOthersAction(null, true);
                                launchButtonSelectedAction(entitlementPendingVideoData.pagePath,
                                        entitlementPendingVideoData.action,
                                        entitlementPendingVideoData.filmTitle,
                                        entitlementPendingVideoData.extraData,
                                        entitlementPendingVideoData.contentDatum,
                                        entitlementPendingVideoData.closeLauncher,
                                        entitlementPendingVideoData.currentlyPlayingIndex,
                                        entitlementPendingVideoData.relateVideoIds);
                                entitlementPendingVideoData.pagePath = null;
                                entitlementPendingVideoData.action = null;
                                entitlementPendingVideoData.filmTitle = null;
                                entitlementPendingVideoData.extraData = null;
                                entitlementPendingVideoData.contentDatum = null;
                                entitlementPendingVideoData.closeLauncher = false;
                                entitlementPendingVideoData.currentlyPlayingIndex = -1;
                                entitlementPendingVideoData.relateVideoIds = null;
                                entitlementPendingVideoData = null;
                            } else {
                                sendCloseOthersAction(null, true);
                                cancelInternalEvents();
                                restartInternalEvents();
                                NavigationPrimary homePageNavItem = findHomePageNavItem();
                                if (homePageNavItem != null) {
                                    cancelInternalEvents();
                                    navigateToPage(homePageNavItem.getPageId(),
                                            homePageNavItem.getTitle(),
                                            homePageNavItem.getUrl(),
                                            false,
                                            true,
                                            false,
                                            true,
                                            true,
                                            deeplinkSearchQuery);
                                }
                            }
                        }
                    }
                }).execute(params);
    }

    public void refreshIdentity(final String refreshToken, final Action0 onReadyAction) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_refresh_identity_api_url,
                    appCMSMain.getApiBaseUrl(),
                    refreshToken);
            GetAppCMSRefreshIdentityAsyncTask.Params params =
                    new GetAppCMSRefreshIdentityAsyncTask.Params
                            .Builder()
                            .url(url)
                            .build();
            new GetAppCMSRefreshIdentityAsyncTask(appCMSRefreshIdentityCall,
                    refreshIdentityResponse -> {
                        if (refreshIdentityResponse != null) {
                            setLoggedInUser(currentActivity, refreshIdentityResponse.getId());
                            setRefreshToken(currentActivity, refreshIdentityResponse.getRefreshToken());
                            setAuthToken(currentActivity, refreshIdentityResponse.getAuthorizationToken());
                            onReadyAction.call();
                        }
                    }).execute(params);
        }
    }

    public boolean isAppSVOD() {
        return jsonValueKeyMap.get(appCMSMain.getServiceType()) == AppCMSUIKeyType.MAIN_SVOD_SERVICE_TYPE;
    }

    public void setNavItemToCurrentAction(Activity activity) {
        if (activity != null && currentActions != null && currentActions.size() > 0) {
            Intent setNavigationItemIntent = new Intent(PRESENTER_RESET_NAVIGATION_ITEM_ACTION);
            setNavigationItemIntent.putExtra(activity.getString(R.string.navigation_item_key),
                    currentActions.peek());
            activity.sendBroadcast(setNavigationItemIntent);
        }
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    private Bundle getPageActivityBundle(Activity activity,
                                         AppCMSPageUI appCMSPageUI,
                                         AppCMSPageAPI appCMSPageAPI,
                                         String pageID,
                                         String pageName,
                                         String pagePath,
                                         String screenName,
                                         boolean loadFromFile,
                                         boolean appbarPresent,
                                         boolean fullscreenEnabled,
                                         boolean navbarPresent,
                                         boolean sendCloseAction,
                                         Uri searchQuery,
                                         ExtraScreenType extraScreenType) {
        Bundle args = new Bundle();
        AppCMSBinder appCMSBinder = getAppCMSBinder(activity,
                appCMSPageUI,
                appCMSPageAPI,
                pageID,
                pageName,
                pagePath,
                screenName,
                loadFromFile,
                appbarPresent,
                fullscreenEnabled,
                navbarPresent,
                sendCloseAction,
                searchQuery,
                extraScreenType);
        args.putBinder(activity.getString(R.string.app_cms_binder_key), appCMSBinder);
        return args;
    }

    private Bundle getAutoplayActivityBundle(Activity activity,
                                             AppCMSPageUI appCMSPageUI,
                                             AppCMSPageAPI appCMSPageAPI,
                                             String pageID,
                                             String pageName,
                                             String screenName,
                                             boolean loadFromFile,
                                             boolean appbarPresent,
                                             boolean fullscreenEnabled,
                                             boolean navbarPresent,
                                             boolean sendCloseAction,
                                             AppCMSVideoPageBinder binder) {
        Bundle args = new Bundle();
        binder.setAppCMSPageUI(appCMSPageUI);
        binder.setAppCMSPageAPI(appCMSPageAPI);
        binder.setPageID(pageID);
        binder.setPageName(pageName);
        binder.setScreenName(screenName);
        binder.setLoadFromFile(loadFromFile);
        binder.setAppbarPresent(appbarPresent);
        binder.setFullscreenEnabled(fullscreenEnabled);
        binder.setNavbarPresent(navbarPresent);
        binder.setSendCloseAction(sendCloseAction);
        args.putBinder(activity.getString(R.string.app_cms_video_player_binder_key), binder);
        return args;
    }

    private AppCMSDownloadQualityBinder getAppCMSDownloadQualityBinder(Activity activity,
                                                                       AppCMSPageUI appCMSPageUI,
                                                                       AppCMSPageAPI appCMSPageAPI,
                                                                       String pageId,
                                                                       String pageName,
                                                                       String screenName,
                                                                       boolean loadedFromFile,
                                                                       boolean appbarPresent,
                                                                       boolean fullScreenEnabled,
                                                                       boolean navbarPresent,
                                                                       ContentDatum contentDatum,
                                                                       Action1<UserVideoDownloadStatus> resultAction
    ) {
        return new AppCMSDownloadQualityBinder(appCMSMain,
                appCMSPageUI,
                appCMSPageAPI,
                pageId,
                pageName,
                screenName,
                loadedFromFile,
                appbarPresent,
                fullScreenEnabled,
                navbarPresent,
                isUserLoggedIn(activity),
                jsonValueKeyMap,
                contentDatum,
                resultAction);
    }

    private AppCMSBinder getAppCMSBinder(Activity activity,
                                         AppCMSPageUI appCMSPageUI,
                                         AppCMSPageAPI appCMSPageAPI,
                                         String pageID,
                                         String pageName,
                                         String pagePath,
                                         String screenName,
                                         boolean loadFromFile,
                                         boolean appbarPresent,
                                         boolean fullscreenEnabled,
                                         boolean navbarPresent,
                                         boolean sendCloseAction,
                                         Uri searchQuery,
                                         ExtraScreenType extraScreenType) {
        return new AppCMSBinder(appCMSMain,
                appCMSPageUI,
                appCMSPageAPI,
                navigation,
                pageID,
                pageName,
                pagePath,
                screenName,
                loadFromFile,
                appbarPresent,
                fullscreenEnabled,
                navbarPresent,
                sendCloseAction,
                isUserLoggedIn(activity),
                isUserSubscribed(activity),
                extraScreenType,
                jsonValueKeyMap,
                searchQuery);
    }

    private AppCMSVideoPageBinder getAppCMSVideoPageBinder(Activity activity,
                                                           AppCMSPageUI appCMSPageUI,
                                                           AppCMSPageAPI appCMSPageAPI,
                                                           String pageID,
                                                           String pageName,
                                                           String screenName,
                                                           boolean loadFromFile,
                                                           boolean appbarPresent,
                                                           boolean fullscreenEnabled,
                                                           boolean navbarPresent,
                                                           boolean sendCloseAction,
                                                           boolean playAds,
                                                           String fontColor,
                                                           String backgroundColor,
                                                           String adsUrl,
                                                           ContentDatum contentDatum,
                                                           boolean isTrailer,
                                                           List<String> relatedVideoIds,
                                                           int currentlyPlayingIndex,
                                                           boolean isOffline) {

        return new AppCMSVideoPageBinder(
                appCMSPageUI,
                appCMSPageAPI,
                pageID,
                pageName,
                screenName,
                loadFromFile,
                appbarPresent,
                fullscreenEnabled,
                navbarPresent,
                sendCloseAction,
                jsonValueKeyMap,
                playAds,
                fontColor,
                backgroundColor,
                adsUrl,
                contentDatum,
                isTrailer,
                isUserLoggedIn(activity),
                isUserSubscribed(activity),
                relatedVideoIds,
                currentlyPlayingIndex,
                isOffline);
    }

    private void launchPageActivity(Activity activity,
                                    AppCMSPageUI appCMSPageUI,
                                    AppCMSPageAPI appCMSPageAPI,
                                    String pageId,
                                    String pageName,
                                    String pagePath,
                                    String screenName,
                                    boolean loadFromFile,
                                    boolean appbarPresent,
                                    boolean fullscreenEnabled,
                                    boolean navbarPresent,
                                    boolean sendCloseAction,
                                    Uri searchQuery,
                                    ExtraScreenType extraScreenType) {
        Bundle args = getPageActivityBundle(activity,
                appCMSPageUI,
                appCMSPageAPI,
                pageId,
                pageName,
                pagePath,
                screenName,
                loadFromFile,
                appbarPresent,
                fullscreenEnabled,
                navbarPresent,
                sendCloseAction,
                searchQuery,
                extraScreenType);
        Intent appCMSIntent = new Intent(activity, AppCMSPageActivity.class);
        appCMSIntent.putExtra(activity.getString(R.string.app_cms_bundle_key), args);
        appCMSIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(appCMSIntent);
    }

    private void launchAutoplayActivity(Activity activity,
                                        AppCMSPageUI appCMSPageUI,
                                        AppCMSPageAPI appCMSPageAPI,
                                        String pageId,
                                        String pageName,
                                        String screenName,
                                        boolean loadFromFile,
                                        boolean appbarPresent,
                                        boolean fullscreenEnabled,
                                        boolean navbarPresent,
                                        boolean sendCloseAction,
                                        AppCMSVideoPageBinder binder) {
        if (currentActivity instanceof AppCMSPlayVideoActivity) {
            ((AppCMSPlayVideoActivity) currentActivity).closePlayer();
        }

        Bundle args = getAutoplayActivityBundle(activity,
                appCMSPageUI,
                appCMSPageAPI,
                pageId,
                pageName,
                screenName,
                loadFromFile,
                appbarPresent,
                fullscreenEnabled,
                navbarPresent,
                sendCloseAction,
                binder);
        Intent intent = new Intent(currentActivity, AutoplayActivity.class);
        intent.putExtra(currentActivity.getString(R.string.app_cms_video_player_bundle_binder_key), args);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        currentActivity.startActivity(intent);
    }

    private void launchDownloadQualityActivity(Activity activity,
                                               AppCMSPageUI appCMSPageUI,
                                               AppCMSPageAPI appCMSPageAPI,
                                               String pageId,
                                               String pageName,
                                               String screenName,
                                               boolean loadFromFile,
                                               boolean appbarPresent,
                                               boolean fullscreenEnabled,
                                               boolean navbarPresent,
                                               boolean sendCloseAction,
                                               AppCMSDownloadQualityBinder binder) {


        Bundle args = new Bundle();
        args.putBinder(activity.getString(R.string.app_cms_download_setting_binder_key), binder);
        Intent intent = new Intent(currentActivity, AppCMSDownloadQualityActivity.class);
        intent.putExtra(currentActivity.getString(R.string.app_cms_download_setting_bundle_key), args);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        currentActivity.startActivity(intent);
    }

    private void getAppCMSSite(final Activity activity,
                               final AppCMSMain main,
                               final PlatformType platformType) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_site_api_url,
                    main.getApiBaseUrl(),
                    main.getDomainName());
            new GetAppCMSSiteAsyncTask(appCMSSiteCall,
                    appCMSSite -> {
                        if (appCMSSite != null) {
                            apikey = appCMSSite.getGist().getAppAccess().getAppSecretKey();
                            AppCMSAPIComponent appCMSAPIComponent = DaggerAppCMSAPIComponent.builder()
                                    .appCMSAPIModule(new AppCMSAPIModule(activity,
                                            main.getApiBaseUrl(),
                                            apikey))
                                    .build();
                            appCMSPageAPICall = appCMSAPIComponent.appCMSPageAPICall();
                            appCMSStreamingInfoCall = appCMSAPIComponent.appCMSStreamingInfoCall();
                            appCMSVideoDetailCall = appCMSAPIComponent.appCMSVideoDetailCall();
                            clearMaps();
                            switch (platformType) {
                                case ANDROID:
                                    getAppCMSAndroid(activity, main, 0);
                                    break;
                                case TV:
                                    getAppCMSTV(activity, main, null);
                                    break;
                                default:
                            }
                        } else {
                            launchErrorActivity(activity, platformType);
                        }
                    }).execute(url);
        }
    }

    private void getAppCMSAndroid(final Activity activity, final AppCMSMain main, int tryCount) {
        if (!isUserLoggedIn(currentActivity) && tryCount == 0) {
            signinAnonymousUser(activity, main, tryCount);
        } else if (isUserLoggedIn(currentActivity) && shouldRefreshAuthToken() && tryCount == 0) {
            refreshIdentity(getRefreshToken(activity),
                    () -> getAppCMSAndroid(activity, main, tryCount + 1));
        } else {
            GetAppCMSAndroidUIAsyncTask.Params params =
                    new GetAppCMSAndroidUIAsyncTask.Params.Builder()
                            .url(activity.getString(R.string.app_cms_url_with_appended_timestamp,
                                    main.getAndroid(),
                                    main.getTimestamp()))
                            .loadFromFile(loadFromFile)
                            .build();
            Log.d(TAG, "Params: " + main.getAndroid() + " " + loadFromFile);
            new GetAppCMSAndroidUIAsyncTask(appCMSAndroidUICall, appCMSAndroidUI -> {
                if (appCMSAndroidUI == null ||
                        appCMSAndroidUI.getMetaPages() == null ||
                        appCMSAndroidUI.getMetaPages().size() < 1) {
                    Log.e(TAG, "AppCMS keys for pages for appCMSAndroidUI not found");
                    launchErrorActivity(activity, platformType);
                } else {
                    initializeGA(appCMSAndroidUI.getAnalytics().getGoogleAnalyticsId());
                    navigation = appCMSAndroidUI.getNavigation();
                    queueMetaPages(appCMSAndroidUI.getMetaPages());
                    final MetaPage firstPage = pagesToProcess.peek();
                    Log.d(TAG, "Processing meta pages queue");
                    processMetaPagesQueue(activity,
                            main,
                            loadFromFile,
                            new Action0() {
                                @Override
                                public void call() {
                                    if (firstPage != null) {
                                        Log.d(TAG, "Launching first page: "
                                                + firstPage.getPageName());
                                        boolean appbarPresent =
                                                (jsonValueKeyMap.get(firstPage.getPageName())
                                                        != AppCMSUIKeyType.ANDROID_SPLASH_SCREEN_KEY);
                                        boolean fullscreen = !appbarPresent;
                                        refreshSubscriptionData();
                                        boolean launchSuccess = navigateToPage(firstPage.getPageId(),
                                                firstPage.getPageName(),
                                                firstPage.getPageUI(),
                                                true,
                                                appbarPresent,
                                                fullscreen,
                                                appbarPresent,
                                                false,
                                                deeplinkSearchQuery);
                                        if (!launchSuccess) {
                                            Log.e(TAG, "Failed to launch page: "
                                                    + firstPage.getPageName());
                                            launchErrorActivity(currentActivity, platformType);
                                        }
                                    }
                                }
                            });
                }
            }).execute(params);
        }
    }

    private void getAppCMSPage(String url,
                               final Action1<AppCMSPageUI> onPageReady,
                               boolean loadFromFile) {
        GetAppCMSPageUIAsyncTask.Params params =
                new GetAppCMSPageUIAsyncTask.Params.Builder()
                        .url(url)
                        .loadFromFile(loadFromFile).build();
        new GetAppCMSPageUIAsyncTask(appCMSPageUICall, onPageReady).execute(params);
    }

    private void queueMetaPages(List<MetaPage> metaPageList) {
        if (pagesToProcess == null) {
            pagesToProcess = new ConcurrentLinkedQueue<>();
        }
        if (metaPageList.size() > 0) {
            int loginPageIndex = getSigninPage(metaPageList);
            if (loginPageIndex >= 0) {
                loginPage = metaPageList.get(loginPageIndex);
            }
            int DownloadQualitysIndex = getdownloadQualityPage(metaPageList);
            if (DownloadQualitysIndex >= 0) {
                downloadQualityPage = metaPageList.get(DownloadQualitysIndex);
            }
            int homePageIndex = getHomePage(metaPageList);
            if (homePageIndex >= 0) {
                homePage = metaPageList.get(homePageIndex);
            }
            int subscriptionPageIndex = getSubscriptionPage(metaPageList);
            if (subscriptionPageIndex >= 0) {
                subscriptionPage = metaPageList.get(subscriptionPageIndex);
            }
            int splashScreenIndex = getSplashPage(metaPageList);
            if (splashScreenIndex >= 0) {
                splashPage = metaPageList.get(splashScreenIndex);
            }
            int pageToQueueIndex = -1;
            if (jsonValueKeyMap.get(appCMSMain.getServiceType()) == AppCMSUIKeyType.MAIN_SVOD_SERVICE_TYPE
                    && !isUserLoggedIn(currentActivity)) {
                launchType = LaunchType.LOGIN_AND_SIGNUP;
                if (appCMSMain.isForceLogin()) {
                    pageToQueueIndex = splashScreenIndex;
                }
            }
            if (pageToQueueIndex == -1) {
                pageToQueueIndex = homePageIndex;
            }
            if (pageToQueueIndex >= 0) {
                pagesToProcess.add(metaPageList.get(pageToQueueIndex));
                Log.d(TAG, "Queuing meta page: " +
                        metaPageList.get(pageToQueueIndex).getPageName() + ": " +
                        metaPageList.get(pageToQueueIndex).getPageId() + " " +
                        metaPageList.get(pageToQueueIndex).getPageUI() + " " +
                        metaPageList.get(pageToQueueIndex).getPageAPI());
                metaPageList.remove(pageToQueueIndex);
                queueMetaPages(metaPageList);
            } else {
                pagesToProcess.addAll(metaPageList);
            }
        }
    }

    private void processMetaPagesQueue(final Activity activity,
                                       final AppCMSMain main,
                                       final boolean loadFromFile,
                                       final Action0 onPagesFinishedAction) {
        final MetaPage metaPage = pagesToProcess.remove();

        Log.d(TAG, "Processing meta page " +
                metaPage.getPageName() + ": " +
                metaPage.getPageId() + " " +
                metaPage.getPageUI() + " " +
                metaPage.getPageAPI());
        pageIdToPageAPIUrlMap.put(metaPage.getPageId(), metaPage.getPageAPI());
        pageIdToPageNameMap.put(metaPage.getPageId(), metaPage.getPageName());

        getAppCMSPage(activity.getString(R.string.app_cms_url_with_appended_timestamp,
                metaPage.getPageUI(),
                main.getTimestamp()),
                appCMSPageUI -> {
                    navigationPages.put(metaPage.getPageId(), appCMSPageUI);
                    String action = pageNameToActionMap.get(metaPage.getPageName());
                    if (action != null && actionToPageMap.containsKey(action)) {
                        actionToPageMap.put(action, appCMSPageUI);
                        actionToPageNameMap.put(action, metaPage.getPageName());
                        actionToPageAPIUrlMap.put(action, metaPage.getPageAPI());
                        actionTypeToMetaPageMap.put(actionToActionTypeMap.get(action), metaPage);
                        Log.d(TAG, "Action: " + action + "  PageAPI URL: "
                                + metaPage.getPageAPI());
                    }
                    if (pagesToProcess.size() > 0) {
                        processMetaPagesQueue(activity,
                                main,
                                loadFromFile,
                                onPagesFinishedAction);
                    } else {
                        onPagesFinishedAction.call();
                    }
                },
                loadFromFile);
    }

    /**
     * Temp method for loading download Quality screen from Assets till json is not updated at Server
     */
    public AppCMSPageUI getDataFromFile(String fileName) {
        StringBuilder buf = new StringBuilder();
        try {
            InputStream json = currentActivity.getAssets().open(fileName);
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();

        return gson.fromJson(buf.toString().trim(), AppCMSPageUI.class);
    }

    private int getSplashPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName())
                    == AppCMSUIKeyType.ANDROID_SPLASH_SCREEN_KEY) {
                return i;
            }
        }
        return -1;
    }

    private int getSigninPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName())
                    == AppCMSUIKeyType.ANDROID_AUTH_SCREEN_KEY) {
                return i;
            }
        }
        return -1;
    }

    private int getdownloadQualityPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName())
                    == AppCMSUIKeyType.ANDROID_DOWNLOAD_SETTINGS_KEY) {
                return i;
            }
        }
        return -1;
    }

    private int getHomePage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName())
                    == AppCMSUIKeyType.ANDROID_HOME_SCREEN_KEY) {
                return i;
            }
        }
        return -1;
    }

    private int getSubscriptionPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName())
                    == AppCMSUIKeyType.ANDROID_SUBSCRIPTION_SCREEN_KEY) {
                return i;
            }
        }
        return -1;
    }

    private String getAutoplayPageId() {

        for (Map.Entry<String, String> entry : pageIdToPageNameMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.equals(currentActivity.getString(R.string.app_cms_page_autoplay_key))) {
                return key;
            }
        }
        return null;
    }

    private String getPageId(AppCMSPageUI appCMSPageUI) {
        for (String key : navigationPages.keySet()) {
            if (navigationPages.get(key) == appCMSPageUI) {
                return key;
            }
        }
        return null;
    }

    private void clearMaps() {
        navigationPages.clear();
        navigationPageData.clear();
        pageIdToPageAPIUrlMap.clear();
        actionToPageAPIUrlMap.clear();
        actionToPageNameMap.clear();
        pageIdToPageNameMap.clear();
    }

    private void getAppCMSTV(final Activity activity, final AppCMSMain main, final Uri searchQuery) {
        GetAppCMSAndroidUIAsyncTask.Params params =
                new GetAppCMSAndroidUIAsyncTask.Params.Builder()
                        .url(activity.getString(R.string.app_cms_url_with_appended_timestamp,
                                main.getAndroid(),
                                main.getTimestamp()))
                        .loadFromFile(loadFromFile)
                        .build();
        Log.d(TAG, "Params: " + main.getAndroid() + " " + loadFromFile);
        new GetAppCMSAndroidUIAsyncTask(appCMSAndroidUICall, appCMSAndroidUI -> {
            if (appCMSAndroidUI == null ||
                    appCMSAndroidUI.getMetaPages() == null ||
                    appCMSAndroidUI.getMetaPages().size() < 1) {
                Log.e(TAG, "AppCMS keys for pages for appCMSAndroidUI not found");
                launchErrorActivity(activity, PlatformType.TV);
            } else {
                //TODO : change navigation object as per TV.
                Navigation navigationTV = new GsonBuilder().create().fromJson
                        (MainUtils.loadJsonFromAssets(currentActivity, "navigation.json"), Navigation.class);

                navigation = navigationTV; //appCMSAndroidUI.getNavigation();
                queueMetaPages(appCMSAndroidUI.getMetaPages());
                final MetaPage firstPage = pagesToProcess.peek();
                Log.d(TAG, "Processing meta pages queue");
                processMetaPagesQueue(activity,
                        main,
                        loadFromFile,
                        new Action0() {
                            @Override
                            public void call() {
                                Log.d(TAG, "Launching first page: " + firstPage.getPageName());
                                cancelInternalEvents();
                                NavigationPrimary homePageNav = findHomePageNavItem();
                                boolean launchSuccess = navigateToTVPage(homePageNav.getPageId(),
                                        homePageNav.getTitle(),
                                        homePageNav.getUrl(),
                                        true,
                                        searchQuery);
                                if (!launchSuccess) {
                                    Log.e(TAG, "Failed to launch page: "
                                            + firstPage.getPageName());
                                    launchErrorActivity(currentActivity, PlatformType.TV);
                                }
                            }
                        });
            }
        }).execute(params);
    }

    public boolean navigateToTVPage(String pageId,
                                    String pageTitle,
                                    String url,
                                    boolean launchActivity,
                                    Uri searchQuery) {
        boolean result = false;
        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {
            loadingPage = true;
            Log.d(TAG, "Launching page " + pageTitle + ": " + pageId);
            // Log.d(TAG, "Search query (optional): " + searchQuery);
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);
            AppCMSPageAPI appCMSPageAPI = navigationPageData.get(pageId);

            currentActivity.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));

            if (appCMSPageAPI == null) {
                getPageIdContent(appCMSMain.getApiBaseUrl(),
                        pageIdToPageAPIUrlMap.get(pageId),
                        appCMSMain.getInternalName(),
                        true,
                        getPageId(appCMSPageUI),
                        new AppCMSPageAPIAction(true,
                                false,
                                true,
                                appCMSPageUI,
                                pageId,
                                pageId,
                                pageTitle,
                                pageId,
                                launchActivity,
                                false,
                                searchQuery) {
                            @Override
                            public void call(AppCMSPageAPI appCMSPageAPI) {
                                if (appCMSPageAPI != null) {
                                    cancelInternalEvents();
                                    pushActionInternalEvents(this.pageId
                                            + BaseView.isLandscape(currentActivity));
                                    navigationPageData.put(this.pageId, appCMSPageAPI);
                                    if (this.launchActivity) {
                                        launchTVPageActivity(currentActivity,
                                                this.appCMSPageUI,
                                                appCMSPageAPI,
                                                this.pageId,
                                                this.pageTitle,
                                                pageIdToPageNameMap.get(this.pageId),
                                                loadFromFile,
                                                this.appbarPresent,
                                                this.fullscreenEnabled,
                                                this.navbarPresent,
                                                this.searchQuery);

                                        setNavItemToCurrentAction(currentActivity);

                                    } else {
                                        Bundle args = getPageActivityBundle(currentActivity,
                                                this.appCMSPageUI,
                                                appCMSPageAPI,
                                                this.pageId,
                                                this.pageTitle,
                                                this.pagePath,
                                                pageIdToPageNameMap.get(this.pageId),
                                                loadFromFile,
                                                this.appbarPresent,
                                                this.fullscreenEnabled,
                                                this.navbarPresent,
                                                false,
                                                this.searchQuery,
                                                ExtraScreenType.NONE);
                                        Intent updatePageIntent =
                                                new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                        updatePageIntent.putExtra(
                                                currentActivity.getString(R.string.app_cms_bundle_key),
                                                args);
                                        currentActivity.sendBroadcast(updatePageIntent);

                                        setNavItemToCurrentAction(currentActivity);
                                    }
                                } else {
                                    sendStopLoadingPageAction();
                                    setNavItemToCurrentAction(currentActivity);
                                }
                                loadingPage = false;
                            }
                        });
            } else {
                cancelInternalEvents();
                pushActionInternalEvents(pageId);
                if (launchActivity) {
                    launchTVPageActivity(currentActivity,
                            appCMSPageUI,
                            appCMSPageAPI,
                            pageId,
                            pageTitle,
                            pageIdToPageNameMap.get(pageId),
                            loadFromFile,
                            true,
                            false,
                            true,
                            searchQuery);
                    setNavItemToCurrentAction(currentActivity);
                } else {
                    Bundle args = getPageActivityBundle(currentActivity,
                            appCMSPageUI,
                            appCMSPageAPI,
                            pageId,
                            pageTitle,
                            pageId,
                            pageIdToPageNameMap.get(pageId),
                            loadFromFile,
                            true,
                            false,
                            true,
                            false,
                            searchQuery,
                            ExtraScreenType.NONE);
                    Intent updatePageIntent =
                            new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                    updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                            args);
                    currentActivity.sendBroadcast(updatePageIntent);
                    setNavItemToCurrentAction(currentActivity);
                }

                loadingPage = false;
            }
            result = true;
        } else if (currentActivity != null &&
                !TextUtils.isEmpty(url) &&
                url.contains(currentActivity.getString(R.string.app_cms_page_navigation_contact_us_key))) {
            if (Apptentive.canShowMessageCenter()) {
                Apptentive.showMessageCenter(currentActivity);
            }
        } else {
            Log.d(TAG, "Resetting page navigation to previous tab");
            setNavItemToCurrentAction(currentActivity);
        }
        return result;
    }

    private void launchTVPageActivity(Activity activity,
                                      AppCMSPageUI appCMSPageUI,
                                      AppCMSPageAPI appCMSPageAPI,
                                      String pageId,
                                      String pageName,
                                      String screenName,
                                      boolean loadFromFile,
                                      boolean appbarPresent,
                                      boolean fullscreenEnabled,
                                      boolean navbarPresent,
                                      Uri searchQuery) {
        Bundle args = getPageActivityBundle(activity,
                appCMSPageUI,
                appCMSPageAPI,
                pageId,
                pageName,
                pageId,
                screenName,
                loadFromFile,
                appbarPresent,
                fullscreenEnabled,
                navbarPresent,
                false,
                searchQuery,
                ExtraScreenType.NONE);

        try {
            Intent appCMSIntent = new Intent(activity, Class.forName(tvHomeScreenPackage));
            appCMSIntent.putExtra(activity.getString(R.string.app_cms_bundle_key), args);
            appCMSIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(appCMSIntent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sendStopLoadingPageAction();
        }
    }

    public void playNextVideo(AppCMSVideoPageBinder binder,
                              int currentlyPlayingIndex,
                              long watchedTime) {
        sendCloseOthersAction(null, true);
        if (!binder.isOffline()) {
            launchVideoPlayer(binder.getContentData(),
                    currentlyPlayingIndex,
                    binder.getRelateVideoIds(),
                    watchedTime / 1000L);
        } else {
            String permalink = binder.getContentData().getGist().getPermalink();
            String action = currentActivity.getString(R.string.app_cms_action_watchvideo_key);
            String title = binder.getContentData().getGist().getTitle();
            String hlsUrl = binder.getContentData().getGist().getLocalFileUrl();
            String[] extraData = new String[4];
            extraData[0] = permalink;
            extraData[1] = hlsUrl;
            extraData[2] = binder.getContentData().getGist().getId();
            extraData[3] = "true"; // to know that this is an offline video
            Log.d(TAG, "Launching " + permalink + ": " + action);

            if (!launchButtonSelectedAction(
                    permalink,
                    action,
                    title,
                    extraData,
                    binder.getContentData(),
                    false,
                    binder.getCurrentPlayingVideoIndex(),
                    binder.getRelateVideoIds())) {
                Log.e(TAG, "Could not launch action: " +
                        " permalink: " +
                        permalink +
                        " action: " +
                        action +
                        " hlsUrl: " +
                        hlsUrl);
            }
        }
    }

    public Map<String, AppCMSUIKeyType> getJsonValueKeyMap() {
        return jsonValueKeyMap;
    }

    /**
     * Method opens the autoplay screen when one movie finishes playing
     *
     * @param binder binder to share data
     */
    public void openAutoPlayScreen(final AppCMSVideoPageBinder binder) {
        String url = null;
        if (!binder.isOffline()) {
            final String filmId =
                    binder.getRelateVideoIds().get(binder.getCurrentPlayingVideoIndex() + 1);
            if (currentActivity != null &&
                    !loadingPage && appCMSMain != null &&
                    !TextUtils.isEmpty(appCMSMain.getApiBaseUrl()) &&
                    !TextUtils.isEmpty(appCMSMain.getSite())) {
                url = currentActivity.getString(R.string.app_cms_video_detail_api_url,
                        appCMSMain.getApiBaseUrl(),
                        filmId,
                        appCMSMain.getSite());
            }
        } else {
            ContentDatum contentDatum = realmController.getDownloadById(
                    binder.getRelateVideoIds().get(
                            binder.getCurrentPlayingVideoIndex() + 1))
                    .convertToContentDatum(getLoggedInUser(currentActivity));
            binder.setCurrentPlayingVideoIndex(binder.getCurrentPlayingVideoIndex() + 1);
            binder.setContentData(contentDatum);
        }
        String pageId = getAutoplayPageId();
        if (!TextUtils.isEmpty(pageId)) {
            navigateToAutoplayPage(pageId,
                    currentActivity.getString(R.string.app_cms_page_autoplay_key),
                    url,
                    binder);
        } else {
            Log.e(TAG, "Can't find autoplay page ui in pageIdToPageNameMap");
        }
    }

    public void getRelatedMedia(String filmIds, final Action1<AppCMSVideoDetail> action1) {

        String url = currentActivity.getString(R.string.app_cms_video_detail_api_url,
                appCMSMain.getApiBaseUrl(),
                filmIds,
                appCMSMain.getSite());
        GetAppCMSVideoDetailAsyncTask.Params params =
                new GetAppCMSVideoDetailAsyncTask.Params.Builder().url(url)
                        .authToken(getAuthToken(currentActivity)).build();
        new GetAppCMSVideoDetailAsyncTask(appCMSVideoDetailCall,
                action1).execute(params);
    }

    public boolean launchTVButtonSelectedAction(String pagePath,
                                                String action,
                                                String filmTitle,
                                                String[] extraData,
                                                final boolean closeLauncher) {
        boolean result = false;
        Log.d(TAG, "Attempting to load page " + filmTitle + ": " + pagePath);
        if (!isNetworkConnected()) {
            showDialog(DialogType.NETWORK, null, false, null); //TODO : Need to change Error Dialog for TV.
        } else if (currentActivity != null && !loadingPage) {
            AppCMSActionType actionType = actionToActionTypeMap.get(action);
            if (actionType == null) {
                Log.e(TAG, "Action " + action + " not found!");
                return false;
            }
            result = true;
            if (actionType == AppCMSActionType.PLAY_VIDEO_PAGE ||
                    actionType == AppCMSActionType.WATCH_TRAILER) {
                sendStopLoadingPageAction();
                Intent playVideoIntent = new Intent(currentActivity, AppCMSPlayVideoActivity.class);
                try {
                    Class videoPlayer = Class.forName(tvVideoPlayerPackage);
                    playVideoIntent = new Intent(currentActivity, videoPlayer);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (actionType == AppCMSActionType.PLAY_VIDEO_PAGE) {
                    boolean requestAds = true;
                    if (pagePath != null && pagePath.contains(currentActivity
                            .getString(R.string.app_cms_action_qualifier_watchvideo_key))) {
                        requestAds = false;
                    }
                    playVideoIntent.putExtra(currentActivity.getString(R.string.play_ads_key), requestAds);
                } else {
                    playVideoIntent.putExtra(currentActivity.getString(R.string.play_ads_key), false);
                }

                playVideoIntent.putExtra(currentActivity.getString(R.string.video_player_font_color_key),
                        appCMSMain.getBrand().getGeneral().getTextColor());
                playVideoIntent.putExtra(currentActivity.getString(R.string.video_player_title_key),
                        filmTitle);
                playVideoIntent.putExtra(currentActivity.getString(R.string.video_player_hls_url_key),
                        extraData);

                Date now = new Date();
                playVideoIntent.putExtra(currentActivity.getString(R.string.video_player_ads_url_key),
                        currentActivity.getString(R.string.app_cms_ads_api_url,
                                getPermalinkCompletePath(pagePath),
                                now.getTime(),
                                appCMSMain.getSite()));
                playVideoIntent.putExtra(currentActivity.getString(R.string.app_cms_bg_color_key),
                        appCMSMain.getBrand()
                                .getGeneral()
                                .getBackgroundColor());
                if (closeLauncher) {
                    sendCloseOthersAction(null, true);
                }
                currentActivity.startActivity(playVideoIntent);
            } else if (actionType == AppCMSActionType.SHARE) {
                if (extraData != null && extraData.length > 0) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, extraData[0]);
                    sendIntent.setType(currentActivity.getString(R.string.text_plain_mime_type));
                    currentActivity.startActivity(Intent.createChooser(sendIntent,
                            currentActivity.getResources().getText(R.string.send_to)));
                }
            } else if (actionType == AppCMSActionType.CLOSE) {
                sendCloseOthersAction(null, true);
            } else if (actionType == AppCMSActionType.LOGIN) {
                Log.d(TAG, "Login action selected: " + extraData[0]);
                closeSoftKeyboard();
                login(extraData[0], extraData[1]);
            } else if (actionType == AppCMSActionType.FORGOT_PASSWORD) {
                Log.d(TAG, "Forgot password selected: " + extraData[0]);
                closeSoftKeyboard();
                launchResetPasswordPage(extraData[0]);
            } else if (actionType == AppCMSActionType.LOGIN_FACEBOOK) {
                Log.d(TAG, "Login Facebook selected");
                loginFacebook();
            } else if (actionType == AppCMSActionType.SIGNUP) {
                Log.d(TAG, "Sign-Up selected: " + extraData[0]);
                closeSoftKeyboard();
                signup(extraData[0], extraData[1]);
            } else {
                boolean appbarPresent = true;
                boolean fullscreenEnabled = false;
                boolean navbarPresent = true;
                final StringBuffer screenName = new StringBuffer();
                if (!TextUtils.isEmpty(actionToPageNameMap.get(action))) {
                    screenName.append(actionToPageNameMap.get(action));
                }
                loadingPage = true;
                switch (actionType) {
                    case AUTH_PAGE:
                        appbarPresent = false;
                        fullscreenEnabled = false;
                        navbarPresent = false;
                        break;

                    case VIDEO_PAGE:
                        appbarPresent = true;
                        fullscreenEnabled = false;
                        navbarPresent = false;
                        screenName.append(currentActivity.getString(R.string.app_cms_template_page_separator));
                        screenName.append(filmTitle);
                        break;

                    case PLAY_VIDEO_PAGE:
                        appbarPresent = false;
                        fullscreenEnabled = false;
                        navbarPresent = false;
                        break;

                    case HOME_PAGE:
                    default:
                        break;
                }
                currentActivity.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
                AppCMSPageUI appCMSPageUI = actionToPageMap.get(action);
                getPageIdContent(appCMSMain.getApiBaseUrl(),
                        actionToPageAPIUrlMap.get(action),
                        appCMSMain.getSite(),
                        false,
                        pagePath,
                        new AppCMSPageAPIAction(appbarPresent,
                                fullscreenEnabled,
                                navbarPresent,
                                appCMSPageUI,
                                action,
                                getPageId(appCMSPageUI),
                                filmTitle,
                                pagePath,
                                false,
                                closeLauncher,
                                null) {
                            @Override
                            public void call(AppCMSPageAPI appCMSPageAPI) {
                                if (appCMSPageAPI != null) {
                                    cancelInternalEvents();
                                    pushActionInternalEvents(this.action + BaseView.isLandscape(currentActivity));
                                    Bundle args = getPageActivityBundle(currentActivity,
                                            this.appCMSPageUI,
                                            appCMSPageAPI,
                                            this.pageId,
                                            appCMSPageAPI.getTitle(),
                                            pagePath,
                                            screenName.toString(),
                                            loadFromFile,
                                            this.appbarPresent,
                                            this.fullscreenEnabled,
                                            this.navbarPresent,
                                            this.sendCloseAction,
                                            this.searchQuery,
                                            ExtraScreenType.NONE);
                                    Intent updatePageIntent =
                                            new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                    updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                            args);
                                    currentActivity.sendBroadcast(updatePageIntent);
                                } else {
                                    sendStopLoadingPageAction();
                                }
                                loadingPage = false;
                            }
                        });
            }
        }
        return result;
    }

    public void showLoadingDialog(boolean showDialog) {
        if (showDialog) {
            currentActivity.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
        } else {
            currentActivity.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION));
        }
    }

    public enum LaunchType {
        SUBSCRIBE, LOGIN_AND_SIGNUP
    }

    public enum PlatformType {
        ANDROID, TV
    }

    public enum BeaconEvent {
        PLAY, RESUME, PING, AD_REQUEST, AD_IMPRESSION
    }

    public enum DialogType {
        NETWORK,
        SIGNIN,
        SIGNUP,
        RESET_PASSWORD,
        CANCEL_SUBSCRIPTION,
        SUBSCRIBE,
        SUBSCRIPTION_REQUIRED,
        LOGIN_AND_SUBSCRIPTION_REQUIRED,
        EXISTING_SUBSCRIPTION,
        DOWNLOAD_INCOMPLETE
    }

    public enum ExtraScreenType {
        NAVIGATION,
        SEARCH,
        RESET_PASSWORD,
        EDIT_PROFILE,
        NONE
    }

    private static class BeaconRunnable implements Runnable {
        final AppCMSBeaconRest appCMSBeaconRest;
        String url;

        public BeaconRunnable(AppCMSBeaconRest appCMSBeaconRest) {
            this.appCMSBeaconRest = appCMSBeaconRest;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            appCMSBeaconRest.sendBeaconMessage(url).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Log.d(TAG, "Succeeded to send Beacon message: " + response.code());
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.d(TAG, "Failed to send Beacon message: " + t.getMessage());
                }
            });
        }
    }

    private static abstract class AppCMSPageAPIAction implements Action1<AppCMSPageAPI> {
        boolean appbarPresent;
        boolean fullscreenEnabled;
        boolean navbarPresent;
        AppCMSPageUI appCMSPageUI;
        String action;
        String pageId;
        String pageTitle;
        String pagePath;
        boolean launchActivity;
        boolean sendCloseAction;
        Uri searchQuery;

        public AppCMSPageAPIAction(boolean appbarPresent,
                                   boolean fullscreenEnabled,
                                   boolean navbarPresent,
                                   AppCMSPageUI appCMSPageUI,
                                   String action,
                                   String pageId,
                                   String pageTitle,
                                   String pagePath,
                                   boolean launchActivity,
                                   boolean sendCloseAction,
                                   Uri searchQuery) {
            this.appbarPresent = appbarPresent;
            this.fullscreenEnabled = fullscreenEnabled;
            this.navbarPresent = navbarPresent;
            this.appCMSPageUI = appCMSPageUI;
            this.action = action;
            this.pageId = pageId;
            this.pageTitle = pageTitle;
            this.pagePath = pagePath;
            this.launchActivity = launchActivity;
            this.sendCloseAction = sendCloseAction;
            this.searchQuery = searchQuery;
        }
    }

    private static abstract class AppCMSWatchlistAPIAction implements Action1<AppCMSWatchlistResult> {
        boolean appbarPresent;
        boolean fullscreenEnabled;
        boolean navbarPresent;
        AppCMSPageUI appCMSPageUI;
        String action;
        String pageId;
        String pageTitle;
        String pagePath;
        boolean launchActivity;
        Uri searchQuery;

        public AppCMSWatchlistAPIAction(boolean appbarPresent,
                                        boolean fullscreenEnabled,
                                        boolean navbarPresent,
                                        AppCMSPageUI appCMSPageUI,
                                        String action,
                                        String pageId,
                                        String pageTitle,
                                        String pagePath,
                                        boolean launchActivity,
                                        Uri searchQuery) {
            this.appbarPresent = appbarPresent;
            this.fullscreenEnabled = fullscreenEnabled;
            this.navbarPresent = navbarPresent;
            this.appCMSPageUI = appCMSPageUI;
            this.action = action;
            this.pageId = pageId;
            this.pageTitle = pageTitle;
            this.pagePath = pagePath;
            this.launchActivity = launchActivity;
            this.searchQuery = searchQuery;
        }
    }

    private static abstract class AppCMSHistoryAPIAction implements Action1<AppCMSHistoryResult> {
        boolean appbarPresent;
        boolean fullscreenEnabled;
        boolean navbarPresent;
        AppCMSPageUI appCMSPageUI;
        String action;
        String pageId;
        String pageTitle;
        String pagePath;
        boolean launchActivity;
        Uri searchQuery;

        public AppCMSHistoryAPIAction(boolean appbarPresent,
                                      boolean fullscreenEnabled,
                                      boolean navbarPresent,
                                      AppCMSPageUI appCMSPageUI,
                                      String action,
                                      String pageId,
                                      String pageTitle,
                                      String pagePath,
                                      boolean launchActivity,
                                      Uri searchQuery) {
            this.appbarPresent = appbarPresent;
            this.fullscreenEnabled = fullscreenEnabled;
            this.navbarPresent = navbarPresent;
            this.appCMSPageUI = appCMSPageUI;
            this.action = action;
            this.pageId = pageId;
            this.pageTitle = pageTitle;
            this.pagePath = pagePath;
            this.launchActivity = launchActivity;
            this.searchQuery = searchQuery;
        }
    }

    private static abstract class AppCMSSubscriptionAPIAction
            implements Action1<AppCMSSubscriptionResult> {

        boolean appbarPresent;
        boolean fullscreenEnabled;
        boolean navbarPresent;
        AppCMSPageUI appCMSPageUI;
        String action;
        String pageId;
        String pageTitle;
        boolean launchActivity;
        Uri searchQuery;

        public AppCMSSubscriptionAPIAction(boolean appbarPresent,
                                           boolean fullscreenEnabled,
                                           boolean navbarPresent,
                                           AppCMSPageUI appCMSPageUI,
                                           String action,
                                           String pageId,
                                           String pageTitle,
                                           boolean launchActivity,
                                           Uri searchQuery) {
            this.appbarPresent = appbarPresent;
            this.fullscreenEnabled = fullscreenEnabled;
            this.navbarPresent = navbarPresent;
            this.appCMSPageUI = appCMSPageUI;
            this.action = action;
            this.pageId = pageId;
            this.pageTitle = pageTitle;
            this.launchActivity = launchActivity;
            this.searchQuery = searchQuery;
        }
    }

    private static class EntitlementPendingVideoData {
        String pagePath;
        String action;
        String filmTitle;
        String[] extraData;
        ContentDatum contentDatum;
        boolean closeLauncher;
        int currentlyPlayingIndex;
        List<String> relateVideoIds;
    }
}