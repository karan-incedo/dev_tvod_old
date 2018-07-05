package com.viewlift.models.data.appcms.api;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.playlist.AudioList;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

@UseStag
public class AppCMSTeamRoasterResult {
    public Roasters getRosters() {
        return rosters;
    }

    public void setRosters(Roasters rosters) {
        this.rosters = rosters;
    }

    public SeasonData getSeasonData() {
        return seasonData;
    }

    public void setSeasonData(SeasonData seasonData) {
        this.seasonData = seasonData;
    }

    @SerializedName("rosters")
    @Expose
    Roasters rosters;

    @SerializedName("seasonData")
    @Expose
    SeasonData seasonData;

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

//        ContentDatum contentDatum = new ContentDatum();
//        contentDatum.setTeamList(this.getSeasonData().getStandings().getTeam());

//        data.add(contentDatum);

        if (getSeasonData().getStandings().getTeam() != null) {
            for (Team records : getSeasonData().getStandings().getTeam()) {
                ContentDatum contentDatum1=new ContentDatum();
                contentDatum1.setTeam(records);
                data.add(contentDatum1);
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
