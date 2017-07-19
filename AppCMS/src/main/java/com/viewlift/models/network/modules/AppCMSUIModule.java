package com.viewlift.models.network.modules;

/**
 * Created by viewlift on 5/4/17.
 */

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.network.rest.AppCMSAddToWatchlistCall;
import com.viewlift.models.network.rest.AppCMSAddToWatchlistRest;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import com.viewlift.models.network.rest.AppCMSAndroidUIRest;
import com.viewlift.models.network.rest.AppCMSBeaconRest;
import com.viewlift.models.network.rest.AppCMSDeleteHistoryRest;
import com.viewlift.models.network.rest.AppCMSFacebookLoginCall;
import com.viewlift.models.network.rest.AppCMSFacebookLoginRest;
import com.viewlift.models.network.rest.AppCMSHistoryCall;
import com.viewlift.models.network.rest.AppCMSHistoryRest;
import com.viewlift.models.network.rest.AppCMSMainUICall;
import com.viewlift.models.network.rest.AppCMSMainUIRest;
import com.viewlift.models.network.rest.AppCMSPageUICall;
import com.viewlift.models.network.rest.AppCMSPageUIRest;
import com.viewlift.models.network.rest.AppCMSRefreshIdentityCall;
import com.viewlift.models.network.rest.AppCMSRefreshIdentityRest;
import com.viewlift.models.network.rest.AppCMSResetPasswordCall;
import com.viewlift.models.network.rest.AppCMSResetPasswordRest;
import com.viewlift.models.network.rest.AppCMSSignInCall;
import com.viewlift.models.network.rest.AppCMSSignInRest;
import com.viewlift.models.network.rest.AppCMSSubscriptionPlanCall;
import com.viewlift.models.network.rest.AppCMSSubscriptionPlanRest;
import com.viewlift.models.network.rest.AppCMSSubscriptionRest;
import com.viewlift.models.network.rest.AppCMSUpdateWatchHistoryCall;
import com.viewlift.models.network.rest.AppCMSUpdateWatchHistoryRest;
import com.viewlift.models.network.rest.AppCMSUserIdentityCall;
import com.viewlift.models.network.rest.AppCMSUserIdentityRest;
import com.viewlift.models.network.rest.AppCMSUserVideoStatusCall;
import com.viewlift.models.network.rest.AppCMSUserVideoStatusRest;
import com.viewlift.models.network.rest.AppCMSWatchlistCall;
import com.viewlift.models.network.rest.AppCMSWatchlistRest;
import com.viewlift.presenters.AppCMSActionType;
import com.viewlift.stag.generated.Stag;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
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
                context.getResources().getInteger(R.integer.app_cms_unknownhostexception_connection_timeout_msec);
    }

    private void createJsonValueKeyMap(Context context) {
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_authscreen_key),
                AppCMSUIKeyType.ANDROID_AUTH_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_splashscreen_key),
                AppCMSUIKeyType.ANDROID_SPLASH_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_homescreen_key),
                AppCMSUIKeyType.ANDROID_HOME_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_historyscreen_key),
                AppCMSUIKeyType.ANDROID_HISTORY_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_watchlist_navigation_title),
                AppCMSUIKeyType.ANDROID_WATCHLIST_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_history_navigation_title),
                AppCMSUIKeyType.ANDROID_HISTORY_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_settings_page_title_text),
                AppCMSUIKeyType.ANDROID_SETTINGS_NAV_KEY);
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
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_image_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_add_to_watchlist_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_ADD_TO_WATCHLIST_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_add_to_watchlist_key),
                AppCMSUIKeyType.PAGE_ADD_TO_WATCHLIST_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_page_control_key),
                AppCMSUIKeyType.PAGE_PAGE_CONTROL_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_seperator_key),
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
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_watchnow_key),
                AppCMSUIKeyType.PAGE_WATCH_VIDEO_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_play_image_key),
                AppCMSUIKeyType.PAGE_PLAY_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_tray_title_key),
                AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_image_key),
                AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_thumbnail_title_key),
                AppCMSUIKeyType.PAGE_THUMBNAIL_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_text_alignment_center_key),
                AppCMSUIKeyType.PAGE_TEXTALIGNMENT_CENTER_KEY);
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

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_history_module_key),
                AppCMSUIKeyType.PAGE_HISTORY_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_module_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_continue_watching_module_key),
                AppCMSUIKeyType.PAGE_CONTINUE_WATCHING_MODULE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_duration_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_DURATION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_description_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_DESCRIPTION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_watchlist_title_key),
                AppCMSUIKeyType.PAGE_WATCHLIST_TITLE_KEY);

        jsonValueKeyMap.put(context.getString(R.string.app_cms_api_history_module_key),
                AppCMSUIKeyType.PAGE_API_HISTORY_MODULE_KEY);

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
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_play_button_key),
                AppCMSUIKeyType.PAGE_VIDEO_PLAY_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_description_key),
                AppCMSUIKeyType.PAGE_VIDEO_DESCRIPTION_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_title_key),
                AppCMSUIKeyType.PAGE_VIDEO_TITLE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_video_subtitle_key),
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
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_description_key),
                AppCMSUIKeyType.PAGE_API_DESCRIPTION);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_api_thumbnail_url_key),
                AppCMSUIKeyType.PAGE_API_THUMBNAIL_URL);
        jsonValueKeyMap.put("", AppCMSUIKeyType.PAGE_EMPTY_KEY);
        jsonValueKeyMap.put(null, AppCMSUIKeyType.PAGE_NULL_KEY);
    }

    private void createPageNameToActionMap(Context context) {
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_splashscreen_key),
                context.getString(R.string.app_cms_action_authpage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_homescreen_key),
                context.getString(R.string.app_cms_action_homepage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_historyscreen_key),
                context.getString(R.string.app_cms_action_historypage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_videoscreen_key),
                context.getString(R.string.app_cms_action_videopage_key));
    }

    private void createActionToPageMap(Context context) {
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_authpage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_homepage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_historypage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_videopage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_watchvideo_key), null);
    }

    private void createActionToPageAPIMap(Context context) {
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_authpage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_homepage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_videopage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_watchvideo_key), null);
    }

    private void createActionToActionTypeMap(Context context) {
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_authpage_key),
                AppCMSActionType.SPLASH_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_authpage_key),
                AppCMSActionType.AUTH_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_pagename_homescreen_key),
                AppCMSActionType.HOME_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_historypage_key),
                AppCMSActionType.HISTORY_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_videopage_key),
                AppCMSActionType.VIDEO_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_watchvideo_key),
                AppCMSActionType.PLAY_VIDEO_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_watchtrailervideo_key),
                AppCMSActionType.WATCH_TRAILER);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_share_key),
                AppCMSActionType.SHARE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_close_key),
                AppCMSActionType.CLOSE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_login_key),
                AppCMSActionType.LOGIN);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_forgotpassword_key),
                AppCMSActionType.FORGOT_PASSWORD);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_forgotpassword_key),
                AppCMSActionType.LOGIN_GOOGLE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_loginfacebook_key),
                AppCMSActionType.LOGIN_FACEBOOK);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_signup_key),
                AppCMSActionType.SIGNUP);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_logout_key),
                AppCMSActionType.LOGOUT);
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
    public AppCMSResetPasswordCall providesAppCMSPasswordCall(AppCMSResetPasswordRest appCMSResetPasswordRest) {
        return new AppCMSResetPasswordCall(appCMSResetPasswordRest);
    }

    @Provides
    @Singleton
    public AppCMSFacebookLoginCall providesAppCMSFacebookLoginCall(AppCMSFacebookLoginRest appCMSFacebookLoginRest) {
        return new AppCMSFacebookLoginCall(appCMSFacebookLoginRest);
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
    public AppCMSAddToWatchlistCall providesAppCMSAddToWatchlistCall(AppCMSAddToWatchlistRest appCMSAddToWatchlistRest,
                                                                     Gson gson) {
        return new AppCMSAddToWatchlistCall(appCMSAddToWatchlistRest, gson);
    }

    @Provides
    @Singleton
    public AppCMSSubscriptionPlanCall appCMSSubscriptionPlanCall(AppCMSSubscriptionPlanRest appCMSSubscriptionPlanRest,
                                                                 Gson gson) {
        return new AppCMSSubscriptionPlanCall(appCMSSubscriptionPlanRest, gson);
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
