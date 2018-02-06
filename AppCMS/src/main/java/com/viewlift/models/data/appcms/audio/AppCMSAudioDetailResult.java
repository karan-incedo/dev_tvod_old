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
    String id;

    @SerializedName("gist")
    @Expose
    Gist gist;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Gist getGist() {
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
        contentDatum.setGist(this.gist);
        contentDatum.setCreditBlocks(this.creditBlocks);
        data.add(contentDatum);

        module.setContentData(data);
        appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }

    public void setGist(Gist gist) {
        this.gist = gist;
    }

    public void setCreditBlocks(List<CreditBlock> creditBlocks) {
        this.creditBlocks = creditBlocks;
    }

    public void setStreamingInfo(StreamingInfo streamingInfo) {
        this.streamingInfo = streamingInfo;
    }
}
