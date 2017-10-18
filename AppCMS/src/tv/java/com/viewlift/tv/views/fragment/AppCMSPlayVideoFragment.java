package com.viewlift.tv.views.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.views.customviews.VideoPlayerView;

import rx.functions.Action1;

/**
 * Created by viewlift on 6/14/17.
 */

public class AppCMSPlayVideoFragment extends Fragment implements AdErrorEvent.AdErrorListener,
        AdEvent.AdEventListener, AppCmsTvErrorFragment.ErrorFragmentListener, VideoPlayerView.ErrorEventListener{
    private static final String TAG = "PlayVideoFragment";

    private AppCMSPresenter appCMSPresenter;
    private static final long SECONDS_TO_MILLIS = 1000L;

    private String fontColor;
    private String title;
    private String hlsUrl;
    private String permaLink;
    private String filmId;
    private String parentScreenName;
    private String adsUrl;
    private boolean shouldRequestAds;
    private RelativeLayout playBackStateLayout;
    private ProgressBar progressBar;
    private LinearLayout videoPlayerInfoContainer;
    private Button videoPlayerViewDoneButton;
    private TextView videoPlayerTitleView, playBackStateTextView;
    private VideoPlayerView videoPlayerView;
    private OnClosePlayerEvent onClosePlayerEvent;
    private BeaconPingThread beaconMessageThread;
    private long beaconMsgTimeoutMsec;

    private ImaSdkFactory sdkFactory;
    private AdsLoader adsLoader;
    private AdsManager adsManager;
    private boolean isAdDisplayed, isADPlay;
    AppCmsTvErrorFragment errorActivityFragment;
    boolean networkConnect, networkDisconnect = true;
    private String closedCaptionUrl;
    private Context mContext;
    private boolean isTrailer;
    private int playIndex;
    private long watchedTime;
    private String imageUrl;
    private String primaryCategory;
    private String parentalRating;
    private String mStreamId;
    private long runtime;
    private long videoPlayTime;

    public interface OnClosePlayerEvent {
        void closePlayer();
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

                        if (appCMSPresenter != null
                                && videoPlayerView != null
                                && videoPlayerView.getPlayer().getPlayWhenReady()) { // For not to sent PIN in PAUSE mode
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
                                    false);

                            if (!isTrailer) {
                                appCMSPresenter.updateWatchedTime(filmId,
                                        videoPlayerView.getCurrentPosition() / 1000);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    //Log.e(TAG, "BeaconPingThread sleep interrupted");
                }
            }
        }
    }

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
                                                      long runtime,
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
        args.putLong(context.getString(R.string.run_time_key), runtime);
        args.putString(context.getString(R.string.played_movie_image_url), imageUrl);
        args.putString(context.getString(R.string.video_player_closed_caption_key), closedCaptionUrl);
        args.putBoolean(context.getString(R.string.video_player_is_trailer_key), isTrailer);
        args.putString(context.getString(R.string.video_player_content_rating_key), parentalRating);
        appCMSPlayVideoFragment.setArguments(args);
        return appCMSPlayVideoFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
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
            hlsUrl = args.getString(getString(R.string.video_player_hls_url_key));
            filmId = args.getString(getString(R.string.video_layer_film_id_key));
            adsUrl = args.getString(getString(R.string.video_player_ads_url_key));
            shouldRequestAds = args.getBoolean(getString(R.string.video_player_request_ads_key));
            playIndex = args.getInt(getString(R.string.play_index_key));
            watchedTime = args.getLong(getString(R.string.watched_time_key));
            runtime = args.getLong(getString(R.string.run_time_key));
            imageUrl = args.getString(getString(R.string.played_movie_image_url));
            closedCaptionUrl = args.getString(getString(R.string.video_player_closed_caption_key));
            primaryCategory = args.getString(getString(R.string.video_primary_category_key));
            parentalRating = args.getString(getString(R.string.video_player_content_rating_key));
        }

        appCMSPresenter =
                ((AppCMSApplication) getActivity().getApplication())
                        .getAppCMSPresenterComponent()
                        .appCMSPresenter();

        beaconMsgTimeoutMsec =
                getActivity().getResources().getInteger(R.integer.app_cms_beacon_timeout_msec);

        parentScreenName = getActivity().getString(R.string.app_cms_beacon_video_player_parent_screen_name);

        setRetainInstance(true);
    }

    private void preparePlayer(){
        if (!TextUtils.isEmpty(hlsUrl)) {
            videoPlayerView.setClosedCaptionEnabled(false);
            videoPlayerView.getPlayerView().getSubtitleView()
                    .setVisibility(appCMSPresenter.getClosedCaptionPreference()
                            ? View.VISIBLE
                            : View.GONE);
            videoPlayerView.setUri(Uri.parse(hlsUrl),
                    !TextUtils.isEmpty(closedCaptionUrl) ? Uri.parse(closedCaptionUrl) : null);
            //Log.i(TAG, "Playing video: " + hlsUrl);
        }
        try {
            mStreamId = appCMSPresenter.getStreamingId(title);
        } catch (Exception e) {
            //Log.e(TAG, e.getMessage());
            mStreamId = filmId + appCMSPresenter.getCurrentTimeStamp();
        }


        long playDifference = runtime - watchedTime;//((watchedTime*100)/runTime);
        long playTimePercentage = ((watchedTime * 100) / runtime);

        // if video watchtime is greater or equal to 98% of total run time and interval is less than 30 then play from start
        if (isTrailer || (playTimePercentage >= 98 && playDifference <= 30)) {
            videoPlayTime = 0;
        } else {
            videoPlayTime = watchedTime;
        }

        videoPlayerView.setCurrentPosition(videoPlayTime * SECONDS_TO_MILLIS);

        videoPlayerView.setOnPlayerStateChanged(new Action1<VideoPlayerView.PlayerState>() {
            @Override
            public void call(VideoPlayerView.PlayerState playerState) {
                String text = "";
                switch(playerState.getPlaybackState()) {
                    case ExoPlayer.STATE_BUFFERING:
                        //Log.d(TAG, "Video STATE_BUFFERING");
                        text += "buffering...";
                        playBackStateLayout.setVisibility(View.VISIBLE);
                        break;
                    case ExoPlayer.STATE_READY:
                        //Log.d(TAG, "Video STATE_READY");
                        text += "";
                        playBackStateLayout.setVisibility(View.GONE);
                        if (shouldRequestAds && !isADPlay) {
                            requestAds(adsUrl);
                            isADPlay = true;
                        } /*else if(isTrailer){
                            videoPlayerView.startPlayer();
                        }*/
                        break;
                    case ExoPlayer.STATE_ENDED:
                        //Log.d(TAG, "Video STATE_ENDED");
                        playBackStateLayout.setVisibility(View.GONE);
                        if (shouldRequestAds) {
                            adsLoader.contentComplete();
                        }
                        if (onClosePlayerEvent != null) {
                            videoPlayerView.releasePlayer();
                            onClosePlayerEvent.closePlayer();
                        }
                        getActivity().finish();
                        break;
                    default:
                        text += "";
                        playBackStateLayout.setVisibility(View.GONE);
                        break;
                }
                playBackStateTextView.setText(text);
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
        videoPlayerView.setOnClosedCaptionButtonClicked(isChecked -> {
            videoPlayerView.getPlayerView().getSubtitleView()
                    .setVisibility(isChecked ? View.VISIBLE : View.GONE);
            appCMSPresenter.setClosedCaptionPreference(isChecked);
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_player_tv, container, false);

        videoPlayerInfoContainer =
                (LinearLayout) rootView.findViewById(R.id.app_cms_video_player_info_container);

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
        videoPlayerView.getPlayerView().hideController();
        videoPlayerInfoContainer.setVisibility(View.INVISIBLE);
        playBackStateLayout = (RelativeLayout) rootView.findViewById(R.id.playback_state_layout);
        playBackStateTextView = (TextView) rootView.findViewById(R.id.playback_state_text);
        playBackStateTextView.setTextColor(Color.parseColor(fontColor));
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        progressBar.getIndeterminateDrawable().
                setColorFilter(Color.parseColor(Utils.getFocusColor(getActivity(),appCMSPresenter)) ,
                        PorterDuff.Mode.MULTIPLY
                );

        if (!shouldRequestAds) {
            videoPlayerView.getPlayer().setPlayWhenReady(true);
            preparePlayer();
        }


        beaconMessageThread = new BeaconPingThread(beaconMsgTimeoutMsec,
                appCMSPresenter,
                filmId,
                permaLink,
                isTrailer,
                parentScreenName,
                videoPlayerView,
                mStreamId);
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sdkFactory = ImaSdkFactory.getInstance();
        adsLoader = sdkFactory.createAdsLoader(getActivity());
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
        videoPlayerView.setListener(this);
       /* if (shouldRequestAds && adsManager != null && isAdDisplayed) {
            adsManager.resume();
        } else {
            videoPlayerView.resumePlayer();
            //Log.d(TAG, "Resuming playback");
        }*/


       if(shouldRequestAds && !isADPlay){
           requestAds(adsUrl);
           isADPlay = true;
       }

        getActivity().registerReceiver(networkReciever , new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        super.onResume();
    }

    @Override
    public void onPause() {
        if (shouldRequestAds && adsManager != null && isAdDisplayed) {
            adsManager.pause();
        } else {
            videoPlayerView.pausePlayer();
        }
        getActivity().unregisterReceiver(networkReciever);
        super.onPause();
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        //Log.e(TAG, "Ad Error: " + adErrorEvent.getError().getMessage());
        videoPlayerView.getPlayer().setPlayWhenReady(true);
        preparePlayer();
       // videoPlayerView.getPlayer().setPlayWhenReady(true);
       // videoPlayerView.resumePlayer();
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        //Log.i(TAG, "Event: " + adEvent.getType());

        switch (adEvent.getType()) {
            case LOADED:
                playBackStateLayout.setVisibility(View.GONE);
                videoPlayerInfoContainer.setVisibility(View.GONE); //to hide the player controls.
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
                            videoPlayerView.getCurrentPosition(),
                            false);
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
                preparePlayer();
                videoPlayerInfoContainer.setVisibility(View.VISIBLE); //show player controlls.
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
            //Log.d(TAG, "Requesting ads: " + adTagUrl);
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

    public boolean showController(KeyEvent event){
        SimpleExoPlayerView playerView = videoPlayerView.getPlayerView();
        if(playerView.getPlayer().getPlayWhenReady()){
            playerView.showController();
            return playerView.dispatchMediaKeyEvent(event);
        }
     return true;
    }

    BroadcastReceiver networkReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null && action.equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")){
                if(appCMSPresenter.isNetworkConnected()){
                    //TODO:resume the video.
                    if(networkConnect) {
                        networkDisconnect = true;
                        if (!TextUtils.isEmpty(hlsUrl)) {
                            videoPlayerView.sendPlayerPosition(videoPlayerView.getPlayer().getCurrentPosition());
                            videoPlayerView.setUriOnConnection(Uri.parse(hlsUrl), null);
                        }
                    }
                } else {
                    if(networkDisconnect) {
                        networkConnect = true;
                        networkDisconnect = false;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(getString(R.string.retry_key), false);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        errorActivityFragment = AppCmsTvErrorFragment.newInstance(
                                bundle);
                        errorActivityFragment.setErrorListener(AppCMSPlayVideoFragment.this);
                        errorActivityFragment.show(ft, getString(R.string.error_dialog_fragment_tag));
                    }
                }
            }
        }
    };

    @Override
    public void onErrorScreenClose() {
        errorActivityFragment.dismiss();
    }

    @Override
    public void onRetry(Bundle bundle) {

    }

    @Override
    public void onRefreshTokenCallback() {

    }

    @Override
    public void onFinishCallback(String message) {
        getActivity().finish();
    }
}
