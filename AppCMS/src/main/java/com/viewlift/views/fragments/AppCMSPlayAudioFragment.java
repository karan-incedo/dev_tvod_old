package com.viewlift.views.fragments;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.viewlift.AppCMSApplication;
import com.viewlift.Audio.MusicService;
import com.viewlift.Audio.playback.AudioPlaylistHelper;
import com.viewlift.Audio.ui.PlaybackControlsFragment;
import com.viewlift.R;
import com.viewlift.casting.CastHelper;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.ViewCreator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
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
    @BindView(R.id.extra_info)
    TextView extra_info;

    @BindView(R.id.progressBarLoading)
    ProgressBar progressBarLoading;


    @BindView(R.id.progressBarPlayPause)
    ProgressBar progressBarPlayPause;

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private final Handler mHandler = new Handler();
    private MediaBrowserCompat mMediaBrowser;
    private Drawable mPauseDrawable;
    private Drawable mPlayDrawable;
    private String mCurrentArtUrl;
    private OnUpdateMetaChange onUpdateMetaChange;

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

    private AppCMSPresenter appCMSPresenter;

    public static AppCMSPlayAudioFragment newInstance(Context context) {
        AppCMSPlayAudioFragment appCMSPlayAudioFragment = new AppCMSPlayAudioFragment();
        Bundle args = new Bundle();

        appCMSPlayAudioFragment.setArguments(args);


        return appCMSPlayAudioFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnUpdateMetaChange) {
            onUpdateMetaChange = (OnUpdateMetaChange) context;
        }

    }

    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            System.out.println("update playback state in fullscreen" + state);
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                updateMediaDescription(metadata);
                updateDuration(metadata);
                onUpdateMetaChange.updateMetaData(metadata);
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

        appCMSPresenter = ((AppCMSApplication) getActivity().getApplication()).
                getAppCMSPresenterComponent().appCMSPresenter();
        if (!BaseView.isTablet(getActivity())) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
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
        setProgress();
        updataeShuffleState();
        getActivity().registerReceiver(mMessageReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_AUDIO_LOADING_ACTION));

        getActivity().registerReceiver(mMessageReceiver,
                new IntentFilter(AppCMSPresenter.PRESENTER_AUDIO_LOADING_STOP_ACTION));
        return rootView;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AppCMSPresenter.PRESENTER_AUDIO_LOADING_ACTION)) {
                progressBarPlayPause.setVisibility(VISIBLE);
                playPauseTrack.setVisibility(GONE);
            }
            if (intent.getAction().equals(AppCMSPresenter.PRESENTER_AUDIO_LOADING_STOP_ACTION)) {
                progressBarPlayPause.setVisibility(INVISIBLE);
                playPauseTrack.setVisibility(VISIBLE);


            }
            // Get extra data included in the Intent

        }
    };

    private void setProgress() {
        if (progressBarLoading != null) {
            try {
                progressBarLoading.getIndeterminateDrawable().setTint(Color.parseColor(appCMSPresenter.getAppCMSMain()
                        .getBrand().getCta().getPrimary().getBackgroundColor()));
            } catch (Exception e) {
//                //Log.w(TAG, "Failed to set color for loader: " + e.getMessage());
                progressBarLoading.getIndeterminateDrawable().setTint(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            }
        }
    }

    private void updataeShuffleState() {
        if (appCMSPresenter.getAudioShuffledPreference()) {
            int tintColor = Color.parseColor(ViewCreator.getColor(getActivity(),
                    appCMSPresenter.getAppCMSMain().getBrand().getCta().getPrimary().getBackgroundColor()));
            applyTintToDrawable(shuffle.getDrawable(), tintColor);
        } else {

            int tintColor = (getActivity().getResources().getColor(android.R.color.darker_gray));
            applyTintToDrawable(shuffle.getDrawable(), tintColor);
        }
    }

    private void applyTintToDrawable(@Nullable Drawable drawable, int color) {
        if (drawable != null) {
            drawable.setTint(color);
            drawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == shuffle) {
            if (appCMSPresenter.getAudioShuffledPreference()) {
                appCMSPresenter.setAudioShuffledPreference(false);
                AudioPlaylistHelper.getInstance().undoShufflePlaylist();
            } else {
                appCMSPresenter.setAudioShuffledPreference(true);
                AudioPlaylistHelper.getInstance().doShufflePlaylist();
            }
            updataeShuffleState();
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
            if (AudioPlaylistHelper.getInstance().getCurrentPlaylistData() != null) {
                appCMSPresenter.navigatePlayListPageWithPreLoadData(AudioPlaylistHelper.getInstance().getCurrentPlaylistData());
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), getString(R.string.not_item_available_inqueue), Toast.LENGTH_SHORT).show();
            }
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
            updateMediaDescription(metadata);
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
            MediaMetadataCompat description = intent.getParcelableExtra(
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
        getActivity().unregisterReceiver(mMessageReceiver);
    }

    private void fetchImageAsync(@NonNull MediaDescriptionCompat description) {
        if (description.getIconUri() == null) {
            return;
        }
        String artUrl = description.getIconUri().toString();
        mCurrentArtUrl = artUrl;


        if(getActivity()!=null) {
            Glide.with(getActivity())
                    .load(mCurrentArtUrl).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.logo)
                    .into(trackImage);
        }

    }

    private void updateMediaDescription(MediaMetadataCompat metaData) {
        if (metaData == null) {
            return;
        }
        trackName.setText(metaData.getDescription().getTitle());
        artistName.setText(metaData.getText(MediaMetadataCompat.METADATA_KEY_ARTIST));
        String albumInfo=metaData.getText(AudioPlaylistHelper.CUSTOM_METADATA_TRACK_ALBUM_YEAR)+" | "+metaData.getText(MediaMetadataCompat.METADATA_KEY_ALBUM);
        artistName.setText(metaData.getText(MediaMetadataCompat.METADATA_KEY_ARTIST));
        trackYear.setText(albumInfo);

        fetchImageAsync(metaData.getDescription());
    }

    long duration = 0;

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        if (duration != metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)) {
            if (metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) > 0) {
                duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                seekAudio.setMax((int) duration);
                seekAudio.setProgress(0);
                trackEndTime.setText(DateUtils.formatElapsedTime(duration / 1000));
            }
        }
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        mLastPlaybackState = state;
        if (CastHelper.getInstance(getActivity().getApplicationContext()).getDeviceName() != null && !TextUtils.isEmpty(CastHelper.getInstance(getActivity().getApplicationContext()).getDeviceName())) {
            String castName = CastHelper.getInstance(getActivity().getApplicationContext()).getDeviceName();
            String line3Text = castName == null ? "" : getResources()
                    .getString(R.string.casting_to_device, castName);
            extra_info.setText(line3Text);
            extra_info.setVisibility(View.VISIBLE);
        } else {
            extra_info.setVisibility(View.GONE);
        }
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
//                progressBarLoading.setVisibility(INVISIBLE);
                playPauseTrack.setVisibility(VISIBLE);
                playPauseTrack.setBackground(mPauseDrawable);
                progressBarPlayPause.setVisibility(GONE);
                scheduleSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
//                mControllers.setVisibility(VISIBLE);
//                progressBarLoading.setVisibility(INVISIBLE);
                playPauseTrack.setVisibility(VISIBLE);
                playPauseTrack.setBackground(mPlayDrawable);
                progressBarPlayPause.setVisibility(GONE);

                stopSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
//                progressBarLoading.setVisibility(INVISIBLE);
                playPauseTrack.setVisibility(VISIBLE);
                playPauseTrack.setBackground(mPlayDrawable);
                progressBarPlayPause.setVisibility(GONE);
                stopSeekbarUpdate();
                getActivity().finish();
                break;

            case PlaybackStateCompat.STATE_BUFFERING:
                playPauseTrack.setVisibility(INVISIBLE);
//                progressBarLoading.setVisibility(VISIBLE);
                progressBarPlayPause.setVisibility(View.VISIBLE);
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

    public interface OnUpdateMetaChange {
        void updateMetaData(MediaMetadataCompat metadata);
    }

}
