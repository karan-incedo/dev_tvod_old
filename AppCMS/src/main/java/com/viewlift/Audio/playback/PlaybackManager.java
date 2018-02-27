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
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.media.MediaRouter;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.viewlift.Audio.AudioServiceHelper;
import com.viewlift.R;


/**
 * Manage the interactions among the container service, localplayback ,audio cast playback and the actual playback.
 */
public class PlaybackManager implements Playback.Callback {

    Activity mActivity;
    private Playback mPlayback;
    private PlaybackServiceCallback mServiceCallback;
    private MediaSessionCallback mMediaSessionCallback;
    public static String mCurrentMusicId;
    public static MediaMetadataCompat mCurrentMediaMetaData;
    Context mContext;
    LocalPlayback.MetadataUpdateListener mListener;

    private SessionManager mCastSessionManager;
    private SessionManagerListener<CastSession> mCastSessionManagerListener;
    private MediaRouter mMediaRouter;
    private static boolean isCastConnected = false;
    MediaSessionCompat msession;


    public PlaybackManager(PlaybackServiceCallback serviceCallback,
                           Playback playback, Context applicationContext, LocalPlayback.MetadataUpdateListener callBackLocalPlaybackListener, MediaSessionCompat mSession) {
        mServiceCallback = serviceCallback;
        mMediaSessionCallback = new MediaSessionCallback();
        this.mPlayback = playback;
        this.mListener = callBackLocalPlaybackListener;

        this.msession = mSession;
        mContext = applicationContext;
        mPlayback.setCallback(this);

        int playServicesAvailable =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext);
        if (playServicesAvailable == ConnectionResult.SUCCESS) {
            mCastSessionManager = CastContext.getSharedInstance(applicationContext).getSessionManager();
            mCastSessionManagerListener = new CastSessionManagerListener();
            mCastSessionManager.addSessionManagerListener(mCastSessionManagerListener,
                    CastSession.class);
        }
        mMediaRouter = MediaRouter.getInstance(mContext);

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
        mServiceCallback.onPlaybackStart();
        mServiceCallback.switchPlayback(currentPosition);
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
            mServiceCallback.onPlaybackPause();
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
        if (mPlayback.getCurrentStreamPosition() < mPlayback.getTotalDuration())
            AudioPlaylistHelper.getInstance().saveLastPlayPositionDetails(mPlayback.getCurrentId(), mPlayback.getCurrentStreamPosition());
        mPlayback.stop(true);
        mServiceCallback.onPlaybackStop();
        updatePlaybackState(withError);
        if (AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData() != null && AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData().getGist() != null) {
            AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData().getGist().setAudioPlaying(false);
        }
        AudioPlaylistHelper.getInstance().getAppCmsPresenter().notifyDownloadHasCompleted();

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
        int state = mPlayback.getState();

        // If there is an error message, send it to the playback state:
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
//            stateBuilder.setErrorMessage(error);
            state = PlaybackStateCompat.STATE_ERROR;
        }


        updatePlaybackStatus(state, position, error);

        if (mServiceCallback != null && state == PlaybackStateCompat.STATE_PLAYING ||
                state == PlaybackStateCompat.STATE_PAUSED) {
            mServiceCallback.onNotificationRequired();
        }
    }

    private void updatePlaybackStatus(int playBackState, long position, String error) {
        //noinspection ResourceType
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());
        if (error != null) {
            // Error states are really only supposed to be used for errors that cause playback to
            // stop unexpectedly and persist until the user takes action to fix it.
            stateBuilder.setErrorMessage(error);
            playBackState = PlaybackStateCompat.STATE_ERROR;
        }
        stateBuilder.setState(playBackState, position, 1.0f, SystemClock.elapsedRealtime());

        mServiceCallback.onPlaybackStateUpdated(stateBuilder.build());
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
            } else if (!AudioPlaylistHelper.getInstance().getAppCmsPresenter().isNetworkConnected()) {
                AudioPlaylistHelper.getInstance().getAppCmsPresenter().setAudioReload(true);
                onPlaybackStatusChanged(PlaybackStateCompat.STATE_PAUSED);
//                getPlayback().setCurrentId(AudioPlaylistHelper.getInstance().getNextItemId());
            } else {
                AudioPlaylistHelper.getInstance().autoPlayNextItemFromPLaylist(callBackPlaylistHelper);

            }
        }
    }

    @Override
    public void onCastCompletion() {
    }


    @Override
    public void onPlaybackStatusChanged(int state) {
        updatePlaybackState(null);
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

    public String getCurrentMediaId() {
        return mCurrentMusicId;
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


            boolean mediaHasChanged = !TextUtils.equals(mediaId, mPlayback.getCurrentId());
            if (mediaHasChanged || AudioPlaylistHelper.getInstance().getAppCmsPresenter().getAudioReload()) {
                long currentPosition = 0;
                if (extras != null) {
                    currentPosition = extras.getLong("CURRENT_POSITION");
                }
                setCurrentMediaId(mediaId);
                handlePlayRequest(currentPosition);
            }
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
            if (!AudioPlaylistHelper.getInstance().getAppCmsPresenter().isNetworkConnected()) {
                onPlaybackStatusChanged(PlaybackStateCompat.STATE_PAUSED);
                AudioPlaylistHelper.getInstance().getAppCmsPresenter().setAudioReload(true);

                Toast.makeText(mContext, mContext.getResources().getString(R.string.no_network_connectivity_message), Toast.LENGTH_SHORT).show();

            } else {
                AudioPlaylistHelper.getInstance().skipToNextItem(callBackPlaylistHelper);
            }
        }

        @Override
        public void onSkipToPrevious() {
            if (!AudioPlaylistHelper.getInstance().getAppCmsPresenter().isNetworkConnected()) {
                onPlaybackStatusChanged(PlaybackStateCompat.STATE_PAUSED);
                AudioPlaylistHelper.getInstance().getAppCmsPresenter().setAudioReload(true);

                Toast.makeText(mContext, mContext.getResources().getString(R.string.no_network_connectivity_message), Toast.LENGTH_SHORT).show();
            } else {
                AudioPlaylistHelper.getInstance().skipToPreviousItem(callBackPlaylistHelper);
            }
        }

        @Override
        public void onCustomAction(@NonNull String action, Bundle extras) {

        }

        @Override
        public void onPlayFromSearch(final String query, final Bundle extras) {

        }

        @Override
        public void onPrepare() {
//            AudioPlaylistHelper.getInstance().setDuration(mExoPlayer.getDuration());

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

        @Override
        public void updatePlayStateOnSkip() {
            if ((mPlayback instanceof LocalPlayback) && mPlayback.isPlaying()) {
                mPlayback.pause();
            }
            updatePlaybackStatus(PlaybackStateCompat.STATE_BUFFERING, 0, null);
        }

    };


    public void updatePlayback(Playback playback, boolean resumePlaying, long currentPosition) {
        this.mPlayback = playback;

        MediaMetadataCompat currentMusic = getCurrentMediaData();
        mPlayback.setCallback(this);
        if (currentMusic != null) {
            updatePlaybackStatus(PlaybackStateCompat.STATE_BUFFERING, currentPosition, null);
            mPlayback.setCallback(this);
            mServiceCallback.onPlaybackStart();
            mPlayback.play(currentMusic, currentPosition);
        }
    }


    /**
     * Switch to a different Playback instance, maintaining all playback state, if possible.
     *
     * @param playback switch to this playback
     */
    public void switchToPlayback(Playback playback, boolean resumePlaying) {
        if (playback == null) {
            throw new IllegalArgumentException("Playback cannot be null");
        }

        long pos = getPlayback().getCurrentStreamPosition();
        String currentMediaId = AudioPlaylistHelper.getInstance().getCurrentMediaId();
        AudioPlaylistHelper.getInstance().pausePlayback();
        this.mPlayback.stopPlayback(true);
        AudioPlaylistHelper.getInstance().setCurrentMediaId(currentMediaId);
        setCurrentMediaId(currentMediaId);
        updatePlaybackStatus(PlaybackStateCompat.STATE_BUFFERING, 0, null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PlaybackManager.this.mPlayback = playback;
                AudioPlaylistHelper.getInstance().playAudio(currentMediaId, pos);
            }
        }, 500);

    }

    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void switchPlayback(long currentPosition);

        void onNotificationRequired();

        void onPlaybackStop();

        void onPlaybackPause();

        void onPlaybackStateUpdated(PlaybackStateCompat newState);
    }

    /**
     * Session Manager Listener responsible for switching the Playback instances
     * depending on whether it is connected to a remote player.
     */
    private class CastSessionManagerListener implements SessionManagerListener<CastSession> {

        @Override
        public void onSessionEnded(CastSession session, int error) {
            System.out.println("Music Service on Session ended");
            boolean isAudioPlaying = AudioServiceHelper.getAudioInstance().isAudioPlaying();

            if (isCastConnected) {

                mMediaRouter.setMediaSessionCompat(null);
                if (isAudioPlaying) {
                    mMediaRouter.setMediaSessionCompat(null);
                    switchToPlayback(LocalPlayback.getInstance(mContext, mListener), false);
                }
                isCastConnected = false;
            }
        }

        @Override
        public void onSessionResumed(CastSession session, boolean wasSuspended) {

        }

        @Override
        public void onSessionStarted(CastSession session, String sessionId) {
            System.out.println("Music Service on Session started");
//            System.out.println("ram value in session-"+ramk);

            //in case if audio is playing than on cast session start play audio on casting device
            boolean isAudioPlaying = AudioServiceHelper.getAudioInstance().isAudioPlaying();
            // In case we are casting, send the device name as an extra on MediaSession metadata.
            if (!isCastConnected) {

//                playback = castPlayback;
                if (isAudioPlaying) {

                    // Now we can switch to AudioCastPlayback
                    AudioCastPlayback.getInstance(mContext, mListener).initRemoteClient();
                    mMediaRouter.setMediaSessionCompat(msession);

                    switchToPlayback(AudioCastPlayback.getInstance(mContext, mListener), true);
                }
                isCastConnected = true;
            }
        }

        @Override
        public void onSessionStarting(CastSession session) {
        }

        @Override
        public void onSessionStartFailed(CastSession session, int error) {
        }

        @Override
        public void onSessionEnding(CastSession session) {
            // This is our final chance to update the underlying stream position
            // In onSessionEnded(), the underlying AudioCastPlayback#mRemoteMediaClient
            // is disconnected and hence we update our local value of stream position
            // to the latest position.
//            mPlaybackManager.getPlayback().updateLastKnownStreamPosition();
        }

        @Override
        public void onSessionResuming(CastSession session, String sessionId) {
            System.out.println("on seesion resumed");
        }

        @Override
        public void onSessionResumeFailed(CastSession session, int error) {

        }

        @Override
        public void onSessionSuspended(CastSession session, int reason) {
        }
    }

}
