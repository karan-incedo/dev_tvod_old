package com.viewlift.models.data.appcms.audio;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.CreditBlock;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.StreamingInfo;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

@UseStag
public class AppCMSAudioDetailResult {



    @SerializedName("id")
    @Expose
    String  id;

    @SerializedName("gist")
    @Expose
    AudioGist gist;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public AudioGist getGist() {
        return gist;
    }

    @SerializedName("creditBlocks")
    @Expose
    List<CreditBlock> creditBlocks;

    public List<CreditBlock> getCreditBlocks() {
        return creditBlocks;
    }

    @SerializedName("streamingInfo")
    @Expose
    StreamingInfo streamingInfo = null;

    public StreamingInfo getStreamingInfo() {
        return streamingInfo;
    }

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setStreamingInfo(this.streamingInfo);
        contentDatum.setAudioGist(this.gist);
        contentDatum.setCreditBlocks(this.creditBlocks);
        Gist gist=new Gist();
        gist.setId(this.gist.getId());
        gist.setMediaType(this.gist.getMediaType());
        gist.setContentType(this.gist.getContentType());
        gist.setTitle(this.gist.getTitle());
        gist.setDescription(this.gist.getDescription());
        gist.setRuntime(this.gist.getRuntime());
        gist.setPosterImageUrl(this.gist.getPosterImageUrl());
        contentDatum.setGist(gist);
        data.add(contentDatum);

        module.setContentData(data);
        appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }
}
