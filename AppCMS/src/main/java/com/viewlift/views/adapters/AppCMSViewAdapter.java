package com.viewlift.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.ViewCreator;

import java.util.List;
import java.util.Map;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSViewAdapter extends RecyclerView.Adapter<AppCMSViewAdapter.ViewHolder> {
    private static final String TAG = "AppCMSViewAdapter";

    private Context context;
    private Component component;
    private AppCMSPresenter appCMSPresenter;
    private Settings settings;
    private ViewCreator viewCreator;
    private Module moduleAPI;
    private Map<AppCMSUIKeyType, String> jsonValueKeyMap;
    private List<ContentDatum> adapterData;

    public AppCMSViewAdapter(Context context,
                             ViewCreator viewCreator,
                             AppCMSPresenter appCMSPresenter,
                             Settings settings,
                             Component component,
                             Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                             Module moduleAPI) {
        this.viewCreator = viewCreator;
        this.appCMSPresenter = appCMSPresenter;
        this.context = context;
        this.component = component;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.moduleAPI = moduleAPI;
        this.adapterData = moduleAPI.getContentData();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CollectionGridItemView view = viewCreator.createCollectionGridItemView(context,
                        component,
                        appCMSPresenter,
                        moduleAPI,
                        settings,
                        ViewCreator.NOOP_ON_COMPONENT_LOADED,
                        jsonValueKeyMap);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        bindView(holder.componentView, adapterData.get(position));
    }

    @Override
    public int getItemCount() {
        return (adapterData == null ? 0 : adapterData.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }
    }

    private void bindView(CollectionGridItemView itemView,
                          ContentDatum data) throws IllegalArgumentException {
        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(context, itemView.getChild(i), data, jsonValueKeyMap);
        }
    }
}
