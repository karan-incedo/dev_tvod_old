package com.viewlift.views.binders;

import android.os.Binder;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;

import java.util.Map;

import com.viewlift.models.data.appcms.ui.android.Navigation;

/**
 * Created by viewlift on 5/4/17.
 */

public class AppCMSBinder extends Binder {
    private final AppCMSPageUI appCMSPageUI;
    private final AppCMSPageAPI appCMSPageAPI;
    private final Navigation navigation;
    private final boolean loadedFromFile;
    private final String pageId;
    private final boolean appbarPresent;
    private final boolean fullScreen;
    private final Map<AppCMSUIKeyType, String> jsonValueKeyMap;

    public AppCMSBinder(AppCMSPageUI appCMSPageUI,
                        AppCMSPageAPI appCMSPageAPI,
                        Navigation navigation,
                        boolean loadedFromFile,
                        String pageId,
                        boolean appbarPresent,
                        boolean fullScreen,
                        Map<AppCMSUIKeyType, String> jsonValueKeyMap) {
        this.appCMSPageUI = appCMSPageUI;
        this.appCMSPageAPI = appCMSPageAPI;
        this.navigation = navigation;
        this.loadedFromFile = loadedFromFile;
        this.pageId = pageId;
        this.appbarPresent = appbarPresent;
        this.fullScreen = fullScreen;
        this.jsonValueKeyMap = jsonValueKeyMap;
    }

    public AppCMSPageUI getAppCMSPageUI() {
        return appCMSPageUI;
    }

    public AppCMSPageAPI getAppCMSPageAPI() {
        return appCMSPageAPI;
    }

    public boolean getLoadedFromFile() {
        return loadedFromFile;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public Map<AppCMSUIKeyType, String> getJsonValueKeyMap() {
        return jsonValueKeyMap;
    }

    public String getPageId() {
        return pageId;
    }

    public boolean isLoadedFromFile() {
        return loadedFromFile;
    }

    public boolean isAppbarPresent() {
        return appbarPresent;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }
}
