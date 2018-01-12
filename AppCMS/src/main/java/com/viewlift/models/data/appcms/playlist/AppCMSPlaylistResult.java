package com.viewlift.models.data.appcms.playlist;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

@UseStag
public class AppCMSPlaylistResult {
//    @SerializedName("id")
//    @Expose
//    int id;

    @SerializedName("audioList")
    @Expose
    List<AudioList> audioList = null;


    public List<AudioList> getAudioList() {
        return audioList;
    }

//    public int getId() {
//        return id;
//    }

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        ContentDatum contentDatum = new ContentDatum();
        if (getAudioList() != null) {
            contentDatum.setAudioList(this.getAudioList());
            data.add(contentDatum);
        }
        module.setContentData(data);
        appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }

}
