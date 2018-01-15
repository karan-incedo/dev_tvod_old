package com.viewlift.Audio.playback;

import android.app.Activity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;
import com.viewlift.presenters.AppCMSPresenter;

import net.nightwhistler.htmlspanner.TextUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by karan.kaushik on 1/12/2018.
 */

public class AudioPlaylistHelper {

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static List<String> audioPlaylistId = new ArrayList<String>();
    private static List<String> copyOfAudioPlaylistID = new ArrayList<String>();

    public static AudioPlaylistHelper audioPlaylistInstance;
    public static int indexAudioFromPlaylist = 0;
    public static String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    String mCurrentMusicId = "";

    Activity mAct;
    private AppCMSPresenter appCmsPresenter;

    public static AudioPlaylistHelper getAudioPlaylistHelperInstance() {
        if (audioPlaylistInstance == null) {
            audioPlaylistInstance = new AudioPlaylistHelper();
        }
        return audioPlaylistInstance;
    }

    public void setAppCMSPresenter(AppCMSPresenter appCmsPresenterInstance, Activity mActivity) {
        appCmsPresenter = appCmsPresenterInstance;
        mAct = mActivity;
    }

    public void setPlaylist(List<String> arrPlaylist) {
        audioPlaylistId = arrPlaylist;
        if (appCmsPresenter.getAudioShuffledPreference()) {
            doShufflePlaylist();
        } else {
            indexAudioFromPlaylist = 0;
        }
    }

    public static List<String> getPlaylist() {
        return audioPlaylistId;
    }

    public void doShufflePlaylist() {
        copyOfAudioPlaylistID.clear();
        copyOfAudioPlaylistID.addAll(audioPlaylistId);
        Collections.shuffle(audioPlaylistId);

        //reset current Media Id
        if (getCurrentMediaId() != null && TextUtils.isEmpty(getCurrentMediaId())) {
            indexAudioFromPlaylist = audioPlaylistId.indexOf(getCurrentMediaId());
        } else {
            indexAudioFromPlaylist = 0;
        }
    }

    public void undoShufflePlaylist() {
        copyOfAudioPlaylistID.clear();
        audioPlaylistId.addAll(copyOfAudioPlaylistID);

        //reset current Media Id
        indexAudioFromPlaylist = audioPlaylistId.indexOf(getCurrentMediaId());

    }

    public void autoPlayNextItemFromPLaylist() {
        indexAudioFromPlaylist++;
        if (audioPlaylistId.size() > indexAudioFromPlaylist) {
            String mediaId = audioPlaylistId.get(indexAudioFromPlaylist);
            appCmsPresenter.getAudioDetail(mediaId);
        }
    }

    // play audio on click on item so set index position as per sequence of
    public void playAudioOnClick(String mediaId) {
        indexAudioFromPlaylist = audioPlaylistId.indexOf(mediaId);
        appCmsPresenter.getAudioDetail(mediaId);
    }

    public void skipToNextItem() {
        indexAudioFromPlaylist++;
        if (audioPlaylistId.size() > indexAudioFromPlaylist) {
            String mediaId = audioPlaylistId.get(indexAudioFromPlaylist);
            appCmsPresenter.getAudioDetail(mediaId);
        } else {
            Toast.makeText(mAct, "No next item avilable in playlist", Toast.LENGTH_SHORT).show();
        }
    }

    public void skipToPreviousItem() {
        indexAudioFromPlaylist--;
        if ((audioPlaylistId.size() > indexAudioFromPlaylist) && indexAudioFromPlaylist >= 0) {
            String mediaId = audioPlaylistId.get(indexAudioFromPlaylist);
            appCmsPresenter.getAudioDetail(mediaId);
        } else {
            Toast.makeText(mAct, "No previous item avilable in playlist", Toast.LENGTH_SHORT).show();
        }
    }

    public static void createMediaMetaDataForAudioItem(AppCMSAudioDetailResult appCMSAudioDetailResult) {
        String mediaId = appCMSAudioDetailResult.getId();
        String title = "";
        String album = "", iconUrl = "", artist = "", source = "";
        if (appCMSAudioDetailResult.getGist() != null) {
            title = appCMSAudioDetailResult.getGist().getTitle();

            if (appCMSAudioDetailResult.getGist().getDescription() != null)
                album = appCMSAudioDetailResult.getGist().getDescription();

            if (appCMSAudioDetailResult.getGist().getImageGist() != null && appCMSAudioDetailResult.getGist().getImageGist().get_16x9() != null)
                iconUrl = appCMSAudioDetailResult.getGist().getImageGist().get_16x9();

        }
        if (appCMSAudioDetailResult.getCreditBlocks() != null && appCMSAudioDetailResult.getCreditBlocks().size() > 0 && appCMSAudioDetailResult.getCreditBlocks().get(0).getTitle() != null)
            artist = appCMSAudioDetailResult.getCreditBlocks().get(0).getTitle();

        if (appCMSAudioDetailResult.getStreamingInfo() != null && appCMSAudioDetailResult.getStreamingInfo().getAudioAssets() != null
                && appCMSAudioDetailResult.getStreamingInfo().getAudioAssets().getMp3() != null && appCMSAudioDetailResult.getStreamingInfo().getAudioAssets().getMp3().getUrl() != null)
            source = appCMSAudioDetailResult.getStreamingInfo().getAudioAssets().getMp3().getUrl();

        source = "http://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3";
        String genre = "";
        int trackNumber = 0;
        int totalTrackCount = 0;
        int duration = 102 * 1000; // ms

        music.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(CUSTOM_METADATA_TRACK_SOURCE, source)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, iconUrl)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNumber)
                        .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, totalTrackCount)
                        .build());
    }

    public static MediaMetadataCompat getMetadata(String mediaId) {
        MediaMetadataCompat metaDataForMediaId = music.get(mediaId);

        return metaDataForMediaId;
    }

    public static MediaBrowserCompat.MediaItem getMediaMetaDataItem(String mediaId) {
        MediaMetadataCompat metaData = getMetadata(mediaId);

        return new MediaBrowserCompat.MediaItem(
                metaData.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item) {
        if (item.isPlayable()) {
            MediaControllerCompat.getMediaController(appCmsPresenter.getCurrentActivity()).getTransportControls()
                    .playFromMediaId(item.getMediaId(), null);
        }
    }

    public void setCurrentMediaId(String mediaId) {
        mCurrentMusicId = mediaId;
    }

    public String getCurrentMediaId() {
        return mCurrentMusicId;
    }
}
