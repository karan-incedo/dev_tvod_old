package com.viewlift.models.data.appcms.audio;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.vimeo.stag.UseStag;

@UseStag
public class StreamingInfo {

    @SerializedName("audioAssets")
    @Expose
    AudioAssets audioAssets = null;

    public AudioAssets getAudioAssets() {
        return audioAssets;
    }

    public ContentDatum convertToContentDatum() {
        ContentDatum contentDatum = new ContentDatum();
        //contentDatum.setAudioAssets(this.audioAssets);
        return contentDatum;
    }
}
