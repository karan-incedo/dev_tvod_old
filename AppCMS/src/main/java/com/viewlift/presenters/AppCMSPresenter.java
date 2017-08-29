package com.viewlift.presenters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.viewlift.R;
import com.viewlift.analytics.AppsFlyerUtils;
import com.viewlift.casting.CastHelper;
import com.viewlift.ccavenue.screens.WebViewActivity;
import com.viewlift.ccavenue.utility.AvenuesParams;
import com.viewlift.models.billing.appcms.authentication.GoogleRefreshTokenResponse;
import com.viewlift.models.billing.appcms.subscriptions.InAppPurchaseData;
import com.viewlift.models.billing.appcms.subscriptions.SkuDetails;
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
import com.viewlift.models.data.appcms.beacon.AppCMSBeaconRequest;
import com.viewlift.models.data.appcms.beacon.BeaconRequest;
import com.viewlift.models.data.appcms.beacon.OfflineBeaconData;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.downloads.DownloadVideoRealm;
import com.viewlift.models.data.appcms.downloads.RealmController;
import com.viewlift.models.data.appcms.downloads.UserVideoDownloadStatus;
import com.viewlift.models.data.appcms.history.AppCMSDeleteHistoryResult;
import com.viewlift.models.data.appcms.history.AppCMSHistoryResult;
import com.viewlift.models.data.appcms.history.UpdateHistoryRequest;
import com.viewlift.models.data.appcms.history.UserVideoStatusResponse;
import com.viewlift.models.data.appcms.sites.AppCMSSite;
import com.viewlift.models.data.appcms.subscriptions.AppCMSSubscriptionPlanResult;
import com.viewlift.models.data.appcms.subscriptions.AppCMSSubscriptionResult;
import com.viewlift.models.data.appcms.subscriptions.Receipt;
import com.viewlift.models.data.appcms.subscriptions.UserSubscriptionPlan;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.MetaPage;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.ui.android.NavigationUser;
import com.viewlift.models.data.appcms.ui.authentication.UserIdentity;
import com.viewlift.models.data.appcms.ui.authentication.UserIdentityPassword;
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
import com.viewlift.models.network.background.tasks.GetAppCMSStreamingInfoAsyncTask;
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
import com.viewlift.models.network.rest.AppCMSBeaconCall;
import com.viewlift.models.network.rest.AppCMSBeaconRest;
import com.viewlift.models.network.rest.AppCMSCCAvenueCall;
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
import com.viewlift.views.binders.RetryCallBinder;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.fragments.AppCMSMoreFragment;
import com.viewlift.views.fragments.AppCMSNavItemsFragment;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import io.realm.RealmList;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.BUTTON_ACTION;
import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.PAGE_ACTION;
import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.SEARCH_RETRY_ACTION;
import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.VIDEO_ACTION;

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
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_FOR_DOWNLOADS = 2002;
    public static final String PRESENTER_DIALOG_ACTION = "appcms_presenter_dialog_action";
    public static final String SEARCH_ACTION = "SEARCH_ACTION";
    public static final String ERROR_DIALOG_ACTION = "appcms_error_dialog_action";
    public static final String ACTION_LOGO_ANIMATION = "appcms_logo_animation";
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
    private static final String NETWORK_CONNECTED_SHARED_PREF_NAME = "network_connected_share_pref_name";
    private static final String WIFI_CONNECTED_SHARED_PREF_NAME = "wifi_connected_shared_pref_name";
    private static final String ACTIVE_SUBSCRIPTION_SKU = "active_subscription_sku_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_ID = "active_subscription_id_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_CURRENCY = "active_subscription_currency_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_RECEIPT = "active_subscription_token_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_PLAN_NAME = "active_subscription_plan_name_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_PRICE_NAME = "active_subscription_plan_price_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_PROCESSOR_NAME = "active_subscription_payment_processor_key";
    private static final String IS_USER_SUBSCRIBED = "is_user_subscribed_pref_key";
    private static final String AUTO_PLAY_ENABLED_PREF_NAME = "autoplay_enabled_pref_key";
    private static final String EXISTING_GOOGLE_PLAY_SUBSCRIPTION_DESCRIPTION = "existing_google_play_subscription_title_pref_key";
    private static final String EXISTING_GOOGLE_PLAY_SUBSCRIPTION_ID = "existing_google_play_subscription_id_key_pref_key";
    private static final String EXISTING_GOOGLE_PLAY_SUBSCRIPTION_SUSPENDED = "existing_google_play_subscription_suspended_pref_key";
    private static final String EXISTING_GOOGLE_PLAY_SUBSCRIPTION_PRICE = "existing_google_play_subscription_price_pref_key";
    private static final String USER_DOWNLOAD_QUALITY_SHARED_PREF_NAME = "user_download_quality_pref";
    private static final String USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME = "user_download_sd_card_pref";

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
    private final String USER_ID_KEY = "user_id";
    private final String LOGIN_STATUS_KEY = "logged_in_status";
    private final String LOGIN_STATUS_LOGGED_IN = "logged_in";
    private final String LOGIN_STATUS_LOGGED_OUT = "not_logged_in";
    private final String SUBSCRIPTION_STATUS_KEY = "subscription_status";
    private final String SUBSCRIPTION_SUBSCRIBED = "subscribed";
    private final String SUBSCRIPTION_NOT_SUBSCRIBED = "unsubscribed";
    private final String SUBSCRIPTION_PLAN_ID = "cur_sub_plan_id";
    private final String SUBSCRIPTION_PLAN_NAME = "cur_sub_plan_name";
    private final String FIREBASE_SIGN_UP_EVENT = "sign_up";
    private final String FIREBASE_SIGN_UP_METHOD = "sign_up_method";
    private final String FIREBASE_SIGN_In_EVENT = "sign_in";
    private final String FIREBASE_SIGN_IN_METHOD = "sign_in_method";
    private final String FIREBASE_EMAIL_METHOD = "email";
    private final String FIREBASE_FACEBOOK_METHOD = "Facebook";
    private final String FIREBASE_GOOGLE_METHOD = "Google";
    private final String FIREBASE_PLAN_ID = "item_id";
    private final String FIREBASE_PLAN_NAME = "item_name";
    private final String FIREBASE_CURRENCY_NAME = "currency";
    private final String FIREBASE_VALUE = "value";
    private final String FIREBASE_TRANSACTION_ID = "transaction_id";
    private final String FIREBASE_ADD_CART = "add_to_cart";
    private final String FIREBASE_ECOMMERCE_PURCHASE = "ecommerce_purchase";
    private final String FIREBASE_CHANGE_SUBSCRIPTION = "change_subscription";
    private final String FIREBASE_CANCEL_SUBSCRIPTION = "cancel_subscription";
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

    private final AppCMSCCAvenueCall appCMSCCAvenueCall;

    private final AppCMSUpdateWatchHistoryCall appCMSUpdateWatchHistoryCall;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final Map<String, String> pageNameToActionMap;
    private final Map<String, AppCMSPageUI> actionToPageMap;
    private final Map<String, AppCMSPageAPI> actionToPageAPIMap;
    private final Map<String, AppCMSActionType> actionToActionTypeMap;
    private final AppCMSWatchlistCall appCMSWatchlistCall;
    private final AppCMSHistoryCall appCMSHistoryCall;
    private final AppCMSUserDownloadVideoStatusCall appCMSUserDownloadVideoStatusCall;
    private final AppCMSBeaconCall appCMSBeaconCall;

    private final AppCMSUserVideoStatusCall appCMSUserVideoStatusCall;
    private final AppCMSAddToWatchlistCall appCMSAddToWatchlistCall;
    private final AppCMSDeleteHistoryCall appCMSDeleteHistoryCall;
    private final AppCMSSubscriptionCall appCMSSubscriptionCall;
    private final AppCMSSubscriptionPlanCall appCMSSubscriptionPlanCall;
    private final AppCMSAnonymousAuthTokenCall appCMSAnonymousAuthTokenCall;
    public String[] physicalPaths = {
            "/storage/sdcard0", "/storage/sdcard1", // Motorola Xoom
            "/storage/extsdcard", // Samsung SGS3
            "/storage/sdcard0/external_sdcard", // User request
            "/mnt/extsdcard", "/mnt/sdcard/external_sd", // Samsung galaxy family
            "/mnt/external_sd", "/mnt/media_rw/sdcard1", // 4.4.2 on CyanogenMod S3
            "/removable/microsd", // Asus transformer prime
            "/mnt/emmc", "/storage/external_SD", // LG
            "/storage/ext_sd", // HTC One Max
            "/storage/removable/sdcard1", // Sony Xperia Z1
            "/data/sdext", "/data/sdext2", "/data/sdext3", "/data/sdext4", "/sdcard1", // Sony Xperia Z
            "/sdcard2", // HTC One M8s
            "/storage/microsd" // ASUS ZenFone 2
    };
    boolean isRenewable;
    private AppCMSPageAPICall appCMSPageAPICall;
    private AppCMSStreamingInfoCall appCMSStreamingInfoCall;
    private AppCMSVideoDetailCall appCMSVideoDetailCall;
    private Activity currentActivity;
    private Navigation navigation;
    private boolean loadFromFile;
    private boolean loadingPage;
    private AppCMSMain appCMSMain;
    private AppCMSSite appCMSSite;
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
    private MetaPage downloadPage;
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
    private String currencyCode;
    private String countryCode;
    private String currencyOfPlanToPurchase;
    private String planToPurchaseName;
    private String apikey;
    private double planToPurchasePrice;
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
    private List<Timer> downloadProgressTimerList = new ArrayList<Timer>();
    private ContentDatum downloadContentDatumAfterPermissionGranted;
    private Action1<UserVideoDownloadStatus> downloadResultActionAfterPermissionGranted;
    private boolean requestDownloadQualityScreen;
    private DownloadQueueThread downloadQueueThread;
    private boolean isVideoPlayerStarted;

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
                           AppCMSBeaconCall appCMSBeaconCall,
                           AppCMSAddToWatchlistCall appCMSAddToWatchlistCall,

                           AppCMSCCAvenueCall appCMSCCAvenueCall,

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
        this.appCMSBeaconCall = appCMSBeaconCall;
        this.appCMSAddToWatchlistCall = appCMSAddToWatchlistCall;

        this.appCMSCCAvenueCall = appCMSCCAvenueCall;

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
                            try {
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
                            } catch (Exception e) {
                                Log.e(TAG, "Error retrieving AppCMS Video Details: " + e.getMessage());
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
            updateHistoryRequest.setSiteOwner(appCMSSite.getGist().getSiteInternalName());

            String url = currentActivity.getString(R.string.app_cms_update_watch_history_api_url,
                    appCMSMain.getApiBaseUrl());

            appCMSUpdateWatchHistoryCall.call(url, getAuthToken(currentActivity),
                    updateHistoryRequest, s -> {
                        try {
                            if (currentActivity != null) {
                                sendUpdateHistoryAction();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error updating watched time: " + e.getMessage());
                        }
                    });
            currentActivity.runOnUiThread(() -> {
                try {
                    // copyFromRealm is used to get an unmanaged in-memory copy of an already
                    // persisted RealmObject
                    DownloadVideoRealm downloadedVideo = realmController.getRealm()
                            .copyFromRealm(realmController.getDownloadById(filmId));
                    downloadedVideo.setWatchedTime(watchedTime);
                    downloadedVideo.setLastWatchDate(System.currentTimeMillis());
                    realmController.updateDownload(downloadedVideo);
                } catch (Exception e) {
                    Log.e(TAG, "Film " + filmId + " has not been downloaded");
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
                    () -> {
                        String url = currentActivity.getString(R.string.app_cms_video_status_api_url,
                                appCMSMain.getApiBaseUrl(), filmId, appCMSSite.getGist().getSiteInternalName());
                        appCMSUserVideoStatusCall.call(url, getAuthToken(currentActivity), responseAction);
                    });
        } else {
            String url = currentActivity.getString(R.string.app_cms_video_status_api_url,
                    appCMSMain.getApiBaseUrl(), filmId, appCMSSite.getGist().getSiteInternalName());
            appCMSUserVideoStatusCall.call(url, getAuthToken(currentActivity), responseAction);
        }
    }

    public void getUserVideoDownloadStatus(String filmId, Action1<UserVideoDownloadStatus> responseAction, String userId) {
        appCMSUserDownloadVideoStatusCall.call(filmId, this, responseAction, userId);
    }

    public void signinAnonymousUser() {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_anonymous_auth_token_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSSite.getGist().getSiteInternalName());
            appCMSAnonymousAuthTokenCall.call(url, anonymousAuthTokenResponse -> {
                try {
                    if (anonymousAuthTokenResponse != null) {
                        setAnonymousUserToken(currentActivity, anonymousAuthTokenResponse.getAuthorizationToken());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error signing in as anonymous user: " + e.getMessage());
                }
            });
        }
    }

    public void signinAnonymousUser(int tryCount,
                                    Uri searchQuery,
                                    PlatformType platformType) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_anonymous_auth_token_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSSite.getGist().getSiteInternalName());
            appCMSAnonymousAuthTokenCall.call(url, anonymousAuthTokenResponse -> {
                if (anonymousAuthTokenResponse != null) {
                    setAnonymousUserToken(currentActivity, anonymousAuthTokenResponse.getAuthorizationToken());
                    if (tryCount == 0) {
                        if (platformType == PlatformType.ANDROID) {
                            getAppCMSAndroid(tryCount + 1);
                        } else if (platformType == PlatformType.TV) {
                            getAppCMSTV(tryCount + 1);
                        }
                    } else {
                        showDialog(DialogType.NETWORK, null, false, null);
                    }
                } else {
                    if (platformType == PlatformType.TV) {
                        getAppCMSTV(tryCount + 1);
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
        boolean isVideoOffline = false;
        try {
            isVideoOffline = Boolean.parseBoolean(extraData[3]);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        if (!isNetworkConnected() && !isVideoOffline) { //checking isVideoOffline here to fix SVFA-1431 in offline mode
            // showDialog(DialogType.NETWORK, null, false, null);
            // Fix of SVFA-1435
            showDialog(DialogType.NETWORK,
                    currentActivity.getString(R.string.app_cms_network_connectivity_error_message_download),
                    true,
                    () -> navigateToDownloadPage(downloadPage.getPageId(),
                            downloadPage.getPageName(), downloadPage.getPageUI(), false));
        } else {
            final AppCMSActionType actionType = actionToActionTypeMap.get(action);

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
                boolean isTrailer = (actionType == AppCMSActionType.WATCH_TRAILER ||
                        (pagePath != null &&
                                pagePath.contains(currentActivity.getString(R.string.app_cms_action_qualifier_watchvideo_key))));
                if ((actionType == AppCMSActionType.PLAY_VIDEO_PAGE ||
                        actionType == AppCMSActionType.WATCH_TRAILER) &&
                        !isVideoPlayerStarted) {
                    isVideoPlayerStarted = true;
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

                        //Send Firebase Analytics when user is subscribed and user is Logged In
                        sendFirebaseLoginSubscribeSuccess();

                        String adsUrl;
                        if (actionType == AppCMSActionType.PLAY_VIDEO_PAGE) {
                            if (pagePath != null && pagePath.contains(
                                    currentActivity.getString(R.string.app_cms_action_qualifier_watchvideo_key))) {
                                requestAds = false;
                            }
                            playVideoIntent.putExtra(currentActivity.getString(R.string.play_ads_key), requestAds);

                            if (contentDatum != null &&
                                    contentDatum.getGist() != null &&
                                    !TextUtils.isEmpty(contentDatum.getGist().getId())) {
                                String filmId = contentDatum.getGist().getId();
                                try {
                                    DownloadVideoRealm downloadedVideo = realmController.getRealm()
                                            .copyFromRealm(realmController.getDownloadById(filmId));
                                    if (downloadedVideo != null) {
                                        if (isVideoOffline && !isNetworkConnected()) {
                                            long timeAfterDownloadMsec = System.currentTimeMillis() -
                                                    downloadedVideo.getDownloadDate();

                                            if ((timeAfterDownloadMsec / (1000 * 60 * 60 * 24)) >= 30) {
                                                showDialog(DialogType.DOWNLOAD_NOT_AVAILABLE,
                                                        currentActivity.getString(R.string.app_cms_download_limit_message),
                                                        false,
                                                        null);
                                                isVideoPlayerStarted = false;
                                                return false;
                                            }

                                            contentDatum.getGist().setWatchedTime(downloadedVideo.getWatchedTime());
                                        }
                                        if (isNetworkConnected() && !downloadedVideo.isSyncedWithServer()) {
                                            updateWatchedTime(filmId, downloadedVideo.getWatchedTime());
                                            downloadedVideo.setSyncedWithServer(true);
                                        } else if (!isNetworkConnected() && isVideoOffline) {
                                            downloadedVideo.setSyncedWithServer(false);
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Film " + filmId + " has not been downloaded");
                                }
                            }

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

                        if (!getAutoplayEnabledUserPref(currentActivity)) {
                            relateVideoIds = null;
                            currentlyPlayingIndex = -1;
                        }

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
                        isVideoPlayerStarted = false;
                    }
                } else if (actionType == AppCMSActionType.SHARE) {
                    if (extraData.length > 0) {
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
                    Log.d(TAG, "Facebook Login selected");
                    loginFacebook();
                    sendSignInFacebookFirebase();
                } else if (actionType == AppCMSActionType.SIGNUP_FACEBOOK) {
                    Log.d(TAG, "Facebook Signup selected");
                    loginFacebook();
                    sendSignUpFacebookFirebase();
                } else if (actionType == AppCMSActionType.LOGIN_GOOGLE) {
                    Log.d(TAG, "Google Login selected");
                    loginGoogle();
                    sendSignInGoogleFirebase();
                } else if (actionType == AppCMSActionType.SIGNUP_GOOGLE) {
                    Log.d(TAG, "Google signup selected");
                    loginGoogle();
                    sendSignUpGoogleFirebase();
                } else {
                    if (actionType == AppCMSActionType.SIGNUP) {
                        Log.d(TAG, "Sign-Up selected: " + extraData[0]);
                        closeSoftKeyboard();
                        signup(extraData[0], extraData[1]);
                        sendSignUpEmailFirebase();
                    } else if (actionType == AppCMSActionType.START_TRIAL) {
                        Log.d(TAG, "Start Trial selected");
                        navigateToSubscriptionPlansPage(null, null);
                    } else if (actionType == AppCMSActionType.EDIT_PROFILE) {
                        launchEditProfilePage();
                    } else if (actionType == AppCMSActionType.CHANGE_PASSWORD) {
                        launchChangePasswordPage();
                    } else if (actionType == AppCMSActionType.MANAGE_SUBSCRIPTION) {
                        if (extraData != null && extraData.length > 0) {
                            String key = extraData[0];
                            if (jsonValueKeyMap.get(key) == AppCMSUIKeyType.PAGE_SETTINGS_UPGRADE_PLAN_PROFILE_KEY) {
                                String paymentProcessor = getActiveSubscriptionProcessor(currentActivity);
                                if (!TextUtils.isEmpty(paymentProcessor) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly))) {
                                    showEntitlementDialog(DialogType.CANNOT_UPGRADE_SUBSCRIPTION);
                                } else if (TextUtils.isEmpty(paymentProcessor)) {
                                    showEntitlementDialog(DialogType.UNKNOWN_SUBSCRIPTION_FOR_UPGRADE);
                                } else if (isExistingGooglePlaySubscriptionSuspended(currentActivity) &&
                                        !upgradesAvailableForUser(getLoggedInUser(currentActivity))) {
                                    showEntitlementDialog(DialogType.UPGRADE_UNAVAILABLE);
                                } else {
                                    navigateToSubscriptionPlansPage(null, null);
                                }
                            } else if (jsonValueKeyMap.get(key) == AppCMSUIKeyType.PAGE_SETTINGS_CANCEL_PLAN_PROFILE_KEY) {
                                String paymentProcessor = getActiveSubscriptionProcessor(currentActivity);
                                if ((!TextUtils.isEmpty(paymentProcessor) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly)))) {
                                    showEntitlementDialog(DialogType.CANNOT_CANCEL_SUBSCRIPTION);
                                } else if (!TextUtils.isEmpty(paymentProcessor) &&
                                        TextUtils.isEmpty(getExistingGooglePlaySubscriptionId(currentActivity))) {
                                    showEntitlementDialog(DialogType.CANNOT_CANCEL_SUBSCRIPTION);
                                } else if (isUserSubscribed(currentActivity) && TextUtils.isEmpty(paymentProcessor)) {
                                    showEntitlementDialog(DialogType.UNKNOWN_SUBSCRIPTION_FOR_CANCEL);
                                } else {
                                    sendSubscriptionCancellation();
                                }
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
                                appCMSSite.getGist().getSiteInternalName(),
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
                                                                !module.getContentData().isEmpty()) {
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
                                                                        if (args != null) {
                                                                            Intent updatePageIntent =
                                                                                    new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                                                            updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                                                                    args);
                                                                            currentActivity.sendBroadcast(updatePageIntent);
                                                                            dismissOpenDialogs(null);
                                                                        }
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
                                                if (args != null) {
                                                    Intent updatePageIntent =
                                                            new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                                    updatePageIntent.putExtra(
                                                            currentActivity.getString(R.string.app_cms_bundle_key),
                                                            args);
                                                    currentActivity.sendBroadcast(updatePageIntent);
                                                }
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

    public void setVideoPlayerHasStarted() {
        isVideoPlayerStarted = false;
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
            if (args != null) {
                Intent updatePageIntent =
                        new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                updatePageIntent.putExtra(
                        currentActivity.getString(R.string.app_cms_bundle_key),
                        args);
                currentActivity.sendBroadcast(updatePageIntent);
            }

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
                            toContentDatum.getGist().setUpdateDate(fromContentDatum.getGist().getUpdateDate());
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
            if (args != null) {
                Intent updatePageIntent =
                        new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                updatePageIntent.putExtra(
                        currentActivity.getString(R.string.app_cms_bundle_key),
                        args);
                currentActivity.sendBroadcast(updatePageIntent);
            }
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
            if (args != null) {
                Intent updatePageIntent =
                        new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                updatePageIntent.putExtra(
                        currentActivity.getString(R.string.app_cms_bundle_key),
                        args);
                currentActivity.sendBroadcast(updatePageIntent);
            }
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
            if (args != null) {
                Intent updatePageIntent =
                        new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                updatePageIntent.putExtra(
                        currentActivity.getString(R.string.app_cms_bundle_key),
                        args);
                currentActivity.sendBroadcast(updatePageIntent);
            }
        }
    }

    public void launchChangePasswordPage() {
        if (currentActivity != null) {
            cancelInternalEvents();

            Bundle args = getPageActivityBundle(currentActivity,
                    null,
                    null,
                    currentActivity.getString(R.string.app_cms_change_password_page_tag),
                    currentActivity.getString(R.string.app_cms_change_password_page_tag),
                    null,
                    currentActivity.getString(R.string.app_cms_change_password_page_tag),
                    false,
                    true,
                    false,
                    false,
                    false,
                    null,
                    ExtraScreenType.CHANGE_PASSWORD);
            if (args != null) {
                Intent updatePageIntent =
                        new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                updatePageIntent.putExtra(
                        currentActivity.getString(R.string.app_cms_bundle_key),
                        args);
                currentActivity.sendBroadcast(updatePageIntent);
            }
        }
    }

    public void loginFacebook() {
        if (currentActivity != null) {
            signupFromFacebook = true;
            LoginManager.getInstance().logInWithReadPermissions(currentActivity,
                    Arrays.asList("public_profile", "email", "user_friends"));
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
                                              double planPrice,
                                              String recurringPaymentCurrencyCode,
                                              String countryCode,
                                              boolean isRenewable) {
        if (currentActivity != null) {
            launchType = LaunchType.SUBSCRIBE;
            skuToPurchase = sku;
            planToPurchase = planId;
            planToPurchaseName = planName;
            currencyOfPlanToPurchase = currency;
            planToPurchasePrice = planPrice;
            currencyCode = recurringPaymentCurrencyCode;
            this.countryCode = countryCode;
            this.isRenewable = isRenewable;
            if (isUserLoggedIn(currentActivity)) {
                initiateItemPurchase();
            } else {
                navigateToLoginPage();
            }
        }
    }

    private void initiateCCAvenuePurchase() {
        Log.v("authtoken", getAuthToken(currentActivity));
        Log.v("apikey", apikey);
        try {
            String strAmount = Double.toString(planToPurchasePrice);
            Intent intent = new Intent(currentActivity, WebViewActivity.class);
            intent.putExtra(AvenuesParams.CURRENCY, currencyCode);
            //intent.putExtra(AvenuesParams.AMOUNT, "500");
            intent.putExtra(AvenuesParams.AMOUNT, strAmount);
            intent.putExtra(currentActivity.getString(R.string.app_cms_site_name), appCMSSite.getGist().getSiteInternalName());
            intent.putExtra(currentActivity.getString(R.string.app_cms_user_id), getLoggedInUser(currentActivity));
            intent.putExtra(currentActivity.getString(R.string.app_cms_plan_id), planToPurchase);
            intent.putExtra("plan_to_purchase_name", planToPurchaseName);


            intent.putExtra("siteId", appCMSSite.getGist().getSiteInternalName());
            intent.putExtra("email", getLoggedInUserEmail(currentActivity));
            intent.putExtra("authorizedUserName", getLoggedInUser(currentActivity));
            intent.putExtra("x-api-token", apikey);
            intent.putExtra("auth_token", getAuthToken(currentActivity));
            intent.putExtra("renewable", isRenewable);
            currentActivity.startActivityForResult(intent, 1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initiateItemPurchase() {
        if (countryCode.equalsIgnoreCase("IN")) {
            initiateCCAvenuePurchase();
        } else {
            if (currentActivity != null &&
                    inAppBillingService != null) {
                try {
                    Bundle activeSubs = inAppBillingService.getPurchases(3,
                            currentActivity.getPackageName(),
                            "subs",
                            null);
                    ArrayList<String> subscribedSkus = activeSubs.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");

                    Bundle buyIntentBundle;
                    if (subscribedSkus != null && !subscribedSkus.isEmpty()) {
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
    }

    public void sendSubscriptionCancellation() {
        if (currentActivity != null) {
            String paymentProcessor = getActiveSubscriptionProcessor(currentActivity);
            if (!TextUtils.isEmpty(getExistingGooglePlaySubscriptionId(currentActivity)) ||
                    (!TextUtils.isEmpty(paymentProcessor) &&
                            (paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) ||
                                    paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly))))) {
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
                                            appCMSSite.getGist().getSiteInternalName(),
                                            currentActivity.getString(R.string.app_cms_subscription_platform_key)),
                                    R.string.app_cms_subscription_plan_cancel_key,
                                    subscriptionRequest,
                                    apikey,
                                    getAuthToken(currentActivity),
                                    result -> {
                                    },
                                    appCMSSubscriptionPlanResults -> {
                                        sendCloseOthersAction(null, true);

                                        AppsFlyerUtils.subscriptionEvent(currentActivity,
                                                false,
                                                currentActivity.getString(R.string.app_cms_appsflyer_dev_key),
                                                getActiveSubscriptionPrice(currentActivity),
                                                subscriptionRequest.getPlanId(),
                                                subscriptionRequest.getCurrencyCode());

                                        //Subscription Succes Firebase Log Event
                                        Bundle bundle = new Bundle();
                                        bundle.putString(FIREBASE_PLAN_ID, getActiveSubscriptionId(currentActivity));
                                        bundle.putString(FIREBASE_PLAN_NAME, getActiveSubscriptionPlanName(currentActivity));
                                        bundle.putString(FIREBASE_CURRENCY_NAME, getActiveSubscriptionCurrency(currentActivity));
                                        bundle.putString(FIREBASE_VALUE, getActiveSubscriptionPrice(currentActivity));
                                        //bundle.putString(FIREBASE_TRANSACTION_ID,get);
                                        if (mFireBaseAnalytics != null)
                                            mFireBaseAnalytics.logEvent(FIREBASE_CANCEL_SUBSCRIPTION, bundle);
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
                appCMSSite.getGist().getSiteInternalName(),
                getLoggedInUser(currentActivity),
                filmId);

        //Firebase Succesfull Login Check on WatchList Add and Remove
        mFireBaseAnalytics.setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_IN);


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
                            if (add) {
                                displayWatchlistToast("Added to Watchlist");
                            } else {
                                displayWatchlistToast("Removed from Watchlist");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "addToWatchlistContent: " + e.toString());
                        }
                    }, request, add);
        } catch (Exception e) {
            Log.e(TAG, "Error editing watchlist: " + e.getMessage());
        }
    }

    private void displayWatchlistToast(String toastMessage) {
        LayoutInflater inflater = currentActivity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout,
                (ViewGroup) currentActivity.findViewById(R.id.custom_toast_layout_root));

        TextView watchlistToastMessage = (TextView) layout.findViewById(R.id.custom_toast_message);
        watchlistToastMessage.setText(toastMessage);

        Toast toast = new Toast(currentActivity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.FILL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
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

    public void removeDownloadAndLogout() {
        for (DownloadVideoRealm downloadVideoRealm :
                realmController.getAllUnfinishedDownloades(getLoggedInUser(currentActivity))) {
            removeDownloadedFile(downloadVideoRealm.getVideoId());
        }
        cancelInternalEvents();
        logout();
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
        downloadContentDatumAfterPermissionGranted = null;
        downloadResultActionAfterPermissionGranted = null;

        //Send Firebase Analytics when user is subscribed and user is Logged In
        sendFirebaseLoginSubscribeSuccess();
        if (isPreferedStorageLocationSDCard(currentActivity) &&
                !hasWriteExternalStoragePermission()) {
            requestDownloadQualityScreen = true;
            askForPermissionToDownloadToExternalStorage(true,
                    contentDatum,
                    resultAction1);
        } else {
            AppCMSPageAPI apiData = new AppCMSPageAPI();
            List<Module> moduleList = new ArrayList<>();
            Module module = new Module();

            getUserDownloadQualityPref(currentActivity);

            List<ContentDatum> contentData = new ArrayList<>();
            ContentDatum contentDatumLocal = new ContentDatum();
            StreamingInfo streamingInfo = new StreamingInfo();
            VideoAssets videoAssets = new VideoAssets();
            List<Mpeg> mpegs = new ArrayList<>();

            String[] renditionValueArray = {"1080p", "720p", "360p"};
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
    }

    public long getRemainingDownloadSize() {
        List<DownloadVideoRealm> remainDownloads = getRealmController().getAllUnfinishedDownloades(getLoggedInUser(currentActivity));
        long bytesRemainDownload = 0L;
        for (DownloadVideoRealm downloadVideoRealm : remainDownloads) {

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadVideoRealm.getVideoId_DM());
            Cursor c = downloadManager.query(query);
            if (c != null) {
                if (c.moveToFirst()) {
                    long totalSize = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    long downloaded = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    totalSize -= downloaded;
                    bytesRemainDownload += totalSize;
                }
                c.close();
            }

        }
        return bytesRemainDownload / (1024L * 1024L);
    }

    public long getMegabytesAvailable(File f) {
        StatFs stat = new StatFs(f.getPath());
        long bytesAvailable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = (long) stat.getBlockSizeLong() * (long) stat.getAvailableBlocksLong();
        } else {
            bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        }
        return bytesAvailable / (1024L * 1024L);
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
        downloadContentDatumAfterPermissionGranted = null;
        downloadResultActionAfterPermissionGranted = null;
        // Check for low storage.  If there is low storage, the native library will not be
        // downloaded, so detection will not become operational.
        IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
        boolean hasLowStorage = currentActivity.registerReceiver(null, lowstorageFilter) != null;

        //Send Firebase Analytics when user is subscribed and user is Logged In
        sendFirebaseLoginSubscribeSuccess();

        if (isPreferedStorageLocationSDCard(currentActivity) &&
                !hasWriteExternalStoragePermission()) {
            requestDownloadQualityScreen = false;
            askForPermissionToDownloadToExternalStorage(true,
                    contentDatum,
                    resultAction1);
        } else if (hasLowStorage) {
            showDialog(DialogType.DOWNLOAD_FAILED, currentActivity.getString(R.string.app_cms_download_failed_error_message), false, null);

            Log.w(TAG, currentActivity.getString(R.string.app_cms_download_failed_error_message));
        } else {
            if (downloadQueueThread != null) {
                DownloadQueueItem downloadQueueItem = new DownloadQueueItem();
                downloadQueueItem.contentDatum = contentDatum;
                downloadQueueItem.resultAction1 = resultAction1;
                downloadQueueThread.addToQueue(downloadQueueItem);
                if (!downloadQueueThread.running()) {
                    downloadQueueThread.start();
                }
            }
        }
    }

    public void createLocalEntry(long enqueueId,
                                 long thumbEnqueueId,
                                 long posterEnqueueId,
                                 long ccEnqueueId,
                                 ContentDatum contentDatum,
                                 String downloadURL) {
        DownloadVideoRealm downloadVideoRealm = new DownloadVideoRealm();

        if (contentDatum != null && contentDatum.getGist() != null) {
            downloadVideoRealm.setVideoThumbId_DM(thumbEnqueueId);
            downloadVideoRealm.setPosterThumbId_DM(posterEnqueueId);
            downloadVideoRealm.setVideoId_DM(enqueueId);

            if (contentDatum.getGist().getId() != null) {
                downloadVideoRealm.setVideoId(contentDatum.getGist().getId());
                downloadVideoRealm.setVideoImageUrl(getPngPosterPath(contentDatum.getGist().getId()));
                downloadVideoRealm.setPosterFileURL(getPngPosterPath(contentDatum.getGist().getId()));
            }
            if (contentDatum.getGist().getTitle() != null) {
                downloadVideoRealm.setVideoTitle(contentDatum.getGist().getTitle());
            }
            if (contentDatum.getGist().getDescription() != null) {
                downloadVideoRealm.setVideoDescription(contentDatum.getGist().getDescription());
            }
            downloadVideoRealm.setLocalURI(downloadedMediaLocalURI(enqueueId));

            if (ccEnqueueId != 0 && contentDatum.getGist().getId() != null) {
                downloadVideoRealm.setSubtitlesId_DM(ccEnqueueId);
                downloadVideoRealm.setSubtitlesFileURL(getPngPosterPath(contentDatum.getGist().getId()));
            }
            if (contentDatum.getGist().getVideoImageUrl() != null) {
                downloadVideoRealm.setVideoFileURL(contentDatum.getGist().getVideoImageUrl()); //This change has been done due to making thumb image available at time of videos are downloading.
            }

            downloadVideoRealm.setVideoWebURL(downloadURL);
            downloadVideoRealm.setDownloadDate(System.currentTimeMillis());
            downloadVideoRealm.setVideoDuration(contentDatum.getGist().getRuntime());
            downloadVideoRealm.setWatchedTime(contentDatum.getGist().getWatchedTime());

            downloadVideoRealm.setPermalink(contentDatum.getGist().getPermalink());
            downloadVideoRealm.setDownloadStatus(DownloadStatus.STATUS_PENDING);
            downloadVideoRealm.setUserId(getLoggedInUser(currentActivity));

        }
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
                    .setVisibleInDownloadsUi(false)
                    .setShowRunningNotification(true);

            if (isPreferedStorageLocationSDCard(currentActivity)) {
                downloadRequest.setDestinationUri(Uri.fromFile(new File(getSDCardPath(currentActivity, "thumbs"),
                        filename + MEDIA_SURFIX_JPG)));
            } else {
                downloadRequest.setDestinationInExternalFilesDir(currentActivity, "thumbs",
                        filename + MEDIA_SURFIX_JPG);
            }
            enqueueId = downloadManager.enqueue(downloadRequest);


        } catch (Exception e) {
            Log.e(TAG, "Error downloading video image " + downloadURL + ": " + e.getMessage());
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
                    .setVisibleInDownloadsUi(false)
                    .setShowRunningNotification(true);

            if (isPreferedStorageLocationSDCard(currentActivity)) {
                downloadRequest.setDestinationUri(Uri.fromFile(new File(getSDCardPath(currentActivity, "posters"),
                        filename + MEDIA_SURFIX_JPG)));
            } else {
                downloadRequest.setDestinationInExternalFilesDir(currentActivity, "posters",
                        filename + MEDIA_SURFIX_JPG);
            }

            enqueueId = downloadManager.enqueue(downloadRequest);


        } catch (Exception e) {
            Log.e(TAG, "Error downloading poster image for download " + downloadURL + ": " + e.getMessage());
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
                    .setVisibleInDownloadsUi(false)
                    .setShowRunningNotification(true);

            if (isPreferedStorageLocationSDCard(currentActivity)) {
                downloadRequest.setDestinationUri(Uri.fromFile(new File(getSDCardPath(currentActivity, "closedCaptions"),
                        filename + MEDIA_SUFFIX_SRT)));
            } else {
                downloadRequest.setDestinationInExternalFilesDir(currentActivity, "closedCaptions",
                        filename + MEDIA_SUFFIX_SRT);
            }

            enqueueId = downloadManager.enqueue(downloadRequest);


        } catch (Exception e) {
            Log.e(TAG, "Error downloading video subtitles for download " + downloadURL + ": " + e.getMessage());
        }

        return enqueueId;
    }

    public String downloadedMediaLocalURI(long enqueueId) {
        String uriLocal = currentActivity.getString(R.string.download_file_prefix);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(enqueueId);
        Cursor cursor = downloadManager.query(query);
        if (cursor != null) {
            if (enqueueId != 0L && cursor.moveToFirst()) {
                uriLocal = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            }
            cursor.close();
        }
        return uriLocal == null ? "data" : uriLocal;
    }

    public boolean isDownloadUnfinished() {

        List<DownloadVideoRealm> unFinishedVideoList = getRealmController().getAllUnfinishedDownloades(getLoggedInUser(currentActivity));
        return unFinishedVideoList != null && !unFinishedVideoList.isEmpty();
    }

    public AppCMSStreamingInfoCall getAppCMSStreamingInfoCall() {
        return appCMSStreamingInfoCall;
    }

    public String getStreamingInfoURL(String filmId) {

        return currentActivity.getString(R.string.app_cms_streaminginfo_api_url,
                appCMSMain.getApiBaseUrl(),
                filmId,
                appCMSMain.getSite());
    }

    public String getDownloadedFileSize(String filmId) {

        DownloadVideoRealm downloadVideoRealm = realmController.getDownloadById(filmId);
        if (downloadVideoRealm == null)
            return "";
        return getDownloadedFileSize(downloadVideoRealm.getVideoSize());
    }

    @UiThread
    public boolean isVideoDownloaded(String videoId) {
        DownloadVideoRealm downloadVideoRealm = realmController.getDownloadByIdBelongstoUser(videoId,
                getLoggedInUser(currentActivity));
        return downloadVideoRealm != null && downloadVideoRealm.getVideoId().equalsIgnoreCase(videoId);
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

    public void startNextDownload() {
        if (downloadQueueThread != null) {
            if (!downloadQueueThread.running()) {
                downloadQueueThread.start();
            }
            downloadQueueThread.setStartNextDownload();
        }
    }

    public void startDownload(ContentDatum contentDatum,
                              Action1<UserVideoDownloadStatus> resultAction1) {
        currentActivity.runOnUiThread(() -> {
            try {
                long enqueueId;

                if (contentDatum.getStreamingInfo() == null) { // This will handle the case if we get video streaming info null at Video detail page.

                    String url = getStreamingInfoURL(contentDatum.getGist().getId());

                    GetAppCMSStreamingInfoAsyncTask.Params param = new GetAppCMSStreamingInfoAsyncTask.Params.Builder().url(url).build();

                    new GetAppCMSStreamingInfoAsyncTask(appCMSStreamingInfoCall, appCMSStreamingInfo -> {
                        if (appCMSStreamingInfo != null) {
                            contentDatum.setStreamingInfo(appCMSStreamingInfo.getStreamingInfo());
                        }
                    }).execute(param);

                    showDialog(DialogType.STREAMING_INFO_MISSING, null, false, null);
                    return;
                }

                long ccEnqueueId = 0L;
                if (contentDatum.getContentDetails() != null &&
                        contentDatum.getContentDetails().getClosedCaptions() != null &&
                        !contentDatum.getContentDetails().getClosedCaptions().isEmpty() &&
                        contentDatum.getContentDetails().getClosedCaptions().get(0).getUrl() != null) {
                    ccEnqueueId = downloadVideoSubtitles(contentDatum.getContentDetails()
                            .getClosedCaptions().get(0).getUrl(), contentDatum.getGist().getId());
                }

                cancelDownloadIconTimerTask();

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

                downloadURL = downloadURL != null
                        ? downloadURL.replace("https:/", "http:/")
                        : null;

                DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadURL.replace(" ", "%20")))
                        .setTitle(contentDatum.getGist().getTitle())
                        .setDescription(contentDatum.getGist().getDescription())
                        .setAllowedOverRoaming(false)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setVisibleInDownloadsUi(false)
                        .setShowRunningNotification(true);

                if (isPreferedStorageLocationSDCard(currentActivity)) {
                    downloadRequest.setDestinationUri(Uri.fromFile(new File(getSDCardPath(currentActivity, Environment.DIRECTORY_DOWNLOADS),
                            contentDatum.getGist().getId() + MEDIA_SURFIX_MP4)));
                } else {
                    downloadRequest.setDestinationInExternalFilesDir(currentActivity, Environment.DIRECTORY_DOWNLOADS,
                            contentDatum.getGist().getId() + MEDIA_SURFIX_MP4);
                }

                enqueueId = downloadManager.enqueue(downloadRequest);

                long thumbEnqueueId = downloadVideoImage(contentDatum.getGist().getVideoImageUrl(),
                        contentDatum.getGist().getId());
                long posterEnqueueId = downloadPosterImage(contentDatum.getGist().getPosterImageUrl(),
                        contentDatum.getGist().getId());

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
                Log.e(TAG, e.getMessage());
                showDialog(DialogType.DOWNLOAD_INCOMPLETE, e.getMessage(), false, null);
            } finally {
                appCMSUserDownloadVideoStatusCall.call(contentDatum.getGist().getId(), this,
                        resultAction1, getLoggedInUser(currentActivity));
            }
        });
    }

    @UiThread
    public synchronized void updateDownloadingStatus(String filmId, final ImageView imageView,
                                                     AppCMSPresenter presenter,
                                                     final Action1<UserVideoDownloadStatus> responseAction,
                                                     String userId, boolean isFromDownload) {
        long videoId = -1L;
        if (!isFromDownload) {
            cancelDownloadIconTimerTask();
        }
        try {
            videoId = realmController.getDownloadByIdBelongstoUser(filmId, userId).getVideoId_DM();
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(videoId);

            /*
             * Timer code can be optimize with RxJava code
             */
            runUpdateDownloadIconTimer = true;
            updateDownloadIconTimer = new Timer();
            downloadProgressTimerList.add(updateDownloadIconTimer);
            updateDownloadIconTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Cursor c = downloadManager.query(query);
                        if (c != null && c.moveToFirst()) {
                            downloaded = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            long totalSize = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            long downloaded = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            c.close();
                            int downloadPercent = (int) (downloaded * 100.0 / totalSize + 0.5);
                            Log.d(TAG, "download progress =" + downloaded + " total-> " + totalSize + " " + downloadPercent);
                            Log.d(TAG, "getCanonicalName " + this);
                            if (downloaded >= totalSize || downloadPercent > 100) {
                                if (currentActivity != null && isUserLoggedIn(currentActivity))
                                    currentActivity.runOnUiThread(() -> appCMSUserDownloadVideoStatusCall
                                            .call(filmId, presenter, responseAction, getLoggedInUser(currentActivity)));
                                this.cancel();
                            } else {
                                if (currentActivity != null && runUpdateDownloadIconTimer)
                                    currentActivity.runOnUiThread(() -> {
                                        try {
                                            circularImageBar(imageView, downloadPercent);
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error rendering circular image bar");
                                        }
                                    });
                            }

                        }
                    } catch (Exception exception) {
                        Log.e(TAG, exception.getMessage());
                    }
                }
            }, 500, 1000);
        } catch (Exception e) {
            Log.e(TAG, "Could not find video ID in downloads");
        }
    }

    public void cancelDownloadIconTimerTask() {
       /* if (updateDownloadIconTimer != null) {
            runUpdateDownloadIconTimer = false;
            updateDownloadIconTimer.cancel();
            updateDownloadIconTimer.purge();
        }*/
        if (downloadProgressTimerList != null && !downloadProgressTimerList.isEmpty()) {
            for (Timer downloadProgress : downloadProgressTimerList) {
                downloadProgress.cancel();
                downloadProgress.purge();
            }
            downloadProgressTimerList.clear();

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
            if (BaseView.isTablet(currentActivity)) {
                canvas.drawCircle(iv2.getWidth() / 2, iv2.getHeight() / 2, (iv2.getWidth() / 2) - 2, paint);
            } else {
                canvas.drawCircle(iv2.getWidth() / 2, iv2.getHeight() / 2, (iv2.getWidth() / 2) - 7, paint);// Fix SVFA-1561 changed  -2 to -7
            }

            int tintColor = Color.parseColor((this.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()));
            paint.setColor(tintColor);
            paint.setStrokeWidth(iv2.getWidth() / 10);
            paint.setStyle(Paint.Style.FILL);
            final RectF oval = new RectF();
            paint.setStyle(Paint.Style.STROKE);
            if (BaseView.isTablet(currentActivity)) {
                oval.set(2, 2, iv2.getWidth() - 2, iv2.getHeight() - 2);
            } else {
                oval.set(6, 6, iv2.getWidth() - 6, iv2.getHeight() - 6); //Fix SVFA-1561  change 2 to 6
            }
            canvas.drawArc(oval, 270, ((i * 360) / 100), false, paint);


            iv2.setImageBitmap(b);
            iv2.setForegroundGravity(View.TEXT_ALIGNMENT_CENTER);
        }
    }

    public void editHistory(final String filmId,
                            final Action1<AppCMSDeleteHistoryResult> resultAction1, boolean post) {
        final String url = currentActivity.getString(R.string.app_cms_edit_history_api_url,
                appCMSMain.getApiBaseUrl(),
                getLoggedInUser(currentActivity),
                appCMSSite.getGist().getSiteInternalName(),
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
                            showDialog(DialogType.DELETE_ONE_HISTORY_ITEM,
                                    currentActivity.getString(R.string.app_cms_delete_one_history_item_message),
                                    true,
                                    () -> {
                                        try {
                                            Observable.just(appCMSDeleteHistoryResult).subscribe(resultAction1);
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error deleting history: " + e.getMessage());
                                        }
                                    });
                        } catch (Exception e) {
                            Log.e(TAG, "deleteHistoryContent: " + e.toString());
                        }
                    }, request, post);
        } catch (Exception e) {
            Log.e(TAG, "Error editing history for " + filmId + ": " + e.getMessage());
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
                appCMSSite.getGist().getSiteInternalName(),
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
            Log.e(TAG, "Error clearing watchlist: " + e.getMessage());
        }
    }

    public boolean isMemorySpaceAvailable() {
        Log.d(TAG, getRemainingDownloadSize() + "  Available storage space:=  " + getMegabytesAvailable(Environment.getExternalStorageDirectory()));
        File storagePath = null;
        if (!isPreferedStorageLocationSDCard(currentActivity)) {
            storagePath = Environment.getExternalStorageDirectory();
        } else {
            storagePath = new File(getStorageDirectories(currentActivity)[0]);
        }
        return getMegabytesAvailable(storagePath) > getRemainingDownloadSize();
    }

    public void navigateToDownloadPage(String pageId, String pageTitle, String url,
                                       boolean launchActivity) {
        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {

            if (!isMemorySpaceAvailable()) {
                showDialog(DialogType.DOWNLOAD_FAILED, currentActivity.getString(R.string.app_cms_download_failed_error_message), false, null);
            }

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
                                !module1.getContentData().isEmpty()) {
                            loadingHistory = true;
                            getHistoryData(appCMSHistoryResult -> {
                                try {
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
                                                    false,
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
                                                    false,
                                                    false,
                                                    null,
                                                    ExtraScreenType.NONE);
                                            if (args != null) {
                                                Intent updatePageIntent =
                                                        new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                                updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                                        args);
                                                currentActivity.sendBroadcast(updatePageIntent);
                                                dismissOpenDialogs(null);
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error merging history data: " + e.getMessage());
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
                            false,
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
                            false,
                            false,
                            null,
                            ExtraScreenType.NONE);

                    if (args != null) {
                        Intent downloadPageIntent =
                                new Intent(AppCMSPresenter
                                        .PRESENTER_NAVIGATE_ACTION);
                        downloadPageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key), args);
                        currentActivity.sendBroadcast(downloadPageIntent);
                    }
                }
            }
        }
    }

    public void clearHistory(final Action1<AppCMSDeleteHistoryResult> resultAction1) {
        final String url = currentActivity.getString(R.string.app_cms_clear_history_api_url,
                appCMSMain.getApiBaseUrl(),
                getLoggedInUser(currentActivity),
                appCMSSite.getGist().getSiteInternalName());

        try {
            DeleteHistoryRequest request = new DeleteHistoryRequest();
            request.setUserId(getLoggedInUser(currentActivity));
            request.setContentType(currentActivity.getString(R.string.delete_history_content_type_video));
            request.setPosition(1L);
            appCMSDeleteHistoryCall.call(url, getAuthToken(currentActivity),
                    appCMSDeleteHistoryResult -> {
                        try {
                            showDialog(DialogType.DELETE_ALL_HISTORY_ITEMS,
                                    currentActivity.getString(R.string.app_cms_delete_all_history_items_message),
                                    true,
                                    () -> {
                                        try {
                                            Observable.just(appCMSDeleteHistoryResult).subscribe(resultAction1);
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error deleting all history items: " + e.getMessage());
                                        }
                                    });
                        } catch (Exception e) {
                            Log.e(TAG, "clearHistoryContent: " + e.toString());
                        }
                    }, request, false);
        } catch (Exception e) {
            Log.e(TAG, "Error clearing history: " + e.getMessage());
        }
    }

    public void navigateToWatchlistPage(String pageId, String pageTitle, String url,
                                        boolean launchActivity) {

        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            getWatchlistPageContent(appCMSMain.getApiBaseUrl(),
                    pageIdToPageAPIUrlMap.get(pageId),
                    appCMSSite.getGist().getSiteInternalName(),
                    true,
                    getPageId(appCMSPageUI), new AppCMSWatchlistAPIAction(false,
                            false,
                            false,
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
                                if (args != null) {
                                    Intent watchlistPageIntent =
                                            new Intent(AppCMSPresenter
                                                    .PRESENTER_NAVIGATE_ACTION);
                                    watchlistPageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                            args);
                                    currentActivity.sendBroadcast(watchlistPageIntent);
                                }
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
                            try {
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
                            } catch (Exception e) {
                                Log.e(TAG, "Error retrieving video details: " + e.getMessage());
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
                    appCMSSite.getGist().getSiteInternalName(),
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
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            getHistoryPageContent(appCMSMain.getApiBaseUrl(),
                    pageIdToPageAPIUrlMap.get(pageId),
                    appCMSSite.getGist().getSiteInternalName(),
                    true,
                    getPageId(appCMSPageUI), new AppCMSHistoryAPIAction(false,
                            false,
                            false,
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

                                if (args != null) {
                                    Intent historyPageIntent =
                                            new Intent(AppCMSPresenter
                                                    .PRESENTER_NAVIGATE_ACTION);
                                    historyPageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                            args);
                                    currentActivity.sendBroadcast(historyPageIntent);
                                }
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
                } catch (IOException | NullPointerException e) {
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
                } catch (IOException | NullPointerException e) {
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

            if (!launchSuccess) {
                Log.e(TAG, "Failed to launch page: " + subscriptionPage.getPageName());
                launchErrorActivity(platformType);
            }
        }
    }

    public void checkForExistingSubscription(boolean showErrorDialogIfSubscriptionExists) {
        if (currentActivity != null) {
            Bundle activeSubs = null;
            try {
                if (inAppBillingService != null) {
                    activeSubs = inAppBillingService.getPurchases(3,
                            currentActivity.getPackageName(),
                            "subs",
                            null);
                    ArrayList<String> subscribedItemList = activeSubs.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

                    if (subscribedItemList != null && !subscribedItemList.isEmpty()) {
                        boolean subscriptionExpired = true;
                        for (int i = 0; i < subscribedItemList.size(); i++) {
                            try {
                                InAppPurchaseData inAppPurchaseData = gson.fromJson(subscribedItemList.get(i),
                                        InAppPurchaseData.class);

                                ArrayList<String> skuList = new ArrayList<>();
                                skuList.add(inAppPurchaseData.getProductId());
                                Bundle skuListBundle = new Bundle();
                                skuListBundle.putStringArrayList("ITEM_ID_LIST", skuList);
                                Bundle skuListBundleResult = inAppBillingService.getSkuDetails(3,
                                        currentActivity.getPackageName(),
                                        "subs",
                                        skuListBundle);
                                ArrayList<String> skuDetailsList =
                                        skuListBundleResult.getStringArrayList("DETAILS_LIST");
                                if (skuDetailsList != null && !skuDetailsList.isEmpty()) {
                                    SkuDetails skuDetails = gson.fromJson(skuDetailsList.get(0),
                                            SkuDetails.class);
                                    setExistingGooglePlaySubscriptionDescription(currentActivity, skuDetails.getTitle());

                                    setExistingGooglePlaySubscriptionPrice(currentActivity, skuDetails.getPrice());

                                    subscriptionExpired = existingSubscriptionExpired(inAppPurchaseData, skuDetails);
                                }

                                setExistingGooglePlaySubscriptionId(currentActivity, inAppPurchaseData.getProductId());

                                if (inAppPurchaseData.isAutoRenewing() || !subscriptionExpired) {
                                    if (showErrorDialogIfSubscriptionExists &&
                                            !isUserLoggedIn(currentActivity)) {
                                        showDialog(DialogType.EXISTING_SUBSCRIPTION,
                                                currentActivity.getString(R.string.app_cms_existing_subscription_error_message),
                                                false,
                                                () -> {
                                                    try {
                                                        sendCloseOthersAction(null, true);
                                                    } catch (Exception e) {
                                                        Log.e(TAG, "Error retrieving Google Play Subscription data: " + e.getMessage());
                                                    }
                                                });
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing Google Play subscription data: " + e.toString());
                            }
                        }

                        setExistingGooglePlaySubscriptionSuspended(currentActivity, subscriptionExpired);
                    }
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to purchase item with sku: "
                        + getActiveSubscriptionSku(currentActivity));
            }
        }
    }

    private boolean existingSubscriptionExpired(InAppPurchaseData inAppPurchaseData,
                                                SkuDetails skuDetails) {
        try {
            Instant subscribedPurchaseTime = Instant.ofEpochMilli(inAppPurchaseData.getPurchaseTime());
            Instant nowTime = Instant.now();
            Instant subscribedExpirationTime = Instant.from(subscribedPurchaseTime);
            String subscriptionPeriod = skuDetails.getSubscriptionPeriod();
            final String SUBS_PERIOD_REGEX = "P(([0-9]+)[yY])?(([0-9]+)[mM])?(([0-9]+)[wW])?(([0-9]+)[dD])?";
            if (subscriptionPeriod.matches(SUBS_PERIOD_REGEX)) {
                Matcher subscriptionPeriodMatcher = Pattern.compile(SUBS_PERIOD_REGEX).matcher(subscriptionPeriod);
                if (subscriptionPeriodMatcher.group(2) != null) {
                    subscribedExpirationTime.plus(Long.parseLong(subscriptionPeriodMatcher.group(2)),
                            ChronoUnit.YEARS);
                }
                if (subscriptionPeriodMatcher.group(4) != null) {
                    subscribedExpirationTime.plus(Long.parseLong(subscriptionPeriodMatcher.group(4)),
                            ChronoUnit.MONTHS);
                }
                if (subscriptionPeriodMatcher.group(6) != null) {
                    subscribedExpirationTime.plus(Long.parseLong(subscriptionPeriodMatcher.group(6)),
                            ChronoUnit.WEEKS);
                }
                if (subscriptionPeriodMatcher.group(8) != null) {
                    subscribedExpirationTime.plus(Long.parseLong(subscriptionPeriodMatcher.group(8)),
                            ChronoUnit.DAYS);
                }

                Duration betweenSubscribedTimeAndNowTime =
                        Duration.between(subscribedPurchaseTime, nowTime);
                Duration betweenSubscribedTimeAndExpirationTime =
                        Duration.between(subscribedPurchaseTime, subscribedExpirationTime);
                return betweenSubscribedTimeAndExpirationTime.compareTo(betweenSubscribedTimeAndNowTime) < 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "");
        }
        return false;
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
                launchErrorActivity(platformType);
            }
        }
    }

    public void resetPassword(final String email) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_forgot_password_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSSite.getGist().getSiteInternalName());
            appCMSResetPasswordCall.call(url,
                    email,
                    forgotPasswordResponse -> {
                        try {
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
                        } catch (Exception e) {
                            Log.e(TAG, "Error resetting password: " + e.getMessage());
                        }
                    });
        }
    }

    public void getUserData(final Action1<UserIdentity> userIdentityAction) {
        if (currentActivity != null) {
            if (shouldRefreshAuthToken()) {
                callRefreshIdentity(() -> {
                    try {
                        String url = currentActivity.getString(R.string.app_cms_user_identity_api_url,
                                appCMSMain.getApiBaseUrl(),
                                appCMSSite.getGist().getSiteInternalName());
                        appCMSUserIdentityCall.callGet(url,
                                getAuthToken(currentActivity),
                                userIdentity -> {
                                    try {
                                        Observable.just(userIdentity).subscribe(userIdentityAction);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error retrieving user identity information: " + e.getMessage());
                                    }
                                });
                    } catch (Exception e) {
                        Log.e(TAG, "Error refreshing identity: " + e.getMessage());
                    }
                });
            } else {
                String url = currentActivity.getString(R.string.app_cms_user_identity_api_url,
                        appCMSMain.getApiBaseUrl(),
                        appCMSSite.getGist().getSiteInternalName());
                appCMSUserIdentityCall.callGet(url,
                        getAuthToken(currentActivity),
                        userIdentity -> {
                            try {
                                Observable.just(userIdentity).subscribe(userIdentityAction);
                            } catch (Exception e) {
                                Log.e(TAG, "Error retrieving user identity data: " + e.getMessage());
                            }
                        });
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void updateUserProfile(final String username,
                                  final String email,
                                  final String password,
                                  final Action1<UserIdentity> userIdentityAction) {
        if (currentActivity != null) {
            callRefreshIdentity(() -> {
                try {
                    String url = currentActivity.getString(R.string.app_cms_user_identity_api_url,
                            appCMSMain.getApiBaseUrl(),
                            appCMSSite.getGist().getSiteInternalName());
                    UserIdentity userIdentity = new UserIdentity();
                    userIdentity.setName(username);
                    userIdentity.setEmail(email);
                    userIdentity.setId(getLoggedInUser(currentActivity));
                    userIdentity.setPassword(password);

                    appCMSUserIdentityCall.callPost(url,
                            getAuthToken(currentActivity),
                            userIdentity,
                            userIdentityResult -> {
                                try {
                                    if (userIdentityResult != null) {
                                        setLoggedInUserName(currentActivity,
                                                userIdentityResult.getName());
                                        setLoggedInUserEmail(currentActivity,
                                                userIdentityResult.getEmail());
                                        setAuthToken(currentActivity, userIdentityResult.getAuthorizationToken());
                                        setRefreshToken(currentActivity, userIdentityResult.getRefreshToken());
                                    }
                                    sendRefreshPageAction();
                                    userIdentityAction.call(userIdentityResult);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error get user identity data: " + e.getMessage());
                                }
                            }, errorBody -> {
                                try {
                                    UserIdentity userIdentityError = gson.fromJson(errorBody.string(),
                                            UserIdentity.class);
                                    showToast(userIdentityError.getError(), Toast.LENGTH_LONG);
                                } catch (IOException e) {
                                    Log.e(TAG, "Invalid JSON object: " + e.toString());
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing user identity error: " + e.getMessage());
                                }
                            });
                } catch (Exception e) {
                    Log.e(TAG, "Error refreshing identity: " + e.getMessage());
                }
            });
        }
    }

    public void updateUserPassword(final String oldPassword, final String newPassword,
                                   final String confirmPassword) {
        String url = currentActivity.getString(R.string.app_cms_change_password_api_url,
                appCMSMain.getApiBaseUrl(), appCMSSite.getGist().getSiteInternalName());

        if (confirmPassword.equals(newPassword)) {
            UserIdentityPassword userIdentityPassword = new UserIdentityPassword();
            userIdentityPassword.setResetToken(getAuthToken(currentActivity));
            userIdentityPassword.setOldPassword(oldPassword);
            userIdentityPassword.setNewPassword(newPassword);

            appCMSUserIdentityCall.passwordPost(url,
                    getAuthToken(currentActivity), userIdentityPassword,
                    userIdentityPasswordResult -> {
                        try {
                            if (userIdentityPasswordResult != null) {
                                showToast("Password Changed Successfully", Toast.LENGTH_LONG);
                                sendCloseOthersAction(null, true);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error retrieving user password reset result: " + e.getMessage());
                        }
                    }, errorBody -> {
                        try {
                            UserIdentityPassword userIdentityError = gson.fromJson(errorBody.string(),
                                    UserIdentityPassword.class);
                            showToast(userIdentityError.getError(), Toast.LENGTH_LONG);
                        } catch (IOException e) {
                            Log.e(TAG, "Invalid JSON object: " + e.toString());
                        } catch (Exception e) {
                            Log.e(TAG, "Error retrieving user password result: " + e.getMessage());
                        }
                    });
        } else {
            showToast("New password should match with Confirm password.", Toast.LENGTH_LONG);
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
                if (imm != null) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }
        }
    }

    public void closeSoftKeyboard() {
        if (currentActivity != null) {
            View view = currentActivity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm =
                        (InputMethodManager) currentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
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
            Log.d(TAG, "Launching page " + pageTitle + ": " + pageId);
            Log.d(TAG, "Search query (optional): " + searchQuery);
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);
            currentActivity.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
            getPageIdContent(appCMSMain.getApiBaseUrl(),
                    pageIdToPageAPIUrlMap.get(pageId),
                    appCMSSite.getGist().getSiteInternalName(),
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
                                                    !module.getContentData().isEmpty()) {
                                                loadingHistory = true;
                                                getHistoryData(appCMSHistoryResult -> {
                                                    try {
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
                                                                if (args != null) {
                                                                    Intent updatePageIntent =
                                                                            new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                                                    updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                                                            args);
                                                                    currentActivity.sendBroadcast(updatePageIntent);
                                                                    dismissOpenDialogs(null);
                                                                }
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        Log.e(TAG, "Error parsing history result: " + e.getMessage());
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
                                        if (args != null) {
                                            Intent updatePageIntent =
                                                    new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                            updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                                    args);
                                            currentActivity.sendBroadcast(updatePageIntent);
                                            dismissOpenDialogs(null);
                                        }
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
                    currentActivity.getString(R.string.app_cms_network_connectivity_error_message_download),
                    true,
                    () -> {
                        try {
                            navigateToDownloadPage(downloadPage.getPageId(),
                                    downloadPage.getPageName(), downloadPage.getPageUI(), false);
                        } catch (Exception e) {
                            Log.e(TAG, "Error sending stop loading action: " + e.getMessage());
                        }
                    }); // fix of SVFA-1435 for build #1.0.35
        }
    }

    public void launchErrorActivity(PlatformType platformType) {
        if (currentActivity != null) {
            if (platformType == PlatformType.ANDROID) {
                try {
                    sendCloseOthersAction(null, false);
                    Intent errorIntent = new Intent(currentActivity, AppCMSErrorActivity.class);
                    currentActivity.startActivity(errorIntent);
                } catch (Exception e) {
                    Log.e(TAG, "DialogType launching Mobile DialogType Activity");
                }
            } else if (platformType == PlatformType.TV) {
                try {
                    Intent errorIntent = new Intent(currentActivity, Class.forName(tvErrorScreenPackage));
                    currentActivity.startActivity(errorIntent);
                } catch (Exception e) {
                    Log.e(TAG, "DialogType launching TV DialogType Activity");
                }
            }
        }
    }

    public void getPageIdContent(String baseUrl,
                                 String endPoint,
                                 String siteId,
                                 boolean usePageIdQueryParam,
                                 String pageId,
                                 Action1<AppCMSPageAPI> readyAction) {
        if (shouldRefreshAuthToken()) {
            refreshIdentity(getRefreshToken(currentActivity),
                    () -> {
                        try {
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
                        } catch (Exception e) {
                            Log.e(TAG, "Error retrieving page ID content: " + e.getMessage());
                        }
                    });
        } else {
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
        return getIsUserSubscribed(context);
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
            //Set the user Id when user is succesfully logged_in
            if (mFireBaseAnalytics != null)
                mFireBaseAnalytics.setUserProperty(USER_ID_KEY, userId);
            SharedPreferences sharedPrefs = context.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putString(USER_ID_SHARED_PREF_NAME, userId).commit() &&
                    setLoggedInTime(context);
        }
        return false;
    }

    public String getAnonymousUserToken(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ANONYMOUS_AUTH_TOKEN_PREF_NAME, 0);
            return sharedPrefs.getString(ANONYMOUS_AUTH_TOKEN_PREF_NAME, null);
        }
        return null;
    }

    public boolean isPreferedStorageLocationSDCard(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME, 0);
            return sharedPrefs.getBoolean(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME, false);
        }
        return false;
    }

    public boolean setPreferedStorageLocationSDCard(Context context, boolean downloadPref) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putBoolean(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME,
                    downloadPref).commit() && setLoggedInTime(context);
        }
        return false;
    }

    public String getUserDownloadQualityPref(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_DOWNLOAD_QUALITY_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(getLoggedInUser(currentActivity), "720p");
        }
        return null;
    }

    public void setUserDownloadQualityPref(Context context, String downloadQuality) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_DOWNLOAD_QUALITY_SHARED_PREF_NAME, 0);
            sharedPrefs.edit().putString(getLoggedInUser(currentActivity), downloadQuality).apply();
        }
    }

    public boolean setAnonymousUserToken(Context context, String anonymousAuthToken) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ANONYMOUS_AUTH_TOKEN_PREF_NAME, 0);
            return sharedPrefs.edit().putString(ANONYMOUS_AUTH_TOKEN_PREF_NAME, anonymousAuthToken).commit();
        }
        return false;
    }

    public boolean getClosedCaptionPreference(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_CLOSED_CAPTION_PREF_KEY, 0);
            return sharedPrefs.getBoolean(getLoggedInUser(currentActivity), false);
        }
        return false;
    }

    public void setClosedCaptionPreference(Context context, boolean isClosedCaptionOn) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(USER_CLOSED_CAPTION_PREF_KEY, 0);
            sharedPrefs.edit().putBoolean(getLoggedInUser(currentActivity), isClosedCaptionOn).apply();
        }
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

    public boolean setNetworkConnected(Context context, boolean networkConnected) {
        if (context != null) {
            if (networkConnected) {
                sendOfflineBeaconMessage();
            }
            SharedPreferences sharedPrefs =
                    context.getSharedPreferences(NETWORK_CONNECTED_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putBoolean(NETWORK_CONNECTED_SHARED_PREF_NAME, networkConnected).commit();
        }
        return false;
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(NETWORK_CONNECTED_SHARED_PREF_NAME, 0);
            return sharedPrefs.getBoolean(NETWORK_CONNECTED_SHARED_PREF_NAME, false);
        }
        return false;
    }

    public boolean setWifiConnected(Context context, boolean wifiConnected) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(WIFI_CONNECTED_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putBoolean(WIFI_CONNECTED_SHARED_PREF_NAME, wifiConnected).commit();
        }
        return false;
    }

    public boolean isWifiConnected(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(WIFI_CONNECTED_SHARED_PREF_NAME, 0);
            return sharedPrefs.getBoolean(WIFI_CONNECTED_SHARED_PREF_NAME, false);
        }
        return false;
    }

    public boolean setFacebookAccessToken(Context context,
                                          final String facebookAccessToken,
                                          final String facebookUserId,
                                          final String username,
                                          final String email,
                                          boolean refreshSubscriptionData) {
        if (launchType == LaunchType.SUBSCRIBE) {
            this.facebookAccessToken = facebookAccessToken;
            this.facebookUserId = facebookUserId;
            this.facebookUsername = username;
            this.facebookEmail = email;
            initiateItemPurchase();
        } else if (context != null) {
            String url = currentActivity.getString(R.string.app_cms_facebook_login_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSSite.getGist().getSiteInternalName());
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

                            if (appCMSMain.getServiceType()
                                    .equals(currentActivity.getString(R.string.app_cms_main_svod_service_type_key)) &&
                                refreshSubscriptionData) {
                                refreshSubscriptionData(() -> {
                                    try {
                                        if (entitlementPendingVideoData != null) {
                                            navigateToHomeToRefresh = false;
                                            sendRefreshPageAction();
                                            sendCloseOthersAction(null, true);
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
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error refreshing subscription data after logging in with Facebook: " + e.getMessage());
                                    }
                                });
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
                                        final String googleEmail,
                                        boolean refreshSubscriptionData) {

        if (launchType == LaunchType.SUBSCRIBE) {
            this.googleAccessToken = googleAccessToken;
            this.googleUserId = googleUserId;
            this.googleUsername = googleUsername;
            this.googleEmail = googleEmail;
            initiateItemPurchase();
        } else if (context != null) {
            String url = currentActivity.getString(R.string.app_cms_google_login_api_url,
                    appCMSMain.getApiBaseUrl(), appCMSSite.getGist().getSiteInternalName());

            appCMSGoogleLoginCall.call(url, googleAccessToken,
                    googleLoginResponse -> {
                        try {
                            if (googleLoginResponse != null) {
                                setAuthToken(currentActivity, googleLoginResponse.getAuthorizationToken());
                                setRefreshToken(currentActivity, googleLoginResponse.getRefreshToken());
                                setLoggedInUser(currentActivity, googleUserId);
                                setLoggedInUserName(currentActivity, googleUsername);
                                setLoggedInUserEmail(currentActivity, googleEmail);

                                if (appCMSMain.getServiceType()
                                        .equals(currentActivity.getString(R.string.app_cms_main_svod_service_type_key)) &&
                                        refreshSubscriptionData) {
                                    refreshSubscriptionData(() -> {
                                        if (entitlementPendingVideoData != null) {
                                            navigateToHomeToRefresh = false;
                                            sendRefreshPageAction();
                                            sendCloseOthersAction(null, true);
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
                                    });
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
                        } catch (Exception e) {
                            Log.e(TAG, "Error getting Google Access Token login information: " + e.getMessage());
                        }
                    });

            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPreferences.edit().putString(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME,
                    googleAccessToken).commit();
        }
        return false;
    }

    public boolean getAutoplayEnabledUserPref(@NonNull Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(AUTO_PLAY_ENABLED_PREF_NAME, 0);
        return sharedPrefs.getBoolean(getLoggedInUser(currentActivity), true);
    }

    public void setAutoplayEnabledUserPref(Context context, boolean isAutoplayEnabled) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(AUTO_PLAY_ENABLED_PREF_NAME, 0);
            sharedPrefs.edit().putBoolean(getLoggedInUser(currentActivity), isAutoplayEnabled).apply();
        }
    }

    public boolean getIsUserSubscribed(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(IS_USER_SUBSCRIBED, 0);
            return sharedPrefs.getBoolean(getLoggedInUser(currentActivity), false);
        }
        return false;
    }

    public boolean setIsUserSubscribed(Context context, boolean userSubscribed) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(IS_USER_SUBSCRIBED, 0);
            return sharedPrefs.edit().putBoolean(getLoggedInUser(currentActivity), userSubscribed).commit();
        }
        return false;
    }

    public String getExistingGooglePlaySubscriptionDescription(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_DESCRIPTION,
                    0);
            return sharedPrefs.getString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_DESCRIPTION, null);
        }
        return null;
    }

    public boolean setExistingGooglePlaySubscriptionDescription(Context context,
                                                                String existingGooglePlaySubscriptionDescription) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_DESCRIPTION, 0);
            return sharedPrefs.edit().putString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_DESCRIPTION,
                    existingGooglePlaySubscriptionDescription).commit();
        }
        return false;
    }

    public double parseActiveSubscriptionPrice(Context context) {
        try {
            String activeSubscriptionPrice = getActiveSubscriptionPrice(context);
            if (!TextUtils.isEmpty(activeSubscriptionPrice)) {
                return NumberFormat.getNumberInstance().parse(activeSubscriptionPrice).doubleValue();
            }

        } catch (NumberFormatException | ParseException | NullPointerException e) {
            Log.e(TAG, "Error parsing price from Google Play subscription data: " + e.toString());
        }
        return 0.0;
    }

    public double parseExistingGooglePlaySubscriptionPrice(Context context) {
        try {
            String existingGooglePlaySubscriptionPrice = getExistingGooglePlaySubscriptionPrice(context);
            if (!TextUtils.isEmpty(existingGooglePlaySubscriptionPrice)) {
                return NumberFormat.getCurrencyInstance().parse(existingGooglePlaySubscriptionPrice).doubleValue();
            }

        } catch (NumberFormatException | ParseException | NullPointerException e) {
            Log.e(TAG, "Error parsing price from Google Play subscription data: " + e.toString());
        }
        return 0.0;
    }

    public String getExistingGooglePlaySubscriptionPrice(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_PRICE, 0);
            return sharedPrefs.getString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_PRICE, null);
        }
        return null;
    }

    public boolean setExistingGooglePlaySubscriptionPrice(Context context,
                                                          String existingGooglePlaySubscriptionPrice) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_PRICE, 0);
            return sharedPrefs.edit().putString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_PRICE,
                    existingGooglePlaySubscriptionPrice).commit();
        }
        return false;
    }

    public String getExistingGooglePlaySubscriptionId(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_ID, 0);
            return sharedPrefs.getString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_ID, null);
        }
        return null;
    }

    public boolean setExistingGooglePlaySubscriptionId(Context context,
                                                       String existingGooglePlaySubscriptionId) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_ID, 0);
            return sharedPrefs.edit().putString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_ID, existingGooglePlaySubscriptionId).commit();
        }
        return false;
    }

    public boolean isExistingGooglePlaySubscriptionSuspended(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_SUSPENDED, 0);
            return sharedPrefs.getBoolean(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_SUSPENDED, false);
        }
        return false;
    }

    public boolean setExistingGooglePlaySubscriptionSuspended(Context context, boolean existingSubscriptionSuspended) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_SUSPENDED, 0);
            return sharedPrefs.edit().putBoolean(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_SUSPENDED, existingSubscriptionSuspended).commit();
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

    public String getActiveSubscriptionPrice(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_PRICE_NAME, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_PRICE_NAME, null);
        }
        return null;
    }

    private boolean setActiveSubscriptionPrice(Context context, String subscriptionPrice) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_PRICE_NAME, 0);
            return sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_PRICE_NAME, subscriptionPrice).commit();
        }
        return false;
    }

    public String getActiveSubscriptionReceipt(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(ACTIVE_SUBSCRIPTION_RECEIPT, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_RECEIPT, null);
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
                try {
                    if (response != null) {
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            Log.e(TAG, error.toString());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error logging out from Facebook: " + e.getMessage());
                }
            });

            revokePermissions.executeAsync();
            LoginManager.getInstance().logOut();

            setLoggedInUser(currentActivity, null);
            setLoggedInUserName(currentActivity, null);
            setLoggedInUserEmail(currentActivity, null);
            setActiveSubscriptionPrice(currentActivity, null);
            setActiveSubscriptionId(currentActivity, null);
            setActiveSubscriptionSku(currentActivity, null);
            setActiveSubscriptionPlanName(currentActivity, null);
            setActiveSubscriptionReceipt(currentActivity, null);
            setRefreshToken(currentActivity, null);
            setAuthToken(currentActivity, null);
            setIsUserSubscribed(currentActivity, false);
            setExistingGooglePlaySubscriptionId(currentActivity, null);
            setActiveSubscriptionProcessor(currentActivity, null);
            setFacebookAccessToken(currentActivity, null, null, null, null, false);
            setGoogleAccessToken(currentActivity, null, null, null, null, false);

            sendUpdateHistoryAction();

            signinAnonymousUser();

            if (googleApiClient != null && googleApiClient.isConnected()) {
                Auth.GoogleSignInApi.signOut(googleApiClient);
            }

            navigateToHomeToRefresh = true;
            navigateToHomePage();

            CastHelper.getInstance(currentActivity.getApplicationContext()).disconnectChromecastOnLogout();
            AppsFlyerUtils.logoutEvent(currentActivity, getLoggedInUser(currentActivity));
        }
    }

    public void addInternalEvent(OnInternalEvent onInternalEvent) {
        if (!currentActions.isEmpty() &&
                !TextUtils.isEmpty(currentActions.peek()) &&
                onActionInternalEvents.get(currentActions.peek()) != null) {
            onActionInternalEvents.get(currentActions.peek()).add(onInternalEvent);
        }
    }

    public void clearOnInternalEvents() {
        if (!currentActions.isEmpty() &&
                !TextUtils.isEmpty(currentActions.peek()) &&
                onActionInternalEvents.get(currentActions.peek()) != null) {
            onActionInternalEvents.get(currentActions.peek()).clear();
        }
    }

    public @Nullable
    List<OnInternalEvent> getOnInternalEvents() {
        if (!currentActions.isEmpty() &&
                !TextUtils.isEmpty(currentActions.peek()) &&
                onActionInternalEvents.get(currentActions.peek()) != null) {
            return onActionInternalEvents.get(currentActions.peek());
        }
        return null;
    }

    public void restartInternalEvents() {
        if (!currentActions.isEmpty()) {
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
        if (!currentActions.isEmpty()) {
            List<OnInternalEvent> onInternalEvents = onActionInternalEvents.get(currentActions.peek());
            if (onInternalEvents != null) {
                for (OnInternalEvent onInternalEvent : onInternalEvents) {
                    onInternalEvent.cancel(true);
                }
            }
        }
    }

    public void popActionInternalEvents() {
        if (!currentActions.isEmpty()) {
            Log.d(TAG, "Stack size - Popping action internal events: " + currentActions.size());
            currentActions.pop();
            Log.d(TAG, "Stack size - Popped action internal events: " + currentActions.size());
        }
    }

    public NavigationPrimary findHomePageNavItem() {
        if (!navigation.getNavigationPrimary().isEmpty()) {
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
            try {
                if (main == null) {
                    Log.e(TAG, "DialogType retrieving main.json");
                    launchErrorActivity(platformType);
                } else if (TextUtils.isEmpty(main
                        .getAndroid())) {
                    Log.e(TAG, "AppCMS key for main not found");
                    launchErrorActivity(platformType);
                } else if (TextUtils.isEmpty(main
                        .getApiBaseUrl())) {
                    Log.e(TAG, "AppCMS key for API Base URL not found");
                    launchErrorActivity(platformType);
                } else {
                    appCMSMain = main;
                    String version = main.getVersion();
                    String oldVersion = main.getOldVersion();
                    Log.d(TAG, "Version: " + version);
                    Log.d(TAG, "OldVersion: " + oldVersion);
                    loadFromFile = appCMSMain.shouldLoadFromFile();

                    getAppCMSSite(platformType);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error retrieving main.json: " + e.getMessage());
            }
        }).execute(params);
    }

    public AppCMSMain getAppCMSMain() {
        return appCMSMain;
    }

    public AppCMSSite getAppCMSSite() {
        return appCMSSite;
    }

    public boolean isPageAVideoPage(String pageName) {
        if (currentActivity != null && pageName != null) {
            try {
                return pageName.contains(currentActivity.getString(R.string.app_cms_video_page_page_name));
            } catch (Exception e) {
                Log.e(TAG, "Failed to verify if input page is a video page: " + e.toString());
            }
        }
        return false;
    }

    public boolean isPagePrimary(String pageId) {
        for (NavigationPrimary navigationPrimary : navigation.getNavigationPrimary()) {
            if (pageId != null && !TextUtils.isEmpty(pageId) && pageId.contains(navigationPrimary.getPageId())) {
                return true;
            }
        }

        return false;
    }

    public boolean isPageNavigationPage(String pageId) {
        return currentActivity != null &&
                !TextUtils.isEmpty(pageId) &&
                pageId.equals(currentActivity.getString(R.string.app_cms_navigation_page_tag));
    }

    public boolean isPageUser(String pageId) {
        for (NavigationUser navigationUser : navigation.getNavigationUser()) {
            if (pageId != null && !TextUtils.isEmpty(pageId) && pageId.contains(navigationUser.getPageId())) {
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

    public void showToast(String message, int messageDuration) {
        Toast.makeText(currentActivity, message, messageDuration).show();
    }

    public void showEntitlementDialog(DialogType dialogType) {
        if (currentActivity != null) {
            String positiveButtonText = currentActivity.getString(R.string.app_cms_subscription_button_text);
            int textColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor());
            String title = currentActivity.getString(R.string.app_cms_subscription_required_title);
            String message = currentActivity.getString(R.string.app_cms_subscription_required_message);

            if (dialogType == DialogType.LOGOUT_WITH_RUNNING_DOWNLOAD) {
                title = currentActivity.getString(R.string.app_cms_logout_with_running_download_title);
                message = currentActivity.getString(R.string.app_cms_logout_with_running_download_message);
            }
            if (dialogType == DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED) {
                title = currentActivity.getString(R.string.app_cms_login_and_subscription_required_title);
                message = currentActivity.getString(R.string.app_cms_login_and_subscription_required_message);
                //Set Firbase User Propert when user is not logged_in and unsubscribed
                mFireBaseAnalytics.setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_OUT);
                mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_NOT_SUBSCRIBED);
            }

            if (dialogType == DialogType.CANNOT_UPGRADE_SUBSCRIPTION) {
                title = currentActivity.getString(R.string.app_cms_subscription_upgrade_cancel_title);
                message = currentActivity.getString(R.string.app_cms_subscription_upgrade_for_web_user_dialog);
            }

            if (dialogType == DialogType.UPGRADE_UNAVAILABLE) {
                title = currentActivity.getString(R.string.app_cms_subscription_upgrade_unavailable_title);
                message = currentActivity.getString(R.string.app_cms_subscription_upgrade_unavailable_user_dialog);
            }

            if (dialogType == DialogType.CANNOT_CANCEL_SUBSCRIPTION) {
                String paymentProcessor = getActiveSubscriptionProcessor(currentActivity);
                if ((!TextUtils.isEmpty(paymentProcessor) &&
                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) &&
                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly)))) {
                    title = currentActivity.getString(R.string.app_cms_subscription_upgrade_cancel_title);
                    message = currentActivity.getString(R.string.app_cms_subscription_cancel_for_web_user_dialog);
                } else if (!TextUtils.isEmpty(paymentProcessor) &&
                        TextUtils.isEmpty(getExistingGooglePlaySubscriptionId(currentActivity))) {
                    title = currentActivity.getString(R.string.app_cms_subscription_google_play_cancel_title);
                    message = currentActivity.getString(R.string.app_cms_subscription_cancel_for_google_play_user_dialog);
                }
            }

            if (dialogType == DialogType.UNKNOWN_SUBSCRIPTION_FOR_UPGRADE) {
                title = currentActivity.getString(R.string.app_cms_unknown_subscription_for_upgrade_title);
                message = currentActivity.getString(R.string.app_cms_unknown_subscription_for_upgrade_text);
            }

            if (dialogType == DialogType.UNKNOWN_SUBSCRIPTION_FOR_CANCEL) {
                title = currentActivity.getString(R.string.app_cms_unknown_subscription_for_cancellation_title);
                message = currentActivity.getString(R.string.app_cms_unknown_subscription_for_cancellation_text);
            }

            if (dialogType == DialogType.LOGIN_REQUIRED) {
                title = currentActivity.getString(R.string.app_cms_login_required_title);
                message = currentActivity.getString(R.string.app_cms_login_required_message);
                positiveButtonText = currentActivity.getString(R.string.app_cms_login_button_text);
                //Set Firbase User Propert when user is not logged_in and unsubscribed
                mFireBaseAnalytics.setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_OUT);
                mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_NOT_SUBSCRIBED);
            }

            if (dialogType == DialogType.SUBSCRIPTION_REQUIRED) {
                mFireBaseAnalytics.setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_IN);
                mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_NOT_SUBSCRIBED);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
            builder.setTitle(Html.fromHtml(currentActivity.getString(R.string.text_with_color,
                    Integer.toHexString(textColor).substring(2),
                    title)))
                    .setMessage(Html.fromHtml(currentActivity.getString(R.string.text_with_color,
                            Integer.toHexString(textColor).substring(2),
                            message)));

            if (dialogType == DialogType.LOGOUT_WITH_RUNNING_DOWNLOAD) {
                builder.setPositiveButton("Yes",
                        (dialog, which) -> {
                            try {
                                removeDownloadAndLogout();
                                dialog.dismiss();
                            } catch (Exception e) {
                                Log.e(TAG, "Error displaying dialog while logging out with running download: " + e.getMessage());
                            }
                        });
                builder.setNegativeButton("Cancel",
                        (dialog, which) -> {
                            try {
                                dialog.dismiss();
                            } catch (Exception e) {
                                Log.e(TAG, "Error cancelling dialog while logging out with running download: " + e.getMessage());
                            }
                        });
            } else if (dialogType == DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED) {
                builder.setPositiveButton(R.string.app_cms_login_button_text,
                        (dialog, which) -> {
                            try {
                                dialog.dismiss();
                                launchType = LaunchType.LOGIN_AND_SIGNUP;
                                navigateToLoginPage();
                            } catch (Exception e) {
                                Log.e(TAG, "Error closing login & subscription required dialog: " + e.getMessage());
                            }
                        });
                builder.setNegativeButton(R.string.app_cms_subscription_button_text,
                        (dialog, which) -> {
                            try {
                                dialog.dismiss();
                                navigateToSubscriptionPlansPage(null, null);
                            } catch (Exception e) {
                                Log.e(TAG, "Error closing subscribe dialog: " + e.getMessage());
                            }
                        });
            } else if (dialogType == DialogType.CANNOT_UPGRADE_SUBSCRIPTION ||
                    dialogType == DialogType.UPGRADE_UNAVAILABLE) {
                builder.setPositiveButton("OK", null);
            } else if (dialogType == DialogType.CANNOT_CANCEL_SUBSCRIPTION) {
                builder.setPositiveButton("OK", null);
            } else if (dialogType == DialogType.LOGIN_REQUIRED) {
                builder.setPositiveButton(positiveButtonText,
                        (dialog, which) -> {
                            try {
                                dialog.dismiss();
                                navigateToLoginPage();
                            } catch (Exception e) {
                                Log.e(TAG, "Error closing login required dialog: " + e.getMessage());
                            }
                        });
            } else if (dialogType == DialogType.UNKNOWN_SUBSCRIPTION_FOR_UPGRADE ||
                    dialogType == DialogType.UNKNOWN_SUBSCRIPTION_FOR_CANCEL) {
                builder.setPositiveButton("OK",
                        (dialog, which) -> {
                            try {
                                dialog.dismiss();
                            } catch (Exception e) {
                                Log.e(TAG, "Error closing subscription required dialog: " + e.getMessage());
                            }
                        });
            } else {
                builder.setPositiveButton(positiveButtonText,
                        (dialog, which) -> {
                            try {
                                dialog.dismiss();
                                navigateToSubscriptionPlansPage(null, null);
                            } catch (Exception e) {
                                Log.e(TAG, "Error closing navigate to subscription dialog: " + e.getMessage());
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
                        try {
                            dialog.dismiss();
                            if (oncConfirmationAction != null) {
                                Observable.just(false).subscribe(oncConfirmationAction);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error closing confirm cancellation dialog: " + e.getMessage());
                        }
                    });
            builder.setCancelable(false);
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

                case SIGNUP_BLANK_EMAIL_PASSWORD:
                case SIGNUP_BLANK_EMAIL:
                case SIGNUP_BLANK_PASSWORD:
                case SIGNUP_EMAIL_MATCHES_PASSWORD:
                case SIGNUP_PASSWORD_INVALID:
                case SIGNUP_NAME_MATCHES_PASSWORD:
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

                case DELETE_ONE_HISTORY_ITEM:
                case DELETE_ALL_HISTORY_ITEMS:
                    title = currentActivity.getString(R.string.app_cms_delete_history_alert_title);
                    message = optionalMessage;
                    break;

                case DOWNLOAD_INCOMPLETE:
                    title = currentActivity.getString(R.string.app_cms_download_incomplete_error_title);
                    message = currentActivity.getString(R.string.app_cms_download_incomplete_error_message);
                    break;

                case STREAMING_INFO_MISSING:
                    title = currentActivity.getString(R.string.app_cms_download_stream_info_error_title);
                    message = currentActivity.getString(R.string.app_cms_download_streaming_info_error_message);
                    break;

                case REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_FOR_DOWNLOAD:
                    title = currentActivity.getString(R.string.app_cms_download_external_storage_write_permission_info_error_title);
                    message = optionalMessage;
                    break;

                case SD_CARD_NOT_AVAILABLE:
                    title = currentActivity.getString(R.string.app_cms_sdCard_unavailable_error_title);
                    message = currentActivity.getString(R.string.app_cms_sdCard_unavailable_error_message);
                    break;

                case DOWNLOAD_NOT_AVAILABLE:
                    title = currentActivity.getString(R.string.app_cms_download_unavailable_error_title);
                    message = optionalMessage;
                    break;

                case DOWNLOAD_FAILED:
                    title = currentActivity.getString(R.string.app_cms_download_failed_error_title);
                    message = optionalMessage;
                    break;

                default:
                    title = currentActivity.getString(R.string.app_cms_network_connectivity_error_title);
                    if (optionalMessage != null) {
                        message = optionalMessage;
                    } else {
                        message = currentActivity.getString(R.string.app_cms_network_connectivity_error_message);
                    }
                    if (isNetworkConnected()) {
                        title = currentActivity.getString(R.string.app_cms_data_error_title);
                        message = currentActivity.getString(R.string.app_cms_data_error_message);
                    }
                    break;
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
                            try {
                                dialog.dismiss();
                                if (onDismissAction != null) {
                                    onDismissAction.call();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error closing cancellation dialog: " + e.getMessage());
                            }
                        });
                builder.setNegativeButton(R.string.app_cms_cancel_alert_dialog_button_text,
                        (dialog, which) -> dialog.dismiss());
            } else {
                builder.setNegativeButton(R.string.app_cms_close_alert_dialog_button_text,
                        (dialog, which) -> {
                            try {
                                dialog.dismiss();
                                if (onDismissAction != null) {
                                    onDismissAction.call();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error closing cancellation dialog: " + e.getMessage());
                            }
                        });
            }

            builder.setCancelable(false);

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
        Log.d(TAG, "Beacon Ad Impression: " + url);
        beaconMessageRunnable.setUrl(url);
        beaconMessageThread.run();
    }

    public void sendBeaconAdRequestMessage(String vid, String screenName, String parentScreenName,
                                           long currentPosition) {
        Log.d(TAG, "Sending Beacon Ad Request");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition,
                BeaconEvent.AD_REQUEST, false);
        Log.d(TAG, "Beacon Ad Request: " + url);
        beaconMessageRunnable.setUrl(url);
        beaconMessageThread.run();
    }

    public void sendBeaconPingMessage(String vid, String screenName, String parentScreenName,
                                      long currentPosition, boolean usingChromecast) {
        Log.d(TAG, "Sending Beacon Ping Message");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition, BeaconEvent.PING, usingChromecast);
        Log.d(TAG, "Beacon Ping: " + url);
        beaconMessageRunnable.setUrl(url);
        beaconMessageThread.run();
    }

    public void sendBeaconPlayMessage(String vid, String screenName, String parentScreenName,
                                      long currentPosition, boolean usingChromecast) {
        Log.d(TAG, "Sending Beacon Play Message");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition, BeaconEvent.PLAY, usingChromecast);
        Log.d(TAG, "Beacon Play: " + url);
        beaconMessageRunnable.setUrl(url);
        beaconMessageThread.run();
    }


    public ArrayList<BeaconRequest> getBeaconRequestList() {
        String uid = InstanceID.getInstance(currentActivity).getId();
        if (isUserLoggedIn(currentActivity)) {
            uid = getLoggedInUser(currentActivity);
        }

        ArrayList<BeaconRequest> beaconRequestList = new ArrayList<>();
        try {
            for (OfflineBeaconData offlineBeaconData : realmController.getOfflineBeaconDataListByUser(uid)) {
                BeaconRequest beaconRequest = offlineBeaconData.convertToBeaconRequest();
                beaconRequestList.add(beaconRequest);
            }
            return beaconRequestList;
        } catch (Exception e) {
            return null;
        }
    }

    public void sendOfflineBeaconMessage() {
        ArrayList<BeaconRequest> beaconRequests = getBeaconRequestList();

        String url = getBeaconUrl();
        AppCMSBeaconRequest request = new AppCMSBeaconRequest();

        if (url != null && beaconRequests != null) {
            System.out.println("Beacon data : " + gson.toJson(beaconRequests));
            request.setBeaconRequest(beaconRequests);
            appCMSBeaconCall.call(url, (Boolean aBoolean) -> {
                try {

                    if (aBoolean) {
                        Log.d(TAG, "Beacon success Event: Offline " + aBoolean);
                        currentActivity.runOnUiThread(() -> {
                            realmController.deleteOfflineBeaconDataByUser(getLoggedInUser(currentActivity));
                        });
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Beacon fail Event: offline  due to: " + e.getMessage());
                }
            }, request);
        } else {
            Log.d(TAG, "No offline Beacon Event: available ");

        }
    }

    public void sendBeaconMessage(String vid, String screenName, String parentScreenName,
                                  long currentPosition, boolean usingChromecast, BeaconEvent event,
                                  String mediaType, String bitrate, String height, String width,
                                  String streamid, double ttfirstframe, int apod) {

        if (currentActivity != null) {
            currentActivity.runOnUiThread(() -> {
                try {
                    BeaconRequest beaconRequest = getBeaconRequest(vid, screenName, parentScreenName, currentPosition, event,
                            usingChromecast, mediaType, bitrate, height, width, streamid, ttfirstframe, apod);
                    if (!isNetworkConnected()) {
                        currentActivity.runOnUiThread(() -> {
                            try {
                                realmController.addOfflineBeaconData(beaconRequest.convertToOfflineBeaconData());
                            } catch (Exception e) {
                                Log.e(TAG, "Error adding offline Beacon data: " + e.getMessage());
                            }
                        });

                        Log.d(TAG, "Beacon info added to database +++ " + event);
                        return;
                    }
                    String url = getBeaconUrl();
                    AppCMSBeaconRequest request = new AppCMSBeaconRequest();
                    ArrayList<BeaconRequest> beaconRequests = new ArrayList<>();

                    beaconRequests.add(beaconRequest);


                    request.setBeaconRequest(beaconRequests);
                    if (url != null) {

                        appCMSBeaconCall.call(url, aBoolean -> {
                            try {

                                if (aBoolean) {
                                    Log.d(TAG, "Beacon success Event:  " + event);
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "Beacon fail Event: " + event + " due to: " + e.getMessage());
                            }
                        }, request);

                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error sending new beacon message: " + e.getMessage());
                }
            });
        }
    }

    public String getStreamingId(String filmName) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        SecretKeySpec key = new SecretKeySpec((getCurrentTimeStamp()).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);
        byte[] bytes = mac.doFinal(filmName.getBytes("UTF-8"));
        return UUID.nameUUIDFromBytes(bytes).toString();

    }

    private String getPermalinkCompletePath(String pagePath) {
        StringBuffer permalinkCompletePath = new StringBuffer();
        permalinkCompletePath.append(currentActivity.getString(R.string.https_scheme));
        permalinkCompletePath.append(appCMSMain.getDomainName());
        permalinkCompletePath.append(File.separatorChar);
        permalinkCompletePath.append(pagePath);
        return permalinkCompletePath.toString();
    }

    private BeaconRequest getBeaconRequest(String vid, String screenName, String parentScreenName,
                                           long currentPosition, BeaconEvent event, boolean usingChromecast,
                                           String mediaType, String bitrte, String resolutionHeight,
                                           String resolutionWidth, String streamId, double ttfirstframe, int apod) {
        BeaconRequest beaconRequest = new BeaconRequest();
        String uid = InstanceID.getInstance(currentActivity).getId();
        int currentPositionSecs = (int) (currentPosition / MILLISECONDS_PER_SECOND);
        if (isUserLoggedIn(currentActivity)) {
            uid = getLoggedInUser(currentActivity);
        }


        beaconRequest.setAid(appCMSMain.getBeacon().getSiteName());
        beaconRequest.setCid(appCMSMain.getBeacon().getClientId());
        beaconRequest.setPfm((platformType == PlatformType.TV) ?
                currentActivity.getString(R.string.app_cms_beacon_tvplatform) :
                currentActivity.getString(R.string.app_cms_beacon_platform));
        beaconRequest.setVid(vid);
        beaconRequest.setUid(uid);
        beaconRequest.setPa(event.toString());
        beaconRequest.setMedia_type(mediaType);
        beaconRequest.setTstampoverride(getCurrentTimeStamp());
        beaconRequest.setStream_id(streamId);
        beaconRequest.setDp1(currentActivity.getString(R.string.app_cms_beacon_dpm_android));
        beaconRequest.setUrl(getPermalinkCompletePath(screenName));
        beaconRequest.setRef(parentScreenName);
        beaconRequest.setVpos(currentPositionSecs);
        beaconRequest.setApos(currentPositionSecs);
        beaconRequest.setEnvironment(getEnvironment());
        beaconRequest.setBitrate(bitrte);
        beaconRequest.setResolutionheight(resolutionHeight);
        beaconRequest.setResolutionwidth(resolutionWidth);
        if (event == BeaconEvent.FIRST_FRAME) {
            beaconRequest.setTtfirstframe(String.format("%.2f", ttfirstframe));
        }
        if (event == BeaconEvent.AD_IMPRESSION || event == BeaconEvent.AD_REQUEST) {
            beaconRequest.setApod(apod);
        }

        if (isVideoDownloaded(vid)) {
            beaconRequest.setDp2("downloaded_view-online");
        }
        if (usingChromecast) {
            beaconRequest.setPlayer("Chromecast");
        } else {
            beaconRequest.setPlayer("Video Player");
        }


        return beaconRequest;
    }

    public String getCurrentTimeStamp() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    public String getEnvironment() {
        String environment = "unknown";
        if (appCMSMain.getApiBaseUrl().contains("prod")) {
            environment = "production";
        } else if (appCMSMain.getApiBaseUrl().contains("release")) {
            environment = "release";
        } else if (appCMSMain.getApiBaseUrl().contains("preprod")) {
            environment = "preprod";
        } else if (appCMSMain.getApiBaseUrl().contains("develop")) {
            environment = "develop";
        } else if (appCMSMain.getApiBaseUrl().contains("staging")) {
            environment = "staging";
        } else if (appCMSMain.getApiBaseUrl().contains("qa")) {
            environment = "qa";
        }

        return environment;

    }


    private String getBeaconUrl() {
        return currentActivity.getString(R.string.app_cms_beacon_url_base, appCMSMain.getBeacon().getApiBaseUrl());

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

                        URLEncoder.encode(
                                (platformType == PlatformType.TV) ?
                                        currentActivity.getString(R.string.app_cms_beacon_tvplatform) :
                                        currentActivity.getString(R.string.app_cms_beacon_platform),
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

    public void finalizeSignupAfterCCAvenueSubscription(Intent data) {
        String url = currentActivity.getString(R.string.app_cms_signin_api_url,
                appCMSMain.getApiBaseUrl(),
                appCMSSite.getGist().getSiteInternalName());
        startLoginAsyncTask(url,
                subscriptionUserEmail,
                subscriptionUserPassword,
                false,
                false,
                true,
                true,
                false);


//        try {
//            appCMSSubscriptionPlanCall.call(
//                    currentActivity.getString(R.string.app_cms_register_subscription_api_url,
//                            appCMSMain.getApiBaseUrl(),
//                            appCMSSite.getGist().getSiteInternalName(),
//                            currentActivity.getString(R.string.app_cms_subscription_platform_key)),
//                    subscriptionCallType,
//                    subscriptionRequest,
//                    apikey,
//                    getAuthToken(currentActivity),
//                    result -> {
//                        //
//                    });
//        } catch (Exception ex) {
//            ex.
//        }

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
                            appCMSSite.getGist().getSiteInternalName(),
                            currentActivity.getString(R.string.app_cms_subscription_platform_key)),
                    subscriptionCallType,
                    subscriptionRequest,
                    apikey,
                    getAuthToken(currentActivity),
                    result -> {
                        //
                    },
                    appCMSSubscriptionPlanResult -> {
                        try {
                            if (appCMSSubscriptionPlanResult != null) {
                                Log.d(TAG, "Subscription response: " + gson.toJson(appCMSSubscriptionPlanResult,
                                        AppCMSSubscriptionPlanResult.class));
                            }
                            setActiveSubscriptionSku(currentActivity, skuToPurchase);
                            AppsFlyerUtils.subscriptionEvent(currentActivity,
                                    true,
                                    currentActivity.getString(R.string.app_cms_appsflyer_dev_key),
                                    String.valueOf(planToPurchasePrice),
                                    subscriptionRequest.getPlanId(),
                                    subscriptionRequest.getCurrencyCode());

                            //Subscription Succes Firebase Log Event
                            Bundle bundle = new Bundle();
                            bundle.putString(FIREBASE_PLAN_ID, subscriptionRequest.getPlanId());
                            bundle.putString(FIREBASE_PLAN_NAME, planToPurchaseName);
                            bundle.putString(FIREBASE_CURRENCY_NAME, currencyOfPlanToPurchase);
                            bundle.putString(FIREBASE_VALUE, String.valueOf(planToPurchasePrice));
                            if (mFireBaseAnalytics != null)
                                mFireBaseAnalytics.logEvent(FIREBASE_ECOMMERCE_PURCHASE, bundle);

                            setActiveSubscriptionId(currentActivity, planToPurchase);
                            setActiveSubscriptionCurrency(currentActivity, currencyOfPlanToPurchase);
                            setActiveSubscriptionPlanName(currentActivity, planToPurchaseName);
                            setActiveSubscriptionPrice(currentActivity, String.valueOf(planToPurchasePrice));
                            skuToPurchase = null;
                            planToPurchase = null;
                            currencyOfPlanToPurchase = null;
                            planToPurchaseName = null;
                            planToPurchasePrice = 0.0f;
                            countryCode = "";
                            if (launchType == LaunchType.SUBSCRIBE) {
                                launchType = LaunchType.LOGIN_AND_SIGNUP;
                                String url = currentActivity.getString(R.string.app_cms_signin_api_url,
                                        appCMSMain.getApiBaseUrl(),
                                        appCMSSite.getGist().getSiteInternalName());
                                startLoginAsyncTask(url,
                                        subscriptionUserEmail,
                                        subscriptionUserPassword,
                                        false,
                                        false,
                                        true,
                                        true,
                                        false);
                            }
                            if (signupFromFacebook) {
                                setFacebookAccessToken(currentActivity,
                                        facebookAccessToken,
                                        facebookUserId,
                                        facebookUsername,
                                        facebookEmail,
                                        false);
                            } else if (isSignupFromGoogle) {
                                setGoogleAccessToken(currentActivity,
                                        googleAccessToken,
                                        googleUserId,
                                        googleUsername,
                                        googleEmail,
                                        false);
                            }
                            subscriptionUserEmail = null;
                            subscriptionUserPassword = null;
                            facebookAccessToken = null;
                            facebookUserId = null;

                            googleAccessToken = null;
                            googleUserId = null;
                        } catch (Exception e) {
                            Log.e(TAG, "Error getting subscription plan result: " + e.getMessage());
                        }
                    },
                    planResult -> {
                        //
                    });
        } catch (IOException e) {
            Log.e(TAG, "Failed to update user subscription status");
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
        double activeSubscriptionPrice = parseActiveSubscriptionPrice(currentActivity);
        if (availableUpgradesForUser != null && activeSubscriptionPrice != 0.0f) {
            for (int i = 0; i < availableUpgradesForUser.size(); i++) {
                if (activeSubscriptionPrice <
                        availableUpgradesForUser.get(i).getSubscriptionPrice()) {
                    return true;
                }
            }
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
            final String PASSWORD_VERIFICATION_REGEX = "^\\S{5,50}$";

            String userName = getLoggedInUserName(currentActivity);

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                showDialog(DialogType.SIGNUP_BLANK_EMAIL_PASSWORD,
                        currentActivity.getString(R.string.app_cms_signup_invalid_email_and_password_message),
                        false,
                        null);
            } else if (TextUtils.isEmpty(email)) {
                showDialog(DialogType.SIGNUP_BLANK_EMAIL,
                        currentActivity.getString(R.string.app_cms_signup_invalid_email_message),
                        false,
                        null);
            } else if (TextUtils.isEmpty(password)) {
                showDialog(DialogType.SIGNUP_BLANK_PASSWORD,
                        currentActivity.getString(R.string.app_cms_signup_blank_password_message),
                        false,
                        null);
            } else if (!TextUtils.isEmpty(userName) &&
                    userName.equals(password)) {
                showDialog(DialogType.SIGNUP_NAME_MATCHES_PASSWORD,
                        currentActivity.getString(R.string.app_cms_signup_name_matches_password_message),
                        false,
                        null);
            } else if (!TextUtils.isEmpty(email) &&
                    !TextUtils.isEmpty(password)) {
                int atIndex = email.indexOf("@");
                if (0 < atIndex) {
                    String emailName = email.substring(0, atIndex);
                    if (emailName.equals(password)) {
                        showDialog(DialogType.SIGNUP_EMAIL_MATCHES_PASSWORD,
                                currentActivity.getString(R.string.app_cms_signup_email_matches_password_message),
                                false,
                                null);
                    }
                }
            } else if (!TextUtils.isEmpty(password) &&
                    !password.matches(PASSWORD_VERIFICATION_REGEX)) {
                showDialog(DialogType.SIGNUP_PASSWORD_INVALID,
                        currentActivity.getString(R.string.app_cms_signup_invalid_password_message),
                        false,
                        null);
            } else {
                String url = currentActivity.getString(R.string.app_cms_signup_api_url,
                        appCMSMain.getApiBaseUrl(),
                        appCMSSite.getGist().getSiteInternalName());
                startLoginAsyncTask(url,
                        email,
                        password,
                        true,
                        launchType == LaunchType.SUBSCRIBE,
                        false,
                        false,
                        false);
            }
        }
    }

    public void refreshSubscriptionData(Action0 onRefreshReadyAction) {
        try {
            if (currentActivity != null && isUserLoggedIn(currentActivity)) {
                if (shouldRefreshAuthToken()) {
                    refreshIdentity(getRefreshToken(currentActivity),
                            () -> {
                                try {
                                    getPageIdContent(appCMSMain.getApiBaseUrl(),
                                            pageIdToPageAPIUrlMap.get(subscriptionPage.getPageId()),
                                            appCMSSite.getGist().getSiteInternalName(),
                                            true,
                                            subscriptionPage.getPageId(),
                                            appCMSPageAPI -> {
                                                if (appCMSPageAPI != null
                                                        && appCMSPageAPI.getModules() != null) {
                                                    List<SubscriptionPlan> subscriptionPlans = new ArrayList<>();
                                                    for (Module module : appCMSPageAPI.getModules()) {
                                                        if (!TextUtils.isEmpty(module.getModuleType()) &&
                                                                module.getModuleType().equals(currentActivity.getString(R.string.app_cms_view_plan_module_key))) {
                                                            if (module.getContentData() != null &&
                                                                    !module.getContentData().isEmpty()) {
                                                                for (ContentDatum contentDatum : module.getContentData()) {
                                                                    SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
                                                                    subscriptionPlan.setSku(contentDatum.getIdentifier());
                                                                    subscriptionPlan.setPlanId(contentDatum.getId());
                                                                    if (!contentDatum.getPlanDetails().isEmpty()) {
                                                                        subscriptionPlan.setSubscriptionPrice(contentDatum.getPlanDetails().get(0).getRecurringPaymentAmount());
                                                                    }
                                                                    subscriptionPlan.setPlanName(contentDatum.getName());
                                                                    createSubscriptionPlan(subscriptionPlan);
                                                                    subscriptionPlans.add(subscriptionPlan);
                                                                }
                                                            }
                                                        }
                                                    }

                                                    try {
                                                        appCMSSubscriptionPlanCall.call(
                                                                currentActivity.getString(R.string.app_cms_get_current_subscription_api_url,
                                                                        appCMSMain.getApiBaseUrl(),
                                                                        appCMSSite.getGist().getSiteInternalName()),
                                                                R.string.app_cms_subscription_subscribed_plan_key,
                                                                null,
                                                                apikey,
                                                                getAuthToken(currentActivity),
                                                                listResult -> {
                                                                    //
                                                                },
                                                                singleResult -> {
                                                                    //
                                                                },
                                                                appCMSSubscriptionPlanResult -> {
                                                                    try {
                                                                        if (appCMSSubscriptionPlanResult != null) {

                                                                            UserSubscriptionPlan userSubscriptionPlan = new UserSubscriptionPlan();
                                                                            userSubscriptionPlan.setUserId(getLoggedInUser(currentActivity));
                                                                            String planReceipt = appCMSSubscriptionPlanResult.getSubscriptionInfo().getReceipt();
                                                                            Receipt receipt = gson.fromJson(planReceipt, Receipt.class);
                                                                            userSubscriptionPlan.setPlanReceipt(planReceipt);
                                                                            userSubscriptionPlan.setPaymentHandler(appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler());

                                                                            SubscriptionPlan subscribedPlan = null;
                                                                            if (subscriptionPlans != null) {
                                                                                for (SubscriptionPlan subscriptionPlan : subscriptionPlans) {
                                                                                    if (!TextUtils.isEmpty(subscriptionPlan.getSku()) &&
                                                                                            receipt != null &&
                                                                                            subscriptionPlan.getSku().equals(receipt.getProductId())) {
                                                                                        subscribedPlan = subscriptionPlan;
                                                                                    }
                                                                                }
                                                                            }

                                                                            if (subscribedPlan != null) {
                                                                                setActiveSubscriptionSku(currentActivity,
                                                                                        subscribedPlan.getSku());
                                                                                setActiveSubscriptionId(currentActivity,
                                                                                        subscribedPlan.getPlanId());
                                                                                setActiveSubscriptionPlanName(currentActivity,
                                                                                        subscribedPlan.getPlanName());
                                                                                setActiveSubscriptionPrice(currentActivity,
                                                                                        String.valueOf(subscribedPlan.getSubscriptionPrice()));
                                                                            } else if (appCMSSubscriptionPlanResult.getSubscriptionPlanInfo() != null) {
                                                                                setActiveSubscriptionSku(currentActivity,
                                                                                        appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getIdentifier());
                                                                                setActiveSubscriptionId(currentActivity,
                                                                                        appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getId());
                                                                                setActiveSubscriptionPlanName(currentActivity,
                                                                                        appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getName());
                                                                                setActiveSubscriptionPrice(currentActivity,
                                                                                        String.valueOf(appCMSSubscriptionPlanResult.getSubscriptionInfo().getTotalAmount()));
                                                                            }

                                                                            if (appCMSSubscriptionPlanResult.getSubscriptionInfo() != null &&
                                                                                    !TextUtils.isEmpty(appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler())) {
                                                                                String paymentHandler = appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler();
                                                                                if (paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor)) ||
                                                                                        paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor_friendly))) {
                                                                                    setActiveSubscriptionProcessor(currentActivity,
                                                                                            currentActivity.getString(R.string.subscription_ios_payment_processor_friendly));
                                                                                } else if (paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) ||
                                                                                        paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly))) {
                                                                                    setActiveSubscriptionProcessor(currentActivity,
                                                                                            currentActivity.getString(R.string.subscription_android_payment_processor_friendly));
                                                                                } else if (paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_web_payment_processor_friendly))) {
                                                                                    setActiveSubscriptionProcessor(currentActivity,
                                                                                            currentActivity.getString(R.string.subscription_web_payment_processor_friendly));
                                                                                }
                                                                            }

                                                                            setIsUserSubscribed(currentActivity, true);
                                                                        }
                                                                    } catch (Exception e) {
                                                                        Log.e(TAG, "refreshSubscriptionData: " + e.getMessage());
                                                                    }
                                                                }
                                                        );
                                                    } catch (Exception e) {
                                                        Log.e(TAG, "refreshSubscriptionData: " + e.getMessage());
                                                    }
                                                }
                                            });
                                } catch (Exception e) {
                                    Log.e(TAG, "getSubscriptionPageContent: " + e.toString());
                                }
                            });
                } else {
                    try {
                        getPageIdContent(appCMSMain.getApiBaseUrl(),
                                pageIdToPageAPIUrlMap.get(subscriptionPage.getPageId()),
                                appCMSSite.getGist().getSiteInternalName(),
                                true,
                                subscriptionPage.getPageId(),
                                appCMSPageAPI -> {
                                    try {
                                        List<SubscriptionPlan> subscriptionPlans = new ArrayList<>();
                                        for (Module module : appCMSPageAPI.getModules()) {
                                            if (!TextUtils.isEmpty(module.getModuleType()) &&
                                                    module.getModuleType().equals(currentActivity.getString(R.string.app_cms_view_plan_module_key))) {
                                                if (module.getContentData() != null &&
                                                        !module.getContentData().isEmpty()) {
                                                    for (ContentDatum contentDatum : module.getContentData()) {
                                                        SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
                                                        subscriptionPlan.setSku(contentDatum.getIdentifier());
                                                        subscriptionPlan.setPlanId(contentDatum.getId());
                                                        if (!contentDatum.getPlanDetails().isEmpty()) {
                                                            subscriptionPlan.setSubscriptionPrice(contentDatum.getPlanDetails().get(0).getRecurringPaymentAmount());
                                                        }
                                                        subscriptionPlan.setPlanName(contentDatum.getName());
                                                        createSubscriptionPlan(subscriptionPlan);
                                                        subscriptionPlans.add(subscriptionPlan);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error retrieving subscription information: " + e.getMessage());
                                    }

                                    try {
                                        appCMSSubscriptionPlanCall.call(
                                                currentActivity.getString(R.string.app_cms_get_current_subscription_api_url,
                                                        appCMSMain.getApiBaseUrl(),
                                                        appCMSSite.getGist().getSiteInternalName()),
                                                R.string.app_cms_subscription_subscribed_plan_key,
                                                null,
                                                apikey,
                                                getAuthToken(currentActivity),
                                                listResult -> {
                                                    //
                                                },
                                                singleResult -> {
                                                    //
                                                },
                                                appCMSSubscriptionPlanResult -> {

                                                    try {

                                                        if (appCMSSubscriptionPlanResult != null) {

                                                            UserSubscriptionPlan userSubscriptionPlan = new UserSubscriptionPlan();
                                                            userSubscriptionPlan.setUserId(getLoggedInUser(currentActivity));
                                                            String planReceipt = appCMSSubscriptionPlanResult.getSubscriptionInfo().getReceipt();
                                                            Receipt receipt = gson.fromJson(planReceipt, Receipt.class);
                                                            userSubscriptionPlan.setPlanReceipt(planReceipt);
                                                            userSubscriptionPlan.setPaymentHandler(appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler());

                                                            SubscriptionPlan subscribedPlan = null;
                                                            if (subscriptionPlans != null) {
                                                                for (SubscriptionPlan subscriptionPlan : subscriptionPlans) {
                                                                    if (!TextUtils.isEmpty(subscriptionPlan.getSku()) &&
                                                                            receipt != null &&
                                                                            subscriptionPlan.getSku().equals(receipt.getProductId())) {
                                                                        subscribedPlan = subscriptionPlan;
                                                                    }
                                                                }
                                                            }

                                                            if (subscribedPlan != null) {
                                                                setActiveSubscriptionSku(currentActivity,
                                                                        subscribedPlan.getSku());
                                                                setActiveSubscriptionId(currentActivity,
                                                                        subscribedPlan.getPlanId());
                                                                setActiveSubscriptionPlanName(currentActivity,
                                                                        subscribedPlan.getPlanName());
                                                                setActiveSubscriptionPrice(currentActivity,
                                                                        String.valueOf(subscribedPlan.getSubscriptionPrice()));
                                                            } else if (appCMSSubscriptionPlanResult.getSubscriptionPlanInfo() != null) {
                                                                setActiveSubscriptionSku(currentActivity,
                                                                        appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getIdentifier());
                                                                setActiveSubscriptionId(currentActivity,
                                                                        appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getId());
                                                                setActiveSubscriptionPlanName(currentActivity,
                                                                        appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getName());
                                                                setActiveSubscriptionPrice(currentActivity,
                                                                        String.valueOf(appCMSSubscriptionPlanResult.getSubscriptionInfo().getTotalAmount()));
                                                            }

                                                            if (appCMSSubscriptionPlanResult.getSubscriptionInfo() != null &&
                                                                    !TextUtils.isEmpty(appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler())) {
                                                                String paymentHandler = appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler();
                                                                if (paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor)) ||
                                                                        paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor_friendly))) {
                                                                    setActiveSubscriptionProcessor(currentActivity,
                                                                            currentActivity.getString(R.string.subscription_ios_payment_processor_friendly));
                                                                } else if (paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) ||
                                                                        paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly))) {
                                                                    setActiveSubscriptionProcessor(currentActivity,
                                                                            currentActivity.getString(R.string.subscription_android_payment_processor_friendly));
                                                                } else if (paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_web_payment_processor_friendly))) {
                                                                    setActiveSubscriptionProcessor(currentActivity,
                                                                            currentActivity.getString(R.string.subscription_web_payment_processor_friendly));
                                                                }
                                                            }

                                                            setIsUserSubscribed(currentActivity, true);
                                                        }

                                                        if (onRefreshReadyAction != null) {
                                                            onRefreshReadyAction.call();
                                                        }
                                                    } catch (Exception e) {
                                                        Log.e(TAG, "refreshSubscriptionData: " + e.getMessage());
                                                    }
                                                }
                                        );
                                    } catch (Exception e) {
                                        Log.e(TAG, "refreshSubscriptionData: " + e.getMessage());
                                    }
                                });
                    } catch (Exception e) {
                        Log.e(TAG, "refreshSubscriptionData: " + e.getMessage());
                    }
                }
            } else {
                onRefreshReadyAction.call();
            }
        } catch (Exception e) {
            Log.e(TAG, "Caught exception when attempting to refresh subscription data: " + e.getMessage());
        }
    }

    public void refreshPageAPIData(AppCMSPageUI appCMSPageUI,
                                   String pageId,
                                   Action1<AppCMSPageAPI> appCMSPageAPIReadyAction) {
        getPageIdContent(appCMSMain.getApiBaseUrl(),
                pageIdToPageAPIUrlMap.get(pageId),
                appCMSSite.getGist().getSiteInternalName(),
                true,
                getPageId(appCMSPageUI),
                appCMSPageAPIReadyAction);
    }

    public void login(String email, String password) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_signin_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSSite.getGist().getSiteInternalName());
            startLoginAsyncTask(url,
                    email,
                    password,
                    false,
                    false,
                    false,
                    false,
                    true);
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

    private RealmList<SubscriptionPlan> getAvailableUpgradePlans(RealmResults<SubscriptionPlan> availablePlans) {
        RealmList<SubscriptionPlan> availableUpgrades = new RealmList<>();
        if (currentActivity != null && availablePlans != null) {
            double existingSubscriptionPrice = parseActiveSubscriptionPrice(currentActivity);
            String existingSku = getActiveSubscriptionSku(currentActivity);

            if (existingSubscriptionPrice == 0.0) {
                existingSubscriptionPrice = parseExistingGooglePlaySubscriptionPrice(currentActivity);
            }

            if (existingSubscriptionPrice != 0.0) {
                for (SubscriptionPlan subscriptionPlan : availablePlans) {
                    if (existingSubscriptionPrice < subscriptionPlan.getSubscriptionPrice() &&
                            (TextUtils.isEmpty(existingSku)) ||
                            (!TextUtils.isEmpty(existingSku) && !existingSku.equals(subscriptionPlan.getSku()))) {
                        availableUpgrades.add(subscriptionPlan);
                    }
                }
            }
        }
        return availableUpgrades;
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
                                     boolean followWithSubscription,
                                     boolean suppressErrorMessages,
                                     boolean forceSubscribed,
                                     boolean refreshSubscriptionData) {
        PostAppCMSLoginRequestAsyncTask.Params params = new PostAppCMSLoginRequestAsyncTask.Params
                .Builder()
                .url(url)
                .email(email)
                .password(password)
                .build();
        new PostAppCMSLoginRequestAsyncTask(appCMSSignInCall,
                signInResponse -> {
                    try {
                        if (signInResponse == null) {
                            // Show log error
                            Log.e(TAG, "Email and password are not valid.");
                            if (!suppressErrorMessages) {
                                if (signup) {
                                    showDialog(DialogType.SIGNUP_PASSWORD_INVALID, currentActivity.getString(
                                            R.string.app_cms_error_user_already_exists), false, null);
                                } else {
                                    showDialog(DialogType.SIGNIN, currentActivity.getString(
                                            R.string.app_cms_error_email_password), false, null);
                                }
                            }
                        } else {
                            setRefreshToken(currentActivity, signInResponse.getRefreshToken());
                            setAuthToken(currentActivity, signInResponse.getAuthorizationToken());
                            setLoggedInUser(currentActivity, signInResponse.getUserId());
                            setLoggedInUserName(currentActivity, signInResponse.getName());
                            setLoggedInUserEmail(currentActivity, signInResponse.getEmail());

                            if (forceSubscribed) {
                                setIsUserSubscribed(currentActivity, true);
                            } else {
                                setIsUserSubscribed(currentActivity, signInResponse.isSubscribed());
                            }

                            checkForExistingSubscription(false);

                            if (TextUtils.isEmpty(getUserDownloadQualityPref(currentActivity))) {
                                setUserDownloadQualityPref(currentActivity,
                                        currentActivity.getString(R.string.app_cms_default_download_quality));
                            }

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
                                if (appCMSMain.getServiceType()
                                        .equals(currentActivity.getString(R.string.app_cms_main_svod_service_type_key)) &&
                                        refreshSubscriptionData) {
                                    refreshSubscriptionData(() -> {
                                        if (entitlementPendingVideoData != null) {
                                            navigateToHomeToRefresh = false;
                                            sendRefreshPageAction();
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

                                            if (TextUtils.isEmpty(getUserDownloadQualityPref(currentActivity))) {
                                                setUserDownloadQualityPref(currentActivity,
                                                        currentActivity.getString(R.string.app_cms_default_download_quality));
                                            }

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
                                    });
                                } else {
                                    sendCloseOthersAction(null, true);
                                    cancelInternalEvents();
                                    restartInternalEvents();

                                    if (TextUtils.isEmpty(getUserDownloadQualityPref(currentActivity))) {
                                        setUserDownloadQualityPref(currentActivity,
                                                currentActivity.getString(R.string.app_cms_default_download_quality));
                                    }

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
                    } catch (Exception e) {
                        Log.e(TAG, "Error retrieving sign in response: " + e.getMessage());
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
                        try {
                            if (refreshIdentityResponse != null) {
                                setLoggedInUser(currentActivity, refreshIdentityResponse.getId());
                                setRefreshToken(currentActivity, refreshIdentityResponse.getRefreshToken());
                                setAuthToken(currentActivity, refreshIdentityResponse.getAuthorizationToken());
                                onReadyAction.call();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error retrieving refresh identity response: " + e.getMessage());
                        }
                    }).execute(params);
        }
    }

    public void askForPermissionToDownloadToExternalStorage(boolean checkToShowPermissionRationale,
                                                            final ContentDatum contentDatum,
                                                            final Action1<UserVideoDownloadStatus> resultAction1) {
        downloadContentDatumAfterPermissionGranted = contentDatum;
        downloadResultActionAfterPermissionGranted = resultAction1;
        if (currentActivity != null && !hasWriteExternalStoragePermission()) {
            if (checkToShowPermissionRationale &&
                    ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showDialog(DialogType.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_FOR_DOWNLOAD,
                        currentActivity.getString(R.string.app_cms_download_write_external_storage_permission_rationale_message),
                        true,
                        () -> {
                            try {
                                askForPermissionToDownloadToExternalStorage(false,
                                        downloadContentDatumAfterPermissionGranted,
                                        downloadResultActionAfterPermissionGranted);
                            } catch (Exception e) {
                                Log.e(TAG, "Error handling request permissions result: " + e.getMessage());
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE_FOR_DOWNLOADS);
            }
        }
    }

    public boolean hasWriteExternalStoragePermission() {
        if (currentActivity != null) {
            return (ContextCompat.checkSelfPermission(currentActivity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }

    public void resumeDownloadAfterPermissionGranted() {
        if (requestDownloadQualityScreen) {
            showDownloadQualityScreen(downloadContentDatumAfterPermissionGranted,
                    downloadResultActionAfterPermissionGranted);
        } else {
            editDownload(downloadContentDatumAfterPermissionGranted,
                    downloadResultActionAfterPermissionGranted,
                    true);
        }
    }

    public boolean isAppSVOD() {
        return jsonValueKeyMap.get(appCMSMain.getServiceType()) == AppCMSUIKeyType.MAIN_SVOD_SERVICE_TYPE;
    }

    public void setNavItemToCurrentAction(Activity activity) {
        if (activity != null && currentActions != null && !currentActions.isEmpty()) {
            Intent setNavigationItemIntent = new Intent(PRESENTER_RESET_NAVIGATION_ITEM_ACTION);
            setNavigationItemIntent.putExtra(activity.getString(R.string.navigation_item_key),
                    currentActions.peek());
            activity.sendBroadcast(setNavigationItemIntent);
        }
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        this.currentActivity = activity;
        this.downloadManager = (DownloadManager) currentActivity.getSystemService(Context.DOWNLOAD_SERVICE);
        this.realmController = RealmController.with(currentActivity);
        this.downloadQueueThread = new DownloadQueueThread(this);
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
        if (activity != null) {
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
                    extraScreenType,
                    appCMSSearchCall);
            args.putBinder(activity.getString(R.string.app_cms_binder_key), appCMSBinder);
            return args;
        }
        return null;
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

    public void searchRetryDialog(String searchTerm) {
        RetryCallBinder retryCallBinder = getRetryCallBinder(null, null,
                searchTerm, null,
                null, false,
                null, SEARCH_RETRY_ACTION
        );
        Bundle bundle = new Bundle();
        bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
        Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
        args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
        currentActivity.sendBroadcast(args);
    }

    private RetryCallBinder getRetryCallBinder(String pagePath,
                                               String action,
                                               String filmTitle,
                                               String[] extraData,
                                               ContentDatum contentDatum,
                                               boolean closeLauncher,
                                               String filmId,
                                               RETRY_TYPE retry_type) {
        RetryCallBinder retryCallBinder = new RetryCallBinder();
        retryCallBinder.setPagePath(pagePath);
        retryCallBinder.setAction(action);
        retryCallBinder.setFilmTitle(filmTitle);
        retryCallBinder.setExtraData(extraData);
        retryCallBinder.setContentDatum(contentDatum);
        retryCallBinder.setCloselauncher(closeLauncher);
        retryCallBinder.setRetry_type(retry_type);
        retryCallBinder.setFilmId(filmId);
        return retryCallBinder;
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
                                         ExtraScreenType extraScreenType,
                                         AppCMSSearchCall appCMSSearchCall) {
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
                searchQuery,
                appCMSSearchCall);
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

    private void getAppCMSSite(final PlatformType platformType) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_site_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSMain.getDomainName());
            new GetAppCMSSiteAsyncTask(appCMSSiteCall,
                    appCMSSite -> {
                        try {
                            if (appCMSSite != null) {
                                this.appCMSSite = appCMSSite;
                                apikey = currentActivity.getString(R.string.x_api_key);
                                AppCMSAPIComponent appCMSAPIComponent = DaggerAppCMSAPIComponent.builder()
                                        .appCMSAPIModule(new AppCMSAPIModule(currentActivity,
                                                appCMSMain.getApiBaseUrl(),
                                                apikey))
                                        .build();
                                appCMSSearchUrlComponent = DaggerAppCMSSearchUrlComponent.builder()
                                        .appCMSSearchUrlModule(new AppCMSSearchUrlModule(appCMSMain.getApiBaseUrl(),
                                                appCMSSite.getGist().getSiteInternalName(),
                                                appCMSSearchCall))
                                        .build();
                                appCMSPageAPICall = appCMSAPIComponent.appCMSPageAPICall();
                                appCMSStreamingInfoCall = appCMSAPIComponent.appCMSStreamingInfoCall();
                                appCMSVideoDetailCall = appCMSAPIComponent.appCMSVideoDetailCall();
                                clearMaps();
                                switch (platformType) {
                                    case ANDROID:
                                        getAppCMSAndroid(0);
                                        break;
                                    case TV:
                                        getAppCMSTV(0);
                                        break;
                                    default:
                                }
                            } else {
                                launchErrorActivity(platformType);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error retrieving AppCMS Site Info: " + e.getMessage());
                        }
                    }).execute(url);
        }
    }

    private void getAppCMSAndroid(int tryCount) {
        if (!isUserLoggedIn(currentActivity) && tryCount == 0) {
            signinAnonymousUser(tryCount, null, PlatformType.ANDROID);
        } else if (isUserLoggedIn(currentActivity) && shouldRefreshAuthToken() && tryCount == 0) {
            refreshIdentity(getRefreshToken(currentActivity),
                    () -> {
                        try {
                            getAppCMSAndroid(tryCount + 1);
                        } catch (Exception e) {
                            Log.e(TAG, "Error refreshing identity while attempting to retrieving AppCMS Android data: ");
                        }
                    });
        } else {
            GetAppCMSAndroidUIAsyncTask.Params params =
                    new GetAppCMSAndroidUIAsyncTask.Params.Builder()
                            .url(currentActivity.getString(R.string.app_cms_url_with_appended_timestamp,
                                    appCMSMain.getAndroid(),
                                    appCMSMain.getTimestamp()))
                            .loadFromFile(loadFromFile)
                            .build();
            Log.d(TAG, "Params: " + appCMSMain.getAndroid() + " " + loadFromFile);
            new GetAppCMSAndroidUIAsyncTask(appCMSAndroidUICall, appCMSAndroidUI -> {
                try {
                    if (appCMSAndroidUI == null ||
                            appCMSAndroidUI.getMetaPages() == null ||
                            appCMSAndroidUI.getMetaPages().isEmpty()) {
                        Log.e(TAG, "AppCMS keys for pages for appCMSAndroidUI not found");
                        launchErrorActivity(platformType);
                    } else {
                        initializeGA(appCMSAndroidUI.getAnalytics().getGoogleAnalyticsId());
                        navigation = appCMSAndroidUI.getNavigation();
                        queueMetaPages(appCMSAndroidUI.getMetaPages());
                        Log.d(TAG, "Processing meta pages queue");
                        processMetaPagesQueue(loadFromFile,
                                () -> {
                                    if (appCMSMain.getServiceType()
                                            .equals(currentActivity.getString(R.string.app_cms_main_svod_service_type_key))) {
                                        refreshSubscriptionData(() -> {
                                            if (appCMSMain.isForceLogin()) {
                                                boolean launchSuccess = navigateToPage(loginPage.getPageId(),
                                                        loginPage.getPageName(),
                                                        loginPage.getPageUI(),
                                                        true,
                                                        false,
                                                        false,
                                                        false,
                                                        false,
                                                        deeplinkSearchQuery);
                                                if (!launchSuccess) {
                                                    Log.e(TAG, "Failed to launch page: "
                                                            + loginPage.getPageName());
                                                    launchErrorActivity(platformType);
                                                }
                                            } else {
                                                boolean launchSuccess = navigateToPage(homePage.getPageId(),
                                                        homePage.getPageName(),
                                                        homePage.getPageUI(),
                                                        true,
                                                        true,
                                                        false,
                                                        true,
                                                        false,
                                                        deeplinkSearchQuery);
                                                if (!launchSuccess) {
                                                    Log.e(TAG, "Failed to launch page: "
                                                            + loginPage.getPageName());
                                                    launchErrorActivity(platformType);
                                                }
                                            }
                                        });
                                    } else {
                                        if (appCMSMain.isForceLogin()) {
                                            boolean launchSuccess = navigateToPage(loginPage.getPageId(),
                                                    loginPage.getPageName(),
                                                    loginPage.getPageUI(),
                                                    true,
                                                    true,
                                                    false,
                                                    false,
                                                    false,
                                                    deeplinkSearchQuery);
                                            if (!launchSuccess) {
                                                Log.e(TAG, "Failed to launch page: "
                                                        + loginPage.getPageName());
                                                launchErrorActivity(platformType);
                                            }
                                        } else {
                                            boolean launchSuccess = navigateToPage(homePage.getPageId(),
                                                    homePage.getPageName(),
                                                    homePage.getPageUI(),
                                                    true,
                                                    true,
                                                    false,
                                                    true,
                                                    false,
                                                    deeplinkSearchQuery);
                                            if (!launchSuccess) {
                                                Log.e(TAG, "Failed to launch page: "
                                                        + loginPage.getPageName());
                                                launchErrorActivity(platformType);
                                            }
                                        }
                                    }
                                });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing meta pages queue: " + e.getMessage());
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
        if (!metaPageList.isEmpty()) {
            int loginPageIndex = getSigninPage(metaPageList);
            if (loginPageIndex >= 0) {
                loginPage = metaPageList.get(loginPageIndex);
            }
            int downloadQualitysIndex = getdownloadQualityPage(metaPageList);
            if (downloadQualitysIndex >= 0) {
                downloadQualityPage = metaPageList.get(downloadQualitysIndex);
            }
            int downloadPageIndex = getDownloadPage(metaPageList);
            if (downloadPageIndex >= 0) {
                downloadPage = metaPageList.get(downloadPageIndex);
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

    private void processMetaPagesQueue(final boolean loadFromFile,
                                       final Action0 onPagesFinishedAction) {
        final MetaPage metaPage = pagesToProcess.remove();

        Log.d(TAG, "Processing meta page " +
                metaPage.getPageName() + ": " +
                metaPage.getPageId() + " " +
                metaPage.getPageUI() + " " +
                metaPage.getPageAPI());
        pageIdToPageAPIUrlMap.put(metaPage.getPageId(), metaPage.getPageAPI());
        pageIdToPageNameMap.put(metaPage.getPageId(), metaPage.getPageName());

        getAppCMSPage(currentActivity.getString(R.string.app_cms_url_with_appended_timestamp,
                metaPage.getPageUI(),
                appCMSMain.getTimestamp()),
                appCMSPageUI -> {
                    try {
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
                        if (!pagesToProcess.isEmpty()) {

                            processMetaPagesQueue(loadFromFile,
                                    onPagesFinishedAction);
                        } else {
                            onPagesFinishedAction.call();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error retrieving AppCMS Page UI: " + e.getMessage());
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
            Log.e(TAG, "Error getting data from file: " + e.getMessage());
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

    private int getDownloadPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName())
                    == AppCMSUIKeyType.ANDROID_DOWNLOAD_KEY) {
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
        for (Map.Entry<String, AppCMSPageUI> entry : navigationPages.entrySet()) {
            if (entry.getValue() == appCMSPageUI) {
                return entry.getKey();
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

    private void getAppCMSTV(int tryCount) {
        if (!isUserLoggedIn(currentActivity) && tryCount == 0) {
            signinAnonymousUser(tryCount, null, PlatformType.TV);
        } else if (isUserLoggedIn(currentActivity) && shouldRefreshAuthToken() && tryCount == 0) {
            refreshIdentity(getRefreshToken(currentActivity),
                    () -> getAppCMSTV(tryCount + 1));
        } else {
            GetAppCMSAndroidUIAsyncTask.Params params =
                    new GetAppCMSAndroidUIAsyncTask.Params.Builder()
                            .url(currentActivity.getString(R.string.app_cms_url_with_appended_timestamp,
                                    appCMSMain.getFireTv(),
                                    appCMSMain.getTimestamp()))
                            .loadFromFile(loadFromFile)
                            .build();
            Log.d(TAG, "Params: " + appCMSMain.getAndroid() + " " + loadFromFile);
            new GetAppCMSAndroidUIAsyncTask(appCMSAndroidUICall, appCMSAndroidUI -> {
                if (appCMSAndroidUI == null ||
                        appCMSAndroidUI.getMetaPages() == null ||
                        appCMSAndroidUI.getMetaPages().isEmpty()) {
                    Log.e(TAG, "AppCMS keys for pages for appCMSAndroidUI not found");
                    launchErrorActivity(PlatformType.TV);
                } else {
                    if (appCMSAndroidUI.getAnalytics() != null) {
                        initializeGA(appCMSAndroidUI.getAnalytics().getGoogleAnalyticsId());
                    }
                    navigation = appCMSAndroidUI.getNavigation();
                    //add search in navigation item.
                    NavigationPrimary searchNav = new NavigationPrimary();
                    searchNav.setPageId(currentActivity.getString(R.string.app_cms_search_label));
                    searchNav.setTitle(currentActivity.getString(R.string.app_cms_search_label));
                    navigation.getNavigationPrimary().add(searchNav);


                    queueMetaPages(appCMSAndroidUI.getMetaPages());
                    final MetaPage firstPage = pagesToProcess.peek();
                    Log.d(TAG, "Processing meta pages queue");
                    processMetaPagesQueue(loadFromFile,
                            () -> {
                                Log.d(TAG, "Launching first page: " + firstPage.getPageName());
                                cancelInternalEvents();

                                Intent logoAnimIntent = new Intent(AppCMSPresenter.ACTION_LOGO_ANIMATION);
                                currentActivity.sendBroadcast(logoAnimIntent);

                                NavigationPrimary homePageNav = findHomePageNavItem();
                                boolean launchSuccess = navigateToTVPage(homePageNav.getPageId(),
                                        homePageNav.getTitle(),
                                        homePageNav.getUrl(),
                                        true,
                                        null);
                                if (!launchSuccess) {
                                    Log.e(TAG, "Failed to launch page: "
                                            + firstPage.getPageName());
                                    launchErrorActivity(PlatformType.TV);
                                }
                            });
                }
            }).execute(params);
        }
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
                //check internet connection here.
                if (!isNetworkConnected()) {
                    RetryCallBinder retryCallBinder = getRetryCallBinder(url, null,
                            pageTitle, null,
                            null, launchActivity, pageId, PAGE_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
                    Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
                    args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
                    currentActivity.sendBroadcast(args);
                    return false;
                }

                getPageIdContent(appCMSMain.getApiBaseUrl(),
                        pageIdToPageAPIUrlMap.get(pageId),
                        appCMSSite.getGist().getSiteInternalName(),
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
                                        if (args != null) {
                                            Intent updatePageIntent =
                                                    new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                            updatePageIntent.putExtra(
                                                    currentActivity.getString(R.string.app_cms_bundle_key),
                                                    args);
                                            currentActivity.sendBroadcast(updatePageIntent);

                                            setNavItemToCurrentAction(currentActivity);
                                        }
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
                    if (args != null) {
                        Intent updatePageIntent =
                                new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                        updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                args);
                        currentActivity.sendBroadcast(updatePageIntent);
                        setNavItemToCurrentAction(currentActivity);
                    }
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
            if (args != null) {
                Intent appCMSIntent = new Intent(activity, Class.forName(tvHomeScreenPackage));
                appCMSIntent.putExtra(activity.getString(R.string.app_cms_bundle_key), args);
                appCMSIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(appCMSIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error launching TV activity: " + e.getMessage());
        } finally {
            sendStopLoadingPageAction();
        }
    }

    public void playNextVideo(AppCMSVideoPageBinder binder,
                              int currentlyPlayingIndex,
                              long watchedTime) {
        sendCloseOthersAction(null, true);
        isVideoPlayerStarted = false;
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
            RetryCallBinder retryCallBinder = getRetryCallBinder(pagePath, action,
                    filmTitle, extraData,
                    null, closeLauncher, null, BUTTON_ACTION);
            Bundle bundle = new Bundle();
            bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
            Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
            args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
            currentActivity.sendBroadcast(args);
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
                    Log.e(TAG, "Error launching TV Button Selected Action: " + e.getMessage());
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
                sendSignInEmailFirebase();
            } else if (actionType == AppCMSActionType.FORGOT_PASSWORD) {
                Log.d(TAG, "Forgot password selected: " + extraData[0]);
                closeSoftKeyboard();
                launchResetPasswordPage(extraData[0]);
            } else if (actionType == AppCMSActionType.LOGIN_FACEBOOK) {
                Log.d(TAG, "Login Facebook selected");
                loginFacebook();
                sendSignInFacebookFirebase();
            } else if (actionType == AppCMSActionType.SIGNUP) {
                Log.d(TAG, "Sign-Up selected: " + extraData[0]);
                closeSoftKeyboard();
                signup(extraData[0], extraData[1]);
                sendSignUpEmailFirebase();
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
                        appCMSSite.getGist().getSiteInternalName(),
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
                                    if (args != null) {
                                        Intent updatePageIntent =
                                                new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                        updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                                args);
                                        currentActivity.sendBroadcast(updatePageIntent);
                                    }
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

    public PlatformType getPlatformType() {
        return platformType;
    }

    public boolean isRemoveableSDCardAvailable() {
        if (currentActivity != null) {
            if (getStorageDirectories(currentActivity).length >= 1) {
                return true;
            }

        }
        return false;
    }

    public String getSDCardPath(Context context, String dirName) {
        String dirPath = getSDCardPath(context) + File.separator + dirName;
        File dir = new File(dirPath);
        if (!dir.isDirectory())
            dir.mkdirs();

        return dir.getAbsolutePath();

    }

    public String getSDCardPath(Context context) {
        File baseSDCardDir = null;
        String[] dirs = getStorageDirectories(context);


        baseSDCardDir = new File(dirs[0] + File.separator + appCMSMain.getDomainName());


        return baseSDCardDir.getAbsolutePath();
    }

    public String[] getStorageDirectories(Context context) {
        HashSet<String> paths = new HashSet<String>();
        String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                List<String> results = new ArrayList<String>();
                File[] externalDirs = context.getExternalFilesDirs(null);
                for (File file : externalDirs) {
                    String path = null;
                    try {
                        path = file.getPath().split("/Android")[0];
                    } catch (Exception e) {
                        Log.e(TAG, "Error getting storage directories for downloads: " + e.getMessage());
                        path = null;
                    }
                    if (path != null) {
                        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Environment.isExternalStorageRemovable(file))
                                || rawSecondaryStoragesStr != null && rawSecondaryStoragesStr.contains(path)) {
                            results.add(path);
                        }
                    }
                }

                paths.addAll(results);

            } else {
                if (TextUtils.isEmpty(rawExternalStorage)) {
                    boolean b = paths.addAll(Arrays.asList(physicalPaths));
                } else {
                    paths.add(rawExternalStorage);
                }
            }
        } else {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            String[] folders = Pattern.compile("/").split(path);
            String lastFolder = folders[folders.length - 1];
            boolean isDigit = false;
            try {
                Integer.valueOf(lastFolder);
                isDigit = true;
            } catch (NumberFormatException ignored) {
            }

            String rawUserId = isDigit ? lastFolder : "";
            if (TextUtils.isEmpty(rawUserId)) {
                paths.add(rawEmulatedStorageTarget);
            } else {
                paths.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }

        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(paths, rawSecondaryStorages);
        }
        return paths.toArray(new String[paths.size()]);
    }

    public void setSearchResultsOnSharePreference(List<String> searchValues) {
        if (currentActivity == null)
            return;
        SharedPreferences sharePref = currentActivity.getSharedPreferences(
                currentActivity.getString(R.string.app_cms_search_sharepref_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putInt(currentActivity.getString(R.string.app_cms_search_value_size_key), searchValues.size());
        for (int i = 0; i < searchValues.size(); i++) {
            editor.remove(currentActivity.getString(R.string.app_cms_search_value_key) + i);
            editor.putString(currentActivity.getString(R.string.app_cms_search_value_key) + i, searchValues.get(i));
        }
        editor.commit();
    }

    public List<String> getSearchResultsFromSharePreference() {
        if (currentActivity == null)
            return null;
        List<String> searchValues = new ArrayList<String>();
        SharedPreferences sharePref = currentActivity.getSharedPreferences(
                currentActivity.getString(R.string.app_cms_search_sharepref_key), Context.MODE_PRIVATE);
        int size = sharePref.getInt(currentActivity.getString(R.string.app_cms_search_value_size_key), 0);
        for (int i = 0; i < size; i++) {
            searchValues.add(sharePref.getString(currentActivity.getString(R.string.app_cms_search_value_key) + i, null));
        }
        return searchValues;
    }

    public void clearSearchResultsSharePreference() {
        if (currentActivity == null)
            return;
        SharedPreferences sharePref = currentActivity.getSharedPreferences(
                currentActivity.getString(R.string.app_cms_search_sharepref_key), Context.MODE_PRIVATE);
        sharePref.edit().clear().commit();
    }

    public void openSearch() {
        Intent updateHistoryIntent = new Intent(SEARCH_ACTION);
        currentActivity.sendBroadcast(updateHistoryIntent);
    }

    public boolean launchTVVideoPlayer(final String filmId,
                                       final String pagePath,
                                       final String filmTitle,
                                       final ContentDatum contentDatum) {
        boolean result = false;


        if (!isNetworkConnected() && platformType == PlatformType.TV) {
            RetryCallBinder retryCallBinder = getRetryCallBinder(pagePath, null,
                    filmTitle, null,
                    contentDatum, false,
                    filmId, VIDEO_ACTION
            );

            Bundle bundle = new Bundle();
            bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
            Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
            args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
            currentActivity.sendBroadcast(args);
        } else if (currentActivity != null &&
                !loadingPage && appCMSMain != null &&
                !TextUtils.isEmpty(appCMSMain.getApiBaseUrl()) &&
                !TextUtils.isEmpty(appCMSSite.getGist().getSiteInternalName())) {
            result = true;
            final String action = currentActivity.getString(R.string.app_cms_action_watchvideo_key);
            String url = currentActivity.getString(R.string.app_cms_streaminginfo_api_url,
                    appCMSMain.getApiBaseUrl(),
                    filmId,
                    appCMSSite.getGist().getSiteInternalName());
            GetAppCMSStreamingInfoAsyncTask.Params params =
                    new GetAppCMSStreamingInfoAsyncTask.Params.Builder().url(url).build();
            new GetAppCMSStreamingInfoAsyncTask(appCMSStreamingInfoCall,
                    appCMSStreamingInfo -> {
                        String[] extraData = new String[3];
                        if (appCMSStreamingInfo != null &&
                                appCMSStreamingInfo.getStreamingInfo() != null) {
                            StreamingInfo streamingInfo = appCMSStreamingInfo.getStreamingInfo();
                            extraData[0] = pagePath;
                            if (streamingInfo.getVideoAssets() != null &&
                                    !TextUtils.isEmpty(streamingInfo.getVideoAssets().getHls())) {
                                extraData[1] = streamingInfo.getVideoAssets().getHls();
                            } else if (streamingInfo.getVideoAssets() != null &&
                                    streamingInfo.getVideoAssets().getMpeg() != null &&
                                    !streamingInfo.getVideoAssets().getMpeg().isEmpty() &&
                                    streamingInfo.getVideoAssets().getMpeg().get(0) != null &&
                                    !TextUtils.isEmpty(streamingInfo.getVideoAssets().getMpeg().get(0).getUrl())) {
                                extraData[1] = streamingInfo.getVideoAssets().getMpeg().get(0).getUrl();
                            }
                            extraData[2] = filmId;
                            if (!TextUtils.isEmpty(extraData[1])) {

                                if (platformType == PlatformType.TV) {
                                    launchTVButtonSelectedAction(pagePath,
                                            action,
                                            filmTitle,
                                            extraData,
                                            false);
                                }

                            }
                        }
                    }).execute(params);
        }
        return result;
    }

    public void sendSignUpFacebookFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_UP_METHOD, FIREBASE_FACEBOOK_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_UP_EVENT, bundle);
    }

    public void sendSignUpGoogleFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_UP_METHOD, FIREBASE_GOOGLE_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_UP_EVENT, bundle);
    }

    public void sendSignUpEmailFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_UP_METHOD, FIREBASE_EMAIL_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_UP_EVENT, bundle);
    }

    public void sendSignInFacebookFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_IN_METHOD, FIREBASE_FACEBOOK_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_In_EVENT, bundle);
    }

    public void sendSignInGoogleFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_IN_METHOD, FIREBASE_GOOGLE_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_In_EVENT, bundle);
    }

    public void sendSignInEmailFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_IN_METHOD, FIREBASE_EMAIL_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_In_EVENT, bundle);
    }

    public void sendFirebaseLoginSubscribeSuccess() {
        //Send Firebase Analytics when user is subscribed and user is Logged In
        mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_SUBSCRIBED);
        mFireBaseAnalytics.setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_IN);
        mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_PLAN_ID, getActiveSubscriptionId(currentActivity));
        mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_PLAN_NAME, getActiveSubscriptionPlanName(currentActivity));
    }

    public enum LaunchType {
        SUBSCRIBE, LOGIN_AND_SIGNUP
    }

    public enum PlatformType {
        ANDROID, TV
    }

    public enum BeaconEvent {
        PLAY, RESUME, PING, AD_REQUEST, AD_IMPRESSION, FIRST_FRAME, BUFFERING, FAILED_TO_START, DROPPED_STREAM
    }

    public enum DialogType {
        NETWORK,
        SIGNIN,
        SIGNUP_BLANK_EMAIL_PASSWORD,
        SIGNUP_BLANK_EMAIL,
        SIGNUP_BLANK_PASSWORD,
        SIGNUP_EMAIL_MATCHES_PASSWORD,
        SIGNUP_PASSWORD_INVALID,
        SIGNUP_NAME_MATCHES_PASSWORD,
        RESET_PASSWORD,
        CANCEL_SUBSCRIPTION,
        SUBSCRIBE,
        DELETE_ONE_HISTORY_ITEM,
        DELETE_ALL_HISTORY_ITEMS,
        LOGIN_REQUIRED,
        SUBSCRIPTION_REQUIRED,
        LOGIN_AND_SUBSCRIPTION_REQUIRED,
        LOGOUT_WITH_RUNNING_DOWNLOAD,
        EXISTING_SUBSCRIPTION,
        DOWNLOAD_INCOMPLETE,
        CANNOT_UPGRADE_SUBSCRIPTION,
        UPGRADE_UNAVAILABLE,
        CANNOT_CANCEL_SUBSCRIPTION,
        STREAMING_INFO_MISSING,
        REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_FOR_DOWNLOAD,
        DOWNLOAD_NOT_AVAILABLE,
        DOWNLOAD_FAILED,
        SD_CARD_NOT_AVAILABLE,
        UNKNOWN_SUBSCRIPTION_FOR_UPGRADE,
        UNKNOWN_SUBSCRIPTION_FOR_CANCEL
    }

    public enum RETRY_TYPE {
        VIDEO_ACTION, BUTTON_ACTION, PAGE_ACTION, SEARCH_RETRY_ACTION
    }

    public enum ExtraScreenType {
        NAVIGATION,
        SEARCH,
        RESET_PASSWORD,
        CHANGE_PASSWORD,
        EDIT_PROFILE,
        NONE
    }

    private static class DownloadQueueItem {
        ContentDatum contentDatum;
        Action1<UserVideoDownloadStatus> resultAction1;
    }

    private static class DownloadQueueThread extends Thread {
        private final AppCMSPresenter appCMSPresenter;
        private Queue<DownloadQueueItem> filmDownloadQueue;
        private List<String> filmsInQueue;

        private boolean running;
        private boolean startNextDownload;

        public DownloadQueueThread(AppCMSPresenter appCMSPresenter) {
            this.appCMSPresenter = appCMSPresenter;
            this.filmDownloadQueue = new ConcurrentLinkedQueue<>();
            this.filmsInQueue = new ArrayList<>();
            this.running = false;
            this.startNextDownload = true;
        }

        public void addToQueue(DownloadQueueItem downloadQueueItem) {
            if (!filmsInQueue.contains(downloadQueueItem.contentDatum.getGist().getTitle())) {
                filmDownloadQueue.add(downloadQueueItem);
                filmsInQueue.add(downloadQueueItem.contentDatum.getGist().getTitle());

                if (!filmsInQueue.isEmpty()) {
                    downloadQueueItem.resultAction1.call(null);
                }
            }
        }

        @Override
        public void run() {
            running = true;
            while (running) {
                if (!filmDownloadQueue.isEmpty() && startNextDownload) {
                    DownloadQueueItem downloadQueueItem = filmDownloadQueue.remove();

                    if (filmsInQueue.contains(downloadQueueItem.contentDatum.getGist().getTitle())) {
                        filmsInQueue.remove(downloadQueueItem.contentDatum.getGist().getTitle());
                    }

                    appCMSPresenter.startDownload(downloadQueueItem.contentDatum,
                            downloadQueueItem.resultAction1);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error while running download queue: " + e.getMessage());
                }
            }
        }

        public boolean running() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public void setStartNextDownload() {
            this.startNextDownload = true;
        }
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

    private abstract static class AppCMSPageAPIAction implements Action1<AppCMSPageAPI> {
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

    private abstract static class AppCMSWatchlistAPIAction implements Action1<AppCMSWatchlistResult> {
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

    private abstract static class AppCMSHistoryAPIAction implements Action1<AppCMSHistoryResult> {
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

    private abstract static class AppCMSSubscriptionAPIAction
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
