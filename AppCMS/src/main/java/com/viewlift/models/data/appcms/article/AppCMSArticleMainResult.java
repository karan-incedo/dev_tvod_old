package com.viewlift.models.data.appcms.article;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.Module;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinay.singh on 1/24/2018.
 */

@UseStag
public class AppCMSArticleMainResult {

    @SerializedName("id")
    @Expose
    String id;


    @SerializedName("gist")
    @Expose
    Gist gist;

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setGist(this.gist);
        contentDatum.setId(this.id);
        data.add(contentDatum);


        module.setContentData(data);
        appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }
}
