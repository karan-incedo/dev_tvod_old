package com.viewlift.views.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.google.android.exoplayer2.ExoPlayer;
import com.viewlift.AppCMSApplication;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.VideoPlayerView;

import rx.functions.Action1;
import com.viewlift.R;

/**
 * Created by viewlift on 6/14/17.
 */

public class AppCMSPlayVideoFragment extends Fragment
        implements AdErrorEvent.AdErrorListener, AdEvent.AdEventListener {
    private static final String TAG = "PlayVideoFragment";

    private static final long SECS_TO_MSECS = 1000L;

    private AppCMSPresenter appCMSPresenter;

    private String fontColor;
    private String title;
    private String hlsUrl;
    private String permaLink;
    private String filmId;
    private String parentScreenName;
    private String adsUrl;
    private boolean shouldRequestAds;
    private ConstraintLayout videoPlayerInfoContainer;
    private Button videoPlayerViewDoneButton;
    private TextView videoPlayerTitleView;
    private VideoPlayerView videoPlayerView;
    private OnClosePlayerEvent onClosePlayerEvent;
    private BeaconPingThread beaconMessageThread;
    private long beaconMsgTimeoutMsec;

    private ImaSdkFactory sdkFactory;
    private AdsLoader adsLoader;
    private AdsManager adsManager;
    private boolean isAdDisplayed;
    private int watchedTime;

    public interface OnClosePlayerEvent {
        void closePlayer();
    }

    private static class BeaconPingThread extends Thread {
        final long beaconMsgTimeoutMsec;
        final AppCMSPresenter appCMSPresenter;
        final String filmId;
        final String permaLink;
        final String parentScreenName;
        final VideoPlayerView videoPlayerView;
        boolean runBeaconPing;
        boolean sendBeaconPing;

        public BeaconPingThread(long beaconMsgTimeoutMsec,
                                AppCMSPresenter appCMSPresenter,
                                String filmId,
                                String permaLink,
                                String parentScreenName,
                                VideoPlayerView videoPlayerView) {
            this.beaconMsgTimeoutMsec = beaconMsgTimeoutMsec;
            this.appCMSPresenter = appCMSPresenter;
            this.filmId = filmId;
            this.permaLink = permaLink;
            this.parentScreenName = parentScreenName;
            this.videoPlayerView = videoPlayerView;
        }

        public void run() {
            runBeaconPing = true;
            while (runBeaconPing) {
                try {
                    Thread.sleep(beaconMsgTimeoutMsec);
                    if (sendBeaconPing) {
                        if (appCMSPresenter != null && videoPlayerView != null) {
                            appCMSPresenter.sendBeaconPingMessage(filmId,
                                    permaLink,
                                    parentScreenName,
                                    videoPlayerView.getCurrentPosition());
                            appCMSPresenter.updateWatchedTime(filmId,
                                    videoPlayerView.getCurrentPosition() / 1000);
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "BeaconPingThread sleep interrupted");
                }
            }
        }
    }

    public static AppCMSPlayVideoFragment newInstance(Context context,
                                                      String fontColor,
                                                      String title,
                                                      String permaLink,
                                                      String hlsUrl,
                                                      String filmId,
                                                      String adsUrl,
                                                      boolean requestAds,
                                                      int watchedTime) {
        AppCMSPlayVideoFragment appCMSPlayVideoFragment = new AppCMSPlayVideoFragment();
        Bundle args = new Bundle();
        args.putString(context.getString(R.string.video_player_font_color_key), fontColor);
        args.putString(context.getString(R.string.video_player_title_key), title);
        args.putString(context.getString(R.string.video_player_permalink_key), permaLink);
        args.putString(context.getString(R.string.video_player_hls_url_key), hlsUrl);
        args.putString(context.getString(R.string.video_layer_film_id_key), filmId);
        args.putString(context.getString(R.string.video_player_ads_url_key), adsUrl);
        args.putBoolean(context.getString(R.string.video_player_request_ads_key), requestAds);
        args.putInt(context.getString(R.string.watched_time_key), watchedTime);
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
            hlsUrl = args.getString(getContext().getString(R.string.video_player_hls_url_key));
            filmId = args.getString(getContext().getString(R.string.video_layer_film_id_key));
            adsUrl = args.getString(getContext().getString(R.string.video_player_ads_url_key));
            shouldRequestAds = false;
            watchedTime = args.getInt(getContext().getString(R.string.watched_time_key));
        }

        appCMSPresenter =
                ((AppCMSApplication) getActivity().getApplication())
                        .getAppCMSPresenterComponent()
                        .appCMSPresenter();

        beaconMsgTimeoutMsec =
                getActivity().getResources().getInteger(R.integer.app_cms_beacon_timeout_msec);

        parentScreenName = getContext().getString(R.string.app_cms_beacon_video_player_parent_screen_name);

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_player, container, false);

        videoPlayerInfoContainer =
                (ConstraintLayout) rootView.findViewById(R.id.app_cms_video_player_info_container);

        videoPlayerTitleView = (TextView) rootView.findViewById(R.id.app_cms_video_player_title_view);
        if (!TextUtils.isEmpty(title)) {
            videoPlayerTitleView.setText(title);
        }
        if (!TextUtils.isEmpty(fontColor)) {
            videoPlayerTitleView.setTextColor(Color.parseColor(fontColor));
        }

        videoPlayerViewDoneButton = (Button) rootView.findViewById(R.id.app_cms_video_player_done_button);
        videoPlayerViewDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClosePlayerEvent != null) {
                    videoPlayerView.releasePlayer();
                    onClosePlayerEvent.closePlayer();
                }
            }
        });
        videoPlayerViewDoneButton.setTextColor(Color.parseColor(fontColor));

        videoPlayerInfoContainer.bringToFront();

        videoPlayerView = (VideoPlayerView) rootView.findViewById(R.id.app_cms_video_player_container);
        if (!TextUtils.isEmpty(hlsUrl)) {
            videoPlayerView.setUri(Uri.parse(hlsUrl));
            Log.i(TAG, "Playing video: " + hlsUrl);
        }
        videoPlayerView.setCurrentPosition((long) watchedTime * SECS_TO_MSECS);
        videoPlayerView.setOnPlayerStateChanged(new Action1<VideoPlayerView.PlayerState>() {
            @Override
            public void call(VideoPlayerView.PlayerState playerState) {
                if (playerState.getPlaybackState() == ExoPlayer.STATE_READY) {
                    if (shouldRequestAds) {
                        requestAds(adsUrl);
                    } else {
                        videoPlayerView.resumePlayer();
                    }
                } else if (playerState.getPlaybackState() == ExoPlayer.STATE_ENDED) {
                    Log.d(TAG, "Video ended");
                    if (shouldRequestAds) {
                        adsLoader.contentComplete();
                    }
                    if (onClosePlayerEvent != null) {
                        videoPlayerView.releasePlayer();
                        onClosePlayerEvent.closePlayer();
                    }
                }
            }
        });
        videoPlayerView.setOnPlayerControlsStateChanged(new Action1<Integer>() {
            @Override
            public void call(Integer visiblity) {
                if (visiblity == View.GONE) {
                    videoPlayerInfoContainer.setVisibility(View.GONE);
                } else if (visiblity == View.VISIBLE) {
                    videoPlayerInfoContainer.setVisibility(View.VISIBLE);
                }
            }
        });
        if (!shouldRequestAds) {
            videoPlayerView.startPlayer();
        }
        beaconMessageThread = new BeaconPingThread(beaconMsgTimeoutMsec,
                appCMSPresenter,
                filmId,
                permaLink,
                parentScreenName,
                videoPlayerView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sdkFactory = ImaSdkFactory.getInstance();
        adsLoader = sdkFactory.createAdsLoader(getContext());
        adsLoader.addAdErrorListener(this);
        adsLoader.addAdsLoadedListener(new AdsLoader.AdsLoadedListener() {
            @Override
            public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
                adsManager = adsManagerLoadedEvent.getAdsManager();
                adsManager.addAdErrorListener(AppCMSPlayVideoFragment.this);
                adsManager.addAdEventListener(AppCMSPlayVideoFragment.this);
                adsManager.init();
            }
        });
    }

    @Override
    public void onResume() {
        if (shouldRequestAds && adsManager != null && isAdDisplayed) {
            adsManager.resume();
        } else {
            videoPlayerView.resumePlayer();
            Log.d(TAG, "Resuming playback");
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (shouldRequestAds && adsManager != null && isAdDisplayed) {
            adsManager.pause();
        } else {
            videoPlayerView.pausePlayer();
        }
        super.onPause();
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Log.e(TAG, "Ad DialogType: " + adErrorEvent.getError().getMessage());
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
                }
                if (appCMSPresenter != null) {
                    appCMSPresenter.sendBeaconAdImpression(filmId,
                            permaLink,
                            parentScreenName,
                            videoPlayerView.getCurrentPosition());
                }
                videoPlayerView.pausePlayer();
                break;
            case CONTENT_RESUME_REQUESTED:
                isAdDisplayed = false;
                videoPlayerView.startPlayer();
                if (beaconMessageThread != null) {
                    beaconMessageThread.sendBeaconPing = true;
                }
                if (appCMSPresenter != null) {
                    appCMSPresenter.sendBeaconPlayMessage(filmId,
                            permaLink,
                            parentScreenName,
                            videoPlayerView.getCurrentPosition());
                }
                if (beaconMessageThread != null && !beaconMessageThread.isAlive()) {
                    beaconMessageThread.start();
                }
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

    @Override
    public void onDestroyView() {
        videoPlayerView.releasePlayer();
        beaconMessageThread.sendBeaconPing = false;
        beaconMessageThread.runBeaconPing = false;
        beaconMessageThread = null;
        onClosePlayerEvent = null;
        adsLoader = null;
        super.onDestroyView();
    }

    private void requestAds(String adTagUrl) {
        if (!TextUtils.isEmpty(adTagUrl) && adsLoader != null) {
            Log.d(TAG, "Requesting ads: " + adTagUrl);
            AdDisplayContainer adDisplayContainer = sdkFactory.createAdDisplayContainer();
            adDisplayContainer.setAdContainer(videoPlayerView);

            AdsRequest request = sdkFactory.createAdsRequest();
            request.setAdTagUrl(adTagUrl);
            request.setAdDisplayContainer(adDisplayContainer);
            request.setContentProgressProvider(new ContentProgressProvider() {
                @Override
                public VideoProgressUpdate getContentProgress() {
                    if (isAdDisplayed || videoPlayerView.getDuration() <= 0) {
                        return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                    }
                    return new VideoProgressUpdate(videoPlayerView.getCurrentPosition(),
                            videoPlayerView.getDuration());
                }
            });

            adsLoader.requestAds(request);

            if (appCMSPresenter != null) {
                appCMSPresenter.sendBeaconAdRequestMessage(filmId,
                        permaLink,
                        parentScreenName,
                        videoPlayerView.getCurrentPosition());
            }
        }
    }
}
