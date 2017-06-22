package com.viewlift.models.network.modules;

import android.content.Context;

import com.google.gson.Gson;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import com.viewlift.models.network.rest.AppCMSAndroidUIRest;
import com.viewlift.models.network.rest.AppCMSBeaconRest;
import com.viewlift.models.network.rest.AppCMSMainUICall;
import com.viewlift.models.network.rest.AppCMSMainUIRest;
import com.viewlift.models.network.rest.AppCMSPageUICall;
import com.viewlift.models.network.rest.AppCMSPageUIRest;
import com.viewlift.presenters.AppCMSActionType;

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
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/4/17.
 */

@Module
public class AppCMSUIModule {
    private final String baseUrl;
    private final File storageDirectory;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final Map<String, String> pageNameToActionMap;
    private final Map<String, AppCMSPageUI> actionToPageMap;
    private final Map<String, AppCMSPageAPI> actionToPageAPIMap;
    private final Map<String, AppCMSActionType> actionToActionTypeMap;

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
    }

    private void createJsonValueKeyMap(Context context) {
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_splashscreen_key),
                AppCMSUIKeyType.ANDROID_SPLASH_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_pagename_homepage_key),
                AppCMSUIKeyType.ANDROID_HOME_SCREEN_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_action_homepage_nav),
                AppCMSUIKeyType.ANDROID_HOME_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_action_movies_nav),
                AppCMSUIKeyType.ANDROID_MOVIES_NAV_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_button_key),
                AppCMSUIKeyType.PAGE_BUTTON_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_label_key),
                AppCMSUIKeyType.PAGE_LABEL_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_collection_grid_key),
                AppCMSUIKeyType.PAGE_COLLECTIONGRID_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_progress_view_key),
                AppCMSUIKeyType.PAGE_PROGRESS_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_VIEW_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_image_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_IMAGE_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_carousel_add_to_watchlist_key),
                AppCMSUIKeyType.PAGE_CAROUSEL_ADD_TO_WATCHLIST_KEY);
        jsonValueKeyMap.put(context.getString(R.string.app_cms_page_page_control_key),
                AppCMSUIKeyType.PAGE_PAGE_CONTROL_VIEW_KEY);
        jsonValueKeyMap.put( context.getString(R.string.app_cms_page_seperator_key),
                AppCMSUIKeyType.PAGE_SEPARATOR_VIEW_KEY);
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
        jsonValueKeyMap.put( context.getString(R.string.app_cms_page_text_alignment_center_key),
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
                context.getString(R.string.app_cms_action_splashpage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_homepage_key),
                context.getString(R.string.app_cms_action_homepage_key));
        this.pageNameToActionMap.put(context.getString(R.string.app_cms_pagename_videopage_key),
                context.getString(R.string.app_cms_action_videopage_key));
    }

    private void createActionToPageMap(Context context) {
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_splashpage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_homepage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_videopage_key), null);
        this.actionToPageMap.put(context.getString(R.string.app_cms_action_watchvideo_key), null);
    }

    private void createActionToPageAPIMap(Context context) {
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_splashpage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_homepage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_videopage_key), null);
        this.actionToPageAPIMap.put(context.getString(R.string.app_cms_action_watchvideo_key), null);
    }

    private void createActionToActionTypeMap(Context context) {
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_splashpage_key),
                AppCMSActionType.SPLASH_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_pagename_homepage_key),
                AppCMSActionType.HOME_PAGE);
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
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public File providesStorageDirectory() {
        return storageDirectory;
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(Gson gson) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
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
    public AppCMSBeaconRest providesAppCMSBeaconMessage(Retrofit retrofit) {
        return retrofit.create(AppCMSBeaconRest.class);
    }

    @Provides
    @Singleton
    public AppCMSMainUICall providesAppCMSMainUICall(AppCMSMainUIRest appCMSMainUIRest, Gson gson) {
        return new AppCMSMainUICall(appCMSMainUIRest,
                gson,
                storageDirectory);
    }

    @Provides
    @Singleton
    public AppCMSAndroidUICall providesAppCMSAndroidUICall(AppCMSAndroidUIRest appCMSAndroidUIRest, Gson gson) {
        return new AppCMSAndroidUICall(appCMSAndroidUIRest, gson, storageDirectory);
    }

    @Provides
    @Singleton
    public AppCMSPageUICall providesAppCMSPageUICall(AppCMSPageUIRest appCMSPageUIRest, Gson gson) {
        return new AppCMSPageUICall(appCMSPageUIRest, gson, storageDirectory);
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
