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

package com.viewlift.Audio.playback;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.SimpleExoPlayer;


/**
 * Manage the interactions among the container service, the queue manager and the actual playback.
 */
public class PlaybackManager implements Playback.Callback {

    Activity mActivity;
    private Resources mResources;
    private Playback mPlayback;
    private PlaybackServiceCallback mServiceCallback;
    private MediaSessionCallback mMediaSessionCallback;
    public static String mCurrentMusicId;
    public static MediaMetadataCompat mCurrentMediaMetaData;
    Context mContext;

    public PlaybackManager(PlaybackServiceCallback serviceCallback, Resources resources,
                           Playback playback, Context applicationContext) {
        mServiceCallback = serviceCallback;
        mResources = resources;
        mMediaSessionCallback = new MediaSessionCallback();
        mPlayback = playback;
        mContext = applicationContext;
        mPlayback.setCallback(this);
    }

    public Playback getPlayback() {
        return mPlayback;
    }

    public MediaSessionCompat.Callback getMediaSessionCallback() {
        return mMediaSessionCallback;
    }

    /**
     * Handle a request to play music
     *
     * @param currentPosition
     */
    public void handlePlayRequest(long currentPosition) {
        MediaMetadataCompat currentMusic = getCurrentMediaData();
        if (currentMusic != null) {

            mServiceCallback.onPlaybackStart();
            mPlayback.play(currentMusic, currentPosition);
        }
    }

    public void setActivity(Activity mAct) {
        mActivity = mAct;
    }

    /**
     * Handle a request to pause music
     */
    public void handlePauseRequest() {
        if (mPlayback.isPlaying()) {
            mPlayback.pause();
            mServiceCallback.onPlaybackStop();
        }
    }

    /**
     * Handle a request to stop music
     *
     * @param withError Error message in case the stop has an unexpected cause. The error
     *                  message will be set in the PlaybackState and will be visible to
     *                  MediaController clients.
     */
    public void handleStopRequest(String withError) {
        mPlayback.stop(true);
        mServiceCallback.onPlaybackStop();
        updatePlaybackState(withError);
        AudioPlaylistHelper.getInstance().setCurrentMediaId(null);
    }


    /**
     * Update the current media player state, optionally showing an error message.
     *
     * @param error if not null, error message to present to the user.
     */
    public void updatePlaybackState(String error) {
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (mPlayback != null && mPlayback.isConnected()) {
            position = mPlayback.getCurrentStreamPosition();
        }

        //noinspection ResourceType
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());

        int state = mPlayback.getState();

        // If there is an error message, send it to the playback state:
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(error);
            state = PlaybackStateCompat.STATE_ERROR;
        }
        //noinspection ResourceType
        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());

        mServiceCallback.onPlaybackStateUpdated(stateBuilder.build());

        if (state == PlaybackStateCompat.STATE_PLAYING ||
                state == PlaybackStateCompat.STATE_PAUSED) {
            mServiceCallback.onNotificationRequired();
        }
    }


    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        if (mPlayback.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY;
        }
        return actions;
    }

    /**
     * Implementation of the Playback.Callback interface
     */
    @Override
    public void onCompletion() {
        {
            if (AudioPlaylistHelper.getPlaylist().size() <= AudioPlaylistHelper.indexAudioFromPlaylist + 1) {
                handleStopRequest(null);
            } else {
                AudioPlaylistHelper.getInstance().autoPlayNextItemFromPLaylist(callBackPlaylistHelper);
            }

        }
    }

    SimpleExoPlayer mExoPlayer;

    @Override
    public void onPlaybackStatusChanged(int state, SimpleExoPlayer mExoPlayerInstance) {
        updatePlaybackState(null);
        mExoPlayer = mExoPlayerInstance;
        if (mExoPlayer != null) {
            AudioPlaylistHelper.getInstance().setDuration(mExoPlayer.getDuration());
        }
    }

    @Override
    public void onError(String error) {
        handleStopRequest(null);

        updatePlaybackState(error);
    }


    public static void setCurrentMediaData(MediaMetadataCompat mediaMetaData) {
        mCurrentMediaMetaData = mediaMetaData;
    }

    public MediaMetadataCompat getCurrentMediaData() {
        return mCurrentMediaMetaData;
    }

    public void setCurrentMediaId(String mediaId) {
        mCurrentMusicId = mediaId;
    }

    public MediaMetadataCompat getCurrentMediaId() {
        return mCurrentMediaMetaData;
    }


    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {

            handlePlayRequest(0);
        }

        @Override
        public void onSkipToQueueItem(long queueId) {

        }

        @Override
        public void onSeekTo(long position) {
            mPlayback.seekTo((int) position);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            long currentPosition = 0;
            if (extras != null) {
                currentPosition = extras.getLong("CURRENT_POSITION");
            }
            setCurrentMediaId(mediaId);
            handlePlayRequest(currentPosition);
        }

        @Override
        public void onPause() {
            handlePauseRequest();
        }

        @Override
        public void onStop() {
            handleStopRequest(null);
        }

        @Override
        public void onSkipToNext() {
            AudioPlaylistHelper.getInstance().skipToNextItem(callBackPlaylistHelper);
        }

        @Override
        public void onSkipToPrevious() {
            AudioPlaylistHelper.getInstance().skipToPreviousItem(callBackPlaylistHelper);

        }

        @Override
        public void onCustomAction(@NonNull String action, Bundle extras) {

        }

        @Override
        public void onPlayFromSearch(final String query, final Bundle extras) {

        }

        @Override
        public void onPrepare() {
            AudioPlaylistHelper.getInstance().setDuration(mExoPlayer.getDuration());

            super.onPrepare();
        }
    }


    private void playMediaData(String mediaId, long currentPosition) {
        setCurrentMediaId(mediaId);
        handlePlayRequest(currentPosition);
    }

    AudioPlaylistHelper.IPlaybackCall callBackPlaylistHelper = new AudioPlaylistHelper.IPlaybackCall() {
        @Override
        public void onPlaybackStart(MediaBrowserCompat.MediaItem item, long mCurrentPlayerPosition) {

            playMediaData(item.getMediaId(), mCurrentPlayerPosition);
        }

    };

    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onNotificationRequired();

        void onPlaybackStop();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);
    }

}
