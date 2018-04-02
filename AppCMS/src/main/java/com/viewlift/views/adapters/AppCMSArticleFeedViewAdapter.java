package com.viewlift.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.internal.LinkedTreeMap;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.ArticleFeedModule;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sandeep on 20/02/18.
 */

public class AppCMSArticleFeedViewAdapter extends RecyclerView.Adapter<AppCMSArticleFeedViewAdapter.ViewHolder> {
    protected Context mContext;
    protected Layout parentLayout;
    protected Component component;
    protected AppCMSPresenter appCMSPresenter;
    protected Settings settings;
    protected ViewCreator viewCreator;
    protected Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    Module moduleAPI;
    List<ContentDatum> adapterData;
    int defaultWidth;
    int defaultHeight;
    boolean useMarginsAsPercentages;
    String componentViewType;
    AppCMSAndroidModules appCMSAndroidModules;
    private String defaultAction;
    private AppCMSUIKeyType viewTypeKey;
    private boolean isSelected;
    ArticleFeedModule articleFeedModuleAd;

    private int ADS_TYPE, FEED_TYPE;

    public AppCMSArticleFeedViewAdapter(Context context,
                                        ViewCreator viewCreator,
                                        AppCMSPresenter appCMSPresenter,
                                        Settings settings,
                                        Layout parentLayout,
                                        boolean useParentSize,
                                        Component component,
                                        Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                                        Module moduleAPI,
                                        int defaultWidth,
                                        int defaultHeight,
                                        String viewType,
                                        AppCMSAndroidModules appCMSAndroidModules) {
        this.mContext = context;
        this.viewCreator = viewCreator;
        this.appCMSPresenter = appCMSPresenter;
        this.parentLayout = parentLayout;
        this.component = component;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.moduleAPI = moduleAPI;
        if (moduleAPI != null && moduleAPI.getContentData() != null) {
            this.adapterData = moduleAPI.getContentData();
        } else {
            this.adapterData = new ArrayList<>();
        }

        if (moduleAPI != null &&
                moduleAPI.getMetadataMap() != null &&
                moduleAPI.getMetadataMap() instanceof LinkedTreeMap &&
                this.adapterData.size() > 0) {
            AdView adView = new AdView(context);
            adView.setFocusable(false);
            adView.setEnabled(false);
            adView.setClickable(false);
            LinkedTreeMap<String, String> admap = (LinkedTreeMap<String, String>) moduleAPI.getMetadataMap();
            MobileAds.initialize(context, admap.get("adTag"));
            adView.setAdUnitId(admap.get("adTag"));
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.loadAd(adRequest);
            articleFeedModuleAd = new ArticleFeedModule(context, adView);
            this.adapterData.add(this.adapterData.size() - 2, new ContentDatum());
            ADS_TYPE = this.adapterData.size() - 2;

        }
        this.componentViewType = viewType;
        this.viewTypeKey = jsonValueKeyMap.get(componentViewType);
        if (this.viewTypeKey == null) {
            this.viewTypeKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ArticleFeedModule view= null;
        if (ADS_TYPE !=0 && ADS_TYPE==viewType) {
            view = new ArticleFeedModule(parent.getContext(),articleFeedModuleAd);
            view.setBackgroundColor(appCMSPresenter.getGeneralBackgroundColor());
        }else {
            view = new ArticleFeedModule(parent.getContext(),
                    parentLayout,
                    component,
                    defaultWidth,
                    defaultHeight,
                    false,
                    false,
                    viewTypeKey,
                    appCMSPresenter,
                    jsonValueKeyMap);
        }

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
         super.getItemViewType(position);
         if (articleFeedModuleAd != null &&
                 position== (this.adapterData.size()-2)){
             return ADS_TYPE;
         }
         return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        bindView(holder.componentView, adapterData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return (adapterData != null ? adapterData.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ArticleFeedModule componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (ArticleFeedModule) itemView;
        }
    }

    void bindView(ArticleFeedModule articleFeedModule, ContentDatum data, int position) {
       if (position != ADS_TYPE)
            articleFeedModule.bindChild(articleFeedModule.getContext(),
                    articleFeedModule,
                    data,
                    jsonValueKeyMap,
                    appCMSPresenter,
                    position);
    }

}
