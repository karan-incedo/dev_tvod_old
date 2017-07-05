package com.viewlift.models.data.appcms.history;

/*
 * Created by Viewlift on 7/5/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
}
