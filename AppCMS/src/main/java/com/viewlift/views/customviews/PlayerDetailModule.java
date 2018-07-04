package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.GsonBuilder;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.Map;

import static com.viewlift.Utils.loadJsonFromAssets;

/**
 * Created by viewlift on 6/28/17.
 */

@SuppressLint("ViewConstructor")
public class PlayerDetailModule extends ModuleView {
    private static final String TAG = PlayerDetailModule.class.getSimpleName();

    private static final int NUM_CHILD_VIEWS = 2;

    private final ModuleWithComponents moduleInfo;
    private final Module moduleAPI;
    private  Module subTrayModuleAPI;

    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final ViewCreator viewCreator;
    Context context;
    private ModuleView[] childViews;
    private GradientDrawable[] underlineViews;
    private int underlineColor;
    private int transparentColor;
    private int bgColor;
    private int loginBorderPadding;
    private AppCMSAndroidModules appCMSAndroidModules;
    PageView pageView;

    View downloadSeparator;

    @SuppressWarnings("unchecked")
    public PlayerDetailModule(Context context,
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
        this.childViews = new ModuleView[NUM_CHILD_VIEWS];
        this.underlineViews = new GradientDrawable[NUM_CHILD_VIEWS];
        this.loginBorderPadding = context.getResources().getInteger(R.integer.app_cms_login_underline_padding);
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
            subTrayModuleAPI=new Module();
            subTrayModuleAPI=(Module) moduleAPI.clone();
            AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
            underlineColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getPageTitleColor());
            transparentColor = ContextCompat.getColor(getContext(), android.R.color.transparent);
            bgColor = Color.parseColor(appCMSPresenter.getAppBackgroundColor());

            int textColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor());
            ViewGroup childContainer = getChildrenContainer();
            childContainer.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            NestedScrollView scrollView = new NestedScrollView(getContext());

            LinearLayout topLayoutContainer = new LinearLayout(getContext());
            MarginLayoutParams topLayoutContainerLayoutParams =
                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            topLayoutContainerLayoutParams.setMargins(0, 5, 0, 0);
            topLayoutContainer.setLayoutParams(topLayoutContainerLayoutParams);
            topLayoutContainer.setPadding(0, 0, 0, 0);
            topLayoutContainer.setOrientation(LinearLayout.VERTICAL);

            AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                    loadJsonFromAssets(context, "event_detail.json"),
                    AppCMSPageUI.class);
            ModuleWithComponents module = appCMSPageUI1.getModuleList().get(1);
            if (module == null) {
                module = moduleInfo;
            }/* else if (moduleInfo != null) {
                module.setId(moduleInfo.getId());
                module.setSettings(moduleInfo.getSettings());
                module.setSvod(moduleInfo.isSvod());
                module.setType(moduleInfo.getType());
                module.setView(moduleInfo.getView());
                module.setBlockName(moduleInfo.getBlockName());
            }*/

            if (module != null && module.getComponents() != null) {
                for (int i = 0; i < module.getComponents().size(); i++) {
                    Component component = module.getComponents().get(i);
                    ModuleWithComponents module1 = module;

                    if (jsonValueKeyMap.get(component.getType()) == AppCMSUIKeyType.PAGE_TRAY_05_MODULE_KEY) {
                        ModuleWithComponents module2 = component;

                        for(int k=0;k<moduleAPI.getContentData().get(0).getMonthlySchedule().size();k++) {
                             String key= (String) moduleAPI.getContentData().get(0).getMonthlySchedule().keySet().toArray()[k];
                            subTrayModuleAPI.setContentData(moduleAPI.getContentData().get(0).getMonthlySchedule().get(key));
                            subTrayModuleAPI.setContentType(subTrayModuleAPI.getContentData().get(0).getGist().getContentType());
                            subTrayModuleAPI.setTitle(key);
                            for (int j = 0; j < component.getComponents().size(); j++) {
                                Component subComp = component.getComponents().get(j);
                                ModuleView moduleView1 = new ModuleView<>(context, module2, true);
                                addChildComponents(moduleView1, subComp, appCMSAndroidModules, j,subTrayModuleAPI);
                                topLayoutContainer.addView(moduleView1);
                            }
                        }
                    }else{
                        ModuleView moduleView2 = new ModuleView<>(context, module1, true);
                        addChildComponents(moduleView2, component, appCMSAndroidModules, i, subTrayModuleAPI);
                        //moduleView2.setLayoutParams(topLayoutContainerLayoutParams);
                        topLayoutContainer.addView(moduleView2);

                    }
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
                        context.getString(R.string.app_cms_page_tray_05_module_key));
            } else {
                moduleView.setComponentHasView(i, false);
            }
        }
    }



}