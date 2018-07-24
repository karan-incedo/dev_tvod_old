package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinay.singh on 1/24/2018.
 */

@UseStag
public class AppCMSLibraryResult {

    public List<Videos> getVideos() {
        return videos;
    }

    public void setVideos(List<Team> teams) {
        this.videos = videos;
    }

    @SerializedName("videos")
    @Expose
    List<Videos> videos = null;

    @SerializedName("seasons")
    @Expose
    List<Season_library> seasons = null;


    public List<Season_library> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season_library> seasons) {
        this.seasons = seasons;
    }

    public List<Bundles_library> getBundles() {
        return bundles;
    }

    public void setBundles(List<Bundles_library> bundles) {
        this.bundles = bundles;
    }

    @SerializedName("bundles")
    @Expose
    List<Bundles_library> bundles = null;



    public AppCMSPageAPI convertToAppCMSPageAPI() {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();


//        ContentDatum contentDatum = new ContentDatum();
//        contentDatum.setTeamList(this.getSeasonData().getStandings().getTeam());

//        data.add(contentDatum);

        if (getVideos() != null) {
            for (Videos videos : getVideos()) {
                ContentDatum contentDatum1=new ContentDatum();
                Gist gistObj=new Gist();
                gistObj.setId(videos.getId());
                gistObj.setImageGist(videos.getImageGist());
                gistObj.setTitle(videos.getTitle());
                contentDatum1.setGist(gistObj);
                contentDatum1.setVideoData(videos);
                data.add(contentDatum1);
            }
            for (Videos videos : getVideos()) {
                ContentDatum contentDatum1=new ContentDatum();
                Gist gistObj=new Gist();
                gistObj.setId(videos.getId());
                gistObj.setImageGist(videos.getImageGist());


                gistObj.setTitle(videos.getTitle());
                contentDatum1.setGist(gistObj);
                contentDatum1.setVideoData(videos);
                data.add(contentDatum1);
            }
        }
        module.setContentData(data);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }
}
