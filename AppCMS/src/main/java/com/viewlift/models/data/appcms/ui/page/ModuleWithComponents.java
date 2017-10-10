package com.viewlift.models.data.appcms.ui.page;

import java.util.List;

/**
 * Created by viewlift on 6/29/17.
 */

public interface ModuleWithComponents {

    List<Component> getComponents();

    Layout getLayout();

    String getView();

    Settings getSettings();

    String getId();

    boolean isSvod();

    String getBlockName();

    void setBlockName(String blockName);

    String getType();

    void setType(String type);

    void setId(String id);

    void setSettings(Settings settings);

    void setSvod(boolean svod);

    void setView(String view);
}
