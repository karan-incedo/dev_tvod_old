package com.viewlift.views.customviews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
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
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSDownloadRadioAdapter;

import net.nightwhistler.htmlspanner.TextUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

/*
 * Created by viewlift on 5/31/17.
 */

public class VideoPlayerView extends FrameLayout implements Player.EventListener,
        AdaptiveMediaSourceEventListener, SimpleExoPlayer.VideoListener, VideoRendererEventListener {
    private static final String TAG = "VideoPlayerFragment";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    protected DataSource.Factory mediaDataSourceFactory;
    protected String userAgent;
    boolean isLoadedNext;
    private AppCMSPresenter appCMSPresenter;
    private ToggleButton ccToggleButton;
    private LinearLayout chromecastLivePlayerParent;
    private FrameLayout chromecastButtonPlaceholder;
    private ViewGroup chromecastButtonPreviousParent;
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
    private PlayerState playerState;
    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private int resumeWindow;
    private long resumePosition;
    private int timeBarColor;

    private long bitrate = 0l;
    private int videoHeight = 0;
    private int videoWidth = 0;

    private long mCurrentPlayerPosition;
    private ErrorEventListener mErrorEventListener;

    private StreamingQualitySelector streamingQualitySelector;

    private Map<String, Integer> failedMediaSourceLoads;

    private int fullscreenResizeMode;
    private Uri closedCaptionUri;

    private String policyCookie;
    private String signatureCookie;
    private String keyPairIdCookie;

    private boolean playerJustInitialized;

    private boolean playOnReattach;

    private String filmId;

    private PageView pageView;

    private RecyclerView listView;
    private StreamingQualitySelectorAdapter listViewAdapter;

    private boolean fullScreenMode;

    public VideoPlayerView(Context context, AppCMSPresenter appCMSPresenter) {
        super(context);
        this.appCMSPresenter = appCMSPresenter;
        initializeView(context);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    public void setUriOnConnection(Uri uri, Uri closedCaptionUri) {
        this.uri = uri;
        try {
            player.prepare(buildMediaSource(uri, closedCaptionUri));
            player.seekTo(mCurrentPlayerPosition);
        } catch (IllegalStateException e) {
            //Log.e(TAG, "Unsupported video format for URI: " + uri.toString());
        }
    }

    public void setUri(Uri videoUri, Uri closedCaptionUri) {
        this.uri = videoUri;
        this.closedCaptionUri = closedCaptionUri;
        try {
            player.prepare(buildMediaSource(videoUri, closedCaptionUri));
        } catch (IllegalStateException e) {
            //Log.e(TAG, "Unsupported video format for URI: " + videoUri.toString());
        }
        if (closedCaptionUri == null) {
            if (ccToggleButton != null) {
                ccToggleButton.setVisibility(GONE);
            }
        } else {
            if (ccToggleButton != null) {
                ccToggleButton.setChecked(isClosedCaptionEnabled);
            }
        }

        if (getContext().getResources().getBoolean(R.bool.enable_stream_quality_selection) &&
                currentStreamingQualitySelector != null &&
                streamingQualitySelector != null) {
            List<String> availableStreamingQualities = streamingQualitySelector.getAvailableStreamingQualities();
            if (0 < availableStreamingQualities.size()) {
                String videoStreamingUrl = streamingQualitySelector.getVideoStreamingUrl();
                if(TextUtils.isEmpty(videoStreamingUrl)){
                    videoStreamingUrl = videoUri.toString();
                }
                int streamingQualityIndex = streamingQualitySelector.getMpegResolutionIndexFromUrl(videoStreamingUrl);
                if (0 <= streamingQualityIndex) {
                    currentStreamingQualitySelector.setText(availableStreamingQualities.get(streamingQualityIndex));
                    setSelectedStreamingQualityIndex();
                }
            }
        }
    }

    public Uri getUri() {
        return uri;
    }

    public boolean shouldPlayWhenReady() {
        return player != null && player.getPlayWhenReady();
    }

    public void startPlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
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

    public SimpleExoPlayerView getPlayerView() {
        return playerView;
    }

    public void setFillBasedOnOrientation() {
        if (BaseView.isLandscape(getContext())) {
            playerView.setResizeMode(fullscreenResizeMode);
        } else {
//            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
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
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.video_player_view, this);
        playerView = findViewById(R.id.videoPlayerView);
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

    public boolean shouldPlayOnReattach() {
        return playOnReattach;
    }

    private void initializePlayer(Context context) {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
        userAgent = Util.getUserAgent(getContext(),
                getContext().getString(R.string.app_cms_user_agent));
        ccToggleButton = (ToggleButton) playerView.findViewById(R.id.ccButton);
        ccToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onClosedCaptionButtonClicked != null) {
                onClosedCaptionButtonClicked.call(isChecked);
            }
            isClosedCaptionEnabled = isChecked;
        });

        chromecastLivePlayerParent = playerView.findViewById(R.id.chromecast_live_player_parent);

        chromecastButtonPlaceholder = playerView.findViewById(R.id.chromecast_live_player_placeholder);

        enterFullscreenButton = playerView.findViewById(R.id.full_screen_button);

        enterFullscreenButton.setOnClickListener(v -> {
            enterFullScreenMode();
        });

        exitFullscreenButton = playerView.findViewById(R.id.full_screen_back_button);

        exitFullscreenButton.setOnClickListener(v -> {
            exitFullscreenMode(true);
        });

        currentStreamingQualitySelector = playerView.findViewById(R.id.streamingQualitySelector);
        if (getContext().getResources().getBoolean(R.bool.enable_stream_quality_selection)) {
            createStreamingQualitySelector();
        } else {
            currentStreamingQualitySelector.setVisibility(View.GONE);
        }

        videoPlayerTitle = playerView.findViewById(R.id.app_cms_video_player_title_view);
        videoPlayerTitle.setText("");

        mediaDataSourceFactory = buildDataSourceFactory(true);

        timeBar = playerView.findViewById(R.id.exo_progress);

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        DefaultTrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        trackSelector.setTunnelingAudioSessionId(C.generateAudioSessionIdV21(getContext()));

        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        player.addListener(this);
        player.setVideoDebugListener(this);
        playerView.setPlayer(player);
        playerView.setControllerVisibilityListener(visibility -> {
            if (onPlayerControlsStateChanged != null) {
                onPlayerControlsStateChanged.call(visibility);
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

    private void createStreamingQualitySelector() {
        if (streamingQualitySelector != null && appCMSPresenter != null) {
            currentStreamingQualitySelector.setVisibility(VISIBLE);
            List<String> availableStreamingQualities = streamingQualitySelector.getAvailableStreamingQualities();
            if (availableStreamingQualities != null && 1 < availableStreamingQualities.size()) {
                listView = new RecyclerView(getContext());
                listViewAdapter = new StreamingQualitySelectorAdapter(getContext(),
                        appCMSPresenter,
                        availableStreamingQualities);

                listView.setAdapter(listViewAdapter);
                listView.setBackgroundColor(Color.parseColor(appCMSPresenter.getAppBackgroundColor()));
                listView.setLayoutManager(new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL,
                        false));

                setSelectedStreamingQualityIndex();

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                if (listView.getParent() != null && listView.getParent() instanceof ViewGroup) {
                    ((ViewGroup) listView.getParent()).removeView(listView);
                }
                builder.setView(listView);
                final Dialog dialog = builder.create();
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                            Color.parseColor(appCMSPresenter.getAppBackgroundColor())));
                }
                currentStreamingQualitySelector.setOnClickListener(v -> {
                    dialog.show();
                    listViewAdapter.notifyDataSetChanged();
                });
                listViewAdapter.setItemClickListener(v -> {
                    try {
                        long currentPosition = getCurrentPosition();
                        if (listViewAdapter.selectedIndex != listViewAdapter.getDownloadQualityPosition()) {
                            streamingQualitySelector.setVideoStreaming(availableStreamingQualities.get(listViewAdapter.getDownloadQualityPosition()));
                            setUri(Uri.parse(streamingQualitySelector.getStreamingQualityUrl(availableStreamingQualities.get(listViewAdapter.getDownloadQualityPosition()))),
                                    closedCaptionUri);
                        }
                        setCurrentPosition(currentPosition);
                        currentStreamingQualitySelector.setText(availableStreamingQualities.get(listViewAdapter.getDownloadQualityPosition()));
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
    }

    private void setSelectedStreamingQualityIndex() {
        if (streamingQualitySelector != null && listViewAdapter != null) {
            int currentIndex = -1;
            int updatedIndex = -1;
            try {
                currentIndex = listViewAdapter.selectedIndex;
                String videoUrl = streamingQualitySelector.getVideoStreamingUrl();
                if(TextUtils.isEmpty(videoUrl)){
                    videoUrl = uri.toString();
                }
                    updatedIndex = streamingQualitySelector.getMpegResolutionIndexFromUrl(videoUrl);
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

    private MediaSource buildMediaSource(Uri uri, Uri ccFileUrl) {
        if (mediaDataSourceFactory instanceof UpdatedUriDataSourceFactory) {
            ((UpdatedUriDataSourceFactory) mediaDataSourceFactory).signatureCookies.policyCookie = policyCookie;
            ((UpdatedUriDataSourceFactory) mediaDataSourceFactory).signatureCookies.signatureCookie = signatureCookie;
            ((UpdatedUriDataSourceFactory) mediaDataSourceFactory).signatureCookies.keyPairIdCookie = keyPairIdCookie;
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

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri) :
                Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri,
                        buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        null,
                        null);

            case C.TYPE_DASH:
                return new DashMediaSource(uri,
                        buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        null,
                        null);

            case C.TYPE_HLS:
                return new HlsMediaSource(uri,
                        mediaDataSourceFactory,
                        new Handler(),
                        this);

            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri,
                        mediaDataSourceFactory,
                        new DefaultExtractorsFactory(),
                        null,
                        null);

            default:
                throw new IllegalStateException("Unsupported type: " + type);
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
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
        //
    }

    @Override
    public void onLoadingChanged(boolean b) {
        //
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
        }
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        //
    }

    @Override
    public void onSeekProcessed() {

    }

    public void sendPlayerPosition(long position) {
        mCurrentPlayerPosition = position;
    }

    @Override
    public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                              int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                              long mediaEndTimeMs, long elapsedRealtimeMs) {
        //Log.d(TAG, "Load started");
        bitrate = (trackFormat.bitrate / 1000);
    }

    @Override
    public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                                int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                                long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs,
                                long bytesLoaded) {
        failedMediaSourceLoads.clear();
    }

    @Override
    public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                               int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                               long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs,
                               long bytesLoaded) {
        Log.d(TAG, "Load cancelled");
    }

    @Override
    public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat,
                            int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs,
                            long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs,
                            long bytesLoaded, IOException error, boolean wasCanceled) {
        //Log.d(TAG, "onLoadError : " + error.getMessage());
        /**
         * We can enhance logic here depending on the error code list that we will use for closing the video page.
         */
        if ((error.getMessage().contains("404") ||
                error.getMessage().contains("400"))
                && !isLoadedNext) {
            String failedMediaSourceLoadKey = dataSpec.uri.toString();
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
    public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {
        //
    }

    @Override
    public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason,
                                          Object trackSelectionData, long mediaTimeMs) {
        //
    }

    public void setListener(ErrorEventListener errorEventListener) {
        mErrorEventListener = errorEventListener;
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {
        //
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
    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {
        //
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
        //
    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {
        //
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

    public interface ErrorEventListener {
        void onRefreshTokenCallback();

        void onFinishCallback(String message);
    }

    public interface StreamingQualitySelector {
        List<String> getAvailableStreamingQualities();
        String getStreamingQualityUrl(String streamingQuality);
        String getMpegResolutionFromUrl(String mpegUrl);
        int getMpegResolutionIndexFromUrl(String mpegUrl);
        String getVideoStreamingUrl();
        void setVideoStreaming(String videoStreamingUrl);
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

    public AppCMSPresenter getAppCMSPresenter() {
        return appCMSPresenter;
    }

    public void setAppCMSPresenter(AppCMSPresenter appCMSPresenter) {
        this.appCMSPresenter = appCMSPresenter;
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

            boolean useHls = dataSpec.uri.toString().contains("m3u8");

            if (useHls && updatedUri.toString().contains("?")) {
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
            if (dataSource instanceof FileDataSource &&
                    !dataSource.getUri().toString().toLowerCase().contains("srt")) {
                try {
                    long bytesRead = ((FileDataSource) dataSource).getBytesRead();
                    result = dataSource.read(buffer, offset, readLength);
                    for (int i = 0; i < 10 - bytesRead && i < readLength; i++) {
                        if (~buffer[i] >= -128 &&
                                ~buffer[i] <= 127 &&
                                buffer[i + offset]<0) {
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
                result = dataSource.read(buffer, offset, readLength);
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

    private static class StreamingQualitySelectorAdapter extends AppCMSDownloadRadioAdapter<String> {
        List<String> availableStreamingQualities;
        int selectedIndex;
        AppCMSPresenter appCMSPresenter;
        public StreamingQualitySelectorAdapter(Context context,
                                               AppCMSPresenter appCMSPresenter,
                                               List<String> items) {
            super(context, items);
            this.appCMSPresenter = appCMSPresenter;
            this.availableStreamingQualities = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ViewHolder viewHolder = super.onCreateViewHolder(viewGroup, i);

            viewHolder.getmText().setTextColor(Color.parseColor(ViewCreator.getColor(viewGroup.getContext(), appCMSPresenter.getAppCtaTextColor())));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (viewHolder.getmRadio().getButtonDrawable() != null) {
                    viewHolder.getmRadio().getButtonDrawable().setColorFilter(Color.parseColor(
                            ViewCreator.getColor(viewGroup.getContext(),
                                    appCMSPresenter.getAppCtaBackgroundColor())),
                            PorterDuff.Mode.MULTIPLY);
                }
            } else {
                int switchOnColor = Color.parseColor(
                        ViewCreator.getColor(viewGroup.getContext(),
                                appCMSPresenter.getAppCtaBackgroundColor()));
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_checked},
                                new int[]{}
                        }, new int[]{
                        switchOnColor,
                        switchOnColor
                });

                viewHolder.getmRadio().setButtonTintList(colorStateList);
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(AppCMSDownloadRadioAdapter.ViewHolder viewHolder, int i) {
            super.onBindViewHolder(viewHolder, i);
            viewHolder.getmText().setText(availableStreamingQualities.get(i));
            if (selectedIndex == i) {
                viewHolder.getmRadio().setChecked(true);
            } else {
                viewHolder.getmRadio().setChecked(false);
            }
            viewHolder.getmRadio().invalidate();
        }

        @Override
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void setSelectedIndex(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }

        public int getDownloadQualityPosition() {
            return downloadQualityPosition;
        }
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

    public void enterFullScreenMode() {
        disableFullScreenMode();
        fullScreenMode = true;
        if (appCMSPresenter != null) {
            appCMSPresenter.sendEnterFullScreenAction();
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
            appCMSPresenter.sendExitFullScreenAction(true);
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
}
