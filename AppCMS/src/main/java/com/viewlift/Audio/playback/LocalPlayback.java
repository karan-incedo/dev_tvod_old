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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.viewlift.Audio.MusicService;
import com.viewlift.Audio.model.MusicLibrary;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.beacon.BeaconBuffer;
import com.viewlift.models.data.appcms.beacon.BeaconPing;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.Date;

import static com.google.android.exoplayer2.C.CONTENT_TYPE_MUSIC;
import static com.google.android.exoplayer2.C.USAGE_MEDIA;

/**
 * A class that implements local media playback using {@link
 * com.google.android.exoplayer2.ExoPlayer}
 */
public final class LocalPlayback implements Playback {


    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    public static final float VOLUME_DUCK = 0.2f;
    // The volume we set the media player when we have audio focus.
    public static final float VOLUME_NORMAL = 1.0f;

    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private static final int AUDIO_FOCUSED = 2;

    private final Context mContext;
    private final WifiManager.WifiLock mWifiLock;
    private boolean mPlayOnFocusGain;
    private Callback mCallback;
    private boolean mAudioNoisyReceiverRegistered;
    private String mCurrentMediaId;
    AppCMSPresenter appCMSPresenter;
    private int mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
    private final AudioManager mAudioManager;
    private SimpleExoPlayer mExoPlayer;
    private final ExoPlayerEventListener mEventListener = new ExoPlayerEventListener();
    boolean isStreamStart, isStream25, isStream50, isStream75, isStream100;

    // Whether to return STATE_NONE or STATE_STOPPED when mExoPlayer is null;
    private boolean mExoPlayerNullIsStopped = false;
    private MetadataUpdateListener mListener;
    long mTotalAudioDuration;

    Handler mProgressHandler;
    Runnable mProgressRunnable;

    private final String FIREBASE_STREAM_START = "stream_start";
    private final String FIREBASE_STREAM_25 = "stream_25_pct";
    private final String FIREBASE_STREAM_50 = "stream_50_pct";
    private final String FIREBASE_STREAM_75 = "stream_75_pct";
    private final String FIREBASE_STREAM_100 = "stream_100_pct";

    private final String FIREBASE_AUDIO_ID_KEY = "video_id";
    private final String FIREBASE_AUDIO_NAME_KEY = "auieo_name";
    private final String FIREBASE_PLAYER_NAME_KEY = "player_name";
    private final String FIREBASE_MEDIA_TYPE_KEY = "media_type";
    private final String FIREBASE_PLAYER_NATIVE = "Native";
    private final String FIREBASE_PLAYER_CHROMECAST = "Chromecast";
    private final String FIREBASE_MEDIA_TYPE_VIDEO = "Audio";
    private final String FIREBASE_SCREEN_VIEW_EVENT = "screen_view";

    private final IntentFilter mAudioNoisyIntentFilter =
            new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private final BroadcastReceiver mAudioNoisyReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                        if (isPlaying()) {
                            Intent i = new Intent(context, MusicService.class);
                            i.setAction(MusicService.ACTION_CMD);
                            i.putExtra(MusicService.CMD_NAME, MusicService.CMD_PAUSE);
                            mContext.startService(i);
                        }
                    }
                }
            };

    public static LocalPlayback localPlaybackInstance;
    MediaMetadataCompat updatedMetaItem;
    private BeaconPing beaconPing;
    private long beaconMsgTimeoutMsec;
    private BeaconBuffer beaconBuffer;
    private long beaconBufferingTimeoutMsec;
    private boolean sentBeaconPlay;
    private boolean sentBeaconFirstFrame;
    private ContentDatum audioData;
    private long mStartBufferMilliSec = 0l;

    public static LocalPlayback getInstance(Context context, MetadataUpdateListener listener) {
        if (localPlaybackInstance == null) {

            synchronized (LocalPlayback.class) {

                if (localPlaybackInstance == null) {
                    localPlaybackInstance = new LocalPlayback(context, listener);
                }
            }
        }
        return localPlaybackInstance;
    }

    public LocalPlayback(Context context, MetadataUpdateListener listener) {
        Context applicationContext = context.getApplicationContext();
        System.out.println("LocalPlayback constructor");

        this.mContext = applicationContext;
        this.mListener = listener;

        this.mAudioManager =
                (AudioManager) applicationContext.getSystemService(Context.AUDIO_SERVICE);
        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        this.mWifiLock =
                ((WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE))
                        .createWifiLock(WifiManager.WIFI_MODE_FULL, "uAmp_lock");

        appCMSPresenter = AudioPlaylistHelper.getInstance().getAppCmsPresenter();
        if (appCMSPresenter != null && appCMSPresenter.getCurrentContext() != null) {
            beaconMsgTimeoutMsec = appCMSPresenter.getCurrentContext().getResources().getInteger(R.integer.app_cms_beacon_timeout_msec);
            beaconBufferingTimeoutMsec = appCMSPresenter.getCurrentContext().getResources().getInteger(R.integer.app_cms_beacon_buffering_timeout_msec);
        }
        beaconPing = new BeaconPing(beaconMsgTimeoutMsec,
                appCMSPresenter,
                null,
                null,
                false,
                null,
                null,
                null,
                null);

        beaconBuffer = new BeaconBuffer(beaconBufferingTimeoutMsec,
                appCMSPresenter,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    @Override
    public void start() {
        // Nothing to do
    }

    @Override
    public void stop(boolean notifyListeners) {

        mCurrentMediaId = null;
        giveUpAudioFocus();
        unregisterAudioNoisyReceiver();
        releaseResources(true);
        if (beaconPing != null) {
            beaconPing.sendBeaconPing = false;
            beaconPing.runBeaconPing = false;
            beaconPing = null;
        }

        if (beaconBuffer != null) {
            beaconBuffer.sendBeaconBuffering = false;
            beaconBuffer.runBeaconBuffering = false;
            beaconBuffer = null;
        }
        if (mProgressHandler != null) {
            isStreamStart = false;
            isStream25 = false;
            isStream50 = false;
            isStream75 = false;
            isStream100 = false;
            mProgressHandler.removeCallbacks(mProgressRunnable);
            mProgressHandler = null;
        }

    }

    @Override
    public void stopPlayback(boolean notifyListeners) {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
        mCurrentMediaId = null;
        if (mProgressHandler != null) {
            isStreamStart = false;
            isStream25 = false;
            isStream50 = false;
            isStream75 = false;
            isStream100 = false;
            mProgressHandler.removeCallbacks(mProgressRunnable);
            mProgressHandler = null;
        }
        giveUpAudioFocus();
        unregisterAudioNoisyReceiver();
        releaseResources(true);
    }

    @Override
    public void setState(int state) {
        // Nothing to do (mExoPlayer holds its own state).
    }

    @Override
    public int getState() {
        if (mExoPlayer == null) {
            return mExoPlayerNullIsStopped
                    ? PlaybackStateCompat.STATE_STOPPED
                    : PlaybackStateCompat.STATE_NONE;
        }
        switch (mExoPlayer.getPlaybackState()) {
            case ExoPlayer.STATE_IDLE:
                return PlaybackStateCompat.STATE_PAUSED;
            case ExoPlayer.STATE_BUFFERING:
                return PlaybackStateCompat.STATE_BUFFERING;
            case ExoPlayer.STATE_READY:
                return mExoPlayer.getPlayWhenReady()
                        ? PlaybackStateCompat.STATE_PLAYING
                        : PlaybackStateCompat.STATE_PAUSED;
            case ExoPlayer.STATE_ENDED:
                return PlaybackStateCompat.STATE_PAUSED;
            default:
                return PlaybackStateCompat.STATE_NONE;
        }
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isPlaying() {
        return mPlayOnFocusGain || (mExoPlayer != null && mExoPlayer.getPlayWhenReady());
    }

    @Override
    public long getCurrentStreamPosition() {
        return mExoPlayer != null ? mExoPlayer.getCurrentPosition() : 0;
    }

    @Override
    public long getTotalDuration() {
        return mExoPlayer != null ? mExoPlayer.getDuration() : 0;
    }

    @Override
    public void updateLastKnownStreamPosition() {
        // Nothing to do. Position maintained by ExoPlayer.
    }

    @Override
    public String getCurrentId() {
        return mCurrentMediaId;
    }

    @Override
    public void play(MediaMetadataCompat item, long currentPosition) {
        mPlayOnFocusGain = true;
        tryToGetAudioFocus();
        registerAudioNoisyReceiver();
        audioData = AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData();
        String mediaId = item.getDescription().getMediaId();
        boolean mediaHasChanged = !TextUtils.equals(mediaId, mCurrentMediaId);
        if (mediaHasChanged) {
            mCurrentMediaId = mediaId;
            AudioPlaylistHelper.getInstance().setCurrentMediaId(mCurrentMediaId);
            audioData = AudioPlaylistHelper.getInstance().getCurrentAudioPLayingData();
        }

        //if media has changed than load new audio url
        if (mediaHasChanged || mExoPlayer == null || currentPosition > 0) {

            mListener.onMetadataChanged(item);
            updatedMetaItem = item;

            releaseResources(false); // release everything except the player
            MediaMetadataCompat track = item;


            String source = track.getString(MusicLibrary.CUSTOM_METADATA_TRACK_SOURCE);
            if (source != null) {
                source = source.replaceAll(" ", "%20"); // Escape spaces for URLs
            }

            if (mExoPlayer == null) {
                this.mExoPlayer =
                        ExoPlayerFactory.newSimpleInstance(
                                mContext, new DefaultTrackSelector(), new DefaultLoadControl());
                this.mExoPlayer.addListener(mEventListener);
                localPlaybackInstance.mExoPlayer = mExoPlayer;
            }

            // Android "O" makes much greater use of AudioAttributes, especially
            // with regards to AudioFocus. All of UAMP's tracks are music, but
            // if your content includes spoken word such as audiobooks or podcasts
            // then the content type should be set to CONTENT_TYPE_SPEECH for those
            // tracks.
            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA)
                    .build();
            mExoPlayer.setAudioAttributes(audioAttributes);
            setUri(source);
            mExoPlayer.seekTo(currentPosition);
            // If we are streaming from the internet, we want to hold a
            // Wifi lock, which prevents the Wifi radio from going to
            // sleep while the song is playing.
            mWifiLock.acquire();
            if (mCallback != null) {
                mCallback.onPlaybackStatusChanged(getState());
            }
            setFirebaseProgressHandling();
        }

        configurePlayerState();
        mCallback.onPlaybackStatusChanged(getState());
    }

    private void setUri(String source) {
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(
                        mContext, Util.getUserAgent(mContext, "uamp"), null);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // The MediaSource represents the media to be played.
        MediaSource mediaSource =
                new ExtractorMediaSource(
                        Uri.parse(source), dataSourceFactory, extractorsFactory, null, null);

        // Prepares media to play (happens on background thread) and triggers
        // {@code onPlayerStateChanged} callback when the stream is ready to play.
        mExoPlayer.prepare(mediaSource);

    }

    @Override
    public void pause() {
        // Pause player and cancel the 'foreground service' state.
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
        // While paused, retain the player instance, but give up audio focus.
        releaseResources(false);
        unregisterAudioNoisyReceiver();
        if (beaconPing != null) {
            beaconPing.sendBeaconPing = false;
        }
        if (beaconBuffer != null) {
            beaconBuffer.sendBeaconBuffering = false;
        }
    }

    @Override
    public void seekTo(long position) {
        if (mExoPlayer != null) {
            registerAudioNoisyReceiver();
            mExoPlayer.seekTo(position);
        }
    }

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    private void tryToGetAudioFocus() {
        int result =
                mAudioManager.requestAudioFocus(
                        mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_FOCUSED;
        } else {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void giveUpAudioFocus() {
        if (mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    /**
     * Reconfigures the player according to audio focus settings and starts/restarts it. This method
     * starts/restarts the ExoPlayer instance respecting the current audio focus state. So if we
     * have focus, it will play normally; if we don't have focus, it will either leave the player
     * paused or set it to a low volume, depending on what is permitted by the current focus
     * settings.
     */
    private void configurePlayerState() {
        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            pause();
        } else {
            registerAudioNoisyReceiver();

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK) {
                // We're permitted to play, but only if we 'duck', ie: play softly
                mExoPlayer.setVolume(VOLUME_DUCK);
            } else {
                mExoPlayer.setVolume(VOLUME_NORMAL);
            }

            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                mExoPlayer.setPlayWhenReady(true);
                mPlayOnFocusGain = false;
            }
        }
    }

    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            mCurrentAudioFocusState = AUDIO_FOCUSED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            // Lost audio focus, but will gain it back (shortly), so note whether
                            // playback should resume
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            mPlayOnFocusGain = mExoPlayer != null && mExoPlayer.getPlayWhenReady();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            // Lost audio focus, probably "permanently"
                            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                            break;
                    }

                    if (mExoPlayer != null) {
                        // Update the player state based on the change
                        configurePlayerState();
                    }
                }
            };

    /**
     * Releases resources used by the service for playback, which is mostly just the WiFi lock for
     * local playback. If requested, the ExoPlayer instance is also released.
     *
     * @param releasePlayer Indicates whether the player should also be released
     */
    private void releaseResources(boolean releasePlayer) {

        // Stops and releases player (if requested and available).
        if (releasePlayer && mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer.removeListener(mEventListener);
            mExoPlayer = null;
            mExoPlayerNullIsStopped = true;
            mPlayOnFocusGain = false;
        }

        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }

    private void registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            mContext.registerReceiver(mAudioNoisyReceiver, mAudioNoisyIntentFilter);
            mAudioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            mContext.unregisterReceiver(mAudioNoisyReceiver);
            mAudioNoisyReceiverRegistered = false;
        }
    }

    private final class ExoPlayerEventListener implements ExoPlayer.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            // Nothing to do.
        }

        @Override
        public void onTracksChanged(
                TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // Nothing to do.
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Nothing to do.
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            setBeaconBufferValues();
            setBeaconPingValues();
            if (playbackState == ExoPlayer.STATE_READY && mExoPlayer != null) {
                long duration = mExoPlayer.getDuration();
                updatedMetaItem = new MediaMetadataCompat.Builder(updatedMetaItem)

                        // set high resolution bitmap in METADATA_KEY_ALBUM_ART. This is used, for
                        // example, on the lockscreen background when the media session is active.
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)

                        // set small version of the album art in the DISPLAY_ICON. This is used on
                        // the MediaDescription and thus it should be small to be serialized if
                        // necessary

                        .build();
                mListener.onMetadataChanged(updatedMetaItem);
            }

            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                case ExoPlayer.STATE_BUFFERING:
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(getState());

                        if (beaconBuffer != null) {
                            beaconBuffer.sendBeaconBuffering = true;
                            if (!beaconBuffer.isAlive()) {
                                beaconBuffer.start();
                            }
                        }

                    }
                    break;
                case ExoPlayer.STATE_READY:
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(getState());
                        if (beaconBuffer != null) {
                            beaconBuffer.sendBeaconBuffering = false;
                        }

                        if (beaconPing != null) {
                            beaconPing.sendBeaconPing = true;

                            if (!beaconPing.isAlive()) {
                                try {
                                    beaconPing.start();
                                } catch (Exception e) {

                                }
                            }
                            if (mExoPlayer != null) {
                                mTotalAudioDuration = getTotalDuration() / 1000;
                                mTotalAudioDuration -= mTotalAudioDuration % 4;

                            }

                            if (mProgressHandler != null)
                                mProgressHandler.post(mProgressRunnable);
                            if (!sentBeaconFirstFrame && appCMSPresenter != null) {
//                                mStopBufferMilliSec = new Date().getTime();
//                                ttfirstframe = mStartBufferMilliSec == 0l ? 0d : ((mStopBufferMilliSec - mStartBufferMilliSec) / 1000d);
                                appCMSPresenter.sendBeaconMessage(audioData.getAudioGist().getId(),
                                        audioData.getAudioGist().getPermalink(),
                                        null,
                                        mExoPlayer.getCurrentPosition(),
                                        false,
                                        AppCMSPresenter.BeaconEvent.FIRST_FRAME,
                                        audioData.getAudioGist().getMediaType(),
                                        null,
                                        null,
                                        null,
                                        getStreamId(),
                                        10,
                                        0,
                                        appCMSPresenter.isVideoDownloaded(audioData.getAudioGist().getId()));
                                sentBeaconFirstFrame = true;

                            }
                        }
                    }
                    break;
                case ExoPlayer.STATE_ENDED:
                    // The media player finished playing the current song.
                    if (mCallback != null) {
                        mCallback.onCompletion();
                    }
                    break;
                default:
                    if (!sentBeaconPlay && appCMSPresenter != null) {
                        appCMSPresenter.sendBeaconMessage(audioData.getAudioGist().getId(),
                                audioData.getAudioGist().getPermalink(),
                                null,
                                mExoPlayer.getCurrentPosition(),
                                false,
                                AppCMSPresenter.BeaconEvent.PLAY,
                                audioData.getAudioGist().getMediaType(),
                                null,
                                null,
                                null,
                                getStreamId(),
                                0d,
                                0,
                                appCMSPresenter.isVideoDownloaded(audioData.getAudioGist().getId()));
                        sentBeaconPlay = true;
                        mStartBufferMilliSec = new Date().getTime();
                    }
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            final String what;
            switch (error.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                    what = error.getSourceException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    what = error.getRendererException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                    what = error.getUnexpectedException().getMessage();
                    break;
                default:
                    what = "Unknown: " + error;
            }
            long mCurrentPlayerPosition = mExoPlayer.getCurrentPosition();
            MediaMetadataCompat track = AudioPlaylistHelper.getMetadata(mCurrentMediaId);
            System.out.println("Exo Player erroe" + error.type);
            AudioPlaylistHelper.getInstance().playAudio(mCurrentMediaId, mCurrentPlayerPosition);
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }


        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Nothing to do.
        }

        @Override
        public void onSeekProcessed() {

        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            // Nothing to do.
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        }

    }


    public interface MetadataUpdateListener {
        void onMetadataChanged(MediaMetadataCompat metadata);
    }

    String getStreamId() {
        String mStreamId = "";
        if (audioData != null && audioData.getAudioGist() != null && audioData.getAudioGist().getTitle() != null && appCMSPresenter != null) {
            try {
                mStreamId = appCMSPresenter.getStreamingId(audioData.getAudioGist().getTitle());
            } catch (Exception e) {
                //Log.e(TAG, e.getMessage());
                mStreamId = audioData.getAudioGist().getId() + appCMSPresenter.getCurrentTimeStamp();
            }
        }
        return mStreamId;
    }

    void setBeaconPingValues() {
        if (beaconPing == null) {
            beaconPing = new BeaconPing(beaconMsgTimeoutMsec,
                    appCMSPresenter,
                    null,
                    null,
                    false,
                    null,
                    null,
                    null,
                    null);
        }
        beaconPing.setFilmId(audioData.getAudioGist().getId());
        beaconPing.setPermaLink(audioData.getAudioGist().getPermalink());
        beaconPing.setStreamId(getStreamId());
        audioData.getAudioGist().setCastingConnected(false);
        audioData.getAudioGist().setCurrentPlayingPosition(getCurrentStreamPosition());
        beaconPing.setContentDatum(audioData);
    }

    void setBeaconBufferValues() {
        if (beaconBuffer == null) {
            beaconBuffer = new BeaconBuffer(beaconBufferingTimeoutMsec,
                    appCMSPresenter,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        }
        beaconBuffer.setFilmId(audioData.getAudioGist().getId());
        beaconBuffer.setPermaLink(audioData.getAudioGist().getPermalink());
        beaconBuffer.setStreamId(getStreamId());
        audioData.getAudioGist().setCastingConnected(false);
        audioData.getAudioGist().setCurrentPlayingPosition(getCurrentStreamPosition());
        beaconBuffer.setContentDatum(audioData);
    }


    public void setFirebaseProgressHandling() {
        isStreamStart = false;
        isStream25 = false;
        isStream50 = false;
        isStream75 = false;
        isStream100 = false;

        mProgressHandler = new Handler();
        mProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mProgressHandler != null && mProgressRunnable != null) {


                    if (mProgressHandler != null && mProgressRunnable != null) {
                        mProgressHandler.removeCallbacks(this);
                        long totalAudioDurationMod4 = mTotalAudioDuration / (4);
                        if (totalAudioDurationMod4 > 0 && mExoPlayer != null) {
                            long mPercentage = (long)
                                    (((float) (mExoPlayer.getCurrentPosition() / 1000) / mTotalAudioDuration) * 100);

                            if (appCMSPresenter.getmFireBaseAnalytics() != null) {
                                sendProgressAnalyticEvents(mPercentage);
                            }
                        }
                    }
                    mProgressHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    public void sendProgressAnalyticEvents(long progressPercent) {
        String title = "";
        if (audioData != null && audioData.getAudioGist() != null && audioData.getAudioGist().getTitle() != null && appCMSPresenter != null) {
            title = audioData.getAudioGist().getTitle();
        }
        Bundle bundle = new Bundle();
        bundle.putString(FIREBASE_AUDIO_ID_KEY, getCurrentId());
        bundle.putString(FIREBASE_AUDIO_NAME_KEY, title);
        bundle.putString(FIREBASE_PLAYER_NAME_KEY, FIREBASE_PLAYER_NATIVE);
        bundle.putString(FIREBASE_MEDIA_TYPE_KEY, FIREBASE_MEDIA_TYPE_VIDEO);
        //bundle.putString(FIREBASE_SERIES_ID_KEY, "");
        //bundle.putString(FIREBASE_SERIES_NAME_KEY, "");

        System.out.println("Audio Firebase-" + getCurrentId());
        //Logs an app event.
        if (progressPercent == 0 && !isStreamStart) {
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_START, bundle);
            isStreamStart = true;
        }

        if (!isStreamStart) {
            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_START, bundle);
            isStreamStart = true;
        }

        if (progressPercent >= 25 && progressPercent < 50 && !isStream25) {
            if (!isStreamStart) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_START, bundle);
                isStreamStart = true;
            }

            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_25, bundle);
            isStream25 = true;
        }

        if (progressPercent >= 50 && progressPercent < 75 && !isStream50) {
            if (!isStream25) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_25, bundle);
                isStream25 = true;
            }

            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_50, bundle);
            isStream50 = true;
        }

        if (progressPercent >= 75 && progressPercent <= 100 && !isStream75) {
            if (!isStream25) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_25, bundle);
                isStream25 = true;
            }

            if (!isStream50) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_50, bundle);
                isStream50 = true;
            }

            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_75, bundle);
            isStream75 = true;
        }

        if (progressPercent >= 98 && progressPercent <= 100 && !isStream100) {
            if (!isStream25) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_25, bundle);
                isStream25 = true;
            }

            if (!isStream50) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_50, bundle);
                isStream50 = true;
            }

            if (!isStream75) {
                appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_75, bundle);
                isStream75 = true;
            }

            appCMSPresenter.getmFireBaseAnalytics().logEvent(FIREBASE_STREAM_100, bundle);
            isStream100 = true;
        }
    }
}
