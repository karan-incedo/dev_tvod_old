package com.viewlift.models.data.appcms.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.ImageGist;
import com.viewlift.models.data.appcms.audio.AudioGist;
import com.vimeo.stag.UseStag;

/**
 * Created by wishy.gupta on 09-01-2018.
 */
@UseStag
public class AudioList {
    @SerializedName("gist")
    @Expose
    AudioGist gist;

    public AudioGist getGist() {
        return gist;
    }

    public void setGist(AudioGist gist) {
        this.gist = gist;
    }
}
