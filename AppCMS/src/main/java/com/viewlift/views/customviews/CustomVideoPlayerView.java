package com.viewlift.views.customviews;

/**
 * Created by viewlift on 11/17/2017.
 */


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.gson.Gson;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.beacon.thread.BeaconBufferingThread;
import com.viewlift.models.data.appcms.beacon.thread.BeaconPingThread;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.fragments.AppCMSPlayVideoFragment;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.functions.Action1;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static com.google.android.exoplayer2.Player.STATE_READY;
import static com.google.android.gms.internal.zzagr.runOnUiThread;

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

    private BeaconBufferingThread beaconBufferingThread;
    private long beaconBufferingTimeoutMsec;
    private boolean sentBeaconPlay;
    private boolean sentBeaconFirstFrame;
    private BeaconPingThread beaconMessageThread;
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
    private boolean isVideoPlaying=false;

//    public CustomVideoPlayerView(Context context, String videoId) {
//        super(context);
//        mContext = context;
//        this.videoDataId = videoId;
//        appCMSPresenter = ((AppCMSApplication) mContext.getApplicationContext()).getAppCMSPresenterComponent().appCMSPresenter();
//        createLoader();
//        //createPlaybackFullScreen();
//        createCustomMessageView();
//
//
//        mFullScreenButton = createFullScreenToggleButton();
//        ((RelativeLayout) getPlayerView().findViewById(R.id.exo_controller_container)).addView(mFullScreenButton);
//
//
//    }

    public CustomVideoPlayerView(Context context) {
        super(context);
        mContext = context;
        appCMSPresenter = ((AppCMSApplication) mContext.getApplicationContext()).getAppCMSPresenterComponent().appCMSPresenter();
        createLoader();
        //createPlaybackFullScreen();
        createCustomMessageView();


        mFullScreenButton = createFullScreenToggleButton();
        ((RelativeLayout) getPlayerView().findViewById(R.id.exo_controller_container)).addView(mFullScreenButton);
        setupAds();
        createPreviewMessageView();


        try {
            mStreamId = appCMSPresenter.getStreamingId(videoDataId);
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
            mStreamId = videoDataId + appCMSPresenter.getCurrentTimeStamp();
        }

        setFirebaseProgressHandling();


        parentScreenName = getContext().getString(R.string.app_cms_beacon_video_player_parent_screen_name);

        beaconMsgTimeoutMsec = getResources().getInteger(R.integer.app_cms_beacon_timeout_msec);
        beaconBufferingTimeoutMsec = getResources().getInteger(R.integer.app_cms_beacon_buffering_timeout_msec);

        beaconMessageThread = new BeaconPingThread(beaconMsgTimeoutMsec,
                appCMSPresenter,
                videoDataId,
                permaLink,
                isTrailer,
                parentScreenName,
                this,
                mStreamId);

        beaconBufferingThread = new BeaconBufferingThread(beaconBufferingTimeoutMsec,
                appCMSPresenter,
                videoDataId,
                permaLink,
                parentScreenName,
                this,
                mStreamId);
    }

    public void setVideoId(String videoId) {
        this.videoDataId = videoId;


    }

    public String getVideoId() {
        return videoDataId ;

    }

    public void setupAds() {
        //this.adsUrl = adsUrl;


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

    ContentDatum onUpdatedContentDatum;

    public void setVideoUri(String videoId, int resIdMessage) {
        showProgressBar(getResources().getString(resIdMessage));
        releasePlayer();
        init(mContext);
        getPlayerView().hideController();
        isVideoDownloaded = appCMSPresenter.isVideoDownloaded(videoDataId);
        appCMSPresenter.refreshVideoData(videoId, contentDatum -> {
            onUpdatedContentDatum = contentDatum;
            getPermalink(contentDatum);
            setWatchedTime(contentDatum);
            System.out.println("Runnung Timer is free "+contentDatum.getGist().getFree());
            if (!contentDatum.getGist().getFree()) {
                //check login and subscription first.
                if (!appCMSPresenter.isUserLoggedIn()) {
                    if (shouldRequestAds){
                        requestAds(adsUrl);
                    }else{
                        playVideos(0, contentDatum);
                    }
                    getVideoPreview();
                    //showRestrictMessage(getResources().getString(R.string.app_cms_subscribe_text_message));
                } else {
                    //check subscription data
                    appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                        try {
                            if (appCMSUserSubscriptionPlanResult != null) {
                                String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                                if (subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                        subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                                    if (shouldRequestAds && !appCMSPresenter.getPreviewStatus()){
                                        requestAds(adsUrl);
                                    }else{
                                        playVideos(0, contentDatum);
                                    }
                                    appCMSPresenter.setPreviewStatus(false);

                                } else {
                                    if (shouldRequestAds && !appCMSPresenter.getPreviewStatus()){
                                        requestAds(adsUrl);
                                    }else{
                                        playVideos(0, contentDatum);
                                    }
                                    getVideoPreview();
                                    //showRestrictMessage(getResources().getString(R.string.app_cms_subscribe_text_message));
                                }
                            } else {

                                if (shouldRequestAds && !appCMSPresenter.getPreviewStatus()){
                                    requestAds(adsUrl);
                                }else{
                                    playVideos(0, contentDatum);
                                }
                                getVideoPreview();
                                //showRestrictMessage(getResources().getString(R.string.app_cms_subscribe_text_message));
                            }
                        } catch (Exception e) {
                            showRestrictMessage(getResources().getString(R.string.app_cms_subscribe_text_message));
                        }
                    });
                }
            } else {
                if (shouldRequestAds){
                    requestAds(adsUrl);
                }else{
                    playVideos(0, contentDatum);
                }

            }


            //System.out.println(" JSON for Video details " + new Gson().toJson(contentDatum));
        });
        videoDataId = videoId;
        sentBeaconPlay=false;
        sentBeaconFirstFrame=false;


    }

//    public void showFullScreenController(){
//        if(getPlayerView()!=null) {
//            getPlayerView().showController();
//
//            if(getPlayerView()!=null) {
////                getPlayerView().getController();
//                PlaybackControlView playbackControlView = getPlayerView().getController();
//                FrameLayout mFullScreenButton = (FrameLayout) playbackControlView.findViewById(R.id.exo_fullscreen_button);
//                ImageView mFullScreenIcon = (ImageView) playbackControlView.findViewById(R.id.exo_fullscreen_icon);
//                mFullScreenIcon.setImageResource(R.drawable.ic_fullscreen_expand);
//                mFullScreenButton.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        appCMSPresenter.launchFullScreenStandalonePlayer(videoDataId);
//                    }
//                });
//            }
//        }
//    }

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
        if (contentDatum!=null &&
                contentDatum.getGist() != null &&
                contentDatum.getGist().getKisweEventId()!= null ){
            appCMSPresenter.launchKiswePlayer(contentDatum.getGist().getKisweEventId());
            pausePlayer();
            return;
        }

        if (null != customPlayBack)
            customPlayBack.setVisibility(View.VISIBLE);

        String url = null;
        String closedCaptionUrl = null;
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
            isLiveStream = contentDatum.getStreamingInfo().getIsLiveStream();

            if (playerView != null && playerView.getController() != null)  {
                playerView.getController().setPlayingLive(isLiveStream);
            }
        }

        if (null != url) {
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
            permaLink= contentDatum.getGist().getPermalink();
            long duration = contentDatum.getGist().getRuntime();
            if (duration <= watchedTime) {
                watchedTime = 0L;
            }
            videoTitle= contentDatum.getGist().getTitle();
        }
    }

    private boolean checkVideoSubscriptionStatus(ContentDatum contentDatum) {
        final boolean[] isSubscribe = {false};
        if (!contentDatum.getGist().getFree()) {
            //check login and subscription first.
            if (!appCMSPresenter.isUserLoggedIn()) {
                isSubscribe[0] = false;
            } else {
                //check subscription data
                appCMSPresenter.getSubscriptionData(appCMSUserSubscriptionPlanResult -> {
                    try {
                        if (appCMSUserSubscriptionPlanResult != null) {
                            String subscriptionStatus = appCMSUserSubscriptionPlanResult.getSubscriptionInfo().getSubscriptionStatus();
                            if (subscriptionStatus.equalsIgnoreCase("COMPLETED") ||
                                    subscriptionStatus.equalsIgnoreCase("DEFERRED_CANCELLATION")) {
                                isSubscribe[0] = true;
                            } else {
                                isSubscribe[0] = false;
                            }
                        } else {
                            isSubscribe[0] = false;
                        }
                    } catch (Exception e) {
                        isSubscribe[0] = false;
                    }
                });
            }
        } else {
            isSubscribe[0] = true;
        }
        return isSubscribe[0];
    }

    public Timer entitlementCheckTimer;
    public TimerTask entitlementCheckTimerTask;
    private boolean entitlementCheckCancelled = false;
    int maxPreviewSecs = 0;
    int playedVideoSecs = 0;
    int secsViewed=0;
    private void getVideoPreview() {

        if (appCMSPresenter.isAppSVOD() &&
                !appCMSPresenter.isUserSubscribed()) {
            if(appCMSPresenter.getPreviewStatus()){
                pausePlayer();
                showPreviewFrame();
                return;
            }
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
                        if (!entitlementCheckCancelled &&  getPlayerView().getPlayer()!=null &&  getPlayerView().getPlayer().getPlayWhenReady()) {
                            if(!isLiveStream && appCMSMain.getFeatures().getFreePreview().isPer_video()){
                                secsViewed = (int) getPlayer().getCurrentPosition() / 1000;
                            }
                            if(!appCMSMain.getFeatures().getFreePreview().isPer_video()){
                                playedVideoSecs=appCMSPresenter.getPreviewTimerValue();
                            }
                            if (((maxPreviewSecs < playedVideoSecs )|| (maxPreviewSecs < secsViewed ))&& (userIdentity == null || !userIdentity.isSubscribed()) ) {


                                if (onUpdatedContentDatum != null) {
                                    AppCMSPresenter.EntitlementPendingVideoData entitlementPendingVideoData
                                            = new AppCMSPresenter.EntitlementPendingVideoData.Builder()
                                            .action(getContext().getString(R.string.app_cms_page_play_key))
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showPreviewFrame();
                                    }
                                });
                                pausePlayer();
                                cancel();
                                entitlementCheckCancelled = true;


                            } else {
                                //Log.d(TAG, "User is subscribed - resuming video");
                            }
                            System.out.println("Runnung Timer"+playedVideoSecs);
                            playedVideoSecs++;
                            appCMSPresenter.setPreviewTimerValue(playedVideoSecs);
                        }
                    });

                }
            };

            entitlementCheckTimer = new Timer();
            entitlementCheckTimer.schedule(entitlementCheckTimerTask, 1000, 1000);
        }else{
            appCMSPresenter.setPreviewStatus(false);
        }
    }

    private void showPreviewFrame(){
        appCMSPresenter.setPreviewStatus(true);
        customPreviewContainer.setVisibility(View.VISIBLE);
        if (appCMSPresenter.isUserLoggedIn()) {
            btnLogin.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if (beaconMessageThread != null) {
            beaconMessageThread.playbackState = playbackState;
        }
        switch (playbackState) {
            case STATE_ENDED:
                pausePlayer();
                if (null != relatedVideoId && currentPlayingIndex <= relatedVideoId.size() - 1) {
                    //showProgressBar("Loading Next Video...");
                    if (entitlementCheckTimer!=null) {
                        entitlementCheckTimer.cancel();
                    }
                    setVideoUri(relatedVideoId.get(currentPlayingIndex), R.string.loading_next_video_text);

                    /*appCMSPresenter.refreshVideoData(relatedVideoId.get(currentPlayingIndex), new Action1<ContentDatum>() {
                        @Override
                        public void call(ContentDatum contentDatum) {
                            if (!checkVideoSubscriptionStatus(contentDatum)) {
                                showRestrictMessage("This video is only available to Monumental Sports Network subscribers");
                                return;
                            }
                            hideRestrictedMessage();
                            setUri(Uri.parse(contentDatum.getStreamingInfo().getVideoAssets().getHls()), null);
                            getPlayerView().getPlayer().setPlayWhenReady(true);
                            hideProgressBar();
                        }
                    });*/
                } else {
                    showRestrictMessage(getResources().getString(R.string.app_cms_video_ended_text_message));
                }
                //Log.d(TAG, "Video ended");
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
                isVideoPlaying=false;
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

                break;
            case STATE_READY:
                hideProgressBar();

                if( getPlayerView().getPlayer().getPlayWhenReady()){
                    isVideoPlaying=true;
                }else{
                    isVideoPlaying=false;
                }

                long updatedRunTime = 0;
                try {
                    updatedRunTime = getDuration() / 1000;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                videoPlayTime= appCMSPresenter.setCurrentWatchProgress(updatedRunTime, watchedTime);

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

                        }
                    }
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
        }
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

    private void createPlaybackFullScreen() {
        customPlayBack = new LinearLayout(mContext);
        customPlayBack.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams llLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        customPlayBack.setLayoutParams(llLinear);

        customPlayBack.setGravity(Gravity.BOTTOM | Gravity.RIGHT);

        ImageView imgFullScreen = new ImageView(mContext);
        imgFullScreen.setScaleType(ImageView.ScaleType.FIT_XY);
        imgFullScreen.setBackground(mContext.getDrawable(R.drawable.full_screen_player_icon));
        LinearLayout.LayoutParams paramsImgFullScreen = new LinearLayout.LayoutParams(BaseView.dpToPx(R.dimen.full_screen_item_min_width, mContext), BaseView.dpToPx(R.dimen.full_screen_item_min_width, mContext));

        paramsImgFullScreen.setMargins(0, 0, 32, 32);

        imgFullScreen.setLayoutParams(paramsImgFullScreen);
        imgFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                appCMSPresenter.launchFullScreenStandalonePlayer(videoDataId);
            }
        });
        customPlayBack.addView(imgFullScreen);
        customPlayBack.setVisibility(View.INVISIBLE);

        this.addView(customPlayBack);
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

    private void createPreviewMessageView() {
        customPreviewContainer = new LinearLayout(mContext);
        customPreviewContainer.setOrientation(LinearLayout.VERTICAL);
        customPreviewContainer.setGravity(Gravity.CENTER);
        customPreviewContainer.setBackgroundColor(Color.parseColor("#000000"));
        customMessageView = new TextView(mContext);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.gravity = Gravity.CENTER;
        customMessageView.setText(getResources().getString(R.string.app_cms_live_preview_text_message));
        customMessageView.setLayoutParams(textViewParams);
        customMessageView.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));
        customMessageView.setTextSize(15);
        customMessageView.setPadding(20, 20, 20, 20);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(5,5,5,5);

        LinearLayout previewBtnsLayout = new LinearLayout(mContext);
        previewBtnsLayout.setOrientation(LinearLayout.HORIZONTAL);
        previewBtnsLayout.setGravity(Gravity.CENTER);

        btnStartFreeTrial = new Button(mContext);
        btnStartFreeTrial.setBackgroundColor(Color.parseColor(appCMSPresenter.getTabBarUIFooterModule().getTabSeparator_color()));
        btnStartFreeTrial.setText(getResources().getString(R.string.app_cms_start_free_trial));
        btnStartFreeTrial.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));
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
        btnLogin.setText(getResources().getString(R.string.app_cms_login));
        btnLogin.setBackgroundColor(Color.parseColor(appCMSPresenter.getTabBarUIFooterModule().getTabSeparator_color()));
        btnLogin.setTextColor(Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor()));
        btnLogin.setPadding(10, 10, 10, 10);
        btnLogin.setGravity(Gravity.CENTER);
        btnLogin.setLayoutParams(buttonParams);

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                appCMSPresenter.setEntitlementPendingVideoData(null);
                appCMSPresenter.navigateToLoginPage(false);
            }
        });

        previewBtnsLayout.addView(btnStartFreeTrial);
        previewBtnsLayout.addView(btnLogin);

        customPreviewContainer.addView(customMessageView);
        customPreviewContainer.addView(previewBtnsLayout);

        customPreviewContainer.setVisibility(View.INVISIBLE);
        this.addView(customPreviewContainer);
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
            hideProgressBar();
            loaderMessageView.setTextColor(getResources().getColor(android.R.color.white));
            customMessageView.setText(message);
            customMessageContainer.setVisibility(View.VISIBLE);

        }


    }

    private void hideRestrictedMessage() {
        if (null != customMessageContainer) {
            customMessageContainer.setVisibility(View.INVISIBLE);
        }


    }

    protected ToggleButton createFullScreenToggleButton() {
        ToggleButton mToggleButton = new ToggleButton(getContext());
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
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //todo work on maximizing the player on this event
                if (isChecked) {
                        /*new Handler().postDelayed(() -> {
                            if (appCMSPresenter.isAutoRotate() && !BaseView.isTablet(mContext)) {
                                appCMSPresenter.unrestrictPortraitOnly();
                            }
                        }, 3000);*/
                    //appCMSPresenter.getCurrentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    appCMSPresenter.showFullScreenPlayer();

                } else {
                    //appCMSPresenter.getCurrentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

                       /* new Handler().postDelayed(() -> {
                            if (appCMSPresenter.isAutoRotate() && !BaseView.isTablet(mContext)) {
                                appCMSPresenter.unrestrictPortraitOnly();
                            }
                        }, 3000);*/
                    appCMSPresenter.exitFullScreenPlayer();
                }
                /*if (isChecked){
                    FrameLayout.LayoutParams frameLayoutParams=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    setLayoutParams(frameLayoutParams);
                }else{
                    setLayoutParams(baseLayoutParms);
                }*/
                /*if ((appCMSPresenter.isAppSVOD() && appCMSPresenter.isUserSubscribed()) ||
                        !appCMSPresenter.isAppSVOD() && appCMSPresenter.isUserLoggedIn()) {

                    if (isChecked) {
                        new Handler().postDelayed(() -> {
                            if (appCMSPresenter.isAutoRotate() && !BaseView.isTablet(mContext)) {
                                appCMSPresenter.unrestrictPortraitOnly();
                            }
                        }, 3000);
                        //appCMSPresenter.getCurrentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                        appCMSPresenter.showFullScreenPlayer();

                    } else {
                        //appCMSPresenter.getCurrentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

                        new Handler().postDelayed(() -> {
                            if (appCMSPresenter.isAutoRotate() && !BaseView.isTablet(mContext)) {
                                appCMSPresenter.unrestrictPortraitOnly();
                            }
                        }, 3000);
                        appCMSPresenter.exitFullScreenPlayer();
                    }


                } else {
                    if (appCMSPresenter.isAppSVOD()) {
                        if (appCMSPresenter.isUserLoggedIn()) {
                            appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.SUBSCRIPTION_REQUIRED,
                                    () -> {
                                        appCMSPresenter.setAfterLoginAction(() -> {
                                        });
                                    });
                        } else {
                            appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_AND_SUBSCRIPTION_REQUIRED,
                                    () -> {
                                        appCMSPresenter.setAfterLoginAction(() -> {
                                        });
                                    });
                        }
                    } else if (!(appCMSPresenter.isAppSVOD() && appCMSPresenter.isUserLoggedIn())) {
                        appCMSPresenter.showEntitlementDialog(AppCMSPresenter.DialogType.LOGIN_REQUIRED,
                                () -> {
                                });
                    }
                }*/
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
       /* boolean serviceType = appCMSPresenter.getAppCMSMain().getServiceType().equals(
                mContext.getString(R.string.app_cms_main_svod_service_type_key));*/
        if(/*!serviceType &&*/ contentDatum != null) {
            adsUrl = appCMSPresenter.getAdsUrl(appCMSPresenter.getPermalinkCompletePath(contentDatum.getGist().getPermalink()));
        }
        if ( adsUrl != null && !TextUtils.isEmpty(adsUrl)) {
            shouldRequestAds = true;
        } else {
            shouldRequestAds = false;
        }
        shouldRequestAds = true;
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
        playVideos(0,onUpdatedContentDatum);
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
                isAdDisplayed = true;
                pausePlayer();
                break;

            case CONTENT_RESUME_REQUESTED:
                isAdDisplayed = false;
                break;

            case ALL_ADS_COMPLETED:
                if (adsManager != null) {
                    adsManager.destroy();
                    adsManager = null;
                }
                isAdsDisplaying = false;
                playVideos(0,onUpdatedContentDatum);
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

    private void setWatchedTime(ContentDatum contentDatum){
        if(contentDatum != null) {
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

}

