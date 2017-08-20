package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.GsonBuilder;
import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.CreditBlock;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.VideoAssets;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.main.AppCMSMain;
import com.viewlift.models.data.appcms.ui.page.AppCMSPageUI;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.ModuleList;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
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

import com.viewlift.R;

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
                                   List<String> modulesToIgnore) {
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
                    modulesToIgnore);
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
                                  List<String> modulesToIgnore) {
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
                        appCMSPresenter);
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
    }

    int trayIndex = -1;

    public View createModuleView(final Context context,
                                 ModuleList module,
                                 final Module moduleAPI,
                                 TVPageView pageView,
                                 Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                 AppCMSPresenter appCMSPresenter) {
        TVModuleView moduleView = null;

        if (Arrays.asList(context.getResources().getStringArray(R.array.app_cms_tray_modules)).contains(module.getView())) {
            if (module.getView().equalsIgnoreCase(context.getResources().getString(R.string.carousel_nodule))) {
                if (null == mRowsAdapter) {
                    AppCmsListRowPresenter appCmsListRowPresenter = new AppCmsListRowPresenter(context, appCMSPresenter);
                    mRowsAdapter = new ArrayObjectAdapter(appCmsListRowPresenter);
                }

                /*module = new GsonBuilder().create().
                        fromJson(Utils.loadJsonFromAssets(context, "carousel_ftv_component.json"), ModuleList.class);*/

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
        } else if (context.getResources().getString(R.string.appcms_detail_module).equalsIgnoreCase(module.getView())) {

            /*module = new GsonBuilder().create().
                    fromJson(Utils.loadJsonFromAssets(context, "videodetail.json"), ModuleList.class);*/

            moduleView = new TVModuleView<>(context, module);
            ViewGroup childrenContainer = moduleView.getChildrenContainer();

            final TVPageView finalPageView = pageView;
            Glide.with(context).load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                    .asBitmap().into(new SimpleTarget<Bitmap>(TVBaseView.DEVICE_WIDTH,
                    TVBaseView.DEVICE_HEIGHT ) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable drawable = new BitmapDrawable(context.getResources(), resource);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finalPageView.setBackground(drawable);
                        finalPageView.getChildrenContainer().setBackgroundColor(ContextCompat.getColor(context,R.color.appcms_detail_screen_shadow_color));

                    }
                }
            });

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
                            false);
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
                                componentViewResult.useWidthOfScreen);

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
                                      boolean isCarousel){
        AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());
        if (componentType == null) {
            componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }
        AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());
        if (componentKey == null) {
            componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }
        switch (componentType){
            case PAGE_LABEL_KEY:
                switch (componentKey) {
                    case PAGE_TRAY_TITLE_KEY:
                        if(moduleData != null) {
                            customHeaderItem = null;
                            customHeaderItem = new CustomHeaderItem(context, trayIndex++,
                                    ( moduleData != null && moduleData.getTitle() != null) ? moduleData.getTitle().toUpperCase() : "");
                            customHeaderItem.setmIsCarousal(isCarousel);
                            customHeaderItem.setmListRowLeftMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                            customHeaderItem.setmListRowRightMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                            customHeaderItem.setmBackGroundColor(moduleUI.getLayout().getTv().getBackgroundColor());
                            customHeaderItem.setmListRowHeight(Integer.valueOf(moduleUI.getLayout().getTv().getHeight()));
                            customHeaderItem.setFontFamily(component.getFontFamily());
                            customHeaderItem.setFontWeight(component.getFontWeight());
                            customHeaderItem.setFontSize(component.getLayout().getTv().getFontSize());
                        }
                        break;
                }
                break;
            case PAGE_CAROUSEL_VIEW_KEY:
                        {
                        customHeaderItem = new CustomHeaderItem(context, trayIndex++, "");
                        customHeaderItem.setmIsCarousal(true);
                        customHeaderItem.setmListRowLeftMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                        customHeaderItem.setmListRowRightMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                        customHeaderItem.setmBackGroundColor(moduleUI.getLayout().getTv().getBackgroundColor());
                        customHeaderItem.setmListRowHeight(Integer.valueOf(moduleUI.getLayout().getTv().getHeight()));
                        }

                        if(moduleData != null) {
                            CardPresenter cardPresenter = new JumbotronPresenter(context, appCMSPresenter);
                            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                            if (moduleData.getContentData() != null && moduleData.getContentData().size() > 0) {
                                List<ContentDatum> contentData1 = moduleData.getContentData();
                                List<Component> components = component.getComponents();
                                for (ContentDatum contentData : contentData1) {
                                    BrowseFragmentRowData rowData = new BrowseFragmentRowData();
                                    rowData.contentData = contentData;
                                    rowData.uiComponentList = components;
                                    listRowAdapter.add(rowData);
                                    Log.d(TAG, "NITS header Items ===== " + rowData.contentData.getGist().getTitle());
                                }
                                mRowsAdapter.add(new ListRow(customHeaderItem, listRowAdapter));
                            }
                        }
                        break;

                    case PAGE_COLLECTIONGRID_KEY:
                        /*for(Component component1 : component.getComponents()){*/
                            if  (customHeaderItem == null ) {
                                customHeaderItem = new CustomHeaderItem(context, trayIndex++, moduleData != null ? moduleData.getTitle() : "");
                                customHeaderItem.setmIsCarousal(false);
                                customHeaderItem.setmListRowLeftMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                                customHeaderItem.setmListRowRightMargin(Integer.valueOf(moduleUI.getLayout().getTv().getPadding()));
                                customHeaderItem.setmBackGroundColor(moduleUI.getLayout().getTv().getBackgroundColor());
                                customHeaderItem.setmListRowHeight(Integer.valueOf(moduleUI.getLayout().getTv().getHeight()));
                                customHeaderItem.setFontFamily(component.getFontFamily());
                                customHeaderItem.setFontWeight(component.getFontWeight());
                                customHeaderItem.setFontSize(component.getLayout().getTv().getFontSize());
                            }
                            if(null != moduleData) {
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
                                    boolean gridElement) {
        componentViewResult.componentView = null;
        componentViewResult.useMarginsAsPercentagesOverride = true;
        componentViewResult.useWidthOfScreen = false;
        if (moduleAPI == null) {
            return;
        }
        AppCMSUIKeyType componentType = jsonValueKeyMap.get(component.getType());
        if (moduleAPI == null) {
            return;
        }
        if (componentType == null) {
            componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        AppCMSUIKeyType componentKey = jsonValueKeyMap.get(component.getKey());
        if (componentKey == null) {
            componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        switch (componentType) {
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
                    if(!TextUtils.isEmpty(component.getBorderColor())){
                        ((TextView)componentViewResult.componentView).setTextColor(Utils.getButtonTextColorDrawable(
                                Utils.getColor(context,component.getBorderColor()),
                                Utils.getColor(context,component.getTextColor())));
                    }
                }
                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    componentViewResult.componentView.setBackgroundColor(Color.parseColor(getColor(context, component.getBackgroundColor())));
                } else {
                    componentViewResult.componentView.setBackground(
                            Utils.setButtonBackgroundSelector(context ,
                                                        Color.parseColor(Utils.getTitleColor(context,appCMSPresenter)),
                                                        component ));
                }

                if(component.getLetetrSpacing() != 0){
                    ((TextView)componentViewResult.componentView).
                            setLetterSpacing(component.getLetetrSpacing());
                }

                int tintColor = Color.parseColor(getColor(context,
                        Utils.getFocusColor(context,appCMSPresenter)));

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
                            componentViewResult.componentView.setFocusable(true);
                            componentViewResult.componentView.setTag("WATCH_TRAILER");
                            componentViewResult.componentView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    appCMSPresenter.showLoadingDialog(true);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            String[] extraData = new String[3];
                                            extraData[0] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getPermalink();
                                            extraData[1] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getVideoAssets().getHls();
                                            extraData[2] = moduleAPI.getContentData().get(0).getContentDetails().getTrailers().get(0).getId();
                                            if (!appCMSPresenter.launchTVButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                                    component.getAction(),
                                                    moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                    extraData,
                                                    false)) {
                                                appCMSPresenter.showLoadingDialog(false);
                                                Log.e(TAG, "Could not launch action: " +
                                                        " permalink: " +
                                                        moduleAPI.getContentData().get(0).getGist().getPermalink() +
                                                        " action: " +
                                                        component.getAction() +
                                                        " hls URL: " +
                                                        moduleAPI.getContentData().get(0).getStreamingInfo().getVideoAssets().getHls());
                                            }
                                        }
                                    },300);
                                }
                            });
                        } else {
                            componentViewResult.componentView = null;
                        }
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
                                        String[] extraData = new String[3];
                                        extraData[0] = moduleAPI.getContentData().get(0).getGist().getPermalink();
                                        extraData[1] = videoUrl;
                                        extraData[2] = moduleAPI.getContentData().get(0).getGist().getId();
                                        if (!appCMSPresenter.launchTVButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
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
                        componentViewResult.componentView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
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
                                if (component.getNumberOfLines() != 0) {
                                    ((TextView) componentViewResult.componentView).setMaxLines(component.getNumberOfLines());
                                }
                                ((TextView) componentViewResult.componentView).setEllipsize(TextUtils.TruncateAt.END);
                            }
                            componentViewResult.componentView.setFocusable(false);
                            componentViewResult.componentView.setTag("TITLE");
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
                            componentViewResult.componentView.setFocusable(false);
                            componentViewResult.componentView.setTag("API_DSECRIPTION");
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

                            if(null == videoDescription){
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
                                    appCMSPresenter.showMoreDialog( moduleAPI.getContentData().get(0).getGist().getTitle(),
                                            fullText);
                                }
                            });

                           // componentViewResult.componentView.setFocusable(true);
                            final int _textColor = textColor;
                            componentViewResult.componentView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View view, boolean b) {
                                    viewCreatorLayoutListener.setSpanOnFocus((TextView) view , b , _textColor);
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
                if (!TextUtils.isEmpty(component.getFontFamily())) {
                    setTypeFace(context,
                            jsonValueKeyMap,
                            component,
                            (TextView) componentViewResult.componentView);
                }
                break;

            case PAGE_IMAGE_KEY:
                if(componentKey == AppCMSUIKeyType.PAGE_VIDEO_IMAGE_KEY) {
                    componentViewResult.componentView = new FrameLayout(context);
                }else{
                    componentViewResult.componentView = new ImageView(context);
                }
                switch (componentKey) {
                    case PAGE_VIDEO_IMAGE_KEY:
                        ImageView imageView = new ImageView(componentViewResult.componentView.getContext());

                        int padding = Integer.valueOf(component.getLayout().getTv().getPadding());
                        imageView.setPadding(padding+1,padding,padding+1,padding);

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);
                        imageView.setLayoutParams(params);


                        ((FrameLayout)componentViewResult.componentView).addView(imageView);
                         componentViewResult.componentView.setFocusable(false);
                         imageView.setFocusable(true);

                        String borderColor = Utils.getFocusColor(context,appCMSPresenter);
                        imageView.setBackground(Utils.getTrayBorder(context,borderColor,component));
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
                                    .placeholder(ContextCompat.getDrawable(context , R.drawable.poster_image_placeholder))
                                    .into(imageView);
                        } else if (viewWidth > 0) {
                            Glide.with(context)
                                    .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl())
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .error(ContextCompat.getDrawable(context, R.drawable.video_image_placeholder))
                                    .placeholder(ContextCompat.getDrawable(context , R.drawable.video_image_placeholder))
                                    .into(imageView);
                        } else {
                            Glide.with(context)
                                    .load(moduleAPI.getContentData().get(0).getGist().getVideoImageUrl()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .error(ContextCompat.getDrawable(context, R.drawable.video_image_placeholder))
                                    .placeholder(ContextCompat.getDrawable(context , R.drawable.video_image_placeholder))
                                    .into(imageView);
                        }
                        componentViewResult.componentView.setTag(context.getString(R.string.video_image_key));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
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
                                                String[] extraData = new String[3];
                                                extraData[0] = moduleAPI.getContentData().get(0).getGist().getPermalink();
                                                extraData[1] = videoUrl;
                                                extraData[2] = moduleAPI.getContentData().get(0).getGist().getId();
                                                if (!appCMSPresenter.launchTVButtonSelectedAction(moduleAPI.getContentData().get(0).getGist().getPermalink(),
                                                        component.getAction(),
                                                        moduleAPI.getContentData().get(0).getGist().getTitle(),
                                                        extraData,
                                                        false)) {
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
                            }

                        });

                        imageView.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                                switch(keyEvent.getAction()){
                                    case KeyEvent.ACTION_DOWN:
                                        switch(keyEvent.getKeyCode()) {
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

                                                    /*if (!appCMSPresenter.launchVideoPlayer(moduleAPI.getContentData().get(0) , -1 , null , 0)) {
                                                        appCMSPresenter.showLoadingDialog(false);
                                                        Log.e(TAG, "Could not launch play action: " +
                                                                " filmId: " +
                                                                filmId +
                                                                " permaLink: " +
                                                                permaLink +
                                                                " title: " +
                                                                title);
                                                    }*/


                                                    break;
                                                }
                                        }
                                        break;
                                }
                                return false;
                            }
                        });


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
           }catch (Exception e){
               e.printStackTrace();
           }
           break;

            case PAGE_TEXTFIELD_KEY:
                componentViewResult.componentView = new TextInputLayout(context);
                TextInputEditText textInputEditText = new TextInputEditText(context);
                switch (componentKey) {
                    case PAGE_EMAILTEXTFIELD_KEY:
                    case PAGE_EMAILTEXTFIELD2_KEY:
                        textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case PAGE_PASSWORDTEXTFIELD_KEY:
                    case PAGE_PASSWORDTEXTFIELD2_KEY:
                        textInputEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        ((TextInputLayout) componentViewResult.componentView).setPasswordVisibilityToggleEnabled(true);
                        break;
                    case PAGE_MOBILETEXTFIELD_KEY:
                        textInputEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    default:
                }
                if (!TextUtils.isEmpty(component.getText())) {
                    textInputEditText.setHint(component.getText());
                }
                if (!TextUtils.isEmpty(component.getBackgroundColor())) {
                    textInputEditText.setBackgroundColor(Color.parseColor(getColor(context, component.getBackgroundColor())));
                }
                if (!TextUtils.isEmpty(component.getTextColor())) {
                    textInputEditText.setTextColor(Color.parseColor(getColor(context, component.getTextColor())));
                }
                setTypeFace(context, jsonValueKeyMap, component, textInputEditText);
                int loginInputHorizontalMargin = context.getResources().getInteger(R.integer.app_cms_login_input_horizontal_margin);
                textInputEditText.setPadding(loginInputHorizontalMargin,
                        0,
                        loginInputHorizontalMargin,
                        0);
                textInputEditText.setTextSize(context.getResources().getInteger(R.integer.app_cms_login_input_textsize));
                TextInputLayout.LayoutParams textInputEditTextLayoutParams =
                        new TextInputLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT);
                textInputEditText.setLayoutParams(textInputEditTextLayoutParams);
                ((TextInputLayout) componentViewResult.componentView).addView(textInputEditText);
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
                    ,component , jsonValueKeyMap , moduleAPI);
                componentViewResult.componentView.setFocusable(false);
                break;
            default:
        }
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
                    Log.d(TAG , "setTypeFace===Opensans_Bold" + " text = "+component.getKey().toString());
                    break;
                case PAGE_TEXT_SEMIBOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_semibold_ttf));
                    Log.d(TAG , "setTypeFace===Opensans_SemiBold" + " text = "+component.getKey().toString());
                    break;
                case PAGE_TEXT_EXTRABOLD_KEY:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_extrabold_ttf));
                    Log.d(TAG , "setTypeFace===Opensans_ExtraBold" + " text = "+component.getKey().toString());
                    break;
                default:
                    face = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.opensans_regular_ttf));
                    Log.d(TAG , "setTypeFace===Opensans_RegularBold" + " text = "+component.getKey().toString());
            }
            textView.setTypeface(face);
        }
    }

    public void makeTextViewResizable(final TextView tv, boolean hasFocus) {
        Spannable wordToSpan = new SpannableString(tv.getText().toString());
        int length = wordToSpan.length();
        if (hasFocus) {
            wordToSpan.setSpan(new BackgroundColorSpan(ContextCompat.getColor(tv.getContext(),android.R.color.holo_red_dark)), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(new StyleSpan(Typeface.BOLD), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(new ForegroundColorSpan(Color.BLACK), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            wordToSpan.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(new StyleSpan(Typeface.BOLD), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordToSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(tv.getContext(),android.R.color.transparent)), length - 6, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
