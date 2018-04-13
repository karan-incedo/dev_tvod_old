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

package com.viewlift.Audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.viewlift.Audio.playback.AudioCastPlayback;
import com.viewlift.Audio.playback.AudioPlaylistHelper;
import com.viewlift.Audio.playback.LocalPlayback;
import com.viewlift.Audio.playback.Playback;
import com.viewlift.Audio.playback.PlaybackManager;
import com.viewlift.Audio.utils.CarHelper;
import com.viewlift.casting.CastServiceProvider;

import java.lang.ref.WeakReference;
import java.util.List;

//import static com.viewlift.Audio.utils.MediaIDHelper.MEDIA_ID_ROOT;


public class MusicService extends MediaBrowserServiceCompat implements
        PlaybackManager.PlaybackServiceCallback {
    String TAG = "Music Service";
    public static final String MEDIA_ID_ROOT = "__ROOT__";


    // Extra on MediaSession that contains the Cast device name currently connected to
    public static final String EXTRA_CONNECTED_CAST = "com.viewlift.Audio.CAST_NAME";
    // The action of the incoming Intent indicating that it contains a command
    // to be executed (see {@link #onStartCommand})
    public static final String ACTION_CMD = "com.viewlift.Audio.ACTION_CMD";
    // The key in the extras of the incoming Intent indicating the command that
    // should be executed (see {@link #onStartCommand})
    public static final String CMD_NAME = "CMD_NAME";
    // A value of a CMD_NAME key in the extras of the incoming Intent that
    // indicates that the music playback should be paused (see {@link #onStartCommand})
    public static final String CMD_PAUSE = "CMD_PAUSE";
    // A value of a CMD_NAME key that indicates that the music playback should switch
    // to local playback from cast playback.
    public static final String CMD_STOP_CASTING = "CMD_STOP_CASTING";
    // Delay stopSelf by using a handler.
    private static final int STOP_DELAY = 60 * 60 * 1000;

    private PlaybackManager mPlaybackManager;

    public MediaSessionCompat mSession;
    private MediaNotificationManager mMediaNotificationManager;
    private Bundle mSessionExtras;
    private final DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);
    private boolean mIsConnectedToCar;
    private BroadcastReceiver mCarConnectionReceiver;
    private static boolean isCastConnected = false;
    private MusicServiceReceiver serviceReceiver;
    LocalPlayback localPlayback;
    AudioCastPlayback castPlayback;
    Playback playback;
    ConnectivityManager connectivityManager;
    BroadcastReceiver networkConnectedReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // create localPlayback instance for playing audio on local device
        localPlayback = LocalPlayback.getInstance(MusicService.this, callBackPlaybackListener);

        // create castPlayback instance for playing audio on casting device
        castPlayback = AudioCastPlayback.getInstance(MusicService.this, callBackPlaybackListener);

        //switch playback instance to local or casting playback based on casting device status

        try {
            CastSession castSession = CastContext.getSharedInstance(getApplicationContext()).getSessionManager()
                    .getCurrentCastSession();
            if (castSession != null && castSession.isConnected()) {
                isCastConnected = true;
                playback = castPlayback;
            } else {
                isCastConnected = false;
                playback = localPlayback;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            isCastConnected = false;
            playback = localPlayback;

        }


        // Start a new MediaSession
        mSession = new MediaSessionCompat(this, "MusicService");

        // create mPlaybackManager instance to manage audio between different services
        mPlaybackManager = new PlaybackManager(this,
                playback, getApplicationContext(), callBackPlaybackListener, mSession);

        setSessionToken(mSession.getSessionToken());
        mSession.setCallback(mPlaybackManager.getMediaSessionCallback());

        mSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);


        mSessionExtras = new Bundle();
//        CarHelper.setSlotReservationFlags(mSessionExtras, true, true, true);
        mSession.setExtras(mSessionExtras);
        mPlaybackManager.updatePlaybackState(null);

        try {
            mMediaNotificationManager = new MediaNotificationManager(this);
        } catch (RemoteException e) {
            throw new IllegalStateException("Could not create a MediaNotificationManager", e);
        }
//        registerCarConnectionReceiver();
        serviceReceiver = new MusicServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioServiceHelper.APP_CMS_STOP_AUDIO_SERVICE_ACTION);
        try {
            registerReceiver(serviceReceiver, intentFilter);

        } catch (Exception e) {
            //
        }
        networkConnectedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (isConnected) {

                    mPlaybackManager.getPlayback().relaodAudioItem();
                }
            }
        };
        try {
            registerReceiver(networkConnectedReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception e) {
            //
        }
    }

    LocalPlayback.MetadataUpdateListener callBackPlaybackListener = new LocalPlayback.MetadataUpdateListener() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            mSession.setMetadata(metadata);
            updateMetaPlaylist();
            updateMetaPlayerPage();

        }
    };
    public void updateMetaPlaylist() {
        Intent intent = new Intent();
        intent.setAction(AudioServiceHelper.APP_CMS_DATA_UPDATE_ACTION);
        intent.putExtra(AudioServiceHelper.APP_CMS_DATA_UPDATE_MESSAGE, true);
        getApplicationContext().sendBroadcast(intent);
    }
    public void updateMetaPlayerPage() {
        Intent intent = new Intent();
        intent.setAction(AudioServiceHelper.APP_CMS_DATA_PLAYLIST_UPDATE_ACTION);
        intent.putExtra(AudioServiceHelper.APP_CMS_DATA_PLAYLIST_UPDATE_MESSAGE, true);
        getApplicationContext().sendBroadcast(intent);
    }
    /**
     * (non-Javadoc)A
     *
     * @see android.app.Service#onStartCommand(Intent, int, int)
     */

    @Override
    public int onStartCommand(Intent startIntent, int flags, int startId) {
        if (startIntent != null) {
            String action = startIntent.getAction();
            String command = startIntent.getStringExtra(CMD_NAME);
            if (ACTION_CMD.equals(action)) {
                if (CMD_PAUSE.equals(command)) {
                    mPlaybackManager.handlePauseRequest();
                } else if (CMD_STOP_CASTING.equals(command)) {
                    CastContext.getSharedInstance(this).getSessionManager().endCurrentSession(true);
                }
            } else {
                // Try to handle the intent as a media button event wrapped by MediaButtonReceiver
                MediaButtonReceiver.handleIntent(mSession, startIntent);
            }
        }
        // Reset the delay handler to enqueue a message to stop the service if
        // nothing is playing.
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        return START_STICKY;
    }

    /*
     * Handle case when user swipes the app away from the recents apps list by
     * stopping the service (and any ongoing playback).
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        System.out.println("TAsk Stopped stop on task removed");
        mPlaybackManager.saveLastPositionAudioOnForcefullyStop();
        RemoteMediaClient mRemoteMediaClient = null;
        boolean isAudioPlaying = AudioServiceHelper.getAudioInstance().isAudioPlaying();

        try {
            CastSession castSession = CastContext.getSharedInstance(getApplicationContext()).getSessionManager()
                    .getCurrentCastSession();
            if (castSession != null && castSession.isConnected()) {
                mRemoteMediaClient = castSession.getRemoteMediaClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //if audio is casting to remote media client than dont stop the service
        if (!(mRemoteMediaClient != null && mRemoteMediaClient.hasMediaSession() && isAudioPlaying)) {
            stopSelf();
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {

        try {
//            unregisterCarConnectionReceiver();
            // Service is being killed, so make sure we release our resources
            mPlaybackManager.handleStopRequest(null);
            mMediaNotificationManager.stopNotification();
            mDelayedStopHandler.removeCallbacksAndMessages(null);
            mSession.release();
            LocalPlayback.localPlaybackInstance = null;
            AudioCastPlayback.castPlaybackInstance = null;
            unregisterReceiver(serviceReceiver);
            unregisterReceiver(networkConnectedReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid,
                                 Bundle rootHints) {

        return new BrowserRoot(MEDIA_ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull final String parentMediaId,
                               @NonNull final Result<List<MediaItem>> result) {

        result.sendResult(null);

    }

    /**
     * Callback method called from PlaybackManager whenever the music is about to play.
     */
    @Override
    public void onPlaybackStart() {
        mSession.setActive(true);

        mDelayedStopHandler.removeCallbacksAndMessages(null);

        // The service needs to continue running even after the bound client (usually a
        // MediaController) disconnects, otherwise the music playback will stop.
        // Calling startService(Intent) will keep the service running until it is explicitly killed.
        startService(new Intent(getApplicationContext(), MusicService.class));
    }

    @Override
    public void switchPlayback(long currentPosition) {
        if (!CastServiceProvider.getInstance(getApplicationContext()).isCastingConnected()) {
            mPlaybackManager.updatePlayback(localPlayback, true, currentPosition);
        } else {
            castPlayback.initRemoteClient();
            mPlaybackManager.updatePlayback(castPlayback, true, currentPosition);
        }
    }


    /**
     * Callback method called from PlaybackManager whenever the music stops playing.
     */
    @Override
    public void onPlaybackStop() {
        System.out.println("TAsk Stopped stop on playbackstop");

        mSession.setActive(false);
        // Reset the delayed stop handler, so after STOP_DELAY it will be executed again,
        // potentially stopping the service.
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        stopForeground(true);
    }

    @Override
    public void onPlaybackPause() {
        // Reset the delayed stop handler, so after STOP_DELAY it will be executed again,
        // potentially stopping the service.
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        stopForeground(true);
    }

    @Override
    public void onNotificationRequired() {
        if (mMediaNotificationManager != null) {
            mMediaNotificationManager.startNotification();
        }
    }

    @Override
    public void stopNotification() {
        if (mMediaNotificationManager != null) {
            mMediaNotificationManager.stopNotification();
        }
    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {
        mSession.setPlaybackState(newState);
    }

    private void registerCarConnectionReceiver() {
        IntentFilter filter = new IntentFilter(CarHelper.ACTION_MEDIA_STATUS);
        mCarConnectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String connectionEvent = intent.getStringExtra(CarHelper.MEDIA_CONNECTION_STATUS);
                mIsConnectedToCar = CarHelper.MEDIA_CONNECTED.equals(connectionEvent);
            }
        };
        registerReceiver(mCarConnectionReceiver, filter);
    }

    private void unregisterCarConnectionReceiver() {
        unregisterReceiver(mCarConnectionReceiver);
    }

    /**
     * A simple handler that stops the service if playback is not active (playing)
     */
    private static class DelayedStopHandler extends Handler {
        private final WeakReference<MusicService> mWeakReference;

        private DelayedStopHandler(MusicService service) {
            mWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicService service = mWeakReference.get();
            if (service != null && service.mPlaybackManager.getPlayback() != null) {
                if (service.mPlaybackManager.getPlayback().isPlaying()) {
                    return;
                }
                service.stopSelf();
            }
        }
    }

    private class MusicServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            System.out.println("TAsk Stopped stop on receiver");

            if (arg1 != null && arg1.hasExtra(AudioServiceHelper.APP_CMS_STOP_AUDIO_SERVICE_MESSAGE)) {

                mPlaybackManager.handleStopRequest(null);
                mPlaybackManager.setCurrentMediaId(null);
                AudioServiceHelper.getAudioInstance().changeMiniControllerVisiblity(true);
                stopSelf();

            }
        }
    }


}
