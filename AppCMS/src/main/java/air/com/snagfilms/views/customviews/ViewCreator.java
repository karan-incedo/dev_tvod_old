package air.com.snagfilms.views.customviews;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;
import java.util.Map;

import air.com.snagfilms.models.data.appcms.AppCMSKeyType;
import air.com.snagfilms.models.data.appcms.page.Component;
import air.com.snagfilms.models.data.appcms.page.ModuleList;
import air.com.snagfilms.models.data.appcms.page.Page;
import air.com.snagfilms.models.network.modules.AppCMSAPIModule;

/**
 * Created by viewlift on 5/5/17.
 */

public class ViewCreator {
    private static final String TAG = "ViewCreator";

    public PageView generatePage(Context context,
                                 Page page,
                                 Map<AppCMSKeyType, String> jsonValueKeyMap) {
        PageView pageView = new PageView(context, page);
        generateModularPage(context, page, pageView, jsonValueKeyMap);
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

    protected void generateModularPage(Context context,
                                       Page page,
                                       final PageView pageView,
                                       Map<AppCMSKeyType, String> jsonValueKeyMap) {
        List<ModuleList> modulesList = page.getModuleList();
        LinearLayout childrenContainer = pageView.getChildrenContainer(context, LinearLayout.VERTICAL);
        for (ModuleList module : modulesList) {
            View childView = generateModule(context,
                    module,
                    new OnComponentLoaded() {
                        @Override
                        public void onBitmapLoaded(Drawable drawable) {
                            pageView.setBackground(drawable);
                        }
                    },
                    jsonValueKeyMap);
            if (childView != null) {
                childrenContainer.addView(childView);
            }
        }
    }

    public View generateModule(final Context context,
                               final ModuleList modules,
                               final OnComponentLoaded onComponentLoaded,
                               Map<AppCMSKeyType, String> jsonValueKeyMap) {
        ModuleView moduleView = new ModuleView(context, modules);
        for (Component component : modules.getComponents()) {
            View componentView = generateComponent(context,
                    component,
                    onComponentLoaded,
                    jsonValueKeyMap);
            if (componentView != null) {
                moduleView.addView(componentView);
            }
        }
        return moduleView;
    }

    public View generateComponent(final Context context,
                                  final Component component,
                                  final OnComponentLoaded onComponentLoaded,
                                  Map<AppCMSKeyType, String> jsonValueKeyMap) {
        View moduleView = null;

        if (component
                .getType()
                .equals(jsonValueKeyMap.get(AppCMSKeyType.PAGE_COLLECTIONGRID_KEY))) {
            moduleView = new RecyclerView(context);
            if (component.isHorizontalScroll()) {
                ((RecyclerView) moduleView)
                        .setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            } else {
                ((RecyclerView) moduleView)
                        .setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            }
        } else if (component
                .getType()
                .endsWith(jsonValueKeyMap.get(AppCMSKeyType.PAGE_BUTTON_KEY))) {
            moduleView = new Button(context);
            ViewGroup.LayoutParams buttonLayoutParams =
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            moduleView.setLayoutParams(buttonLayoutParams);
            ((Button) moduleView).setText(component.getText());
            ((Button) moduleView).setTextColor(Color.parseColor("#" + component.getTextColor()));
            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                moduleView.setBackgroundColor(Color.parseColor("#" + component.getBackgroundColor()));
            }
            moduleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Call AppPresenter with the ID from the Action field
                }
            });
        } else if (component
                .getType()
                .endsWith(jsonValueKeyMap.get(AppCMSKeyType.PAGE_LABEL_KEY))) {
            moduleView = new TextView(context);
            ViewGroup.LayoutParams labelLayoutParams =
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            moduleView.setLayoutParams(labelLayoutParams);
            ((TextView) moduleView).setText(component.getText());
            ((TextView) moduleView).setTextColor(Color.parseColor("#" + component.getTextColor()));
            if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                moduleView.setBackgroundColor(Color.parseColor("#" + component.getBackgroundColor()));
            }
        } else if (component
                .getType()
                .equals(jsonValueKeyMap.get(AppCMSKeyType.PAGE_IMAGE_KEY))) {
            if (!TextUtils.isEmpty(component.getImageName())) {
                Picasso.with(context)
                        .load(component.getImageName())
                        .placeholder(android.R.drawable.screen_background_dark_transparent)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                onComponentLoaded.onBitmapLoaded(new BitmapDrawable(context.getResources(), bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                Log.e(TAG, "Drawable failed to load: " + component.getImageName());
                                onComponentLoaded
                                        .onBitmapLoaded(context.getResources()
                                                .getDrawable(android.R.drawable.screen_background_dark_transparent, context.getTheme()));
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                Log.i(TAG, "Preparing to load drawable: " + component.getImageName());
                            }
                        });
            } else {
                onComponentLoaded
                        .onBitmapLoaded(context.getResources()
                                .getDrawable(android.R.drawable.screen_background_dark_transparent, context.getTheme()));
            }
        }

        return moduleView;
    }

    protected boolean isTablet(Context context) {
        int largeScreenLayout =
                (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        int xLargeScreenLayout =
                (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

        return (largeScreenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                xLargeScreenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }

    protected boolean isLandscape(Context context) {
        int layoutDirection = context.getResources().getConfiguration().getLayoutDirection();
        return layoutDirection == Configuration.ORIENTATION_LANDSCAPE;
    }
}
