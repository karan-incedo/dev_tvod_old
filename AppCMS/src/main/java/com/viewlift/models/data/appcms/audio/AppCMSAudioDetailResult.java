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
import com.viewlift.models.data.appcms.playlist.AudioList;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

@UseStag
public class AppCMSAudioDetailResult {

    @SerializedName("gist")
    @Expose
    Gist gist;

    public Gist getGist() {
        return gist;
    }
//
//    @SerializedName("creditBlocks")
//    @Expose
//    List<CreditBlock> creditBlocks;
//
//    public List<CreditBlock> getCreditBlocks() {
//        return creditBlocks;
//    }

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

        StreamingInfo streamingInfo = getStreamingInfo();
        data.add(streamingInfo.convertToContentDatum());

        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setGist(this.gist);
        data.add(contentDatum);
//
//        ContentDatum contentDatum1 = new ContentDatum();
//        contentDatum1.setCreditBlocks(this.creditBlocks);
//        data.add(contentDatum1);

        module.setContentData(data);
        appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }
}
