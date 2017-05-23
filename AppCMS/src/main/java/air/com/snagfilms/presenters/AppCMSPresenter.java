package air.com.snagfilms.presenters;

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

import air.com.snagfilms.models.data.appcms.api.AppCMSPageAPI;
import air.com.snagfilms.models.data.appcms.ui.AppCMSUIKeyType;
import air.com.snagfilms.models.data.appcms.ui.android.AppCMSAndroidUI;
import air.com.snagfilms.models.data.appcms.ui.android.MetaPage;
import air.com.snagfilms.models.data.appcms.ui.android.Navigation;
import air.com.snagfilms.models.data.appcms.ui.AppCMSBinder;
import air.com.snagfilms.models.data.appcms.ui.page.AppCMSPageUI;
import air.com.snagfilms.models.network.background.tasks.GetAppCMSAPIAsyncTask;
import air.com.snagfilms.models.network.background.tasks.GetAppCMSAndroidUIAsyncTask;
import air.com.snagfilms.models.network.background.tasks.GetAppCMSMainUIAsyncTask;
import air.com.snagfilms.models.network.background.tasks.GetAppCMSPageUIAsyncTask;
import air.com.snagfilms.models.network.rest.AppCMSAndroidUICall;
import air.com.snagfilms.models.network.rest.AppCMSMainUICall;
import air.com.snagfilms.models.network.rest.AppCMSPageAPICall;
import air.com.snagfilms.models.network.rest.AppCMSPageUICall;
import air.com.snagfilms.views.activity.AppCMSPageActivity;
import air.com.snagfilms.views.activity.AppCMSErrorActivity;
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

    private Activity currentActivity;

    private Navigation navigation;

    private boolean loadFromFile;

    private final AppCMSMainUICall appCMSMainUICall;

    private final AppCMSAndroidUICall appCMSAndroidUICall;

    private final AppCMSPageUICall appCMSPageUICall;

    private final AppCMSPageAPICall appCMSPageAPICall;

    private Queue<MetaPage> pagesToProcess;

    private Map<String, AppCMSPageUI> navigationPages;

    private final Map<AppCMSUIKeyType, String> jsonValueKeyMap;

    private final Map<String, String> pageNameToActionMap;

    private final Map<String, AppCMSPageUI> actionToPageMap;

    private final Map<String, AppCMSActionType> actionToActionTypeMap;

    @Inject
    public AppCMSPresenter(AppCMSMainUICall appCMSMainUICall,
                           AppCMSAndroidUICall appCMSAndroidUICall,
                           AppCMSPageUICall appCMSPageUICall,
                           AppCMSPageAPICall appCMSPageAPICall,
                           Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                           Map<String, String> pageNameToActionMap,
                           Map<String, AppCMSPageUI> actionToPageMap,
                           Map<String, AppCMSActionType> actionToActionTypeMap) {
        this.appCMSMainUICall = appCMSMainUICall;
        this.appCMSAndroidUICall = appCMSAndroidUICall;
        this.appCMSPageUICall = appCMSPageUICall;
        this.appCMSPageAPICall = appCMSPageAPICall;
        this.navigationPages = new HashMap<>();
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.pageNameToActionMap = pageNameToActionMap;
        this.actionToPageMap = actionToPageMap;
        this.actionToActionTypeMap = actionToActionTypeMap;
    }

    public void setCurrentActivity(Activity activity) {
        this.currentActivity = activity;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public boolean launchAction(String action, @Nullable Bundle data) {
        Log.d(TAG, "Attempting to launch page for action: " + action);

        boolean result = false;
        if (currentActivity != null) {
            result = true;
            if (data != null &&
                    action.equals(currentActivity.getString(R.string.app_cms_action_initialize_key))) {
                String pageId = data.getString(currentActivity.getString(R.string.page_id));
                boolean userLoggedIn = data.getBoolean(currentActivity.getString(R.string.is_logged_in_key));
                getAppCMSMain(currentActivity, pageId, userLoggedIn);
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
                    AppCMSPageUI appCMSPageUI = actionToPageMap.get(action);
                    launchPageActivity(currentActivity,
                            appCMSPageUI,
                            getPageId(appCMSPageUI),
                            loadFromFile,
                            appbarPresent,
                            fullscreen);
                }
            }
        }

        return result;
    }

    private void launchPageActivity(Activity activity,
                                    AppCMSPageUI appCMSPageUI,
                                    String pageID,
                                    boolean loadFromFile,
                                    boolean appbarPresent,
                                    boolean fullscreen) {
        Bundle args = new Bundle();
        AppCMSBinder appCMSBinder = new AppCMSBinder(appCMSPageUI,
                navigation,
                loadFromFile,
                pageID,
                appbarPresent,
                fullscreen,
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
                               String siteId,
                               final boolean userLoggedIn) {
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
                    Log.e(TAG, "AppCMS keys for main not found");
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
                    loadFromFile = version.equals(oldVersion);
                    getAppCMSAndroid(activity,
                            androidUrl,
                            userLoggedIn);
                }
            }
        }).execute(params);
    }

    private void getAppCMSAndroid(final Activity activity,
                                  String url,
                                  final boolean userLoggedIn) {
        GetAppCMSAndroidUIAsyncTask.Params params =
                new GetAppCMSAndroidUIAsyncTask.Params.Builder()
                    .url(url)
                    .loadFromFile(loadFromFile)
                    .build();
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
                    queueMetaPages(appCMSAndroidUI.getMetaPages(), userLoggedIn);
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

    private void queueMetaPages(List<MetaPage> metaPageList, boolean userLoggedIn) {
        if (pagesToProcess == null) {
            pagesToProcess = new ConcurrentLinkedQueue<>();
        }
        if (metaPageList.size() > 0) {
            int pageToQueueIndex = !userLoggedIn ? getSoftwallPage(metaPageList) : -1;
            if (pageToQueueIndex < 0) {
                pageToQueueIndex = getHomePage(metaPageList);
            }
            if (pageToQueueIndex >= 0) {
                pagesToProcess.add(metaPageList.get(pageToQueueIndex));
                metaPageList.remove(pageToQueueIndex);
                queueMetaPages(metaPageList, userLoggedIn);
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
        Log.d(TAG, "Processing meta page: " + metaPage.getPageName() + ": " + metaPage.getPageId());
        getAppCMSPage(metaPage.getPageUI(),
                new Action1<AppCMSPageUI>() {
                    @Override
                    public void call(AppCMSPageUI appCMSPageUI) {
                        navigationPages.put(metaPage.getPageId(), appCMSPageUI);
                        String action = pageNameToActionMap.get(metaPage.getPageName());
                        if (action != null && actionToPageMap.containsKey(action)) {
                            actionToPageMap.put(action, appCMSPageUI);
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

    public void getContent(String url, Action1<AppCMSPageAPI> readyAction) {
        GetAppCMSAPIAsyncTask.Params params = new GetAppCMSAPIAsyncTask.Params.Builder()
                .context(currentActivity)
                .url(url)
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
            if (navigationPages.get(appCMSPageUI) == appCMSPageUI) {
                return key;
            }
        }
        return null;
    }
}
