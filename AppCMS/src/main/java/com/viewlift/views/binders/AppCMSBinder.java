package com.viewlift.views.binders;

import android.net.Uri;
import android.os.Binder;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.Navigation;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.network.rest.AppCMSSearchCall;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.Map;

/*
 * Created by viewlift on 5/4/17.
 */

public class AppCMSBinder extends Binder {
    private final AppCMSMain appCMSMain;
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
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private AppCMSPageUI appCMSPageUI;
    private AppCMSPageAPI appCMSPageAPI;
    private boolean sendCloseAction;
    private Uri searchQuery;
    private AppCMSSearchCall appCMSSearchCall;
    private int xScroll;
    private int yScroll;
    private boolean scrollOnLandscape;
    private int currentScrollPosition;

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

    public AppCMSSearchCall getAppCMSSearchCall() {
        return appCMSSearchCall;
    }

    public void setAppCMSSearchCall(AppCMSSearchCall appCMSSearchCall) {
        this.appCMSSearchCall = appCMSSearchCall;
    }

    public AppCMSMain getAppCMSMain() {
        return appCMSMain;
    }

    public AppCMSPageUI getAppCMSPageUI() {
        return appCMSPageUI;
    }

    public void setAppCMSPageUI(AppCMSPageUI appCMSPageUI) {
        this.appCMSPageUI = appCMSPageUI;
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

    public int getxScroll() {
        return xScroll;
    }

    public void setxScroll(int xScroll) {
        this.xScroll = xScroll;
    }

    public int getyScroll() {
        return yScroll;
    }

    public void setyScroll(int yScroll) {
        this.yScroll = yScroll;
    }

    public boolean isScrollOnLandscape() {
        return scrollOnLandscape;
    }

    public void setScrollOnLandscape(boolean scrollOnLandscape) {
        this.scrollOnLandscape = scrollOnLandscape;
    }

    public int getCurrentScrollPosition() {
        return currentScrollPosition;
    }

    public void setCurrentScrollPosition(int currentScrollPosition) {
        this.currentScrollPosition = currentScrollPosition;
    }

    public void setAppbarPresent(boolean appbarPresent) {
        this.appbarPresent = appbarPresent;
    }
}
