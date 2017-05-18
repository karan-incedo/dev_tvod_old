package air.com.snagfilms.presenters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import air.com.snagfilms.models.data.appcms.AppCMSKeyType;
import air.com.snagfilms.models.data.appcms.android.Android;
import air.com.snagfilms.models.data.appcms.android.MetaPage;
import air.com.snagfilms.models.data.binders.AppCMSBinder;
import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.models.network.background.tasks.GetAppCMSAndroidAsyncTask;
import air.com.snagfilms.models.network.background.tasks.GetAppCMSMainAsyncTask;
import air.com.snagfilms.models.network.background.tasks.GetAppCMSPageAsyncTask;
import air.com.snagfilms.models.network.components.AppCMSAPIComponent;
import air.com.snagfilms.models.network.modules.AppCMSAPIModule;
import air.com.snagfilms.views.activity.AppCMSPageActivity;
import air.com.snagfilms.views.activity.ErrorActivity;
import rx.functions.Action0;
import rx.functions.Action1;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/3/17.
 */

public class AppCMSPresenter {
    private static final String TAG = "AppCMSPresenter";

    public enum PresenterAction {
        CREATE,
        START,
        RESUME,
        PAUSE,
        STOP,
        DESTROY,
        VIDEO_PLAY,
        VIDEO_PAUSE,
        VIDEO_STOP,
        VIDEO_REVERSE,
        VIDEO_REWIND,
        LOGIN,
        EXIT,
        CONFIGURATION_CHANGE,
        SUBSCRIPTION,
        ERROR
    }

    private static AppCMSPresenter presenter;

    private Queue<MetaPage> pagesToProcess;

    private Map<String, Page> navigationPages;

    private Stack<Activity> activityStack;

    private Map<AppCMSKeyType, String> jsonValueKeyMap;

    public static AppCMSPresenter getAppCMSPresenter() {
        if (presenter == null) {
            presenter = new AppCMSPresenter();
        }
        return presenter;
    }

    private AppCMSPresenter() {
        navigationPages = new HashMap<>();
        activityStack = new Stack<>();
    }

    public boolean launchPageActivity(Activity activity,
                                      String navigationNode,
                                      boolean loadFromFile) {
        if (navigationPages.containsKey(navigationNode)) {
            Bundle args = new Bundle();
            AppCMSBinder appCMSBinder = new AppCMSBinder(navigationPages.get(navigationNode),
                    loadFromFile,
                    navigationNode,
                    jsonValueKeyMap);
            args.putBinder(activity.getString(R.string.app_cms_binder_key), appCMSBinder);
            Intent appCMSIntent = new Intent(activity, AppCMSPageActivity.class);
            appCMSIntent.putExtra(activity.getString(R.string.app_cms_bundle_key), args);
            activity.startActivity(appCMSIntent);
            return true;
        }
        return false;
    }

    public void launchErrorActivity(Activity activity) {
        Intent errorIntent = new Intent(activity, ErrorActivity.class);
        activity.startActivity(errorIntent);
    }

    public void loadMain(Activity activity, AppCMSAPIComponent appCMSAPIComponent, String url) {
        pushActivityToStack(activity);
        jsonValueKeyMap = appCMSAPIComponent.jsonValueKeyMap();
        getAppCMSMain(activity, appCMSAPIComponent, url);
    }

    private void getAppCMSMain(final Activity activity,
                               final AppCMSAPIComponent appCMSAPIComponent,
                               String url) {
        new GetAppCMSMainAsyncTask(appCMSAPIComponent.appCMSMainCall(), new Action1<JsonElement>() {
            @Override
            public void call(JsonElement main) {
                if (main == null ||
                        TextUtils.isEmpty(main
                                .getAsJsonObject()
                                .get(jsonValueKeyMap.get(AppCMSKeyType.MAIN_ANDROID_KEY))
                                .getAsString())) {
                    Log.e(TAG, "AppCMS keys for main not found");
                    launchErrorActivity(activity);
                } else {
                    String androidUrl = main
                            .getAsJsonObject()
                            .get(jsonValueKeyMap.get(AppCMSKeyType.MAIN_ANDROID_KEY))
                            .getAsString();
                    String version = main
                            .getAsJsonObject()
                            .get(jsonValueKeyMap.get(AppCMSKeyType.MAIN_VERSION_KEY))
                            .getAsString();
                    String oldVersion = main
                            .getAsJsonObject()
                            .get(jsonValueKeyMap.get(AppCMSKeyType.MAIN_OLD_VERSION_KEY))
                            .getAsString();
                    getAppCMSAndroid(activity,
                            appCMSAPIComponent,
                            androidUrl,
                            !version.equals(oldVersion));
                }
            }
        }).execute(Uri.parse(url));
    }

    private void getAppCMSAndroid(final Activity activity,
                                  final AppCMSAPIComponent appCMSAPIComponent,
                                  String url,
                                  final boolean loadFromFile) {
        GetAppCMSAndroidAsyncTask.RunOptions runOptions = new GetAppCMSAndroidAsyncTask.RunOptions();
        runOptions.dataUri = Uri.parse(url);
        runOptions.loadFromFile = loadFromFile;
        new GetAppCMSAndroidAsyncTask(appCMSAPIComponent.appCMSAndroidCall(), new Action1<Android>() {
            @Override
            public void call(Android android) {
                if (android == null || android.getMetaPages() == null || android.getMetaPages().size() < 1) {
                    Log.e(TAG, "AppCMS keys for pages for android not found");
                    launchErrorActivity(activity);
                } else {
                    queueMetaPages(android.getMetaPages());
                    final MetaPage firstPage = pagesToProcess.peek();
                    processMetaPagesQueue(appCMSAPIComponent,
                            loadFromFile,
                            new Action0() {
                                @Override
                                public void call() {
                                    launchPageActivity(activity,
                                            firstPage.getPageName(),
                                             loadFromFile);
                                }
                            });
                }
            }
        }).execute(runOptions);
    }

    private void getAppCMSPage(AppCMSAPIComponent appCMSAPIComponent,
                               String url,
                               final Action1<Page> onPageReady,
                               boolean loadFromFile) {
        GetAppCMSPageAsyncTask.RunOptions runOptions = new GetAppCMSPageAsyncTask.RunOptions();
        runOptions.dataUri = Uri.parse(url);
        runOptions.loadFromFile = loadFromFile;
        new GetAppCMSPageAsyncTask(appCMSAPIComponent.appCMSPageCall(), onPageReady).execute(runOptions);
    }

    public void queueMetaPages(List<MetaPage> metaPageList) {
        if (pagesToProcess == null) {
            pagesToProcess = new ConcurrentLinkedQueue();
        }
        if (metaPageList.size() > 0) {
            int pageToQueueIndex = getSplashPage(metaPageList);
            if (pageToQueueIndex >= 0) {
                pagesToProcess.add(metaPageList.get(pageToQueueIndex));
                metaPageList.remove(pageToQueueIndex);
                queueMetaPages(metaPageList);
            } else {
                for (int i = 0; i < metaPageList.size(); i++) {
                    pagesToProcess.add(metaPageList.get(i));
                }
            }

        }
    }

    public void processMetaPagesQueue(final AppCMSAPIComponent appCMSAPIComponent,
                                      final boolean loadFromFile,
                                      final Action0 onPagesFinishedAction) {
        final MetaPage metaPage = pagesToProcess.remove();
        getAppCMSPage(appCMSAPIComponent,
                metaPage.getPageUI(),
                new Action1<Page>() {
                    @Override
                    public void call(Page page) {
                        navigationPages.put(metaPage.getPageName(), page);
                        if (pagesToProcess.size() > 0) {
                            processMetaPagesQueue(appCMSAPIComponent,
                                    loadFromFile,
                                    onPagesFinishedAction);
                        } else {
                            onPagesFinishedAction.call();
                        }
                    }
                },
                loadFromFile);
    }

    public void getContent(String url, Action1<JsonElement> readyAction) {

    }

    public void pushActivityToStack(Activity activity) {
        activityStack.add(activity);
    }

    public Activity popActivityFromStack() {
        return activityStack.pop();
    }

    private int getSplashPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (metaPageList
                    .get(i)
                    .getPageName()
                    .equals(jsonValueKeyMap.get(AppCMSKeyType.ANDROID_SPLASH_SCREEN_KEY))) {
                return i;
            }
        }
        return -1;
    }
}
