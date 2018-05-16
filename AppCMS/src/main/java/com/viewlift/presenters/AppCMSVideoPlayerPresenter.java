package com.viewlift.presenters;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.viewlift.R;
import com.viewlift.casting.CastHelper;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.models.data.appcms.beacon.BeaconBuffer;
import com.viewlift.models.data.appcms.beacon.BeaconPing;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.ui.authentication.UserIdentity;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.customviews.VideoPlayerView;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.functions.Action1;

/**
 * Created by viewlift on 1/8/18.
 */

public class AppCMSVideoPlayerPresenter implements AdErrorEvent.AdErrorListener,
        AdEvent.AdEventListener,
        VideoPlayerView.ErrorEventListener,
        AudioManager.OnAudioFocusChangeListener {

    private static final long SECS_TO_MSECS = 1000L;

    private final String FIREBASE_VIDEO_ID_KEY = "video_id";
    private final String FIREBASE_VIDEO_NAME_KEY = "video_name";
    private final String FIREBASE_PLAYER_NAME_KEY = "player_name";
    private final String FIREBASE_PLAYER_NATIVE = "Native";
    private final String FIREBASE_MEDIA_TYPE_KEY = "media_type";
    private final String FIREBASE_MEDIA_TYPE_VIDEO = "Video";
    private final String FIREBASE_STREAM_START = "stream_start";
    private final String FIREBASE_STREAM_25 = "stream_25_pct";
    private final String FIREBASE_STREAM_50 = "stream_50_pct";
    private final String FIREBASE_STREAM_75 = "stream_75_pct";
    private final String FIREBASE_STREAM_100 = "stream_100_pct";

    /** AppCMSPresenter */
    private AppCMSPresenter appCMSPresenter;

    /** VideoPlayerView */
    private VideoPlayerView videoPlayerView;

    /** VideoPlayerView data */
    /** External values */
    private long watchedTime;
    private String adsUrl;
    private int playIndex;
    private AppCMSVideoPageBinder appCMSVideoPageBinder;
    /** Gist values */
    private String closedCaptionUrl;
    private String permaLink;
    private String filmId;
    private boolean freeContent;
    private String title;
    private String videoUrl;
    private String primaryCategory;
    private boolean isTrailer;
    private boolean shouldRequestAds;

    /** Internal values */
    private boolean isVideoLoaded;
    private long videoPlayTime;
    private boolean isAdDisplayed;
    private long mTotalVideoDuration;
    private long mStopBufferMilliSec;
    private static double ttfirstframe;
    private int maxPreviewSecs;
    private long mStartBufferMilliSec;
    private static int apod;

    /** Created values */
    private boolean useHls;
    private String defaultVideoResolution;
    private String trailerKey;
    private String downloadFilePrefix;
    private String parentScreenName;
    private String pagePlayAction;
    private String mStreamId;
    private boolean isVideoDownloaded;
    private UserIdentity userIdentityObj;

    /** Firebase */
    private Handler mProgressHandler;
    private Runnable mProgressRunnable;
    private boolean isStreamStart, isStream25, isStream50, isStream75, isStream100;

    /** Beacon */
    private BeaconPing beaconPing;
    private BeaconBuffer beaconBuffer;
    private boolean sentBeaconFirstFrame;
    private boolean sentBeaconPlay;
    private long beaconMsgTimeoutMsec;
    private long beaconBufferingTimeoutMsec;

    /** Ads */
    private ImaSdkFactory sdkFactory;
    private AdsLoader adsLoader;
    private AdsManager adsManager;
    private AdsLoader.AdsLoadedListener listenerAdsLoaded = adsManagerLoadedEvent -> {
        adsManager = adsManagerLoadedEvent.getAdsManager();
        adsManager.addAdErrorListener(this);
        adsManager.addAdEventListener(this);
        adsManager.init();
    };

    /** Custom listeners */
    private OnClosePlayerEvent onClosePlayerEvent;
    private OnUpdateContentDatumEvent onUpdateContentDatumEvent;

    /** Audio focus control */
    private AudioManager audioManager;
    private boolean mAudioFocusGranted;

    /** Refresh token */
    private boolean refreshToken;
    private Timer refreshTokenTimer;
    private TimerTask refreshTokenTimerTask;

    /** Entitlement checker */
    private Timer entitlementCheckTimer;
    private TimerTask entitlementCheckTimerTask;
    private boolean entitlementCheckCancelled;
    private boolean showEntitlementDialog;

    public void updateBinder(Context context,
                             AppCMSPresenter appCMSPresenter,
                             VideoPlayerView videoPlayerView,
                             OnClosePlayerEvent onClosePlayerEvent,
                             OnUpdateContentDatumEvent onUpdateContentDatumEvent,
                             AppCMSVideoPageBinder binder,
                             int playIndex,
                             boolean isTrailer,
                             String downloadedVideoUrl,
                             boolean reset) {
        this.playIndex = playIndex;
        this.isTrailer = isTrailer;
        this.appCMSVideoPageBinder = binder;
        this.adsUrl = binder.getAdsUrl();

        binder.setAutoplayCancelled(false);

        if (binder.getContentData() != null) {
            if (binder.getContentData().getContentDetails().getClosedCaptions() != null &&
                    !binder.getContentData().getContentDetails().getClosedCaptions().isEmpty()) {
                for (ClosedCaptions cc : binder.getContentData().getContentDetails().getClosedCaptions()) {
                    if (cc.getUrl() != null &&
                            !cc.getUrl().equalsIgnoreCase(downloadFilePrefix) &&
                            cc.getFormat() != null &&
                            cc.getFormat().equalsIgnoreCase("SRT")) {
                        closedCaptionUrl = cc.getUrl();
                    }
                }
            }

            Gist gist = binder.getContentData().getGist();

            if (gist != null) {
                permaLink = gist.getPermalink();
                filmId = gist.getId();
                freeContent = gist.getFree();
                title = gist.getTitle();
                watchedTime = gist.getWatchedTime();

                if (gist.getPrimaryCategory() != null && gist.getPrimaryCategory().getTitle() != null) {
                    primaryCategory = gist.getPrimaryCategory().getTitle();
                }

                if (binder.isOffline() &&
                        !TextUtils.isEmpty(downloadedVideoUrl) &&
                        gist.getDownloadStatus().equals(DownloadStatus.STATUS_SUCCESSFUL)) {
                    videoUrl = downloadedVideoUrl;
                }
                /*If the video is already downloaded, play if from there, even if Internet is
                * available*/
                else if (gist.getId() != null &&
                        appCMSPresenter.getRealmController() != null &&
                        appCMSPresenter.getRealmController().getDownloadById(gist.getId()) != null &&
                        appCMSPresenter.getRealmController().getDownloadById(gist.getId()).getDownloadStatus() != null &&
                        appCMSPresenter.getRealmController().getDownloadById(gist.getId()).getDownloadStatus().equals(DownloadStatus.STATUS_SUCCESSFUL)) {
                    videoUrl = appCMSPresenter.getRealmController().getDownloadById(gist.getId()).getLocalURI();
                } else if (binder.getContentData() != null &&
                        binder.getContentData().getStreamingInfo() != null &&
                        binder.getContentData().getStreamingInfo().getVideoAssets() != null) {
                    VideoAssets videoAssets = binder.getContentData().getStreamingInfo().getVideoAssets();

                    if (useHls) {
                        videoUrl = videoAssets.getHls();
                    }
                    if (TextUtils.isEmpty(videoUrl)) {
                        if (videoAssets.getMpeg() != null && !videoAssets.getMpeg().isEmpty()) {
                            if (videoAssets.getMpeg().get(0) != null) {
                                videoUrl = videoAssets.getMpeg().get(0).getUrl();
                            }
                            for (int i = 0; i < videoAssets.getMpeg().size() && TextUtils.isEmpty(videoUrl); i++) {
                                if (videoAssets.getMpeg().get(i) != null &&
                                        videoAssets.getMpeg().get(i).getRenditionValue() != null &&
                                        videoAssets.getMpeg().get(i).getRenditionValue().contains(defaultVideoResolution)) {
                                    videoUrl = videoAssets.getMpeg().get(i).getUrl();
                                }
                            }
                        }
                    }

                    if (useHls && videoAssets.getMpeg() != null && videoAssets.getMpeg().size() > 0) {
                        if (videoAssets.getMpeg().get(0).getUrl() != null &&
                                videoAssets.getMpeg().get(0).getUrl().indexOf("?") > 0) {
                            videoUrl = videoUrl + videoAssets.getMpeg().get(0).getUrl().substring(videoAssets.getMpeg().get(0).getUrl().indexOf("?"));
                        }
                    }
                }

                if (reset) {
                    stop();
                    init(context, appCMSPresenter, videoPlayerView, onClosePlayerEvent, onUpdateContentDatumEvent);
                }
            }
        }
    }

    public void init(Context context,
                     AppCMSPresenter appCMSPresenter,
                     VideoPlayerView videoPlayerView,
                     OnClosePlayerEvent onClosePlayerEvent,
                     OnUpdateContentDatumEvent onUpdateContentDatumEvent) {
        this.appCMSPresenter = appCMSPresenter;
        this.videoPlayerView = videoPlayerView;
        this.onClosePlayerEvent = onClosePlayerEvent;
        this.onUpdateContentDatumEvent = onUpdateContentDatumEvent;

        sentBeaconPlay = (0 < playIndex && watchedTime != 0);

        stop();

        initAdsManager(context);
        initResourceValues(context);
        initRefreshTokenTask();
        initEntitlementCheckTask();
        initVideoListener();
        initBeacon();
        initAnalytics();
    }

    private void initAdsManager(Context context) {
        sdkFactory = ImaSdkFactory.getInstance();
        adsLoader = sdkFactory.createAdsLoader(context);
        adsLoader.addAdErrorListener(this);
        adsLoader.addAdsLoadedListener(listenerAdsLoaded);

        try {
            mStreamId = appCMSPresenter.getStreamingId(title);
            isVideoDownloaded = appCMSPresenter.isVideoDownloaded(filmId);
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
            mStreamId = filmId + appCMSPresenter.getCurrentTimeStamp();
            isVideoDownloaded = false;
        }
    }

    private void initResourceValues(Context context) {
        audioManager = (AudioManager) context.getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);

        useHls = context.getResources().getBoolean(R.bool.use_hls);
        defaultVideoResolution = context.getString(R.string.default_video_resolution);
        trailerKey = context.getString(R.string.app_cms_action_qualifier_watchvideo_key);
        parentScreenName = context.getString(R.string.app_cms_beacon_video_player_parent_screen_name);
        pagePlayAction = context.getString(R.string.app_cms_page_play_key);

        trailerKey = context.getString(R.string.app_cms_action_qualifier_watchvideo_key);
        parentScreenName = context.getString(R.string.app_cms_beacon_video_player_parent_screen_name);
        pagePlayAction = context.getString(R.string.app_cms_page_play_key);

        downloadFilePrefix = context.getString(R.string.download_file_prefix);

        shouldRequestAds = !appCMSPresenter.isAppSVOD();

        beaconMsgTimeoutMsec = context.getResources().getInteger(R.integer.app_cms_beacon_timeout_msec);
        beaconBufferingTimeoutMsec = context.getResources().getInteger(R.integer.app_cms_beacon_buffering_timeout_msec);
    }

    private void initRefreshTokenTask() {
        if (!isVideoDownloaded && refreshToken) {
            refreshTokenTimer = new Timer();
            refreshTokenTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (onUpdateContentDatumEvent != null) {
                        appCMSPresenter.refreshVideoData(onUpdateContentDatumEvent.getCurrentContentDatum().getGist().getId(), updatedContentDatum -> {
                            onUpdateContentDatumEvent.updateContentDatum(updatedContentDatum);
                            appCMSPresenter.getAppCMSSignedURL(filmId, appCMSSignedURLResult -> {
                                if (videoPlayerView != null && appCMSSignedURLResult != null) {
                                    videoPlayerView.updateSignatureCookies(appCMSSignedURLResult.getPolicy(),
                                            appCMSSignedURLResult.getSignature(),
                                            appCMSSignedURLResult.getKeyPairId());
                                }
                            });
                        });
                    }
                }
            };
            refreshTokenTimer.schedule(refreshTokenTimerTask, 0, 600000);
        }
    }

    private void initBeacon() {
        beaconPing = new BeaconPing(beaconMsgTimeoutMsec,
                appCMSPresenter,
                filmId,
                permaLink,
                isTrailer,
                parentScreenName,
                videoPlayerView,
                mStreamId,onUpdateContentDatumEvent.getCurrentContentDatum());

        beaconBuffer = new BeaconBuffer(beaconBufferingTimeoutMsec,
                appCMSPresenter,
                filmId,
                permaLink,
                parentScreenName,
                videoPlayerView,
                mStreamId,onUpdateContentDatumEvent.getCurrentContentDatum());
    }


    private void initEntitlementCheckTask() {
        if (appCMSPresenter.isAppSVOD() &&
                !isTrailer &&
                !freeContent &&
                !appCMSPresenter.isUserSubscribed()) {
            int entitlementCheckMultiplier = 5;
            entitlementCheckCancelled = false;

            AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
            if (appCMSMain != null &&
                    appCMSMain.getFeatures() != null &&
                    appCMSMain.getFeatures().getFreePreview() != null &&
                    appCMSMain.getFeatures().getFreePreview().isFreePreview() &&
                    appCMSMain.getFeatures().getFreePreview().getLength() != null &&
                    appCMSMain.getFeatures().getFreePreview().getLength().getUnit().equalsIgnoreCase("Minutes")) {
                try {
                    entitlementCheckMultiplier = Integer.parseInt(appCMSMain.getFeatures().getFreePreview().getLength().getMultiplier());
                } catch (Exception e) {
                    //Log.e(TAG, "Error parsing free preview multiplier value: " + e.getMessage());
                }
            }

            maxPreviewSecs = entitlementCheckMultiplier * 60;

            entitlementCheckTimerTask = new TimerTask() {
                @Override
                public void run() {
                    appCMSPresenter.getUserData(userIdentity -> {
                        userIdentityObj = userIdentity;
                        //Log.d(TAG, "Video player entitlement check triggered");
                        if (!entitlementCheckCancelled) {
                            int secsViewed = (int) videoPlayerView.getCurrentPosition() / 1000;
                            if (maxPreviewSecs < secsViewed && (userIdentity == null || !userIdentity.isSubscribed())) {

                                if (onUpdateContentDatumEvent != null) {
                                    AppCMSPresenter.EntitlementPendingVideoData entitlementPendingVideoData
                                            = new AppCMSPresenter.EntitlementPendingVideoData.Builder()
                                            .action(pagePlayAction)
                                            .closerLauncher(false)
                                            .contentDatum(onUpdateContentDatumEvent.getCurrentContentDatum())
                                            .currentlyPlayingIndex(playIndex)
                                            .pagePath(permaLink)
                                            .filmTitle(title)
                                            .extraData(null)
                                            .relatedVideoIds(onUpdateContentDatumEvent.getCurrentRelatedVideoIds())
                                            .currentWatchedTime(videoPlayerView.getCurrentPosition() / 1000)
                                            .build();
                                    appCMSPresenter.setEntitlementPendingVideoData(entitlementPendingVideoData);
                                }

                                //Log.d(TAG, "User is not subscribed - pausing video and showing Subscribe dialog");
                                pauseVideo();

                                if (videoPlayerView != null) {
                                    videoPlayerView.disableController();
                                }
                                if (appCMSPresenter.isUserLoggedIn()) {
                                    appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.SUBSCRIPTION_REQUIRED_PLAYER,
                                            () -> {
                                                if (onClosePlayerEvent != null) {
                                                    onClosePlayerEvent.closePlayer();
                                                }
                                            });
                                } else {
                                    appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED_PLAYER,
                                            () -> {
                                                if (onClosePlayerEvent != null) {
                                                    onClosePlayerEvent.closePlayer();
                                                }
                                            });
                                }
                                cancel();
                                entitlementCheckCancelled = true;
                            } else {
                                //Log.d(TAG, "User is subscribed - resuming video");
                            }
                        }
                    });
                }
            };

            entitlementCheckTimer = new Timer();
            entitlementCheckTimer.schedule(entitlementCheckTimerTask, 1000, 1000);
        }
    }

    private void initVideoListener() {
        videoPlayerView.setOnPlayerStateChanged(playerState -> {
            if (beaconPing != null) {
                beaconPing.playbackState = playerState.getPlaybackState();
            }

            if (playerState.getPlaybackState() == Player.STATE_READY) {
                long updatedRunTime = 0;

                appCMSVideoPageBinder.setAutoplayCancelled(false);
                appCMSVideoPageBinder.setPlayerState(Player.STATE_READY);

                try {
                    updatedRunTime = videoPlayerView.getDuration() / 1000;
                } catch (Exception e) {

                }

                setCurrentWatchProgress(updatedRunTime, watchedTime);

                if (!isVideoLoaded) {
                    videoPlayerView.setCurrentPosition(videoPlayTime * SECS_TO_MSECS);
                    if (!isTrailer) {
                        appCMSPresenter.updateWatchedTime(filmId,
                                videoPlayerView.getCurrentPosition() / 1000);
                    }
                    isVideoLoaded = true;
                }

                if (shouldRequestAds && !isAdDisplayed && adsUrl != null) {
                    requestAds(adsUrl);
                } else {
                    if (beaconBuffer != null) {
                        beaconBuffer.sendBeaconBuffering = false;
                    }

                    if (beaconPing != null) {
                        beaconPing.sendBeaconPing = true;

                        if (!beaconPing.isAlive()) {
                            try {
                                beaconPing.start();
                                mTotalVideoDuration = videoPlayerView.getDuration() / 1000;
                                mTotalVideoDuration -= mTotalVideoDuration % 4;
                                mProgressHandler.post(mProgressRunnable);
                            } catch (Exception e) {
                                //
                            }
                        }
                    }
                }

                if (!TextUtils.isEmpty(mStreamId) && !sentBeaconFirstFrame) {
                    mStopBufferMilliSec = new Date().getTime();
                    ttfirstframe = mStartBufferMilliSec == 0l ? 0d : ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                    appCMSPresenter.sendBeaconMessage(filmId,
                            permaLink,
                            parentScreenName,
                            videoPlayerView.getCurrentPosition(),
                            false,
                            AppCMSPresenter.BeaconEvent.FIRST_FRAME,
                            "Video",
                            videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                            String.valueOf(videoPlayerView.getVideoHeight()),
                            String.valueOf(videoPlayerView.getVideoWidth()),
                            mStreamId,
                            ttfirstframe,
                            0,
                            isVideoDownloaded);
                    sentBeaconFirstFrame = true;

                }

            } else if (playerState.getPlaybackState() == Player.STATE_ENDED) {
                //Log.d(TAG, "Video ended");
                if (shouldRequestAds && adsLoader != null) {
                    adsLoader.contentComplete();
                }

                // close the player if current video is a trailer. We don't want to auto-play it
                if (onClosePlayerEvent != null &&
                        permaLink.contains(trailerKey)) {
                    onClosePlayerEvent.closePlayer();
                    return;
                }

                //if user is not subscribe or ot login than on seek to end dont open autoplay screen# fix for SVFA-2403
                if (appCMSPresenter.isAppSVOD() &&
                        !isTrailer &&
                        !freeContent &&
                        !appCMSPresenter.isUserSubscribed() && !entitlementCheckCancelled && (userIdentityObj == null || !userIdentityObj.isSubscribed())) {
                    showEntitlementDialog = true;
                }

                if (appCMSVideoPageBinder.getPlayerState() != Player.STATE_ENDED &&
                        0 < videoPlayerView.getDuration() &&
                        videoPlayerView.getDuration() <= videoPlayerView.getCurrentPosition()) {
                    if (!appCMSVideoPageBinder.isAutoplayCancelled() &&
                            appCMSVideoPageBinder.getCurrentPlayingVideoIndex() <
                                    appCMSVideoPageBinder.getRelateVideoIds().size()) {
                        if (appCMSPresenter.getAutoplayEnabledUserPref(appCMSPresenter.getCurrentActivity()) &&
                                appCMSVideoPageBinder != null) {
                            appCMSVideoPageBinder.setCurrentPlayingVideoIndex(appCMSVideoPageBinder.getCurrentPlayingVideoIndex() + 1);
                            appCMSPresenter.playNextVideo(appCMSVideoPageBinder,
                                    appCMSVideoPageBinder.getCurrentPlayingVideoIndex() + 1,
                                    appCMSVideoPageBinder.getContentData().getGist().getWatchedTime());
                        }
                    }
                }
                appCMSVideoPageBinder.setAutoplayCancelled(appCMSVideoPageBinder.getPlayerState() == playerState.getPlaybackState());
                appCMSVideoPageBinder.setPlayerState(playerState.getPlaybackState());

                if (onClosePlayerEvent != null && playerState.isPlayWhenReady() && !showEntitlementDialog) {

                    // tell the activity that the movie is finished
                    onClosePlayerEvent.onMovieFinished();
                }

                if (!isTrailer && 30 <= (videoPlayerView.getCurrentPosition() / 1000)) {
                    appCMSPresenter.updateWatchedTime(filmId,
                            videoPlayerView.getCurrentPosition() / 1000);
                }
            } else if (playerState.getPlaybackState() == Player.STATE_BUFFERING ||
                    playerState.getPlaybackState() == Player.STATE_IDLE) {
                if (beaconPing != null) {
                    beaconPing.sendBeaconPing = false;
                }

                if (beaconBuffer != null) {
                    beaconBuffer.sendBeaconBuffering = true;
                    if (!beaconBuffer.isAlive()) {
                        beaconBuffer.start();
                    }
                }

            }

            if (!TextUtils.isEmpty(mStreamId) && !sentBeaconPlay) {
                appCMSPresenter.sendBeaconMessage(filmId,
                        permaLink,
                        parentScreenName,
                        videoPlayerView.getCurrentPosition(),
                        false,
                        AppCMSPresenter.BeaconEvent.PLAY,
                        "Video",
                        videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                        String.valueOf(videoPlayerView.getVideoHeight()),
                        String.valueOf(videoPlayerView.getVideoWidth()),
                        mStreamId,
                        0d,
                        0,
                        isVideoDownloaded);
                sentBeaconPlay = true;
                mStartBufferMilliSec = new Date().getTime();
            }
        });
    }

    private void initAnalytics() {
        appCMSPresenter.sendAppsFlyerFilmViewingEvent(primaryCategory, filmId);
        initFirebaseProgressHandling();
    }

    private void setCurrentWatchProgress(long runTime, long watchedTime) {
        System.out.println("videoPlayerView run time on setcurrent progress-" + runTime + " watch time-" + watchedTime);

        if (runTime > 0 && watchedTime > 0 && runTime > watchedTime) {
            long playDifference = runTime - watchedTime;
            long playTimePercentage = ((watchedTime * 100) / runTime);

            // if video watchtime is greater or equal to 98% of total run time and interval is less than 30 then play from start
            if (playTimePercentage >= 98 && playDifference <= 30) {
                videoPlayTime = 0;
            } else {
                videoPlayTime = watchedTime;
            }
        } else {
            videoPlayTime = 0;
        }

    }

    public void initFirebaseProgressHandling() {
        mProgressHandler = new Handler();
        mProgressRunnable = new Runnable() {
            @Override
            public void run() {
                mProgressHandler.removeCallbacks(this);
                long totalVideoDurationMod4 = mTotalVideoDuration / 4;
                if (totalVideoDurationMod4 > 0) {
                    long mPercentage = (long)
                            (((float) (videoPlayerView.getCurrentPosition() / 1000) / mTotalVideoDuration) * 100);
                    if (appCMSPresenter.getmFireBaseAnalytics() != null) {
                        sendProgressAnalyticEvents(mPercentage);
                    }
                }
                mProgressHandler.postDelayed(this, 1000);
            }
        };
    }

    public void sendProgressAnalyticEvents(long progressPercent) {
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_VIDEO_ID_KEY, filmId);
        bundle.putString(FIREBASE_VIDEO_NAME_KEY, title);
        bundle.putString(FIREBASE_PLAYER_NAME_KEY, FIREBASE_PLAYER_NATIVE);
        bundle.putString(FIREBASE_MEDIA_TYPE_KEY, FIREBASE_MEDIA_TYPE_VIDEO);
        //bundle.putString(FIREBASE_SERIES_ID_KEY, "");
        //bundle.putString(FIREBASE_SERIES_NAME_KEY, "");

        //Logs an app event.
        if (progressPercent == 0 && !isStreamStart) {
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_START, bundle);
            isStreamStart = true;
        }

        if (!isStreamStart) {
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_START, bundle);
            isStreamStart = true;
        }

        if (progressPercent >= 25 && progressPercent < 50 && !isStream25) {
            if (!isStreamStart) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_START, bundle);
                isStreamStart = true;
            }

            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_25, bundle);
            isStream25 = true;
        }

        if (progressPercent >= 50 && progressPercent < 75 && !isStream50) {
            if (!isStream25) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_25, bundle);
                isStream25 = true;
            }

            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_50, bundle);
            isStream50 = true;
        }

        if (progressPercent >= 75 && progressPercent <= 100 && !isStream75) {
            if (!isStream25) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_25, bundle);
                isStream25 = true;
            }

            if (!isStream50) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_50, bundle);
                isStream50 = true;
            }

            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_75, bundle);
            isStream75 = true;
        }

        if (progressPercent >= 98 && progressPercent <= 100 && !isStream100) {
            if (!isStream25) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_25, bundle);
                isStream25 = true;
            }

            if (!isStream50) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_50, bundle);
                isStream50 = true;
            }

            if (!isStream75) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_75, bundle);
                isStream75 = true;
            }

            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_100, bundle);
            isStream100 = true;
        }
    }

    private void pauseVideo() {
        if (shouldRequestAds && adsManager != null && isAdDisplayed) {
            adsManager.pause();
        } else {
            videoPlayerView.pausePlayer();
        }

        if (beaconPing != null) {
            beaconPing.sendBeaconPing = false;
        }
        if (beaconBuffer != null) {
            beaconBuffer.sendBeaconBuffering = false;
        }
    }

    private void resumeVideo() {
        if (shouldRequestAds && adsManager != null && isAdDisplayed) {
            adsManager.resume();
        } else {
            videoPlayerView.resumePlayer();
            if (beaconPing != null) {
                beaconPing.sendBeaconPing = true;
            }
            if (beaconBuffer != null) {
                beaconBuffer.sendBeaconBuffering = true;
            }
        }
    }

    private void requestAds(String adTagUrl) {
        if (!TextUtils.isEmpty(adTagUrl) && adsLoader != null) {
            //Log.d(TAG, "Requesting ads: " + adTagUrl);
            AdDisplayContainer adDisplayContainer = sdkFactory.createAdDisplayContainer();
            adDisplayContainer.setAdContainer(videoPlayerView);

            AdsRequest request = sdkFactory.createAdsRequest();
            request.setAdTagUrl(adTagUrl);
            request.setAdDisplayContainer(adDisplayContainer);
            request.setContentProgressProvider(() -> {
                if (isAdDisplayed || videoPlayerView.getDuration() <= 0) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new VideoProgressUpdate(videoPlayerView.getCurrentPosition(),
                        videoPlayerView.getDuration());
            });

            adsLoader.requestAds(request);
            apod += 1;
        }
    }

    @Override
    public void onRefreshTokenCallback() {
        if (onUpdateContentDatumEvent != null &&
                onUpdateContentDatumEvent.getCurrentContentDatum() != null &&
                onUpdateContentDatumEvent.getCurrentContentDatum().getGist() != null) {
            appCMSPresenter.refreshVideoData(onUpdateContentDatumEvent.getCurrentContentDatum()
                            .getGist()
                            .getId(),
                    updatedContentDatum -> {
                        onUpdateContentDatumEvent.updateContentDatum(updatedContentDatum);
                        appCMSPresenter.getAppCMSSignedURL(filmId, appCMSSignedURLResult -> {
                            if (videoPlayerView != null && appCMSSignedURLResult != null) {
                                boolean foundMatchingMpeg = false;
                                if (!TextUtils.isEmpty(videoUrl) && videoUrl.contains("mp4")) {
                                    if (updatedContentDatum != null &&
                                            updatedContentDatum.getStreamingInfo() != null &&
                                            updatedContentDatum.getStreamingInfo().getVideoAssets() != null &&
                                            updatedContentDatum.getStreamingInfo()
                                                    .getVideoAssets()
                                                    .getMpeg() != null &&
                                            !updatedContentDatum.getStreamingInfo()
                                                    .getVideoAssets()
                                                    .getMpeg()
                                                    .isEmpty()) {
                                        updatedContentDatum.getGist()
                                                .setWatchedTime(videoPlayerView.getCurrentPosition() / 1000L);
                                        for (int i = 0;
                                             i < updatedContentDatum.getStreamingInfo()
                                                     .getVideoAssets()
                                                     .getMpeg()
                                                     .size() &&
                                                     !foundMatchingMpeg;
                                             i++) {
                                            int queryIndex = videoUrl.indexOf("?");
                                            if (0 <= queryIndex) {
                                                if (updatedContentDatum.getStreamingInfo()
                                                        .getVideoAssets()
                                                        .getMpeg()
                                                        .get(0)
                                                        .getUrl()
                                                        .contains(videoUrl.substring(0, queryIndex))) {
                                                    foundMatchingMpeg = true;
                                                    videoUrl = updatedContentDatum.getStreamingInfo()
                                                            .getVideoAssets()
                                                            .getMpeg()
                                                            .get(0)
                                                            .getUrl();
                                                }
                                            }
                                        }
                                    }
                                }

                                videoPlayerView.updateSignatureCookies(appCMSSignedURLResult.getPolicy(),
                                        appCMSSignedURLResult.getSignature(),
                                        appCMSSignedURLResult.getKeyPairId());

                                if (foundMatchingMpeg && updatedContentDatum.getGist() != null) {
                                    videoPlayerView.setUri(Uri.parse(videoUrl),
                                            !TextUtils.isEmpty(closedCaptionUrl) ?
                                                    Uri.parse(closedCaptionUrl) : null);
                                    videoPlayerView.setCurrentPosition(updatedContentDatum.getGist()
                                            .getWatchedTime() * 1000L);
                                }
                            }
                        });
                    });
        }
    }

    @Override
    public void onFinishCallback(String message) {

        AppCMSPresenter.BeaconEvent event;
        if (message.contains("Unable")) {
            event = AppCMSPresenter.BeaconEvent.DROPPED_STREAM;
        } else if (message.contains("Response")) {
            event = AppCMSPresenter.BeaconEvent.FAILED_TO_START;
        } else {
            event = AppCMSPresenter.BeaconEvent.FAILED_TO_START;
        }

        if (!TextUtils.isEmpty(mStreamId)) {
            appCMSPresenter.sendBeaconMessage(filmId,
                    permaLink,
                    parentScreenName,
                    videoPlayerView.getCurrentPosition(),
                    false,
                    event,
                    "Video",
                    videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                    String.valueOf(videoPlayerView.getVideoHeight()),
                    String.valueOf(videoPlayerView.getVideoWidth()),
                    mStreamId,
                    0d,
                    0,
                    isVideoDownloaded);
        }
        if (onClosePlayerEvent != null) {
            onClosePlayerEvent.closePlayer();
        }

        appCMSPresenter.showToast(message, Toast.LENGTH_LONG);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                videoPlayerView.pausePlayer();
                break;

            case AudioManager.AUDIOFOCUS_GAIN:
                if (videoPlayerView.getPlayer() != null && videoPlayerView.getPlayer().getPlayWhenReady()) {
                    videoPlayerView.startPlayer(true);
                } else {
                    videoPlayerView.pausePlayer();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                videoPlayerView.pausePlayer();
                abandonAudioFocus();
                break;

            default:
                break;
        }
    }

    protected void abandonAudioFocus() {
        int result = audioManager.abandonAudioFocus(this);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocusGranted = false;
        }
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        videoPlayerView.resumePlayer();
        sendAdRequest();
        resumeContent();
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        switch (adEvent.getType()) {
            case LOADED:
                adsManager.start();
                break;

            case CONTENT_PAUSE_REQUESTED:
                isAdDisplayed = true;
                if (beaconPing != null) {
                    beaconPing.sendBeaconPing = false;
                    if (mProgressHandler != null) {
                        mProgressHandler.removeCallbacks(mProgressRunnable);
                    }
                }
                sendAdRequest();
                if (!TextUtils.isEmpty(mStreamId) && appCMSPresenter != null) {

                    appCMSPresenter.sendBeaconMessage(filmId,
                            permaLink,
                            parentScreenName,
                            videoPlayerView.getCurrentPosition(),
                            false,
                            AppCMSPresenter.BeaconEvent.AD_IMPRESSION,
                            "Video",
                            videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                            String.valueOf(videoPlayerView.getVideoHeight()),
                            String.valueOf(videoPlayerView.getVideoWidth()),
                            mStreamId,
                            0d,
                            apod,
                            isVideoDownloaded);
                }
                videoPlayerView.pausePlayer();
                break;

            case CONTENT_RESUME_REQUESTED:
                resumeContent();
                break;

            case ALL_ADS_COMPLETED:
                if (adsManager != null) {
                    adsManager.destroy();
                    adsManager = null;
                }
                break;

            default:
                break;
        }
    }

    private void sendAdRequest() {
        if (!TextUtils.isEmpty(mStreamId) && appCMSPresenter != null) {
            appCMSPresenter.sendBeaconMessage(filmId,
                    permaLink,
                    parentScreenName,
                    videoPlayerView.getCurrentPosition(),
                    false,
                    AppCMSPresenter.BeaconEvent.AD_REQUEST,
                    "Video",
                    videoPlayerView.getBitrate() != 0 ? String.valueOf(videoPlayerView.getBitrate()) : null,
                    String.valueOf(videoPlayerView.getVideoHeight()),
                    String.valueOf(videoPlayerView.getVideoWidth()),
                    mStreamId,
                    0d,
                    apod,
                    isVideoDownloaded);
        }
    }

    private void resumeContent() {
        isAdDisplayed = false;
        // videoPlayerView.startPlayer();
        if (beaconPing != null) {
            beaconPing.sendBeaconPing = true;
        }
        if (appCMSPresenter != null) {
            mStopBufferMilliSec = new Date().getTime();
        }
        if (beaconPing != null && !beaconPing.isAlive()) {
            beaconPing.start();

            if (mProgressHandler != null)
                mProgressHandler.post(mProgressRunnable);
        }
    }

    public void stop() {
        if (videoPlayerView != null) {
            videoPlayerView.setOnPlayerStateChanged(null);
        }
        if (beaconPing != null) {
            beaconPing.sendBeaconPing = false;
            beaconPing.runBeaconPing = false;
            beaconPing.videoPlayerView = null;
            beaconPing = null;
        }

        if (mProgressHandler != null) {
            mProgressHandler.removeCallbacks(mProgressRunnable);
            mProgressHandler = null;
        }

        if (beaconBuffer != null) {
            beaconBuffer.sendBeaconBuffering = false;
            beaconBuffer.runBeaconBuffering = false;
            beaconBuffer.videoPlayerView = null;
            beaconBuffer = null;
        }

        onClosePlayerEvent = null;
        if (adsLoader != null) {
            adsLoader.removeAdsLoadedListener(listenerAdsLoaded);
            adsLoader.removeAdErrorListener(this);
        }
        adsLoader = null;
        sentBeaconFirstFrame = false;
        sentBeaconPlay = false;
    }

    public interface OnClosePlayerEvent {
        void closePlayer();

        /**
         * Method is to be called by the fragment to tell the activity that a movie is finished
         * playing. Primarily in the {@link ExoPlayer#STATE_ENDED}
         */
        void onMovieFinished();

        void onRemotePlayback(long currentPosition,
                              int castingMode,
                              boolean sentBeaconPlay,
                              Action1<CastHelper.OnApplicationEnded> onApplicationEndedAction);
    }

    public interface OnUpdateContentDatumEvent {
        void updateContentDatum(ContentDatum contentDatum);

        ContentDatum getCurrentContentDatum();

        List<String> getCurrentRelatedVideoIds();
    }
}
