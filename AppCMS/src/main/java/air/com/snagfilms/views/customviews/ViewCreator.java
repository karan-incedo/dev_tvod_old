package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import air.com.snagfilms.models.data.appcms.AppCMSKeyType;
import air.com.snagfilms.models.data.appcms.page.Component;
import air.com.snagfilms.models.data.appcms.page.ModuleList;
import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.presenters.AppCMSPresenter;
import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/5/17.
 */

public class ViewCreator {
    private static final String TAG = "ViewCreator";

    private enum LayoutType {
        MOBILE,
        TABLET_PORTRAIT,
        TABLET_LANDSCAPE
    }

    public PageView generatePage(Context context,
                                 Page page,
                                 Map<AppCMSKeyType, String> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter) {
        PageView pageView = new PageView(context, page);
        createPageView(context, page, pageView, jsonValueKeyMap, appCMSPresenter);
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
                                  Page page,
                                  final PageView pageView,
                                  Map<AppCMSKeyType, String> jsonValueKeyMap,
                                  AppCMSPresenter appCMSPresenter) {
        List<ModuleList> modulesList = page.getModuleList();
        ViewGroup childrenContainer = pageView.getChildrenContainer(context, LinearLayout.VERTICAL);
        for (ModuleList module : modulesList) {
            View childView = createModuleView(context,
                    module,
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
                                 final OnComponentLoaded onComponentLoaded,
                                 Map<AppCMSKeyType, String> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter) {
        ModuleView moduleView = new ModuleView(context, module);
        ViewGroup childrenContainer = moduleView.getChildrenContainer(context, LinearLayout.VERTICAL);
        if (module.getComponents() != null) {
            for (int i = 0; i < module.getComponents().size(); i++) {
                Component component = module.getComponents().get(i);
                View componentView = createComponentView(context,
                        component,
                        onComponentLoaded,
                        jsonValueKeyMap,
                        appCMSPresenter);
                if (componentView != null) {
                    childrenContainer.addView(componentView);
                    moduleView.setComponentHasView(i, true);
                } else {
                    moduleView.setComponentHasView(i, false);
                }
            }
        }
        return moduleView;
    }

    public View createComponentView(final Context context,
                                    final Component component,
                                    final OnComponentLoaded onComponentLoaded,
                                    Map<AppCMSKeyType, String> jsonValueKeyMap,
                                    final AppCMSPresenter appCMSPresenter) {
        View componentView = null;

        if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSKeyType.PAGE_COLLECTIONGRID_KEY))) {
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
        } else if (component.getType()
                .endsWith(jsonValueKeyMap.get(AppCMSKeyType.PAGE_BUTTON_KEY))) {
            componentView = new Button(context);
            ((Button) componentView).setText(component.getText());
            Log.d(TAG, "Button text color: " + component.getTextColor());
            ((Button) componentView).setTextColor(Color.parseColor(getColor(component.getTextColor())));
            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                componentView.setBackgroundColor(Color.parseColor(getColor(component.getBackgroundColor())));
            }
            componentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Call AppPresenter with the ID from the Action field
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
                .endsWith(jsonValueKeyMap.get(AppCMSKeyType.PAGE_LABEL_KEY))) {
            componentView = new TextView(context);
            ((TextView) componentView).setText(component.getText());
            Log.d(TAG, "Texview text color: " + component.getTextColor());
            ((TextView) componentView).setTextColor(Color.parseColor(getColor(component.getTextColor())));
            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                componentView.setBackgroundColor(Color.parseColor(getColor(component.getBackgroundColor())));
            }
        } else if (component.getType()
                .equals(jsonValueKeyMap.get(AppCMSKeyType.PAGE_IMAGE_KEY))) {
            if (!TextUtils.isEmpty(component.getImageName())) {
                if (component.getImageName().equals(jsonValueKeyMap.get(AppCMSKeyType.PAGE_BG_KEY))) {
                    onComponentLoaded.onBitmapLoaded(context.getDrawable(R.drawable.bg));
                } else if (component.getImageName().equals(jsonValueKeyMap.get(AppCMSKeyType.PAGE_LOGO_KEY))) {
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
                        .getDrawable(android.R.drawable.screen_background_dark_transparent, context.getTheme()));
            }
        }

        return componentView;
    }

    public CollectionGridView createCollectionGridView(final Context context,
                                                       final Component component,
                                                       final OnComponentLoaded onComponentLoaded,
                                                       Map<AppCMSKeyType, String> jsonValueKeyMap) {
        // TODO: Parse json data and map to child elements
        throw new IllegalArgumentException(getClass().getCanonicalName() +
                "." +
                getClass().getEnclosingMethod().getName() +
                ": operation not supported.");
    }

    private String getColor(String color) {
        if (color.indexOf("#") != 0) {
            return "#" + color;
        }
        return color;
    }
}
