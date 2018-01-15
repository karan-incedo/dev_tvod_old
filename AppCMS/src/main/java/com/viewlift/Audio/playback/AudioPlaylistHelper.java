package com.viewlift.Audio.playback;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;

import com.viewlift.Audio.AudioServiceHelper;
import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;
import com.viewlift.models.data.appcms.playlist.AudioList;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by karan.kaushik on 1/12/2018.
 */

public class AudioPlaylistHelper {

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static List<AudioList> audioPlaylistId = new ArrayList<AudioList>();
    public static AudioPlaylistHelper audioPlaylistInstance;
    public static int indexAudioFromPlaylist = 0;
    public static String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";

    private AppCMSPresenter appCmsPresenter;

    public static AudioPlaylistHelper getAudioPlaylistHelperInstance() {
        if (audioPlaylistInstance == null) {
            audioPlaylistInstance = new AudioPlaylistHelper();
        }
        return audioPlaylistInstance;
    }

    public AudioPlaylistHelper() {
    }

    public void setAppCMSPresenter(AppCMSPresenter appCmsPresenterInstance) {
        appCmsPresenter = appCmsPresenterInstance;
    }

    public static void setPlaylist(List<AudioList> arrPlaylist) {
        audioPlaylistId = arrPlaylist;
        indexAudioFromPlaylist = 0;
    }

    public static List<AudioList> getPlaylist() {
        return audioPlaylistId;
    }

    public  void autoPlayNextItemOnComplete(){
        if(audioPlaylistId.size()<indexAudioFromPlaylist){
           String mediaId= audioPlaylistId.get(indexAudioFromPlaylist).getGist().getId();
            getAudioDataFromPlaylistToPlay(mediaId);
        }
    }

    public  void getAudioDataFromPlaylistToPlay(String MediaId) {
        indexAudioFromPlaylist++;
        appCmsPresenter.getAudioDetail(MediaId);
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
}
