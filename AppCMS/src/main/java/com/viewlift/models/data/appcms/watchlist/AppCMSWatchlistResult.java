package com.viewlift.models.data.appcms.watchlist;

/*
 * Created by Viewlift on 6/28/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.history.Record;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

@UseStag
public class AppCMSWatchlistResult {

    @SerializedName("records")
    @Expose
    List<Record> records = null;

    @SerializedName("nextOffset")
    @Expose
    Integer nextOffset;

    @SerializedName("limit")
    @Expose
    Integer limit;

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public Integer getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(Integer nextOffset) {
        this.nextOffset = nextOffset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public AppCMSPageAPI convertToAppCMSPageAPI(String Id) {
        AppCMSPageAPI appCMSPageAPI = new AppCMSPageAPI();
        Module module = new Module();
        List<ContentDatum> data = new ArrayList<>();

        if (getRecords() != null) {
            for (Record records : getRecords()) {
                data.add(records.convertToContentDatum());
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
