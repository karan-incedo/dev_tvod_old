package com.viewlift.presenters;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.apptentive.android.sdk.Apptentive;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.iid.InstanceID;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.AppCMSStreamingInfo;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.StreamingInfo;
import com.viewlift.models.data.appcms.history.AppCMSHistoryResult;
import com.viewlift.models.data.appcms.history.UpdateHistoryRequest;
import com.viewlift.models.data.appcms.history.UserVideoStatusResponse;
import com.viewlift.models.data.appcms.sites.AppCMSSite;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidUI;
import com.viewlift.models.data.appcms.ui.android.MetaPage;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.android.Primary;
import com.viewlift.models.data.appcms.ui.android.User;
import com.viewlift.models.data.appcms.ui.authentication.FacebookLoginResponse;
import com.viewlift.models.data.appcms.ui.authentication.ForgotPasswordResponse;
import com.viewlift.models.data.appcms.ui.authentication.RefreshIdentityResponse;
import com.viewlift.models.data.appcms.ui.authentication.SignInResponse;
import com.viewlift.models.data.appcms.ui.authentication.UserIdentity;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.watchlist.AppCMSWatchlistResult;
import com.viewlift.models.network.background.tasks.GetAppCMSAPIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSAndroidUIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSMainUIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSPageUIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSRefreshIdentityAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSSiteAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSStreamingInfoAsyncTask;
import com.viewlift.models.network.background.tasks.PostAppCMSLoginRequestAsyncTask;
import com.viewlift.models.network.components.AppCMSAPIComponent;
import com.viewlift.models.network.components.AppCMSSearchUrlComponent;
import com.viewlift.models.network.components.DaggerAppCMSAPIComponent;
import com.viewlift.models.network.components.DaggerAppCMSSearchUrlComponent;
import com.viewlift.models.network.modules.AppCMSAPIModule;
import com.viewlift.models.network.modules.AppCMSSearchUrlModule;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import com.viewlift.models.network.rest.AppCMSBeaconRest;
import com.viewlift.models.network.rest.AppCMSFacebookLoginCall;
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
import com.viewlift.models.network.rest.AppCMSUpdateWatchHistoryCall;
import com.viewlift.models.network.rest.AppCMSUserIdentityCall;
import com.viewlift.models.network.rest.AppCMSUserVideoStatusCall;
import com.viewlift.models.network.rest.AppCMSWatchlistCall;
import com.viewlift.views.activity.AppCMSErrorActivity;
import com.viewlift.views.activity.AppCMSPageActivity;
import com.viewlift.views.activity.AppCMSPlayVideoActivity;
import com.viewlift.views.activity.AppCMSSearchActivity;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.fragments.AppCMSNavItemsFragment;
import com.viewlift.views.fragments.AppCMSResetPasswordFragment;
import com.viewlift.views.fragments.AppCMSSearchFragment;
import com.viewlift.views.fragments.AppCMSSettingsFragment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/3/17.
 */

public class AppCMSPresenter {
    private static final String TAG = "AppCMSPresenter";

    public static final String PRESENTER_NAVIGATE_ACTION = "appcms_presenter_navigate_action";
    public static final String PRESENTER_PAGE_LOADING_ACTION = "appcms_presenter_page_loading_action";
    public static final String PRESENTER_STOP_PAGE_LOADING_ACTION = "appcms_presenter_stop_page_loading_action";
    public static final String PRESENTER_CLOSE_SCREEN_ACTION = "appcms_presenter_close_action";
    public static final String PRESENTER_RESET_NAVIGATION_ITEM_ACTION = "appcms_presenter_set_navigation_item_action";
    public static final String PRESENTER_DEEPLINK_ACTION = "appcms_presenter_deeplink_action";

    private static final String LOGIN_SHARED_PREF_NAME = "login_pref";
    private static final String USER_ID_SHARED_PREF_NAME = "user_id_pref";
    private static final String REFRESH_TOKEN_SHARED_PREF_NAME = "refresh_token_pref";

    private static final String AUTH_TOKEN_SHARED_PREF_NAME = "auth_token_pref";

    private static final long MILLISECONDS_PER_SECOND = 1000L;

    private final AppCMSMainUICall appCMSMainUICall;
    private final AppCMSAndroidUICall appCMSAndroidUICall;
    private final AppCMSPageUICall appCMSPageUICall;
    private final AppCMSSiteCall appCMSSiteCall;
    private final AppCMSSearchCall appCMSSearchCall;
    private final AppCMSSignInCall appCMSSignInCall;
    private final AppCMSRefreshIdentityCall appCMSRefreshIdentityCall;
    private final AppCMSResetPasswordCall appCMSResetPasswordCall;
    private final AppCMSFacebookLoginCall appCMSFacebookLoginCall;
    private final AppCMSUserIdentityCall appCMSUserIdentityCall;

    private final AppCMSUpdateWatchHistoryCall appCMSUpdateWatchHistoryCall;

    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final Map<String, String> pageNameToActionMap;
    private final Map<String, AppCMSPageUI> actionToPageMap;
    private final Map<String, AppCMSPageAPI> actionToPageAPIMap;
    private final Map<String, AppCMSActionType> actionToActionTypeMap;

    private final AppCMSWatchlistCall appCMSWatchlistCall;
    private final AppCMSHistoryCall appCMSHistoryCall;
    private final AppCMSUserVideoStatusCall appCMSUserVideoStatusCall;

    private AppCMSPageAPICall appCMSPageAPICall;
    private AppCMSStreamingInfoCall appCMSStreamingInfoCall;
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

    private BeaconRunnable beaconMessageRunnable;
    private Runnable beaconMessageThread;
    private GoogleAnalytics googleAnalytics;
    private Tracker tracker;

    private String tvHomeScreenPackage = "com.viewlift.tv.views.activity.AppCmsHomeActivity";
    private String tvErrorScreenPackage = "com.viewlift.tv.views.activity.AppCmsTvErrorActivity";

    private Uri deeplinkSearchQuery;
    private String refreshToken;
    private MetaPage loginPage;

    private PlatformType platformType;

    public enum PlatformType {
        ANDROID, TV
    }

    private AppCMSNavItemsFragment appCMSNavItemsFragment;

    public enum BeaconEvent {
        PLAY, RESUME, PING, AD_REQUEST, AD_IMPRESSION
    }

    public enum Error {
        NETWORK, SIGNIN
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
        boolean launchActivity;
        Uri searchQuery;

        public AppCMSWatchlistAPIAction(boolean appbarPresent,
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

    private static abstract class AppCMSHistoryAPIAction implements Action1<AppCMSHistoryResult> {
        boolean appbarPresent;
        boolean fullscreenEnabled;
        boolean navbarPresent;
        AppCMSPageUI appCMSPageUI;
        String action;
        String pageId;
        String pageTitle;
        boolean launchActivity;
        Uri searchQuery;

        public AppCMSHistoryAPIAction(boolean appbarPresent,
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

    @Inject
    public AppCMSPresenter(AppCMSMainUICall appCMSMainUICall,
                           AppCMSAndroidUICall appCMSAndroidUICall,
                           AppCMSPageUICall appCMSPageUICall,
                           AppCMSSiteCall appCMSSiteCall,
                           AppCMSSearchCall appCMSSearchCall,

                           AppCMSWatchlistCall appCMSWatchlistCall,
                           AppCMSHistoryCall appCMSHistoryCall,

                           AppCMSBeaconRest appCMSBeaconRest,
                           AppCMSSignInCall appCMSSignInCall,
                           AppCMSRefreshIdentityCall appCMSRefreshIdentityCall,
                           AppCMSResetPasswordCall appCMSResetPasswordCall,
                           AppCMSFacebookLoginCall appCMSFacebookLoginCall,
                           AppCMSUserIdentityCall appCMSUserIdentityCall,

                           AppCMSUpdateWatchHistoryCall appCMSUpdateWatchHistoryCall,
                           AppCMSUserVideoStatusCall appCMSUserVideoStatusCall,

                           Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                           Map<String, String> pageNameToActionMap,
                           Map<String, AppCMSPageUI> actionToPageMap,
                           Map<String, AppCMSPageAPI> actionToPageAPIMap,
                           Map<String, AppCMSActionType> actionToActionTypeMap) {
        this.appCMSMainUICall = appCMSMainUICall;
        this.appCMSAndroidUICall = appCMSAndroidUICall;
        this.appCMSPageUICall = appCMSPageUICall;
        this.appCMSSiteCall = appCMSSiteCall;
        this.appCMSSearchCall = appCMSSearchCall;
        this.appCMSSignInCall = appCMSSignInCall;
        this.appCMSRefreshIdentityCall = appCMSRefreshIdentityCall;
        this.appCMSResetPasswordCall = appCMSResetPasswordCall;
        this.appCMSFacebookLoginCall = appCMSFacebookLoginCall;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.pageNameToActionMap = pageNameToActionMap;
        this.actionToPageMap = actionToPageMap;
        this.actionToPageAPIMap = actionToPageAPIMap;
        this.actionToActionTypeMap = actionToActionTypeMap;
        this.appCMSUserIdentityCall = appCMSUserIdentityCall;

        this.appCMSUpdateWatchHistoryCall = appCMSUpdateWatchHistoryCall;
        this.appCMSUserVideoStatusCall = appCMSUserVideoStatusCall;

        this.appCMSWatchlistCall = appCMSWatchlistCall;
        this.appCMSHistoryCall = appCMSHistoryCall;

        this.loadingPage = false;
        this.navigationPages = new HashMap<>();
        this.navigationPageData = new HashMap<>();
        this.pageIdToPageAPIUrlMap = new HashMap<>();
        this.actionToPageAPIUrlMap = new HashMap<>();
        this.actionToPageNameMap = new HashMap<>();
        this.pageIdToPageNameMap = new HashMap();
        this.actionTypeToMetaPageMap = new HashMap<>();
        this.onOrientationChangeHandlers = new ArrayList<>();
        this.onActionInternalEvents = new HashMap<>();
        this.currentActions = new Stack<>();
        this.beaconMessageRunnable = new BeaconRunnable(appCMSBeaconRest);
        this.beaconMessageThread = new Thread(this.beaconMessageRunnable);
    }

    public void setCurrentActivity(Activity activity) {
        this.currentActivity = activity;
    }

    public void unsetCurrentActivity(Activity closedActivity) {
        if (currentActivity == closedActivity) {
            currentActivity = null;
        }
    }

    public void initializeGA(String trackerId) {
        if (this.googleAnalytics == null) {
            this.googleAnalytics = GoogleAnalytics.getInstance(currentActivity);
            this.tracker = this.googleAnalytics.newTracker(trackerId);
        }
    }

    public boolean launchVideoPlayer(final String filmId,
                                     final String pagePath,
                                     final String filmTitle,
                                     final ContentDatum contentDatum) {
        boolean result = false;
        if (currentActivity != null &&
                !loadingPage && appCMSMain != null &&
                !TextUtils.isEmpty(appCMSMain.getApiBaseUrl()) &&
                !TextUtils.isEmpty(appCMSMain.getSite())) {
            result = true;
            final String action = currentActivity.getString(R.string.app_cms_action_watchvideo_key);
            String url = currentActivity.getString(R.string.app_cms_streaminginfo_api_url,
                    appCMSMain.getApiBaseUrl(),
                    filmId,
                    appCMSMain.getSite());
            GetAppCMSStreamingInfoAsyncTask.Params params =
                    new GetAppCMSStreamingInfoAsyncTask.Params.Builder().url(url).build();
            new GetAppCMSStreamingInfoAsyncTask(appCMSStreamingInfoCall,
                    new Action1<AppCMSStreamingInfo>() {
                        @Override
                        public void call(AppCMSStreamingInfo appCMSStreamingInfo) {
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
                                        streamingInfo.getVideoAssets().getMpeg().size() > 0 &&
                                        streamingInfo.getVideoAssets().getMpeg().get(0) != null &&
                                        !TextUtils.isEmpty(streamingInfo.getVideoAssets().getMpeg().get(0).getUrl())) {
                                    extraData[1] = streamingInfo.getVideoAssets().getMpeg().get(0).getUrl();
                                }
                                extraData[2] = filmId;
                                if (!TextUtils.isEmpty(extraData[1])) {
                                    launchButtonSelectedAction(pagePath,
                                            action,
                                            filmTitle,
                                            extraData,
                                            contentDatum,
                                            false);
                                }
                            }
                        }
                    }).execute(params);
        }
        return result;
    }

    public void updateWatchedTime(String filmId, long watchedTime) {
        if (getLoggedInUser(currentActivity) != null) {
            UpdateHistoryRequest updateHistoryRequest = new UpdateHistoryRequest();
            updateHistoryRequest.setUserId(getLoggedInUser(currentActivity));
            updateHistoryRequest.setWatchedTime(watchedTime);
            updateHistoryRequest.setVideoId(filmId);
            updateHistoryRequest.setSiteOwner(appCMSMain.getSite());

            String url = currentActivity.getString(R.string.app_cms_update_watch_history_api_url,
                    appCMSMain.getApiBaseUrl());

            appCMSUpdateWatchHistoryCall.call(url, getAuthToken(currentActivity), updateHistoryRequest,
                    new Action1<String>() {
                        @Override
                        public void call(String s) {
                            //
                        }
                    });
        }
    }

    public void getUserVideoStatus(String filmId, Action1<UserVideoStatusResponse> responseAction) {
        String url = currentActivity.getString(R.string.app_cms_video_status_api_url,
                appCMSMain.getApiBaseUrl(), filmId, appCMSMain.getSite());
        appCMSUserVideoStatusCall.call(url, getAuthToken(currentActivity), responseAction);
    }

    public boolean launchButtonSelectedAction(String pagePath,
                                              String action,
                                              String filmTitle,
                                              String[] extraData,
                                              ContentDatum contentDatum,
                                              final boolean closeLauncher) {
        boolean result = false;
        Log.d(TAG, "Attempting to load page " + filmTitle + ": " + pagePath);
        if (!isNetworkConnected()) {
            showErrorDialog(Error.NETWORK, null);
        } else if (currentActivity != null && !loadingPage) {
            AppCMSActionType actionType = actionToActionTypeMap.get(action);
            if (actionType == null) {
                Log.e(TAG, "Action " + action + " not found!");
                return false;
            }
            result = true;
            if (actionType == AppCMSActionType.PLAY_VIDEO_PAGE ||
                    actionType == AppCMSActionType.WATCH_TRAILER) {
                Intent playVideoIntent = new Intent(currentActivity, AppCMSPlayVideoActivity.class);
                if (actionType == AppCMSActionType.PLAY_VIDEO_PAGE) {
                    boolean requestAds = true;
                    if (pagePath != null && pagePath.contains(
                            currentActivity.getString(R.string.app_cms_action_qualifier_watchvideo_key))) {
                        requestAds = false;
                    }
                    playVideoIntent.putExtra(currentActivity.getString(R.string.play_ads_key), requestAds);
                    if (contentDatum != null &&
                            contentDatum.getGist() != null &&
                            contentDatum.getGist().getWatchedTime() != null) {
                        playVideoIntent.putExtra(currentActivity.getString(R.string.watched_time_key), contentDatum.getGist().getWatchedTime());
                    }
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
                                false,
                                closeLauncher,
                                null) {
                            @Override
                            public void call(AppCMSPageAPI appCMSPageAPI) {
                                if (appCMSPageAPI != null) {
                                    cancelInternalEvents();
                                    pushActionInternalEvents(this.action
                                            + BaseView.isLandscape(currentActivity));
                                    Bundle args = getPageActivityBundle(currentActivity,
                                            this.appCMSPageUI,
                                            appCMSPageAPI,
                                            this.pageId,
                                            appCMSPageAPI.getTitle(),
                                            screenName.toString(),
                                            loadFromFile,
                                            this.appbarPresent,
                                            this.fullscreenEnabled,
                                            this.navbarPresent,
                                            this.sendCloseAction,
                                            this.searchQuery);
                                    Intent updatePageIntent =
                                            new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                    updatePageIntent.putExtra(
                                            currentActivity.getString(R.string.app_cms_bundle_key),
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

    public boolean launchNavigationPage(String previousPageId, String previousPageName) {
        boolean result = false;
        if (currentActivity != null) {
            result = true;
            appCMSNavItemsFragment =
                    AppCMSNavItemsFragment.newInstance(currentActivity,
                            getAppCMSBinder(currentActivity,
                                    null,
                                    null,
                                    previousPageId,
                                    previousPageName,
                                    null,
                                    false,
                                    true,
                                    false,
                                    false,
                                    false,
                                    null),
                            Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor()),
                            Color.parseColor(appCMSMain.getBrand().getGeneral().getBackgroundColor()),
                            Color.parseColor(appCMSMain.getBrand().getGeneral().getPageTitleColor()));

            appCMSNavItemsFragment.show(((AppCompatActivity) currentActivity).getSupportFragmentManager(),
                    currentActivity.getString(R.string.app_cms_navigation_page_tag));
        }
        return result;
    }

    public void dismissOpenDialogs() {
        if (appCMSNavItemsFragment != null) {
            appCMSNavItemsFragment.dismiss();
        }
    }

    public void launchSearchPage() {
        if (currentActivity != null) {
            AppCMSSearchFragment appCMSSearchFragment = AppCMSSearchFragment.newInstance(currentActivity,
                    Color.parseColor(appCMSMain.getBrand().getGeneral().getBackgroundColor()),
                    Color.parseColor(appCMSMain.getBrand().getGeneral().getPageTitleColor()),
                    Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor()));
            appCMSSearchFragment.show(((AppCompatActivity) currentActivity).getSupportFragmentManager(),
                    currentActivity.getString(R.string.app_cms_search_page_tag));
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
            AppCMSResetPasswordFragment appCMSResetPasswordFragment =
                    AppCMSResetPasswordFragment.newInstance(currentActivity, email);
            appCMSResetPasswordFragment.show(((AppCompatActivity) currentActivity)
                            .getSupportFragmentManager(),
                    currentActivity.getString(R.string.app_cms_reset_password_page_tag));
        }
    }

    public void loginFacebook() {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_facebook_login_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSMain.getSite());
            appCMSFacebookLoginCall.call(url,
                    new Action1<FacebookLoginResponse>() {
                        @Override
                        public void call(FacebookLoginResponse facebookLoginResponse) {
                            if (facebookLoginResponse != null) {

                            }
                        }
                    });
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

    @SuppressWarnings("unused")
    public void navigateToWatchlistPage(String pageId, String pageTitle, String url,
                                        boolean launchActivity) {

        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);
            AppCMSPageAPI appCMSPageAPI = navigationPageData.get(pageId);

            getWatchlistPageContent(appCMSMain.getApiBaseUrl(),
                    pageIdToPageAPIUrlMap.get(pageId),
                    appCMSMain.getSite(),
                    true,
                    getPageId(appCMSPageUI), new AppCMSWatchlistAPIAction(true,
                            false,
                            true,
                            appCMSPageUI,
                            pageId,
                            pageId,
                            pageTitle,
                            launchActivity, null) {
                        @Override
                        public void call(AppCMSWatchlistResult appCMSWatchlistResult) {
                            if (appCMSWatchlistResult != null) {
                                cancelInternalEvents();
                                pushActionInternalEvents(this.pageId
                                        + BaseView.isLandscape(currentActivity));

                                AppCMSPageAPI pageAPI =
                                        appCMSWatchlistResult.convertToAppCMSPageAPI(this.pageId);

                                navigationPageData.put(this.pageId, pageAPI);

                                if (this.launchActivity) {
                                    launchPageActivity(currentActivity,
                                            this.appCMSPageUI,
                                            pageAPI,
                                            this.pageId,
                                            this.pageTitle,
                                            pageIdToPageNameMap.get(this.pageId),
                                            loadFromFile,
                                            this.appbarPresent,
                                            this.fullscreenEnabled,
                                            this.navbarPresent,
                                            false,
                                            this.searchQuery);
                                } else {
                                    Bundle args = getPageActivityBundle(currentActivity,
                                            this.appCMSPageUI,
                                            pageAPI,
                                            this.pageId,
                                            this.pageTitle,
                                            pageIdToPageNameMap.get(this.pageId),
                                            loadFromFile,
                                            this.appbarPresent,
                                            this.fullscreenEnabled,
                                            this.navbarPresent,
                                            false,
                                            null);
                                    Intent watchlistPageIntent =
                                            new Intent(AppCMSPresenter
                                                    .PRESENTER_NAVIGATE_ACTION);
                                    watchlistPageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key), args);
                                    currentActivity.sendBroadcast(watchlistPageIntent);
                                }
                            } else {
                                sendStopLoadingPageAction();
                                setNavItemToCurrentAction(currentActivity);
                                loadingPage = false;
                            }
                        }
                    });
        }
    }

    private void getWatchlistPageContent(final String apiBaseUrl, String endPoint,
                                         final String siteId,
                                         boolean userPageIdQueryParam, String pageId,
                                         final AppCMSWatchlistAPIAction watchlist) {
        String url = currentActivity.getString(R.string.app_cms_refresh_identity_api_url,
                appCMSMain.getApiBaseUrl(),
                getRefreshToken(currentActivity));

        appCMSRefreshIdentityCall.call(url, new Action1<RefreshIdentityResponse>() {
            @Override
            public void call(RefreshIdentityResponse refreshIdentityResponse) {
                try {
                    appCMSWatchlistCall.call(
                            currentActivity.getString(R.string.app_cms_watchlist_api_url,
                                    apiBaseUrl, //getLoggedInUser(currentActivity,
                                    siteId,
                                    getLoggedInUser(currentActivity)),
                            getAuthToken(currentActivity),
                            new Action1<AppCMSWatchlistResult>() {
                                @Override
                                public void call(AppCMSWatchlistResult appCMSWatchlistResult) {
                                    watchlist.call(appCMSWatchlistResult);
                                }
                            });
                } catch (IOException e) {
                    Log.e(TAG, "getWatchlistPageContent: " + e.toString());
                }
            }
        });
    }

    public void getHistoryData(final Action1<AppCMSHistoryResult> appCMSHistoryResultAction) {
        if (currentActivity != null) {
            MetaPage historyMetaPage = actionTypeToMetaPageMap.get(AppCMSActionType.HISTORY_PAGE);
            AppCMSPageUI appCMSPageUI = navigationPages.get(historyMetaPage.getPageId());
            getHistoryPageContent(appCMSMain.getApiBaseUrl(),
                    historyMetaPage.getPageAPI(),
                    appCMSMain.getSite(),
                    true,
                    getPageId(appCMSPageUI),
                    new AppCMSHistoryAPIAction(true,
                            false,
                            true,
                            appCMSPageUI,
                            historyMetaPage.getPageId(),
                            historyMetaPage.getPageId(),
                            historyMetaPage.getPageName(),
                            false,
                            null) {
                        @Override
                        public void call(AppCMSHistoryResult appCMSHistoryResult) {
                            Observable.just(appCMSHistoryResult).subscribe(appCMSHistoryResultAction);
                        }
                    });
        }
    }

    public void navigateToHistoryPage(String pageId, String pageTitle, String url,
                                      boolean launchActivity) {

        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {
            AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);
            final AppCMSPageAPI appCMSPageAPI = navigationPageData.get(pageId);

            getHistoryPageContent(appCMSMain.getApiBaseUrl(),
                    pageIdToPageAPIUrlMap.get(pageId),
                    appCMSMain.getSite(),
                    true,
                    getPageId(appCMSPageUI), new AppCMSHistoryAPIAction(true,
                            false,
                            true,
                            appCMSPageUI,
                            pageId,
                            pageId,
                            pageTitle,
                            launchActivity, null) {
                        @Override
                        public void call(AppCMSHistoryResult appCMSHistoryResult) {
                            if (appCMSHistoryResult != null) {
                                cancelInternalEvents();
                                pushActionInternalEvents(this.pageId
                                        + BaseView.isLandscape(currentActivity));

                                AppCMSPageAPI pageAPI =
                                        appCMSHistoryResult.convertToAppCMSPageAPI(this.pageId);

                                navigationPageData.put(this.pageId, pageAPI);

                                if (this.launchActivity) {
                                    launchPageActivity(currentActivity,
                                            this.appCMSPageUI,
                                            pageAPI,
                                            this.pageId,
                                            this.pageTitle,
                                            pageIdToPageNameMap.get(this.pageId),
                                            loadFromFile,
                                            this.appbarPresent,
                                            this.fullscreenEnabled,
                                            this.navbarPresent,
                                            false,
                                            this.searchQuery);
                                } else {
                                    Bundle args = getPageActivityBundle(currentActivity,
                                            this.appCMSPageUI,
                                            pageAPI,
                                            this.pageId,
                                            this.pageTitle,
                                            pageIdToPageNameMap.get(this.pageId),
                                            loadFromFile,
                                            this.appbarPresent,
                                            this.fullscreenEnabled,
                                            this.navbarPresent,
                                            false,
                                            null);

                                    Intent historyPageIntent =
                                            new Intent(AppCMSPresenter
                                                    .PRESENTER_NAVIGATE_ACTION);
                                    historyPageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key), args);
                                    currentActivity.sendBroadcast(historyPageIntent);
                                }
                            } else {
                                sendStopLoadingPageAction();
                                setNavItemToCurrentAction(currentActivity);
                                loadingPage = false;
                            }
                        }
                    });
        }
    }

    private void getHistoryPageContent(final String apiBaseUrl, String endPoint, final String siteiD,
                                       boolean userPageIdQueryParam, String pageId,
                                       final AppCMSHistoryAPIAction history) {

        String url = currentActivity.getString(R.string.app_cms_refresh_identity_api_url,
                appCMSMain.getApiBaseUrl(),
                getRefreshToken(currentActivity));

        appCMSRefreshIdentityCall.call(url, new Action1<RefreshIdentityResponse>() {
            @Override
            public void call(RefreshIdentityResponse refreshIdentityResponse) {
                try {
                    appCMSHistoryCall.call(currentActivity.getString(R.string.app_cms_history_api_url,
                            apiBaseUrl, getLoggedInUser(currentActivity), siteiD,
                            getLoggedInUser(currentActivity)),
                            getAuthToken(currentActivity),
                            new Action1<AppCMSHistoryResult>() {
                                @Override
                                public void call(AppCMSHistoryResult appCMSHistoryResult) {
                                    history.call(appCMSHistoryResult);
                                }
                            });
                } catch (IOException e) {
                    Log.e(TAG, "getHistoryPageContent: " + e.toString());
                }
            }
        });
    }

    public void navigateToLoginPage() {
        if (loginPage != null) {
            boolean launchSuccess = navigateToPage(loginPage.getPageId(),
                    loginPage.getPageName(),
                    loginPage.getPageUI(),
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

    public void navigateToSettingsPage() {
        AppCMSSettingsFragment appCMSSettingsFragment = AppCMSSettingsFragment.newInstance(currentActivity);
        appCMSSettingsFragment.show(((AppCompatActivity) currentActivity).getSupportFragmentManager(),
                currentActivity.getString(R.string.app_cms_settings_page_tag));
    }

    public void resetPassword(final String email) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_forgot_password_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSMain.getSite());
            appCMSResetPasswordCall.call(url,
                    email,
                    new Action1<ForgotPasswordResponse>() {
                        @Override
                        public void call(ForgotPasswordResponse forgotPasswordResponse) {
                            if (forgotPasswordResponse != null && TextUtils.isEmpty(forgotPasswordResponse.getError())) {
                                Log.d(TAG, "Successfully reset password for email: " + email);
                            } else if (forgotPasswordResponse != null) {
                                Log.e(TAG, "Failed to reset password for email: " + email);
                            }
                        }
                    });
        }
    }

    public void getUserData(final Action1<UserIdentity> userIdentityAction) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_user_identity_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSMain.getSite());
            appCMSUserIdentityCall.callGet(url,
                    getAuthToken(currentActivity),
                    new Action1<UserIdentity>() {
                        @Override
                        public void call(UserIdentity userIdentity) {
                            Observable.just(userIdentity).subscribe(userIdentityAction);
                        }
                    });
        }
    }

    public void updateUserData(String username,
                               String email,
                               final Action1<UserIdentity> userIdentityAction) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_user_identity_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSMain.getSite());
            UserIdentity userIdentity = new UserIdentity();
            userIdentity.setName(username);
            userIdentity.setEmail(email);
            appCMSUserIdentityCall.callPost(url,
                    getAuthToken(currentActivity),
                    userIdentity,
                    new Action1<UserIdentity>() {
                        @Override
                        public void call(UserIdentity userIdentity) {
                            Observable.just(userIdentity).subscribe(userIdentityAction);
                        }
                    });
        }
    }

    private void closeSoftKeyboard() {
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
                    appCMSMain.getSite(),
                    true,
                    getPageId(appCMSPageUI),
                    new AppCMSPageAPIAction(true,
                            false,
                            navbarPresent,
                            appCMSPageUI,
                            pageId,
                            pageId,
                            pageTitle,
                            launchActivity,
                            sendCloseAction,
                            searchQuery) {
                        @Override
                        public void call(final AppCMSPageAPI appCMSPageAPI) {
                            if (appCMSPageAPI != null) {
                                cancelInternalEvents();
                                pushActionInternalEvents(this.pageId + BaseView.isLandscape(currentActivity));
                                navigationPageData.put(this.pageId, appCMSPageAPI);
                                if (isUserLoggedIn(currentActivity)) {
                                    final AppCMSPageAPIAction appCMSPageAPIAction = this;
                                    getHistoryData(new Action1<AppCMSHistoryResult>() {
                                        @Override
                                        public void call(AppCMSHistoryResult appCMSHistoryResult) {
                                            for (Module module : appCMSPageAPI.getModules()) {
                                                if (jsonValueKeyMap.get(module.getModuleType()) ==
                                                        AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY) {
                                                    AppCMSPageAPI pageAPI =
                                                            appCMSHistoryResult.convertToAppCMSPageAPI(module.getId());
                                                    module.setContentData(pageAPI.getModules().get(0).getContentData());
                                                }
                                            }
                                            if (appCMSPageAPIAction.launchActivity) {
                                                launchPageActivity(currentActivity,
                                                        appCMSPageAPIAction.appCMSPageUI,
                                                        appCMSPageAPI,
                                                        appCMSPageAPIAction.pageId,
                                                        appCMSPageAPIAction.pageTitle,
                                                        pageIdToPageNameMap.get(appCMSPageAPIAction.pageId),
                                                        loadFromFile,
                                                        appCMSPageAPIAction.appbarPresent,
                                                        appCMSPageAPIAction.fullscreenEnabled,
                                                        appCMSPageAPIAction.navbarPresent,
                                                        appCMSPageAPIAction.sendCloseAction,
                                                        appCMSPageAPIAction.searchQuery);
                                            } else {
                                                Bundle args = getPageActivityBundle(currentActivity,
                                                        appCMSPageAPIAction.appCMSPageUI,
                                                        appCMSPageAPI,
                                                        appCMSPageAPIAction.pageId,
                                                        appCMSPageAPIAction.pageTitle,
                                                        pageIdToPageNameMap.get(appCMSPageAPIAction.pageId),
                                                        loadFromFile,
                                                        appCMSPageAPIAction.appbarPresent,
                                                        appCMSPageAPIAction.fullscreenEnabled,
                                                        appCMSPageAPIAction.navbarPresent,
                                                        false,
                                                        appCMSPageAPIAction.searchQuery);
                                                Intent updatePageIntent =
                                                        new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                                updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                                        args);
                                                currentActivity.sendBroadcast(updatePageIntent);
                                            }
                                        }
                                    });
                                } else {
                                    if (this.launchActivity) {
                                        launchPageActivity(currentActivity,
                                                this.appCMSPageUI,
                                                appCMSPageAPI,
                                                this.pageId,
                                                this.pageTitle,
                                                pageIdToPageNameMap.get(this.pageId),
                                                loadFromFile,
                                                this.appbarPresent,
                                                this.fullscreenEnabled,
                                                this.navbarPresent,
                                                this.sendCloseAction,
                                                this.searchQuery);
                                    } else {
                                        Bundle args = getPageActivityBundle(currentActivity,
                                                this.appCMSPageUI,
                                                appCMSPageAPI,
                                                this.pageId,
                                                this.pageTitle,
                                                pageIdToPageNameMap.get(this.pageId),
                                                loadFromFile,
                                                this.appbarPresent,
                                                this.fullscreenEnabled,
                                                this.navbarPresent,
                                                false,
                                                this.searchQuery);
                                        Intent updatePageIntent =
                                                new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                                        updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
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

    public boolean sendCloseOthersAction(String pageName, boolean closeSelf) {
        Log.d(TAG, "Sending close others action");
        boolean result = false;
        if (currentActivity != null) {
            if (currentActions.size() > 0) {
                currentActions.pop();
            }
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
        Intent stopLoadingPageIntent =
                new Intent(AppCMSPresenter.PRESENTER_STOP_PAGE_LOADING_ACTION);
        currentActivity.sendBroadcast(stopLoadingPageIntent);
        showErrorDialog(Error.NETWORK, null);
    }

    public void launchErrorActivity(Activity activity, PlatformType platformType) {
        if (platformType == PlatformType.ANDROID) {
            try {
                sendCloseOthersAction(null, true);
                Intent errorIntent = new Intent(activity, AppCMSErrorActivity.class);
                activity.startActivity(errorIntent);
            } catch (Exception e) {
                Log.e(TAG, "Error launching Mobile Error Activity");
            }
        } else if (platformType == PlatformType.TV) {
            try {
                Intent errorIntent = new Intent(activity, Class.forName(tvErrorScreenPackage));
                activity.startActivity(errorIntent);
            } catch (Exception e) {
                Log.e(TAG, "Error launching TV Error Activity");
            }
        }
    }

    public void getPageIdContent(String baseUrl,
                                 String endPoint,
                                 String siteId,
                                 boolean usePageIdQueryParam,
                                 String pageId,
                                 Action1<AppCMSPageAPI> readyAction) {
        GetAppCMSAPIAsyncTask.Params params = new GetAppCMSAPIAsyncTask.Params.Builder()
                .context(currentActivity)
                .baseUrl(baseUrl)
                .endpoint(endPoint)
                .siteId(siteId)
                .usePageIdQueryParam(usePageIdQueryParam)
                .pageId(pageId)
                .build();
        new GetAppCMSAPIAsyncTask(appCMSPageAPICall, readyAction).execute(params);
    }

    public boolean isUserLoggedIn(Context context) {
        return getLoggedInUser(context) != null;
    }

    public String getLoggedInUser(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
        return sharedPrefs.getString(USER_ID_SHARED_PREF_NAME, null);
    }

    public boolean setLoggedInUser(Context context, String userId) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
        return sharedPrefs.edit().putString(USER_ID_SHARED_PREF_NAME, userId).commit();
    }

    public String getRefreshToken(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(REFRESH_TOKEN_SHARED_PREF_NAME, 0);
        return sharedPrefs.getString(REFRESH_TOKEN_SHARED_PREF_NAME, null);
    }

    public boolean setRefreshToken(Context context, String refreshToken) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(REFRESH_TOKEN_SHARED_PREF_NAME, 0);
        return sharedPrefs.edit().putString(REFRESH_TOKEN_SHARED_PREF_NAME, refreshToken).commit();
    }

    public String getAuthToken(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(AUTH_TOKEN_SHARED_PREF_NAME, 0);
        return sharedPrefs.getString(AUTH_TOKEN_SHARED_PREF_NAME, null);
    }

    public boolean setAuthToken(Context context, String authToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AUTH_TOKEN_SHARED_PREF_NAME, 0);
        return sharedPreferences.edit().putString(AUTH_TOKEN_SHARED_PREF_NAME, authToken).commit();
    }

    public void logout() {
        if (currentActivity != null) {
            SharedPreferences sharedPrefs = currentActivity.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
            sharedPrefs.edit().putString(USER_ID_SHARED_PREF_NAME, null).apply();
            Primary homePageNavItem = findHomePageNavItem();
            if (homePageNavItem != null) {
                navigateToPage(homePageNavItem.getPageId(),
                        homePageNavItem.getTitle(),
                        homePageNavItem.getUrl(),
                        false,
                        true,
                        false,
                        deeplinkSearchQuery);
            }
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

    public Primary findHomePageNavItem() {
        for (Primary primary : navigation.getPrimary()) {
            AppCMSUIKeyType navTitle = jsonValueKeyMap.get(primary.getTitle());
            if (navTitle == AppCMSUIKeyType.ANDROID_HOME_NAV_KEY) {
                return primary;
            }
        }
        return null;
    }

    public Primary findMoviesPageNavItem() {
        for (Primary primary : navigation.getPrimary()) {
            AppCMSUIKeyType navTitle = jsonValueKeyMap.get(primary.getTitle());
            if (navTitle == AppCMSUIKeyType.ANDROID_MOVIES_NAV_KEY) {
                return primary;
            }
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
        new GetAppCMSMainUIAsyncTask(appCMSMainUICall, new Action1<AppCMSMain>() {
            @Override
            public void call(AppCMSMain main) {
                if (main == null) {
                    Log.e(TAG, "Error retrieving main.json");
                    launchErrorActivity(activity, platformType);
                } else if (TextUtils.isEmpty(main
                        .getAndroid())) {
                    Log.e(TAG, "AppCMS key for main not found");
                    launchErrorActivity(activity, platformType);
                } else if (TextUtils.isEmpty(main
                        .getApiBaseUrl())) {
                    Log.e(TAG, "AppCMS key for API Base URL not found");
                    launchErrorActivity(activity, platformType);
                } else if (TextUtils.isEmpty(main.getSite())) {
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
                                    main.getSite(),
                                    appCMSSearchCall))
                            .build();
                    getAppCMSSite(activity, main, platformType);
                }
            }
        }).execute(params);
    }

    public AppCMSMain getAppCMSMain() {
        return appCMSMain;
    }

    public boolean isActionAPage(String action) {
        for (Primary primary : navigation.getPrimary()) {
            if (!TextUtils.isEmpty(primary.getPageId()) && action.contains(primary.getPageId())) {
                return true;
            }
        }

        for (User user : navigation.getUser()) {
            if (!TextUtils.isEmpty(user.getPageId()) &&
                    action.contains(user.getPageId())) {
                return true;
            }
        }

        return false;
    }

    public AppCMSSearchUrlComponent getAppCMSSearchUrlComponent() {
        return appCMSSearchUrlComponent;
    }

    public void showMoreDialog(String title, String fullText, int textColor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setTitle(Html.fromHtml(currentActivity.getString(R.string.text_with_color,
                Integer.toHexString(textColor).substring(2),
                title)))
                .setMessage(Html.fromHtml(currentActivity.getString(R.string.text_with_color,
                        Integer.toHexString(textColor).substring(2),
                        fullText)));
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor(
                    appCMSMain.getBrand().getGeneral().getBackgroundColor())));
        }
        dialog.show();
    }

    public void showErrorDialog(Error error, String optionalMessage) {
        if (currentActivity != null) {
            int textColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor());
            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
            String title;
            String message;
            switch (error) {
                case SIGNIN:
                    title = currentActivity.getString(R.string.app_cms_signin_error_title);
                    message = optionalMessage;
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
                            message)))
                    .setNegativeButton(R.string.app_cms_close_alert_dialog_button_text,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
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
                        Log.e(TAG, "An exception has occurred when attempting to show the error dialog: "
                                + e.toString());
                    }
                }
            }
        }
    }

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
        if (currentActions.size() > 0 && !isActionAPage(currentActions.peek())) {
            Log.d(TAG, "Stack size - pushing internal events (popping extra): "
                    + currentActions.size());
            popActionInternalEvents();
        }
        if (onActionInternalEvents.get(action) == null) {
            onActionInternalEvents.put(action, new ArrayList<OnInternalEvent>());
        }
        currentActions.push(action);
    }

    public void sendBeaconAdImpression(String vid, String screenName, String parentScreenName,
                                       long currentPosition) {
        Log.d(TAG, "Sending Beacon Ad Impression");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition,
                BeaconEvent.AD_IMPRESSION);
        if (url != null) {
            Log.d(TAG, "Beacon Ad Impression: " + url);
            beaconMessageRunnable.setUrl(url);
            beaconMessageThread.run();
        }
    }

    public void sendBeaconAdRequestMessage(String vid, String screenName, String parentScreenName,
                                           long currentPosition) {
        Log.d(TAG, "Sending Beacon Ad Message");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition,
                BeaconEvent.AD_REQUEST);
        if (url != null) {
            Log.d(TAG, "Beacon Ad Request: " + url);
            beaconMessageRunnable.setUrl(url);
            beaconMessageThread.run();
        }
    }

    public void sendBeaconPingMessage(String vid, String screenName, String parentScreenName,
                                      long currentPosition) {
        Log.d(TAG, "Sending Beacon Ping Message");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition, BeaconEvent.PING);
        if (url != null) {
            Log.d(TAG, "Beacon Ping: " + url);
            beaconMessageRunnable.setUrl(url);
            beaconMessageThread.run();
        }
    }

    public void sendBeaconPlayMessage(String vid, String screenName, String parentScreenName,
                                      long currentPosition) {
        Log.d(TAG, "Sending Beacon Ad Message");
        String url = getBeaconUrl(vid, screenName, parentScreenName, currentPosition, BeaconEvent.PLAY);
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
                                long currentPosition, BeaconEvent event) {
        String url = null;
        if (currentActivity != null) {
            final String utfEncoding = currentActivity.getString(R.string.utf8enc);
            String uid = InstanceID.getInstance(currentActivity).getId();
            int currentPositionSecs = (int) (currentPosition / MILLISECONDS_PER_SECOND);
            if (isUserLoggedIn(currentActivity)) {
                uid = getLoggedInUser(currentActivity);
            }
            try {
                url = currentActivity.getString(R.string.app_cms_beacon_url,
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
                        URLEncoder.encode(uid, utfEncoding));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "Error encoding Beacon URL parameters: " + e.toString());
            }
        }
        return url;
    }

    public void sendGaScreen(String screenName) {
        if (tracker != null) {
            Log.d(TAG, "Sending GA screen tracking event: " + screenName);
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public void signup(String email, String password) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_signup_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSMain.getSite());
            startLoginAsyncTask(url, email, password);
        }
    }

    public void login(String email, String password) {
        if (currentActivity != null) {
            String url = currentActivity.getString(R.string.app_cms_signin_api_url,
                    appCMSMain.getApiBaseUrl(),
                    appCMSMain.getSite());
            startLoginAsyncTask(url, email, password);
        }
    }

    public void callRefreshIdentity() {
        if (currentActivity != null) {
            refreshIdentity(getRefreshToken(currentActivity));
        }
    }

    private void startLoginAsyncTask(String url, String email, String password) {
        PostAppCMSLoginRequestAsyncTask.Params params = new PostAppCMSLoginRequestAsyncTask.Params
                .Builder()
                .url(url)
                .email(email)
                .password(password)
                .build();
        new PostAppCMSLoginRequestAsyncTask(appCMSSignInCall,
                new Action1<SignInResponse>() {
                    @Override
                    public void call(SignInResponse signInResponse) {
                        if (signInResponse == null) {
                            // Show log error
                            Log.e(TAG, "Email and password are not valid.");
                            showErrorDialog(Error.SIGNIN, currentActivity.getString(
                                    R.string.app_cms_error_email_password));
                        } else {
                            refreshToken = signInResponse.getRefreshToken();
                            setAuthToken(currentActivity, signInResponse.getAuthorizationToken());
                            refreshIdentity(refreshToken);
                        }
                    }
                }).execute(params);
    }

    public void refreshIdentity(final String refreshToken) {
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
                    new Action1<RefreshIdentityResponse>() {
                        @Override
                        public void call(RefreshIdentityResponse refreshIdentityResponse) {
                            if (refreshIdentityResponse != null) {
                                setLoggedInUser(currentActivity, refreshIdentityResponse.getId());
                                setRefreshToken(currentActivity, refreshIdentityResponse.getRefreshToken());
                                Primary homePageNavItem = findHomePageNavItem();
                                if (homePageNavItem != null) {
                                    navigateToPage(homePageNavItem.getPageId(),
                                            homePageNavItem.getTitle(),
                                            homePageNavItem.getUrl(),
                                            false,
                                            true,
                                            false,
                                            deeplinkSearchQuery);
                                }
                            }
                        }
                    }).execute(params);
        }
    }

    public void setNavItemToCurrentAction(Activity activity) {
        if (currentActions.size() > 0) {
            Intent setNavigationItemIntent = new Intent(PRESENTER_RESET_NAVIGATION_ITEM_ACTION);
            setNavigationItemIntent.putExtra(activity.getString(R.string.navigation_item_key),
                    currentActions.peek());
            activity.sendBroadcast(setNavigationItemIntent);
        }
    }

    private Bundle getPageActivityBundle(Activity activity,
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
                                         Uri searchQuery) {
        Bundle args = new Bundle();
        AppCMSBinder appCMSBinder = getAppCMSBinder(activity,
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
                searchQuery);
        args.putBinder(activity.getString(R.string.app_cms_binder_key), appCMSBinder);
        return args;
    }

    private AppCMSBinder getAppCMSBinder(Activity activity,
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
                                         Uri searchQuery) {
        return new AppCMSBinder(appCMSMain,
                appCMSPageUI,
                appCMSPageAPI,
                navigation,
                pageID,
                pageName,
                screenName,
                loadFromFile,
                appbarPresent,
                fullscreenEnabled,
                navbarPresent,
                sendCloseAction,
                isUserLoggedIn(activity),
                jsonValueKeyMap,
                searchQuery);
    }

    private void launchPageActivity(Activity activity,
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
                                    Uri searchQuery) {
        Bundle args = getPageActivityBundle(activity,
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
                searchQuery);
        Intent appCMSIntent = new Intent(activity, AppCMSPageActivity.class);
        appCMSIntent.putExtra(activity.getString(R.string.app_cms_bundle_key), args);
        appCMSIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(appCMSIntent);
    }

    private void getAppCMSSite(final Activity activity,
                               final AppCMSMain main,
                               final PlatformType platformType) {
        String url = currentActivity.getString(R.string.app_cms_site_api_url,
                main.getApiBaseUrl(),
                main.getDomainName());
        new GetAppCMSSiteAsyncTask(appCMSSiteCall,
                new Action1<AppCMSSite>() {
                    @Override
                    public void call(AppCMSSite appCMSSite) {
                        if (appCMSSite != null) {
                            AppCMSAPIComponent appCMSAPIComponent = DaggerAppCMSAPIComponent.builder()
                                    .appCMSAPIModule(new AppCMSAPIModule(activity,
                                            main.getApiBaseUrl(),
                                            appCMSSite.getGist().getAppAccess().getAppSecretKey()))
                                    .build();
                            appCMSPageAPICall = appCMSAPIComponent.appCMSPageAPICall();
                            appCMSStreamingInfoCall = appCMSAPIComponent.appCMSStreamingInfoCall();
                            clearMaps();
                            switch (platformType) {
                                case ANDROID:
                                    getAppCMSAndroid(activity, main);
//                                    getAppCMSAndroid(activity, main);
                                    break;
                                case TV:
                                    getAppCMSTV(activity , main , null);
                                    break;
                                default:
                            }
                        }
                    }
                }).execute(url);
    }

    private void getAppCMSAndroid(final Activity activity, final AppCMSMain main) {
        GetAppCMSAndroidUIAsyncTask.Params params =
                new GetAppCMSAndroidUIAsyncTask.Params.Builder()
                        .url(activity.getString(R.string.app_cms_url_with_appended_timestamp,
                                main.getAndroid(),
                                main.getTimestamp()))
                        .loadFromFile(loadFromFile)
                        .build();
        Log.d(TAG, "Params: " + main.getAndroid() + " " + loadFromFile);
        new GetAppCMSAndroidUIAsyncTask(appCMSAndroidUICall, new Action1<AppCMSAndroidUI>() {
            @Override
            public void call(AppCMSAndroidUI appCMSAndroidUI) {
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
                                        boolean navbarPresent =
                                                (jsonValueKeyMap.get(firstPage.getPageName())
                                                        != AppCMSUIKeyType.ANDROID_AUTH_SCREEN_KEY);
                                        boolean launchSuccess = navigateToPage(firstPage.getPageId(),
                                                firstPage.getPageName(),
                                                firstPage.getPageUI(),
                                                true,
                                                navbarPresent,
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
            }
        }).execute(params);
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
            int pageToQueueIndex = getHomePage(metaPageList);
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
                new Action1<AppCMSPageUI>() {
                    @Override
                    public void call(AppCMSPageUI appCMSPageUI) {
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
                    }
                },
                loadFromFile);
    }

    private int getSigninPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName()) ==
                    AppCMSUIKeyType.ANDROID_AUTH_SCREEN_KEY) {
                return i;
            }
        }
        return -1;
    }

    private int getHomePage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (jsonValueKeyMap.get(metaPageList.get(i).getPageName()) ==
                    AppCMSUIKeyType.ANDROID_HOME_SCREEN_KEY) {
                return i;
            }
        }
        return -1;
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
        new GetAppCMSAndroidUIAsyncTask(appCMSAndroidUICall, new Action1<AppCMSAndroidUI>() {
            @Override
            public void call(final AppCMSAndroidUI appCMSAndroidUI) {
                if (appCMSAndroidUI == null ||
                        appCMSAndroidUI.getMetaPages() == null ||
                        appCMSAndroidUI.getMetaPages().size() < 1) {
                    Log.e(TAG, "AppCMS keys for pages for appCMSAndroidUI not found");
                    launchErrorActivity(activity, PlatformType.TV);
                } else {
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
                                    Log.d(TAG, "Launching first page: " + firstPage.getPageName());
                                    Primary homePageNav = findHomePageNavItem();
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
                        appCMSMain.getSite(),
                        true,
                        getPageId(appCMSPageUI),
                        new AppCMSPageAPIAction(true,
                                false,
                                true,
                                appCMSPageUI,
                                pageId,
                                pageId,
                                pageTitle,
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
                                                pageIdToPageNameMap.get(this.pageId),
                                                loadFromFile,
                                                this.appbarPresent,
                                                this.fullscreenEnabled,
                                                this.navbarPresent,
                                                false,
                                                this.searchQuery);
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
                pushActionInternalEvents(pageId + BaseView.isLandscape(currentActivity));
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
                            pageIdToPageNameMap.get(pageId),
                            loadFromFile,
                            true,
                            false,
                            true,
                            false,
                            searchQuery);
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
                screenName,
                loadFromFile,
                appbarPresent,
                fullscreenEnabled,
                navbarPresent,
                false,
                searchQuery);

        try {
            Intent appCMSIntent = new Intent(activity, Class.forName(tvHomeScreenPackage));
            appCMSIntent.putExtra(activity.getString(R.string.app_cms_bundle_key), args);
            appCMSIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(appCMSIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            sendStopLoadingPageAction();
        }
    }
}
