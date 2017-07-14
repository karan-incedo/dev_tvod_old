package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.List;

@UseStag
public class AppCMSPageUI {

    @SerializedName("moduleList")
    @Expose
    List<ModuleList> moduleList = null;

    @SerializedName("version")
    @Expose
    String version;

    public List<ModuleList> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<ModuleList> moduleList) {
        this.moduleList = moduleList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
