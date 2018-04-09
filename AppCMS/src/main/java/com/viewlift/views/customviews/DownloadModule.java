package com.viewlift.views.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.viewlift.views.rxbus.DownloadTabSelectorBus;

import java.util.Map;

import rx.subjects.PublishSubject;

import static com.viewlift.Utils.loadJsonFromAssets;

/**
 * Created by viewlift on 6/28/17.
 */

@SuppressLint("ViewConstructor")
public class DownloadModule extends ModuleView {
    private static final String TAG = DownloadModule.class.getSimpleName();

    private static final int NUM_CHILD_VIEWS = 2;

    private final ModuleWithComponents moduleInfo;
    private final Module moduleAPI;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final AppCMSPresenter appCMSPresenter;
    private final ViewCreator viewCreator;
    Context context;
    private Button[] buttonSelectors;
    private ModuleView[] childViews;
    private GradientDrawable[] underlineViews;
    private int underlineColor;
    private int transparentColor;
    private int bgColor;
    private int loginBorderPadding;
    private AppCMSAndroidModules appCMSAndroidModules;
    PageView pageView;
    public static final int AUDIO_TAB = 259;
    public static final int VIDEO_TAB = 260;
    View downloadSeparator;

    @SuppressWarnings("unchecked")
    public DownloadModule(Context context,
                          ModuleWithComponents moduleInfo,
                          Module moduleAPI,
                          Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                          AppCMSPresenter appCMSPresenter,
                          ViewCreator viewCreator,
                          AppCMSAndroidModules appCMSAndroidModules, PageView pageView) {
        super(context, moduleInfo, false);
//        this.moduleInfo = moduleInfo;
        this.moduleAPI = moduleAPI;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.appCMSPresenter = appCMSPresenter;
        this.viewCreator = viewCreator;
        this.buttonSelectors = new Button[NUM_CHILD_VIEWS];
        this.childViews = new ModuleView[NUM_CHILD_VIEWS];
        this.underlineViews = new GradientDrawable[NUM_CHILD_VIEWS];
        this.loginBorderPadding = context.getResources().getInteger(R.integer.app_cms_login_underline_padding);
        this.context = context;
        this.appCMSAndroidModules = appCMSAndroidModules;
        this.pageView = pageView;
        //TODO : remove local json once we recieve from API
        AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                loadJsonFromAssets(context, "download.json"),
                AppCMSPageUI.class);
        this.moduleInfo = appCMSPageUI1.getModuleList().get(1);
        init();
    }

    public void init() {
        if (moduleInfo != null &&
                moduleAPI != null &&
                jsonValueKeyMap != null &&
                appCMSPresenter != null &&
                viewCreator != null) {
            AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
            underlineColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getPageTitleColor());
            transparentColor = ContextCompat.getColor(getContext(), android.R.color.transparent);
            bgColor = Color.parseColor(appCMSPresenter.getAppBackgroundColor());

            int textColor = Color.parseColor(appCMSMain.getBrand().getGeneral().getTextColor());
            ViewGroup childContainer = getChildrenContainer();
            childContainer.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

            LinearLayout topLayoutContainer = new LinearLayout(getContext());
            MarginLayoutParams topLayoutContainerLayoutParams =
                    new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            topLayoutContainerLayoutParams.setMargins(0, 0, 0, 0);
            topLayoutContainer.setLayoutParams(topLayoutContainerLayoutParams);
            topLayoutContainer.setPadding(0, 0, 0, 0);
            topLayoutContainer.setOrientation(LinearLayout.VERTICAL);

            LinearLayout downloadModuleSwitcherContainer = new LinearLayout(getContext());
            downloadModuleSwitcherContainer.setOrientation(LinearLayout.HORIZONTAL);
            MarginLayoutParams downloadModuleContainerLayoutParams =
                    new MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            downloadModuleContainerLayoutParams.setMargins((int) convertDpToPixel(getContext().getResources().getInteger(R.integer.app_cms_login_selector_margin), getContext()),
                    0,
                    (int) convertDpToPixel(getContext().getResources().getInteger(R.integer.app_cms_login_selector_margin), getContext()),
                    0);
            downloadModuleSwitcherContainer.setLayoutParams(downloadModuleContainerLayoutParams);
            downloadModuleSwitcherContainer.setBackgroundColor(bgColor);
            downloadModuleSwitcherContainer.setPadding(0, 0, 0, 0);

            topLayoutContainer.addView(downloadModuleSwitcherContainer);

            ModuleWithComponents module = appCMSAndroidModules.getModuleListMap().get(moduleInfo.getBlockName());
            //TODO : remove local json once we recieve from API
            AppCMSPageUI appCMSPageUI1 = new GsonBuilder().create().fromJson(
                    loadJsonFromAssets(context, "download.json"),
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
            ViewGroup childrenContainer = moduleView1.getChildrenContainer();

            ViewCreator.AdjustOtherState adjustOthers = ViewCreator.AdjustOtherState.IGNORE;
            if (module.getSettings() != null && !module.getSettings().isHidden()) {
                pageView.addModuleViewWithModuleId(module.getId(), moduleView1, false);
            }

            if (module != null && module.getComponents() != null) {
                for (int i = 0; i < module.getComponents().size(); i++) {
                    Component component = module.getComponents().get(i);
                    if (jsonValueKeyMap.get(component.getType()) == AppCMSUIKeyType.PAGE_DOWNLOAD_VIDEO_TAB_COMPONENT_KEY) {
                        buttonSelectors[0] = new Button(getContext());
                        LinearLayout.LayoutParams videoSelectorLayoutParams =
                                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                        videoSelectorLayoutParams.weight = 1;
                        buttonSelectors[0].setText(R.string.app_cms_download_tab_video_title);
                        buttonSelectors[0].setTextColor(textColor);
                        buttonSelectors[0].setBackgroundColor(bgColor);
                        buttonSelectors[0].setLayoutParams(videoSelectorLayoutParams);
                        buttonSelectors[0].setTransformationMethod(null);

                        buttonSelectors[0].setOnClickListener((v) -> {
                            selectChild(0);
                            unselectChild(1);
                            DownloadTabSelectorBus.instanceOf().setTab(VIDEO_TAB);
                            appCMSPresenter.setDownloadTabSelected(VIDEO_TAB);
                        });

                        underlineViews[0] = new GradientDrawable();
                        underlineViews[0].setShape(GradientDrawable.LINE);
                        buttonSelectors[0].setCompoundDrawablePadding(loginBorderPadding);
                        Rect textBounds = new Rect();
                        Paint textPaint = buttonSelectors[0].getPaint();
                        textPaint.getTextBounds(buttonSelectors[0].getText().toString(),
                                0,
                                buttonSelectors[0].getText().length(),
                                textBounds);
                        Rect bounds = new Rect(0,
                                textBounds.top,
                                textBounds.width() + loginBorderPadding,
                                textBounds.bottom);
                        underlineViews[0].setBounds(bounds);
                        buttonSelectors[0].setCompoundDrawables(null, null, null, underlineViews[0]);
                        downloadModuleSwitcherContainer.addView(buttonSelectors[0]);

                        ModuleView moduleView = new ModuleView<>(getContext(), component, false);
                        setViewHeight(getContext(), component.getLayout(), LayoutParams.WRAP_CONTENT);
                        childViews[0] = moduleView;
                        addChildComponents(moduleView, component, appCMSAndroidModules);
                        topLayoutContainer.addView(moduleView);
                    } else if (jsonValueKeyMap.get(component.getType()) == AppCMSUIKeyType.PAGE_DOWNLOAD_AUDIO_TAB_COMPONENT_KEY) {

                        buttonSelectors[1] = new Button(getContext());
                        LinearLayout.LayoutParams audioSelectorLayoutParams =
                                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                        audioSelectorLayoutParams.weight = 1;
                        buttonSelectors[1].setText(R.string.app_cms_download_tab_audio_title);
                        buttonSelectors[1].setTransformationMethod(null);
                        buttonSelectors[1].setTextColor(textColor);
                        buttonSelectors[1].setBackgroundColor(bgColor);
                        audioSelectorLayoutParams.gravity = Gravity.END;
                        buttonSelectors[1].setLayoutParams(audioSelectorLayoutParams);
                        buttonSelectors[1].setOnClickListener((v) -> {
                            selectChild(1);
                            unselectChild(0);
                            DownloadTabSelectorBus.instanceOf().setTab(AUDIO_TAB);
                            appCMSPresenter.setDownloadTabSelected(AUDIO_TAB);
                        });

                        underlineViews[1] = new GradientDrawable();
                        underlineViews[1].setShape(GradientDrawable.LINE);
                        buttonSelectors[1].setCompoundDrawablePadding(loginBorderPadding);
                        Rect textBounds = new Rect();
                        Paint textPaint = buttonSelectors[1].getPaint();
                        textPaint.getTextBounds(buttonSelectors[1].getText().toString(),
                                0,
                                buttonSelectors[1].getText().length(),
                                textBounds);
                        Rect bounds = new Rect(0,
                                textBounds.top,
                                textBounds.width() + loginBorderPadding,
                                textBounds.bottom);
                        underlineViews[1].setBounds(bounds);
                        buttonSelectors[1].setCompoundDrawables(null, null, null, underlineViews[1]);
                        downloadModuleSwitcherContainer.addView(buttonSelectors[1]);

                        ModuleView moduleView = new ModuleView<>(getContext(), component, false);
                        setViewHeight(getContext(), component.getLayout(), LayoutParams.WRAP_CONTENT);
                        childViews[1] = moduleView;
                        addChildComponents(moduleView, component, appCMSAndroidModules);
                        topLayoutContainer.addView(moduleView);
                    } else {
                        viewCreator.createComponentView(getContext(),
                                component,
                                component.getLayout(),
                                moduleAPI,
                                appCMSAndroidModules,
                                pageView,
                                module.getSettings(),
                                jsonValueKeyMap,
                                appCMSPresenter,
                                false,
                                module.getView(),
                                module.getId());
                        ViewCreator.ComponentViewResult componentViewResult = viewCreator.getComponentViewResult();
                        View componentView = componentViewResult.componentView;

                        if (componentView != null) {
                            if (component.getType() != null &&
                                    component.getType().contains(context.getString(R.string.app_cms_page_separator_key))) {
                                downloadSeparator = componentView;
                            }
                            if (componentViewResult.onInternalEvent != null) {
                                appCMSPresenter.addInternalEvent(componentViewResult.onInternalEvent);
                            }
                            if (componentViewResult.addToPageView) {
                                pageView.addView(componentView);
                            } else {
                                if (component.isHeaderView()) {
                                    pageView.addToHeaderView(componentView);
                                } else {
                                    childrenContainer.addView(componentView);
                                }
                                moduleView1.setComponentHasView(i, true);
                                moduleView1.setViewMarginsFromComponent(component,
                                        componentView,
                                        moduleView1.getLayout(),
                                        childrenContainer,
                                        false,
                                        jsonValueKeyMap,
                                        componentViewResult.useMarginsAsPercentagesOverride,
                                        componentViewResult.useWidthOfScreen,
                                        module.getView());
                                if ((adjustOthers == ViewCreator.AdjustOtherState.IGNORE &&
                                        componentViewResult.shouldHideComponent) ||
                                        adjustOthers == ViewCreator.AdjustOtherState.ADJUST_OTHERS) {
                                    moduleView1.addChildComponentAndView(component, componentView);
                                } else {
                                    moduleView1.setComponentHasView(i, false);
                                }
                            }
                        }
                    }
                }
            }
            if (downloadSeparator != null) {
                LayoutParams lp = (LayoutParams) downloadSeparator.getLayoutParams();
                topLayoutContainerLayoutParams.setMargins(0, lp.topMargin + 5, 0, 0);
                topLayoutContainer.setLayoutParams(topLayoutContainerLayoutParams);
            }
            pageView.addToHeaderView(topLayoutContainer);
            childContainer.addView(pageView);
            DownloadTabSelectorBus.instanceOf().setTab(appCMSPresenter.getDownloadTabSelected());
            if (appCMSPresenter.getDownloadTabSelected() == VIDEO_TAB) {
                selectChild(0);
                unselectChild(1);
            }
            if (appCMSPresenter.getDownloadTabSelected() == AUDIO_TAB) {
                selectChild(1);
                unselectChild(0);
            }
        }
    }

    private void selectChild(int childIndex) {
        if (childViews != null &&
                childIndex < childViews.length &&
                childViews[childIndex] != null) {
            childViews[childIndex].setVisibility(VISIBLE);
            setAlphaTextColorForSelector(buttonSelectors[childIndex], 200);
            applyUnderlineToComponent(underlineViews[childIndex], underlineColor);

        }
    }

    private void unselectChild(int childIndex) {
        if (childViews != null &&
                childIndex < childViews.length &&
                childViews[childIndex] != null) {
            childViews[childIndex].setVisibility(GONE);
            setAlphaTextColorForSelector(buttonSelectors[childIndex], 100);
            applyUnderlineToComponent(underlineViews[childIndex], bgColor);
        }
    }

    private void addChildComponents(ModuleView moduleView,
                                    Component subComponent,
                                    final AppCMSAndroidModules appCMSAndroidModules) {
        ViewCreator.ComponentViewResult componentViewResult = viewCreator.getComponentViewResult();
        if (componentViewResult.onInternalEvent != null) {
            appCMSPresenter.addInternalEvent(componentViewResult.onInternalEvent);
        }
        ViewGroup subComponentChildContainer = moduleView.getChildrenContainer();
        float parentYAxis = 2 * getYAxis(getContext(), subComponent.getLayout(), 0.0f);
        if (componentViewResult != null && subComponentChildContainer != null) {
            for (int i = 0; i < subComponent.getComponents().size(); i++) {
                final Component component = subComponent.getComponents().get(i);
                viewCreator.createComponentView(getContext(),
                        component,
                        component.getLayout(),
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
                            component.getLayout(),
                            0.0f);
                    if (!component.isyAxisSetManually()) {
                        setYAxis(getContext(),
                                component.getLayout(),
                                componentYAxis - parentYAxis);
                        component.setyAxisSetManually(true);
                    }
                    subComponentChildContainer.addView(componentView);
                    moduleView.setComponentHasView(i, true);
                    moduleView.setViewMarginsFromComponent(component,
                            componentView,
                            subComponent.getLayout(),
                            subComponentChildContainer,
                            false,
                            jsonValueKeyMap,
                            componentViewResult.useMarginsAsPercentagesOverride,
                            componentViewResult.useWidthOfScreen,
                            "");
                    AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());
                    if (componentType == null) {
                        componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                    }
                    AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());
                    if (componentKey == null) {
                        componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                    }
                } else {
                    moduleView.setComponentHasView(i, false);
                }
            }
        }
    }

    private void applyUnderlineToComponent(GradientDrawable underline, int color) {
        underline.setStroke((int) convertDpToPixel(2, getContext()), color);
        underline.setColor(transparentColor);
    }

    void setAlphaTextColorForSelector(Button button, int alpha) {
        String textColor = appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getTextColor();
        int color = Color.parseColor(textColor);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        button.setTextColor(Color.argb(alpha, r, g, b));
    }
}
