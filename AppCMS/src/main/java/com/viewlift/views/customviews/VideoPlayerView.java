package com.viewlift.views.customviews;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
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
import android.widget.RelativeLayout;
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
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
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
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSDownloadRadioAdapter;
import com.viewlift.views.customviews.exoplayerview.AppCMSSimpleExoPlayerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/31/17.
 */

public class VideoPlayerView extends FrameLayout implements Player.EventListener,
        AdaptiveMediaSourceEventListener, SimpleExoPlayer.VideoListener, VideoRendererEventListener, AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "VideoPlayerFragment";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    protected DataSource.Factory mediaDataSourceFactory;
    protected String userAgent;
    boolean isLoadedNext;
    private AppCMSPresenter appCMSPresenter;
    private ToggleButton ccToggleButton;
    private TextView currentStreamingQualitySelector;
    private AlwaysSelectedTextView videoPlayerTitle;
    private DefaultTimeBar timeBar;
    private boolean isClosedCaptionEnabled = false;
    private Uri uri;
    private Action1<PlayerState> onPlayerStateChanged;
    private Action1<Integer> onPlayerControlsStateChanged;
    private Action1<Boolean> onClosedCaptionButtonClicked;
    protected PlayerState playerState;
    protected SimpleExoPlayer player;
    protected AppCMSSimpleExoPlayerView playerView;
    private int resumeWindow;
    private long resumePosition;

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
    private boolean mAudioFocusGranted = false;
    private String filmId;
    DefaultTrackSelector trackSelector;

    private boolean playOnReattach;

    private PageView pageView;

    private RecyclerView listView;
    private StreamingQualitySelectorAdapter listViewAdapter;
    private HLSStreamingQualitySelectorAdapter hlsListViewAdapter;

    private boolean fullScreenMode;
    private AdaptiveTrackSelection.Factory videoTrackSelectionFactory;
    private int mVideoRendererIndex;
    private boolean streamingQualitySelectorCreated;
    private boolean useHls;

    public VideoPlayerView(Context context) {
        super(context);
        initializeView(context);
    }

    public VideoPlayerView(Context context, AppCMSPresenter appCMSPresenter) {
        super(context);
        initializeView(context);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializePlayer(context);
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
        if(appCMSPresenter != null &&  appCMSPresenter.getPlatformType() == AppCMSPresenter.PlatformType.ANDROID){
            if (closedCaptionUri == null) {
                if (ccToggleButton != null) {
                    ccToggleButton.setVisibility(GONE);
                }
            } else {
                if (ccToggleButton != null) {
                    ccToggleButton.setChecked(isClosedCaptionEnabled);
                    ccToggleButton.setVisibility(VISIBLE);
                }
            }

        } else {
            if (ccToggleButton != null) {
                ccToggleButton.setVisibility(GONE);
            }
        }

        if (getContext().getResources().getBoolean(R.bool.enable_stream_quality_selection) &&
                currentStreamingQualitySelector != null &&
                streamingQualitySelector != null) {
            List<String> availableStreamingQualities = streamingQualitySelector.getAvailableStreamingQualities();
            if (0 < availableStreamingQualities.size()) {
                int streamingQualityIndex = streamingQualitySelector.getMpegResolutionIndexFromUrl(videoUri.toString());
                if (0 <= streamingQualityIndex) {
                    currentStreamingQualitySelector.setText(availableStreamingQualities.get(streamingQualityIndex));
                    setSelectedStreamingQualityIndex();
                }
            }
        }
    }

    public boolean shouldPlayWhenReady() {
        return player != null && player.getPlayWhenReady();
    }

    public void startPlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
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
        }
    }

    public void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }

    }

    public void stopPlayer() {
        if (player != null) {
            player.stop();
        }
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
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

    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }

    public long getBitrate() {
        return bitrate;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoWidth() {
        return videoWidth;
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



    private void initializePlayer(Context context) {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
        userAgent = Util.getUserAgent(getContext(),
                getContext().getString(R.string.app_cms_user_agent));
        useHls = getResources().getBoolean(R.bool.use_hls);
        ccToggleButton = createCC_ToggleButton();
        ((RelativeLayout) playerView.findViewById(R.id.exo_controller_container)).addView(ccToggleButton);
        ccToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onClosedCaptionButtonClicked != null) {
                onClosedCaptionButtonClicked.call(isChecked);
            }
            isClosedCaptionEnabled = isChecked;
        });

        currentStreamingQualitySelector = playerView.findViewById(R.id.streamingQualitySelector);
        if (getContext().getResources().getBoolean(R.bool.enable_stream_quality_selection)
                && !useHls
                && !streamingQualitySelectorCreated) {
            createStreamingQualitySelector();
            currentStreamingQualitySelector.setVisibility(View.VISIBLE);
        } else {
            currentStreamingQualitySelector.setVisibility(View.GONE);
        }

        mediaDataSourceFactory = buildDataSourceFactory(true);

        timeBar = playerView.findViewById(R.id.exo_progress);

        videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        trackSelector.setTunnelingAudioSessionId(C.generateAudioSessionIdV21(getContext()));

        if (player != null) {
            player.release();
        }
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
                listView.setBackgroundColor(appCMSPresenter.getGeneralBackgroundColor());
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
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(appCMSPresenter.getGeneralBackgroundColor()));
                }
                currentStreamingQualitySelector.setOnClickListener(v -> {
                    dialog.show();
                    listViewAdapter.notifyDataSetChanged();
                });


                listViewAdapter.setItemClickListener(v -> {
                    try {
                        long currentPosition = getCurrentPosition();
                        if (listViewAdapter.selectedIndex != listViewAdapter.getDownloadQualityPosition()) {
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
        streamingQualitySelectorCreated = true;
    }

    /**
     * Used to extract the different tracks available in an HLS stream.
     *
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
                if (player.getRendererType(i) == C.TRACK_TYPE_VIDEO){
                    mVideoRendererIndex = i;
                    break;
                }
            }
        }
        if (streamingQualitySelector != null && appCMSPresenter != null) {
            currentStreamingQualitySelector.setVisibility(VISIBLE);
            TrackGroupArray trackGroups = trackSelector.getCurrentMappedTrackInfo().getTrackGroups(mVideoRendererIndex);
            List<HLSStreamingQuality> availableStreamingQualities = new ArrayList<>();
            availableStreamingQualities.add(new HLSStreamingQuality(0, "Auto"));
            for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
                TrackGroup group = trackGroups.get(groupIndex);
                for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                    Format format = group.getFormat(trackIndex);
                    availableStreamingQualities.add(new HLSStreamingQuality(trackIndex,
                            format.height == Format.NO_VALUE ? "" : format.height +"p"));
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
            set.addAll(availableStreamingQualities);
            availableStreamingQualities.clear();
            availableStreamingQualities.addAll(set);

            if (availableStreamingQualities.size() > 1) {
                listView = new RecyclerView(getContext());
                hlsListViewAdapter = new HLSStreamingQualitySelectorAdapter(getContext(),
                        appCMSPresenter,
                        availableStreamingQualities);

                listView.setAdapter(hlsListViewAdapter);
                listView.setBackgroundColor(appCMSPresenter.getGeneralBackgroundColor());
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
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(appCMSPresenter.getGeneralBackgroundColor()));
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
                            if (selectedIndex == 0) {
                                trackSelector.clearSelectionOverrides(mVideoRendererIndex);
                            } else {
                                int[] tracks = new int[1];
                                tracks[0] = ((HLSStreamingQuality) v).getIndex();
                                MappingTrackSelector.SelectionOverride override = new MappingTrackSelector.SelectionOverride(videoTrackSelectionFactory,
                                        0, tracks);
                                trackSelector.setSelectionOverride(mVideoRendererIndex, trackGroups, override);
                            }
                            currentStreamingQualitySelector.setText(availableStreamingQualities.get(selectedIndex).getValue());
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

    private void setSelectedStreamingQualityIndex() {
        if (streamingQualitySelector != null && listViewAdapter != null) {
            int currentIndex = -1;
            int updatedIndex = -1;
            try {
                currentIndex = listViewAdapter.selectedIndex;
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
    public void onTimelineChanged(Timeline timeline, Object o) {

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
                    && getContext().getResources().getBoolean(R.bool.enable_stream_quality_selection) /*check if stream quality selector is enabled*/
                    && useHls /*createStreamingQualitySelectorForHLS is only called for HLS stream*/
                    && !streamingQualitySelectorCreated /*making sure the selector isn't already created*/
                    ) {
                createStreamingQualitySelectorForHLS();

                // Default "Auto" is selected
                currentStreamingQualitySelector.setText(getContext().getString(R.string.auto));
                currentStreamingQualitySelector.setVisibility(View.VISIBLE);
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

    }

    @Override
    public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason,
                                          Object trackSelectionData, long mediaTimeMs) {

    }

    public void setListener(ErrorEventListener errorEventListener) {
        mErrorEventListener = errorEventListener;
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {
        setBitrate(format.bitrate / 1000);
        setVideoHeight(format.height);
        setVideoWidth(format.width);
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
        //commented due to custom video player implementation
//        pausePlayer();
//
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

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pausePlayer();
                break;

            case AudioManager.AUDIOFOCUS_GAIN:
                if (getPlayer() != null && getPlayer().getPlayWhenReady()) {
                    startPlayer();
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

    public interface ErrorEventListener {
        void onRefreshTokenCallback();

        void onFinishCallback(String message);
    }

    public interface StreamingQualitySelector {
        List<String> getAvailableStreamingQualities();
        String getStreamingQualityUrl(String streamingQuality);
        String getMpegResolutionFromUrl(String mpegUrl);
        int getMpegResolutionIndexFromUrl(String mpegUrl);
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

            boolean useHls = false;
            if (dataSpec.uri.toString().contains("m3u8") || dataSpec.uri.toString().contains("hls")){
                useHls=true;
            }

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
            if (dataSource == null) {
                return 0;
            }
            if (dataSource instanceof FileDataSource) {
                try {
                    long bytesRead = ((FileDataSource) dataSource).getBytesRead();
                    result = dataSource.read(buffer, offset, readLength);
                    for (int i = 0; i < 10 - bytesRead && i < readLength; i++) {
                        if (~buffer[i] >= -128 && ~buffer[i] <= 127) {
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

    protected ToggleButton createCC_ToggleButton() {
        ToggleButton mToggleButton = new ToggleButton(getContext());
        RelativeLayout.LayoutParams toggleLP = new RelativeLayout.LayoutParams(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_width, getContext()), BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_width, getContext()));
        toggleLP.addRule(RelativeLayout.CENTER_VERTICAL);
        toggleLP.addRule(RelativeLayout.RIGHT_OF, R.id.exo_media_controller);
        toggleLP.setMarginStart(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_left_margin, getContext()));
        toggleLP.setMarginEnd(BaseView.dpToPx(R.dimen.app_cms_video_controller_cc_left_margin, getContext()));
        mToggleButton.setLayoutParams(toggleLP);
        mToggleButton.setChecked(false);
        mToggleButton.setTextOff("");
        mToggleButton.setTextOn("");
        mToggleButton.setText("");
        mToggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.cc_toggle_selector, null));
        mToggleButton.setVisibility(GONE);
        return mToggleButton;
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

            viewHolder.getmText().setTextColor(appCMSPresenter.getBrandPrimaryCtaColor());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (viewHolder.getmRadio().getButtonDrawable() != null) {
                    viewHolder.getmRadio().getButtonDrawable().setColorFilter(appCMSPresenter.getBrandPrimaryCtaColor(),
                            PorterDuff.Mode.MULTIPLY);
                }
            } else {
                int switchOnColor = appCMSPresenter.getBrandPrimaryCtaColor();
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


    /**
     * Specialized version of the {@link AppCMSDownloadRadioAdapter} for HLS track selection
     */
    private static class HLSStreamingQualitySelectorAdapter
            extends AppCMSDownloadRadioAdapter<HLSStreamingQuality> {
        List<HLSStreamingQuality> availableStreamingQualities;
        int selectedIndex;
        AppCMSPresenter appCMSPresenter;
        HLSStreamingQualitySelectorAdapter(Context context,
                                           AppCMSPresenter appCMSPresenter,
                                           List<HLSStreamingQuality> items) {
            super(context, items);
            this.appCMSPresenter = appCMSPresenter;
            this.availableStreamingQualities = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ViewHolder viewHolder = super.onCreateViewHolder(viewGroup, i);

            viewHolder.getmText().setTextColor(appCMSPresenter.getBrandPrimaryCtaColor());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (viewHolder.getmRadio().getButtonDrawable() != null) {
                    viewHolder.getmRadio().getButtonDrawable().setColorFilter(appCMSPresenter.getBrandPrimaryCtaColor(),
                            PorterDuff.Mode.MULTIPLY);
                }
            } else {
                int switchOnColor = appCMSPresenter.getBrandPrimaryCtaColor();
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
            viewHolder.getmText().setText(availableStreamingQualities.get(i).getValue());
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

        void setSelectedIndex(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }

        int getDownloadQualityPosition() {
            return downloadQualityPosition;
        }
    }

    /**
     * Class is used to store the index and value (resolution eg. 360p) of a particular track of
     *  an HLS stream.
     */
    private static class HLSStreamingQuality {
        int index;
        String value;

        HLSStreamingQuality(int index, String value) {
            this.index = index;
            this.value = value;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }


}
