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
    AdView adView;
    private static String adURL;

    private int ADS_TYPE=-1, FEED_TYPE;

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

        if (adapterData!= null &&
                adapterData.size()>0) {
            for (int i = 0; i < this.adapterData.size(); i++) {
                if (adapterData.get(i)!= null  &&
                        adapterData.get(i).getId() != null &&
                        adapterData.get(i).getId().equalsIgnoreCase("adTag")) {
                    ADS_TYPE = i;
                }
            }
        }

        if (moduleAPI != null &&
                moduleAPI.getMetadataMap() != null &&
                moduleAPI.getMetadataMap() instanceof LinkedTreeMap &&
                this.adView == null &&
                !this.adapterData.get(this.adapterData.size()-2).getId().equalsIgnoreCase("adTag") &&
                ADS_TYPE == -1
                ) {

            adView = new AdView(context);
            ContentDatum data=new ContentDatum();
            data.setId("adTag");
            this.adapterData.add(this.adapterData.size() - 1, data);

            LinkedTreeMap<String, String> admap = (LinkedTreeMap<String, String>) moduleAPI.getMetadataMap();

            adURL = admap.get("adTag");
            //articleFeedModuleAd = new ArticleFeedModule(context, adView);


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

        ArticleFeedModule view = null;
        if (ADS_TYPE != 0 && ADS_TYPE == viewType
                ) {
            if (adView == null){
                adView = new AdView(mContext);
            }
            return new ViewHolder(adView);

        } else {
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
        if (adView != null &&
                position == ADS_TYPE) {
            return ADS_TYPE;
        }
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position != ADS_TYPE) {
            bindView(holder.componentView, adapterData.get(position), position);
        } else if (adView != null) {

            adView.setFocusable(false);
            adView.setEnabled(false);
            adView.setClickable(false);
            MobileAds.initialize(this.mContext, adURL);
            adView.setAdUnitId(adURL);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.loadAd(adRequest);
        }

    }

    @Override
    public int getItemCount() {
        return (adapterData != null ? adapterData.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ArticleFeedModule componentView;
        AdView adsView;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof AdView) {
                this.adsView = (AdView) itemView;
            } else {
                this.componentView = (ArticleFeedModule) itemView;
            }
        }
    }

    void bindView(ArticleFeedModule articleFeedModule, ContentDatum data, int position) {
        articleFeedModule.bindChild(articleFeedModule.getContext(),
                articleFeedModule,
                data,
                jsonValueKeyMap,
                appCMSPresenter,
                position);
    }

}
