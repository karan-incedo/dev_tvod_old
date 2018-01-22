package com.viewlift.Audio.playback;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class AudioPlaylistHelper {

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static List<String> currentAudioPlaylist = new ArrayList<String>();
    private static List<String> copyOfAudioPlaylistID = new ArrayList<String>();
    private static List<String> tempAudioPlaylistID = new ArrayList<String>();

    public static AudioPlaylistHelper audioPlaylistInstance;
    public static int indexAudioFromPlaylist = 0;
    private static String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    private String mCurrentMusicId = "";

    Activity mAct;
    private AppCMSPresenter appCmsPresenter;
    Context context;

    public static AudioPlaylistHelper getInstance() {
        if (audioPlaylistInstance == null) {
            audioPlaylistInstance = new AudioPlaylistHelper();
        }
        return audioPlaylistInstance;
    }

    public void setAppCMSPresenter(AppCMSPresenter appCmsPresenterInstance, Activity mActivity) {
        appCmsPresenter = appCmsPresenterInstance;
        mAct = mActivity;
        context = mActivity.getApplicationContext();
    }

    public void setPlaylist(List<String> arrPlaylist) {
        currentAudioPlaylist.clear();
        currentAudioPlaylist.addAll(arrPlaylist);
        if (appCmsPresenter.getAudioShuffledPreference()) {
            doShufflePlaylist();
        } else {
            indexAudioFromPlaylist = 0;
        }
        getTempPlaylist().clear();
    }

    public List<String> getTempPlaylist() {
        return tempAudioPlaylistID;
    }

    public void setTempPlaylist(List<String> arrPlaylist) {
        tempAudioPlaylistID = arrPlaylist;
    }

    public static List<String> getPlaylist() {
        return currentAudioPlaylist;
    }


    public void doShufflePlaylist() {
        copyOfAudioPlaylistID.clear();
        copyOfAudioPlaylistID.addAll(currentAudioPlaylist);
        Collections.shuffle(currentAudioPlaylist);

        //reset current Media Id
        if (getCurrentMediaId() != null && TextUtils.isEmpty(getCurrentMediaId())) {
            indexAudioFromPlaylist = currentAudioPlaylist.indexOf(getCurrentMediaId());
        } else {
            indexAudioFromPlaylist = 0;
        }
    }

    public void undoShufflePlaylist() {
        copyOfAudioPlaylistID.clear();
        currentAudioPlaylist.addAll(copyOfAudioPlaylistID);

        //reset current Media Id
        if (getCurrentMediaId() != null)
            indexAudioFromPlaylist = currentAudioPlaylist.indexOf(getCurrentMediaId());
    }

    public void autoPlayNextItemFromPLaylist(IPlaybackCall callBackPlaylistHelper) {
        indexAudioFromPlaylist++;
        if (currentAudioPlaylist.size() > indexAudioFromPlaylist) {
            String mediaId = currentAudioPlaylist.get(indexAudioFromPlaylist);
            appCmsPresenter.getAudioDetail(mediaId, 0, callBackPlaylistHelper,true,null);
        }
    }

    public long mediaDuration = 0;

    public void setDuration(long duration) {
        mediaDuration = duration;
    }

    // play audio on click on item so set index position as per sequence of
    public void playAudioOnClick(String mediaId, long currentPosition) {
        indexAudioFromPlaylist = currentAudioPlaylist.indexOf(mediaId);
        appCmsPresenter.getAudioDetail(mediaId, currentPosition, null,true,null);
    }

    public void skipToNextItem(IPlaybackCall callBackPlaylistHelper) {
        if ((currentAudioPlaylist.size() > indexAudioFromPlaylist + 1) && indexAudioFromPlaylist + 1 >= 0) {
            indexAudioFromPlaylist++;
            String mediaId = currentAudioPlaylist.get(indexAudioFromPlaylist);
            appCmsPresenter.getAudioDetail(mediaId, 0,callBackPlaylistHelper,true,null);

        } else {
            Toast.makeText(context, "No next item avilable in playlist", Toast.LENGTH_SHORT).show();
        }
    }

    public void skipToPreviousItem(IPlaybackCall callBackPlaylistHelper) {
        if ((currentAudioPlaylist.size() > indexAudioFromPlaylist - 1) && indexAudioFromPlaylist - 1 >= 0) {
            indexAudioFromPlaylist--;
            String mediaId = currentAudioPlaylist.get(indexAudioFromPlaylist);
            appCmsPresenter.getAudioDetail(mediaId, 0,callBackPlaylistHelper,true,null);
        } else {
            Toast.makeText(context, "No previous item avilable in playlist", Toast.LENGTH_SHORT).show();
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

        source = source;//"http://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3";
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

    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item, long mCurrentPlayerPosition) {
        if (item.isPlayable()) {
            Bundle bundle = new Bundle();
            bundle.putLong("CURRENT_POSITION", mCurrentPlayerPosition);
            MediaControllerCompat.getMediaController(appCmsPresenter.getCurrentActivity()).getTransportControls()
                    .playFromMediaId(item.getMediaId(), bundle);
        }
    }

    public void setCurrentMediaId(String mediaId) {
        mCurrentMusicId = mediaId;
    }

    public String getCurrentMediaId() {
        return mCurrentMusicId;
    }

    public interface IPlaybackCall {
        void onPlaybackStart(MediaBrowserCompat.MediaItem item, long mCurrentPlayerPosition);

    }

    ContentDatum currentAudioPLayingData;

    public ContentDatum getCurrentAudioPLayingData() {
        return currentAudioPLayingData;
    }

    public void setCurrentAudioPLayingData(ContentDatum currentAudioPLayingData) {
        this.currentAudioPLayingData = currentAudioPLayingData;
    }
}
