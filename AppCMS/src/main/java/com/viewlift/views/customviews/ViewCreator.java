package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.views.adapters.AppCMSCarouselItemAdapter;
import com.viewlift.views.adapters.AppCMSViewAdapter;

import rx.functions.Action1;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/5/17.
 */

public class ViewCreator {
    private static final String TAG = "ViewCreator";

    private static class ComponentViewResult {
        View componentView;
        OnInternalEvent onInternalEvent;
        boolean hideOnFullscreenLandscape;
        Action1<LifecycleStatus> onLifecycleChangeHandler;
        boolean useMarginsAsPercentagesOverride;
        boolean useWidthOfScreen;
    }

    public PageView generatePage(Context context,
                                 AppCMSPageUI appCMSPageUI,
                                 AppCMSPageAPI appCMSPageAPI,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter,
                                 List<String> modulesToIgnore) {
        if (appCMSPageUI == null || appCMSPageAPI == null) {
            return null;
        }
        PageView pageView = new PageView(context, appCMSPageUI);
        createPageView(context,
                appCMSPageUI,
                appCMSPageAPI,
                pageView,
                jsonValueKeyMap,
                appCMSPresenter,
                modulesToIgnore);
        return pageView;
    }

    protected void createPageView(Context context,
                                  AppCMSPageUI appCMSPageUI,
                                  AppCMSPageAPI appCMSPageAPI,
                                  final PageView pageView,
                                  Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                  AppCMSPresenter appCMSPresenter,
                                  List<String> modulesToIgnore) {
        List<ModuleList> modulesList = appCMSPageUI.getModuleList();
        ViewGroup childrenContainer = pageView.getChildrenContainer();
        for (ModuleList module : modulesList) {
            if (!modulesToIgnore.contains(module.getView())) {
                Module moduleAPI = matchModuleAPIToModuleUI(module, appCMSPageAPI);
                View childView = createModuleView(context,
                        module,
                        moduleAPI,
                        jsonValueKeyMap,
                        appCMSPresenter);
                if (childView != null) {
                    childrenContainer.addView(childView);
                }
            }
        }
    }

    public View createModuleView(final Context context,
                                 final ModuleList module,
                                 final Module moduleAPI,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter) {
        ModuleView moduleView = new ModuleView(context, module);
        ViewGroup childrenContainer = moduleView.getChildrenContainer();
        boolean hideOnFullscreenLandscape = true;
        if (module.getComponents() != null &&
                moduleAPI != null &&
                moduleAPI.getContentData() != null &&
                moduleAPI.getContentData().size() > 0) {

            for (int i = 0; i < module.getComponents().size(); i++) {
                Component component = module.getComponents().get(i);
                ComponentViewResult componentViewResult = createComponentView(context,
                        component,
                        moduleAPI,
                        module.getSettings(),
                        jsonValueKeyMap,
                        appCMSPresenter,
                        false);
                hideOnFullscreenLandscape &= componentViewResult.hideOnFullscreenLandscape;
                if (componentViewResult.onInternalEvent != null) {
                    appCMSPresenter.addInternalEvent(componentViewResult.onInternalEvent);
                }
                if (componentViewResult.onLifecycleChangeHandler != null) {
                    appCMSPresenter.addOnLifecycleChangeHandler(componentViewResult.onLifecycleChangeHandler);
                }
                View componentView = componentViewResult.componentView;
                if (componentView != null) {
                    childrenContainer.addView(componentView);
                    moduleView.setComponentHasView(i, true);
                    moduleView.setViewMarginsFromComponent(component,
                            componentView,
                            moduleView.getLayout(),
                            childrenContainer,
                            false,
                            jsonValueKeyMap,
                            componentViewResult.useMarginsAsPercentagesOverride,
                            componentViewResult.useWidthOfScreen);
                } else {
                    moduleView.setComponentHasView(i, false);
                }
            }

            for (OnInternalEvent onInternalEvent : appCMSPresenter.getOnInternalEvents()) {
                for (OnInternalEvent receiverInternalEvent : appCMSPresenter.getOnInternalEvents()) {
                    if (receiverInternalEvent != onInternalEvent) {
                        onInternalEvent.addReceiver(receiverInternalEvent);
                    }
                }
            }
        }
        if (moduleAPI == null ||
                moduleAPI.getContentData() == null ||
                moduleAPI.getContentData().size() == 0) {
            moduleView.setVisibility(View.GONE);
        } else {
            moduleView.setHideOnFullscreenLandscape(hideOnFullscreenLandscape);
            appCMSPresenter.addOnOrientationChangeHandler(moduleView.getOrientationChangeHandler());
        }

        return moduleView;
    }

    public CollectionGridItemView createCollectionGridItemView(final Context context,
                                                               final Component component,
                                                               final AppCMSPresenter appCMSPresenter,
                                                               final Module moduleAPI,
                                                               Settings settings,
                                                               Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                                               int defaultWidth,
                                                               int defaultHeight,
                                                               boolean useMarginsAsPercentages) {
        CollectionGridItemView collectionGridItemView = new CollectionGridItemView(context,
                component,
                defaultWidth,
                defaultHeight);
        List<OnInternalEvent> onInternalEvents = new ArrayList<>();
        boolean hideOnFullScreenLandscape = true;
        for (int i = 0; i < component.getComponents().size(); i++) {
            Component childComponent = component.getComponents().get(i);
            ComponentViewResult componentViewResult = createComponentView(context,
                    childComponent,
                    moduleAPI,
                    settings,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    true);
            hideOnFullScreenLandscape &= componentViewResult.hideOnFullscreenLandscape;
            if (componentViewResult.onInternalEvent != null) {
                onInternalEvents.add(componentViewResult.onInternalEvent);
            }
            if (componentViewResult.onLifecycleChangeHandler != null) {
                appCMSPresenter.addOnLifecycleChangeHandler(componentViewResult.onLifecycleChangeHandler);
            }
            View componentView = componentViewResult.componentView;
            if (componentView != null) {
                CollectionGridItemView.ItemContainer itemContainer =
                        new CollectionGridItemView.ItemContainer.Builder()
                            .childView(componentView)
                            .component(childComponent)
                            .build();
                collectionGridItemView.addChild(itemContainer);
                collectionGridItemView.setComponentHasView(i, true);
                collectionGridItemView.setViewMarginsFromComponent(childComponent,
                        componentView,
                        collectionGridItemView.getLayout(),
                        collectionGridItemView.getChildrenContainer(),
                        false,
                        jsonValueKeyMap,
                        useMarginsAsPercentages,
                        componentViewResult.useWidthOfScreen);
            } else {
                collectionGridItemView.setComponentHasView(i, false);
            }
        }
        for (OnInternalEvent onInternalEvent : onInternalEvents) {
            for (OnInternalEvent receiverInternalEvent : onInternalEvents) {
                if (receiverInternalEvent != onInternalEvent) {
                    onInternalEvent.addReceiver(receiverInternalEvent);
                }
            }
        }
        collectionGridItemView.setHideOnFullscreenLandscape(hideOnFullScreenLandscape);
        return collectionGridItemView;
    }

    public ComponentViewResult createComponentView(final Context context,
                                    final Component component,
                                    final Module moduleAPI,
                                    final Settings settings,
                                    Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                    final AppCMSPresenter appCMSPresenter,
                                    boolean gridElement) {
        ComponentViewResult componentViewResult = new ComponentViewResult();
        componentViewResult.useMarginsAsPercentagesOverride = true;
        componentViewResult.useWidthOfScreen = false;
        AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());
        if (componentType == null) {
            componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }
        AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());
        if (componentKey == null) {
            componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }
        switch (componentType) {
            case PAGE_COLLECTIONGRID_KEY:
                componentViewResult.componentView = new RecyclerView(context);
                ((RecyclerView) componentViewResult.componentView)
                        .setLayoutManager(new LinearLayoutManager(context,
                                LinearLayoutManager.HORIZONTAL,
                                false));
                AppCMSViewAdapter appCMSViewAdapter = new AppCMSViewAdapter(this,
                        appCMSPresenter,
                        settings,
                        component,
                        jsonValueKeyMap,
                        moduleAPI,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSViewAdapter);
                component.getLayout().getMobile().setWidth((float) ViewGroup.LayoutParams.MATCH_PARENT);
                component.getLayout().getTabletLandscape().setWidth((float) ViewGroup.LayoutParams.MATCH_PARENT);
                component.getLayout().getTabletPortrait().setWidth((float) ViewGroup.LayoutParams.MATCH_PARENT);
                componentViewResult.hideOnFullscreenLandscape = true;
                break;
            case PAGE_CAROUSEL_VIEW_KEY:
                componentViewResult.componentView = new RecyclerView(context);
                ((RecyclerView) componentViewResult.componentView)
                        .setLayoutManager(new LinearLayoutManager(context,
                                LinearLayoutManager.HORIZONTAL,
                                false));
                boolean loop = false;
                if (settings.getLoop() != null) {
                    loop = settings.getLoop();
                }
                AppCMSCarouselItemAdapter appCMSCarouselItemAdapter =
                        new AppCMSCarouselItemAdapter(this,
                                appCMSPresenter,
                                settings,
                                component,
                                jsonValueKeyMap,
                                moduleAPI,
                                (RecyclerView) componentViewResult.componentView,
                                loop);
                ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSCarouselItemAdapter);
                componentViewResult.onInternalEvent = appCMSCarouselItemAdapter;
                componentViewResult.hideOnFullscreenLandscape = true;
                break;
            case PAGE_PAGE_CONTROL_VIEW_KEY:
                int selectedColor =
                        component.getSelectedColor() != null ? Color.parseColor(getColor(component.getSelectedColor())) : 0;
                int deselectedColor =
                        component.getUnSelectedColor() != null ? Color.parseColor(getColor(component.getUnSelectedColor())) : 0;
                componentViewResult.componentView = new DotSelectorView(context,
                        component,
                        selectedColor,
                        deselectedColor);
                int numDots = moduleAPI.getContentData() != null ? moduleAPI.getContentData().size() : 0;
                ((DotSelectorView) componentViewResult.componentView).addDots(numDots);
                componentViewResult.onInternalEvent = (DotSelectorView) componentViewResult.componentView;
                componentViewResult.hideOnFullscreenLandscape = true;
                componentViewResult.useMarginsAsPercentagesOverride = false;
                break;
            case PAGE_BUTTON_KEY:
                componentViewResult.componentView = new Button(context);
                if (!gridElement) {
                    if (!TextUtils.isEmpty(component.getText())) {
                        ((TextView) componentViewResult.componentView).setText(component.getText());
                    } else if (!moduleAPI.getSettings().getHideTitle() &&
                            !TextUtils.isEmpty(moduleAPI.getTitle())) {
                        ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle());
                    }
                }
                if (!TextUtils.isEmpty(component.getTextColor())) {
                    ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(component.getTextColor())));
                }
                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.setBackgroundColor(Color.parseColor(getColor(component.getBackgroundColor())));
                }
                componentViewResult.hideOnFullscreenLandscape = true;
                switch (componentKey) {
                    case PAGE_INFO_KEY:
                        componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.info_icon));
                        break;
                    case PAGE_VIDEO_PLAY_BUTTON_KEY:
                        componentViewResult.hideOnFullscreenLandscape = false;
                    case PAGE_PLAY_KEY:
                    case PAGE_PLAY_IMAGE_KEY:
                        componentViewResult.componentView.setBackground(ContextCompat.getDrawable(context, R.drawable.play_icon));
                        int tintColor = Color.parseColor(getColor(appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()));
                        componentViewResult.componentView.getBackground().setTint(tintColor);
                        componentViewResult.componentView.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                        break;
                    case PAGE_VIDEO_SHARE_KEY:
                        componentViewResult.componentView.setBackground(ContextCompat.getDrawable(context, R.drawable.share));
                        break;
                    case PAGE_VIDEO_CLOSE_KEY:
                        componentViewResult.componentView.setBackground(ContextCompat.getDrawable(context, R.drawable.close));
                        break;
                    default:
                }
                componentViewResult.hideOnFullscreenLandscape = true;
                break;
            case PAGE_LABEL_KEY:
                componentViewResult.componentView = new TextView(context);
                ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                if (!gridElement) {
                    switch (componentKey) {
                        case PAGE_TRAY_TITLE_KEY:
                            if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText().toUpperCase());
                            } else if (!moduleAPI.getSettings().getHideTitle() &&
                                    !TextUtils.isEmpty(moduleAPI.getTitle())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle().toUpperCase());
                            }
                            break;
                        case PAGE_VIDEO_DESCRIPTION_KEY:
                            if (!TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getDescription())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getDescription());
                            }

                            ViewTreeObserver textVto = componentViewResult.componentView.getViewTreeObserver();
                            ViewCreatorLayoutListener viewCreatorLayoutListener =
                                    new ViewCreatorLayoutListener((int) BaseView.getViewHeight(context, component.getLayout(), 0),
                                            ((TextView) componentViewResult.componentView));
                            textVto.addOnGlobalLayoutListener(viewCreatorLayoutListener);
                        case PAGE_VIDEO_TITLE_KEY:
                            if (!TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getTitle())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getTitle());
                            }
                            break;
                        case PAGE_VIDEO_SUBTITLE_KEY:
                            setViewWithSubtitle(moduleAPI.getContentData().get(0), componentViewResult.componentView);
                            break;
                        default:
                    }
                } else {
                    ((TextView) componentViewResult.componentView).setSingleLine(true);
                }
                if (!TextUtils.isEmpty(component.getTextColor())) {
                    ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(component.getTextColor())));
                } else if (component.getStyles() != null) {
                    if (!TextUtils.isEmpty(component.getStyles().getColor())) {
                        ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(component.getStyles().getColor())));
                    } else if (!TextUtils.isEmpty(component.getStyles().getTextColor())) {
                        ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(component.getStyles().getTextColor())));
                    }
                }
                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.setBackgroundColor(Color.parseColor(getColor(component.getBackgroundColor())));
                }
                if (!TextUtils.isEmpty(component.getFontFamily())) {
                    AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(component.getFontWeight());
                    if (fontWeight == AppCMSUIKeyType.PAGE_TEXT_BOLD_KEY ||
                            fontWeight == AppCMSUIKeyType.PAGE_TEXT_SEMIBOLD_KEY) {
                        ((TextView) componentViewResult.componentView).setTypeface(Typeface.create(component.getFontFamily(), Typeface.BOLD));
                    } else {
                        ((TextView) componentViewResult.componentView).setTypeface(Typeface.create(component.getFontFamily(), Typeface.NORMAL));
                    }
                }
                componentViewResult.hideOnFullscreenLandscape = true;
                break;
            case PAGE_IMAGE_KEY:
                componentViewResult.componentView = new ImageView(context);
                switch (componentKey) {
                    case PAGE_VIDEO_IMAGE_KEY:
                        int viewWidth = context.getResources().getDisplayMetrics().widthPixels;
                        int viewHeight = (int) BaseView.getViewHeight(context,
                                component.getLayout(),
                                context.getResources().getDisplayMetrics().heightPixels);
                        if (viewHeight > viewWidth) {
                            Picasso.with(context)
                                    .load(moduleAPI.getContentData().get(0).getGist().getPosterImageUrl())
                                    .resize(viewWidth, viewHeight)
                                    .centerInside()
                                    .into((ImageView) componentViewResult.componentView);
                        } else {
                            Picasso.with(context)
                                    .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                    .resize(viewWidth, viewHeight)
                                    .centerInside()
                                    .into((ImageView) componentViewResult.componentView);

                        }
                        componentViewResult.hideOnFullscreenLandscape = false;
                        componentViewResult.useWidthOfScreen = true;
                        break;
                    default:
                        if (!TextUtils.isEmpty(component.getImageName())) {
                            Picasso.with(context)
                                    .load(component.getImageName())
                                    .into((ImageView) componentViewResult.componentView);
                        }
                        componentViewResult.hideOnFullscreenLandscape = true;
                }
                break;
            case PAGE_PROGRESS_VIEW_KEY:
                componentViewResult.componentView = new ProgressBar(context,
                        null,
                        R.style.Widget_AppCompat_ProgressBar_Horizontal);
                if (!TextUtils.isEmpty(component.getProgressColor())) {
                    int color = Color.parseColor(getColor(component.getProgressColor()));
                    ((ProgressBar) componentViewResult.componentView).setProgressDrawable(new ColorDrawable(color));
                }
                componentViewResult.hideOnFullscreenLandscape = true;
                break;
            case PAGE_SEPARATOR_VIEW_KEY:
                componentViewResult.componentView = new View(context);
                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.setBackgroundColor(Color.parseColor(getColor(component.getBackgroundColor())));
                }
                componentViewResult.hideOnFullscreenLandscape = true;
                break;
            default:
        }
        return componentViewResult;
    }

    public static void setViewWithSubtitle(ContentDatum data, View view) {
        int runtime = (data.getGist().getRuntime() / 60);
        String year = data.getGist().getYear();
        String primaryCategory =
                data.getGist().getPrimaryCategory() != null ?
                        data.getGist().getPrimaryCategory().getTitle() :
                        null;
        boolean appendFirstSep = runtime > 0 &&
                (!TextUtils.isEmpty(year) || !TextUtils.isEmpty(primaryCategory));
        boolean appendSecondSep = (runtime > 0 || !TextUtils.isEmpty(year)) &&
                !TextUtils.isEmpty(primaryCategory);
        StringBuffer infoText = new StringBuffer();
        if (runtime > 0) {
            infoText.append(runtime + "MINS");
        }
        if (appendFirstSep) {
            infoText.append(" | ");
        }
        if (!TextUtils.isEmpty(year)) {
            infoText.append(year);
        }
        if (appendSecondSep) {
            infoText.append(" | ");
        }
        if (!TextUtils.isEmpty(primaryCategory)) {
            infoText.append(primaryCategory.toUpperCase());
        }
        ((TextView) view).setText(infoText.toString());
        view.setAlpha(0.6f);
    }

    private String getColor(String color) {
        if (color.indexOf("#") != 0) {
            return "#" + color;
        }
        return color;
    }

    private Module matchModuleAPIToModuleUI(ModuleList module, AppCMSPageAPI appCMSPageAPI) {
        if (appCMSPageAPI != null && appCMSPageAPI.getModules() != null) {
            if (appCMSPageAPI.getModules() != null) {
                for (Module moduleAPI : appCMSPageAPI.getModules()) {
                    if (module.getId().equals(moduleAPI.getId())) {
                        return moduleAPI;
                    }
                }
            }
        }
        return null;
    }
}
