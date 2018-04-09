package com.viewlift.models.data.appcms.history;

/*
 * Created by Viewlift on 7/5/2017.
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
public class AppCMSHistoryResult {

    @SerializedName("records")
    @Expose
    List<Record> records = null;

    @SerializedName("limit")
    @Expose
    int limit;

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public AppCMSPageAPI convertToAppCMSPageAPI(String apiId) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();

        List<ContentDatum> data = new ArrayList<>();

        if (getRecords() != null) {
            for (Record records : getRecords()) {
                if (records.getContentResponse() !=null) {
                    data.add(records.convertToContentDatum());
                }
            }
        }

        module.setContentData(data);
        appCMSPageAPI.setId(apiId);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }
}
