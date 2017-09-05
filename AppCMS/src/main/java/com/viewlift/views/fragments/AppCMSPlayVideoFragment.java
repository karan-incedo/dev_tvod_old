package com.viewlift.views.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.android.gms.cast.framework.CastSession;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.analytics.AppsFlyerUtils;
import com.viewlift.casting.CastHelper;
import com.viewlift.casting.CastServiceProvider;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.VideoPlayerView;

import java.util.Date;

import rx.functions.Action1;

/**
 * Created by viewlift on 6/14/17.
 */

public class AppCMSPlayVideoFragment extends Fragment
        implements AdErrorEvent.AdErrorListener,
        AdEvent.AdEventListener,
        VideoPlayerView.FinishListener,
        Animation.AnimationListener {
    private static final String TAG = "PlayVideoFragment";

    private static final long SECS_TO_MSECS = 1000L;
    private static final String PLAYER_SCREEN_NAME = "Player Screen";
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
    private TextView contentRatingTitleView;
    private TextView contentRatingBack;
    private View contentRatingBackUnderline;
    private VideoPlayerView videoPlayerView;
    private LinearLayout videoLoadingProgress;
    private OnClosePlayerEvent onClosePlayerEvent;
    private BeaconPingThread beaconMessageThread;
    private long beaconMsgTimeoutMsec;

    private BeaconBufferingThread beaconBufferingThread;
    private long beaconBufferingTimeoutMsec;
    private boolean sentBeaconPlay;

    private boolean sentBeaconFirstFrame;

    private ImaSdkFactory sdkFactory;
    private AdsLoader adsLoader;
    private AdsManager adsManager;

    Handler mProgressHandler;
    Runnable mProgressRunnable;
    long mTotalVideoDuration;

    //Setting the Key Values for Firebase Player Events
    private final String FIREBASE_STREAM_START = "stream_start";
    private final String FIREBASE_STREAM_25 = "stream_25_pct";
    private final String FIREBASE_STREAM_50 = "stream_50_pct";
    private final String FIREBASE_STREAM_75 = "stream_75_pct";

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


    private String mStreamId;
    private long mStartBufferMilliSec;
    private long mStopBufferMilliSec;
    private static double ttfirstframe = 0d;
    private static int apod = 0;
    private static boolean isVideoDownloaded;

    private ProgressBar progressBar;
    private Runnable seekListener;
    private int progressCount = 0;
    private Handler seekBarHandler;
    Animation animSequential, animFadeIn, animFadeOut, animTranslate;
    private final int totalCountdownInMillis = 10000;
    private final int countDownIntervalInMillis = 10;
    private boolean showCRWWarningMessage;

    AdsLoader.AdsLoadedListener listenerAdsLoaded = adsManagerLoadedEvent -> {
        adsManager = adsManagerLoadedEvent.getAdsManager();
        adsManager.addAdErrorListener(AppCMSPlayVideoFragment.this);
        adsManager.addAdEventListener(AppCMSPlayVideoFragment.this);
        adsManager.init();
    };

    private boolean isAdDisplayed;
    private int playIndex;
    private long watchedTime;
    private ImageButton mMediaRouteButton;
    private CastServiceProvider castProvider;
    private CastSession mCastSession;
    private CastHelper mCastHelper;
    private String closedCaptionUrl;
    private boolean isCastConnected;

    CastServiceProvider.ILaunchRemoteMedia callBackRemotePlayback = castingModeChromecast -> {
        if (onClosePlayerEvent != null) {
            pauseVideo();
            long castPlayPosition = watchedTime * SECS_TO_MSECS;
            if (!isCastConnected) {
                castPlayPosition = videoPlayerView.getCurrentPosition();
            }

            onClosePlayerEvent.onRemotePlayback(castPlayPosition,
                    castingModeChromecast,
                    sentBeaconPlay,
                    onApplicationEnded -> {
                        //
                    });
        }
    };

    public static AppCMSPlayVideoFragment newInstance(Context context,
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
                                                      String parentalRating) {
        AppCMSPlayVideoFragment appCMSPlayVideoFragment = new AppCMSPlayVideoFragment();
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
        args.putString(context.getString(R.string.played_movie_image_url), imageUrl);
        args.putString(context.getString(R.string.video_player_closed_caption_key), closedCaptionUrl);
        args.putBoolean(context.getString(R.string.video_player_is_trailer_key), isTrailer);
        args.putString(context.getString(R.string.video_player_content_rating_key), parentalRating);
        appCMSPlayVideoFragment.setArguments(args);
        return appCMSPlayVideoFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClosePlayerEvent) {
            onClosePlayerEvent = (OnClosePlayerEvent) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            fontColor = args.getString(getString(R.string.video_player_font_color_key));
            title = args.getString(getString(R.string.video_player_title_key));
            permaLink = args.getString(getString(R.string.video_player_permalink_key));
            isTrailer = args.getBoolean(getString(R.string.video_player_is_trailer_key));
            hlsUrl = args.getString(getContext().getString(R.string.video_player_hls_url_key));
            filmId = args.getString(getContext().getString(R.string.video_layer_film_id_key));
            adsUrl = args.getString(getContext().getString(R.string.video_player_ads_url_key));
            shouldRequestAds = args.getBoolean(getContext().getString(R.string.video_player_request_ads_key));
            playIndex = args.getInt(getString(R.string.play_index_key));
            watchedTime = args.getLong(getContext().getString(R.string.watched_time_key));
            imageUrl = args.getString(getContext().getString(R.string.played_movie_image_url));
            closedCaptionUrl = args.getString(getContext().getString(R.string.video_player_closed_caption_key));
            primaryCategory = args.getString(getString(R.string.video_primary_category_key));
            parentalRating = args.getString(getString(R.string.video_player_content_rating_key));
        }

        sentBeaconPlay = (0 < playIndex && watchedTime != 0);

        appCMSPresenter =
                ((AppCMSApplication) getActivity().getApplication())
                        .getAppCMSPresenterComponent()
                        .appCMSPresenter();

        beaconMsgTimeoutMsec = getActivity().getResources().getInteger(R.integer.app_cms_beacon_timeout_msec);
        beaconBufferingTimeoutMsec = getActivity().getResources().getInteger(R.integer.app_cms_beacon_buffering_timeout_msec);

        // It Handles the player stream Firebase events.
        setFirebaseProgressHandling();

        parentScreenName = getContext().getString(R.string.app_cms_beacon_video_player_parent_screen_name);
        setRetainInstance(true);

        AppsFlyerUtils.filmViewingEvent(getContext(), primaryCategory, filmId, appCMSPresenter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_player, container, false);

        videoPlayerMainContainer =
                (RelativeLayout) rootView.findViewById(R.id.app_cms_video_player_main_container);

        videoPlayerInfoContainer =
                (LinearLayout) rootView.findViewById(R.id.app_cms_video_player_info_container);

        mMediaRouteButton = (ImageButton) rootView.findViewById(R.id.media_route_button);

        videoPlayerTitleView = (TextView) rootView.findViewById(R.id.app_cms_video_player_title_view);

        if (!TextUtils.isEmpty(title)) {
            videoPlayerTitleView.setText(title);
        }
        if (!TextUtils.isEmpty(fontColor)) {
            videoPlayerTitleView.setTextColor(Color.parseColor(fontColor));
        }

        sendFirebaseAnalyticsEvents(title);

        videoPlayerViewDoneButton = (ImageButton) rootView.findViewById(R.id.app_cms_video_player_done_button);
        videoPlayerViewDoneButton.setOnClickListener(v -> {
            if (onClosePlayerEvent != null) {
                videoPlayerView.releasePlayer();
                onClosePlayerEvent.closePlayer();
            }
        });

        videoPlayerViewDoneButton.setColorFilter(Color.parseColor(fontColor));
        videoPlayerInfoContainer.bringToFront();
        videoPlayerView = (VideoPlayerView) rootView.findViewById(R.id.app_cms_video_player_container);
        videoPlayerView.setListener(this);

        videoLoadingProgress = (LinearLayout) rootView.findViewById(R.id.app_cms_video_loading);

        setCasting();

        if (!TextUtils.isEmpty(hlsUrl)) {
            videoPlayerView.setClosedCaptionEnabled(appCMSPresenter.getClosedCaptionPreference(getContext()));
            videoPlayerView.getPlayerView().getSubtitleView()
                    .setVisibility(appCMSPresenter.getClosedCaptionPreference(getContext())
                            ? View.VISIBLE
                            : View.GONE);
            videoPlayerView.setUri(Uri.parse(hlsUrl),
                    !TextUtils.isEmpty(closedCaptionUrl) ? Uri.parse(closedCaptionUrl) : null);
            Log.i(TAG, "Playing video: " + hlsUrl);
        }
        try {
            mStreamId = appCMSPresenter.getStreamingId(title);
        } catch (Exception e) {
            e.printStackTrace();
            mStreamId = filmId + appCMSPresenter.getCurrentTimeStamp();
        }
        isVideoDownloaded = appCMSPresenter.isVideoDownloaded(filmId);
        videoPlayerView.setCurrentPosition(watchedTime * SECS_TO_MSECS);
        videoPlayerView.setOnPlayerStateChanged(playerState -> {
            if (playerState.getPlaybackState() == ExoPlayer.STATE_READY && !isCastConnected) {
                if (shouldRequestAds && !isAdDisplayed) {
                    requestAds(adsUrl);
                } else {
                    if (beaconBufferingThread != null) {
                        beaconBufferingThread.sendBeaconBuffering = false;
                    }
                    if (beaconMessageThread != null) {
                        beaconMessageThread.sendBeaconPing = true;
                        if (!beaconMessageThread.isAlive()) {
                            beaconMessageThread.start();
                            mTotalVideoDuration = videoPlayerView.getDuration() / 1000;
                            mTotalVideoDuration -= mTotalVideoDuration % 4;
                            mProgressHandler.post(mProgressRunnable);
                        }
                        if (!sentBeaconFirstFrame) {
                            mStopBufferMilliSec = new Date().getTime();
                            ttfirstframe = ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                            appCMSPresenter.sendBeaconMessage(filmId,
                                    permaLink,
                                    parentScreenName,
                                    videoPlayerView.getCurrentPosition(),
                                    false,
                                    AppCMSPresenter.BeaconEvent.FIRST_FRAME,
                                    "Video",
                                    String.valueOf(videoPlayerView.getBitrate()),
                                    String.valueOf(videoPlayerView.getHeight()),
                                    String.valueOf(videoPlayerView.getWidth()),
                                    mStreamId,
                                    ttfirstframe,
                                    0,
                                    isVideoDownloaded);
                            sentBeaconFirstFrame = true;

                        }
                    }
                }
                videoLoadingProgress.setVisibility(View.GONE);
            } else if (playerState.getPlaybackState() == ExoPlayer.STATE_ENDED) {
                Log.d(TAG, "Video ended");
                if (shouldRequestAds && adsLoader != null) {
                    adsLoader.contentComplete();
                }

                // close the player if current video is a trailer. We don't want to auto-play it
                if (onClosePlayerEvent != null &&
                        permaLink.contains(
                                getString(R.string.app_cms_action_qualifier_watchvideo_key))) {
                    videoPlayerView.releasePlayer();
                    onClosePlayerEvent.closePlayer();
                    return;
                }

                if (onClosePlayerEvent != null && playerState.isPlayWhenReady()) {
                    // tell the activity that the movie is finished
                    onClosePlayerEvent.onMovieFinished();
                }
                if (!isTrailer) {
                    appCMSPresenter.updateWatchedTime(filmId,
                            videoPlayerView.getCurrentPosition() / 1000);
                }
            } else if (playerState.getPlaybackState() == ExoPlayer.STATE_BUFFERING ||
                    playerState.getPlaybackState() == ExoPlayer.STATE_IDLE) {
                if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = false;
                }
                if (beaconBufferingThread != null) {
                    beaconBufferingThread.sendBeaconBuffering = true;
                    if (!beaconBufferingThread.isAlive()) {
                        beaconBufferingThread.start();
                    }
                }
                videoLoadingProgress.setVisibility(View.VISIBLE);
            }

            if (!sentBeaconPlay) {
              /*  appCMSPresenter.sendBeaconPlayMessage(filmId,
                        permaLink,
                        parentScreenName,
                        videoPlayerView.getCurrentPosition(),
                        false);*/
                appCMSPresenter.sendBeaconMessage(filmId,
                        permaLink,
                        parentScreenName,
                        videoPlayerView.getCurrentPosition(),
                        false,
                        AppCMSPresenter.BeaconEvent.PLAY,
                        "Video",
                        String.valueOf(videoPlayerView.getBitrate()),
                        String.valueOf(videoPlayerView.getHeight()),
                        String.valueOf(videoPlayerView.getWidth()),
                        mStreamId,
                        0d,
                        0,
                        isVideoDownloaded);
                sentBeaconPlay = true;
                mStartBufferMilliSec = new Date().getTime();
            }
        });
        videoPlayerView.setOnPlayerControlsStateChanged(visibility -> {
            if (visibility == View.GONE) {
                videoPlayerInfoContainer.setVisibility(View.GONE);
            } else if (visibility == View.VISIBLE) {
                videoPlayerInfoContainer.setVisibility(View.VISIBLE);
            }
        });

        videoPlayerView.setOnClosedCaptionButtonClicked(isChecked -> {
            videoPlayerView.getPlayerView().getSubtitleView()
                    .setVisibility(isChecked ? View.VISIBLE : View.GONE);
            appCMSPresenter.setClosedCaptionPreference(getContext(), isChecked);
        });

        initViewForCRW(rootView);
        if (!shouldRequestAds) {
            try {
                createContentRatingView();
            } catch (Exception e) {
                Log.e(TAG, "Error ContentRatingView: " + e.getMessage());
            }
        }

        beaconMessageThread = new BeaconPingThread(beaconMsgTimeoutMsec,
                appCMSPresenter,
                filmId,
                permaLink,
                isTrailer,
                parentScreenName,
                videoPlayerView,
                mStreamId);

        beaconBufferingThread = new BeaconBufferingThread(beaconBufferingTimeoutMsec,
                appCMSPresenter,
                filmId,
                permaLink,
                parentScreenName,
                videoPlayerView,
                mStreamId);

        videoLoadingProgress.bringToFront();
        videoLoadingProgress.setVisibility(View.VISIBLE);

        showCRWWarningMessage = true;

        return rootView;

    }

    private void sendFirebaseAnalyticsEvents(String screenVideoName) {
        if (screenVideoName == null)
            return;
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_SCREEN_VIEW_EVENT, PLAYER_SCREEN_NAME + "-" + screenVideoName);
        //Logs an app event.
        appCMSPresenter.getmFireBaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
        //Sets whether analytics collection is enabled for this app on this device.
        appCMSPresenter.getmFireBaseAnalytics().setAnalyticsCollectionEnabled(true);
    }


    private void setCasting() {
        try {
            castProvider = CastServiceProvider.getInstance(getActivity());
            castProvider.setRemotePlaybackCallback(callBackRemotePlayback);
            isCastConnected = castProvider.isCastingConnected();
            castProvider.playChromeCastPlaybackIfCastConnected();
            if (isCastConnected) {
                getActivity().finish();
            } else {
                castProvider.setActivityInstance(getActivity(), mMediaRouteButton);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing cast provider: " + e.getMessage());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sdkFactory = ImaSdkFactory.getInstance();
        adsLoader = sdkFactory.createAdsLoader(getContext());
        adsLoader.addAdErrorListener(this);
        adsLoader.addAdsLoadedListener(listenerAdsLoaded);
    }

    @Override
    public void onResume() {
        resumeVideo();
        super.onResume();
    }

    @Override
    public void onPause() {
        pauseVideo();
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getPercentageFromResource();
    }

    private void pauseVideo() {
        if (shouldRequestAds && adsManager != null && isAdDisplayed) {
            adsManager.pause();
        } else {
            videoPlayerView.pausePlayer();
        }

        if (beaconMessageThread != null) {
            beaconMessageThread.sendBeaconPing = false;
        }
        if (beaconBufferingThread != null) {
            beaconBufferingThread.sendBeaconBuffering = false;
        }
    }

    private void resumeVideo() {
        if (shouldRequestAds && adsManager != null && isAdDisplayed) {
            adsManager.resume();
        } else {
            videoPlayerView.resumePlayer();
            if (beaconMessageThread != null) {
                beaconMessageThread.sendBeaconPing = true;
            }
            if (beaconBufferingThread != null) {
                beaconBufferingThread.sendBeaconBuffering = true;
            }
            Log.d(TAG, "Resuming playback");
        }
        if (castProvider != null) {
            castProvider.onActivityResume();
        }
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Log.e(TAG, "Ad DialogType: " + adErrorEvent.getError().getMessage());
//        createContentRatingView();
        videoPlayerView.resumePlayer();
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        Log.i(TAG, "Event: " + adEvent.getType());

        switch (adEvent.getType()) {
            case LOADED:
                adsManager.start();
                break;

            case CONTENT_PAUSE_REQUESTED:
                isAdDisplayed = true;
                if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = false;
                    if (mProgressHandler != null)
                        mProgressHandler.removeCallbacks(mProgressRunnable);
                }
                if (appCMSPresenter != null) {

                    appCMSPresenter.sendBeaconMessage(filmId,
                            permaLink,
                            parentScreenName,
                            videoPlayerView.getCurrentPosition(),
                            false,
                            AppCMSPresenter.BeaconEvent.AD_IMPRESSION,
                            "Video",
                            String.valueOf(videoPlayerView.getBitrate()),
                            String.valueOf(videoPlayerView.getHeight()),
                            String.valueOf(videoPlayerView.getWidth()),
                            mStreamId,
                            0d,
                            apod,
                            isVideoDownloaded);
                }
                videoPlayerView.pausePlayer();
                break;

            case CONTENT_RESUME_REQUESTED:
                isAdDisplayed = false;
                // videoPlayerView.startPlayer();
                if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = true;
                }
                if (appCMSPresenter != null) {
                    mStopBufferMilliSec = new Date().getTime();
                    ttfirstframe = ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                    appCMSPresenter.sendBeaconMessage(filmId,
                            permaLink,
                            parentScreenName,
                            videoPlayerView.getCurrentPosition(),
                            false,
                            AppCMSPresenter.BeaconEvent.FIRST_FRAME,
                            "Video",
                            String.valueOf(videoPlayerView.getBitrate()),
                            String.valueOf(videoPlayerView.getHeight()),
                            String.valueOf(videoPlayerView.getWidth()),
                            mStreamId,
                            ttfirstframe,
                            0,
                            isVideoDownloaded);
                }
                if (beaconMessageThread != null && !beaconMessageThread.isAlive()) {
                    beaconMessageThread.start();

                    if (mProgressHandler != null)
                        mProgressHandler.post(mProgressRunnable);
                }
                break;

            case ALL_ADS_COMPLETED:
                videoLoadingProgress.setVisibility(View.GONE);
                try {
                    createContentRatingView();
                } catch (Exception e) {
                    Log.e(TAG, "Error ContentRatingView: " + e.getMessage());
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

    @Override
    public void onDestroyView() {
        videoPlayerView.setOnPlayerStateChanged(null);
        videoPlayerView.releasePlayer();
        beaconMessageThread.sendBeaconPing = false;
        beaconMessageThread.runBeaconPing = false;
        beaconMessageThread.videoPlayerView = null;
        beaconMessageThread = null;

        beaconBufferingThread.sendBeaconBuffering = false;
        beaconBufferingThread.runBeaconBuffering = false;
        beaconBufferingThread.videoPlayerView = null;
        beaconBufferingThread = null;


        onClosePlayerEvent = null;
        if (adsLoader != null) {
            adsLoader.removeAdsLoadedListener(listenerAdsLoaded);
            adsLoader.removeAdErrorListener(this);
        }
        adsLoader = null;

        super.onDestroyView();
    }

    public void setFirebaseProgressHandling() {
        mProgressHandler = new Handler();
        mProgressRunnable = new Runnable() {
            @Override
            public void run() {
                mProgressHandler.removeCallbacks(this);
                long totalVideoDurationMod4 = mTotalVideoDuration / 4;
                if (totalVideoDurationMod4 > 0) {
                    if (((videoPlayerView.getCurrentPosition() / 1000) % totalVideoDurationMod4) == 0) {
                        long mPercentage = (long) (((float) (videoPlayerView.getCurrentPosition() / 1000) / mTotalVideoDuration) * 100);
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
        if (progressPercent == 0)
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_START, bundle);
        if (progressPercent == 25)
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_25, bundle);
        if (progressPercent == 50)
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_50, bundle);
        if (progressPercent == 75)
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_75, bundle);
    }

    private void requestAds(String adTagUrl) {
        if (!TextUtils.isEmpty(adTagUrl) && adsLoader != null) {
            Log.d(TAG, "Requesting ads: " + adTagUrl);
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
            if (appCMSPresenter != null) {

                appCMSPresenter.sendBeaconMessage(filmId,
                        permaLink,
                        parentScreenName,
                        videoPlayerView.getCurrentPosition(),
                        false,
                        AppCMSPresenter.BeaconEvent.AD_REQUEST,
                        "Video",
                        String.valueOf(videoPlayerView.getBitrate()),
                        String.valueOf(videoPlayerView.getHeight()),
                        String.valueOf(videoPlayerView.getWidth()),
                        mStreamId,
                        0d,
                        apod,
                        isVideoDownloaded);
            }
        }
    }

    @Override
    public void onFinishCallback(String message) {

        AppCMSPresenter.BeaconEvent event;
        if (message.contains("Unable")) {// If video position is something else then 0 It is droped in between playing
            event = AppCMSPresenter.BeaconEvent.DROPPED_STREAM;
        } else if (message.contains("Response")) {
            event = AppCMSPresenter.BeaconEvent.FAILED_TO_START;
        } else {
            event = AppCMSPresenter.BeaconEvent.FAILED_TO_START;
        }

        appCMSPresenter.sendBeaconMessage(filmId,
                permaLink,
                parentScreenName,
                videoPlayerView.getCurrentPosition(),
                false,
                event,
                "Video",
                String.valueOf(videoPlayerView.getBitrate()),
                String.valueOf(videoPlayerView.getHeight()),
                String.valueOf(videoPlayerView.getWidth()),
                mStreamId,
                0d,
                0,
                isVideoDownloaded);
        videoPlayerView.releasePlayer();
        onClosePlayerEvent.closePlayer();
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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

    private static class BeaconPingThread extends Thread {
        final long beaconMsgTimeoutMsec;
        final AppCMSPresenter appCMSPresenter;
        final String filmId;
        final String permaLink;
        final String parentScreenName;
        final String mStreamId;
        VideoPlayerView videoPlayerView;
        boolean runBeaconPing;
        boolean sendBeaconPing;
        boolean isTrailer;


        public BeaconPingThread(long beaconMsgTimeoutMsec,
                                AppCMSPresenter appCMSPresenter,
                                String filmId,
                                String permaLink,
                                boolean isTrailer,
                                String parentScreenName,
                                VideoPlayerView videoPlayerView,
                                String mStreamId) {
            this.beaconMsgTimeoutMsec = beaconMsgTimeoutMsec;
            this.appCMSPresenter = appCMSPresenter;
            this.filmId = filmId;
            this.permaLink = permaLink;
            this.parentScreenName = parentScreenName;
            this.videoPlayerView = videoPlayerView;
            this.isTrailer = isTrailer;
            this.mStreamId = mStreamId;
        }

        @Override
        public void run() {
            runBeaconPing = true;
            while (runBeaconPing) {
                try {
                    Thread.sleep(beaconMsgTimeoutMsec);
                    if (sendBeaconPing) {

                        if (appCMSPresenter != null && videoPlayerView != null && videoPlayerView.getPlayer().getPlayWhenReady()) { // For not to sent PIN in PAUSE mode
                            /*appCMSPresenter.sendBeaconPingMessage(filmId,

                                    permaLink,
                                    parentScreenName,
                                    videoPlayerView.getCurrentPosition(),
                                    false);*/

                            appCMSPresenter.sendBeaconMessage(filmId,
                                    permaLink,
                                    parentScreenName,
                                    videoPlayerView.getCurrentPosition(),
                                    false,
                                    AppCMSPresenter.BeaconEvent.PING,
                                    "Video",
                                    String.valueOf(videoPlayerView.getBitrate()),
                                    String.valueOf(videoPlayerView.getHeight()),
                                    String.valueOf(videoPlayerView.getWidth()),
                                    mStreamId,
                                    0d,
                                    0,
                                    isVideoDownloaded);

                            if (!isTrailer) {
                                appCMSPresenter.updateWatchedTime(filmId,
                                        videoPlayerView.getCurrentPosition() / 1000);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "BeaconPingThread sleep interrupted");
                }
            }
        }
    }


    private static class BeaconBufferingThread extends Thread {
        final long beaconBufferTimeoutMsec;
        final AppCMSPresenter appCMSPresenter;
        final String filmId;
        final String permaLink;
        final String parentScreenName;
        final String mStreamId;
        VideoPlayerView videoPlayerView;
        boolean runBeaconBuffering;
        boolean sendBeaconBuffering;


        public BeaconBufferingThread(long beaconBufferTimeoutMsec,
                                     AppCMSPresenter appCMSPresenter,
                                     String filmId,
                                     String permaLink,
                                     String parentScreenName,
                                     VideoPlayerView videoPlayerView,
                                     String mStreamId) {
            this.beaconBufferTimeoutMsec = beaconBufferTimeoutMsec;
            this.appCMSPresenter = appCMSPresenter;
            this.filmId = filmId;
            this.permaLink = permaLink;
            this.parentScreenName = parentScreenName;
            this.videoPlayerView = videoPlayerView;
            this.mStreamId = mStreamId;
        }

        public void run() {
            runBeaconBuffering = true;
            while (runBeaconBuffering) {
                try {
                    Thread.sleep(beaconBufferTimeoutMsec);
                    if (sendBeaconBuffering) {

                        if (appCMSPresenter != null && videoPlayerView != null &&
                                videoPlayerView.getPlayer().getPlayWhenReady() &&
                                videoPlayerView.getPlayer().getPlaybackState() == ExoPlayer.STATE_BUFFERING) { // For not to sent PIN in PAUSE mode
                            appCMSPresenter.sendBeaconMessage(filmId,
                                    permaLink,
                                    parentScreenName,
                                    videoPlayerView.getCurrentPosition(),
                                    false,
                                    AppCMSPresenter.BeaconEvent.BUFFERING,
                                    "Video",
                                    String.valueOf(videoPlayerView.getBitrate()),
                                    String.valueOf(videoPlayerView.getHeight()),
                                    String.valueOf(videoPlayerView.getWidth()),
                                    mStreamId,
                                    0d,
                                    0,
                                    isVideoDownloaded);

                        }
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "beaconBufferingThread sleep interrupted");
                }
            }
        }
    }

    private void initViewForCRW(View rootView) {

        contentRatingMainContainer =
                (PercentRelativeLayout) rootView.findViewById(R.id.app_cms_content_rating_main_container);

        contentRatingAnimationContainer =
                (PercentRelativeLayout) rootView.findViewById(R.id.app_cms_content_rating_animation_container);

        contentRatingInfoContainer =
                (LinearLayout) rootView.findViewById(R.id.app_cms_content_rating_info_container);

        contentRatingHeaderView = (TextView) rootView.findViewById(R.id.app_cms_content_rating_header_view);
        setTypeFace(getContext(), contentRatingHeaderView, getString(R.string.helvaticaneu_bold));

        contentRatingTitleView = (TextView) rootView.findViewById(R.id.app_cms_content_rating_title_view);
        setTypeFace(getContext(), contentRatingTitleView, getString(R.string.helvaticaneu_bold));

        contentRatingDiscretionView = (TextView) rootView.findViewById(R.id.app_cms_content_rating_viewer_discretion);
        setTypeFace(getContext(), contentRatingDiscretionView, getString(R.string.helvaticaneu_bold));

        contentRatingBack = (TextView) rootView.findViewById(R.id.app_cms_content_rating_back);
        setTypeFace(getContext(), contentRatingBack, getContext().getString(R.string.helvaticaneu_bold));

        contentRatingBackUnderline = rootView.findViewById(R.id.app_cms_content_rating_back_underline);

        progressBar = (ProgressBar) rootView.findViewById(R.id.app_cms_content_rating_progress_bar);

        if (!TextUtils.isEmpty(fontColor)) {
            contentRatingTitleView.setTextColor(Color.parseColor(fontColor));
            contentRatingDiscretionView.setTextColor(Color.parseColor(fontColor));
            contentRatingBack.setTextColor(Color.parseColor(fontColor));
        }

        if (!TextUtils.isEmpty(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor())) {
            int highlightColor =
                    Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor());
            contentRatingBackUnderline.setBackgroundColor(highlightColor);
            contentRatingHeaderView.setTextColor(highlightColor);
            applyBorderToComponent(contentRatingInfoContainer, 1, highlightColor);
            progressBar.getProgressDrawable()
                    .setColorFilter(highlightColor, PorterDuff.Mode.SRC_IN);
        }

        contentRatingBack.setOnClickListener(v -> {
            getActivity().finish();
        });

        contentRatingDiscretionView.setVisibility(View.GONE);
    }

    private void createContentRatingView() throws Exception {
        if (!isTrailer && getParentalRating() != null && !getParentalRating().equalsIgnoreCase(getString(R.string.age_rating_converted_default))) {
            videoPlayerMainContainer.setVisibility(View.GONE);
            animateView();
            startCountdown();
        } else {
            contentRatingMainContainer.setVisibility(View.GONE);
            videoPlayerMainContainer.setVisibility(View.VISIBLE);
            videoPlayerView.startPlayer();
        }
    }

    private String getParentalRating() {
        if (!TextUtils.isEmpty(parentalRating) && !parentalRating.contentEquals(getContext().getString(R.string.age_rating_converted_default))) {
            contentRatingTitleView.setText(getResources().getString(R.string.content_rating_description) + parentalRating);
        }
        return parentalRating;
    }

    private void startCountdown() {
        new CountDownTimer(totalCountdownInMillis, countDownIntervalInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                long time = totalCountdownInMillis - millisUntilFinished;
                progressBar.setProgress((int) time);
            }

            @Override
            public void onFinish() {
                contentRatingMainContainer.setVisibility(View.GONE);
                videoPlayerMainContainer.setVisibility(View.VISIBLE);
                videoPlayerView.startPlayer();
            }
        }.start();
    }

    private void applyBorderToComponent(View view, int width, int Color) {
        GradientDrawable rectangleBorder = new GradientDrawable();
        rectangleBorder.setShape(GradientDrawable.RECTANGLE);
        rectangleBorder.setStroke(width, Color);
        view.setBackground(rectangleBorder);
    }

    private void getPercentageFromResource() {

        float heightPercent = getResources().getFraction(R.fraction.mainContainerHeightPercent, 1, 1);
        float widthPercent = getResources().getFraction(R.fraction.mainContainerWidthPercent, 1, 1);
        float bottomMarginPercent = getResources().getFraction(R.fraction.app_cms_content_rating_progress_bar_margin_bottom_percent, 1, 1);

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

        animSequential = AnimationUtils.loadAnimation(getContext(),
                R.anim.sequential);
        animFadeIn = AnimationUtils.loadAnimation(getContext(),
                R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getContext(),
                R.anim.fade_out);
        animTranslate = AnimationUtils.loadAnimation(getContext(),
                R.anim.translate);

        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        animSequential.setAnimationListener(this);
        animTranslate.setAnimationListener(this);

        contentRatingMainContainer.setVisibility(View.VISIBLE);

        if (getParentalRating().contains(getString(R.string.age_rating_pg)) ||
                !getParentalRating().contains(getString(R.string.age_rating_g))) {
            contentRatingHeaderView.startAnimation(animFadeIn);
            contentRatingInfoContainer.startAnimation(animFadeIn);
        } else {
            contentRatingHeaderView.setVisibility(View.GONE);
        }
        contentRatingInfoContainer.setVisibility(View.VISIBLE);


        contentRatingTitleView.startAnimation(animSequential);
        contentRatingTitleView.setVisibility(View.VISIBLE);

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

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (showCRWWarningMessage &&
                getParentalRating().contains(getString(R.string.age_rating_pg)) ||
                !getParentalRating().contains(getString(R.string.age_rating_g))) {
            contentRatingDiscretionView.startAnimation(animFadeOut);
            contentRatingDiscretionView.setVisibility(View.VISIBLE);
            showCRWWarningMessage = false;
        } else {
            contentRatingDiscretionView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
