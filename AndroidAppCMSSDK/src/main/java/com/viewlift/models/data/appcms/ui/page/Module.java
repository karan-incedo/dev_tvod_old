package com.viewlift.models.data.appcms.ui.page;

import java.util.List;

/**
 * Created by viewlift on 6/29/17.
 */

public interface Module {
    List<Component> getComponents();
    Layout getLayout();
    String getView();
}
