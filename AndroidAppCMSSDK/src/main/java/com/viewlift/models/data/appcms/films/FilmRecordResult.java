
package com.viewlift.models.data.appcms.films;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilmRecordResult {

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
