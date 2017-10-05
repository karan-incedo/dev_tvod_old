package com.viewlift.models.data.appcms.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.ContentDetails;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.StreamingInfo;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class AppCMSSearchResult {
    @SerializedName("gist")
    @Expose
    Gist gist;

    @SerializedName("contentDetails")
    ContentDetails contentDetails;

    public Gist getGist() {
        return gist;
    }

    public void setGist(Gist gist) {
        this.gist = gist;
    }

    public ContentDetails getContentDetails() {
        return contentDetails;
    }

    public void setContentDetails(ContentDetails contentDetails) {
        this.contentDetails = contentDetails;
    }

    public ContentDatum getContent(){
        ContentDatum contentDatum = new ContentDatum();
        Gist gist = new Gist();
        gist.setTitle(gist.getTitle());
        gist.setPosterImageUrl(gist.getPosterImageUrl());
        gist.setPermalink(gist.getPermalink());
        gist.setId(gist.getId());
        contentDatum.setGist(gist);
        return contentDatum;
    }
}
