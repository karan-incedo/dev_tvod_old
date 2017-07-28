package com.viewlift.views.customviews;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
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
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.viewlift.R;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by viewlift on 5/31/17.
 */

public class VideoPlayerView extends FrameLayout implements ExoPlayer.EventListener {
    private static final String TAG = "VideoPlayerFragment";
    private ToggleButton ccToggleButton;

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

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private Uri uri;
    private Action1<PlayerState> onPlayerStateChanged;
    private Action1<Integer> onPlayerControlsStateChanged;
    private Action1<Boolean> onClosedCaptionButtonClicked;
    private PlayerState playerState;
    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private int resumeWindow;
    private long resumePosition;
    protected DataSource.Factory mediaDataSourceFactory;
    protected String userAgent;

    public VideoPlayerView(Context context) {
        super(context);
        this.uri = uri;
        init(context, null, 0);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public VideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
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

    public void setUri(Uri videoUri, Uri closedCaptionUri) {
        this.uri = videoUri;
        try {
            player.prepare(buildMediaSource(videoUri, closedCaptionUri));
        } catch (IllegalStateException e) {
            Log.e(TAG, "Unsupported video format for URI: " + videoUri.toString());
        }
        if (closedCaptionUri == null) {
            if (ccToggleButton != null) {
                ccToggleButton.setVisibility(GONE);
            }
        } else {
            if (ccToggleButton != null) {
                ccToggleButton.setChecked(true);
            }
        }
    }

    public boolean shouldPlayWhenReady() {
        if (player != null) {
            return player.getPlayWhenReady();
        }

        return false;
    }

    public void startPlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    public void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(player.getPlayWhenReady());
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

    public SimpleExoPlayerView getPlayerView() {
        return playerView;
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        initializePlayer(context, attrs, defStyleAttr);
        playerState = new PlayerState();
    }

    private void initializePlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
        LayoutInflater.from(context).inflate(R.layout.video_player_view, this);
        playerView = (SimpleExoPlayerView) findViewById(R.id.videoPlayerView);
        userAgent = Util.getUserAgent(getContext(),
                getContext().getString(R.string.app_cms_user_agent));
        ccToggleButton = (ToggleButton) playerView.findViewById(R.id.ccButton);
        ccToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onClosedCaptionButtonClicked != null) {
                    onClosedCaptionButtonClicked.call(isChecked);
                }
            }
        });


        mediaDataSourceFactory = buildDataSourceFactory(true);

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        player.addListener(this);
        playerView.setPlayer(player);
        playerView.setControllerVisibilityListener(new PlaybackControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (onPlayerControlsStateChanged != null) {
                    onPlayerControlsStateChanged.call(visibility);
                }
            }
        });

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        Log.i(TAG, "Audio focus has changed: " + focusChange);
                    }
                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    private MediaSource buildMediaSource(Uri uri, Uri ccFileUrl) {
        Format textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP,
                null, Format.NO_VALUE, C.SELECTION_FLAG_DEFAULT, "en", null);
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
                        null,
                        null);
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
        return new DefaultDataSourceFactory(getContext(),
                bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
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
        playerState.playWhenReady = playWhenReady;
        playerState.playbackState = playbackState;

        if (onPlayerStateChanged != null) {
            Observable.just(playerState).subscribe(onPlayerStateChanged);
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
}
