package com.viewlift.views.fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.viewlift.Audio.AlbumArtCache;
import com.viewlift.Audio.MusicService;
import com.viewlift.Audio.ui.PlaybackControlsFragment;
import com.viewlift.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AppCMSPlayAudioFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.track_image)
    ImageView trackImage;
    @BindView(R.id.track_name)
    TextView trackName;
    @BindView(R.id.artist_name)
    TextView artistName;
    @BindView(R.id.track_year)
    TextView trackYear;
    @BindView(R.id.album_name)
    TextView albumName;
    @BindView(R.id.start_time)
    TextView trackStartTime;
    @BindView(R.id.end_time)
    TextView trackEndTime;
    @BindView(R.id.seek_audio)
    SeekBar seekAudio;
    @BindView(R.id.shuffle)
    ImageButton shuffle;
    @BindView(R.id.prev)
    ImageButton previousTrack;
    @BindView(R.id.play_pause)
    ImageButton playPauseTrack;
    @BindView(R.id.next)
    ImageButton nextTrack;
    @BindView(R.id.playlist)
    ImageButton playlist;

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private final Handler mHandler = new Handler();
    private MediaBrowserCompat mMediaBrowser;
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private String mCurrentArtUrl;
    private View rootView;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;
    private PlaybackStateCompat mLastPlaybackState;

    public static AppCMSPlayAudioFragment newInstance(Context context) {
        AppCMSPlayAudioFragment appCMSPlayAudioFragment = new AppCMSPlayAudioFragment();
        Bundle args = new Bundle();

        appCMSPlayAudioFragment.setArguments(args);


        return appCMSPlayAudioFragment;
    }

    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                updateMediaDescription(metadata.getDescription());
                updateDuration(metadata);
            }
        }
    };

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    try {
                        connectToSession(mMediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                    }
                }
            };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaBrowser = new MediaBrowserCompat(getActivity(),
                new ComponentName(getActivity(), MusicService.class), mConnectionCallback, null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_app_cmsplay_audio, container, false);
        ButterKnife.bind(this, rootView);
        shuffle.setOnClickListener(this);
        previousTrack.setOnClickListener(this);
        playPauseTrack.setOnClickListener(this);
        nextTrack.setOnClickListener(this);
        playlist.setOnClickListener(this);
        mPauseDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.pause_track);
        mPlayDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.play_track);


        seekAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                trackStartTime.setText(DateUtils.formatElapsedTime(progress / 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopSeekbarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().seekTo(seekBar.getProgress());
                scheduleSeekbarUpdate();
            }
        });
        if (savedInstanceState == null) {
            updateFromParams(getActivity().getIntent());
        }
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view == shuffle) {
        }
        if (view == previousTrack) {
            MediaControllerCompat.TransportControls controls = MediaControllerCompat.getMediaController(getActivity()).getTransportControls();
            controls.skipToPrevious();
        }
        if (view == playPauseTrack) {
            PlaybackStateCompat state = MediaControllerCompat.getMediaController(getActivity()).getPlaybackState();
            if (state != null) {
                MediaControllerCompat.TransportControls controls = MediaControllerCompat.getMediaController(getActivity()).getTransportControls();
                switch (state.getState()) {
                    case PlaybackStateCompat.STATE_PLAYING: // fall through
                    case PlaybackStateCompat.STATE_BUFFERING:
                        controls.pause();
                        stopSeekbarUpdate();
                        break;
                    case PlaybackStateCompat.STATE_PAUSED:
                    case PlaybackStateCompat.STATE_STOPPED:
                        controls.play();
                        scheduleSeekbarUpdate();
                        break;
                    default:
                }
            }
        }
        if (view == nextTrack) {
            MediaControllerCompat.TransportControls controls = MediaControllerCompat.getMediaController(getActivity()).getTransportControls();
            controls.skipToNext();
        }
        if (view == playlist) {
        }
    }


    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(
                getActivity(), token);
        if (mediaController.getMetadata() == null) {
            getActivity().finish();
            return;
        }
        MediaControllerCompat.setMediaController(getActivity(), mediaController);
        mediaController.registerCallback(mCallback);
        PlaybackStateCompat state = mediaController.getPlaybackState();

        updatePlaybackState(state);
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMediaDescription(metadata.getDescription());
            updateDuration(metadata);
        }
        updateProgress();
        if (state != null && (state.getState() == PlaybackStateCompat.STATE_PLAYING ||
                state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            scheduleSeekbarUpdate();
        }
    }

    private void updateFromParams(Intent intent) {
        if (intent != null) {
            MediaDescriptionCompat description = intent.getParcelableExtra(
                    PlaybackControlsFragment.EXTRA_CURRENT_MEDIA_DESCRIPTION);
            if (description != null) {
                updateMediaDescription(description);
            }
        }
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMediaBrowser != null) {
            mMediaBrowser.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaBrowser != null) {
            mMediaBrowser.disconnect();
        }
        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(getActivity());
        if (controllerCompat != null) {
            controllerCompat.unregisterCallback(mCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
        mExecutorService.shutdown();
    }

    private void fetchImageAsync(@NonNull MediaDescriptionCompat description) {
        if (description.getIconUri() == null) {
            return;
        }
        String artUrl = description.getIconUri().toString();
        mCurrentArtUrl = artUrl;


        Glide.with(getActivity())
                .load(mCurrentArtUrl).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.logo)
                .into(trackImage);

    }

    private void updateMediaDescription(MediaDescriptionCompat description) {
        if (description == null) {
            return;
        }
        trackName.setText(description.getTitle());
        artistName.setText(description.getSubtitle());
        fetchImageAsync(description);
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        seekAudio.setMax(duration);

        trackEndTime.setText(DateUtils.formatElapsedTime(duration / 1000));
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        mLastPlaybackState = state;
        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(getActivity());


        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
//                mLoading.setVisibility(INVISIBLE);
                playPauseTrack.setVisibility(VISIBLE);
                playPauseTrack.setImageDrawable(mPauseDrawable);
//                mControllers.setVisibility(VISIBLE);
                scheduleSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
//                mControllers.setVisibility(VISIBLE);
//                mLoading.setVisibility(INVISIBLE);
                playPauseTrack.setVisibility(VISIBLE);
                playPauseTrack.setImageDrawable(mPlayDrawable);
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
//                mLoading.setVisibility(INVISIBLE);
                playPauseTrack.setVisibility(VISIBLE);
                playPauseTrack.setImageDrawable(mPlayDrawable);
                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                playPauseTrack.setVisibility(INVISIBLE);
//                mLoading.setVisibility(VISIBLE);
//                mLine3.setText(R.string.loading);
                stopSeekbarUpdate();
                break;
            default:
        }

        nextTrack.setVisibility((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) == 0
                ? INVISIBLE : VISIBLE);
        previousTrack.setVisibility((state.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) == 0
                ? INVISIBLE : VISIBLE);
    }

    private void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }
        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        seekAudio.setProgress((int) currentPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }
}
