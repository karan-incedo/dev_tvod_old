package com.viewlift.models.data.appcms.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.ContentDetails;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.Season_;
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


    @SerializedName("seasons")
    @Expose
    List<Season_> seasons = null;

    public List<Season_> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season_> seasons) {
        this.seasons = seasons;
    }

    public List<String> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<String> audioList) {
        this.audioList = audioList;
    }

    @SerializedName("audioList")
    @Expose
    List<String> audioList = null;

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


    public StreamingInfo getStreamingInfo() {
        return streamingInfo;
    }

    public void setStreamingInfo(StreamingInfo streamingInfo) {
        this.streamingInfo = streamingInfo;
    }

    @SerializedName("streamingInfo")
    @Expose
    StreamingInfo streamingInfo;

    public ContentDatum getContent(){
        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setStreamingInfo(getStreamingInfo());
        contentDatum.setGist(getGist());
        return contentDatum;
    }
}
