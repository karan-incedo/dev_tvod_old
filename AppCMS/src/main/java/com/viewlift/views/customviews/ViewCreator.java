package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;

import java.util.List;
import java.util.Map;

import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.views.adapters.AppCMSViewAdapter;

import org.w3c.dom.Text;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/5/17.
 */

public class ViewCreator {
    private static final String TAG = "ViewCreator";

    public PageView generatePage(Context context,
                                 AppCMSPageUI appCMSPageUI,
                                 AppCMSPageAPI appCMSPageAPI,
                                 Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter) {
        PageView pageView = new PageView(context, appCMSPageUI);
        createPageView(context,
                appCMSPageUI,
                appCMSPageAPI,
                pageView,
                jsonValueKeyMap,
                appCMSPresenter);
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
                                  AppCMSPresenter appCMSPresenter) {
        List<ModuleList> modulesList = appCMSPageUI.getModuleList();
        ViewGroup childrenContainer = pageView.getChildrenContainer(context);
        for (ModuleList module : modulesList) {
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

    public View createModuleView(final Context context,
                                 final ModuleList module,
                                 final Module moduleAPI,
                                 final OnComponentLoaded onComponentLoaded,
                                 Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter) {
        ModuleView moduleView = new ModuleView(context, module);
        ViewGroup childrenContainer = moduleView.getChildrenContainer(context);
        if (module.getComponents() != null) {
            for (int i = 0; i < module.getComponents().size(); i++) {
                Component component = module.getComponents().get(i);
                View componentView = createComponentView(context,
                        component,
                        moduleAPI,
                        module.getSettings(),
                        onComponentLoaded,
                        jsonValueKeyMap,
                        appCMSPresenter);
                if (componentView != null) {
                    childrenContainer.addView(componentView);
                    moduleView.setComponentHasView(i, true);
                    Log.d(TAG, "Setting margins for: " + module.getId());
                    moduleView.setViewMarginsFromComponent(context,
                            component.getLayout(),
                            componentView,
                            moduleView.getLayout(),
                            childrenContainer,
                            module.getLayout().getMobile() == null);
                } else {
                    moduleView.setComponentHasView(i, false);
                }
            }
        }
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
        for (int i = 0; i < component.getComponents().size(); i++) {
            Component childComponent = component.getComponents().get(i);
            View componentView = createComponentView(context,
                    childComponent,
                    moduleAPI,
                    settings,
                    onComponentLoaded,
                    jsonValueKeyMap,
                    appCMSPresenter);
            if (componentView != null) {
                CollectionGridItemView.ItemContainer itemContainer =
                        new CollectionGridItemView.ItemContainer.Builder()
                            .childView(componentView)
                            .component(childComponent)
                            .build();
                collectionGridItemView.addChild(context, itemContainer);
                collectionGridItemView.setComponentHasView(i, true);
                Log.d(TAG, "Setting margins for: "  + childComponent.getType());
                collectionGridItemView.setViewMarginsFromComponent(context,
                        childComponent.getLayout(),
                        componentView,
                        collectionGridItemView.getLayout(),
                        collectionGridItemView.getChildrenContainer(context),
                        false);
            } else {
                collectionGridItemView.setComponentHasView(i, false);
            }
        }
        return collectionGridItemView;
    }

    public View createComponentView(final Context context,
                                    final Component component,
                                    final Module moduleAPI,
                                    final Settings settings,
                                    final OnComponentLoaded onComponentLoaded,
                                    Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                                    final AppCMSPresenter appCMSPresenter) {
        View componentView = null;

        if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_COLLECTIONGRID_KEY))) {
            componentView = new RecyclerView(context);
            if (component.isHorizontalScroll()) {
                ((RecyclerView) componentView)
                        .setLayoutManager(new LinearLayoutManager(context,
                                LinearLayoutManager.HORIZONTAL,
                                false));
            } else {
                ((RecyclerView) componentView)
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
            ((RecyclerView) componentView).setAdapter(appCMSViewAdapter);
        } else if (component.getType()
                .endsWith(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_BUTTON_KEY))) {
            componentView = new Button(context);
            if (!TextUtils.isEmpty(component.getText())) {
                ((Button) componentView).setText(component.getText());
            }
            if (!TextUtils.isEmpty(component.getTextColor())) {
                Log.d(TAG, "Button text color: " + component.getTextColor());
                ((Button) componentView).setTextColor(Color.parseColor(getColor(component.getTextColor())));
            }
            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                componentView.setBackgroundColor(Color.parseColor(getColor(component.getBackgroundColor())));
            }
            componentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Button click event: " +
                            component.getText() +
                            " -> " +
                            component.getAction());
                    if (!TextUtils.isEmpty(component.getAction())) {
                        boolean launchResult = appCMSPresenter.launchAction(component.getAction(),
                                null);
                        if (!launchResult) {
                            Log.e(TAG, "Failed to launch " + component.getAction());
                            appCMSPresenter.launchErrorActivity(appCMSPresenter.getCurrentActivity());
                        }
                    }
                }
            });
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_LABEL_KEY))) {
            componentView = new TextView(context);
            if (!TextUtils.isEmpty(component.getText())) {
                ((TextView) componentView).setText(component.getText().toUpperCase());
            } else if (component.getKey()
                    .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_TRAY_TITLE_KEY))) {
                if (settings != null & !TextUtils.isEmpty(settings.getTitle())) {
                    ((TextView) componentView).setText(settings.getTitle().toUpperCase());
                }
            }
            if (!TextUtils.isEmpty(component.getTextColor())) {
                ((TextView) componentView).setTextColor(Color.parseColor(getColor(component.getTextColor())));
            }
            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                componentView.setBackgroundColor(Color.parseColor(getColor(component.getBackgroundColor())));
            }
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_IMAGE_KEY))) {
            if (!TextUtils.isEmpty(component.getImageName())) {
                if (component.getImageName().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_BG_KEY))) {
                    if (TextUtils.isEmpty(component.getKey()) ||
                            !component.getKey().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_THUMBNAIL_IMAGE_KEY))) {
                        onComponentLoaded.onBitmapLoaded(context.getDrawable(R.drawable.bg));
                    }
                } else if (component.getImageName().equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_LOGO_KEY))) {
                    componentView = new ImageView(context);
                    ((ImageView) componentView).setImageDrawable(context.getDrawable(R.drawable.logo));
                } else {
                    componentView = new ImageView(context);
                    Picasso.with(context)
                            .load(component.getImageName())
                            .into((ImageView) componentView);
                }
            } else {
                onComponentLoaded.onBitmapLoaded(context.getResources()
                        .getDrawable(android.R.drawable.screen_background_dark_transparent,
                                context.getTheme()));
            }
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_PROGRESS_VIEW_KEY))) {
            componentView = new ProgressBar(context,
                    null,
                    R.style.Widget_AppCompat_ProgressBar_Horizontal);
            if (!TextUtils.isEmpty(component.getProgressColor())) {
                int color = Color.parseColor(getColor(component.getProgressColor()));
                ((ProgressBar) componentView).setProgressDrawable(new ColorDrawable(color));
            }
        }

        return componentView;
    }

    private String getColor(String color) {
        if (color.indexOf("#") != 0) {
            return "#" + color;
        }
        return color;
    }

    private Module matchModuleAPIToModuleUI(ModuleList module, AppCMSPageAPI appCMSPageAPI) {
        for (Module moduleAPI : appCMSPageAPI.getModules()) {
            if (module.getId().equals(moduleAPI.getId())) {
                return moduleAPI;
            }
        }
        return null;
    }
}
