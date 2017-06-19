package com.viewlift.views.binders;

import android.os.Binder;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;

import java.util.Map;

import com.viewlift.models.data.appcms.ui.android.Navigation;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSBinder extends Binder {
    private final AppCMSMain appCMSMain;
    private final AppCMSPageUI appCMSPageUI;
    private final AppCMSPageAPI appCMSPageAPI;
    private final Navigation navigation;
    private final String pageId;
    private final String pageName;
    private final boolean loadedFromFile;
    private final boolean appbarPresent;
    private final boolean fullScreenEnabled;
    private final boolean navbarPresent;
    private final boolean userLoggedIn;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;

    public AppCMSBinder(AppCMSMain appCMSMain,
                        AppCMSPageUI appCMSPageUI,
                        AppCMSPageAPI appCMSPageAPI,
                        Navigation navigation,
                        String pageId,
                        String pageName,
                        boolean loadedFromFile,
                        boolean appbarPresent,
                        boolean fullScreenEnabled,
                        boolean navbarPresent,
                        boolean userLoggedIn,
                        Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
        this.appCMSMain = appCMSMain;
        this.appCMSPageUI = appCMSPageUI;
        this.appCMSPageAPI = appCMSPageAPI;
        this.navigation = navigation;
        this.pageId = pageId;
        this.pageName = pageName;
        this.loadedFromFile = loadedFromFile;
        this.appbarPresent = appbarPresent;
        this.fullScreenEnabled = fullScreenEnabled;
        this.navbarPresent = navbarPresent;
        this.userLoggedIn = userLoggedIn;
        this.jsonValueKeyMap = jsonValueKeyMap;
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
}
