package com.viewlift.models.data.appcms.article;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.Category;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.ContentDetails;
import com.viewlift.models.data.appcms.api.CreditBlock;
import com.viewlift.models.data.appcms.api.Gist;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.StreamingInfo;
import com.viewlift.models.data.appcms.api.Tag;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinay.singh on 1/24/2018.
 */

@UseStag
public class AppCMSArticleResult {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("gist")
    @Expose
    Gist gist;

    @SerializedName("contentDetails")
    ContentDetails contentDetails;

    @SerializedName("permalink")
    @Expose
    String permalink;

    @SerializedName("tags")
    @Expose
    List<Tag> tags = null;

    @SerializedName("categories")
    @Expose
    List<Category> categories = null;

    @SerializedName("creditBlocks")
    @Expose
    List<CreditBlock> creditBlocks = null;

    @SerializedName("streamingInfo")
    @Expose
    StreamingInfo streamingInfo;

    @SerializedName("relatedArticleIds")
    @Expose
    List<String> relatedArticleIds;

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        ContentDatum contentDatum = new ContentDatum();
        contentDatum.setGist(this.gist);
        contentDatum.setId(this.id);
        contentDatum.setStreamingInfo(this.streamingInfo);
        contentDatum.setContentDetails(this.contentDetails);
        contentDatum.setCategories(this.categories);
        contentDatum.setTags(this.tags);
        //contentDatum.setRelatedArticleIds(this.relatedArticleIds);
        data.add(contentDatum);

        module.setContentData(data);
        appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }

    public String getId() {
        return id;
    }
}
