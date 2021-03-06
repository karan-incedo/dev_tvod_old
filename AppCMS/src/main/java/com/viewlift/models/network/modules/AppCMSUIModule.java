package com.viewlift.models.network.modules;

/**
 * Created by viewlift on 5/4/17.
 */

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viewlift.R;
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
import com.viewlift.models.network.rest.AppCMSBeaconRest;
import com.viewlift.models.network.rest.AppCMSCCAvenueCall;
import com.viewlift.models.network.rest.AppCMSCCAvenueRest;
import com.viewlift.models.network.rest.AppCMSDeleteHistoryRest;
import com.viewlift.models.network.rest.AppCMSFacebookLoginCall;
import com.viewlift.models.network.rest.AppCMSFacebookLoginRest;
import com.viewlift.models.network.rest.AppCMSGoogleLoginCall;
import com.viewlift.models.network.rest.AppCMSGoogleLoginRest;
import com.viewlift.models.network.rest.AppCMSHistoryCall;
import com.viewlift.models.network.rest.AppCMSHistoryRest;
import com.viewlift.models.network.rest.AppCMSIPGeoLocatorCall;
import com.viewlift.models.network.rest.AppCMSIPGeoLocatorRest;
import com.viewlift.models.network.rest.AppCMSMainUICall;
import com.viewlift.models.network.rest.AppCMSMainUIRest;
import com.viewlift.models.network.rest.AppCMSPageUICall;
import com.viewlift.models.network.rest.AppCMSPageUIRest;
import com.viewlift.models.network.rest.AppCMSRefreshIdentityCall;
import com.viewlift.models.network.rest.AppCMSRefreshIdentityRest;
import com.viewlift.models.network.rest.AppCMSResetPasswordCall;
import com.viewlift.models.network.rest.AppCMSResetPasswordRest;
import com.viewlift.models.network.rest.AppCMSRestorePurchaseCall;
import com.viewlift.models.network.rest.AppCMSRestorePurchaseRest;
import com.viewlift.models.network.rest.AppCMSSignInCall;
import com.viewlift.models.network.rest.AppCMSSignInRest;
import com.viewlift.models.network.rest.AppCMSSignedURLCall;
import com.viewlift.models.network.rest.AppCMSSignedURLRest;
import com.viewlift.models.network.rest.AppCMSSubscriptionPlanCall;
import com.viewlift.models.network.rest.AppCMSSubscriptionPlanRest;
import com.viewlift.models.network.rest.AppCMSSubscriptionRest;
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
import com.viewlift.models.network.rest.UANamedUserEventCall;
import com.viewlift.models.network.rest.UANamedUserEventRest;
import com.viewlift.presenters.AppCMSActionType;
import com.viewlift.presenters.UrbanAirshipEventPresenter;
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

    public AppCMSUIModule(Context context) {
        this.baseUrl = context.getString(R.string.app_cms_baseurl);
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

    private void createJsonValueKeyMap(Context context) {
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
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_homescreen_key),
                AppCMSUIKeyType.ANDROID_HOME_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_moviesscreen_key),
                AppCMSUIKeyType.ANDROID_MOVIES_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_showsscreen_key),
                AppCMSUIKeyType.ANDROID_SHOWS_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_subscriptionscreen_key),
                AppCMSUIKeyType.ANDROID_SUBSCRIPTION_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_historyscreen_key),
                AppCMSUIKeyType.ANDROID_HISTORY_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_watchlist_navigation_title),
                AppCMSUIKeyType.ANDROID_WATCHLIST_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_watchlistscreen_key),
                AppCMSUIKeyType.ANDROID_WATCHLIST_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_download_page_title),
                AppCMSUIKeyType.ANDROID_DOWNLOAD_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_history_navigation_title),
                AppCMSUIKeyType.ANDROID_HISTORY_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_settings_page_title_text),
                AppCMSUIKeyType.ANDROID_SETTINGS_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_button_switch_key),
                AppCMSUIKeyType.PAGE_BUTTON_SWITCH_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_button_key),
                AppCMSUIKeyType.PAGE_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_label_key),
                AppCMSUIKeyType.PAGE_LABEL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_collection_grid_key),
                AppCMSUIKeyType.PAGE_COLLECTIONGRID_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_table_view_key),
                AppCMSUIKeyType.PAGE_TABLE_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_progress_view_key),
                AppCMSUIKeyType.PAGE_PROGRESS_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_player_key),
                AppCMSUIKeyType.PAGE_VIDEO_PLAYER_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_image_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY);
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
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_watchnow_key),
                AppCMSUIKeyType.PAGE_WATCH_VIDEO_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_play_image_key),
                AppCMSUIKeyType.PAGE_PLAY_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_play_live_image_key),
                AppCMSUIKeyType.PAGE_PLAY_LIVE_IMAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tray_title_key),
                AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_view_all_key),
                AppCMSUIKeyType.PAGE_VIEW_ALL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_image_key),
                AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_badge_image_key),
                AppCMSUIKeyType.PAGE_BADGE_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_title_key),
                AppCMSUIKeyType.PAGE_THUMBNAIL_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_text_alignment_center_key),
                AppCMSUIKeyType.PAGE_TEXTALIGNMENT_CENTER_KEY);
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
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_font_family_key),
                AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY);
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

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_resume_watching_key),
                AppCMSUIKeyType.PAGE_RESUME_WATCHING_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_percentage_watched_key),
                AppCMSUIKeyType.PAGE_PERCENTAGE_WATCHED_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_authentication_module),
                AppCMSUIKeyType.PAGE_AUTHENTICATION_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_description_key),
                AppCMSUIKeyType.PAGE_API_DESCRIPTION);
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

        jsonValueKeyMap.put(context.getString(R.string.app_cms_closed_captions_toggle_button_key),
                AppCMSUIKeyType.PAGE_CLOSED_CAPTIONS_TOGGLE_BUTTON_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_user_management_autoplay_text_key),
                AppCMSUIKeyType.PAGE_USER_MANAGEMENT_AUTOPLAY_TEXT_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_title_key),
                AppCMSUIKeyType.PAGE_PLAN_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_priceinfo_key),
                AppCMSUIKeyType.PAGE_PLAN_PRICEINFO_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_bestvalue_key),
                AppCMSUIKeyType.PAGE_PLAN_BESTVALUE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_purchase_button_key),
                AppCMSUIKeyType.PAGE_PLAN_PURCHASE_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_plan_meta_dataview_key),
                AppCMSUIKeyType.PAGE_PLAN_META_DATA_VIEW_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_history_module_key),
                AppCMSUIKeyType.PAGE_HISTORY_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_module_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_module_key),
                AppCMSUIKeyType.PAGE_DOWNLOAD_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_continue_watching_module_key),
                AppCMSUIKeyType.PAGE_CONTINUE_WATCHING_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_settings_component_key),
                AppCMSUIKeyType.PAGE_SETTINGS_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_user_management_download_settings_key),
                AppCMSUIKeyType.PAGE_USER_MANAGEMENT_DOWNLOADS_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_duration_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_duration_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_download_duration_key),
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

        jsonValueKeyMap.put(context.getString(R.string.app_cms_signup_footer_label_key),
                AppCMSUIKeyType.PAGE_SIGNUP_FOOTER_LABEL_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_api_history_module_key),
                AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_module_showdetail_key),
                AppCMSUIKeyType.PAGE_API_SHOWDETAIL_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_show_detail_module_key),
                AppCMSUIKeyType.PAGE_API_SHOWDETAIL_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_subscription_page_key),
                AppCMSUIKeyType.PAGE_SUBSCRIPTION_PAGE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_subscription_selectionplan_02_key),
                AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_subscription_imagetextrow_key),
                AppCMSUIKeyType.PAGE_SUBSCRIPTION_IMAGEROW_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_season_tray_module_key),
                AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_module_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_player_module_key),
                AppCMSUIKeyType.PAGE_VIDEO_PLAYER_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tray_module_key),
                AppCMSUIKeyType.PAGE_TRAY_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_grid_module_key),
                AppCMSUIKeyType.PAGE_GRID_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_player_with_info_key),
                AppCMSUIKeyType.PAGE_VIDEO_DETAILS_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_video_detail_module_key),
                AppCMSUIKeyType.PAGE_VIDEO_DETAILS_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_login_component_key),
                AppCMSUIKeyType.PAGE_LOGIN_COMPONENT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_signup_component_key),
                AppCMSUIKeyType.PAGE_SIGNUP_COMPONENT_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_removeall_key),
                AppCMSUIKeyType.PAGE_REMOVEALL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_image_key),
                AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_show_image_video_key),
                AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY);
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
                AppCMSUIKeyType.PAGE_VIDEO_SUBTITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_share_key),
                AppCMSUIKeyType.PAGE_VIDEO_SHARE_KEY);
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
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_title_key),
                AppCMSUIKeyType.PAGE_API_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_title_key),
                AppCMSUIKeyType.PAGE_API_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_title_key),
                AppCMSUIKeyType.PAGE_API_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_show_title_key),
                AppCMSUIKeyType.PAGE_API_TITLE);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_description_key),
                AppCMSUIKeyType.PAGE_API_DESCRIPTION);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_thumbnail_url_key),
                AppCMSUIKeyType.PAGE_API_THUMBNAIL_URL);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_header_view),
                AppCMSUIKeyType.PAGE_HEADER_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_videodetail_header_view),
                AppCMSUIKeyType.PAGE_VIDEO_DETAIL_HEADER_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_autoplay_module_key),
                AppCMSUIKeyType.PAGE_AUTOPLAY_MODULE_KEY);
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
    }

    private void createPageNameToActionMap(Context context) {
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_splashscreen_key),
                context.getString(R.string.app_cms_action_authpage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_homescreen_key),
                context.getString(R.string.app_cms_action_homepage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_historyscreen_key),
                context.getString(R.string.app_cms_action_historypage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_watchlistscreen_key),
                context.getString(R.string.app_cms_action_watchlistpage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_videoscreen_key),
                context.getString(R.string.app_cms_action_detailvideopage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_showscreen_key),
                context.getString(R.string.app_cms_action_showvideopage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_page_name_forgotpassword),
                context.getString(R.string.app_cms_action_forgotpassword_key));
    }

    private void createActionToPageMap(Context context) {
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_authpage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_homepage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_historypage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_videopage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_detailvideopage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_watchvideo_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_watchlistpage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_showvideopage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_forgotpassword_key), null);
    }

    private void createActionToPageAPIMap(Context context) {
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_authpage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_homepage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_videopage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_watchvideo_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_showvideopage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_watchvideo_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_forgotpassword_key), null);
    }

    private void createActionToActionTypeMap(Context context) {
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

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_videopage_key),
                AppCMSActionType.PLAY_VIDEO_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_detailvideopage_key),
                AppCMSActionType.VIDEO_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_draggablevideopage_key),
                AppCMSActionType.DRAGGABLE_VIDEO_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_showvideopage_key),
                AppCMSActionType.SHOW_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_watchvideo_key),
                AppCMSActionType.PLAY_VIDEO_PAGE);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_watchtrailervideo_key),
                AppCMSActionType.WATCH_TRAILER);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_share_key),
                AppCMSActionType.SHARE);

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

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_managesubscription_key),
                AppCMSActionType.MANAGE_SUBSCRIPTION);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_change_download_quality_key),
                AppCMSActionType.CHANGE_DOWNLOAD_QUALITY);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_open_option_dialog),
                AppCMSActionType.OPEN_OPTION_DIALOG);

        actionToActionTypeMap.put(context.getString(R.string.app_cms_banner_detail_button_action_key),
                AppCMSActionType.BANNER_DETAIL_CLICK);
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
    public AppCMSHistoryRest providesAppCMSHistoryRest(Retrofit retrofit) {
        return retrofit.create(AppCMSHistoryRest.class);
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
    public AppCMSHistoryCall providesAppCMSHistoryCall(AppCMSHistoryRest appCMSHistoryRest, Gson gson) {
        return new AppCMSHistoryCall(appCMSHistoryRest, gson);
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
}
