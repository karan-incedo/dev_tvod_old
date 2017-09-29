package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.CreditBlock;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.NavigationFooter;
import com.viewlift.models.data.appcms.ui.android.NavigationUser;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.fragment.ClearDialogFragment;
import com.viewlift.tv.views.presenter.AppCmsListRowPresenter;
import com.viewlift.tv.views.presenter.CardPresenter;
import com.viewlift.tv.views.presenter.JumbotronPresenter;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.CreditBlocksView;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.StarRating;
import com.viewlift.views.customviews.ViewCreatorMultiLineLayoutListener;
import com.viewlift.views.customviews.ViewCreatorTitleLayoutListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.viewlift.models.data.appcms.ui.AppCMSUIKeyType.PAGE_API_DESCRIPTION;
import static com.viewlift.tv.views.activity.AppCmsHomeActivity.DIALOG_FRAGMENT_TAG;

/**
 * Created by viewlift on 5/5/17.
 */

public class TVViewCreator {
    private static final String TAG = "ViewCreator";

    private static LruCache<String, TVPageView> pageViewLruCache;
    private static int PAGE_LRU_CACHE_SIZE = 10;
    public ArrayObjectAdapter mRowsAdapter;

    private static LruCache<String, TVPageView> getPageViewLruCache() {
        if (pageViewLruCache == null) {
            pageViewLruCache = new LruCache<>(PAGE_LRU_CACHE_SIZE);
        }
        return pageViewLruCache;
    }

    public static class ComponentViewResult {
        View componentView;
        OnInternalEvent onInternalEvent;
        boolean useMarginsAsPercentagesOverride;
        boolean useWidthOfScreen;
    }

    ComponentViewResult componentViewResult;

    public void removeLruCacheItem(Context context, String pageId) {
        if (getPageViewLruCache().get(pageId + BaseView.isLandscape(context)) != null) {
            getPageViewLruCache().remove(pageId + BaseView.isLandscape(context));
        }
    }

    public TVPageView generatePage(Context context,
                                   AppCMSPageUI appCMSPageUI,
                                   AppCMSPageAPI appCMSPageAPI,
                                   Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                   AppCMSPresenter appCMSPresenter,
                                   List<String> modulesToIgnore,
                                   boolean isFromLoginPage) {
        if (appCMSPageUI == null || appCMSPageAPI == null) {
            return null;
        }

        TVPageView pageView = getPageViewLruCache().get(appCMSPageAPI.getId());
        boolean newView = false;
        if (pageView == null || pageView.getContext() != context) {
            pageView = new TVPageView(context, appCMSPageUI);
            getPageViewLruCache().put(appCMSPageAPI.getId(), pageView);
            newView = true;
        }

        if (true/*newView || !appCMSPresenter.isPagePrimary(appCMSPageAPI.getId())*/) {
            pageView.getChildrenContainer().removeAllViews();
            Runtime.getRuntime().gc();
            componentViewResult = new ComponentViewResult();
            createPageView(context,
                    appCMSPageUI,
                    appCMSPageAPI,
                    pageView,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    modulesToIgnore,
                    isFromLoginPage);
            getPageViewLruCache().put(appCMSPageAPI.getId(), pageView);
        } /*else {
            pageView.
        }*/
        return pageView;
    }

    public ComponentViewResult getComponentViewResult() {
        return componentViewResult;
    }

    protected void createPageView(Context context,
                                  AppCMSPageUI appCMSPageUI,
                                  AppCMSPageAPI appCMSPageAPI,
                                  TVPageView pageView,
                                  Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                  AppCMSPresenter appCMSPresenter,
                                  List<String> modulesToIgnore,
                                  boolean isFromLoginDialog) {
        appCMSPresenter.clearOnInternalEvents();
        List<ModuleList> modulesList = appCMSPageUI.getModuleList();
        ViewGroup childrenContainer = pageView.getChildrenContainer();
        for (int i = 0; i < modulesList.size(); i++) {
            ModuleList module = modulesList.get(i);
            if (!modulesToIgnore.contains(module.getView())) {
                Module moduleAPI = matchModuleAPIToModuleUI(module, appCMSPageAPI, jsonValueKeyMap);
                View childView = createModuleView(context,
                        module,
                        moduleAPI,
                        pageView,
                        jsonValueKeyMap,
                        appCMSPresenter,
                        appCMSPageAPI,
                        isFromLoginDialog);
                if (childView != null) {
                    childrenContainer.addView(childView);
                }
            }

            if (i == modulesList.size() - 1) {
                //now check the Rows Adapter.
                if (mRowsAdapter != null) {
                    FrameLayout browseFrame = new FrameLayout(pageView.getContext());
                    LinearLayout.LayoutParams browseParam = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    browseFrame.setLayoutParams(browseParam);
                    browseFrame.setId(R.id.appcms_browsefragment);
                    childrenContainer.addView(browseFrame);
                }
            }
        }
        List<OnInternalEvent> presenterOnInternalEvents = appCMSPresenter.getOnInternalEvents();
        if (presenterOnInternalEvents != null) {
            for (OnInternalEvent onInternalEvent : presenterOnInternalEvents) {
                for (OnInternalEvent receiverInternalEvent : presenterOnInternalEvents) {
                    onInternalEvent.addReceiver(receiverInternalEvent);
                }
            }
        }
    }

    int trayIndex = -1;

    public View createModuleView(final Context context,
                                 ModuleList module,
                                 final Module moduleAPI,
                                 TVPageView pageView,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter, AppCMSPageAPI appCMSPageAPI,
                                 boolean isFromLoginDialog) {
        TVModuleView moduleView = null;
        if (Arrays.asList(context.getResources().getStringArray(R.array.app_cms_tray_modules)).contains(module.getView())) {
            if (module.getView().equalsIgnoreCase(context.getResources().getString(R.string.carousel_nodule))) {
                if (null == mRowsAdapter) {
                    AppCmsListRowPresenter appCmsListRowPresenter = new AppCmsListRowPresenter(context, appCMSPresenter);
                    mRowsAdapter = new ArrayObjectAdapter(appCmsListRowPresenter);
                }

                for (Component component : module.getComponents()) {
                    createTrayModule(context, component, module.getLayout(), module, moduleAPI,
                            pageView, jsonValueKeyMap, appCMSPresenter, true);
                }
            } else {
                if (null == mRowsAdapter) {
                    AppCmsListRowPresenter appCmsListRowPresenter = new AppCmsListRowPresenter(context, appCMSPresenter);
                    mRowsAdapter = new ArrayObjectAdapter(appCmsListRowPresenter);
                }

                for (Component component : module.getComponents()) {
                    createTrayModule(context, component, module.getLayout(), module, moduleAPI,
                            pageView, jsonValueKeyMap, appCMSPresenter, false);
                }
            }
            return null;
        } else {

            moduleView = new TVModuleView<>(context, module);
            ViewGroup childrenContainer = moduleView.getChildrenContainer();

            if (context.getResources().getString(R.string.appcms_detail_module).equalsIgnoreCase(module.getView())) {
                if (null == moduleAPI
                        || moduleAPI.getContentData() == null) {
                    TextView textView = new TextView(context);
                    textView.setText(context.getString(R.string.no_data_available));
                    textView.setGravity(Gravity.CENTER);
                    Component component = new Component();
                    component.setFontFamily(context.getString(R.string.app_cms_page_font_family_key));
                    component.setFontWeight(context.getString(R.string.app_cms_page_font_semibold_key));
                    textView.setTypeface(Utils.getTypeFace(context, jsonValueKeyMap, component));
                    childrenContainer.addView(textView);
                    return moduleView;
                }

                final TVPageView finalPageView = pageView;
                if (null != moduleAPI.getContentData()
                        && null != moduleAPI.getContentData().get(0)
                        && null != moduleAPI.getContentData().get(0).getGist()
                        && null != moduleAPI.getContentData().get(0).getGist().getVideoImageUrl()) {
                    Glide.with(context).load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                            .asBitmap().into(new SimpleTarget<Bitmap>(TVBaseView.DEVICE_WIDTH,
                            TVBaseView.DEVICE_HEIGHT) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Drawable drawable = new BitmapDrawable(context.getResources(), resource);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                finalPageView.setBackground(drawable);
                                finalPageView.getChildrenContainer().setBackgroundColor(ContextCompat.getColor(context, R.color.appcms_detail_screen_shadow_color));
                            }
                        }
                    });
                }
            }

            if (module.getComponents() != null) {
                for (int i = 0; i < module.getComponents().size(); i++) {
                    Component component = module.getComponents().get(i);
                    createComponentView(context,
                            component,
                            module.getLayout(),
                            moduleAPI,
                            pageView,
                            module.getSettings(),
                            jsonValueKeyMap,
                            appCMSPresenter,
                            false,
                            module.getView(),
                            isFromLoginDialog);
                    if (componentViewResult.onInternalEvent != null) {
                        appCMSPresenter.addInternalEvent(componentViewResult.onInternalEvent);
                    }

                    View componentView = componentViewResult.componentView;
                    if (componentView != null) {
                        childrenContainer.addView(componentView);
                        moduleView.setComponentHasView(i, true);

                        moduleView.setViewMarginsFromComponent(component,
                                componentView,
                                moduleView.getLayout(),
                                childrenContainer,
                                jsonValueKeyMap,
                                componentViewResult.useMarginsAsPercentagesOverride,
                                componentViewResult.useWidthOfScreen,
                                module.getView());

                    } else {
                        moduleView.setComponentHasView(i, false);
                    }
                }
            }
        }
        return moduleView;
    }


    CustomHeaderItem customHeaderItem = null;

    public void createTrayModule(final Context context,
                                 final Component component,
                                 final Layout parentLayout,
                                 final ModuleList moduleUI,
                                 final Module moduleData,
                                 @Nullable TVPageView pageView,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 final AppCMSPresenter appCMSPresenter,
                                 boolean isCarousel) {
        AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());
        if (componentType == null) {
            componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }
        AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());
        if (componentKey == null) {
            componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }
        switch (componentType) {
            case PAGE_LABEL_KEY:
                switch (componentKey) {
                    case PAGE_TRAY_TITLE_KEY:
                        if (moduleData != null) {
                            customHeaderItem = null;
                            customHeaderItem = new CustomHeaderItem(context, trayIndex++,
                                    (moduleData != null && moduleData.getTitle() != null) ? moduleData.getTitle().toUpperCase() : "");
                            customHeaderItem.setmIsCarousal(isCarousel);
                            customHeaderItem.setmListRowLeftMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                            customHeaderItem.setmListRowRightMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                            customHeaderItem.setmBackGroundColor(moduleUI.getLayout().getTv().getBackgroundColor());
                            customHeaderItem.setmListRowHeight(Integer.valueOf(moduleUI.getLayout().getTv().getHeight()));
                            customHeaderItem.setFontFamily(component.getFontFamily());
                            customHeaderItem.setFontWeight(component.getFontWeight());
                            customHeaderItem.setFontSize(component.getLayout().getTv().getFontSize());
                            customHeaderItem.setmModuleId(moduleData.getId());
                        }
                        break;
                }
                break;
            case PAGE_CAROUSEL_VIEW_KEY: {
                customHeaderItem = new CustomHeaderItem(context, trayIndex++, "");
                customHeaderItem.setmIsCarousal(true);
                customHeaderItem.setmListRowLeftMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                customHeaderItem.setmListRowRightMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                customHeaderItem.setmBackGroundColor(moduleUI.getLayout().getTv().getBackgroundColor());
                customHeaderItem.setmListRowHeight(Integer.valueOf(moduleUI.getLayout().getTv().getHeight()));
                customHeaderItem.setmModuleId(moduleData.getId());
            }

            if (moduleData != null) {
                CardPresenter cardPresenter = new JumbotronPresenter(context, appCMSPresenter);
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                if (moduleData.getContentData() != null && moduleData.getContentData().size() > 0) {
                    List<ContentDatum> contentData1 = moduleData.getContentData();
                    List<Component> components = component.getComponents();
                    for (ContentDatum contentData : contentData1) {
                        BrowseFragmentRowData rowData = new BrowseFragmentRowData();
                        rowData.contentData = contentData;
                        rowData.uiComponentList = components;
                        rowData.action = component.getTrayClickAction();
                        listRowAdapter.add(rowData);
                        Log.d(TAG, "NITS header Items ===== " + rowData.contentData.getGist().getTitle());
                    }
                    mRowsAdapter.add(new ListRow(customHeaderItem, listRowAdapter));
                }
            }
            break;

            case PAGE_COLLECTIONGRID_KEY:
                        /*for(Component component1 : component.getComponents()){*/
                if (customHeaderItem == null) {
                    customHeaderItem = new CustomHeaderItem(context, trayIndex++, moduleData != null ? moduleData.getTitle() : "");
                    customHeaderItem.setmIsCarousal(false);
                    customHeaderItem.setmListRowLeftMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                    customHeaderItem.setmListRowRightMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                    customHeaderItem.setmBackGroundColor(moduleUI.getLayout().getTv().getBackgroundColor());
                    customHeaderItem.setmListRowHeight(Integer.valueOf(moduleUI.getLayout().getTv().getHeight()));
                    customHeaderItem.setFontFamily(component.getFontFamily());
                    customHeaderItem.setFontWeight(component.getFontWeight());
                    customHeaderItem.setFontSize(component.getLayout().getTv().getFontSize());
                    customHeaderItem.setmModuleId(moduleData.getId());
                }
                if (null != moduleData) {
                    CardPresenter trayCardPresenter = new CardPresenter(context, appCMSPresenter,
                            Integer.valueOf(component.getLayout().getTv().getHeight()),
                            Integer.valueOf(component.getLayout().getTv().getWidth()),
                            jsonValueKeyMap
                    );
                    ArrayObjectAdapter traylistRowAdapter = new ArrayObjectAdapter(trayCardPresenter);

                    if (moduleData.getContentData() != null && moduleData.getContentData().size() > 0) {
                        List<ContentDatum> contentData1 = moduleData.getContentData();
                        List<Component> components = component.getComponents();
                        for (ContentDatum contentData : contentData1) {
                            BrowseFragmentRowData rowData = new BrowseFragmentRowData();
                            rowData.contentData = contentData;
                            rowData.uiComponentList = components;
                            rowData.action = component.getTrayClickAction();
                            traylistRowAdapter.add(rowData);
                            Log.d(TAG, "NITS header Items ===== " + rowData.contentData.getGist().getTitle());
                        }
                        mRowsAdapter.add(new ListRow(customHeaderItem, traylistRowAdapter));
                    }
                }
                break;
        }
    }


    public void createComponentView(final Context context,
                                    final Component component,
                                    final Layout parentLayout,
                                    final Module moduleAPI,
                                    @Nullable final TVPageView pageView,
                                    final Settings settings,
                                    Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                    final AppCMSPresenter appCMSPresenter,
                                    boolean gridElement,
                                    String viewType,
                                    boolean isFromLoginDialog) {
        componentViewResult.componentView = null;
        componentViewResult.useMarginsAsPercentagesOverride = true;
        componentViewResult.useWidthOfScreen = false;
        if (moduleAPI == null) {
            return;
        }
        AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());
        if (componentType == null) {
            componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());
        if (componentKey == null) {
            componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        switch (componentType) {
            case PAGE_TABLE_VIEW_KEY:
                componentViewResult.componentView = new RecyclerView(context);
                ((RecyclerView) componentViewResult.componentView)
                        .setLayoutManager(new LinearLayoutManager(context,
                                LinearLayoutManager.VERTICAL,
                                false));
                AppCMSTVTrayAdapter appCMSTVTrayItemAdapter = new AppCMSTVTrayAdapter(context,
                        moduleAPI.getContentData(),
                        component,
                        component.getLayout(),
                        appCMSPresenter,
                        jsonValueKeyMap,
                        viewType,
                        this,
                        moduleAPI);
                ((RecyclerView) componentViewResult.componentView)
                        .setAdapter(appCMSTVTrayItemAdapter);
                componentViewResult.onInternalEvent = appCMSTVTrayItemAdapter;
                break;
            case PAGE_BUTTON_KEY:
                if (componentKey != AppCMSUIKeyType.PAGE_VIDEO_CLOSE_KEY) {
                    componentViewResult.componentView = new Button(context);
                } else {
                    componentViewResult.componentView = new ImageButton(context);
                }
                if (!gridElement) {
                    if (!TextUtils.isEmpty(component.getText()) && componentKey != AppCMSUIKeyType.PAGE_PLAY_KEY) {
                        ((TextView) componentViewResult.componentView).setText(component.getText());
                    } else if (moduleAPI.getSettings() != null &&
                            !moduleAPI.getSettings().getHideTitle() &&
                            !TextUtils.isEmpty(moduleAPI.getTitle()) &&
                            componentKey != AppCMSUIKeyType.PAGE_VIDEO_CLOSE_KEY) {
                        ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle());
                    }
                }

                if (!TextUtils.isEmpty(component.getTextColor())) {
                    ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(getColor(context, component.getTextColor())));
                    if (!TextUtils.isEmpty(component.getBorderColor())) {
                        ((TextView) componentViewResult.componentView).setTextColor(Utils.getButtonTextColorDrawable(
                                Utils.getColor(context, component.getBorderColor()),
                                Utils.getColor(context, component.getTextColor())));
                    }
                }
                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.setBackgroundColor(Color.parseColor(getColor(context, component.getBackgroundColor())));
                } else {
                    componentViewResult.componentView.setBackground(
                            Utils.setButtonBackgroundSelector(context,
                                    Color.parseColor(Utils.getTitleColor(context, appCMSPresenter)),
                                    component));
                }

                if (component.getLetetrSpacing() != 0) {
                    ((TextView) componentViewResult.componentView).
                            setLetterSpacing(component.getLetetrSpacing());
                }

                int tintColor = Color.parseColor(getColor(context,
                        Utils.getFocusColor(context, appCMSPresenter)));

                switch (componentKey) {
                    case PAGE_INFO_KEY:
                        componentViewResult.componentView.setBackground(context.getDrawable(R.drawable.info_icon));
                        componentViewResult.componentView.setFocusable(false);
                        break;

                    case PAGE_VIDEO_WATCH_TRAILER_KEY:
                        if (moduleAPI.getContentData().get(0).getContentDetails() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().size() > 0 &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0) != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getId() != null &&
                                moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getVideoAssets() != null) {
                            View btnWatchTrailer = componentViewResult.componentView;
                            componentViewResult.componentView.setFocusable(true);
                            componentViewResult.componentView.setTag("WATCH_TRAILER");
                            componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    appCMSPresenter.showLoadingDialog(true);
                                    String[] extraData = new String[4];
                                    extraData[0] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink();
                                    extraData[1] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getVideoAssets().getHls();
                                    extraData[2] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getId();
                                    if (!appCMSPresenter.launchTVButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                            component.getAction(),
                                            moduleAPI.getContentData().get(0).getGist().getTitle(),
                                            extraData,
                                            false,
                                            moduleAPI.getContentData().get(0))) {
                                        appCMSPresenter.showLoadingDialog(false);
                                        Log.e(TAG, "Could not launch action: " +
                                                " permalink: " +
                                                moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                                " action: " +
                                                component.getAction() +
                                                " hls URL: " +
                                                moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets().getHls());
                                    }

                                    // Disable the button for 1 second and enable it back in handler
                                    btnWatchTrailer.setClickable(false);

                                    // enable the button after 1 second
                                    new Handler().postDelayed(() ->
                                            btnWatchTrailer.setClickable(true), 1000);
                                }
                            });
                        } else {
                            componentViewResult.componentView = null;
                        }
                        break;

                    case PAGE_CAROUSEL_ADD_TO_WATCHLIST_KEY:
                        componentViewResult.componentView.setFocusable(true);
                        componentViewResult.componentView.setTag("WATCHLIST");

                        Button btn = (Button) componentViewResult.componentView;
                        final boolean[] queued = new boolean[1];


                        if(appCMSPresenter.isUserLoggedIn(context)) {
                            appCMSPresenter.getUserVideoStatus(
                                    moduleAPI.getContentData().get(0).getGist().getId(),
                                    userVideoStatusResponse -> {
                                        if (null != userVideoStatusResponse) {
                                            queued[0] = userVideoStatusResponse.getQueued();
                                            Log.d(TAG, "appCMSAddToWatchlistResult: qued: " + queued[0]);
                                            if (queued[0]) {
                                                btn.setText(context.getString(R.string.remove_from_watchlist));
                                            } else {
                                                btn.setText(context.getString(R.string.add_to_watchlist));
                                            }
                                        }
                                    });
                        }

                        componentViewResult.componentView.setOnClickListener(v -> {
                                    Log.d(TAG, "appCMSAddToWatchlistResult: clicked");
                                    if (appCMSPresenter.isUserLoggedIn(context)) {
                                        appCMSPresenter.editWatchlist(
                                                moduleAPI.getContentData().get(0).getGist().getId(),
                                                appCMSAddToWatchlistResult -> {
                                                    Log.d(TAG, "appCMSAddToWatchlistResult");
                                                    queued[0] = !queued[0];
                                                    if (queued[0]) {
                                                        btn.setText(context.getString(R.string.remove_from_watchlist));
                                                    } else {
                                                        btn.setText(context.getString(R.string.add_to_watchlist));
                                                    }
                                                }, !queued[0]);
                                    } else /*User is not logged in*/{

                                        ClearDialogFragment newFragment = getClearDialogFragment(
                                                context,
                                                appCMSPresenter,
                                                context.getResources().getDimensionPixelSize(R.dimen.text_clear_dialog_width),
                                                context.getResources().getDimensionPixelSize(R.dimen.text_add_to_watchlist_sign_in_dialog_height),
                                                context.getString(R.string.add_to_watchlist),
                                                context.getString(R.string.add_to_watchlist_dialog_text),
                                                context.getString(R.string.sign_in_text),
                                                context.getString(android.R.string.cancel),
                                                14

                                        );
                                        newFragment.setOnPositiveButtonClicked(s ->{

                                            NavigationUser navigationUser = appCMSPresenter.getLoginNavigation();
                                            appCMSPresenter.navigateToTVPage(
                                                    navigationUser.getPageId(),
                                                    navigationUser.getTitle(),
                                                    navigationUser.getUrl(),
                                                    false,
                                                    Uri.EMPTY,
                                                    false,
                                                    false,
                                                    true
                                            );
                                        });
                                    }
                                }
                        );
                        break;


                    case PAGE_ADD_TO_WATCHLIST_KEY:
                        componentViewResult.componentView.setFocusable(true);
                        componentViewResult.componentView.setTag("WATCHLIST");
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
                                        String[] extraData = new String[4];
                                        extraData[0] = moduleAPI.getContentData().get(0).getGist().getPermalink();
                                        extraData[1] = videoUrl;
                                        extraData[2] = moduleAPI.getContentData().get(0).getGist().getId();
                                        if (null != moduleAPI.getContentData().get(0).getContentDetails()
                                                && moduleAPI.getContentData().get(0).getContentDetails().getClosedCaptions() != null
                                                && moduleAPI.getContentData().get(0).getContentDetails().getClosedCaptions().get(0) != null
                                                && moduleAPI.getContentData().get(0).getContentDetails().getClosedCaptions().get(0).getUrl() != null) {
                                            extraData[3] = moduleAPI.getContentData().get(0).getContentDetails().getClosedCaptions().get(0).getUrl();
                                        }

                                        if (!appCMSPresenter.launchTVButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                                component.getAction(),
                                                moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                extraData,
                                                false,
                                                moduleAPI.getContentData().get(0))) {
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
                        componentViewResult.componentView.setFocusable(false);
                        componentViewResult.componentView.setTag("PLAY_BUTTON");
                        // componentViewResult.componentView = null;
                        break;

                    case PAGE_PLAY_KEY:
                    case PAGE_PLAY_IMAGE_KEY:
                        componentViewResult.componentView.setBackground(ContextCompat.getDrawable(context, R.drawable.play_icon));
                        componentViewResult.componentView.getBackground().setTint(tintColor);
                        componentViewResult.componentView.getBackground().setTintMode(PorterDuff.Mode.MULTIPLY);
                        break;

                    case PAGE_VIDEO_CLOSE_KEY:
                        ((ImageButton) componentViewResult.componentView).setImageResource(R.drawable.cancel);
                        ((ImageButton) componentViewResult.componentView).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        componentViewResult.componentView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                        componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!appCMSPresenter.launchTVButtonSelectedAction(null,
                                        component.getAction(),
                                        null,
                                        null,
                                        false,
                                        null)) {
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
                                   /* if (!appCMSPresenter.launchButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                            component.getAction(),
                                            moduleAPI.getContentData().get(0).getGist().getTitle(),
                                            extraData,
                                            moduleAPI.getContentData().get(0),
                                            false)) {
                                        Log.e(TAG, "Could not launch action: " +
                                                " permalink: " +
                                                moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                                " action: " +
                                                component.getAction() +
                                                " film URL: " +
                                                filmUrl.toString());
                                    }*/
                                }
                            }
                        });
                        break;

                    case PAGE_FORGOTPASSWORD_KEY:
                        componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String[] extraData = new String[1];
                                extraData[0] = component.getKey();
                                appCMSPresenter.launchTVButtonSelectedAction(null,
                                        component.getAction(),
                                        null,
                                        extraData,
                                        false,
                                        null
                                );
                            }
                        });
                        break;

                    case RESET_PASSWORD_CANCEL_BUTTON_KEY:
                    case PAGE_DOWNLOAD_QUALITY_CANCEL_BUTTON_KEY:
                        componentViewResult.componentView.setId(R.id.reset_password_cancel_button);
                        break;
                    case RESET_PASSWORD_CONTINUE_BUTTON_KEY:
                        componentViewResult.componentView.setId(R.id.reset_password_continue_button);
                        break;

                    case PAGE_LOGIN_BUTTON_KEY:
                        componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (pageView.getChildrenContainer().getChildAt(0) instanceof TVModuleView) {
                                    TVModuleView tvModuleView = (TVModuleView) pageView.getChildrenContainer().getChildAt(0);
                                    String emailId = ((EditText) tvModuleView.findViewById(R.id.email_edit_box)).getEditableText().toString();
                                    String password = ((EditText) tvModuleView.findViewById(R.id.password_edit_box)).getEditableText().toString();
                                    Log.d(TAG, "emailid = " + emailId + "password = " + password);

                                    if ((emailId != null && emailId.length() == 0)) {
                                        appCMSPresenter.openTVErrorDialog(context.getString(R.string.blank_email_error_msg),
                                                context.getString(R.string.app_cms_login));
                                        return;
                                    }
                                    if((password != null && password.length() == 0)){
                                        appCMSPresenter.openTVErrorDialog(context.getString(R.string.blank_password_error_msg),
                                                context.getString(R.string.app_cms_login));
                                        return;
                                    }
                                    String[] authData = new String[2];
                                    authData[0] = emailId;
                                    authData[1] = password;
                                    appCMSPresenter.setLaunchType(isFromLoginDialog ? AppCMSPresenter.LaunchType.LOGIN_AND_SIGNUP : AppCMSPresenter.LaunchType.HOME);
                                    appCMSPresenter.launchTVButtonSelectedAction(null,
                                            component.getAction(),
                                            null,
                                            authData,
                                            false,
                                            null);
                                }
                            }
                        });
                        break;
                    case PAGE_SETTING_LOGOUT_BUTTON_KEY:
                        componentViewResult.componentView.setOnClickListener(v -> appCMSPresenter.logoutTV());
                        break;


                    case PAGE_REMOVEALL_KEY:
                        if (moduleAPI.getContentData() != null
                                && moduleAPI.getContentData().size() > 0) {
                            Button buttonRemoveAll = (Button) componentViewResult.componentView;
                            buttonRemoveAll.setId(R.id.appcms_removeall);
                            buttonRemoveAll.setOnClickListener(v -> {
                                OnInternalEvent onInternalEvent = componentViewResult.onInternalEvent;
                                switch (jsonValueKeyMap.get(viewType)) {
                                    case PAGE_HISTORY_MODULE_KEY:
                                        ClearDialogFragment newFragment = getClearDialogFragment(
                                                context,
                                                appCMSPresenter,
                                                context.getResources().getDimensionPixelSize(R.dimen.text_clear_dialog_width),
                                                context.getResources().getDimensionPixelSize(R.dimen.text_clear_dialog_height),
                                                null,
                                                context.getString(R.string.clear_history_message),
                                                context.getString(R.string.yes),
                                                context.getString(android.R.string.cancel),
                                                22.5f

                                        );
                                        newFragment.setOnPositiveButtonClicked(s ->
                                                appCMSPresenter.clearHistory(
                                                        appCMSDeleteHistoryResult -> {
                                                            onInternalEvent.sendEvent(null);
                                                            buttonRemoveAll.setFocusable(false);

                                                        }
                                                )
                                        );
                                        break;

                                    case PAGE_WATCHLIST_MODULE_KEY:
                                        ClearDialogFragment newFragment1 = getClearDialogFragment(
                                                context,
                                                appCMSPresenter,
                                                context.getResources().getDimensionPixelSize(R.dimen.text_clear_dialog_width),
                                                context.getResources().getDimensionPixelSize(R.dimen.text_clear_dialog_height),
                                                null,
                                                context.getString(R.string.clear_watchlist_message),
                                                context.getString(R.string.yes),
                                                context.getString(android.R.string.cancel),
                                                22.5f
                                        );
                                        newFragment1.setOnPositiveButtonClicked(s ->
                                                appCMSPresenter.clearWatchlist(
                                                        appCMSAddToWatchlistResult -> {
                                                            onInternalEvent.sendEvent(null);
                                                            buttonRemoveAll.setFocusable(false);
                                                        }
                                                )
                                        );
                                        break;

                                    default:
                                        break;
                                }
                            });
                        } else {
                            componentViewResult.componentView.setFocusable(false);
                        }
                        break;

                    default:
                }
                break;

            case PAGE_LABEL_KEY:
            case PAGE_TEXTVIEW_KEY:
                if (componentKey == PAGE_API_DESCRIPTION) {
                    componentViewResult.componentView = new ScrollView(context);
                } else {
                    componentViewResult.componentView = new TextView(context);
                }

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
                if (componentKey != PAGE_API_DESCRIPTION)
                    ((TextView) componentViewResult.componentView).setTextColor(textColor);
                if (!gridElement) {
                    switch (componentKey) {
                        case PAGE_API_TITLE:
                            if (!TextUtils.isEmpty(moduleAPI.getTitle())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle().toUpperCase());
                                if (component.getNumberOfLines() != 0) {
                                    ((TextView) componentViewResult.componentView).setMaxLines(component.getNumberOfLines());
                                }
                                ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                            } else if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText().toUpperCase());
                            }
                            ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(Utils.getFocusColor(context, appCMSPresenter)));
                            componentViewResult.componentView.setFocusable(false);
                            componentViewResult.componentView.setTag("TITLE");
                            break;

                        case PAGE_API_DESCRIPTION:
                            if (!TextUtils.isEmpty(moduleAPI.getRawText())) {
                                TextView textView = new TextView(context);
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    textView.setText(Html.fromHtml(moduleAPI.getRawText()));
                                } else {
                                    textView.setText(Html.fromHtml(moduleAPI.getRawText(), Html.FROM_HTML_MODE_COMPACT));
                                }

                                textView.setFocusable(true);
                                //  componentViewResult.componentView.setTag("API_DSECRIPTION");

                                int color = ContextCompat.getColor(context, R.color.colorAccent);
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
                                textView.setTextColor(textColor);

                                setTypeFace(context,
                                        jsonValueKeyMap,
                                        component,
                                        textView);

                                ((ScrollView) componentViewResult.componentView).addView(textView);
                            }


                            break;

                        case RESET_PASSWORD_TITLE_KEY:
                            ((TextView) componentViewResult.componentView).setTextColor(Color.parseColor(Utils.getFocusColor(context, appCMSPresenter)));
                            if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText());
                            }
                            break;

                        case CONTACT_US_EMAIL_LABEL:
                            if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText() + " "
                                        + appCMSPresenter.getAppCMSMain().getCustomerService().getEmail());
                            }
                            break;

                        case CONTACT_US_PHONE_LABEL:
                            if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText() + " "
                                        + appCMSPresenter.getAppCMSMain().getCustomerService().getPhone());
                            }
                            break;
                        case PAGE_TRAY_TITLE_KEY:
                            if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText().toUpperCase());
                            } else if (moduleAPI.getSettings() != null && !moduleAPI.getSettings().getHideTitle() &&
                                    !TextUtils.isEmpty(moduleAPI.getTitle())) {
                                ((TextView) componentViewResult.componentView).setText(moduleAPI.getTitle().toUpperCase());
                            }
                            componentViewResult.componentView.setFocusable(false);
                            componentViewResult.componentView.setTag("TRAY_TITLE");
                            break;

                        case PAGE_VIDEO_DESCRIPTION_KEY:
                            String videoDescription = moduleAPI.getContentData().get(0).getGist().getDescription();

                            if (null == videoDescription) {
                                videoDescription = moduleAPI.getContentData().get(0).getGist().getTitle();
                            }
                            if (!TextUtils.isEmpty(videoDescription)) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                    ((TextView) componentViewResult.componentView).setText(Html.fromHtml(videoDescription));
                                } else {
                                    ((TextView) componentViewResult.componentView).setText(Html.fromHtml(videoDescription, Html.FROM_HTML_MODE_COMPACT));
                                }
                            }
                            ViewTreeObserver textVto = componentViewResult.componentView.getViewTreeObserver();
                            final ViewCreatorMultiLineLayoutListener viewCreatorLayoutListener =
                                    new ViewCreatorMultiLineLayoutListener(((TextView) componentViewResult.componentView),
                                            moduleAPI.getContentData().get(0).getGist().getTitle(),
                                            videoDescription,
                                            appCMSPresenter,
                                            false);
                            textVto.addOnGlobalLayoutListener(viewCreatorLayoutListener);

                            final String fullText = videoDescription;
                            componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    appCMSPresenter.showMoreDialog(moduleAPI.getContentData().get(0).getGist().getTitle(),
                                            fullText);
                                }
                            });

                            // componentViewResult.componentView.setFocusable(true);
                            final int _textColor = textColor;
                            componentViewResult.componentView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View view, boolean b) {
                                    viewCreatorLayoutListener.setSpanOnFocus((TextView) view, b, _textColor);
                                }
                            });
                            componentViewResult.componentView.setTag("VIDEO_DESC_KEY");
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
                            componentViewResult.componentView.setFocusable(false);
                            componentViewResult.componentView.setTag("VIDEO_TITLE_KEY");
                            break;

                        case PAGE_VIDEO_SUBTITLE_KEY:
                            setViewWithSubtitle(context,
                                    moduleAPI.getContentData().get(0),
                                    componentViewResult.componentView);
                            componentViewResult.componentView.setFocusable(false);
                            componentViewResult.componentView.setTag("SUBTITLE");
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
                            componentViewResult.componentView.setFocusable(false);
                            componentViewResult.componentView.setTag("AGE_LABEL");

                        case PAGE_SIGNUP_FOOTER_LABEL_KEY:
                            String text = context.getString(R.string.sign_up_tos_and_pp_text);
                            SpannableString spannableString = new SpannableString(text);
                            String tosText = "terms of use";
                            if (text.contains(tosText)) {
                                int tosStartIndex = text.indexOf(tosText);
                                int tosEndIndex = tosText.length() + tosStartIndex;
                                ClickableSpan clickableSpan = new ClickableSpan() {
                                    @Override
                                    public void onClick(View textView) {
                                        NavigationFooter tosNavigation = null;
                                        List<NavigationFooter> navigationFooter = appCMSPresenter.getNavigation().getNavigationFooter();
                                        for(NavigationFooter navigationFooter1 : navigationFooter){
                                            if(navigationFooter1.getTitle().equalsIgnoreCase("Terms of Service")){
                                                tosNavigation = navigationFooter1;
                                                break;
                                            }
                                        }

                                        if(null != tosNavigation){
                                            appCMSPresenter.navigateToTVPage(
                                                    tosNavigation.getPageId(),
                                                    tosNavigation.getTitle(),
                                                    tosNavigation.getUrl(),
                                                    false,
                                                    Uri.EMPTY,
                                                    false,
                                                    true,
                                                    false
                                            );
                                        }

                                    }
                                };
                                spannableString.setSpan(clickableSpan, tosStartIndex, tosEndIndex,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannableString.setSpan(
                                        new ForegroundColorSpan(Color.parseColor(appCMSPresenter
                                                .getAppCMSMain().getBrand().getGeneral()
                                                .getBlockTitleColor())),
                                        tosStartIndex,
                                        tosEndIndex,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            }
                            String ppText = "privacy policy";
                            if (text.contains(ppText)) {
                                int ppStartIndex = text.indexOf(ppText);
                                int ppEndIndex = ppText.length() + ppStartIndex;
                                ClickableSpan clickableSpan1 = new ClickableSpan() {
                                    @Override
                                    public void onClick(View textView) {
                                        NavigationFooter tosNavigation = null;
                                        List<NavigationFooter> navigationFooter = appCMSPresenter.getNavigation().getNavigationFooter();
                                        for(NavigationFooter navigationFooter1 : navigationFooter){
                                            if(navigationFooter1.getTitle().equalsIgnoreCase("Privacy Policy")){
                                                tosNavigation = navigationFooter1;
                                                break;
                                            }
                                        }
                                        if(null != tosNavigation){
                                            appCMSPresenter.navigateToTVPage(
                                                    tosNavigation.getPageId(),
                                                    tosNavigation.getTitle(),
                                                    tosNavigation.getUrl(),
                                                    false,
                                                    Uri.EMPTY,
                                                    false,
                                                    true,
                                                    false
                                            );
                                        }
                                    }
                                };
                                spannableString.setSpan(clickableSpan1, ppStartIndex, ppEndIndex,
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                spannableString.setSpan(
                                        new ForegroundColorSpan(Color.parseColor(appCMSPresenter
                                                .getAppCMSMain().getBrand().getGeneral()
                                                .getBlockTitleColor())),
                                        ppStartIndex,
                                        ppEndIndex,
                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            }

                            TextView textView = (TextView) componentViewResult.componentView;
                            textView.setText(spannableString);
                            textView.setMovementMethod(LinkMovementMethod.getInstance());
                            break;
                        default:
                            if (!TextUtils.isEmpty(component.getText())) {
                                ((TextView) componentViewResult.componentView).setText(component.getText());
                            }
                    }
                } else {
                    ((TextView) componentViewResult.componentView).setSingleLine(true);
                    ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                }

                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.setBackgroundColor(Color.parseColor(getColor(context, component.getBackgroundColor())));
                }
                if (!TextUtils.isEmpty(component.getFontFamily())
                        && componentKey != PAGE_API_DESCRIPTION) {
                    setTypeFace(context,
                            jsonValueKeyMap,
                            component,
                            (TextView) componentViewResult.componentView);
                }
                break;

            case PAGE_IMAGE_KEY:
                if (componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY) {
                    componentViewResult.componentView = new FrameLayout(context);
                } else {
                    componentViewResult.componentView = new ImageView(context);
                }
                switch (componentKey) {
                    case PAGE_VIDEO_IMAGE_KEY:
                        ImageView imageView = new ImageView(componentViewResult.componentView.getContext());

                        int padding = Integer.valueOf(component.getLayout().getTv().getPadding());
                        imageView.setPadding(padding + 1, padding, padding + 1, padding);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);
                        imageView.setLayoutParams(params);


                        ((FrameLayout) componentViewResult.componentView).addView(imageView);
                        componentViewResult.componentView.setFocusable(false);
                        imageView.setFocusable(true);

                        String borderColor = Utils.getFocusColor(context, appCMSPresenter);
                        imageView.setBackground(Utils.getTrayBorder(context, borderColor, component));
                        int viewWidth = (int) Utils.getViewWidth(context,
                                component.getLayout(),
                                ViewGroup.LayoutParams.WRAP_CONTENT);

                        int viewHeight = (int) Utils.getViewHeight(context,
                                component.getLayout(),
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                        if (viewHeight > 0 && viewWidth > 0 && viewHeight > viewWidth) {
                            Glide.with(context)
                                    .load(moduleAPI.getContentData().get(0).getGist().getPosterImageUrl()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .error(ContextCompat.getDrawable(context, R.drawable.poster_image_placeholder))
                                    .placeholder(ContextCompat.getDrawable(context, R.drawable.poster_image_placeholder))
                                    .into(imageView);
                        } else if (viewWidth > 0) {
                            Glide.with(context)
                                    .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .error(ContextCompat.getDrawable(context, R.drawable.video_image_placeholder))
                                    .placeholder(ContextCompat.getDrawable(context, R.drawable.video_image_placeholder))
                                    .into(imageView);
                        } else {
                            Glide.with(context)
                                    .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .error(ContextCompat.getDrawable(context, R.drawable.video_image_placeholder))
                                    .placeholder(ContextCompat.getDrawable(context, R.drawable.video_image_placeholder))
                                    .into(imageView);
                        }
                        componentViewResult.componentView.setTag(context.getString(R.string.video_image_key));
                        imageView.setOnClickListener(view -> {
                            appCMSPresenter.showLoadingDialog(true);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
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
                                            String[] extraData = new String[4];
                                            extraData[0] = moduleAPI.getContentData().get(0).getGist().getPermalink();
                                            extraData[1] = videoUrl;
                                            extraData[2] = moduleAPI.getContentData().get(0).getGist().getId();
                                            if (moduleAPI.getContentData().get(0).getContentDetails() != null &&
                                                    moduleAPI.getContentData().get(0).getContentDetails().getClosedCaptions() != null &&
                                                    moduleAPI.getContentData().get(0).getContentDetails().getClosedCaptions().get(0) != null &&
                                                    moduleAPI.getContentData().get(0).getContentDetails().getClosedCaptions().get(0).getUrl() != null) {
                                                extraData[3] = moduleAPI.getContentData().get(0).getContentDetails().getClosedCaptions().get(0).getUrl();
                                            }
                                            if (!appCMSPresenter.launchTVButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                                    component.getAction(),
                                                    moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                    extraData,
                                                    false,
                                                    moduleAPI.getContentData().get(0))) {
                                                appCMSPresenter.showLoadingDialog(false);
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

                            }, 300);
                        });

                        imageView.setOnKeyListener((view, i, keyEvent) -> {
                            switch (keyEvent.getAction()) {
                                case KeyEvent.ACTION_DOWN:
                                    switch (keyEvent.getKeyCode()) {
                                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                                            appCMSPresenter.showLoadingDialog(true);

                                            if (moduleAPI.getContentData() != null &&
                                                    moduleAPI.getContentData().size() > 0 &&
                                                    moduleAPI.getContentData().get(0) != null &&
                                                    moduleAPI.getContentData().get(0).getGist() != null &&
                                                    moduleAPI.getContentData().get(0).getGist().getId() != null &&
                                                    moduleAPI.getContentData().get(0).getGist().getPermalink() != null) {

                                                String filmId = moduleAPI.getContentData().get(0).getGist().getId();
                                                String permaLink = moduleAPI.getContentData().get(0).getGist().getPermalink();
                                                String title = moduleAPI.getContentData().get(0).getGist().getTitle();

                                                if (!appCMSPresenter.launchTVVideoPlayer(filmId, permaLink, title, moduleAPI.getContentData().get(0))) {
                                                    appCMSPresenter.showLoadingDialog(false);
                                                    Log.e(TAG, "Could not launch play action: " +
                                                            " filmId: " +
                                                            filmId +
                                                            " permaLink: " +
                                                            permaLink +
                                                            " title: " +
                                                            title);
                                                }
                                                break;
                                            }
                                    }
                                    break;
                            }
                            return false;
                        });


                        break;

                    case CONTACT_US_PHONE_IMAGE:
                        componentViewResult.componentView.setBackgroundResource(R.drawable.call_icon);
                        break;

                    case CONTACT_US_EMAIL_IMAGE:
                        componentViewResult.componentView.setBackgroundResource(R.drawable.email_icon);
                        break;

                    default:
                        if (!TextUtils.isEmpty(component.getImageName())) {
                            Glide.with(context)
                                    .load(component.getImageName()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    //.error(ContextCompat.getDrawable(context, R.drawable.poster_image_placeholder))
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
                componentViewResult.componentView.setFocusable(false);
                break;

            case PAGE_SEPARATOR_VIEW_KEY:
            case PAGE_SEGMENTED_VIEW_KEY:
                componentViewResult.componentView = new View(context);
                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.
                            setBackgroundColor(Color.parseColor(getColor(context, component.getBackgroundColor())));
                }
                componentViewResult.componentView.setFocusable(false);
                break;

            case PAGE_CASTVIEW_VIEW_KEY:
                if (moduleAPI.getContentData().get(0).getCreditBlocks() == null) {
                    componentViewResult.componentView = null;
                    return;
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
                        directorListSb.toString().toUpperCase(),
                        starringTitle,
                        starringListSb.toString().toUpperCase(),
                        textColor,
                        Utils.getFontSizeKey(context, component.getLayout()),
                        Utils.getFontSizeValue(context, component.getLayout()));
                componentViewResult.componentView.setFocusable(false);

                try {
                    if (TextUtils.isEmpty(directorListSb.toString())) {
                        ((CreditBlocksView) componentViewResult.componentView).getChildAt(0).setVisibility(View.GONE);
                        ((CreditBlocksView) componentViewResult.componentView).getChildAt(1).setVisibility(View.GONE);
                    }


                    if (TextUtils.isEmpty(starringListSb.toString())) {
                        ((CreditBlocksView) componentViewResult.componentView).getChildAt(2).setVisibility(View.GONE);
                        ((CreditBlocksView) componentViewResult.componentView).getChildAt(3).setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case PAGE_TEXTFIELD_KEY:
                componentViewResult.componentView = new LinearLayout(context);
                EditText textInputEditText = new EditText(context);
                textInputEditText.setBackground(Utils.getTrayBorder(context, Utils.getFocusColor(context, appCMSPresenter), component));
                switch (componentKey) {
                    case PAGE_EMAILTEXTFIELD_KEY:
                    case PAGE_EMAILTEXTFIELD2_KEY:
                        textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        textInputEditText.setId(R.id.email_edit_box);
                        //textInputEditText.setNextFocusRightId(R.id.password_edit_box);
                        break;
                    case PAGE_PASSWORDTEXTFIELD_KEY:
                    case PAGE_PASSWORDTEXTFIELD2_KEY:
                        textInputEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        textInputEditText.setId(R.id.password_edit_box);
                        //textInputEditText.setNextFocusLeftId(R.id.email_edit_box);
                        // ((TextInputLayout) componentViewResult.componentView).setPasswordVisibilityToggleEnabled(true);
                        break;
                    case PAGE_MOBILETEXTFIELD_KEY:
                        textInputEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    default:
                }
                if (!TextUtils.isEmpty(component.getText())) {
                    textInputEditText.setHint(component.getText());
                }
                /*if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    textInputEditText.setBackgroundColor(Color.parseColor(getColor(context, component.getBackgroundColor())));
                }*/
                if (!TextUtils.isEmpty(component.getTextColor())) {
                    textInputEditText.setTextColor(Color.parseColor(getColor(context, component.getTextColor())));
                    textInputEditText.setHintTextColor(Utils.getButtonTextColorDrawable(
                            component.getHintColor(),
                            component.getHintColor()
                    ));
                }
                setTypeFace(context, jsonValueKeyMap, component, textInputEditText);
                int loginInputHorizontalMargin = context.getResources().getInteger(R.integer.app_cms_tv_login_input_horizontal_margin);
                textInputEditText.setPadding(loginInputHorizontalMargin,
                        0,
                        loginInputHorizontalMargin,
                        0);
                textInputEditText.setTextSize(context.getResources().getInteger(R.integer.app_cms_login_input_textsize));
                LinearLayout.LayoutParams textInputEditTextLayoutParams =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
                textInputEditText.setLayoutParams(textInputEditTextLayoutParams);
                ((LinearLayout) componentViewResult.componentView).addView(textInputEditText);
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


            case PAGE_HEADER_KEY:
                componentViewResult.componentView = new HeaderView(context
                        , component, jsonValueKeyMap, moduleAPI);
                componentViewResult.componentView.setFocusable(false);
                break;
            case PAGE_SETTING_TOGGLE_SWITCH_TYPE:
                componentViewResult.componentView = new ToggleSwitchView(
                        context,
                        component,
                        jsonValueKeyMap
                );


                /*if (componentKey == AppCMSUIKeyType.PAGE_SETTING_AUTOPLAY_TOGGLE_SWITCH_KEY) {
                    boolean isAutoplayEnabled = appCMSPresenter.getAutoplayEnabledUserPref(context);
                    componentViewResult.componentView.setOnClickListener(v -> {
                        if (isAutoplayEnabled){
                            ((ToggleSwitchView) componentViewResult.componentView)
                                    .getImageView().setImageResource(R.drawable.focused_off);
                        } else {
                            ((ToggleSwitchView) componentViewResult.componentView)
                                    .getImageView().setImageResource(R.drawable.focused_on);
                        }
                    });
                } else if (componentKey == AppCMSUIKeyType.PAGE_SETTING_CLOSED_CAPTION_TOGGLE_SWITCH_KEY) {
                    boolean isClosedCaptionEnabled =
                            appCMSPresenter.getClosedCaptionPreference(context);
                    componentViewResult.componentView.setOnClickListener(v -> {
                        if (isClosedCaptionEnabled){
                            ((ToggleSwitchView) componentViewResult.componentView)
                                    .getImageView().setImageResource(R.drawable.focused_off);
                        } else {
                            ((ToggleSwitchView) componentViewResult.componentView)
                                    .getImageView().setImageResource(R.drawable.focused_on);
                        }
                    });
                }*/


                break;
            default:
        }
    }

    @NonNull
    private ClearDialogFragment getClearDialogFragment(Context context,
                                                       AppCMSPresenter appCMSPresenter,
                                                       int dialogWidth,
                                                       int dialogHeight,
                                                       String dialogTitle,
                                                       String dialogMessage,
                                                       String positiveButtonText,
                                                       String negativeButtonText,
                                                       float messageSize) {
        Bundle bundle = new Bundle();
        bundle.putInt(ClearDialogFragment.DIALOG_WIDTH_KEY, dialogWidth);
        bundle.putInt(ClearDialogFragment.DIALOG_HEIGHT_KEY,dialogHeight);
        bundle.putFloat(ClearDialogFragment.DIALOG_MESSAGE__SIZE_KEY, messageSize);
        bundle.putString(ClearDialogFragment.DIALOG_MESSAGE_TEXT_COLOR_KEY,
                Utils.getTextColor(context, appCMSPresenter));
        bundle.putString(ClearDialogFragment.DIALOG_TITLE_KEY, dialogTitle);
        bundle.putString(ClearDialogFragment.DIALOG_MESSAGE_KEY, dialogMessage);
        bundle.putString(ClearDialogFragment.DIALOG_POSITIVE_BUTTON_TEXT_KEY,
                positiveButtonText);
        bundle.putString(ClearDialogFragment.DIALOG_NEGATIVE_BUTTON_TEXT_KEY,
                negativeButtonText);
        Intent args = new Intent(AppCMSPresenter.PRESENTER_DIALOG_ACTION);
        args.putExtra(context.getString(R.string.dialog_item_key), bundle);
        android.app.FragmentTransaction ft = appCMSPresenter
                .getCurrentActivity().getFragmentManager()
                .beginTransaction();
        ClearDialogFragment newFragment =
                ClearDialogFragment.newInstance(bundle);
        newFragment.show(ft, DIALOG_FRAGMENT_TAG);
        return newFragment;
    }

    public static void setViewWithSubtitle(Context context, ContentDatum data, View view) {
        long runtime = (data.getGist().getRuntime() / 60);
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

    private Module matchModuleAPIToModuleUI(ModuleList module, AppCMSPageAPI appCMSPageAPI,
                                            Map<String, AppCMSUIKeyType> jsonValueKeyMap) {
        if (appCMSPageAPI != null && appCMSPageAPI.getModules() != null) {
            if (AppCMSUIKeyType.PAGE_HISTORY_MODULE_KEY == jsonValueKeyMap.get(module.getView())) {
                if (appCMSPageAPI.getModules() != null && appCMSPageAPI.getModules().size() > 0) {
                    return appCMSPageAPI.getModules().get(0);
                }
            }

            if (AppCMSUIKeyType.PAGE_WATCHLIST_MODULE_KEY == jsonValueKeyMap.get(module.getView())) {
                if (appCMSPageAPI.getModules() != null && appCMSPageAPI.getModules().size() > 0) {
                    return appCMSPageAPI.getModules().get(0);
                }
            }

            if (AppCMSUIKeyType.PAGE_RESET_PASSWORD_MODULE_KEY == jsonValueKeyMap.get(module.getView())
                    || AppCMSUIKeyType.PAGE_CONTACT_US_MODULE_KEY == jsonValueKeyMap.get(module.getView())) {
                return new Module();
            }

            for (Module moduleAPI : appCMSPageAPI.getModules()) {
                if (module.getId().equals(moduleAPI.getId())) {
                    return moduleAPI;
                }
            }
        }
        return null;
    }


    private void applyBorderToComponent(Context context, View view, Component component) {
        if (component.getBorderColor() != null) {
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

    private void setTypeFace(Context context,
                             Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                             Component component,
                             TextView textView) {
        if (jsonValueKeyMap.get(component.getFontFamily()) == AppCMSUIKeyType.PAGE_TEXT_OPENSANS_FONTFAMILY_KEY) {
            AppCMSUIKeyType fontWeight = jsonValueKeyMap.get(component.getFontWeight());
            if (fontWeight == null) {
                fontWeight = AppCMSUIKeyType.PAGE_EMPTY_KEY;
            }
            Typeface face = null;
            switch (fontWeight) {
                case PAGE_TEXT_BOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_bold_ttf));
                    Log.d(TAG, "setTypeFace===Opensans_Bold" + " text = " + component.getKey().toString());
                    break;
                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_semibold_ttf));
                    Log.d(TAG, "setTypeFace===Opensans_SemiBold" + " text = " + component.getKey().toString());
                    break;
                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_extrabold_ttf));
                    Log.d(TAG, "setTypeFace===Opensans_ExtraBold" + " text = " + component.getKey().toString());
                    break;
                default:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_regular_ttf));
                    Log.d(TAG, "setTypeFace===Opensans_RegularBold" + " text = " + component.getKey().toString());
            }
            textView.setTypeface(face);
        }
    }

    public void makeTextViewResizable(final TextView tv, boolean hasFocus) {
        Spannable wordToSpan = new SpannableString(tv.getText().toString());
        int length = wordToSpan.length();
        if (hasFocus) {
            wordToSpan.setSpan(new BackgroundColorSpan(ContextCompat.getColor(tv.getContext(), android.R.color.holo_red_dark)), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(new StyleSpan(Typeface.BOLD), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(new ForegroundColorSpan(Color.BLACK), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            wordToSpan.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(new StyleSpan(Typeface.BOLD), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(tv.getContext(), android.R.color.transparent)), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        wordToSpan.setSpan(new AbsoluteSizeSpan(18), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setText(wordToSpan);
    }

    private String getColor(Context context, String color) {
        if (color.indexOf(context.getString(R.string.color_hash_prefix)) != 0) {
            return context.getString(R.string.color_hash_prefix) + color;
        }
        return color;
    }


}
