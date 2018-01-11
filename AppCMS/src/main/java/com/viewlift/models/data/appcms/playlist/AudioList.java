package com.viewlift.models.data.appcms.playlist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.ImageGist;
import com.vimeo.stag.UseStag;

/**
 * Created by wishy.gupta on 09-01-2018.
 */
@UseStag
public class AudioList {
    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("permalink")
    @Expose
    String permalink;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("runtime")
    @Expose
    long runtime;

    @SerializedName("contentType")
    @Expose
    String contentType;

    @SerializedName("mediaType")
    @Expose
    String mediaType;

    @SerializedName("imageGist")
    @Expose
    ImageGist imageGist;

    public String getId() {
        return id;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getTitle() {
        return title;
    }

    public long getRuntime() {
        return runtime;
    }

    public String getContentType() {
        return contentType;
    }

    public String getMediaType() {
        return mediaType;
    }

    public ImageGist getImageGist() {
        return imageGist;
    }

    public ContentDatum convertToContentDatum() {
        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setPermalink(this.permalink);
        contentDatum.setTitle(this.title);
        contentDatum.setContentType(this.contentType);
        contentDatum.setMediaType(this.mediaType);
        return contentDatum;
    }
}
