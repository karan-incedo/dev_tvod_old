package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.history.Record;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinay.singh on 1/24/2018.
 */

@UseStag
public class AppCMSLibraryResult {

    @SerializedName("records")
    @Expose
    List<Record> records = null;

    @SerializedName("nextOffset")
    @Expose
    int nextOffset;

    @SerializedName("limit")
    @Expose
    int limit;

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public int etNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(int nextOffset) {
        this.nextOffset = nextOffset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        if (getRecords() != null) {
            for (Record record : getRecords()) {
                if (record.getContentResponse() !=null) {
                    data.add(record.convertToContentDatum());
                }
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