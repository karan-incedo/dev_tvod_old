
package com.viewlift.models.data.appcms.ui.page;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppCMSPageUI {

    @SerializedName("moduleList")
    @Expose
    private List<ModuleList> moduleList = null;
    @SerializedName("version")
    @Expose
    private String version;

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
