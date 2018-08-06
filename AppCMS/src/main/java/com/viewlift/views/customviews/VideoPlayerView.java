package com.viewlift.views.customviews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.MediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdPlaybackState;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.ContentDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.viewlift.AppCMSApplication;
import com.viewlift.R;
import com.viewlift.Utils;
import com.viewlift.models.data.appcms.api.ClosedCaptions;
import com.viewlift.models.data.playersettings.HLSStreamingQuality;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AppCMSPlayVideoActivity;
import com.viewlift.views.adapters.ClosedCaptionSelectorAdapter;
import com.viewlift.views.adapters.HLSStreamingQualitySelectorAdapter;
import com.viewlift.views.adapters.StreamingQualitySelectorAdapter;
import com.viewlift.views.customviews.exoplayerview.AppCMSSimpleExoPlayerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import rx.Observable;
import rx.functions.Action1;


/**
 * Created by viewlift on 5/31/17.
 */

public class VideoPlayerView extends FrameLayout implements Player.EventListener,
        MediaSourceEventListener, com.google.android.exoplayer2.video.VideoListener,
        VideoRendererEventListener, AudioManager.OnAudioFocusChangeListener,
        AdsMediaSource.EventListener,
        DefaultDrmSessionManager.EventListener {
    private static final String TAG = "VideoPlayerView";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    protected DataSource.Factory mediaDataSourceFactory;
    protected String userAgent;
    protected PlayerState playerState;
    protected SimpleExoPlayer player;
    protected AppCMSSimpleExoPlayerView playerView;
    boolean isLoadedNext;
    OnBeaconAdsEvent onBeaconAdsEvent;
    DefaultTrackSelector trackSelector;
    TrackGroupArray trackGroups;
    private AppCMSPresenter appCMSPresenter;
    private ImageButton ccToggleButton, mSettingButton;
    private LinearLayout chromecastLivePlayerParent;
    private ViewGroup chromecastButtonPreviousParent;
    private FrameLayout chromecastButtonPlaceholder;
    private ImageButton enterFullscreenButton;
    private ImageButton exitFullscreenButton;
    private TextView currentStreamingQualitySelector;
    private AlwaysSelectedTextView videoPlayerTitle;
    private DefaultTimeBar timeBar;
    private boolean isClosedCaptionEnabled = false;
    private Uri uri;
    private Action1<PlayerState> onPlayerStateChanged;
    private Action1<Integer> onPlayerControlsStateChanged;
    private Action1<Boolean> onClosedCaptionButtonClicked;
    private int resumeWindow;
    private long resumePosition;
    private int timeBarColor;
    private long bitrate = 0l;
    private int videoHeight = 0;
    private int videoWidth = 0;
    private long mCurrentPlayerPosition;
    private ErrorEventListener mErrorEventListener;
    private StreamingQualitySelector streamingQualitySelector;
    private ClosedCaptionSelector closedCaptionSelector;
    private VideoPlayerSettingsEvent videoPlayerSettingsEvent;
    private Map<String, Integer> failedMediaSourceLoads;
    private int fullscreenResizeMode;
    private Uri closedCaptionUri;
    private String policyCookie;
    private String signatureCookie;
    private String keyPairIdCookie;
    private boolean playerJustInitialized;
    private boolean mAudioFocusGranted = false;
    private boolean playOnReattach;

    private boolean isDRMEnabled;
    private String licenseUrlDRM;

    private String filmId;

    private PageView pageView;

    private RecyclerView qualitySelectorRecyclerView;
    private RecyclerView closedCaptionSelectorRecyclerView;
    private ClosedCaptionSelectorAdapter closedCaptionSelectorAdapter;

    private StreamingQualitySelectorAdapter listViewAdapter;
    private HLSStreamingQualitySelectorAdapter hlsListViewAdapter;

    private boolean fullScreenMode;
    private AdaptiveTrackSelection.Factory videoTrackSelectionFactory;
    private int mVideoRendererIndex;
    private int mTextRendererIndex;
    private boolean streamingQualitySelectorCreated;
    private boolean useHls;
    private boolean closedCaptionSelectorCreated;
    private int selectedSubtitleIndex;
    private boolean shouldShowSubtitle;
    private boolean selectedSubtitleLanguageAvailable;

    List<HLSStreamingQuality> availableStreamingQualitiesHLS;
    List<String> availableStreamingQualities;
    List<ClosedCaptions> availableClosedCaptions;

    public VideoPlayerView(Context context) {
        super(context);
        this.appCMSPresenter = ((AppCMSApplication) context.getApplicationContext())
                .getAppCMSPresenterComponent().appCMSPresenter();
        initializeView(context);
    }

    public VideoPlayerView(Context context, AppCMSPresenter appCMSPresenter) {
        super(context);
        this.appCMSPresenter = appCMSPresenter;
        initializeView(context);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.appCMSPresenter = ((AppCMSApplication) context.getApplicationContext())
                .getAppCMSPresenterComponent().appCMSPresenter();
        initializeView(context);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.appCMSPresenter = ((AppCMSApplication) context.getApplicationContext())
                .getAppCMSPresenterComponent().appCMSPresenter();
        initializeView(context);
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void setOnPlayerStateChanged(Action1<PlayerState> onPlayerStateChanged) {
        this.onPlayerStateChanged = onPlayerStateChanged;
    }

    public void setOnPlayerControlsStateChanged(Action1<Integer> onPlayerControlsStateChanged) {
        this.onPlayerControlsStateChanged = onPlayerControlsStateChanged;
    }

    public void setOnClosedCaptionButtonClicked(Action1<Boolean> onClosedCaptionButtonClicked) {
        this.onClosedCaptionButtonClicked = onClosedCaptionButtonClicked;
    }

    public void setUriOnConnection() {
        this.uri = uri;
        try {
            player.prepare(buildMediaSource());
            player.seekTo(mCurrentPlayerPosition);
        } catch (IllegalStateException e) {
            //Log.e(TAG, "Unsupported video format for URI: " + uri.toString());
        }
    }

    String adsUrl;

    public void setAdsUrl(String adsUrl) {
        this.adsUrl = adsUrl;
    }

    public void setUri(Uri videoUri, Uri closedCaptionUri) {
        this.uri = videoUri;
        String strUri = videoUri.toString().split("\\?")[0];
        this.uri = Uri.parse(strUri);
        this.closedCaptionUri = closedCaptionUri;
        // adsLoader = new ImaAdsLoader(getContext(), Uri.parse(adsUrl));
        try {
            imaAdsLoader = null;
            player.prepare(buildAdsMediaSource(buildMediaSource(videoUri, closedCaptionUri), this.adsUrl));
        } catch (IllegalStateException e) {
            //Log.e(TAG, "Unsupported video format for URI: " + videoUri.toString());
        }
        if (appCMSPresenter != null/* && appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.ANDROID*/) {
            if (closedCaptionUri == null) {
                toggleCCSelectorVisibility(false);
                settingsButtonVisibility(false);
            } else {
                if (ccToggleButton != null) {
//                    ccToggleButton.setChecked(isClosedCaptionEnabled);
//                    ccToggleButton.setVisibility(VISIBLE);
                }
            }

        } else {
            toggleCCSelectorVisibility(false);
            settingsButtonVisibility(false);
        }

        try {
            if (getContext().getResources().getBoolean(R.bool.enable_stream_quality_selection) &&
                    currentStreamingQualitySelector != null &&
                    streamingQualitySelector != null) {
                List<String> availableStreamingQualities = streamingQualitySelector.getAvailableStreamingQualities();
                if (0 < availableStreamingQualities.size()) {
                    int streamingQualityIndex = streamingQualitySelector.getMpegResolutionIndexFromUrl(videoUri.toString());
                    if (0 <= streamingQualityIndex) {
                        currentStreamingQualitySelector.setText(availableStreamingQualities.get(streamingQualityIndex));
                        appCMSPresenter.setCurrentVideoStreamingQuality(currentStreamingQualitySelector.getText().toString());
                        setSelectedStreamingQualityIndex();
                    }
                }
                if (availableStreamingQualities.size() == 0) {
                    currentStreamingQualitySelector.setVisibility(GONE);
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * This method doesn't require Video Urls and Subtitle Urls in arguments because both of those
     * are queried, in {@link #buildMediaSource()} from the hosting activity using interfaces
     * which have methods implemented
     * eg. {@link AppCMSPlayVideoActivity#getAvailableClosedCaptions()}
     */
    public void preparePlayer() {
        try {
            player.prepare(buildMediaSource());
        } catch (IllegalStateException e) {
            //Log.e(TAG, "Unsupported video format for URI: " + videoUri.toString());
        }
    }

    public Uri getUri() {
        return uri;
    }

    public boolean shouldPlayWhenReady() {
        return player != null && player.getPlayWhenReady();
    }

    public void startPlayer(boolean playWhenReady) {
        if (player != null) {
            player.setPlayWhenReady(playWhenReady);
            if (appCMSPresenter != null) {
                appCMSPresenter.sendKeepScreenOnAction();
            }
        }
    }

    public void resumePlayer() {
        if (player != null) {
            if (playerJustInitialized) {
                player.setPlayWhenReady(true);
                playerJustInitialized = false;
            } else {
                player.setPlayWhenReady(player.getPlayWhenReady());
            }

            if (appCMSPresenter != null) {
                if (player.getPlayWhenReady()) {
                    appCMSPresenter.sendKeepScreenOnAction();
                } else {
                    appCMSPresenter.sendClearKeepScreenOnAction();
                }
            }
            appCMSPresenter.cancelInternalEvents();
        }
    }

    public void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            if (appCMSPresenter != null) {
                appCMSPresenter.sendClearKeepScreenOnAction();
            }
        }

    }

    public void stopPlayer() {
        if (player != null) {
            player.stop();
            if (appCMSPresenter != null) {
                appCMSPresenter.sendClearKeepScreenOnAction();
                appCMSPresenter.restartInternalEvents();
            }
        }
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
            if (appCMSPresenter != null) {
                appCMSPresenter.sendClearKeepScreenOnAction();
            }
        }
    }

    public long getDuration() {
        if (player != null) {
            return player.getDuration();
        }

        return -1L;
    }

    public long getCurrentPosition() {
        if (player != null) {
            return player.getCurrentPosition();
        }

        return -1L;
    }

    public void setCurrentPosition(long currentPosition) {
        if (player != null) {
            player.seekTo(currentPosition);
        }
    }

    public long getBitrate() {
        return bitrate;
    }

    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public void setClosedCaptionEnabled(boolean closedCaptionEnabled) {
        isClosedCaptionEnabled = closedCaptionEnabled;
    }

    public AppCMSSimpleExoPlayerView getPlayerView() {
        return playerView;
    }

    public void setFillBasedOnOrientation() {
        if (BaseView.isLandscape(getContext())) {
            playerView.setResizeMode(fullscreenResizeMode);
        } else {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
    }

    public void enableController() {
        playerView.setUseController(true);
    }

    public void disableController() {
        playerView.setUseController(false);
    }

    public void updateSignatureCookies(String policyCookie,
                                       String signatureCookie,
                                       String keyPairIdCookie) {
        if (mediaDataSourceFactory != null &&
                mediaDataSourceFactory instanceof UpdatedUriDataSourceFactory) {
            ((UpdatedUriDataSourceFactory) mediaDataSourceFactory).updateSignatureCookies(policyCookie,
                    signatureCookie,
                    keyPairIdCookie);
        }
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.video_player_view, this);
        playerView = (AppCMSSimpleExoPlayerView) findViewById(R.id.videoPlayerView);
        playerJustInitialized = true;
        fullScreenMode = false;
        init(context);
    }

    public void init(Context context) {
        initializePlayer(context);
        playerState = new PlayerState();
        failedMediaSourceLoads = new HashMap<>();
    }

    public StreamingQualitySelector getStreamingQualitySelector() {
        return streamingQualitySelector;
    }

    public void setStreamingQualitySelector(StreamingQualitySelector streamingQualitySelector) {
        this.streamingQualitySelector = streamingQualitySelector;
    }


    public ClosedCaptionSelector getClosedCaptionSelector() {
        return closedCaptionSelector;
    }

    public void setClosedCaptionsSelector(ClosedCaptionSelector closedCaptionSelector) {
        this.closedCaptionSelector = closedCaptionSelector;
    }

    public void setVideoPlayerSettingsEvent(VideoPlayerSettingsEvent videoPlayerSettingsEvent) {
        this.videoPlayerSettingsEvent = videoPlayerSettingsEvent;
    }

    public boolean shouldPlayOnReattach() {
        return playOnReattach;
    }

    private void initializePlayer(Context context) {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
        userAgent = Util.getUserAgent(getContext(),
                getContext().getString(R.string.app_cms_user_agent));

        useHls = !Utils.isHLS() ? getResources().getBoolean(R.bool.use_hls) : Utils.isHLS();

        ccToggleButton = createCC_ToggleButton();
        ((RelativeLayout) playerView.findViewById(R.id.exo_controller_container)).addView(ccToggleButton);
        /*ccToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
         *//*if (onClosedCaptionButtonClicked != null) {
                onClosedCaptionButtonClicked.call(isChecked);
            }*//*
            isClosedCaptionEnabled = isChecked;
        });*/

        ///*
        if (appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.ANDROID) {
            mSettingButton = createSettingButton();
            ((RelativeLayout) playerView.findViewById(R.id.exo_controller_container)).addView(mSettingButton);
            mSettingButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (availableClosedCaptions == null
                            && hlsListViewAdapter == null
                            && listViewAdapter == null ){
                        appCMSPresenter.showToast(getContext().getString(R.string.no_settings_available), Toast.LENGTH_SHORT);
                    }else if (videoPlayerSettingsEvent != null){
                        videoPlayerSettingsEvent.launchSetting(closedCaptionSelectorAdapter,listViewAdapter);
                        /*videoPlayerSettingsEvent.launchSetting(availableClosedCaptions, closedCaptionSelectorAdapter == null ? 0 : closedCaptionSelectorAdapter.getSelectedIndex(),
                                availableStreamingQualitiesHLS, hlsListViewAdapter == null ? 0 : hlsListViewAdapter.getSelectedIndex(),
                                availableStreamingQualities, listViewAdapter == null ? 0 : listViewAdapter.getSelectedIndex());
*/
                    }else{
                        appCMSPresenter.showToast(getContext().getString(R.string.something_wrong), Toast.LENGTH_SHORT);
                    }

                }
            });
        }
        //*/
        currentStreamingQualitySelector = playerView.findViewById(R.id.streamingQualitySelector);

        try {
            if (appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) {
                StateListDrawable drawable = (StateListDrawable) currentStreamingQualitySelector.getBackground();
                DrawableContainer.DrawableContainerState dcs = (DrawableContainer.DrawableContainerState) drawable.getConstantState();
                Drawable[] drawableItems = dcs.getChildren();
                GradientDrawable gradientDrawableChecked = (GradientDrawable) drawableItems[0]; // item 1
                gradientDrawableChecked.setStroke(1, appCMSPresenter.getBrandPrimaryCtaColor());
            }
        } catch (Exception e) {
        }

        /*currentStreamingQualitySelector = playerView.findViewById(R.id.streamingQualitySelector);
        if (getContext().getResources().getBoolean(R.bool.enable_stream_quality_selection)
                *//*&& (null != appCMSPresenter && appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.ANDROID)*//*) {
            createStreamingQualitySelector();
        } else {
            currentStreamingQualitySelector.setVisibility(View.GONE);
        }*/

       /* videoPlayerTitle = playerView.findViewById(R.id.app_cms_video_player_title_view);

        videoPlayerTitle.setText("");*/

        mediaDataSourceFactory = buildDataSourceFactory(true);

        timeBar = playerView.findViewById(R.id.exo_progress);

        videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);


        //trackSelector.setTunnelingAudioSessionId(C.generateAudioSessionIdV21(getContext()));
        trackSelector.buildUponParameters().setTunnelingAudioSessionId(C.generateAudioSessionIdV21(getContext()));

        if (player != null) {
            player.release();
        }
        if (isDRMEnabled()) {

            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
            try {
                drmSessionManager = buildOnlineDrmSessionManager(licenseUrlDRM);

                player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext(), drmSessionManager), trackSelector);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        }

        player.addListener(this);
        player.setVideoDebugListener(this);
        playerView.setPlayer(player);
        playerView.setControllerVisibilityListener(visibility -> {
            if (onPlayerControlsStateChanged != null) {

                onPlayerControlsStateChanged.call(visibility);
            }
            if (appCMSPresenter.getPlatformType().equals(AppCMSPresenter.PlatformType.TV)){
                if (visibility == View.VISIBLE) {
                    offsetSubtitleView();
                } else {
                    resetSubtitleView();
                }
            }
        });
        player.addVideoListener(this);

        if (context != null) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.requestAudioFocus(focusChange -> Log.i(TAG, "Audio focus has changed: " + focusChange),
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
            }
        }

        setFillBasedOnOrientation();

        fullscreenResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH;
//        fullscreenResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
    }

    private void offsetSubtitleView() {
        if (playerView.getSubtitleView() != null) {
            if (appCMSPresenter.getPlatformType().equals(AppCMSPresenter.PlatformType.TV)) {
                playerView.getSubtitleView().animate().translationY(-100).setDuration(100);
            } else if (appCMSPresenter.getPlatformType().equals(AppCMSPresenter.PlatformType.ANDROID)) {
                playerView.getSubtitleView().animate().translationY(-150).setDuration(100);
            }
        }
    }

    private void resetSubtitleView() {
        if (playerView.getSubtitleView() != null) {
            playerView.getSubtitleView().animate().translationY(0).setDuration(100);
        }
    }

    public void applyTimeBarColor(int timeBarColor) {
        timeBar.applyPlayedColor(timeBarColor);
        timeBar.applyScrubberColor(timeBarColor);
        timeBar.applyUnplayedColor(timeBarColor);
        timeBar.applyBufferedColor(timeBarColor);
        timeBar.applyAdMarkerColor(timeBarColor);
        timeBar.applyPlayedAdMarkerColor(timeBarColor);
    }

    public void setVideoTitle(String title, int textColor) {
        if (videoPlayerTitle != null) {
            videoPlayerTitle.setText(title);
            videoPlayerTitle.setTextColor(textColor);
        }
    }


    private void setSelectedStreamingQualityIndex() {
        if (streamingQualitySelector != null && listViewAdapter != null) {
            int currentIndex = -1;
            int updatedIndex = -1;
            try {
                currentIndex = listViewAdapter.getSelectedIndex();
                updatedIndex = streamingQualitySelector.getMpegResolutionIndexFromUrl(uri.toString());
                if (updatedIndex != -1) {
                    listViewAdapter.setSelectedIndex(updatedIndex);
                }
            } catch (Exception e) {
                listViewAdapter.setSelectedIndex(0);
            }
            if (updatedIndex != -1 && currentIndex != -1 && updatedIndex != currentIndex) {
                listViewAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * This method gets the available CCs, parse them and put them in list and show it to user when
     * CC button on the player is tapped.
     */
    private void createClosedCaptioningSelector() {

        /*Simply return if there are no tracks to be selected from*/
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) {
            return;
        }

        /*get the text (subtitle) renderer index*/
        for (int i = 0; i < mappedTrackInfo.length; i++) {
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
            if (trackGroups.length != 0) {
                if (player.getRendererType(i) == C.TRACK_TYPE_TEXT) {
                    mTextRendererIndex = i;
                    break;
                }
            }
        }

        int selectedTrack = getSelectedCCTrack();

        /*a mock entry for "Off" option*/
        ClosedCaptions captions = new ClosedCaptions();
        captions.setLanguage("Off");

        /*fetch all the available SRTs*/
        availableClosedCaptions = closedCaptionSelector.getAvailableClosedCaptions();

        /*add the mock entry at the 0th index*/
        availableClosedCaptions.add(0, captions);

        /*create adapter*/
        closedCaptionSelectorAdapter = new ClosedCaptionSelectorAdapter(getContext(),
                appCMSPresenter,
                availableClosedCaptions);
        closedCaptionSelectorAdapter.setSelectedIndex(selectedTrack);
        closedCaptionSelectorRecyclerView = new RecyclerView(getContext());
        closedCaptionSelectorRecyclerView.setAdapter(closedCaptionSelectorAdapter);
        closedCaptionSelectorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,
                false));
        AlertDialog.Builder builder;
        if ((appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) && Utils.isFireTVDevice(getContext())) {
            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        } else {
            builder = new AlertDialog.Builder(getContext());
        }

        builder.setView(closedCaptionSelectorRecyclerView);
        final Dialog closedCaptionSelectorDialog = builder.create();

        if (closedCaptionSelectorDialog.getWindow() != null) {
            if ((appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) && Utils.isFireTVDevice(getContext())) {
                closedCaptionSelectorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#03000000")));
                closedCaptionSelectorDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            } else {
                closedCaptionSelectorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(appCMSPresenter.getGeneralBackgroundColor()));
            }
        }

        /*Click handler of the dialog list items*/
        closedCaptionSelectorAdapter.setItemClickListener(item -> {
            int position = closedCaptionSelectorAdapter.getDownloadQualityPosition();
            setClosedCaption(position);
            closedCaptionSelectorDialog.dismiss();
        });
        /*Click handler of the CC button on the player, which just opens the dialog*/
        ccToggleButton.setOnClickListener(v -> {

            //if (appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) {
            closedCaptionSelectorDialog.show();
            closedCaptionSelectorAdapter.notifyDataSetChanged();
            closedCaptionSelectorRecyclerView.scrollToPosition(selectedTrack);
            // }
        });
        closedCaptionSelectorCreated = true;
    }

    /**
     * Returns the selected CC group index
     * @return selected Closed Caption track
     */
    private int getSelectedCCTrack() {
//        getSelectedVideoTrack();
        int selectedTrack = 0;
        TrackGroupArray trackGroups = trackSelector.getCurrentMappedTrackInfo().getTrackGroups(mTextRendererIndex);
        for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
            TrackGroup trackGroup = trackGroups.get(groupIndex);
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                DefaultTrackSelector.SelectionOverride selectionOverride = trackSelector.getSelectionOverride(mTextRendererIndex, trackSelector.getCurrentMappedTrackInfo().getTrackGroups(mTextRendererIndex));
                if (selectionOverride != null && selectionOverride.groupIndex == groupIndex && selectionOverride.containsTrack(trackIndex)) {
//                    Toast.makeText(getContext(), "Group Index: " +groupIndex +", Track Index: " + trackIndex, Toast.LENGTH_SHORT).show();

                    /* +1 to offset the mock "off" entry into the list*/
                    selectedTrack = groupIndex + 1;
                    break;
                }
            }
        }
        return selectedTrack;
    }

    /**
     * overrides the CC track selection with the group id passed as a paramater
     * @param groupIndex index of the group you wanna select
     */
    private void setSelectedCCTrack(int groupIndex) {
        TrackGroupArray trackGroups1 = trackSelector.getCurrentMappedTrackInfo().getTrackGroups(mTextRendererIndex);
        DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(
                groupIndex, 0);
        trackSelector.setSelectionOverride(mTextRendererIndex, trackGroups1, override);
    }

    private void createStreamingQualitySelector() {

        if (!appCMSPresenter.isVideoDownloaded(streamingQualitySelector.getFilmId())) {
            if (streamingQualitySelector != null && appCMSPresenter != null) {
                availableStreamingQualities = streamingQualitySelector.getAvailableStreamingQualities();
                if (availableStreamingQualities != null && 1 < availableStreamingQualities.size()) {
                    qualitySelectorRecyclerView = new RecyclerView(getContext());
                    listViewAdapter = new StreamingQualitySelectorAdapter(getContext(),
                            appCMSPresenter,
                            availableStreamingQualities);

                    qualitySelectorRecyclerView.setAdapter(listViewAdapter);
                    qualitySelectorRecyclerView.setBackgroundColor(Color.TRANSPARENT/*appCMSPresenter.getGeneralBackgroundColor()*/);
                    qualitySelectorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                            LinearLayoutManager.VERTICAL,
                            false));

                    setSelectedStreamingQualityIndex();
                    AlertDialog.Builder builder;
                    if ((appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) && Utils.isFireTVDevice(getContext())) {
                        builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
                    } else {
                        builder = new AlertDialog.Builder(getContext());
                    }

                    if (qualitySelectorRecyclerView.getParent() != null && qualitySelectorRecyclerView.getParent() instanceof ViewGroup) {
                        ((ViewGroup) qualitySelectorRecyclerView.getParent()).removeView(qualitySelectorRecyclerView);
                    }
                    builder.setView(qualitySelectorRecyclerView);
                    final Dialog streamingQualitySelectorDialog = builder.create();
                    if (streamingQualitySelectorDialog.getWindow() != null) {
                        if ((appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) && Utils.isFireTVDevice(getContext())) {
                            streamingQualitySelectorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#03000000")));
                            streamingQualitySelectorDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        } else {
                            streamingQualitySelectorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(appCMSPresenter.getGeneralBackgroundColor()));
                        }
                    }
                    currentStreamingQualitySelector.setOnClickListener(v -> {
                        streamingQualitySelectorDialog.show();
                        listViewAdapter.notifyDataSetChanged();
                    });

                    listViewAdapter.setItemClickListener(v -> {
                        try {
                            setStreamingQuality(listViewAdapter.getDownloadQualityPosition(), v);
                            currentStreamingQualitySelector.setText(availableStreamingQualities.get(listViewAdapter.getDownloadQualityPosition()));
                            listViewAdapter.setSelectedIndex(listViewAdapter.getDownloadQualityPosition());
                            streamingQualitySelectorDialog.dismiss();
                            appCMSPresenter.sendPlayerBitrateEvent(currentStreamingQualitySelector.getText().toString());
                            appCMSPresenter.setCurrentVideoStreamingQuality(currentStreamingQualitySelector.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    settingsButtonVisibility(true);
                } else {
                    currentStreamingQualitySelector.setVisibility(GONE);
                }
            } else {
                currentStreamingQualitySelector.setVisibility(GONE);
            }
        } else {
            //video coming from downloaded
            if (appCMSPresenter.isUserLoggedIn()) {
                currentStreamingQualitySelector.setVisibility(GONE);
            }
        }
        streamingQualitySelectorCreated = true;
    }

    /**
     * Used to extract the different tracks available in an HLS stream.
     * <p>
     * {@link DefaultTrackSelector#getCurrentMappedTrackInfo} returns {@link MappingTrackSelector.MappedTrackInfo} object
     * </br>
     * <p>
     * {@link MappingTrackSelector.MappedTrackInfo#getTrackGroups(int)} is called with 0 as argument for video tracks, which returns {@link TrackGroupArray}.
     * </br></br></p><p>
     * {@link TrackGroupArray} is then iterated on index which return {@link TrackGroup} by calling the {@link TrackGroupArray#get(int)}
     * </br></br></p>
     * {@link TrackGroup#getFormat(int)} is called and {@link Format} is used to get the track index and the {@link Format#height} is used to calculate the resolution of the track.
     */
    private void createStreamingQualitySelectorForHLS() {
        if (player == null) {
            return;
        }
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) {
            return;
        }
        for (int i = 0; i < mappedTrackInfo.length; i++) {
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
            if (trackGroups.length != 0) {
                if (player.getRendererType(i) == C.TRACK_TYPE_VIDEO) {
                    mVideoRendererIndex = i;
                    break;
                }
            }
        }
        if (streamingQualitySelector != null && appCMSPresenter != null) {
            showStreamingQualitySelector();
            trackGroups = trackSelector.getCurrentMappedTrackInfo().getTrackGroups(mVideoRendererIndex);
            availableStreamingQualitiesHLS = new ArrayList<>();
            availableStreamingQualitiesHLS.add(new HLSStreamingQuality(0, "Auto"));
            for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
                TrackGroup group = trackGroups.get(groupIndex);
                for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                    Format format = group.getFormat(trackIndex);
                    if (format.height != Format.NO_VALUE) {
                        availableStreamingQualitiesHLS.add(new HLSStreamingQuality(trackIndex,
                                format.height == Format.NO_VALUE ? "" : format.height + "p"));
                    } else {
                        availableStreamingQualitiesHLS.add(new HLSStreamingQuality(trackIndex,
                                buildBitrateString(format)));
                    }
                }
            }

            /*the following is done to only have distinct values in the HLS track list. We are getting
             * multiple tracks for same resolution with different bitrate.*/
            Set<HLSStreamingQuality> set = new TreeSet<>((o1, o2) -> {
                if (o1.getValue().equalsIgnoreCase(o2.getValue())) {
                    return 0;
                }
                return 1;
            });

            set.addAll(availableStreamingQualitiesHLS);
            availableStreamingQualitiesHLS.clear();
            availableStreamingQualitiesHLS.addAll(set);

            if (availableStreamingQualitiesHLS.size() > 1) {
                qualitySelectorRecyclerView = new RecyclerView(getContext());
                hlsListViewAdapter = new HLSStreamingQualitySelectorAdapter(getContext(),
                        appCMSPresenter,
                        availableStreamingQualitiesHLS);

                qualitySelectorRecyclerView.setAdapter(hlsListViewAdapter);
                if (appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) {
                    qualitySelectorRecyclerView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    qualitySelectorRecyclerView.setBackgroundColor(appCMSPresenter.getGeneralBackgroundColor());
                }
                qualitySelectorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL,
                        false));

                setSelectedStreamingQualityIndex();
                AlertDialog.Builder builder = null;

                if ((appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) && Utils.isFireTVDevice(getContext())) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }

                if (qualitySelectorRecyclerView.getParent() != null && qualitySelectorRecyclerView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) qualitySelectorRecyclerView.getParent()).removeView(qualitySelectorRecyclerView);
                }
                builder.setView(qualitySelectorRecyclerView);
                final Dialog dialog = builder.create();
                if (dialog.getWindow() != null) {
                    if ((appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) && Utils.isFireTVDevice(getContext())) {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#03000000")));
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    } else {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(appCMSPresenter.getGeneralBackgroundColor()));
                    }
                }
                currentStreamingQualitySelector.setOnClickListener(v -> {
                    /*Click Handler*/
                    dialog.show();
                    hlsListViewAdapter.notifyDataSetChanged();
                });
                hlsListViewAdapter.setItemClickListener(v -> {

                    try {
                        if (v instanceof HLSStreamingQuality) {
                            int selectedIndex = hlsListViewAdapter.getDownloadQualityPosition();
                            /*if (selectedIndex == 0) {
                                trackSelector.clearSelectionOverrides(mVideoRendererIndex);
                            } else {
                                int[] tracks = new int[1];
                                tracks[0] = ((HLSStreamingQuality) v).getIndex();
                                DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(
                                        0, tracks);
                                trackSelector.setSelectionOverride(mVideoRendererIndex, trackGroups, override);
                            }*/
                            setStreamingQuality(selectedIndex, v);
                            currentStreamingQualitySelector.setText(availableStreamingQualitiesHLS.get(selectedIndex).getValue());
                            appCMSPresenter.setCurrentVideoStreamingQuality(currentStreamingQualitySelector.getText().toString());

                            hlsListViewAdapter.setSelectedIndex(selectedIndex);
                        }
                        dialog.hide();
                    } catch (Exception e) {

                    }
                });
            } else {
                currentStreamingQualitySelector.setVisibility(GONE);
            }
        } else {
            currentStreamingQualitySelector.setVisibility(GONE);
        }
        streamingQualitySelectorCreated = true;
    }

    private String buildBitrateString(Format format) {
        return format.bitrate == Format.NO_VALUE ? ""
                : String.format(Locale.US, "%.2fMbit", format.bitrate / 1000000f);
    }

    private DrmSessionManager<FrameworkMediaCrypto> buildOnlineDrmSessionManager(String licenseUrl) throws UnsupportedDrmException {
        HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(getContext(), "ExoOnline"));
        MediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl, httpDataSourceFactory);
        return DefaultDrmSessionManager.newWidevineInstance(drmCallback, null, new Handler(), this);
    }

    public Uri getAdTagUri(final String adURL) {
        return Uri.parse(adURL);
    }

    ImaAdsLoader imaAdsLoader;

    void initAds() {

        imaAdsLoader = new ImaAdsLoader.Builder(getContext()).setAdEventListener(new PlayerAdEvent()).buildForAdTag(getAdTagUri(this.adsUrl));

    }

    private MediaSource buildAdsMediaSource(MediaSource contentMediaSource, String adsUrl) {


        if (adsUrl != null && !TextUtils.isEmpty(adsUrl) &&
                !getPlayerView().getController().isPlayingLive() &&
                !appCMSPresenter.isUserSubscribed()) {
            if (imaAdsLoader == null) {
                initAds();
            } else {
                imaAdsLoader.loadAd(adsUrl);
            }
            AdsMediaSource.MediaSourceFactory adMediaFactory = new AdsMediaSource.MediaSourceFactory() {

                @Override
                public MediaSource createMediaSource(Uri uri) {
                    return buildMediaSource(uri, "");
                }

                @Override
                public int[] getSupportedTypes() {
                    return new int[0];
                }
            };


            MediaSource mediaSourceWithAds = new AdsMediaSource(
                    contentMediaSource,
                    adMediaFactory,
                    imaAdsLoader,
                    playerView.getOverlayFrameLayout());
            return mediaSourceWithAds;

        } else {
            return contentMediaSource;
        }
    }

    private MediaSource buildMediaSource(Uri uri, Uri ccFileUrl) {
        if (mediaDataSourceFactory instanceof UpdatedUriDataSourceFactory) {
            if (null != policyCookie && null != signatureCookie && null != keyPairIdCookie) {
                ((UpdatedUriDataSourceFactory) mediaDataSourceFactory).signatureCookies.policyCookie = policyCookie;
                ((UpdatedUriDataSourceFactory) mediaDataSourceFactory).signatureCookies.signatureCookie = signatureCookie;
                ((UpdatedUriDataSourceFactory) mediaDataSourceFactory).signatureCookies.keyPairIdCookie = keyPairIdCookie;
            }
        }

        Format textFormat = Format.createTextSampleFormat(null,
                MimeTypes.APPLICATION_SUBRIP,
                C.SELECTION_FLAG_DEFAULT,
                "en");
        MediaSource videoSource = buildMediaSource(uri, "");
        if (ccFileUrl == null) {
            return videoSource;
        }
        MediaSource subtitleSource = new SingleSampleMediaSource(
                ccFileUrl,
                mediaDataSourceFactory,
                textFormat,
                C.TIME_UNSET);

        // Plays the video with the side-loaded subtitle.
        return new MergingMediaSource(videoSource, subtitleSource);
    }


    /**
     * Queries video urls and subtitle urls from the hosting Activities which have implemented the
     * {@link VideoPlayerView.StreamingQualitySelector} or {@link VideoPlayerView.ClosedCaptionSelector}
     * <p>
     * This method iterated over multiple Mp4 & srt urls to create a {@link MergingMediaSource}
     * which have every possible rendition of Mp4 and every available SRT merged. In case of HLS, a
     * single .m3u8 file is merged with all available SRTs
     *
     * @return the merged media source object
     */
    private MediaSource buildMediaSource() {

        if (mediaDataSourceFactory instanceof UpdatedUriDataSourceFactory) {
            if (null != policyCookie && null != signatureCookie && null != keyPairIdCookie) {
                ((UpdatedUriDataSourceFactory) mediaDataSourceFactory).signatureCookies.policyCookie = policyCookie;
                ((UpdatedUriDataSourceFactory) mediaDataSourceFactory).signatureCookies.signatureCookie = signatureCookie;
                ((UpdatedUriDataSourceFactory) mediaDataSourceFactory).signatureCookies.keyPairIdCookie = keyPairIdCookie;
            }
        }

        // List of MediaSource which is later converted to an array and used to create MergingMediaSource
        List<MediaSource> mediaSourceList = new ArrayList<>();

        /*Iterated over the available Mp4s, create an ExtractorMediaSource by calling the overloaded
         * buildMediaSource method */
        if (!useHls) {
            List<String> availableStreamingQualities = streamingQualitySelector.getAvailableStreamingQualities();
            for (int i = 0; i < availableStreamingQualities.size(); i++) {

                /*this returns an item something in the format of 360p or 0.25Mbit*/
                String streamingQuality = availableStreamingQualities.get(i);
                /*use this method to get the Mp4 url from the streaming quslitu*/
                String streamingQualityUrl = streamingQualitySelector.getStreamingQualityUrl(streamingQuality);

                if (streamingQualityUrl != null && !TextUtils.isEmpty(streamingQualityUrl)) {
                    // add the media source to the list
                    mediaSourceList.add(buildMediaSource(Uri.parse(streamingQualityUrl), ""));
                }
            }
        } else { /* this is for HLS, getVideoUrl() returns the HLS url from the hosting activity*/
            mediaSourceList.add(buildMediaSource(Uri.parse(streamingQualitySelector.getVideoUrl()), ""));
        }

        /*Check if user has enabled CC in the app setting, it is off by default*/
        if (appCMSPresenter.getClosedCaptionPreference()) {
            /*getAvailableClosedCaptions() returns all the SRTs which we got in the ContentDatum*/
            List<ClosedCaptions> closedCaptionsList = closedCaptionSelector.getAvailableClosedCaptions();

            /*check if user has a preferred subtitle language, which he/she might have chosen in the
            past, method returns null if there is no preference*/
            String preferredSubtitleLanguage = appCMSPresenter.getPreferredSubtitleLanguage();

            /* Iterate over the CC list and create a SingleSampleMediaSource for each Subtitles and add
             * each one of them to the list*/
            if (closedCaptionsList != null && closedCaptionsList.size() > 0) {
                for (int i = 0; i < closedCaptionsList.size(); i++) {
                    ClosedCaptions closedCaptions = closedCaptionsList.get(i);

                    if ("SRT".equalsIgnoreCase(closedCaptions.getFormat())) {
                        Format textFormat = Format.createTextSampleFormat(null,
                                MimeTypes.APPLICATION_SUBRIP,
                                C.SELECTION_FLAG_DEFAULT,
                                closedCaptions.getLanguage());

                        String ccFileUrl = closedCaptions.getUrl();
                        mediaSourceList.add(new SingleSampleMediaSource(
                                Uri.parse(ccFileUrl),
                                mediaDataSourceFactory,
                                textFormat,
                                C.TIME_UNSET));

                        /* CC button visibility & state is manipulated here*/
                        if (preferredSubtitleLanguage != null) {
                            if (preferredSubtitleLanguage.equalsIgnoreCase(closedCaptions.getLanguage())) {
                                selectedSubtitleIndex = i;
                                selectedSubtitleLanguageAvailable = true;

                                /*this is used in the onPlayerStateChanged*/
                                shouldShowSubtitle = true;
                            }
                        } else {
                            selectedSubtitleLanguageAvailable = false;
                            setCCToggleButtonSelection(false);
                            VideoPlayerView.this.getPlayerView().getSubtitleView().setVisibility(INVISIBLE);
                        }
                    }
                }

                if (selectedSubtitleLanguageAvailable) {
                    setCCToggleButtonSelection(true);
                    VideoPlayerView.this.getPlayerView().getSubtitleView().setVisibility(VISIBLE);
                } else {
                    setCCToggleButtonSelection(false);
                    VideoPlayerView.this.getPlayerView().getSubtitleView().setVisibility(INVISIBLE);
                }
            } else {
                /*Disable CC if the list is empty meaning no cc available for the particular movie*/
                settingsButtonVisibility(false);
                toggleCCSelectorVisibility(false);
                setCCToggleButtonSelection(false);
                VideoPlayerView.this.getPlayerView().getSubtitleView().setVisibility(INVISIBLE);
            }
        } else {
            /*Disable CC if the user has turned CC off from settings*/
            settingsButtonVisibility(false);
            toggleCCSelectorVisibility(false);
            setCCToggleButtonSelection(false);
            VideoPlayerView.this.getPlayerView().getSubtitleView().setVisibility(INVISIBLE);
        }

        // Convert list into array and pass onto the MergingMediaSource constructor
        MediaSource mediaSources[] = new MediaSource[mediaSourceList.size()];
        mediaSourceList.toArray(mediaSources);

        if (adsUrl != null && !TextUtils.isEmpty(adsUrl)) {
            return buildAdsMediaSource(new MergingMediaSource(mediaSources), adsUrl);
        } else {
            // Plays the video with the side-loaded subtitle.
            return new MergingMediaSource(mediaSources);
        }
    }


    private MediaSource buildMediaSource(
            Uri uri,
            String overrideExtension) {
        @C.ContentType int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new UpdatedUriDataSourceFactory(getContext(),
                bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter),
                policyCookie,
                signatureCookie,
                keyPairIdCookie);
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object o, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {

    }

    @Override
    public void onLoadingChanged(boolean b) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playerState != null) {
            playerState.playWhenReady = playWhenReady;
            playerState.playbackState = playbackState;

            if (onPlayerStateChanged != null) {
                try {
                    Observable.just(playerState).subscribe(onPlayerStateChanged);
                } catch (Exception e) {
                    //Log.e(TAG, "Failed to update player state change status: " + e.getMessage());
                }
            }

            if (playbackState == Player.STATE_READY /*checking if the playback state is ready*/
                    && !getPlayerView().getController().isPlayingLive() /* if video is not Live*/
                    && useHls /*createStreamingQualitySelectorForHLS is only called for HLS stream*/
                    && !streamingQualitySelectorCreated /*making sure the selector isn't already created*/
                    && isLiveStreaming()) {
                createStreamingQualitySelectorForHLS();
                // Default "Auto" is selected
                currentStreamingQualitySelector.setText(getContext().getString(R.string.auto));
                showStreamingQualitySelector();
            } else if (getContext().getResources().getBoolean(R.bool.enable_stream_quality_selection)
                    && !useHls
                    && !streamingQualitySelectorCreated
                    && isLiveStreaming()) {

                createStreamingQualitySelector();
                String defaultVideoResolution = getContext().getString(R.string.default_video_resolution);
                int res = Integer.parseInt(defaultVideoResolution.replace("p", ""));

                /*For MP4s, by default, the highest resolution is rendered, to honor the setting we
                 * are telling the player that the max height can only be "res"*/
                trackSelector.setParameters(trackSelector.getParameters().buildUpon().setMaxVideoSize(Integer.MAX_VALUE, res).build());
            }
            if (closedCaptionSelector != null
                    && closedCaptionSelector.getAvailableClosedCaptions() != null
                    && !closedCaptionSelector.getAvailableClosedCaptions().isEmpty()) {
                if (playbackState == Player.STATE_READY
                        && !closedCaptionSelectorCreated) {

                    // create the dialog which contains the CC switcher list
                    createClosedCaptioningSelector();
                    if (shouldShowSubtitle) {
                        setSelectedCCTrack(selectedSubtitleIndex);
                        /* +1 to offset the "off" selection added to the dialog list*/
                        closedCaptionSelectorAdapter.setSelectedIndex(selectedSubtitleIndex + 1);
                    }
                    toggleCCSelectorVisibility(true);
                    settingsButtonVisibility(true);
                }
            }
            if (playbackState == Player.STATE_READY
                    && streamingQualitySelectorCreated) {

                /*Show streaming quality selector only after the player is ready*/
                showStreamingQualitySelector();
                settingsButtonVisibility(true);

                String defaultVideoResolution = getContext().getString(R.string.default_video_resolution);
                int res = Integer.parseInt(defaultVideoResolution.replace("p", ""));

                /*sometime the same call which is done in the other block doesn't function, so calling
                 * it when player is ready warrants that this will work, but if the other call didn't
                 * work, we see the highest resolution selected for a fraction of second before this
                 * call changes it to the desired setting*/
                trackSelector.setParameters(trackSelector.getParameters().buildUpon().setMaxVideoSize(Integer.MAX_VALUE, res).build());
            }

        }
    }


    private void showStreamingQualitySelector() {
        if (null != currentStreamingQualitySelector
                && null != appCMSPresenter && uri != null && !appCMSPresenter.isVideoDownloaded(streamingQualitySelector.getFilmId())
                && appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) {
            currentStreamingQualitySelector.setVisibility(View.VISIBLE);
        } else {
            if (appCMSPresenter.isVideoDownloaded(streamingQualitySelector.getFilmId()) && appCMSPresenter.isUserLoggedIn()) {
                currentStreamingQualitySelector.setVisibility(View.GONE);
            } else if(appCMSPresenter != null
                    && appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV){
                currentStreamingQualitySelector.setVisibility(View.VISIBLE);
            }
        }
    }

    private void toggleCCSelectorVisibility(boolean show) {
        if (null != ccToggleButton
                && appCMSPresenter!= null
                && appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.TV) {
            ccToggleButton.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void settingsButtonVisibility(boolean show) {
        if (null != ccToggleButton
                && appCMSPresenter!= null
                && appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.ANDROID) {
            mSettingButton.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }


    @Override
    public void onPlayerError(ExoPlaybackException e) {
        mCurrentPlayerPosition = player.getCurrentPosition();
        if (mErrorEventListener != null) {
            mErrorEventListener.onRefreshTokenCallback();
            mErrorEventListener.playerError(e);
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }


    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


    public void sendPlayerPosition(long position) {
        mCurrentPlayerPosition = position;
    }

    public void setListener(ErrorEventListener errorEventListener) {
        mErrorEventListener = errorEventListener;
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs,
                                          long initializationDurationMs) {
        //
    }

    @Override
    public void onVideoInputFormatChanged(Format format) {
        setBitrate(format.bitrate / 1000);
        setVideoHeight(format.height);
        setVideoWidth(format.width);

        Log.d(TAG, "resolution: " + format.width + "x" + format.height);

        /*Only after the successful video track change, this method is called, setting the
         * currentStreamingQualitySelector warrants that it is the actual value which is now playing*/
        String text = format.height + "p";
        currentStreamingQualitySelector.setText(text);
        try {
            this.uri = Uri.parse(streamingQualitySelector.getStreamingQualityUrl(text));
            setSelectedStreamingQualityIndex();
        } catch (Exception e) {
        }
    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        //Log.i(TAG, "Video size changed: width = " +
//                width +
//                " height = " +
//                height +
//                " rotation degrees = " +
//                unappliedRotationDegrees +
//                " width/height ratio = " +
//                pixelWidthHeightRatio);
        if (width > height) {
            fullscreenResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH;
        } else {
            fullscreenResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT;
        }

        if (BaseView.isLandscape(getContext())) {
            playerView.setResizeMode(fullscreenResizeMode);
        } else {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }

        videoWidth = width;
        videoHeight = height;
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }

    @Override
    public void onRenderedFirstFrame() {
        //Log.d(TAG, "Rendered first frame");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        playOnReattach = player.getPlayWhenReady();
//        pausePlayer();

//        appCMSPresenter.updateWatchedTime(getFilmId(), player.getCurrentPosition());
    }

    public String getFilmId() {
        return filmId;
    }

    public void setFilmId(String filmId) {
        this.filmId = filmId;
    }

    public String getPolicyCookie() {
        return policyCookie;
    }

    public void setPolicyCookie(String policyCookie) {
        this.policyCookie = policyCookie;
    }

    public String getSignatureCookie() {
        return signatureCookie;
    }

    public void setSignatureCookie(String signatureCookie) {
        this.signatureCookie = signatureCookie;
    }

    public String getKeyPairIdCookie() {
        return keyPairIdCookie;
    }

    public void setKeyPairIdCookie(String keyPairIdCookie) {
        this.keyPairIdCookie = keyPairIdCookie;
    }

    public PageView getPageView() {
        return pageView;
    }

    public void setPageView(PageView pageView) {
        this.pageView = pageView;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pausePlayer();
                break;

            case AudioManager.AUDIOFOCUS_GAIN:
                if (getPlayer() != null && getPlayer().getPlayWhenReady()) {
                    startPlayer(true);
                } else {
                    pausePlayer();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                pausePlayer();
                abandonAudioFocus();
                break;

            default:
                break;
        }
    }

    protected void abandonAudioFocus() {
        if (getContext() != null) {
            AudioManager am = (AudioManager) getContext().getApplicationContext()
                    .getSystemService(Context.AUDIO_SERVICE);
            int result = am.abandonAudioFocus(this);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocusGranted = false;
            }
        }
    }

    public boolean requestAudioFocus() {
        if (getContext() != null && !mAudioFocusGranted) {
            AudioManager am = (AudioManager) getContext().getApplicationContext()
                    .getSystemService(Context.AUDIO_SERVICE);
            int result = am.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocusGranted = true;
            }
        }
        return mAudioFocusGranted;
    }

    public AppCMSPresenter getAppCMSPresenter() {
        return appCMSPresenter;
    }

    public void setAppCMSPresenter(AppCMSPresenter appCMSPresenter) {
        this.appCMSPresenter = appCMSPresenter;
    }

    protected ImageButton createCC_ToggleButton() {
        ImageButton mToggleButton = new ImageButton(getContext());
        RelativeLayout.LayoutParams toggleLP = new RelativeLayout.LayoutParams(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_width, getContext()), BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_width, getContext()));
        toggleLP.addRule(RelativeLayout.CENTER_VERTICAL);
        toggleLP.addRule(RelativeLayout.RIGHT_OF, R.id.exo_media_controller);
        toggleLP.setMarginStart(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_left_margin, getContext()));
        toggleLP.setMarginEnd(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_left_margin, getContext()));
        mToggleButton.setLayoutParams(toggleLP);
        mToggleButton.setBackground(getResources().getDrawable(R.drawable.cc_button_selector, null));
        mToggleButton.setVisibility(GONE);
        return mToggleButton;
    }

    protected ImageButton createSettingButton() {
        ImageButton mSettingButton = new ImageButton(getContext());
        RelativeLayout.LayoutParams toggleLP = new RelativeLayout.LayoutParams(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_width, getContext()), BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_width, getContext()));
        toggleLP.addRule(RelativeLayout.CENTER_VERTICAL);
        toggleLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        toggleLP.setMarginStart(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_left_margin, getContext()));
        toggleLP.setMarginEnd(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_left_margin, getContext()));
        mSettingButton.setLayoutParams(toggleLP);
        mSettingButton.setBackground(getResources().getDrawable(R.drawable.ic_settings_24dp, null));
        mSettingButton.setVisibility(GONE);
        return mSettingButton;
    }

    public void showChromecastLiveVideoPlayer(boolean show) {
        if (show) {
            chromecastLivePlayerParent.setVisibility(VISIBLE);
            if (appCMSPresenter != null && appCMSPresenter.getCurrentMediaRouteButton() != null) {
                chromecastButtonPlaceholder.setVisibility(VISIBLE);
            } else {
                chromecastButtonPlaceholder.setVisibility(INVISIBLE);
            }
        } else {
            chromecastLivePlayerParent.setVisibility(INVISIBLE);
        }
    }


    public void disableFullScreenMode() {
        if (enterFullscreenButton != null &&
                exitFullscreenButton != null &&
                BaseView.isTablet(getContext())) {
            enterFullscreenButton.setVisibility(GONE);
            exitFullscreenButton.setVisibility(VISIBLE);
        }
    }

    public void exitFullscreenMode(boolean relaunchPage) {
        enableFullScreenMode();
        fullScreenMode = false;
        if (appCMSPresenter != null) {
            // appCMSPresenter.sendExitFullScreenAction(true);
        }
    }

    public void enableFullScreenMode() {
        if (enterFullscreenButton != null &&
                exitFullscreenButton != null &&
                BaseView.isTablet(getContext())) {
            exitFullscreenButton.setVisibility(INVISIBLE);
            enterFullscreenButton.setVisibility(VISIBLE);
        }
    }

    public void setChromecastButton(ImageButton chromecastButton) {
        if (chromecastButton.getParent() != null && chromecastButton.getParent() instanceof ViewGroup) {
            chromecastButtonPreviousParent = (ViewGroup) chromecastButton.getParent();
            chromecastButtonPreviousParent.removeView(chromecastButton);
        }
        chromecastButtonPlaceholder.addView(chromecastButton);
    }

    public void resetChromecastButton(ImageButton chromecastButton) {
        if (chromecastButton != null &&
                chromecastButton.getParent() != null &&
                chromecastButton.getParent() instanceof ViewGroup) {
            ((ViewGroup) chromecastButton.getParent()).removeView(chromecastButton);
        }
        if (chromecastButtonPreviousParent != null) {
            chromecastButtonPreviousParent.addView(chromecastButton);
        }
    }

    public boolean fullScreenModeEnabled() {
        return fullScreenMode;
    }

    @Override
    public void onDrmKeysLoaded() {

    }

    @Override
    public void onDrmSessionManagerError(Exception e) {

    }

    @Override
    public void onDrmKeysRestored() {

    }

    @Override
    public void onDrmKeysRemoved() {

    }

    @Override
    public void onAdLoadError(IOException error) {

    }

    @Override
    public void onInternalAdLoadError(RuntimeException error) {

    }

    @Override
    public void onAdClicked() {

    }

    @Override
    public void onAdTapped() {

    }

    @Override
    public void onMediaPeriodCreated(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

    }

    @Override
    public void onMediaPeriodReleased(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

    }

    @Override
    public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
        bitrate = (mediaLoadData.trackFormat.bitrate / 1000);
    }

    @Override
    public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

    }

    @Override
    public void onLoadCanceled(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {

    }

    @Override
    public void onLoadError(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {

        /**
         * We can enhance logic here depending on the error code list that we will use for closing the video page.
         */
        if ((error.getMessage().contains("404") ||
                error.getMessage().contains("400"))
                && !isLoadedNext) {
            String failedMediaSourceLoadKey = loadEventInfo.dataSpec.uri.toString();
            if (failedMediaSourceLoads.containsKey(failedMediaSourceLoadKey)) {
                int tryCount = failedMediaSourceLoads.get(failedMediaSourceLoadKey);
                if (tryCount == 3) {
                    isLoadedNext = true;
                    mErrorEventListener.onFinishCallback(error.getMessage());
                } else {
                    failedMediaSourceLoads.put(failedMediaSourceLoadKey, tryCount + 1);
                }
            } else {
                failedMediaSourceLoads.put(failedMediaSourceLoadKey, 1);
            }
        } else if (mErrorEventListener != null) {
            mErrorEventListener.onRefreshTokenCallback();
        }
    }

    @Override
    public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {

    }

    @Override
    public void onUpstreamDiscarded(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {

    }

    @Override
    public void onDownstreamFormatChanged(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {

    }


    public interface ErrorEventListener {
        void onRefreshTokenCallback();

        void onFinishCallback(String message);

        void playerError(ExoPlaybackException ex);
    }

    public interface StreamingQualitySelector {
        List<String> getAvailableStreamingQualities();


        /**
         * Returns the HLS url which will be used for playback
         */
        String getVideoUrl();

        String getStreamingQualityUrl(String streamingQuality);

        String getMpegResolutionFromUrl(String mpegUrl);

        int getMpegResolutionIndexFromUrl(String mpegUrl);

        String getFilmId();
    }

    /**
     * Contain methods used to fetch the closed captions' list and the language from the selected
     * index
     */
    public interface ClosedCaptionSelector {
        List<ClosedCaptions> getAvailableClosedCaptions();

        String getSubtitleLanguageFromIndex(int index);
    }

    public static class PlayerState {
        boolean playWhenReady;
        int playbackState;

        public boolean isPlayWhenReady() {
            return playWhenReady;
        }

        public int getPlaybackState() {
            return playbackState;
        }
    }

    public static class SignatureCookies {
        String policyCookie;
        String signatureCookie;
        String keyPairIdCookie;
    }

    private static class UpdatedUriDataSourceFactory implements Factory {
        private final Context context;
        private final TransferListener<? super DataSource> listener;
        private final DataSource.Factory baseDataSourceFactory;
        private SignatureCookies signatureCookies;

        /**
         * @param context   A context.
         * @param userAgent The User-Agent string that should be used.
         */
        public UpdatedUriDataSourceFactory(Context context, String userAgent, String policyCookie,
                                           String signatureCookie, String keyPairIdCookie) {
            this(context, userAgent, null, policyCookie, signatureCookie, keyPairIdCookie);
        }

        /**
         * @param context   A context.
         * @param userAgent The User-Agent string that should be used.
         * @param listener  An optional listener.
         */
        public UpdatedUriDataSourceFactory(Context context, String userAgent,
                                           TransferListener<? super DataSource> listener,
                                           String policyCookie, String signatureCookie, String keyPairIdCookie) {
            this(context, listener, new DefaultHttpDataSourceFactory(userAgent, listener), policyCookie,
                    signatureCookie, keyPairIdCookie);
        }

        /**
         * @param context               A context.
         * @param listener              An optional listener.
         * @param baseDataSourceFactory A {@link DataSource.Factory} to be used to create a base {@link DataSource}
         *                              for {@link DefaultDataSource}.
         * @param policyCookie          The cookie used for accessing CDN protected data.
         * @see DefaultDataSource#DefaultDataSource(Context, TransferListener, DataSource)
         */
        public UpdatedUriDataSourceFactory(Context context, TransferListener<? super DataSource> listener,
                                           DataSource.Factory baseDataSourceFactory, String policyCookie,
                                           String signatureCookie, String keyPairIdCookie) {
            this.context = context.getApplicationContext();
            this.listener = listener;
            this.baseDataSourceFactory = baseDataSourceFactory;

            signatureCookies = new SignatureCookies();

            signatureCookies.policyCookie = policyCookie;
            signatureCookies.signatureCookie = signatureCookie;
            signatureCookies.keyPairIdCookie = keyPairIdCookie;
        }

        @Override
        public UpdatedUriDataSource createDataSource() {
            return new UpdatedUriDataSource(context, listener, baseDataSourceFactory.createDataSource(),
                    signatureCookies);
        }

        public void updateSignatureCookies(String policyCookie,
                                           String signatureCookie,
                                           String keyPairIdCookie) {
            signatureCookies.policyCookie = policyCookie;
            signatureCookies.signatureCookie = signatureCookie;
            signatureCookies.keyPairIdCookie = keyPairIdCookie;
        }
    }

    private static class UpdatedUriDataSource implements DataSource {
        private static final String SCHEME_ASSET = "asset";
        private static final String SCHEME_CONTENT = "content";

        private final DataSource baseDataSource;
        private final DataSource fileDataSource;
        private final DataSource assetDataSource;
        private final DataSource contentDataSource;
        private final SignatureCookies signatureCookies;

        private DataSource dataSource;

        /**
         * Constructs a new instance, optionally configured to follow cross-protocol redirects.
         *
         * @param context                     A context.
         * @param listener                    An optional listener.
         * @param userAgent                   The User-Agent string that should be used when requesting remote data.
         * @param allowCrossProtocolRedirects Whether cross-protocol redirects (i.e. redirects from HTTP
         *                                    to HTTPS and vice versa) are enabled when fetching remote data.
         */
        public UpdatedUriDataSource(Context context, TransferListener<? super DataSource> listener,
                                    String userAgent, boolean allowCrossProtocolRedirects,
                                    SignatureCookies signatureCookies) {
            this(context, listener, userAgent, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, allowCrossProtocolRedirects,
                    signatureCookies);
        }

        /**
         * Constructs a new instance, optionally configured to follow cross-protocol redirects.
         *
         * @param context                     A context.
         * @param listener                    An optional listener.
         * @param userAgent                   The User-Agent string that should be used when requesting remote data.
         * @param connectTimeoutMillis        The connection timeout that should be used when requesting remote
         *                                    data, in milliseconds. A timeout of zero is interpreted as an infinite timeout.
         * @param readTimeoutMillis           The read timeout that should be used when requesting remote data,
         *                                    in milliseconds. A timeout of zero is interpreted as an infinite timeout.
         * @param allowCrossProtocolRedirects Whether cross-protocol redirects (i.e. redirects from HTTP
         *                                    to HTTPS and vice versa) are enabled when fetching remote data.
         */
        public UpdatedUriDataSource(Context context, TransferListener<? super DataSource> listener,
                                    String userAgent, int connectTimeoutMillis, int readTimeoutMillis,
                                    boolean allowCrossProtocolRedirects, SignatureCookies signatureCookies) {
            this(context, listener,
                    new DefaultHttpDataSource(userAgent, null, listener, connectTimeoutMillis,
                            readTimeoutMillis, allowCrossProtocolRedirects, null),
                    signatureCookies);
        }

        /**
         * Constructs a new instance that delegates to a provided {@link DataSource} for URI schemes other
         * than file, asset and content.
         *
         * @param context        A context.
         * @param listener       An optional listener.
         * @param baseDataSource A {@link DataSource} to use for URI schemes other than file, asset and
         *                       content. This {@link DataSource} should normally support at least http(s).
         */
        public UpdatedUriDataSource(Context context, TransferListener<? super DataSource> listener,
                                    DataSource baseDataSource,
                                    SignatureCookies signatureCookies) {
            this.baseDataSource = Assertions.checkNotNull(baseDataSource);
            this.fileDataSource = new FileDataSource(listener);
            this.assetDataSource = new AssetDataSource(context, listener);
            this.contentDataSource = new ContentDataSource(context, listener);
            this.signatureCookies = signatureCookies;
        }

        @Override
        public long open(DataSpec dataSpec) throws IOException {
            Assertions.checkState(dataSource == null);
            // Choose the correct source for the scheme.
            String scheme = dataSpec.uri.getScheme();
            if (Util.isLocalFileUri(dataSpec.uri)) {
                if (dataSpec.uri.getPath().startsWith("/android_asset/")) {
                    dataSource = assetDataSource;
                } else {
                    dataSource = fileDataSource;
                }
            } else if (SCHEME_ASSET.equals(scheme)) {
                dataSource = assetDataSource;
            } else if (SCHEME_CONTENT.equals(scheme)) {
                dataSource = contentDataSource;
            } else {
                dataSource = baseDataSource;
            }

            Uri updatedUri = Uri.parse(dataSpec.uri.toString().replaceAll(" ", "%20"));

            boolean useHls = dataSpec.uri.toString().contains(".m3u8") ||
                    dataSpec.uri.toString().contains(".ts") ||
                    dataSpec.uri.toString().contains("hls");
            if (useHls
                    && updatedUri.toString().contains("Policy=")
                    && updatedUri.toString().contains("Key-Pair-Id=")
                    && updatedUri.toString().contains("Signature=")
                    && updatedUri.toString().contains("?")) {
                updatedUri = Uri.parse(updatedUri.toString().substring(0, dataSpec.uri.toString().indexOf("?")));
            }

            if (useHls && dataSource instanceof DefaultHttpDataSource) {
                if (!TextUtils.isEmpty(signatureCookies.policyCookie) &&
                        !TextUtils.isEmpty(signatureCookies.signatureCookie) &&
                        !TextUtils.isEmpty(signatureCookies.keyPairIdCookie)) {
                    StringBuilder cookies = new StringBuilder();
                    cookies.append("CloudFront-Policy=");
                    cookies.append(signatureCookies.policyCookie);
                    cookies.append("; ");
                    cookies.append("CloudFront-Signature=");
                    cookies.append(signatureCookies.signatureCookie);
                    cookies.append("; ");
                    cookies.append("CloudFront-Key-Pair-Id=");
                    cookies.append(signatureCookies.keyPairIdCookie);
                    ((DefaultHttpDataSource) dataSource).setRequestProperty("Cookie", cookies.toString());
                }
            }

            final DataSpec updatedDataSpec = new DataSpec(updatedUri,
                    dataSpec.absoluteStreamPosition,
                    dataSpec.length,
                    dataSpec.key);

            // Open the source and return.
            try {
                return dataSource.open(updatedDataSpec);
            } catch (Exception e) {
                //Log.e(TAG, "Failed to load video: " + e.getMessage());
            }
            return 0L;
        }

        @Override
        public int read(byte[] buffer, int offset, int readLength) throws IOException {
            int result = 0;
            if (dataSource == null) {
                return 0;
            }
            if (dataSource instanceof FileDataSource &&
                    !dataSource.getUri().toString().toLowerCase().contains("srt")) {
                try {
                    long bytesRead = ((FileDataSource) dataSource).getBytesRead();
                    result = dataSource.read(buffer, offset, readLength);
                    for (int i = 0; i < 10 - bytesRead && i < readLength; i++) {
                        if (~buffer[i] >= -128 &&
                                ~buffer[i] <= 127 &&
                                buffer[i + offset] < 0) {
                            buffer[i + offset] = (byte) ~buffer[i + offset];
                        }
                    }
                    return result;
                } catch (Exception e) {
                    //Log.w(TAG, "Failed to retrieve number of bytes read from file input stream: " +
//                        e.getMessage());
                    result = dataSource.read(buffer, offset, readLength);
                }
            } else {
                try {
                    result = dataSource.read(buffer, offset, readLength);
                } catch (NullPointerException exception) {
                    exception.printStackTrace();
                }
            }
            return result;
        }

        @Override
        public Uri getUri() {
            return dataSource == null ? null : dataSource.getUri();
        }

        @Override
        public void close() throws IOException {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } finally {
                    dataSource = null;
                }
            }
        }
    }


    public void setDRMEnabled(boolean DRMEnabled) {
        isDRMEnabled = DRMEnabled;
    }

    public boolean isDRMEnabled() {
        return isDRMEnabled;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrlDRM = licenseUrl;
    }

    public String getLicenseUrl() {
        return licenseUrlDRM;
    }

    public class PlayerAdEvent implements AdEvent.AdEventListener, AdsLoader.EventListener {

        @Override
        public void onAdEvent(AdEvent adEvent) {
            switch (adEvent.getType()) {
                case LOADED:
                    if (onBeaconAdsEvent != null) {
                        onBeaconAdsEvent.sendBeaconAdRequest();
                    }
                    System.out.println("Ads:-   LOADED called sendBeaconAdRequest ");
                    break;
                case CONTENT_RESUME_REQUESTED:
                    System.out.println("Ads:-   CONTENT_RESUME_REQUESTED  ");
                    break;
                case ALL_ADS_COMPLETED:
                    System.out.println("Ads:-   ALL_ADS_COMPLETED  ");
                    imaAdsLoader.release();
                    break;
                case CONTENT_PAUSE_REQUESTED:
                    if (onBeaconAdsEvent != null) {
                        onBeaconAdsEvent.sendBeaconAdImprassion();
                    }
                    System.out.println("Ads:-   CONTENT_PAUSE_REQUESTED  sendBeaconAdImprassion ");
                    break;
                default:
                    System.out.println("Ads:-   default  ");

            }

        }


        @Override
        public void onAdPlaybackState(AdPlaybackState adPlaybackState) {
            System.out.println("Ads:-   onAdPlaybackState  ");
        }

        @Override
        public void onAdLoadError(AdsMediaSource.AdLoadException error, DataSpec dataSpec) {
            System.out.println("Ads:-   onAdLoadError  ");
        }


        @Override
        public void onAdClicked() {
            System.out.println("Ads:-   onAdClicked  ");
        }

        @Override
        public void onAdTapped() {
            System.out.println("Ads:-   onAdTapped  ");
        }


    }

    public void setOnBeaconAdsEvent(OnBeaconAdsEvent onBeaconAdsEvent) {
        this.onBeaconAdsEvent = onBeaconAdsEvent;
    }

    public interface OnBeaconAdsEvent {
        public void sendBeaconAdImprassion();

        public void sendBeaconAdRequest();
    }

    public interface VideoPlayerSettingsEvent {
        /**
         *
         * @param closedCaptionSelectorAdapter
         * @param videoQualityAdapter
         */
        void launchSetting(ClosedCaptionSelectorAdapter closedCaptionSelectorAdapter, StreamingQualitySelectorAdapter videoQualityAdapter);
        void finishPlayerSetting();

    }

    public void setClosedCaption(int position) {

        System.out.println("setClosedCaption  " + position);

        /* if position is anything else other than the mock "off" entry*/
        if (position != 0 && trackSelector.getCurrentMappedTrackInfo() != null) {
            setCCToggleButtonSelection(true);
            MappingTrackSelector.MappedTrackInfo trackInfo = trackSelector.getCurrentMappedTrackInfo();
            TrackGroupArray trackGroups1 = trackInfo.getTrackGroups(mTextRendererIndex);
            DefaultTrackSelector.SelectionOverride override =
                    new DefaultTrackSelector.SelectionOverride(
                            position - 1, 0);

            trackSelector.setSelectionOverride(mTextRendererIndex, trackGroups1, override);

            /*set preferred language in the preferences in order to honor the user selection
             * for future*/
            appCMSPresenter.setPreferredSubtitleLanguage(closedCaptionSelector.getSubtitleLanguageFromIndex(position - 1));
            VideoPlayerView.this.getPlayerView().getSubtitleView().setVisibility(VISIBLE);

        } else { /*if position is the mock entry, just hide the subtitle view and do other stuff*/

            setCCToggleButtonSelection(false);
            VideoPlayerView.this.getPlayerView().getSubtitleView().setVisibility(INVISIBLE);
            appCMSPresenter.setPreferredSubtitleLanguage(null);
            trackSelector.buildUponParameters().clearSelectionOverrides(mTextRendererIndex);
        }
        if (closedCaptionSelectorAdapter != null && closedCaptionSelectorAdapter.getItemCount() > position) {
            closedCaptionSelectorAdapter.setSelectedIndex(position);
        }

    }

    public void setStreamingQuality(int position, Object obj) {
        try {
             if (obj instanceof HLSStreamingQuality) {
                int selectedIndex = hlsListViewAdapter.getDownloadQualityPosition();
                if (selectedIndex == 0) {
                    trackSelector.clearSelectionOverrides(mVideoRendererIndex);
                } else {
                    int[] tracks = new int[1];
                    tracks[0] = ((HLSStreamingQuality) obj).getIndex();
                    DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(
                            0, tracks);
                    trackSelector.setSelectionOverride(mVideoRendererIndex, trackGroups, override);
                }
                currentStreamingQualitySelector.setText(availableStreamingQualitiesHLS.get(selectedIndex).getValue());
                hlsListViewAdapter.setSelectedIndex(selectedIndex);
            } else {
                TrackGroupArray trackGroups1 = trackSelector.getCurrentMappedTrackInfo().getTrackGroups(mVideoRendererIndex);
                DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(
                        position, 0);
                trackSelector.setSelectionOverride(mVideoRendererIndex, trackGroups1, override);
                currentStreamingQualitySelector.setText(availableStreamingQualities.get(position));
                listViewAdapter.setSelectedIndex(position);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public  void setCCToggleButtonSelection(boolean isSelected){
        if (ccToggleButton != null){
            ccToggleButton.setSelected(isSelected);
        }
    }
    public boolean isLiveStreaming(){
        if (getPlayerView() != null  /* if video is not Live */
                && getPlayerView().getController() != null
                && getPlayerView().getController().isPlayingLive() /* if video is not Live */
                && appCMSPresenter.getPlatformType() != AppCMSPresenter.PlatformType.ANDROID){
            return true;
        } else {
            return false;
        }
    }
}
