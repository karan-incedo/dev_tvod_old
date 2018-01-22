package com.viewlift.models.data.appcms.playlist;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.audio.AudioGist;
import com.viewlift.models.data.appcms.history.Record;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

@UseStag
public class AppCMSPlaylistResult {
    @SerializedName("id")
    @Expose
    String id;


    @SerializedName("gist")
    @Expose
    AudioGist gist;

    public AudioGist getGist() {
        return gist;
    }

    public void setGist(AudioGist gist) {
        this.gist = gist;
    }

    @SerializedName("audioList")
    @Expose
    List<AudioList> audioList = null;


    public List<AudioList> getAudioList() {
        return audioList;
    }

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setAudioList(this.audioList);
        contentDatum.setAudioGist(this.gist);
        contentDatum.setId(this.id);
        data.add(contentDatum);

        if (getAudioList() != null) {
            for (AudioList records : getAudioList()) {
                data.add(records.convertToContentDatum());
            }
        }

        module.setContentData(data);
        appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }

}