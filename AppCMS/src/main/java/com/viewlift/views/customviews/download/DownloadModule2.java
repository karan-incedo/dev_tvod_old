package com.viewlift.views.customviews.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.GsonBuilder;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.ModuleWithComponents;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.adapters.AppCMSDownloadTabPagerAdapter;
import com.viewlift.views.customviews.ModuleView;
import com.viewlift.views.customviews.PageView;
import com.viewlift.views.customviews.ViewCreator;
import com.viewlift.views.rxbus.DownloadTabSelectorBus;

import java.util.Map;

import static com.viewlift.Utils.loadJsonFromAssets;

/**
 * Created by viewlift on 6/28/17.
 */

@SuppressLint("ViewConstructor")
public class DownloadModule2 extends ModuleView {
    private static final String TAG = DownloadModule2.class.getSimpleName();


    private final ModuleWithComponents moduleInfo;
    private final Module moduleAPI;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final ViewCreator viewCreator;
    Context context;
    private AppCMSAndroidModules appCMSAndroidModules;
    PageView pageView;
    TabLayout downloadTab;
    ViewPager downloadPager;
    public static final int AUDIO_TAB = 259;
    public static final int VIDEO_TAB = 260;

    @SuppressWarnings("unchecked")
    public DownloadModule2(Context context,
                           ModuleWithComponents moduleInfo,
                           Module moduleAPI,
                           Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                           AppCMSPresenter appCMSPresenter,
                           ViewCreator viewCreator,
                           AppCMSAndroidModules appCMSAndroidModules, PageView pageView) {
        super(context, moduleInfo, false);
        this.moduleInfo = moduleInfo;
        this.moduleAPI = moduleAPI;
        /*if (moduleAPI.getContentData() != null && moduleAPI.getContentData().get(0) != null &&
                moduleAPI.getContentData().get(0).getSeason() != null &&
                moduleAPI.getContentData().get(0).getSeason().get(0) != null &&
                moduleAPI.getContentData().get(0).getSeason().get(0).getTitle().contains("Season 1")) {
            Collections.reverse(moduleAPI.getContentData().get(0).getSeason());
        }*/
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

            ViewGroup childContainer = getChildrenContainer();
            childContainer.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

            ModuleWithComponents module = appCMSAndroidModules.getModuleListMap().get(moduleInfo.getBlockName());
            AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                    loadJsonFromAssets(context, "download.json"),
                    AppCMSPageUI.class);
            module = appCMSPageUI1.getModuleList().get(0);

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
                    if (jsonValueKeyMap.get(component.getType()) == AppCMSUIKeyType.PAGE_DOWNLOAD_TAB_KEY) {
                        for (int j = 0; j < component.getComponents().size(); j++) {
                            Component subComp = component.getComponents().get(j);
                            addChildComponents(moduleView1, subComp, appCMSAndroidModules, i);
                        }
                    }
                }
            }
            AppCMSDownloadTabPagerAdapter adapter = new AppCMSDownloadTabPagerAdapter(recyclerViewComponent, viewCreator, moduleAPI, appCMSAndroidModules,
                    jsonValueKeyMap, appCMSPresenter, moduleInfo);
            downloadPager.setAdapter(adapter);

            downloadTab.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
            downloadTab.setSelectedTabIndicatorHeight((int) (3 * getResources().getDisplayMetrics().density));
            downloadTab.setTabTextColors(Color.parseColor("#ffffff"),
                    Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getBlockTitleColor()));
            downloadTab.setTabMode(TabLayout.MODE_FIXED);
            downloadTab.setupWithViewPager(downloadPager);
            reduceMarginsInTabs(downloadTab, 70);
            downloadTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getText().toString().contains("Video")) {
                        appCMSPresenter.setDownloadTabSelected(VIDEO_TAB);
                    } else if (tab.getText().toString().contains("Audio")) {
                        appCMSPresenter.setDownloadTabSelected(AUDIO_TAB);
                    }
                    DownloadTabSelectorBus.instanceOf().setTab(appCMSPresenter.getDownloadTabSelected());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            downloadPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    DownloadTabSelectorBus.instanceOf().setTab(appCMSPresenter.getDownloadTabSelected());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            View root = downloadTab.getChildAt(0);
            if (root instanceof LinearLayout) {
                ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(getResources().getColor(android.R.color.darker_gray));
                drawable.setSize(2, 1);
                ((LinearLayout) root).setDividerPadding(10);
                ((LinearLayout) root).setDividerDrawable(drawable);
            }
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
        if (componentType == AppCMSUIKeyType.PAGE_TABLE_VIEW_KEY) {
            recyclerViewComponent = subComponent;
            return;
        }

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
                if (componentType != AppCMSUIKeyType.PAGE_TABLE_VIEW_KEY) {

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
                            context.getString(R.string.app_cms_page_download_tab_type));
                }
                switch (componentType) {
                    case PAGE_TABLAYOUT_KEY:
                        downloadTab = (TabLayout) componentView;
                        break;
                    case PAGE_VIEWPAGER_KEY:
                        downloadPager = (ViewPager) componentView;
                        break;
                }
            } else {
                moduleView.setComponentHasView(i, false);
            }
        }

    }


}
