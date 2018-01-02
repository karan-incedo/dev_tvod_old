package com.viewlift.presenters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.analytics.AppsFlyerUtils;
import com.viewlift.casting.CastHelper;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.models.data.appcms.api.AppCMSSignedURLResult;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.Mpeg;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.models.data.appcms.beacon.BeaconBuffer;
import com.viewlift.models.data.appcms.beacon.BeaconPing;
import com.viewlift.models.data.appcms.downloads.DownloadStatus;
import com.viewlift.models.data.appcms.ui.authentication.UserIdentity;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.views.binders.AppCMSVideoPageBinder;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.customviews.VideoPlayerView;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.fragments.AppCMSPlayVideoFragment;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.functions.Action1;

/**
 * Created by viewlift on 1/1/18.
 */

public class VideoPlayerPresenter implements AdErrorEvent.AdErrorListener,
        AdEvent.AdEventListener,
        VideoPlayerView.ErrorEventListener,
        Animation.AnimationListener,
        AudioManager.OnAudioFocusChangeListener,
        VideoPlayerView.StreamingQualitySelector{

    private static final long SECS_TO_MSECS = 1000L;
    private static final String PLAYER_SCREEN_NAME = "Player Screen";
    private static double ttfirstframe = 0d;
    private static int apod = 0;
    private static boolean isVideoDownloaded;
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
    private final int totalCountdownInMillis = 2000;
    private final int countDownIntervalInMillis = 20;
    private Handler mProgressHandler;
    private Runnable mProgressRunnable;
    private long mTotalVideoDuration;
    private Animation animSequential, animFadeIn, animFadeOut, animTranslate;
    private boolean isStreamStart, isStream25, isStream50, isStream75, isStream100;
    private int maxPreviewSecs = 0;
    private AppCMSPresenter appCMSPresenter;
    private String fontColor;
    private String title;
    private String hlsUrl;
    private String permaLink;
    private boolean isTrailer;
    private String filmId;
    private String primaryCategory;
    private String imageUrl;
    private String parentScreenName;
    private String adsUrl;
    private String parentalRating;
    private boolean freeContent;
    private boolean shouldRequestAds;
    private LinearLayout videoPlayerInfoContainer;
    private RelativeLayout videoPlayerMainContainer;
    private PercentRelativeLayout contentRatingMainContainer;
    private PercentRelativeLayout contentRatingAnimationContainer;
    private LinearLayout contentRatingInfoContainer;
    private ImageButton videoPlayerViewDoneButton;
    private TextView videoPlayerTitleView;
    private TextView contentRatingHeaderView;
    private TextView contentRatingDiscretionView;
    private TextView contentRatingTitleHeader;
    private TextView contentRatingTitleView;
    private TextView contentRatingBack;
    private View contentRatingBackUnderline;

    private String ageRatingConvertedG;
    private String ageRatingConvertedPG;
    private String ageRagingConvertedDefault;

    private String downloadFilePrefix;
    private String watchedTimeKey;

    private View fullScreenVideoPlayerRoot;
    private VideoPlayerView fullScreenVideoPlayerView;
    private VideoPlayerView embeddedVideoPlayerView;

    private AppCMSVideoPageBinder videoPlayerViewBinder;
    private boolean ignoreBinderUpdate;

    private LinearLayout videoLoadingProgress;
    private OnClosePlayerEvent onClosePlayerEvent;
    private OnUpdateContentDatumEvent onUpdateContentDatumEvent;
    private BeaconPing beaconPing;
    private long beaconMsgTimeoutMsec;
    private String policyCookie;
    private String signatureCookie;
    private String keyPairIdCookie;
    private boolean isVideoLoaded = false;
    private BeaconBuffer beaconBuffer;
    private long beaconBufferingTimeoutMsec;
    private boolean sentBeaconPlay;
    private boolean sentBeaconFirstFrame;
    private ImaSdkFactory sdkFactory;
    private AdsLoader adsLoader;
    private AdsManager adsManager;
    AdsLoader.AdsLoadedListener listenerAdsLoaded = adsManagerLoadedEvent -> {
        adsManager = adsManagerLoadedEvent.getAdsManager();
        adsManager.addAdErrorListener(VideoPlayerPresenter.this);
        adsManager.addAdEventListener(VideoPlayerPresenter.this);
        adsManager.init();
    };
    private VideoPlayerView.StreamingQualitySelector streamingQualitySelector;
    private boolean showEntitlementDialog = false;
    private String mStreamId;
    private long mStartBufferMilliSec = 0l;
    private long mStopBufferMilliSec;
    private ProgressBar progressBar;
    private Runnable seekListener;
    private int progressCount = 0;
    private Handler seekBarHandler;
    private boolean showCRWWarningMessage;

    private boolean mAudioFocusGranted = false;
    private AudioManager audioManager;

    private boolean isAdDisplayed;
    private int playIndex;
    private long watchedTime;
    private long runTime;
    private long videoPlayTime = 0;

    private CastServiceProvider castProvider;
    private String closedCaptionUrl;
    private boolean isCastConnected;

    CastServiceProvider.ILaunchRemoteMedia callBackRemotePlayback = castingModeChromecast -> {
        if (onClosePlayerEvent != null) {
            pauseVideo();
            long castPlayPosition = watchedTime * SECS_TO_MSECS;
            if (!isCastConnected) {
                castPlayPosition = fullScreenVideoPlayerView.getCurrentPosition();
            }

            onClosePlayerEvent.onRemotePlayback(castPlayPosition,
                    castingModeChromecast,
                    sentBeaconPlay,
                    onApplicationEnded -> {
                        //
                    });
        }
    };

    private UserIdentity userIdentityObj;
    private boolean refreshToken;
    private Timer refreshTokenTimer;
    private TimerTask refreshTokenTimerTask;
    private Timer entitlementCheckTimer;
    private TimerTask entitlementCheckTimerTask;
    private boolean entitlementCheckCancelled = false;

    private PageView pageView;

    private Map<String, String> availableStreamingFormats;
    private String contentRating;
    private String videoImageUrl;
    private long videoRunTime;
    private List<String> relateVideoIds;
    private int currentlyPlayingIndex = 0;

    public void init(Activity activity,
                     ContentDatum contentDatum,
                     Action1<Bundle> onBundleReadyAction) {
        initAppCMSPresenter(activity);
        initAudioManager(activity);
        initVideoPlayerViewBinder(contentDatum);

        String videoTitleTextColor = appCMSPresenter.getAppTextColor();
        setVideoTitle(contentDatum.getGist().getTitle(),
                Color.parseColor(ViewCreator.getColor(activity, videoTitleTextColor)));

        initBundle(activity,
                activity.getString(R.string.default_video_resolution),
                activity.getResources().getBoolean(R.bool.use_hls),
                getHlsUrl(contentDatum),
                onBundleReadyAction);
    }

    public void initAppCMSPresenter(Activity activity) {
        appCMSPresenter =
                ((AppCMSApplication) activity.getApplication())
                        .getAppCMSPresenterComponent()
                        .appCMSPresenter();
    }

    public void initAudioManager(Context context) {
        audioManager = (AudioManager) context.getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
    }

    public void initVideoPlayerViewBinder(ContentDatum contentDatum) {
        videoPlayerViewBinder =
                appCMSPresenter.getDefaultAppCMSVideoPageBinder(contentDatum,
                        -1,
                        contentDatum.getContentDetails().getRelatedVideoIds(),
                        false,
                        false,  /** TODO: Replace with a value that is true if the video is a trailer */
                        !appCMSPresenter.isAppSVOD(),
                        appCMSPresenter.getAppAdsURL(contentDatum.getGist().getPermalink()),
                        appCMSPresenter.getAppBackgroundColor());
    }

    public void attach(Context context) {
        if (context instanceof AppCMSPlayVideoFragment.OnClosePlayerEvent) {
            onClosePlayerEvent = (OnClosePlayerEvent) context;
        }
        if (context instanceof AppCMSPlayVideoFragment.OnUpdateContentDatumEvent) {
            onUpdateContentDatumEvent = (OnUpdateContentDatumEvent) context;
        }
        if (context instanceof VideoPlayerView.StreamingQualitySelector) {
            streamingQualitySelector = (VideoPlayerView.StreamingQualitySelector) context;
        }
    }

    public void create(Bundle args, Activity activity) {
        if (args != null) {
            fontColor = args.getString(activity.getString(R.string.video_player_font_color_key));
            title = args.getString(activity.getString(R.string.video_player_title_key));
            permaLink = args.getString(activity.getString(R.string.video_player_permalink_key));
            isTrailer = args.getBoolean(activity.getString(R.string.video_player_is_trailer_key));
            hlsUrl = args.getString(activity.getString(R.string.video_player_hls_url_key));
            filmId = args.getString(activity.getString(R.string.video_layer_film_id_key));
            adsUrl = args.getString(activity.getString(R.string.video_player_ads_url_key));
            shouldRequestAds = args.getBoolean(activity.getString(R.string.video_player_request_ads_key));
            playIndex = args.getInt(activity.getString(R.string.play_index_key));
            watchedTime = args.getLong(activity.getString(R.string.watched_time_key));
            runTime = args.getLong(activity.getString(R.string.run_time_key));

            imageUrl = args.getString(activity.getString(R.string.played_movie_image_url));
            closedCaptionUrl = args.getString(activity.getString(R.string.video_player_closed_caption_key));
            primaryCategory = args.getString(activity.getString(R.string.video_primary_category_key));
            parentalRating = args.getString(activity.getString(R.string.video_player_content_rating_key));

            freeContent = args.getBoolean(activity.getString(R.string.free_content_key));

            policyCookie = args.getString(activity.getString(R.string.signed_policy_key));
            signatureCookie = args.getString(activity.getString(R.string.signed_signature_key));
            keyPairIdCookie = args.getString(activity.getString(R.string.signed_keypairid_key));

            refreshToken = !(TextUtils.isEmpty(policyCookie) ||
                    TextUtils.isEmpty(signatureCookie) ||
                    TextUtils.isEmpty(keyPairIdCookie));
        }

        hlsUrl = hlsUrl.replaceAll(" ", "+");

        sentBeaconPlay = (0 < playIndex && watchedTime != 0);

        downloadFilePrefix = activity.getString(R.string.download_file_prefix);
        watchedTimeKey = activity.getString(R.string.watched_time_key);

        beaconMsgTimeoutMsec = activity.getResources().getInteger(R.integer.app_cms_beacon_timeout_msec);
        beaconBufferingTimeoutMsec = activity.getResources().getInteger(R.integer.app_cms_beacon_buffering_timeout_msec);

        // It Handles the player stream Firebase events.
        setFirebaseProgressHandling();

        parentScreenName = activity.getString(R.string.app_cms_beacon_video_player_parent_screen_name);

        if (!isVideoDownloaded && refreshToken) {
            refreshTokenTimer = new Timer();
            refreshTokenTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (onUpdateContentDatumEvent != null) {
                        appCMSPresenter.refreshVideoData(onUpdateContentDatumEvent.getCurrentContentDatum().getGist().getId(), updatedContentDatum -> {
                            onUpdateContentDatumEvent.updateContentDatum(updatedContentDatum);
                            appCMSPresenter.getAppCMSSignedURL(filmId, appCMSSignedURLResult -> {
                                if (fullScreenVideoPlayerView != null && appCMSSignedURLResult != null) {
                                    fullScreenVideoPlayerView.updateSignatureCookies(appCMSSignedURLResult.getPolicy(),
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
                            int secsViewed = (int) fullScreenVideoPlayerView.getCurrentPosition() / 1000;
                            if (maxPreviewSecs < secsViewed && (userIdentity == null || !userIdentity.isSubscribed())) {

                                if (onUpdateContentDatumEvent != null) {
                                    AppCMSPresenter.EntitlementPendingVideoData entitlementPendingVideoData
                                            = new AppCMSPresenter.EntitlementPendingVideoData.Builder()
                                            .action(activity.getString(R.string.app_cms_page_play_key))
                                            .closerLauncher(false)
                                            .contentDatum(onUpdateContentDatumEvent.getCurrentContentDatum())
                                            .currentlyPlayingIndex(playIndex)
                                            .pagePath(permaLink)
                                            .filmTitle(title)
                                            .extraData(null)
                                            .relatedVideoIds(onUpdateContentDatumEvent.getCurrentRelatedVideoIds())
                                            .currentWatchedTime(fullScreenVideoPlayerView.getCurrentPosition() / 1000)
                                            .build();
                                    appCMSPresenter.setEntitlementPendingVideoData(entitlementPendingVideoData);
                                }

                                //Log.d(TAG, "User is not subscribed - pausing video and showing Subscribe dialog");
                                pauseVideo();

                                if (fullScreenVideoPlayerView != null) {
                                    fullScreenVideoPlayerView.disableController();
                                }
                                videoPlayerInfoContainer.setVisibility(View.VISIBLE);
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

        AppsFlyerUtils.filmViewingEvent(activity, primaryCategory, filmId, appCMSPresenter);
    }

    private void initializeStreamingQualityValues(VideoAssets videoAssets) {
        if (availableStreamingFormats == null) {
            availableStreamingFormats = new HashMap<>();
        }
        if (videoAssets != null && videoAssets.getMpeg() != null) {
            List<Mpeg> availableMpegs = videoAssets.getMpeg();
            int numAvailableMpegs = availableMpegs.size();
            for (int i = 0; i < numAvailableMpegs; i++) {
                Mpeg availableMpeg = availableMpegs.get(i);
                String mpegUrl = availableMpeg.getUrl();
                if (!TextUtils.isEmpty(mpegUrl)) {
                    String resolution = getMpegResolutionFromUrl(mpegUrl);
                    if (!TextUtils.isEmpty(resolution)) {
                        availableStreamingFormats.put(resolution, availableMpeg.getUrl());
                    }
                }
            }
        }
    }

    @Override
    public List<String> getAvailableStreamingQualities() {
        return new ArrayList<>(availableStreamingFormats.keySet());
    }

    @Override
    public String getStreamingQualityUrl(String streamingQuality) {
        if (availableStreamingFormats != null && availableStreamingFormats.containsKey(streamingQuality)) {
            return availableStreamingFormats.get(streamingQuality);
        }
        return null;
    }

    @Override
    public String getMpegResolutionFromUrl(String mpegUrl) {
        if (mpegUrl != null) {
            int mpegIndex = mpegUrl.indexOf(".mp4");
            if (0 < mpegIndex) {
                int startIndex = mpegUrl.substring(0, mpegIndex).lastIndexOf("/");
                if (0 <= startIndex && startIndex < mpegIndex) {
                    return mpegUrl.substring(startIndex + 1, mpegIndex);
                }
            }
        }
        return null;
    }

    public void initBundle(Activity activity,
                           String defaultVideoResolution,
                           boolean useHls,
                           String hlsUrl,
                           @NonNull Action1<Bundle> onBundleReadyAction) {
        String id = videoPlayerViewBinder.getContentData().getGist().getId();

        if (videoPlayerViewBinder.isTrailer()) {
            id = null;
            if (videoPlayerViewBinder.getContentData() != null &&
                    videoPlayerViewBinder.getContentData().getContentDetails() != null &&
                    videoPlayerViewBinder.getContentData().getContentDetails().getTrailers() != null &&
                    !videoPlayerViewBinder.getContentData().getContentDetails().getTrailers().isEmpty() &&
                    videoPlayerViewBinder.getContentData().getContentDetails().getTrailers().get(0) != null) {
                id = videoPlayerViewBinder.getContentData().getContentDetails().getTrailers().get(0).getId();
            } else if (videoPlayerViewBinder.getContentData().getShowDetails() != null &&
                    videoPlayerViewBinder.getContentData().getShowDetails().getTrailers() != null &&
                    !videoPlayerViewBinder.getContentData().getShowDetails().getTrailers().isEmpty() &&
                    videoPlayerViewBinder.getContentData().getShowDetails().getTrailers().get(0) != null &&
                    videoPlayerViewBinder.getContentData().getShowDetails().getTrailers().get(0).getId() != null) {
                id = videoPlayerViewBinder.getContentData().getShowDetails().getTrailers().get(0).getId();
            }
        }
        if (id != null) {
            appCMSPresenter.refreshVideoData(id,
                    updatedContentDatum -> {
                        try {
                            videoPlayerViewBinder.setContentData(updatedContentDatum);
                        } catch (Exception e) {
                            //
                        }

                        onBundleReadyAction.call(createBundle(activity,
                                updatedContentDatum.getGist(),
                                hlsUrl,
                                useHls,
                                appCMSPresenter.getAppTextColor(),
                                defaultVideoResolution,
                                watchedTime,
                                null));
                    });
        }
    }

    private String getHlsUrl(ContentDatum data) {
        if (data.getStreamingInfo() != null &&
                data.getStreamingInfo().getVideoAssets() != null &&
                data.getStreamingInfo().getVideoAssets().getHls() != null) {
            return data.getStreamingInfo().getVideoAssets().getHls();
        }
        return null;
    }

    private Bundle createBundle(Activity activity,
                              Gist gist,
                              String videoUrl,
                              boolean useHls,
                              String fontColor,
                              String defaultVideoResolution,
                              long watchedTime,
                              AppCMSSignedURLResult appCMSSignedURLResult) {
        String closedCaptionUrl = null;
        title = gist.getTitle();
        if (gist.getId() != null &&
                appCMSPresenter.getRealmController() != null &&
                appCMSPresenter.getRealmController().getDownloadById(gist.getId()) != null &&
                appCMSPresenter.getRealmController().getDownloadById(gist.getId()).getDownloadStatus() != null &&
                appCMSPresenter.getRealmController().getDownloadById(gist.getId()).getDownloadStatus().equals(DownloadStatus.STATUS_SUCCESSFUL)) {
            videoUrl = appCMSPresenter.getRealmController().getDownloadById(gist.getId()).getLocalURI();
        } else if (videoPlayerViewBinder.getContentData() != null &&
                videoPlayerViewBinder.getContentData().getStreamingInfo() != null &&
                videoPlayerViewBinder.getContentData().getStreamingInfo().getVideoAssets() != null) {
            VideoAssets videoAssets = videoPlayerViewBinder.getContentData().getStreamingInfo().getVideoAssets();

            initializeStreamingQualityValues(videoAssets);

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

        // TODO: 7/27/2017 Implement CC for multiple languages.
        if (videoPlayerViewBinder.getContentData() != null &&
                videoPlayerViewBinder.getContentData().getContentDetails() != null &&
                videoPlayerViewBinder.getContentData().getContentDetails().getClosedCaptions() != null &&
                !videoPlayerViewBinder.getContentData().getContentDetails().getClosedCaptions().isEmpty()) {
            for (ClosedCaptions cc : videoPlayerViewBinder.getContentData().getContentDetails().getClosedCaptions()) {
                if (cc.getUrl() != null &&
                        !cc.getUrl().equalsIgnoreCase(downloadFilePrefix) &&
                        cc.getFormat() != null &&
                        cc.getFormat().equalsIgnoreCase("SRT")) {
                    closedCaptionUrl = cc.getUrl();
                }
            }
        }

        String permaLink = gist.getPermalink();
        hlsUrl = videoUrl;
        videoImageUrl = gist.getVideoImageUrl();
        if (videoPlayerViewBinder.getContentData() != null &&
                videoPlayerViewBinder.getContentData().getGist() != null) {
            filmId = videoPlayerViewBinder.getContentData().getGist().getId();
            videoRunTime = videoPlayerViewBinder.getContentData().getGist().getRuntime();
        }
        String adsUrl = videoPlayerViewBinder.getAdsUrl();
        String bgColor = videoPlayerViewBinder.getBgColor();
        int playIndex = videoPlayerViewBinder.getCurrentPlayingVideoIndex();
        long duration = videoPlayerViewBinder.getContentData().getGist().getRuntime();
        if (duration <= watchedTime) {
            watchedTime = 0L;
        }
        if (gist.getPrimaryCategory() != null && gist.getPrimaryCategory().getTitle() != null) {
            primaryCategory = gist.getPrimaryCategory().getTitle();
        }
        boolean playAds = videoPlayerViewBinder.isPlayAds();
        relateVideoIds = videoPlayerViewBinder.getRelateVideoIds();
        currentlyPlayingIndex = videoPlayerViewBinder.getCurrentPlayingVideoIndex();
        if (videoPlayerViewBinder.getContentData() != null &&
                videoPlayerViewBinder.getContentData().getParentalRating() != null) {
            contentRating = videoPlayerViewBinder.getContentData().getParentalRating() == null ?
                    ageRagingConvertedDefault :
                    videoPlayerViewBinder.getContentData().getParentalRating();
        }

        boolean freeContent = false;
        if (videoPlayerViewBinder.getContentData() != null &&
                videoPlayerViewBinder.getContentData().getGist() != null &&
                videoPlayerViewBinder.getContentData().getGist().getFree()) {
            freeContent = videoPlayerViewBinder.getContentData().getGist().getFree();
        }

        String finalClosedCaptionUrl = closedCaptionUrl;
        boolean finalFreeContent = freeContent;

        return packageBundle(activity,
                primaryCategory,
                fontColor,
                title,
                permaLink,
                videoPlayerViewBinder.isTrailer(),
                hlsUrl,
                filmId,
                adsUrl,
                playAds,
                playIndex,
                watchedTime,
                videoImageUrl,
                finalClosedCaptionUrl,
                contentRating,
                videoRunTime,
                finalFreeContent,
                appCMSSignedURLResult);
    }

    private Bundle packageBundle(Context context,
                                 String primaryCategory,
                                 String fontColor,
                                 String title,
                                 String permaLink,
                                 boolean isTrailer,
                                 String hlsUrl,
                                 String filmId,
                                 String adsUrl,
                                 boolean requestAds,
                                 int playIndex,
                                 long watchedTime,
                                 String imageUrl,
                                 String closedCaptionUrl,
                                 String parentalRating,
                                 long videoRunTime,
                                 boolean freeContent,
                                 AppCMSSignedURLResult appCMSSignedURLResult) {
        Bundle args = new Bundle();
        args.putString(context.getString(R.string.video_player_font_color_key), fontColor);
        args.putString(context.getString(R.string.video_primary_category_key), primaryCategory);
        args.putString(context.getString(R.string.video_player_title_key), title);
        args.putString(context.getString(R.string.video_player_permalink_key), permaLink);
        args.putString(context.getString(R.string.video_player_hls_url_key), hlsUrl);
        args.putString(context.getString(R.string.video_layer_film_id_key), filmId);
        args.putString(context.getString(R.string.video_player_ads_url_key), adsUrl);
        args.putBoolean(context.getString(R.string.video_player_request_ads_key), requestAds);
        args.putInt(context.getString(R.string.play_index_key), playIndex);
        args.putLong(context.getString(R.string.watched_time_key), watchedTime);
        args.putLong(context.getString(R.string.run_time_key), videoRunTime);
        args.putBoolean(context.getString(R.string.free_content_key), freeContent);

        args.putString(context.getString(R.string.played_movie_image_url), imageUrl);
        args.putString(context.getString(R.string.video_player_closed_caption_key), closedCaptionUrl);
        args.putBoolean(context.getString(R.string.video_player_is_trailer_key), isTrailer);
        args.putString(context.getString(R.string.video_player_content_rating_key), parentalRating);

        if (appCMSSignedURLResult != null) {
            appCMSSignedURLResult.parseKeyValuePairs();
            args.putString(context.getString(R.string.signed_policy_key), appCMSSignedURLResult.getPolicy());
            args.putString(context.getString(R.string.signed_signature_key), appCMSSignedURLResult.getSignature());
            args.putString(context.getString(R.string.signed_keypairid_key), appCMSSignedURLResult.getKeyPairId());
        } else {
            args.putString(context.getString(R.string.signed_policy_key), "");
            args.putString(context.getString(R.string.signed_signature_key), "");
            args.putString(context.getString(R.string.signed_keypairid_key), "");
        }

        return args;
    }

    public View createEmbeddedPlayerView(Context context,
                                         String videoUrl,
                                         String ccUrl,
                                         String filmId,
                                         long watchedTime) {

        if (embeddedVideoPlayerView == null) {
            embeddedVideoPlayerView = new VideoPlayerView(context, appCMSPresenter);
            embeddedVideoPlayerView.init(context);

            boolean resetWatchTime = false;
            long currentWatchedTime = watchedTime;
            if (filmId != null && !filmId.equals(embeddedVideoPlayerView.getFilmId())) {
                resetWatchTime = true;
            } else {
                currentWatchedTime = embeddedVideoPlayerView.getCurrentPosition();
            }

            if (embeddedVideoPlayerView.getDuration() <= currentWatchedTime &&
                    0 < currentWatchedTime) {
                videoPlayerViewBinder.setAutoplayCancelled(true);
            }

            embeddedVideoPlayerView.setUri(Uri.parse(videoUrl),
                    !TextUtils.isEmpty(ccUrl) ?
                            Uri.parse(ccUrl) : null);

            if (!CastServiceProvider.getInstance(appCMSPresenter.getCurrentActivity()).isCastingConnected()) {
                embeddedVideoPlayerView.startPlayer();
            }

            embeddedVideoPlayerView.setFilmId(filmId);
            embeddedVideoPlayerView.getPlayerView().setControllerAutoShow(true);
            embeddedVideoPlayerView.getPlayerView().setControllerHideOnTouch(true);
            embeddedVideoPlayerView.getPlayerView().setVisibility(View.VISIBLE);

            if (resetWatchTime) {
                embeddedVideoPlayerView.getPlayerView().getPlayer().seekTo(watchedTime);
            } else if (0L < currentWatchedTime) {
                embeddedVideoPlayerView.getPlayerView().getPlayer().seekTo(currentWatchedTime);
            }
        } else if (embeddedVideoPlayerView.getParent() != null &&
                embeddedVideoPlayerView.getParent() instanceof ViewGroup) {
            ((ViewGroup) embeddedVideoPlayerView.getParent()).removeView(embeddedVideoPlayerView);
        }

        return embeddedVideoPlayerView;
    }

    public View createFullPlayerView(Activity activity) {
        if (fullScreenVideoPlayerRoot == null) {
            fullScreenVideoPlayerRoot = activity.getLayoutInflater().inflate(R.layout.fragment_video_player, null, false);

            videoPlayerMainContainer =
                    fullScreenVideoPlayerRoot.findViewById(R.id.app_cms_video_player_main_container);

            videoPlayerInfoContainer =
                    fullScreenVideoPlayerRoot.findViewById(R.id.app_cms_video_player_info_container);

            videoPlayerTitleView = fullScreenVideoPlayerRoot.findViewById(R.id.app_cms_video_player_title_view);

            if (!TextUtils.isEmpty(title)) {
                videoPlayerTitleView.setText(title);
            }

            if (!TextUtils.isEmpty(fontColor)) {
                videoPlayerTitleView.setTextColor(Color.parseColor(fontColor));
            }

            sendFirebaseAnalyticsEvents(title);

            videoPlayerViewDoneButton = fullScreenVideoPlayerRoot.findViewById(R.id.app_cms_video_player_done_button);
            videoPlayerViewDoneButton.setOnClickListener(v -> {
                if (onClosePlayerEvent != null) {
                    onClosePlayerEvent.closePlayer();
                }
            });

            videoPlayerViewDoneButton.setColorFilter(Color.parseColor(fontColor));
            videoPlayerInfoContainer.bringToFront();
            fullScreenVideoPlayerView = fullScreenVideoPlayerRoot.findViewById(R.id.app_cms_video_player_container);

            fullScreenVideoPlayerView.setAppCMSPresenter(appCMSPresenter);

            if (streamingQualitySelector != null) {
                fullScreenVideoPlayerView.setStreamingQualitySelector(streamingQualitySelector);
            }

            if (!TextUtils.isEmpty(policyCookie) &&
                    !TextUtils.isEmpty(signatureCookie) &&
                    !TextUtils.isEmpty(keyPairIdCookie)) {
                CookieManager cookieManager = new CookieManager();
                CookieHandler.setDefault(cookieManager);

                fullScreenVideoPlayerView.setPolicyCookie(policyCookie);
                fullScreenVideoPlayerView.setSignatureCookie(signatureCookie);
                fullScreenVideoPlayerView.setKeyPairIdCookie(keyPairIdCookie);
            }

            fullScreenVideoPlayerView.setListener(this);

            videoLoadingProgress = fullScreenVideoPlayerRoot.findViewById(R.id.app_cms_video_loading);

            boolean allowFreePlay = !appCMSPresenter.isAppSVOD() || isTrailer || freeContent;

            setCasting(activity, allowFreePlay);

            try {
                mStreamId = appCMSPresenter.getStreamingId(title);
            } catch (Exception e) {
                //Log.e(TAG, e.getMessage());
                mStreamId = filmId + appCMSPresenter.getCurrentTimeStamp();
            }

            isVideoDownloaded = appCMSPresenter.isVideoDownloaded(filmId);

            setCurrentWatchProgress(runTime, watchedTime);

            fullScreenVideoPlayerView.setOnPlayerStateChanged(playerState -> {
                if (beaconPing != null) {
                    beaconPing.playbackState = playerState.getPlaybackState();
                }

                if (playerState.getPlaybackState() == Player.STATE_READY && !isCastConnected) {
                    long updatedRunTime = 0;

                    try {
                        updatedRunTime = fullScreenVideoPlayerView.getDuration() / 1000;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    setCurrentWatchProgress(updatedRunTime, watchedTime);

                    if (!isVideoLoaded) {
                        fullScreenVideoPlayerView.setCurrentPosition(videoPlayTime * SECS_TO_MSECS);
                        if (!isTrailer) {
                            appCMSPresenter.updateWatchedTime(filmId,
                                    fullScreenVideoPlayerView.getCurrentPosition() / 1000);
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
                                    mTotalVideoDuration = fullScreenVideoPlayerView.getDuration() / 1000;
                                    mTotalVideoDuration -= mTotalVideoDuration % 4;
                                    mProgressHandler.post(mProgressRunnable);
                                } catch (Exception e) {
                                    //
                                }
                            }

                            if (!sentBeaconFirstFrame) {
                                mStopBufferMilliSec = new Date().getTime();
                                ttfirstframe = mStartBufferMilliSec == 0l ? 0d : ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                                appCMSPresenter.sendBeaconMessage(filmId,
                                        permaLink,
                                        parentScreenName,
                                        fullScreenVideoPlayerView.getCurrentPosition(),
                                        false,
                                        AppCMSPresenter.BeaconEvent.FIRST_FRAME,
                                        "Video",
                                        fullScreenVideoPlayerView.getBitrate() != 0 ? String.valueOf(fullScreenVideoPlayerView.getBitrate()) : null,
                                        String.valueOf(fullScreenVideoPlayerView.getVideoHeight()),
                                        String.valueOf(fullScreenVideoPlayerView.getVideoWidth()),
                                        mStreamId,
                                        ttfirstframe,
                                        0,
                                        isVideoDownloaded);
                                sentBeaconFirstFrame = true;

                            }
                        }
                    }

                    videoLoadingProgress.setVisibility(View.GONE);
                } else if (playerState.getPlaybackState() == Player.STATE_ENDED) {
                    //Log.d(TAG, "Video ended");
                    if (shouldRequestAds && adsLoader != null) {
                        adsLoader.contentComplete();
                    }

                    // close the player if current video is a trailer. We don't want to auto-play it
                    if (onClosePlayerEvent != null &&
                            permaLink.contains(
                                    activity.getString(R.string.app_cms_action_qualifier_watchvideo_key))) {
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

                    if (onClosePlayerEvent != null && playerState.isPlayWhenReady() && !showEntitlementDialog) {

                        // tell the activity that the movie is finished
                        onClosePlayerEvent.onMovieFinished();
                    }

                    if (!isTrailer && 30 <= (fullScreenVideoPlayerView.getCurrentPosition() / 1000)) {
                        appCMSPresenter.updateWatchedTime(filmId,
                                fullScreenVideoPlayerView.getCurrentPosition() / 1000);
                    }

                    if (videoPlayerViewBinder.getPlayerState() != Player.STATE_ENDED &&
                            0 < fullScreenVideoPlayerView.getDuration() &&
                            fullScreenVideoPlayerView.getDuration() <= fullScreenVideoPlayerView.getCurrentPosition()) {
                        if (!videoPlayerViewBinder.isAutoplayCancelled() &&
                                videoPlayerViewBinder.getCurrentPlayingVideoIndex() <
                                        videoPlayerViewBinder.getRelateVideoIds().size()) {
                            if (appCMSPresenter.getAutoplayEnabledUserPref(appCMSPresenter.getCurrentActivity()) &&
                                    videoPlayerViewBinder != null) {
                                videoPlayerViewBinder.setCurrentPlayingVideoIndex(videoPlayerViewBinder.getCurrentPlayingVideoIndex() + 1);
                                appCMSPresenter.playNextVideo(videoPlayerViewBinder,
                                        videoPlayerViewBinder.getCurrentPlayingVideoIndex() + 1,
                                        videoPlayerViewBinder.getContentData().getGist().getWatchedTime());
                            }
                        }
                    }
                    videoPlayerViewBinder.setAutoplayCancelled(videoPlayerViewBinder.getPlayerState() == playerState.getPlaybackState());
                    videoPlayerViewBinder.setPlayerState(playerState.getPlaybackState());
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

                    videoLoadingProgress.setVisibility(View.VISIBLE);
                }

                if (!sentBeaconPlay) {
                    appCMSPresenter.sendBeaconMessage(filmId,
                            permaLink,
                            parentScreenName,
                            fullScreenVideoPlayerView.getCurrentPosition(),
                            false,
                            AppCMSPresenter.BeaconEvent.PLAY,
                            "Video",
                            fullScreenVideoPlayerView.getBitrate() != 0 ? String.valueOf(fullScreenVideoPlayerView.getBitrate()) : null,
                            String.valueOf(fullScreenVideoPlayerView.getVideoHeight()),
                            String.valueOf(fullScreenVideoPlayerView.getVideoWidth()),
                            mStreamId,
                            0d,
                            0,
                            isVideoDownloaded);
                    sentBeaconPlay = true;
                    mStartBufferMilliSec = new Date().getTime();
                }
            });

            fullScreenVideoPlayerView.setOnPlayerControlsStateChanged(visibility -> {
                if (visibility == View.GONE) {
                    videoPlayerInfoContainer.setVisibility(View.GONE);
                } else if (visibility == View.VISIBLE) {
                    videoPlayerInfoContainer.setVisibility(View.VISIBLE);
                }
            });

            fullScreenVideoPlayerView.setOnClosedCaptionButtonClicked(isChecked -> {
                fullScreenVideoPlayerView.getPlayerView().getSubtitleView()
                        .setVisibility(isChecked ? View.VISIBLE : View.GONE);
                appCMSPresenter.setClosedCaptionPreference(isChecked);
            });

            initViewForCRW(activity, fullScreenVideoPlayerRoot);

            if (!shouldRequestAds) {
                try {
                    createContentRatingView();
                } catch (Exception e) {
                    //Log.e(TAG, "Error ContentRatingView: " + e.getMessage());
                }
            }

            beaconPing = new BeaconPing(beaconMsgTimeoutMsec,
                    appCMSPresenter,
                    filmId,
                    permaLink,
                    isTrailer,
                    parentScreenName,
                    fullScreenVideoPlayerView,
                    mStreamId);

            beaconBuffer = new BeaconBuffer(beaconBufferingTimeoutMsec,
                    appCMSPresenter,
                    filmId,
                    permaLink,
                    parentScreenName,
                    fullScreenVideoPlayerView,
                    mStreamId);

            videoLoadingProgress.bringToFront();
            videoLoadingProgress.setVisibility(View.VISIBLE);

            showCRWWarningMessage = true;
        }
        else if (fullScreenVideoPlayerRoot.getParent() != null &&
                fullScreenVideoPlayerRoot.getParent() instanceof ViewGroup) {
            ((ViewGroup) fullScreenVideoPlayerRoot.getParent()).removeView(fullScreenVideoPlayerRoot);
        }

        return fullScreenVideoPlayerRoot;
    }

    public View getFullScreenVideoPlayerRoot() {
        return fullScreenVideoPlayerRoot;
    }

    public void activityCreated(Activity activity) {
        sdkFactory = ImaSdkFactory.getInstance();
        adsLoader = sdkFactory.createAdsLoader(activity);
        adsLoader.addAdErrorListener(this);
        adsLoader.addAdsLoadedListener(listenerAdsLoaded);
    }

    public void resume(Activity activity) {
        videoPlayerMainContainer.requestLayout();
        fullScreenVideoPlayerView.setAppCMSPresenter(appCMSPresenter);
        fullScreenVideoPlayerView.init(activity);
        fullScreenVideoPlayerView.enableController();
        if (!TextUtils.isEmpty(hlsUrl)) {
            fullScreenVideoPlayerView.setClosedCaptionEnabled(appCMSPresenter.getClosedCaptionPreference());
            fullScreenVideoPlayerView.getPlayerView().getSubtitleView()
                    .setVisibility(appCMSPresenter.getClosedCaptionPreference()
                            ? View.VISIBLE
                            : View.GONE);
            fullScreenVideoPlayerView.setUri(Uri.parse(hlsUrl),
                    !TextUtils.isEmpty(closedCaptionUrl) ? Uri.parse(closedCaptionUrl) : null);
            //Log.i(TAG, "Playing video: " + title);
        }
        fullScreenVideoPlayerView.setCurrentPosition(videoPlayTime * SECS_TO_MSECS);

        appCMSPresenter.setShowNetworkConnectivity(false);

        requestAudioFocus();
        resumeVideo();
    }

    public void pause() {
        pauseVideo();
        videoPlayTime = fullScreenVideoPlayerView.getCurrentPosition() / SECS_TO_MSECS;
        fullScreenVideoPlayerView.releasePlayer();
    }

    public void configurationChanged(Activity activity) {
        getPercentageFromResource(activity);
        if (fullScreenVideoPlayerView != null) {
            fullScreenVideoPlayerView.setFillBasedOnOrientation();
        }
    }

    public void destroy() {
        if (appCMSPresenter.isAppSVOD() && !freeContent) {
            if (entitlementCheckTimerTask != null) {
                entitlementCheckTimerTask.cancel();
            }

            if (entitlementCheckTimer != null) {
                entitlementCheckTimer.cancel();
            }
        }

        if (refreshToken) {
            if (refreshTokenTimerTask != null) {
                refreshTokenTimerTask.cancel();
            }

            if (refreshTokenTimer != null) {
                refreshTokenTimer.cancel();
            }
        }
    }

    public void destroyView() {
        fullScreenVideoPlayerView.setOnPlayerStateChanged(null);
        beaconPing.sendBeaconPing = false;
        beaconPing.runBeaconPing = false;
        beaconPing.videoPlayerView = null;
        beaconPing = null;

        if (mProgressHandler != null) {
            mProgressHandler.removeCallbacks(mProgressRunnable);
            mProgressHandler = null;
        }

        beaconBuffer.sendBeaconBuffering = false;
        beaconBuffer.runBeaconBuffering = false;
        beaconBuffer.videoPlayerView = null;
        beaconBuffer = null;

        onClosePlayerEvent = null;
        if (adsLoader != null) {
            adsLoader.removeAdsLoadedListener(listenerAdsLoaded);
            adsLoader.removeAdErrorListener(this);
        }
        adsLoader = null;
    }

    public void setVideoTitle(String title, int textColor) {
        if (videoPlayerTitleView != null) {
            videoPlayerTitleView.setText(title);
            videoPlayerTitleView.setTextColor(textColor);
        }
    }

    private void setCurrentWatchProgress(long runTime, long watchedTime) {
        System.out.println("fullScreenVideoPlayerView run time on setcurrent progress-" + runTime + " watch time-" + watchedTime);

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

    private void sendFirebaseAnalyticsEvents(String screenVideoName) {
        if (screenVideoName == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SCREEN_VIEW_EVENT, PLAYER_SCREEN_NAME + "-" + screenVideoName);
        if (appCMSPresenter.getmFireBaseAnalytics() != null) {
            //Logs an app event.
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
            //Sets whether analytics collection is enabled for this app on this device.
            appCMSPresenter.getmFireBaseAnalytics().setAnalyticsCollectionEnabled(true);
        }
    }

    private void setCasting(Activity activity, boolean allowFreePlay) {
        try {
            castProvider = CastServiceProvider.getInstance(activity);
            castProvider.setAllowFreePlay(allowFreePlay);
            castProvider.setRemotePlaybackCallback(callBackRemotePlayback);
            isCastConnected = castProvider.isCastingConnected();
            castProvider.playChromeCastPlaybackIfCastConnected();
        } catch (Exception e) {
            //Log.e(TAG, "Error initializing cast provider: " + e.getMessage());
        }
    }

    private void pauseVideo() {
        if (shouldRequestAds && adsManager != null && isAdDisplayed) {
            adsManager.pause();
        } else {
            fullScreenVideoPlayerView.pausePlayer();
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
            fullScreenVideoPlayerView.resumePlayer();
            if (beaconPing != null) {
                beaconPing.sendBeaconPing = true;
            }
            if (beaconBuffer != null) {
                beaconBuffer.sendBeaconBuffering = true;
            }
            //Log.d(TAG, "Resuming playback");
        }
        if (castProvider != null) {
            castProvider.onActivityResume();
        }
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        //Log.e(TAG, "Ad DialogType: " + adErrorEvent.getError().getMessage());
//        createContentRatingView();
        fullScreenVideoPlayerView.resumePlayer();
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        //Log.i(TAG, "Event: " + adEvent.getType());

        switch (adEvent.getType()) {
            case LOADED:
                adsManager.start();
                break;

            case CONTENT_PAUSE_REQUESTED:
                isAdDisplayed = true;
                if (beaconPing != null) {
                    beaconPing.sendBeaconPing = false;
                    if (mProgressHandler != null)
                        mProgressHandler.removeCallbacks(mProgressRunnable);
                }
                if (appCMSPresenter != null) {

                    appCMSPresenter.sendBeaconMessage(filmId,
                            permaLink,
                            parentScreenName,
                            fullScreenVideoPlayerView.getCurrentPosition(),
                            false,
                            AppCMSPresenter.BeaconEvent.AD_IMPRESSION,
                            "Video",
                            fullScreenVideoPlayerView.getBitrate() != 0 ? String.valueOf(fullScreenVideoPlayerView.getBitrate()) : null,
                            String.valueOf(fullScreenVideoPlayerView.getVideoHeight()),
                            String.valueOf(fullScreenVideoPlayerView.getVideoWidth()),
                            mStreamId,
                            0d,
                            apod,
                            isVideoDownloaded);
                }
                fullScreenVideoPlayerView.pausePlayer();
                break;

            case CONTENT_RESUME_REQUESTED:
                isAdDisplayed = false;
                // fullScreenVideoPlayerView.startPlayer();
                if (beaconPing != null) {
                    beaconPing.sendBeaconPing = true;
                }
                if (appCMSPresenter != null) {
                    mStopBufferMilliSec = new Date().getTime();
                    ttfirstframe = mStartBufferMilliSec == 0l ? 0d : ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                    appCMSPresenter.sendBeaconMessage(filmId,
                            permaLink,
                            parentScreenName,
                            fullScreenVideoPlayerView.getCurrentPosition(),
                            false,
                            AppCMSPresenter.BeaconEvent.FIRST_FRAME,
                            "Video",
                            fullScreenVideoPlayerView.getBitrate() != 0 ? String.valueOf(fullScreenVideoPlayerView.getBitrate()) : null,
                            String.valueOf(fullScreenVideoPlayerView.getVideoHeight()),
                            String.valueOf(fullScreenVideoPlayerView.getVideoWidth()),
                            mStreamId,
                            ttfirstframe,
                            0,
                            isVideoDownloaded);
                }
                if (beaconPing != null && !beaconPing.isAlive()) {
                    beaconPing.start();

                    if (mProgressHandler != null)
                        mProgressHandler.post(mProgressRunnable);
                }
                break;

            case ALL_ADS_COMPLETED:
                videoLoadingProgress.setVisibility(View.GONE);
                try {
                    createContentRatingView();
                } catch (Exception e) {
                    //Log.e(TAG, "Error ContentRatingView: " + e.getMessage());
                }
                if (adsManager != null) {
                    adsManager.destroy();
                    adsManager = null;
                }
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
                            (((float) (fullScreenVideoPlayerView.getCurrentPosition() / 1000) / mTotalVideoDuration) * 100);
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

    private void requestAds(String adTagUrl) {
        if (!TextUtils.isEmpty(adTagUrl) && adsLoader != null) {
            //Log.d(TAG, "Requesting ads: " + adTagUrl);
            AdDisplayContainer adDisplayContainer = sdkFactory.createAdDisplayContainer();
            adDisplayContainer.setAdContainer(fullScreenVideoPlayerView);

            AdsRequest request = sdkFactory.createAdsRequest();
            request.setAdTagUrl(adTagUrl);
            request.setAdDisplayContainer(adDisplayContainer);
            request.setContentProgressProvider(() -> {
                if (isAdDisplayed || fullScreenVideoPlayerView.getDuration() <= 0) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                return new VideoProgressUpdate(fullScreenVideoPlayerView.getCurrentPosition(),
                        fullScreenVideoPlayerView.getDuration());
            });

            adsLoader.requestAds(request);
            apod += 1;

            if (appCMSPresenter != null) {
                appCMSPresenter.sendBeaconMessage(filmId,
                        permaLink,
                        parentScreenName,
                        fullScreenVideoPlayerView.getCurrentPosition(),
                        false,
                        AppCMSPresenter.BeaconEvent.AD_REQUEST,
                        "Video",
                        fullScreenVideoPlayerView.getBitrate() != 0 ? String.valueOf(fullScreenVideoPlayerView.getBitrate()) : null,
                        String.valueOf(fullScreenVideoPlayerView.getVideoHeight()),
                        String.valueOf(fullScreenVideoPlayerView.getVideoWidth()),
                        mStreamId,
                        0d,
                        apod,
                        isVideoDownloaded);
            }
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
                            if (fullScreenVideoPlayerView != null && appCMSSignedURLResult != null) {
                                boolean foundMatchingMpeg = false;
                                if (!TextUtils.isEmpty(hlsUrl) && hlsUrl.contains("mp4")) {
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
                                                .setWatchedTime(fullScreenVideoPlayerView.getCurrentPosition() / 1000L);
                                        for (int i = 0;
                                             i < updatedContentDatum.getStreamingInfo()
                                                     .getVideoAssets()
                                                     .getMpeg()
                                                     .size() &&
                                                     !foundMatchingMpeg;
                                             i++) {
                                            int queryIndex = hlsUrl.indexOf("?");
                                            if (0 <= queryIndex) {
                                                if (updatedContentDatum.getStreamingInfo()
                                                        .getVideoAssets()
                                                        .getMpeg()
                                                        .get(0)
                                                        .getUrl()
                                                        .contains(hlsUrl.substring(0, queryIndex))) {
                                                    foundMatchingMpeg = true;
                                                    hlsUrl = updatedContentDatum.getStreamingInfo()
                                                            .getVideoAssets()
                                                            .getMpeg()
                                                            .get(0)
                                                            .getUrl();
                                                }
                                            }
                                        }
                                    }
                                }

                                fullScreenVideoPlayerView.updateSignatureCookies(appCMSSignedURLResult.getPolicy(),
                                        appCMSSignedURLResult.getSignature(),
                                        appCMSSignedURLResult.getKeyPairId());

                                if (foundMatchingMpeg && updatedContentDatum.getGist() != null) {
                                    fullScreenVideoPlayerView.setUri(Uri.parse(hlsUrl),
                                            !TextUtils.isEmpty(closedCaptionUrl) ?
                                                    Uri.parse(closedCaptionUrl) : null);
                                    fullScreenVideoPlayerView.setCurrentPosition(updatedContentDatum.getGist()
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

        appCMSPresenter.sendBeaconMessage(filmId,
                permaLink,
                parentScreenName,
                fullScreenVideoPlayerView.getCurrentPosition(),
                false,
                event,
                "Video",
                fullScreenVideoPlayerView.getBitrate() != 0 ? String.valueOf(fullScreenVideoPlayerView.getBitrate()) : null,
                String.valueOf(fullScreenVideoPlayerView.getVideoHeight()),
                String.valueOf(fullScreenVideoPlayerView.getVideoWidth()),
                mStreamId,
                0d,
                0,
                isVideoDownloaded);
        if (onClosePlayerEvent != null) {
            onClosePlayerEvent.closePlayer();
        }

        if (!TextUtils.isEmpty(message)) {
            appCMSPresenter.showToast(message, Toast.LENGTH_LONG);
        }
    }

    private void initViewForCRW(Activity activity, View rootView) {

        contentRatingMainContainer =
                rootView.findViewById(R.id.app_cms_content_rating_main_container);

        contentRatingAnimationContainer =
                rootView.findViewById(R.id.app_cms_content_rating_animation_container);

        contentRatingInfoContainer =
                rootView.findViewById(R.id.app_cms_content_rating_info_container);

        contentRatingHeaderView = rootView.findViewById(R.id.app_cms_content_rating_header_view);
        setTypeFace(activity, contentRatingHeaderView, activity.getString(R.string.helvaticaneu_bold));

        contentRatingTitleHeader = rootView.findViewById(R.id.app_cms_content_rating_title_header);
        setTypeFace(activity, contentRatingTitleHeader, activity.getString(R.string.helvaticaneu_italic));

        contentRatingTitleView = rootView.findViewById(R.id.app_cms_content_rating_title);
        setTypeFace(activity, contentRatingTitleView, activity.getString(R.string.helvaticaneu_bold));

        contentRatingDiscretionView = rootView.findViewById(R.id.app_cms_content_rating_viewer_discretion);
        setTypeFace(activity, contentRatingDiscretionView, activity.getString(R.string.helvaticaneu_bold));

        contentRatingBack = rootView.findViewById(R.id.app_cms_content_rating_back);
        setTypeFace(activity, contentRatingBack, activity.getString(R.string.helvaticaneu_bold));

        contentRatingBackUnderline = rootView.findViewById(R.id.app_cms_content_rating_back_underline);

        progressBar = rootView.findViewById(R.id.app_cms_content_rating_progress_bar);

        if (!TextUtils.isEmpty(fontColor)) {
            contentRatingTitleHeader.setTextColor(Color.parseColor(fontColor));
            contentRatingTitleView.setTextColor(Color.parseColor(fontColor));
            contentRatingDiscretionView.setTextColor(Color.parseColor(fontColor));
            contentRatingBack.setTextColor(Color.parseColor(fontColor));
        }

        if (appCMSPresenter.getAppCMSMain() != null &&
                !TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor())) {
            int highlightColor =
                    Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor());
            contentRatingBackUnderline.setBackgroundColor(highlightColor);
            contentRatingHeaderView.setTextColor(highlightColor);
            applyBorderToComponent(contentRatingInfoContainer, 1, highlightColor);
            progressBar.getProgressDrawable()
                    .setColorFilter(highlightColor, PorterDuff.Mode.SRC_IN);
            progressBar.setMax(100);
        }

        contentRatingBack.setOnClickListener(v -> activity.finish());

        ageRatingConvertedG = activity.getString(R.string.age_rating_converted_g);
        ageRatingConvertedPG = activity.getString(R.string.age_rating_converted_pg);
        ageRagingConvertedDefault = activity.getString(R.string.age_rating_converted_default);

        animSequential = AnimationUtils.loadAnimation(activity,
                R.anim.sequential);
        animFadeIn = AnimationUtils.loadAnimation(activity,
                R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(activity,
                R.anim.fade_out);
        animTranslate = AnimationUtils.loadAnimation(activity,
                R.anim.translate);
    }

    private void createContentRatingView() throws Exception {
        if (!isTrailer &&
                !getParentalRating().equalsIgnoreCase(ageRatingConvertedG) &&
                !getParentalRating().equalsIgnoreCase(ageRagingConvertedDefault) &&
                watchedTime == 0) {
            videoPlayerMainContainer.setVisibility(View.GONE);
            contentRatingMainContainer.setVisibility(View.VISIBLE);
            //animateView();
            fullScreenVideoPlayerView.pausePlayer();
            startCountdown();
        } else {
            contentRatingMainContainer.setVisibility(View.GONE);
            videoPlayerMainContainer.setVisibility(View.VISIBLE);
            fullScreenVideoPlayerView.startPlayer();
        }
    }

    private String getParentalRating() {
        if (!isTrailer &&
                !parentalRating.equalsIgnoreCase(ageRatingConvertedG) &&
                !parentalRating.equalsIgnoreCase(ageRagingConvertedDefault) &&
                watchedTime == 0) {
            contentRatingTitleView.setText(parentalRating);
        }
        return parentalRating != null ? parentalRating : ageRagingConvertedDefault;
    }

    // TODO: 11/30/17 Replace countdown timer with value coming from Template Builder.
    private void startCountdown() {
        new CountDownTimer(totalCountdownInMillis, countDownIntervalInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                long progress = (long) (100.0 * (1.0 - (double) millisUntilFinished / (double) totalCountdownInMillis));
//                Log.d(TAG, "CRW Progress:" + progress);
                progressBar.setProgress((int) progress);
            }

            @Override
            public void onFinish() {
                contentRatingMainContainer.setVisibility(View.GONE);
                videoPlayerMainContainer.setVisibility(View.VISIBLE);
                fullScreenVideoPlayerView.startPlayer();
            }
        }.start();
    }

    private void applyBorderToComponent(View view, int width, int Color) {
        GradientDrawable rectangleBorder = new GradientDrawable();
        rectangleBorder.setShape(GradientDrawable.RECTANGLE);
        rectangleBorder.setStroke(width, Color);
        view.setBackground(rectangleBorder);
    }

    private void getPercentageFromResource(Activity activity) {
        float heightPercent = activity.getResources().getFraction(R.fraction.mainContainerHeightPercent, 1, 1);
        float widthPercent = activity.getResources().getFraction(R.fraction.mainContainerWidthPercent, 1, 1);
        float bottomMarginPercent = activity.getResources().getFraction(R.fraction.app_cms_content_rating_progress_bar_margin_bottom_percent, 1, 1);

        PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) contentRatingAnimationContainer.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();

        PercentRelativeLayout.LayoutParams paramsProgressBar = (PercentRelativeLayout.LayoutParams) progressBar.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoProgress = paramsProgressBar.getPercentLayoutInfo();

        info.heightPercent = heightPercent;
        info.widthPercent = widthPercent;
        infoProgress.bottomMarginPercent = bottomMarginPercent;

        contentRatingAnimationContainer.requestLayout();
        progressBar.requestLayout();
    }

    private void animateView() {
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        animSequential.setAnimationListener(this);
        animTranslate.setAnimationListener(this);

        contentRatingMainContainer.setVisibility(View.VISIBLE);

        if (getParentalRating().contains(ageRatingConvertedPG) ||
                !getParentalRating().contains(ageRatingConvertedG)) {
            contentRatingHeaderView.startAnimation(animFadeIn);
            contentRatingInfoContainer.startAnimation(animFadeIn);
        } else {
            contentRatingHeaderView.setVisibility(View.GONE);
        }
        contentRatingInfoContainer.setVisibility(View.VISIBLE);

        contentRatingTitleView.startAnimation(animSequential);
        contentRatingTitleHeader.startAnimation(animSequential);

        contentRatingTitleView.setVisibility(View.VISIBLE);
        contentRatingTitleHeader.setVisibility(View.VISIBLE);
    }

    private void setTypeFace(Context context,
                             TextView view, String fontType) {
        if (null != context && null != view && null != fontType) {
            try {
                Typeface face = Typeface.createFromAsset(context.getAssets(), fontType);
                view.setTypeface(face);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        //
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animFadeIn) {
            if (showCRWWarningMessage &&
                    getParentalRating().contains(ageRatingConvertedPG) ||
                    !getParentalRating().contains(ageRatingConvertedG)) {
                contentRatingDiscretionView.startAnimation(animFadeOut);
                contentRatingDiscretionView.setVisibility(View.VISIBLE);
                showCRWWarningMessage = false;
            } else {
                contentRatingDiscretionView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        //
    }

    protected void abandonAudioFocus() {
        if (audioManager != null) {
            int result = audioManager.abandonAudioFocus(this);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocusGranted = false;
            }
        }
    }

    protected boolean requestAudioFocus() {
        if (audioManager != null && !mAudioFocusGranted) {
            int result = audioManager.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocusGranted = true;
            }
        }
        return mAudioFocusGranted;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                fullScreenVideoPlayerView.pausePlayer();
                break;

            case AudioManager.AUDIOFOCUS_GAIN:
                if (fullScreenVideoPlayerView.getPlayer() != null && fullScreenVideoPlayerView.getPlayer().getPlayWhenReady()) {
                    fullScreenVideoPlayerView.startPlayer();
                } else {
                    fullScreenVideoPlayerView.pausePlayer();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                fullScreenVideoPlayerView.pausePlayer();
                abandonAudioFocus();
                break;

            default:
                break;
        }
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

    public void updateVideoPlayerBinder(AppCMSPresenter appCMSPresenter,
                                         ContentDatum contentDatum) {
        if (!ignoreBinderUpdate ||
                (videoPlayerViewBinder != null &&
                        videoPlayerViewBinder.getContentData() != null &&
                        videoPlayerViewBinder.getContentData().getGist() != null &&
                        videoPlayerViewBinder.getContentData().getGist().getId() != null &&
                        contentDatum != null &&
                        contentDatum.getGist() != null &&
                        !videoPlayerViewBinder.getContentData().getGist().getId().equals(contentDatum.getGist().getId()))) {
            if (videoPlayerViewBinder == null) {
                videoPlayerViewBinder =
                        appCMSPresenter.getDefaultAppCMSVideoPageBinder(contentDatum,
                                -1,
                                contentDatum.getContentDetails().getRelatedVideoIds(),
                                false,
                                false,  /** TODO: Replace with a value that is true if the video is a trailer */
                                !appCMSPresenter.isAppSVOD(),
                                appCMSPresenter.getAppAdsURL(contentDatum.getGist().getPermalink()),
                                appCMSPresenter.getAppBackgroundColor());
            } else {
                int currentlyPlayingIndex = -1;
                if (videoPlayerViewBinder.getRelateVideoIds() != null &&
                        videoPlayerViewBinder.getRelateVideoIds().contains(contentDatum.getGist().getId())) {
                    currentlyPlayingIndex = videoPlayerViewBinder.getRelateVideoIds().indexOf(contentDatum.getGist().getId());
                } else {
                    videoPlayerViewBinder.setPlayerState(Player.STATE_IDLE);
                    videoPlayerViewBinder.setRelateVideoIds(contentDatum.getContentDetails().getRelatedVideoIds());
                }
                if (videoPlayerViewBinder.getContentData().getGist().getId().equals(contentDatum.getGist().getId())) {
                    currentlyPlayingIndex = videoPlayerViewBinder.getCurrentPlayingVideoIndex();
                    videoPlayerViewBinder.setAutoplayCancelled(true);
                }
                videoPlayerViewBinder.setCurrentPlayingVideoIndex(currentlyPlayingIndex);
                videoPlayerViewBinder.setContentData(contentDatum);
            }
        }
        ignoreBinderUpdate = false;
    }

    public PageView getPageView() {
        return pageView;
    }

    public void setPageView(PageView pageView) {
        this.pageView = pageView;
    }
}
