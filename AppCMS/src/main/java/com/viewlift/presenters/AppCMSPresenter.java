package com.viewlift.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Inject;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidUI;
import com.viewlift.models.data.appcms.ui.android.MetaPage;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.views.binders.AppCMSBinder;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.network.background.tasks.GetAppCMSAPIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSAndroidUIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSMainUIAsyncTask;
import com.viewlift.models.network.background.tasks.GetAppCMSPageUIAsyncTask;
import com.viewlift.models.network.rest.AppCMSAndroidUICall;
import com.viewlift.models.network.rest.AppCMSMainUICall;
import com.viewlift.models.network.rest.AppCMSPageAPICall;
import com.viewlift.models.network.rest.AppCMSPageUICall;
import com.viewlift.views.activity.AppCMSPageActivity;
import com.viewlift.views.activity.AppCMSErrorActivity;
import rx.functions.Action0;
import rx.functions.Action1;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/3/17.
 */

public class AppCMSPresenter {
    private static final String TAG = "AppCMSPresenter";

    public static final String PRESENTER_NAVIGATE_ACTION = "appcms_presenter_navigate_action";
    public static final String PRESENTER_CLOSE_SCREEN_ACTION = "appcms_presenter_close_action";

    private static final String LOGIN_SHARED_PREF_NAME = "login_pref";
    private static final String USER_ID_SHARED_PREF_NAME = "user_id_pref";

    private final AppCMSMainUICall appCMSMainUICall;
    private final AppCMSAndroidUICall appCMSAndroidUICall;
    private final AppCMSPageUICall appCMSPageUICall;
    private final AppCMSPageAPICall appCMSPageAPICall;
    private final Map<AppCMSUIKeyType, String> jsonValueKeyMap;
    private final Map<String, String> pageNameToActionMap;
    private final Map<String, AppCMSPageUI> actionToPageMap;
    private final Map<String, AppCMSPageAPI> actionToPageAPIMap;
    private final Map<String, AppCMSActionType> actionToActionTypeMap;

    private Activity currentActivity;
    private Navigation navigation;
    private boolean loadFromFile;
    private String apiBaseUrl;
    private String apiSiteName;
    private Queue<MetaPage> pagesToProcess;
    private Map<String, AppCMSPageUI> navigationPages;
    private Map<String, AppCMSPageAPI> navigationPageData;
    private Map<String, String> pageIdToPageAPIUrlMap;
    private Map<String, String> actionToPageAPIUrlMap;

    private static abstract class AppCMSPageAPIAction implements Action1<AppCMSPageAPI> {
        boolean appbarPresent;
        boolean fullscreen;
        public AppCMSPageAPIAction(boolean appbarPresent, boolean fullscreen) {
            this.appbarPresent = appbarPresent;
            this.fullscreen = fullscreen;
        }
    }

    @Inject
    public AppCMSPresenter(AppCMSMainUICall appCMSMainUICall,
                           AppCMSAndroidUICall appCMSAndroidUICall,
                           AppCMSPageUICall appCMSPageUICall,
                           AppCMSPageAPICall appCMSPageAPICall,
                           Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                           Map<String, String> pageNameToActionMap,
                           Map<String, AppCMSPageUI> actionToPageMap,
                           Map<String, AppCMSPageAPI> actionToPageAPIMap,
                           Map<String, AppCMSActionType> actionToActionTypeMap) {
        this.appCMSMainUICall = appCMSMainUICall;
        this.appCMSAndroidUICall = appCMSAndroidUICall;
        this.appCMSPageUICall = appCMSPageUICall;
        this.appCMSPageAPICall = appCMSPageAPICall;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.pageNameToActionMap = pageNameToActionMap;
        this.actionToPageMap = actionToPageMap;
        this.actionToPageAPIMap = actionToPageAPIMap;
        this.actionToActionTypeMap = actionToActionTypeMap;

        this.navigationPages = new HashMap<>();
        this.navigationPageData = new HashMap<>();
        this.pageIdToPageAPIUrlMap = new HashMap<>();
        this.actionToPageAPIUrlMap = new HashMap<>();
    }

    public void setCurrentActivity(Activity activity) {
        this.currentActivity = activity;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public boolean launchFilmAction(String filmPath, String action) {
        return false;
    }

    public boolean launchAction(final String action, @Nullable Bundle data) {
        Log.d(TAG, "Attempting to launch page for action: " + action);

        boolean result = false;
        if (currentActivity != null) {
            result = true;
            if (data != null &&
                    action.equals(currentActivity.getString(R.string.app_cms_action_initialize_key))) {
                String pageId = data.getString(currentActivity.getString(R.string.page_id));
                getAppCMSMain(currentActivity, pageId);
            } else {
                if (!actionToPageMap.containsKey(action) || actionToPageMap.get(action) == null) {
                    result = false;
                } else {
                    boolean appbarPresent = true;
                    boolean fullscreen = false;
                    switch (actionToActionTypeMap.get(action)) {
                        case SPLASH_PAGE:
                            appbarPresent = false;
                            fullscreen = false;
                            break;
                        case VIDEO_PAGE:
                            appbarPresent = false;
                            fullscreen = true;
                            break;
                        case HOME_PAGE:
                        default:
                            break;
                    }

                    final AppCMSPageUI appCMSPageUI = actionToPageMap.get(action);
                    AppCMSPageAPI currentAppCMSPageAPI = actionToPageAPIMap.get(action);
                    if (currentAppCMSPageAPI == null) {
                        getContent(apiBaseUrl,
                                actionToPageAPIUrlMap.get(action),
                                apiSiteName,
                                getPageId(appCMSPageUI),
                                new AppCMSPageAPIAction(appbarPresent, fullscreen) {
                                    @Override
                                    public void call(AppCMSPageAPI appCMSPageAPI) {
                                        actionToPageAPIMap.put(action, appCMSPageAPI);
                                        launchPageActivity(currentActivity,
                                                appCMSPageUI,
                                                appCMSPageAPI,
                                                getPageId(appCMSPageUI),
                                                loadFromFile,
                                                appbarPresent,
                                                fullscreen);
                                    }
                                });
                    } else {
                        launchPageActivity(currentActivity,
                                appCMSPageUI,
                                currentAppCMSPageAPI,
                                getPageId(appCMSPageUI),
                                loadFromFile,
                                appbarPresent,
                                fullscreen);
                    }
                }
            }
        }

        return result;
    }

    private void launchPageActivity(Activity activity,
                                    AppCMSPageUI appCMSPageUI,
                                    AppCMSPageAPI appCMSPageAPI,
                                    String pageID,
                                    boolean loadFromFile,
                                    boolean appbarPresent,
                                    boolean fullscreen) {
        Bundle args = new Bundle();
        AppCMSBinder appCMSBinder = new AppCMSBinder(appCMSPageUI,
                appCMSPageAPI,
                navigation,
                pageID,
                loadFromFile,
                appbarPresent,
                fullscreen,
                isUserLoggedIn(activity),
                jsonValueKeyMap);
        args.putBinder(activity.getString(R.string.app_cms_binder_key), appCMSBinder);
        Intent appCMSIntent = new Intent(activity, AppCMSPageActivity.class);
        appCMSIntent.putExtra(activity.getString(R.string.app_cms_bundle_key), args);

        activity.startActivity(appCMSIntent);
    }

    public boolean sendCloseOthersAction() {
        boolean result = false;
        if (currentActivity != null) {
            Intent closeOthersIntent = new Intent(AppCMSPresenter.PRESENTER_CLOSE_SCREEN_ACTION);
            closeOthersIntent.putExtra(currentActivity.getString(R.string.close_self_key), true);
            currentActivity.sendBroadcast(closeOthersIntent);
            result = true;
        }
        return result;
    }

    public void launchErrorActivity(Activity activity) {
        Intent errorIntent = new Intent(activity, AppCMSErrorActivity.class);
        activity.startActivity(errorIntent);
    }

    private void getAppCMSMain(final Activity activity,
                               final String siteId) {
        GetAppCMSMainUIAsyncTask.Params params = new GetAppCMSMainUIAsyncTask.Params.Builder()
                .context(currentActivity)
                .siteId(siteId)
                .build();
        new GetAppCMSMainUIAsyncTask(appCMSMainUICall, new Action1<JsonElement>() {
            @Override
            public void call(JsonElement main) {
                if (main == null) {
                    Log.e(TAG, "Error retrieving main.json");
                    launchErrorActivity(activity);
                } else if (TextUtils.isEmpty(main
                        .getAsJsonObject()
                        .get(jsonValueKeyMap.get(AppCMSUIKeyType.MAIN_ANDROID_KEY))
                        .getAsString())) {
                    Log.e(TAG, "AppCMS key for main not found");
                    launchErrorActivity(activity);
                } else if (TextUtils.isEmpty(main
                        .getAsJsonObject()
                        .get(jsonValueKeyMap.get(AppCMSUIKeyType.MAIN_API_BASE_URL))
                        .getAsString())) {
                    Log.e(TAG, "AppCMS key for API Base URL not found");
                    launchErrorActivity(activity);
                } else if (TextUtils.isEmpty(main
                        .getAsJsonObject()
                        .get(jsonValueKeyMap.get(AppCMSUIKeyType.MAIN_SITE_ID))
                        .getAsString())) {
                    Log.e(TAG, "AppCMS key for API Site ID not found");
                    launchErrorActivity(activity);
                } else {
                    String androidUrl = main
                            .getAsJsonObject()
                            .get(jsonValueKeyMap.get(AppCMSUIKeyType.MAIN_ANDROID_KEY))
                            .getAsString();
                    String version = main
                            .getAsJsonObject()
                            .get(jsonValueKeyMap.get(AppCMSUIKeyType.MAIN_VERSION_KEY))
                            .getAsString();
                    String oldVersion = main
                            .getAsJsonObject()
                            .get(jsonValueKeyMap.get(AppCMSUIKeyType.MAIN_OLD_VERSION_KEY))
                            .getAsString();
                    apiBaseUrl = main
                            .getAsJsonObject()
                            .get(jsonValueKeyMap.get(AppCMSUIKeyType.MAIN_API_BASE_URL))
                            .getAsString();
                    apiSiteName = main
                            .getAsJsonObject()
                            .get(jsonValueKeyMap.get(AppCMSUIKeyType.MAIN_SITE_ID))
                            .getAsString();
                    Log.d(TAG, "Version: " + version);
                    Log.d(TAG, "OldVersion: " + oldVersion);
                    loadFromFile = false;
                    getAppCMSAndroid(activity, androidUrl);
                }
            }
        }).execute(params);
    }

    private void getAppCMSAndroid(final Activity activity, String url) {
        GetAppCMSAndroidUIAsyncTask.Params params =
                new GetAppCMSAndroidUIAsyncTask.Params.Builder()
                    .url(url)
                    .loadFromFile(loadFromFile)
                    .build();
        Log.d(TAG, "Params: " + url + " " + loadFromFile);
        new GetAppCMSAndroidUIAsyncTask(appCMSAndroidUICall, new Action1<AppCMSAndroidUI>() {
            @Override
            public void call(final AppCMSAndroidUI appCMSAndroidUI) {
                if (appCMSAndroidUI == null ||
                        appCMSAndroidUI.getMetaPages() == null ||
                        appCMSAndroidUI.getMetaPages().size() < 1) {
                    Log.e(TAG, "AppCMS keys for pages for appCMSAndroidUI not found");
                    launchErrorActivity(activity);
                } else {
                    navigation = appCMSAndroidUI.getNavigation();
                    queueMetaPages(appCMSAndroidUI.getMetaPages());
                    final MetaPage firstPage = pagesToProcess.peek();
                    Log.d(TAG, "Processing meta pages queue");
                    processMetaPagesQueue(loadFromFile,
                            new Action0() {
                                @Override
                                public void call() {
                                    Log.d(TAG, "Launching first page: " + firstPage.getPageName());
                                    boolean launchSuccess =
                                            launchAction(pageNameToActionMap.get(firstPage.getPageName()),
                                                    null);
                                    if (!launchSuccess) {
                                        Log.e(TAG, "Failed to launch page: " + firstPage.getPageName());
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
            int pageToQueueIndex = -1;
            if (pageToQueueIndex < 0) {
                pageToQueueIndex = getHomePage(metaPageList);
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
                for (int i = 0; i < metaPageList.size(); i++) {
                    pagesToProcess.add(metaPageList.get(i));
                }
            }
        }
    }

    private void processMetaPagesQueue(final boolean loadFromFile,
                                       final Action0 onPagesFinishedAction) {
        final MetaPage metaPage = pagesToProcess.remove();
        Log.d(TAG, "Processing meta page: " +
                metaPage.getPageName() + ": " +
                metaPage.getPageId() + " " +
                metaPage.getPageUI() + " " +
                metaPage.getPageAPI());
        getAppCMSPage(metaPage.getPageUI(),
                new Action1<AppCMSPageUI>() {
                    @Override
                    public void call(AppCMSPageUI appCMSPageUI) {
                        navigationPages.put(metaPage.getPageId(), appCMSPageUI);
                        pageIdToPageAPIUrlMap.put(metaPage.getPageId(), metaPage.getPageAPI());
                        String action = pageNameToActionMap.get(metaPage.getPageName());
                        if (action != null && actionToPageMap.containsKey(action)) {
                            actionToPageMap.put(action, appCMSPageUI);
                            actionToPageAPIUrlMap.put(action, metaPage.getPageAPI());
                            Log.d(TAG, "Action: " + action + " PageAPIURL: " + metaPage.getPageAPI());
                        }
                        if (pagesToProcess.size() > 0) {
                            processMetaPagesQueue(loadFromFile,
                                    onPagesFinishedAction);
                        } else {
                            onPagesFinishedAction.call();
                        }
                    }
                },
                loadFromFile);
    }

    public void getContent(String baseUrl,
                           String endPoint,
                           String siteId,
                           String pageId,
                           Action1<AppCMSPageAPI> readyAction) {
        GetAppCMSAPIAsyncTask.Params params = new GetAppCMSAPIAsyncTask.Params.Builder()
                .context(currentActivity)
                .baseUrl(baseUrl)
                .endpoint(endPoint)
                .siteId(siteId)
                .pageId(pageId)
                .build();
        new GetAppCMSAPIAsyncTask(appCMSPageAPICall, readyAction).execute(params);
    }

    public boolean isUserLoggedIn(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
        return sharedPrefs.getString(USER_ID_SHARED_PREF_NAME, null) != null;
    }

    public boolean setLoggedInUser(Context context, String userId) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
        return sharedPrefs.edit().putString(USER_ID_SHARED_PREF_NAME, userId).commit();
    }

    private int getSoftwallPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (metaPageList.get(i)
                    .getPageName()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.ANDROID_SPLASH_SCREEN_KEY))) {
                return i;
            }
        }
        return -1;
    }

    private int getHomePage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (metaPageList.get(i)
                    .getPageName()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.ANDROID_HOME_SCREEN_KEY))) {
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
}
