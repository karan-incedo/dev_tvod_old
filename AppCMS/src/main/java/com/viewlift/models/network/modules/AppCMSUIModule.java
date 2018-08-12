package com.viewlift.models.network.modules;

/**
 * Created by viewlift on 5/4/17.
 */

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viewlift.R;
import com.viewlift.Utils;
import com.viewlift.analytics.CleverTapSDK;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.network.rest.AppCMSAddToWatchlistRest;
import com.viewlift.models.network.rest.AppCMSAndroidModuleCall;
import com.viewlift.models.network.rest.AppCMSAndroidModuleRest;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import com.viewlift.models.network.rest.AppCMSAndroidUIRest;
import com.viewlift.models.network.rest.AppCMSAnonymousAuthTokenCall;
import com.viewlift.models.network.rest.AppCMSAnonymousAuthTokenRest;
import com.viewlift.models.network.rest.AppCMSArticleCall;
import com.viewlift.models.network.rest.AppCMSArticleRest;
import com.viewlift.models.network.rest.AppCMSAudioDetailCall;
import com.viewlift.models.network.rest.AppCMSAudioDetailRest;
import com.viewlift.models.network.rest.AppCMSBeaconRest;
import com.viewlift.models.network.rest.AppCMSCCAvenueCall;
import com.viewlift.models.network.rest.AppCMSCCAvenueRSAKeyCall;
import com.viewlift.models.network.rest.AppCMSCCAvenueRSAKeyRest;
import com.viewlift.models.network.rest.AppCMSCCAvenueRest;
import com.viewlift.models.network.rest.AppCMSDeleteHistoryRest;
import com.viewlift.models.network.rest.AppCMSEventArchieveCall;
import com.viewlift.models.network.rest.AppCMSEventArchieveRest;
import com.viewlift.models.network.rest.AppCMSFacebookLoginCall;
import com.viewlift.models.network.rest.AppCMSFacebookLoginRest;
import com.viewlift.models.network.rest.AppCMSGoogleLoginCall;
import com.viewlift.models.network.rest.AppCMSGoogleLoginRest;
import com.viewlift.models.network.rest.AppCMSHistoryCall;
import com.viewlift.models.network.rest.AppCMSHistoryRest;
import com.viewlift.models.network.rest.AppCMSIPGeoLocatorCall;
import com.viewlift.models.network.rest.AppCMSIPGeoLocatorRest;
import com.viewlift.models.network.rest.AppCMSLibraryRest;
import com.viewlift.models.network.rest.AppCMSMainUICall;
import com.viewlift.models.network.rest.AppCMSMainUIRest;
import com.viewlift.models.network.rest.AppCMSPageUICall;
import com.viewlift.models.network.rest.AppCMSPageUIRest;
import com.viewlift.models.network.rest.AppCMSPhotoGalleryCall;
import com.viewlift.models.network.rest.AppCMSPhotoGalleryRest;
import com.viewlift.models.network.rest.AppCMSPlaylistCall;
import com.viewlift.models.network.rest.AppCMSPlaylistRest;
import com.viewlift.models.network.rest.AppCMSRefreshIdentityCall;
import com.viewlift.models.network.rest.AppCMSRefreshIdentityRest;
import com.viewlift.models.network.rest.AppCMSResetPasswordCall;
import com.viewlift.models.network.rest.AppCMSResetPasswordRest;
import com.viewlift.models.network.rest.AppCMSRestorePurchaseCall;
import com.viewlift.models.network.rest.AppCMSRestorePurchaseRest;
import com.viewlift.models.network.rest.AppCMSRosterRest;
import com.viewlift.models.network.rest.AppCMSSSLCommerzInitiateCall;
import com.viewlift.models.network.rest.AppCMSSSLCommerzInitiateRest;
import com.viewlift.models.network.rest.AppCMSScheduleRest;
import com.viewlift.models.network.rest.AppCMSSignInCall;
import com.viewlift.models.network.rest.AppCMSSignInRest;
import com.viewlift.models.network.rest.AppCMSSignedURLCall;
import com.viewlift.models.network.rest.AppCMSSignedURLRest;
import com.viewlift.models.network.rest.AppCMSSubscribeForLatestNewsCall;
import com.viewlift.models.network.rest.AppCMSSubscribeForLatestNewsRest;
import com.viewlift.models.network.rest.AppCMSSubscriptionPlanCall;
import com.viewlift.models.network.rest.AppCMSSubscriptionPlanRest;
import com.viewlift.models.network.rest.AppCMSSubscriptionRest;
import com.viewlift.models.network.rest.AppCMSTeamRoasterCall;
import com.viewlift.models.network.rest.AppCMSTeamRoasterRest;
import com.viewlift.models.network.rest.AppCMSTeamStandingCall;
import com.viewlift.models.network.rest.AppCMSTeamStandingRest;
import com.viewlift.models.network.rest.AppCMSUpdateWatchHistoryCall;
import com.viewlift.models.network.rest.AppCMSUpdateWatchHistoryRest;
import com.viewlift.models.network.rest.AppCMSUserDownloadVideoStatusCall;
import com.viewlift.models.network.rest.AppCMSUserIdentityCall;
import com.viewlift.models.network.rest.AppCMSUserIdentityRest;
import com.viewlift.models.network.rest.AppCMSUserVideoStatusCall;
import com.viewlift.models.network.rest.AppCMSUserVideoStatusRest;
import com.viewlift.models.network.rest.AppCMSWatchlistCall;
import com.viewlift.models.network.rest.AppCMSWatchlistRest;
import com.viewlift.models.network.rest.GoogleCancelSubscriptionCall;
import com.viewlift.models.network.rest.GoogleCancelSubscriptionRest;
import com.viewlift.models.network.rest.GoogleRefreshTokenCall;
import com.viewlift.models.network.rest.GoogleRefreshTokenRest;
import com.viewlift.presenters.AppCMSActionType;
import com.viewlift.stag.generated.Stag;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppCMSUIModule {
    private final String baseUrl;
    private final File storageDirectory;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final Map<String, String> pageNameToActionMap;
    private final Map<String, AppCMSPageUI> actionToPageMap;
    private final Map<String, AppCMSPageAPI> actionToPageAPIMap;
    private final Map<String, AppCMSActionType> actionToActionTypeMap;
    private final long defaultConnectionTimeout;
    private final long defaultWriteConnectionTimeout;
    private final long defaultReadConnectionTimeout;
    private final long unknownHostExceptionTimeout;
    private final Cache cache;
    private final AssetManager assetManager;
    private final Context context;

    public AppCMSUIModule(Context context) {
        this.context = context;
        this.baseUrl = Utils.getProperty("BaseUrl", context);

        // NOTE: Replaced with Utils.getProperty()
        //this.baseUrl = context.getString(R.string.app_cms_baseurl);

        this.storageDirectory = context.getFilesDir();

        this.jsonValueKeyMap = new HashMap<>();
        createJsonValueKeyMap(context);

        this.pageNameToActionMap = new HashMap<>();
        createPageNameToActionMap(context);

        this.actionToPageMap = new HashMap<>();
        createActionToPageMap(context);

        this.actionToActionTypeMap = new HashMap<>();
        createActionToActionTypeMap(context);

        this.actionToPageAPIMap = new HashMap<>();
        createActionToPageAPIMap(context);

        this.defaultConnectionTimeout =
                context.getResources().getInteger(R.integer.app_cms_default_connection_timeout_msec);

        this.defaultWriteConnectionTimeout =
                context.getResources().getInteger(R.integer.app_cms_default_write_timeout_msec);

        this.defaultReadConnectionTimeout =
                context.getResources().getInteger(R.integer.app_cms_default_read_timeout_msec);

        this.unknownHostExceptionTimeout =
                context.getResources().getInteger(R.integer.app_cms_unknown_host_exception_connection_timeout_msec);
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        cache = new Cache(context.getCacheDir(), cacheSize);

        this.assetManager = context.getAssets();
    }

    @Provides
    @Singleton
    public CleverTapSDK providesCleverTapSDK() {
        return new CleverTapSDK(context);
    }

    private void createJsonValueKeyMap(Context context) {
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_subscribe_email_go_button_key),
                AppCMSUIKeyType.PAGE_SUBSCRIBE_EMAIL_GO_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_subscribeEditText_key),
                AppCMSUIKeyType.PAGE_SUBSCRIBE_EMAIL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_ratingbar_key),
                AppCMSUIKeyType.PAGE_RATINGBAR);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_type_key),
                AppCMSUIKeyType.PAGE_VIDEO_TYPE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_main_svod_service_type_key),
                AppCMSUIKeyType.MAIN_SVOD_SERVICE_TYPE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_authscreen_key),
                AppCMSUIKeyType.ANDROID_AUTH_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_splashscreen_key),
                AppCMSUIKeyType.ANDROID_SPLASH_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_downloadsettingscreen_key),
                AppCMSUIKeyType.ANDROID_DOWNLOAD_SETTINGS_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_title),
                AppCMSUIKeyType.ANDROID_DOWNLOAD_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_action_playlistpage_key),
                AppCMSUIKeyType.ANDROID_PLAYLIST_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_action_teamdetail_key),
                AppCMSUIKeyType.ANDROID_TEAM_DETAIL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_homescreen_key),
                AppCMSUIKeyType.ANDROID_HOME_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_sign_up_key),
                AppCMSUIKeyType.ANDROID_SIGN_UP_SCREEN_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_create_login_key),
                AppCMSUIKeyType.ANDROID_SIGN_UP_SCREEN_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_showsscreen_key),
                AppCMSUIKeyType.ANDROID_SHOWS_SCREEN_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_privacy_policy_key),
                AppCMSUIKeyType.PRIVACY_POLICY_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_link_your_account_key),
                AppCMSUIKeyType.LINK_ACCOUNT_PAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.sub_nav_page_key),
                AppCMSUIKeyType.SUB_NAV_PAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_detail_app_logo_key),
                AppCMSUIKeyType.PAGE_VIDEO_DETAIL_APP_LOGO_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_terms_of_services_key),
                AppCMSUIKeyType.TERMS_OF_SERVICE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_search_screen_key),
                AppCMSUIKeyType.PAGE_SEARCH_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_moviesscreen_key),
                AppCMSUIKeyType.ANDROID_MOVIES_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_showsscreen_key),
                AppCMSUIKeyType.ANDROID_SHOWS_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_subscriptionscreen_key),
                AppCMSUIKeyType.ANDROID_SUBSCRIPTION_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_historyscreen_key),
                AppCMSUIKeyType.ANDROID_HISTORY_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_historyscreen_key),
                AppCMSUIKeyType.ANDROID_HISTORY_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_watchlist_navigation_title),
                AppCMSUIKeyType.ANDROID_WATCHLIST_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_my_watchlistscreen_key),
                AppCMSUIKeyType.ANDROID_WATCHLIST_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_my_library_screen_key),
                AppCMSUIKeyType.ANDROID_LIBRARY_SCREEN_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_library_navigation_title),
                AppCMSUIKeyType.ANDROID_LIBRARY_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_watchlistscreen_key),
                AppCMSUIKeyType.ANDROID_WATCHLIST_SCREEN_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_articlescreen_key),
                AppCMSUIKeyType.ANDROID_ARTICLE_SCREEN_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_photogalleryscreen_key),
                AppCMSUIKeyType.ANDROID_PHOTOGALLERY_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_schedule_screen_key),
                AppCMSUIKeyType.ANDROID_SCHEDULE_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_person_screen_key),
                AppCMSUIKeyType.ANDROID_PERSON_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_eventscreen_key),
                AppCMSUIKeyType.ANDROID_EVENT_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_rosterscreen_key),
                AppCMSUIKeyType.ANDROID_FIGHTER_ROSTER_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_download_page_title),
                AppCMSUIKeyType.ANDROID_DOWNLOAD_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_history_navigation_title),
                AppCMSUIKeyType.ANDROID_HISTORY_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_settings_page_title_text),
                AppCMSUIKeyType.ANDROID_SETTINGS_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_button_switch_key),
                AppCMSUIKeyType.PAGE_BUTTON_SWITCH_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_check_tc_policy_key),
                AppCMSUIKeyType.PAGE_CHECKBOX_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_button_key),
                AppCMSUIKeyType.PAGE_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_label_key),
                AppCMSUIKeyType.PAGE_LABEL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_venue_label_key),
                AppCMSUIKeyType.PAGE_VENUE_LABEL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_timer_title_label_key),
                AppCMSUIKeyType.PAGE_TIMER_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_upcoming_timer_key),
                AppCMSUIKeyType.PAGE_UPCOMING_TIMER_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_ads_key),
                AppCMSUIKeyType.PAGE_ADS_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_article_title_key),
                AppCMSUIKeyType.PAGE_ARTICLE_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_article_feed_bottom_text_key),
                AppCMSUIKeyType.PAGE_ARTICLE_FEED_BOTTOM_TEXT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_article_description_key),
                AppCMSUIKeyType.PAGE_ARTICLE_DESCRIPTION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_collection_grid_key),
                AppCMSUIKeyType.PAGE_COLLECTIONGRID_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_table_view_key),
                AppCMSUIKeyType.PAGE_TABLE_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_multiple_table_view_key),
                AppCMSUIKeyType.PAGE_MULTI_TABLE_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_progress_view_key),
                AppCMSUIKeyType.PAGE_PROGRESS_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_VIEW_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_list_key),
                AppCMSUIKeyType.PAGE_LIST_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_list_module_key),
                AppCMSUIKeyType.PAGE_LIST_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watch_live_button_key),
                AppCMSUIKeyType.PAGE_WATCH_LIVE_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_player_key),
                AppCMSUIKeyType.PAGE_VIDEO_PLAYER_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_player_key_value),
                AppCMSUIKeyType.PAGE_VIDEO_PLAYER_VIEW_KEY_VALUE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_schedule_key_value),
                AppCMSUIKeyType.PAGE_SCHEDULE_PAGE_KEY_VALUE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_show_player_key),
                AppCMSUIKeyType.PAGE_SHOW_PLAYER_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_web_view_key),
                AppCMSUIKeyType.PAGE_WEB_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_article_web_view_key),
                AppCMSUIKeyType.PAGE_ARTICLE_WEB_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_article_previous_button_key),
                AppCMSUIKeyType.PAGE_ARTICLE_PREVIOUS_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_article_next_button_key),
                AppCMSUIKeyType.PAGE_ARTICLE_NEXT_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_full_screen_key),
                AppCMSUIKeyType.PAGE_FULL_SCREEN_IMAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_image_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_detail_player_key),
                AppCMSUIKeyType.PAGE_VIDEO_DETAIL_PLAYER_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_add_to_watchlist_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_ADD_TO_WATCHLIST_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_add_to_watchlist_key),
                AppCMSUIKeyType.PAGE_ADD_TO_WATCHLIST_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_download_button_key),
                AppCMSUIKeyType.PAGE_VIDEO_DOWNLOAD_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_page_control_key),
                AppCMSUIKeyType.PAGE_PAGE_CONTROL_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_separator_key),
                AppCMSUIKeyType.PAGE_SEPARATOR_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_multicolumn_table_key),
                AppCMSUIKeyType.PAGE_MULTICOLUMN_TABLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_article_bottom_button_background_key),
                AppCMSUIKeyType.PAGE_BOTTOM_BACKGROUND_ARTICLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_segmented_view),
                AppCMSUIKeyType.PAGE_SEGMENTED_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_castview_key),
                AppCMSUIKeyType.PAGE_CASTVIEW_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_image_key),
                AppCMSUIKeyType.PAGE_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_bg_key),
                AppCMSUIKeyType.PAGE_BG_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_logo_key),
                AppCMSUIKeyType.PAGE_LOGO_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_info_key),
                AppCMSUIKeyType.PAGE_INFO_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_play_key),
                AppCMSUIKeyType.PAGE_PLAY_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_action_detailvideopage_key),
                AppCMSUIKeyType.PAGE_PLAY_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_action_showvideopage_key),
                AppCMSUIKeyType.PAGE_SHOW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_action_articlepage_key),
                AppCMSUIKeyType.PAGE_ARTICLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_action_photo_gallerypage_key),
                AppCMSUIKeyType.PAGE_PHOTO_GALLERY_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_team_page_tag),
                AppCMSUIKeyType.PAGE_TEAMS_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_watchnow_key),
                AppCMSUIKeyType.PAGE_WATCH_VIDEO_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_play_image_key),
                AppCMSUIKeyType.PAGE_PLAY_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_play_live_image_key),
                AppCMSUIKeyType.PAGE_PLAY_LIVE_IMAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tray_title_key),
                AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_schedule_title_key),
                AppCMSUIKeyType.PAGE_TRAY_SCHEDULE_TITLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_season_tray_title_key),
                AppCMSUIKeyType.PAGE_TRAY_SEASON_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tray_title_underline_key),
                AppCMSUIKeyType.PAGE_TRAY_TITLE_UNDERLINE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_schedule_page_title_key),
                AppCMSUIKeyType.PAGE_SCHEDULE_TRAY_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_schedule_key),
                AppCMSUIKeyType.PAGE_SCHEDULE_SCREEN_TITLE_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_roster_key),
                AppCMSUIKeyType.PAGE_ROSTER_SCREEN_TITLE_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_fighter_key),
                AppCMSUIKeyType.PAGE_FIGHTER_SCREEN_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_home_team_title_key),
                AppCMSUIKeyType.PAGE_HOME_TEAM_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_away_team_title_key),
                AppCMSUIKeyType.PAGE_AWAY_TEAM_TITLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_image_key),
                AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_home_team_image_key),
                AppCMSUIKeyType.PAGE_HOME_TEAM_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_away_team_image_key),
                AppCMSUIKeyType.PAGE_AWAY_TEAM_IMAGE_KEY);



        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_plan_feature_image_key),
                AppCMSUIKeyType.PAGE_PLAN_FEATURE_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_plan_feature_title_key),
                AppCMSUIKeyType.PAGE_PLAN_FEATURE_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_date_and_image_key),
                AppCMSUIKeyType.PAGE_THUMBNAIL_TIME_AND_DATE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_badge_image_key),
                AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_title_key),
                AppCMSUIKeyType.PAGE_THUMBNAIL_TITLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_title_on_hover_key),
                AppCMSUIKeyType.PAGE_VIDEO_TITLE_ON_HOVER_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_sub_title_on_hover_key),
                AppCMSUIKeyType.PAGE_VIDEO_SUB_TITLE_ON_HOVER_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_description_on_hover_key),
                AppCMSUIKeyType.PAGE_VIDEO_DESCRIPTION_ON_HOVER_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_hover_background_key),
                AppCMSUIKeyType.PAGE_VIDEO_HOVER_BACKGROUND_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_description_key),
                AppCMSUIKeyType.PAGE_THUMBNAIL_DESCRIPTION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_read_more_text_key),
                AppCMSUIKeyType.PAGE_THUMBNAIL_READ_MORE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_episode_thumbnail_title_key),
                AppCMSUIKeyType.PAGE_EPISODE_THUMBNAIL_TITLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_text_alignment_center_key),
                AppCMSUIKeyType.PAGE_TEXTALIGNMENT_CENTER_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_text_alignment_center_horizontal_key),
                AppCMSUIKeyType.PAGE_TEXTALIGNMENT_CENTER_HORIZONTAL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_text_alignment_center_vertical_key),
                AppCMSUIKeyType.PAGE_TEXTALIGNMENT_CENTER_VERTICAL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_text_alignment_key),
                AppCMSUIKeyType.PAGE_TEXTALIGNMENT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_text_alignment_left_key),
                AppCMSUIKeyType.PAGE_TEXTALIGNMENT_LEFT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_text_alignment_right_key),
                AppCMSUIKeyType.PAGE_TEXTALIGNMENT_RIGHT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_title_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_info_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_INFO_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_bold_key),
                AppCMSUIKeyType.PAGE_TEXT_BOLD_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_semibold_key),
                AppCMSUIKeyType.PAGE_TEXT_SEMIBOLD_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_extrabold_key),
                AppCMSUIKeyType.PAGE_TEXT_EXTRABOLD_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_black_key),
                AppCMSUIKeyType.PAGE_TEXT_BLACK_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_black_italic_key),
                AppCMSUIKeyType.PAGE_TEXT_BLACK_ITALIC_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_hairline_key),
                AppCMSUIKeyType.PAGE_TEXT_HAIRLINE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_hairline_italic_key),
                AppCMSUIKeyType.PAGE_TEXT_HAIRLINE_ITALIC_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_heavy_key),
                AppCMSUIKeyType.PAGE_TEXT_HEAVY_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_heavy_italic_key),
                AppCMSUIKeyType.PAGE_TEXT_HEAVY_ITALIC_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_light_key),
                AppCMSUIKeyType.PAGE_TEXT_LIGHT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_light_italic_key),
                AppCMSUIKeyType.PAGE_TEXT_LIGHT_ITALIC_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_bold_italic_key),
                AppCMSUIKeyType.PAGE_TEXT_BLACK_ITALIC_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_thin_key),
                AppCMSUIKeyType.PAGE_TEXT_THIN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_thin_italic_key),
                AppCMSUIKeyType.PAGE_TEXT_THIN_ITALIC_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_family_key),
                AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_lato_family_key),
                AppCMSUIKeyType.PAGE_TEXT_LATO_FONTFAMILY_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_textview_key),
                AppCMSUIKeyType.PAGE_TEXTVIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_textfield_key),
                AppCMSUIKeyType.PAGE_TEXTFIELD_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_emailtextfield_key),
                AppCMSUIKeyType.PAGE_EMAILTEXTFIELD_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_emailtextfield2_key),
                AppCMSUIKeyType.PAGE_EMAILTEXTFIELD2_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_passwordtextfield_key),
                AppCMSUIKeyType.PAGE_PASSWORDTEXTFIELD_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_passwordtextfield2_key),
                AppCMSUIKeyType.PAGE_PASSWORDTEXTFIELD2_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_forgotpassword_key),
                AppCMSUIKeyType.PAGE_FORGOTPASSWORD_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_mobileinput_key),
                AppCMSUIKeyType.PAGE_MOBILETEXTFIELD_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_authentication_module),
                AppCMSUIKeyType.PAGE_AUTHENTICATION_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_description_key),
                AppCMSUIKeyType.PAGE_API_DESCRIPTION);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_reset_password_module),
                AppCMSUIKeyType.PAGE_RESET_PASSWORD_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_contact_us_module),
                AppCMSUIKeyType.PAGE_CONTACT_US_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_reset_password_cancel_button_key),
                AppCMSUIKeyType.RESET_PASSWORD_CANCEL_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_reset_password_continue_button_key),
                AppCMSUIKeyType.RESET_PASSWORD_CONTINUE_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_forgotPasswordTitle_key),
                AppCMSUIKeyType.RESET_PASSWORD_TITLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_login_key),
                AppCMSUIKeyType.PAGE_LOGIN_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_signup_key),
                AppCMSUIKeyType.PAGE_SIGNUP_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_event_details_key),
                AppCMSUIKeyType.PAGE_EVENT_DETAILS_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_authentication_module),
                AppCMSUIKeyType.PAGE_AUTHENTICATION_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_description_key),
                AppCMSUIKeyType.PAGE_API_DESCRIPTION);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_summary_text_key),
                AppCMSUIKeyType.PAGE_API_SUMMARY_TEXT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_planmetadatatitle_key),
                AppCMSUIKeyType.PAGE_PLANMETADATATITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_planmetadataimage_key),
                AppCMSUIKeyType.PAGE_PLANMETADDATAIMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_planmetadatadevicecount_key),
                AppCMSUIKeyType.PAGE_PLANMETADATADEVICECOUNT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_settings_title_key),
                AppCMSUIKeyType.PAGE_SETTINGS_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_settings_name_value_key),
                AppCMSUIKeyType.PAGE_SETTINGS_NAME_VALUE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_settings_email_value_key),
                AppCMSUIKeyType.PAGE_SETTINGS_EMAIL_VALUE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_settings_plan_value_key),
                AppCMSUIKeyType.PAGE_SETTINGS_PLAN_VALUE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_settings_plan_processor_title_key),
                AppCMSUIKeyType.PAGE_SETTINGS_PLAN_PROCESSOR_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_settings_plan_processor_value_key),
                AppCMSUIKeyType.PAGE_SETTINGS_PLAN_PROCESSOR_VALUE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_settings_download_quality_value_key),
                AppCMSUIKeyType.PAGE_SETTINGS_DOWNLOAD_QUALITY_PROFILE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_edit_profile_key),
                AppCMSUIKeyType.PAGE_SETTINGS_EDIT_PROFILE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_change_password_key),
                AppCMSUIKeyType.PAGE_SETTINGS_CHANGE_PASSWORD_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_contact_number_label),
                AppCMSUIKeyType.CONTACT_US_PHONE_LABEL);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_email_id_label),
                AppCMSUIKeyType.CONTACT_US_EMAIL_LABEL);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_contact_us_call_icon_key),
                AppCMSUIKeyType.CONTACT_US_PHONE_IMAGE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_contact_us_email_icon_key),
                AppCMSUIKeyType.CONTACT_US_EMAIL_IMAGE);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_cancel_subscription_key),
                AppCMSUIKeyType.PAGE_SETTINGS_CANCEL_PLAN_PROFILE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_upgrade_subscription_key),
                AppCMSUIKeyType.PAGE_SETTINGS_UPGRADE_PLAN_PROFILE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_background_image_view_key),
                AppCMSUIKeyType.PAGE_BACKGROUND_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_background_image_type_type),
                AppCMSUIKeyType.PAGE_BACKGROUND_IMAGE_TYPE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_settings_app_version_value_key),
                AppCMSUIKeyType.PAGE_SETTINGS_APP_VERSION_VALUE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_togglebutton_key),
                AppCMSUIKeyType.PAGE_TOGGLE_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_autoplay_toggle_button_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_TOGGLE_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string
                        .app_cms_page_use_sd_card_for_downloads_toggle_button_key),
                AppCMSUIKeyType.PAGE_SD_CARD_FOR_DOWNLOADS_TOGGLE_BUTTON_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_user_management_use_sd_card_for_downloads_text_key),
                AppCMSUIKeyType.PAGE_SD_CARD_FOR_DOWNLOADS_TEXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photogallery_title),
                AppCMSUIKeyType.PAGE_PHOTO_GALLERY_TITLE_TXT_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_record_type_key),
                AppCMSUIKeyType.PAGE_RECORD_TYPE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photogallery_authName),
                AppCMSUIKeyType.PAGE_PHOTO_GALLERY_AUTH_TXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photogallery_subTitle),
                AppCMSUIKeyType.PAGE_PHOTO_GALLERY_SUBTITLE_TXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photogallery_imgCount),
                AppCMSUIKeyType.PAGE_PHOTO_GALLERY_IMAGE_COUNT_TXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photogallery_selectedImg),
                AppCMSUIKeyType.PAGE_PHOTO_GALLERY_SELECTED_IMAGE);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_Img),
                AppCMSUIKeyType.PAGE_PHOTO_PLAYER_IMAGE);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_team_Img),
                AppCMSUIKeyType.PAGE_PHOTO_TEAM_IMAGE);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_record_label_key),
                AppCMSUIKeyType.PAGE_PLAYER_RECORD_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_team_title_label_key),
                AppCMSUIKeyType.PAGE_PLAYER_TEAM_TITLE_TXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_weightdivision_value_key),
                AppCMSUIKeyType.PAGE_WEIGHT_DIVISION_VALUE_TXT_KEY);



        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tableHeaderLabel_label_key),
                AppCMSUIKeyType.PAGE_TABEL_LABEL_HEADER_TXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_statsViewHeader_label_key),
                AppCMSUIKeyType.PAGE_STATE_LABEL_TXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_roster_title_key),
                AppCMSUIKeyType.PAGE_ROSTER_TITLE_TXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_fight_selection_label_key),
                AppCMSUIKeyType.PAGE_FIGHT_SELECTION_TXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_score_label_key),
                AppCMSUIKeyType.PAGE_PLAYER_SCORE_TEXT);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_weight_value_label_key),
                AppCMSUIKeyType.PAGE_WEIGHT_VALUE_TEXT);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_height_value_key),
                AppCMSUIKeyType.PAGE_HEIGHT_VALUE_TEXT);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_birthdate_value_label_key),
                AppCMSUIKeyType.PAGE_BIRTHDATE_VALUE_TEXT);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_hometown_value_label_key),
                AppCMSUIKeyType.PAGE_HOMETOWN_VALUE_TEXT);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_name_key),
                AppCMSUIKeyType.PAGE_PLAYER_NAME_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photogallery_preButton),
                AppCMSUIKeyType.PAGE_PHOTOGALLERY_PRE_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photogallery_nextButton),
                AppCMSUIKeyType.PAGE_PHOTOGALLERY_NEXT_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photogallery_next_gallery),
                AppCMSUIKeyType.PAGE_PHOTOGALLERY_NEXT_GALLERY_LABEL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photogallery_prev_gallery),
                AppCMSUIKeyType.PAGE_PHOTOGALLERY_PREV_GALLERY_LABEL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photo_gallery_grid_key),
                AppCMSUIKeyType.PAGE_PHOTOGALLERY_GRID_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photogallery_image_key),
                AppCMSUIKeyType.PAGE_PHOTO_GALLERY_IMAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_closed_captions_toggle_button_key),
                AppCMSUIKeyType.PAGE_CLOSED_CAPTIONS_TOGGLE_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_user_management_autoplay_text_key),
                AppCMSUIKeyType.PAGE_USER_MANAGEMENT_AUTOPLAY_TEXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_height_label_key),
                AppCMSUIKeyType.PAGE_HEIGHT_LABEL_TEXT);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_player_weight_label_key),
                AppCMSUIKeyType.PAGE_WEIGHT_LABEL_TEXT);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_single_plan_subscribe_text_key),
                AppCMSUIKeyType.PAGE_SINGLE_PLAN_SUBSCRIBE_TEXT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_feature_text_key),
                AppCMSUIKeyType.PAGE_PLAN_FEATURE_TEXT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_title_key),
                AppCMSUIKeyType.PAGE_PLAN_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_priceinfo_key),
                AppCMSUIKeyType.PAGE_PLAN_PRICEINFO_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_description_key),
                AppCMSUIKeyType.PAGE_PLAN_DESCRIPTION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_bestvalue_key),
                AppCMSUIKeyType.PAGE_PLAN_BESTVALUE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_purchase_button_key),
                AppCMSUIKeyType.PAGE_PLAN_PURCHASE_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_meta_dataview_key),
                AppCMSUIKeyType.PAGE_PLAN_META_DATA_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_article_module_key),
                AppCMSUIKeyType.PAGE_ARTICLE_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_history_module_key),
                AppCMSUIKeyType.PAGE_HISTORY_01_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_module_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_01_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_mylibrary_module_key),
                AppCMSUIKeyType.PAGE_MYLIBRARY_01_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_history_module_key2),
                AppCMSUIKeyType.PAGE_HISTORY_02_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_module_key2),
                AppCMSUIKeyType.PAGE_WATCHLIST_02_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_module_key),
                AppCMSUIKeyType.PAGE_DOWNLOAD_01_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_playlist_module_key),
                AppCMSUIKeyType.PAGE_PLAYLIST_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_fight_list_module_key),
                AppCMSUIKeyType.PAGE_FIGHT_LIST_PARENT_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_module_key2),
                AppCMSUIKeyType.PAGE_DOWNLOAD_02_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_continue_watching_module_key),
                AppCMSUIKeyType.PAGE_CONTINUE_WATCHING_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_ac_web_frame_01),
                AppCMSUIKeyType.PAGE_AC_WEB_FRAME_03_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_settings_component_key),
                AppCMSUIKeyType.PAGE_SETTINGS_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_user_management_download_settings_key),
                AppCMSUIKeyType.PAGE_USER_MANAGEMENT_DOWNLOADS_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_duration_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_history_duration_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_duration_unit_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_UNIT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_description_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_DESCRIPTION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_title_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_TITLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_episode_title_key),
                AppCMSUIKeyType.PAGE_EPISODE_TITLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_contact_number_label),
                AppCMSUIKeyType.CONTACT_US_PHONE_LABEL);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_email_id_label),
                AppCMSUIKeyType.CONTACT_US_EMAIL_LABEL);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_history_last_added_label),
                AppCMSUIKeyType.PAGE_HISTORY_LAST_ADDED_LABEL_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_fighter_name_label_key),
                AppCMSUIKeyType.PAGE_FIGHTER_LABEL_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_fighter_selector_view_key),
                AppCMSUIKeyType.PAGE_FIGHTER_SELECTOR_VIEW_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_fighter_selector_arrow_view_key),
                AppCMSUIKeyType.PAGE_FIGHTER_SELECTOR_ARROW_VIEW_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_signup_footer_label_key),
                AppCMSUIKeyType.PAGE_SIGNUP_FOOTER_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_api_history_module_key),
                AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_module_showdetail_key),
                AppCMSUIKeyType.PAGE_API_SHOWDETAIL_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_show_detail_module_key),
                AppCMSUIKeyType.PAGE_API_SHOWDETAIL_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_team_detail_module_key),
                AppCMSUIKeyType.PAGE_API_TEAMDETAIL_MODULE_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_multitabletray_key),
                AppCMSUIKeyType.PAGE_API_MULTITABLE_TEAM_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_subscription_page_key),
                AppCMSUIKeyType.PAGE_SUBSCRIPTION_PAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_subscription_selectionplan_02_key),
                AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_subscription_selectionplan_03_key),
                AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_03_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photo_gallery_02_key),
                AppCMSUIKeyType.PAGE_PHOTO_GALLERY_TRAY_02_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photo_gallery_grid_01_key),
                AppCMSUIKeyType.PAGE_PHOTO_GALLERY_GRID_01_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_article_tray_key),
                AppCMSUIKeyType.PAGE_ARTICLE_TRAY_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_subscription_selectionplan_01_key),
                AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_01_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_subscription_imagetextrow_key),
                AppCMSUIKeyType.PAGE_SUBSCRIPTION_IMAGEROW_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_subscription_imagetextrow_02_key),
                AppCMSUIKeyType.PAGE_SUBSCRIPTION_IMAGEROW_02_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_season_tray_module_key),
                AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_module_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_event_carousel_module_key),
                AppCMSUIKeyType.PAGE_EVENT_CAROUSEL_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_player_module_key),
                AppCMSUIKeyType.PAGE_VIDEO_PLAYER_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_setting_module),
                AppCMSUIKeyType.PAGE_SETTINGS_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_authentication_module),
                AppCMSUIKeyType.PAGE_AUTHENTICATION_MODULE_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_game_detail_module),
                AppCMSUIKeyType.PAGE_GAME_DETAIL_MODULE_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_fightsummary_key_type),
                AppCMSUIKeyType.PAGE_FIGHT_SUMMARY_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_player_detail_module),
                AppCMSUIKeyType.PAGE_PLAYER_DETAIL_MODULE_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_event_detail_module),
                AppCMSUIKeyType.PAGE_EVENT_DETAIL_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tray_module_key),
                AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tray_02_module_key),
                AppCMSUIKeyType.PAGE_TRAY_02_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tray_05_module_key),
                AppCMSUIKeyType.PAGE_TRAY_05_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_fight_table_key),
                AppCMSUIKeyType.PAGE_FIGHT_TABLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tray_05_module_key),
                AppCMSUIKeyType.PAGE_EVENT_DETAIL_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_schedule_module_key),
                AppCMSUIKeyType.PAGE_AC_TEAM_SCHEDULE_MODULE_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_parent_bg_key),
                AppCMSUIKeyType.PAGE_AC_TEAM_PARENT_BG_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_roster_page_module_key),
                AppCMSUIKeyType.PAGE_AC_ROSTER_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tray_06_module_key),
                AppCMSUIKeyType.PAGE_TRAY_06_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tray_03_module_key),
                AppCMSUIKeyType.PAGE_TRAY_03_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_audio_tray_module_key),
                AppCMSUIKeyType.PAGE_AUDIO_TRAY_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_bannerAd_module_key),
                AppCMSUIKeyType.PAGE_BANNER_AD_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_mediumRectangleAd_module_key),
                AppCMSUIKeyType.PAGE_MEDIAM_RECTANGLE_AD_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_photo_tray_module_key),
                AppCMSUIKeyType.PAGE_PHOTO_TRAY_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_article_feed_module_key),
                AppCMSUIKeyType.PAGE_ARTICLE_FEED_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_person_details_module_key),
                AppCMSUIKeyType.PAGE_PERSON_DETAIL_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_grid_module_key),
                AppCMSUIKeyType.PAGE_GRID_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_player_with_info_key),
                AppCMSUIKeyType.PAGE_VIDEO_DETAILS_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_video_detail_module_key),
                AppCMSUIKeyType.PAGE_VIDEO_DETAILS_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_login_component_key),
                AppCMSUIKeyType.PAGE_LOGIN_COMPONENT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_create_login_component_key),
                AppCMSUIKeyType.PAGE_CREATE_LOGIN_COMPONENT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_video_tab_component_key),
                AppCMSUIKeyType.PAGE_DOWNLOAD_VIDEO_TAB_COMPONENT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_audio_tab_component_key),
                AppCMSUIKeyType.PAGE_DOWNLOAD_AUDIO_TAB_COMPONENT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_signup_component_key),
                AppCMSUIKeyType.PAGE_SIGNUP_COMPONENT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_removeall_key),
                AppCMSUIKeyType.PAGE_REMOVEALL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_game_tickets_key),
                AppCMSUIKeyType.PAGE_GAME_TICKETS_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_image_key),
                AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_show_image_video_key),
                AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_bedge_image_key),
                AppCMSUIKeyType.PAGE_BEDGE_IMAGE_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_image_video_key),
                AppCMSUIKeyType.PAGE_THUMBNAIL_VIDEO_IMAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_start_watching_button_key),
                AppCMSUIKeyType.PAGE_START_WATCHING_BUTTON_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_show_start_watching_button_key),
                AppCMSUIKeyType.PAGE_SHOW_START_WATCHING_BUTTON_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_play_button_key),
                AppCMSUIKeyType.PAGE_VIDEO_PLAY_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_description_key),
                AppCMSUIKeyType.PAGE_VIDEO_DESCRIPTION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_show_video_description_key),
                AppCMSUIKeyType.PAGE_VIDEO_DESCRIPTION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_title_key),
                AppCMSUIKeyType.PAGE_VIDEO_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_setting_title_key),
                AppCMSUIKeyType.PAGE_DOWNLOAD_SETTING_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_subtitle_key),
                AppCMSUIKeyType.PAGE_VIDEO_SUBTITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_show_video_subtitle_key),
                AppCMSUIKeyType.PAGE_SHOW_SUBTITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_share_key),
                AppCMSUIKeyType.PAGE_VIDEO_SHARE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_cast_key),
                AppCMSUIKeyType.PAGE_VIDEO_CAST_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_close_key),
                AppCMSUIKeyType.PAGE_VIDEO_CLOSE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_starrating_key),
                AppCMSUIKeyType.PAGE_VIDEO_STARRATING_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_ageLabel_key),
                AppCMSUIKeyType.PAGE_VIDEO_AGE_LABEL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_credits_director_key),
                AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTOR_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_credits_directedby_key),
                AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTEDBY_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_credits_directors),
                AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTORS_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_credits_starring_key),
                AppCMSUIKeyType.PAGE_VIDEO_CREDITS_STARRING_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchTrailer_key),
                AppCMSUIKeyType.PAGE_VIDEO_WATCH_TRAILER_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_show_watchTrailer_key),
                AppCMSUIKeyType.PAGE_SHOW_WATCH_TRAILER_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_title_key),
                AppCMSUIKeyType.PAGE_API_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_expire_time_key),
                AppCMSUIKeyType.PAGE_EXPIRE_TIME_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_title_key),
                AppCMSUIKeyType.PAGE_API_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_show_title_key),
                AppCMSUIKeyType.PAGE_API_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_switch_seasons_key),
                AppCMSUIKeyType.PAGE_SHOW_SWITCH_SEASONS_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_description_key),
                AppCMSUIKeyType.PAGE_API_DESCRIPTION);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_thumbnail_url_key),
                AppCMSUIKeyType.PAGE_API_THUMBNAIL_URL);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_header_view),
                AppCMSUIKeyType.PAGE_HEADER_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_videodetail_header_view),
                AppCMSUIKeyType.PAGE_VIDEO_DETAIL_HEADER_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_autoplay_module_key_01),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_01);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_autoplay_module_key_02),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_02);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_autoplay_module_key_03),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY_03);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_autoplay_landscape_module_key_01),
                AppCMSUIKeyType.PAGE_AUTOPLAY_LANDSCAPE_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_autoplay_portrait_module_key_01),
                AppCMSUIKeyType.PAGE_AUTOPLAY_PORTRAIT_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_setting_module_key),
                AppCMSUIKeyType.PAGE_DOWNLOAD_SETTING_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_back_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_BACK_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_finished_up_title_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_FINISHED_UP_TITLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_movie_title_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_TITLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_finished_movie_title_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_FINISHED_MOVIE_TITLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_finished_movie_image_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_FINISHED_MOVIE_IMAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.icon_image_key), AppCMSUIKeyType.PAGE_ICON_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.icon_label_key), AppCMSUIKeyType.PAGE_ICON_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_movie_subheading_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_SUBHEADING_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_movie_description_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_DESCRIPTION_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_movie_star_rating_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_STAR_RATING_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_movie_director_label_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_DIRECTOR_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_movie_sub_director_label_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_SUB_DIRECTOR_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_movie_image_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_IMAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_play_button_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_PLAY_BUTTON_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_cancel_button_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_CANCEL_BUTTON_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_playing_in_label_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_PLAYING_IN_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_countdown_cancelled_label_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_COUNTDOWN_CANCELLED_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_timer_label_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MOVIE_TIMER_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_download_quality_continue_button_key),
                AppCMSUIKeyType.PAGE_DOWNLOAD_QUALITY_CONTINUE_BUTTON_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_download_quality_cancel_button_key),
                AppCMSUIKeyType.PAGE_DOWNLOAD_QUALITY_CANCEL_BUTTON_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_toggle_switch_type),
                AppCMSUIKeyType.PAGE_SETTING_TOGGLE_SWITCH_TYPE);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_toggle_switch_key),
                AppCMSUIKeyType.PAGE_SETTING_AUTOPLAY_TOGGLE_SWITCH_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_closed_caption_toggle_switch_key),
                AppCMSUIKeyType.PAGE_SETTING_CLOSED_CAPTION_TOGGLE_SWITCH_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_cellular_data_toggle_switch_key),
                AppCMSUIKeyType.PAGE_DOWNLOAD_VIA_CELLULAR_NETWORK_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_logout_button_key),
                AppCMSUIKeyType.PAGE_SETTING_LOGOUT_BUTTON_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_title_label),
                AppCMSUIKeyType.PAGE_WATCHLIST_TITLE_LABEL);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_description_label),
                AppCMSUIKeyType.PAGE_WATCHLIST_DESCRIPTION_LABEL);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_subtitle_label),
                AppCMSUIKeyType.PAGE_WATCHLIST_SUBTITLE_LABEL);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_delete_item_button),
                AppCMSUIKeyType.PAGE_WATCHLIST_DELETE_ITEM_BUTTON);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_history_last_added_label),
                AppCMSUIKeyType.PAGE_HISTORY_LAST_ADDED_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_signup_footer_label_key),
                AppCMSUIKeyType.PAGE_SIGNUP_FOOTER_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_up_next_loader_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_UP_NEXT_LOADER_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_autoplay_rotating_loader_view),
                AppCMSUIKeyType.PAGE_AUTOPLAY_ROTATING_LOADER_VIEW_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_settings_subscription_duration_label),
                AppCMSUIKeyType.PAGE_SETTINGS_SUBSCRIPTION_DURATION_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_settings_subscription_end_date_label),
                AppCMSUIKeyType.PAGE_SETTINGS_SUBSCRIPTION_END_DATE_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_settings_manage_subscription_button_key),
                AppCMSUIKeyType.PAGE_SETTINGS_MANAGE_SUBSCRIPTION_BUTTON_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_settings_subscription_label_key),
                AppCMSUIKeyType.PAGE_SETTINGS_SUBSCRIPTION_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_settings_user_email_label_key),
                AppCMSUIKeyType.PAGE_SETTINGS_USER_EMAIL_LABEL_KEY);

        jsonValueKeyMap.put("", AppCMSUIKeyType.PAGE_EMPTY_KEY);
        jsonValueKeyMap.put(null, AppCMSUIKeyType.PAGE_NULL_KEY);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_grid_option_key),
                AppCMSUIKeyType.PAGE_GRID_OPTION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_option_key),
                AppCMSUIKeyType.PAGE_GRID_THUMBNAIL_INFO);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_photo_gallery_thumbnail_option_key),
                AppCMSUIKeyType.PAGE_GRID_PHOTO_GALLERY_THUMBNAIL_INFO);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_duration_label),
                AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_KEY_BG);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_badgeimage),
                AppCMSUIKeyType.PAGE_THUMBNAIL_BADGE_IMAGE);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_banner_image_key),
                AppCMSUIKeyType.PAGE_BANNER_IMAGE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_banner_detail_key),
                AppCMSUIKeyType.PAGE_BANNER_DETAIL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_banner_detail_icon_key),
                AppCMSUIKeyType.PAGE_BANNER_DETAIL_ICON);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_banner_detail_background_key),
                AppCMSUIKeyType.PAGE_BANNER_DETAIL_BACKGROUND);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_banner_detail_button_key),
                AppCMSUIKeyType.PAGE_BANNER_DETAIL_BUTTON);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_banner_detail_title_key),
                AppCMSUIKeyType.PAGE_BANNER_DETAIL_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_delete_watchlist_key),
                AppCMSUIKeyType.PAGE_DELETE_WATCHLIST_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_delete_history_key),
                AppCMSUIKeyType.PAGE_DELETE_HISTORY_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_table_background_view),
                AppCMSUIKeyType.PAGE_GRID_BACKGROUND);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_duration_key),
                AppCMSUIKeyType.PAGE_DOWNLOAD_DURATION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_description_key),
                AppCMSUIKeyType.PAGE_DOWNLOAD_DESCRIPTION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_history_description_key),
                AppCMSUIKeyType.PAGE_HISTORY_DESCRIPTION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_history_duration_key),
                AppCMSUIKeyType.PAGE_HISTORY_DURATION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_playlist_audio_duration_key),
                AppCMSUIKeyType.PAGE_AUDIO_DURATION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_history_watched_time_key),
                AppCMSUIKeyType.PAGE_HISTORY_WATCHED_TIME_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_game_time_key),
                AppCMSUIKeyType.PAGE_GAME_TIME_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_game_date_key),
                AppCMSUIKeyType.PAGE_GAME_DATE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_delete_download_key),
                AppCMSUIKeyType.PAGE_DELETE_DOWNLOAD_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_size_key),
                AppCMSUIKeyType.PAGE_DELETE_DOWNLOAD_VIDEO_SIZE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_description_key),
                AppCMSUIKeyType.PAGE_API_DESCRIPTION);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_rawhtml_title_key),
                AppCMSUIKeyType.RAW_HTML_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_rawhtml_image_key),
                AppCMSUIKeyType.RAW_HTML_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_playlist_title_key),
                AppCMSUIKeyType.PAGE_PLAYLIST_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_playlist_sub_title_key),
                AppCMSUIKeyType.PAGE_PLAYLIST_SUB_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_playlist_audio_artist_key),
                AppCMSUIKeyType.PAGE_PLAYLIST_AUDIO_ARTIST_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_audio_download_button_key),
                AppCMSUIKeyType.PAGE_AUDIO_DOWNLOAD_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_playlist_download_button_key),
                AppCMSUIKeyType.PAGE_PLAYLIST_DOWNLOAD_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_audio_tray_module_key),
                AppCMSUIKeyType.PAGE_AUDIO_TRAY_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_link_your_account_btn_key),
                AppCMSUIKeyType.PAGE_LINK_YOUR_ACOOUNT_BTN_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_link_your_account_text_key),
                AppCMSUIKeyType.PAGE_LINK_YOUR_ACCOUNT_TEXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_open_sign_up_button_key),
                AppCMSUIKeyType.OPEN_SIGN_UP_PAGE_BUTTON_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_link_your_account_module),
                AppCMSUIKeyType.PAGE_LINK_YOUR_ACCOUNT_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_action_cancel_key),
                AppCMSUIKeyType.CANCEL_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_request_new_code_btn_key),
                AppCMSUIKeyType.REQUEST_NEW_CODE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_code_sync_text_line_1),
                AppCMSUIKeyType.CODE_SYNC_TEXT_LINE_1);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_code_sync_text_line_2),
                AppCMSUIKeyType.CODE_SYNC_TEXT_LINE_2);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_code_sync_text_line_3),
                AppCMSUIKeyType.CODE_SYNC_TEXT_LINE_3);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_code_sync_text_line_header),
                AppCMSUIKeyType.CODE_SYNC_TEXT_LINE_HEADER);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tabs_type),
                AppCMSUIKeyType.PAGE_TABLAYOUT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.manageLanguageBtnKey),
                AppCMSUIKeyType.MANAGE_LANGUAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.changeLanguageLabel),
                AppCMSUIKeyType.LANGUAGE_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_change_language_key),
                AppCMSUIKeyType.LANGUAGE_SCREEN_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_link_change_language_action),
                AppCMSUIKeyType.CHANGE_LANGUAGE_KEY);
    }

    private void createPageNameToActionMap(Context context) {
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_splashscreen_key),
                context.getString(R.string.app_cms_action_authpage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_homescreen_key),
                context.getString(R.string.app_cms_action_homepage_key));
        /*this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_musicHub_screen_key),
                context.getString(R.string.app_cms_action_musicHub_page_key));*/
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_historyscreen_key),
                context.getString(R.string.app_cms_action_historypage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_history_screen_key),
                context.getString(R.string.app_cms_action_historypage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_page_schedule_key),
                context.getString(R.string.app_cms_pagename_schedule_screen_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_watchlistscreen_key),
                context.getString(R.string.app_cms_action_watchlistpage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_videoscreen_key),
                context.getString(R.string.app_cms_action_detailvideopage_key));

        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_showscreen_key),
                context.getString(R.string.app_cms_action_showvideopage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_articlescreen_key),
                context.getString(R.string.app_cms_action_articlepage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_photogalleryscreen_key),
                context.getString(R.string.app_cms_action_photo_gallerypage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_moviesscreen_key),
                context.getString(R.string.app_cms_action_moviespage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_page_name_forgotpassword),
                context.getString(R.string.app_cms_action_forgotpassword_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_page_name_forgotpassword),
                context.getString(R.string.app_cms_action_forgotpassword_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_link_your_account_key),
                context.getString(R.string.app_cms_link_your_account_action));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_change_language_key),
                context.getString(R.string.app_cms_link_change_language_action));
    }

    private void createActionToPageMap(Context context) {
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_authpage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_homepage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_historypage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_videopage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_detailvideopage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_articlepage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_photo_gallerypage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_watchvideo_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_watchlistpage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_showvideopage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_pagename_moviesscreen_key), null);
        //this.actionToPageMap.put(context.getString(R.string.app_cms_action_musicHub_page_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_forgotpassword_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_link_your_account_action), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_link_change_language_action), null);
    }

    private void createActionToPageAPIMap(Context context) {
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_authpage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_homepage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_videopage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_watchvideo_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_showvideopage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_articlepage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_photo_gallerypage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_pagename_moviesscreen_key), null);
        //this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_musicHub_page_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_watchvideo_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_forgotpassword_key), null);
    }

    private void createActionToActionTypeMap(Context context) {
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_subscribe_go),
                AppCMSActionType.SUBSCRIBEGO);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_authpage_key),
                AppCMSActionType.SPLASH_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_authpage_key),
                AppCMSActionType.AUTH_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_pagename_homescreen_key),
                AppCMSActionType.HOME_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_browse_key),
                AppCMSActionType.HOME_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_historypage_key),
                AppCMSActionType.HISTORY_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_watchlistpage_key),
                AppCMSActionType.WATCHLIST_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_musicHub_page_key),
                AppCMSActionType.MUSIC_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_playlistpage_key),
                AppCMSActionType.PLAYLIST_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_videopage_key),
                AppCMSActionType.PLAY_VIDEO_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_detailvideopage_key),
                AppCMSActionType.VIDEO_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_showvideopage_key),
                AppCMSActionType.SHOW_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_articlepage_key),
                AppCMSActionType.ARTICLE_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_photo_gallerypage_key),
                AppCMSActionType.PHOTO_GALLERY_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_moviespage_key),
                AppCMSActionType.MOVIES_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_watchvideo_key),
                AppCMSActionType.PLAY_VIDEO_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_watchtrailervideo_key),
                AppCMSActionType.WATCH_TRAILER);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_share_key),
                AppCMSActionType.SHARE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_cast_key),
                AppCMSActionType.CAST_VIDEO);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_close_key),
                AppCMSActionType.CLOSE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_cancel_key),
                AppCMSActionType.CLOSE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_login_key),
                AppCMSActionType.LOGIN);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_signin_key),
                AppCMSActionType.SIGNIN);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_forgotpassword_key),
                AppCMSActionType.FORGOT_PASSWORD);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_loginfacebook_key),
                AppCMSActionType.LOGIN_FACEBOOK);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_signupfacebook_key),
                AppCMSActionType.SIGNUP_FACEBOOK);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_logingoogle_key),
                AppCMSActionType.LOGIN_GOOGLE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_signupgoogle_key),
                AppCMSActionType.SIGNUP_GOOGLE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_signup_key),
                AppCMSActionType.SIGNUP);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_logout_key),
                AppCMSActionType.LOGOUT);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_title_label),
                AppCMSUIKeyType.PAGE_WATCHLIST_TITLE_LABEL);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_description_label),
                AppCMSUIKeyType.PAGE_WATCHLIST_DESCRIPTION_LABEL);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_delete_history_item_button),
                AppCMSUIKeyType.PAGE_WATCHLIST_DELETE_ITEM_BUTTON);


        jsonValueKeyMap.put(context.getString(R.string.app_cms_title_label),
                AppCMSUIKeyType.PAGE_WATCHLIST_TITLE_LABEL);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_description_label),
                AppCMSUIKeyType.PAGE_WATCHLIST_DESCRIPTION_LABEL);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_delete_watchlist_item_button),
                AppCMSUIKeyType.PAGE_WATCHLIST_DELETE_ITEM_BUTTON);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_startfreetrial_key),
                AppCMSActionType.START_TRIAL);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_editprofile_key),
                AppCMSActionType.EDIT_PROFILE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_change_password_key),
                AppCMSActionType.CHANGE_PASSWORD);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_change_password_key),
                AppCMSActionType.CHANGE_PASSWORD);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_managesubscription_key),
                AppCMSActionType.MANAGE_SUBSCRIPTION);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_change_download_quality_key),
                AppCMSActionType.CHANGE_DOWNLOAD_QUALITY);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_open_option_dialog),
                AppCMSActionType.OPEN_OPTION_DIALOG);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_banner_detail_button_action_key),
                AppCMSActionType.BANNER_DETAIL_CLICK);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_delete_single_watchlist_action),
                AppCMSActionType.DELETE_SINGLE_WATCHLIST_ITEM);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_delete_single_history_action),
                AppCMSActionType.DELETE_SINGLE_HISTORY_ITEM);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_publishdate_key),
                AppCMSUIKeyType.PAGE_VIDEO_PUBLISHDATE_KEY);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_delete_single_download_action),
                AppCMSActionType.DELETE_SINGLE_DOWNLOAD_ITEM);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_link_your_account_action),
                AppCMSActionType.LINK_YOUR_ACCOUNT);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_delete_single_download_action),
                AppCMSActionType.DELETE_SINGLE_DOWNLOAD_ITEM);

        actionToActionTypeMap.put(context.getString(R.string.changeLanguage),
                AppCMSActionType.CHANGE_LANGUAGE);

    }


    @Provides
    @Singleton
    public AssetManager providesAssetManager() {
        return assetManager;
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new GsonBuilder().registerTypeAdapterFactory(new Stag.Factory())
                .create();
        // return new Gson();
    }

    @Provides
    @Singleton
    public File providesStorageDirectory() {
        return storageDirectory;
    }

    @Provides
    @Singleton
    public OkHttpClient providesOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(defaultConnectionTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(defaultWriteConnectionTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(defaultReadConnectionTimeout, TimeUnit.MILLISECONDS)
                .cache(cache)
                .build();
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    public AppCMSMainUIRest providesAppCMSMainUIRest(Retrofit retrofit) {
        return retrofit.create(AppCMSMainUIRest.class);
    }

    @Provides
    @Singleton
    public AppCMSAndroidUIRest providesAppCMSAndroidUIRest(Retrofit retrofit) {
        return retrofit.create(AppCMSAndroidUIRest.class);
    }

    @Provides
    @Singleton
    public AppCMSPageUIRest providesAppCMSPageUIRest(Retrofit retrofit) {
        return retrofit.create(AppCMSPageUIRest.class);
    }

    @Provides
    @Singleton
    public AppCMSWatchlistRest providesAppCMSWatchlistRest(Retrofit retrofit) {
        return retrofit.create(AppCMSWatchlistRest.class);
    }

    @Provides
    @Singleton
    public AppCMSPlaylistRest providesAppCMSPlaylistRest(Retrofit retrofit) {
        return retrofit.create(AppCMSPlaylistRest.class);
    }

    @Provides
    @Singleton
    public AppCMSTeamStandingRest providesAppCMSStandingRest(Retrofit retrofit) {
        return retrofit.create(AppCMSTeamStandingRest.class);
    }

    @Provides
    @Singleton
    public AppCMSTeamRoasterRest providesAppCMSRoasterRest(Retrofit retrofit) {
        return retrofit.create(AppCMSTeamRoasterRest.class);
    }

    @Provides
    @Singleton
    public AppCMSEventArchieveRest providesAppCMSEventArchieveRestt(Retrofit retrofit) {
        return retrofit.create(AppCMSEventArchieveRest.class);
    }

    @Provides
    @Singleton
    public AppCMSSSLCommerzInitiateRest providesAppCMSSSLCommerzConfigRest(Retrofit retrofit) {
        return retrofit.create(AppCMSSSLCommerzInitiateRest.class);
    }

    @Provides
    @Singleton
    public AppCMSSSLCommerzInitiateCall providesAppCMSSSLCommerzConfigCall(AppCMSSSLCommerzInitiateRest initiateRest, Gson gson) {
        return new AppCMSSSLCommerzInitiateCall(initiateRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSCCAvenueRSAKeyRest providesAppCMSCCAvenueRSAKeyRest(Retrofit retrofit) {
        return retrofit.create(AppCMSCCAvenueRSAKeyRest.class);
    }

    @Provides
    @Singleton
    public AppCMSCCAvenueRSAKeyCall providesAppCMSCCAvenueRSAKeyCall(AppCMSCCAvenueRSAKeyRest initiateRest, Gson gson) {
        return new AppCMSCCAvenueRSAKeyCall(initiateRest, gson);
    }


    @Provides
    @Singleton
    public AppCMSAudioDetailRest providesAppCMSAudioDetailRest(Retrofit retrofit) {
        return retrofit.create(AppCMSAudioDetailRest.class);
    }

    @Provides
    @Singleton
    public AppCMSHistoryRest providesAppCMSHistoryRest(Retrofit retrofit) {
        return retrofit.create(AppCMSHistoryRest.class);
    }

    @Provides
    @Singleton
    public AppCMSArticleRest providesAppCMSArticleRest(Retrofit retrofit) {
        return retrofit.create(AppCMSArticleRest.class);
    }

    @Provides
    @Singleton
    public AppCMSScheduleRest providesAppCMSScheduleRest(Retrofit retrofit) {
        return retrofit.create(AppCMSScheduleRest.class);
    }
    @Provides
    @Singleton
    public AppCMSRosterRest providesAppCMSRostRest(Retrofit retrofit) {
        return retrofit.create(AppCMSRosterRest.class);
    }

    @Provides
    @Singleton
    public AppCMSLibraryRest providesAppCMSLibraryRest(Retrofit retrofit) {
        return retrofit.create(AppCMSLibraryRest.class);
    }
    @Provides
    @Singleton
    public AppCMSPhotoGalleryRest providesAppCMSPhotoGalleryRest(Retrofit retrofit) {

        return retrofit.create(AppCMSPhotoGalleryRest.class);
    }

    @Provides
    @Singleton
    public AppCMSBeaconRest providesAppCMSBeaconMessage(Retrofit retrofit) {
        return retrofit.create(AppCMSBeaconRest.class);
    }

    @Provides
    @Singleton
    public AppCMSSignInRest providesAppCMSSignInRest(Retrofit retrofit) {
        return retrofit.create(AppCMSSignInRest.class);
    }

    @Provides
    @Singleton
    public AppCMSRefreshIdentityRest providesAppCMSRefreshIdentityRest(Retrofit retrofit) {
        return retrofit.create(AppCMSRefreshIdentityRest.class);
    }

    @Provides
    @Singleton
    public AppCMSResetPasswordRest providesAppCMSResetPasswordRest(Retrofit retrofit) {
        return retrofit.create(AppCMSResetPasswordRest.class);
    }

    @Provides
    @Singleton
    public AppCMSFacebookLoginRest providesAppCMSFacebookLoginRest(Retrofit retrofit) {
        return retrofit.create(AppCMSFacebookLoginRest.class);
    }

    @Provides
    @Singleton
    public AppCMSGoogleLoginRest providesAppCMSGoogleLoginRest(Retrofit retrofit) {
        return retrofit.create(AppCMSGoogleLoginRest.class);
    }

    @Provides
    @Singleton
    public AppCMSUserIdentityRest providesAppCMSUserIdentityRest(Retrofit retrofit) {
        return retrofit.create(AppCMSUserIdentityRest.class);
    }

    @Provides
    @Singleton
    public AppCMSUpdateWatchHistoryRest providesAppCMSUpdateWatchHistoryRest(Retrofit retrofit) {
        return retrofit.create(AppCMSUpdateWatchHistoryRest.class);
    }

    @Provides
    @Singleton
    public AppCMSUserVideoStatusRest providesAppCMSUserVideoStatusRest(Retrofit retrofit) {
        return retrofit.create(AppCMSUserVideoStatusRest.class);
    }

    @Provides
    @Singleton
    public AppCMSAddToWatchlistRest providesAppCMSAddToWatchlistRest(Retrofit retrofit) {
        return retrofit.create(AppCMSAddToWatchlistRest.class);
    }

    @Provides
    @Singleton
    public AppCMSDeleteHistoryRest providesAppCMSDeleteHistoryRest(Retrofit retrofit) {
        return retrofit.create(AppCMSDeleteHistoryRest.class);
    }

    @Provides
    @Singleton
    public AppCMSSubscriptionRest providesAppCMSSubscriptionRest(Retrofit retrofit) {
        return retrofit.create(AppCMSSubscriptionRest.class);
    }

    @Provides
    @Singleton
    public AppCMSSubscriptionPlanRest providesAppCMSSubscriptionPlanRest(Retrofit retrofit) {
        return retrofit.create(AppCMSSubscriptionPlanRest.class);
    }

    @Provides
    @Singleton
    public AppCMSAnonymousAuthTokenRest providesAppCMSAnonymousAuthTokenRest(Retrofit retrofit) {
        return retrofit.create(AppCMSAnonymousAuthTokenRest.class);
    }

    @Provides
    @Singleton
    public GoogleRefreshTokenRest providesGoogleRefreshTokenRest(Retrofit retrofit) {
        return retrofit.create(GoogleRefreshTokenRest.class);
    }

    @Provides
    @Singleton
    public GoogleCancelSubscriptionRest providesGoogleCancelSubscriptionRest(Retrofit retrofit) {
        return retrofit.create(GoogleCancelSubscriptionRest.class);
    }

    @Provides
    @Singleton
    public AppCMSIPGeoLocatorRest providesAppCMSIPGeoLocatorRest(Retrofit retrofit) {
        return retrofit.create(AppCMSIPGeoLocatorRest.class);
    }

    @Provides
    @Singleton
    public AppCMSCCAvenueRest providesAppCMSAvenueRest(Retrofit retrofit) {
        return retrofit.create(AppCMSCCAvenueRest.class);
    }

    @Provides
    @Singleton
    public AppCMSRestorePurchaseRest providesAppCMSRestorePurchaseRest(Retrofit retrofit) {
        return retrofit.create(AppCMSRestorePurchaseRest.class);
    }

    @Provides
    @Singleton
    public AppCMSAndroidModuleRest providesAppCMSAndroidModuleRest(Retrofit retrofit) {
        return retrofit.create(AppCMSAndroidModuleRest.class);
    }

    @Provides
    @Singleton
    public AppCMSSignedURLRest providesAppCMSSignedURLRest(Retrofit retrofit) {
        return retrofit.create(AppCMSSignedURLRest.class);
    }

    @Provides
    @Singleton
    public AppCMSMainUICall providesAppCMSMainUICall(OkHttpClient client,
                                                     AppCMSMainUIRest appCMSMainUIRest,
                                                     Gson gson) {
        return new AppCMSMainUICall(unknownHostExceptionTimeout,
                client,
                appCMSMainUIRest,
                gson,
                storageDirectory);
    }

    @Provides
    @Singleton
    public AppCMSAndroidUICall providesAppCMSAndroidUICall(AppCMSAndroidUIRest appCMSAndroidUIRest,
                                                           Gson gson) {
        return new AppCMSAndroidUICall(appCMSAndroidUIRest, gson, storageDirectory);
    }

    @Provides
    @Singleton
    public AppCMSPageUICall providesAppCMSPageUICall(AppCMSPageUIRest appCMSPageUIRest, Gson gson) {
        return new AppCMSPageUICall(appCMSPageUIRest, gson, storageDirectory);
    }

    @Provides
    @Singleton
    public AppCMSSignInCall providesAppCMSSignInCall(AppCMSSignInRest appCMSSignInRest, Gson gson) {
        return new AppCMSSignInCall(appCMSSignInRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSRefreshIdentityCall providesAppCMSRefreshIdentityCall(AppCMSRefreshIdentityRest appCMSRefreshIdentityRest) {
        return new AppCMSRefreshIdentityCall(appCMSRefreshIdentityRest);
    }

    @Provides
    @Singleton
    public AppCMSWatchlistCall providesAppCMSWatchlistCall(AppCMSWatchlistRest appCMSWatchlistRest, Gson gson) {
        return new AppCMSWatchlistCall(appCMSWatchlistRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSPlaylistCall providesAppCMSPlaylistCall(AppCMSPlaylistRest appCMSPlaylistRest, Gson gson) {
        return new AppCMSPlaylistCall(appCMSPlaylistRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSTeamStandingCall providesAppCMSStandingCall(AppCMSTeamStandingRest appCMSStandingRest, Gson gson) {
        return new AppCMSTeamStandingCall(appCMSStandingRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSTeamRoasterCall providesAppCMSTeamRoasterCall(AppCMSTeamRoasterRest appCMSStandingRest, Gson gson) {
        return new AppCMSTeamRoasterCall(appCMSStandingRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSEventArchieveCall providesAppCMSEventArchieveCall(AppCMSEventArchieveRest appCMSEventArchievRest, Gson gson) {
        return new AppCMSEventArchieveCall(appCMSEventArchievRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSAudioDetailCall providesAppCMSAudioDetailCall(AppCMSAudioDetailRest appCMSAudioDetailRest, Gson gson) {
        return new AppCMSAudioDetailCall(appCMSAudioDetailRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSHistoryCall providesAppCMSHistoryCall(AppCMSHistoryRest appCMSHistoryRest, Gson gson) {
        return new AppCMSHistoryCall(appCMSHistoryRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSArticleCall providesAppCMSArticleCall(AppCMSArticleRest appCMSArticleRest, Gson gson) {
        return new AppCMSArticleCall(appCMSArticleRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSPhotoGalleryCall providesAppCMSPhotoGalleryCall(AppCMSPhotoGalleryRest appCMSPhotoGalleryRest, Gson gson) {
        return new AppCMSPhotoGalleryCall(appCMSPhotoGalleryRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSResetPasswordCall providesAppCMSPasswordCall(AppCMSResetPasswordRest appCMSResetPasswordRest,
                                                              Gson gson) {
        return new AppCMSResetPasswordCall(appCMSResetPasswordRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSFacebookLoginCall providesAppCMSFacebookLoginCall(AppCMSFacebookLoginRest appCMSFacebookLoginRest,
                                                                   Gson gson) {
        return new AppCMSFacebookLoginCall(appCMSFacebookLoginRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSGoogleLoginCall providesAppCMSGoogleLoginCall(AppCMSGoogleLoginRest appCMSGoogleLoginRest,
                                                               Gson gson) {
        return new AppCMSGoogleLoginCall(appCMSGoogleLoginRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSUserIdentityCall providesAppCMSUserIdentityCall(AppCMSUserIdentityRest appCMSUserIdentityRest) {
        return new AppCMSUserIdentityCall(appCMSUserIdentityRest);
    }

    @Provides
    @Singleton
    public AppCMSUpdateWatchHistoryCall providesAppCMSUpdateWatchHistoryCall(AppCMSUpdateWatchHistoryRest appCMSUpdateWatchHistoryRest) {
        return new AppCMSUpdateWatchHistoryCall(appCMSUpdateWatchHistoryRest);
    }

    @Provides
    @Singleton
    public AppCMSUserVideoStatusCall providesAppCMSUserVideoStatusCall(AppCMSUserVideoStatusRest appCMSUserVideoStatusRest) {
        return new AppCMSUserVideoStatusCall(appCMSUserVideoStatusRest);
    }

    @Provides
    @Singleton
    public AppCMSUserDownloadVideoStatusCall providesAppCMSUserDownloadVideoStatusCall() {
        return new AppCMSUserDownloadVideoStatusCall();
    }


    @Provides
    @Singleton
    public AppCMSSubscriptionPlanCall appCMSSubscriptionPlanCall(AppCMSSubscriptionPlanRest appCMSSubscriptionPlanRest,
                                                                 Gson gson) {
        return new AppCMSSubscriptionPlanCall(appCMSSubscriptionPlanRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSAnonymousAuthTokenCall appCMSAnonymousAuthTokenCall(AppCMSAnonymousAuthTokenRest appCMSAnonymousAuthTokenRest,
                                                                     Gson gson) {
        return new AppCMSAnonymousAuthTokenCall(appCMSAnonymousAuthTokenRest, gson);
    }

    @Provides
    @Singleton
    public GoogleRefreshTokenCall providesGoogleRefreshTokenCall(GoogleRefreshTokenRest googleRefreshTokenRest) {
        return new GoogleRefreshTokenCall(googleRefreshTokenRest);
    }

    @Provides
    @Singleton
    public GoogleCancelSubscriptionCall providesGoogleCancelSubscriptionCall(GoogleCancelSubscriptionRest googleCancelSubscriptionRest) {
        return new GoogleCancelSubscriptionCall(googleCancelSubscriptionRest);
    }

    @Provides
    @Singleton
    public AppCMSIPGeoLocatorCall providesAppCMSIPGeoLocatorCall(AppCMSIPGeoLocatorRest appCMSIPGeoLocatorRest) {
        return new AppCMSIPGeoLocatorCall(appCMSIPGeoLocatorRest);
    }

    @Provides
    @Singleton
    public AppCMSCCAvenueCall providesAppCMSCCAvenueCall(AppCMSCCAvenueRest appCMSCCAvenueRest) {
        return new AppCMSCCAvenueCall(appCMSCCAvenueRest);
    }

    @Provides
    @Singleton
    public AppCMSRestorePurchaseCall providesAppCMSRestorePurchaseCall(Gson gson,
                                                                       AppCMSRestorePurchaseRest appCMSRestorePurchaseRest) {
        return new AppCMSRestorePurchaseCall(gson, appCMSRestorePurchaseRest);
    }

    @Provides
    @Singleton
    public AppCMSAndroidModuleCall providesAppCMSAndroidModuleCall(AssetManager assetManager,
                                                                   Gson gson,
                                                                   AppCMSAndroidModuleRest appCMSAndroidModuleRest) {
        return new AppCMSAndroidModuleCall(assetManager,
                gson,
                appCMSAndroidModuleRest,
                storageDirectory);
    }

    @Provides
    @Singleton
    public AppCMSSignedURLCall providesAppCMSSignedURLCall(AppCMSSignedURLRest appCMSSignedURLRest) {
        return new AppCMSSignedURLCall(appCMSSignedURLRest);
    }

    @Provides
    @Singleton
    public Map<String, AppCMSUIKeyType> providesJsonValueKeyMap() {
        return jsonValueKeyMap;
    }

    @Provides
    @Singleton
    public Map<String, String> providesPageNameToActionMap() {
        return pageNameToActionMap;
    }

    @Provides
    @Singleton
    public Map<String, AppCMSPageUI> providesActionToPageMap() {
        return actionToPageMap;
    }

    @Provides
    @Singleton
    public Map<String, AppCMSPageAPI> providesActionToToPageAPIMap() {
        return actionToPageAPIMap;
    }

    @Provides
    @Singleton
    public Map<String, AppCMSActionType> providesActionToActionTypeMap() {
        return actionToActionTypeMap;
    }

    @Provides
    @Singleton
    public AppCMSSubscribeForLatestNewsRest appCMSSubscribeForLatestNewsRest(Retrofit retrofit) {
        return retrofit.create(AppCMSSubscribeForLatestNewsRest.class);
    }

    @Provides
    @Singleton
    public AppCMSSubscribeForLatestNewsCall appCMSSubscribeForLatestNewsCall(AppCMSSubscribeForLatestNewsRest appCMSSubscribeForLatestNewsRest, Gson gson) {
        return new AppCMSSubscribeForLatestNewsCall(appCMSSubscribeForLatestNewsRest, gson);
    }
}
