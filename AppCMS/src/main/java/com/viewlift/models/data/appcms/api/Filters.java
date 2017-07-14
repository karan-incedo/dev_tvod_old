package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

@UseStag
public class Filters {

    @SerializedName("limit")
    @Expose
    int limit;

    @SerializedName("offset")
    @Expose
    Object offset;

    @SerializedName("tags")
    @Expose
    Object tags;

    @SerializedName("categories")
    @Expose
    Object categories;

    @SerializedName("sortBy")
    @Expose
    String sortBy;

    @SerializedName("contentStatus")
    @Expose
    String contentStatus;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Object getOffset() {
        return offset;
    }

    public void setOffset(Object offset) {
        this.offset = offset;
    }

    public Object getTags() {
        return tags;
    }

    public void setTags(Object tags) {
        this.tags = tags;
    }

    public Object getCategories() {
        return categories;
    }

    public void setCategories(Object categories) {
        this.categories = categories;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(String contentStatus) {
        this.contentStatus = contentStatus;
    }
}
