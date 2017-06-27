package com.viewlift.views.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
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
import com.viewlift.models.data.appcms.api.CreditBlock;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.Layout;
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

    private static LruCache<String, PageView> pageViewLruCache;
    private static int PAGE_LRU_CACHE_SIZE = 10;
    private static LruCache<String, PageView> getPageViewLruCache() {
        if (pageViewLruCache == null) {
            pageViewLruCache = new LruCache<>(PAGE_LRU_CACHE_SIZE);
        }
        return pageViewLruCache;
    }

    private static class ComponentViewResult {
        View componentView;
        OnInternalEvent onInternalEvent;
        Action1<LifecycleStatus> onLifecycleChangeHandler;
        boolean useMarginsAsPercentagesOverride;
        boolean useWidthOfScreen;
    }

    ComponentViewResult componentViewResult;

    public void removeLruCacheItem(Context context, String pageId) {
        if (getPageViewLruCache().get(pageId + BaseView.isLandscape(context)) != null) {
            getPageViewLruCache().remove(pageId + BaseView.isLandscape(context));
        }
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
        PageView pageView = getPageViewLruCache().get(appCMSPageAPI.getId() + BaseView.isLandscape(context));
        boolean newView = false;
        if (pageView == null || pageView.getContext() != context) {
            pageView = new PageView(context, appCMSPageUI);
            getPageViewLruCache().put(appCMSPageAPI.getId() + BaseView.isLandscape(context), pageView);
            newView = true;
        }
        if (newView || !appCMSPresenter.isActionAPage(appCMSPageAPI.getId())) {
            pageView.getChildrenContainer().removeAllViews();
            Runtime.getRuntime().gc();
            componentViewResult = new ComponentViewResult();
            createPageView(context,
                    appCMSPageUI,
                    appCMSPageAPI,
                    pageView,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    modulesToIgnore);
        } else {
            pageView.notifyAdaptersOfUpdate();
        }
        return pageView;
    }

    protected void createPageView(Context context,
                                  AppCMSPageUI appCMSPageUI,
                                  AppCMSPageAPI appCMSPageAPI,
                                  PageView pageView,
                                  Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                  AppCMSPresenter appCMSPresenter,
                                  List<String> modulesToIgnore) {
        appCMSPresenter.clearOnInternalEvents();
        List<ModuleList> modulesList = appCMSPageUI.getModuleList();
        ViewGroup childrenContainer = pageView.getChildrenContainer();
        for (ModuleList module : modulesList) {
            if (!modulesToIgnore.contains(module.getView())) {
                Module moduleAPI = matchModuleAPIToModuleUI(module, appCMSPageAPI);
                View childView = createModuleView(context,
                        module,
                        moduleAPI,
                        pageView,
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
                                 PageView pageView,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter) {
        ModuleView moduleView = new ModuleView(context, module);
        ViewGroup childrenContainer = moduleView.getChildrenContainer();
        if (module.getComponents() != null) {
            for (int i = 0; i < module.getComponents().size(); i++) {
                Component component = module.getComponents().get(i);
                ComponentViewResult componentViewResult = createComponentView(context,
                        component,
                        module.getLayout(),
                        moduleAPI,
                        pageView,
                        module.getSettings(),
                        jsonValueKeyMap,
                        appCMSPresenter,
                        false);
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

            List<OnInternalEvent> presenterOnInternalEvents = appCMSPresenter.getOnInternalEvents();
            if (presenterOnInternalEvents != null) {
                for (OnInternalEvent onInternalEvent : presenterOnInternalEvents) {
                    for (OnInternalEvent receiverInternalEvent : presenterOnInternalEvents) {
                        if (receiverInternalEvent != onInternalEvent) {
                            onInternalEvent.addReceiver(receiverInternalEvent);
                        }
                    }
                }
            }
        }

        return moduleView;
    }

    public CollectionGridItemView createCollectionGridItemView(final Context context,
                                                               final Layout parentLayout,
                                                               final boolean useParentLayout,
                                                               final Component component,
                                                               final AppCMSPresenter appCMSPresenter,
                                                               final Module moduleAPI,
                                                               Settings settings,
                                                               Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                                               int defaultWidth,
                                                               int defaultHeight,
                                                               boolean useMarginsAsPercentages,
                                                               boolean gridElement) {
        CollectionGridItemView collectionGridItemView = new CollectionGridItemView(context,
                parentLayout,
                useParentLayout,
                component,
                defaultWidth,
                defaultHeight);
        List<OnInternalEvent> onInternalEvents = new ArrayList<>();
        for (int i = 0; i < component.getComponents().size(); i++) {
            Component childComponent = component.getComponents().get(i);
            ComponentViewResult componentViewResult = createComponentView(context,
                    childComponent,
                    parentLayout,
                    moduleAPI,
                    null,
                    settings,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    gridElement);
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
        return collectionGridItemView;
    }

    public ComponentViewResult createComponentView(final Context context,
                                                   final Component component,
                                                   final Layout parentLayout,
                                                   final Module moduleAPI,
                                                   @Nullable PageView pageView,
                                                   final Settings settings,
                                                   Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                                   final AppCMSPresenter appCMSPresenter,
                                                   boolean gridElement) {
        componentViewResult.componentView = null;
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
                AppCMSViewAdapter appCMSViewAdapter = new AppCMSViewAdapter(context,
                        this,
                        appCMSPresenter,
                        settings,
                        parentLayout,
                        false,
                        component,
                        jsonValueKeyMap,
                        moduleAPI,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSViewAdapter);
                if (pageView != null) {
                    pageView.addListWithAdapter(new AppCMSViewAdapter.ListWithAdapter.Builder()
                            .adapter(appCMSViewAdapter)
                            .listview((RecyclerView) componentViewResult.componentView)
                            .build());
                }
                componentViewResult.useWidthOfScreen = true;
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
                        new AppCMSCarouselItemAdapter(context,
                                this,
                                appCMSPresenter,
                                settings,
                                parentLayout,
                                component,
                                jsonValueKeyMap,
                                moduleAPI,
                                (RecyclerView) componentViewResult.componentView,
                                loop);
                ((RecyclerView) componentViewResult.componentView).setAdapter(appCMSCarouselItemAdapter);
                if (pageView != null) {
                    pageView.addListWithAdapter(new AppCMSViewAdapter.ListWithAdapter.Builder()
                            .adapter(appCMSCarouselItemAdapter)
                            .listview((RecyclerView) componentViewResult.componentView)
                            .build());
                }
                componentViewResult.onInternalEvent = appCMSCarouselItemAdapter;
                break;
            case PAGE_PAGE_CONTROL_VIEW_KEY:
                int selectedColor =
                        component.getSelectedColor() != null ? Color.parseColor(getColor(context, component.getSelectedColor())) : 0;
                int deselectedColor =
                        component.getUnSelectedColor() != null ? Color.parseColor(getColor(context, component.getUnSelectedColor())) : 0;
                componentViewResult.componentView = new DotSelectorView(context,
                        component,
                        selectedColor,
                        deselectedColor);
                int numDots = moduleAPI.getContentData() != null ? moduleAPI.getContentData().size() : 0;
                ((DotSelectorView) componentViewResult.componentView).addDots(numDots);
                componentViewResult.onInternalEvent = (DotSelectorView) componentViewResult.componentView;
                componentViewResult.useMarginsAsPercentagesOverride = false;
                break;
            case PAGE_BUTTON_KEY:
                // IGNORE FOR NOW
                if (componentKey == AppCMSUIKeyType.PAGE_CAROUSEL_ADD_TO_WATCHLIST_KEY) {
                    return componentViewResult;
                }
                componentViewResult.componentView = new Button(context);
                if (!gridElement) {
                    if (!TextUtils.isEmpty(component.getText()) && componentKey != AppCMSUIKeyType.PAGE_PLAY_KEY) {
                        ((TextView) componentViewResult.componentView).setText(component.getText());
                    } else if (moduleAPI.getSettings() != null && !moduleAPI.getSettings().getHideTitle() &&
                            !TextUtils.isEmpty(moduleAPI.getTitle())) {
                        ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle());
                    }
                }
                if (!TextUtils.isEmpty(component.getTextColor())) {
                    ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(context, component.getTextColor())));
                }
                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.setBackgroundColor(Color.parseColor(getColor(context, component.getBackgroundColor())));
                } else {
                    applyBorderToComponent(context, componentViewResult.componentView, component);
                }
                int tintColor = Color.parseColor(getColor(context,
                        appCMSPresenter.getAppCMSMain().getBrand().getGeneral().getPageTitleColor()));
                switch (componentKey) {
                    case PAGE_INFO_KEY:
                        componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.info_icon));
                        break;
                    case PAGE_VIDEO_WATCH_TRAILER_KEY:
                        if (moduleAPI.getContentData().get(0).getContentDetails() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().size() > 0 &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0) != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getId() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getVideoAssets() != null) {
                            componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String[] extraData = new String[3];
                                    extraData[0] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink();
                                    extraData[1] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getVideoAssets().getHls();
                                    extraData[2] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getId();
                                    if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                            component.getAction(),
                                            moduleAPI.getContentData().get(0).getGist().getTitle(),
                                            extraData,
                                            false)) {
                                        Log.e(TAG, "Could not launch action: " +
                                                " permalink: " +
                                                moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                                " action: " +
                                                component.getAction() +
                                                " hls URL: " +
                                                moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets().getHls());
                                    }
                                }
                            });
                        } else {
                            componentViewResult.componentView = null;
                        }
                        break;
                    case PAGE_VIDEO_PLAY_BUTTON_KEY:
                        componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (moduleAPI.getContentData() != null &&
                                        moduleAPI.getContentData().size() > 0 &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getStreamingInfo() != null &&
                                        moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets() != null) {
                                    VideoAssets videoAssets = moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets();
                                    String videoUrl = videoAssets.getHls();
                                    if (TextUtils.isEmpty(videoUrl)) {
                                        for (int i = 0; i < videoAssets.getMpeg().size() && TextUtils.isEmpty(videoUrl); i++) {
                                            videoUrl = videoAssets.getMpeg().get(i).getUrl();
                                        }
                                    }
                                    if (moduleAPI.getContentData() != null &&
                                            moduleAPI.getContentData().size() > 0 &&
                                            moduleAPI.getContentData().get(0) != null &&
                                            moduleAPI.getContentData().get(0).getGist() != null &&
                                            moduleAPI.getContentData().get(0).getGist().getId() != null &&
                                            moduleAPI.getContentData().get(0).getGist().getPermalink() != null) {
                                        String[] extraData = new String[3];
                                        extraData[0] = moduleAPI.getContentData().get(0).getGist().getPermalink();
                                        extraData[1] = videoUrl;
                                        extraData[2] = moduleAPI.getContentData().get(0).getGist().getId();
                                        if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                                component.getAction(),
                                                moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                extraData,
                                                false)) {
                                            Log.e(TAG, "Could not launch action: " +
                                                    " permalink: " +
                                                    moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                                    " action: " +
                                                    component.getAction() +
                                                    " video URL: " +
                                                    videoUrl);
                                        }
                                    }
                                }
                            }
                        });
                        componentViewResult.componentView.setBackground(ContextCompat.getDrawable(context, R.drawable.play_icon));
                        componentViewResult.componentView.getBackground().setTint(tintColor);
                        componentViewResult.componentView.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                        break;
                    case PAGE_PLAY_KEY:
                    case PAGE_PLAY_IMAGE_KEY:
                        componentViewResult.componentView.setBackground(ContextCompat.getDrawable(context, R.drawable.play_icon));
                        componentViewResult.componentView.getBackground().setTint(tintColor);
                        componentViewResult.componentView.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                        break;
                    case PAGE_VIDEO_CLOSE_KEY:
                        Drawable closeDrawable = ContextCompat.getDrawable(context, R.drawable.cancel);
                        componentViewResult.componentView.setBackground(closeDrawable);
                        componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!appCMSPresenter.launchButtonSelectedAction(null,
                                        component.getAction(),
                                        null,
                                        null,
                                        false)) {
                                    Log.e(TAG, "Could not launch action: " +
                                            " action: " +
                                            component.getAction());
                                }
                            }
                        });
                        break;
                    case PAGE_VIDEO_SHARE_KEY:
                        Drawable shareDrawable = ContextCompat.getDrawable(context, R.drawable.share);
                        componentViewResult.componentView.setBackground(shareDrawable);
                        componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppCMSMain appCMSMain = appCMSPresenter.getAppCMSMain();
                                if (appCMSMain != null &&
                                        moduleAPI.getContentData() != null &&
                                        moduleAPI.getContentData().size() > 0 &&
                                        moduleAPI.getContentData().get(0) != null &&
                                        moduleAPI.getContentData().get(0).getGist() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getTitle() != null &&
                                        moduleAPI.getContentData().get(0).getGist().getPermalink() != null) {
                                    StringBuilder filmUrl = new StringBuilder();
                                    filmUrl.append(appCMSMain.getDomainName());
                                    filmUrl.append(moduleAPI.getContentData().get(0).getGist().getPermalink());
                                    String[] extraData = new String[1];
                                    extraData[0] = filmUrl.toString();
                                    if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                            component.getAction(),
                                            moduleAPI.getContentData().get(0).getGist().getTitle(),
                                            extraData,
                                            false)) {
                                        Log.e(TAG, "Could not launch action: " +
                                                " permalink: " +
                                                moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                                " action: " +
                                                component.getAction() +
                                                " film URL: " +
                                                filmUrl.toString());
                                    }
                                }
                            }
                        });
                        break;
                    default:
                }
                break;
            case PAGE_LABEL_KEY:
            case PAGE_TEXTVIEW_KEY:
                componentViewResult.componentView = new TextView(context);
                int textColor = ContextCompat.getColor(context, R.color.colorAccent);
                if (!TextUtils.isEmpty(component.getTextColor())) {
                    textColor = Color.parseColor(getColor(context, component.getTextColor()));
                } else if (component.getStyles() != null) {
                    if (!TextUtils.isEmpty(component.getStyles().getColor())) {
                        textColor = Color.parseColor(getColor(context, component.getStyles().getColor()));
                    } else if (!TextUtils.isEmpty(component.getStyles().getTextColor())) {
                        textColor =
                                Color.parseColor(getColor(context, component.getStyles().getTextColor()));
                    }
                }
                ((TextView) componentViewResult.componentView).setTextColor(textColor);
                if (!gridElement) {
                    switch (componentKey) {
                        case PAGE_API_TITLE:
                            if (!TextUtils.isEmpty(moduleAPI.getTitle())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle());
                            }
                            break;
                        case PAGE_API_DESCRIPTION:
                            if (!TextUtils.isEmpty(moduleAPI.getRawText())) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    ((TextView) componentViewResult.componentView).setText(Html.fromHtml(moduleAPI.getRawText()));
                                } else {
                                    ((TextView) componentViewResult.componentView).setText(Html.fromHtml(moduleAPI.getRawText(), Html.FROM_HTML_MODE_COMPACT));
                                }
                                ((TextView) componentViewResult.componentView).setMovementMethod(LinkMovementMethod.getInstance());
                            }
                            break;
                        case PAGE_TRAY_TITLE_KEY:
                            if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText().toUpperCase());
                            } else if (moduleAPI.getSettings() != null && !moduleAPI.getSettings().getHideTitle() &&
                                    !TextUtils.isEmpty(moduleAPI.getTitle())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle().toUpperCase());
                            }
                            break;
                        case PAGE_VIDEO_DESCRIPTION_KEY:
                            String videoDescription = moduleAPI.getContentData().get(0).getGist().getDescription();
                            if (!TextUtils.isEmpty(videoDescription)) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    ((TextView) componentViewResult.componentView).setText(Html.fromHtml(videoDescription));
                                } else {
                                    ((TextView) componentViewResult.componentView).setText(Html.fromHtml(videoDescription, Html.FROM_HTML_MODE_COMPACT));
                                }
                            }
                            ViewTreeObserver textVto = componentViewResult.componentView.getViewTreeObserver();
                            ViewCreatorMultiLineLayoutListener viewCreatorLayoutListener =
                                    new ViewCreatorMultiLineLayoutListener(((TextView) componentViewResult.componentView),
                                            moduleAPI.getContentData().get(0).getGist().getTitle(),
                                            videoDescription,
                                            textColor,
                                            appCMSPresenter,
                                            false);
                            textVto.addOnGlobalLayoutListener(viewCreatorLayoutListener);
                            break;
                        case PAGE_VIDEO_TITLE_KEY:
                            if (!TextUtils.isEmpty(moduleAPI.getContentData().get(0).getGist().getTitle())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getContentData().get(0).getGist().getTitle());
                            }
                            ViewTreeObserver titleTextVto = componentViewResult.componentView.getViewTreeObserver();
                            ViewCreatorTitleLayoutListener viewCreatorTitleLayoutListener =
                                    new ViewCreatorTitleLayoutListener((TextView) componentViewResult.componentView);
                            titleTextVto.addOnGlobalLayoutListener(viewCreatorTitleLayoutListener);
                            ((TextView) componentViewResult.componentView).setSingleLine(true);
                            ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                            break;
                        case PAGE_VIDEO_SUBTITLE_KEY:
                            setViewWithSubtitle(context,
                                    moduleAPI.getContentData().get(0),
                                    componentViewResult.componentView);
                            break;
                        case PAGE_VIDEO_AGE_LABEL_KEY:
                            if (!TextUtils.isEmpty(moduleAPI.getContentData().get(0).getParentalRating())) {
                                String parentalRating = moduleAPI.getContentData().get(0).getParentalRating();
                                String convertedRating = context.getString(R.string.age_rating_converted_default);
                                if (parentalRating.contains(context.getString(R.string.age_rating_y7))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_y7);
                                } else if (parentalRating.contains(context.getString(R.string.age_rating_y))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_y);
                                } else if (parentalRating.contains(context.getString(R.string.age_rating_g))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_g);
                                } else if (parentalRating.contains(context.getString(R.string.age_rating_pg))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_pg);
                                } else if (parentalRating.contains(context.getString(R.string.age_rating_fourteen))) {
                                    convertedRating = context.getString(R.string.age_rating_converted_fourteen);
                                }
                                ((TextView) componentViewResult.componentView).setText(convertedRating);
                                ((TextView) componentViewResult.componentView).setGravity(Gravity.CENTER);
                                applyBorderToComponent(context,
                                        componentViewResult.componentView,
                                        component);
                            }
                        default:
                    }
                } else {
                    ((TextView) componentViewResult.componentView).setSingleLine(true);
                    ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                }

                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.setBackgroundColor(Color.parseColor(getColor(context, component.getBackgroundColor())));
                }
                if (!TextUtils.isEmpty(component.getFontFamily())) {
                    if (jsonValueKeyMap.get(component.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY) {
                        AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(component.getFontWeight());
                        if (fontWeight == null) {
                            fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                        }
                        Typeface face = null;
                        switch (fontWeight) {
                            case PAGE_TEXT_BOLD_KEY:
                                face = Typeface.createFromAsset(context.getAssets(),context.getString(R.string.opensans_bold_ttf));
                                break;
                            case PAGE_TEXT_SEMIBOLD_KEY:
                                face = Typeface.createFromAsset(context.getAssets(),context.getString(R.string.opensans_semibold_ttf));
                                break;
                            case PAGE_TEXT_EXTRABOLD_KEY:
                                face = Typeface.createFromAsset(context.getAssets(),context.getString(R.string.opensans_extrabold_ttf));
                                break;
                            default:
                                face = Typeface.createFromAsset(context.getAssets(),context.getString(R.string.opensans_regular_ttf));
                        }
                        ((TextView) componentViewResult.componentView).setTypeface(face);
                    }
                }
                break;
            case PAGE_IMAGE_KEY:
                componentViewResult.componentView = new ImageView(context);
                switch (componentKey) {
                    case PAGE_VIDEO_IMAGE_KEY:
                        int viewWidth = BaseView.isLandscape(context) ?
                                ViewGroup.LayoutParams.WRAP_CONTENT :
                                context.getResources().getDisplayMetrics().widthPixels;
                        int viewHeight = (int) BaseView.getViewHeight(context,
                                component.getLayout(),
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        if (viewHeight > 0 && viewWidth > 0 && viewHeight > viewWidth) {
                            Picasso.with(context)
                                    .load(moduleAPI.getContentData().get(0).getGist().getPosterImageUrl())
                                    .resize(viewWidth, viewHeight)
                                    .centerCrop()
                                    .into((ImageView) componentViewResult.componentView);
                        } else if (viewWidth > 0){
                            Picasso.with(context)
                                    .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                    .resize(viewWidth, viewHeight)
                                    .centerCrop()
                                    .into((ImageView) componentViewResult.componentView);
                        } else {
                            Picasso.with(context)
                                    .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                    .into((ImageView) componentViewResult.componentView);
                        }
                        componentViewResult.useWidthOfScreen = !BaseView.isLandscape(context);
                        break;
                    default:
                        if (!TextUtils.isEmpty(component.getImageName())) {
                            Picasso.with(context)
                                    .load(component.getImageName())
                                    .into((ImageView) componentViewResult.componentView);
                        }
                }
                break;
            case PAGE_PROGRESS_VIEW_KEY:
                componentViewResult.componentView = new ProgressBar(context,
                        null,
                        R.style.Widget_AppCompat_ProgressBar_Horizontal);
                if (!TextUtils.isEmpty(component.getProgressColor())) {
                    int color = Color.parseColor(getColor(context, component.getProgressColor()));
                    ((ProgressBar) componentViewResult.componentView).setProgressDrawable(new ColorDrawable(color));
                }
                break;
            case PAGE_SEPARATOR_VIEW_KEY:
                componentViewResult.componentView = new View(context);
                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.
                            setBackgroundColor(Color.parseColor(getColor(context, component.getBackgroundColor())));
                }
                break;
            case PAGE_CASTVIEW_VIEW_KEY:
                if (moduleAPI.getContentData().get(0).getCreditBlocks() == null) {
                    componentViewResult.componentView = null;
                    return componentViewResult;
                }
                String fontFamilyKey = null, fontFamilyKeyTypeParsed = null;
                if (!TextUtils.isEmpty(component.getFontFamilyKey())) {
                    String[] fontFamilyKeyArr = component.getFontFamilyKey().split("-");
                    if (fontFamilyKeyArr.length == 2) {
                        fontFamilyKey = fontFamilyKeyArr[0];
                        fontFamilyKeyTypeParsed = fontFamilyKeyArr[1];
                    }
                }
                int fontFamilyKeyType = Typeface.NORMAL;
                AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(fontFamilyKeyTypeParsed);
                if (fontWeight == AppCMSUIKeyType.PAGE_TEXT_BOLD_KEY ||
                        fontWeight == AppCMSUIKeyType.PAGE_TEXT_SEMIBOLD_KEY ||
                        fontWeight == AppCMSUIKeyType.PAGE_TEXT_EXTRABOLD_KEY) {
                    fontFamilyKeyType = Typeface.BOLD;
                }

                String fontFamilyValue = null, fontFamilyValueTypeParsed = null;
                if (!TextUtils.isEmpty(component.getFontFamilyValue())) {
                    String[] fontFamilyValueArr = component.getFontFamilyValue().split("-");
                    if (fontFamilyValueArr.length == 2) {
                        fontFamilyValue = fontFamilyValueArr[0];
                        fontFamilyValueTypeParsed = fontFamilyValueArr[1];
                    }
                }
                int fontFamilyValueType = Typeface.NORMAL;
                fontWeight = jsonValueKeyMap.get(fontFamilyValueTypeParsed);
                if (fontWeight == AppCMSUIKeyType.PAGE_TEXT_BOLD_KEY ||
                        fontWeight == AppCMSUIKeyType.PAGE_TEXT_SEMIBOLD_KEY ||
                        fontWeight == AppCMSUIKeyType.PAGE_TEXT_EXTRABOLD_KEY) {
                    fontFamilyValueType = Typeface.BOLD;
                }

                textColor = Color.parseColor(getColor(context, component.getTextColor()));
                String directorTitle = null;
                StringBuffer directorListSb = new StringBuffer();
                String starringTitle = null;
                StringBuffer starringListSb = new StringBuffer();
                for (CreditBlock creditBlock : moduleAPI.getContentData().get(0).getCreditBlocks()) {
                    AppCMSUIKeyType creditBlockType = jsonValueKeyMap.get(creditBlock.getTitle());
                    if (creditBlockType != null &&
                            (creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTEDBY_KEY ||
                            creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTOR_KEY ||
                            creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_DIRECTORS_KEY)) {
                        if (!TextUtils.isEmpty(creditBlock.getTitle())) {
                            directorTitle = creditBlock.getTitle().toUpperCase();
                        }
                        if (creditBlock != null && creditBlock.getCredits() != null) {
                            for (int i = 0; i < creditBlock.getCredits().size(); i++) {
                                directorListSb.append(creditBlock.getCredits().get(i).getTitle());
                                if (i < creditBlock.getCredits().size() - 1) {
                                    directorListSb.append(", ");
                                }
                            }
                        }
                    } else if (creditBlockType != null &&
                            creditBlockType == AppCMSUIKeyType.PAGE_VIDEO_CREDITS_STARRING_KEY) {
                        if (!TextUtils.isEmpty(creditBlock.getTitle())) {
                            starringTitle = creditBlock.getTitle().toUpperCase();
                        }
                        if (creditBlock != null && creditBlock.getCredits() != null) {
                            for (int i = 0; i < creditBlock.getCredits().size(); i++) {
                                starringListSb.append(creditBlock.getCredits().get(i).getTitle());
                                if (i < creditBlock.getCredits().size() - 1) {
                                    starringListSb.append(", ");
                                }
                            }
                        }
                    }
                }

                componentViewResult.componentView = new CreditBlocksView(context,
                        fontFamilyKey,
                        fontFamilyKeyType,
                        fontFamilyValue,
                        fontFamilyValueType,
                        directorTitle,
                        directorListSb.toString(),
                        starringTitle,
                        starringListSb.toString(),
                        textColor,
                        BaseView.getFontSizeKey(context, component.getLayout()),
                        BaseView.getFontSizeValue(context, component.getLayout()));
                break;
            case PAGE_VIDEO_STARRATING_KEY:
                int starBorderColor = Color.parseColor(getColor(context, component.getBorderColor()));
                int starFillColor = Color.parseColor(getColor(context, component.getFillColor()));
                float starRating = moduleAPI.getContentData().get(0).getGist().getAverageStarRating();
                componentViewResult.componentView = new StarRating(context,
                        starBorderColor,
                        starFillColor,
                        starRating);
                break;
            default:
        }
        return componentViewResult;
    }

    public static void setViewWithSubtitle(Context context, ContentDatum data, View view) {
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
            infoText.append(runtime + context.getString(R.string.mins_abbreviation));
        }
        if (appendFirstSep) {
            infoText.append(context.getString(R.string.text_separator));
        }
        if (!TextUtils.isEmpty(year)) {
            infoText.append(year);
        }
        if (appendSecondSep) {
            infoText.append(context.getString(R.string.text_separator));
        }
        if (!TextUtils.isEmpty(primaryCategory)) {
            infoText.append(primaryCategory.toUpperCase());
        }
        ((TextView) view).setText(infoText.toString());
        view.setAlpha(0.6f);
    }

    private String getColor(Context context, String color) {
        if (color.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + color;
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

    private void applyBorderToComponent(Context context, View view, Component component) {
        if (component.getBorderWidth() != null && component.getBorderColor() != null) {
            if (component.getBorderWidth() > 0 && !TextUtils.isEmpty(component.getBorderColor())) {
                GradientDrawable ageBorder = new GradientDrawable();
                ageBorder.setShape(GradientDrawable.RECTANGLE);
                ageBorder.setStroke(component.getBorderWidth(),
                        Color.parseColor(getColor(context, component.getBorderColor())));
                ageBorder.setColor(ContextCompat.getColor(context, android.R.color.transparent));
                view.setBackground(ageBorder);
            }
        }
    }
}
