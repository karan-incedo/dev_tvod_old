package com.viewlift.views.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.viewlift.views.customviews.VideoPlayerView;

import rx.functions.Action1;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 6/14/17.
 */

public class AppCMSPlayVideoFragment extends Fragment
        implements AdErrorEvent.AdErrorListener, AdEvent.AdEventListener{
    private static final String TAG = "PlayVideoFragment";

    private String hlsUrl;
    private String adsUrl;
    private Button videoPlayerViewDoneButton;
    private VideoPlayerView videoPlayerView;

    private ImaSdkFactory sdkFactory;
    private AdsLoader adsLoader;
    private AdsManager adsManager;
    private boolean isAdDisplayed;
    private OnClosePlayerEvent onClosePlayerEvent;

    public interface OnClosePlayerEvent {
        void closePlayer();
    }

    public static AppCMSPlayVideoFragment newInstance(Context context,
                                                      String hlsUrl,
                                                      String adsUrl) {
        AppCMSPlayVideoFragment appCMSPlayVideoFragment = new AppCMSPlayVideoFragment();
        Bundle args = new Bundle();
        args.putString(context.getString(R.string.video_player_hls_url_key), hlsUrl);
        args.putString(context.getString(R.string.video_player_ads_url_key), adsUrl);
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
            hlsUrl = args.getString(getContext().getString(R.string.video_player_hls_url_key));
            adsUrl = args.getString(getContext().getString(R.string.video_player_ads_url_key));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_player, container, false);
        videoPlayerViewDoneButton = (Button) rootView.findViewById(R.id.app_cms_video_player_done_button);
        videoPlayerViewDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClosePlayerEvent != null) {
                    onClosePlayerEvent.closePlayer();
                }
            }
        });
        videoPlayerView = (VideoPlayerView) rootView.findViewById(R.id.app_cms_video_player_container);
        if (!TextUtils.isEmpty(hlsUrl)) {
            videoPlayerView.setUri(Uri.parse(hlsUrl));
        }
        videoPlayerView.setOnPlayerStateChanged(new Action1<VideoPlayerView.PlayerState>() {
            @Override
            public void call(VideoPlayerView.PlayerState playerState) {
                if (playerState.getPlaybackState() == ExoPlayer.STATE_READY) {
                    requestAds(adsUrl);
                } else if (playerState.getPlaybackState() == ExoPlayer.STATE_ENDED) {
                    Log.d(TAG, "Video ended");
                    adsLoader.contentComplete();
                    if (onClosePlayerEvent != null) {
                        onClosePlayerEvent.closePlayer();
                    }
                }
            }
        });
        videoPlayerView.setOnPlayerControlsStateChanged(new Action1<Integer>() {
            @Override
            public void call(Integer visiblity) {
                if (visiblity == View.GONE) {
                    videoPlayerViewDoneButton.setVisibility(View.GONE);
                } else if (visiblity == View.VISIBLE) {
                    videoPlayerViewDoneButton.setVisibility(View.VISIBLE);
                }
            }
        });
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
        if (adsManager != null && isAdDisplayed) {
            adsManager.resume();
        } else {
            videoPlayerView.startPlayer();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (adsManager != null && isAdDisplayed) {
            adsManager.pause();
        } else {
            videoPlayerView.pausePlayer();
        }
        super.onPause();
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Log.e(TAG, "Ad Error: " + adErrorEvent.getError().getMessage());
        videoPlayerView.startPlayer();
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
                videoPlayerView.pausePlayer();
                break;
            case CONTENT_RESUME_REQUESTED:
                isAdDisplayed = false;
                videoPlayerView.startPlayer();
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

    private void requestAds(String adTagUrl) {
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
    }
}
