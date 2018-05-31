package com.viewlift.models.data.appcms.api;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@UseStag
public class AppCMSEventArchieveResult {

    public List<LiveEvents> getLiveEvents() {
        return LiveEvents;
    }

    public void setLiveEvents(List<LiveEvents> liveEvents) {
        LiveEvents = liveEvents;
    }

    @SerializedName("LiveEvents")
    @Expose
    List<LiveEvents> LiveEvents = null;
    public Module convertToAppCMSPageModule() {
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setLiveEvents(LiveEvents);
        data.add(contentDatum);

        module.setContentData(data);


        return module;
    }
}
