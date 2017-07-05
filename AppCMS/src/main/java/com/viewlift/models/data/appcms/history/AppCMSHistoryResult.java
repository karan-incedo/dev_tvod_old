package com.viewlift.models.data.appcms.history;

/*
 * Created by Viewlift on 7/5/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;

import java.util.ArrayList;
import java.util.List;

public class AppCMSHistoryResult {

    @SerializedName("records")
    @Expose
    private List<Record> records = null;

    @SerializedName("limit")
    @Expose
    private Integer limit;

    public List<Record> getRecords() {

        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public AppCMSPageAPI convertToAppCMSPageAPI() {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        for (Record records : getRecords()) {
            data.add(records.convertToContentDatum());
        }

        module.setContentData(data);

        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }
}
