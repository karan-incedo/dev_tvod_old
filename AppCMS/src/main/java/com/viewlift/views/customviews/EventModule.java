package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.GsonBuilder;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.viewlift.Utils.loadJsonFromAssets;

/**
 * Created by viewlift on 6/28/17.
 */

@SuppressLint("ViewConstructor")
public class EventModule extends ModuleView {
    private static final String TAG = EventModule.class.getSimpleName();

    private static final int NUM_CHILD_VIEWS = 2;

    private final ModuleWithComponents moduleInfo;
    private final Module moduleAPI;

    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final ViewCreator viewCreator;
    Context context;
    private  Module subTrayModuleAPI;

    private AppCMSAndroidModules appCMSAndroidModules;
    PageView pageView;

    View downloadSeparator;

    @SuppressWarnings("unchecked")
    public EventModule(Context context,
                       ModuleWithComponents moduleInfo,
                       Module moduleAPI,
                       Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                       AppCMSPresenter appCMSPresenter,
                       ViewCreator viewCreator,
                       AppCMSAndroidModules appCMSAndroidModules, PageView pageView) {
        super(context, moduleInfo, false);
        this.moduleInfo = moduleInfo;
        this.moduleAPI = moduleAPI;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        this.viewCreator = viewCreator;

        this.context = context;
        this.appCMSAndroidModules = appCMSAndroidModules;
        this.pageView = pageView;
        init();
    }

    public void init() {
        if (moduleInfo != null &&
                moduleAPI != null &&
                jsonValueKeyMap != null &&
                appCMSPresenter != null &&
                viewCreator != null) {
            AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();

            subTrayModuleAPI=new Module();
            subTrayModuleAPI=(Module) moduleAPI.clone();
            ViewGroup childContainer = getChildrenContainer();
            childContainer.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            NestedScrollView scrollView = new NestedScrollView(getContext());

            LinearLayout topLayoutContainer = new LinearLayout(getContext());
            MarginLayoutParams topLayoutContainerLayoutParams =
                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            topLayoutContainerLayoutParams.setMargins(0, 0, 0, 0);
            topLayoutContainer.setLayoutParams(topLayoutContainerLayoutParams);
            topLayoutContainer.setPadding(0, 0, 0, 0);
            topLayoutContainer.setOrientation(LinearLayout.VERTICAL);

            AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                    loadJsonFromAssets(context, "player_detail.json"),
                    AppCMSPageUI.class);
            ModuleWithComponents module = appCMSPageUI1.getModuleList().get(1);
            if (module == null) {
                module = moduleInfo;
            } else if (moduleInfo != null) {
                module.setId(moduleInfo.getId());
                module.setSettings(moduleInfo.getSettings());
                module.setSvod(moduleInfo.isSvod());
                module.setType(moduleInfo.getType());
                module.setView(moduleInfo.getView());
                module.setBlockName(moduleInfo.getBlockName());
            }

            if (module != null && module.getComponents() != null) {
                for (int i = 0; i < module.getComponents().size(); i++) {
                    Component component = module.getComponents().get(i);
                    ModuleWithComponents module1 = component;

                    ModuleView moduleView1 = new ModuleView<>(context, module1, true);
                    for (int j = 0; j < component.getComponents().size(); j++) {
                        Component component1 = component.getComponents().get(j);

//                        ModuleView moduleView2 = new ModuleView<>(context, module2, true);
                        if (jsonValueKeyMap.get(component1.getKey()) == AppCMSUIKeyType.PAGE_FIGHT_TABLE_KEY) {
                            List<ContentDatum> data = new ArrayList<>();

                            for(int k=0;k< moduleAPI.getContentData().get(0).getLiveEvents().get(0).getFights().size();k++){


                                ContentDatum contentData = new ContentDatum();
                                contentData.setFights(moduleAPI.getContentData().get(0).getLiveEvents().get(0).getFights().get(k));
                                data.add(contentData);


                            }
                            subTrayModuleAPI.setContentData(data);
                            addChildComponents(moduleView1, component1, appCMSAndroidModules, j,subTrayModuleAPI);

                        }else{
                            addChildComponents(moduleView1, component1, appCMSAndroidModules, j,moduleAPI);

                        }
//                    View childView = viewCreator.createModuleView(context, module, moduleAPI,
//                            appCMSAndroidModules,
//                            pageView,
//                            jsonValueKeyMap,
//                            appCMSPresenter);
                    }
                    topLayoutContainer.addView(moduleView1);


//                        addChildComponents(moduleView1, component, appCMSAndroidModules, i);
//                    topLayoutContainer.addView(moduleView1);
//                    View childView = viewCreator.createModuleView(context, module, moduleAPI,
//                            appCMSAndroidModules,
//                            pageView,
//                            jsonValueKeyMap,
//                            appCMSPresenter);
                }
            }
//            if (module != null && module.getComponents() != null) {
//                for (int i = 0; i < module.getComponents().size(); i++) {
//                    Component component = module.getComponents().get(i);
//                    ModuleView moduleView1 = new ModuleView<>(context, module, true);
//
//                        addChildComponents(moduleView1, component, appCMSAndroidModules, i);
//
//                    topLayoutContainer.addView(moduleView1);
//
//                }
//            }
            scrollView.addView(topLayoutContainer);
            pageView.addView(scrollView);
            childContainer.addView(pageView);

        }
    }

    private void addChildComponents(ModuleView moduleView,
                                    Component subComponent,
                                    final AppCMSAndroidModules appCMSAndroidModules, int i, Module subTrayModuleAPI) {
        ViewCreator.ComponentViewResult componentViewResult = viewCreator.getComponentViewResult();
        if (componentViewResult.onInternalEvent != null) {
            appCMSPresenter.addInternalEvent(componentViewResult.onInternalEvent);
        }
        ViewGroup subComponentChildContainer = moduleView.getChildrenContainer();
        float parentYAxis = 2 * getYAxis(getContext(), subComponent.getLayout(), 0.0f);
        if (componentViewResult != null && subComponentChildContainer != null) {
            viewCreator.createComponentView(getContext(),
                    subComponent,
                    subComponent.getLayout(),
                    subTrayModuleAPI,
                    appCMSAndroidModules,
                    null,
                    moduleInfo.getSettings(),
                    jsonValueKeyMap,
                    appCMSPresenter,
                    false,
                    moduleInfo.getView(),
                    moduleInfo.getId());
            View componentView = componentViewResult.componentView;
            if (componentView != null) {

                subComponentChildContainer.addView(componentView);
                moduleView.setComponentHasView(i, true);
                setViewMarginsFromComponent(subComponent,
                        componentView,
                        subComponent.getLayout(),
                        subComponentChildContainer,
                        false,
                        jsonValueKeyMap,
                        componentViewResult.useMarginsAsPercentagesOverride,
                        componentViewResult.useWidthOfScreen,
                        context.getString(R.string.app_cms_event_detail_module));
            } else {
                moduleView.setComponentHasView(i, false);
            }
        }
    }


}
