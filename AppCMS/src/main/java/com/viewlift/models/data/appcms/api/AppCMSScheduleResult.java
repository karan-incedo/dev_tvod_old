package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vinay.singh on 1/24/2018.
 */

@UseStag
public class AppCMSScheduleResult {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("gist")
    @Expose
    Gist gist;

    @SerializedName("contentDetails")
    ContentDetails contentDetails;

    @SerializedName("site")
    @Expose
    String site;

    @SerializedName("tags")
    @Expose
    List<Tag> tags = null;

    @SerializedName("categories")
    @Expose
    List<Category> categories = null;

    @SerializedName("seo")
    @Expose
    List<Seo> seo = null;

    @SerializedName("scheduleResult")
    @Expose
    HashMap<String,List<ContentDatum>> scheduleResult;



    public AppCMSPageAPI convertToAppCMSPageAPI(HashMap<String,List<ContentDatum>> monthlySchedule) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setMonthlySchedule(monthlySchedule);
        data.add(contentDatum);

        module.setContentData(data);
        //appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }

    public AppCMSPageAPI convertToAppCMSPageAPI(List<ContentDatum> data) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();

        module.setContentData(data);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }
    public String getId() {
        return id;
    }

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

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Seo> getSeo() {
        return seo;
    }

    public void setSeo(List<Seo> seo) {
        this.seo = seo;
    }

    public HashMap<String, List<ContentDatum>> getScheduleResult() {
        return scheduleResult;
    }

    public void setScheduleResult(HashMap<String, List<ContentDatum>> scheduleResult) {
        this.scheduleResult = scheduleResult;
    }

}
