package com.viewlift.views.binders;

import android.net.Uri;
import android.os.Binder;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;

import java.util.Map;

import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.models.network.rest.AppCMSSearchCall;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSBinder extends Binder {
    private final AppCMSMain appCMSMain;
    private AppCMSPageUI appCMSPageUI;
    private AppCMSPageAPI appCMSPageAPI;
    private final Navigation navigation;
    private final String pageId;
    private final String pageName;
    private final String pagePath;
    private final String screenName;
    private final boolean loadedFromFile;
    private boolean appbarPresent;
    private final boolean fullScreenEnabled;
    private final boolean navbarPresent;
    private final boolean userLoggedIn;
    private final boolean userSubscribed;
    private final AppCMSPresenter.ExtraScreenType extraScreenType;
    private boolean sendCloseAction;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private Uri searchQuery;

    public AppCMSSearchCall getAppCMSSearchCall() {
        return appCMSSearchCall;
    }

    public void setAppCMSSearchCall(AppCMSSearchCall appCMSSearchCall) {
        this.appCMSSearchCall = appCMSSearchCall;
    }

    private AppCMSSearchCall appCMSSearchCall;

    public AppCMSBinder(AppCMSMain appCMSMain,
                        AppCMSPageUI appCMSPageUI,
                        AppCMSPageAPI appCMSPageAPI,
                        Navigation navigation,
                        String pageId,
                        String pageName,
                        String pagePath,
                        String screenName,
                        boolean loadedFromFile,
                        boolean appbarPresent,
                        boolean fullScreenEnabled,
                        boolean navbarPresent,
                        boolean sendCloseAction,
                        boolean userLoggedIn,
                        boolean userSubscribed,
                        AppCMSPresenter.ExtraScreenType extraScreenType,
                        Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                        Uri searchQuery,
                        AppCMSSearchCall appCMSSearchCall) {
        this.appCMSMain = appCMSMain;
        this.appCMSPageUI = appCMSPageUI;
        this.appCMSPageAPI = appCMSPageAPI;
        this.navigation = navigation;
        this.pageId = pageId;
        this.pageName = pageName;
        this.pagePath = pagePath;
        this.screenName = screenName;
        this.loadedFromFile = loadedFromFile;
        this.appbarPresent = appbarPresent;
        this.fullScreenEnabled = fullScreenEnabled;
        this.navbarPresent = navbarPresent;
        this.sendCloseAction = sendCloseAction;
        this.userLoggedIn = userLoggedIn;
        this.extraScreenType = extraScreenType;
        this.userSubscribed = userSubscribed;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.searchQuery = searchQuery;
        this.appCMSSearchCall = appCMSSearchCall;
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

    public Navigation getNavigation() {
        return navigation;
    }

    public String getPageId() {
        return pageId;
    }

    public String getPageName() {
        return pageName;
    }

    public String getPagePath() {
        return pagePath;
    }

    public boolean getLoadedFromFile() {
        return loadedFromFile;
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

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public boolean isNavbarPresent() {
        return navbarPresent;
    }

    public Map<String, AppCMSUIKeyType> getJsonValueKeyMap() {
        return jsonValueKeyMap;
    }

    public Uri getSearchQuery() {
        return searchQuery;
    }

    public void clearSearchQuery() {
        searchQuery = null;
    }

    public String getScreenName() {
        return screenName;
    }

    public boolean shouldSendCloseAction() {
        return sendCloseAction;
    }

    public void updateAppCMSPageAPI(AppCMSPageAPI appCMSPageAPI) {
        this.appCMSPageAPI = appCMSPageAPI;
    }

    public void unsetSendCloseAction() {
        sendCloseAction = false;
    }

    public boolean isUserSubscribed() {
        return userSubscribed;
    }

    public AppCMSPresenter.ExtraScreenType getExtraScreenType() {
        return extraScreenType;
    }

    public void setAppCMSPageUI(AppCMSPageUI appCMSPageUI) {
        this.appCMSPageUI = appCMSPageUI;
    }

    public void setAppbarPresent(boolean appbarPresent) {
        this.appbarPresent = appbarPresent;
    }
}
