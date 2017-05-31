package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
    }

    public PageView generatePage(Context context,
                                 AppCMSPageUI appCMSPageUI,
                                 AppCMSPageAPI appCMSPageAPI,
                                 Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter,
                                 List<String> modulesToIgnore) {
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
                Log.d(TAG, "Creating view for: " + module.getView());
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
        if (module.getComponents() != null) {
            List<OnInternalEvent> onInternalEvents = new ArrayList<>();
            for (int i = 0; i < module.getComponents().size(); i++) {
                Component component = module.getComponents().get(i);
                ComponentViewResult componentViewResult = createComponentView(context,
                        component,
                        moduleAPI,
                        module.getSettings(),
                        onComponentLoaded,
                        jsonValueKeyMap,
                        appCMSPresenter);
                hideOnFullscreenLandscape &= componentViewResult.hideOnFullscreenLandscape;
                if (componentViewResult.onInternalEvent != null) {
                    onInternalEvents.add(componentViewResult.onInternalEvent);
                }
                View componentView = componentViewResult.componentView;
                if (componentView != null) {
                    childrenContainer.addView(componentView);
                    moduleView.setComponentHasView(i, true);
                    Log.d(TAG, "Setting margins for: " + component.getKey());
                    moduleView.setViewMarginsFromComponent(component.getLayout(),
                            componentView,
                            moduleView.getLayout(),
                            childrenContainer,
                            module.getLayout().getMobile() == null);
                } else {
                    moduleView.setComponentHasView(i, false);
                }
            }

            for (OnInternalEvent onInternalEvent : onInternalEvents) {
                for (OnInternalEvent receiverInternalEvent : onInternalEvents) {
                    if (receiverInternalEvent != onInternalEvent) {
                        onInternalEvent.addReceiver(receiverInternalEvent);
                    }
                }
            }
        }
        moduleView.setHideOnFullscreenLandscape(hideOnFullscreenLandscape);
        appCMSPresenter.addOnOrientationChangeHandler(moduleView.getOrientationChangeHandler());
        return moduleView;
    }

    public CollectionGridItemView createCollectionGridItemView(final Context context,
                                                               final Component component,
                                                               final AppCMSPresenter appCMSPresenter,
                                                               final Module moduleAPI,
                                                               Settings settings,
                                                               final OnComponentLoaded onComponentLoaded,
                                                               Map<AppCMSUIKeyType, String> jsonValueKeyMap) {
        CollectionGridItemView collectionGridItemView = new CollectionGridItemView(context, component);
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
                    appCMSPresenter);
            hideOnFullScreenLandscape &= componentViewResult.hideOnFullscreenLandscape;
            if (componentViewResult.onInternalEvent != null) {
                onInternalEvents.add(componentViewResult.onInternalEvent);
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
                Log.d(TAG, "Setting margins for: "  + childComponent.getType());
                collectionGridItemView.setViewMarginsFromComponent(childComponent.getLayout(),
                        componentView,
                        collectionGridItemView.getLayout(),
                        collectionGridItemView.getChildrenContainer(),
                        false);
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
                                    final AppCMSPresenter appCMSPresenter) {
        ComponentViewResult componentViewResult = new ComponentViewResult();

        if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_COLLECTIONGRID_KEY))) {
            componentViewResult.componentView = new RecyclerView(context);
            if (component.isHorizontalScroll()) {
                ((RecyclerView) componentViewResult.componentView)
                        .setLayoutManager(new LinearLayoutManager(context,
                                LinearLayoutManager.HORIZONTAL,
                                false));
            } else {
                ((RecyclerView) componentViewResult.componentView)
                        .setLayoutManager(new LinearLayoutManager(context,
                                LinearLayoutManager.VERTICAL,
                                false));
            }
            AppCMSViewAdapter appCMSViewAdapter = new AppCMSViewAdapter(context,
                    this,
                    appCMSPresenter,
                    settings,
                    component,
                    jsonValueKeyMap,
                    moduleAPI);
            ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSViewAdapter);
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
            AppCMSCarouselItemAdapter appCMSCarouselItemAdapter = new AppCMSCarouselItemAdapter(context,
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
                .equals(AppCMSUIKeyType.PAGE_PAGE_CONTROL_VIEW_KEY)) {
            int selectedColor =
                    component.getSelectedColor() != null ? Color.parseColor(getColor(component.getSelectedColor())) : 0;
            int deselectedColor =
                    component.getUnSelectedColor() != null ? Color.parseColor(getColor(component.getUnSelectedColor())) : 0;;
            componentViewResult.componentView = new DotSelectorView(context,
                    selectedColor,
                    deselectedColor);
            componentViewResult.onInternalEvent = (DotSelectorView) componentViewResult.componentView;
            componentViewResult.hideOnFullscreenLandscape = true;
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_BUTTON_KEY))) {
            componentViewResult.componentView = new Button(context);
            if (!TextUtils.isEmpty(component.getText())) {
                ((Button) componentViewResult.componentView).setText(component.getText());
            }
            if (!TextUtils.isEmpty(component.getTextColor())) {
                Log.d(TAG, "Button text color: " + component.getTextColor());
                ((Button) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(component.getTextColor())));
            }
            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                componentViewResult.componentView.setBackgroundColor(Color.parseColor(getColor(component.getBackgroundColor())));
            }
            componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Button click event: " +
                            component.getText() +
                            " -> " +
                            component.getAction());
                    if (!TextUtils.isEmpty(component.getAction())) {
                        boolean launchResult = appCMSPresenter.launchPageAction(component.getAction(),
                                null);
                        if (!launchResult) {
                            Log.e(TAG, "Failed to launch " + component.getAction());
                            appCMSPresenter.launchErrorActivity(appCMSPresenter.getCurrentActivity());
                        }
                    }
                }
            });
            componentViewResult.hideOnFullscreenLandscape = true;
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_LABEL_KEY))) {
            componentViewResult.componentView = new AlwaysSelectedTextView(context);
            if (!TextUtils.isEmpty(component.getText())) {
                ((TextView) componentViewResult.componentView).setText(component.getText().toUpperCase());
            } else if (component.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY))) {
                if (settings != null & !TextUtils.isEmpty(settings.getTitle())) {
                    ((TextView) componentViewResult.componentView).setText(settings.getTitle().toUpperCase());
                }
            }
            if (!TextUtils.isEmpty(component.getTextColor())) {
                ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(component.getTextColor())));
            }
            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                ((TextView) componentViewResult.componentView).setBackgroundColor(Color.parseColor(getColor(component.getBackgroundColor())));
            }
            if (component.getFontSize() > 0) {
                ((TextView) componentViewResult.componentView).setTextSize((float) component.getFontSize());
            }
            if (!TextUtils.isEmpty(component.getFontFamily())) {
                ((TextView) componentViewResult.componentView).setTypeface(Typeface.create(component.getFontFamily(), Typeface.NORMAL));
            }
            ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.MARQUEE);
            ((TextView) componentViewResult.componentView).setHorizontallyScrolling(true);
            ((TextView) componentViewResult.componentView).setMarqueeRepeatLimit(-1);
            ((TextView) componentViewResult.componentView).setSingleLine(true);
            ((TextView) componentViewResult.componentView).setLines(1);
            componentViewResult.hideOnFullscreenLandscape = true;
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_IMAGE_KEY))) {
            if (!TextUtils.isEmpty(component.getImageName())) {
                if (component.getImageName().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_BG_KEY))) {
                    if (TextUtils.isEmpty(component.getKey()) ||
                            !component.getKey().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY))) {
                        onComponentLoaded.onBitmapLoaded(context.getDrawable(R.drawable.bg));
                    } else {
                        componentViewResult.componentView = new ImageView(context);
                    }
                } else if (component.getImageName().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_LOGO_KEY))) {
                    componentViewResult.componentView = new ImageView(context);
                    ((ImageView) componentViewResult.componentView).setImageDrawable(context.getDrawable(R.drawable.logo));
                } else {
                    componentViewResult.componentView = new ImageView(context);
                    Picasso.with(context)
                            .load(component.getImageName())
                            .into((ImageView) componentViewResult.componentView);
                }
            } else {
                onComponentLoaded.onBitmapLoaded(context.getResources()
                        .getDrawable(android.R.drawable.screen_background_dark_transparent,
                                context.getTheme()));
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
