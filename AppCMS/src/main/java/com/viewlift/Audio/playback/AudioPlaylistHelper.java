package com.viewlift.Audio.playback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.viewlift.Audio.MusicService;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.audio.AppCMSAudioDetailResult;
import com.viewlift.models.data.appcms.audio.LastPlayAudioDetail;
import com.viewlift.models.data.appcms.playlist.AppCMSPlaylistResult;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class AudioPlaylistHelper {

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static List<String> currentAudioPlaylist = new ArrayList<String>();
    private static List<String> copyOfAudioPlaylistID = new ArrayList<String>();
    public static long mediaDuration = 0;
    public static AudioPlaylistHelper audioPlaylistInstance;
    public static int indexAudioFromPlaylist = 0;
    public static String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";
    private String mCurrentMusicId = "";
    private String lastMediaId = "";

    public static String mCurrentPlayListId = "";
    public static String CUSTOM_METADATA_TRACK_PARAM_LINK = "__PARAM_LINK__";
    public static String CUSTOM_METADATA_TRACK_ALBUM_YEAR = "_ALBUM_YEAR";
    public static String CUSTOM_METADATA_IS_FREE = "__IS_FREE__";
    public static String CUSTOM_METADATA_TRACK_DIRECTOR = "_ALBUM_DIRECTOR";

    Activity mAct;

    public AppCMSPresenter getAppCmsPresenter() {
        return appCmsPresenter;
    }

    private static AppCMSPresenter appCmsPresenter;
    Context context;
    AppCMSPlaylistResult appCMSPlaylistResult;
    AppCMSPlaylistResult currentPlaylistData;

    public static AudioPlaylistHelper getInstance() {
        if (audioPlaylistInstance == null) {
            audioPlaylistInstance = new AudioPlaylistHelper();
        }
        return audioPlaylistInstance;
    }

    private AudioPlaylistHelper() {
    }

    public void setAppCMSPresenter(AppCMSPresenter appCmsPresenterInstance, Activity mActivity) {
        appCmsPresenter = appCmsPresenterInstance;
        mAct = mActivity;
        context = mActivity.getApplicationContext();
    }


    public void setCurrentPlaylistData(AppCMSPlaylistResult mAppCMSPlaylistResult) {
        currentPlaylistData = mAppCMSPlaylistResult;
    }

    public void pausePlayback() {
        MediaControllerCompat.TransportControls controls = MediaControllerCompat.getMediaController(mAct).getTransportControls();
        controls.pause();
    }

    public void stopPlayback() {
        MediaControllerCompat.TransportControls controls = MediaControllerCompat.getMediaController(mAct).getTransportControls();
        controls.stop();
    }

    public AppCMSPlaylistResult getCurrentPlaylistData() {
        return currentPlaylistData;
    }

    public void setPlaylist(List<String> arrPlaylist) {
        currentAudioPlaylist.clear();
        currentAudioPlaylist.addAll(arrPlaylist);
        if (appCmsPresenter.getAudioShuffledPreference()) {
            doShufflePlaylist();
        } else {
            indexAudioFromPlaylist = 0;
        }
    }

    public void setTempPlaylistData(AppCMSPlaylistResult mAppCMSPlaylistResult) {
        appCMSPlaylistResult = mAppCMSPlaylistResult;
    }

    public AppCMSPlaylistResult getTempPlaylistData() {
        return appCMSPlaylistResult;
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
        currentAudioPlaylist.addAll(copyOfAudioPlaylistID);
        //reset current Media Id
        if (getCurrentMediaId() != null) {
            indexAudioFromPlaylist = currentAudioPlaylist.indexOf(getCurrentMediaId());
        }
        copyOfAudioPlaylistID.clear();
    }

    public void autoPlayNextItemFromPLaylist(IPlaybackCall callBackPlaylistHelper) {

        if (!appCmsPresenter.isNetworkConnected()) {
            Toast.makeText(context, context.getResources().getString(R.string.no_network_connectivity_message), Toast.LENGTH_SHORT).show();
            return;
        }
        indexAudioFromPlaylist++;
        if (currentAudioPlaylist.size() > indexAudioFromPlaylist) {
            String mediaId = currentAudioPlaylist.get(indexAudioFromPlaylist);
            appCmsPresenter.getAudioDetail(mediaId, 0, callBackPlaylistHelper, false, true, 0, null);
        }
    }

    public String getNextItemId() {
        String mediaId = null;
        indexAudioFromPlaylist++;
        if (currentAudioPlaylist.size() > indexAudioFromPlaylist) {
            mediaId = currentAudioPlaylist.get(indexAudioFromPlaylist);
        }
        return mediaId;
    }


    public void setDuration(long duration) {
        mediaDuration = duration;
    }

    // play audio on click on item so set index position as per sequence of
    public void playAudioOnClickItem(String mediaId, long currentPosition) {
        appCmsPresenter.setAudioReload(false);
        getAudioDetails(mediaId, currentPosition, true);
    }

    public void playAudio(String mediaId, long currentPosition) {
        getAudioDetails(mediaId, currentPosition, false);
    }

    private void getAudioDetails(String mediaId, long currentPosition, boolean isPlayerScreenOpen) {
        context.startService(new Intent(context, MusicService.class));
        indexAudioFromPlaylist = currentAudioPlaylist.indexOf(mediaId);
        appCmsPresenter.getAudioDetail(mediaId, currentPosition, null, isPlayerScreenOpen, true, 0, null);
    }

    public void skipToNextItem(IPlaybackCall callBackPlaylistHelper) {
        if ((currentAudioPlaylist.size() > indexAudioFromPlaylist + 1) && indexAudioFromPlaylist + 1 >= 0) {
            indexAudioFromPlaylist++;
            String mediaId = currentAudioPlaylist.get(indexAudioFromPlaylist);
            //pause current item while loading next item
            callBackPlaylistHelper.updatePlayStateOnSkip();
            appCmsPresenter.getAudioDetail(mediaId, 0, callBackPlaylistHelper, false, true, 0, null);
        } else {
            Toast.makeText(context, "No next item available in queue", Toast.LENGTH_SHORT).show();
        }
    }

    public void skipToPreviousItem(IPlaybackCall callBackPlaylistHelper) {

        if (!appCmsPresenter.isNetworkConnected()) {
            Toast.makeText(context, context.getResources().getString(R.string.no_network_connectivity_message), Toast.LENGTH_SHORT).show();
            return;
        }
        if ((currentAudioPlaylist.size() > indexAudioFromPlaylist - 1) && indexAudioFromPlaylist - 1 >= 0) {
            indexAudioFromPlaylist--;
            String mediaId = currentAudioPlaylist.get(indexAudioFromPlaylist);

            //pause current item while loading next item
            callBackPlaylistHelper.updatePlayStateOnSkip();

            appCmsPresenter.getAudioDetail(mediaId, 0, callBackPlaylistHelper, false, true, 0, null);
        } else {
            Toast.makeText(context, "No previous item available in playlist", Toast.LENGTH_SHORT).show();
        }
    }

    public void createMediaMetaDataForAudioItem(AppCMSAudioDetailResult appCMSAudioDetailResult) {
        String mediaId = appCMSAudioDetailResult.getId();
        String title = "";
        String artist = "";
        String director = "";

        String album = "Unknown", iconUrl = "", source = "", param_link = "", album_year = "Unknown", isFree = "true";
        long runTime = 240 * 1000;

        if (appCMSAudioDetailResult.getGist() != null) {
            title = appCMSAudioDetailResult.getGist().getTitle();

            if (appCMSAudioDetailResult.getGist().getDescription() != null)
                album = appCMSAudioDetailResult.getGist().getDescription();

            if (appCMSAudioDetailResult.getGist().getImageGist() != null) {
                if (appCMSAudioDetailResult.getGist().getImageGist().get_1x1() != null) {
                    iconUrl = appCMSAudioDetailResult.getGist().getImageGist().get_1x1();
                }
            }
            if (appCmsPresenter.isVideoDownloaded(appCMSAudioDetailResult.getGist().getId())) {
                iconUrl = appCMSAudioDetailResult.getGist().getVideoImageUrl();
            }
            if (appCMSAudioDetailResult.getGist().getPermalink() != null)
                param_link = appCMSAudioDetailResult.getGist().getPermalink();

            if (appCMSAudioDetailResult.getGist().getRuntime() != 0)
                runTime = appCMSAudioDetailResult.getGist().getRuntime() * 1000;


            if (appCMSAudioDetailResult.getGist().getYear() != null)
                album_year = appCMSAudioDetailResult.getGist().getYear();


            isFree = String.valueOf(appCMSAudioDetailResult.getGist().isFree());

        }

        artist = appCmsPresenter.getArtistNameFromCreditBlocks(appCMSAudioDetailResult.getCreditBlocks());
        director = appCmsPresenter.getDirectorNameFromCreditBlocks(appCMSAudioDetailResult.getCreditBlocks());

        if (appCMSAudioDetailResult.getStreamingInfo() != null && appCMSAudioDetailResult.getStreamingInfo().getAudioAssets() != null
                && appCMSAudioDetailResult.getStreamingInfo().getAudioAssets().getMp3() != null && appCMSAudioDetailResult.getStreamingInfo().getAudioAssets().getMp3().getUrl() != null)
            source = appCMSAudioDetailResult.getStreamingInfo().getAudioAssets().getMp3().getUrl();

        String genre = "";
        int trackNumber = 0;
        int totalTrackCount = 0;
        long duration = -1;

//        if(title.equalsIgnoreCase("Aasan Nahin Yahan") || title.equalsIgnoreCase("Qismat")){
//            isFree="true";
//        }
        music.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(CUSTOM_METADATA_TRACK_SOURCE, source)
                        .putString(CUSTOM_METADATA_TRACK_ALBUM_YEAR, album_year)
                        .putString(CUSTOM_METADATA_TRACK_DIRECTOR, director)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, iconUrl)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNumber)
                        .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, totalTrackCount)
                        .putString(CUSTOM_METADATA_TRACK_PARAM_LINK, param_link)
                        .putString(CUSTOM_METADATA_IS_FREE, isFree)

                        .build());
    }

    public MediaMetadataCompat getMetadata(String mediaId) {
        MediaMetadataCompat metaDataForMediaId = null;
        if (music != null && music.size() > 0 && mediaId != null) {
            metaDataForMediaId = music.get(mediaId);

        }

        return metaDataForMediaId;
    }

    public MediaBrowserCompat.MediaItem getMediaMetaDataItem(String mediaId) {
        MediaMetadataCompat metaData = getMetadata(mediaId);

        return new MediaBrowserCompat.MediaItem(
                metaData.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item, long mCurrentPlayerPosition) {
        if (item.isPlayable() && appCmsPresenter.getCurrentActivity() != null) {
            Bundle bundle = new Bundle();
            bundle.putLong("CURRENT_POSITION", mCurrentPlayerPosition);
            MediaControllerCompat.getMediaController(mAct).getTransportControls()
                    .playFromMediaId(item.getMediaId(), bundle);
        }
    }

    public void saveLastPlayPositionDetails(String id, long pos) {
        appCmsPresenter.saveLastPlaySongPosition(id, pos);
    }

    public LastPlayAudioDetail getLastPlayPositionDetails() {
        return appCmsPresenter.getLastPlaySongPosition();
    }

    public void setCurrentMediaId(String mediaId) {
        mCurrentMusicId = mediaId;
    }

    public void setLastMediaId(String mediaId) {
        lastMediaId = mediaId;
    }


    public String getLastMediaId() {
        return lastMediaId;
    }

    public String getCurrentPlaylistId() {
        return mCurrentPlayListId;
    }


    public void setCurrentPlaylistId(String playListId) {
        mCurrentPlayListId = playListId;
    }

    public String getCurrentMediaId() {
        return mCurrentMusicId;
    }

    public interface IPlaybackCall {
        void onPlaybackStart(MediaBrowserCompat.MediaItem item, long mCurrentPlayerPosition);

        void updatePlayStateOnSkip();

    }

    ContentDatum currentAudioPLayingData;

    public ContentDatum getCurrentAudioPLayingData() {
        return currentAudioPLayingData;
    }

    public void setCurrentAudioPLayingData(ContentDatum currentAudioPLayingData) {
        this.currentAudioPLayingData = currentAudioPLayingData;
    }

}
