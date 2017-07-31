package com.viewlift.views.binders;

import android.net.Uri;
import android.os.Binder;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.downloads.UserVideoDownloadStatus;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;

import java.util.Map;

import rx.functions.Action1;

/**
 * Created by sandeep.singh on 7/30/2017.
 */

public class AppCMSDownloadQualityBinder extends Binder {
    private final AppCMSMain appCMSMain;
    private final AppCMSPageUI appCMSPageUI;
    private AppCMSPageAPI appCMSPageAPI;
    private final String pageId;
    private final String pageName;
    private final String screenName;
    private final boolean loadedFromFile;
    private final boolean appbarPresent;
    private final boolean fullScreenEnabled;
    private final boolean navbarPresent;
    private final  boolean userLoggedIn;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final ContentDatum contentDatum;
    private final Action1<UserVideoDownloadStatus> resultAction1;


    public AppCMSDownloadQualityBinder(AppCMSMain appCMSMain,
                                       AppCMSPageUI appCMSPageUI,
                                       AppCMSPageAPI appCMSPageAPI,
                                       String pageId,
                                       String pageName,
                                       String screenName,
                                       boolean loadedFromFile,
                                       boolean appbarPresent,
                                       boolean fullScreenEnabled,
                                       boolean navbarPresent,
                                       boolean userLoggedIn,
                                       Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                       ContentDatum contentDatum,
                                       Action1<UserVideoDownloadStatus> resultAction1
    ) {
        this.appCMSMain = appCMSMain;
        this.appCMSPageUI = appCMSPageUI;
        this.appCMSPageAPI = appCMSPageAPI;
        this.pageId = pageId;
        this.pageName = pageName;
        this.screenName = screenName;
        this.loadedFromFile = loadedFromFile;
        this.appbarPresent = appbarPresent;
        this.fullScreenEnabled = fullScreenEnabled;
        this.navbarPresent = navbarPresent;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.userLoggedIn=userLoggedIn;
        this.contentDatum=contentDatum;
        this.resultAction1=resultAction1;
    }

    public AppCMSMain getAppCMSMain() {
        return appCMSMain;
    }

    public AppCMSPageUI getAppCMSPageUI() {
        return appCMSPageUI;
    }

    public AppCMSPageAPI getAppCMSPageAPI() {
        return appCMSPageAPI;
    }

    public void setAppCMSPageAPI(AppCMSPageAPI appCMSPageAPI) {
        this.appCMSPageAPI = appCMSPageAPI;
    }

    public String getPageId() {
        return pageId;
    }

    public String getPageName() {
        return pageName;
    }

    public String getScreenName() {
        return screenName;
    }

    public boolean isLoadedFromFile() {
        return loadedFromFile;
    }

    public boolean isAppbarPresent() {
        return appbarPresent;
    }

    public boolean isFullScreenEnabled() {
        return fullScreenEnabled;
    }

    public boolean isNavbarPresent() {
        return navbarPresent;
    }

    public Map<String, AppCMSUIKeyType> getJsonValueKeyMap() {
        return jsonValueKeyMap;

    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public ContentDatum getContentDatum() {
        return contentDatum;
    }

    public Action1<UserVideoDownloadStatus> getResultAction1() {
        return resultAction1;
    }
}
