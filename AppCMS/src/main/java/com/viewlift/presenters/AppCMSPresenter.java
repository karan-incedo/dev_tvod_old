package com.viewlift.presenters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.StaleDataException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.urbanairship.UAirship;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.analytics.AppsFlyerUtils;
import com.viewlift.casting.CastHelper;
import com.viewlift.ccavenue.screens.EnterMobileNumberActivity;
import com.viewlift.ccavenue.utility.AvenuesParams;
import com.viewlift.models.billing.appcms.authentication.GoogleRefreshTokenResponse;
import com.viewlift.models.billing.appcms.subscriptions.InAppPurchaseData;
import com.viewlift.models.billing.appcms.subscriptions.SkuDetails;
import com.viewlift.models.billing.utils.IabHelper;
import com.viewlift.models.data.appcms.api.AddToWatchlistRequest;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.AppCMSSignedURLResult;
import com.viewlift.models.data.appcms.api.AppCMSVideoDetail;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
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
import com.viewlift.models.data.appcms.history.Record;
import com.viewlift.models.data.appcms.history.UpdateHistoryRequest;
import com.viewlift.models.data.appcms.history.UserVideoStatusResponse;
import com.viewlift.models.data.appcms.sites.AppCMSSite;
import com.viewlift.models.data.appcms.subscriptions.AppCMSSubscriptionResult;
import com.viewlift.models.data.appcms.subscriptions.AppCMSUserSubscriptionPlanResult;
import com.viewlift.models.data.appcms.subscriptions.PlanDetail;
import com.viewlift.models.data.appcms.subscriptions.Receipt;
import com.viewlift.models.data.appcms.subscriptions.UserSubscriptionPlan;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidUI;
import com.viewlift.models.data.appcms.ui.android.MetaPage;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.NavigationFooter;
import com.viewlift.models.data.appcms.ui.android.NavigationPrimary;
import com.viewlift.models.data.appcms.ui.android.NavigationUser;
import com.viewlift.models.data.appcms.ui.authentication.UserIdentity;
import com.viewlift.models.data.appcms.ui.authentication.UserIdentityPassword;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.models.data.appcms.watchlist.AppCMSAddToWatchlistResult;
import com.viewlift.models.data.appcms.watchlist.AppCMSWatchlistResult;
import com.viewlift.models.data.urbanairship.UAAssociateNamedUserRequest;
import com.viewlift.models.data.urbanairship.UANamedUserRequest;
import com.viewlift.models.network.background.tasks.GetAppCMSAPIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSAndroidUIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSMainUIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSPageUIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSRefreshIdentityAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSSignedURLAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSSiteAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSStreamingInfoAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSVideoDetailAsyncTask;
import com.viewlift.models.network.background.tasks.PostAppCMSLoginRequestAsyncTask;
import com.viewlift.models.network.background.tasks.PostUANamedUserEventAsyncTask;
import com.viewlift.models.network.components.AppCMSAPIComponent;
import com.viewlift.models.network.components.AppCMSSearchUrlComponent;
import com.viewlift.models.network.components.DaggerAppCMSAPIComponent;
import com.viewlift.models.network.components.DaggerAppCMSSearchUrlComponent;
import com.viewlift.models.network.modules.AppCMSAPIModule;
import com.viewlift.models.network.modules.AppCMSSearchUrlModule;
import com.viewlift.models.network.rest.AppCMSAddToWatchlistCall;
import com.viewlift.models.network.rest.AppCMSAndroidModuleCall;
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
import com.viewlift.models.network.rest.AppCMSRestorePurchaseCall;
import com.viewlift.models.network.rest.AppCMSSearchCall;
import com.viewlift.models.network.rest.AppCMSSignInCall;
import com.viewlift.models.network.rest.AppCMSSignedURLCall;
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
import com.viewlift.models.network.rest.UANamedUserEventCall;
import com.viewlift.views.activity.AppCMSDownloadQualityActivity;
import com.viewlift.views.activity.AppCMSErrorActivity;
import com.viewlift.views.activity.AppCMSPageActivity;
import com.viewlift.views.activity.AppCMSPlayVideoActivity;
import com.viewlift.views.activity.AppCMSSearchActivity;
import com.viewlift.views.activity.AppCMSUpgradeActivity;
import com.viewlift.views.activity.AutoplayActivity;
import com.viewlift.views.adapters.AppCMSBaseAdapter;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.binders.AppCMSDownloadQualityBinder;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.binders.RetryCallBinder;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.customviews.PopupMenu;
import com.viewlift.views.customviews.VideoPlayerView;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.fragments.AppCMSMoreFragment;
import com.viewlift.views.fragments.AppCMSNavItemsFragment;
import com.viewlift.views.fragments.AppCMSTrayMenuDialogFragment;

import org.jsoup.Jsoup;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.BUTTON_ACTION;
import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.EDIT_WATCHLIST;
import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.HISTORY_RETRY_ACTION;
import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.LOGOUT_ACTION;
import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.PAGE_ACTION;
import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.RESET_PASSWORD_RETRY;
import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.SEARCH_RETRY_ACTION;
import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.VIDEO_ACTION;
import static com.viewlift.presenters.AppCMSPresenter.RETRY_TYPE.WATCHLIST_RETRY_ACTION;

/*
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
    public static final String PRESENTER_UPDATE_LISTS_ACTION = "appcms_presenter_update_lists_action";
    public static final String PRESENTER_REFRESH_PAGE_DATA_ACTION = "appcms_presenter_refresh_page_data_action";
    public static final String PRESENTER_ENTER_FULLSCREEN_ACTION = "apppresenter_enter_fullscreen_action";
    public static final String PRESENTER_EXIT_FULLSCREEN_ACTION = "appcms_presenter_exit_fullscreen_action";
    public static final String PRESENTER_KEEP_SCREEN_ON_ACTION = "appcms_presenter_keep_screen_on_action";
    public static final String PRESENTER_CLEAR_KEEP_SCREEN_ON_ACTION = "appcms_presenter_dont_keep_screen_on_action";
    public static final String PRESENTER_CHROMECAST_DISCONNECTED_ACTION = "appcms_presenter_chromecast_disconnected_action";

    public static final int RC_PURCHASE_PLAY_STORE_ITEM = 1002;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_FOR_DOWNLOADS = 2002;
    public static final String MY_PROFILE_ACTION = "MY_PROFILE_ACTION";
    public static final int RC_GOOGLE_SIGN_IN = 1001;
    public static final int ADD_GOOGLE_ACCOUNT_TO_DEVICE_REQUEST_CODE = 5555;
    public static final int CC_AVENUE_REQUEST_CODE = 1;
    public static final String PRESENTER_DIALOG_ACTION = "appcms_presenter_dialog_action";
    public static final String PRESENTER_CLEAR_DIALOG_ACTION = "appcms_presenter_clear_dialog_action";
    public static final String SEARCH_ACTION = "SEARCH_ACTION";
    public static final String CLOSE_DIALOG_ACTION = "CLOSE_DIALOG_ACTION";
    public static final String ERROR_DIALOG_ACTION = "appcms_error_dialog_action";
    public static final String ACTION_LOGO_ANIMATION = "appcms_logo_animation";
    public static final String ACTION_RESET_PASSWORD = "appcms_reset_password_action";
    public static final int PLAYER_REQUEST_CODE = 1111;
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
    private static final String ACTIVE_SUBSCRIPTION_STATUS = "active_subscription_status_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_PLATFORM = "active_subscription_platform_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_PRICE_NAME = "active_subscription_plan_price_pref_key";
    private static final String ACTIVE_SUBSCRIPTION_PROCESSOR_NAME = "active_subscription_payment_processor_key";
    private static final String RESTORE_SUBSCRIPTION_RECEIPT = "restore_subscription_payment_process_key";
    private static final String ACTIVE_SUBSCRIPTION_COUNTRY_CODE = "active_subscription_country_code_key";
    private static final String IS_USER_SUBSCRIBED = "is_user_subscribed_pref_key";

    private static final String AUTO_PLAY_ENABLED_PREF_NAME = "autoplay_enabled_pref_key";
    private static final String EXISTING_GOOGLE_PLAY_SUBSCRIPTION_DESCRIPTION = "existing_google_play_subscription_title_pref_key";
    private static final String EXISTING_GOOGLE_PLAY_SUBSCRIPTION_ID = "existing_google_play_subscription_id_key_pref_key";
    private static final String EXISTING_GOOGLE_PLAY_SUBSCRIPTION_SUSPENDED = "existing_google_play_subscription_suspended_pref_key";
    private static final String EXISTING_GOOGLE_PLAY_SUBSCRIPTION_PRICE = "existing_google_play_subscription_price_pref_key";
    private static final String USER_DOWNLOAD_QUALITY_SHARED_PREF_NAME = "user_download_quality_pref";
    private static final String USER_DOWNLOAD_QUALITY_SCREEN_SHARED_PREF_NAME = "user_download_quality_screen_pref";
    private static final String USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME = "user_download_sd_card_pref";
    private static final String USER_AUTH_PROVIDER_SHARED_PREF_NAME = "user_auth_provider_shared_pref_name";
    private static final String GOOGLE_PLAY_APP_STORE_VERSION_PREF_NAME = "google_play_app_store_version_pref_name";
    private static final String APPS_FLYER_KEY_PREF_NAME = "apps_flyer_pref_name_key";
    private static final String INSTANCE_ID_PREF_NAME = "instance_id_pref_name";
    private static final String SUBSCRIPTION_STATUS = "subscription_status_pref_name";
    private static final String DOWNLOAD_OVER_CELLULAR_ENABLED_PREF_NAME = "download_over_cellular_enabled_pref_name";
    private static final String ACTIVE_NETWORK_TYPE_PREF_NAME = "active_network_type_pref_name";

    private static final String PLAYING_VIDEO_PREF_NAME = "playing_offline_video_pref_name";

    private static final String AUTH_TOKEN_SHARED_PREF_NAME = "auth_token_pref";
    private static final String ANONYMOUS_AUTH_TOKEN_PREF_NAME = "anonymous_auth_token_pref_key";
    private static final long MILLISECONDS_PER_SECOND = 1000L;
    private static final long SECONDS_PER_MINUTE = 60L;
    private static final long MAX_SESSION_DURATION_IN_MINUTES = 15L;
    private static final long MAX_ANONYMOUS_SESSIONS_DURATION_IN_MINUTES = 30L;
    private static final String MEDIA_SURFIX_MP4 = ".mp4";
    private static final String MEDIA_SURFIX_PNG = ".png";
    private static final String MEDIA_SURFIX_JPG = ".jpg";
    private static final String MEDIA_SUFFIX_SRT = ".srt";

    private static final String SUBSCRIPTION_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC+00:00");

    private static int PAGE_LRU_CACHE_SIZE = 10;
    private static int PAGE_API_LRU_CACHE_SIZE = 10;
    private final String USER_ID_KEY = "user_id";
    private final String FIREBASE_SCREEN_VIEW_EVENT = "screen_view";

    private final String LOGIN_STATUS_KEY = "logged_in_status";
    private final String LOGIN_STATUS_LOGGED_IN = "logged_in";
    private final String LOGIN_STATUS_LOGGED_OUT = "not_logged_in";
    private final String SUBSCRIPTION_STATUS_KEY = "subscription_status";
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
    private final String DOWNLOAD_UI_ID = "download_page_id_pref";

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

    //private final GoogleCancelSubscriptionCall googleCancelSubscriptionCall;
    private final String FIREBASE_SCREEN_SIGN_OUT = "sign_out";
    private final String FIREBASE_SCREEN_LOG_OUT = "log_out";
    //private final AppCMSCCAvenueCall appCMSCCAvenueCall;

    private final AppCMSUpdateWatchHistoryCall appCMSUpdateWatchHistoryCall;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final Map<String, String> pageNameToActionMap;
    private final Map<String, AppCMSPageUI> actionToPageMap;

    private final Map<String, AppCMSActionType> actionToActionTypeMap;
    private final AppCMSWatchlistCall appCMSWatchlistCall;
    private final AppCMSHistoryCall appCMSHistoryCall;
    private final AppCMSUserDownloadVideoStatusCall appCMSUserDownloadVideoStatusCall;
    private final AppCMSBeaconCall appCMSBeaconCall;

    private final AppCMSRestorePurchaseCall appCMSRestorePurchaseCall;
    private final AppCMSAndroidModuleCall appCMSAndroidModuleCall;
    private final AppCMSSignedURLCall appCMSSignedURLCall;

    private final AppCMSUserVideoStatusCall appCMSUserVideoStatusCall;
    private final AppCMSAddToWatchlistCall appCMSAddToWatchlistCall;
    private final AppCMSDeleteHistoryCall appCMSDeleteHistoryCall;
    private final AppCMSSubscriptionPlanCall appCMSSubscriptionPlanCall;
    private final AppCMSAnonymousAuthTokenCall appCMSAnonymousAuthTokenCall;

    private final String[] physicalPaths = {
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

    private final Map<String, AppCMSPageUI> navigationPages;
    private final Map<String, AppCMSPageAPI> navigationPageData;
    private final Map<String, String> pageIdToPageAPIUrlMap;
    private final Map<String, String> actionToPageAPIUrlMap;
    private final Map<String, String> actionToPageNameMap;
    private final Map<String, String> pageIdToPageNameMap;
    private final Map<AppCMSActionType, MetaPage> actionTypeToMetaPageMap;
    private final List<Action1<Boolean>> onOrientationChangeHandlers;
    private final Map<String, List<OnInternalEvent>> onActionInternalEvents;
    private final Stack<String> currentActions;
    private final BeaconRunnable beaconMessageRunnable;
    private final Runnable beaconMessageThread;
    private final String tvVideoPlayerPackage = "com.viewlift.tv.views.activity.AppCMSTVPlayVideoActivity";
    private final List<DownloadTimerTask> downloadProgressTimerList = new ArrayList<>();
    private final ReferenceQueue<Object> referenceQueue;
    public boolean pipPlayerVisible = false;

    public PopupWindow pipDialog;
    VideoPlayerView videoPlayerViewPIP, videoPlayerViewPage;
    RelativeLayout relativeLayoutPIP;
    private boolean isRenewable;
    private String FIREBASE_EVENT_LOGIN_SCREEN = "Login Screen";
    private String serverClientId;
    private AppCMSPageAPICall appCMSPageAPICall;
    private AppCMSStreamingInfoCall appCMSStreamingInfoCall;
    private AppCMSVideoDetailCall appCMSVideoDetailCall;
    private AppCompatActivity currentActivity;
    private Context currentContext;
    private Navigation navigation;
    private boolean loadFromFile;
    private boolean loadingPage;
    private AppCMSMain appCMSMain;
    private AppCMSSite appCMSSite;
    private Queue<MetaPage> pagesToProcess;
    private AppCMSSearchUrlComponent appCMSSearchUrlComponent;
    private DownloadManager downloadManager;
    private RealmController realmController;
    private GoogleAnalytics googleAnalytics;
    private Tracker tracker;
    private ServiceConnection inAppBillingServiceConn;
    private String tvErrorScreenPackage = "com.viewlift.tv.views.activity.AppCmsTvErrorActivity";
    private Uri deeplinkSearchQuery;
    private boolean launched;
    private MetaPage splashPage;
    private MetaPage loginPage;
    private MetaPage downloadQualityPage;
    private MetaPage homePage;
    private MetaPage moviesPage;
    private MetaPage showsPage;
    private MetaPage downloadPage;
    private MetaPage subscriptionPage;
    private MetaPage historyPage;
    private MetaPage watchlistPage;
    private PlatformType platformType;
    private AppCMSNavItemsFragment appCMSNavItemsFragment;
    private LaunchType launchType;
    private IInAppBillingService inAppBillingService;
    private String subscriptionUserEmail;
    private String subscriptionUserPassword;
    private boolean isSignupFromFacebook;
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
    private boolean upgradesAvailable;
    private boolean checkUpgradeFlag;
    private String currencyOfPlanToPurchase;
    private String planToPurchaseName;
    private String apikey;
    private double planToPurchasePrice;
    private String renewableFrequency = "";
    private double planToPurchaseDiscountedPrice;
    private String planReceipt;
    private GoogleApiClient googleApiClient;
    private long downloaded = 0L;
    private LruCache<String, PageView> pageViewLruCache;
    private LruCache<String, AppCMSPageAPI> pageAPILruCache;
    private EntitlementPendingVideoData entitlementPendingVideoData;

    private List<SubscriptionPlan> subscriptionPlans;
    private boolean configurationChanged;
    private FirebaseAnalytics mFireBaseAnalytics;
    private boolean runUpdateDownloadIconTimer;
    private ContentDatum downloadContentDatumAfterPermissionGranted;
    private Action1<UserVideoDownloadStatus> downloadResultActionAfterPermissionGranted;
    private boolean requestDownloadQualityScreen;
    private DownloadQueueThread downloadQueueThread;
    private boolean isVideoPlayerStarted;
    private EntitlementCheckActive entitlementCheckActive;
    private AppCMSAndroidModules appCMSAndroidModules;
    private Toast customToast;
    private boolean pageLoading;
    private boolean cancelLoad;
    private boolean cancelAllLoads;
    private boolean downloadInProgress;
    private boolean loginFromNavPage;
    private Action0 afterLoginAction;
    private boolean shouldLaunchLoginAction;
    private Map<String, ContentDatum> userHistoryData;
    private AppCMSWatchlistResult filmsInUserWatchList;
    private List<String> temporaryWatchlist;
    private ImageButton currentMediaRouteButton;
    private ViewGroup currentMediaRouteButtonParent;

    private boolean loginDialogPopupOpen;

    private volatile boolean processedUIModules;
    private volatile boolean processedUIPages;

    public AppCMSTrayMenuDialogFragment.TrayMenuClickListener trayMenuClickListener =
            new AppCMSTrayMenuDialogFragment.TrayMenuClickListener() {
                @Override
                public void addToWatchListClick(boolean isAddedOrNot, ContentDatum contentDatum) {
                    // ADD WATCHLIST API CALLING
                    Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                    pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                    currentActivity.sendBroadcast(pageLoadingActionIntent);
                    if (isUserLoggedIn()) {
                        editWatchlist(contentDatum.getId(), appCMSAddToWatchlistResult -> {
                                    Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                                    stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                    currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                                    Toast.makeText(currentContext, "Updated Successfully :", Toast.LENGTH_LONG);
                                },
                                isAddedOrNot,
                                true);
                    } else {
                        if (isAppSVOD() && isUserLoggedIn()) {
                            showEntitlementDialog(AppCMSPresenter.DialogType.SUBSCRIPTION_REQUIRED, null);
                        } else {
                            showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_REQUIRED, null);
                        }
                        Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                        stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                    }
                }

                @Override
                public void downloadClick(ContentDatum contentDatum) {
                    // DOWNLOAD API CALLING
                }
            };

    private String cachedAPIUserToken;
    private Typeface regularFontFace;
    private Typeface boldTypeFace;
    private Typeface semiBoldTypeFace;
    private Typeface extraBoldTypeFace;
    private long mLastClickTime = 0;
    private boolean showNetworkConnectivity;
    private boolean waithingFor3rdPartyLogin;
    private AppCMSAndroidUI appCMSAndroid;
    private Map<String, MetaPage> pageIdToMetaPageMap;
    private boolean forceLoad;

    private Map<String, ViewCreator.UpdateDownloadImageIconAction> updateDownloadImageIconActionMap;

    private UrbanAirshipEventPresenter urbanAirshipEventPresenter;
    private UANamedUserEventCall uaNamedUserEventCall;

    private boolean purchaseFromRestore;

    private BitmapCachePresenter bitmapCachePresenter;

    private int numPagesProcessed;

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

                           AppCMSRestorePurchaseCall appCMSRestorePurchaseCall,

                           AppCMSAndroidModuleCall appCMSAndroidModuleCall,
                           AppCMSSignedURLCall appCMSSignedURLCall,

                           AppCMSAddToWatchlistCall appCMSAddToWatchlistCall,

                           AppCMSCCAvenueCall appCMSCCAvenueCall,

                           Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                           Map<String, String> pageNameToActionMap,
                           Map<String, AppCMSPageUI> actionToPageMap,
                           Map<String, AppCMSPageAPI> actionToPageAPIMap,
                           Map<String, AppCMSActionType> actionToActionTypeMap,

                           ReferenceQueue<Object> referenceQueue) {
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
        Map<String, AppCMSPageAPI> actionToPageAPIMap1 = actionToPageAPIMap;
        this.actionToActionTypeMap = actionToActionTypeMap;
        this.appCMSUserIdentityCall = appCMSUserIdentityCall;
        this.googleRefreshTokenCall = googleRefreshTokenCall;
        GoogleCancelSubscriptionCall googleCancelSubscriptionCall1 = googleCancelSubscriptionCall;

        this.appCMSUpdateWatchHistoryCall = appCMSUpdateWatchHistoryCall;
        this.appCMSUserVideoStatusCall = appCMSUserVideoStatusCall;
        this.appCMSUserDownloadVideoStatusCall = appCMSUserDownloadVideoStatusCall;
        this.appCMSBeaconCall = appCMSBeaconCall;

        this.appCMSRestorePurchaseCall = appCMSRestorePurchaseCall;

        this.appCMSAndroidModuleCall = appCMSAndroidModuleCall;
        this.appCMSSignedURLCall = appCMSSignedURLCall;

        this.appCMSAddToWatchlistCall = appCMSAddToWatchlistCall;

        AppCMSCCAvenueCall appCMSCCAvenueCall1 = appCMSCCAvenueCall;

        this.appCMSWatchlistCall = appCMSWatchlistCall;
        this.appCMSHistoryCall = appCMSHistoryCall;

        this.appCMSDeleteHistoryCall = appCMSDeleteHistoryCall;

        AppCMSSubscriptionCall appCMSSubscriptionCall1 = appCMSSubscriptionCall;
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

        this.referenceQueue = referenceQueue;

        this.entitlementCheckActive = new EntitlementCheckActive(() -> {
            sendCloseOthersAction(null, true, false);
            AppCMSActionPresenter actionPresenter = new AppCMSActionPresenter();
            actionPresenter.setAction(entitlementCheckActive.getAction());
            launchButtonSelectedAction(entitlementCheckActive.getPagePath(),
                    actionPresenter,
                    entitlementCheckActive.getFilmTitle(),
                    entitlementCheckActive.getExtraData(),
                    entitlementCheckActive.getContentDatum(),
                    entitlementCheckActive.isCloseLauncher(),
                    entitlementCheckActive.getCurrentlyPlayingIndex(),
                    entitlementCheckActive.getRelateVideoIds());
        }, () -> {
            if (isUserLoggedIn()) {
                showEntitlementDialog(DialogType.SUBSCRIPTION_REQUIRED, null);
            } else {
                showEntitlementDialog(DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED, null);
            }
        });

        this.checkUpgradeFlag = false;
        this.upgradesAvailable = false;
        this.cancelAllLoads = false;
        this.downloadInProgress = false;
        this.loginFromNavPage = true;

        this.showNetworkConnectivity = true;

        this.waithingFor3rdPartyLogin = false;

        this.userHistoryData = new HashMap<>();

        this.updateDownloadImageIconActionMap = new HashMap<>();

        this.temporaryWatchlist = new ArrayList<>();

        this.purchaseFromRestore = false;

        this.pageIdToMetaPageMap = new HashMap<>();

        clearMaps();
    }

    /*does not let user enter space in editText*/
    public static void noSpaceInEditTextFilter(EditText passwordEditText, Context con) {
        /* To restrict Space Bar in Keyboard */
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (Character.isWhitespace(source.charAt(i))) {
                    Toast.makeText(con, con.getResources().getString(R.string.password_space_error), Toast.LENGTH_SHORT).show();
                    return "";
                }
            }
            return null;
        };
        passwordEditText.setFilters(new InputFilter[]{filter});
    }

    public boolean shouldShowNetworkContectivity() {
        return showNetworkConnectivity;
    }

    public void setShowNetworkConnectivity(boolean showNetworkConnectivity) {
        this.showNetworkConnectivity = showNetworkConnectivity;
    }

    public void setCancelAllLoads(boolean cancelAllLoads) {
        this.cancelAllLoads = cancelAllLoads;
        if (cancelAllLoads) {
            showLoadingDialog(false);
        }
    }

    public Navigation getNavigation() {
        return navigation;
    }

    private LruCache<String, AppCMSPageAPI> getPageAPILruCache() {
        if (pageAPILruCache == null) {
            int PAGE_API_LRU_CACHE_SIZE = 10;
            pageAPILruCache = new LruCache<>(PAGE_API_LRU_CACHE_SIZE);
        }
        return pageAPILruCache;
    }

    public LruCache<String, PageView> getPageViewLruCache() {
        if (pageViewLruCache == null) {
            int PAGE_LRU_CACHE_SIZE = 10;
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
            if (this.realmController != null) {
                try {
                    this.realmController.closeRealm();
                } catch (Exception e) {

                }
            }
        }
    }

    private void initializeGA(String trackerId) {
        if (this.googleAnalytics == null && currentActivity != null) {
            this.googleAnalytics = GoogleAnalytics.getInstance(currentActivity);
            this.tracker = this.googleAnalytics.newTracker(trackerId);
        }
    }

    public void setIsLoading(boolean isLoading) {
        loadingPage = isLoading;
    }

    @SuppressWarnings("unused")
    public boolean isDownloadInProgress() {
        return downloadInProgress;
    }

    public void setDownloadInProgress(boolean downloadInProgress) {
        this.downloadInProgress = downloadInProgress;
    }

    public String getApiUrl(boolean usePageIdQueryParam,
                            boolean viewPlansPage,
                            boolean showPage,
                            String baseUrl,
                            String endpoint,
                            String siteId,
                            String pageId,
                            boolean usedCachedAPI) {
        if (currentContext != null && pageId != null) {
            String urlWithContent;
            if (usePageIdQueryParam) {
                if (viewPlansPage) {
                    urlWithContent =
                            currentContext.getString(R.string.app_cms_page_api_view_plans_url,
                                    baseUrl,
                                    endpoint,
                                    siteId,
                                    currentContext.getString(R.string.app_cms_subscription_platform_key));
                } else {
                    try {
                        if (usedCachedAPI) {
                            if (isUserLoggedIn() || (moviesPage != null && pageId != null &&
                                    pageId.equals(moviesPage.getPageId()))) {
                                urlWithContent = currentContext.getString(R.string.app_cms_cached_page_api_url_with_user_id,
                                        appCMSMain.getApiBaseUrlCached(),
                                        siteId,
                                        pageId,
                                        getLoggedInUser());
                            } else {
                                urlWithContent = currentContext.getString(R.string.app_cms_cached_page_api_url,
                                        appCMSMain.getApiBaseUrlCached(),
                                        siteId,
                                        pageId);
                            }
                        } else {
                            urlWithContent = currentContext.getString(R.string.app_cms_page_api_url,
                                    baseUrl,
                                    endpoint,
                                    siteId,
                                    currentContext.getString(R.string.app_cms_page_id_query_parameter),
                                    pageId,
                                    getLoggedInUser());
                        }
                    } catch (Exception e) {
                        urlWithContent = currentContext.getString(R.string.app_cms_page_api_url,
                                baseUrl,
                                endpoint,
                                siteId,
                                currentContext.getString(R.string.app_cms_page_id_query_parameter),
                                pageId,
                                getLoggedInUser());
                    }
                }
            } else {
                if (showPage) {
                    urlWithContent = currentContext.getString(R.string.app_cms_shows_status_api_url,
                            baseUrl,
                            endpoint,
                            pageId,
                            siteId,
                            isUserLoggedIn());
                } else {
                    urlWithContent =
                            currentContext.getString(R.string.app_cms_page_api_url,
                                    baseUrl,
                                    endpoint,
                                    siteId,
                                    currentContext.getString(R.string.app_cms_page_path_query_parameter),
                                    pageId,
                                    getLoggedInUser());
                }
            }
            return urlWithContent;
        }
        return null;
    }

    public boolean isPageLoading() {
        return pageLoading;
    }

    public void setPageLoading(boolean pageLoading) {
        this.pageLoading = pageLoading;
    }

    public AppCMSAndroidModules getAppCMSAndroidModules() {
        return appCMSAndroidModules;
    }

    public void refreshVideoData(final String id, Action1<ContentDatum> readyAction) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_video_detail_api_url,
                    appCMSMain.getApiBaseUrl(),
                    id,
                    appCMSSite.getGist().getSiteInternalName());
            GetAppCMSVideoDetailAsyncTask.Params params =
                    new GetAppCMSVideoDetailAsyncTask.Params.Builder().url(url)
                            .authToken(getAuthToken()).build();
            new GetAppCMSVideoDetailAsyncTask(appCMSVideoDetailCall,
                    appCMSVideoDetail -> {
                        if (appCMSVideoDetail != null &&
                                appCMSVideoDetail.getRecords() != null &&
                                appCMSVideoDetail.getRecords().get(0) != null) {
                            ContentDatum currentContentDatum = appCMSVideoDetail.getRecords().get(0);
                            ContentDatum userHistoryContentDatum = getUserHistoryContentDatum(currentContentDatum.getGist().getId());

                            if (userHistoryContentDatum != null) {
                                currentContentDatum.getGist().setWatchedTime(userHistoryContentDatum.getGist().getWatchedTime());
                            }

                            readyAction.call(currentContentDatum);
                        }
                    }).execute(params);
        }
    }

    public boolean launchVideoPlayer(final ContentDatum contentDatum,
                                     String filmId,
                                     final int currentlyPlayingIndex,
                                     List<String> relateVideoIds,
                                     long watchedTime,
                                     String expectedAction) {
        boolean result = false;
        if (currentActivity != null &&
                appCMSMain != null &&
                !TextUtils.isEmpty(appCMSMain.getApiBaseUrl()) &&
                !TextUtils.isEmpty(appCMSSite.getGist().getSiteInternalName())) {

            final String action = currentActivity.getString(R.string.app_cms_action_watchvideo_key);
            result = true;

            /*When content details are null it means video player is launched from somewhere
            * other than video detail fragment*/

            String url = currentActivity.getString(R.string.app_cms_video_detail_api_url,
                    appCMSMain.getApiBaseUrl(),
                    filmId,
                    appCMSSite.getGist().getSiteInternalName());
            GetAppCMSVideoDetailAsyncTask.Params params =
                    new GetAppCMSVideoDetailAsyncTask.Params.Builder().url(url)
                            .authToken(getAuthToken()).build();

            new GetAppCMSVideoDetailAsyncTask(appCMSVideoDetailCall,
                    appCMSVideoDetail -> {
                        try {
                            if (appCMSVideoDetail != null &&
                                    appCMSVideoDetail.getRecords() != null &&
                                    appCMSVideoDetail.getRecords().get(0) != null &&
                                    appCMSVideoDetail.getRecords().get(0).getContentDetails() != null) {
                                String updatedAction = expectedAction;

                                if (!TextUtils.isEmpty(expectedAction) &&
                                        !expectedAction.equals(currentContext.getString(R.string.app_cms_action_videopage_key)) &&
                                        !expectedAction.equals(currentContext.getString(R.string.app_cms_action_watchvideo_key))) {
                                    String contentType = "";

                                    if (appCMSVideoDetail.getRecords().get(0).getGist() != null &&
                                            appCMSVideoDetail.getRecords().get(0).getGist().getContentType() != null) {
                                        contentType = appCMSVideoDetail.getRecords().get(0).getGist().getContentType();
                                    }

                                    switch (contentType) {
                                        case "SHOW":
                                            updatedAction = currentContext.getString(R.string.app_cms_action_showvideopage_key);
                                            break;

                                        case "VIDEO":
                                            updatedAction = currentContext.getString(R.string.app_cms_action_detailvideopage_key);
                                            break;

                                        default:
                                            break;
                                    }
                                }

                                if (updatedAction == null) {
                                    updatedAction = currentContext.getString(R.string.app_cms_action_videopage_key);
                                }

                                Log.d(TAG, "Existing watched time: " + contentDatum.getGist().getWatchedTime());
                                Log.d(TAG, "Updated watched time: " + appCMSVideoDetail.getRecords().get(0).getGist().getWatchedTime());

                                appCMSVideoDetail.getRecords().get(0).getGist().setWatchedTime(contentDatum.getGist().getWatchedTime());
                                appCMSVideoDetail.getRecords().get(0).getGist().setWatchedPercentage(contentDatum.getGist().getWatchedPercentage());

                                AppCMSActionPresenter actionPresenter = new AppCMSActionPresenter();
                                actionPresenter.setAction(updatedAction);

                                launchButtonSelectedAction(appCMSVideoDetail.getRecords().get(0).getGist().getPermalink(),
                                        actionPresenter,
                                        appCMSVideoDetail.getRecords().get(0).getGist().getTitle(),
                                        null,
                                        appCMSVideoDetail.getRecords().get(0),
                                        false,
                                        currentlyPlayingIndex,
                                        relateVideoIds);
                            } else {
                                if (!isNetworkConnected()) {
                                    // Fix of SVFA-1435
                                    openDownloadScreenForNetworkError(false,
                                            () -> launchVideoPlayer(contentDatum,
                                                    contentDatum.getGist().getId(),
                                                    currentlyPlayingIndex,
                                                    relateVideoIds,
                                                    watchedTime,
                                                    expectedAction));
                                } else {
                                    if (watchedTime >= 0) {
                                        contentDatum.getGist().setWatchedTime(watchedTime);
                                    }

                                    AppCMSActionPresenter actionPresenter = new AppCMSActionPresenter();
                                    actionPresenter.setAction(action);

                                    launchButtonSelectedAction(
                                            contentDatum.getGist().getPermalink(),
                                            actionPresenter,
                                            contentDatum.getGist().getTitle(),
                                            null,
                                            contentDatum,
                                            false,
                                            currentlyPlayingIndex,
                                            relateVideoIds);
                                }
                            }

                        } catch (Exception e) {
                            //Log.e(TAG, "Error retrieving AppCMS Video Details: " + e.getMessage());
                        }
                    }).execute(params);
        }
        return result;
    }

    public Map<String, ViewCreator.UpdateDownloadImageIconAction> getUpdateDownloadImageIconActionMap() {
        return updateDownloadImageIconActionMap;
    }

    private void updateAllOfflineWatchTime() {
        if (getLoggedInUser() != null) {
            if (currentActivity != null) {
                currentActivity.runOnUiThread(() -> {
                    if (realmController != null) {
                        for (DownloadVideoRealm downloadVideoRealm : realmController.getAllUnSyncedWithServer(getLoggedInUser())) {
                            updateWatchedTime(downloadVideoRealm.getVideoId(), downloadVideoRealm.getWatchedTime());
                        }
                    }
                });
            }
        }
    }

    public boolean shouldDisplaySubscriptionCancelButton() {
        if (currentActivity != null) {
            return currentActivity.getResources().getBoolean(R.bool.display_cancel_subscription_button);
        }

        return true;
    }

    public void updateWatchedTime(String filmId, long watchedTime) {
        if (getLoggedInUser() != null && appCMSSite != null && appCMSMain != null) {
            UpdateHistoryRequest updateHistoryRequest = new UpdateHistoryRequest();
            updateHistoryRequest.setUserId(getLoggedInUser());
            updateHistoryRequest.setWatchedTime(watchedTime);
            updateHistoryRequest.setVideoId(filmId);
            updateHistoryRequest.setSiteOwner(appCMSSite.getGist().getSiteInternalName());

            String url = currentActivity.getString(R.string.app_cms_update_watch_history_api_url,
                    appCMSMain.getApiBaseUrl());

            appCMSUpdateWatchHistoryCall.call(url, getAuthToken(),
                    updateHistoryRequest, s -> {
                        try {
                            if (currentActivity != null) {
                                sendUpdateHistoryAction();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error updating watched time: " + e.getMessage());
                        }
                    });

            populateUserHistoryData();

            currentActivity.runOnUiThread(() -> {
                try {
                    // copyFromRealm is used to get an unmanaged in-memory copy of an already
                    // persisted RealmObject
                    DownloadVideoRealm downloadedVideo = realmController.getRealm()
                            .copyFromRealm(realmController.getDownloadById(filmId));
                    downloadedVideo.setWatchedTime(watchedTime);
                    downloadedVideo.setLastWatchDate(System.currentTimeMillis());
                    if (!isNetworkConnected()) {
                        downloadedVideo.setSyncedWithServer(false);
                    } else {
                        downloadedVideo.setSyncedWithServer(true);
                    }
                    realmController.updateDownload(downloadedVideo);
                } catch (Exception e) {
                    //Log.e(TAG, "Film " + filmId + " has not been downloaded");
                }
            });
        }
    }

    private void populateUserHistoryData() {
        getHistoryData(appCMSHistoryResult -> {
            try {
                int contentDatumLength = appCMSHistoryResult.getRecords().size();
                List<Record> historyRecords = appCMSHistoryResult.getRecords();
                for (int i = 0; i < contentDatumLength; i++) {
                    ContentDatum recordContentDatum = historyRecords.get(i).convertToContentDatum();
                    userHistoryData.put(recordContentDatum.getGist().getId(), recordContentDatum);
                }
            } catch (Exception e) {

            }
        });
    }

    private void populateFilmsInUserWatchlist() {
        AppCMSPageUI appCMSPageUI = navigationPages.get(watchlistPage.getPageId());

        if (appCMSPageUI == null) {
            getAppCMSPage(watchlistPage.getPageUI(),
                    appCMSPageUIResult -> {
                        if (appCMSPageUIResult != null) {
                            navigationPages.put(watchlistPage.getPageId(), appCMSPageUIResult);
                            String action = pageNameToActionMap.get(watchlistPage.getPageName());
                            if (action != null && actionToPageMap.containsKey(action)) {
                                actionToPageMap.put(action, appCMSPageUIResult);
                            }

                            populateFilmsInUserWatchlist();
                        }
                    },
                    loadFromFile,
                    false);
        }

        getWatchlistPageContent(appCMSMain.getApiBaseUrl(),
                watchlistPage.getPageAPI(),
                appCMSSite.getGist().getSiteInternalName(),
                true,
                getPageId(appCMSPageUI),
                new AppCMSWatchlistAPIAction(true,
                        false,
                        true,
                        appCMSPageUI,
                        watchlistPage.getPageId(),
                        watchlistPage.getPageId(),
                        watchlistPage.getPageName(),
                        watchlistPage.getPageId(),
                        false,
                        null) {
                    @Override
                    public void call(AppCMSWatchlistResult appCMSWatchlistResult) {
                        AppCMSPresenter.this.filmsInUserWatchList = appCMSWatchlistResult;
                    }
                });
    }

    private void sendUpdateHistoryAction() {
        Intent updateHistoryIntent = new Intent(PRESENTER_UPDATE_HISTORY_ACTION);
        updateHistoryIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
        currentActivity.sendBroadcast(updateHistoryIntent);
    }

    public void getUserVideoStatus(String filmId, Action1<UserVideoStatusResponse> responseAction) {
        if (currentActivity != null) {
            if (shouldRefreshAuthToken()) {
                refreshIdentity(getRefreshToken(),
                        () -> {
                            String url = currentActivity.getString(R.string.app_cms_video_status_api_url,
                                    appCMSMain.getApiBaseUrl(), filmId, appCMSSite.getGist().getSiteInternalName());
                            appCMSUserVideoStatusCall.call(url, getAuthToken(), responseAction);
                        });
            } else {
                String url = currentActivity.getString(R.string.app_cms_video_status_api_url,
                        appCMSMain.getApiBaseUrl(), filmId, appCMSSite.getGist().getSiteInternalName());
                appCMSUserVideoStatusCall.call(url, getAuthToken(), responseAction);
            }
        }
    }

    public void getUserVideoDownloadStatus(String filmId, Action1<UserVideoDownloadStatus> responseAction, String userId) {
        appCMSUserDownloadVideoStatusCall.call(filmId, this, responseAction, userId);
    }

    private void signinAnonymousUser() {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_anonymous_auth_token_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSSite.getGist().getSiteInternalName());
            appCMSAnonymousAuthTokenCall.call(url, anonymousAuthTokenResponse -> {
                try {
                    if (anonymousAuthTokenResponse != null) {
                        setAnonymousUserToken(anonymousAuthTokenResponse.getAuthorizationToken());
                    }
                } catch (Exception e) {
                    //Log.e(TAG, "Error signing in as anonymous user: " + e.getMessage());
                }
            });
        }
    }

    private void signinAnonymousUser(int tryCount,
                                     Uri searchQuery,
                                     PlatformType platformType) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_anonymous_auth_token_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSSite.getGist().getSiteInternalName());
            appCMSAnonymousAuthTokenCall.call(url, anonymousAuthTokenResponse -> {
                if (anonymousAuthTokenResponse != null) {
                    setAnonymousUserToken(anonymousAuthTokenResponse.getAuthorizationToken());
                    if (tryCount == 0) {
                        if (platformType == PlatformType.TV) {
                            getAppCMSTV(tryCount + 1);
                        }
                    } else {
                        showDialog(DialogType.NETWORK, null, false, null, null);
                    }
                } else {
                    if (platformType == PlatformType.ANDROID) {
                        getAppCMSAndroid(tryCount + 1);
                    } else if (platformType == PlatformType.TV) {
                        getAppCMSTV(tryCount + 1);
                    }
                }
            });
        }
    }

    public String getAppTextColor() {
        if (appCMSMain != null) {
            return getAppCMSMain()
                    .getBrand()
                    .getGeneral()
                    .getTextColor();
        }

        return null;
    }

    public String getAppBackgroundColor() {
        if (appCMSMain != null) {
            return appCMSMain.getBrand()
                    .getGeneral()
                    .getBackgroundColor();
        }

        return null;
    }

    public String getAppCtaTextColor() {
        if (appCMSMain != null) {
            return appCMSMain.getBrand()
                    .getCta()
                    .getPrimary()
                    .getTextColor();
        }
        return null;
    }

    public String getAppCtaBackgroundColor() {
        if (appCMSMain != null) {
            return appCMSMain.getBrand()
                    .getCta()
                    .getPrimary()
                    .getBackgroundColor();
        }
        return null;
    }

    public String getAppAdsURL(String pagePath) {
        if (currentActivity != null && appCMSAndroid != null) {
            Date now = new Date();

            String videoTag;

            if (appCMSAndroid.getAdvertising() != null &&
                    (TextUtils.isEmpty(appCMSAndroid.getAdvertising().getVideoTag()))) {
                videoTag = appCMSAndroid.getAdvertising().getVideoTag();
            } else {
                videoTag = "";
            }

            return currentActivity.getString(R.string.app_cms_ads_api_url,
                    videoTag,
                    getPermalinkCompletePath(pagePath),
                    now.getTime(),
                    appCMSMain.getSite());
        }
        return null;
    }

    public boolean shouldDisplayCRW() {
        if (appCMSMain != null && appCMSMain.getFeatures() != null) {
            return appCMSMain.getFeatures().isAutoPlay();
        }
        return false;
    }

    public void forceLoad() {
        this.forceLoad = true;
    }

    public boolean launchButtonSelectedAction(String pagePath,
                                              AppCMSActionPresenter actionPresenter,
                                              String filmTitle,
                                              String[] extraData,
                                              ContentDatum contentDatum,
                                              final boolean closeLauncher,
                                              int currentlyPlayingIndex,
                                              List<String> relateVideoIds) {
        boolean result = false;
        boolean isVideoOffline = false;

        try {
            isVideoOffline = Boolean.parseBoolean(extraData != null && extraData.length > 2 ? extraData[3] : "false");
        } catch (Exception e) {
            //Log.e(TAG, e.getLocalizedMessage());
        }
        final AppCMSActionType actionType = actionToActionTypeMap.get(actionPresenter.getAction());
        if ((actionType == AppCMSActionType.OPEN_OPTION_DIALOG)) {

            Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
            pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(pageLoadingActionIntent);

            getUserVideoStatus(contentDatum.getGist().getId(), userVideoStatusResponse -> {
                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                AppCMSTrayMenuDialogFragment appCMSTrayMenuDialogFragment = AppCMSTrayMenuDialogFragment.newInstance(userVideoStatusResponse.getQueued(), contentDatum);
                appCMSTrayMenuDialogFragment.show(currentActivity.getFragmentManager(), "AppCMSTrayMenuDialogFragment");
                appCMSTrayMenuDialogFragment.setMoreClickListener(trayMenuClickListener);
            });

            return false;
        }

        if (!isNetworkConnected() && !isVideoOffline) { //checking isVideoOffline here to fix SVFA-1431 in offline mode
            // Fix of SVFA-1435
            if (actionType == AppCMSActionType.CLOSE) {
                if (pagePath == null) {
                    currentActivity.finish();
                    return false;
                }
                sendCloseOthersAction(null, true, false);
                return false;
            }
            int finalCurrentlyPlayingIndex = currentlyPlayingIndex;
            List<String> finalRelateVideoIds = relateVideoIds;
            openDownloadScreenForNetworkError(false,
                    () -> launchButtonSelectedAction(pagePath,
                            actionPresenter,
                            filmTitle,
                            extraData,
                            contentDatum,
                            closeLauncher,
                            finalCurrentlyPlayingIndex,
                            finalRelateVideoIds));
        } else if (!cancelAllLoads) {
            //Log.d(TAG, "Attempting to load page " + filmTitle + ": " + pagePath);
            if (launched) {
                refreshPages(null, false, 0, 0);
            }

            /*This is to enable offline video playback even if Internet is not available*/
            if (!(actionType == AppCMSActionType.PLAY_VIDEO_PAGE && isVideoOffline) && !isNetworkConnected()) {
                showDialog(DialogType.NETWORK, null, false, null, null);
            } else if (currentActivity != null && (!loadingPage || forceLoad)) {
                forceLoad = false;
                if (actionType == null) {
                    //Log.e(TAG, "Action " + action + " not found!");
                    return false;
                }
                result = true;
                boolean isTrailer = actionType == AppCMSActionType.WATCH_TRAILER;
                if ((actionType == AppCMSActionType.PLAY_VIDEO_PAGE ||
                        actionType == AppCMSActionType.WATCH_TRAILER) &&
                        contentDatum != null &&
                        !isVideoPlayerStarted) {

                    ViewCreator.clearPlayerView();

                    isVideoPlayerStarted = true;
                    boolean entitlementActive = true;
                    boolean svodServiceType =
                            appCMSMain.getServiceType()
                                    .equals(currentActivity.getString(R.string.app_cms_main_svod_service_type_key));
                    if (svodServiceType &&
                            !isTrailer &&
                            contentDatum.getGist() != null &&
                            !contentDatum.getGist().getFree()) {
                        boolean freePreview = appCMSMain.getFeatures() != null &&
                                appCMSMain.getFeatures().getFreePreview() != null &&
                                appCMSMain.getFeatures().getFreePreview().isFreePreview();

                        if (!freePreview && !entitlementCheckActive.isSuccess()) {
                            entitlementCheckActive.setPagePath(pagePath);
                            entitlementCheckActive.setAction(actionPresenter.getAction());
                            entitlementCheckActive.setFilmTitle(filmTitle);
                            entitlementCheckActive.setExtraData(extraData);
                            entitlementCheckActive.setContentDatum(contentDatum);
                            entitlementCheckActive.setCloseLauncher(closeLauncher);
                            entitlementCheckActive.setCurrentlyPlayingIndex(currentlyPlayingIndex);
                            entitlementCheckActive.setRelateVideoIds(relateVideoIds);
                            getUserData(entitlementCheckActive);
                            entitlementActive = false;
                        }
                    }

                    if (entitlementActive) {
                        entitlementCheckActive.setSuccess(false);
                        Intent playVideoIntent = new Intent(currentActivity, AppCMSPlayVideoActivity.class);
                        boolean requestAds = !svodServiceType && actionType == AppCMSActionType.PLAY_VIDEO_PAGE;

                        //Send Firebase Analytics when user is subscribed and user is Logged In
                        sendFirebaseLoginSubscribeSuccess();

                        String adsUrl = null;
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
                                                        null,
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
                                    //Log.e(TAG, "Film " + filmId + " has not been downloaded");
                                }
                            }

                            long entitlementCheckVideoWatchTime = -1L;
                            if (entitlementPendingVideoData != null) {
                                if (isUserSubscribed()) {
                                    entitlementCheckVideoWatchTime = entitlementPendingVideoData.currentWatchedTime;
                                    entitlementPendingVideoData = null;
                                }
                            }

                            if (entitlementCheckVideoWatchTime != -1L) {
                                if (isUserSubscribed() && contentDatum.getGist().getWatchedTime() == 0L) {
                                    contentDatum.getGist().setWatchedTime(entitlementCheckVideoWatchTime);
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

                        try {
                            adsUrl = getAppAdsURL(pagePath);
                        } catch (Exception e) {
                            //
                        }

                        String backgroundColor = getAppBackgroundColor();

                        if (!getAutoplayEnabledUserPref(currentActivity)) {
                            relateVideoIds = null;
                            currentlyPlayingIndex = -1;
                        }

                        AppCMSVideoPageBinder appCMSVideoPageBinder =
                                getDefaultAppCMSVideoPageBinder(contentDatum, currentlyPlayingIndex, relateVideoIds, isVideoOffline, isTrailer, requestAds, adsUrl, backgroundColor);
                        if (closeLauncher) {
                            sendCloseOthersAction(null, true, false);
                        }

                        Bundle bundle = new Bundle();
                        bundle.putBinder(currentActivity.getString(R.string.app_cms_video_player_binder_key),
                                appCMSVideoPageBinder);
                        playVideoIntent.putExtra(currentActivity.getString(R.string.app_cms_video_player_bundle_binder_key), bundle);
                        playVideoIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                        stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                        currentActivity.startActivity(playVideoIntent);
                    } else {
                        entitlementPendingVideoData = new EntitlementPendingVideoData();
                        entitlementPendingVideoData.action = actionPresenter.getAction();
                        entitlementPendingVideoData.closeLauncher = closeLauncher;
                        entitlementPendingVideoData.contentDatum = contentDatum;
                        entitlementPendingVideoData.currentlyPlayingIndex = currentlyPlayingIndex;
                        entitlementPendingVideoData.pagePath = pagePath;
                        entitlementPendingVideoData.filmTitle = filmTitle;
                        entitlementPendingVideoData.extraData = extraData;
                        entitlementPendingVideoData.relateVideoIds = relateVideoIds;
                        isVideoPlayerStarted = false;
                        Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                        stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                    }
                } else if (actionType == AppCMSActionType.SHARE) {
                    if (extraData.length > 0) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, extraData[0]);
                        sendIntent.setType(currentActivity.getString(R.string.text_plain_mime_type));
                        Intent chooserIntent = Intent.createChooser(sendIntent,
                                currentActivity.getResources().getText(R.string.send_to));
                        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        currentActivity.startActivity(chooserIntent);
                        Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                        stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                    }
                } else if (actionType == AppCMSActionType.CLOSE) {
                    if (!BaseView.isTablet(currentContext)) {
                        restrictPortraitOnly();
                    }
                    ViewCreator.clearPlayerView();
                    sendCloseOthersAction(null, true, false);
                } else if (actionType == AppCMSActionType.LOGIN) {
                    //Log.d(TAG, "Login action selected: " + extraData[0]);
                    ViewCreator.clearPlayerView();
                    closeSoftKeyboard();
                    login(extraData[0], extraData[1]);
//                    sendSignInEmailFirebase();
                } else if (actionType == AppCMSActionType.FORGOT_PASSWORD) {
                    //Log.d(TAG, "Forgot password selected: " + extraData[0]);
                    ViewCreator.clearPlayerView();
                    closeSoftKeyboard();
                    launchResetPasswordPage(extraData[0]);
                } else if (actionType == AppCMSActionType.LOGIN_FACEBOOK) {
                    //Log.d(TAG, "Facebook Login selected");
                    ViewCreator.clearPlayerView();
                    loginFacebook();
                    sendSignInFacebookFirebase();
                } else if (actionType == AppCMSActionType.SIGNUP_FACEBOOK) {
                    //Log.d(TAG, "Facebook Signup selected");
                    ViewCreator.clearPlayerView();
                    loginFacebook();
                    sendSignUpFacebookFirebase();
                } else if (actionType == AppCMSActionType.LOGIN_GOOGLE) {
                    //Log.d(TAG, "Google Login selected");
                    ViewCreator.clearPlayerView();
                    loginGoogle();
                    sendSignInGoogleFirebase();
                } else if (actionType == AppCMSActionType.SIGNUP_GOOGLE) {
                    //Log.d(TAG, "Google signup selected");
                    ViewCreator.clearPlayerView();
                    loginGoogle();
                    sendSignUpGoogleFirebase();
                } else {
                    if (actionType == AppCMSActionType.SIGNUP) {
                        //Log.d(TAG, "Sign-Up selected: " + extraData[0]);
                        ViewCreator.clearPlayerView();
                        closeSoftKeyboard();
                        signup(extraData[0], extraData[1]);
                        sendSignUpEmailFirebase();
                    } else if (actionType == AppCMSActionType.START_TRIAL) {
                        //Log.d(TAG, "Start Trial selected");
                        ViewCreator.clearPlayerView();
                        navigateToSubscriptionPlansPage(false);
                    } else if (actionType == AppCMSActionType.EDIT_PROFILE) {
                        ViewCreator.clearPlayerView();
                        launchEditProfilePage();
                    } else if (actionType == AppCMSActionType.CHANGE_PASSWORD) {
                        ViewCreator.clearPlayerView();
                        launchChangePasswordPage();
                    } else if (actionType == AppCMSActionType.MANAGE_SUBSCRIPTION) {
                        ViewCreator.clearPlayerView();
                        if (extraData != null && extraData.length > 0) {
                            String key = extraData[0];
                            if (jsonValueKeyMap.get(key) == AppCMSUIKeyType.PAGE_SETTINGS_UPGRADE_PLAN_PROFILE_KEY) {
                                String paymentProcessor = getActiveSubscriptionProcessor();
                                if (isUserSubscribed() &&
                                        !TextUtils.isEmpty(paymentProcessor) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly)) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ccavenue_payment_processor)) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ccavenue_payment_processor_friendly))) {
                                    showEntitlementDialog(DialogType.CANNOT_UPGRADE_SUBSCRIPTION, null);
                                } else if (isUserSubscribed() &&
                                        TextUtils.isEmpty(paymentProcessor)) {
                                    showEntitlementDialog(DialogType.UNKNOWN_SUBSCRIPTION_FOR_UPGRADE, null);
                                } else if (isUserSubscribed() &&
                                        (isExistingGooglePlaySubscriptionSuspended() ||
                                                !upgradesAvailableForUser())) {
                                    showEntitlementDialog(DialogType.UPGRADE_UNAVAILABLE, null);
                                } else {
                                    navigateToSubscriptionPlansPage(false);
                                }
                            } else if (jsonValueKeyMap.get(key) == AppCMSUIKeyType.PAGE_SETTINGS_CANCEL_PLAN_PROFILE_KEY) {
                                String paymentProcessor = getActiveSubscriptionProcessor();
                                if ((!TextUtils.isEmpty(paymentProcessor) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly))) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ccavenue_payment_processor)) &&
                                        !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ccavenue_payment_processor_friendly))) {
                                    showEntitlementDialog(DialogType.CANNOT_CANCEL_SUBSCRIPTION, null);
                                } else if (isUserSubscribed() && TextUtils.isEmpty(paymentProcessor)) {
                                    showEntitlementDialog(DialogType.UNKNOWN_SUBSCRIPTION_FOR_CANCEL, null);
                                } else {
                                    initiateSubscriptionCancellation();
                                }
                            }
                        }
                    } else if (actionType == AppCMSActionType.HOME_PAGE) {
                        ViewCreator.clearPlayerView();
                        navigateToHomePage();
                    } else if (actionType == AppCMSActionType.SIGNIN) {
                        ViewCreator.clearPlayerView();
                        navigateToLoginPage(false);
                    } else if (actionType == AppCMSActionType.CHANGE_DOWNLOAD_QUALITY) {
                        ViewCreator.clearPlayerView();
                        showDownloadQualityScreen(contentDatum, userVideoDownloadStatus -> {
                            //
                        });
                    } else {
                        ViewCreator.clearPlayerView();
                        boolean appbarPresent = true;
                        boolean fullscreenEnabled = false;
                        boolean navbarPresent = true;
                        ExtraScreenType screenType;
                        final StringBuffer screenName = new StringBuffer();

                        if (!TextUtils.isEmpty(actionToPageNameMap.get(actionPresenter.getAction()))) {
                            screenName.append(actionToPageNameMap.get(actionPresenter.getAction()));
                        }

                        loadingPage = true;

                        String baseUrl = appCMSMain.getApiBaseUrl();
                        String endPoint = actionToPageAPIUrlMap.get(actionPresenter.getAction());
                        String siteId = appCMSSite.getGist().getSiteInternalName();
                        boolean usePageIdQueryParam = false;
                        boolean showPage = false;

                        switch (actionType) {
                            case AUTH_PAGE:
                                appbarPresent = false;
                                fullscreenEnabled = false;
                                navbarPresent = false;
                                screenType = ExtraScreenType.NONE;
                                break;

                            case SHOW_PAGE:
                                showPage = true;
                            case VIDEO_PAGE:
                                appbarPresent = false;
                                fullscreenEnabled = false;
                                navbarPresent = false;
                                screenType = ExtraScreenType.NONE;
                                screenName.append(currentActivity.getString(
                                        R.string.app_cms_template_page_separator));
                                screenName.append(filmTitle);
                                break;

                            case DRAGGABLE_VIDEO_PAGE:
                                appbarPresent = false;
                                fullscreenEnabled = true;
                                navbarPresent = false;
                                screenType = ExtraScreenType.DRAGGABLE_PANEL;
                                break;

                            case PLAY_VIDEO_PAGE:
                                appbarPresent = false;
                                fullscreenEnabled = false;
                                navbarPresent = false;
                                screenType = ExtraScreenType.NONE;
                                break;

                            case HOME_PAGE:
                            default:
                                screenType = ExtraScreenType.NONE;
                                break;
                        }

                        AppCMSPageUI appCMSPageUI = actionToPageMap.get(actionPresenter.getAction());

                        if (appCMSPageUI == null) {
                            MetaPage metaPage = actionTypeToMetaPageMap.get(actionType);
                            if (metaPage != null) {
                                int finalCurrentlyPlayingIndex2 = currentlyPlayingIndex;
                                List<String> finalRelateVideoIds2 = relateVideoIds;
                                getAppCMSPage(metaPage.getPageUI(),
                                        appCMSPageUIResult -> {
                                            if (appCMSPageUIResult != null) {
                                                navigationPages.put(metaPage.getPageId(), appCMSPageUIResult);
                                                String action = pageNameToActionMap.get(metaPage.getPageName());
                                                if (action != null && actionToPageMap.containsKey(action)) {
                                                    actionToPageMap.put(action, appCMSPageUIResult);
                                                }

                                                launchButtonSelectedAction(pagePath,
                                                        actionPresenter,
                                                        filmTitle,
                                                        extraData,
                                                        contentDatum,
                                                        closeLauncher,
                                                        finalCurrentlyPlayingIndex2,
                                                        finalRelateVideoIds2);
                                            }
                                        },
                                        loadFromFile,
                                        false);
                            }
                        }

                        String apiUrl = getApiUrl(usePageIdQueryParam,
                                false,
                                showPage,
                                baseUrl,
                                endPoint,
                                siteId,
                                pagePath,
                                appCMSPageUI != null &&
                                        appCMSPageUI.getCaching() != null &&
                                        !appCMSPageUI.getCaching().shouldOverrideCaching() &&
                                        appCMSPageUI.getCaching().isEnabled());

                        Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                        pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(pageLoadingActionIntent);

                        if (appCMSPageUI != null) {
                            int finalCurrentlyPlayingIndex1 = currentlyPlayingIndex;
                            List<String> finalRelateVideoIds1 = relateVideoIds;
                            getPageIdContent(apiUrl,
                                    pagePath,
                                    null,
                                    appCMSPageUI != null &&
                                            appCMSPageUI.getCaching() != null &&
                                            !appCMSPageUI.getCaching().shouldOverrideCaching() &&
                                            appCMSPageUI.getCaching().isEnabled(),
                                    new AppCMSPageAPIAction(appbarPresent,
                                            fullscreenEnabled,
                                            navbarPresent,
                                            appCMSPageUI,
                                            actionPresenter.getAction(),
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
                                                        screenType);
                                                if (args != null) {
                                                    Intent updatePageIntent =
                                                            new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                                    updatePageIntent.putExtra(
                                                            currentActivity.getString(R.string.app_cms_bundle_key),
                                                            args);
                                                    updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                                    currentActivity.sendBroadcast(updatePageIntent);

                                                    Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                                                    stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                                    currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                                                }
                                                launched = true;
                                            } else {
                                                if (launched) {
                                                    sendStopLoadingPageAction(true,
                                                            () -> launchButtonSelectedAction(pagePath, actionPresenter, filmTitle, extraData, contentDatum, closeLauncher, finalCurrentlyPlayingIndex1, finalRelateVideoIds1));
                                                    setNavItemToCurrentAction(currentActivity);
                                                } else {
                                                    launchBlankPage();
                                                }
                                            }
                                            loadingPage = false;
                                        }
                                    });
                        } else {
                            loadingPage = false;
                        }
                    }
                }
            }
        }
        return result;
    }

    public AppCMSVideoPageBinder getDefaultAppCMSVideoPageBinder(ContentDatum contentDatum,
                                                                 int currentlyPlayingIndex,
                                                                 List<String> relateVideoIds,
                                                                 boolean isVideoOffline,
                                                                 boolean isTrailer,
                                                                 boolean requestAds,
                                                                 String adsUrl,
                                                                 String backgroundColor) {
        return getAppCMSVideoPageBinder(currentActivity,
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
    }

    public void setVideoPlayerHasStarted() {
        isVideoPlayerStarted = false;
    }

    @SuppressWarnings("unused")
    public boolean launchCCAvenueSeamless() {
        boolean result = false;

        if (currentActivity != null) {
            cancelInternalEvents();

            Bundle args = getPageActivityBundle(currentActivity,
                    null,
                    null,
                    currentActivity.getString(R.string.app_cms_ccavenue_page_tag),
                    currentActivity.getString(R.string.app_cms_ccavenue_page_tag),
                    null,
                    currentActivity.getString(R.string.app_cms_ccavenue_page_tag),
                    false,
                    true,
                    false,
                    false,
                    false,
                    null,
                    ExtraScreenType.CCAVENUE);
            if (args != null) {
                Intent updatePageIntent =
                        new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                updatePageIntent.putExtra(
                        currentActivity.getString(R.string.app_cms_bundle_key),
                        args);
                updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(updatePageIntent);
            }

            result = true;
        }

        return result;
    }

    public ContentDatum getUserHistoryContentDatum(String filmId) {
        try {
            return userHistoryData.get(filmId);
        } catch (Exception e) {

        }
        return null;
    }

    public ArrayList<ContentDatum> getAllUserHistory() {
        if (userHistoryData != null) {
            ArrayList<ContentDatum> continueWatching = new ArrayList<>();
            for (ContentDatum data : userHistoryData.values()) {
                if (getWatchingPercentage(data) < 97) {
                    continueWatching.add(data);
                }
            }
            return continueWatching;
        }
        return null;
    }

    public int getWatchingPercentage(ContentDatum historyData) {

        int progress = 0;
        if (historyData != null) {
            if (historyData.getGist().getWatchedPercentage() > 0) {
                progress = historyData.getGist().getWatchedPercentage();
            } else {
                long watchedTime = historyData.getGist().getWatchedTime();
                long runTime = historyData.getGist().getRuntime();
                if (watchedTime > 0 && runTime > 0) {
                    long percentageWatched = (long) (((double) watchedTime / (double) runTime) * 100.0);
                    progress = (int) percentageWatched;
                }
            }
        }
        return progress;
    }

    public boolean isFilmAddedToWatchlist(String filmId) {
        try {
            if (filmId != null) {
                List<Record> watchlistRecords = filmsInUserWatchList.getRecords();
                int numWatchlistRecords = watchlistRecords.size();
                for (int i = 0; i < numWatchlistRecords; i++) {
                    Record watchlistRecord = watchlistRecords.get(i);
                    if (watchlistRecord != null &&
                            watchlistRecord.getContentResponse() != null &&
                            watchlistRecord.getContentResponse().getGist() != null) {
                        if (filmId.equals(watchlistRecord.getContentResponse().getGist().getId())) {
                            return true;
                        }
                    }
                }
            }

            return temporaryWatchlist.contains(filmId);
        } catch (Exception e) {

        }

        return false;
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
                    true,
                    false,
                    null,
                    ExtraScreenType.NAVIGATION);
            if (args != null) {
                Intent updatePageIntent =
                        new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                updatePageIntent.putExtra(
                        currentActivity.getString(R.string.app_cms_bundle_key),
                        args);
                updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(updatePageIntent);
            }

            result = true;
        }

        return result;
    }

    public boolean isLaunched() {
        return launched;
    }

    public void resetLaunched() {
        launched = false;
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
            if (updateFromModule != null && updateFromModule.getContentData() != null) {
                if (updateToModule != null &&
                        updateToModule.getContentData() != null) {
                    AppCMSUIKeyType moduleType = jsonValueKeyMap.get(updateToModule.getModuleType());
                    if (moduleType == AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY) {
                        updateToModule.setContentData(updateFromModule.getContentData());
                    } else {
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
                } else {
                    updateToModule.setContentData(updateFromModule.getContentData());
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

    private void setAllChildrenEnabled(boolean isEnabled, ViewGroup viewGroup) {
        viewGroup.setNestedScrollingEnabled(isEnabled);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                if (view instanceof RecyclerView) {
                    ((RecyclerView) view).setLayoutFrozen(!isEnabled);
                    if (((RecyclerView) view).getAdapter() instanceof AppCMSBaseAdapter) {
                        AppCMSBaseAdapter appCMSViewAdapter =
                                (AppCMSBaseAdapter) ((RecyclerView) view).getAdapter();
                        appCMSViewAdapter.setClickable(isEnabled);
                    }
                }
                setAllChildrenEnabled(isEnabled, (ViewGroup) view);
            } else {
                if (view instanceof Button || view instanceof ImageButton) {
                    view.setEnabled(isEnabled);
                    view.setClickable(isEnabled);
                }
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

    public boolean isAddOnFragmentVisible() {
        if (currentActivity != null) {
            FrameLayout addOnFragment =
                    (FrameLayout) currentActivity.findViewById(R.id.app_cms_addon_fragment);
            return addOnFragment != null && addOnFragment.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    public boolean isAdditionalFragmentVisibile() {
        if (currentActivity != null) {
            FrameLayout additionalFragmentView =
                    (FrameLayout) currentActivity.findViewById(R.id.app_cms_addon_fragment);
            if (additionalFragmentView != null) {
                return additionalFragmentView.getVisibility() == View.VISIBLE;
            }
        }
        return false;
    }

    public void showAddOnFragment(boolean showMainFragment, float mainFragmentTransparency) {
        if (currentActivity != null) {
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
    }

    private boolean isAdditionalFragmentViewAvailable() {
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

    public ImageButton getCurrentMediaRouteButton() {
        return currentMediaRouteButton;
    }

    public void setCurrentMediaRouteButton(ImageButton currentMediaRouteButton) {
        this.currentMediaRouteButton = currentMediaRouteButton;
    }

    public ViewGroup getCurrentMediaRouteButtonParent() {
        return currentMediaRouteButtonParent;
    }

    public void setCurrentMediaRouteButtonParent(ViewGroup currentMediaRouteButtonParent) {
        this.currentMediaRouteButtonParent = currentMediaRouteButtonParent;
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
                updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(updatePageIntent);
            }
        }
    }

    public void launchSearchResultsPage(String searchQuery) {
        if (currentActivity != null && !cancelAllLoads) {

            Intent searchIntent = new Intent(currentActivity, AppCMSSearchActivity.class);
            searchIntent.setAction(Intent.ACTION_SEARCH);
            searchIntent.putExtra(SearchManager.QUERY, searchQuery);
            currentActivity.startActivity(searchIntent);
        }
    }

    private void launchResetPasswordPage(String email) {
        if (currentActivity != null && !cancelAllLoads) {
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
                updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(updatePageIntent);
            }
        }
    }

    private void launchResetPasswordTVPage(AppCMSPageUI appCMSPageUI) {
        if (currentActivity != null && !cancelAllLoads) {
            cancelInternalEvents();
            AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
            appCMSPageAPI.setId(getPageId(appCMSPageUI));
            Bundle args = getPageActivityBundle(currentActivity,
                    appCMSPageUI,
                    appCMSPageAPI,
                    currentActivity.getString(R.string.app_cms_reset_password_page_tag),
                    currentActivity.getString(R.string.app_cms_reset_password_page_tag),
                    null,
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
                        new Intent(AppCMSPresenter.ACTION_RESET_PASSWORD);
                updatePageIntent.putExtra(
                        currentActivity.getString(R.string.app_cms_bundle_key),
                        args);
                updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(updatePageIntent);
            }
        }
    }

    private void launchEditProfilePage() {
        if (currentActivity != null && !cancelAllLoads) {
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
                updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(updatePageIntent);
            }
        }
    }

    private void launchChangePasswordPage() {
        if (currentActivity != null && !cancelAllLoads) {
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
                updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(updatePageIntent);
            }
        }
    }

    private void loginFacebook() {
        if (currentActivity != null) {
            isSignupFromFacebook = true;
            waithingFor3rdPartyLogin = true;
            LoginManager.getInstance().logOut();
            Intent pageLoadingIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
            pageLoadingIntent.putExtra(currentActivity.getString(R.string.thrid_party_login_intent_extra_key), true);
            pageLoadingIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(pageLoadingIntent);
            LoginManager.getInstance().logInWithReadPermissions(currentActivity,
                    Arrays.asList("public_profile", "email", "user_friends"));
        }
    }

    public boolean isSignUpFromFacebook() {
        return isSignupFromFacebook;
    }

    private void loginGoogle() {
        if (currentActivity != null) {
            Intent pageLoadingIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
            pageLoadingIntent.putExtra(currentActivity.getString(R.string.thrid_party_login_intent_extra_key), true);
            pageLoadingIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(pageLoadingIntent);

            isSignupFromGoogle = true;

            waithingFor3rdPartyLogin = true;

            if (googleApiClient != null && googleApiClient.isConnected()) {
                Auth.GoogleSignInApi.signOut(googleApiClient);
            }

            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(serverClientId)
                    .build();

            Intent googleIntent = Auth.GoogleSignInApi
                    .getSignInIntent(getGoogleApiClient(googleSignInOptions));
            currentActivity.startActivityForResult(googleIntent, RC_GOOGLE_SIGN_IN);
        }
    }

    private GoogleApiClient getGoogleApiClient(GoogleSignInOptions googleSignInOptions) {
        if (googleApiClient == null) {
            try {
                googleApiClient = new GoogleApiClient.Builder(currentActivity)
                        .enableAutoManage((FragmentActivity) currentActivity,
                                (GoogleApiClient.OnConnectionFailedListener) currentActivity)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                        .build();
            } catch (Exception e) {

            }
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
                                              double discountedPrice,
                                              String recurringPaymentCurrencyCode,
                                              String countryCode,
                                              boolean isRenewable,
                                              String getRenewableFrequency,
                                              boolean upgradesAvailable) {
        if (currentActivity != null) {
            launchType = LaunchType.SUBSCRIBE;
            skuToPurchase = sku;
            planToPurchase = planId;
            planToPurchaseName = planName;
            currencyOfPlanToPurchase = currency;
            planToPurchasePrice = planPrice;
            planToPurchaseDiscountedPrice = discountedPrice;
            currencyCode = recurringPaymentCurrencyCode;
            this.upgradesAvailable = upgradesAvailable;
            this.checkUpgradeFlag = true;
            this.countryCode = countryCode;
            this.isRenewable = isRenewable;
            this.renewableFrequency = getRenewableFrequency;
            Bundle bundle = new Bundle();
            String FIREBASE_PLAN_ITEM_ID = "item_id";
            bundle.putString(FIREBASE_PLAN_ITEM_ID, planToPurchase);
            String FIREBASE_PLAN_ITEM_NAME = "item_name";
            bundle.putString(FIREBASE_PLAN_ITEM_NAME, planToPurchaseName);
            String FIREBASE_PLAN_ITEM_CURRENCY = "currency";
            bundle.putString(FIREBASE_PLAN_ITEM_CURRENCY, currencyOfPlanToPurchase);
            String FIREBASE_PLAN_ITEM_PRICE = "value";
            bundle.putDouble(FIREBASE_PLAN_ITEM_PRICE, planToPurchasePrice);

            String firebaseSelectPlanEventKey = "add_to_cart";
            sendFirebaseSelectedEvents(firebaseSelectPlanEventKey, bundle);

            if (isUserLoggedIn()) {

                //Log.d(TAG, "Initiating item purchase for subscription");
                initiateItemPurchase(false);
            } else {
                //Log.d(TAG, "Navigating to login page for subscription");
                navigateToLoginPage(loginFromNavPage);

                Bundle bundleSignUp = new Bundle();
                String FIREBASE_SIGNUP_SCREEN_VALUE = "Sign Up Screen";
                bundleSignUp.putString(FIREBASE_SCREEN_VIEW_EVENT, FIREBASE_SIGNUP_SCREEN_VALUE);
                String firebaseEventKey = FirebaseAnalytics.Event.VIEW_ITEM;

                sendFirebaseSelectedEvents(firebaseEventKey, bundleSignUp);
            }
        }
    }

    private void initiateCCAvenuePurchase() {
        //Log.v("authtoken", getAuthToken());
        //Log.v("apikey", apikey);
        try {
            String strAmount = Double.toString(planToPurchaseDiscountedPrice);
            Intent intent = new Intent(currentActivity, EnterMobileNumberActivity.class);
            //Intent intent = new Intent(currentActivity, WebViewActivity.class);
            //Intent intent = new Intent(currentActivity, PaymentOptionsActivity.class);
            intent.putExtra(AvenuesParams.CURRENCY, currencyCode);
            intent.putExtra(AvenuesParams.AMOUNT, strAmount);
            intent.putExtra(currentActivity.getString(R.string.app_cms_site_name), appCMSSite.getGist().getSiteInternalName());
            intent.putExtra(currentActivity.getString(R.string.app_cms_user_id), getLoggedInUser());
            intent.putExtra(currentActivity.getString(R.string.app_cms_plan_id), planToPurchase);
            intent.putExtra("plan_to_purchase_name", planToPurchaseName);
            intent.putExtra("siteId", appCMSSite.getGist().getSiteInternalName());
            intent.putExtra("email", getLoggedInUserEmail());
            intent.putExtra("authorizedUserName", getLoggedInUser());
            intent.putExtra("x-api-token", apikey);
            intent.putExtra("auth_token", getAuthToken());
            intent.putExtra("renewable", isRenewable);
            intent.putExtra("mobile_number", "");
            intent.putExtra("api_base_url", appCMSMain.getApiBaseUrl());
            intent.putExtra("si_frequency", "2");
            intent.putExtra("si_frequency_type", renewableFrequency);
            intent.putExtra("color_theme", getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor());
            currentActivity.startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean useCCAvenue() {
        boolean useCCAve = (TextUtils.isEmpty(getActiveSubscriptionProcessor()) ||
                (!TextUtils.isEmpty(getActiveSubscriptionProcessor()) &&
                        (!getActiveSubscriptionProcessor().equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) &&
                                !getActiveSubscriptionProcessor().equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly))))) &&
                TextUtils.isEmpty(getExistingGooglePlaySubscriptionId()) &&
                !TextUtils.isEmpty(countryCode) &&
                appCMSMain != null &&
                appCMSMain.getPaymentProviders() != null &&
                appCMSMain.getPaymentProviders().getCcav() != null &&
                !TextUtils.isEmpty(appCMSMain.getPaymentProviders().getCcav().getCountry()) &&
                appCMSMain.getPaymentProviders().getCcav().getCountry().equalsIgnoreCase(countryCode);
        Log.v("useCCAve", useCCAve + "");
        return useCCAve;
    }

    public void initiateItemPurchase(boolean purchaseFromRestore) {
        //Log.d(TAG, "Initiating item purchase");

        //Log.d(TAG, "checkForExistingSubscription()");

        this.purchaseFromRestore = purchaseFromRestore;

        checkForExistingSubscription(false);

        if (useCCAvenue()) {
            //Log.d(TAG, "Initiating CCAvenue purchase");
            if (isUserSubscribed()) {
                try {
                    showLoadingDialog(true);
                    appCMSSubscriptionPlanCall.call(
                            currentActivity.getString(R.string.app_cms_get_current_subscription_api_url,
                                    appCMSMain.getApiBaseUrl(),
                                    getLoggedInUser(),
                                    appCMSSite.getGist().getSiteInternalName()),
                            R.string.app_cms_subscription_subscribed_plan_key,
                            null,
                            apikey,
                            getAuthToken(),
                            listResult -> {
                                //Log.v("currentActivity", "currentActivity");
                                showLoadingDialog(false);
                            }, appCMSSubscriptionPlanResults -> {
                                showLoadingDialog(false);
                                sendCloseOthersAction(null, true, false);
                                refreshSubscriptionData(this::sendRefreshPageAction, true);
                            },
                            appCMSSubscriptionPlanResult -> {
                                showLoadingDialog(false);
                                try {
                                    if (appCMSSubscriptionPlanResult != null) {
                                        upgradePlanAPICall();
                                    }
                                } catch (Exception e) {
                                    //Log.e(TAG, "refreshSubscriptionData: " + e.getMessage());
                                }
                            }
                    );
                } catch (Exception ex) {
                    //
                }
            } else {
                initiateCCAvenuePurchase();
            }
        } else {
            if (currentActivity != null &&
                    inAppBillingService != null &&
                    TextUtils.isEmpty(getRestoreSubscriptionReceipt())) {
                //Log.d(TAG, "Initiating Google Play Services purchase");
                try {
                    Bundle activeSubs = null;
                    try {
                        activeSubs = inAppBillingService.getPurchases(3,
                                currentActivity.getPackageName(),
                                "subs",
                                null);
                    } catch (RemoteException e) {
                        //Log.e(TAG, "Failed to retrieve current active subscriptions: " + e.getMessage());
                    }

                    ArrayList<String> subscribedSkus = null;

                    if (activeSubs != null) {
                        subscribedSkus = activeSubs.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                    }

                    Bundle buyIntentBundle;
                    if (subscribedSkus != null && !subscribedSkus.isEmpty()) {
                        //Log.d(TAG, "Initiating upgrade purchase");
                    } else {
                        //Log.d(TAG, "Initiating new item purchase");
                    }

                    buyIntentBundle = inAppBillingService.getBuyIntentToReplaceSkus(5,
                            currentActivity.getPackageName(),
                            subscribedSkus,
                            skuToPurchase,
                            "subs",
                            null);

                    if (buyIntentBundle != null) {
                        int resultCode = buyIntentBundle.getInt("RESPONSE_CODE");
                        if (resultCode == 0) {
                            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                            if (pendingIntent != null) {
                                //Log.d(TAG, "Launching intent to initiate item purchase");
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
                        } else {
                            if (resultCode == IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED) {
                                showDialog(DialogType.SUBSCRIBE, "Billing response was cancelled by user", false, null, null);
                            } else if (resultCode == IabHelper.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE) {
                                showDialog(DialogType.SUBSCRIBE, "Billing response is unavailable", false, null, null);
                            } else if (resultCode == IabHelper.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE) {
                                addGoogleAccountToDevice();
                            } else if (resultCode == IabHelper.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE) {
                                showDialog(DialogType.SUBSCRIBE, "Billing response result item is unavailable", false, null, null);
                            } else if (resultCode == IabHelper.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR) {
                                showDialog(DialogType.SUBSCRIBE, "Billing response result developer error", false, null, null);
                            } else if (resultCode == IabHelper.BILLING_RESPONSE_RESULT_ERROR) {
                                showDialog(DialogType.SUBSCRIBE, "Billing response result error", false, null, null);
                            } else if (resultCode == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                                showDialog(DialogType.SUBSCRIBE, "Billing response item already purchased", false, null, null);
                            } else if (resultCode == IabHelper.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED) {
                                showDialog(DialogType.SUBSCRIBE, "Billing response item not owned", false, null, null);
                            }
                        }
                    }
                } catch (RemoteException | IntentSender.SendIntentException e) {
                    //Log.e(TAG, "Failed to purchase item with sku: "
//                            + skuToPurchase
//                            + e.getMessage());
                }
            } else if (!TextUtils.isEmpty(getRestoreSubscriptionReceipt())) {
                //Log.d(TAG, "Finalizing subscription after signup - existing subscription: " +
//                        getRestoreSubscriptionReceipt());
                try {
                    InAppPurchaseData inAppPurchaseData = gson.fromJson(getRestoreSubscriptionReceipt(),
                            InAppPurchaseData.class);
                    skuToPurchase = inAppPurchaseData.getProductId();
                    finalizeSignupAfterSubscription(getRestoreSubscriptionReceipt());
                } catch (Exception e) {
                    //Log.e(TAG, "Could not parse InApp Purchase Data: " + getRestoreSubscriptionReceipt());
                }
            } else {
                //Log.e(TAG, "InAppBillingService: " + inAppBillingService);
            }
        }
    }

    @SuppressWarnings("unused")
    private void checkCCAvenueUpgradeStatus(String referenceNo) {
        try {
            SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
            subscriptionRequest.setReferenceNo(referenceNo);
            Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
            pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(pageLoadingActionIntent);
            appCMSSubscriptionPlanCall.call(
                    currentActivity.getString(R.string.app_cms_ccavenue_is_plan_upgradable_url,
                            appCMSMain.getApiBaseUrl(),
                            appCMSSite.getGist().getSiteInternalName()),
                    R.string.app_cms_check_ccavenue_plan_status_key,
                    subscriptionRequest,
                    apikey,
                    getAuthToken(),
                    listResult -> {
                        //Log.v("currentActivity", "currentActivity");
                    },
                    singleResult -> {
                        if (singleResult != null) {
                            String siStatus = singleResult.getSiStatus();
                            if (siStatus != null && siStatus.equalsIgnoreCase("ACTI")) {
                                upgradePlanAPICall();
                            } else {
                                showDialog(DialogType.SUBSCRIBE, "Please Try Again Later!", false, null, null);
                                sendCloseOthersAction(null, true, false);
                            }
                        } else {
                            showDialog(DialogType.SUBSCRIBE, "Please Try Again Later!", false, null, null);
                            sendCloseOthersAction(null, true, false);
                        }
                    },
                    appCMSSubscriptionPlanResult -> {
                        //
                    }
            );
        } catch (Exception ex) {
            //
        }
    }

    private void upgradePlanAPICall() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setPlatform(currentActivity.getString(R.string.app_cms_subscription_platform_key));
        subscriptionRequest.setSiteId(currentActivity.getString(R.string.app_cms_app_name));
        subscriptionRequest.setSubscription(currentActivity.getString(R.string.app_cms_subscription_key));
        subscriptionRequest.setCurrencyCode(getActiveSubscriptionCurrency());
        subscriptionRequest.setPlanIdentifier(skuToPurchase);
        subscriptionRequest.setPlanId(planToPurchase);
        subscriptionRequest.setUserId(getLoggedInUser());
        subscriptionRequest.setReceipt(getActiveSubscriptionReceipt());
        Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
        pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
        currentActivity.sendBroadcast(pageLoadingActionIntent);
        try {
            appCMSSubscriptionPlanCall.call(
                    currentActivity.getString(R.string.app_cms_register_subscription_api_url,
                            appCMSMain.getApiBaseUrl(),
                            appCMSSite.getGist().getSiteInternalName(),
                            currentActivity.getString(R.string.app_cms_subscription_platform_key)),
                    R.string.app_cms_subscription_plan_update_key,
                    subscriptionRequest,
                    apikey,
                    getAuthToken(),
                    result -> {
                        //
                        //Log.v("got result", "got result");
                    }, appCMSSubscriptionPlanResults -> {
                        if (appCMSSubscriptionPlanResults != null &&
                                (!TextUtils.isEmpty(appCMSSubscriptionPlanResults.getMessage()) ||
                                        !TextUtils.isEmpty(appCMSSubscriptionPlanResults.getError()))) {
                            if (!TextUtils.isEmpty(appCMSSubscriptionPlanResults.getMessage())) {
                                if (!TextUtils.isEmpty(appCMSSubscriptionPlanResults.getSubscriptionStatus())) {
                                    if (appCMSSubscriptionPlanResults.getSubscriptionStatus().equalsIgnoreCase("COMPLETED") &&
                                            !TextUtils.isEmpty(appCMSSubscriptionPlanResults.getMessage())) {
                                        showDialog(DialogType.SUBSCRIBE,
                                                appCMSSubscriptionPlanResults.getMessage(),
                                                false,
                                                null,
                                                null);
                                    }
                                    sendCloseOthersAction(null, true, false);

                                    refreshSubscriptionData(this::sendRefreshPageAction, true);
                                } else {
                                    showDialog(DialogType.SUBSCRIBE,
                                            appCMSSubscriptionPlanResults.getMessage(),
                                            false,
                                            null,
                                            null);
                                }
                            } else {
                                showDialog(DialogType.SUBSCRIBE,
                                        appCMSSubscriptionPlanResults.getError(),
                                        false,
                                        null,
                                        null);
                            }
                            showLoadingDialog(false);
                        } else {
                            sendCloseOthersAction(null, true, false);

                            refreshSubscriptionData(this::sendRefreshPageAction, true);
                        }
                    },
                    currentUserPlan -> {
                        //
                    });
        } catch (IOException e) {

        }
    }

    private void addGoogleAccountToDevice() {
        Intent addAccountIntent = new Intent(android.provider.Settings.ACTION_ADD_ACCOUNT)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        addAccountIntent.putExtra(android.provider.Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
        currentActivity.startActivityForResult(addAccountIntent, ADD_GOOGLE_ACCOUNT_TO_DEVICE_REQUEST_CODE);
    }

    private void sendSubscriptionCancellation() {
        if (currentActivity != null) {
            if (!TextUtils.isEmpty(getActiveSubscriptionSku())) {
                SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
                subscriptionRequest.setPlatform(currentActivity.getString(R.string.app_cms_subscription_platform_key));
                subscriptionRequest.setSiteId(currentActivity.getString(R.string.app_cms_app_name));
                subscriptionRequest.setSubscription(currentActivity.getString(R.string.app_cms_subscription_key));
                subscriptionRequest.setCurrencyCode(getActiveSubscriptionCurrency());
                subscriptionRequest.setPlanIdentifier(getActiveSubscriptionSku());
                subscriptionRequest.setPlanId(getActiveSubscriptionId());
                subscriptionRequest.setUserId(getLoggedInUser());
                subscriptionRequest.setReceipt(getActiveSubscriptionReceipt());

                //Log.d(TAG, "Subscription request: " + gson.toJson(subscriptionRequest, SubscriptionRequest.class));

                try {
                    //Log.v("authtoken", getAuthToken());
                    appCMSSubscriptionPlanCall.call(
                            currentActivity.getString(R.string.app_cms_cancel_subscription_api_url,
                                    appCMSMain.getApiBaseUrl(),
                                    appCMSSite.getGist().getSiteInternalName(),
                                    currentActivity.getString(R.string.app_cms_subscription_platform_key)),
                            R.string.app_cms_subscription_plan_cancel_key,
                            subscriptionRequest,
                            apikey,
                            getAuthToken(),
                            result -> {
                                if (result != null) {
                                    setIsUserSubscribed(false);
                                }
                            },
                            appCMSSubscriptionPlanResults -> {
                                sendCloseOthersAction(null, true, false);

                                refreshSubscriptionData(this::sendRefreshPageAction, true);

                                if (!TextUtils.isEmpty(getAppsFlyerKey())) {
                                    AppsFlyerUtils.subscriptionEvent(currentActivity,
                                            false,
                                            getAppsFlyerKey(),
                                            getActiveSubscriptionPrice(),
                                            subscriptionRequest.getPlanId(),
                                            subscriptionRequest.getCurrencyCode());
                                }

                                //Subscription Succes Firebase Log Event
                                Bundle bundle = new Bundle();
                                bundle.putString(FIREBASE_PLAN_ID, getActiveSubscriptionId());
                                bundle.putString(FIREBASE_PLAN_NAME, getActiveSubscriptionPlanName());
                                bundle.putString(FIREBASE_CURRENCY_NAME, getActiveSubscriptionCurrency());
                                bundle.putString(FIREBASE_VALUE, getActiveSubscriptionPrice());
                                //bundle.putString(FIREBASE_TRANSACTION_ID,get);
                                if (mFireBaseAnalytics != null)
                                    mFireBaseAnalytics.logEvent(FIREBASE_CANCEL_SUBSCRIPTION, bundle);
                            },
                            currentUserPlan -> {
                                //
                            });
                } catch (IOException e) {
                    //Log.e(TAG, "Failed to update user subscription status");
                }
            }
        }
    }

    private void initiateSubscriptionCancellation() {
        if (currentActivity != null) {
            if (!TextUtils.isEmpty(getActiveSubscriptionCountryCode()) &&
                    appCMSMain != null &&
                    appCMSMain.getPaymentProviders() != null &&
                    appCMSMain.getPaymentProviders().getCcav() != null &&
                    !TextUtils.isEmpty(appCMSMain.getPaymentProviders().getCcav().getCountry()) &&
                    appCMSMain.getPaymentProviders().getCcav().getCountry().equalsIgnoreCase(countryCode)) {
                showDialog(DialogType.CANCEL_SUBSCRIPTION, "Are you sure you want to cancel subscription?",
                        true, this::sendSubscriptionCancellation,
                        null);
            } else {
                String paymentProcessor = getActiveSubscriptionProcessor();
                if (!TextUtils.isEmpty(getExistingGooglePlaySubscriptionId()) ||
                        (!TextUtils.isEmpty(paymentProcessor) &&
                                (paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) ||
                                        paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly))))) {
                    Intent googlePlayStoreCancelIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(currentActivity.getString(R.string.google_play_store_subscriptions_url)));
                    currentActivity.startActivity(googlePlayStoreCancelIntent);
                } else {
                    if ("CCAvenue".equalsIgnoreCase(paymentProcessor)) {
                        showDialog(DialogType.CANCEL_SUBSCRIPTION, "Are you sure you want to cancel subscription?",
                                true, this::sendSubscriptionCancellation,
                                null);
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

    public void rotateToLandscape() {
        if (currentActivity != null) {
            currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public void restrictLandscapeOnly() {
        if (currentActivity != null) {
            currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    public void unrestrictPortraitOnly() {
        if (currentActivity != null) {
            currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public void getAppCMSSignedURL(String filmId,
                                   Action1<AppCMSSignedURLResult> readyAction) {
        if (currentContext != null) {
            if (shouldRefreshAuthToken()) {
                refreshIdentity(getRefreshToken(), () -> {
                    String url = currentContext.getString(R.string.app_cms_signed_url_api_url,
                            appCMSMain.getApiBaseUrl(),
                            filmId,
                            appCMSSite.getGist().getSiteInternalName());
                    GetAppCMSSignedURLAsyncTask.Params params = new GetAppCMSSignedURLAsyncTask.Params.Builder()
                            .authToken(getAuthToken())
                            .url(url)
                            .build();

                    try {
                        new GetAppCMSSignedURLAsyncTask(appCMSSignedURLCall, readyAction).execute(params);
                    } catch (Exception e) {
                        //Log.e(TAG, "Failed to retrieve signed URL: " + e.getMessage());
                    }
                });
            } else {
                String url = currentContext.getString(R.string.app_cms_signed_url_api_url,
                        appCMSMain.getApiBaseUrl(),
                        filmId,
                        appCMSSite.getGist().getSiteInternalName());
                GetAppCMSSignedURLAsyncTask.Params params = new GetAppCMSSignedURLAsyncTask.Params.Builder()
                        .authToken(getAuthToken())
                        .url(url)
                        .build();
                try {
                    new GetAppCMSSignedURLAsyncTask(appCMSSignedURLCall, readyAction).execute(params);
                } catch (Exception e) {
                    //Log.e(TAG, "Failed to retrieve signed URL: " + e.getMessage());
                }
            }
        }
    }

    public void clearPageViewCache() {
        try {
            getPageViewLruCache().evictAll();
        } catch (Exception e) {

        }
    }

    public void clearPageAPIData(Action0 onRefreshFinished, boolean sendRefreshPageDataAction) {
        if (isNetworkConnected()) {
            cancelInternalEvents();
            showLoadingDialog(true);
            try {
                getPageViewLruCache().evictAll();
                getPageAPILruCache().evictAll();
            } catch (Exception e) {
                //
            }
            getUserData((userIdentity) -> {
                try {
                    setLoggedInUser(userIdentity.getUserId());
                    setLoggedInUserEmail(userIdentity.getEmail());
                    setLoggedInUserName(userIdentity.getName());
                    setIsUserSubscribed(userIdentity.isSubscribed());
                } catch (Exception e) {
                    //
                }

                new GetAppCMSAPIAsyncTask(appCMSPageAPICall, null).deleteAll(() -> {
                    if (currentActivity != null && sendRefreshPageDataAction) {
                        Intent refreshPageDataActionIntent = new Intent(AppCMSPresenter.PRESENTER_REFRESH_PAGE_DATA_ACTION);
                        refreshPageDataActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(refreshPageDataActionIntent);
                    }
                    if (onRefreshFinished != null) {
                        onRefreshFinished.call();
                    }
                });
            });
        } else {
            if (onRefreshFinished != null) {
                onRefreshFinished.call();
            }
        }
    }

    public void editWatchlist(final String filmId,
                              final Action1<AppCMSAddToWatchlistResult> resultAction1,
                              boolean add,
                              boolean showToast) {
        if (!isNetworkConnected()) {
            if (!isUserSubscribed()) {
                showDialog(AppCMSPresenter.DialogType.NETWORK, null, false,
                        this::launchBlankPage,
                        null);
                return;
            }
            navigateToDownloadPage(getDownloadPageId(),
                    null, null, false);
            return;
        }

        final String url = currentActivity.getString(R.string.app_cms_edit_watchlist_api_url,
                appCMSMain.getApiBaseUrl(),
                appCMSSite.getGist().getSiteInternalName(),
                getLoggedInUser(),
                filmId);

        //Firebase Successful Login Check on WatchList Add and Remove
        mFireBaseAnalytics.setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_IN);

        try {
            AddToWatchlistRequest request = new AddToWatchlistRequest();
            request.setUserId(getLoggedInUser());
            request.setContentType(currentActivity.getString(R.string.add_to_watchlist_content_type_video));
            request.setPosition(1L);
            if (add) {
                request.setContentId(filmId);

                if (!temporaryWatchlist.contains(filmId)) {
                    temporaryWatchlist.add(filmId);
                }

            } else {
                request.setContentIds(filmId);

                if (temporaryWatchlist.contains(filmId)) {
                    temporaryWatchlist.remove(filmId);
                }
            }

            appCMSAddToWatchlistCall.call(url, getAuthToken(),
                    addToWatchlistResult -> {
                        try {
                            Observable.just(addToWatchlistResult)
                                    .onErrorResumeNext(throwable -> Observable.empty())
                                    .subscribe(resultAction1);
                            if (showToast) {
                                if (add) {
                                    displayCustomToast("Added to Watchlist");
                                } else {
                                    displayCustomToast("Removed from Watchlist");
                                }
                            }
                            populateFilmsInUserWatchlist();
                        } catch (Exception e) {
                            //Log.e(TAG, "addToWatchlistContent: " + e.toString());
                        }
                    }, request, add);
        } catch (Exception e) {
            //Log.e(TAG, "Error editing watchlist: " + e.getMessage());
        }
    }

    private void displayCustomToast(String toastMessage) {
//        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
//            return;
//        }
//        mLastClickTime = SystemClock.elapsedRealtime();
        if (currentActivity != null) {
            LayoutInflater inflater = currentActivity.getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_layout,
                    currentActivity.findViewById(R.id.custom_toast_layout_root));

            TextView customToastMessage = layout.findViewById(R.id.custom_toast_message);
            customToastMessage.setText(toastMessage);

            customToast = new Toast(currentActivity.getApplicationContext());
            customToast.setDuration(Toast.LENGTH_SHORT);
            customToast.setView(layout);
            customToast.setGravity(Gravity.FILL | Gravity.CENTER_VERTICAL, 0, 0);
            customToast.show();
        }
    }

    public void cancelCustomToast() {
        if (customToast != null) {
            customToast.cancel();
        }
    }

    public void reStartDownloadedFile(String filmId, final Action1<UserVideoDownloadStatus> resultAction1,
                                      final ImageButton downloadStatus,int radiusDifference) {

        reStartDownload(filmId, resultAction1);
        updateDownloadingStatus(filmId,downloadStatus,this,resultAction1,getLoggedInUser(),true,radiusDifference,getLoggedInUser());


    }
    public void removeDownloadedFile(String filmId, final Action1<UserVideoDownloadStatus> resultAction1) {
        removeDownloadedFile(filmId);

        appCMSUserDownloadVideoStatusCall.call(filmId, this, resultAction1,
                getLoggedInUser());
    }

    @SuppressWarnings("ConstantConditions")
    private void removeDownloadedFile(String filmId) {
        List<DownloadVideoRealm> downloadVideoRealmList = realmController.getDownloadsById(filmId);
        if (downloadVideoRealmList == null && downloadVideoRealmList.get(0) == null) {
            //
        } else {
            DownloadVideoRealm downloadVideoRealm = null;
            for (DownloadVideoRealm downloadVideoRealm1 : downloadVideoRealmList) {
                if (downloadVideoRealm1.getUserId().trim().equalsIgnoreCase(getLoggedInUser().trim())) {
                    downloadVideoRealm = downloadVideoRealm1;
                }
            }
            if (downloadVideoRealm == null) {
                return;
            } else if (downloadVideoRealm != null && downloadVideoRealmList.size() > 1) {
                realmController.removeFromDB(downloadVideoRealm);
                return;
            }
            downloadManager.remove(downloadVideoRealm.getVideoId_DM());
            downloadManager.remove(downloadVideoRealm.getVideoThumbId_DM());
            downloadManager.remove(downloadVideoRealm.getPosterThumbId_DM());
            downloadManager.remove(downloadVideoRealm.getSubtitlesId_DM());
            realmController.removeFromDB(downloadVideoRealm);
        }
    }

    private void removeDownloadAndLogout() {
        Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
        pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
        getCurrentActivity().sendBroadcast(pageLoadingActionIntent);
        for (DownloadVideoRealm downloadVideoRealm :
                realmController.getAllUnfinishedDownloades(getLoggedInUser())) {
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
        try {
            downloadContentDatumAfterPermissionGranted = null;
            downloadResultActionAfterPermissionGranted = null;

            //Send Firebase Analytics when user is subscribed and user is Logged In
            sendFirebaseLoginSubscribeSuccess();
            if (!hasWriteExternalStoragePermission()) {
                requestDownloadQualityScreen = true;
                askForPermissionToDownloadToExternalStorage(true,
                        contentDatum,
                        resultAction1);
            } else {
                if (navigationPages.get(downloadQualityPage.getPageId()) == null) {
                    Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                    pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                    currentActivity.sendBroadcast(pageLoadingActionIntent);
                    MetaPage metaPage = pageIdToMetaPageMap.get(downloadQualityPage.getPageId());
                    if (metaPage != null) {
                        getAppCMSPage(metaPage.getPageUI(),
                                appCMSPageUIResult -> {
                                    Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                                    stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                    currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                                    if (appCMSPageUIResult != null) {
                                        navigationPages.put(downloadQualityPage.getPageId(), appCMSPageUIResult);
                                        String action = pageNameToActionMap.get(metaPage.getPageName());
                                        if (action != null && actionToPageMap.containsKey(action)) {
                                            actionToPageMap.put(action, appCMSPageUIResult);
                                        }
                                        showDownloadQualityScreen(contentDatum, resultAction1);
                                    }
                                },
                                loadFromFile,
                                false);
                    }
                } else {
                    AppCMSPageAPI apiData = new AppCMSPageAPI();
                    List<Module> moduleList = new ArrayList<>();
                    Module module = new Module();

                    getUserDownloadQualityPref();

                    List<ContentDatum> contentData = new ArrayList<>();
                    ContentDatum contentDatumLocal = new ContentDatum();
                    StreamingInfo streamingInfo = new StreamingInfo();
                    VideoAssets videoAssets = new VideoAssets();
                    List<Mpeg> mpegs = new ArrayList<>();

                    String[] renditionValueArray = currentContext.getResources()
                            .getStringArray(R.array.app_cms_download_quality_array);
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
        } catch (Exception e) {
            //Log.e(TAG, "Failed to display Download Quality Screen");
        }
    }

    private long getRemainingDownloadSize() {
        if (getRealmController() != null) {
            List<DownloadVideoRealm> remainDownloads = getRealmController().getAllUnfinishedDownloades(getLoggedInUser());
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
        return 0L;
    }

    private long getMegabytesAvailable() {
        File storagePath;
        long bytesAvailable = 0L;
        try {
            if (!getUserDownloadLocationPref()) {
                storagePath = Environment.getExternalStorageDirectory();
            } else {
                storagePath = new File(getStorageDirectories(currentActivity)[0]);
            }
            StatFs stat = new StatFs(storagePath.getPath());

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
            } else {
                bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
            }
        } catch (Exception e) {

        }
        return bytesAvailable / (1024L * 1024L);
    }

    private long getMegabytesAvailable(File f) {
        StatFs stat = new StatFs(f.getPath());
        long bytesAvailable = 0L;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
            } else {
                bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
            }
        } catch (Exception e) {

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
        if (!getDownloadOverCellularEnabled() && getActiveNetworkType() == ConnectivityManager.TYPE_MOBILE) {
            showDialog(DialogType.DOWNLOAD_VIA_MOBILE_DISABLED,
                    currentActivity.getString(R.string.app_cms_download_over_cellular_disabled_error_message),
                    false,
                    null,
                    null);
            return;
        }

        downloadContentDatumAfterPermissionGranted = null;
        downloadResultActionAfterPermissionGranted = null;

        //Send Firebase Analytics when user is subscribed and user is Logged In
        sendFirebaseLoginSubscribeSuccess();

        if (!hasWriteExternalStoragePermission()) {
            requestDownloadQualityScreen = false;
            askForPermissionToDownloadToExternalStorage(true,
                    contentDatum,
                    resultAction1);
        } else if (!isMemorySpaceAvailable()) {
            showDialog(DialogType.DOWNLOAD_FAILED, currentActivity.getString(R.string.app_cms_download_failed_error_message), false, null, null);
            //Log.w(TAG, currentActivity.getString(R.string.app_cms_download_failed_error_message));
        } else {

            // Uncomment to allow for Pause/Resume
//            if (isVideoDownloadRunning(contentDatum)) {
//                if (!pauseDownload(contentDatum)) {
//                    Log.e(TAG, "Failed to pause download");
//                }
//                return;
//            } else if (isVideoDownloadPaused(contentDatum)) {
//                if (!resumeDownload(contentDatum)) {
//                    Log.e(TAG, "Failed to resume download");
//                }
//            }
            String downloadURL;
            long file_size = 0L;
            try {
                downloadURL = getDownloadURL(contentDatum);
                URL url = new URL(downloadURL);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                //file_size =urlConnection.getContentLength();  // some of the video url length value go over the max limit of int for 720p  rendition
                file_size = Long.parseLong(urlConnection.getHeaderField("content-length"));
                file_size = ((file_size / 1000) / 1000);

            } catch (Exception e) {
                //Log.e(TAG, "Error trying to download: " + e.getMessage());
            }
            if (isVideoDownloadedByOtherUser(contentDatum.getGist().getId())) {
                createLocalCopyForUser(contentDatum, resultAction1);
            } else if (getMegabytesAvailable() > file_size) {
                try {
                    startDownload(contentDatum, resultAction1);
                } catch (Exception e) {

                }
            } else {
                currentActivity.runOnUiThread(() -> showDialog(DialogType.DOWNLOAD_FAILED, currentActivity.getString(R.string.app_cms_download_failed_error_message), false, null, null));
            }
        }
    }

    private boolean resumeDownload(ContentDatum contentDatum) {
        if (currentContext != null &&
                contentDatum != null &&
                contentDatum.getGist() != null &&
                !TextUtils.isEmpty(contentDatum.getGist().getTitle())) {
            ContentValues pauseDownload = new ContentValues();
            pauseDownload.put("control", 0);

            int updatedRows = 0;
            try {
                updatedRows = currentContext.getContentResolver().update(Uri.parse("content://downloads/my_downloads"),
                        pauseDownload,
                        "title=?",
                        new String[]{contentDatum.getGist().getTitle()});
            } catch (Exception e) {
                Log.e(TAG, "Failed to update control for downloading video");
            }

            return 0 < updatedRows;
        }
        return false;
    }

    private boolean pauseDownload(ContentDatum contentDatum) {
        if (currentContext != null &&
                contentDatum != null &&
                contentDatum.getGist() != null &&
                !TextUtils.isEmpty(contentDatum.getGist().getTitle())) {
            ContentValues pauseDownload = new ContentValues();
            pauseDownload.put("control", 1);

            int updatedRows = 0;

            try {
                updatedRows = currentContext.getContentResolver().update(Uri.parse("content://downloads/my_downloads"),
                        pauseDownload,
                        "title=?",
                        new String[]{contentDatum.getGist().getTitle()});
            } catch (Exception e) {
                Log.e(TAG, "Failed to update control for downloading video");
            }
            return 0 < updatedRows;
        }
        return false;
    }

    private int getVideoDownloadStatus(ContentDatum contentDatum) {
        if (contentDatum != null && contentDatum.getGist() != null) {
            Cursor c = null;
            try {
                DownloadManager.Query query = new DownloadManager.Query();
                long videoId = realmController.getDownloadByIdBelongstoUser(contentDatum.getGist().getId(),
                        getLoggedInUser()).getVideoId_DM();
                query.setFilterById(videoId);
                c = downloadManager.query(query);
                if (c != null && c.moveToFirst()) {
                    return c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                }
            } catch (Exception e) {

            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
        return DownloadManager.STATUS_PENDING;
    }

    private boolean isVideoDownloadRunning(ContentDatum contentDatum) {
        return getVideoDownloadStatus(contentDatum) ==
                DownloadManager.STATUS_RUNNING;
    }

    private boolean isVideoDownloadPaused(ContentDatum contentDatum) {
        return getVideoDownloadStatus(contentDatum) ==
                DownloadManager.STATUS_PAUSED;
    }

    private void createLocalCopyForUser(ContentDatum contentDatum,
                                        Action1<UserVideoDownloadStatus> resultAction1) {
        currentActivity.runOnUiThread(() -> {
            showToast(
                    currentActivity.getString(R.string.app_cms_download_available_already_message,
                            contentDatum.getGist().getTitle()), Toast.LENGTH_LONG);
            DownloadVideoRealm videoDownloaded = getVideoDownloadedByOtherUser(contentDatum.getGist().getId());
            DownloadVideoRealm downloadVideoRealm = videoDownloaded.createCopy();
            try {
                downloadVideoRealm.setVideoIdDB(getStreamingId(videoDownloaded.getVideoTitle()));
            } catch (Exception e) {
                //Log.e(TAG, e.getMessage());
                downloadVideoRealm.setVideoIdDB(videoDownloaded.getVideoId() + getCurrentTimeStamp());
            }
            downloadVideoRealm.setWatchedTime(contentDatum.getGist().getWatchedTime());
            downloadVideoRealm.setUserId(getLoggedInUser());
            realmController.addDownload(downloadVideoRealm);
            appCMSUserDownloadVideoStatusCall.call(videoDownloaded.getVideoId(), this,
                    resultAction1, getLoggedInUser());
        });
    }

    private void createLocalEntry(long enqueueId,
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

            try {
                if (!TextUtils.isEmpty(downloadVideoRealm.getVideoTitle())) {
                    downloadVideoRealm.setVideoIdDB(getStreamingId(downloadVideoRealm.getVideoTitle()));
                } else if (!TextUtils.isEmpty(contentDatum.getGist().getId())) {
                    downloadVideoRealm.setVideoIdDB(contentDatum.getGist().getId() + getCurrentTimeStamp());
                }
            } catch (Exception e) {
                //Log.e(TAG, e.getMessage());
                downloadVideoRealm.setVideoIdDB(downloadVideoRealm.getVideoId() + getCurrentTimeStamp());
            }

            downloadVideoRealm.setLocalURI(downloadedMediaLocalURI(enqueueId));

            if (ccEnqueueId != 0 && contentDatum.getGist().getId() != null) {
                downloadVideoRealm.setSubtitlesId_DM(ccEnqueueId);
                downloadVideoRealm.setSubtitlesFileURL(getClosedCaptionsPath(contentDatum.getGist().getId()));
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
            downloadVideoRealm.setUserId(getLoggedInUser());

        }
        realmController.addDownload(downloadVideoRealm);

    }

    private void clearSubscriptionPlans() {
        if (realmController != null) {
            try {
                realmController.deleteSubscriptionPlans();
            } catch (Exception e) {

            }
        }
    }

    private void createSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        if (realmController != null) {
            try {
                realmController.addSubscriptionPlan(subscriptionPlan);
            } catch (Exception e) {

            }
        }
    }

    @SuppressWarnings("unused")
    public List<SubscriptionPlan> getExistingSubscriptionPlans() {
        List<SubscriptionPlan> subscriptionPlans = new ArrayList<>();
        if (realmController != null) {
            try {
                RealmResults<SubscriptionPlan> subscriptionPlanRealmResults = realmController.getAllSubscriptionPlans();
                subscriptionPlans.addAll(subscriptionPlanRealmResults);
            } catch (Exception e) {

            }
        }
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
    private long downloadVideoImage(String downloadURL, String filename) {

        long enqueueId = 0L;
        try {

            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadURL))
                    .setTitle(filename)
                    .setDescription(filename)
                    .setAllowedOverRoaming(false)
                    .setVisibleInDownloadsUi(false)
                    .setShowRunningNotification(false);


//
            if (getUserDownloadLocationPref()) {
                downloadRequest.setDestinationUri(Uri.fromFile(new File(getSDCardPath(currentActivity, "thumbs"),
                        filename + MEDIA_SURFIX_JPG)));
            } else {
                downloadRequest.setDestinationInExternalFilesDir(currentActivity, "thumbs",
                        filename + MEDIA_SURFIX_JPG);
            }
            enqueueId = downloadManager.enqueue(downloadRequest);


        } catch (Exception e) {
            //Log.e(TAG, "Error downloading video image " + downloadURL + ": " + e.getMessage());
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
    private long downloadPosterImage(String downloadURL, String filename) {

        long enqueueId = 0L;
        try {

            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadURL))
                    .setTitle(filename)
                    .setDescription(filename)
                    .setAllowedOverRoaming(false)
                    .setVisibleInDownloadsUi(false)
                    .setShowRunningNotification(false);

            if (getUserDownloadLocationPref()) {
                downloadRequest.setDestinationUri(Uri.fromFile(new File(getSDCardPath(currentActivity, "posters"),
                        filename + MEDIA_SURFIX_JPG)));
            } else {
                downloadRequest.setDestinationInExternalFilesDir(currentActivity, "posters",
                        filename + MEDIA_SURFIX_JPG);
            }

            enqueueId = downloadManager.enqueue(downloadRequest);


        } catch (Exception e) {
            //Log.e(TAG, "Error downloading poster image for download " + downloadURL + ": " + e.getMessage());
        }

        return enqueueId;
    }

    private long downloadVideoSubtitles(String downloadURL, String filename) {

        long enqueueId = 0L;
        try {

            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadURL))
                    .setTitle(filename)
                    .setDescription(filename)
                    .setAllowedOverRoaming(false)
                    .setVisibleInDownloadsUi(false)
                    .setShowRunningNotification(false);

            if (getUserDownloadLocationPref()) {
                downloadRequest.setDestinationUri(Uri.fromFile(new File(getSDCardPath(currentActivity, "closedCaptions"),
                        filename + MEDIA_SUFFIX_SRT)));
            } else {
                downloadRequest.setDestinationInExternalFilesDir(currentActivity, "closedCaptions",
                        filename + MEDIA_SUFFIX_SRT);
            }

            enqueueId = downloadManager.enqueue(downloadRequest);

        } catch (Exception e) {
            //Log.e(TAG, "Error downloading video subtitles for download " + downloadURL + ": " + e.getMessage());
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
        if (getRealmController() != null) {
            try {
                List<DownloadVideoRealm> unFinishedVideoList = getRealmController().getAllUnfinishedDownloades(getLoggedInUser());
                return unFinishedVideoList != null && !unFinishedVideoList.isEmpty();
            } catch (Exception e) {

            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public AppCMSStreamingInfoCall getAppCMSStreamingInfoCall() {
        return appCMSStreamingInfoCall;
    }

    private String getStreamingInfoURL(String filmId) {

        return currentActivity.getString(R.string.app_cms_streaminginfo_api_url,
                appCMSMain.getApiBaseUrl(),
                filmId,
                appCMSSite.getGist().getSiteInternalName());
    }

    public String getDownloadedFileSize(String filmId) {

        if (realmController != null) {
            try {
                DownloadVideoRealm downloadVideoRealm = realmController.getDownloadById(filmId);
                if (downloadVideoRealm == null)
                    return "";
                return getDownloadedFileSize(downloadVideoRealm.getVideoSize());
            } catch (Exception e) {

            }
        }
        return "";
    }

    @UiThread
    public DownloadStatus getVideoDownloadStatus(String videoId) {
        if (realmController != null) {
            try {
                DownloadVideoRealm downloadVideoRealm = realmController.getDownloadByIdBelongstoUser(videoId,
                        getLoggedInUser());
                if (downloadVideoRealm != null &&
                        downloadVideoRealm.getVideoId().equalsIgnoreCase(videoId)) {
                    return downloadVideoRealm.getDownloadStatus();
                }
            } catch (Exception e) {

            }
        }
        return DownloadStatus.STATUS_PENDING;
    }

    @UiThread
    public boolean isVideoDownloaded(String videoId) {
        if (realmController != null) {
            try {
                DownloadVideoRealm downloadVideoRealm = realmController.getDownloadByIdBelongstoUser(videoId,
                        getLoggedInUser());
                return downloadVideoRealm != null &&
                        downloadVideoRealm.getVideoId().equalsIgnoreCase(videoId) &&
                        downloadVideoRealm.getDownloadStatus() == DownloadStatus.STATUS_COMPLETED;
            } catch (Exception e) {

            }
        }
        return false;
    }

    @UiThread
    private boolean isVideoDownloadedByOtherUser(String videoId) {
        if (realmController != null) {
            try {
                DownloadVideoRealm downloadVideoRealm = realmController.getDownloadById(videoId);
                return downloadVideoRealm != null && downloadVideoRealm.getVideoId().equalsIgnoreCase(videoId);
            } catch (Exception e) {

            }
        }
        return false;
    }

    @UiThread
    private DownloadVideoRealm getVideoDownloadedByOtherUser(String videoId) {
        if (realmController != null) {
            try {
                return realmController.getDownloadById(videoId);
            } catch (Exception e) {

            }
        }
        return null;
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

    @SuppressWarnings("unused")
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

    private void startDownload(ContentDatum contentDatum,
                               Action1<UserVideoDownloadStatus> resultAction1) {
        refreshVideoData(contentDatum.getGist().getId(), updateContentDatum -> {
            if (updateContentDatum != null &&
                    updateContentDatum.getGist().getId() != null) {
                getAppCMSSignedURL(updateContentDatum.getGist().getId(), appCMSSignedURLResult -> currentActivity.runOnUiThread(() -> {
                    if (appCMSSignedURLResult != null) {
                        try {

                            long enqueueId;

                            if (updateContentDatum.getStreamingInfo() == null) { // This will handle the case if we get video streaming info null at Video detail page.

                                String url = getStreamingInfoURL(updateContentDatum.getGist().getId());

                                GetAppCMSStreamingInfoAsyncTask.Params param = new GetAppCMSStreamingInfoAsyncTask.Params.Builder().url(url).build();

                                new GetAppCMSStreamingInfoAsyncTask(appCMSStreamingInfoCall, appCMSStreamingInfo -> {
                                    if (appCMSStreamingInfo != null) {
                                        updateContentDatum.setStreamingInfo(appCMSStreamingInfo.getStreamingInfo());
                                    }
                                }).execute(param);

                                showDialog(DialogType.STREAMING_INFO_MISSING, null, false, null, null);
                                return;
                            }

                            long ccEnqueueId = 0L;
                            if (updateContentDatum.getContentDetails() != null &&
                                    updateContentDatum.getContentDetails().getClosedCaptions() != null &&
                                    !updateContentDatum.getContentDetails().getClosedCaptions().isEmpty() &&
                                    updateContentDatum.getContentDetails().getClosedCaptions().get(0).getUrl() != null) {
                                ccEnqueueId = downloadVideoSubtitles(updateContentDatum.getContentDetails()
                                        .getClosedCaptions().get(0).getUrl(), updateContentDatum.getGist().getId());
                            }

                            String downloadURL;

                            int bitrate = updateContentDatum.getStreamingInfo().getVideoAssets().getMpeg().get(0).getBitrate();

                            downloadURL = getDownloadURL(updateContentDatum);

                            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadURL.replace(" ", "%20")))
                                    .setTitle(updateContentDatum.getGist().getTitle())
                                    .setDescription(updateContentDatum.getGist().getDescription())
                                    .setAllowedOverRoaming(false)
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    .setVisibleInDownloadsUi(false)
                                    .setShowRunningNotification(true);

                            if (getUserDownloadLocationPref()) {
                                downloadRequest.setDestinationUri(Uri.fromFile(new File(getSDCardPath(currentActivity, Environment.DIRECTORY_DOWNLOADS),
                                        updateContentDatum.getGist().getId() + MEDIA_SURFIX_MP4)));
                            } else {
                                downloadRequest.setDestinationInExternalFilesDir(currentActivity, Environment.DIRECTORY_DOWNLOADS,
                                        updateContentDatum.getGist().getId() + MEDIA_SURFIX_MP4);
                            }

                            if (contentDatum == null ||
                                    contentDatum.getGist() == null |
                                            !downloadTaskRunning(contentDatum.getGist().getId())) {
                                enqueueId = downloadManager.enqueue(downloadRequest);


                                long thumbEnqueueId = downloadVideoImage(updateContentDatum.getGist().getVideoImageUrl(),
                                        updateContentDatum.getGist().getId());
                                long posterEnqueueId = downloadPosterImage(updateContentDatum.getGist().getPosterImageUrl(),
                                        updateContentDatum.getGist().getId());

                                /*
                                 * Inserting data in realm data object
                                 */
                                createLocalEntry(
                                        enqueueId,
                                        thumbEnqueueId,
                                        posterEnqueueId,
                                        ccEnqueueId,
                                        updateContentDatum,
                                        downloadURL);

                                showToast(
                                        currentActivity.getString(R.string.app_cms_download_started_message,
                                                updateContentDatum.getGist().getTitle()), Toast.LENGTH_LONG);
                            }

                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            showDialog(DialogType.DOWNLOAD_INCOMPLETE, e.getMessage(), false, null, null);
                        } finally {
                            appCMSUserDownloadVideoStatusCall.call(updateContentDatum.getGist().getId(), this,
                                    resultAction1, getLoggedInUser());
                        }
                    } else {
                        showDialog(DialogType.DOWNLOAD_NOT_AVAILABLE,
                                currentActivity.getString(R.string.app_cms_download_unavailable_error_message),
                                false,
                                null,
                                null);
                    }
                }));
            } else {
                showDialog(DialogType.DOWNLOAD_NOT_AVAILABLE,
                        currentActivity.getString(R.string.app_cms_download_unavailable_error_message),
                        false,
                        null,
                        null);
            }
        });
    }
    public synchronized void reStartDownload(String filmId,final Action1<UserVideoDownloadStatus> resultAction1) {
        String userId = getLoggedInUser();
        DownloadVideoRealm downloadVideoRealm = realmController.getDownloadByIdBelongstoUser(filmId, userId);
        if (downloadVideoRealm == null) {
            System.out.println("  updated rows ar downloadVideoRealm null ");
            return;
        }
        try {


            DownloadStatus status = downloadVideoRealm.getDownloadStatus();
            int id = (int) downloadVideoRealm.getVideoId_DM();
            String mediaURL = downloadVideoRealm.getVideoWebURL();
            //boolean isValidURL = isValidURL(mediaURL);
            System.out.println( " : " + id + " : " + status + " updated rows are =:- " + mediaURL);
            refreshVideoData(downloadVideoRealm.getVideoId(), contentDatum -> {
                if (contentDatum != null && contentDatum.getGist() != null && contentDatum.getGist().getId() != null) {

                    getAppCMSSignedURL(contentDatum.getGist().getId(), appCMSSignedURLResult ->
                    {
                        String downloadURL = getDownloadURL(contentDatum);
                        try {
                            DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadURL.replace(" ", "%20")))
                                    .setTitle(contentDatum.getGist().getTitle())
                                    .setDescription(contentDatum.getGist().getDescription())
                                    .setAllowedOverRoaming(false)
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    .setVisibleInDownloadsUi(false)
                                    .setShowRunningNotification(true);
                            long idDownload = getDownloadManager().enqueue(downloadRequest);
                            currentActivity.runOnUiThread(() -> {

                                DownloadVideoRealm editObj = realmController.getRealm()
                                        .copyFromRealm(realmController.getDownloadByIdBelongstoUser(downloadVideoRealm.getVideoId(),userId));
                                editObj.setVideoId_DM(idDownload);
                                editObj.setVideoWebURL(downloadURL);
                                editObj.setDownloadStatus(DownloadStatus.STATUS_RUNNING);
                                realmController.updateDownload(editObj);

                                appCMSUserDownloadVideoStatusCall.call(filmId, this, resultAction1,
                                        getLoggedInUser());

                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	
    @SuppressWarnings("unused")
    public void checkDownloadCurrentStatus(String filmId, final Action1<UserVideoDownloadStatus> responseAction) {
        appCMSUserDownloadVideoStatusCall
                .call(filmId, this, responseAction, getLoggedInUser());
    }

    public void notifyDownloadHasCompleted() {
        if (currentActivity != null) {
            Intent notifiyDownloadHasCompleted = new Intent(PRESENTER_UPDATE_LISTS_ACTION);
            notifiyDownloadHasCompleted.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(notifiyDownloadHasCompleted);
        }
    }

    @UiThread
    public synchronized void updateDownloadingStatus(String filmId, final ImageView imageView,
                                                     AppCMSPresenter presenter,
                                                     final Action1<UserVideoDownloadStatus> responseAction,
                                                     String userId, boolean isFromDownload,
                                                     int radiusDifference,
                                                     String id) {
        if (!isFromDownload) {
            cancelDownloadIconTimerTask(filmId);
        }
        try {
            /*
             * Timer code can be optimize with RxJava code
             */
            runUpdateDownloadIconTimer = true;
            Timer updateDownloadIconTimer = new Timer();
            long videoId = realmController.getDownloadByIdBelongstoUser(filmId, getLoggedInUser()).getVideoId_DM();


            DownloadTimerTask downloadTimerTask = findDownloadTimerTask(videoId, filmId, id);
            if (downloadTimerTask == null ||
                    downloadTimerTask.cancelled) {
                if (downloadTimerTask != null) {
                    downloadProgressTimerList.remove(downloadTimerTask);
                }

                downloadTimerTask = new DownloadTimerTask(filmId,
                        videoId,
                        runOnUiThreadAction -> {
                            currentActivity.runOnUiThread(runOnUiThreadAction::call);
                        },
                        BaseView.isTablet(currentActivity.getApplicationContext()),
                        this,
                        imageView,
                        responseAction,
                        updateDownloadIconTimer,
                        radiusDifference,
                        id);
                downloadProgressTimerList.add(downloadTimerTask);
            }

            downloadTimerTask.imageView = imageView;

            if (!downloadTimerTask.running) {
                updateDownloadIconTimer.schedule(downloadTimerTask, 0, 1000);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating download status: " + e.getMessage());
        }
    }

    public DownloadTimerTask findDownloadTimerTask(long videoId,
                                                   String filmId,
                                                   String id) {
        if (downloadTaskRunning(filmId)) {
            if (downloadProgressTimerList != null &&
                    !downloadProgressTimerList.isEmpty()) {
                for (DownloadTimerTask downloadProgressTask : downloadProgressTimerList) {
                    if (downloadProgressTask.videoId == videoId &&
                            !TextUtils.isEmpty(id) &&
                            id.equals(downloadProgressTask.id)) {
                        return downloadProgressTask;
                    }
                }
            }
        }

        return null;
    }

    public void updateDownloadTimerTask(String filmId,
                                        String id,
                                        ImageView downloadStatusIcon) {
        if (downloadTaskRunning(filmId)) {
            if (filmId != null &&
                    downloadProgressTimerList != null &&
                    !downloadProgressTimerList.isEmpty()) {
                for (DownloadTimerTask downloadProgressTask : downloadProgressTimerList) {
                    if (downloadProgressTask.filmIdLocal.equals(filmId) &&
                            !TextUtils.isEmpty(id) &&
                            id.equals(downloadProgressTask.id)) {
                        downloadProgressTask.imageView = downloadStatusIcon;
                        try {
                            new Thread(downloadProgressTask).run();
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to re-run download progress task for film: " +
                                    filmId);
                        }
                    }
                }
            }
        }
    }

    public boolean downloadTaskRunning(String filmId) {
        if (filmId != null &&
                downloadProgressTimerList != null &&
                !downloadProgressTimerList.isEmpty()) {
            for (DownloadTimerTask downloadProgressTask : downloadProgressTimerList) {
                if (downloadProgressTask.filmIdLocal.equals(filmId)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void cancelDownloadIconTimerTask(String filmId) {
       /* if (updateDownloadIconTimer != null) {
            runUpdateDownloadIconTimer = false;
            updateDownloadIconTimer.cancel();
            updateDownloadIconTimer.purge();
        }*/
        if (downloadProgressTimerList != null && !downloadProgressTimerList.isEmpty()) {
            int indexToDelete = -1;
            for (int i = 0; i < downloadProgressTimerList.size() && indexToDelete == -1; i++) {
                DownloadTimerTask downloadTimerTask = downloadProgressTimerList.get(i);
                if (filmId == null || downloadTimerTask.filmIdLocal.equals(filmId)) {
                    downloadTimerTask.timer.cancel();
                    downloadTimerTask.timer.purge();
                    indexToDelete = i;
                }
            }
            if (filmId == null) {
                downloadProgressTimerList.clear();
            } else if (indexToDelete != -1) {
                downloadProgressTimerList.remove(indexToDelete);
            }
        }
    }

    public void editHistory(final String filmId,
                            final Action1<AppCMSDeleteHistoryResult> resultAction1, boolean post) {
        final String url = currentActivity.getString(R.string.app_cms_edit_history_api_url,
                appCMSMain.getApiBaseUrl(),
                getLoggedInUser(),
                appCMSSite.getGist().getSiteInternalName(),
                filmId);

        try {
            DeleteHistoryRequest request = new DeleteHistoryRequest();
            request.setUserId(getLoggedInUser());
            request.setContentType(currentActivity.getString(R.string.delete_history_content_type_video));
            request.setPosition(1L);
            if (post) {
                request.setContentId(filmId);
            } else {
                request.setContentIds(filmId);
            }

            appCMSDeleteHistoryCall.call(url, getAuthToken(),
                    appCMSDeleteHistoryResult -> {
                        try {
                            Observable.just(appCMSDeleteHistoryResult)
                                    .onErrorResumeNext(throwable -> Observable.empty())
                                    .subscribe(resultAction1);
                            if (userHistoryData.containsKey(filmId)) {
                                userHistoryData.remove(filmId);
                            }
                        } catch (Exception e) {
                            //Log.e(TAG, "Error deleting history: " + e.getMessage());
                        } finally {
                            sendUpdateHistoryAction();
                        }
                    }, request, post);
        } catch (Exception e) {
            //Log.e(TAG, "Error editing history for " + filmId + ": " + e.getMessage());
        }
    }

    public void clearDownload(final Action1<UserVideoDownloadStatus> resultAction1) {
        showDialog(DialogType.DELETE_ALL_DOWNLOAD_ITEMS,
                currentActivity.getString(R.string.app_cms_delete_all_download_items_message),
                true, () -> {
                    for (DownloadVideoRealm downloadVideoRealm :
                            realmController.getDownloadesByUserId(getLoggedInUser())) {
                        removeDownloadedFile(downloadVideoRealm.getVideoId());
                    }
                    appCMSUserDownloadVideoStatusCall.call("", this,
                            resultAction1, getLoggedInUser());
                    cancelDownloadIconTimerTask(null);
                },
                null);
    }

    public void clearWatchlist(final Action1<AppCMSAddToWatchlistResult> resultAction1) {
        try {
            showDialog(DialogType.DELETE_ALL_WATCHLIST_ITEMS,
                    currentActivity.getString(R.string.app_cms_delete_all_watchlist_items_message),
                    true,
                    () -> makeClearWatchlistRequest(resultAction1),
                    null);
        } catch (Exception e) {
            //Log.e(TAG, "clearWatchlistContent: " + e.toString());
        }
    }

    public void makeClearWatchlistRequest(Action1<AppCMSAddToWatchlistResult> resultAction1) {
        final String url = currentActivity.getString(R.string.app_cms_clear_watchlist_api_url,
                appCMSMain.getApiBaseUrl(),
                appCMSSite.getGist().getSiteInternalName(),
                getLoggedInUser());

        try {
            AddToWatchlistRequest request = new AddToWatchlistRequest();
            request.setUserId(getLoggedInUser());
            request.setContentType(currentActivity.getString(R.string.add_to_watchlist_content_type_video));
            request.setPosition(1L);
            appCMSAddToWatchlistCall.call(url, getAuthToken(),
                    addToWatchlistResult -> {
                        try {
                            populateFilmsInUserWatchlist();
                            Observable.just(addToWatchlistResult)
                                    .onErrorResumeNext(throwable -> Observable.empty())
                                    .subscribe(resultAction1);
                        } catch (Exception e) {
                            //Log.e(TAG, "Error deleting all watchlist items: " + e.getMessage());
                        }
                    }, request, false);
        } catch (Exception e) {
            //Log.e(TAG, "Error clearing watchlist: " + e.getMessage());
            //Log.e(TAG, "clearWatchlistContent: " + e.toString());
        }
    }

    private boolean isMemorySpaceAvailable() {
        //Log.d(TAG, getRemainingDownloadSize() + "  Available storage space:=  "
//                + getMegabytesAvailable(Environment.getExternalStorageDirectory()));
        File storagePath;
        if (getStorageDirectories(currentActivity).length > 0) {
            try {
                storagePath = new File(getStorageDirectories(currentActivity)[0]);
            } catch (Exception e) {
                //Log.e(TAG, "Failed to set appropriate storage path: " +
//                        e.getMessage());
                setUserDownloadLocationPref(false);
                storagePath = Environment.getExternalStorageDirectory();
            }
        } else {
            setUserDownloadLocationPref(false);
            storagePath = Environment.getExternalStorageDirectory();
        }
        return getMegabytesAvailable(storagePath) > getRemainingDownloadSize();
    }

    public boolean isExternalStorageAvailable() {
        return getStorageDirectories(currentActivity).length > 0;
    }

    public boolean downloadsAvailableForApp() {
        if (appCMSMain != null &&
                appCMSMain.getFeatures() != null) {
            return appCMSMain.getFeatures().isMobileAppDownloads();
        }
        return false;
    }

    public void navigateToDownloadPage(String pageId, String pageTitle, String url,
                                       boolean launchActivity) {
        setPlayingVideo(false);

        if (currentActivity != null && !TextUtils.isEmpty(pageId) && downloadsAvailableForApp()) {
            for (Fragment fragment : ((FragmentActivity) currentActivity).getSupportFragmentManager().getFragments()) {
                if (fragment instanceof AppCMSMoreFragment) {
                    ((AppCMSMoreFragment) fragment).sendDismissAction();
                }
            }
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            if (appCMSPageUI == null) {
                MetaPage metaPage = pageIdToMetaPageMap.get(pageId);
                if (metaPage != null) {
                    getAppCMSPage(metaPage.getPageUI(),
                            appCMSPageUIResult -> {
                                if (appCMSPageUIResult != null) {
                                    navigationPages.put(metaPage.getPageId(), appCMSPageUIResult);
                                    String action = pageNameToActionMap.get(metaPage.getPageName());
                                    if (action != null && actionToPageMap.containsKey(action)) {
                                        actionToPageMap.put(action, appCMSPageUIResult);
                                    }

                                    navigateToDownloadPage(pageId, pageTitle, url, launchActivity);
                                }
                            },
                            !isNetworkConnected(),
                            false);
                }
            } else {

                AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
                appCMSPageAPI.setId(pageId);

                List<Module> moduleList = new ArrayList<>();
                Module module = new Module();

                Settings settings = new Settings();

                settings.setHideDate(true);
                settings.setHideTitle(false);
                settings.setLazyLoad(false);

                List<ContentDatum> contentData = new ArrayList<>();
                for (DownloadVideoRealm downloadVideoRealm : realmController.getDownloadesByUserId(getLoggedInUser())) {
                    contentData.add(downloadVideoRealm.convertToContentDatum(getLoggedInUser()));
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
                if (isUserLoggedIn()) {
                    for (Module module1 : appCMSPageAPI.getModules()) {
                        if (jsonValueKeyMap.get(module1.getModuleType()) ==
                                AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY) {

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
                                    updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                    currentActivity.sendBroadcast(updatePageIntent);
                                    dismissOpenDialogs(null);
                                }
                            }

                            Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter
                                    .PRESENTER_STOP_PAGE_LOADING_ACTION);
                            stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                            currentActivity.sendBroadcast(stopPageLoadingActionIntent);
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
                            downloadPageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                            currentActivity.sendBroadcast(downloadPageIntent);
                        }
                    }
                }
            }
        }
    }

    public void launchUpgradeAppActivity() {
        if (platformType == PlatformType.ANDROID && !cancelAllLoads) {
            try {
                Intent upgradeIntent = new Intent(currentActivity, AppCMSUpgradeActivity.class);
                currentActivity.startActivity(upgradeIntent);
            } catch (Exception e) {
                //Log.e(TAG, "DialogType launching Mobile DialogType Activity");
            }
        } else if (platformType == PlatformType.TV) {
            try {
                //
            } catch (Exception e) {
                //Log.e(TAG, "DialogType launching TV DialogType Activity");
            }
        }
    }

    private SemVer getInstalledAppSemVer() {
        SemVer semVer = null;
        if (currentActivity != null) {
            String currentApplicationVersion = currentActivity.getString(R.string.app_cms_app_version);
            semVer = getSemVer(currentApplicationVersion);
        }
        return semVer;
    }

    private SemVer getSemVer(String applicationVersion) {
        SemVer semVer = new SemVer();
        semVer.parse(applicationVersion);
        return semVer;
    }

    public boolean isAppUpgradeAvailable() {
        try {
            SemVer installAppSemVer = getInstalledAppSemVer();
            SemVer latestAppSemVer = new SemVer();
            latestAppSemVer.parse(appCMSMain.getAppVersions().getAndroidAppVersion().getLatest());

            if (installAppSemVer.major > latestAppSemVer.major) {
                return false;
            }

            return !(installAppSemVer.major == latestAppSemVer.major &&
                    installAppSemVer.minor > latestAppSemVer.minor) &&
                    !(installAppSemVer.major == latestAppSemVer.major && installAppSemVer.minor == latestAppSemVer.minor
                            && installAppSemVer.patch >= latestAppSemVer.patch);

        } catch (Exception e) {
            //Log.e(TAG, "Error attempting to retrieve app version");
        }

        return false;
    }

    public boolean isAppBelowMinVersion() {
        try {
            SemVer installAppSemVer = getInstalledAppSemVer();
            SemVer minAppSemVer = new SemVer();
            minAppSemVer.parse(appCMSMain.getAppVersions().getAndroidAppVersion().getMinimum());

            if (installAppSemVer.major > minAppSemVer.major) {
                return false;
            }

            return !(installAppSemVer.major == minAppSemVer.major &&
                    installAppSemVer.minor > minAppSemVer.minor) &&
                    !(installAppSemVer.major == minAppSemVer.major && installAppSemVer.minor == minAppSemVer.minor
                            && installAppSemVer.patch >= minAppSemVer.patch);

        } catch (Exception e) {
            //Log.e(TAG, "Error attempting to retrieve app version");
        }

        return false;
    }

    @SuppressWarnings("unused")
    public void retrieveCurrentAppVersion() {
        if (currentActivity != null) {
            try {
                Observable
                        .fromCallable(() -> {
                            String currentAppVersion = "";
                            try {
                                currentAppVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" +
                                        currentActivity.getPackageName() +
                                        "&hl=en")
                                        .timeout(30000)
                                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                                        .referrer("http://www.google.com")
                                        .get()
                                        .select("div[itemprop=softwareVersion]")
                                        .first()
                                        .ownText();
                            } catch (Exception e) {
                                //Log.e(TAG, "Failed to receive ");
                            }
                            return currentAppVersion;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe((result) -> Observable.just(result)
                                .onErrorResumeNext(throwable -> Observable.empty())
                                .subscribe(this::setGooglePlayAppStoreVersion));
            } catch (Exception e) {
                //Log.e(TAG, "Failed to refresh app version: " + e.getMessage());
            }
        }
    }

    public void clearHistory(final Action1<AppCMSDeleteHistoryResult> resultAction1) {
        try {
            showDialog(DialogType.DELETE_ALL_HISTORY_ITEMS,
                    currentActivity.getString(R.string.app_cms_delete_all_history_items_message),
                    true,
                    () -> makeClearHistoryRequest(resultAction1),
                    null);
        } catch (Exception e) {
            //Log.e(TAG, "clearHistoryContent: " + e.toString());
        }
    }

    public void makeClearHistoryRequest(Action1<AppCMSDeleteHistoryResult> resultAction1) {
        final String url = currentActivity.getString(R.string.app_cms_clear_history_api_url,
                appCMSMain.getApiBaseUrl(),
                getLoggedInUser(),
                appCMSSite.getGist().getSiteInternalName());
        try {
            DeleteHistoryRequest request = new DeleteHistoryRequest();
            request.setUserId(getLoggedInUser());
            request.setContentType(currentActivity.getString(R.string.delete_history_content_type_video));
            request.setPosition(1L);
            appCMSDeleteHistoryCall.call(url, getAuthToken(),
                    appCMSDeleteHistoryResult -> {
                        try {
                            sendUpdateHistoryAction();
                            Observable.just(appCMSDeleteHistoryResult)
                                    .onErrorResumeNext(throwable -> Observable.empty())
                                    .subscribe(resultAction1);
                            userHistoryData.clear();
                        } catch (Exception e) {
                            //Log.e(TAG, "Error deleting all history items: " + e.getMessage());
                        }
                    }, request, false);
        } catch (Exception e) {
            //Log.e(TAG, "Error clearing history: " + e.getMessage());
            //Log.e(TAG, "clearHistoryContent: " + e.toString());
        }
    }

    public void getWatchlistData(final Action1<AppCMSWatchlistResult> appCMSWatchlistResultAction) {
        if (currentActivity != null) {
            MetaPage watchlistMetaPage = actionTypeToMetaPageMap.get(AppCMSActionType.WATCHLIST_PAGE);
            AppCMSPageUI appCMSPageUI = navigationPages.get(watchlistMetaPage.getPageId());
            getWatchlistPageContent(appCMSMain.getApiBaseUrl(),
                    watchlistMetaPage.getPageAPI(),
                    appCMSSite.getGist().getSiteInternalName(),
                    true,
                    getPageId(appCMSPageUI),
                    new AppCMSWatchlistAPIAction(true,
                            false,
                            true,
                            appCMSPageUI,
                            watchlistMetaPage.getPageId(),
                            watchlistMetaPage.getPageId(),
                            watchlistMetaPage.getPageName(),
                            watchlistMetaPage.getPageId(),
                            false,
                            null) {
                        @Override
                        public void call(AppCMSWatchlistResult appCMSWatchlistResult) {
                            if (appCMSWatchlistResult != null) {
                                Observable.just(appCMSWatchlistResult)
                                        .onErrorResumeNext(throwable -> Observable.empty())
                                        .subscribe(appCMSWatchlistResultAction);
                            } else {
                                Observable.just((AppCMSWatchlistResult) null)
                                        .onErrorResumeNext(throwable -> Observable.empty())
                                        .subscribe(appCMSWatchlistResultAction);
                            }
                        }
                    });
        }
    }

    public void navigateToWatchlistPage(String pageId, String pageTitle, String url,
                                        boolean launchActivity) {

        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            if (appCMSPageUI == null) {
                MetaPage metaPage = pageIdToMetaPageMap.get(pageId);
                if (metaPage != null) {
                    getAppCMSPage(metaPage.getPageUI(),
                            appCMSPageUIResult -> {
                                if (appCMSPageUIResult != null) {
                                    navigationPages.put(metaPage.getPageId(), appCMSPageUIResult);
                                    String action = pageNameToActionMap.get(metaPage.getPageName());
                                    if (action != null && actionToPageMap.containsKey(action)) {
                                        actionToPageMap.put(action, appCMSPageUIResult);
                                    }
                                    navigateToWatchlistPage(pageId, pageTitle, url, launchActivity);
                                }
                            },
                            loadFromFile,
                            false);
                }
            } else {

                if (platformType.equals(PlatformType.TV) && !isNetworkConnected()) {
                    RetryCallBinder retryCallBinder = getRetryCallBinder(url, null,
                            pageTitle, null,
                            null, false, null, WATCHLIST_RETRY_ACTION);
                    retryCallBinder.setPageId(pageId);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(currentActivity.getString(R.string.retry_key), true);
                    bundle.putBoolean(currentActivity.getString(R.string.register_internet_receiver_key), true);
                    bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
                    Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
                    args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
                    args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                    currentActivity.sendBroadcast(args);
                    return;
                }
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

                                AppCMSPageAPI pageAPI;
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
                                        watchlistPageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                        currentActivity.sendBroadcast(watchlistPageIntent);
                                    }
                                }

                                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter
                                        .PRESENTER_STOP_PAGE_LOADING_ACTION);
                                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                currentActivity.sendBroadcast(stopPageLoadingActionIntent);

                            }
                        });
            }
        }
    }

    /**
     * Method launches the autoplay screen
     *
     * @param pageId    pageId to get the Page UI from navigationPages
     * @param pageTitle pageTitle
     * @param url       url of the API which gets the VideoDetails
     * @param binder    binder to share data
     * @param action1
     */
    private void navigateToAutoplayPage(final String pageId,
                                        final String pageTitle,
                                        String url,
                                        final AppCMSVideoPageBinder binder, Action1<Object> action1) {

        if (currentActivity != null) {
            final AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            if (appCMSPageUI == null) {
                Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(pageLoadingActionIntent);
                MetaPage metaPage = pageIdToMetaPageMap.get(pageId);
                if (metaPage != null) {
                    getAppCMSPage(metaPage.getPageUI(),
                            appCMSPageUIResult -> {
                                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                                if (appCMSPageUIResult != null) {
                                    navigationPages.put(pageId, appCMSPageUIResult);
                                    String action = pageNameToActionMap.get(metaPage.getPageName());
                                    if (action != null && actionToPageMap.containsKey(action)) {
                                        actionToPageMap.put(action, appCMSPageUIResult);
                                    }

                                    navigateToAutoplayPage(pageId, pageTitle, url, binder, action1);
                                }
                            },
                            loadFromFile,
                            false);
                }
            } else {
                if (!binder.isOffline()) {
                    launchMobileAutoplayActivity(pageId, pageTitle, url, binder, action1, appCMSPageUI);
                } else {
                    AppCMSPageAPI pageAPI = binder.getContentData().convertToAppCMSPageAPI(
                            currentActivity.getString(R.string.app_cms_page_autoplay_module_key));

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
                                binder, action1);
                    }
                }
            }
        }
    }

    public void launchMobileAutoplayActivity(String pageId, String pageTitle, String url, AppCMSVideoPageBinder binder, Action1<Object> action1, AppCMSPageUI appCMSPageUI) {
        GetAppCMSVideoDetailAsyncTask.Params params =
                new GetAppCMSVideoDetailAsyncTask.Params.Builder().url(url)
                        .authToken(getAuthToken()).build();
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
                                        binder,
                                        action1);
                            }
                        } else {
                            //Log.e(TAG, "API issue in VideoDetail call");
                            if (platformType == PlatformType.TV) {
                                action1.call(null);
                            }
                        }
                    } catch (Exception e) {
                        //Log.e(TAG, "Error retrieving video details: " + e.getMessage());
                        if (platformType == PlatformType.TV) {
                            action1.call(null);
                        }
                    }
                }).execute(params);
    }

    private void getWatchlistPageContent(final String apiBaseUrl, String endPoint,
                                         final String siteId,
                                         boolean userPageIdQueryParam, String pageId,
                                         final AppCMSWatchlistAPIAction watchlist) {
        if (currentActivity != null) {
            try {
                String url = currentActivity.getString(R.string.app_cms_refresh_identity_api_url,
                        appCMSMain.getApiBaseUrl(),
                        getRefreshToken());

                appCMSRefreshIdentityCall.call(url, refreshIdentityResponse -> {
                    try {
                        appCMSWatchlistCall.call(
                                currentActivity.getString(R.string.app_cms_watchlist_api_url,
                                        apiBaseUrl, //getLoggedInUser(currentActivity,
                                        siteId,
                                        getLoggedInUser()),
                                getAuthToken(),
                                watchlist);
                    } catch (IOException e) {
                        //Log.e(TAG, "getWatchlistPageContent: " + e.toString());
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    public void getHistoryData(final Action1<AppCMSHistoryResult> appCMSHistoryResultAction) {
        if (currentActivity != null) {
            MetaPage historyMetaPage = actionTypeToMetaPageMap.get(AppCMSActionType.HISTORY_PAGE);
            try {
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
                                    Observable.just(appCMSHistoryResult)
                                            .onErrorResumeNext(throwable -> Observable.empty())
                                            .subscribe(appCMSHistoryResultAction);
                                } else {
                                    Observable.just((AppCMSHistoryResult) null)
                                            .onErrorResumeNext(throwable -> Observable.empty())
                                            .subscribe(appCMSHistoryResultAction);
                                }
                            }
                        });
            } catch (Exception e) {
                //
            }
        }
    }

    public void navigateToHistoryPage(String pageId, String pageTitle, String url,
                                      boolean launchActivity) {

        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            if (appCMSPageUI == null) {
                MetaPage metaPage = pageIdToMetaPageMap.get(pageId);
                if (metaPage != null) {
                    getAppCMSPage(metaPage.getPageUI(),
                            appCMSPageUIResult -> {
                                if (appCMSPageUIResult != null) {
                                    navigationPages.put(metaPage.getPageId(), appCMSPageUIResult);
                                    String action = pageNameToActionMap.get(metaPage.getPageName());
                                    if (action != null && actionToPageMap.containsKey(action)) {
                                        actionToPageMap.put(action, appCMSPageUIResult);
                                    }

                                    navigateToHistoryPage(pageId, pageTitle, url, launchActivity);
                                }
                            },
                            loadFromFile,
                            false);
                }
            } else {

                if (platformType.equals(PlatformType.TV) && !isNetworkConnected()) {
                    RetryCallBinder retryCallBinder = getRetryCallBinder(url, null,
                            pageTitle, null,
                            null, false, null, HISTORY_RETRY_ACTION);
                    retryCallBinder.setPageId(pageId);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(currentActivity.getString(R.string.retry_key), true);
                    bundle.putBoolean(currentActivity.getString(R.string.register_internet_receiver_key), true);
                    bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
                    Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
                    args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
                    args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                    currentActivity.sendBroadcast(args);
                    return;
                }

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
                                pushActionInternalEvents(this.pageId + BaseView.isLandscape(currentActivity));

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
                                        historyPageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                        currentActivity.sendBroadcast(historyPageIntent);
                                    }
                                }

                                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter
                                        .PRESENTER_STOP_PAGE_LOADING_ACTION);
                                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                            }
                        });
            }
        }
    }

    private void getHistoryPageContent(final String apiBaseUrl, String endPoint, final String siteiD,
                                       boolean userPageIdQueryParam, String pageId,
                                       final AppCMSHistoryAPIAction history) {

        if (shouldRefreshAuthToken()) {
            callRefreshIdentity(() -> {
                try {
                    appCMSHistoryCall.call(currentActivity.getString(R.string.app_cms_history_api_url,
                            apiBaseUrl, getLoggedInUser(), siteiD,
                            getLoggedInUser()),
                            getAuthToken(),
                            history);
                } catch (IOException | NullPointerException e) {
                    //Log.e(TAG, "getHistoryPageContent: " + e.toString());
                }
            });
        } else {

            try {
                String url = currentActivity.getString(R.string.app_cms_refresh_identity_api_url,
                        appCMSMain.getApiBaseUrl(),
                        getRefreshToken());

                appCMSRefreshIdentityCall.call(url, refreshIdentityResponse -> {
                    try {
                        appCMSHistoryCall.call(currentActivity.getString(R.string.app_cms_history_api_url,
                                apiBaseUrl, getLoggedInUser(), siteiD,
                                getLoggedInUser()),
                                getAuthToken(),
                                history);
                    } catch (Exception e) {
                        //Log.e(TAG, "getHistoryPageContent: " + e.toString());
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    public void navigateToSubscriptionPlansPage(boolean loginFromNavPage) {
        this.loginFromNavPage = loginFromNavPage;
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

            /*
              send events when click on plan page
             */

            Bundle bundle = new Bundle();
            String FIREBASE_SCREEN_BEGIN_CHECKOUT = "begin_checkout";
            bundle.putString(FIREBASE_SCREEN_BEGIN_CHECKOUT, FIREBASE_SCREEN_BEGIN_CHECKOUT);
            sendFirebaseSelectedEvents(FIREBASE_SCREEN_BEGIN_CHECKOUT, bundle);

            if (!launchSuccess) {
                //Log.e(TAG, "Failed to launch page: " + subscriptionPage.getPageName());
                launchBlankPage();
            }
        }
    }

    public void checkForExistingSubscription(boolean showErrorDialogIfSubscriptionExists) {
        //Log.d(TAG, "Checking for existing Google Play subscription");

        if (!isNetworkConnected()) {
            return;
        }

        if (!isUserSubscribed()) {
            setActiveSubscriptionPrice(null);
            setActiveSubscriptionId(null);
            setActiveSubscriptionSku(null);
            setActiveSubscriptionCountryCode(null);
            setActiveSubscriptionPlanName(null);
            setActiveSubscriptionReceipt(null);
        }

        if (currentActivity != null) {
            Bundle activeSubs;
            try {
                if (inAppBillingService != null) {
                    //Log.d(TAG, "InApp Billing Service is non-null");

                    //Log.d(TAG, "Retrieving purchase data");

                    activeSubs = inAppBillingService.getPurchases(3,
                            currentActivity.getPackageName(),
                            "subs",
                            null);
                    ArrayList<String> subscribedItemList = activeSubs.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

                    if (subscribedItemList != null && !subscribedItemList.isEmpty()) {
                        boolean subscriptionExpired = false;
                        boolean subscriptionAutoRenewing = false;
                        for (int i = 0; i < subscribedItemList.size(); i++) {
                            try {
                                //Log.d(TAG, "Examining existing subscription data");
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
                                    setExistingGooglePlaySubscriptionDescription(skuDetails.getTitle());

                                    setExistingGooglePlaySubscriptionPrice(skuDetails.getPrice());

                                    subscriptionExpired = existingSubscriptionExpired(inAppPurchaseData, skuDetails);
                                }

                                setExistingGooglePlaySubscriptionId(inAppPurchaseData.getProductId());

                                if (inAppPurchaseData.isAutoRenewing()) {
                                    subscriptionAutoRenewing = true;
                                }

                                if (inAppPurchaseData.isAutoRenewing() || !subscriptionExpired) {
                                    if (TextUtils.isEmpty(skuToPurchase) || skuToPurchase.equals(inAppPurchaseData.getProductId())) {
                                        setActiveSubscriptionReceipt(subscribedItemList.get(i));
                                        //Log.d(TAG, "Restoring purchase for SKU: " + skuToPurchase);
                                    } else {
                                        setActiveSubscriptionReceipt(null);
                                        if (!TextUtils.isEmpty(skuToPurchase)) {
                                            //Log.d(TAG, "Making purchase for another subscription: " + skuToPurchase);
                                        }
                                    }
                                    //Log.d(TAG, "Set active subscription: " + inAppPurchaseData.getProductId());

                                    //Log.d(TAG, "Making restore purchase call with token: " + inAppPurchaseData.getPurchaseToken());
                                    String restorePurchaseUrl = currentContext.getString(R.string.app_cms_restore_purchase_api_url,
                                            appCMSMain.getApiBaseUrl(),
                                            appCMSSite.getGist().getSiteInternalName());
                                    try {
                                        if (showErrorDialogIfSubscriptionExists) {
                                            showLoadingDialog(true);
                                            showToast(currentActivity.getString(R.string.checking_for_existing_subscription_toast_message), Toast.LENGTH_SHORT);

                                        }
                                        final String restoreSubscriptionReceipt = subscribedItemList.get(i);
                                        appCMSRestorePurchaseCall.call(apikey,
                                                restorePurchaseUrl,
                                                inAppPurchaseData.getPurchaseToken(),
                                                appCMSSite.getGist().getSiteInternalName(),
                                                (signInResponse) -> {
                                                    if (showErrorDialogIfSubscriptionExists) {
                                                        showLoadingDialog(false);
                                                    }
                                                    //Log.d(TAG, "Retrieved restore purchase call");
                                                    if (signInResponse == null || !TextUtils.isEmpty(signInResponse.getMessage())) {
                                                        if (showErrorDialogIfSubscriptionExists) {
                                                            showToast(currentActivity.getString(R.string.existing_subscription_does_not_exist_toast_message), Toast.LENGTH_SHORT);
                                                        }
                                                        //Log.d(TAG, "SignIn response is null or error response is non empty");
                                                        if (!isUserLoggedIn()) {
                                                            if (signInResponse != null) {
                                                                //Log.e(TAG, "Received restore purchase call error: " + signInResponse.getMessage());
                                                            }
                                                            if (showErrorDialogIfSubscriptionExists) {
                                                                showEntitlementDialog(DialogType.EXISTING_SUBSCRIPTION,
                                                                        () -> {
                                                                            setRestoreSubscriptionReceipt(restoreSubscriptionReceipt);
                                                                            sendCloseOthersAction(null, true, false);
                                                                            launchType = LaunchType.LOGIN_AND_SIGNUP;
                                                                            navigateToLoginPage(loginFromNavPage);
                                                                        });
                                                            }
                                                        } else {
                                                            setRestoreSubscriptionReceipt(restoreSubscriptionReceipt);
                                                            if (!purchaseFromRestore) {
                                                                initiateItemPurchase(true);
                                                            }
                                                        }
                                                    } else {
                                                        //Log.d(TAG, "Received a valid signin response");
                                                        if (isUserLoggedIn()) {
                                                            //Log.d(TAG, "User is logged in");
                                                            if (!TextUtils.isEmpty(getLoggedInUser()) &&
                                                                    !TextUtils.isEmpty(signInResponse.getUserId()) &&
                                                                    signInResponse.getUserId().equals(getLoggedInUser())) {

                                                                //Log.d(TAG, "User ID: " + signInResponse.getUserId());
                                                                setRefreshToken(signInResponse.getRefreshToken());
                                                                setAuthToken(signInResponse.getAuthorizationToken());
                                                                setLoggedInUser(signInResponse.getUserId());
                                                                setLoggedInUserName(signInResponse.getName());
                                                                setLoggedInUserEmail(signInResponse.getEmail());
                                                                setIsUserSubscribed(signInResponse.isSubscribed());
                                                                setUserAuthProviderName(signInResponse.getProvider());

                                                                refreshSubscriptionData(() -> {

                                                                }, true);
                                                            } else if (showErrorDialogIfSubscriptionExists) {
                                                                showEntitlementDialog(DialogType.EXISTING_SUBSCRIPTION_LOGOUT,
                                                                        this::logout);
                                                            }
                                                        } else {
                                                            //Log.d(TAG, "User is logged out");
                                                            if (showErrorDialogIfSubscriptionExists) {
                                                                setRefreshToken(signInResponse.getRefreshToken());
                                                                setAuthToken(signInResponse.getAuthorizationToken());
                                                                setLoggedInUser(signInResponse.getUserId());
                                                                sendSignInEmailFirebase();
                                                                setLoggedInUserName(signInResponse.getName());
                                                                setLoggedInUserEmail(signInResponse.getEmail());
                                                                setIsUserSubscribed(signInResponse.isSubscribed());
                                                                setUserAuthProviderName(signInResponse.getProvider());

                                                                refreshSubscriptionData(() -> {

                                                                }, true);

                                                                if (showErrorDialogIfSubscriptionExists) {
                                                                    StringBuilder loggingInWithUserSb =
                                                                            new StringBuilder();
                                                                    loggingInWithUserSb.append(currentActivity.getString(R.string.logging_in_using_existing_subscription));
                                                                    loggingInWithUserSb.append(" ");
                                                                    loggingInWithUserSb.append(signInResponse.getEmail());
                                                                    showToast(loggingInWithUserSb.toString(), Toast.LENGTH_SHORT);
                                                                    finalizeLogin(false,
                                                                            signInResponse.isSubscribed(),
                                                                            false,
                                                                            false);
                                                                    showLoadingDialog(true);
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                    } catch (Exception e) {
                                        //Log.d(TAG, "Error making restore purchase request: " + e.getMessage());
                                        if (showErrorDialogIfSubscriptionExists) {
                                            showEntitlementDialog(DialogType.EXISTING_SUBSCRIPTION,
                                                    () -> {
                                                        sendCloseOthersAction(null, true, false);
                                                        navigateToLoginPage(loginFromNavPage);
                                                    });
                                        }
                                    }
                                } else {
                                    setActiveSubscriptionReceipt(null);
                                }

                            } catch (Exception e) {
                                //Log.e(TAG, "Error parsing Google Play subscription data: " + e.toString());
                            }
                        }

                        setExistingGooglePlaySubscriptionSuspended(subscriptionExpired);
                    }
                }
            } catch (RemoteException e) {
                //Log.e(TAG, "Failed to purchase item with sku: "
//                        + getActiveSubscriptionSku());
            }
        }
    }

    private boolean existingSubscriptionExpired(InAppPurchaseData inAppPurchaseData,
                                                SkuDetails skuDetails) {
        try {
            Instant subscribedPurchaseTimeInstant = Instant.ofEpochMilli(inAppPurchaseData.getPurchaseTime());
            Instant nowTimeInstant = Instant.now();
            ZonedDateTime subscribedPurchaseTime = ZonedDateTime.ofInstant(subscribedPurchaseTimeInstant, ZoneId.systemDefault());
            ZonedDateTime nowTime = ZonedDateTime.ofInstant(nowTimeInstant, ZoneId.systemDefault());
            ZonedDateTime subscriptionPeriodTime = ZonedDateTime.ofInstant(subscribedPurchaseTimeInstant, ZoneId.systemDefault());
            String subscriptionPeriod = skuDetails.getSubscriptionPeriod();
            final String SUBS_PERIOD_REGEX = "P(([0-9]+)[yY])?(([0-9]+)[mM])?(([0-9]+)[wW])?(([0-9]+)[dD])?";
            if (subscriptionPeriod.matches(SUBS_PERIOD_REGEX)) {
                Matcher subscriptionPeriodMatcher = Pattern.compile(SUBS_PERIOD_REGEX).matcher(subscriptionPeriod);
                if (subscriptionPeriodMatcher.find()) {
                    if (subscriptionPeriodMatcher.group(2) != null) {
                        subscriptionPeriodTime = subscriptionPeriodTime.plus(Long.parseLong(subscriptionPeriodMatcher.group(2)),
                                ChronoUnit.YEARS);
                    }
                    if (subscriptionPeriodMatcher.group(4) != null) {
                        subscriptionPeriodTime = subscriptionPeriodTime.plus(Long.parseLong(subscriptionPeriodMatcher.group(4)),
                                ChronoUnit.MONTHS);
                    }
                    if (subscriptionPeriodMatcher.group(6) != null) {
                        subscriptionPeriodTime = subscriptionPeriodTime.plus(Long.parseLong(subscriptionPeriodMatcher.group(6)),
                                ChronoUnit.WEEKS);
                    }
                    if (subscriptionPeriodMatcher.group(8) != null) {
                        subscriptionPeriodTime = subscriptionPeriodTime.plus(Long.parseLong(subscriptionPeriodMatcher.group(8)),
                                ChronoUnit.DAYS);
                    }
                }

                while (subscriptionPeriodTime.toEpochSecond() < nowTime.toEpochSecond() - subscribedPurchaseTime.toEpochSecond()) {
                    subscribedPurchaseTime = subscribedPurchaseTime.plus(subscribedPurchaseTime.toEpochSecond(),
                            ChronoUnit.SECONDS);
                }

                Duration betweenSubscribedTimeAndExpirationTime =
                        Duration.between(subscribedPurchaseTime, subscriptionPeriodTime);

                Duration betweenSubscribedTimeAndNowTime =
                        Duration.between(subscribedPurchaseTime, nowTime);

                return betweenSubscribedTimeAndExpirationTime.compareTo(betweenSubscribedTimeAndNowTime) < 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing end date: " + e.getMessage());
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

    public void navigateToLoginPage(boolean loginFromNavPage) {
        this.loginFromNavPage = loginFromNavPage;
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
                //Log.e(TAG, "Failed to launch page: " + loginPage.getPageName());
                launchBlankPage();
            }
        }
    }

    public void resetPassword(final String email) {
        if (currentActivity != null) {

            if (platformType == PlatformType.TV && !isNetworkConnected()) {
                //open error dialog.
                RetryCallBinder retryCallBinder = getRetryCallBinder(null, null,
                        email, null,
                        null, false,
                        null, RESET_PASSWORD_RETRY
                );
                Bundle bundle = new Bundle();
                bundle.putBoolean(currentActivity.getString(R.string.retry_key), true);
                bundle.putBoolean(currentActivity.getString(R.string.register_internet_receiver_key), true);
                bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
                Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
                args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
                args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(args);
                return;
            }

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

                                if (platformType == PlatformType.TV) {
                                    openTVErrorDialog(currentActivity.getString(R.string.app_cms_reset_password_success_description, email),
                                            currentActivity.getString(R.string.app_cms_forgot_password_title));
                                } else {
                                    showDialog(DialogType.RESET_PASSWORD,
                                            currentActivity.getString(R.string.app_cms_reset_password_success_description, email),
                                            false,
                                            null,
                                            null);
                                }
                            } else if (forgotPasswordResponse != null) {
                                Log.e(TAG, "Failed to reset password for email: " + email);
                                if (platformType == PlatformType.TV) {
                                    openTVErrorDialog(forgotPasswordResponse.getError(),
                                            currentActivity.getString(R.string.app_cms_forgot_password_title));
                                } else {
                                    showDialog(DialogType.RESET_PASSWORD,
                                            forgotPasswordResponse.getError(),
                                            false,
                                            null,
                                            null);
                                }
                            }
                        } catch (Exception e) {
                            //Log.e(TAG, "Error resetting password: " + e.getMessage());
                        }
                    });
        }
    }

    /**
     * this dialog is use for showing a message with OK button in case of TV.
     *
     * @param message
     * @param headerTitle
     */
    public void openTVErrorDialog(String message, String headerTitle) {
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(currentActivity.getString(R.string.retry_key), false);
            bundle.putBoolean(currentActivity.getString(R.string.register_internet_receiver_key), false);
            bundle.putString(currentActivity.getString(R.string.tv_dialog_msg_key), message);
            bundle.putString(currentActivity.getString(R.string.tv_dialog_header_key),
                    headerTitle.toUpperCase()
            );

            Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
            args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
            args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(args);
        } catch (Exception e) {
            //Log.e(TAG, "DialogType launching TV DialogType Activity");
        }
    }

    public void getUserData(final Action1<UserIdentity> userIdentityAction) {
        if (currentActivity != null) {
            if (isUserLoggedIn()) {
                callRefreshIdentity(() -> {
                    try {
                        String url = currentActivity.getString(R.string.app_cms_user_identity_api_url,
                                appCMSMain.getApiBaseUrl(),
                                appCMSSite.getGist().getSiteInternalName());
                        appCMSUserIdentityCall.callGet(url,
                                getAuthToken(),
                                userIdentity -> {
                                    try {
                                        Observable.just(userIdentity)
                                                .onErrorResumeNext(throwable -> Observable.empty())
                                                .subscribe(userIdentityAction);
                                    } catch (Exception e) {
                                        //Log.e(TAG, "Error retrieving user identity information: " + e.getMessage());
                                        Observable.just((UserIdentity) null)
                                                .onErrorResumeNext(throwable -> Observable.empty())
                                                .subscribe(userIdentityAction);
                                    }
                                });
                    } catch (Exception e) {
                        //Log.e(TAG, "Error refreshing identity: " + e.getMessage());
                        Observable.just((UserIdentity) null)
                                .onErrorResumeNext(throwable -> Observable.empty())
                                .subscribe(userIdentityAction);
                    }
                });
            } else {
                try {
                    Observable.just((UserIdentity) null)
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(userIdentityAction);
                } catch (Exception e) {

                }
            }
        } else {
            try {
                Observable.just((UserIdentity) null)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(userIdentityAction);
            } catch (Exception e) {

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
                    userIdentity.setId(getLoggedInUser());
                    userIdentity.setPassword(password);
                    Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                    pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                    currentActivity.sendBroadcast(pageLoadingActionIntent);
                    appCMSUserIdentityCall.callPost(url,
                            getAuthToken(),
                            userIdentity,
                            userIdentityResult -> {
                                sendCloseOthersAction(null, true, false);
                                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                                try {
                                    if (userIdentityResult != null) {
                                        setLoggedInUserName(userIdentityResult.getName());
                                        setLoggedInUserEmail(userIdentityResult.getEmail());
                                        setAuthToken(userIdentityResult.getAuthorizationToken());
                                        setRefreshToken(userIdentityResult.getRefreshToken());
                                    }
                                    sendRefreshPageAction();
                                    userIdentityAction.call(userIdentityResult);
                                } catch (Exception e) {
                                    //Log.e(TAG, "Error get user identity data: " + e.getMessage());
                                }
                            }, errorBody -> {
                                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                                try {
                                    UserIdentity userIdentityError = gson.fromJson(errorBody.string(),
                                            UserIdentity.class);
                                    showToast(userIdentityError.getError(), Toast.LENGTH_LONG);
                                    //Log.e(TAG, "Invalid JSON object: " + e.toString());
                                } catch (Exception e) {
                                    //Log.e(TAG, "Error parsing user identity error: " + e.getMessage());
                                }
                            });
                } catch (Exception e) {
                    //Log.e(TAG, "Error refreshing identity: " + e.getMessage());
                }
            });
        }
    }

    public void updateUserPassword(final String oldPassword, final String newPassword,
                                   final String confirmPassword) {
        String url = currentActivity.getString(R.string.app_cms_change_password_api_url,
                appCMSMain.getApiBaseUrl(), appCMSSite.getGist().getSiteInternalName());
        if (!isNetworkConnected()) {
            showDialog(DialogType.NETWORK, null, false, null, null);
            return;
        }
        if (confirmPassword.equals(newPassword)) {
            UserIdentityPassword userIdentityPassword = new UserIdentityPassword();
            userIdentityPassword.setResetToken(getAuthToken());
            userIdentityPassword.setOldPassword(oldPassword);
            userIdentityPassword.setNewPassword(newPassword);
            Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
            pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(pageLoadingActionIntent);
            appCMSUserIdentityCall.passwordPost(url,
                    getAuthToken(), userIdentityPassword,
                    userIdentityPasswordResult -> {
                        Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                        stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                        try {
                            if (userIdentityPasswordResult != null) {
                                showToast("Password Changed Successfully", Toast.LENGTH_LONG);
                                sendCloseOthersAction(null, true, false);
                            }
                        } catch (Exception e) {
                            //Log.e(TAG, "Error retrieving user password reset result: " + e.getMessage());
                        }
                    }, errorBody -> {
                        Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                        stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                        try {
                            UserIdentityPassword userIdentityError = gson.fromJson(errorBody.string(),
                                    UserIdentityPassword.class);
                            showToast(userIdentityError.getError(), Toast.LENGTH_LONG);
                            //Log.e(TAG, "Invalid JSON object: " + e.toString());
                        } catch (Exception e) {
                            //Log.e(TAG, "Error retrieving user password result: " + e.getMessage());
                        }
                    });
        } else {
            showToast("New password should match with Confirm password.", Toast.LENGTH_LONG);
        }
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("ConstantConditions")
    public void closeSoftKeyboardNoView() {
        if (currentActivity != null) {
            InputMethodManager imm = (InputMethodManager) currentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public void scrollUpWhenSoftKeyboardIsVisible() {
        if (currentActivity != null) {
            currentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    public void initiateAfterLoginAction() {
        if (afterLoginAction != null && shouldLaunchLoginAction) {
            afterLoginAction.call();
            afterLoginAction = null;
            shouldLaunchLoginAction = false;
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
        ViewCreator.clearPlayerView();

        boolean result = false;

        if (currentActivity != null && !TextUtils.isEmpty(pageId) && !cancelAllLoads) {

            if (launched) {
                refreshPages(null, false, 0, 0);
            }

            if (isUserLoggedIn()) {
                refreshUserSubscriptionData(() -> {
                }, true);
            }

            loadingPage = true;
            //Log.d(TAG, "Launching page " + pageTitle + ": " + pageId);
            //Log.d(TAG, "Search query (optional): " + searchQuery);
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            if (appCMSPageUI != null) {
                Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(pageLoadingActionIntent);

                AppCMSPageAPI appCMSPageAPI = null;
                if (platformType == PlatformType.ANDROID) {
                    try {
                        appCMSPageAPI = getPageAPILruCache().get(pageId);
                    } catch (Exception e) {
                        appCMSPageAPI = null;
                    }
                }

                pushActionInternalEvents(pageId
                        + BaseView.isLandscape(currentActivity));

                if (launchActivity) {
                    launchPageActivity(currentActivity,
                            appCMSPageUI,
                            appCMSPageAPI,
                            pageId,
                            pageTitle,
                            pageId,
                            pageIdToPageNameMap.get(pageId),
                            loadFromFile,
                            appbarPresent,
                            fullscreenEnabled,
                            navbarPresent,
                            sendCloseAction,
                            searchQuery,
                            ExtraScreenType.NONE);

                    launched = true;
                } else {
                    Bundle args = getPageActivityBundle(currentActivity,
                            appCMSPageUI,
                            appCMSPageAPI,
                            pageId,
                            pageTitle,
                            pageId,
                            pageIdToPageNameMap.get(pageId),
                            loadFromFile,
                            appbarPresent,
                            fullscreenEnabled,
                            navbarPresent,
                            sendCloseAction,
                            searchQuery,
                            ExtraScreenType.NONE);
                    if (args != null) {
                        Intent updatePageIntent =
                                new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                        updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                args);
                        updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(updatePageIntent);
                        dismissOpenDialogs(null);
                    }

                    launched = true;
                }

                if (appCMSPageAPI == null) {
                    showLoadingDialog(true);
                    refreshPageAPIData(appCMSPageUI, pageId, null, appCMSPageAPI1 -> {
                        loadingPage = false;
                        try {
                            getPageAPILruCache().put(pageId, appCMSPageAPI1);
                        } catch (Exception e) {

                        }
                        cancelInternalEvents();
                        restartInternalEvents();
                        navigationPageData.put(pageId, appCMSPageAPI1);
                        Intent refreshPageDataActionIntent = new Intent(AppCMSPresenter.PRESENTER_REFRESH_PAGE_DATA_ACTION);
                        refreshPageDataActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(refreshPageDataActionIntent);
                        Log.w(TAG, "Sent PRESENTER_REFRESH_PAGE_DATA_ACTION");
                    });
                } else {
                    loadingPage = false;
                    cancelInternalEvents();
                    navigationPageData.put(pageId, appCMSPageAPI);
                }

                //Firebase Event when contact us screen is opened.
            } else {
                Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(pageLoadingActionIntent);
                MetaPage metaPage = pageIdToMetaPageMap.get(pageId);
                if (metaPage != null) {
                    getAppCMSPage(metaPage.getPageUI(),
                            appCMSPageUIResult -> {
                                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                                if (appCMSPageUIResult != null) {
                                    navigationPages.put(pageId, appCMSPageUIResult);
                                    String action = pageNameToActionMap.get(metaPage.getPageName());
                                    if (action != null && actionToPageMap.containsKey(action)) {
                                        actionToPageMap.put(action, appCMSPageUIResult);
                                    }

                                    navigateToPage(pageId,
                                            pageTitle,
                                            url,
                                            launchActivity,
                                            appbarPresent,
                                            fullscreenEnabled,
                                            navbarPresent,
                                            sendCloseAction,
                                            searchQuery);
                                }
                            },
                            loadFromFile,
                            false);
                }
            }
        } else if (isNetworkConnected() &&
                currentActivity != null &&
                !TextUtils.isEmpty(url) &&
                url.contains(currentActivity.getString(
                        R.string.app_cms_page_navigation_contact_us_key))) {
            //Firebase Event when contact us screen is opened.
            sendFireBaseContactUsEvent();
            if (Apptentive.canShowMessageCenter()) {
                if (currentActivity != null) {
                    Apptentive.engage(currentActivity, currentActivity.getString(R.string.app_cms_apptentive_launch_of_message_center_event_name));
                }
                Apptentive.showMessageCenter(currentActivity);
            }
        } else if (!cancelAllLoads && !isNetworkConnected()) {
            showDialog(DialogType.NETWORK, null, false, null, null);
        } else if (!cancelAllLoads) {
            if (launched) {
                //Log.d(TAG, "Resetting page navigation to previous tab");
                setNavItemToCurrentAction(currentActivity);
            } else {
                launchBlankPage();
            }
        } else {
            showLoadingDialog(false);
        }

        return result;
    }

    private void sendFireBaseContactUsEvent() {
        Bundle bundle = new Bundle();
        String FIREBASE_CONTACT_SCREEN = "Contact Us";
        bundle.putString(FIREBASE_SCREEN_VIEW_EVENT, FIREBASE_CONTACT_SCREEN);
        if (getmFireBaseAnalytics() != null)
            getmFireBaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    private void sendFirebaseAnalyticsEvents(String eventValue) {
        if (getmFireBaseAnalytics() == null)
            return;
        Bundle bundle = new Bundle();

        bundle.putString(FIREBASE_SCREEN_VIEW_EVENT, eventValue);

        //Logs an app event.
        getmFireBaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
        //Sets whether analytics collection is enabled for this app on this device.
        getmFireBaseAnalytics().setAnalyticsCollectionEnabled(true);
    }

    public void sendRefreshPageAction() {
        if (currentActivity != null) {
            Intent refreshPageIntent = new Intent(AppCMSPresenter.PRESENTER_REFRESH_PAGE_ACTION);
            refreshPageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(refreshPageIntent);
        }
    }

    public boolean sendCloseOthersAction(String pageName,
                                         boolean closeSelf,
                                         boolean closeOnePage) {
        //Log.d(TAG, "Sending close others action");
        boolean result = false;
        if (currentActivity != null) {
            Intent closeOthersIntent = new Intent(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION);
            closeOthersIntent.putExtra(currentActivity.getString(R.string.close_self_key),
                    closeSelf);
            closeOthersIntent.putExtra(currentActivity.getString(R.string.close_one_page_key),
                    closeOnePage);
            closeOthersIntent.putExtra(currentActivity.getString(R.string.app_cms_closing_page_name),
                    pageName);
            closeOthersIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(closeOthersIntent);
            result = true;
        }
        return result;
    }

    public void sendEnterFullScreenAction() {
        if (currentActivity != null) {
            Intent enterFullScreenAction = new Intent(AppCMSPresenter.PRESENTER_ENTER_FULLSCREEN_ACTION);
            enterFullScreenAction.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(enterFullScreenAction);
        }
    }

    public void sendExitFullScreenAction(boolean relaunchPage) {
        if (currentActivity != null) {
            Intent exitFullScreenAction = new Intent(AppCMSPresenter.PRESENTER_EXIT_FULLSCREEN_ACTION);
            exitFullScreenAction.putExtra(currentActivity.getString(R.string.exit_fullscreen_relaunch_page_extra_key), relaunchPage);
            exitFullScreenAction.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(exitFullScreenAction);
        }
    }

    public void sendKeepScreenOnAction() {
        if (currentActivity != null) {
            Intent keepScreenOnAction = new Intent(AppCMSPresenter.PRESENTER_KEEP_SCREEN_ON_ACTION);
            keepScreenOnAction.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(keepScreenOnAction);
        }
    }

    public void sendClearKeepScreenOnAction() {
        if (currentActivity != null) {
            Intent clearKeepScreenOnAction = new Intent(AppCMSPresenter.PRESENTER_CLEAR_KEEP_SCREEN_ON_ACTION);
            clearKeepScreenOnAction.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(clearKeepScreenOnAction);
        }
    }

    public void sendChromecastDisconnectedAction() {
        if (currentActivity != null) {
            Intent chromecastDisconnected = new Intent(AppCMSPresenter.PRESENTER_CHROMECAST_DISCONNECTED_ACTION);
            chromecastDisconnected.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(chromecastDisconnected);
        }
    }

    public boolean sendDeepLinkAction(Uri deeplinkUri) {
        //Log.d(TAG, "Sending deeplink action");
        boolean result = false;
        if (currentActivity != null) {
            Intent deeplinkIntent = new Intent(AppCMSPresenter.PRESENTER_DEEPLINK_ACTION);
            deeplinkIntent.putExtra(currentActivity.getString(R.string.deeplink_uri_extra_key), deeplinkUri.toString());
            deeplinkIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(deeplinkIntent);
            result = true;
        }
        return result;
    }

    public void sendStopLoadingPageAction(boolean showNetworkErrorDialog,
                                          Action0 retryAction) {
        if (currentActivity != null) {
            Intent stopLoadingPageIntent =
                    new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
            stopLoadingPageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(stopLoadingPageIntent);
            if (!isNetworkConnected() && showNetworkErrorDialog) { // Fix of SVFA-1918
                openDownloadScreenForNetworkError(false, retryAction);
                // fix of SVFA-1435 for build #1.0.35
            }
        }
    }

    private void launchErrorActivity(PlatformType platformType) {
        if (platformType == PlatformType.ANDROID) {
            try {
                if (!cancelLoad && !cancelAllLoads) {
                    Intent errorIntent = new Intent(currentActivity, AppCMSErrorActivity.class);
                    currentActivity.startActivity(errorIntent);
                }
            } catch (Exception e) {
                //Log.e(TAG, "DialogType launching Mobile DialogType Activity");
            }
        } else if (platformType == PlatformType.TV) {
            try {
                Bundle bundle = new Bundle();
                bundle.putBoolean(currentActivity.getString(R.string.retry_key), false);
                Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
                args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
                args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(args);
            } catch (Exception e) {
                //Log.e(TAG, "DialogType launching TV DialogType Activity");
            }
        }
    }

    public void getPagesContent(List<String> modules, Action1<AppCMSPageAPI> readyAction) {
        if (currentActivity != null && modules != null && !modules.isEmpty()) {
            int modulesLength = modules.size();
            StringBuilder modulesParamsSb = new StringBuilder();

            for (int i = 0; i < modulesLength; i++) {
                modulesParamsSb.append(modules.get(i));
                if (i < modulesLength - 1) {
                    modulesParamsSb.append(",");
                }
            }

            String url = currentActivity.getString(R.string.app_cms_pages_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSSite.getGist().getSiteInternalName(),
                    modulesParamsSb.toString());

            if (shouldRefreshAuthToken()) {
                refreshIdentity(getRefreshToken(),
                        () -> {
                            try {
                                GetAppCMSAPIAsyncTask.Params params = new GetAppCMSAPIAsyncTask.Params.Builder()
                                        .urlWithContent(url)
                                        .authToken(getAuthToken())
                                        .build();
                                new GetAppCMSAPIAsyncTask(appCMSPageAPICall,
                                        readyAction)
                                        .executeWithModules(params);
                            } catch (Exception e) {
                                //Log.e(TAG, "Error retrieving page ID content: " + e.getMessage());
                                showDialog(DialogType.NETWORK, null, false, null, null);
                            }
                        });
            } else {
                GetAppCMSAPIAsyncTask.Params params = new GetAppCMSAPIAsyncTask.Params.Builder()
                        .urlWithContent(url)
                        .authToken(getAuthToken())
                        .build();
                new GetAppCMSAPIAsyncTask(appCMSPageAPICall, readyAction).executeWithModules(params);
            }
        }
    }

    public void getPageIdContent(String urlWithContent,
                                 String pageId,
                                 List<String> modules,
                                 boolean usedCachedAPI,
                                 Action1<AppCMSPageAPI> readyAction) {
        if (appCMSPageAPICall == null ||
                appCMSStreamingInfoCall == null ||
                appCMSVideoDetailCall == null) {
            AppCMSAPIComponent appCMSAPIComponent = DaggerAppCMSAPIComponent.builder()
                    .appCMSAPIModule(new AppCMSAPIModule(currentActivity,
                            appCMSMain.getApiBaseUrl(),
                            apikey))
                    .build();
            appCMSPageAPICall = appCMSAPIComponent.appCMSPageAPICall();
            appCMSStreamingInfoCall = appCMSAPIComponent.appCMSStreamingInfoCall();
            appCMSVideoDetailCall = appCMSAPIComponent.appCMSVideoDetailCall();
            urbanAirshipEventPresenter = appCMSAPIComponent.urbanAirshipEventPresenter();
            uaNamedUserEventCall = appCMSAPIComponent.uaNamedUserEventCall();
        }

        AppCMSPageAPI appCMSPageAPI = null;
        if (platformType == PlatformType.ANDROID) {
            try {
                appCMSPageAPI = getPageAPILruCache().get(pageId);
            } catch (Exception e) {
                appCMSPageAPI = null;
            }
        }
        if (appCMSPageAPI == null && pageId != null) {
            if (shouldRefreshAuthToken()) {
                refreshIdentity(getRefreshToken(),
                        () -> {
                            try {
                                String authToken = getAuthToken();
                                try {
                                    if ((!isUserLoggedIn() && usedCachedAPI)) {
                                        authToken = getCachedUserToken();
                                    }
                                } catch (Exception e) {

                                }
                                GetAppCMSAPIAsyncTask.Params params = new GetAppCMSAPIAsyncTask.Params.Builder()
                                        .context(currentContext)
                                        .urlWithContent(urlWithContent)
                                        .authToken(authToken)
                                        .pageId(pageId)
                                        .loadFromFile(platformType != PlatformType.TV && appCMSMain.shouldLoadFromFile())
                                        .modules(modules)
                                        .appCMSPageAPILruCache(getPageAPILruCache())
                                        .build();
                                new GetAppCMSAPIAsyncTask(appCMSPageAPICall,
                                        readyAction)
                                        .execute(params);
                            } catch (Exception e) {
                                //Log.e(TAG, "Error retrieving page ID content: " + e.getMessage());
                                showDialog(DialogType.NETWORK, null, false, null, null);
                            }
                        });
            } else {
                String authToken = getAuthToken();
                try {
                    if (!isUserLoggedIn() && usedCachedAPI) {
                        authToken = getCachedUserToken();
                    }
                } catch (Exception e) {

                }
                GetAppCMSAPIAsyncTask.Params params = new GetAppCMSAPIAsyncTask.Params.Builder()
                        .context(currentContext)
                        .urlWithContent(urlWithContent)
                        .authToken(authToken)
                        .pageId(pageId)
                        .loadFromFile(platformType != PlatformType.TV && appCMSMain.shouldLoadFromFile())
                        .modules(modules)
                        .appCMSPageAPILruCache(getPageAPILruCache())
                        .build();
                new GetAppCMSAPIAsyncTask(appCMSPageAPICall, readyAction).execute(params);
            }
        } else {
            if (readyAction != null) {
                Observable.just(appCMSPageAPI)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(readyAction);
            }
        }
    }

    public boolean isViewPlanPage(String pageId) {
        if (currentActivity != null) {
            String pageName = pageIdToPageNameMap.get(pageId);
            return (!TextUtils.isEmpty(pageName) &&
                    pageName.equals(currentActivity.getString(R.string.app_cms_page_subscription_page_name_key)));
        }
        return false;
    }

    public boolean isDownloadPage(String pageId) {
        if (currentActivity != null) {
            return (downloadPage != null &&
                    !TextUtils.isEmpty(pageId) &&
                    pageId.equals(downloadPage.getPageId()));
        }
        return false;
    }

    public boolean isShowPage(String pageId) {
        if (currentActivity != null) {
            String pageName = pageIdToPageNameMap.get(pageId);
            return (!TextUtils.isEmpty(pageName) &&
                    pageName.equals(currentActivity.getString(R.string.app_cms_page_show_page_name_key)));
        }
        return false;
    }

    public String getPageIdToPageAPIUrl(String pageId) {
        return pageIdToPageAPIUrlMap.get(pageId);
    }

    public String getPageNameToPageAPIUrl(String pageName) {
        return actionToPageAPIUrlMap.get(pageNameToActionMap.get(pageName));
    }

    public boolean isUserLoggedIn() {
        return getLoggedInUser() != null;
    }

    public boolean isUserSubscribed() {
        return getIsUserSubscribed();
    }

    private String getClosedCaptionsPath(String fileName) {
        return currentActivity.getFilesDir().getAbsolutePath() + File.separator
                + "closedCaptions" + File.separator + fileName + MEDIA_SUFFIX_SRT;
    }

    private String getPngPosterPath(String fileName) {
        return currentActivity.getFilesDir().getAbsolutePath() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator + fileName + MEDIA_SURFIX_PNG;
    }

    @SuppressWarnings("unused")
    public String getJpgPosterPath(String fileName) {
        return getBaseDownloadDir() + fileName + MEDIA_SURFIX_JPG;
    }

    @SuppressWarnings("unused")
    public String getMP4VideoPath(String fileName) {
        return getBaseDownloadDir() + fileName + MEDIA_SURFIX_MP4;
    }

    private String getBaseDownloadDir() {
        return currentActivity.getFilesDir().getAbsolutePath() + File.separator
                + Environment.DIRECTORY_DOWNLOADS + File.separator;
    }

    @SuppressWarnings("unused")
    public String getBaseImageDir() {
        return currentActivity.getFilesDir().getAbsolutePath() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator;
    }

    public String getGooglePlayAppStoreVersion() {
        if (appCMSMain != null &&
                appCMSMain.getAppVersions() != null &&
                appCMSMain.getAppVersions().getAndroidAppVersion() != null &&
                !TextUtils.isEmpty(appCMSMain.getAppVersions().getAndroidAppVersion().getLatest())) {
            return appCMSMain.getAppVersions().getAndroidAppVersion().getLatest();
        }

        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(GOOGLE_PLAY_APP_STORE_VERSION_PREF_NAME, 0);
            return sharedPrefs.getString(GOOGLE_PLAY_APP_STORE_VERSION_PREF_NAME, null);
        }
        return null;
    }

    private boolean setGooglePlayAppStoreVersion(String googlePlayAppStoreVersion) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(GOOGLE_PLAY_APP_STORE_VERSION_PREF_NAME, 0);
            return sharedPrefs.edit().putString(GOOGLE_PLAY_APP_STORE_VERSION_PREF_NAME, googlePlayAppStoreVersion).commit();
        }
        return false;
    }

    private String getInstanceId() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(INSTANCE_ID_PREF_NAME, 0);
            return sharedPrefs.getString(INSTANCE_ID_PREF_NAME, null);
        }
        return null;
    }

    public boolean setInstanceId(String instanceId) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(INSTANCE_ID_PREF_NAME, 0);
            return sharedPrefs.edit().putString(INSTANCE_ID_PREF_NAME, instanceId).commit();
        }
        return false;
    }

    public String getLoggedInUser() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(USER_ID_SHARED_PREF_NAME, null);
        }
        return null;
    }

    @SuppressWarnings("unused")
    public String getDownloadPageId(Context context) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(DOWNLOAD_UI_ID, 0);
            return sharedPrefs.getString(DOWNLOAD_UI_ID, null);
        }
        return null;
    }

    @SuppressWarnings("unused")
    public boolean setDownloadPageId(Context context, String url) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(DOWNLOAD_UI_ID, 0);
            return sharedPrefs.edit().putString(DOWNLOAD_UI_ID, url).commit() &&
                    setLoggedInTime();
        }
        return false;
    }

    public String getDownloadPageId() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(DOWNLOAD_UI_ID, 0);
            return sharedPrefs.getString(DOWNLOAD_UI_ID, null);
        }
        return null;
    }

    private void setDownloadPageId(String url) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(DOWNLOAD_UI_ID, 0);
            sharedPrefs.edit().putString(DOWNLOAD_UI_ID, url).apply();
        }
    }

    private boolean setSubscriptionStatus(String subscriptionStatus) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(SUBSCRIPTION_STATUS, 0);
            return sharedPrefs.edit().putString(SUBSCRIPTION_STATUS, subscriptionStatus).commit();
        }
        return false;
    }

    private String getSubscriptionStatus() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(SUBSCRIPTION_STATUS, 0);
            return sharedPrefs.getString(SUBSCRIPTION_STATUS, null);
        }
        return null;
    }

    public boolean setDownloadOverCellularEnabled(boolean downloadOverCellularEnabled) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(DOWNLOAD_OVER_CELLULAR_ENABLED_PREF_NAME, 0);
            return sharedPrefs.edit().putBoolean(DOWNLOAD_OVER_CELLULAR_ENABLED_PREF_NAME, downloadOverCellularEnabled).commit();
        }
        return false;
    }

    public boolean getDownloadOverCellularEnabled() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(DOWNLOAD_OVER_CELLULAR_ENABLED_PREF_NAME, 0);
            return sharedPrefs.getBoolean(DOWNLOAD_OVER_CELLULAR_ENABLED_PREF_NAME, false);
        }
        return false;
    }

    public boolean setActiveNetworkType(int activeNetworkType) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_NETWORK_TYPE_PREF_NAME, 0);
            return sharedPrefs.edit().putInt(ACTIVE_NETWORK_TYPE_PREF_NAME, activeNetworkType).commit();
        }
        return false;
    }

    public int getActiveNetworkType() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_NETWORK_TYPE_PREF_NAME, 0);
            return sharedPrefs.getInt(ACTIVE_NETWORK_TYPE_PREF_NAME, ConnectivityManager.TYPE_MOBILE);
        }
        return ConnectivityManager.TYPE_MOBILE;
    }

    public boolean setPlayingVideo(boolean playingOfflineVideo) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(PLAYING_VIDEO_PREF_NAME, 0);
            return sharedPrefs.edit().putBoolean(PLAYING_VIDEO_PREF_NAME, playingOfflineVideo).commit();
        }

        return false;
    }

    public boolean getPlayingVideo() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(PLAYING_VIDEO_PREF_NAME, 0);
            return sharedPrefs.getBoolean(PLAYING_VIDEO_PREF_NAME, false);
        }
        return false;
    }

    public boolean setCastOverLay() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(CASTING_OVERLAY_PREF_NAME, 0);
            return sharedPrefs.edit().putBoolean(CAST_SHARED_PREF_NAME, true).commit();
        }
        return false;
    }

    public boolean isWaitingFor3rdPartyLogin() {
        return isWaitingFor3rdPartyLogin();
    }

    /**
     * Get The Value of Cast Overlay is shown or not
     *
     * @return
     */
    public boolean isCastOverLayShown() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(CASTING_OVERLAY_PREF_NAME, 0);
            return sharedPrefs.getBoolean(CAST_SHARED_PREF_NAME, false);
        }
        return false;
    }

    /**
     * Set The Value for the Cast Introductory Overlay
     *
     * @param userId
     * @return
     */
    private boolean setLoggedInUser(String userId) {
        if (currentContext != null) {
            //Set the user Id when user is successfully logged_in
            if (mFireBaseAnalytics != null)
                mFireBaseAnalytics.setUserId(userId);
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putString(USER_ID_SHARED_PREF_NAME, userId).commit() &&
                    setLoggedInTime();
        }
        return false;
    }

    private String getCachedUserToken() {
        return cachedAPIUserToken;
    }

    private String getAnonymousUserToken() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ANONYMOUS_AUTH_TOKEN_PREF_NAME, 0);
            return sharedPrefs.getString(ANONYMOUS_AUTH_TOKEN_PREF_NAME, null);
        }
        return null;
    }

    private void setAnonymousUserToken(String anonymousAuthToken) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ANONYMOUS_AUTH_TOKEN_PREF_NAME, 0);
            sharedPrefs.edit().putString(ANONYMOUS_AUTH_TOKEN_PREF_NAME, anonymousAuthToken).apply();
        }
    }

    public boolean isPreferredStorageLocationSDCard() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME, 0);
            return sharedPrefs.getBoolean(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME, false);
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setPreferredStorageLocationSDCard(boolean downloadPref) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putBoolean(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME,
                    downloadPref).commit();
        }
        return false;
    }

    public boolean getUserDownloadLocationPref() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME, 0);
            return sharedPrefs.getBoolean(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME, false);
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setUserDownloadLocationPref(boolean downloadPref) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putBoolean(USER_DOWNLOAD_SDCARD_SHARED_PREF_NAME,
                    downloadPref).commit();
        }
        return false;
    }

    public boolean isDownloadQualityScreenShowBefore() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_DOWNLOAD_QUALITY_SCREEN_SHARED_PREF_NAME, 0);
            return sharedPrefs.getBoolean(getLoggedInUser(), false);
        }
        return false;
    }

    public void setDownloadQualityScreenShowBefore(boolean show) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_DOWNLOAD_QUALITY_SCREEN_SHARED_PREF_NAME, 0);
            sharedPrefs.edit().putBoolean(getLoggedInUser(), show).apply();
        }
    }

    public String getUserDownloadQualityPref() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_DOWNLOAD_QUALITY_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(getLoggedInUser(), "720p");
        }
        return null;
    }

    public void setUserDownloadQualityPref(String downloadQuality) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_DOWNLOAD_QUALITY_SHARED_PREF_NAME, 0);
            sharedPrefs.edit().putString(getLoggedInUser(), downloadQuality).apply();
        }
    }

    public boolean getClosedCaptionPreference() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_CLOSED_CAPTION_PREF_KEY, 0);
            return sharedPrefs.getBoolean(getLoggedInUser(), false);
        }
        return false;
    }

    public void setClosedCaptionPreference(boolean isClosedCaptionOn) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_CLOSED_CAPTION_PREF_KEY, 0);
            sharedPrefs.edit().putBoolean(getLoggedInUser(), isClosedCaptionOn).apply();
        }
    }

    public String getLoggedInUserName() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_NAME_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(USER_NAME_SHARED_PREF_NAME, null);
        }
        return null;
    }

    private void setLoggedInUserName(String userName) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_NAME_SHARED_PREF_NAME, 0);
            sharedPrefs.edit().putString(USER_NAME_SHARED_PREF_NAME, userName).apply();
        }
    }

    public String getUserAuthProviderName() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_AUTH_PROVIDER_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(USER_AUTH_PROVIDER_SHARED_PREF_NAME, null);
        }
        return null;
    }

    private void setUserAuthProviderName(String userAuthProviderName) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_AUTH_PROVIDER_SHARED_PREF_NAME, 0);
            sharedPrefs.edit().putString(USER_AUTH_PROVIDER_SHARED_PREF_NAME, userAuthProviderName).apply();
        }
    }

    public String getLoggedInUserEmail() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_EMAIL_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(USER_EMAIL_SHARED_PREF_NAME, null);
        }
        return null;
    }

    private void setLoggedInUserEmail(String userEmail) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_EMAIL_SHARED_PREF_NAME, 0);
            sharedPrefs.edit().putString(USER_EMAIL_SHARED_PREF_NAME, userEmail).apply();
        }
    }

    private long getLoggedInTime() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_LOGGED_IN_TIME_PREF_NAME, 0);
            return sharedPrefs.getLong(USER_LOGGED_IN_TIME_PREF_NAME, -1L);
        }
        return -1L;
    }

    private boolean setLoggedInTime() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(USER_LOGGED_IN_TIME_PREF_NAME, 0);
            Date now = new Date();
            return sharedPrefs.edit().putLong(USER_LOGGED_IN_TIME_PREF_NAME, now.getTime()).commit();
        }
        return false;
    }

    private String getRefreshToken() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(REFRESH_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(REFRESH_TOKEN_SHARED_PREF_NAME, null);
        }
        return null;
    }

    private void setRefreshToken(String refreshToken) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(REFRESH_TOKEN_SHARED_PREF_NAME, 0);
            sharedPrefs.edit().putString(REFRESH_TOKEN_SHARED_PREF_NAME, refreshToken).apply();
        }
    }

    private String getAuthToken() {
        if (currentContext != null) {
            if (isUserLoggedIn()) {
                SharedPreferences sharedPrefs = currentContext.getSharedPreferences(AUTH_TOKEN_SHARED_PREF_NAME, 0);
                return sharedPrefs.getString(AUTH_TOKEN_SHARED_PREF_NAME, null);
            } else {
                return getAnonymousUserToken();
            }
        }
        return null;
    }

    private void setAuthToken(String authToken) {
        if (currentContext != null) {
            SharedPreferences sharedPreferences = currentContext.getSharedPreferences(AUTH_TOKEN_SHARED_PREF_NAME, 0);
            sharedPreferences.edit().putString(AUTH_TOKEN_SHARED_PREF_NAME, authToken).apply();
        }
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public RealmController getRealmController() {
        return realmController;
    }

    public FirebaseAnalytics getmFireBaseAnalytics() {
        return mFireBaseAnalytics;
    }

    public void setmFireBaseAnalytics(FirebaseAnalytics mFireBaseAnalytics) {
        this.mFireBaseAnalytics = mFireBaseAnalytics;
    }

    public String getFacebookAccessToken() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(FACEBOOK_ACCESS_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPrefs.getString(FACEBOOK_ACCESS_TOKEN_SHARED_PREF_NAME, null);
        }
        return null;
    }

    public String getGoogleAccessToken() {
        if (currentContext != null) {
            SharedPreferences sharedPreferences =
                    currentContext.getSharedPreferences(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPreferences.getString(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME, null);
        }
        return null;
    }

    public String getAppsFlyerKey() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs =
                    currentContext.getSharedPreferences(APPS_FLYER_KEY_PREF_NAME, 0);
            return sharedPrefs.getString(APPS_FLYER_KEY_PREF_NAME, null);
        }
        return null;
    }

    private void setAppsFlyerKey(String appsFlyerKey) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs =
                    currentContext.getSharedPreferences(APPS_FLYER_KEY_PREF_NAME, 0);
            sharedPrefs.edit().putString(APPS_FLYER_KEY_PREF_NAME, appsFlyerKey).apply();
        }
    }

    public void sendAppsFlyerFilmViewingEvent(String primaryCategory, String filmId) {
        if (currentContext != null) {
            AppsFlyerUtils.filmViewingEvent(currentContext, primaryCategory, filmId, this);
        }
    }

    public void showNoNetworkConnectivityToast() {
        if (currentContext != null) {
            displayCustomToast(currentContext.getString(R.string.no_network_connectivity_message));
        }
    }

    public boolean getNetworkConnectedState() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs =
                    currentContext.getSharedPreferences(NETWORK_CONNECTED_SHARED_PREF_NAME, 0);
            return sharedPrefs.getBoolean(NETWORK_CONNECTED_SHARED_PREF_NAME, true);
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setNetworkConnected(boolean networkConnected, String pageId) {
        if (currentContext != null) {
            if (networkConnected) {
                sendOfflineBeaconMessage();
                updateAllOfflineWatchTime();
            }

            SharedPreferences sharedPrefs =
                    currentContext.getSharedPreferences(NETWORK_CONNECTED_SHARED_PREF_NAME, 0);
            String downloadPageId = getDownloadPageId();
            boolean onDownloadPage = false;
            if (!TextUtils.isEmpty(downloadPageId)) {
                onDownloadPage = downloadPageId.equals(pageId);
            }
            if (!networkConnected &&
                    (downloadInProgress || !onDownloadPage) &&
                    downloadsAvailableForApp()) {
                navigateToDownloadPage(getDownloadPageId(),
                        null, null, false);
            } else if (!sharedPrefs.getBoolean(NETWORK_CONNECTED_SHARED_PREF_NAME, true) &&
                    networkConnected &&
                    !getPlayingVideo()) {
                sendCloseOthersAction(null, true, true);
                navigateToHomePage();
            }

            return sharedPrefs.edit().putBoolean(NETWORK_CONNECTED_SHARED_PREF_NAME, networkConnected).commit();
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setWifiConnected(boolean wifiConnected) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(WIFI_CONNECTED_SHARED_PREF_NAME, 0);
            return sharedPrefs.edit().putBoolean(WIFI_CONNECTED_SHARED_PREF_NAME, wifiConnected).commit();
        }
        return false;
    }

    @SuppressWarnings("unused")
    public boolean isWifiConnected() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(WIFI_CONNECTED_SHARED_PREF_NAME, 0);
            return sharedPrefs.getBoolean(WIFI_CONNECTED_SHARED_PREF_NAME, false);
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setFacebookAccessToken(final String facebookAccessToken,
                                          final String facebookUserId,
                                          final String username,
                                          final String email,
                                          boolean forceSubscribed,
                                          boolean refreshSubscriptionData) {
        checkForExistingSubscription(false);

        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_facebook_login_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSSite.getGist().getSiteInternalName());
            appCMSFacebookLoginCall.call(url,
                    facebookAccessToken,
                    facebookUserId,
                    facebookLoginResponse -> {
                        waithingFor3rdPartyLogin = false;
                        if (facebookLoginResponse != null) {
                            if (!TextUtils.isEmpty(facebookLoginResponse.getError())) {
                                showDialog(DialogType.SIGNIN, facebookLoginResponse.getError(), false, null, null);
                                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                            } else {
                                setAuthToken(facebookLoginResponse.getAuthorizationToken());
                                setRefreshToken(facebookLoginResponse.getRefreshToken());
                                setLoggedInUser(facebookLoginResponse.getUserId());
                                setLoggedInUserName(username);
                                setLoggedInUserEmail(email);

                                //Log.d(TAG, "checkForExistingSubscription()");

                                if (launchType == LaunchType.SUBSCRIBE ||
                                        launchType == LaunchType.INIT_SIGNUP) {
                                    this.facebookAccessToken = facebookAccessToken;
                                    this.facebookUserId = facebookUserId;
                                    this.facebookUsername = username;
                                    this.facebookEmail = email;
                                }

                                finalizeLogin(forceSubscribed,
                                        facebookLoginResponse.isSubscribed(),
                                        launchType == LaunchType.INIT_SIGNUP ||
                                                launchType == LaunchType.SUBSCRIBE ||
                                                !TextUtils.isEmpty(getRestoreSubscriptionReceipt()),
                                        refreshSubscriptionData);
                            }
                        }
                    });
        }

        if (currentContext != null) {
            SharedPreferences sharedPreferences =
                    currentContext.getSharedPreferences(FACEBOOK_ACCESS_TOKEN_SHARED_PREF_NAME, 0);
            return sharedPreferences.edit().putString(FACEBOOK_ACCESS_TOKEN_SHARED_PREF_NAME,
                    facebookAccessToken).commit();
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setGoogleAccessToken(final String googleAccessToken,
                                        final String googleUserId,
                                        final String googleUsername,
                                        final String googleEmail,
                                        boolean forceSubscribed,
                                        boolean refreshSubscriptionData) {
        checkForExistingSubscription(false);

        String url = currentActivity.getString(R.string.app_cms_google_login_api_url,
                appCMSMain.getApiBaseUrl(), appCMSSite.getGist().getSiteInternalName());

        appCMSGoogleLoginCall.call(url, googleAccessToken,
                googleLoginResponse -> {
                    try {
                        if (googleLoginResponse != null) {
                            if (!TextUtils.isEmpty(googleLoginResponse.getMessage())) {
                                showDialog(DialogType.SIGNIN, googleLoginResponse.getError(), false, null, null);
                                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                            } else if (!TextUtils.isEmpty(googleLoginResponse.getError())) {
                                showDialog(DialogType.SIGNIN, googleLoginResponse.getError(), false, null, null);
                                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                            } else {
                                setAuthToken(googleLoginResponse.getAuthorizationToken());
                                setRefreshToken(googleLoginResponse.getRefreshToken());
                                setLoggedInUser(googleLoginResponse.getUserId());
                                setLoggedInUserName(googleUsername);
                                setLoggedInUserEmail(googleEmail);

                                //Log.d(TAG, "checkForExistingSubscription()");

                                if (launchType == LaunchType.SUBSCRIBE) {
                                    this.googleAccessToken = googleAccessToken;
                                    this.googleUserId = googleUserId;
                                    this.googleUsername = googleUsername;
                                    this.googleEmail = googleEmail;
                                }

                                waithingFor3rdPartyLogin = false;

                                finalizeLogin(forceSubscribed,
                                        googleLoginResponse.isSubscribed(),
                                        launchType == LaunchType.INIT_SIGNUP ||
                                                launchType == LaunchType.SUBSCRIBE ||
                                                !TextUtils.isEmpty(getRestoreSubscriptionReceipt()),
                                        refreshSubscriptionData);
                            }
                        }
                    } catch (Exception e) {
                        //Log.e(TAG, "Error getting Google Access Token login information: " + e.getMessage());
                    }
                });

        SharedPreferences sharedPreferences =
                currentContext.getSharedPreferences(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME, 0);
        return sharedPreferences.edit().putString(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME,
                googleAccessToken).commit();
    }

    public boolean getAutoplayEnabledUserPref(@NonNull Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(AUTO_PLAY_ENABLED_PREF_NAME, 0);
        return sharedPrefs.getBoolean(getLoggedInUser(), true);
    }

    public void setAutoplayEnabledUserPref(Context context, boolean isAutoplayEnabled) {
        if (context != null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(AUTO_PLAY_ENABLED_PREF_NAME, 0);
            sharedPrefs.edit().putBoolean(getLoggedInUser(), isAutoplayEnabled).apply();
        }
    }

    private boolean getIsUserSubscribed() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(IS_USER_SUBSCRIBED, 0);
            return sharedPrefs.getBoolean(getLoggedInUser(), false);
        }
        return false;
    }

    private void setIsUserSubscribed(boolean userSubscribed) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(IS_USER_SUBSCRIBED, 0);
            sharedPrefs.edit().putBoolean(getLoggedInUser(), userSubscribed).apply();
        }
    }

    @SuppressWarnings("unused")
    public String getExistingGooglePlaySubscriptionDescription() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_DESCRIPTION,
                    0);
            return sharedPrefs.getString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_DESCRIPTION, null);
        }
        return null;
    }

    private void setExistingGooglePlaySubscriptionDescription(String existingGooglePlaySubscriptionDescription) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_DESCRIPTION, 0);
            sharedPrefs.edit().putString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_DESCRIPTION,
                    existingGooglePlaySubscriptionDescription).apply();
        }
    }

    private double parseActiveSubscriptionPrice() {
        try {
            String activeSubscriptionPrice = getActiveSubscriptionPrice();
            if (!TextUtils.isEmpty(activeSubscriptionPrice)) {
                return NumberFormat.getNumberInstance().parse(activeSubscriptionPrice).doubleValue();
            }

        } catch (NumberFormatException | ParseException | NullPointerException e) {
            //Log.e(TAG, "Error parsing price from Google Play subscription data: " + e.toString());
        }
        return 0.0;
    }

    private double parseExistingGooglePlaySubscriptionPrice() {
        try {
            String existingGooglePlaySubscriptionPrice = getExistingGooglePlaySubscriptionPrice();
            if (!TextUtils.isEmpty(existingGooglePlaySubscriptionPrice)) {
                return NumberFormat.getCurrencyInstance().parse(existingGooglePlaySubscriptionPrice).doubleValue();
            }

        } catch (NumberFormatException | ParseException | NullPointerException e) {
            //Log.e(TAG, "Error parsing price from Google Play subscription data: " + e.toString());
        }
        return 0.0;
    }

    private String getExistingGooglePlaySubscriptionPrice() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_PRICE, 0);
            return sharedPrefs.getString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_PRICE, null);
        }
        return null;
    }

    private void setExistingGooglePlaySubscriptionPrice(String existingGooglePlaySubscriptionPrice) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_PRICE, 0);
            sharedPrefs.edit().putString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_PRICE,
                    existingGooglePlaySubscriptionPrice).apply();
        }
    }

    private String getExistingGooglePlaySubscriptionId() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_ID, 0);
            return sharedPrefs.getString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_ID, null);
        }
        return null;
    }

    private void setExistingGooglePlaySubscriptionId(String existingGooglePlaySubscriptionId) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_ID, 0);
            sharedPrefs.edit().putString(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_ID, existingGooglePlaySubscriptionId).apply();
        }
    }

    public boolean isExistingGooglePlaySubscriptionSuspended() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_SUSPENDED, 0);
            return sharedPrefs.getBoolean(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_SUSPENDED, false);
        }
        return false;
    }

    private void setExistingGooglePlaySubscriptionSuspended(boolean existingSubscriptionSuspended) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_SUSPENDED, 0);
            sharedPrefs.edit().putBoolean(EXISTING_GOOGLE_PLAY_SUBSCRIPTION_SUSPENDED, existingSubscriptionSuspended).apply();
        }
    }

    public String getActiveSubscriptionSku() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_SKU, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_SKU, null);
        }
        return null;
    }

    private void setActiveSubscriptionSku(String subscriptionSku) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_SKU, 0);
            sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_SKU, subscriptionSku).apply();
        }
    }

    private String getActiveSubscriptionCountryCode() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_COUNTRY_CODE, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_COUNTRY_CODE, null);
        }
        return null;
    }

    private void setActiveSubscriptionCountryCode(String countryCode) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_COUNTRY_CODE, 0);
            sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_COUNTRY_CODE, countryCode).apply();
        }
    }

    public String getActiveSubscriptionId() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_ID, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_ID, null);
        }
        return null;
    }

    private void setActiveSubscriptionId(String subscriptionId) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_ID, 0);
            sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_ID, subscriptionId).apply();
        }
    }

    private String getActiveSubscriptionCurrency() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_CURRENCY, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_CURRENCY, null);
        }
        return null;
    }

    private void setActiveSubscriptionCurrency(String subscriptionCurrency) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_CURRENCY, 0);
            sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_CURRENCY, subscriptionCurrency).apply();
        }
    }

    public String getActiveSubscriptionPlanName() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_PLAN_NAME, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_PLAN_NAME, null);
        }
        return null;
    }

    public void setActiveSubscriptionPlanName(String subscriptionPlanName) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_PLAN_NAME, 0);
            sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_PLAN_NAME, subscriptionPlanName).apply();
        }
    }

    public boolean isSubscriptionCompleted() {
        String activeSubscriptionStatus = getActiveSubscriptionStatus();

        return !TextUtils.isEmpty(activeSubscriptionStatus) && activeSubscriptionStatus.equalsIgnoreCase("COMPLETED");

    }

    public String getActiveSubscriptionStatus() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_STATUS, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_STATUS, null);
        }
        return null;
    }

    public void setActiveSubscriptionStatus(String subscriptionStatus) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_STATUS, 0);
            sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_STATUS, subscriptionStatus).apply();
        }
    }

    @SuppressWarnings("unused")
    public String getActiveSubscriptionPlatform() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_PLATFORM, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_PLATFORM, null);
        }
        return null;
    }

    @SuppressWarnings("unused")
    public boolean setActiveSubscriptionPlatform(String platform) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_PLATFORM, 0);
            return sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_PLATFORM, platform).commit();
        }
        return false;
    }

    public String getActiveSubscriptionPrice() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_PRICE_NAME, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_PRICE_NAME, null);
        }
        return null;
    }

    private void setActiveSubscriptionPrice(String subscriptionPrice) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_PRICE_NAME, 0);
            sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_PRICE_NAME, subscriptionPrice).apply();
        }
    }

    public String getActiveSubscriptionProcessor() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_PROCESSOR_NAME, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_PROCESSOR_NAME, null);
        }
        return null;
    }

    private void setActiveSubscriptionProcessor(String paymentProcessor) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_PROCESSOR_NAME, 0);
            sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_PROCESSOR_NAME, paymentProcessor).apply();
        }
    }

    public String getRestoreSubscriptionReceipt() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(RESTORE_SUBSCRIPTION_RECEIPT, 0);
            return sharedPrefs.getString(RESTORE_SUBSCRIPTION_RECEIPT, null);
        }
        return null;
    }

    private void setRestoreSubscriptionReceipt(String subscriptionToken) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(RESTORE_SUBSCRIPTION_RECEIPT, 0);
            sharedPrefs.edit().putString(RESTORE_SUBSCRIPTION_RECEIPT, subscriptionToken).apply();
        }
    }

    private String getActiveSubscriptionReceipt() {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_RECEIPT, 0);
            return sharedPrefs.getString(ACTIVE_SUBSCRIPTION_RECEIPT, null);
        }
        return null;
    }

    private void setActiveSubscriptionReceipt(String subscriptionToken) {
        if (currentContext != null) {
            SharedPreferences sharedPrefs = currentContext.getSharedPreferences(ACTIVE_SUBSCRIPTION_RECEIPT, 0);
            sharedPrefs.edit().putString(ACTIVE_SUBSCRIPTION_RECEIPT, subscriptionToken).apply();
        }
    }

    public void logout() {
        ViewCreator.clearPlayerView();

        if (currentActivity != null) {
            if (!TextUtils.isEmpty(getLoggedInUser())) {
                sendUALoggedOutEvent(getLoggedInUser());
            }

            showLoadingDialog(true);
            GraphRequest revokePermissions = new GraphRequest(AccessToken.getCurrentAccessToken(),
                    getLoggedInUser() + "/permissions/", null,
                    HttpMethod.DELETE, response -> {
                try {
                    if (response != null) {
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            //Log.e(TAG, error.toString());
                        }
                    }
                } catch (Exception e) {
                    //Log.e(TAG, "Error logging out from Facebook: " + e.getMessage());
                }
            });

            revokePermissions.executeAsync();
            LoginManager.getInstance().logOut();
            //Send Firebase Logout Event
            sendFireBaseLogOutEvent();

            AppsFlyerUtils.logoutEvent(currentActivity, getLoggedInUser());

            setLoggedInUser(null);
            setLoggedInUserName(null);
            setLoggedInUserEmail(null);
            setActiveSubscriptionPrice(null);
            setActiveSubscriptionId(null);
            setActiveSubscriptionSku(null);
            setActiveSubscriptionCountryCode(null);
            setActiveSubscriptionPlanName(null);
            setActiveSubscriptionReceipt(null);
            setRefreshToken(null);
            setAuthToken(null);
            setIsUserSubscribed(false);
            setExistingGooglePlaySubscriptionId(null);
            setActiveSubscriptionProcessor(null);
            setRestoreSubscriptionReceipt(null);

            SharedPreferences sharedPreferences =
                    currentContext.getSharedPreferences(FACEBOOK_ACCESS_TOKEN_SHARED_PREF_NAME, 0);
            sharedPreferences.edit().putString(FACEBOOK_ACCESS_TOKEN_SHARED_PREF_NAME, null).apply();

            sharedPreferences = currentContext.getSharedPreferences(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME, 0);
            sharedPreferences.edit().putString(GOOGLE_ACCESS_TOKEN_SHARED_PREF_NAME, null).apply();

            signinAnonymousUser();

            setEntitlementPendingVideoData(null);

            if (googleApiClient != null && googleApiClient.isConnected()) {
                Auth.GoogleSignInApi.signOut(googleApiClient);
            }

            userHistoryData.clear();
            filmsInUserWatchList = null;
            getPageViewLruCache().evictAll();
            clearPageAPIData(this::navigateToHomePage, false);
            CastHelper.getInstance(currentActivity.getApplicationContext()).disconnectChromecastOnLogout();
        }
    }

    private void sendFireBaseLogOutEvent() {
        Bundle bundle = new Bundle();

        String FIREBASE_SCREEN_LOG_OUT = "log_out";
        String FIREBASE_SCREEN_SIGN_OUT = "sign_out";
        bundle.putString(FIREBASE_SCREEN_SIGN_OUT, FIREBASE_SCREEN_LOG_OUT);
        if (getmFireBaseAnalytics() != null) {

            mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_PLAN_ID, null);
            mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_PLAN_NAME, null);
            mFireBaseAnalytics.setUserId(null);

            mFireBaseAnalytics.setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_OUT);
            mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_NOT_SUBSCRIBED);
            getmFireBaseAnalytics().logEvent(FIREBASE_SCREEN_SIGN_OUT, bundle);

        }
    }

    @SuppressWarnings("unused")
    public void logoutTV() {
        if (!isNetworkConnected()) {
            RetryCallBinder retryCallBinder = getRetryCallBinder(null, null,
                    null, null,
                    null, false, null, LOGOUT_ACTION);
            Bundle bundle = new Bundle();
            bundle.putBoolean(currentActivity.getString(R.string.retry_key), true);
            bundle.putBoolean(currentActivity.getString(R.string.register_internet_receiver_key), true);
            bundle.putBoolean(currentActivity.getString(R.string.is_tos_dialog_page_key), false);
            bundle.putBoolean(currentActivity.getString(R.string.is_login_dialog_page_key), false);
            bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
            Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
            args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
            args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(args);
            return;
        }

        if (currentActivity != null) {
            setLoggedInUser(null);
            setLoggedInUserName(null);
            setLoggedInUserEmail(null);
            setActiveSubscriptionPrice(null);
            setActiveSubscriptionId(null);
            setActiveSubscriptionSku(null);
            setActiveSubscriptionPlanName(null);
            setActiveSubscriptionStatus(null);
            setActiveSubscriptionReceipt(null);
            setActiveSubscriptionPlatform(null);
            setRefreshToken(null);
            setAuthToken(null);
            setIsUserSubscribed(false);
            setExistingGooglePlaySubscriptionId(null);
            setActiveSubscriptionProcessor(null);
            setFacebookAccessToken(null, null, null, null, false, false);
            setGoogleAccessToken(null, null, null, null, false, false);

            sendUpdateHistoryAction();

            signinAnonymousUser();
            AppsFlyerUtils.logoutEvent(currentActivity, getLoggedInUser());
            NavigationPrimary homePageNavItem = findHomePageNavItem();
            if (homePageNavItem != null) {
                cancelInternalEvents();
                navigateToTVPage(
                        homePageNavItem.getPageId(),
                        homePageNavItem.getTitle(),
                        homePageNavItem.getUrl(),
                        false,
                        deeplinkSearchQuery,
                        true,
                        false,
                        false
                );
            }
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
            //Log.d(TAG, "Restarting internal events");
            List<OnInternalEvent> onInternalEvents = onActionInternalEvents.get(currentActions.peek());
            if (onInternalEvents != null) {
                for (OnInternalEvent onInternalEvent : onInternalEvents) {
                    onInternalEvent.cancel(false);
                    //Log.d(TAG, "Restarted internal event");
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
            //Log.d(TAG, "Stack size - Popping action internal events: " + currentActions.size());
            currentActions.pop();
            //Log.d(TAG, "Stack size - Popped action internal events: " + currentActions.size());
        }
    }

    public NavigationPrimary findHomePageNavItem() {
        if (navigation != null && !navigation.getNavigationPrimary().isEmpty()) {
            return navigation.getNavigationPrimary().get(0);
        }
        return null;
    }

    public NavigationPrimary findMoviesPageNavItem() {
        if (navigation != null && navigation.getNavigationPrimary().size() >= 2) {
            return navigation.getNavigationPrimary().get(1);
        }
        return null;
    }

    public NavigationPrimary findLivePageNavItem() {
        if (navigation != null && navigation.getNavigationPrimary().size() >= 3) {
            return navigation.getNavigationPrimary().get(2);
        }
        return null;
    }

    public void resetDeeplinkQuery() {
        deeplinkSearchQuery = null;
    }

    public void getAppCMSMain(final Activity activity,
                              final String siteId,
                              final Uri searchQuery,
                              final PlatformType platformType,
                              boolean bustCache) {
        Log.w(TAG, "Attempting to retrieve main.json");

        this.deeplinkSearchQuery = searchQuery;
        this.platformType = platformType;
        this.launched = false;
        this.cancelLoad = false;
        this.cancelAllLoads = false;
        this.processedUIModules = false;
        this.processedUIPages = false;

        GetAppCMSMainUIAsyncTask.Params params = new GetAppCMSMainUIAsyncTask.Params.Builder()
                .context(currentActivity)
                .siteId(siteId)
                .bustCache(bustCache)
                .networkDisconnected(!isNetworkConnected())
                .build();

        try {
            new GetAppCMSMainUIAsyncTask(appCMSMainUICall, main -> {
                try {
                    if (main == null && appCMSMain == null) {
                        //Log.e(TAG, "DialogType retrieving main.json");
                        if (!isNetworkConnected()) {//Fix for SVFA-1435 issue 2nd by manoj comment
                            openDownloadScreenForNetworkError(true,
                                    () -> getAppCMSMain(activity,
                                            siteId,
                                            searchQuery,
                                            platformType,
                                            bustCache));
                        } else {
                            launchBlankPage();
                        }
                    } else if (main != null && TextUtils.isEmpty(main
                            .getAndroid())) {
                        //Log.e(TAG, "AppCMS key for main not found");
                        launchBlankPage();
                    } else if (main != null && TextUtils.isEmpty(main
                            .getApiBaseUrl())) {
                        //Log.e(TAG, "AppCMS key for API Base URL not found");
                        launchBlankPage();
                    } else {
                        if (main != null) {
                            appCMSMain = main;
                        }
                        new SoftReference<Object>(appCMSMain, referenceQueue);
                        loadFromFile = appCMSMain.shouldLoadFromFile();

                        apikey = currentActivity.getString(R.string.x_api_key);

                        getAppCMSSite(platformType);
                    }
                } catch (Exception e) {
                    //Log.e(TAG, "Error retrieving main.json: " + e.getMessage());
                    launchBlankPage();
                }
            }).execute(params);
        } catch (Exception e) {
            //Log.e(TAG, "Error retrieving main.json: " + e.getMessage());
        }
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
                //Log.e(TAG, "Failed to verify if input page is a video page: " + e.toString());
            }
        }
        return false;
    }

    public boolean isPageAShowPage(String pageName) {
        if (currentActivity != null && pageName != null) {
            try {
                return pageName.contains(currentActivity.getString(R.string.app_cms_pagename_showscreen_key));
            } catch (Exception e) {

            }
        }
        return false;
    }

    private void initAppsFlyer(AppCMSAndroidUI appCMSAndroidUI) {
        if (currentContext != null &&
                currentContext instanceof AppCMSApplication) {
            if (appCMSAndroidUI != null &&
                    appCMSAndroidUI.getAnalytics() != null &&
                    !TextUtils.isEmpty(appCMSAndroidUI.getAnalytics().getAppflyerDevKey())) {
                setAppsFlyerKey(appCMSAndroidUI.getAnalytics().getAppflyerDevKey());
                ((AppCMSApplication) currentContext).initAppsFlyer(appCMSAndroidUI.getAnalytics().getAppflyerDevKey());
            }
        }
    }

    public boolean isPagePrimary(String pageId) {
        for (NavigationPrimary navigationPrimary : navigation.getNavigationPrimary()) {
            if (pageId != null &&
                    navigationPrimary != null &&
                    !TextUtils.isEmpty(navigationPrimary.getPageId()) &&
                    !TextUtils.isEmpty(pageId) &&
                    pageId.contains(navigationPrimary.getPageId()) &&
                    !isViewPlanPage(pageId)) {
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
            if (pageId != null &&
                    !TextUtils.isEmpty(pageId) &&
                    navigationUser != null &&
                    !TextUtils.isEmpty(navigationUser.getPageId()) &&
                    pageId.contains(navigationUser.getPageId())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public boolean isPageFooter(String pageId) {
        for (NavigationFooter navigationFooter : navigation.getNavigationFooter()) {
            if (pageId != null && !TextUtils.isEmpty(pageId) && pageId.contains(navigationFooter.getPageId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isPageLoginPage(String pageId) {
        return loginPage != null && !TextUtils.isEmpty(pageId) && !TextUtils.isEmpty(loginPage.getPageId()) && loginPage.getPageId().equals(pageId);
    }

    @SuppressWarnings("unused")
    public boolean isPageSplashPage(String pageId) {
        return splashPage != null && !TextUtils.isEmpty(pageId) && !TextUtils.isEmpty(splashPage.getPageId()) && splashPage.getPageId().equals(pageId);
    }

    public AppCMSSearchUrlComponent getAppCMSSearchUrlComponent() {
        return appCMSSearchUrlComponent;
    }

    public void showMoreDialog(String title, String fullText) {
        if (platformType == PlatformType.ANDROID && !isAdditionalFragmentVisibile()) {
            if (currentActivity != null &&
                    currentActivity instanceof AppCompatActivity &&
                    isAdditionalFragmentViewAvailable()) {
                pushActionInternalEvents(currentActivity.getString(R.string.more_page_action));
                String FIREBASE_VIDEO_DETAIL_SCREEN = "Video Detail Screen";
                String eventValue = FIREBASE_VIDEO_DETAIL_SCREEN + "-" + title;
                sendFirebaseAnalyticsEvents(eventValue);
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
            args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(args);
        }
    }

    @SuppressWarnings("unused")
    public void showClearHistoryDialog(String title, String fullText, Action1<Integer> action1) {
        Intent args = new Intent(AppCMSPresenter.PRESENTER_CLEAR_DIALOG_ACTION);
        Bundle bundle = new Bundle();
        bundle.putString(currentActivity.getString(R.string.dialog_item_title_key), title);
        bundle.putString(currentActivity.getString(R.string.dialog_item_description_key), fullText);
        args.putExtra(currentActivity.getString(R.string.dialog_item_key), bundle);
        args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
        currentActivity.sendBroadcast(args);
    }

    private boolean shouldRefreshAuthToken() {
        if (currentActivity != null) {
            long lastLoginTime = getLoggedInTime();
            if (lastLoginTime >= 0) {
                long now = new Date().getTime();
                long timeDiff = now - lastLoginTime;
                long minutesSinceLogin = timeDiff / (MILLISECONDS_PER_SECOND * SECONDS_PER_MINUTE);

                long maxDuration = MAX_SESSION_DURATION_IN_MINUTES;

                if (!TextUtils.isEmpty(getLoggedInUser()) &&
                        !TextUtils.isEmpty(getAnonymousUserToken()) &&
                        getLoggedInUser().equals(getAnonymousUserToken())) {
                    maxDuration = MAX_ANONYMOUS_SESSIONS_DURATION_IN_MINUTES;
                }

                if (minutesSinceLogin >= maxDuration) {
                    return true;
                }
            }
        }
        return false;
    }

    public void showToast(String message, int messageDuration) {
        if (currentActivity != null) {
            Toast.makeText(currentActivity, message, messageDuration).show();
        }
    }

    public void showEntitlementDialog(DialogType dialogType, Action0 onCloseAction) {
        if (currentActivity != null && !loginDialogPopupOpen) {

            try {

                String positiveButtonText = currentActivity.getString(R.string.app_cms_subscription_button_text);
                int textColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor());
                String title = currentActivity.getString(R.string.app_cms_subscription_required_title);
                String message = currentActivity.getString(R.string.app_cms_subscription_required_message);

                if (dialogType == DialogType.LOGOUT_WITH_RUNNING_DOWNLOAD) {
                    title = currentActivity.getString(R.string.app_cms_logout_with_running_download_title);
                    message = currentActivity.getString(R.string.app_cms_logout_with_running_download_message);
                }
                if (dialogType == DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED || dialogType == DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED_PLAYER) {
                    title = currentActivity.getString(R.string.app_cms_login_and_subscription_required_title);
                    message = currentActivity.getString(R.string.app_cms_login_and_subscription_required_message);
                    //Set Firebase User Property when user is not logged in and unsubscribed
                    mFireBaseAnalytics.setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_OUT);
                    mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_NOT_SUBSCRIBED);
                }

                if (dialogType == DialogType.CANNOT_UPGRADE_SUBSCRIPTION) {
                    String paymentProcessor = getActiveSubscriptionProcessor();
                    if ((!TextUtils.isEmpty(paymentProcessor) &&
                            !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) &&
                            !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly)) &&
                            !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor)) &&
                            !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor_friendly)))) {
                        title = currentActivity.getString(R.string.app_cms_subscription_upgrade_cancel_title);
                        message = currentActivity.getString(R.string.app_cms_subscription_upgrade_for_web_user_dialog);
                    } else if (!TextUtils.isEmpty(paymentProcessor) &&
                            ((paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor))
                                    || paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor_friendly))))) {
                        title = currentActivity.getString(R.string.app_cms_subscription_upgrade_cancel_title);
                        message = currentActivity.getString(R.string.app_cms_subscription_upgrade_for_ios_user_dialog);
                    } else {
                        title = currentActivity.getString(R.string.app_cms_subscription_google_play_cancel_title);
                        message = currentActivity.getString(R.string.app_cms_subscription_cancel_for_google_play_user_dialog);
                    }
                }

                if (dialogType == DialogType.CANNOT_CANCEL_SUBSCRIPTION) {
                    String paymentProcessor = getActiveSubscriptionProcessor();
                    if ((!TextUtils.isEmpty(paymentProcessor) &&
                            !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) &&
                            !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly)) &&
                            !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor)) &&
                            !paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor_friendly)))) {
                        title = currentActivity.getString(R.string.app_cms_subscription_upgrade_cancel_title);
                        message = currentActivity.getString(R.string.app_cms_subscription_cancel_for_web_user_dialog);
                    } else if (!TextUtils.isEmpty(paymentProcessor) &&
                            ((paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor))
                                    || paymentProcessor.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor_friendly))))) {
                        title = currentActivity.getString(R.string.app_cms_subscription_upgrade_cancel_title);
                        message = currentActivity.getString(R.string.app_cms_subscription_cancel_for_ios_user_dialog);
                    } else {
                        if (!TextUtils.isEmpty(paymentProcessor) &&
                                TextUtils.isEmpty(getExistingGooglePlaySubscriptionId())) {
                            title = currentActivity.getString(R.string.app_cms_subscription_google_play_cancel_title);
                            message = currentActivity.getString(R.string.app_cms_subscription_cancel_for_google_play_user_dialog);
                        }
                    }
                }

                if (dialogType == DialogType.UPGRADE_UNAVAILABLE) {
                    title = currentActivity.getString(R.string.app_cms_subscription_upgrade_unavailable_title);
                    message = currentActivity.getString(R.string.app_cms_subscription_upgrade_unavailable_user_dialog);
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
                    //Set Firebase User Property when user is not logged in and unsubscribed
                    mFireBaseAnalytics.setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_OUT);
                    mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_NOT_SUBSCRIBED);
                }

                if (dialogType == DialogType.SUBSCRIPTION_REQUIRED || dialogType == DialogType.SUBSCRIPTION_REQUIRED_PLAYER) {
                    mFireBaseAnalytics.setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_IN);
                    mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_NOT_SUBSCRIBED);
                }

                if (dialogType == DialogType.EXISTING_SUBSCRIPTION) {
                    title = currentActivity.getString(R.string.app_cms_existing_subscription_title);
                    message = currentActivity.getString(R.string.app_cms_existing_subscription_error_message);
                    positiveButtonText = currentActivity.getString(R.string.app_cms_login_button_text);
                }

                if (dialogType == DialogType.EXISTING_SUBSCRIPTION_LOGOUT) {
                    title = currentActivity.getString(R.string.app_cms_existing_subscription_title);
                    message = currentActivity.getString(R.string.app_cms_existing_subscription_logout_error_message);
                    positiveButtonText = currentActivity.getString(R.string.app_cms_signout_button_text);
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
                                    //Log.e(TAG, "Error displaying dialog while logging out with running download: " + e.getMessage());
                                }
                            });
                    builder.setNegativeButton("Cancel",
                            (dialog, which) -> {
                                try {
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    //Log.e(TAG, "Error cancelling dialog while logging out with running download: " + e.getMessage());
                                }
                            });
                } else if (dialogType == DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED || dialogType == DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED_PLAYER) {
                    builder.setPositiveButton(R.string.app_cms_login_button_text,
                            (dialog, which) -> {
                                try {
                                    dialog.dismiss();
                                    launchType = LaunchType.LOGIN_AND_SIGNUP;
                                    if (onCloseAction != null) {
                                        onCloseAction.call();
                                    }
                                    navigateToLoginPage(false);
                                } catch (Exception e) {
                                    //Log.e(TAG, "Error closing login & subscription required dialog: " + e.getMessage());
                                }
                            });
                    builder.setNegativeButton(R.string.app_cms_subscription_button_text,
                            (dialog, which) -> {
                                try {
                                    dialog.dismiss();
                                    if (onCloseAction != null) {
                                        onCloseAction.call();
                                    }
                                    navigateToSubscriptionPlansPage(false);
                                } catch (Exception e) {
                                    //Log.e(TAG, "Error closing subscribe dialog: " + e.getMessage());
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
                                    if (onCloseAction != null) {
                                        onCloseAction.call();
                                    }
                                    navigateToLoginPage(false);
                                } catch (Exception e) {
                                    //Log.e(TAG, "Error closing login required dialog: " + e.getMessage());
                                }
                            });
                } else if (dialogType == DialogType.UNKNOWN_SUBSCRIPTION_FOR_UPGRADE ||
                        dialogType == DialogType.UNKNOWN_SUBSCRIPTION_FOR_CANCEL) {
                    builder.setPositiveButton("OK",
                            (dialog, which) -> {
                                try {
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    //Log.e(TAG, "Error closing subscription required dialog: " + e.getMessage());
                                }
                            });
                } else if (dialogType == DialogType.EXISTING_SUBSCRIPTION) {
                    builder.setPositiveButton(positiveButtonText,
                            (dialog, which) -> {
                                if (onCloseAction != null) {
                                    onCloseAction.call();
                                }
                                dialog.dismiss();
                            });
                } else if (dialogType == DialogType.EXISTING_SUBSCRIPTION_LOGOUT) {
                    builder.setPositiveButton(positiveButtonText,
                            (dialog, which) -> {
                                if (onCloseAction != null) {
                                    onCloseAction.call();
                                }
                                dialog.dismiss();
                            });
                } else {
                    builder.setPositiveButton(positiveButtonText,
                            (dialog, which) -> {
                                try {
                                    dialog.dismiss();
                                    if (onCloseAction != null) {
                                        onCloseAction.call();
                                    }
                                    navigateToSubscriptionPlansPage(false);
                                } catch (Exception e) {
                                    //Log.e(TAG, "Error closing navigate to subscription dialog: " + e.getMessage());
                                }
                            });
                }

                if (dialogType == DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED_PLAYER || dialogType == DialogType.SUBSCRIPTION_REQUIRED_PLAYER) {
                    builder.setOnKeyListener((arg0, keyCode, event) -> {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            loginDialogPopupOpen = false;
                            if (onCloseAction != null) {
                                //if user press back key without doing login subscription ,clear saved data
                                setEntitlementPendingVideoData(null);
                                onCloseAction.call();
                                //if user press back key without doing login subscription ,clear saved data
                                setEntitlementPendingVideoData(null);
                            }
                        }
                        return true;
                    });
                }
                currentActivity.runOnUiThread(() -> {
                    AlertDialog dialog = builder.create();
                    if (onCloseAction != null) {
                        dialog.setCanceledOnTouchOutside(false);

                        dialog.setOnCancelListener(dialogInterface -> {
                            if (dialogType == DialogType.EXISTING_SUBSCRIPTION ||
                                    dialogType == DialogType.EXISTING_SUBSCRIPTION_LOGOUT) {
                                sendCloseOthersAction(null, true, false);
                            }
                        });
                    }

                    dialog.setOnShowListener(arg0 -> {
                        loginDialogPopupOpen = true;
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getBackgroundColor()));
                        if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getBackgroundColor()));
                        }
                    });

                    dialog.setOnDismissListener(arg0 -> {
                        loginDialogPopupOpen = false;
                    });

                    dialog.setOnCancelListener(arg0 -> {
                        loginDialogPopupOpen = false;
                    });

                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                                Color.parseColor(getAppBackgroundColor())));
                        if (currentActivity.getWindow().isActive()) {
                            try {
                                dialog.show();
                            } catch (Exception e) {
                                //Log.e(TAG, "An exception has occurred when attempting to show the dialogType dialog: "
//                                + e.toString());
                            }
                        }
                    }
                });
            } catch (Exception e) {

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
                            Observable.just(true)
                                    .onErrorResumeNext(throwable -> Observable.empty())
                                    .subscribe(oncConfirmationAction);
                        }
                    });
            builder.setNegativeButton(R.string.app_cms_negative_confirmation_button_text,
                    (dialog, which) -> {
                        try {
                            dialog.dismiss();
                            if (oncConfirmationAction != null) {
                                Observable.just(false)
                                        .onErrorResumeNext(throwable -> Observable.empty())
                                        .subscribe(oncConfirmationAction);
                            }
                        } catch (Exception e) {
                            //Log.e(TAG, "Error closing confirm cancellation dialog: " + e.getMessage());
                        }
                    });
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                        Color.parseColor(getAppBackgroundColor())));
                if (currentActivity.getWindow().isActive()) {
                    try {
                        dialog.show();
                    } catch (Exception e) {
                        //Log.e(TAG, "An exception has occurred when attempting to show the dialogType dialog: "
//                                + e.toString());
                    }
                }
            }
        }
    }

    public void openDownloadScreenForNetworkError(boolean launchActivity, Action0 retryAction) {
        try { // Applied this flow for fixing SVFA-1435 App Launch Scenario
            if (!isUserSubscribed() || !downloadsAvailableForApp()) {//fix SVFA-1911
                showDialog(DialogType.NETWORK, null, true,
                        () -> {
                            if (retryAction != null) {
                                retryAction.call();
                            }
                        },
                        () -> {
                            if (currentActivity != null) {
                                currentActivity.finish();
                            }
//                            launched = true;
//                            launchBlankPage();
//                            sendStopLoadingPageAction(false, null);
//                            showNoNetworkConnectivityToast();
//                            showNetworkConnectivity = false;
                        });
                return;
            }

            if (downloadsAvailableForApp()) {
                navigateToDownloadPage(getDownloadPageId(),
                        null, null, launchActivity);
            }
        } catch (Exception e) {
            launchBlankPage();// Fix for SVFA-1435 after killing app
            sendStopLoadingPageAction(false, null);
            //Log.d(TAG, e.getMessage());
        }
    }

    @SuppressWarnings("UnusedAssignment")
    public void showDialog(DialogType dialogType,
                           String optionalMessage,
                           boolean showCancelButton,
                           final Action0 onDismissAction,
                           final Action0 onCloseAction) {
        if (currentActivity != null) {
            int textColor = ContextCompat.getColor(currentContext, android.R.color.white);
            try {
                textColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor());
            } catch (Exception e) {
                //Log.w(TAG, "Failed to get branding text color - defaulting to accent color: " +
//                    e.getMessage());
                textColor = ContextCompat.getColor(currentContext, android.R.color.white);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
            String title;
            String message;

            switch (dialogType) {
                case SIGNIN:
                    title = currentActivity.getString(R.string.app_cms_signin_error_title);
                    message = optionalMessage;
                    break;
                case SIGN_OUT:
                    title = currentActivity.getString(R.string.app_cms_signout_error_title);
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

                case DELETE_ONE_WATCHLIST_ITEM:
                case DELETE_ALL_WATCHLIST_ITEMS:
                    title = currentActivity.getString(R.string.app_cms_delete_watchlist_alert_title);
                    message = optionalMessage;
                    break;
                case RE_START_DOWNLOAD_ITEM:
                    title = currentActivity.getString(R.string.app_cms_download_retry_alert_title);
                    message = optionalMessage;
                    break;
                case DELETE_ONE_DOWNLOAD_ITEM:
                case DELETE_ALL_DOWNLOAD_ITEMS:
                    title = currentActivity.getString(R.string.app_cms_delete_download_alert_title);
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

                case DOWNLOAD_VIA_MOBILE_DISABLED:
                    title = currentActivity.getString(R.string.app_cms_download_over_cellular_disabled_error_title);
                    message = optionalMessage;
                    break;

                case VIDEO_NOT_AVAILABLE:
                    title = currentActivity.getString(R.string.app_cms_video_not_available_error_title);
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
                String okText = currentContext.getString(R.string.app_cms_confirm_alert_dialog_button_text);
                String cancelText = currentContext.getString(R.string.app_cms_cancel_alert_dialog_button_text);
                if (dialogType == DialogType.NETWORK && optionalMessage == null) {
                    okText = currentActivity.getString(R.string.app_cms_retry_text);
                    cancelText = currentActivity.getString(R.string.app_cms_close_text);
                }else if (dialogType ==DialogType.RE_START_DOWNLOAD_ITEM){
                    okText=currentActivity.getString(R.string.app_cms_retry_text);
                    cancelText=currentContext.getString(R.string.app_cms_cancel_alert_dialog_button_text);
                }
                builder.setPositiveButton(okText,
                        (dialog, which) -> {
                            try {
                                dialog.dismiss();
                                if (onDismissAction != null) {
                                    onDismissAction.call();
                                }
                            } catch (Exception e) {
                                //Log.e(TAG, "Error closing cancellation dialog: " + e.getMessage());
                            }
                        });
                builder.setNegativeButton(cancelText,
                        (dialog, which) -> {
                            try {
                                dialog.dismiss();
                                if (onCloseAction != null) {
                                    onCloseAction.call();
                                }
                            } catch (Exception e) {
                                //
                            }
                        });
            } else {
                builder.setNegativeButton(R.string.app_cms_close_alert_dialog_button_text,
                        (dialog, which) -> {
                            try {
                                dialog.dismiss();
                                if (onDismissAction != null) {
                                    onDismissAction.call();
                                }
                            } catch (Exception e) {
                                //Log.e(TAG, "Error closing cancellation dialog: " + e.getMessage());
                            }
                        });
            }

            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            if (dialog.getWindow() != null) {
                try {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                            Color.parseColor(getAppBackgroundColor())));
                } catch (Exception e) {
                    //Log.w(TAG, "Failed to set background color from AppCMS branding - defaulting to colorPrimaryDark: " +
//                            e.getMessage());
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                            ContextCompat.getColor(currentContext, R.color.colorPrimaryDark)));
                }

                dialog.setOnShowListener(arg0 -> {
                    int buttonTextColor = ContextCompat.getColor(currentActivity, R.color.colorAccent);
                    try {
                        buttonTextColor = Color.parseColor(appCMSMain.getBrand().getCta().getPrimary().getBackgroundColor());
                    } catch (Exception e) {
                        buttonTextColor = ContextCompat.getColor(currentActivity, R.color.colorAccent);
                    }
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(buttonTextColor);
                    if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(buttonTextColor);
                    }
                });

                currentActivity.runOnUiThread(() -> {
                    if (currentActivity.getWindow().isActive()) {
                        try {
                            if (!dialog.isShowing())
                                dialog.show();
                        } catch (Exception e) {
                            //Log.e(TAG, "An exception has occurred when attempting to show the dialogType dialog: "
//                                + e.toString());
                        }
                    }
                });
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public boolean isNetworkConnected() {
        if (currentActivity != null) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) currentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network activeNetwork = connectivityManager.getActiveNetwork();
                if (activeNetwork != null) {
                    NetworkInfo activeNetworkInfo = connectivityManager.getNetworkInfo(activeNetwork);
                    try {
                        return activeNetworkInfo.isConnectedOrConnecting();
                    } catch (Exception e) {
                        //
                    }
                }
            } else {
                for (NetworkInfo networkInfo : connectivityManager.getAllNetworkInfo()) {
                    try {
                        if (networkInfo.isConnectedOrConnecting()) {
                            return true;
                        }
                    } catch (Exception e) {
                        //
                    }
                }
            }
        }

        return false;
    }

    public void pushActionInternalEvents(String action) {
        //Log.d(TAG, "Stack size - pushing internal events: " + currentActions.size());
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

    @SuppressWarnings("unused")
    public void sendBeaconAdImpression(String vid, String screenName, String parentScreenName,
                                       long currentPosition) {
        //Log.d(TAG, "Sending Beacon Ad Impression");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition,
                BeaconEvent.AD_IMPRESSION, false);
        //Log.d(TAG, "Beacon Ad Impression: " + url);
        beaconMessageRunnable.setUrl(url);
        beaconMessageThread.run();
    }

    @SuppressWarnings("unused")
    public void sendBeaconAdRequestMessage(String vid, String screenName, String parentScreenName,
                                           long currentPosition) {
        //Log.d(TAG, "Sending Beacon Ad Request");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition,
                BeaconEvent.AD_REQUEST, false);
        //Log.d(TAG, "Beacon Ad Request: " + url);
        beaconMessageRunnable.setUrl(url);
        beaconMessageThread.run();
    }

    @SuppressWarnings("unused")
    public void sendBeaconPingMessage(String vid, String screenName, String parentScreenName,
                                      long currentPosition, boolean usingChromecast) {
        //Log.d(TAG, "Sending Beacon Ping Message");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition, BeaconEvent.PING, usingChromecast);
        //Log.d(TAG, "Beacon Ping: " + url);
        beaconMessageRunnable.setUrl(url);
        beaconMessageThread.run();
    }

    @SuppressWarnings("unused")
    public void sendBeaconPlayMessage(String vid, String screenName, String parentScreenName,
                                      long currentPosition, boolean usingChromecast) {
        //Log.d(TAG, "Sending Beacon Play Message");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition, BeaconEvent.PLAY, usingChromecast);
        //Log.d(TAG, "Beacon Play: " + url);
        beaconMessageRunnable.setUrl(url);
        beaconMessageThread.run();
    }

    private ArrayList<BeaconRequest> getBeaconRequestList() {
        String uid = getInstanceId();
        if (isUserLoggedIn()) {
            uid = getLoggedInUser();
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

    private void sendOfflineBeaconMessage() {
        ArrayList<BeaconRequest> beaconRequests = getBeaconRequestList();

        String url = getBeaconUrl();
        AppCMSBeaconRequest request = new AppCMSBeaconRequest();

        if (url != null && beaconRequests != null) {

            request.setBeaconRequest(beaconRequests);
            appCMSBeaconCall.call(url, beaconResponse -> {
                try {

                    if (beaconResponse.beaconRequestResponse.size() > 0 &&
                            beaconResponse.beaconRequestResponse.get(0).recordId != null &&
                            beaconResponse.beaconRequestResponse.get(0).recordId.length() > 0) {
                        //Log.d(TAG, "Beacon success Event: Offline " + beaconResponse.beaconRequestResponse.get(0).recordId);
                        currentActivity.runOnUiThread(() -> realmController.deleteOfflineBeaconDataByUser(getLoggedInUser()));
                    }
                } catch (Exception e) {
                    //Log.d(TAG, "Beacon fail Event: offline  due to: " + e.getMessage());
                }
            }, request);
        } else {
            //Log.d(TAG, "No offline Beacon Event: available ");
        }
    }

    public void sendBeaconMessage(String vid, String screenName, String parentScreenName,
                                  long currentPosition, boolean usingChromecast, BeaconEvent event,
                                  String mediaType, String bitrate, String height, String width,
                                  String streamid, double ttfirstframe, int apod, boolean isDownloaded) {


        if (currentActivity != null) {

            try {
                BeaconRequest beaconRequest = getBeaconRequest(vid, screenName, parentScreenName, currentPosition, event,
                        usingChromecast, mediaType, bitrate, height, width, streamid, ttfirstframe, apod, isDownloaded);


                if (!isNetworkConnected()) {
                    currentActivity.runOnUiThread(() -> {
                        try {
                            beaconRequest.setTstampoverride(getCurrentTimeStamp());
                            realmController.addOfflineBeaconData(beaconRequest.convertToOfflineBeaconData());
                        } catch (Exception e) {
                            //Log.e(TAG, "Error adding offline Beacon data: " + e.getMessage());
                        }
                    });

                    //Log.d(TAG, "Beacon info added to database +++ " + event);
                    return;
                }
                String url = getBeaconUrl();

                AppCMSBeaconRequest request = new AppCMSBeaconRequest();
                ArrayList<BeaconRequest> beaconRequests = new ArrayList<>();

                beaconRequests.add(beaconRequest);


                request.setBeaconRequest(beaconRequests);
                if (url != null) {

                    Log.e(TAG, "Beacon request: " + gson.toJson(request));

                    appCMSBeaconCall.call(url, beaconResponse -> {
                        try {

                            if (beaconResponse.beaconRequestResponse.size() > 0 &&
                                    beaconResponse.beaconRequestResponse.get(0).recordId != null &&
                                    beaconResponse.beaconRequestResponse.get(0).recordId.length() > 0) {
                                //Log.d(TAG, "Beacon success Event: Offline " + event + "  " + beaconResponse.beaconRequestResponse.get(0).recordId);
                            }
                        } catch (Exception e) {
                            //Log.d(TAG, "Beacon fail Event: " + event + " due to: " + e.getMessage());
                        }
                    }, request);

                }
            } catch (Exception e) {
                //Log.e(TAG, "Error sending new beacon message: " + e.getMessage());
            }
        }

    }

    public String getStreamingId(String filmName) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        if (filmName != null) {
            SecretKeySpec key = new SecretKeySpec((getCurrentTimeStamp()).getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);
            byte[] bytes = mac.doFinal(filmName.getBytes("UTF-8"));
            return UUID.nameUUIDFromBytes(bytes).toString();
        }
        return "";
    }

    private String getPermalinkCompletePath(String pagePath) {
        StringBuffer permalinkCompletePath = new StringBuffer();
        permalinkCompletePath.append(currentActivity.getString(R.string.https_scheme));
        permalinkCompletePath.append(appCMSMain.getDomainName());
        //  permalinkCompletePath.append(File.separatorChar); //Commented due to Page path is already having '/' with it
        permalinkCompletePath.append(pagePath);
        return permalinkCompletePath.toString();
    }

    @SuppressLint("DefaultLocale")
    private BeaconRequest getBeaconRequest(String vid, String screenName, String parentScreenName,
                                           long currentPosition, BeaconEvent event, boolean usingChromecast,
                                           String mediaType, String bitrte, String resolutionHeight,
                                           String resolutionWidth, String streamId, double ttfirstframe, int apod, boolean isDownloaded) {
        BeaconRequest beaconRequest = new BeaconRequest();
        String uid = getInstanceId();
        int currentPositionSecs = (int) (currentPosition / MILLISECONDS_PER_SECOND);
        if (isUserLoggedIn()) {
            uid = getLoggedInUser();
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
        beaconRequest.setStream_id(streamId);
        beaconRequest.setDp1(currentActivity.getString(R.string.app_cms_beacon_dpm_android));
        beaconRequest.setUrl(getPermalinkCompletePath(screenName));
        beaconRequest.setRef(parentScreenName);
        beaconRequest.setVpos(String.valueOf(currentPositionSecs));
        beaconRequest.setApos(String.valueOf(currentPositionSecs));
        beaconRequest.setEnvironment(getEnvironment());
        beaconRequest.setResolutionheight(resolutionHeight);
        beaconRequest.setResolutionwidth(resolutionWidth);
        if (bitrte != null) {
            beaconRequest.setBitrate(bitrte);
        }
        if (event == BeaconEvent.FIRST_FRAME && ttfirstframe != 0d) {
            beaconRequest.setTtfirstframe(String.format("%.2f", ttfirstframe));
        }
        if (event == BeaconEvent.AD_IMPRESSION || event == BeaconEvent.AD_REQUEST) {
            beaconRequest.setApod(String.valueOf(apod));
        }

        if (isDownloaded) {
            beaconRequest.setDp2("downloaded_view-online");
        } else {
            beaconRequest.setDp2("view-online");
        }


        if (usingChromecast) {
            beaconRequest.setPlayer("Chromecast");
        } else {
            beaconRequest.setPlayer("Native");
        }


        return beaconRequest;
    }

    @SuppressLint("SimpleDateFormat")
    public String getCurrentTimeStamp() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    private String getEnvironment() {
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

    @SuppressLint("StringFormatInvalid")
    private String getBeaconUrl() {
        if (currentActivity != null &&
                appCMSMain != null &&
                appCMSMain.getBeacon() != null &&
                appCMSMain.getBeacon().getApiBaseUrl() != null) {
            return currentActivity.getString(R.string.app_cms_beacon_url_base, appCMSMain.getBeacon().getApiBaseUrl());
        }
        return null;
    }

    private String getBeaconUrl(String vid, String screenName, String parentScreenName,
                                long currentPosition, BeaconEvent event, boolean usingChromecast) {
        StringBuilder url = new StringBuilder();
        if (currentActivity != null && appCMSMain != null) {
            final String utfEncoding = currentActivity.getString(R.string.utf8enc);
            String uid = getInstanceId();
            int currentPositionSecs = (int) (currentPosition / MILLISECONDS_PER_SECOND);
            if (isUserLoggedIn()) {
                uid = getLoggedInUser();
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
                //Log.e(TAG, "DialogType encoding Beacon URL parameters: " + e.toString());
            }
        }
        return url.toString();
    }

    public void sendGaScreen(String screenName) {
        if (tracker != null) {
            //Log.d(TAG, "Sending GA screen tracking event: " + screenName);
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public void finalizeSignupAfterCCAvenueSubscription(Intent data) {
        /*String url = currentActivity.getString(R.string.app_cms_signin_api_url,
                appCMSMain.getApiBaseUrl(),
                appCMSSite.getGist().getSiteInternalName());
        startLoginAsyncTask(url,
                subscriptionUserEmail,
                subscriptionUserPassword,
                false,
                false,
                true,
                true,
                false);*/

        if (entitlementPendingVideoData != null) {
            isVideoPlayerStarted = false;
            sendRefreshPageAction();
            sendCloseOthersAction(null, true, false);

            AppCMSActionPresenter actionPresenter = new AppCMSActionPresenter();
            actionPresenter.setAction(entitlementPendingVideoData.action);

            launchButtonSelectedAction(entitlementPendingVideoData.pagePath,
                    actionPresenter,
                    entitlementPendingVideoData.filmTitle,
                    entitlementPendingVideoData.extraData,
                    entitlementPendingVideoData.contentDatum,
                    entitlementPendingVideoData.closeLauncher,
                    entitlementPendingVideoData.currentlyPlayingIndex,
                    entitlementPendingVideoData.relateVideoIds);
            if (entitlementPendingVideoData != null) {
                entitlementPendingVideoData.pagePath = null;
                entitlementPendingVideoData.action = null;
                entitlementPendingVideoData.filmTitle = null;
                entitlementPendingVideoData.extraData = null;
                entitlementPendingVideoData.contentDatum = null;
                entitlementPendingVideoData.closeLauncher = false;
                entitlementPendingVideoData.currentlyPlayingIndex = -1;
                entitlementPendingVideoData.relateVideoIds = null;
            }
        } else {
            sendCloseOthersAction(null, true, false);
            cancelInternalEvents();
            restartInternalEvents();

            if (TextUtils.isEmpty(getUserDownloadQualityPref())) {
                setUserDownloadQualityPref(currentActivity.getString(R.string.app_cms_default_download_quality));
            }

            if (currentContext != null && currentContext instanceof AppCMSApplication) {
                ((AppCMSApplication) currentContext).setOnActivityResumedAction(() -> {
                    navigateToHomePage();
                });
            }
        }

        setIsUserSubscribed(true);
        setActiveSubscriptionId(planToPurchase);
        setActiveSubscriptionCurrency(currencyOfPlanToPurchase);
        setActiveSubscriptionPlanName(planToPurchaseName);
        setActiveSubscriptionPrice(String.valueOf(planToPurchasePrice));
        if (currentContext != null) {
            setActiveSubscriptionProcessor(currentContext.getString(R.string.subscription_ccavenue_payment_processor_friendly));
        }
        refreshSubscriptionData(null, true);
    }

    public void finalizeSignupAfterSubscription(String receiptData) {
        setActiveSubscriptionReceipt(receiptData);
        setRestoreSubscriptionReceipt(null);

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.setPlatform(currentActivity.getString(R.string.app_cms_subscription_platform_key));
        subscriptionRequest.setSiteId(currentActivity.getString(R.string.app_cms_app_name));
        subscriptionRequest.setSubscription(currentActivity.getString(R.string.app_cms_subscription_key));
        subscriptionRequest.setPlanId(planToPurchase);
        subscriptionRequest.setPlanIdentifier(skuToPurchase);
        subscriptionRequest.setUserId(getLoggedInUser());
        subscriptionRequest.setReceipt(receiptData);

        //Log.d(TAG, "Subscription request: " + gson.toJson(subscriptionRequest, SubscriptionRequest.class));

        int subscriptionCallType = R.string.app_cms_subscription_plan_create_key;

        if (getActiveSubscriptionSku() != null) {
            subscriptionCallType = R.string.app_cms_subscription_plan_update_key;
        }

        try {
            Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
            pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(pageLoadingActionIntent);
            appCMSSubscriptionPlanCall.call(
                    currentActivity.getString(R.string.app_cms_register_subscription_api_url,
                            appCMSMain.getApiBaseUrl(),
                            appCMSSite.getGist().getSiteInternalName(),
                            currentActivity.getString(R.string.app_cms_subscription_platform_key)),
                    subscriptionCallType,
                    subscriptionRequest,
                    apikey,
                    getAuthToken(),
                    result -> {
                        //
                    },
                    appCMSSubscriptionPlanResult -> {
                        try {
                            if (appCMSSubscriptionPlanResult != null) {
                                //Log.d(TAG, "Subscription response: " + gson.toJson(appCMSSubscriptionPlanResult,
//                                        AppCMSSubscriptionPlanResult.class));
                            }
                            setActiveSubscriptionSku(skuToPurchase);
                            setActiveSubscriptionCountryCode(countryCode);
                            if (!TextUtils.isEmpty(getAppsFlyerKey())) {
                                AppsFlyerUtils.subscriptionEvent(currentActivity,
                                        true,
                                        getAppsFlyerKey(),
                                        String.valueOf(planToPurchasePrice),
                                        subscriptionRequest.getPlanId(),
                                        subscriptionRequest.getCurrencyCode());
                            }

                            System.out.println("Plan to purchase-" + planToPurchasePrice);
                            //Subscription Succes Firebase Log Event
                            Bundle bundle = new Bundle();
                            bundle.putString(FIREBASE_PLAN_ID, subscriptionRequest.getPlanId());
                            bundle.putString(FIREBASE_PLAN_NAME, planToPurchaseName);
                            bundle.putString(FIREBASE_CURRENCY_NAME, currencyOfPlanToPurchase);
                            bundle.putDouble(FIREBASE_VALUE, planToPurchasePrice);
                            if (mFireBaseAnalytics != null)
                                mFireBaseAnalytics.logEvent(FIREBASE_ECOMMERCE_PURCHASE, bundle);

                            setActiveSubscriptionId(planToPurchase);
                            setActiveSubscriptionCurrency(currencyOfPlanToPurchase);
                            setActiveSubscriptionPlanName(planToPurchaseName);
                            setActiveSubscriptionPrice(String.valueOf(planToPurchasePrice));

                            if (useCCAvenue()) {
                                //Log.d(TAG, "Initiating CCAvenue purchase");
                                initiateCCAvenuePurchase();
                            } else {
                                setActiveSubscriptionProcessor(currentActivity.getString(R.string.subscription_android_payment_processor_friendly));
                            }

                            skuToPurchase = null;
                            planToPurchase = null;
                            currencyOfPlanToPurchase = null;
                            planToPurchaseName = null;
                            planToPurchasePrice = 0.0f;
                            countryCode = "";

                            if (!isUserLoggedIn()) {
                                if ((launchType == LaunchType.SUBSCRIBE ||
                                        launchType == LaunchType.INIT_SIGNUP) &&
                                        !isSignupFromFacebook &&
                                        !isSignupFromGoogle) {
                                    String url = currentActivity.getString(R.string.app_cms_signin_api_url,
                                            appCMSMain.getApiBaseUrl(),
                                            appCMSSite.getGist().getSiteInternalName());
                                    startLoginAsyncTask(url,
                                            subscriptionUserEmail,
                                            subscriptionUserPassword,
                                            false,
                                            launchType == LaunchType.INIT_SIGNUP,
                                            true,
                                            true,
                                            false);
                                    launchType = LaunchType.LOGIN_AND_SIGNUP;
                                }
                                if (isSignupFromFacebook) {
                                    launchType = LaunchType.LOGIN_AND_SIGNUP;
                                    setFacebookAccessToken(facebookAccessToken,
                                            facebookUserId,
                                            facebookUsername,
                                            facebookEmail,
                                            true,
                                            false);
                                } else if (isSignupFromGoogle) {
                                    launchType = LaunchType.LOGIN_AND_SIGNUP;
                                    setGoogleAccessToken(googleAccessToken,
                                            googleUserId,
                                            googleUsername,
                                            googleEmail,
                                            true,
                                            false);
                                }
                            } else {
                                setIsUserSubscribed(true);
                                launchType = LaunchType.LOGIN_AND_SIGNUP;
                                if (entitlementPendingVideoData != null) {
                                    sendRefreshPageAction();
                                    if (!loginFromNavPage) {
                                        sendCloseOthersAction(null, true, !loginFromNavPage);
                                    }

                                    AppCMSActionPresenter actionPresenter = new AppCMSActionPresenter();
                                    actionPresenter.setAction(entitlementPendingVideoData.action);

                                    launchButtonSelectedAction(entitlementPendingVideoData.pagePath,
                                            actionPresenter,
                                            entitlementPendingVideoData.filmTitle,
                                            entitlementPendingVideoData.extraData,
                                            entitlementPendingVideoData.contentDatum,
                                            entitlementPendingVideoData.closeLauncher,
                                            entitlementPendingVideoData.currentlyPlayingIndex,
                                            entitlementPendingVideoData.relateVideoIds);
                                    if (entitlementPendingVideoData != null) {
                                        entitlementPendingVideoData.pagePath = null;
                                        entitlementPendingVideoData.action = null;
                                        entitlementPendingVideoData.filmTitle = null;
                                        entitlementPendingVideoData.extraData = null;
                                        entitlementPendingVideoData.contentDatum = null;
                                        entitlementPendingVideoData.closeLauncher = false;
                                        entitlementPendingVideoData.currentlyPlayingIndex = -1;
                                        entitlementPendingVideoData.relateVideoIds = null;
                                    }
                                } else {
                                    sendCloseOthersAction(null, true, false);
                                    cancelInternalEvents();
                                    restartInternalEvents();

                                    if (TextUtils.isEmpty(getUserDownloadQualityPref())) {
                                        setUserDownloadQualityPref(
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
                            subscriptionUserEmail = null;
                            subscriptionUserPassword = null;
                            facebookAccessToken = null;
                            facebookUserId = null;

                            googleAccessToken = null;
                            googleUserId = null;
                        } catch (Exception e) {
                            //Log.e(TAG, "Error getting subscription plan result: " + e.getMessage());
                        }
                    },
                    planResult -> {
                        //
                    });
        } catch (IOException e) {
            //Log.e(TAG, "Failed to update user subscription status");
            Intent stopLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
            stopLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(stopLoadingActionIntent);
        }
    }

    public List<SubscriptionPlan> availablePlans() {
        if (realmController != null) {
            try {
                return realmController.getAllSubscriptionPlans();
            } catch (Exception e) {

            }
        }
        return new ArrayList<>();
    }

    public boolean upgradesAvailableForUser() {
        if (checkUpgradeFlag) {
            return upgradesAvailable;
        }

        boolean canUpgrade = true;
        if (useCCAvenue() || "ccavenue".equalsIgnoreCase(getActiveSubscriptionProcessor())) {
            canUpgrade = "COMPLETED".equals(getSubscriptionStatus());
        }
        if (canUpgrade) {
            List<SubscriptionPlan> availablePlans = availablePlans();

            List<SubscriptionPlan> availableUpgradesForUser = new ArrayList<>();
            if (availablePlans != null) {
                for (SubscriptionPlan subscriptionPlan : availablePlans) {
                    String activeSubscriptionPriceStr = getActiveSubscriptionPrice();
                    double activeSubscriptionPrice = 0.0;
                    try {
                        activeSubscriptionPrice = Double.valueOf(activeSubscriptionPriceStr);
                    } catch (Exception e) {

                    }
                    String activeSubscriptionSku = getActiveSubscriptionSku();
                    if (TextUtils.isEmpty(activeSubscriptionSku)) {
                        activeSubscriptionSku = "";
                    }
                    if (subscriptionPlan != null &&
                            activeSubscriptionPrice < subscriptionPlan.getSubscriptionPrice() &&
                            !activeSubscriptionSku.equalsIgnoreCase(subscriptionPlan.getSku())) {
                        availableUpgradesForUser.add(subscriptionPlan);
                    }
                }
            }

            return !availableUpgradesForUser.isEmpty();
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

    private void signup(String email, String password) {
        if (currentActivity != null && !isUserLoggedIn()) {
            String url = currentActivity.getString(R.string.app_cms_signup_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSSite.getGist().getSiteInternalName());
            startLoginAsyncTask(url,
                    email,
                    password,
                    true,
                    launchType == LaunchType.SUBSCRIBE || (!isUserLoggedIn() && !TextUtils.isEmpty(getRestoreSubscriptionReceipt())),
                    false,
                    false,
                    false);
        }
    }

    public void refreshSubscriptionData(Action0 onRefreshReadyAction,
                                        boolean reloadUserSubscriptionData) {
        try {
            if (currentActivity != null && isUserLoggedIn()) {
                if (shouldRefreshAuthToken()) {
                    refreshIdentity(getRefreshToken(),
                            () -> refreshUserSubscriptionData(onRefreshReadyAction, reloadUserSubscriptionData));
                } else {
                    refreshUserSubscriptionData(onRefreshReadyAction, reloadUserSubscriptionData);
                }
            } else {
                if (onRefreshReadyAction != null) {
                    onRefreshReadyAction.call();
                }
            }
        } catch (Exception e) {
            //Log.e(TAG, "Caught exception when attempting to refresh subscription data: " + e.getMessage());
            if (onRefreshReadyAction != null) {
                onRefreshReadyAction.call();
            }
        }
    }

    private void refreshUserSubscriptionData(Action0 onRefreshReadyAction,
                                             boolean reloadUserSubscriptionData) {
        try {
            String baseUrl = appCMSMain.getApiBaseUrl();
            String endPoint = pageIdToPageAPIUrlMap.get(subscriptionPage.getPageId());
            String siteId = appCMSSite.getGist().getSiteInternalName();
            boolean usePageIdQueryParam = true;
            boolean viewPlans = true;
            boolean showPage = false;
            String apiUrl = getApiUrl(usePageIdQueryParam,
                    viewPlans,
                    showPage,
                    baseUrl,
                    endPoint,
                    siteId,
                    subscriptionPage.getPageId(),
                    false);
            getPageIdContent(apiUrl,
                    subscriptionPage.getPageId(),
                    null,
                    false,
                    appCMSPageAPI -> {
                        clearSubscriptionPlans();
                        List<SubscriptionPlan> subscriptionPlans = new ArrayList<>();
                        try {
                            for (Module module : appCMSPageAPI.getModules()) {
                                if (!TextUtils.isEmpty(module.getModuleType()) &&
                                        module.getModuleType().equals(currentActivity.getString(R.string.app_cms_view_plan_module_key))) {
                                    if (module.getContentData() != null &&
                                            !module.getContentData().isEmpty()) {
                                        for (ContentDatum contentDatum : module.getContentData()) {
                                            SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
                                            subscriptionPlan.setSku(contentDatum.getIdentifier());
                                            subscriptionPlan.setPlanId(contentDatum.getId());
                                            if (contentDatum.getPlanDetails() != null &&
                                                    !contentDatum.getPlanDetails().isEmpty() &&
                                                    contentDatum.getPlanDetails().get(0) != null &&
                                                    !TextUtils.isEmpty(contentDatum.getPlanDetails().get(0).getCountryCode())) {
                                                subscriptionPlan.setCountryCode(contentDatum.getPlanDetails().get(0).getCountryCode());
                                            }
                                            if (contentDatum.getPlanDetails() != null &&
                                                    !contentDatum.getPlanDetails().isEmpty() &&
                                                    contentDatum.getPlanDetails().get(0) != null) {
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
                            //Log.e(TAG, "Error retrieving subscription information: " + e.getMessage());
                            if (onRefreshReadyAction != null) {
                                onRefreshReadyAction.call();
                            }
                        }

                        if (reloadUserSubscriptionData) {
                            try {
                                appCMSSubscriptionPlanCall.call(
                                        currentActivity.getString(R.string.app_cms_get_current_subscription_api_url,
                                                appCMSMain.getApiBaseUrl(),
                                                getLoggedInUser(),
                                                appCMSSite.getGist().getSiteInternalName()),
                                        R.string.app_cms_subscription_subscribed_plan_key,
                                        null,
                                        apikey,
                                        getAuthToken(),
                                        listResult -> {
                                            //
                                        },
                                        singleResult -> {
                                            //
                                        },
                                        appCMSSubscriptionPlanResult -> {

                                            try {

                                                if (appCMSSubscriptionPlanResult != null) {
                                                    if (isUserSubscribed() && checkForSubscriptionCancellation(appCMSSubscriptionPlanResult)) {
                                                        sendSubscriptionCancellation();
                                                        setIsUserSubscribed(false);
                                                    }

                                                    if (urbanAirshipEventPresenter.subscriptionAboutToExpire(appCMSSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionEndDate())) {
                                                        sendUASubscriptionAboutToExpireEvent(getLoggedInUser());
                                                    } else if (isUserSubscribed()) {
                                                        sendUASubscribedEvent(getLoggedInUser());
                                                    } else {
                                                        sendUAUnsubscribedEvent(getLoggedInUser());
                                                    }
                                                    sendUASubscriptionEndDateEvent(getLoggedInUser(),
                                                            appCMSSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionEndDate());
                                                    sendUASubscriptionPlanEvent(getLoggedInUser(),
                                                            appCMSSubscriptionPlanResult.getSubscriptionInfo().getIdentifier());

                                                    UserSubscriptionPlan userSubscriptionPlan = new UserSubscriptionPlan();
                                                    userSubscriptionPlan.setUserId(getLoggedInUser());
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
                                                        setActiveSubscriptionSku(subscribedPlan.getSku());
                                                        setActiveSubscriptionId(subscribedPlan.getPlanId());
                                                        setActiveSubscriptionPlanName(subscribedPlan.getPlanName());
                                                        setActiveSubscriptionPrice(String.valueOf(subscribedPlan.getSubscriptionPrice()));
                                                        setActiveSubscriptionCountryCode(subscribedPlan.getCountryCode());
                                                    } else if (appCMSSubscriptionPlanResult.getSubscriptionPlanInfo() != null &&
                                                            appCMSSubscriptionPlanResult.getSubscriptionInfo() != null) {
                                                        setActiveSubscriptionSku(appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getIdentifier());
                                                        setActiveSubscriptionCountryCode(appCMSSubscriptionPlanResult.getSubscriptionInfo().getCountryCode());
                                                        setActiveSubscriptionId(appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getId());
                                                        setActiveSubscriptionPlanName(appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getName());
                                                        String countryCode = appCMSSubscriptionPlanResult.getSubscriptionInfo().getCountryCode();
                                                        if (appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getPlanDetails() != null)
                                                            for (PlanDetail planDetail : appCMSSubscriptionPlanResult.getSubscriptionPlanInfo().getPlanDetails()) {
                                                                if (!TextUtils.isEmpty(planDetail.getRecurringPaymentCurrencyCode()) &&
                                                                        planDetail.getCountryCode().equalsIgnoreCase(countryCode)) {
                                                                    setActiveSubscriptionPrice(String.valueOf(planDetail.getRecurringPaymentAmount()));
                                                                }
                                                            }
                                                        setActiveSubscriptionStatus(appCMSSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus());
                                                        //if (useCCAvenue() && !isSubscriptionCompleted()) {
                                                        if (!isSubscriptionCompleted()) {
                                                            StringBuilder sb = new StringBuilder("Scheduled to be cancelled by ");
                                                            String subscriptionEndDate = appCMSSubscriptionPlanResult
                                                                    .getSubscriptionInfo()
                                                                    .getSubscriptionEndDate();
                                                            int timeStampIndex = subscriptionEndDate
                                                                    .indexOf("T");
                                                            if (0 <= timeStampIndex) {
                                                                sb.append(subscriptionEndDate.substring(0, timeStampIndex));
                                                            } else {
                                                                sb.append(subscriptionEndDate);
                                                            }
                                                            setActiveSubscriptionPlanName(sb.toString());
                                                        }
                                                        //}
                                                    }

                                                    if (appCMSSubscriptionPlanResult.getSubscriptionInfo() != null) {
                                                        setSubscriptionStatus(appCMSSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus());
                                                    }

                                                    if (appCMSSubscriptionPlanResult.getSubscriptionInfo() != null &&
                                                            !TextUtils.isEmpty(appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler())) {
                                                        String paymentHandler = appCMSSubscriptionPlanResult.getSubscriptionInfo().getPaymentHandler();
                                                        if (paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor)) ||
                                                                paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ios_payment_processor_friendly))) {
                                                            setActiveSubscriptionProcessor(currentActivity.getString(R.string.subscription_ios_payment_processor_friendly));
                                                        } else if (paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor)) ||
                                                                paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_android_payment_processor_friendly))) {
                                                            setActiveSubscriptionProcessor(currentActivity.getString(R.string.subscription_android_payment_processor_friendly));
                                                        } else if (paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_web_payment_processor_friendly))) {
                                                            setActiveSubscriptionProcessor(currentActivity.getString(R.string.subscription_web_payment_processor_friendly));
                                                        } else if (paymentHandler.equalsIgnoreCase(currentActivity.getString(R.string.subscription_ccavenue_payment_processor))) {
                                                            setActiveSubscriptionProcessor(currentActivity.getString(R.string.subscription_ccavenue_payment_processor_friendly));
                                                        }
                                                    }
                                                }

                                                if (onRefreshReadyAction != null) {
                                                    onRefreshReadyAction.call();
                                                }
                                            } catch (Exception e) {
                                                //Log.e(TAG, "refreshSubscriptionData: " + e.getMessage());
                                                if (onRefreshReadyAction != null) {
                                                    onRefreshReadyAction.call();
                                                }
                                            }
                                        }
                                );
                            } catch (Exception e) {
                                //Log.e(TAG, "refreshSubscriptionData: " + e.getMessage());
                                if (onRefreshReadyAction != null) {
                                    onRefreshReadyAction.call();
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            //Log.e(TAG, "refreshSubscriptionData: " + e.getMessage());
            //Log.e(TAG, "Caught exception when attempting to refresh subscription data: " + e.getMessage());
            if (onRefreshReadyAction != null) {
                onRefreshReadyAction.call();
            }
        }
    }

    private boolean checkForSubscriptionCancellation(AppCMSUserSubscriptionPlanResult appCMSSubscriptionPlanResult) {
        try {
            ZonedDateTime nowTime = ZonedDateTime.now(UTC_ZONE_ID);
            ZonedDateTime subscriptionEndTime = ZonedDateTime.from(DateTimeFormatter.ofPattern(SUBSCRIPTION_DATE_FORMAT).parse(appCMSSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionEndDate()));
            return subscriptionEndTime.toEpochSecond() < nowTime.toEpochSecond();
        } catch (Exception e) {

        }
        return false;
    }

    public void refreshPageAPIData(AppCMSPageUI appCMSPageUI,
                                   String pageId,
                                   List<String> modules,
                                   Action1<AppCMSPageAPI> appCMSPageAPIReadyAction) {
        if (appCMSPageUI != null) {
            String baseUrl = appCMSMain.getApiBaseUrl();
            String endPoint = pageIdToPageAPIUrlMap.get(pageId);
            String siteId = appCMSSite.getGist().getSiteInternalName();
            boolean usePageIdQueryParam = true;
            boolean viewPlans = isViewPlanPage(pageId);
            boolean showPage = isShowPage(pageId);
            String apiUrl = getApiUrl(usePageIdQueryParam,
                    viewPlans,
                    showPage,
                    baseUrl,
                    endPoint,
                    siteId,
                    getPageId(appCMSPageUI),
                    appCMSPageUI != null &&
                            appCMSPageUI.getCaching() != null &&
                            !appCMSPageUI.getCaching().shouldOverrideCaching() &&
                            appCMSPageUI.getCaching().isEnabled());
            getPageViewLruCache().remove(pageId);
            getPageIdContent(apiUrl,
                    getPageId(appCMSPageUI),
                    modules,
                    appCMSPageUI != null &&
                            appCMSPageUI.getCaching() != null &&
                            !appCMSPageUI.getCaching().shouldOverrideCaching() &&
                            appCMSPageUI.getCaching().isEnabled(),
                    appCMSPageAPIReadyAction);
        }
    }

    private void login(String email, String password) {
        if (currentActivity != null) {
            Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
            pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(pageLoadingActionIntent);
            String url = currentActivity.getString(R.string.app_cms_signin_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSSite.getGist().getSiteInternalName());
            startLoginAsyncTask(url,
                    email,
                    password,
                    false,
                    (!isUserLoggedIn() && !TextUtils.isEmpty(getRestoreSubscriptionReceipt())),
                    false,
                    false,
                    true);
        }
    }

    private void callRefreshIdentity(Action0 onReadyAction) {
        if (currentActivity != null) {
            refreshIdentity(getRefreshToken(), onReadyAction);
        }
    }

    public LaunchType getLaunchType() {
        return launchType;
    }

    public void setLaunchType(LaunchType launchType) {
        this.launchType = launchType;
    }

    @SuppressWarnings("unused")
    private RealmList<SubscriptionPlan> getAvailableUpgradePlans(RealmResults<SubscriptionPlan> availablePlans) {
        RealmList<SubscriptionPlan> availableUpgrades = new RealmList<>();
        if (currentActivity != null && availablePlans != null) {
            double existingSubscriptionPrice = parseActiveSubscriptionPrice();
            String existingSku = getActiveSubscriptionSku();

            if (existingSubscriptionPrice == 0.0) {
                existingSubscriptionPrice = parseExistingGooglePlaySubscriptionPrice();
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

    @SuppressWarnings("unused")
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
                    //Log.v("anonymousToken", getAnonymousUserToken());

                    try {
                        if (signInResponse == null) {
                            // Show log error
                            //Log.e(TAG, "Email and password are not valid.");
                            if (!suppressErrorMessages) {
                                if (signup) {
                                    showDialog(DialogType.SIGNUP_PASSWORD_INVALID, currentActivity.getString(
                                            R.string.app_cms_error_user_already_exists), false, null, null);
                                } else {
                                    showDialog(DialogType.SIGNIN, currentActivity.getString(
                                            R.string.app_cms_error_email_password), false, null, null);
                                }

                            }
                            Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                            stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                            currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                        } else if (!TextUtils.isEmpty(signInResponse.getMessage()) || signInResponse.isErrorResponseSet()) {
                            if (platformType == PlatformType.TV) {
                                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                                try {
                                    openTVErrorDialog(signInResponse.getErrorResponse().getError(),
                                            signup ? currentActivity.getString(R.string.app_cms_signup).toUpperCase() :
                                                    currentActivity.getString(R.string.app_cms_login).toUpperCase());
                                } catch (Exception e) {
                                    Log.e(TAG, "DialogType launching TV DialogType Activity");
                                }
                            } else {
                                showDialog(DialogType.SIGNIN, signInResponse.getErrorResponse().getError(), false, null, null);
                            }
                            Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                            stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                            currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                        } else {
//                            String signResponseValue = gson.toJson(signInResponse, SignInResponse.class);
                            //Log.d(TAG, "Sign in response value: " + signResponseValue);
                            setRefreshToken(signInResponse.getRefreshToken());
                            setAuthToken(signInResponse.getAuthorizationToken());
                            setLoggedInUser(signInResponse.getUserId());
                            //Log.d(TAG, "Sign in user ID response: " + signInResponse.getUserId());
                            sendSignInEmailFirebase();
                            setLoggedInUserName(signInResponse.getName());
                            setLoggedInUserEmail(signInResponse.getEmail());

                            //Log.d(TAG, "Initiating subscription purchase");

                            if (followWithSubscription) {
                                isSignupFromFacebook = false;
                                isSignupFromGoogle = false;
                                subscriptionUserEmail = email;
                                subscriptionUserPassword = password;
                            }

                            if (signup) {
                                if (!TextUtils.isEmpty(getAppsFlyerKey())) {
                                    AppsFlyerUtils.registrationEvent(currentActivity, signInResponse.getUserId(),
                                            getAppsFlyerKey());
                                }
                            } else {
                                AppsFlyerUtils.loginEvent(currentActivity, signInResponse.getUserId());
                            }

                            finalizeLogin(forceSubscribed,
                                    signInResponse.isSubscribed(),
                                    followWithSubscription,
                                    refreshSubscriptionData);

                        }
                    } catch (Exception e) {
                        //Log.e(TAG, "Error retrieving sign in response: " + e.getMessage());
                        Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                        stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                    }
                }).execute(params);
    }

    private void finalizeLogin(boolean forceSubscribed,
                               boolean isUserSubscribed,
                               boolean followWithSubscription,
                               boolean refreshSubscriptionData) {
        if (forceSubscribed) {
            setIsUserSubscribed(true);
        } else {
            setIsUserSubscribed(isUserSubscribed);
        }

        //Log.d(TAG, "checkForExistingSubscription()");
        checkForExistingSubscription(false);

        populateUserHistoryData();

        //Log.d(TAG, "Initiating user login - user subscribed: " + getIsUserSubscribed());

        if (TextUtils.isEmpty(getUserDownloadQualityPref())) {
            setUserDownloadQualityPref(currentActivity.getString(R.string.app_cms_default_download_quality));
        }

        if (followWithSubscription) {
            sendCloseOthersAction(null, true, false);
            initiateItemPurchase(false);
            Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
            stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(stopPageLoadingActionIntent);
        } else {
            shouldLaunchLoginAction = true;

            populateFilmsInUserWatchlist();

            // Download the Download Page UI if it hasn't been download already
            if (downloadsAvailableForApp()) {
                String downloadPageId = getDownloadPageId();
                if (TextUtils.isEmpty(downloadPageId) &&
                        appCMSAndroid != null &&
                        appCMSAndroid.getMetaPages() != null &&
                        !appCMSAndroid.getMetaPages().isEmpty()) {
                    for (MetaPage metaPage : appCMSAndroid.getMetaPages()) {
                        if (metaPage.getPageName().contains("Downloads") && !metaPage.getPageName().contains("Settings")) {
                            setDownloadPageId(metaPage.getPageId());
                            downloadPageId = metaPage.getPageId();
                        }
                    }
                }

                AppCMSPageUI appCMSPageUI = navigationPages.get(downloadPageId);
                if (appCMSPageUI == null) {
                    MetaPage downloadMetaPage = pageIdToMetaPageMap.get(downloadPageId);
                    if (downloadMetaPage != null) {
                        getAppCMSPage(downloadMetaPage.getPageUI(),
                                appCMSPageUIResult -> {
                                    if (appCMSPageUIResult != null) {
                                        navigationPages.put(downloadMetaPage.getPageId(), appCMSPageUIResult);
                                        String action = pageNameToActionMap.get(downloadMetaPage.getPageName());
                                        if (action != null && actionToPageMap.containsKey(action)) {
                                            actionToPageMap.put(action, appCMSPageUIResult);
                                        }
                                    }
                                },
                                loadFromFile,
                                false);
                    }
                }
            }

            cacheMoviesPage();
            cacheShowsPage();

            //Log.d(TAG, "Logging in");
            if (appCMSMain.getServiceType()
                    .equals(currentActivity.getString(R.string.app_cms_main_svod_service_type_key)) &&
                    refreshSubscriptionData) {
                checkUpgradeFlag = false;
                refreshSubscriptionData(() -> {
                    if (entitlementPendingVideoData != null) {
                        sendRefreshPageAction();
                        if (!loginFromNavPage) {
                            sendCloseOthersAction(null, true, !loginFromNavPage);
                        }

                        AppCMSActionPresenter actionPresenter = new AppCMSActionPresenter();
                        actionPresenter.setAction(entitlementPendingVideoData.action);

                        launchButtonSelectedAction(entitlementPendingVideoData.pagePath,
                                actionPresenter,
                                entitlementPendingVideoData.filmTitle,
                                entitlementPendingVideoData.extraData,
                                entitlementPendingVideoData.contentDatum,
                                entitlementPendingVideoData.closeLauncher,
                                entitlementPendingVideoData.currentlyPlayingIndex,
                                entitlementPendingVideoData.relateVideoIds);
                    } else {
                        try {
                            getPageViewLruCache().evictAll();
                        } catch (Exception e) {
                            //
                        }

                        clearPageAPIData(() -> {
                            if (!loginFromNavPage) {
                                sendCloseOthersAction(null, true, !loginFromNavPage);
                            }
                            cancelInternalEvents();
                            restartInternalEvents();

                            if (TextUtils.isEmpty(getUserDownloadQualityPref())) {
                                setUserDownloadQualityPref(currentActivity.getString(R.string.app_cms_default_download_quality));
                            }

                            if (loginFromNavPage) {
                                NavigationPrimary homePageNavItem = findHomePageNavItem();
                                if (homePageNavItem != null) {
                                    cancelInternalEvents();
                                    if (platformType == PlatformType.ANDROID) {
                                        navigateToPage(homePageNavItem.getPageId(),
                                                homePageNavItem.getTitle(),
                                                homePageNavItem.getUrl(),
                                                false,
                                                true,
                                                false,
                                                true,
                                                true,
                                                deeplinkSearchQuery);
                                    } else if (platformType == PlatformType.TV) {
                                        if (getLaunchType() == LaunchType.LOGIN_AND_SIGNUP) {
                                            Intent myProfileIntent = new Intent(CLOSE_DIALOG_ACTION);
                                            myProfileIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                            currentActivity.sendBroadcast(myProfileIntent);
                                        } else if (getLaunchType() == LaunchType.HOME) {
                                            navigateToTVPage(
                                                    homePageNavItem.getPageId(),
                                                    homePageNavItem.getTitle(),
                                                    homePageNavItem.getUrl(),
                                                    false,
                                                    deeplinkSearchQuery,
                                                    true,
                                                    false,
                                                    false
                                            );
                                        }
                                    }
                                }

                                if (currentActivity != null) {
                                    Apptentive.engage(currentActivity, currentActivity.getString(R.string.app_cms_apptentive_login_event_name));
                                }
                            }
                        }, false);
                    }
//                    currentActivity.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION));
                }, true);
            } else {
                clearPageAPIData(() -> {
                }, false);
                if (entitlementPendingVideoData != null) {
                    sendRefreshPageAction();
                    if (!loginFromNavPage) {
                        sendCloseOthersAction(null, true, !loginFromNavPage);
                    }

                    AppCMSActionPresenter actionPresenter = new AppCMSActionPresenter();
                    actionPresenter.setAction(entitlementPendingVideoData.action);

                    launchButtonSelectedAction(entitlementPendingVideoData.pagePath,
                            actionPresenter,
                            entitlementPendingVideoData.filmTitle,
                            entitlementPendingVideoData.extraData,
                            entitlementPendingVideoData.contentDatum,
                            entitlementPendingVideoData.closeLauncher,
                            entitlementPendingVideoData.currentlyPlayingIndex,
                            entitlementPendingVideoData.relateVideoIds);
                    if (entitlementPendingVideoData != null) {
                        entitlementPendingVideoData.pagePath = null;
                        entitlementPendingVideoData.action = null;
                        entitlementPendingVideoData.filmTitle = null;
                        entitlementPendingVideoData.extraData = null;
                        entitlementPendingVideoData.contentDatum = null;
                        entitlementPendingVideoData.closeLauncher = false;
                        entitlementPendingVideoData.currentlyPlayingIndex = -1;
                        entitlementPendingVideoData.relateVideoIds = null;
                    }
                } else {
                    if (!loginFromNavPage) {
                        sendCloseOthersAction(null, true, !loginFromNavPage);
                    }
                    cancelInternalEvents();
                    restartInternalEvents();

                    if (TextUtils.isEmpty(getUserDownloadQualityPref())) {
                        setUserDownloadQualityPref(currentActivity.getString(R.string.app_cms_default_download_quality));
                    }

                    if (loginFromNavPage) {
                        NavigationPrimary homePageNavItem = findHomePageNavItem();
                        if (homePageNavItem != null) {
                            cancelInternalEvents();

                            if (platformType == PlatformType.TV) {
                                if (getLaunchType() == LaunchType.LOGIN_AND_SIGNUP) {
                                    Intent myProfileIntent = new Intent(CLOSE_DIALOG_ACTION);
                                    myProfileIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                    currentActivity.sendBroadcast(myProfileIntent);
                                } else if (getLaunchType() == LaunchType.HOME) {
                                    navigateToTVPage(
                                            homePageNavItem.getPageId(),
                                            homePageNavItem.getTitle(),
                                            homePageNavItem.getUrl(),
                                            false,
                                            deeplinkSearchQuery,
                                            true,
                                            false,
                                            false
                                    );
                                }
                            } else {
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
                        if (currentActivity != null) {
                            Apptentive.engage(currentActivity, currentActivity.getString(R.string.app_cms_apptentive_login_event_name));
                        }
                    }
                }
                if (platformType.equals(PlatformType.ANDROID)) {
                    Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                    stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                    currentActivity.sendBroadcast(stopPageLoadingActionIntent);
                }
            }

            sendUALoggedInEvent(getLoggedInUser());
        }
    }

    private void refreshIdentity(final String refreshToken, final Action0 onReadyAction) {
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

                                setLoggedInUser(refreshIdentityResponse.getId());
                                setRefreshToken(refreshIdentityResponse.getRefreshToken());
                                setAuthToken(refreshIdentityResponse.getAuthorizationToken());
                                onReadyAction.call();
                            } else {
                                onReadyAction.call();
                            }
                        } catch (Exception e) {
                            //Log.e(TAG, "Error retrieving refresh identity response: " + e.getMessage());
                            onReadyAction.call();
                        }
                    }).execute(params);
        }
    }

    private void askForPermissionToDownloadToExternalStorage(boolean checkToShowPermissionRationale,
                                                             final ContentDatum contentDatum,
                                                             final Action1<UserVideoDownloadStatus> resultAction1) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                                    //Log.e(TAG, "Error handling request permissions result: " + e.getMessage());
                                }
                            },
                            null);
                } else {
                    ActivityCompat.requestPermissions(currentActivity,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_EXTERNAL_STORAGE_FOR_DOWNLOADS);
                }
            }
        }
    }

    private boolean hasWriteExternalStoragePermission() {
        if (currentActivity != null) {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    (ContextCompat.checkSelfPermission(currentActivity,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
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
        return appCMSMain != null
                && jsonValueKeyMap.get(appCMSMain.getServiceType()) == AppCMSUIKeyType.MAIN_SVOD_SERVICE_TYPE;
    }

    public void setNavItemToCurrentAction(Activity activity) {
        if (activity != null && currentActions != null && !currentActions.isEmpty()) {
            Intent setNavigationItemIntent = new Intent(PRESENTER_RESET_NAVIGATION_ITEM_ACTION);
            setNavigationItemIntent.putExtra(activity.getString(R.string.navigation_item_key),
                    currentActions.peek());
            setNavigationItemIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            activity.sendBroadcast(setNavigationItemIntent);
        }
    }

    public AppCompatActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(AppCompatActivity activity) {
        this.currentActivity = activity;
        this.downloadManager = (DownloadManager) currentActivity.getSystemService(Context.DOWNLOAD_SERVICE);
        this.downloadQueueThread = new DownloadQueueThread(this);
        String clientId = activity.getString(R.string.default_web_client_id);
        this.serverClientId = activity.getString(R.string.server_client_id);
        try {
            this.realmController = RealmController.with(currentActivity);
        } catch (Exception e) {
            Log.d(TAG,e.getMessage());

            e.printStackTrace();
        }

        if (bitmapCachePresenter == null) {
            bitmapCachePresenter = new BitmapCachePresenter(activity, activity.getSupportFragmentManager());
        }
    }

    public Bitmap getBitmapFromCache(String url) {
        if (bitmapCachePresenter != null) {
            return bitmapCachePresenter.getBitmapFromMemCache(url);
        }
        return null;
    }

    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (bitmapCachePresenter != null && currentContext != null) {
            bitmapCachePresenter.addBitmapToCache(currentContext, url, bitmap);
        }
    }

    public void setCurrentContext(Context context) {
        this.currentContext = context;
        try {
            this.cachedAPIUserToken = context.getString(R.string.app_cms_cached_api_user_token);
        } catch (Exception e) {

        }
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
        AppCMSDownloadQualityBinder appCMSDownloadQualityBinder = new AppCMSDownloadQualityBinder(appCMSMain,
                appCMSPageUI,
                appCMSPageAPI,
                pageId,
                pageName,
                screenName,
                loadedFromFile,
                appbarPresent,
                fullScreenEnabled,
                navbarPresent,
                isUserLoggedIn(),
                jsonValueKeyMap,
                contentDatum,
                resultAction);
        new SoftReference<>(appCMSDownloadQualityBinder, referenceQueue);
        return appCMSDownloadQualityBinder;
    }

    @SuppressWarnings("unused")
    public void searchRetryDialog(String searchTerm) {
        RetryCallBinder retryCallBinder = getRetryCallBinder(null, null,
                searchTerm, null,
                null, false,
                null, SEARCH_RETRY_ACTION
        );
        Bundle bundle = new Bundle();
        bundle.putBoolean(currentActivity.getString(R.string.retry_key), true);
        bundle.putBoolean(currentActivity.getString(R.string.register_internet_receiver_key), true);
        bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
        Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
        args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
        args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
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
        AppCMSBinder appCMSBinder = new AppCMSBinder(appCMSMain,
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
                isUserLoggedIn(),
                isUserSubscribed(),
                extraScreenType,
                jsonValueKeyMap,
                searchQuery,
                appCMSSearchCall);
        new SoftReference<>(appCMSBinder, referenceQueue);
        return appCMSBinder;
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

        AppCMSVideoPageBinder appCMSVideoPageBinder = new AppCMSVideoPageBinder(
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
                isUserLoggedIn(),
                isUserSubscribed(),
                relatedVideoIds,
                currentlyPlayingIndex,
                isOffline);
        new SoftReference<>(appCMSVideoPageBinder, referenceQueue);
        return appCMSVideoPageBinder;
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
        if (!cancelAllLoads) {
            try {
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
                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
            } catch (Exception e) {
                //Log.e(TAG, "Error launching page activity: " + pageName);
                showDialog(DialogType.NETWORK, null, false, null, null);
            }
        }
    }

    public void launchBlankPage() {
        Log.w(TAG, "Launching blank page");
        if (currentActivity != null) {
            if (!(currentActivity instanceof AppCMSPageActivity)) {
                Bundle args = getPageActivityBundle(currentActivity,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        true,
                        false,
                        true,
                        false,
                        null,
                        ExtraScreenType.BLANK);
                Intent appCMSIntent = new Intent(currentActivity, AppCMSPageActivity.class);
                appCMSIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key), args);
                appCMSIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                currentActivity.startActivity(appCMSIntent);
            }
        }
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
                                        AppCMSVideoPageBinder binder,
                                        Action1<Object> action1) {
        if (currentActivity instanceof AppCMSPlayVideoActivity) {
            ((AppCMSPlayVideoActivity) currentActivity).closePlayer();
        } else if (platformType == PlatformType.TV) {
            action1.call(null);
        }
        if (!cancelAllLoads) {
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
            Intent intent;
            if (platformType == PlatformType.ANDROID) {
                intent = new Intent(currentActivity, AutoplayActivity.class);
                intent.putExtra(currentActivity.getString(R.string.app_cms_video_player_bundle_binder_key), args);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                currentActivity.startActivity(intent);
            } else {
                try {
                    String tvAutoplayActivityPackage = "com.viewlift.tv.views.activity.AppCMSTVAutoplayActivity";
                    intent = new Intent(currentActivity, Class.forName(tvAutoplayActivityPackage));
                    intent.putExtra(currentActivity.getString(R.string.app_cms_video_player_bundle_binder_key), args);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    currentActivity.startActivity(intent);
                } catch (ClassNotFoundException e) {

                }
            }
        }
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
        if (!cancelAllLoads) {

            Bundle args = new Bundle();
            args.putBinder(activity.getString(R.string.app_cms_download_setting_binder_key), binder);
            Intent intent = new Intent(currentActivity, AppCMSDownloadQualityActivity.class);
            intent.putExtra(currentActivity.getString(R.string.app_cms_download_setting_bundle_key), args);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            currentActivity.startActivity(intent);
        }
    }

    private void getAppCMSSite(final PlatformType platformType) {
        Log.w(TAG, "Attempting to retrieve site.json");
        if (currentActivity != null) {
            //Log.d(TAG, "Retrieving site.json");
            String url = currentActivity.getString(R.string.app_cms_site_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSMain.getDomainName());
            new GetAppCMSSiteAsyncTask(appCMSSiteCall,
                    appCMSSite -> {
                        try {
                            if (appCMSSite != null) {
                                this.appCMSSite = appCMSSite;

                                appCMSSearchUrlComponent = DaggerAppCMSSearchUrlComponent.builder()
                                        .appCMSSearchUrlModule(new AppCMSSearchUrlModule(appCMSMain.getApiBaseUrl(),
                                                appCMSSite.getGist().getSiteInternalName(),
                                                apikey,
                                                appCMSSearchCall))
                                        .build();
                                switch (platformType) {
                                    case ANDROID:
                                        getAppCMSAndroid(0);
                                        break;

                                    case TV:
                                        getAppCMSTV(0);
                                        break;

                                    default:
                                        break;
                                }
                            } else {
                                launchErrorActivity(platformType);
                            }
                        } catch (Exception e) {
                            //Log.e(TAG, "Error retrieving AppCMS Site Info: " + e.getMessage());
                            launchErrorActivity(platformType);
                        }
                    }).execute(url, !isNetworkConnected());
        } else {
            launchBlankPage();
        }
    }

    public void refreshPages(Action1<Boolean> onReadyAction,
                             boolean attemptRetry,
                             int retryAttempts,
                             int maxRetryAttempts) {
        //Log.d(TAG, "Refreshing pages");
        if (currentActivity != null) {
            if (appCMSMain != null) {
                Log.d(TAG, "Refreshing main.json version: " + appCMSMain.getVersion());
            }

            try {
                refreshAppCMSMain((appCMSMainUpdated) -> {
                    if (appCMSMainUpdated != null && appCMSMain != null) {
                        //Log.d(TAG, "Refreshed main.json");
                        Log.d(TAG, "Current main.json version: " + appCMSMain.getVersion());
                        Log.d(TAG, "Received main.json version: " + appCMSMainUpdated.getVersion());
                        if (appCMSMainUpdated.getVersion().equals(appCMSMain.getVersion()) &&
                                attemptRetry &&
                                retryAttempts < maxRetryAttempts) {
                            refreshPages(onReadyAction,
                                    attemptRetry,
                                    retryAttempts + 1,
                                    maxRetryAttempts);
                        } else if (!appCMSMainUpdated.getVersion().equals(appCMSMain.getVersion())) {
                            Log.d(TAG, "Reloading page data");
                            this.appCMSMain = appCMSMainUpdated;
                            try {
                                refreshAppCMSAndroid((appCMSAndroid) -> {
                                    if (appCMSAndroid != null) {
                                        Log.d(TAG, "Clearing Page and API cache");
                                        clearPageAPIData(() -> {
                                            final int numPages = appCMSAndroid.getMetaPages().size();
                                            for (int i = 0; i < numPages ; i++) {
                                                final MetaPage metaPage = appCMSAndroid.getMetaPages().get(i);
                                                numPagesProcessed = 0;
                                                //Log.d(TAG, "Refreshed module page: " + metaPage.getPageName() +
//                                                " " +
//                                                metaPage.getPageId() +
//                                                " " +
//                                                metaPage.getPageUI());
                                                if (currentActivity != null) {
                                                    try {
                                                        getAppCMSPage(metaPage.getPageUI(),
                                                                appCMSPageUI -> {
                                                                    numPagesProcessed++;
                                                                    if (appCMSPageUI != null) {
                                                                        if (appCMSPageUI.getCaching() != null) {
                                                                            appCMSPageUI.getCaching().setOverrideCaching(true);
                                                                            new GetAppCMSPageUIAsyncTask(appCMSPageUICall, null).writeToFile(appCMSPageUI, metaPage.getPageUI());
                                                                        }
                                                                        navigationPages.put(metaPage.getPageId(), appCMSPageUI);

                                                                        String action = pageNameToActionMap.get(metaPage.getPageName());
                                                                        if (action != null && actionToPageMap.containsKey(action)) {
                                                                            actionToPageMap.put(action, appCMSPageUI);
                                                                        }
                                                                    }
                                                                    try {
                                                                        if (numPagesProcessed == numPages) {
                                                                            numPagesProcessed = 0;
                                                                            getAppCMSModules(appCMSAndroid,
                                                                                    true,
                                                                                    true,
                                                                                    (appCMSAndroidModules) -> {
                                                                                        if (appCMSAndroidModules != null) {
                                                                                            //Log.d(TAG, "Received and refreshed module list");
                                                                                            this.appCMSAndroidModules = appCMSAndroidModules;
                                                                                            if (appCMSAndroidModules.isLoadedFromNetwork() &&
                                                                                                    pageViewLruCache != null) {
                                                                                                Log.d(TAG, "Clearing page cache");
                                                                                                try {
                                                                                                    pageViewLruCache.evictAll();
                                                                                                } catch (Exception e) {

                                                                                                }
                                                                                            }

                                                                                            Log.d(TAG, "Refreshing API Data");
                                                                                        }

                                                                                        if (onReadyAction != null) {
                                                                                            onReadyAction.call(true);
                                                                                        }
                                                                                    });
                                                                        }
                                                                    } catch (Exception e) {
                                                                        //Log.e(TAG, "Failed to refresh AppCMS modules: " +
//                                            e.getMessage());
                                                                    }
                                                                },
                                                                false,
                                                                true);
                                                    } catch (Exception e) {
                                                        //Log.e(TAG, "Failed to refresh AppCMS page " +
//                                                    metaPage.getPageName() +
//                                                " " +
//                                                e.getMessage());
                                                    }
                                                }
                                            }
                                        }, false);
                                    }
                                });
                            } catch (Exception e) {
                                //Log.e(TAG, "Failed to refresh android.json: " + e.getMessage());
                            }
                        } else {
                            if (onReadyAction != null) {
                                onReadyAction.call(false);
                            }
                        }
                    } else {
                        Log.w(TAG, "Resulting main.json from refresh is null");
                        if (onReadyAction != null) {
                            onReadyAction.call(false);
                        }
                    }
                });
            } catch (Exception e) {
                //Log.e(TAG, "Failed to refresh main.json: " + e.getMessage());
            }
        } else {
            //Log.w(TAG, "Current activity is null, can not refresh page data");
        }
    }

    public void updateAppCMSMain(AppCMSMain appCMSMain) {
        this.appCMSMain = appCMSMain;
    }

    public void refreshAppCMSMain(Action1<AppCMSMain> readyAction) {
        if (currentActivity != null) {
            if (shouldRefreshAuthToken()) {
                refreshIdentity(getRefreshToken(),
                        () -> {
                            try {
                                GetAppCMSMainUIAsyncTask.Params params = new GetAppCMSMainUIAsyncTask.Params.Builder()
                                        .context(currentActivity)
                                        .siteId(currentActivity.getString(R.string.app_cms_app_name))
                                        .bustCache(true)
                                        .build();
                                new GetAppCMSMainUIAsyncTask(appCMSMainUICall, main -> {
                                    if (readyAction != null && main != null) {
                                        Log.d(TAG, "Refreshed main.json with update version: " + main.getVersion());
                                        Log.d(TAG, "Notifying listeners that main.json has been updated");
                                        Observable.just(main)
                                                .onErrorResumeNext(throwable -> Observable.empty())
                                                .subscribe(readyAction);
                                    }
                                }).execute(params);
                            } catch (Exception e) {
                                Log.e(TAG, "Error retrieving main.json: " + e.getMessage());
                                Observable.just((AppCMSMain) null)
                                        .onErrorResumeNext(throwable -> Observable.empty())
                                        .subscribe(readyAction);
                            }
                        });
            } else {
                try {
                    GetAppCMSMainUIAsyncTask.Params params = new GetAppCMSMainUIAsyncTask.Params.Builder()
                            .context(currentActivity)
                            .siteId(currentActivity.getString(R.string.app_cms_app_name))
                            .bustCache(true)
                            .build();
                    new GetAppCMSMainUIAsyncTask(appCMSMainUICall, main -> {
                        Log.d(TAG, "Refreshed main.json");
                        if (readyAction != null) {
                            Log.d(TAG, "Notifying listeners that main.json has been updated");
                            Observable.just(main)
                                    .onErrorResumeNext(throwable -> Observable.empty())
                                    .subscribe(readyAction);
                        }
                    }).execute(params);
                } catch (Exception e) {
                    Log.e(TAG, "Error retrieving main.json: " + e.getMessage());
                    Observable.just((AppCMSMain) null)
                            .onErrorResumeNext(throwable -> Observable.empty())
                            .subscribe(readyAction);
                }
            }
        }
    }

    private void refreshAppCMSAndroid(Action1<AppCMSAndroidUI> readyAction) {
        if (currentActivity != null) {
            GetAppCMSAndroidUIAsyncTask.Params params =
                    new GetAppCMSAndroidUIAsyncTask.Params.Builder()
                            .url(appCMSMain.getAndroid())
                            .loadFromFile(false)
                            .bustCache(true)
                            .build();
            try {
                new GetAppCMSAndroidUIAsyncTask(appCMSAndroidUICall, appCMSAndroidUI -> {
                    //Log.d(TAG, "Refreshed android.json");
                    if (readyAction != null) {
                        //Log.d(TAG, "Notifying listeners that android.json has been updated");
                        if (appCMSAndroidUI != null) {
                            Observable.just(appCMSAndroidUI)
                                    .onErrorResumeNext(throwable -> Observable.empty())
                                    .subscribe(readyAction);
                        } else {
                            Observable.just((AppCMSAndroidUI) null)
                                    .onErrorResumeNext(throwable -> Observable.empty())
                                    .subscribe(readyAction);
                        }
                    }
                }).execute(params);
            } catch (Exception e) {
                //Log.e(TAG, "Error retrieving android.json: " + e.getMessage());
                Observable.just((AppCMSAndroidUI) null)
                        .onErrorResumeNext(throwable -> Observable.empty())
                        .subscribe(readyAction);
            }
        }
    }

    private void getAppCMSAndroid(int tryCount) {
        Log.w(TAG, "Attempting to retrieve android.json");

        try {
            GetAppCMSAndroidUIAsyncTask.Params params =
                    new GetAppCMSAndroidUIAsyncTask.Params.Builder()
                            .url(appCMSMain.getAndroid())
                            .loadFromFile(appCMSMain.shouldLoadFromFile())
                            .bustCache(false)
                            .build();
//                    Log.d(TAG, "Params: " + appCMSMain.getAndroid() + " " + loadFromFile);
            new GetAppCMSAndroidUIAsyncTask(appCMSAndroidUICall, appCMSAndroidUI -> {
                try {
                    if (appCMSAndroidUI == null ||
                            appCMSAndroidUI.getMetaPages() == null ||
                            appCMSAndroidUI.getMetaPages().isEmpty()) {
                        //Log.e(TAG, "AppCMS keys for pages for appCMSAndroid not found");
                        if (tryCount < 2) {
                            getAppCMSAndroid(tryCount + 1);
                        } else {
                            launchBlankPage();
                        }
                    } else if (isAppBelowMinVersion()) {
                        //Log.e(TAG, "AppCMS current application version is below the minimum version supported");
                        launchUpgradeAppActivity();
                    } else {
                        this.appCMSAndroid = appCMSAndroidUI;
                        this.navigation = appCMSAndroidUI.getNavigation();
                        new SoftReference<>(navigation, referenceQueue);
                        queueMetaPages(appCMSAndroidUI.getMetaPages());
                        //Log.d(TAG, "Processing meta pages queue");

                        launchBlankPage();

                        getAppCMSModules(appCMSAndroidUI,
                                false,
                                false,
                                (appCMSAndroidModules) -> {
                                    //Log.d(TAG, "Received module list");
                                    this.appCMSAndroidModules = appCMSAndroidModules;
                                    this.processedUIModules = true;
                                    if (processedUIModules && processedUIPages) {
                                        finalizeLaunch(tryCount);
                                    }
                                });

                        MetaPage launchPage = homePage;
                        if (appCMSMain.isForceLogin() && !isUserLoggedIn()) {
                            launchPage = loginPage;
                        }

                        if (launchPage != null) {
                            pageIdToPageAPIUrlMap.put(launchPage.getPageId(), launchPage.getPageAPI());
                            pageIdToPageNameMap.put(launchPage.getPageId(), launchPage.getPageName());

                            final MetaPage launchPageFinal = launchPage;

                            getAppCMSPage(launchPage.getPageUI(),
                                    appCMSPageUI -> {
                                        navigationPages.put(launchPageFinal.getPageId(), appCMSPageUI);
                                        String action = pageNameToActionMap.get(launchPageFinal.getPageName());
                                        if (action != null && actionToPageMap.containsKey(action)) {
                                            actionToPageMap.put(action, appCMSPageUI);
                                        }

                                        processedUIPages = true;

                                        if (processedUIModules && processedUIPages) {
                                            finalizeLaunch(tryCount);
                                        }
                                    },
                                    loadFromFile,
                                    false);

                            if (launchPageFinal == homePage) {
                                cacheHomePage();
                            }
                        } else {
                            processMetaPagesList(loadFromFile,
                                    appCMSAndroidUI.getMetaPages(),
                                    () -> {
                                        this.processedUIPages = true;
                                        if (processedUIModules && processedUIPages) {
                                            finalizeLaunch(tryCount);
                                        }
                                    });
                        }
                    }
                } catch (Exception e) {
                    //Log.e(TAG, "Error processing meta pages queue: " + e.getMessage());
                    launchBlankPage();
                }
            }).execute(params);
        } catch (Exception e) {
            //Log.e(TAG, "Failed to load Android json file: " + e.getMessage());
            launchBlankPage();
        }
    }

    private void finalizeLaunch(int tryCount) {
        Log.w(TAG, "Finalizing launch");
        if (!isNetworkConnected()) {
            openDownloadScreenForNetworkError(true,
                    () -> getAppCMSAndroid(tryCount));
        } else {
            if (isUserLoggedIn()) {
                //Log.d(TAG, "Updating logged in user data");
                getUserData(userIdentity -> {
                    try {
                        if (userIdentity != null) {
                            //Log.d(TAG, "Retrieved valid user identity");
                            setLoggedInUser(userIdentity.getUserId());
                            setLoggedInUserEmail(userIdentity.getEmail());
                            setLoggedInUserName(userIdentity.getName());
                            setIsUserSubscribed(userIdentity.isSubscribed());
                            if (!userIdentity.isSubscribed()) {
                                setActiveSubscriptionProcessor(null);
                            }
                        }
                    } catch (Exception e) {
                        //Log.e(TAG, "Error refreshing identity while attempting to retrieving AppCMS Android data: " +
//                                e.getMessage());
                        launchBlankPage();
                    }
                });

                populateUserHistoryData();
            } else {
                signinAnonymousUser();
            }

            if (appCMSMain.isForceLogin() && !isUserLoggedIn()) {
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
                    //Log.e(TAG, "Failed to launch page: "
//                                                                        + loginPage.getPageName());
                    launchBlankPage();
                }
            } else {
                boolean launchSuccess = navigateToPage(homePage.getPageId(),
                        homePage.getPageName(),
                        homePage.getPageUI(),
                        false,
                        true,
                        false,
                        true,
                        false,
                        deeplinkSearchQuery);
                if (!launchSuccess) {
                    //Log.e(TAG, "Failed to launch page: "
//                                                                        + loginPage.getPageName());
                    launchBlankPage();
                }
            }
        }
    }

    public void initializeAppCMSAnalytics() {
        if (appCMSAndroid != null) {
            initializeGA(appCMSAndroid.getAnalytics().getGoogleAnalyticsId());
            initAppsFlyer(appCMSAndroid);
        }
    }

    private void getAppCMSModules(AppCMSAndroidUI appCMSAndroidUI,
                                  boolean forceLoadFromNetwork,
                                  boolean bustCache,
                                  Action1<AppCMSAndroidModules> readyAction) {
        Log.w(TAG, "Attempting to retrieve modules.json");
        if (currentActivity != null) {
            appCMSAndroidModuleCall.call(appCMSAndroidUI.getBlocksBundleUrl(),
                    appCMSAndroidUI.getVersion(),
                    forceLoadFromNetwork,
                    bustCache,
                    readyAction);
        }
    }

    private void getAppCMSPage(String url,
                               final Action1<AppCMSPageUI> onPageReady,
                               boolean loadFromFile,
                               boolean bustCache) {
        GetAppCMSPageUIAsyncTask.Params params =
                new GetAppCMSPageUIAsyncTask.Params.Builder()
                        .url(url)
                        .bustCache(bustCache)
                        .loadFromFile(loadFromFile)
                        .build();
        new GetAppCMSPageUIAsyncTask(appCMSPageUICall, onPageReady).execute(params);
    }

    private void queueMetaPages(List<MetaPage> metaPageList) {
        if (pagesToProcess == null) {
            pagesToProcess = new ConcurrentLinkedQueue<>();
        }

        if (!metaPageList.isEmpty()) {
            for (MetaPage metaPage : metaPageList) {
                pageIdToMetaPageMap.put(metaPage.getPageId(), metaPage);
                pageIdToPageAPIUrlMap.put(metaPage.getPageId(), metaPage.getPageAPI());
                pageIdToPageNameMap.put(metaPage.getPageId(), metaPage.getPageName());

                String action = pageNameToActionMap.get(metaPage.getPageName());
                if (action != null && actionToPageMap.containsKey(action)) {
                    actionToPageNameMap.put(action, metaPage.getPageName());
                    actionToPageAPIUrlMap.put(action, metaPage.getPageAPI());
                    actionTypeToMetaPageMap.put(actionToActionTypeMap.get(action), metaPage);
                }

                if (jsonValueKeyMap.get(metaPage.getPageName())
                        == AppCMSUIKeyType.ANDROID_AUTH_SCREEN_KEY) {
                    loginPage = metaPage;
                    new SoftReference<Object>(loginPage, referenceQueue);
                }

                if (jsonValueKeyMap.get(metaPage.getPageName())
                        == AppCMSUIKeyType.ANDROID_DOWNLOAD_SETTINGS_KEY) {
                    downloadQualityPage = metaPage;
                    new SoftReference<Object>(downloadQualityPage, referenceQueue);
                }

                if (jsonValueKeyMap.get(metaPage.getPageName())
                        == AppCMSUIKeyType.ANDROID_DOWNLOAD_KEY) {
                    downloadPage = metaPage;
                    new SoftReference<Object>(downloadPage, referenceQueue);
                }

                if (jsonValueKeyMap.get(metaPage.getPageName())
                        == AppCMSUIKeyType.ANDROID_HOME_SCREEN_KEY) {
                    homePage = metaPage;
                    new SoftReference<Object>(homePage, referenceQueue);
                }

                if (jsonValueKeyMap.get(metaPage.getPageName())
                        == AppCMSUIKeyType.ANDROID_MOVIES_SCREEN_KEY) {
                    moviesPage = metaPage;
                    new SoftReference<Object>(moviesPage, referenceQueue);
                }

                if (jsonValueKeyMap.get(metaPage.getPageName())
                        == AppCMSUIKeyType.ANDROID_SHOWS_SCREEN_KEY) {
                    showsPage = metaPage;
                    new SoftReference<Object>(showsPage, referenceQueue);
                }

                if (jsonValueKeyMap.get(metaPage.getPageName())
                        == AppCMSUIKeyType.ANDROID_SUBSCRIPTION_SCREEN_KEY) {
                    subscriptionPage = metaPage;
                    new SoftReference<Object>(subscriptionPage, referenceQueue);
                }

                if (jsonValueKeyMap.get(metaPage.getPageName())
                        == AppCMSUIKeyType.ANDROID_HISTORY_SCREEN_KEY) {
                    splashPage = metaPage;
                    new SoftReference<Object>(splashPage, referenceQueue);
                }

                if (jsonValueKeyMap.get(metaPage.getPageName())
                        == AppCMSUIKeyType.ANDROID_WATCHLIST_SCREEN_KEY) {
                    watchlistPage = metaPage;
                    new SoftReference<Object>(watchlistPage, referenceQueue);
                }
            }

            if (jsonValueKeyMap.get(appCMSMain.getServiceType()) == AppCMSUIKeyType.MAIN_SVOD_SERVICE_TYPE
                    && !isUserLoggedIn()) {
                launchType = LaunchType.LOGIN_AND_SIGNUP;
            }

            pagesToProcess.addAll(metaPageList);
        }
    }

    public void cacheHomePage() {
        if (homePage != null && appCMSMain != null && appCMSSite != null) {
            String baseUrl = appCMSMain.getApiBaseUrl();
            String endPoint = homePage.getPageAPI();
            String siteId = appCMSSite.getGist().getSiteInternalName();

            // Cache home page when the app is loading
            getPageIdContent(getApiUrl(true,
                    false,
                    false,
                    baseUrl,
                    endPoint,
                    siteId,
                    homePage.getPageId(),
                    !TextUtils.isEmpty(appCMSMain.getApiBaseUrlCached())),
                    homePage.getPageId(),
                    null,
                    !TextUtils.isEmpty(appCMSMain.getApiBaseUrlCached()),
                    null);
        }
    }

    public void cacheMoviesPage() {
        if (moviesPage != null && appCMSMain != null && appCMSSite != null) {
            String pageId = moviesPage.getPageId();
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            if (appCMSPageUI == null) {
                MetaPage metaPage = pageIdToMetaPageMap.get(pageId);
                if (metaPage != null) {
                    getAppCMSPage(metaPage.getPageUI(),
                            appCMSPageUIResult -> {
                                if (appCMSPageUIResult != null) {
                                    navigationPages.put(metaPage.getPageId(), appCMSPageUIResult);
                                    String action = pageNameToActionMap.get(metaPage.getPageName());
                                    if (action != null && actionToPageMap.containsKey(action)) {
                                        actionToPageMap.put(action, appCMSPageUIResult);
                                    }
                                }
                            },
                            loadFromFile,
                            false);
                }
            }

            String baseUrl = appCMSMain.getApiBaseUrl();
            String endPoint = moviesPage.getPageAPI();
            String siteId = appCMSSite.getGist().getSiteInternalName();

            // Cache movies page when the app is loading
            getPageIdContent(getApiUrl(true,
                    false,
                    false,
                    baseUrl,
                    endPoint,
                    siteId,
                    moviesPage.getPageId(),
                    !TextUtils.isEmpty(appCMSMain.getApiBaseUrlCached())),
                    moviesPage.getPageId(),
                    null,
                    !TextUtils.isEmpty(appCMSMain.getApiBaseUrlCached()),
                    null);
        }
    }

    public void cacheShowsPage() {
        if (showsPage != null && appCMSMain != null && appCMSSite != null) {
            String pageId = showsPage.getPageId();
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);

            if (appCMSPageUI == null) {
                MetaPage metaPage = pageIdToMetaPageMap.get(pageId);
                if (metaPage != null) {
                    getAppCMSPage(metaPage.getPageUI(),
                            appCMSPageUIResult -> {
                                if (appCMSPageUIResult != null) {
                                    navigationPages.put(metaPage.getPageId(), appCMSPageUIResult);
                                    String action = pageNameToActionMap.get(metaPage.getPageName());
                                    if (action != null && actionToPageMap.containsKey(action)) {
                                        actionToPageMap.put(action, appCMSPageUIResult);
                                    }
                                }
                            },
                            loadFromFile,
                            false);
                }
            }

            String baseUrl = appCMSMain.getApiBaseUrl();
            String endPoint = showsPage.getPageAPI();
            String siteId = appCMSSite.getGist().getSiteInternalName();

            // Cache movies page when the app is loading
            getPageIdContent(getApiUrl(true,
                    false,
                    false,
                    baseUrl,
                    endPoint,
                    siteId,
                    showsPage.getPageId(),
                    !TextUtils.isEmpty(appCMSMain.getApiBaseUrlCached())),
                    showsPage.getPageId(),
                    null,
                    !TextUtils.isEmpty(appCMSMain.getApiBaseUrlCached()),
                    null);
        }
    }

    private void processMetaPagesList(final boolean loadFromFile,
                                      List<MetaPage> metaPageList,
                                      final Action0 onPagesFinishedAction) {
        Log.w(TAG, "Attempting to retrieve Page UI json");
        if (currentActivity != null) {
            GetAppCMSPageUIAsyncTask getAppCMSPageUIAsyncTask =
                    new GetAppCMSPageUIAsyncTask(appCMSPageUICall, null);

            List<Observable<GetAppCMSPageUIAsyncTask.MetaPageUI>> observables = new ArrayList<>();
            for (MetaPage metaPage : metaPageList) {
                if (metaPage.getPageName().contains("Downloads") && !metaPage.getPageName().contains("Settings")) {
                    setDownloadPageId(metaPage.getPageId());
                }

                String url = metaPage.getPageUI();

                GetAppCMSPageUIAsyncTask.Params params =
                        new GetAppCMSPageUIAsyncTask.Params.Builder()
                                .url(url)
                                .bustCache(false)
                                .loadFromFile(loadFromFile)
                                .metaPage(metaPage)
                                .build();
                observables.add(getAppCMSPageUIAsyncTask.getObservable(params));
            }

            Observable.zip(observables, args -> {
                try {
                    for (Object arg : args) {
                        if (arg instanceof GetAppCMSPageUIAsyncTask.MetaPageUI) {
                            GetAppCMSPageUIAsyncTask.MetaPageUI metaPageUI = (GetAppCMSPageUIAsyncTask.MetaPageUI) arg;
                            navigationPages.put(metaPageUI.getMetaPage().getPageId(), metaPageUI.getAppCMSPageUI());
                            String action = pageNameToActionMap.get(metaPageUI.getMetaPage().getPageName());
                            if (action != null && actionToPageMap.containsKey(action)) {
                                actionToPageMap.put(action, metaPageUI.getAppCMSPageUI());
                                actionToPageNameMap.put(action, metaPageUI.getMetaPage().getPageName());
                                actionToPageAPIUrlMap.put(action, metaPageUI.getMetaPage().getPageAPI());
                                actionTypeToMetaPageMap.put(actionToActionTypeMap.get(action), metaPageUI.getMetaPage());
                                //Log.d(TAG, "Action: " + action + "  PageAPI URL: "
//                                        + metaPage.getPageAPI());
                            }
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
                return true;
            }).onErrorResumeNext(throwable -> Observable.empty())
                    .subscribe(r -> {
                        if (r) {
                            onPagesFinishedAction.call();
                        } else {
                            launchBlankPage();
                        }
                    });
        }
    }

    private void processMetaPagesQueue(final boolean loadFromFile,
                                       final Action0 onPagesFinishedAction) {
        if (currentActivity != null) {
            final MetaPage metaPage = pagesToProcess.remove();

            //Log.d(TAG, "Processing meta page " +
//                    metaPage.getPageName() + ": " +
//                    metaPage.getPageId() + " " +
//                    metaPage.getPageUI() + " " +
//                    metaPage.getPageAPI());
            if (metaPage.getPageName().contains("Downloads") && !metaPage.getPageName().contains("Settings")) {

                setDownloadPageId(metaPage.getPageId());
            }

            pageIdToPageAPIUrlMap.put(metaPage.getPageId(), metaPage.getPageAPI());
            pageIdToPageNameMap.put(metaPage.getPageId(), metaPage.getPageName());

            getAppCMSPage(metaPage.getPageUI(),
                    appCMSPageUI -> {
                        try {
                            navigationPages.put(metaPage.getPageId(), appCMSPageUI);
                            String action = pageNameToActionMap.get(metaPage.getPageName());
                            if (action != null && actionToPageMap.containsKey(action)) {
                                actionToPageMap.put(action, appCMSPageUI);
                                actionToPageNameMap.put(action, metaPage.getPageName());
                                actionToPageAPIUrlMap.put(action, metaPage.getPageAPI());
                                actionTypeToMetaPageMap.put(actionToActionTypeMap.get(action), metaPage);
                                //Log.d(TAG, "Action: " + action + "  PageAPI URL: "
//                                        + metaPage.getPageAPI());
                            }
                            if (!pagesToProcess.isEmpty()) {

                                processMetaPagesQueue(loadFromFile,
                                        onPagesFinishedAction);
                            } else {
                                onPagesFinishedAction.call();
                            }
                        } catch (Exception e) {
                            //Log.e(TAG, "Error retrieving AppCMS Page UI: " + e.getMessage());
                            launchBlankPage();
                        }
                    },
                    loadFromFile,
                    false);
        }
    }

    public AppCMSPageUI getAppCMSPageUI(String pageName) {
        String action = pageNameToActionMap.get(pageName);
        if (action != null && actionToPageMap.containsKey(action)) {
            return actionToPageMap.get(action);
        }
        return null;
    }

    /**
     * Temp method for loading download Quality screen from Assets till json is not updated at Server
     */
    @SuppressWarnings("unused")
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
            //Log.e(TAG, "Error getting data from file: " + e.getMessage());
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

    private int getHistoryPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName())
                    == AppCMSUIKeyType.ANDROID_HISTORY_SCREEN_KEY) {
                return i;
            }
        }
        return -1;
    }

    public boolean isHistoryPage(String pageId) {
        return !TextUtils.isEmpty(pageId) && historyPage != null && pageId.equals(historyPage.getPageId());
    }

    private int getWatchlistPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName())
                    == AppCMSUIKeyType.ANDROID_WATCHLIST_SCREEN_KEY) {
                return i;
            }
        }
        return -1;
    }

    public boolean isWatchlistPage(String pageId) {
        return !TextUtils.isEmpty(pageId) && watchlistPage != null && pageId.equals(watchlistPage.getPageId());
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

    private int getDownloadQualityPage(List<MetaPage> metaPageList) {
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

    private int getMoviesPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName())
                    == AppCMSUIKeyType.ANDROID_MOVIES_SCREEN_KEY) {
                return i;
            }
        }
        return -1;
    }

    private int getShowsPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName())
                    == AppCMSUIKeyType.ANDROID_SHOWS_SCREEN_KEY) {
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
        pageIdToMetaPageMap.clear();
    }

    private void getAppCMSTV(int tryCount) {
        if (!isUserLoggedIn() && tryCount == 0) {
            signinAnonymousUser(tryCount, null, PlatformType.TV);
        } else if (isUserLoggedIn() && shouldRefreshAuthToken() && tryCount == 0) {
            refreshIdentity(getRefreshToken(),
                    () -> getAppCMSTV(tryCount + 1));
        } else {
            GetAppCMSAndroidUIAsyncTask.Params params =
                    new GetAppCMSAndroidUIAsyncTask.Params.Builder()
                            .url(appCMSMain.getFireTv())
                            .loadFromFile(loadFromFile)
                            .build();
            //Log.d(TAG, "Params: " + appCMSMain.getAndroid() + " " + loadFromFile);
            new GetAppCMSAndroidUIAsyncTask(appCMSAndroidUICall, appCMSAndroidUI -> {
                if (appCMSAndroidUI == null ||
                        appCMSAndroidUI.getMetaPages() == null ||
                        appCMSAndroidUI.getMetaPages().isEmpty()) {
                    //Log.e(TAG, "AppCMS keys for pages for appCMSAndroid not found");
                    launchErrorActivity(PlatformType.TV);
                } else {
                    if (appCMSAndroidUI.getAnalytics() != null) {
                        initializeGA(appCMSAndroidUI.getAnalytics().getGoogleAnalyticsId());
                    }
                    navigation = appCMSAndroidUI.getNavigation();

                    //add search in navigation item.
                    NavigationPrimary myProfile = new NavigationPrimary();
                    myProfile.setPageId(currentActivity.getString(R.string.app_cms_my_profile_label,
                            currentActivity.getString(R.string.profile_label)));
                    myProfile.setTitle(currentActivity.getString(R.string.app_cms_my_profile_label,
                            appCMSAndroidUI.getShortAppName() != null ?
                                    appCMSAndroidUI.getShortAppName() :
                                    currentActivity.getString(R.string.profile_label)));
                    navigation.getNavigationPrimary().add(myProfile);

                    //add search in navigation item.
                    NavigationPrimary searchNav = new NavigationPrimary();
                    searchNav.setPageId(currentActivity.getString(R.string.app_cms_search_label));
                    searchNav.setTitle(currentActivity.getString(R.string.app_cms_search_label));
                    navigation.getNavigationPrimary().add(searchNav);


                    queueMetaPages(appCMSAndroidUI.getMetaPages());
                    final MetaPage firstPage = pagesToProcess.peek();
                    //Log.d(TAG, "Processing meta pages queue");
                    processMetaPagesQueue(loadFromFile,
                            () -> {
                                //Log.d(TAG, "Launching first page: " + firstPage.getPageName());
                                cancelInternalEvents();

                                Intent logoAnimIntent = new Intent(AppCMSPresenter.ACTION_LOGO_ANIMATION);
                                logoAnimIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                currentActivity.sendBroadcast(logoAnimIntent);

                                NavigationPrimary homePageNav = findHomePageNavItem();
                                boolean launchSuccess = navigateToTVPage(homePageNav.getPageId(),
                                        homePageNav.getTitle(),
                                        homePageNav.getUrl(),
                                        true,
                                        null,
                                        false,
                                        false,
                                        false);
                                if (!launchSuccess) {
                                    //Log.e(TAG, "Failed to launch page: "
//                                            + firstPage.getPageName());
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
                                    Uri searchQuery,
                                    boolean forcedDownload,
                                    boolean isTOSDialogPage,
                                    boolean isLoginDialogPage) {
        boolean result = false;
        if (currentActivity != null && !TextUtils.isEmpty(pageId)
                && !(pageTitle.equalsIgnoreCase(currentActivity.getString(R.string.contact_us)))) {
            loadingPage = true;
            //Log.d(TAG, "Launching page " + pageTitle + ": " + pageId);
            // //Log.d(TAG, "Search query (optional): " + searchQuery);

            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);
            AppCMSPageAPI appCMSPageAPI = navigationPageData.get(pageId);
            Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
            stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(stopPageLoadingActionIntent);
            if (forcedDownload) {
                appCMSPageAPI = null;
                if (null != pageId)
                    getPageAPILruCache().remove(pageId);
            }

            if (appCMSPageAPI == null) {
                //check internet connection here.
                if (!isNetworkConnected()) {
                    RetryCallBinder retryCallBinder = getRetryCallBinder(url, null,
                            pageTitle, null,
                            null, launchActivity, pageId, PAGE_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(currentActivity.getString(R.string.retry_key), true);
                    bundle.putBoolean(currentActivity.getString(R.string.register_internet_receiver_key), true);
                    bundle.putBoolean(currentActivity.getString(R.string.is_tos_dialog_page_key), isTOSDialogPage);
                    bundle.putBoolean(currentActivity.getString(R.string.is_login_dialog_page_key), isLoginDialogPage);
                    bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
                    Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
                    args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
                    args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                    currentActivity.sendBroadcast(args);
                    return false;
                }

                String apiUrl = getApiUrl(true,
                        false,
                        false,
                        appCMSMain.getApiBaseUrl(),
                        pageIdToPageAPIUrlMap.get(pageId),
                        appCMSSite.getGist().getSiteInternalName(),
                        pageId,
                        appCMSPageUI != null &&
                                appCMSPageUI.getCaching() != null &&
                                !appCMSPageUI.getCaching().shouldOverrideCaching() &&
                                appCMSPageUI.getCaching().isEnabled());

                getPageIdContent(apiUrl,
                        pageId,
                        null,
                        appCMSPageUI != null &&
                                appCMSPageUI.getCaching() != null &&
                                !appCMSPageUI.getCaching().shouldOverrideCaching() &&
                                appCMSPageUI.getCaching().isEnabled(),
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
                                    boolean isHistoryUpdate = false;
                                    if (isUserLoggedIn()) {
                                        if (appCMSPageAPI.getModules() != null) {
                                            List<Module> modules = appCMSPageAPI.getModules();
                                            for (int i = 0; i < modules.size(); i++) {
                                                Module module = modules.get(i);
                                                AppCMSUIKeyType moduleType = getJsonValueKeyMap().get(module.getModuleType());
                                                if (moduleType == AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY) {
                                                    if (module.getContentData() != null &&
                                                            !module.getContentData().isEmpty()) {
                                                        int finalI = i;
                                                        isHistoryUpdate = true;
                                                        getHistoryData(appCMSHistoryResult -> {
                                                            if (appCMSHistoryResult != null) {
                                                                AppCMSPageAPI historyAPI =
                                                                        appCMSHistoryResult.convertToAppCMSPageAPI(appCMSPageAPI.getId());
                                                                historyAPI.getModules().get(0).setId(module.getId());
                                                                historyAPI.getModules().get(0).setTitle(module.getTitle());
                                                                   /* appCMSPresenter.mergeData(historyAPI, appCMSPageAPI);*/
                                                                modules.set(finalI, historyAPI.getModules().get(0));
                                                                populateTVPage(appCMSPageAPI, appCMSPageUI, this.pageId, this.launchActivity, this.pageTitle, isTOSDialogPage, isLoginDialogPage, this.pagePath);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (!isHistoryUpdate)
                                        populateTVPage(appCMSPageAPI, appCMSPageUI, this.pageId, this.launchActivity, this.pageTitle, isTOSDialogPage, isLoginDialogPage, this.pagePath);
                                } else {
                                    sendStopLoadingPageAction(true, () -> navigateToTVPage(pageId, pageTitle, url, launchActivity, searchQuery, forcedDownload, isTOSDialogPage, isLoginDialogPage));
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
                            searchQuery,
                            isTOSDialogPage,
                            isLoginDialogPage);
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
                            isTOSDialogPage ? ExtraScreenType.TERM_OF_SERVICE
                                    : (isLoginDialogPage ? ExtraScreenType.EDIT_PROFILE : ExtraScreenType.NONE));
                    if (args != null) {
                        Intent updatePageIntent =
                                new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                        updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                args);
                        updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                        currentActivity.sendBroadcast(updatePageIntent);
                        setNavItemToCurrentAction(currentActivity);
                    }
                }
                loadingPage = false;
            }
            result = true;
        } else if (currentActivity != null &&
                !TextUtils.isEmpty(url) &&
                pageTitle.contains(currentActivity.getString(R.string.contact_us))) {
            openContactUsScreen(pageId, pageTitle, url);

        } else {
            //Log.d(TAG, "Resetting page navigation to previous tab");
            setNavItemToCurrentAction(currentActivity);
        }
        return result;
    }

    private void populateTVPage(AppCMSPageAPI appCMSPageAPI, AppCMSPageUI appCMSPageUI, String pageId,
                                boolean launchActivity, String pageTitle, boolean isTosPage,
                                boolean isLoginPage, String pagePath) {
        cancelInternalEvents();
        pushActionInternalEvents(pageId
                + BaseView.isLandscape(currentActivity));
        navigationPageData.put(pageId, appCMSPageAPI);
        if (launchActivity) {
            launchTVPageActivity(currentActivity,
                    appCMSPageUI,
                    appCMSPageAPI,
                    pageId,
                    pageTitle,
                    pageIdToPageNameMap.get(pageId),
                    loadFromFile,
                    false,
                    false,
                    false,
                    Uri.EMPTY,
                    isTosPage,
                    isLoginPage);
            setNavItemToCurrentAction(currentActivity);

        } else {
            Bundle args = getPageActivityBundle(currentActivity,
                    appCMSPageUI,
                    appCMSPageAPI,
                    pageId,
                    pageTitle,
                    pagePath,
                    pageIdToPageNameMap.get(pageId),
                    loadFromFile,
                    false,
                    false,
                    false,
                    false,
                    Uri.EMPTY,
                    isTosPage ? ExtraScreenType.TERM_OF_SERVICE : (isLoginPage ? ExtraScreenType.EDIT_PROFILE : ExtraScreenType.NONE));
            if (args != null) {
                Intent updatePageIntent =
                        new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                updatePageIntent.putExtra(
                        currentActivity.getString(R.string.app_cms_bundle_key),
                        args);
                updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(updatePageIntent);

                setNavItemToCurrentAction(currentActivity);
            }
        }
    }

    private void openContactUsScreen(String pageId,
                                     String pageTitle,
                                     String url) {
        AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        appCMSPageAPI.setId(getPageId(appCMSPageUI));
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
                Uri.EMPTY,
                ExtraScreenType.NONE);
        if (args != null) {
            Intent updatePageIntent =
                    new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
            updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                    args);
            updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(updatePageIntent);
            setNavItemToCurrentAction(currentActivity);
        }
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
                                      Uri searchQuery,
                                      boolean isTosPage,
                                      boolean isLoginPage) {
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
                isTosPage ? ExtraScreenType.TERM_OF_SERVICE : (isLoginPage ? ExtraScreenType.EDIT_PROFILE : ExtraScreenType.NONE));

        try {
            if (args != null) {
                String tvHomeScreenPackage = "com.viewlift.tv.views.activity.AppCmsHomeActivity";
                Intent appCMSIntent = new Intent(activity, Class.forName(tvHomeScreenPackage));
                appCMSIntent.putExtra(activity.getString(R.string.app_cms_bundle_key), args);
                appCMSIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(appCMSIntent);
            }
        } catch (Exception e) {
            //Log.e(TAG, "Error launching TV activity: " + e.getMessage());
        } finally {
            sendStopLoadingPageAction(true,
                    () -> launchTVPageActivity(activity, appCMSPageUI, appCMSPageAPI, pageId, pageName,
                            screenName, loadFromFile, appbarPresent, fullscreenEnabled, navbarPresent,
                            searchQuery, isTosPage, isLoginPage));
        }
    }

    public void playNextVideo(AppCMSVideoPageBinder binder,
                              int currentlyPlayingIndex,
                              long watchedTime) {
        isVideoPlayerStarted = false;
        if (!binder.isOffline()) {
            if (platformType.equals(PlatformType.ANDROID)) {
                String action = null;
                if (currentActivity != null &&
                        currentActivity.getResources().getBoolean(R.bool.video_detail_page_plays_video)) {
                    action = currentContext.getString(R.string.app_cms_action_detailvideopage_key);
                }
                if (0 <= binder.getCurrentPlayingVideoIndex()) {
                    launchVideoPlayer(binder.getContentData(),
                            binder.getRelateVideoIds() != null &&
                                    currentlyPlayingIndex < binder.getRelateVideoIds().size() ?
                                    binder.getRelateVideoIds().get(binder.getCurrentPlayingVideoIndex()) :
                                    binder.getContentData().getGist().getId(),
                            currentlyPlayingIndex,
                            binder.getRelateVideoIds(),
                            watchedTime / 1000L,
                            action);
                }
            } else {
                launchTVVideoPlayer(binder.getContentData(),
                        currentlyPlayingIndex,
                        binder.getRelateVideoIds(),
                        watchedTime / 1000L);
            }
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
            //Log.d(TAG, "Launching " + permalink + ": " + action);

            AppCMSActionPresenter actionPresenter = new AppCMSActionPresenter();
            actionPresenter.setAction(action);

            if (!launchButtonSelectedAction(
                    permalink,
                    actionPresenter,
                    title,
                    extraData,
                    binder.getContentData(),
                    false,
                    binder.getCurrentPlayingVideoIndex(),
                    binder.getRelateVideoIds())) {
                //Log.e(TAG, "Could not launch action: " +
//                        " permalink: " +
//                        permalink +
//                        " action: " +
//                        action +
//                        " hlsUrl: " +
//                        hlsUrl);
            }
        }
    }

    public Map<String, AppCMSUIKeyType> getJsonValueKeyMap() {
        return jsonValueKeyMap;
    }

    /**
     * Method opens the autoplay screen when one movie finishes playing
     *
     * @param binder  binder to share data
     * @param action1
     */
    public void openAutoPlayScreen(final AppCMSVideoPageBinder binder, Action1<Object> action1) {
        String url = null;
        binder.setCurrentMovieName(binder.getContentData().getGist().getTitle());
        if (!binder.isOffline()) {
            final String filmId =
                    binder.getRelateVideoIds().get(binder.getCurrentPlayingVideoIndex() + 1);
            if (currentActivity != null &&
                    !loadingPage && appCMSMain != null &&
                    !TextUtils.isEmpty(appCMSMain.getApiBaseUrl()) &&
                    !TextUtils.isEmpty(appCMSSite.getGist().getSiteInternalName())) {
                url = currentActivity.getString(R.string.app_cms_video_detail_api_url,
                        appCMSMain.getApiBaseUrl(),
                        filmId,
                        appCMSSite.getGist().getSiteInternalName());
            }
        } else {
            ContentDatum contentDatum = realmController.getDownloadById(
                    binder.getRelateVideoIds().get(
                            binder.getCurrentPlayingVideoIndex() + 1))
                    .convertToContentDatum(getLoggedInUser());
            binder.setContentData(contentDatum);
        }
        String pageId = getAutoplayPageId();
        if (!TextUtils.isEmpty(pageId)) {
            navigateToAutoplayPage(pageId,
                    currentActivity.getString(R.string.app_cms_page_autoplay_key),
                    url,
                    binder,
                    action1);
        } else {
            //Log.e(TAG, "Can't find autoplay page ui in pageIdToPageNameMap");
        }
    }

    public void getRelatedMedia(String filmIds, final Action1<AppCMSVideoDetail> action1) {
        String url = currentContext.getString(R.string.app_cms_video_detail_api_url,
                appCMSMain.getApiBaseUrl(),
                filmIds,
                appCMSSite.getGist().getSiteInternalName());
        GetAppCMSVideoDetailAsyncTask.Params params =
                new GetAppCMSVideoDetailAsyncTask.Params.Builder().url(url)
                        .authToken(getAuthToken()).build();
        new GetAppCMSVideoDetailAsyncTask(appCMSVideoDetailCall,
                action1).execute(params);
    }

    public boolean launchTVButtonSelectedAction(String pagePath,
                                                String action,
                                                String filmTitle,
                                                String[] extraData,
                                                ContentDatum contentDatum,
                                                final boolean closeLauncher,
                                                int currentlyPlayingIndex,
                                                List<String> relateVideoIds) {
        boolean result = false;
        //Log.d(TAG, "Attempting to load page " + filmTitle + ": " + pagePath);
        if (!isNetworkConnected()) {
            RetryCallBinder retryCallBinder = getRetryCallBinder(pagePath, action,
                    filmTitle, extraData,
                    contentDatum, closeLauncher, null, BUTTON_ACTION);
            Bundle bundle = new Bundle();
            bundle.putBoolean(currentActivity.getString(R.string.retry_key), true);
            bundle.putBoolean(currentActivity.getString(R.string.register_internet_receiver_key), true);
            bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
            Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
            args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
            args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(args);
        } else if (currentActivity != null && !loadingPage) {
            AppCMSActionType actionType = actionToActionTypeMap.get(action);
            if (actionType == null) {
                //Log.e(TAG, "Action " + action + " not found!");
                return false;
            }
            result = true;
            boolean isTrailer = actionType == AppCMSActionType.WATCH_TRAILER;
            if (actionType == AppCMSActionType.PLAY_VIDEO_PAGE ||
                    actionType == AppCMSActionType.WATCH_TRAILER) {

                getUserVideoStatus(contentDatum.getGist().getId(),
                        userVideoStatusResponse -> {
                            if (userVideoStatusResponse != null) {
                                contentDatum.getGist().setWatchedTime
                                        (userVideoStatusResponse.getWatchedTime());
                            }
                            Intent playVideoIntent = new Intent();
                            try {
                                Class videoPlayer = Class.forName(tvVideoPlayerPackage);
                                playVideoIntent = new Intent(currentActivity, videoPlayer);
                            } catch (Exception e) {

                            }
                            String adsUrl;

                            boolean svodServiceType = appCMSMain.getServiceType().equals(
                                    currentActivity.getString(R.string.app_cms_main_svod_service_type_key));

                            boolean requestAds = !svodServiceType && actionType == AppCMSActionType.PLAY_VIDEO_PAGE;

                            Date now = new Date();
                            adsUrl = currentActivity.getString(R.string.app_cms_ads_api_url,
                                    appCMSAndroid.getAdvertising().getVideoTag(),
                                    getPermalinkCompletePath(pagePath),
                                    now.getTime(),
                                    appCMSMain.getSite());

                            String backgroundColor = getAppBackgroundColor();
                            AppCMSVideoPageBinder appCMSVideoPageBinder =
                                    getDefaultAppCMSVideoPageBinder(contentDatum, currentlyPlayingIndex, relateVideoIds, false, isTrailer, requestAds, adsUrl, backgroundColor);
                            if (closeLauncher) {
                                sendCloseOthersAction(null, true, false);
                            }


                            Bundle bundle = new Bundle();
                            bundle.putBinder(currentActivity.getString(R.string.app_cms_video_player_binder_key),
                                    appCMSVideoPageBinder);
                            playVideoIntent.putExtra(currentActivity.getString(R.string.app_cms_video_player_bundle_binder_key), bundle);

                            currentActivity.startActivityForResult(playVideoIntent, PLAYER_REQUEST_CODE);
                        });
                //sendStopLoadingPageAction();

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
                sendCloseOthersAction(null, true, false);
            } else if (actionType == AppCMSActionType.LOGIN) {
                //Log.d(TAG, "Login action selected: " + extraData[0]);
                Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(pageLoadingActionIntent);
                //closeSoftKeyboard();
                login(extraData[0], extraData[1]);
                sendSignInEmailFirebase();
            } else if (actionType == AppCMSActionType.FORGOT_PASSWORD) {
                //Log.d(TAG, "Forgot password selected: " + extraData[0]);
                AppCMSPageUI appCMSPageUI = actionToPageMap.get(action);
                launchResetPasswordTVPage(appCMSPageUI);
            } else if (actionType == AppCMSActionType.LOGIN_FACEBOOK) {
                //Log.d(TAG, "Login Facebook selected");
                loginFacebook();
                sendSignInFacebookFirebase();
            } else if (actionType == AppCMSActionType.SIGNUP) {
                //Log.d(TAG, "Sign-Up selected: " + extraData[0]);
                Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(pageLoadingActionIntent);
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

                    case DRAGGABLE_VIDEO_PAGE:
                        appbarPresent = false;
                        fullscreenEnabled = true;
                        navbarPresent = false;
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
                Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(pageLoadingActionIntent);
                AppCMSPageUI appCMSPageUI = actionToPageMap.get(action);

                String apiUrl = getApiUrl(false,
                        false,
                        false,
                        appCMSMain.getApiBaseUrl(),
                        actionToPageAPIUrlMap.get(action),
                        appCMSSite.getGist().getSiteInternalName(),
                        pagePath,
                        appCMSPageUI != null &&
                                appCMSPageUI.getCaching() != null &&
                                !appCMSPageUI.getCaching().shouldOverrideCaching() &&
                                appCMSPageUI.getCaching().isEnabled());

                getPageIdContent(apiUrl,
                        pagePath,
                        null,
                        appCMSPageUI != null &&
                                appCMSPageUI.getCaching() != null &&
                                !appCMSPageUI.getCaching().shouldOverrideCaching() &&
                                appCMSPageUI.getCaching().isEnabled(),
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
                                        updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                                        currentActivity.sendBroadcast(updatePageIntent);
                                    }
                                } else {
                                    sendStopLoadingPageAction(true,
                                            () -> launchTVButtonSelectedAction(pagePath, action,
                                                    filmTitle, extraData, contentDatum, closeLauncher,
                                                    currentlyPlayingIndex, relateVideoIds));
                                }
                                loadingPage = false;
                            }
                        });
            }
        }
        return result;
    }

    @SuppressWarnings("unused")
    private void LaunchTVVideoPlayerActivity(String pagePath, String filmTitle, String[] extraData,
                                             boolean closeLauncher, ContentDatum contentDatum,
                                             AppCMSActionType actionType) {
        Intent playVideoIntent = new Intent(currentActivity, AppCMSPlayVideoActivity.class);
        try {
            Class videoPlayer = Class.forName(tvVideoPlayerPackage);
            playVideoIntent = new Intent(currentActivity, videoPlayer);
        } catch (Exception e) {
            //Log.e(TAG, "Error launching TV Button Selected Action: " + e.getMessage());
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
            playVideoIntent.putExtra(currentActivity.getString(R.string.is_trailer_key), true);
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
                        appCMSAndroid.getAdvertising().getVideoTag(),
                        getPermalinkCompletePath(pagePath),
                        now.getTime(),
                        appCMSSite.getGist().getSiteInternalName()));
        playVideoIntent.putExtra(currentActivity.getString(R.string.app_cms_bg_color_key),
                getAppBackgroundColor());
        playVideoIntent.putExtra(currentActivity.getString(R.string.video_player_closed_caption_key), extraData[3]);
        playVideoIntent.putExtra(currentActivity.getString(R.string.video_player_watched_time_key), contentDatum.getGist().getWatchedTime());
        playVideoIntent.putExtra(currentActivity.getString(R.string.video_player_run_time_key), contentDatum.getGist().getRuntime());
        if (closeLauncher) {
            sendCloseOthersAction(null, true, false);
        }
        currentActivity.startActivityForResult(playVideoIntent, PLAYER_REQUEST_CODE);
    }

    public void showLoadingDialog(boolean showDialog) {
        if (currentActivity != null) {
            if (showDialog) {
                Intent pageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION);
                pageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(pageLoadingActionIntent);
            } else {
                Intent stopPageLoadingActionIntent = new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
                stopPageLoadingActionIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
                currentActivity.sendBroadcast(stopPageLoadingActionIntent);
            }
        }
    }

    public PlatformType getPlatformType() {
        return platformType;
    }

    public boolean isRemovableSDCardAvailable() {
        return currentActivity != null && getStorageDirectories(currentActivity).length >= 1;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String getSDCardPath(Context context, String dirName) {
        String dirPath = getSDCardPath(context) + File.separator + dirName;
        File dir = new File(dirPath);
        if (!dir.isDirectory())
            dir.mkdirs();

        return dir.getAbsolutePath();

    }

    private String getSDCardPath(Context context) {
        File baseSDCardDir;
        String[] dirs = getStorageDirectories(context);
        baseSDCardDir = new File(dirs[0]);

        return baseSDCardDir.getAbsolutePath();
    }

    private String[] getStorageDirectories(Context context) {
        HashSet<String> paths = new HashSet<>();
        String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                List<String> results = new ArrayList<>();
                File[] externalDirs = context.getExternalFilesDirs(null);
                for (File file : externalDirs) {
                    String path;
                    try {
                        // path = file.getPath().split("/Android")[0];
                        path = file.getAbsolutePath();
                    } catch (Exception e) {
                        //Log.e(TAG, "Error getting storage directories for downloads: " + e.getMessage());
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
        // Code has not any use in case of build >=23 (M)
       /*
       if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(paths, rawSecondaryStorages);
        }*/
        return paths.toArray(new String[paths.size()]);
    }

    @SuppressWarnings("unused")
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
        editor.apply();
    }

    @SuppressWarnings("unused")
    public List<String> getSearchResultsFromSharePreference() {
        if (currentActivity == null)
            return null;
        List<String> searchValues = new ArrayList<>();
        SharedPreferences sharePref = currentActivity.getSharedPreferences(
                currentActivity.getString(R.string.app_cms_search_sharepref_key), Context.MODE_PRIVATE);
        int size = sharePref.getInt(currentActivity.getString(R.string.app_cms_search_value_size_key), 0);
        for (int i = 0; i < size; i++) {
            searchValues.add(sharePref.getString(currentActivity.getString(R.string.app_cms_search_value_key) + i, null));
        }
        return searchValues;
    }

    @SuppressWarnings("unused")
    public void clearSearchResultsSharePreference() {
        if (currentActivity == null)
            return;
        SharedPreferences sharePref = currentActivity.getSharedPreferences(
                currentActivity.getString(R.string.app_cms_search_sharepref_key), Context.MODE_PRIVATE);
        sharePref.edit().clear().apply();
    }

    @SuppressWarnings("unused")
    public void openSearch() {
        Intent searchIntent = new Intent(SEARCH_ACTION);
        searchIntent.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
        currentActivity.sendBroadcast(searchIntent);
    }

    public void launchTVVideoPlayer(final ContentDatum contentDatum,
                                    final int currentlyPlayingIndex,
                                    List<String> relateVideoIds,
                                    long watchTime) {
        boolean result = false;


        if (!isNetworkConnected() && platformType == PlatformType.TV) {
            RetryCallBinder retryCallBinder = getRetryCallBinder(contentDatum.getGist().getPermalink(), null,
                    contentDatum.getGist().getTitle(), null,
                    contentDatum, false,
                    contentDatum.getGist().getId(), VIDEO_ACTION
            );

            Bundle bundle = new Bundle();
            bundle.putBoolean(currentActivity.getString(R.string.retry_key), true);
            bundle.putBoolean(currentActivity.getString(R.string.register_internet_receiver_key), true);
            bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
            Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
            args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
            args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
            currentActivity.sendBroadcast(args);
        } else if (currentActivity != null &&
                !loadingPage && appCMSMain != null &&
                !TextUtils.isEmpty(appCMSMain.getApiBaseUrl()) &&
                !TextUtils.isEmpty(appCMSSite.getGist().getSiteInternalName())) {
            result = true;
            final String action = currentActivity.getString(R.string.app_cms_action_watchvideo_key);

            if (contentDatum.getContentDetails() == null) {
                String url = currentActivity.getString(R.string.app_cms_video_detail_api_url,
                        appCMSMain.getApiBaseUrl(),
                        contentDatum.getGist().getId(),
                        appCMSSite.getGist().getSiteInternalName());
                GetAppCMSVideoDetailAsyncTask.Params params =
                        new GetAppCMSVideoDetailAsyncTask.Params.Builder().url(url)
                                .authToken(getAuthToken()).build();

                new GetAppCMSVideoDetailAsyncTask(appCMSVideoDetailCall,
                        appCMSVideoDetail -> {
                            if (appCMSVideoDetail != null &&
                                    appCMSVideoDetail.getRecords() != null &&
                                    appCMSVideoDetail.getRecords().get(0) != null) {
                                getUserVideoStatus(appCMSVideoDetail.getRecords().get(0).getGist().getId(),
                                        userVideoStatusResponse -> {
                                            if (userVideoStatusResponse != null) {
                                                long watchedTime = userVideoStatusResponse.getWatchedTime();
                                                String[] extraData = new String[4];
                                                appCMSVideoDetail.getRecords().get(0).getGist().setWatchedTime(watchedTime);
                                                if (appCMSVideoDetail.getRecords().get(0).getStreamingInfo() != null) {
                                                    StreamingInfo streamingInfo = appCMSVideoDetail.getRecords().get(0).getStreamingInfo();
                                                    extraData[0] = contentDatum.getGist().getPermalink();
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
                                                    extraData[2] = contentDatum.getGist().getId();
                                                    if (appCMSVideoDetail.getRecords().get(0).getContentDetails() != null &&
                                                            appCMSVideoDetail.getRecords().get(0).getContentDetails().getClosedCaptions() != null) {
                                                        for (ClosedCaptions closedCaption :
                                                                appCMSVideoDetail.getRecords().get(0).getContentDetails().getClosedCaptions()) {
                                                            if (closedCaption.getFormat().equalsIgnoreCase("SRT")) {
                                                                extraData[3] = closedCaption.getUrl();
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    //  extraData[3] = "https://vsvf.viewlift.com/Gannett/2015/ClosedCaptions/GANGSTER.srt";
                                                    if (!TextUtils.isEmpty(extraData[1])) {
                                                        launchTVButtonSelectedAction(contentDatum.getGist().getId(),
                                                                action,
                                                                appCMSVideoDetail.getRecords().get(0).getGist().getTitle(),
                                                                extraData,
                                                                appCMSVideoDetail.getRecords().get(0),
                                                                false,
                                                                currentlyPlayingIndex,
                                                                appCMSVideoDetail.getRecords().get(0).getContentDetails().getRelatedVideoIds());
                                                    } else {
                                                        openTVErrorDialog(currentActivity.getString(R.string.api_error_message,
                                                                currentActivity.getString(R.string.app_name)),
                                                                currentActivity.getString(R.string.app_connectivity_dialog_title));
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                openTVErrorDialog(currentActivity.getString(R.string.api_error_message,
                                        currentActivity.getString(R.string.app_name)),
                                        currentActivity.getString(R.string.app_connectivity_dialog_title));
                            }
                        }).execute(params);
            } else {
                if (watchTime >= 0) {
                    contentDatum.getGist().setWatchedTime(watchTime);
                }
                launchTVButtonSelectedAction(
                        contentDatum.getGist().getPermalink(),
                        action,
                        contentDatum.getGist().getTitle(),
                        null,
                        contentDatum,
                        false,
                        currentlyPlayingIndex,
                        relateVideoIds);
            }
        }
    }

    private void sendSignUpFacebookFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_UP_METHOD, FIREBASE_FACEBOOK_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_UP_EVENT, bundle);
    }

    private void sendSignUpGoogleFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_UP_METHOD, FIREBASE_GOOGLE_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_UP_EVENT, bundle);
    }

    private void sendSignUpEmailFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_UP_METHOD, FIREBASE_EMAIL_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_UP_EVENT, bundle);
    }

    private void sendSignInFacebookFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_IN_METHOD, FIREBASE_FACEBOOK_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_In_EVENT, bundle);
    }

    private void sendSignInGoogleFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_IN_METHOD, FIREBASE_GOOGLE_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_In_EVENT, bundle);
    }

    private void sendSignInEmailFirebase() {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SIGN_IN_METHOD, FIREBASE_EMAIL_METHOD);
        if (mFireBaseAnalytics != null)
            mFireBaseAnalytics.logEvent(FIREBASE_SIGN_In_EVENT, bundle);
    }

    private void sendFirebaseLoginSubscribeSuccess() {
        //Send Firebase Analytics when user is subscribed and user is Logged In
        String SUBSCRIPTION_SUBSCRIBED = "subscribed";
        mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_STATUS_KEY, SUBSCRIPTION_SUBSCRIBED);
        mFireBaseAnalytics.setUserProperty(LOGIN_STATUS_KEY, LOGIN_STATUS_LOGGED_IN);
        mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_PLAN_ID, getActiveSubscriptionId());
        mFireBaseAnalytics.setUserProperty(SUBSCRIPTION_PLAN_NAME, getActiveSubscriptionPlanName());
    }

    public void sendFirebaseSelectedEvents(String eventKey, Bundle bundleData) {
        getmFireBaseAnalytics().logEvent(eventKey, bundleData);
        getmFireBaseAnalytics().setAnalyticsCollectionEnabled(true);
    }

    public ReferenceQueue<Object> getSoftReferenceQueue() {
        return referenceQueue;
    }

    public String getApiKey() {
        return apikey;
    }

    private String getDownloadURL(ContentDatum contentDatum) {

        String downloadURL;
        String downloadQualityRendition = getUserDownloadQualityPref();
        Map<String, String> urlRenditionMap = new HashMap<>();
        for (Mpeg mpeg : contentDatum.getStreamingInfo().getVideoAssets().getMpeg()) {
            if (mpeg.getRenditionValue() != null) {
                urlRenditionMap.put(mpeg.getRenditionValue().replace("_", "").trim(),
                        mpeg.getUrl());
            }
        }

        downloadURL = urlRenditionMap.get(downloadQualityRendition);
        if (TextUtils.isEmpty(downloadURL)) {
            Iterator<String> urlRenditionMapKeysIter = urlRenditionMap.keySet().iterator();
            if (urlRenditionMapKeysIter.hasNext()) {
                downloadURL = urlRenditionMap.get(urlRenditionMapKeysIter.next());
            }
        }

        if (TextUtils.isEmpty(downloadURL)) {
            Iterator<String> urlRenditionMapKeysIter = urlRenditionMap.keySet().iterator();
            if (urlRenditionMapKeysIter.hasNext()) {
                downloadURL = urlRenditionMap.get(urlRenditionMapKeysIter.next());
            }
        }

        if (downloadQualityRendition != null) {
            if (downloadURL == null && downloadQualityRendition.contains("360")) {
                if (urlRenditionMap.get("360p") != null) {
                    downloadURL = urlRenditionMap.get("360p");
                }

            } else if (downloadURL == null && downloadQualityRendition.contains("720")) {
                if (urlRenditionMap.get("720p") != null) {
                    downloadURL = urlRenditionMap.get("720p");
                }
            } else if (downloadURL == null && downloadQualityRendition.contains("1080")) {
                if (urlRenditionMap.get("1080p") != null) {
                    downloadURL = urlRenditionMap.get("1080p");
                }
            } else if (downloadURL == null) {
                //noinspection SuspiciousMethodCalls
                downloadURL = urlRenditionMap.get(urlRenditionMap.keySet().toArray()[0]);
            }
        } else {
            downloadURL = contentDatum.getStreamingInfo().getVideoAssets().getMpeg().get(0).getUrl();
        }

        return downloadURL;
    }

    public String getNetworkConnectivityDownloadErrorMsg() {
        return currentActivity.getString(R.string.app_cms_network_connectivity_error_message_download);
    }

    public String getSignOutErrorMsg() {
        return currentActivity.getString(R.string.app_cms_signout_error_msg);
    }

    public String getNetworkConnectedVideoPlayerErrorMsg() {
        return currentActivity.getString(R.string.app_cms_network_connectivity_error_message);
    }

    public void openVideoPageFromSearch(String[] searchResultClick) {
        String permalink = searchResultClick[3];
        String action = currentActivity.getString(R.string.app_cms_action_detailvideopage_key);
        String contentType = searchResultClick[4];

        if (contentType.contains(currentActivity.getString(R.string.app_cms_series_content_type))) {
            action = currentActivity.getString(R.string.app_cms_action_showvideopage_key);
        }
        String title = searchResultClick[0];
        String runtime = searchResultClick[1];

        AppCMSActionPresenter actionPresenter = new AppCMSActionPresenter();
        actionPresenter.setAction(action);
        //Log.d(TAG, "Launching " + permalink + ":" + action);
        if (!launchButtonSelectedAction(permalink,
                actionPresenter,
                title,
                null,
                null,
                false,
                0,
                null)) {
            //Log.e(TAG, "Could not launch action: " +
//                    " permalink: " +
//                    permalink +
//                    " action: " +
//                    action);
        }
    }

    @SuppressWarnings("unused")
    public NavigationUser getLoginNavigation() {
        for (NavigationUser navigationUser : getNavigation().getNavigationUser()) {
            if (!isUserLoggedIn() && navigationUser.getUrl().contains(currentActivity.getString(R.string.app_cms_action_login_key))) {
                return navigationUser;
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public NavigationUser getSignUpNavigation() {
        for (NavigationUser navigationUser : getNavigation().getNavigationUser()) {
            if (!isUserLoggedIn() && navigationUser.getTitle().equalsIgnoreCase(currentActivity.getString(R.string.app_cms_signup))) {
                return navigationUser;
            }
        }
        return null;
    }

    public boolean getLoginFromNavPage() {
        return loginFromNavPage;
    }

    public void openErrorDialog(String filmId,
                                boolean queued,
                                Action1<AppCMSAddToWatchlistResult> action1) {

        RetryCallBinder retryCallBinder = getRetryCallBinder(null, null,
                null, null,
                null, false, filmId, EDIT_WATCHLIST);
        retryCallBinder.setCallback(action1);
        Bundle bundle = new Bundle();
        bundle.putBoolean(currentActivity.getString(R.string.retry_key), true);
        bundle.putBoolean(currentActivity.getString(R.string.register_internet_receiver_key), true);
        bundle.putBoolean("queued", queued);
        Intent args = new Intent(AppCMSPresenter.ERROR_DIALOG_ACTION);
        args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
        bundle.putBinder(currentActivity.getString(R.string.retryCallBinderKey), retryCallBinder);
        args.putExtra(currentActivity.getString(R.string.retryCallBundleKey), bundle);
        args.putExtra(currentActivity.getString(R.string.app_cms_package_name_key), currentActivity.getPackageName());
        currentActivity.sendBroadcast(args);
    }

    public void setEntitlementPendingVideoData(EntitlementPendingVideoData entitlementPendingVideoData) {
        this.entitlementPendingVideoData = entitlementPendingVideoData;
    }

    @SuppressWarnings("unused")
    public void getSubscriptionData(Action1<AppCMSUserSubscriptionPlanResult> action1) {
        try {
            appCMSSubscriptionPlanCall.call(
                    currentActivity.getString(R.string.app_cms_get_current_subscription_api_url,
                            appCMSMain.getApiBaseUrl(),
                            getLoggedInUser(),
                            appCMSSite.getGist().getSiteInternalName()),
                    R.string.app_cms_subscription_subscribed_plan_key,
                    null,
                    apikey,
                    getAuthToken(),
                    listResult -> Log.v("currentActivity", "currentActivity"),
                    appCMSSubscriptionPlanResults -> {
                        AppCMSPresenter.this.sendCloseOthersAction(null, true, false);
                        AppCMSPresenter.this.refreshSubscriptionData(
                                AppCMSPresenter.this::sendRefreshPageAction, true);
                    }, action1
            );
        } catch (IOException e) {
            //
        }
    }

    public Typeface getRegularFontFace() {
        return regularFontFace;
    }

    public void setRegularFontFace(Typeface regularFontFace) {
        this.regularFontFace = regularFontFace;
    }

    public Typeface getBoldTypeFace() {
        return boldTypeFace;
    }

    public void setBoldTypeFace(Typeface boldTypeFace) {
        this.boldTypeFace = boldTypeFace;
    }

    public Typeface getSemiBoldTypeFace() {
        return semiBoldTypeFace;
    }

    public void setSemiBoldTypeFace(Typeface semiBoldTypeFace) {
        this.semiBoldTypeFace = semiBoldTypeFace;
    }

    public Typeface getExtraBoldTypeFace() {
        return extraBoldTypeFace;
    }

    public void setExtraBoldTypeFace(Typeface extraBoldTypeFace) {
        this.extraBoldTypeFace = extraBoldTypeFace;
    }

    public void showPopupWindowPlayer(View v, long watchedTime) {
        //  dismissPopupWindowPlayer();
        // FIXME: this should not be using a hardcoded url.
//        Uri mp4VideoUri = Uri.parse("https://vtgcmp4-snagfilms.akamaized.net/video_assets/2015/mp4/1960_Masters/1960_01DL/1960_01DL_1280kbps.mp4");
        Uri mp4VideoUri = Uri.parse("https://vhoichoi.viewlift.com/encodes/originals/12/hls/master.m3u8");
        pipPlayerVisible = true;

        videoPlayerViewPIP = ViewCreator.playerView(currentActivity,
                this,
                mp4VideoUri.toString(),
                null,
                null,
                watchedTime);

        //videoPlayerViewPIP.setCurrentPosition(videoPlayerViewPage.getCurrentPosition());
        relativeLayoutPIP = new RelativeLayout(currentActivity);

        RelativeLayout.LayoutParams lpPipView = new RelativeLayout.LayoutParams(750, 450);
        lpPipView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpPipView.addRule(RelativeLayout.ABOVE, R.id.app_cms_tab_nav_container);
        lpPipView.rightMargin = 50;
        lpPipView.bottomMargin = 20;

        relativeLayoutPIP.setLayoutParams(lpPipView);

        relativeLayoutPIP.addView(videoPlayerViewPIP);
        relativeLayoutPIP.setVisibility(View.VISIBLE);

        ((RelativeLayout) currentActivity.findViewById(R.id.app_cms_parent_view)).addView(relativeLayoutPIP);
    }

    public void showPopupWindowPlayer(View v, String url) {
        //  dismissPopupWindowPlayer();
        // FIXME: this should not be using a hardcoded url.
//        Uri mp4VideoUri = Uri.parse("https://vtgcmp4-snagfilms.akamaized.net/video_assets/2015/mp4/1960_Masters/1960_01DL/1960_01DL_1280kbps.mp4");
        Uri mp4VideoUri = Uri.parse("https://vhoichoi.viewlift.com/encodes/originals/12/hls/master.m3u8");
//        Uri mp4VideoUri = Uri.parse(url);
        pipPlayerVisible = true;

//        videoPlayerViewPIP = ViewCreator.playerView(currentActivity, mp4VideoUri.toString());
        videoPlayerViewPIP = ViewCreator.playerView(currentActivity, this, url, null, null, 0L);

        //videoPlayerViewPIP.setCurrentPosition(videoPlayerViewPage.getCurrentPosition());
        relativeLayoutPIP = new RelativeLayout(currentActivity);

        RelativeLayout.LayoutParams lpPipView = new RelativeLayout.LayoutParams(750, 450);
        lpPipView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpPipView.addRule(RelativeLayout.ABOVE, R.id.app_cms_tab_nav_container);
        lpPipView.rightMargin = 50;
        lpPipView.bottomMargin = 20;

        relativeLayoutPIP.setLayoutParams(lpPipView);

        relativeLayoutPIP.addView(videoPlayerViewPIP);
        relativeLayoutPIP.setVisibility(View.VISIBLE);

        ((RelativeLayout) currentActivity.findViewById(R.id.app_cms_parent_view)).addView(relativeLayoutPIP);
    }

    public void pausePIP() {
        try {
            if (videoPlayerViewPIP != null) {
                videoPlayerViewPIP.pausePlayer();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void resumePIP() {
        try {
            if (videoPlayerViewPIP != null) {
                videoPlayerViewPIP.startPlayer();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void dismissPopupWindowPlayer() {
        try {
            if (videoPlayerViewPIP != null) {

                videoPlayerViewPIP.releasePlayer();
                videoPlayerViewPIP = null;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        try {
            if (relativeLayoutPIP != null) {
                //  relativeLayout.removeAllViews();
                relativeLayoutPIP.setVisibility(View.GONE);
                RelativeLayout rootView = ((RelativeLayout) currentActivity.findViewById(R.id.app_cms_parent_view));
                rootView.postDelayed(() -> {
                    rootView.removeView(relativeLayoutPIP);
                    relativeLayoutPIP = null;
                }, 100);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        pipPlayerVisible = false;
    }

    public int getFirstVisibleChildPosition(NestedScrollView nestedScrollView) {

        final Rect scrollBounds = new Rect();
        nestedScrollView.getHitRect(scrollBounds);
        LinearLayout holder = (LinearLayout) nestedScrollView.getChildAt(0);
        for (int i = 0; i < holder.getChildCount(); i++) {
            View childView = holder.getChildAt(i);
            if (childView != null) {
                if (childView.getLocalVisibleRect(scrollBounds)) {
                    //Here is the position of first visible view
                    return i;

                }
            }
        }
        return 0;
    }

    public void showPopUpMenuSports(View v) {
        PopupMenu popupMenu = new PopupMenu(getCurrentActivity());
        popupMenu.showLocation(v.getId(), getCurrentActivity());
    }

    public void showEmptySearchToast() {
        showToast(getCurrentActivity().getResources().getString(R.string.search_blank_toast_msg), Toast.LENGTH_SHORT);
    }

    public Action0 getAfterLoginAction() {
        return afterLoginAction;
    }

    public void setAfterLoginAction(Action0 afterLoginAction) {
        this.afterLoginAction = afterLoginAction;
        this.shouldLaunchLoginAction = false;
    }

    public enum LaunchType {
        SUBSCRIBE, LOGIN_AND_SIGNUP, INIT_SIGNUP, HOME
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
        DELETE_ONE_WATCHLIST_ITEM,
        DELETE_ALL_WATCHLIST_ITEMS,
        DELETE_ONE_DOWNLOAD_ITEM,
        RE_START_DOWNLOAD_ITEM,
        DELETE_ALL_DOWNLOAD_ITEMS,
        LOGIN_REQUIRED,
        SUBSCRIPTION_REQUIRED,
        SUBSCRIPTION_REQUIRED_PLAYER,
        LOGIN_AND_SUBSCRIPTION_REQUIRED,
        LOGIN_AND_SUBSCRIPTION_REQUIRED_PLAYER,
        LOGOUT_WITH_RUNNING_DOWNLOAD,
        EXISTING_SUBSCRIPTION,
        EXISTING_SUBSCRIPTION_LOGOUT,
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
        UNKNOWN_SUBSCRIPTION_FOR_CANCEL,
        SIGN_OUT,
        DOWNLOAD_VIA_MOBILE_DISABLED,
        VIDEO_NOT_AVAILABLE
    }

    public enum RETRY_TYPE {
        VIDEO_ACTION, BUTTON_ACTION, PAGE_ACTION, SEARCH_RETRY_ACTION, WATCHLIST_RETRY_ACTION,
        HISTORY_RETRY_ACTION, RESET_PASSWORD_RETRY, LOGOUT_ACTION, EDIT_WATCHLIST
    }

    public enum ExtraScreenType {
        NAVIGATION,
        SEARCH,
        DRAGGABLE_PANEL,
        RESET_PASSWORD,
        CHANGE_PASSWORD,
        EDIT_PROFILE,
        CCAVENUE,
        TERM_OF_SERVICE,
        BLANK,
        NONE
    }

    private interface OnRunOnUIThread {
        void runOnUiThread(Action0 runOnUiThreadAction);
    }

    private static class DownloadTimerTask extends TimerTask {
        final String filmIdLocal;
        final long videoId;
        final OnRunOnUIThread onRunOnUIThread;
        final boolean isTablet;
        final AppCMSPresenter appCMSPresenter;
        volatile ImageView imageView;
        final Action1<UserVideoDownloadStatus> responseAction;
        final Timer timer;
        final int radiusDifference;
        final String id;
        volatile boolean cancelled;
        volatile boolean finished;
        volatile boolean running;

        public DownloadTimerTask(String filmId,
                                 long videoId,
                                 OnRunOnUIThread onRunOnUIThread,
                                 boolean isTablet,
                                 AppCMSPresenter appCMSPresenter,
                                 ImageView imageView,
                                 Action1<UserVideoDownloadStatus> responseAction,
                                 Timer timer,
                                 int radiusDifference,
                                 String id) {
            this.filmIdLocal = filmId;
            this.videoId = videoId;
            this.onRunOnUIThread = onRunOnUIThread;
            this.isTablet = isTablet;
            this.appCMSPresenter = appCMSPresenter;
            this.imageView = imageView;
            this.responseAction = responseAction;
            this.timer = timer;
            this.radiusDifference = radiusDifference;
            this.cancelled = false;
            this.finished = false;
            this.id = id;
            this.running = false;
        }

        @Override
        public boolean cancel() {
            this.cancelled = true;
            this.running = false;
            return super.cancel();
        }

        @Override
        public void run() {
            this.running = true;
            try {
                DownloadManager.Query query = new DownloadManager.Query();
                DownloadManager.Request request;
                query.setFilterById(videoId);
                Cursor c = appCMSPresenter.downloadManager.query(query);
                if (c != null && c.moveToFirst()) {
                    long totalSize = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    long downloaded = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int downloadStatus = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    String filmId =
                            c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    Log.e(TAG, "Updating download status for: " + filmId +" status:- "+downloadStatus +" " +(downloadStatus==DownloadManager.STATUS_FAILED)+" " +(downloadStatus==DownloadManager.STATUS_PAUSED));

                    c.close();


                    int downloadPercent = (int) (downloaded * 100.0 / totalSize + 0.5);
                    //Log.d(TAG, "download progress =" + downloaded + " total-> " + totalSize + " " + downloadPercent);
                    //Log.d(TAG, "download getCanonicalName " + filmIdLocal);
                    if (onRunOnUIThread != null &&
                            appCMSPresenter.runUpdateDownloadIconTimer &&
                            appCMSPresenter.isUserLoggedIn()) {
                        if ((downloaded >= totalSize ||
                                downloadPercent > 100 ||
                                downloadStatus == DownloadManager.STATUS_SUCCESSFUL ||
                                downloadStatus == DownloadManager.STATUS_FAILED) && totalSize > 0) {
                            cancelled = true;
                            this.cancel();
                        }
                        onRunOnUIThread.runOnUiThread(() -> {
                            if ((downloaded >= totalSize ||
                                    downloadPercent > 100 ||
                                    downloadStatus == DownloadManager.STATUS_SUCCESSFUL) &&
                                    totalSize > 0 &&
                                    !finished) {
                                imageView.setImageBitmap(null);
                                imageView.requestLayout();
                                appCMSPresenter.appCMSUserDownloadVideoStatusCall
                                        .call(filmIdLocal, appCMSPresenter, responseAction, appCMSPresenter.getLoggedInUser());
                                finished = true;
                                Log.e(TAG, "Film download completed: " + filmId);
                            } else if (downloadStatus == DownloadManager.STATUS_RUNNING) {
                                try {
                                    if ((imageView.getTag() == null) ||
                                            (imageView.getTag() != null &&
                                                    imageView.getTag() instanceof String &&
                                                    imageView.getTag().equals(filmIdLocal))) {
//                                        int radiusDifference = 5;
//                                        if (isTablet) {
//                                            radiusDifference = 2;
//                                        }
                                        imageView.setBackground(null);
                                        circularImageBar(imageView, downloadPercent, radiusDifference);
                                    } else if (cancelled) {
                                        imageView.setImageBitmap(null);
                                        imageView.requestLayout();
                                    }
                                } catch (Exception e) {
                                    //Log.e(TAG, "Error rendering circular image bar");
                                }
                                Log.e(TAG, "Updating film download progress: " + filmId);
                            } else if (downloadStatus == DownloadManager.STATUS_FAILED ||
                                    //downloadStatus == DownloadManager.STATUS_PAUSED ||
                                    downloadStatus==403 ||
                                    downloadStatus==195) {
                                Log.e(TAG, "Failed to download film: " + filmId);
                                updateDownloadStatusException();
                            }else if(downloadStatus == DownloadManager.STATUS_PAUSED){
                                appCMSPresenter.appCMSUserDownloadVideoStatusCall
                                        .call(filmIdLocal, appCMSPresenter, responseAction, appCMSPresenter.getLoggedInUser());
                            }
                        });
                    }
                } else {
                    //noinspection ConstantConditions
                    Log.e(TAG, " Downloading failed: " + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
                    imageView.setImageBitmap(null);
                    imageView.setBackground(ContextCompat.getDrawable(imageView.getContext(),
                            android.R.drawable.stat_sys_warning));
                }
            } catch (StaleDataException exception) {

            } catch (Exception exception) {
                Log.e(TAG, filmIdLocal + " Removed from top +++ " + exception.getMessage());
                updateDownloadStatusException();
            }


        }
        private void updateDownloadStatusException(){
            this.cancel();
            UserVideoDownloadStatus statusResponse = new UserVideoDownloadStatus();
            statusResponse.setDownloadStatus(DownloadStatus.STATUS_INTERRUPTED);

            if (onRunOnUIThread != null) {
                try {
                    onRunOnUIThread.runOnUiThread(() -> {
                        try {
                            DownloadVideoRealm downloadVideoRealm = appCMSPresenter.realmController.getRealm()
                                    .copyFromRealm(
                                            appCMSPresenter.realmController
                                                    .getDownloadByIdBelongstoUser(filmIdLocal, appCMSPresenter.getLoggedInUser()));
                            downloadVideoRealm.setDownloadStatus(statusResponse.getDownloadStatus());
                            appCMSPresenter.realmController.updateDownload(downloadVideoRealm);

                            Observable.just(statusResponse)
                                    .onErrorResumeNext(throwable -> Observable.empty())
                                    .subscribe(responseAction);
                            //   removeDownloadedFile(filmIdLocal);
                        } catch (Exception e) {
                            //Log.e(TAG, "Error rendering circular image bar");
                        }
                    });
                } catch (Exception e) {

                }
            }
        }
        private void circularImageBar(ImageView iv2, int i, int radiusDifference) {
            if (appCMSPresenter.runUpdateDownloadIconTimer) {
                Bitmap b = Bitmap.createBitmap(iv2.getWidth(), iv2.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(b);
                Paint paint = new Paint();

                paint.setColor(Color.DKGRAY);
                paint.setStrokeWidth(iv2.getWidth() / 10);
                paint.setStyle(Paint.Style.STROKE);
                paint.setAntiAlias(true);
                canvas.drawCircle(iv2.getWidth() / 2, iv2.getHeight() / 2, (iv2.getWidth() / 2) - radiusDifference, paint);// Fix SVFA-1561 changed  -2 to -7

//                if (isTablet) {
//                    canvas.drawCircle(iv2.getWidth() / 2, iv2.getHeight() / 2, (iv2.getWidth() / 2) - 5, paint);
//                } else {
//                    canvas.drawCircle(iv2.getWidth() / 2, iv2.getHeight() / 2, (iv2.getWidth() / 2) - 5, paint);// Fix SVFA-1561 changed  -2 to -7
//                }

                int tintColor = Color.parseColor((appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()));
                paint.setColor(tintColor);
                paint.setStrokeWidth(iv2.getWidth() / 10);
                paint.setAntiAlias(true);
//                paint.setStyle(Paint.Style.FILL);
                final RectF oval = new RectF();
                paint.setStyle(Paint.Style.STROKE);
                oval.set(radiusDifference, radiusDifference, iv2.getWidth() - radiusDifference, iv2.getHeight() - radiusDifference); //Fix SVFA-1561  change 2 to 6

//                if (isTablet) {
//                    oval.set(2, 2, iv2.getWidth() - 2, iv2.getHeight() - radiusDifference);
//                } else {
//                    oval.set(6, 6, iv2.getWidth() - 6, iv2.getHeight() - radiusDifference); //Fix SVFA-1561  change 2 to 6
//                }
                canvas.drawArc(oval, 270, ((i * 360) / 100), false, paint);


                iv2.setImageBitmap(b);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    iv2.setForegroundGravity(View.TEXT_ALIGNMENT_CENTER);
                }
                iv2.requestLayout();
            }
        }

    }

    private UAAssociateNamedUserRequest getUAAssociateNamedUserRequest(String userId) {
        UAAssociateNamedUserRequest uaAssociateNamedUserRequest = new UAAssociateNamedUserRequest();
        uaAssociateNamedUserRequest.setNamedUserId(userId);
        if (currentContext != null) {
            uaAssociateNamedUserRequest.setDeviceType(currentContext.getString(R.string.ua_android_device_key));
        }
        uaAssociateNamedUserRequest.setChannelId(UAirship.shared().getPushManager().getChannelId());

        return uaAssociateNamedUserRequest;
    }

    private void sendUALoggedInEvent(String userId) {
        if (currentContext != null &&
                currentContext.getResources().getBoolean(R.bool.send_ua_user_churn_events)) {
            urbanAirshipEventPresenter.sendUserLoginEvent(userId,
                    uaNamedUserRequest -> {
                        sendUANamedUserEventRequest(uaNamedUserRequest);
                        sendUAAssociateUserEventRequest(getUAAssociateNamedUserRequest(userId),
                                true);
                    });
        }
    }

    private void sendUALoggedOutEvent(String userId) {
        if (currentContext != null &&
                currentContext.getResources().getBoolean(R.bool.send_ua_user_churn_events)) {
            urbanAirshipEventPresenter.sendUserLogoutEvent(userId,
                    uaNamedUserRequest -> {
                        sendUANamedUserEventRequest(uaNamedUserRequest);
                        sendUAAssociateUserEventRequest(getUAAssociateNamedUserRequest(userId),
                                false);
                    });
        }
    }

    private void sendUASubscribedEvent(String userId) {
        if (currentContext != null &&
                currentContext.getResources().getBoolean(R.bool.send_ua_user_churn_events)) {
            urbanAirshipEventPresenter.sendSubscribedEvent(userId,
                    uaNamedUserRequest -> {
                        sendUANamedUserEventRequest(uaNamedUserRequest);
                    });
        }
    }

    private void sendUAUnsubscribedEvent(String userId) {
        if (currentContext != null &&
                currentContext.getResources().getBoolean(R.bool.send_ua_user_churn_events)) {
            urbanAirshipEventPresenter.sendUnsubscribedEvent(userId,
                    uaNamedUserRequest -> {
                        sendUANamedUserEventRequest(uaNamedUserRequest);
                    });
        }
    }

    private void sendUASubscriptionAboutToExpireEvent(String userId) {
        if (currentContext != null &&
                currentContext.getResources().getBoolean(R.bool.send_ua_user_churn_events)) {
            urbanAirshipEventPresenter.sendSubscriptionAboutToExpireEvent(userId,
                    uaNamedUserRequest -> {
                        sendUANamedUserEventRequest(uaNamedUserRequest);
                    });
        }
    }

    private void sendUASubscriptionEndDateEvent(String userId, String subscriptionEndDate) {
        if (currentContext != null &&
                currentContext.getResources().getBoolean(R.bool.send_ua_user_churn_events)) {
            urbanAirshipEventPresenter.sendSubscriptionEndDateEvent(userId,
                    subscriptionEndDate,
                    uaNamedUserRequest -> {
                        sendUANamedUserEventRequest(uaNamedUserRequest);
                    });
        }
    }

    private void sendUASubscriptionPlanEvent(String userId, String subscriptionPlan) {
        if (currentContext != null &&
                currentContext.getResources().getBoolean(R.bool.send_ua_user_churn_events)) {
            urbanAirshipEventPresenter.sendSubscriptionPlanEvent(userId,
                    subscriptionPlan,
                    uaNamedUserRequest -> {
                        sendUANamedUserEventRequest(uaNamedUserRequest);
                    });
        }
    }

    private PostUANamedUserEventAsyncTask.Params getUAParams() {
        return new PostUANamedUserEventAsyncTask.Params
                .Builder()
                .accessKey(UAirship.shared().getAirshipConfigOptions().getAppKey())
                /** This value should ideally come from the Site.json response (2017-12-22 WIP AC-1384) */
                .authKey("4qiw5pNUSuaw5HfAfVf-AQ") /** Production */
//                        .authKey("9NvLFbMITeuJtb-AqrwOpw") /** QA */
                .build();
    }

    private void sendUAAssociateUserEventRequest(UAAssociateNamedUserRequest uaAssociateNamedUserRequest,
                                                 boolean associate) {
        PostUANamedUserEventAsyncTask.Params params = getUAParams();

        new PostUANamedUserEventAsyncTask(uaNamedUserEventCall)
                .execute(params, uaAssociateNamedUserRequest, associate);
    }

    private void sendUANamedUserEventRequest(UANamedUserRequest uaNamedUserRequest) {
        PostUANamedUserEventAsyncTask.Params params = getUAParams();

        new PostUANamedUserEventAsyncTask(uaNamedUserEventCall)
                .execute(params, uaNamedUserRequest);
    }

    private static class EntitlementCheckActive implements Action1<UserIdentity> {
        private final Action0 onFailAction;
        private final Action0 onSuccessAction;
        private String pagePath;
        private String action;
        private String filmTitle;
        private String[] extraData;
        private ContentDatum contentDatum;
        private boolean closeLauncher;
        private int currentlyPlayingIndex;
        private List<String> relateVideoIds;
        private boolean success;

        EntitlementCheckActive(Action0 onSuccessAction, Action0 onFailAction) {
            this.onSuccessAction = onSuccessAction;
            this.onFailAction = onFailAction;
            this.success = false;
        }

        @Override
        public void call(UserIdentity userIdentity) {
            if (!userIdentity.isSubscribed()) {
                onFailAction.call();
                success = false;
            } else {
                onSuccessAction.call();
                success = true;
            }
        }

        String getPagePath() {
            return pagePath;
        }

        void setPagePath(String pagePath) {
            this.pagePath = pagePath;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        String getFilmTitle() {
            return filmTitle;
        }

        void setFilmTitle(String filmTitle) {
            this.filmTitle = filmTitle;
        }

        public String[] getExtraData() {
            return extraData;
        }

        public void setExtraData(String[] extraData) {
            this.extraData = extraData;
        }

        public ContentDatum getContentDatum() {
            return contentDatum;
        }

        public void setContentDatum(ContentDatum contentDatum) {
            this.contentDatum = contentDatum;
        }

        boolean isCloseLauncher() {
            return closeLauncher;
        }

        void setCloseLauncher(boolean closeLauncher) {
            this.closeLauncher = closeLauncher;
        }

        int getCurrentlyPlayingIndex() {
            return currentlyPlayingIndex;
        }

        void setCurrentlyPlayingIndex(int currentlyPlayingIndex) {
            this.currentlyPlayingIndex = currentlyPlayingIndex;
        }

        List<String> getRelateVideoIds() {
            return relateVideoIds;
        }

        void setRelateVideoIds(List<String> relateVideoIds) {
            this.relateVideoIds = relateVideoIds;
        }

        boolean isSuccess() {
            return success;
        }

        void setSuccess(boolean success) {
            this.success = success;
        }
    }

    private static class DownloadQueueItem {
        ContentDatum contentDatum;
        Action1<UserVideoDownloadStatus> resultAction1;
        boolean isDownloadedFromOther;
    }

    private static class DownloadQueueThread extends Thread {
        private final AppCMSPresenter appCMSPresenter;
        private final Queue<DownloadQueueItem> filmDownloadQueue;
        private final List<String> filmsInQueue;
        private String downloadURL;
        private long file_size = 0;

        private boolean running;
        private boolean startNextDownload;

        DownloadQueueThread(AppCMSPresenter appCMSPresenter) {
            this.appCMSPresenter = appCMSPresenter;
            this.filmDownloadQueue = new ConcurrentLinkedQueue<>();
            this.filmsInQueue = new ArrayList<>();
            this.running = false;
            this.startNextDownload = true;
        }

        void addToQueue(DownloadQueueItem downloadQueueItem) {
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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //Log.e(TAG, "Error while running download queue: " + e.getMessage());
                }
            }
        }

        boolean running() {
            return running;
        }

        @SuppressWarnings("unused")
        public void setRunning(boolean running) {
            this.running = running;
        }

        void setStartNextDownload() {
            this.startNextDownload = true;
        }
    }

    private static class BeaconRunnable implements Runnable {
        final AppCMSBeaconRest appCMSBeaconRest;
        String url;

        BeaconRunnable(AppCMSBeaconRest appCMSBeaconRest) {
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
                    //Log.d(TAG, "Succeeded to send Beacon message: " + response.code());
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    //Log.d(TAG, "Failed to send Beacon message: " + t.getMessage());
                }
            });
        }
    }

    private abstract static class AppCMSPageAPIAction implements Action1<AppCMSPageAPI> {
        final boolean appbarPresent;
        final boolean fullscreenEnabled;
        final boolean navbarPresent;
        final AppCMSPageUI appCMSPageUI;
        final String action;
        final String pageId;
        final String pageTitle;
        final String pagePath;
        final boolean launchActivity;
        final boolean sendCloseAction;
        final Uri searchQuery;

        AppCMSPageAPIAction(boolean appbarPresent,
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
        final boolean appbarPresent;
        final boolean fullscreenEnabled;
        final boolean navbarPresent;
        final AppCMSPageUI appCMSPageUI;
        final String action;
        final String pageId;
        final String pageTitle;
        final String pagePath;
        final boolean launchActivity;
        final Uri searchQuery;

        AppCMSWatchlistAPIAction(boolean appbarPresent,
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
        final boolean appbarPresent;
        final boolean fullscreenEnabled;
        final boolean navbarPresent;
        final AppCMSPageUI appCMSPageUI;
        final String action;
        final String pageId;
        final String pageTitle;
        final String pagePath;
        final boolean launchActivity;
        final Uri searchQuery;

        AppCMSHistoryAPIAction(boolean appbarPresent,
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

    @SuppressWarnings("unused")
    private abstract static class AppCMSSubscriptionAPIAction
            implements Action1<AppCMSSubscriptionResult> {

        final boolean appbarPresent;
        final boolean fullscreenEnabled;
        final boolean navbarPresent;
        final AppCMSPageUI appCMSPageUI;
        final String action;
        final String pageId;
        final String pageTitle;
        final boolean launchActivity;
        final Uri searchQuery;

        @SuppressWarnings("unused")
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

    public static class EntitlementPendingVideoData {
        String pagePath;
        String action;
        String filmTitle;
        String[] extraData;
        ContentDatum contentDatum;
        boolean closeLauncher;
        int currentlyPlayingIndex;
        List<String> relateVideoIds;
        long currentWatchedTime;

        public static class Builder {
            final EntitlementPendingVideoData entitlementPendingVideoData;

            public Builder() {
                entitlementPendingVideoData = new EntitlementPendingVideoData();
            }

            public Builder pagePath(String pagePath) {
                entitlementPendingVideoData.pagePath = pagePath;
                return this;
            }

            public Builder action(String action) {
                entitlementPendingVideoData.action = action;
                return this;
            }

            public Builder filmTitle(String filmTitle) {
                entitlementPendingVideoData.filmTitle = filmTitle;
                return this;
            }

            public Builder extraData(String[] extraData) {
                entitlementPendingVideoData.extraData = extraData;
                return this;
            }

            public Builder contentDatum(ContentDatum contentDatum) {
                entitlementPendingVideoData.contentDatum = contentDatum;
                return this;
            }

            public Builder closerLauncher(boolean closeLauncher) {
                entitlementPendingVideoData.closeLauncher = closeLauncher;
                return this;
            }

            public Builder currentlyPlayingIndex(int currentlyPlayingIndex) {
                entitlementPendingVideoData.currentlyPlayingIndex = currentlyPlayingIndex;
                return this;
            }

            public Builder relatedVideoIds(List<String> relatedVideosIds) {
                entitlementPendingVideoData.relateVideoIds = relatedVideosIds;
                return this;
            }

            public Builder currentWatchedTime(long currentWatchedTime) {
                entitlementPendingVideoData.currentWatchedTime = currentWatchedTime;
                return this;
            }

            public EntitlementPendingVideoData build() {
                return entitlementPendingVideoData;
            }
        }
    }

    public static class SemVer {
        private static final String SEMVER_REGEX = "(\\d+)\\.(\\d+)\\.(\\d+)";
        int major;
        int minor;
        int patch;
        String original;

        public void parse(String original) {
            this.original = original;

            Matcher semverMatcher = Pattern.compile(SEMVER_REGEX).matcher(original);
            if (semverMatcher.find()) {
                if (semverMatcher.group(1) != null) {
                    major = Integer.parseInt(semverMatcher.group(1));
                }

                if (semverMatcher.group(2) != null) {
                    minor = Integer.parseInt(semverMatcher.group(2));
                }

                if (semverMatcher.group(3) != null) {
                    patch = Integer.parseInt(semverMatcher.group(3));
                }
            }
        }
    }
}
