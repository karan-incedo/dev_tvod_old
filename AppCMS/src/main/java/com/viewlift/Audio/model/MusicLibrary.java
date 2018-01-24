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

import com.viewlift.models.data.appcms.playlist.AudioList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class MusicLibrary {

    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    public static String CUSTOM_METADATA_TRACK_SOURCE = "__SOURCE__";

    public static MediaMetadataCompat getMetadata(Context ctx, String mediaId) {
        MediaMetadataCompat metaDataForMediaId = music.get(mediaId);
        return metaDataForMediaId;
    }

    public static List<String> createPlaylistByIDList(List<AudioList> audioList) {
        List<String> audioPlaylistId = new ArrayList<String>();
        for (AudioList audioData : audioList) {
            audioPlaylistId.add(audioData.getGist().getId());
        }
        return audioPlaylistId;
    }
}
