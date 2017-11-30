package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;

import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.Map;

@SuppressLint("ViewConstructor")
public class DraggableModule extends ModuleView {
//    private final ModuleWithComponents moduleInfo;
//    private final Module moduleAPI;
//    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
//    private final AppCMSPresenter appCMSPresenter;
//    private final ViewCreator viewCreator;
    Context context;
    private ModuleView[] childViews;
    private AppCMSAndroidModules appCMSAndroidModules;

    public DraggableModule(Context context, ModuleWithComponents module, boolean init) {
        super(context, module, init);
    }

    //
}
