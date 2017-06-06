package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
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
    }

    public PageView generatePage(Context context,
                                 AppCMSPageUI appCMSPageUI,
                                 AppCMSPageAPI appCMSPageAPI,
                                 Map<AppCMSUIKeyType, String> jsonValueKeyMap,
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

    public interface OnComponentLoaded {
        void onBitmapLoaded(Drawable drawable);
    }

    public static OnComponentLoaded NOOP_ON_COMPONENT_LOADED = new OnComponentLoaded() {
        @Override
        public void onBitmapLoaded(Drawable drawable) {
            // noop - Loading of bitmap into image may be ddelayed
        }
    };

    protected void createPageView(Context context,
                                  AppCMSPageUI appCMSPageUI,
                                  AppCMSPageAPI appCMSPageAPI,
                                  final PageView pageView,
                                  Map<AppCMSUIKeyType, String> jsonValueKeyMap,
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
                        new OnComponentLoaded() {
                            @Override
                            public void onBitmapLoaded(Drawable drawable) {
                                pageView.setBackground(drawable);
                            }
                        },
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
                                 final OnComponentLoaded onComponentLoaded,
                                 Map<AppCMSUIKeyType, String> jsonValueKeyMap,
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
                        onComponentLoaded,
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
                            componentViewResult.useMarginsAsPercentagesOverride);
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
                                                               final OnComponentLoaded onComponentLoaded,
                                                               Map<AppCMSUIKeyType, String> jsonValueKeyMap,
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
                    onComponentLoaded,
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
                        useMarginsAsPercentages);
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
                                    final OnComponentLoaded onComponentLoaded,
                                    Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                                    final AppCMSPresenter appCMSPresenter,
                                    boolean gridElement) {
        ComponentViewResult componentViewResult = new ComponentViewResult();
        componentViewResult.useMarginsAsPercentagesOverride = true;
        if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_COLLECTIONGRID_KEY))) {
            componentViewResult.componentView = new RecyclerView(context);
            ((RecyclerView) componentViewResult.componentView)
                    .setLayoutManager(new LinearLayoutManager(context,
                            LinearLayoutManager.HORIZONTAL,
                            false));
            AppCMSViewAdapter appCMSViewAdapter = new AppCMSViewAdapter(context,
                    this,
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
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_CAROUSEL_VIEW_KEY))) {
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
                    new AppCMSCarouselItemAdapter(context,
                        this,
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
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_PAGE_CONTROL_VIEW_KEY))) {
            int selectedColor =
                    component.getSelectedColor() != null ? Color.parseColor(getColor(component.getSelectedColor())) : 0;
            int deselectedColor =
                    component.getUnSelectedColor() != null ? Color.parseColor(getColor(component.getUnSelectedColor())) : 0;;
            componentViewResult.componentView = new DotSelectorView(context,
                    component,
                    selectedColor,
                    deselectedColor);
            int numDots = moduleAPI.getContentData() != null ? moduleAPI.getContentData().size() : 0;
            ((DotSelectorView) componentViewResult.componentView).addDots(numDots);
            componentViewResult.onInternalEvent = (DotSelectorView) componentViewResult.componentView;
            componentViewResult.hideOnFullscreenLandscape = true;
            componentViewResult.useMarginsAsPercentagesOverride = false;
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_BUTTON_KEY))) {
            componentViewResult.componentView = new Button(context);
            if (!gridElement) {
                if (!TextUtils.isEmpty(component.getText())) {
                    ((Button) componentViewResult.componentView).setText(component.getText());
                } else if (!moduleAPI.getSettings().getHideTitle() &&
                        !TextUtils.isEmpty(moduleAPI.getTitle())) {
                    ((Button) componentViewResult.componentView).setText(moduleAPI.getTitle());
                }
            }
            if (!TextUtils.isEmpty(component.getTextColor())) {
                ((Button) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(component.getTextColor())));
            }
            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                componentViewResult.componentView.setBackgroundColor(Color.parseColor(getColor(component.getBackgroundColor())));
            }
            if (component.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_INFO_KEY))) {
                componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.info_icon));
            } else if (component.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_PLAY_KEY)) ||
                    component.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_PLAY_IMAGE_KEY))) {
                componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.play_icon));
                componentViewResult.componentView.getBackground().setColorFilter(context.getColor(R.color.colorAccent),
                        PorterDuff.Mode.MULTIPLY);
            }
            componentViewResult.hideOnFullscreenLandscape = true;
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_LABEL_KEY))) {
            componentViewResult.componentView = new AlwaysSelectedTextView(context);
            if (!gridElement) {
                if (component.getKey().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY))) {
                    if (!TextUtils.isEmpty(component.getText())) {
                        ((TextView) componentViewResult.componentView).setText(component.getText().toUpperCase());
                    } else if (!moduleAPI.getSettings().getHideTitle() &&
                            !TextUtils.isEmpty(moduleAPI.getTitle())) {
                        ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle().toUpperCase());
                    }
                }
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
                if (!TextUtils.isEmpty(component.getFontWeight()) &&
                        (component.getFontWeight().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_TEXT_BOLD_KEY)) ||
                                component.getFontWeight().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_TEXT_SEMIBOLD_KEY)))) {
                    ((TextView) componentViewResult.componentView).setTypeface(Typeface.create(component.getFontFamily(), Typeface.BOLD));
                } else {
                    ((TextView) componentViewResult.componentView).setTypeface(Typeface.create(component.getFontFamily(), Typeface.NORMAL));
                }
            }
            ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.MARQUEE);
            ((TextView) componentViewResult.componentView).setHorizontallyScrolling(true);
            ((TextView) componentViewResult.componentView).setMarqueeRepeatLimit(-1);
            ((TextView) componentViewResult.componentView).setSingleLine(true);
            ((TextView) componentViewResult.componentView).setLines(1);
            componentViewResult.hideOnFullscreenLandscape = true;
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_IMAGE_KEY))) {
            componentViewResult.componentView = new ImageView(context);
            if (!TextUtils.isEmpty(component.getImageName())) {
                if (component.getImageName().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_BG_KEY))) {
                    if (TextUtils.isEmpty(component.getKey()) ||
                            !component.getKey().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY))) {
                        onComponentLoaded.onBitmapLoaded(context.getDrawable(R.drawable.bg));
                    }
                } else if (component.getImageName().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_LOGO_KEY))) {
                    ((ImageView) componentViewResult.componentView).setImageDrawable(context.getDrawable(R.drawable.logo));
                } else {
                    Picasso.with(context)
                            .load(component.getImageName())
                            .into((ImageView) componentViewResult.componentView);
                }
            } else {
                if (!component.getKey()
                        .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY))) {
                    onComponentLoaded.onBitmapLoaded(context.getResources()
                            .getDrawable(android.R.drawable.screen_background_dark_transparent,
                                    context.getTheme()));
                }
            }
            componentViewResult.hideOnFullscreenLandscape = true;
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_PROGRESS_VIEW_KEY))) {
            componentViewResult.componentView = new ProgressBar(context,
                    null,
                    R.style.Widget_AppCompat_ProgressBar_Horizontal);
            if (!TextUtils.isEmpty(component.getProgressColor())) {
                int color = Color.parseColor(getColor(component.getProgressColor()));
                ((ProgressBar) componentViewResult.componentView).setProgressDrawable(new ColorDrawable(color));
            }
            componentViewResult.hideOnFullscreenLandscape = true;
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_SEPARATOR_VIEW_KEY))) {
            componentViewResult.componentView = new View(context);
            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                componentViewResult.componentView.setBackgroundColor(Color.parseColor(getColor(component.getBackgroundColor())));
            }
            componentViewResult.hideOnFullscreenLandscape = true;
        }

        return componentViewResult;
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
