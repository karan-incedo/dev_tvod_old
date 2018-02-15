package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.ArrayList;

@UseStag
public class AppCMSPageUI implements Serializable {

    @SerializedName("moduleList")
    @Expose
    ArrayList<ModuleList> moduleList = null;

    @SerializedName("caching")
    @Expose
    Caching caching;

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

    public Caching getCaching() {
        return caching;
    }

    public void setCaching(Caching caching) {
        this.caching = caching;
    }
}
