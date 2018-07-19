package com.viewlift.analytics;

import android.content.Context;
import android.net.ConnectivityManager;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.HashMap;

import javax.inject.Inject;

public class CleverTapSDK {


    Context context;
    CleverTapAPI cleverTapAPI;
    AppCMSPresenter appCMSPresenter;
    final String EVENT_PLAY_STARTED = "Play Started";
    final String EVENT_WATCHED = "Watched";
    final String EVENT_DOWNLOAD_INITIATED = "Download initiated";
    final String EVENT_DOWNLOAD_COMPLETED = "Download completed";
    final String EVENT_SHARE = "Shared";
    final String EVENT_SEARCH = "Searched";
    final String EVENT_VIEW_PLANS = "View Plans";
    final String EVENT_SIGNED_UP = "Signed Up";
    final String EVENT_LOGIN = "Login";
    final String EVENT_PLAYER_BITRATE_CHANGE = "Player BitRate changed";
    final String EVENT_DOWNLOAD_BITRATE_CHANGE = "Download BitRate changed";
    final String EVENT_CAST = "Cast";
    final String EVENT_PAGE_VIEWED = "Page Viewed";
    final String EVENT_LOGOUT = "Logout";
    final String EVENT_ADD_TO_WATCHLIST = "Added to Watchlist";
    final String EVENT_REMOVE_FROM_WATCHLIST = "Removed From Watchlist";
    final String EVENT_SUBSCRIPTION_INITIATED = "Subscription Initiated";
    final String EVENT_MEDIA_ERROR = "Media Error";

    final String KEY_PAGE_NAME = "Page Name";
    final String KEY_LAST_ACTIVITY_NAME = "Last Activity Name";
    final String KEY_PLATFORM = "Platform";
    final String KEY_APP_VERSION = "App Version";
    final String KEY_REG_TYPE = "Registration Type";
    final String KEY_CONTENT_ID = "Content ID";
    final String KEY_CONTENT_TITLE = "Content Title";
    final String KEY_CONTENT_TYPE = "Content Type";
    final String KEY_PLAY_SOURCE = "Play Source";
    final String KEY_CONTENT_GENRE = "Content Genre";
    final String KEY_ERROR = "Error Message";
    final String KEY_CONTENT_DURATION = "Content Duration";
    final String KEY_EPISODE_NUMBER = "Episode Number";
    final String KEY_PLAYBACK_TYPE = "Playback Type";
    final String KEY_SEASON_NUMBER = "Season Number";
    final String KEY_NETWORK_TYPE = "Network Type";
    final String KEY_SHOW_NAME = "Show name";
    final String KEY_DIRECTOR_NAME = "Director Name";
    final String KEY_MUSIC_DIRECTOR_NAME = "Music Director Name";
    final String KEY_ACTOR_NAME = "Actor Name";
    final String KEY_SINGER_NAME = "Singer Name";
    final String KEY_WATCH_TIME = "Watched time";
    final String KEY_LISTENING_TIME = "Listening time";
    final String KEY_CHANNEL = "Channel";
    final String KEY_STREAM = "Stream";
    final String KEY_BUFFER_TIME = "buffer_time";
    final String KEY_BUFFER_COUNT = "buffer_count";
    final String KEY_SUBTITLES = "subtitles";
    final String KEY_BITRATE = "Bitrate";
    final String KEY_KEYWORD = "Keyword";
    final String KEY_MEDIUM = "Medium";
    final String KEY_QUALITY = "Quality";
    final String KEY_CAST_TYPE = "Cast Type";
    final String PLATFORM_VALUE = "Android";

    @Inject
    public CleverTapSDK(Context context) {
        this.context = context;

    }

    public void initializeSDK(AppCMSPresenter appCMSPresenter) {
        this.appCMSPresenter = appCMSPresenter;
        try {
            cleverTapAPI = CleverTapAPI.getInstance(context);
        } catch (CleverTapMetaDataNotFoundException e) {
        } catch (CleverTapPermissionsNotSatisfied e) {
        }
    }

    final String KEY_PROFILE_NAME = "Name";
    final String KEY_PROFILE_EMAIL = "Email";
    final String KEY_PROFILE_IDENTITY = "Identity";
    final String KEY_PROFILE_PHONE = "Phone";
    final String KEY_PROFILE_USER_STATUS = "User Status";
    final String KEY_PROFILE_SUBSCRIPTION_PAYMENT_MODE = "Payment Mode";
    final String KEY_PROFILE_SUBSCRIPTION_START_DATE = "Subscription Start Date";
    final String KEY_PROFILE_SUBSCRIPTION_END_DATE = "Subscription End Date";
    final String KEY_PROFILE_SUBSCRIPTION_TRANSACTION_ID = "Transaction ID";
    final String KEY_PROFILE_SUBSCRIPTION_AMOUNT = "Amount";
    final String KEY_PROFILE_DISCOUNT_AMOUNT = "Discount Amount";
    final String KEY_PROFILE_PAYMENT_PLAN = "Payment Plan";
    final String KEY_PROFILE_SOURCE = "Source";
    final String KEY_PROFILE_PAYMENT_HANDLER = "Payment Handler";
    final String KEY_PROFILE_COUNTRY = "Country";
    final String KEY_PROFILE_CURRENCY = "Currency";
    final String KEY_FREE_TRIAL = "Free Trial";

    public void sendUserProfile(String loggedInUser, String loggedInUserName, String loggedInUserEmail,
                                String userStatus, String subscriptionStartDate, String subscriptionEndDate,
                                String transId, String country, double discountPrice, double planPrice,
                                String currency, String planName, String paymentHandler, boolean freeTrial,
                                String mobile) {
        HashMap<String, Object> userProfile = new HashMap<>();
        userProfile.put(KEY_PROFILE_NAME, loggedInUserName);
        userProfile.put(KEY_PROFILE_EMAIL, loggedInUserEmail);
        userProfile.put(KEY_PROFILE_IDENTITY, loggedInUser);
        userProfile.put(KEY_PROFILE_USER_STATUS, userStatus);
//        userProfile.put(KEY_PROFILE_SUBSCRIPTION_PAYMENT_MODE, );
        userProfile.put(KEY_PROFILE_SUBSCRIPTION_START_DATE, subscriptionStartDate);
        userProfile.put(KEY_PROFILE_SUBSCRIPTION_END_DATE, subscriptionEndDate);
        userProfile.put(KEY_PROFILE_SUBSCRIPTION_TRANSACTION_ID, transId);
        userProfile.put(KEY_PROFILE_SUBSCRIPTION_AMOUNT, planPrice);
        userProfile.put(KEY_PROFILE_DISCOUNT_AMOUNT, discountPrice);
        userProfile.put(KEY_PROFILE_PAYMENT_PLAN, planName);
        userProfile.put(KEY_PROFILE_SOURCE, PLATFORM_VALUE);
        userProfile.put(KEY_PROFILE_PAYMENT_HANDLER, paymentHandler);
        userProfile.put(KEY_PROFILE_COUNTRY, country);
        userProfile.put(KEY_PROFILE_CURRENCY, currency);
        userProfile.put(KEY_PROFILE_PHONE, mobile);
        String freetry;
        if (freeTrial)
            freetry = "Yes";
        else
            freetry = "No";
        userProfile.put(KEY_FREE_TRIAL, freetry);

        cleverTapAPI.profile.push(userProfile);
    }

    private HashMap<String, Object> playKeys(ContentDatum contentDatum) {
        HashMap<String, Object> playEvent = commonKeys(contentDatum);
        if (appCMSPresenter.isVideoDownloaded(contentDatum.getGist().getId()))
            playEvent.put(KEY_PLAYBACK_TYPE, "Downloaded");
        else
            playEvent.put(KEY_PLAYBACK_TYPE, "Streamed");

        playEvent.put(KEY_PLAY_SOURCE, appCMSPresenter.getPlaySource());
        if (contentDatum.getGist().getPrimaryCategory() != null && contentDatum.getGist().getPrimaryCategory().getTitle() != null)
            playEvent.put(KEY_CONTENT_GENRE, contentDatum.getGist().getPrimaryCategory().getTitle());
        return playEvent;
    }

    public void sendEventPlayStarted(ContentDatum contentDatum) {
        HashMap<String, Object> playEvent = playKeys(contentDatum);
        cleverTapAPI.event.push(EVENT_PLAY_STARTED, playEvent);
    }

    public void sendEventCast(ContentDatum contentDatum) {
        HashMap<String, Object> castEvent = playKeys(contentDatum);
        castEvent.put(KEY_CAST_TYPE, "chrome cast");
        cleverTapAPI.event.push(EVENT_CAST, castEvent);
    }

    public void sendEventWatched(ContentDatum contentDatum, long watchTime, String stream, int bufferCount, int bufferTime) {
        HashMap<String, Object> watchEvent = commonKeys(contentDatum);
        String playbackType = "streamed";
        if (contentDatum.getGist().getDownloadStatus() == DownloadStatus.STATUS_COMPLETED)
            playbackType = "downloaded";
        watchEvent.put(KEY_PLAYBACK_TYPE, playbackType);
        watchEvent.put(KEY_SUBTITLES, isSubtitlesAvailable(contentDatum));
        if (contentDatum.getGist() != null && contentDatum.getGist().getContentType() != null &&
                contentDatum.getGist().getContentType().toLowerCase().contains(context.getString(R.string.content_type_audio).toLowerCase()))
            watchEvent.put(KEY_LISTENING_TIME, watchTime);
        else
            watchEvent.put(KEY_WATCH_TIME, watchTime);

        if (contentDatum.getGist().getPrimaryCategory() != null && contentDatum.getGist().getPrimaryCategory().getTitle() != null)
            watchEvent.put(KEY_CONTENT_GENRE, contentDatum.getGist().getPrimaryCategory().getTitle());
        watchEvent.put(KEY_BUFFER_TIME, bufferTime);
        watchEvent.put(KEY_BUFFER_COUNT, bufferCount);
        watchEvent.put(KEY_STREAM, stream);
//        watchEvent.put(KEY_CHANNEL, );

        cleverTapAPI.event.push(EVENT_WATCHED, watchEvent);
    }


    private HashMap<String, Object> commonKeys(ContentDatum contentDatum) {
        HashMap<String, Object> commonEvent = new HashMap<>();
        commonEvent.put(KEY_PLATFORM, PLATFORM_VALUE);
        commonEvent.put(KEY_CONTENT_ID, contentDatum.getGist().getId());
        commonEvent.put(KEY_CONTENT_TITLE, contentDatum.getGist().getTitle());
        String contentType = "";
        if (contentDatum.getGist().getMediaType() != null)
            if (contentDatum.getGist().getMediaType().contains(context.getResources().getString(R.string.media_type_episode))) {
                contentType = "Episode";
                if (appCMSPresenter.getShowDatum() != null) {
                    commonEvent.put(KEY_EPISODE_NUMBER, appCMSPresenter.getShowDatum().getGist().getEpisodeNum());
                    commonEvent.put(KEY_SEASON_NUMBER, appCMSPresenter.getShowDatum().getGist().getSeasonNum());
                    commonEvent.put(KEY_SHOW_NAME, appCMSPresenter.getShowDatum().getGist().getShowName());
                }
            } else if (contentDatum.getGist().getMediaType().contains(context.getResources().getString(R.string.app_cms_series_content_type)))
                contentType = "Show";
            else
                contentType = contentDatum.getGist().getMediaType();
        else
            contentType = contentDatum.getGist().getContentType();
        commonEvent.put(KEY_CONTENT_TYPE, contentType);
        if (!contentDatum.getGist().getContentType().contains(context.getResources().getString(R.string.app_cms_series_content_type)))
            commonEvent.put(KEY_CONTENT_DURATION, contentDatum.getGist().getRuntime());
        String networkType = "Wifi";
        if (appCMSPresenter.getActiveNetworkType() == ConnectivityManager.TYPE_MOBILE)
            networkType = "Cellular";
        commonEvent.put(KEY_NETWORK_TYPE, networkType);
        if (contentDatum.getGist() != null && contentDatum.getGist().getContentType() != null &&
                contentDatum.getGist().getContentType().toLowerCase().contains(context.getString(R.string.content_type_audio).toLowerCase())) {
            commonEvent.put(KEY_MUSIC_DIRECTOR_NAME, appCMSPresenter.getDirectorNameFromCreditBlocks(contentDatum.getCreditBlocks()));
            commonEvent.put(KEY_SINGER_NAME, appCMSPresenter.getArtistNameFromCreditBlocks(contentDatum.getCreditBlocks()));
            if (appCMSPresenter.isVideoDownloaded(contentDatum.getGist().getId())) {
                commonEvent.put(KEY_MUSIC_DIRECTOR_NAME, contentDatum.getGist().getArtistName());
                commonEvent.put(KEY_SINGER_NAME, contentDatum.getGist().getDirectorName());
            }
        } else {
            commonEvent.put(KEY_DIRECTOR_NAME, appCMSPresenter.getDirectorNameFromCreditBlocks(contentDatum.getCreditBlocks()));
            commonEvent.put(KEY_ACTOR_NAME, appCMSPresenter.getArtistNameFromCreditBlocks(contentDatum.getCreditBlocks()));
        }
        return commonEvent;
    }

    public void sendEventDownloadStarted(ContentDatum contentDatum) {
        HashMap<String, Object> downloadEvent = playKeys(contentDatum);
        downloadEvent.put(KEY_BITRATE, appCMSPresenter.getUserDownloadQualityPref());
        cleverTapAPI.event.push(EVENT_DOWNLOAD_INITIATED, downloadEvent);
    }

    public void sendEventDownloadComplete(ContentDatum contentDatum) {
        HashMap<String, Object> downloadEvent = playKeys(contentDatum);
        downloadEvent.put(KEY_BITRATE, appCMSPresenter.getUserDownloadQualityPref());
        if (contentDatum.getGist().getMediaType().contains(appCMSPresenter.getCurrentContext().getResources().getString(R.string.media_type_episode))) {
            downloadEvent.put(KEY_EPISODE_NUMBER, contentDatum.getGist().getEpisodeNum());
            downloadEvent.put(KEY_SEASON_NUMBER, contentDatum.getGist().getSeasonNum());
            downloadEvent.put(KEY_SHOW_NAME, contentDatum.getGist().getShowName());
        }
        cleverTapAPI.event.push(EVENT_DOWNLOAD_COMPLETED, downloadEvent);
    }

    public void sendEventShare(ContentDatum contentDatum) {
        HashMap<String, Object> shareEvent = playKeys(contentDatum);
        shareEvent.put(KEY_MEDIUM, "Android Native");
        cleverTapAPI.event.push(EVENT_SHARE, shareEvent);
    }

    public void sendEventSearch(String keyword) {
        HashMap<String, Object> searchEvent = new HashMap<>();
        searchEvent.put(KEY_KEYWORD, keyword);
        searchEvent.put(KEY_PLATFORM, PLATFORM_VALUE);
        cleverTapAPI.event.push(EVENT_SEARCH, searchEvent);
    }

    public void sendEventSignUp(String regType) {
        HashMap<String, Object> signUpEvent = new HashMap<>();
        signUpEvent.put(KEY_REG_TYPE, regType);
        signUpEvent.put(KEY_PLATFORM, PLATFORM_VALUE);
        cleverTapAPI.event.push(EVENT_SIGNED_UP, signUpEvent);
    }

    public void sendEventLogin(String regType, String appVersion) {
        HashMap<String, Object> loginEvent = new HashMap<>();
        loginEvent.put(KEY_REG_TYPE, regType);
        loginEvent.put(KEY_PLATFORM, PLATFORM_VALUE);
        loginEvent.put(KEY_APP_VERSION, appVersion);
        cleverTapAPI.event.push(EVENT_LOGIN, loginEvent);
    }

    public void sendEventPlayerBitrateChange(String quality) {
        HashMap<String, Object> bitrateEvent = new HashMap<>();
        bitrateEvent.put(KEY_PLATFORM, PLATFORM_VALUE);
        bitrateEvent.put(KEY_QUALITY, quality);
        cleverTapAPI.event.push(EVENT_PLAYER_BITRATE_CHANGE, bitrateEvent);
    }

    public void sendEventDownloadBitrateChange(String quality) {
        HashMap<String, Object> bitrateEvent = new HashMap<>();
        bitrateEvent.put(KEY_PLATFORM, PLATFORM_VALUE);
        bitrateEvent.put(KEY_QUALITY, quality);
        cleverTapAPI.event.push(EVENT_DOWNLOAD_BITRATE_CHANGE, bitrateEvent);
    }

    public void sendEventPageViewed(String lastPage, String pageName, String appVersion) {
        HashMap<String, Object> pageEvent = new HashMap<>();
        pageEvent.put(KEY_PLATFORM, PLATFORM_VALUE);
        pageEvent.put(KEY_PAGE_NAME, pageName);
        pageEvent.put(KEY_LAST_ACTIVITY_NAME, lastPage);
        pageEvent.put(KEY_APP_VERSION, appVersion);
        cleverTapAPI.event.push(EVENT_PAGE_VIEWED, pageEvent);
    }

    public void sendEventLogout() {
        HashMap<String, Object> logoutEvent = new HashMap<>();
        logoutEvent.put(KEY_PLATFORM, PLATFORM_VALUE);
        cleverTapAPI.event.push(EVENT_LOGOUT, logoutEvent);
    }

    public void sendEventViewPlans() {
        HashMap<String, Object> viewPlanEvent = new HashMap<>();
        viewPlanEvent.put(KEY_PLATFORM, PLATFORM_VALUE);
        cleverTapAPI.event.push(EVENT_VIEW_PLANS, viewPlanEvent);
    }

    public void sendEventAddWatchlist(ContentDatum contentDatum) {
        HashMap<String, Object> watchlistEvent = playKeys(contentDatum);
        cleverTapAPI.event.push(EVENT_ADD_TO_WATCHLIST, watchlistEvent);
    }

    public void sendEventRemoveWatchlist(ContentDatum contentDatum) {
        HashMap<String, Object> watchlistEvent = playKeys(contentDatum);
        cleverTapAPI.event.push(EVENT_REMOVE_FROM_WATCHLIST, watchlistEvent);
    }

    public void sendEventMediaError(ContentDatum contentDatum, String error, long watchTime) {
        HashMap<String, Object> errorEvent = commonKeys(contentDatum);
        String playbackType = "streamed";
        if (contentDatum.getGist().getDownloadStatus() == DownloadStatus.STATUS_COMPLETED)
            playbackType = "downloaded";
        errorEvent.put(KEY_PLAYBACK_TYPE, playbackType);
        errorEvent.put(KEY_SUBTITLES, isSubtitlesAvailable(contentDatum));
        if (contentDatum.getGist() != null && contentDatum.getGist().getContentType() != null &&
                contentDatum.getGist().getContentType().toLowerCase().contains(context.getString(R.string.content_type_audio).toLowerCase()))
            errorEvent.put(KEY_LISTENING_TIME, watchTime);
        else
            errorEvent.put(KEY_WATCH_TIME, watchTime);
        if (contentDatum.getGist().getPrimaryCategory() != null && contentDatum.getGist().getPrimaryCategory().getTitle() != null)
            errorEvent.put(KEY_CONTENT_GENRE, contentDatum.getGist().getPrimaryCategory().getTitle());
        errorEvent.put(KEY_ERROR, error);
        cleverTapAPI.event.push(EVENT_MEDIA_ERROR, errorEvent);
    }

    public void sendEventSubscriptionInitiated(String paymentHandler, String country, double discountPrice, double planPrice,
                                               String currency, String planName) {
        HashMap<String, Object> subscriptionEvent = new HashMap<>();
        subscriptionEvent.put(KEY_PROFILE_PAYMENT_HANDLER, paymentHandler);
        subscriptionEvent.put(KEY_PROFILE_COUNTRY, country);
        subscriptionEvent.put(KEY_PLATFORM, PLATFORM_VALUE);
        subscriptionEvent.put(KEY_PROFILE_DISCOUNT_AMOUNT, discountPrice);
        subscriptionEvent.put(KEY_PROFILE_SUBSCRIPTION_AMOUNT, planPrice);
        subscriptionEvent.put(KEY_PROFILE_CURRENCY, currency);
        subscriptionEvent.put(KEY_PROFILE_PAYMENT_PLAN, planName);
        cleverTapAPI.event.push(EVENT_SUBSCRIPTION_INITIATED, subscriptionEvent);
    }

    private String isSubtitlesAvailable(ContentDatum contentDatum) {
        boolean subtitleAvailable = false;
        if (contentDatum != null
                && contentDatum.getContentDetails() != null
                && contentDatum.getContentDetails().getClosedCaptions() != null
                && !contentDatum.getContentDetails().getClosedCaptions().isEmpty()) {
            for (ClosedCaptions cc : contentDatum.getContentDetails().getClosedCaptions()) {
                if (cc.getUrl() != null) {
                    if ((cc.getFormat() != null &&
                            cc.getFormat().equalsIgnoreCase("srt")) ||
                            cc.getUrl().toLowerCase().contains("srt")) {
                        String closedCaptionUrl = cc.getUrl();
                        subtitleAvailable = true;
                    }
                }
            }
        }
        if (subtitleAvailable)
            return "Y";
        else
            return "N";
    }
}
