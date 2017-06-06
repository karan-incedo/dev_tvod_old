package com.viewlift.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Inject;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidUI;
import com.viewlift.models.data.appcms.ui.android.MetaPage;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.views.activity.AppCMSNavItemsActivity;
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
import com.viewlift.views.customviews.LifecycleStatus;
import com.viewlift.views.customviews.OnInternalEvent;

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
    private boolean loadingPage;
    private AppCMSMain appCMSMain;
    private Queue<MetaPage> pagesToProcess;
    private Map<String, AppCMSPageUI> navigationPages;
    private Map<String, AppCMSPageAPI> navigationPageData;
    private Map<String, String> pageIdToPageAPIUrlMap;
    private Map<String, String> actionToPageAPIUrlMap;
    private Map<String, String> actionToPageNameMap;
    private Map<String, String> pageIdToPageNameMap;
    private List<Action1<Boolean>> onOrientationChangeHandlers;
    private List<Action1<LifecycleStatus>> onLifecycleChangeHandlers;
    private Map<String, List<OnInternalEvent>> onActionInternalEvents;
    private Stack<String> currentActions;

    private static abstract class AppCMSPageAPIAction implements Action1<AppCMSPageAPI> {
        boolean appbarPresent;
        boolean fullscreenEnabled;
        public AppCMSPageAPIAction(boolean appbarPresent, boolean fullscreenEnabled) {
            this.appbarPresent = appbarPresent;
            this.fullscreenEnabled = fullscreenEnabled;
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

        this.loadingPage = false;
        this.navigationPages = new HashMap<>();
        this.navigationPageData = new HashMap<>();
        this.pageIdToPageAPIUrlMap = new HashMap<>();
        this.actionToPageAPIUrlMap = new HashMap<>();
        this.actionToPageNameMap = new HashMap<>();
        this.pageIdToPageNameMap = new HashMap();
        this.onOrientationChangeHandlers = new ArrayList<>();
        this.onLifecycleChangeHandlers = new ArrayList<>();
        this.onActionInternalEvents = new HashMap<>();
        this.currentActions = new Stack<>();
    }

    public void setCurrentActivity(Activity activity) {
        this.currentActivity = activity;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public boolean launchButtonSelectedAction(String pagePath, final String action, final String filmTitle) {
        boolean result = false;
        if (currentActivity != null && !loadingPage) {
            AppCMSActionType actionType = actionToActionTypeMap.get(action);
            if (actionType == null) {
                Log.e(TAG, "Action " + action + " not found!");
                return false;
            }
            if (isActionUp(action)) {
                Log.d(TAG, "Action has already started");
                return true;
            }
            cancelInternalEvents();
            pushActionInternalEvents(action);
            result = true;
            final AppCMSPageUI appCMSPageUI = actionToPageMap.get(action);
            boolean appbarPresent = true;
            boolean fullscreenEnabled = false;
            loadingPage = true;
            currentActivity.sendBroadcast(new Intent(AppCMSPresenter.PRESENTER_PAGE_LOADING_ACTION));
            switch (actionType) {
                case SPLASH_PAGE:
                    appbarPresent = false;
                    fullscreenEnabled = false;
                    break;
                case VIDEO_PAGE:
                    appbarPresent = true;
                    fullscreenEnabled = true;
                    break;
                case HOME_PAGE:
                default:
                    break;
            }
            getPageIdContent(appCMSMain.getApiBaseUrl(),
                    actionToPageAPIUrlMap.get(action),
                    appCMSMain.getSite(),
                    false,
                    pagePath,
                    new AppCMSPageAPIAction(appbarPresent, fullscreenEnabled) {
                        @Override
                        public void call(AppCMSPageAPI appCMSPageAPI) {
                            Bundle args = getPageActivityBundle(currentActivity,
                                    appCMSPageUI,
                                    appCMSPageAPI,
                                    getPageId(appCMSPageUI),
                                    currentActivity.getString(R.string.default_app_name),
                                    filmTitle,
                                    loadFromFile,
                                    appbarPresent,
                                    fullscreenEnabled);
                            Intent updatePageIntent =
                                    new Intent(AppCMSPresenter.PRESENTER_NAVIGATE_ACTION);
                            updatePageIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                                    args);
                            currentActivity.sendBroadcast(updatePageIntent);
                            loadingPage = false;
                        }
                    });
        }
        return result;
    }

    public boolean launchNavigationPage() {
        boolean result = false;
        if (currentActivity != null) {
            result = true;
            Intent navigationIntent = new Intent(currentActivity, AppCMSNavItemsActivity.class);
            Bundle args = getPageActivityBundle(currentActivity,
                    null,
                    null,
                    null,
                    currentActivity.getString(R.string.default_app_name),
                    currentActivity.getString(R.string.app_cms_menu_label),
                    false,
                    true,
                    false);
            navigationIntent.putExtra(currentActivity.getString(R.string.app_cms_bundle_key),
                    args);
            currentActivity.startActivity(navigationIntent);
        }
        return result;
    }

    public void resetOnOrientationChangeHandlers() {
        onOrientationChangeHandlers.clear();
    }

    public void addOnOrientationChangeHandler(Action1<Boolean> onOrientationChangeHandler) {
        onOrientationChangeHandlers.add(onOrientationChangeHandler);
    }

    public void onOrientationChange(boolean landscape) {
        for (Action1<Boolean> onOrientationChangeHandler : onOrientationChangeHandlers) {
            Observable.just(landscape).subscribe(onOrientationChangeHandler);
        }
    }

    public void addOnLifecycleChangeHandler(Action1<LifecycleStatus> onLifecycleChangeHandler) {
        onLifecycleChangeHandlers.add(onLifecycleChangeHandler);
    }

    public void onLifecycleChange(LifecycleStatus lifecycleStatus) {
        for (Action1<LifecycleStatus> onLifecycleChangeHandler : onLifecycleChangeHandlers) {
            Observable.just(lifecycleStatus).subscribe(onLifecycleChangeHandler);
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

    public boolean navigateToPage(final String pageId) {
        boolean result = false;
        if (currentActivity != null && !TextUtils.isEmpty(pageId)) {
            final AppCMSPageUI appCMSPageUI = navigationPages.get(pageId);
            AppCMSPageAPI appCMSPageAPI = navigationPageData.get(pageId);
            if (appCMSPageAPI == null) {
                getPageIdContent(appCMSMain.getApiBaseUrl(),
                        pageIdToPageAPIUrlMap.get(pageId),
                        appCMSMain.getSite(),
                        true,
                        getPageId(appCMSPageUI),
                        new AppCMSPageAPIAction(true, false) {
                            @Override
                            public void call(AppCMSPageAPI appCMSPageAPI) {
                                navigationPageData.put(pageId, appCMSPageAPI);
                                launchPageActivity(currentActivity,
                                        appCMSPageUI,
                                        appCMSPageAPI,
                                        pageId,
                                        currentActivity.getString(R.string.default_app_name),
                                        pageIdToPageNameMap.get(pageId),
                                        loadFromFile,
                                        appbarPresent,
                                        fullscreenEnabled);
                            }
                        });
            } else {
                launchPageActivity(currentActivity,
                        appCMSPageUI,
                        appCMSPageAPI,
                        pageId,
                        currentActivity.getString(R.string.default_app_name),
                        pageIdToPageNameMap.get(pageId),
                        loadFromFile,
                        true,
                        false);
            }
        }
        return result;
    }

    public boolean launchPageAction(final String action, @Nullable Bundle data) {
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
                    Log.e(TAG, "Action " + action + " not found!");
                } else {
                    AppCMSActionType actionType = actionToActionTypeMap.get(action);
                    if (actionType == null) {
                        Log.e(TAG, "Action " + action + " not found!");
                        return false;
                    }
                    if (isActionUp(action)) {
                        Log.d(TAG, "Action has already started");
                        return true;
                    }
                    cancelInternalEvents();
                    pushActionInternalEvents(action);

                    boolean appbarPresent = true;
                    boolean fullscreenEnabled = false;
                    switch (actionToActionTypeMap.get(action)) {
                        case SPLASH_PAGE:
                            appbarPresent = false;
                            fullscreenEnabled = false;
                            break;
                        case VIDEO_PAGE:
                            appbarPresent = false;
                            fullscreenEnabled = true;
                            break;
                        case HOME_PAGE:
                        default:
                            break;
                    }

                    final AppCMSPageUI appCMSPageUI = actionToPageMap.get(action);
                    AppCMSPageAPI currentAppCMSPageAPI = actionToPageAPIMap.get(action);
                    if (currentAppCMSPageAPI == null) {
                        getPageIdContent(appCMSMain.getApiBaseUrl(),
                                actionToPageAPIUrlMap.get(action),
                                appCMSMain.getSite(),
                                true,
                                getPageId(appCMSPageUI),
                                new AppCMSPageAPIAction(appbarPresent, fullscreenEnabled) {
                                    @Override
                                    public void call(AppCMSPageAPI appCMSPageAPI) {
                                        actionToPageAPIMap.put(action, appCMSPageAPI);
                                        launchPageActivity(currentActivity,
                                                appCMSPageUI,
                                                appCMSPageAPI,
                                                getPageId(appCMSPageUI),
                                                currentActivity.getString(R.string.default_app_name),
                                                actionToPageNameMap.get(action),
                                                loadFromFile,
                                                appbarPresent,
                                                fullscreenEnabled);
                                    }
                                });
                    } else {
                        launchPageActivity(currentActivity,
                                appCMSPageUI,
                                currentAppCMSPageAPI,
                                getPageId(appCMSPageUI),
                                currentActivity.getString(R.string.default_app_name),
                                actionToPageNameMap.get(action),
                                loadFromFile,
                                appbarPresent,
                                fullscreenEnabled);
                    }
                }
            }
        }

        return result;
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
        SharedPreferences sharedPrefs = context.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
        return sharedPrefs.getString(USER_ID_SHARED_PREF_NAME, null) != null;
    }

    public boolean setLoggedInUser(Context context, String userId) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(LOGIN_SHARED_PREF_NAME, 0);
        return sharedPrefs.edit().putString(USER_ID_SHARED_PREF_NAME, userId).commit();
    }

    public void addInternalEvent(OnInternalEvent onInternalEvent) {
        if (!TextUtils.isEmpty(currentActions.peek()) &&
                onActionInternalEvents.get(currentActions.peek()) != null) {
            onActionInternalEvents.get(currentActions.peek()).add(onInternalEvent);
        }
    }

    public List<OnInternalEvent> getOnInternalEvents() {
        if (!TextUtils.isEmpty(currentActions.peek()) &&
                onActionInternalEvents.get(currentActions.peek()) != null) {
            return onActionInternalEvents.get(currentActions.peek());
        }
        return null;
    }

    public void restartInternalEvents() {
        List<OnInternalEvent> onInternalEvents = onActionInternalEvents.get(currentActions.peek());
        if (onInternalEvents != null) {
            for (OnInternalEvent onInternalEvent : onInternalEvents) {
                onInternalEvent.cancel(false);
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
            currentActions.pop();
        }
    }

    private boolean isActionUp(String action) {
        if (currentActions.size() == 1 && action.equals(currentActions.peek())) {
            Log.d(TAG, "Action has already started");
            return true;
        }
        return false;
    }

    private void pushActionInternalEvents(String action) {
        if (currentActions.size() == 2) {
            popActionInternalEvents();
        }
        if (onActionInternalEvents.get(action) == null) {
            onActionInternalEvents.put(action, new ArrayList<OnInternalEvent>());
        }
        currentActions.push(action);
    }

    public AppCMSMain getAppCMSMain() {
        return appCMSMain;
    }

    private Bundle getPageActivityBundle(Activity activity,
                                         AppCMSPageUI appCMSPageUI,
                                         AppCMSPageAPI appCMSPageAPI,
                                         String pageID,
                                         String pageName,
                                         String subpageName,
                                         boolean loadFromFile,
                                         boolean appbarPresent,
                                         boolean fullscreenEnabled) {
        Bundle args = new Bundle();
        AppCMSBinder appCMSBinder = new AppCMSBinder(appCMSMain,
                appCMSPageUI,
                appCMSPageAPI,
                navigation,
                pageID,
                pageName,
                subpageName,
                loadFromFile,
                appbarPresent,
                fullscreenEnabled,
                isUserLoggedIn(activity),
                jsonValueKeyMap);
        args.putBinder(activity.getString(R.string.app_cms_binder_key), appCMSBinder);
        return args;
    }

    private void launchPageActivity(Activity activity,
                                    AppCMSPageUI appCMSPageUI,
                                    AppCMSPageAPI appCMSPageAPI,
                                    String pageId,
                                    String pageName,
                                    String subpageName,
                                    boolean loadFromFile,
                                    boolean appbarPresent,
                                    boolean fullscreenEnabled) {
        Bundle args = getPageActivityBundle(activity,
                appCMSPageUI,
                appCMSPageAPI,
                pageId,
                pageName,
                subpageName,
                loadFromFile,
                appbarPresent,
                fullscreenEnabled);
        Intent appCMSIntent = new Intent(activity, AppCMSPageActivity.class);
        appCMSIntent.putExtra(activity.getString(R.string.app_cms_bundle_key), args);

        activity.startActivity(appCMSIntent);
    }

    private void getAppCMSMain(final Activity activity,
                               final String siteId) {
        GetAppCMSMainUIAsyncTask.Params params = new GetAppCMSMainUIAsyncTask.Params.Builder()
                .context(currentActivity)
                .siteId(siteId)
                .build();
        new GetAppCMSMainUIAsyncTask(appCMSMainUICall, new Action1<AppCMSMain>() {
            @Override
            public void call(AppCMSMain main) {
                if (main == null) {
                    Log.e(TAG, "Error retrieving main.json");
                    launchErrorActivity(activity);
                } else if (TextUtils.isEmpty(main
                        .getAndroid())) {
                    Log.e(TAG, "AppCMS key for main not found");
                    launchErrorActivity(activity);
                } else if (TextUtils.isEmpty(main
                        .getApiBaseUrl())) {
                    Log.e(TAG, "AppCMS key for API Base URL not found");
                    launchErrorActivity(activity);
                } else if (TextUtils.isEmpty(main.getSite())) {
                    Log.e(TAG, "AppCMS key for API Site ID not found");
                    launchErrorActivity(activity);
                } else {
                    appCMSMain = main;
                    String androidUrl = main
                            .getAndroid();
                    String version = main
                            .getVersion();
                    String oldVersion = main
                            .getOldVersion();
                    Log.d(TAG, "Version: " + version);
                    Log.d(TAG, "OldVersion: " + oldVersion);
                    loadFromFile = false;
                    getAppCMSAndroid(activity, main);
                }
            }
        }).execute(params);
    }

    private void getAppCMSAndroid(final Activity activity, AppCMSMain main) {
        GetAppCMSAndroidUIAsyncTask.Params params =
                new GetAppCMSAndroidUIAsyncTask.Params.Builder()
                    .url(main.getAndroid())
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
                                            launchPageAction(pageNameToActionMap.get(firstPage.getPageName()),
                                                    null);
                                    if (!launchSuccess) {
                                        Log.e(TAG, "Failed to launch page: " + firstPage.getPageName());
                                        launchErrorActivity(currentActivity);
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
                        pageIdToPageNameMap.put(metaPage.getPageId(), metaPage.getPageName());
                        String action = pageNameToActionMap.get(metaPage.getPageName());
                        if (action != null && actionToPageMap.containsKey(action)) {
                            actionToPageMap.put(action, appCMSPageUI);
                            actionToPageNameMap.put(action, metaPage.getPageName());
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
