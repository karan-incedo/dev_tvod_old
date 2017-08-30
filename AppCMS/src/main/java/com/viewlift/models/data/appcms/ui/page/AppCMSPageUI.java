package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.util.ArrayList;
import java.util.List;

@UseStag
public class AppCMSPageUI {

    @SerializedName("moduleList")
    @Expose
    ArrayList<ModuleList> moduleList = null;

    @SerializedName("version")
    @Expose
    String version;

    public ArrayList<ModuleList> getModuleList() {
        return moduleList;
    }

    public void setModuleList(ArrayList<ModuleList> moduleList) {
        this.moduleList = moduleList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
