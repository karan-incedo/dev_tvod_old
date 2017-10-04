package com.viewlift.models.data.appcms.ui.android;

import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.vimeo.stag.UseStag;

import java.util.Map;

/**
 * Created by viewlift on 10/3/17.
 */

@UseStag
public class AppCMSAndroidModules {
    Map<String, ModuleList> moduleListMap;

    public Map<String, ModuleList> getModuleListMap() {
        return moduleListMap;
    }

    public void setModuleListMap(Map<String, ModuleList> moduleListMap) {
        this.moduleListMap = moduleListMap;
    }
}
