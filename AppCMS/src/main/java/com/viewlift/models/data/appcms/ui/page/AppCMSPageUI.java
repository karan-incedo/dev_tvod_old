package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@UseStag
public class AppCMSPageUI implements Serializable {

    @SerializedName("moduleList")
    @Expose
    ArrayList<ModuleList> moduleList = null;

    @SerializedName("version")
    @Expose
    String version;

    boolean loadedFromNetwork;

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

    public boolean isLoadedFromNetwork() {
        return loadedFromNetwork;
    }

    public void setLoadedFromNetwork(boolean loadedFromNetwork) {
        this.loadedFromNetwork = loadedFromNetwork;
    }
}
