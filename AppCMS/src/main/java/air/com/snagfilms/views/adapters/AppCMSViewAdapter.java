package air.com.snagfilms.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;

import air.com.snagfilms.models.data.appcms.AppCMSKeyType;
import air.com.snagfilms.models.data.appcms.page.Component;
import air.com.snagfilms.views.components.AppCMSViewComponent;
import air.com.snagfilms.views.components.DaggerAppCMSViewComponent;
import air.com.snagfilms.views.customviews.CollectionGridView;
import air.com.snagfilms.views.customviews.ViewCreator;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSViewAdapter extends RecyclerView.Adapter<AppCMSViewAdapter.ViewHolder> {
    private AppCMSViewComponent appCMSViewComponent;
    private List<JsonElement> adapterData;
    private Context context;
    private Component component;
    private ViewCreator viewCreator;
    private Map<AppCMSKeyType, String> jsonValueKeyMap;

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

    public void updateAdapterData(List<JsonElement> adapterData) {
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
