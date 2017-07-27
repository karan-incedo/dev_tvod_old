package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anas.azeem on 7/12/2017.
 * Owned by ViewLift, NYC
 */

public class AppCMSVideoDetail {

    @SerializedName("records")
    @Expose
    private List<ContentDatum> records;

    public List<ContentDatum> getRecords() {
        return records;
    }

    public void setRecords(List<ContentDatum> records) {
        this.records = records;
    }

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id, String moduleType) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        if (getRecords() != null) {
            data.addAll(getRecords());
        }

        module.setContentData(data);
        module.setModuleType(moduleType);
        appCMSPageAPI.setId(Id);
        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module);
        appCMSPageAPI.setModules(moduleList);

        return appCMSPageAPI;
    }
}
