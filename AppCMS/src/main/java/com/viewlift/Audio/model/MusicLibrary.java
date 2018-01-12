/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.viewlift.Audio.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class MusicLibrary {

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static final HashMap<String, Integer> albumRes = new HashMap<>();
    private static final HashMap<String, Integer> musicRes = new HashMap<>();
    private static int indexMusic = 1;
    public static String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";

    public static void setIndexPosition(int index) {
        indexMusic = index;
    }

    public static int getIndexPosition() {
        return indexMusic;
    }

    public static int getAlbumSize() {
        return music.size();
    }

    public static String getRoot() {
        return "root";
    }


    private static int getMusicRes(String mediaId) {
        return musicRes.containsKey(mediaId) ? musicRes.get(mediaId) : 0;
    }

    private static int getAlbumRes(String mediaId) {
        return albumRes.containsKey(mediaId) ? albumRes.get(mediaId) : 0;
    }

    public static Bitmap getAlbumBitmap(Context ctx, String mediaId) {
        return BitmapFactory.decodeResource(ctx.getResources(), MusicLibrary.getAlbumRes(mediaId));
    }

    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    public static String getPreviousSong(String currentMediaId) {
        String prevMediaId = music.lowerKey(currentMediaId);
        if (prevMediaId == null) {
            prevMediaId = music.firstKey();
        }
        indexMusic--;
        return prevMediaId;
    }

    public static String getNextSong(String currentMediaId) {
        String nextMediaId = music.higherKey(currentMediaId);
        if (nextMediaId == null) {
            nextMediaId = music.firstKey();
        }
        indexMusic++;
        return nextMediaId;
    }

    public static MediaMetadataCompat getMetadata(Context ctx, String mediaId) {
        MediaMetadataCompat metaDataForMediaId = music.get(mediaId);

        return metaDataForMediaId;
    }

    public static void createMediaMetadataCompat(
            String mediaId1,
            String title1,
            String artist1,
            String album1,
            String genre1,
            int duration1,
            int musicResId1,
            int albumArtResId1,
            String albumArtResName1, int trackCount, String source1, int trackNumber1, String iconUrl1) {
        String mediaId = mediaId1;

        String title = title1;
        String album = album1;
        String artist = artist1;
        String genre = genre1;
        String source = source1;
        String iconUrl = iconUrl1;
        int trackNumber = trackNumber1;
        int totalTrackCount = trackCount;
        int duration = duration1 * 1000; // ms


        String id = String.valueOf(title.hashCode());

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

    static Activity mActivity;

    public static void setAct(Activity mActiv) {
        mActivity = mActiv;
    }

    public static void onMediaItemSelected(MediaBrowserCompat.MediaItem item) {
        if (item.isPlayable()) {
            MediaControllerCompat.getMediaController(mActivity).getTransportControls()
                    .playFromMediaId(item.getMediaId(), null);
        }
    }
}
