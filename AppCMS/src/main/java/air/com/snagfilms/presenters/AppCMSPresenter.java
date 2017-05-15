package air.com.snagfilms.presenters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import air.com.snagfilms.models.data.appcms.android.Android;
import air.com.snagfilms.models.data.appcms.android.MetaPage;
import air.com.snagfilms.models.data.appcms.main.Main;
import air.com.snagfilms.models.data.binders.AppCMSBinder;
import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.models.network.background.tasks.GetAppCMSAndroidAsyncTask;
import air.com.snagfilms.models.network.background.tasks.GetAppCMSMainAsyncTask;
import air.com.snagfilms.models.network.background.tasks.GetAppCMSPageAsyncTask;
import air.com.snagfilms.models.network.components.AppCMSAPIComponent;
import air.com.snagfilms.views.activity.AppCMSPageActivity;
import air.com.snagfilms.views.activity.ErrorActivity;
import rx.functions.Action1;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/3/17.
 */

public class AppCMSPresenter {
    public static int REQUEST_SPLASH_SCREEN = 1000;

    private static int SPLASH_PAGE_DELAY = 3000;

    private static final String TAG = "AppCMSPresenter";

    public enum PageName {
        HOME_PAGE("HomePage"),
        SPLASH_PAGE("SplashPage");

        final String name;
        PageName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static PageName fromString(String value) {
            for (PageName pageName : PageName.values()) {
                if (pageName.toString().equals(value)) {
                    return pageName;
                }
            }
            throw new IllegalArgumentException("No enum constant " + value);
        }
    }

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

    private static final String ERROR_TAG = "error_fragment";

    private Queue<MetaPage> pagesToProcess;

    private Map<String, Page> navigationPages;

    public static AppCMSPresenter getAppCMSPresenter() {
        if (presenter == null) {
            presenter = new AppCMSPresenter();
        }
        return presenter;
    }

    private AppCMSPresenter() {
        navigationPages = new HashMap<>();
    }

    public boolean launchPageActivity(Activity activity,
                                      String navigationNode,
                                      boolean loadFromFile) {
        if (navigationPages.containsKey(navigationNode)) {
            Bundle args = new Bundle();
            AppCMSBinder appCMSBinder = new AppCMSBinder(navigationPages.get(navigationNode), loadFromFile);
            args.putBinder(activity.getString(R.string.app_cms_binder_key), appCMSBinder);
            Intent appCMSIntent = new Intent(activity, AppCMSPageActivity.class);
            appCMSIntent.putExtra(activity.getString(R.string.app_cms_bundle_key), args);
            activity.startActivity(appCMSIntent);
            return true;
        }
        return false;
    }

    public boolean launchPageActivityForResult(Activity activity,
                                               String navigationNode,
                                               int requestCode,
                                               boolean loadFromFile) {
        if (navigationPages.containsKey(navigationNode)) {
            Bundle args = new Bundle();
            AppCMSBinder appCMSBinder = new AppCMSBinder(navigationPages.get(navigationNode), loadFromFile);
            args.putBinder(activity.getString(R.string.app_cms_binder_key), appCMSBinder);
            Intent appCMSIntent = new Intent(activity, AppCMSPageActivity.class);
            appCMSIntent.putExtra(activity.getString(R.string.app_cms_bundle_key), args);
            activity.startActivityForResult(appCMSIntent, requestCode);
            return true;
        }
        return false;
    }

    public void launchErrorActivity(Activity activity) {
        Intent errorIntent = new Intent(activity, ErrorActivity.class);
        activity.startActivity(errorIntent);
    }

    public void loadMain(Activity activity, AppCMSAPIComponent appCMSAPIComponent, String url) {
        getAppCMSMain(activity, appCMSAPIComponent, url);
    }

    private void getAppCMSMain(final Activity activity,
                               final AppCMSAPIComponent appCMSAPIComponent,
                               String url) {
        new GetAppCMSMainAsyncTask(appCMSAPIComponent.appCMSMainCall(), new Action1<Main>() {
            @Override
            public void call(Main main) {
                if (main == null ||
                        main.getMain() == null ||
                        main.getMain().getAndroid() == null ||
                        main.getMain().getAndroid().isEmpty()) {
                    Log.e(TAG, "AppCMS keys for main not found");
                    launchErrorActivity(activity);
                } else {
                    getAppCMSAndroid(activity,
                            appCMSAPIComponent,
                            main.getMain().getAndroid(),
                            !main.getVersion().equals(main.getOldVersion()));
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
                    processMetaPagesQueue(appCMSAPIComponent, activity, loadFromFile);
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
            }
            pageToQueueIndex = getHomePage(metaPageList);
            if (pageToQueueIndex >= 0) {
                pagesToProcess.add(metaPageList.get(pageToQueueIndex));
                metaPageList.remove(pageToQueueIndex);
                queueMetaPages(metaPageList);
            }
        }
    }

    public void processMetaPagesQueue(AppCMSAPIComponent appCMSAPIComponent,
                                      final Activity activity,
                                      final boolean loadFromFile) {
        final MetaPage metaPage = pagesToProcess.remove();
        getAppCMSPage(appCMSAPIComponent,
                metaPage.getPageUI(),
                new Action1<Page>() {
                    @Override
                    public void call(Page page) {
                        navigationPages.put(metaPage.getPageName(), page);
                        if (PageName.fromString(metaPage.getPageName()) == PageName.HOME_PAGE) {
                            launchPageActivityForResult(activity,
                                    metaPage.getPageName(),
                                    REQUEST_SPLASH_SCREEN,
                                    loadFromFile);
                        } else {
                            launchPageActivity(activity, metaPage.getPageName(), loadFromFile);
                        }
                    }
                },
                loadFromFile);
    }

    public void getContent(String url, Action1<JsonElement> readyAction) {

    }

    private int getSplashPage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (PageName.fromString(metaPageList.get(i).getPageName()) == PageName.SPLASH_PAGE) {
                return i;
            }
        }
        return -1;
    }

    private int getHomePage(List<MetaPage> metaPageList) {
        for (int i = 0; i < metaPageList.size(); i++) {
            if (PageName.fromString(metaPageList.get(i).getPageName()) == PageName.HOME_PAGE) {
                return i;
            }
        }
        return -1;
    }
}
