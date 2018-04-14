package com.viewlift.Audio;

import android.app.Activity;
import android.content.ComponentName;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.viewlift.Audio.playback.AudioPlaylistHelper;
import com.viewlift.Audio.ui.PlaybackControlsFragment;
import com.viewlift.R;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;


public class AudioServiceHelper {
    public MediaBrowserCompat mMediaBrowser;
    String TAG = "AudioServiceHelper";
    Activity mActivity;
    private PlaybackControlsFragment mControlsFragment;
    public static AudioServiceHelper audioHelper;
    public static IaudioServiceCallBack callbackAudioService;
    public static String APP_CMS_SAVE_LAST_POSITION_MESSAGE = "app_cms_save_last_position_message";
    public static String APP_CMS_STOP_AUDIO_SERVICE_MESSAGE = "app_cms_stop_audio_service_message";
    public static String APP_CMS_STOP_AUDIO_SERVICE_ACTION = "app_cms_stop_audio_service_action";
    public static String APP_CMS_SHOW_PREVIEW_MESSAGE = "app_cms_show_preview_message";
    public static String APP_CMS_SHOW_PREVIEW_ACTION = "app_cms_show_preview_action";
    public static String APP_CMS_SHOW_iS_AUDIO_PREVIEW = "app_cms_show_is_audio_preview";
    public static String APP_CMS_UPDATE_PLAYLIST = "app_cms_update_playlist";
    public static String APP_CMS_PLAYBACK_UPDATE = "app_cms_playback_update";
    public static String APP_CMS_PLAYBACK_UPDATE_MESSAGE = "app_cms_playback_update_message";
    public static String APP_CMS_NOTIFICATION_AUDIO_SERVICE_MESSAGE = "app_cms_notification_audio_service_message";
    public static String APP_CMS_NOTIFICATION_AUDIO_SERVICE_ACTION = "app_cms_notification_audio_service_action";


    public static String APP_CMS_DATA_UPDATE_ACTION = "app_cms_data_update_action";
    public static String APP_CMS_DATA_UPDATE_MESSAGE = "app_cms_data_update_message";

    public static String APP_CMS_DATA_PLAYLIST_UPDATE_ACTION = "app_cms_data_playlist_update_action";
    public static String APP_CMS_DATA_PLAYLIST_UPDATE_MESSAGE = "app_cms_data_playlist_update_message";

    public static AudioServiceHelper getAudioInstance() {
        if (audioHelper == null) {
            audioHelper = new AudioServiceHelper();
        }
        return audioHelper;
    }

    public void setCallBack(IaudioServiceCallBack callbackAudio) {
        callbackAudioService = callbackAudio;

    }

    public void createAudioPlaylistInstance(AppCMSPresenter appCMSPresenter, Activity mActivity) {
        AudioPlaylistHelper.getInstance().setAppCMSPresenter(appCMSPresenter, mActivity);
    }

    public void createMediaBrowserService(Activity mActivity) {
        this.mActivity = mActivity;
        mMediaBrowser = new MediaBrowserCompat(mActivity,
                new ComponentName(mActivity, MusicService.class), mConnectionCallback, null);
    }

    public void onStart() {

        mControlsFragment = (PlaybackControlsFragment) mActivity.getFragmentManager()
                .findFragmentById(R.id.fragment_playback_controls);
        if (mControlsFragment == null) {
            throw new IllegalStateException("Mising fragment with id 'controls'. Cannot continue.");
        }

        hidePlaybackControls();

        mMediaBrowser.connect();
    }

    public void onStop() {
        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(mActivity);
        if (controllerCompat != null) {
            controllerCompat.unregisterCallback(mMediaControllerCallback);
        }
        if (mMediaBrowser != null) {
            mMediaBrowser.disconnect();
        }
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
        MediaControllerCompat mediaController = new MediaControllerCompat(mActivity, token);
        if (mediaController.getMetadata() == null) {
            System.out.println("Metadata null");
        } else {
            System.out.println("Metadata not null");

        }
        MediaControllerCompat.setMediaController(mActivity, mediaController);
        mediaController.registerCallback(mMediaControllerCallback);
        callbackAudioService.onConnect();
        if (shouldShowControls()) {
            showPlaybackControls();
        } else {
            hidePlaybackControls();
        }

        if (mControlsFragment != null) {
            mControlsFragment.onConnected();
        }

    }

    // Callback that ensures that we are showing the controls
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    if (shouldShowControls()) {
                        showPlaybackControls();
                    } else {

                        hidePlaybackControls();
                    }
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    if (shouldShowControls()) {
                        showPlaybackControls();
                    } else {

                        hidePlaybackControls();
                    }
                }
            };

    protected void showPlaybackControls() {
        mActivity.getFragmentManager().beginTransaction()
                .show(mControlsFragment)
                .commitAllowingStateLoss();
        changeMiniControllerVisiblity(false);
    }

    public void changeMiniControllerVisiblity(boolean isShow) {
        if (callbackAudioService != null)
            callbackAudioService.getAudioPlaybackControlVisibility(isShow);

    }


    protected void hidePlaybackControls() {
        try {
            if (mActivity != null && !mActivity.isFinishing()) {
                mActivity.getFragmentManager().beginTransaction()
                        .hide(mControlsFragment)
                        .commit();
            }
        } catch (IllegalStateException e) {

        } catch (Exception e) {

        }

    }

    public boolean isAudioPlaybackControlShowing() {
        return mControlsFragment.isVisible();
    }

    /**
     * Check if the MediaSession is active and in a "playback-able" state
     * (not NONE and not STOPPED).
     *
     * @return true if the MediaSession's state requires playback controls to be visible.
     */
    protected boolean shouldShowControls() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(mActivity);
        if (mediaController == null ||
//                mediaController.getMetadata() == null ||
                mediaController.getPlaybackState() == null) {
            return false;
        }
        switch (mediaController.getPlaybackState().getState()) {
            case PlaybackStateCompat.STATE_ERROR:
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                return false;
            default:
                return true;
        }
    }

    public boolean isAudioPlaying() {
        if (mActivity != null) {
            MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(mActivity);
            if (mediaController == null ||
                    mediaController.getMetadata() == null ||
                    mediaController.getPlaybackState() == null) {
                return false;
            }
            switch (mediaController.getPlaybackState().getState()) {
                case PlaybackStateCompat.STATE_ERROR:
                case PlaybackStateCompat.STATE_NONE:
                case PlaybackStateCompat.STATE_STOPPED:
                    return false;
                default:
                    return true;
            }
        }
        return false;

    }

    protected boolean isFullScreenPlayerEnable() {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(mActivity);
        if (mediaController == null ||
                mediaController.getMetadata() == null ||
                mediaController.getPlaybackState() == null) {
            return false;
        }
        switch (mediaController.getPlaybackState().getState()) {
            case PlaybackStateCompat.STATE_ERROR:
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                return false;
            default:
                return true;
        }
    }

    public void connectMediaBrowserServiceToLoadLibrary() {
        MediaBrowserCompat mediaBrowser = AudioServiceHelper.getAudioInstance().getMediaBrowser();//mMediaFragmentListener.getMediaBrowser();

        if (mediaBrowser.isConnected()) {
            onConnected();
        }
    }

    public void onConnected() {
        String mMediaId = null;
        if (mMediaId == null) {
            mMediaId = AudioServiceHelper.getAudioInstance().getMediaBrowser().getRoot();
        }
        AudioServiceHelper.getAudioInstance().getMediaBrowser().unsubscribe(mMediaId);

        AudioServiceHelper.getAudioInstance().getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);

        // Add MediaController callback so we can redraw the list when metadata changes:
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(mActivity);
        if (controller != null) {
            controller.registerCallback(mMediaControllerCallback);
        }
    }

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                }

                @Override
                public void onError(@NonNull String id) {
                }
            };

    public MediaBrowserCompat getMediaBrowser() {
        return mMediaBrowser;
    }

    public interface IaudioServiceCallBack {
        void getAudioPlaybackControlVisibility(boolean isControllerShowing);

        void onConnect();

    }

}
