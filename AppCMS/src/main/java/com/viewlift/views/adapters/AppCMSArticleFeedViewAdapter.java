package com.viewlift.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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

        this.componentViewType = viewType;
        this.viewTypeKey = jsonValueKeyMap.get(componentViewType);
        if (this.viewTypeKey == null) {
            this.viewTypeKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ArticleFeedModule view = new ArticleFeedModule(parent.getContext(),
                parentLayout,
                component,
                defaultWidth,
                defaultHeight,
                false,
                false,
                viewTypeKey,
                appCMSPresenter,
                jsonValueKeyMap);

        return new ViewHolder(view);
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
        articleFeedModule.bindChild(articleFeedModule.getContext(),
                articleFeedModule,
                data,
                jsonValueKeyMap,
                appCMSPresenter,
                position);
    }

}
