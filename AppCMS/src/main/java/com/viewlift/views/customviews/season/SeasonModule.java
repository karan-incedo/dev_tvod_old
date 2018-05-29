package com.viewlift.views.customviews.season;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.GsonBuilder;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.Season_;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.ModuleView;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.rxbus.SeasonTabSelectorBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.viewlift.Utils.loadJsonFromAssets;

/**
 * Created by viewlift on 6/28/17.
 */

@SuppressLint("ViewConstructor")
public class SeasonModule extends ModuleView {

    private static final String TAG = SeasonModule.class.getSimpleName();
    private final ModuleWithComponents moduleInfo;
    private final Module moduleAPI;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final ViewCreator viewCreator;
    Context context;
    private AppCMSAndroidModules appCMSAndroidModules;
    PageView pageView;
    TabLayout seasonTab;
    List<Season_> seasonList;

    @SuppressWarnings("unchecked")
    public SeasonModule(Context context,
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
        seasonList = new ArrayList<>();
        seasonList.addAll(moduleAPI.getContentData().get(0).getSeason());
        Collections.reverse(seasonList);
        init();
    }

    public void init() {
        if (moduleInfo != null &&
                moduleAPI != null &&
                jsonValueKeyMap != null &&
                appCMSPresenter != null &&
                viewCreator != null) {

            ViewGroup childContainer = getChildrenContainer();
            childContainer.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

            ModuleWithComponents module = appCMSAndroidModules.getModuleListMap().get(moduleInfo.getBlockName());
            AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                    loadJsonFromAssets(context, "show_detail.json"),
                    AppCMSPageUI.class);
            module = appCMSPageUI1.getModuleList().get(1);

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
            ModuleView moduleView1 = new ModuleView<>(context, module, true);


            if (module != null && module.getComponents() != null) {
                for (int i = 0; i < module.getComponents().size(); i++) {
                    Component component = module.getComponents().get(i);
                    if (jsonValueKeyMap.get(component.getType()) == AppCMSUIKeyType.PAGE_SEASON_TRAY_MODULE_KEY) {
                        for (int j = 0; j < component.getComponents().size(); j++) {
                            Component subComp = component.getComponents().get(j);
                            addChildComponents(moduleView1, subComp, appCMSAndroidModules, i);
                        }
                    }
                }
            }

//            seasonTab.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
//            seasonTab.setSelectedTabIndicatorHeight((int) (3 * getResources().getDisplayMetrics().density));
            seasonTab.setSelectedTabIndicatorHeight(0);
            seasonTab.setTabTextColors(Color.parseColor("#ffffff"),
                    Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor()));


            if (moduleAPI != null && moduleAPI.getContentData() != null && moduleAPI.getContentData().size() > 0 && moduleAPI.getContentData().get(0).getSeason() != null) {

//                if (moduleAPI.getContentData().get(0).getSeason().size() > 2) {
                seasonTab.setTabMode(TabLayout.MODE_SCROLLABLE);
//                } else {
//                    seasonTab.setTabMode(TabLayout.MODE_FIXED);
//                }
                seasonTab.setTabGravity(Gravity.LEFT);

                for (Season_ season : seasonList) {
                    TabLayout.Tab firstTab = seasonTab.newTab();
                    firstTab.setText(season.getTitle());
                    seasonTab.addTab(firstTab);
                }
            }

            reduceMarginsInTabs(seasonTab, 10);
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            seasonTab.setScrollX(seasonTab.getWidth());
                            if (seasonTab != null)
                                seasonTab.getTabAt(appCMSPresenter.getSelectedSeason()).select();
                        }
                    }, 100);
           /* int right = ((ViewGroup) seasonTab.getChildAt(0)).getChildAt(appCMSPresenter.getSelectedSeason()).getRight();
            seasonTab.scrollTo(right,0);
            seasonTab.getTabAt(appCMSPresenter.getSelectedSeason()).select();*/

            List<ContentDatum> adapterData = seasonList.get(appCMSPresenter.getSelectedSeason()).getEpisodes();
            SeasonTabSelectorBus.instanceOf().setTab(adapterData);
            seasonTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int position = tab.getPosition();
                    appCMSPresenter.setSelectedSeason(position);
                    List<ContentDatum> adapterData = seasonList.get(position).getEpisodes();
                    SeasonTabSelectorBus.instanceOf().setTab(adapterData);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
            /*View root = seasonTab.getChildAt(0);
            if (root instanceof LinearLayout) {
                ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(getResources().getColor(android.R.color.darker_gray));
                drawable.setSize(2, 1);
                ((LinearLayout) root).setDividerPadding(10);
                ((LinearLayout) root).setDividerDrawable(drawable);
            }*/
            childContainer.addView(moduleView1);
            if (module.getSettings() != null && !module.getSettings().isHidden()) {
                pageView.addModuleViewWithModuleId(module.getId(), moduleView1, false);
            }

        }
    }

    private void reduceMarginsInTabs(TabLayout tabLayout, int marginOffset) {

        View tabStrip = tabLayout.getChildAt(0);
        if (tabStrip instanceof ViewGroup) {
            ViewGroup tabStripGroup = (ViewGroup) tabStrip;
            for (int i = 0; i < ((ViewGroup) tabStrip).getChildCount(); i++) {
                View tabView = tabStripGroup.getChildAt(i);
                if (tabView.getLayoutParams() instanceof MarginLayoutParams) {
                    ((MarginLayoutParams) tabView.getLayoutParams()).leftMargin = marginOffset;
                    ((MarginLayoutParams) tabView.getLayoutParams()).rightMargin = marginOffset;
                }
            }
            tabLayout.requestLayout();
        }
    }

    Component recyclerViewComponent;

    private void addChildComponents(ModuleView moduleView,
                                    Component subComponent,
                                    final AppCMSAndroidModules appCMSAndroidModules, int i) {
        ViewCreator.ComponentViewResult componentViewResult = viewCreator.getComponentViewResult();
        if (componentViewResult.onInternalEvent != null) {
            appCMSPresenter.addInternalEvent(componentViewResult.onInternalEvent);
        }
        ViewGroup subComponentChildContainer = moduleView.getChildrenContainer();
        float parentYAxis = 2 * getYAxis(getContext(), subComponent.getLayout(), 0.0f);
        AppCMSUIKeyType componentType = jsonValueKeyMap.get(subComponent.getType());
        if (componentType == null) {
            componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }
        AppCMSUIKeyType componentKey = jsonValueKeyMap.get(subComponent.getKey());
        if (componentKey == null) {
            componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }
       /* if (componentType == AppCMSUIKeyType.PAGE_COLLECTIONGRID_KEY) {
            recyclerViewComponent = subComponent;
            return;
        }*/

        if (componentViewResult != null && subComponentChildContainer != null) {
            viewCreator.createComponentView(getContext(),
                    subComponent,
                    subComponent.getLayout(),
                    moduleAPI,
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

                float componentYAxis = getYAxis(getContext(),
                        subComponent.getLayout(),
                        0.0f);
                if (!subComponent.isyAxisSetManually()) {
                    setYAxis(getContext(),
                            subComponent.getLayout(),
                            componentYAxis - parentYAxis);
                    subComponent.setyAxisSetManually(true);
                }
                subComponentChildContainer.addView(componentView);
                moduleView.setComponentHasView(i, true);
                moduleView.setViewMarginsFromComponent(subComponent,
                        componentView,
                        subComponent.getLayout(),
                        subComponentChildContainer,
                        false,
                        jsonValueKeyMap,
                        componentViewResult.useMarginsAsPercentagesOverride,
                        componentViewResult.useWidthOfScreen,
                        context.getString(R.string.app_cms_page_season_tray_module_key));
            }
            switch (componentType) {
                case PAGE_TABLAYOUT_KEY:
                    seasonTab = (TabLayout) componentView;
                    break;
                case PAGE_VIEWPAGER_KEY:
                    seasonTab = (TabLayout) componentView;
                    // seasonPager = (ViewPager) componentView;
                    break;
            }
        } else {
            moduleView.setComponentHasView(i, false);
        }
    }
}
