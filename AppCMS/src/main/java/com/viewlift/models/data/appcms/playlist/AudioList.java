package com.viewlift.models.data.appcms.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Gist;
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

    public ContentDatum convertToContentDatum() {
        ContentDatum contentDatum = new ContentDatum();
        Gist gist=new Gist();
        gist.setId(this.gist.getId());
        gist.setPermalink(this.gist.getPermalink());
        gist.setTitle(this.gist.getTitle());
        gist.setDescription(this.gist.getDescription());
        gist.setRuntime(this.gist.getRuntime());
        gist.setImageGist(this.gist.getImageGist());
        gist.setContentType(this.gist.getContentType());
        gist.setMediaType(this.gist.getMediaType());
        contentDatum.setGist(gist);
        return contentDatum;
    }

}
