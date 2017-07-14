package com.viewlift.models.data.appcms.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class AppCMSPageAPI {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("path")
    @Expose
    List<String> path = null;

    @SerializedName("title")
    @Expose
    String title;

    @SerializedName("metadataMap")
    @Expose
    MetadataMap metadataMap;

    @SerializedName("modules")
    @Expose
    List<Module> modules = null;

    @SerializedName("moduleIds")
    @Expose
    List<String> moduleIds = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MetadataMap getMetadataMap() {
        return metadataMap;
    }

    public void setMetadataMap(MetadataMap metadataMap) {
        this.metadataMap = metadataMap;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public List<String> getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(List<String> moduleIds) {
        this.moduleIds = moduleIds;
    }

}
