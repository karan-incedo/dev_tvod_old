package com.viewlift.models.data.appcms.ui.page;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.vimeo.stag.UseStag;

import java.io.Serializable;
import java.util.ArrayList;

@UseStag
public class ModuleListClass {

    ArrayList<ModuleList> moduleList;

    public ArrayList<ModuleList> getModuleList() {
        return moduleList;
    }

    public void setModuleList(ArrayList<ModuleList> moduleList) {
        this.moduleList = moduleList;
    }
}