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

    public AppCMSPageAPI convertToAppCMSPageModule(AppCMSPageAPI appCMSPageAPI) {

        for (int i = 0; i < 4; i++) {

            Fights fight = new Fights();
            fight=LiveEvents.get(0).getFights().get(0);
            fight.setFighter1_FirstName("test"+i);
            LiveEvents.get(0).getFights().add(fight);
        }
        if (appCMSPageAPI.getModules().size() > 0) {
            for (int i = 0; i < appCMSPageAPI.getModules().size(); i++) {
                if (appCMSPageAPI.getModules().get(i).getModuleType().equalsIgnoreCase("PersonDetailModule")) {
                    appCMSPageAPI.getModules().get(i).getContentData().get(0).setLiveEvents(LiveEvents);
                    break;
                }
            }
        }

        return appCMSPageAPI;
    }
}
