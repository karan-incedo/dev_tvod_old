
package com.viewlift.Audio.playback;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.viewlift.Audio.AudioServiceHelper;
import com.viewlift.casting.CastingUtils;
import com.viewlift.presenters.AppCMSPresenter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An implementation of Playback that talks to Cast.
 */
public class AudioCastPlayback implements Playback {

    private static final String TAG = "cast playback";

    private static final String MIME_TYPE_AUDIO_MPEG = "audio/mpeg";
    private static final String ITEM_ID = "itemId";

    private final Context mAppContext;
    private RemoteMediaClient mRemoteMediaClient;

    private int mPlaybackState;
    private RemoteMediaClient.ProgressListener progressListener;
    private boolean isStatusUpdateedForCurrentItem = false;

    /**
     * Playback interface Callbacks
     */
    private Callback mCallback;
    private long mCurrentPosition;
    private String mCurrentMediaId;
    CastMediaClientListener mRemoteMediaClientListener;
    private LocalPlayback.MetadataUpdateListener mListener;
    public static AudioCastPlayback castPlaybackInstance;
    MediaMetadataCompat updatedMetaItem;

    public static synchronized AudioCastPlayback getInstance(Context context, LocalPlayback.MetadataUpdateListener listener) {
        if (castPlaybackInstance == null) {
            synchronized (AudioCastPlayback.class) {
                if (castPlaybackInstance == null) {
                    castPlaybackInstance = new AudioCastPlayback(context, listener);
                }
            }
        }
        return castPlaybackInstance;
    }

    public AudioCastPlayback(Context context, LocalPlayback.MetadataUpdateListener callBackLocalPlaybackListener) {
        System.out.println("AudioCastPlayback constructor");
        mAppContext = context.getApplicationContext();
        this.mListener = callBackLocalPlaybackListener;

        initRemoteClient();
        mRemoteMediaClientListener = new CastMediaClientListener();
        initProgressListeners();
    }

    public void initRemoteClient() {
        CastSession castSession = CastContext.getSharedInstance(mAppContext).getSessionManager()
                .getCurrentCastSession();
        if (castSession != null && castSession.isConnected() ) {
            mRemoteMediaClient = castSession.getRemoteMediaClient();
        }
    }

    @Override
    public void start() {
        initRemoteClient();
        mRemoteMediaClient.addListener(mRemoteMediaClientListener);
    }

    @Override
    public void stop(boolean notifyListeners) {
        if (mRemoteMediaClient != null) {
            mRemoteMediaClient.removeListener(mRemoteMediaClientListener);
            mRemoteMediaClient.removeProgressListener(progressListener);
        }
        mPlaybackState = PlaybackStateCompat.STATE_STOPPED;
        if (notifyListeners && mCallback != null) {
            mCallback.onPlaybackStatusChanged(mPlaybackState);
        }
    }

    @Override
    public void stopPlayback(boolean notifyListeners) {
        if (mRemoteMediaClient != null) {

            mRemoteMediaClient.removeListener(mRemoteMediaClientListener);
            mRemoteMediaClient.removeProgressListener(progressListener);
        }
    }

    @Override
    public void setState(int state) {
        this.mPlaybackState = state;
    }

    @Override
    public long getCurrentStreamPosition() {
        return mCurrentPosition;
    }

    @Override
    public long getTotalDuration() {
        return 0;
    }

    @Override
    public void updateLastKnownStreamPosition() {
//        mCurrentPosition = getCurrentStreamPosition();
    }


    @Override
    public void play(MediaMetadataCompat item, long currentPosition) {
        try {
            {
                initRemoteClient();
                String mediaId = item.getDescription().getMediaId();
                boolean mediaHasChanged = !TextUtils.equals(mediaId, mCurrentMediaId);
                if (mediaHasChanged) {
                    mCurrentMediaId = mediaId;
                    AudioPlaylistHelper.getInstance().setCurrentMediaId(mCurrentMediaId);
                }
                updatedMetaItem = item;
                if (!mediaHasChanged && mRemoteMediaClient != null && mRemoteMediaClient.isPaused()) {
                    mRemoteMediaClient.play();
                } else {
                    mCurrentPosition = currentPosition;
                    loadMedia(item.getDescription().getMediaId(), true, currentPosition);
                    mPlaybackState = PlaybackStateCompat.STATE_BUFFERING;
                    mListener.onMetadataChanged(item);
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(mPlaybackState);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Exception loading media ");

            if (mCallback != null) {
                mCallback.onError(e.getMessage());
            }
        }
    }


    @Override
    public void pause() {
        try {
            if (mRemoteMediaClient != null && mRemoteMediaClient.hasMediaSession()) {
                mRemoteMediaClient.pause();
                mCurrentPosition = (int) mRemoteMediaClient.getApproximateStreamPosition();
            } else {
                loadMedia(mCurrentMediaId, false, mCurrentPosition);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception pausing cast playback");
            if (mCallback != null) {
                mCallback.onError(e.getMessage());
            }
        }
    }

    @Override
    public void seekTo(long position) {
        if (mCurrentMediaId == null) {
            mCurrentPosition = position;
            return;
        }
        try {
            if (mRemoteMediaClient != null && mRemoteMediaClient.hasMediaSession()) {
                mRemoteMediaClient.seek(position);
                mCurrentPosition = position;
            } else {
                mCurrentPosition = position;
                loadMedia(mCurrentMediaId, false, mCurrentPosition);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception pausing cast playback");
            if (mCallback != null) {
                mCallback.onError(e.getMessage());
            }
        }
    }


    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public boolean isConnected() {
        CastSession castSession = CastContext.getSharedInstance(mAppContext).getSessionManager()
                .getCurrentCastSession();
        return (castSession != null && castSession.isConnected());
    }

    @Override
    public boolean isPlaying() {
        return isConnected() && mRemoteMediaClient != null && mRemoteMediaClient.isPlaying();
    }

    @Override
    public int getState() {
        return mPlaybackState;
    }


    private void loadMedia(String mediaId, boolean autoPlay, long currentPosition) throws JSONException {
        String musicId = mediaId;
        String appPackageName = "";
        if (mRemoteMediaClient != null) {
            AudioPlaylistHelper.getInstance().setCurrentMediaId(musicId);
            MediaMetadataCompat track = AudioPlaylistHelper.getInstance().getMetadata(musicId);
            if (track == null) {
                throw new IllegalArgumentException("Invalid mediaId " + mediaId);
            }
            if (!TextUtils.equals(mediaId, mCurrentMediaId)) {
                mCurrentMediaId = mediaId;
            }
            appPackageName = mAppContext.getPackageName();
            JSONObject customData = new JSONObject();
            customData.put(ITEM_ID, mediaId);
            customData.put(CastingUtils.ITEM_TYPE, appPackageName + "" + CastingUtils.ITEM_TYPE_AUDIO);

            MediaInfo media = toCastMediaMetadata(track, customData);
            mRemoteMediaClient.load(media, autoPlay, currentPosition, customData);
            mRemoteMediaClient.addListener(mRemoteMediaClientListener);
            mRemoteMediaClient.addProgressListener(progressListener, 1000);
//            isStatusUpdateedForCurrentItem = false;
            System.out.println("load media data");
        }

    }


    /**
     * Helper method to convert a {@link android.media.MediaMetadata} to a
     * {@link com.google.android.gms.cast.MediaInfo} used for sending media to the receiver app.
     *
     * @param track      {@link com.google.android.gms.cast.MediaMetadata}
     * @param customData custom data specifies the local mediaId used by the player.
     * @return mediaInfo {@link com.google.android.gms.cast.MediaInfo}
     */
    private static MediaInfo toCastMediaMetadata(MediaMetadataCompat track,
                                                 JSONObject customData) {
        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE,
                track.getDescription().getTitle() == null ? "Unknown" :
                        track.getDescription().getTitle().toString());
        mediaMetadata.putString(MediaMetadata.KEY_SUBTITLE,
                track.getDescription().getSubtitle() == null ? "Unknown" :
                        track.getDescription().getSubtitle().toString());

        mediaMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST,
                track.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST) == null ? "Unknown" :
                        track.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST));

        mediaMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST,
                track.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) == null ? "Unknown" :
                        track.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));

        WebImage image = new WebImage(
                new Uri.Builder().encodedPath(track.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) == null ? "" :
                        track.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
                )
                        .build());
        // First image is used by the receiver for showing the audio album art.
        mediaMetadata.addImage(image);
        // Second image is used by Cast Companion Library on the full screen activity that is shown
        // when the cast dialog is clicked.
        mediaMetadata.addImage(image);

        //noinspection ResourceType
        return new MediaInfo.Builder(track.getString(AudioPlaylistHelper.CUSTOM_METADATA_TRACK_SOURCE))
                .setContentType(MIME_TYPE_AUDIO_MPEG)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(mediaMetadata)
                .setCustomData(customData)
                .build();
    }


    private void setMetadataFromRemote() {
        // Sync: We get the customData from the remote media information and update the local
        // metadata if it happens to be different from the one we are currently using.
        // This can happen when the app was either restarted/disconnected + connected, or if the
        // app joins an existing session while the Chromecast was playing a queue.
        try {
            if (mRemoteMediaClient != null) {
                MediaInfo mediaInfo = mRemoteMediaClient.getMediaInfo();
                if (mediaInfo == null) {
                    return;
                }
                JSONObject customData = mediaInfo.getCustomData();

                if (customData != null && customData.has(ITEM_ID)) {
                    String remoteMediaId = customData.getString(ITEM_ID);
                    if (!TextUtils.equals(mCurrentMediaId, remoteMediaId)) {
                        mCurrentMediaId = remoteMediaId;
                        if (mCallback != null) {
                            mCallback.setCurrentMediaId(remoteMediaId);
                        }
                        updateLastKnownStreamPosition();
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception processing update metadata");
        }

    }

    private void updatePlaybackState() {
        if (mRemoteMediaClient != null) {
            int status = mRemoteMediaClient.getPlayerState();
            int idleReason = mRemoteMediaClient.getIdleReason();

            if (mRemoteMediaClient != null && mRemoteMediaClient.getStreamDuration() > 0) {
                long duration = mRemoteMediaClient.getStreamDuration();
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
            // Convert the remote playback states to media playback states.
            switch (status) {
                case MediaStatus.PLAYER_STATE_IDLE:
                    if (idleReason == MediaStatus.IDLE_REASON_FINISHED) {
                        System.out.println("Music Service AudioCastPlayback PLAYER_STATE_IDLE");
                        ifCastDeviceClosedPlayLocally();
                        if (mCallback != null && !isStatusUpdateedForCurrentItem) {
                            mRemoteMediaClient.removeListener(mRemoteMediaClientListener);
                            mRemoteMediaClient.removeProgressListener(progressListener);
                            mCallback.onCompletion();
                            isStatusUpdateedForCurrentItem = true;
                        }
                    }
                    break;
                case MediaStatus.PLAYER_STATE_BUFFERING:
                    System.out.println("Music Service AudioCastPlayback PLAYER_STATE_BUFFERING");

                    ifCastDeviceClosedPlayLocally();
                    mPlaybackState = PlaybackStateCompat.STATE_BUFFERING;
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(mPlaybackState);
                    }
                    break;
                case MediaStatus.PLAYER_STATE_PLAYING:
                    mPlaybackState = PlaybackStateCompat.STATE_PLAYING;
                    isStatusUpdateedForCurrentItem = false;

                    setMetadataFromRemote();
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(mPlaybackState);
                    }
                    break;
                case MediaStatus.PLAYER_STATE_PAUSED:
                    mPlaybackState = PlaybackStateCompat.STATE_PAUSED;
                    setMetadataFromRemote();
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(mPlaybackState);
                    }
                    break;
                default: // case unknown
                    Log.d(TAG, "State default : " + status);
                    break;
            }
        }
    }

    private void ifCastDeviceClosedPlayLocally() {
        CastSession castSession = CastContext.getSharedInstance(mAppContext).getSessionManager()
                .getCurrentCastSession();
        if (castSession == null && (castSession != null && !castSession.isConnected())) {
            AudioPlaylistHelper.getInstance().playAudioOnClick(mCurrentMediaId, mCurrentPosition);
        }
    }

    private class CastMediaClientListener implements RemoteMediaClient.Listener {

        @Override
        public void onMetadataUpdated() {
            Log.d(TAG, "RemoteMediaClient.onMetadataUpdated");
            setMetadataFromRemote();
        }

        @Override
        public void onStatusUpdated() {
            Log.d(TAG, "RemoteMediaClient.onStatusUpdated");
            updatePlaybackState();
        }

        @Override
        public void onSendingRemoteMediaRequest() {
        }

        @Override
        public void onAdBreakStatusUpdated() {
        }

        @Override
        public void onQueueStatusUpdated() {
        }

        @Override
        public void onPreloadStatusUpdated() {
        }
    }


    private void initProgressListeners() {

        progressListener = (remoteCastProgress, totalCastDuration) -> {
            this.mCurrentPosition = remoteCastProgress;
            long castCurrentDuration = remoteCastProgress / 1000;
            try {
                if (castCurrentDuration % 60 == 0) {
                    AudioServiceHelper.getAudioInstance().changeMiniControllerVisiblity(false);

                }
            } catch (Exception e) {
                //Log.e(TAG, "Error initializing progress indicators: " + e.getMessage());
            }
        };
    }
}
