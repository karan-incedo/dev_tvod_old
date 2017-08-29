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
}
