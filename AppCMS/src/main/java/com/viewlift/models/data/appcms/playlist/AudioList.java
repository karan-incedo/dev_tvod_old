package com.viewlift.models.data.appcms.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.ImageGist;
import com.vimeo.stag.UseStag;

/**
 * Created by wishy.gupta on 09-01-2018.
 */
@UseStag
public class AudioList {
    @SerializedName("gist")
    @Expose
    Gist gist;

    public Gist getGist() {
        return gist;
    }

    public void setGist(Gist gist) {
        this.gist = gist;
    }

    public ContentDatum convertToContentDatum() {
        ContentDatum contentDatum = new ContentDatum();

        contentDatum.setGist(this.gist);
        return contentDatum;
    }

}
