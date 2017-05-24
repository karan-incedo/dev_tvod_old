package com.viewlift.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.viewlift.models.data.appcms.api.AppCMSPageAPI;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.views.components.AppCMSViewComponent;
import com.viewlift.views.customviews.CollectionGridView;
import com.viewlift.views.customviews.ViewCreator;

import java.util.List;
import java.util.Map;

import com.viewlift.views.components.DaggerAppCMSViewComponent;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSViewAdapter extends RecyclerView.Adapter<AppCMSViewAdapter.ViewHolder> {
    private AppCMSViewComponent appCMSViewComponent;
    private List<AppCMSPageAPI> adapterData;
    private Context context;
    private Component component;
    private ViewCreator viewCreator;
    private Map<AppCMSUIKeyType, String> jsonValueKeyMap;

    public AppCMSViewAdapter(Context context, Component component) {
        this.appCMSViewComponent = DaggerAppCMSViewComponent
                .builder()
                .build();
        this.context = context;
        this.component = component;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CollectionGridView view = appCMSViewComponent
                .viewCreator()
                .createCollectionGridView(context,
                        component,
                        ViewCreator.NOOP_ON_COMPONENT_LOADED,
                        jsonValueKeyMap);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.componentView.bindView(adapterData.get(position));
    }

    @Override
    public int getItemCount() {
        return (adapterData == null ? 0 : adapterData.size());
    }

    public void updateAdapterData(List<AppCMSPageAPI> adapterData) {
        this.adapterData = adapterData;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridView) itemView;
        }
    }
}
