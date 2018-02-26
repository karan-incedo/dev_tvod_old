package com.viewlift.views.binders;

import android.os.Binder;

import com.google.android.exoplayer2.Player;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;

import java.util.List;
import java.util.Map;

/**
 * Created by anas.azeem on 7/11/2017.
 * Owned by ViewLift, NYC
 */

public class AppCMSVideoPageBinder extends Binder {
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private boolean isOffline;
    private AppCMSPageUI appCMSPageUI;
    private AppCMSPageAPI appCMSPageAPI;
    private String pageID;
    private String pageName;
    private String screenName;
    private boolean loadFromFile;
    private boolean appbarPresent;
    private boolean fullscreenEnabled;
    private boolean navbarPresent;
    private boolean sendCloseAction;
    private String adsUrl;
    private List<String> relateVideoIds;
    private boolean isTrailer;
    private String bgColor;
    private ContentDatum contentData;
    private boolean playAds;
    private String fontColor;
    private boolean isLoggedIn;
    private boolean isSubscribed;
    private volatile int currentPlayingVideoIndex;
    private String currentMovieId;
    private String currentMovieName;
    private String currentMovieImageUrl;
    private int playerState;
    private boolean autoplayCancelled;

    public AppCMSVideoPageBinder(
            AppCMSPageUI appCMSPageUI,
            AppCMSPageAPI appCMSPageAPI,
            String pageID,
            String pageName,
            String screenName,
            boolean loadFromFile,
            boolean appbarPresent,
            boolean fullscreenEnabled,
            boolean navbarPresent,
            boolean sendCloseAction,
            Map<String, AppCMSUIKeyType> jsonValueKeyMap,
            boolean playAds,
            String fontColor,
            String backgroundColor,
            String adsUrl,
            ContentDatum contentDatum,
            boolean isTrailer,
            boolean userLoggedIn,
            boolean isSubscribed,
            List<String> relatedVideoIds,
            int currentlyPlayingIndex,
            boolean isOffline) {
        this.appCMSPageUI = appCMSPageUI;
        this.appCMSPageAPI = appCMSPageAPI;
        this.pageID = pageID;
        this.pageName = pageName;
        this.screenName = screenName;
        this.loadFromFile = loadFromFile;
        this.appbarPresent = appbarPresent;
        this.fullscreenEnabled = fullscreenEnabled;
        this.navbarPresent = navbarPresent;
        this.sendCloseAction = sendCloseAction;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.playAds = playAds;
        this.fontColor = fontColor;
        this.bgColor = backgroundColor;
        this.adsUrl = adsUrl;
        this.contentData = contentDatum;
        this.isTrailer = isTrailer;
        this.isLoggedIn = userLoggedIn;
        this.isSubscribed = isSubscribed;
        this.relateVideoIds = relatedVideoIds;
        this.currentPlayingVideoIndex = currentlyPlayingIndex;
        this.isOffline = isOffline;
        this.playerState = Player.STATE_IDLE;
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

    public void setAppCMSPageAPI(AppCMSPageAPI appCMSPageAPI) {
        this.appCMSPageAPI = appCMSPageAPI;
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public boolean isLoadFromFile() {
        return loadFromFile;
    }

    public void setLoadFromFile(boolean loadFromFile) {
        this.loadFromFile = loadFromFile;
    }

    public boolean isAppbarPresent() {
        return appbarPresent;
    }

    public void setAppbarPresent(boolean appbarPresent) {
        this.appbarPresent = appbarPresent;
    }

    public boolean isFullscreenEnabled() {
        return fullscreenEnabled;
    }

    public void setFullscreenEnabled(boolean fullscreenEnabled) {
        this.fullscreenEnabled = fullscreenEnabled;
    }

    public boolean isNavbarPresent() {
        return navbarPresent;
    }

    public void setNavbarPresent(boolean navbarPresent) {
        this.navbarPresent = navbarPresent;
    }

    public boolean isSendCloseAction() {
        return sendCloseAction;
    }

    public void setSendCloseAction(boolean sendCloseAction) {
        this.sendCloseAction = sendCloseAction;
    }

    public Map<String, AppCMSUIKeyType> getJsonValueKeyMap() {
        return jsonValueKeyMap;
    }

    public boolean isPlayAds() {
        return playAds;
    }

    public void setPlayAds(boolean playAds) {
        this.playAds = playAds;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public void setCurrentPlayingVideoIndex(int currentPlayingVideoIndex) {
        this.currentPlayingVideoIndex = currentPlayingVideoIndex;
    }

    public int getCurrentPlayingVideoIndex() {
        return currentPlayingVideoIndex;
    }

    public ContentDatum getContentData() {
        return contentData;
    }

    public void setContentData(ContentDatum contentData) {
        this.contentData = contentData;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getAdsUrl() {
        return adsUrl;
    }

    public List<String> getRelateVideoIds() {
        return relateVideoIds;
    }

    public boolean isTrailer() {
        return isTrailer;
    }

    public void setAdsUrl(String adsUrl) {
        this.adsUrl = adsUrl;
    }

    public void setRelateVideoIds(List<String> relateVideoIds) {
        this.relateVideoIds = relateVideoIds;
    }

    public void setTrailer(boolean trailer) {
        isTrailer = trailer;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

    public String getCurrentMovieId() {
        return currentMovieId;
    }

    public void setCurrentMovieId(String currentMovieId) {
        this.currentMovieId = currentMovieId;
    }

    public String getCurrentMovieName() {
        return currentMovieName;
    }

    public void setCurrentMovieName(String currentMovieName) {
        this.currentMovieName = currentMovieName;
    }

    public String getCurrentMovieImageUrl() {
        return currentMovieImageUrl;
    }

    public void setCurrentMovieImageUrl(String currentMovieImageUrl) {
        this.currentMovieImageUrl = currentMovieImageUrl;
    }

    public int getPlayerState() {
        return playerState;
    }

    public void setPlayerState(int playerState) {
        this.playerState = playerState;
    }

    public boolean isAutoplayCancelled() {
        return autoplayCancelled;
    }

    public void setAutoplayCancelled(boolean autoplayCancelled) {
        this.autoplayCancelled = autoplayCancelled;
    }
}
