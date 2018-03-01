package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.OnInternalEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by anas.azeem on 1/2/2018.
 * Owned by ViewLift, NYC
 */

public class ShowDetailModuleView extends TVModuleView {
    private static final String TAG = ShowDetailModuleView.class.getSimpleName();
    private final AppCMSPageAPI appCMSPageAPI;

    public ShowDetailModuleView(Context context,
                                ModuleWithComponents module,
                                Module moduleAPI,
                                AppCMSPageAPI appCMSPageAPI,
                                TVViewCreator tvViewCreator,
                                AppCMSPresenter appCMSPresenter,
                                Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
        super(context, module);
        this.appCMSPageAPI = appCMSPageAPI;
        initView(context, module,moduleAPI, tvViewCreator, appCMSPresenter, jsonValueKeyMap);
    }

    public void initView(Context context,
                         ModuleWithComponents module,
                         Module moduleAPI, TVViewCreator tvViewCreator,
                         AppCMSPresenter appCMSPresenter,
                         Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
        TVViewCreator.ComponentViewResult componentViewResult =
                tvViewCreator.getComponentViewResult();
        List<OnInternalEvent> onInternalEvents = new ArrayList<>();
        for (Component component : module.getComponents()) {
            if (!Arrays.asList(context.getResources().getStringArray(R.array.app_cms_tray_modules)).contains(component.getType())) {
                tvViewCreator.createComponentView(context,
                        component,
                        component.getLayout(),
                        moduleAPI,
                        null,
                        component.getSettings(),
                        jsonValueKeyMap,
                        appCMSPresenter,
                        false,
                        module.getView(),
                        false);

                if (componentViewResult.onInternalEvent != null) {
                    onInternalEvents.add(componentViewResult.onInternalEvent);
                }

                View componentView = componentViewResult.componentView;
                if (componentView != null) {
                    setViewMarginsFromComponent(component,
                            componentView,
                            component.getLayout(),
                            this,
                            jsonValueKeyMap,
                            false,
                            false,
                            component.getView());

                    this.getChildrenContainer().addView(componentView);
                }
            } else {
                Log.d(TAG, "It's a tray!");
                for (Component trayComponents: component.getComponents()) {
                    tvViewCreator.createTrayModule(
                            context,
                            trayComponents,
                            module.getLayout(),
                            (ModuleList) module,
                            moduleAPI,
                            null,
                            jsonValueKeyMap,
                            appCMSPresenter,
                            appCMSPageAPI,
                            false,
                            false);
                }
            }
        }
    }
}
