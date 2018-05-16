package com.viewlift.views.customviews;

/**
 * Created by viewlift on 11/17/2017.
 */


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.trackselection.FixedTrackSelection;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.gms.internal.zzahn;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.casting.CastingUtils;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.beacon.BeaconBuffer;
import com.viewlift.models.data.appcms.beacon.BeaconPing;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AppCMSPageActivity;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static com.google.android.exoplayer2.Player.STATE_READY;
import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class CustomVideoPlayerView extends VideoPlayerView implements AdErrorEvent.AdErrorListener,
        AdEvent.AdEventListener {

    private static final String TAG = CustomVideoPlayerView.class.getSimpleName();
    private Context mContext;
    private AppCMSPresenter appCMSPresenter;
    private FrameLayout.LayoutParams baseLayoutParms;
    private LinearLayout customLoaderContainer;
    private TextView loaderMessageView;
    private LinearLayout customMessageContainer;
    private LinearLayout customPreviewContainer;
    private RelativeLayout parentView;
    private LinearLayout llTopBar;
    private TextView app_cms_video_player_title_view;

    private ViewCreator.VideoPlayerContent videoPlayerContent;

    private TextView customMessageView;
    private LinearLayout customPlayBack;
    private String videoDataId = null;
    private String videoTitle = null;
    int currentPlayingIndex = 0;
    List<String> relatedVideoId;
    private ToggleButton mFullScreenButton;
    private boolean shouldRequestAds = false;
    private boolean isADPlay;
    private ImaSdkFactory sdkFactory;
    private AdsLoader adsLoader;
    private AdsManager adsManager;
    private String adsUrl;
    private boolean isAdDisplayed;
    private boolean isAdsDisplaying;
    private Button btnLogin;
    private Button btnStartFreeTrial;
    private boolean isLiveStream;

    private long watchedPercentage = 0;
    private final String FIREBASE_STREAM_START = "stream_start";
    private final String FIREBASE_STREAM_25 = "stream_25_pct";
    private final String FIREBASE_STREAM_50 = "stream_50_pct";
    private final String FIREBASE_STREAM_75 = "stream_75_pct";
    private final String FIREBASE_STREAM_100 = "stream_100_pct";

    private final String FIREBASE_VIDEO_ID_KEY = "video_id";
    private final String FIREBASE_VIDEO_NAME_KEY = "video_name";
    private final String FIREBASE_SERIES_ID_KEY = "series_id";
    private final String FIREBASE_SERIES_NAME_KEY = "series_name";
    private final String FIREBASE_PLAYER_NAME_KEY = "player_name";
    private final String FIREBASE_MEDIA_TYPE_KEY = "media_type";
    private final String FIREBASE_PLAYER_NATIVE = "Native";
    private final String FIREBASE_PLAYER_CHROMECAST = "Chromecast";
    private final String FIREBASE_MEDIA_TYPE_VIDEO = "Video";
    private final String FIREBASE_SCREEN_VIEW_EVENT = "screen_view";
    Handler mProgressHandler;
    Runnable mProgressRunnable;
    long mTotalVideoDuration;
    boolean isStreamStart, isStream25, isStream50, isStream75, isStream100;
    boolean lastPlayState = false;

    private BeaconBuffer beaconBufferingThread;
    private long beaconBufferingTimeoutMsec;
    private boolean sentBeaconPlay;
    private boolean sentBeaconFirstFrame;
    private BeaconPing beaconMessageThread;
    private long beaconMsgTimeoutMsec;
    private String mStreamId;
    private String permaLink;
    private String parentScreenName;
    boolean isVideoDownloaded;
    boolean isTrailer;
    private long mStartBufferMilliSec = 0l;
    private long mStopBufferMilliSec;
    private static double ttfirstframe = 0d;
    private long watchedTime = 0l;
    private static final long SECS_TO_MSECS = 1000L;
    private long videoPlayTime = 0l;
    private boolean isVideoLoaded = false;
    private CustomVideoPlayerView videoPlayerViewSingle;

    private boolean isVideoPlaying = true;
    private boolean isTimerRun = true;
    public String lastUrl = "", closedCaptionUri = "";
    ContentDatum onUpdatedContentDatum;
    public boolean isPreviewShown = false;

    protected int currentTrackIndex = 0;
    private static final TrackSelection.Factory FIXED_FACTORY = new FixedTrackSelection.Factory();

    private ToggleButton mToggleButton;
    public boolean hideMiniPlayer = false;
    private int entitlementCheckMultiplier = 0;

    public CustomVideoPlayerView(Context context, AppCMSPresenter appCMSPresenter) {
        super(context, appCMSPresenter);
        mContext = context;
        this.appCMSPresenter = appCMSPresenter;
        //appCMSPresenter = ((AppCMSApplication) mContext.getApplicationContext()).getAppCMSPresenterComponent().appCMSPresenter();
        createLoader();
        mFullScreenButton = createFullScreenToggleButton();
        ((RelativeLayout) getPlayerView().findViewById(R.id.exo_controller_container)).addView(mFullScreenButton);
        setupAds();
        createPreviewMessageView();
        touchToCastOverlay();
        createTopBarView();
        try {
            mStreamId = appCMSPresenter.getStreamingId(videoDataId);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            mStreamId = videoDataId + appCMSPresenter.getCurrentTimeStamp();
        }

        videoPlayerContent =new ViewCreator.VideoPlayerContent();
        videoPlayerViewSingle = this;
        setFirebaseProgressHandling();

        videoPlayerViewSingle = this;

        parentScreenName = getContext().getString(R.string.app_cms_beacon_video_player_parent_screen_name);

        beaconMsgTimeoutMsec = getResources().getInteger(R.integer.app_cms_beacon_timeout_msec);
        beaconBufferingTimeoutMsec = getResources().getInteger(R.integer.app_cms_beacon_buffering_timeout_msec);

        beaconMessageThread = new BeaconPing(beaconMsgTimeoutMsec,
                appCMSPresenter,
                videoDataId,
                permaLink,
                isTrailer,
                parentScreenName,
                this,
                mStreamId,onUpdatedContentDatum);

        beaconBufferingThread = new BeaconBuffer(beaconBufferingTimeoutMsec,
                appCMSPresenter,
                videoDataId,
                permaLink,
                parentScreenName,
                this,
                mStreamId,onUpdatedContentDatum);

    }

    public void setVideoId(String videoId) {
        this.videoDataId = videoId;
    }

    public String getVideoId() {
        return videoDataId;
    }

    public void setupAds() {
        sdkFactory = ImaSdkFactory.getInstance();
        adsLoader = sdkFactory.createAdsLoader(getContext());
        adsLoader.addAdErrorListener(this);
        adsLoader.addAdsLoadedListener(adsManagerLoadedEvent -> {
            adsManager = adsManagerLoadedEvent.getAdsManager();
            adsManager.addAdErrorListener(this);
            adsManager.addAdEventListener(this);
            adsManager.init();
        });
    }

    public void setVideoUri(String videoId, int resIdMessage) {
        showOverlayWhenCastingConnected();
        hideRestrictedMessage();
        showProgressBar(getResources().getString(resIdMessage));
        releasePlayer();
        init(mContext);
        //getPlayerView().hideController();
        isVideoDownloaded = appCMSPresenter.isVideoDownloaded(videoDataId);
        appCMSPresenter.refreshVideoData(videoId, contentDatum -> {
            onUpdatedContentDatum = contentDatum;
            getPermalink(contentDatum);
            setWatchedTime(contentDatum);
            if (!contentDatum.getGist().getFree()) {
                //check login and subscription first.
                if (!appCMSPresenter.isUserLoggedIn() && !appCMSPresenter.getPreviewStatus()) {
                    getVideoPreview();

                    if(entitlementCheckMultiplier > 0) {
                        if (shouldRequestAds) {
                            requestAds(adsUrl);
                        } else {
                            playVideos(0, contentDatum);
                        }
                    }
                    appCMSPresenter.setPreviewStatus(false);
                    if (!appCMSPresenter.isAppSVOD()){
                        playVideos(0, contentDatum);
                    }
                } else {
                    if (appCMSPresenter.isUserSubscribed()) {
                        playVideos(0, contentDatum);
                        appCMSPresenter.setPreviewStatus(false);
                    } else {
                        getVideoPreview();
                        if (shouldRequestAds && !appCMSPresenter.getPreviewStatus()) {
                            requestAds(adsUrl);
                        } else {
                            if(entitlementCheckMultiplier > 0) {
                                playVideos(0, contentDatum);
                            }
                        }
                    }
                }
            } else {
                if (shouldRequestAds) {
                    requestAds(adsUrl);
                } else {
                    playVideos(0, contentDatum);
                }
            }
            setTopBarStatus();
        });
        videoDataId = videoId;
        sentBeaconPlay = false;
        sentBeaconFirstFrame = false;
    }

    private void setTopBarStatus() {
        setOnPlayerControlsStateChanged(visibility -> {
            if (visibility == View.GONE) {
                llTopBar.setVisibility(View.GONE);
            } else if (visibility == View.VISIBLE && mToggleButton != null && mToggleButton.isChecked()) {
                llTopBar.setVisibility(View.VISIBLE);
            }
        });
        if (onUpdatedContentDatum != null &&
                onUpdatedContentDatum.getGist() != null &&
                onUpdatedContentDatum.getGist().getTitle() != null &&
                app_cms_video_player_title_view != null) {
            app_cms_video_player_title_view.setText(onUpdatedContentDatum.getGist().getTitle());
        }

    }

    public void checkVideoStatus() {
        setTopBarStatus();

        setVideoPlayerStatus();
        CastServiceProvider.getInstance(mContext).setVideoPlayerMediaButton(mediaButton);

        CastServiceProvider.getInstance(mContext).onActivityResume();
        if (isPreviewShown) {
            pausePlayer();
            showPreviewFrame();
        } else {
            hidePreviewFrame();
        }

        if (onUpdatedContentDatum != null && !onUpdatedContentDatum.getGist().getFree()) {
            //check login and subscription first.
            if (!appCMSPresenter.isUserLoggedIn() && !appCMSPresenter.getPreviewStatus()) {
                getVideoPreview();
            } else {
                if (appCMSPresenter.isAppSVOD() &&
                        !appCMSPresenter.isUserSubscribed()) {
                    getVideoPreview();
                } else {
                    hidePreviewFrame();
                }
            }
        }
    }

    private int currentIndex(String videoId) {
        if (relatedVideoId != null && relatedVideoId.size() < currentPlayingIndex)
            for (int i = 0; i < relatedVideoId.size(); i++) {
                if (videoId.equalsIgnoreCase(relatedVideoId.get(i))) {
                    return i;
                }
            }
        return 0;
    }

    private void playVideos(int currentIndex, ContentDatum contentDatum) {
        customPreviewContainer.setVisibility(View.GONE);
        hideRestrictedMessage();
        if (contentDatum != null &&
                contentDatum.getGist() != null &&
                contentDatum.getGist().getKisweEventId() != null) {
            appCMSPresenter.launchKiswePlayer(contentDatum.getGist().getKisweEventId());
            pausePlayer();
            return;
        }
        if (null != customPlayBack)
            customPlayBack.setVisibility(View.VISIBLE);
        String url = null;
        String closedCaptionUrl = null;
        permaLink = contentDatum.getGist().getPermalink();
        if (null != contentDatum && null != contentDatum.getStreamingInfo() && null != contentDatum.getStreamingInfo().getVideoAssets()) {
            if (null != contentDatum.getStreamingInfo().getVideoAssets().getHls()) {
                url = contentDatum.getStreamingInfo().getVideoAssets().getHls();
            } else if (null != contentDatum.getStreamingInfo().getVideoAssets().getMpeg()
                    && contentDatum.getStreamingInfo().getVideoAssets().getMpeg().size() > 0) {
                url = contentDatum.getStreamingInfo().getVideoAssets().getMpeg().get(0).getUrl();
            }
            if (contentDatum.getContentDetails() != null
                    && contentDatum.getContentDetails().getClosedCaptions() != null
                    && !contentDatum.getContentDetails().getClosedCaptions().isEmpty()) {
                for (ClosedCaptions cc : contentDatum.getContentDetails().getClosedCaptions()) {
                    if (cc.getUrl() != null &&
                            !cc.getUrl().equalsIgnoreCase(getContext().getString(R.string.download_file_prefix)) &&
                            cc.getFormat() != null &&
                            cc.getFormat().equalsIgnoreCase("SRT")) {
                        closedCaptionUrl = cc.getUrl();
                    }
                }
            }
            if (playerView != null && playerView.getController() != null) {
                playerView.getController().setPlayingLive(isLiveStream);
            }
        }

        playerView.getController().setPlayerEvents(isVideoPaused -> {

            isVideoPlaying = !isVideoPaused;
        });
        if (null != url) {
            lastUrl = url;
            closedCaptionUri = closedCaptionUrl;
            setBeaconData();
            setUri(Uri.parse(url), closedCaptionUrl == null ? null : Uri.parse(closedCaptionUrl));
            setCurrentPosition(watchedPercentage);
            resumePlayer();
            if (currentIndex == 0) {
                relatedVideoId = contentDatum.getContentDetails().getRelatedVideoIds();
            }
            currentPlayingIndex = currentIndex(contentDatum.getGist().getId());
            hideProgressBar();

            if (contentDatum != null &&
                    contentDatum.getGist() != null &&
                    contentDatum.getGist().getWatchedTime() != 0) {
                watchedTime = contentDatum.getGist().getWatchedTime();
            }
            long duration = contentDatum.getGist().getRuntime();
            if (duration <= watchedTime) {
                watchedTime = 0L;
            }
            videoTitle = contentDatum.getGist().getTitle();

            videoPlayerContent.videoUrl = lastUrl;
            videoPlayerContent.ccUrl = closedCaptionUri;
            videoPlayerContent.videoPlayTime = getCurrentPosition()/SECS_TO_MSECS;
        }
    }

    public Timer entitlementCheckTimer;
    public TimerTask entitlementCheckTimerTask;
    private boolean entitlementCheckCancelled = false;
    int maxPreviewSecs = 0;
    int playedVideoSecs = 0;
    int secsViewed = 0;

    public void getVideoPreview() {

        if (entitlementCheckTimer != null) {
            entitlementCheckTimer.cancel();
            entitlementCheckTimer = null;
        }
        if (appCMSPresenter.isAppSVOD() &&
                !appCMSPresenter.isUserSubscribed()) {
            entitlementCheckCancelled = false;

            AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
            if (appCMSMain != null &&
                    appCMSMain.getFeatures() != null &&
                    appCMSMain.getFeatures().getFreePreview() != null &&
                    appCMSMain.getFeatures().getFreePreview().isFreePreview()) {
                if (appCMSMain.getFeatures().getFreePreview().getLength() != null &&
                        appCMSMain.getFeatures().getFreePreview().getLength().getUnit().equalsIgnoreCase("Minutes")) {
                    try {
                        entitlementCheckMultiplier = Integer.parseInt(appCMSMain.getFeatures().getFreePreview().getLength().getMultiplier());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing free preview multiplier value: " + e.getMessage());
                    }
                }
            }

            maxPreviewSecs = entitlementCheckMultiplier * 60;
            entitlementCheckTimerTask = new TimerTask() {
                @Override
                public void run() {
                    appCMSPresenter.getUserData(userIdentity -> {
                        if (!entitlementCheckCancelled && isTimerRun) {
                            if (!isLiveStream && appCMSMain.getFeatures().getFreePreview().isPerVideo()) {
                                secsViewed = (int) getPlayer().getCurrentPosition() / 1000;
                            }
                            if (!appCMSMain.getFeatures().getFreePreview().isPerVideo()) {
                                playedVideoSecs = appCMSPresenter.getPreviewTimerValue();
                            }
                            if (((maxPreviewSecs < playedVideoSecs) || (maxPreviewSecs < secsViewed)) && (userIdentity == null || !userIdentity.isSubscribed())) {
                                //if mini player is showing than dismiss the mini player
                                runOnUiThread(() -> appCMSPresenter.dismissPopupWindowPlayer(false));

                                if (onUpdatedContentDatum != null) {
                                    AppCMSPresenter.EntitlementPendingVideoData entitlementPendingVideoData
                                            = new AppCMSPresenter.EntitlementPendingVideoData.Builder()
                                            //.action(getContext().getString(R.string.app_cms_page_play_key))
                                            .closerLauncher(false)
                                            .contentDatum(onUpdatedContentDatum)
                                            .currentlyPlayingIndex(0)
                                            .pagePath(onUpdatedContentDatum.getGist().getPermalink())
                                            .filmTitle(onUpdatedContentDatum.getGist().getTitle())
                                            .extraData(null)
                                            .relatedVideoIds(onUpdatedContentDatum.getContentDetails().getRelatedVideoIds())
                                            .currentWatchedTime(getPlayer().getCurrentPosition() / 1000)
                                            .build();
                                    appCMSPresenter.setEntitlementPendingVideoData(entitlementPendingVideoData);
                                }
                                appCMSPresenter.setPreviewStatus(true);
                                pausePlayer();
                                hideMiniPlayer = true;
                                showPreviewFrame();


                                cancel();
                                entitlementCheckCancelled = true;

                            } else {
                                hideMiniPlayer = false;

                            }
                            playedVideoSecs++;
                            appCMSPresenter.setPreviewTimerValue(playedVideoSecs);

                        }
                    });

                }
            };

            entitlementCheckTimer = new Timer();
            entitlementCheckTimer.schedule(entitlementCheckTimerTask, 1000, 1000);
        } else {
            appCMSPresenter.setPreviewStatus(false);
            hidePreviewFrame();
        }

    }

    private void showPreviewFrame() {
        disableController();
        isPreviewShown = true;
        appCMSPresenter.setPreviewStatus(true);
        customPreviewContainer.post(new Runnable() {
            @Override
            public void run() {
                customPreviewContainer.setVisibility(View.VISIBLE);

            }
        });
        if (appCMSPresenter.isUserLoggedIn()) {
            btnLogin.setVisibility(View.GONE);
        } else {
            btnLogin.setVisibility(View.VISIBLE);
        }
    }

    private void hidePreviewFrame() {
        hideMiniPlayer = false;

        enableController();
        isPreviewShown = false;
        customPreviewContainer.post(new Runnable() {
            @Override
            public void run() {
                customPreviewContainer.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if (beaconMessageThread != null) {
            beaconMessageThread.playbackState = playbackState;
        }
        switch (playbackState) {
            case STATE_ENDED:
                pausePlayer();
                createCustomMessageView();
                if (null != relatedVideoId && currentPlayingIndex <= relatedVideoId.size() - 1) {
                    if (entitlementCheckTimer != null) {
                        entitlementCheckTimer.cancel();
                    }
                    if (appCMSPresenter.getAutoplayEnabledUserPref(mContext)) {
                        setVideoUri(relatedVideoId.get(currentPlayingIndex), R.string.loading_next_video_text);

                    } else {
                        //disableController();
                        showRestrictMessage(getResources().getString(R.string.autoplay_off_msg));
                    }

                } else {
                    showRestrictMessage(getResources().getString(R.string.app_cms_video_ended_text_message));
                }
                if (shouldRequestAds && adsLoader != null) {
                    adsLoader.contentComplete();
                }

                if (!isTrailer && 30 <= (getCurrentPosition() / 1000) && !isLiveStream) {
                    appCMSPresenter.updateWatchedTime(videoDataId,
                            getCurrentPosition() / 1000);
                }
                break;
            case STATE_BUFFERING:
            case STATE_IDLE:
                showProgressBar("Streaming...");
                if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = false;
                }
                if (beaconBufferingThread != null) {
                    beaconBufferingThread.sendBeaconBuffering = true;
                    if (!beaconBufferingThread.isAlive()) {
                        beaconBufferingThread.start();
                    }
                }
                ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                break;
            case STATE_READY:
                hideProgressBar();

                if (getPlayerView().getPlayer().getPlayWhenReady()) {
                    ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                } else {
                    ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                }

                long updatedRunTime = 0;
                try {
                    updatedRunTime = getDuration() / 1000;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                videoPlayTime = appCMSPresenter.setCurrentWatchProgress(updatedRunTime, watchedTime);

                if (!isVideoLoaded) {
                    setCurrentPosition(videoPlayTime * SECS_TO_MSECS);
                    if (!isTrailer && !isLiveStream) {
                        appCMSPresenter.updateWatchedTime(videoDataId, getCurrentPosition() / 1000);
                    }
                    isVideoLoaded = true;
                }
                if (shouldRequestAds && !isAdDisplayed && adsUrl != null) {
                    //requestAds(adsUrl);
                } else {
                    if (beaconBufferingThread != null) {
                        beaconBufferingThread.sendBeaconBuffering = false;
                    }
                    if (beaconMessageThread != null && !isLiveStream) {
                        beaconMessageThread.sendBeaconPing = true;
                        if (!beaconMessageThread.isAlive()) {
                            try {
                                beaconMessageThread.start();
                                mTotalVideoDuration = getDuration() / 1000;
                                mTotalVideoDuration -= mTotalVideoDuration % 4;
                                mProgressHandler.post(mProgressRunnable);
                            } catch (Exception e) {

                            }
                        }
                        if (!sentBeaconFirstFrame) {
                            mStopBufferMilliSec = new Date().getTime();
                            ttfirstframe = mStartBufferMilliSec == 0l ? 0d : ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                            appCMSPresenter.sendBeaconMessage(videoDataId,
                                    permaLink,
                                    parentScreenName,
                                    getCurrentPosition(),
                                    false,
                                    AppCMSPresenter.BeaconEvent.FIRST_FRAME,
                                    "Video",
                                    getBitrate() != 0 ? String.valueOf(getBitrate()) : null,
                                    String.valueOf(getVideoHeight()),
                                    String.valueOf(getVideoWidth()),
                                    mStreamId,
                                    ttfirstframe,
                                    0,
                                    isVideoDownloaded);
                            sentBeaconFirstFrame = true;
                            appCMSPresenter.sendGaEvent(mContext.getResources().getString(R.string.play_video_action),
                                    mContext.getResources().getString(R.string.play_video_category), videoDataId);

                        }
                    }
                }
                if (CastServiceProvider.getInstance(mContext).isCastingConnected()) {
                    pausePlayer();
                }

                break;

            default:
                hideProgressBar();
        }

        if (!sentBeaconPlay) {
            appCMSPresenter.sendBeaconMessage(videoDataId,
                    permaLink,
                    parentScreenName,
                    getCurrentPosition(),
                    false,
                    AppCMSPresenter.BeaconEvent.PLAY,
                    "Video",
                    getBitrate() != 0 ? String.valueOf(getBitrate()) : null,
                    String.valueOf(getVideoHeight()),
                    String.valueOf(getVideoWidth()),
                    mStreamId,
                    0d,
                    0,
                    isVideoDownloaded);
            sentBeaconPlay = true;
            mStartBufferMilliSec = new Date().getTime();

            appCMSPresenter.sendGaEvent(mContext.getResources().getString(R.string.play_video_action),
                    mContext.getResources().getString(R.string.play_video_category), videoDataId);
        }
    }

    private void setVideoPlayerStatus() {
        showOverlayWhenCastingConnected();
        if (mToggleButton != null && mToggleButton.isChecked()) {
            llTopBar.setVisibility(View.VISIBLE);
        } else {
            llTopBar.setVisibility(View.GONE);
        }
        ((AppCMSPageActivity) mContext).setCastingVisibility(true);

    }

    public void pausePlayer() {
        if (null != getPlayer()) {
            getPlayer().setPlayWhenReady(false);
        }
    }


    public void resumePlayer() {
        if (null != getPlayer() && !getPlayer().getPlayWhenReady()) {
            getPlayer().setPlayWhenReady(true);
        }
    }

    class ForegroundObserver extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void resumePlayerLastState() {
        if (null != getPlayer()) {
            if (isVideoPlaying) {
              try {
                if(new ForegroundObserver().execute(mContext).get())
                  getPlayer().setPlayWhenReady(true);
              }catch (Exception ex){
                ex.printStackTrace();
              }
            } else {
                getPlayer().setPlayWhenReady(false);
            }
        }
    }

    public void releasePlayer() {
        if (getPlayer() != null) {
            getPlayer().release();
        }
    }

    private void createLoader() {
        customLoaderContainer = new LinearLayout(mContext);
        customLoaderContainer.setOrientation(LinearLayout.VERTICAL);
        customLoaderContainer.setGravity(Gravity.CENTER);
        ProgressBar progressBar = new ProgressBar(mContext);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().
                setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent),
                        PorterDuff.Mode.MULTIPLY
                );
        LinearLayout.LayoutParams progressbarParam = new LinearLayout.LayoutParams(50, 50);
        progressBar.setLayoutParams(progressbarParam);
        customLoaderContainer.addView(progressBar);
        loaderMessageView = new TextView(mContext);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loaderMessageView.setLayoutParams(textViewParams);
        customLoaderContainer.addView(loaderMessageView);
        this.addView(customLoaderContainer);
    }

    private void createCustomMessageView() {
        customMessageContainer = new LinearLayout(mContext);
        customMessageContainer.setOrientation(LinearLayout.HORIZONTAL);
        customMessageContainer.setGravity(Gravity.CENTER);
        customMessageContainer.setBackgroundColor(Color.parseColor("#d4000000"));
        customMessageView = new TextView(mContext);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.gravity = Gravity.CENTER;
        customMessageView.setLayoutParams(textViewParams);
        customMessageView.setTextColor(Color.parseColor("#ffffff"));
        customMessageView.setTextSize(15);
        customMessageView.setPadding(20, 20, 20, 20);

        customMessageContainer.addView(customMessageView);
        customMessageContainer.setVisibility(View.INVISIBLE);
        this.addView(customMessageContainer);
    }

    public void showOverlayWhenCastingConnected() {
        if (CastServiceProvider.getInstance(mContext).isCastingConnected()) {
            if (parentView != null) {
                customMessageView.setText(getResources().getString(R.string.app_cms_touch_to_cast_msg));
                parentView.setVisibility(VISIBLE);
            }
            pausePlayer();

            if (CastingUtils.getRemoteMediaId(mContext) != null && onUpdatedContentDatum != null) {
                String filmId = CastingUtils.getRemoteMediaId(mContext);
                if (filmId.equalsIgnoreCase(""))
                    customMessageView.setText(CastingUtils.getCurrentPlayingVideoName(mContext));
                else
                    customMessageView.setText("Casting the " + CastingUtils.getCurrentPlayingVideoName(mContext) + " to " + CastServiceProvider.getInstance(mContext).getConnectedDeviceName());
            }
        } else {
            if (parentView != null) {
                parentView.setVisibility(GONE);
            }
        }
    }


    private void touchToCastOverlay() {

        parentView = new RelativeLayout(mContext);
        parentView.setClickable(true);
        parentView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.backgroundColor));
        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parentView.setLayoutParams(imageViewParams);
        ImageView defaultIcon = new ImageView(mContext);
        defaultIcon.setPadding(30, 20, 30, 20);

        defaultIcon.setLayoutParams(imageViewParams);
        defaultIcon.setImageResource(R.drawable.logo);
        parentView.addView(defaultIcon);

        customMessageView = new TextView(mContext);
        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);// = Gravity.CENTER;
        customMessageView.setText(getResources().getString(R.string.app_cms_touch_to_cast_msg));
        customMessageView.setLayoutParams(textViewParams);
        customMessageView.setBackgroundColor(Color.parseColor("#CC000000"));
        customMessageView.setTextColor(Color.parseColor("#ffffff"));
        customMessageView.setTextSize(20);
//        customMessageView.setAlpha(0.7f);
        customMessageView.setTypeface(customMessageView.getTypeface(), Typeface.BOLD);
        customMessageView.setPadding(30, 20, 30, 20);
        parentView.addView(customMessageView);

        customMessageView.setOnClickListener((v) -> {

            if (CastingUtils.getRemoteMediaId(mContext) != null && onUpdatedContentDatum != null) {
                String filmId = CastingUtils.getRemoteMediaId(mContext);
                if (filmId.equalsIgnoreCase("") || (!filmId.equalsIgnoreCase(onUpdatedContentDatum.getGist().getId()))) {
                    CastServiceProvider.getInstance(mContext).launchSingeRemoteMedia(onUpdatedContentDatum.getGist().getTitle(), permaLink, onUpdatedContentDatum.getGist().getVideoImageUrl(), lastUrl, onUpdatedContentDatum.getGist().getId(), 0, false);
                }
            }
        });


        if (CastServiceProvider.getInstance(mContext).isCastingConnected()) {
            String filmId = CastingUtils.getRemoteMediaId(mContext);
            if (filmId.equalsIgnoreCase(""))
                customMessageView.setText(CastingUtils.getCurrentPlayingVideoName(mContext));
            else
                customMessageView.setText("Casting the " + CastingUtils.getCurrentPlayingVideoName(mContext) + " to " + CastServiceProvider.getInstance(mContext).getConnectedDeviceName());
        }

        parentView.setVisibility(View.GONE);
        this.addView(parentView);

    }


    private void createPreviewMessageView() {
        int buttonColor, textColor;
        if (appCMSPresenter != null &&
                appCMSPresenter.getGeneralBackgroundColor() != 0 &&
                appCMSPresenter.getBrandPrimaryCtaColor() != 0 ) {
            buttonColor = appCMSPresenter.getBrandPrimaryCtaColor();
            textColor = appCMSPresenter.getGeneralTextColor();

        } else {

            buttonColor = Color.parseColor(String.valueOf(R.color.colorAccent));
            textColor = Color.parseColor("#ffffff");
        }

        customPreviewContainer = new LinearLayout(mContext);
        customPreviewContainer.setOrientation(LinearLayout.VERTICAL);
        customPreviewContainer.setGravity(Gravity.CENTER);
        customPreviewContainer.setBackgroundColor(Color.parseColor("#000000"));
        customMessageView = new TextView(mContext);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.gravity = Gravity.CENTER;
        String message = null;
        if (appCMSPresenter != null &&
                appCMSPresenter.getSubscriptionFlowContent() != null &&
                appCMSPresenter.getSubscriptionFlowContent().getOverlayMessage() != null) {
            message = appCMSPresenter.getSubscriptionFlowContent().getOverlayMessage();
        } else {
            message = getResources().getString(R.string.app_cms_live_preview_text_message);
        }
        customMessageView.setText(message);
        customMessageView.setLayoutParams(textViewParams);
        customMessageView.setTextColor(appCMSPresenter.getGeneralTextColor());
        customMessageView.setTextSize(15);
        customMessageView.setPadding(20, 20, 20, 20);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(5, 5, 5, 5);

        LinearLayout previewBtnsLayout = new LinearLayout(mContext);
        previewBtnsLayout.setOrientation(LinearLayout.HORIZONTAL);
        previewBtnsLayout.setGravity(Gravity.CENTER);

        btnStartFreeTrial = new Button(mContext);
        btnStartFreeTrial.setBackgroundColor(buttonColor);
        if (appCMSPresenter.getAppCMSAndroid() != null && appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent() != null
                && appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent().getSubscriptionButtonText() != null) {
            btnStartFreeTrial.setText(appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent().getSubscriptionButtonText());
        } else {
            btnStartFreeTrial.setText(getResources().getString(R.string.app_cms_start_free_trial));
        }
        btnStartFreeTrial.setTextColor(textColor);
        btnStartFreeTrial.setPadding(10, 10, 10, 10);
        btnStartFreeTrial.setLayoutParams(buttonParams);

        btnStartFreeTrial.setGravity(Gravity.CENTER);

        btnStartFreeTrial.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                appCMSPresenter.setEntitlementPendingVideoData(null);
                appCMSPresenter.navigateToSubscriptionPlansPage(false);

            }
        });
        btnLogin = new Button(mContext);
        if (appCMSPresenter.getAppCMSAndroid() != null && appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent() != null
                && appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent().getLoginButtonText() != null) {
            btnLogin.setText(appCMSPresenter.getAppCMSAndroid().getSubscriptionFlowContent().getLoginButtonText());
        } else {
            btnLogin.setText(getResources().getString(R.string.app_cms_login));
        }
        btnLogin.setBackgroundColor(buttonColor);
        btnLogin.setTextColor(textColor);
        btnLogin.setPadding(10, 10, 10, 10);
        btnLogin.setGravity(Gravity.CENTER);
        btnLogin.setLayoutParams(buttonParams);

        btnLogin.setOnClickListener(view -> {
            appCMSPresenter.setEntitlementPendingVideoData(null);
            appCMSPresenter.navigateToLoginPage(false);
        });

        previewBtnsLayout.addView(btnStartFreeTrial);
        previewBtnsLayout.addView(btnLogin);

        customPreviewContainer.addView(customMessageView);
        customPreviewContainer.addView(previewBtnsLayout);

        customPreviewContainer.setVisibility(View.INVISIBLE);
        this.addView(customPreviewContainer);
    }

    ImageButton mediaButton, app_cms_video_player_done_button;

    private void createTopBarView() {
        llTopBar = new LinearLayout(mContext);
        llTopBar.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater li = LayoutInflater.from(mContext);
        View layout = li.inflate(R.layout.custom_video_player_top_bar, null, false);
        mediaButton = layout.findViewById(R.id.media_route_button);
        app_cms_video_player_done_button = layout.findViewById(R.id.app_cms_video_player_done_button);
        app_cms_video_player_title_view = layout.findViewById(R.id.app_cms_video_player_title_view);
        app_cms_video_player_done_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                appCMSPresenter.restrictPortraitOnly();
                appCMSPresenter.exitFullScreenPlayer();
                if (appCMSPresenter.videoPlayerView != null) {
                    appCMSPresenter.videoPlayerView = null;
                }
                mToggleButton.setChecked(false);
            }
        });
        layout.setLayoutParams(llParams);

        llTopBar.setLayoutParams(llParams);
        llTopBar.addView(layout);
        llTopBar.setVisibility(View.VISIBLE);
        CastServiceProvider.getInstance(mContext).setVideoPlayerMediaButton(mediaButton);

        this.addView(llTopBar);
    }


    private void showProgressBar(String text) {
        if (null != customLoaderContainer && null != loaderMessageView) {
            loaderMessageView.setText(text);
            loaderMessageView.setTextColor(getResources().getColor(android.R.color.white));
            customLoaderContainer.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (null != customLoaderContainer) {
            customLoaderContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void showRestrictMessage(String message) {
        if (null != customMessageContainer && null != customMessageView) {
            disableController();
            hideProgressBar();
            loaderMessageView.setTextColor(getResources().getColor(android.R.color.white));
            customMessageView.setText(message);
            customMessageContainer.setVisibility(View.VISIBLE);
        }
    }

    private void hideRestrictedMessage() {
        if (null != customMessageContainer) {
            enableController();
            customMessageContainer.setVisibility(View.INVISIBLE);
        }
    }

    protected ToggleButton createFullScreenToggleButton() {
        mToggleButton = new ToggleButton(getContext());
        RelativeLayout.LayoutParams toggleLP = new RelativeLayout.LayoutParams(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_width, getContext()), BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_width, getContext()));
        toggleLP.addRule(RelativeLayout.CENTER_VERTICAL);
        toggleLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        toggleLP.setMarginStart(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_left_margin, getContext()));
        mToggleButton.setLayoutParams(toggleLP);
        mToggleButton.setChecked(false);
        mToggleButton.setTextOff("");
        mToggleButton.setTextOn("");
        mToggleButton.setText("");
        mToggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.full_screen_toggle_selector, null));
        mToggleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!appCMSPresenter.isFullScreenVisible) {
                    appCMSPresenter.isExitFullScreen = true;
                } else {
                    appCMSPresenter.isExitFullScreen = false;

                }
            }
        });
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //todo work on maximizing the player on this event
                if (isChecked) {

                    if (appCMSPresenter.videoPlayerView == null) {
                        appCMSPresenter.videoPlayerView = videoPlayerViewSingle;
                    }

                    appCMSPresenter.restrictLandscapeOnly();
                    appCMSPresenter.showFullScreenPlayer();
                    llTopBar.setVisibility(View.VISIBLE);
                } else {
                    AppCMSPresenter.isFullScreenVisible = false;

                    llTopBar.setVisibility(View.GONE);
                    appCMSPresenter.restrictPortraitOnly();

                    appCMSPresenter.exitFullScreenPlayer();
                    if (appCMSPresenter.videoPlayerView != null) {
                        appCMSPresenter.videoPlayerView = null;
                    }
                }

            }
        });

        return mToggleButton;
    }

    public void updateFullscreenButtonState(int screenMode) {
        switch (screenMode) {
            case Configuration.ORIENTATION_LANDSCAPE:
                mFullScreenButton.setChecked(true);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                mFullScreenButton.setChecked(false);
                break;
            default:
                mFullScreenButton.setChecked(true);
                break;
        }
    }

    private void getPermalink(ContentDatum contentDatum) {
        if (contentDatum != null) {
            if (contentDatum.getStreamingInfo() != null) {
                isLiveStream = contentDatum.getStreamingInfo().getIsLiveStream();
            }
            if (!isLiveStream && !appCMSPresenter.isUserSubscribed()) {
                adsUrl = appCMSPresenter.getAdsUrl(appCMSPresenter.getPermalinkCompletePath(contentDatum.getGist().getPermalink()));
            }
        }
        shouldRequestAds = adsUrl != null && !TextUtils.isEmpty(adsUrl);
    }

    private void requestAds(String adTagUrl) {
        if (!TextUtils.isEmpty(adTagUrl) && adsLoader != null) {
            Log.d(TAG, "Requesting ads: " + adTagUrl);
            AdDisplayContainer adDisplayContainer = sdkFactory.createAdDisplayContainer();
            adDisplayContainer.setAdContainer(this);

            AdsRequest request = sdkFactory.createAdsRequest();
            request.setAdTagUrl(adTagUrl);
            request.setAdDisplayContainer(adDisplayContainer);

            adsLoader.requestAds(request);
            isAdsDisplaying = true;
        }
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Log.d(TAG, "OnAdError: " + adErrorEvent.getError().getMessage());
        isTimerRun = true;
        playVideos(0, onUpdatedContentDatum);
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        Log.i(TAG, "onAdEvent: " + adEvent.getType());
        switch (adEvent.getType()) {
            case LOADED:
                if (adsManager != null) {
                    adsManager.start();
                    isAdsDisplaying = true;
                }
                break;

            case CONTENT_PAUSE_REQUESTED:
                isTimerRun = false;
                isAdDisplayed = true;
                pausePlayer();
                break;

            case CONTENT_RESUME_REQUESTED:
                isAdDisplayed = false;
                break;

            case ALL_ADS_COMPLETED:
                isTimerRun = true;
                if (adsManager != null) {
                    adsManager.destroy();
                    adsManager = null;
                }
                isAdsDisplaying = false;
                playVideos(0, onUpdatedContentDatum);
                break;
            default:
                break;
        }
    }

    public void setFirebaseProgressHandling() {
        mProgressHandler = new Handler();
        mProgressRunnable = new Runnable() {
            @Override
            public void run() {
                mProgressHandler.removeCallbacks(this);
                long totalVideoDurationMod4 = mTotalVideoDuration / 4;
                if (totalVideoDurationMod4 > 0) {
                    long mPercentage = (long)
                            (((float) (getCurrentPosition() / 1000) / mTotalVideoDuration) * 100);
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
        bundle.putString(FIREBASE_VIDEO_ID_KEY, videoDataId);
        bundle.putString(FIREBASE_VIDEO_NAME_KEY, videoTitle);
        bundle.putString(FIREBASE_PLAYER_NAME_KEY, FIREBASE_PLAYER_NATIVE);
        bundle.putString(FIREBASE_MEDIA_TYPE_KEY, FIREBASE_MEDIA_TYPE_VIDEO);
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

    private void setWatchedTime(ContentDatum contentDatum) {
        if (contentDatum != null) {
            if (contentDatum.getGist().getWatchedPercentage() > 0) {
                watchedPercentage = contentDatum.getGist().getWatchedPercentage();
            } else {
                long watchedTime = contentDatum.getGist().getWatchedTime();
                long runTime = contentDatum.getGist().getRuntime();
                if (watchedTime > 0 && runTime > 0) {
                    watchedPercentage = (long) (((double) watchedTime / (double) runTime) * 100.0);
                }
            }
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        String errorString = null;
        if (e instanceof ExoPlaybackException) {
            errorString = e.getCause().toString();
            setUri(Uri.parse(lastUrl), closedCaptionUri == null ? null : Uri.parse(closedCaptionUri));
        }
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = mContext.getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = mContext.getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = mContext.getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = mContext.getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        } else if (e.type == ExoPlaybackException.TYPE_SOURCE) {
            MappingTrackSelector.SelectionOverride override = new MappingTrackSelector.SelectionOverride(FIXED_FACTORY, 0, currentTrackIndex++);
            MappingTrackSelector.MappedTrackInfo currentMappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (currentMappedTrackInfo != null
                    && currentMappedTrackInfo.getTrackGroups(0) != null
                    && currentMappedTrackInfo.getTrackGroups(0).get(0) != null
                    && (currentTrackIndex <= currentMappedTrackInfo.getTrackGroups(0).get(0).length)) {
                if ((player.getCurrentPosition() + 5000) >= player.getDuration()) {
                    if (appCMSPresenter.isNetworkConnected()) {
                        currentPlayingIndex++;
                        Toast.makeText(mContext, "There is some video playback error", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (appCMSPresenter.isNetworkConnected()) {
                        trackSelector.setSelectionOverride(0, currentMappedTrackInfo.getTrackGroups(0), override);
                        init(mContext);
                    } else {
                        if (isBehindLiveWindow(e)) {
                            init(mContext);
                            setUri(Uri.parse(lastUrl), closedCaptionUri == null ? null : Uri.parse(closedCaptionUri));
                        }
                    }
                }
            }
        } else {

            if (errorString != null) {
                Toast.makeText(mContext, errorString, Toast.LENGTH_LONG).show();
            }
            if (isBehindLiveWindow(e)) {
                init(mContext);
                setUri(Uri.parse(lastUrl), closedCaptionUri == null ? null : Uri.parse(closedCaptionUri));
            }
        }
        Log.e("Playback exception", errorString);

    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private void setBeaconData() {
        try {
            mStreamId = appCMSPresenter.getStreamingId(videoDataId);
        } catch (Exception e) {
            mStreamId = videoDataId + appCMSPresenter.getCurrentTimeStamp();
        }
        beaconBufferingThread.setBeaconData(videoDataId, permaLink, mStreamId);
        beaconMessageThread.setBeaconData(videoDataId, permaLink, mStreamId);
    }

    public interface IgetPlayerEvent {

        void getIsVideoPaused(boolean isVideoPaused);
    }

    public ViewCreator.VideoPlayerContent getVideoPlayerContent() {
        return videoPlayerContent;
    }
}

