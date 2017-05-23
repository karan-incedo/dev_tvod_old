package air.com.snagfilms.models.network.modules;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import air.com.snagfilms.models.data.appcms.AppCMSActionType;
import air.com.snagfilms.models.data.appcms.AppCMSKeyType;
import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.models.network.rest.AppCMSAndroidUI;
import air.com.snagfilms.models.network.rest.AppCMSAndroidUICall;
import air.com.snagfilms.models.network.rest.AppCMSMainUI;
import air.com.snagfilms.models.network.rest.AppCMSMainUICall;
import air.com.snagfilms.models.network.rest.AppCMSPageUI;
import air.com.snagfilms.models.network.rest.AppCMSPageUICall;
import dagger.Module;
import dagger.Provides;
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
    private final Map<AppCMSKeyType, String> jsonValueKeyMap;
    private final Map<String, String> pageNameToActionMap;
    private final Map<String, Page> actionToPageMap;
    private final Map<String, AppCMSActionType> actionToActionTypeMap;

    public AppCMSUIModule(Context context) {
        this.baseUrl = context.getString(R.string.app_cms_api_baseurl);
        this.storageDirectory = context.getFilesDir();

        this.jsonValueKeyMap = new HashMap<>();
        createJsonValueKeyMap(context);

        this.pageNameToActionMap = new HashMap<>();
        createPageNameToActionMap(context);

        this.actionToPageMap = new HashMap<>();
        createActionToPageMap(context);

        this.actionToActionTypeMap = new HashMap<>();
        createActionToActionTypeMap(context);
    }

    private void createJsonValueKeyMap(Context context) {
        this.jsonValueKeyMap.put(AppCMSKeyType.MAIN_VERSION_KEY,
                context.getString(R.string.app_cms_main_version_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.MAIN_OLD_VERSION_KEY,
                context.getString(R.string.app_cms_main_old_version_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.MAIN_ANDROID_KEY,
                context.getString(R.string.app_cms_main_android_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.ANDROID_SPLASH_SCREEN_KEY,
                context.getString(R.string.app_cms_pagename_splashscreen_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.ANDROID_HOME_SCREEN_KEY,
                context.getString(R.string.app_cms_pagename_homepage_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.PAGE_BUTTON_KEY,
                context.getString(R.string.app_cms_page_button_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.PAGE_LABEL_KEY,
                context.getString(R.string.app_cms_page_label_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.PAGE_COLLECTIONGRID_KEY,
                context.getString(R.string.app_cms_page_collection_grid_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.PAGE_IMAGE_KEY,
                context.getString(R.string.app_cms_page_image_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.PAGE_BG_KEY,
                context.getString(R.string.app_cms_page_bg_key));
        this.jsonValueKeyMap.put(AppCMSKeyType.PAGE_LOGO_KEY,
                context.getString(R.string.app_cms_page_logo_key));
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
    }

    private void createActionToActionTypeMap(Context context) {
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_splashpage_key),
                AppCMSActionType.SPLASH_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_pagename_homepage_key),
                AppCMSActionType.HOME_PAGE);
        actionToActionTypeMap.put(context.getString(R.string.app_cms_action_videopage_key),
                AppCMSActionType.VIDEO_PAGE);
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public Retrofit providesRetrofit(Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .build();
    }

    @Provides
    @Singleton
    public AppCMSMainUI providesAppCMSMainAPI(Retrofit retrofit) {
        return retrofit.create(AppCMSMainUI.class);
    }

    @Provides
    @Singleton
    public AppCMSAndroidUI providesAppCMSAndroidAPI(Retrofit retrofit) {
        return retrofit.create(AppCMSAndroidUI.class);
    }

    @Provides
    @Singleton
    public AppCMSPageUI providesAppCMSPageAPI(Retrofit retrofit) {
        return retrofit.create(AppCMSPageUI.class);
    }

    @Provides
    @Singleton
    public AppCMSMainUICall providesAppCMSMainCall(AppCMSMainUI appCMSAPI, Gson gson) {
        return new AppCMSMainUICall(appCMSAPI,
                gson,
                storageDirectory,
                jsonValueKeyMap.get(AppCMSKeyType.MAIN_VERSION_KEY),
                jsonValueKeyMap.get(AppCMSKeyType.MAIN_OLD_VERSION_KEY));
    }

    @Provides
    @Singleton
    public AppCMSAndroidUICall providesAppCMSAndroidCall(AppCMSAndroidUI appCMSAPI, Gson gson) {
        return new AppCMSAndroidUICall(appCMSAPI, gson, storageDirectory);
    }

    @Provides
    @Singleton
    public AppCMSPageUICall providesAppCMSPageCall(AppCMSPageUI appCMSAPI, Gson gson) {
        return new AppCMSPageUICall(appCMSAPI, gson, storageDirectory);
    }

    @Provides
    @Singleton
    public Map<AppCMSKeyType, String> providesJsonValueKeyMap() {
        return jsonValueKeyMap;
    }

    @Provides
    @Singleton
    public Map<String, String> providesPageNameToActionMap() {
        return pageNameToActionMap;
    }

    @Provides
    @Singleton
    public Map<String, Page> providesActionToPageMap() {
        return actionToPageMap;
    }

    @Provides
    @Singleton
    public Map<String, AppCMSActionType> providesActionToActionTypeMap() {
        return actionToActionTypeMap;
    }
}
