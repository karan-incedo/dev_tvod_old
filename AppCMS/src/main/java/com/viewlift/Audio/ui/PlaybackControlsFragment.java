/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viewlift.Audio.ui;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.viewlift.Audio.AudioServiceHelper;
import com.viewlift.Audio.MusicService;
import com.viewlift.Audio.playback.AudioPlaylistHelper;
import com.viewlift.R;
import com.viewlift.casting.CastHelper;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.activity.AppCMSPlayAudioActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * A class that shows the Media Queue to the user.
 */
public class PlaybackControlsFragment extends Fragment {


    private ImageButton mPlayPause;
    private TextView mTitle, extra_info;
    private SeekBar seek_audio;

    private String mArtUrl;
    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();
    private final Handler mHandler = new Handler();
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION =
            "CURRENT_MEDIA_DESCRIPTION";
    private PlaybackStateCompat mLastPlaybackState;

    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    ProgressBar progressBarPlayPause;
    int currentProgess = 0;
    public MediaBrowserCompat mMediaBrowser;

    private ScheduledFuture<?> mScheduleFuture;
    // Receive callbacks from the MediaController. Here we update our state such as which queue
    // is being shown, the current title and description and the PlaybackState.
    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            PlaybackControlsFragment.this.onPlaybackStateChanged(state);
            updatePlaybackState(state);
            System.out.println("update playback state in playbackcontrol" + state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata == null) {
                return;
            }
            currentProgess=0;
            updateDuration(metadata);
            PlaybackControlsFragment.this.onMetadataChanged(metadata);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);
        mMediaBrowser = new MediaBrowserCompat(getActivity(),
                new ComponentName(getActivity(), MusicService.class), mConnectionCallback, null);

        mMediaBrowser.connect();
        mPlayPause = rootView.findViewById(R.id.play_pause);
        seek_audio = rootView.findViewById(R.id.seek_audio);

        progressBarPlayPause = rootView.findViewById(R.id.progressBarPlayPause);
        mPlayPause.setEnabled(true);
        seek_audio.setEnabled(false);
        seek_audio.setClickable(false);
        mPlayPause.setOnClickListener(mButtonListener);
        extra_info = rootView.findViewById(R.id.extra_info);

        mTitle = rootView.findViewById(R.id.title);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAudioPlayer();
            }
        });

        scheduleSeekbarUpdate();
        return rootView;
    }

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

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(getActivity(), token);
        MediaControllerCompat.setMediaController(getActivity(), mediaController);
        onConnected();

    }

    private void launchAudioPlayer() {
        Intent intent = new Intent(getActivity(), AppCMSPlayAudioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
        MediaMetadataCompat metadata = controller.getMetadata();
        if (metadata != null) {
            intent.putExtra(EXTRA_CURRENT_MEDIA_DESCRIPTION,
                    metadata);
        }
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

    }

    @Override
    public void onStart() {
        super.onStart();
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
        if (controller != null) {
            onConnected();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
        if (controller != null) {
            PlaybackStateCompat state = MediaControllerCompat.getMediaController(getActivity()).getPlaybackState();
            updatePlaybackState(state);
            updateDuration(controller.getMetadata());

        }
        updateCastInfo();
        audioPreview(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
        if (controller != null) {
            controller.unregisterCallback(mCallback);
        }
    }

    public void onConnected() {
        if (getActivity() != null) {
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
            if (controller != null) {
                PlaybackStateCompat state = MediaControllerCompat.getMediaController(getActivity()).getPlaybackState();
                updatePlaybackState(state);
                updateDuration(controller.getMetadata());
                onMetadataChanged(controller.getMetadata());
                onPlaybackStateChanged(controller.getPlaybackState());
                controller.registerCallback(mCallback);
            }

            scheduleSeekbarUpdate();
        }
    }

    private void onMetadataChanged(MediaMetadataCompat metadata) {
        if (getActivity() == null) {
            return;
        }
        if (metadata == null) {
            return;
        }

        updateDuration(metadata);
        mTitle.setText(metadata.getDescription().getTitle());
        checkSubscription(metadata);
    }

    public void checkSubscription(MediaMetadataCompat metadata) {
        if (getActivity() != null) {
            audioPreview(metadata);
        }
    }

    void audioPreview(MediaMetadataCompat metadata) {
        if (isPreviewEnded(metadata)) {
            stopSeekbarUpdate();
            mTitle.setText(getActivity().getResources().getString(R.string.preview_ended));
            mPlayPause.setBackground(getActivity().getDrawable(R.drawable.audio_preview_end_icon));

        }
    }

    private boolean isPreviewEnded(MediaMetadataCompat metadataAudio) {
        boolean showPreview = false;
        MediaMetadataCompat metadata = null;
        if (metadataAudio == null && getActivity() != null
                && MediaControllerCompat.getMediaController(getActivity()) != null
                && MediaControllerCompat.getMediaController(getActivity()).getTransportControls() != null) {
            metadata = MediaControllerCompat.getMediaController(getActivity()).getMetadata();

        } else {
            metadata = metadataAudio;
        }
        AppCMSPresenter appCMSPresenter = AudioPlaylistHelper.getInstance().getAppCmsPresenter();
        String isFree = "true";
        if (metadata != null) {
            isFree = (String) metadata.getText(AudioPlaylistHelper.CUSTOM_METADATA_IS_FREE);
            if (((appCMSPresenter.isUserSubscribed()) && appCMSPresenter.isUserLoggedIn()) || Boolean.valueOf(isFree)) {
                showPreview = false;
            } else {
                if (appCMSPresenter != null && appCMSPresenter.getAppCMSMain() != null
                        && appCMSPresenter.getAppCMSMain().getFeatures() != null
                        && appCMSPresenter.getAppCMSMain().getFeatures().getAudioPreview() != null) {

                    if (appCMSPresenter.getAppCMSMain().getFeatures().getAudioPreview().isAudioPreview()) {
                        if (currentProgess >= Integer.parseInt(appCMSPresenter.getAppCMSMain().getFeatures().getAudioPreview().getLength().getMultiplier())) {
                            showPreview = true;
                        }
                    }
                }
            }
        }
        return showPreview;
    }

    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        if (getActivity() == null) {
            //(TAG, "onPlaybackStateChanged called when getActivity null," +
            //      "this should not happen if the callback was properly unregistered. Ignoring.");
            return;
        }
        if (state == null) {
            return;
        }


        boolean enablePlay = false;
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PAUSED:
            case PlaybackStateCompat.STATE_STOPPED:
                enablePlay = true;
                break;
            case PlaybackStateCompat.STATE_ERROR:
                Toast.makeText(getActivity(), state.getErrorMessage(), Toast.LENGTH_LONG).show();
                break;
        }

        if (enablePlay) {
            mPlayPause.setBackground(getActivity().getDrawable(R.drawable.play_track_white));
        } else {
            mPlayPause.setBackground(getActivity().getDrawable(R.drawable.pause_track_white));
        }
        audioPreview(null);
    }

    private void updateCastInfo() {
        if (getActivity() != null && getActivity().getApplicationContext() != null && CastHelper.getInstance(getActivity().getApplicationContext()).getDeviceName() != null && !TextUtils.isEmpty(CastHelper.getInstance(getActivity().getApplicationContext()).getDeviceName())) {
            String castName = CastHelper.getInstance(getActivity().getApplicationContext()).getDeviceName();
            String line3Text = castName == null ? "" : getResources()
                    .getString(R.string.casting_to_device, castName);
            extra_info.setText(line3Text);
            extra_info.setVisibility(View.VISIBLE);
        } else {
            extra_info.setVisibility(View.GONE);
        }
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        mLastPlaybackState = state;

        updateCastInfo();
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mPlayPause.setVisibility(VISIBLE);
                progressBarPlayPause.setVisibility(GONE);
                scheduleSeekbarUpdate();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mPlayPause.setVisibility(VISIBLE);
                progressBarPlayPause.setVisibility(GONE);
                updateProgress();
                stopSeekbarUpdate();

                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                mPlayPause.setVisibility(VISIBLE);
                progressBarPlayPause.setVisibility(GONE);
                stopSeekbarUpdate();

                break;
            case PlaybackStateCompat.STATE_BUFFERING:
                mPlayPause.setVisibility(INVISIBLE);
                progressBarPlayPause.setVisibility(VISIBLE);
                stopSeekbarUpdate();

                break;
            default:
        }


    }


    private final View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
            PlaybackStateCompat stateObj = controller.getPlaybackState();
            final int state = stateObj == null ?
                    PlaybackStateCompat.STATE_NONE : stateObj.getState();
            switch (v.getId()) {
                case R.id.play_pause:
                    if (state == PlaybackStateCompat.STATE_PAUSED ||
                            state == PlaybackStateCompat.STATE_STOPPED ||
                            state == PlaybackStateCompat.STATE_NONE) {
                        AppCMSPresenter appCMSPresenter = AudioPlaylistHelper.getInstance().getAppCmsPresenter();
                        MediaMetadataCompat metadata = controller.getMetadata();

                        if (!isPreviewEnded(metadata)) {
                            playMedia();
                        } else {
                            launchAudioPlayer();
                        }
                    } else if (state == PlaybackStateCompat.STATE_PLAYING ||
                            state == PlaybackStateCompat.STATE_BUFFERING ||
                            state == PlaybackStateCompat.STATE_CONNECTING) {
                        pauseMedia();
                    }
                    break;
            }
        }
    };

    private void playMedia() {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
        if (controller != null) {
            controller.getTransportControls().play();
        }
    }

    private void pauseMedia() {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
        if (controller != null) {
            controller.getTransportControls().pause();
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

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
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

        if (seek_audio != null)
            seek_audio.setProgress((int) currentPosition);
        currentProgess = (int) (currentPosition / 1000);
        audioPreview(null);

    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        long duration = 0;
        if (duration != metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)) {
            if (metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) > 0) {
                duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            }
            seek_audio.setMax((int) duration);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

}
