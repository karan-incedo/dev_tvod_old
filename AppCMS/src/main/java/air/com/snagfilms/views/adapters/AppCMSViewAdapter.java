package air.com.snagfilms.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonElement;

import java.util.List;

import air.com.snagfilms.models.data.appcms.page.Component;
import air.com.snagfilms.views.components.AppCMSViewComponent;
import air.com.snagfilms.views.components.DaggerAppCMSViewComponent;
import air.com.snagfilms.views.customviews.ComponentView;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSViewAdapter extends RecyclerView.Adapter<AppCMSViewAdapter.ViewHolder> {
    private AppCMSViewComponent appCMSViewComponent;
    private List<JsonElement> adapterData;
    private Context context;
    private Component component;

    public AppCMSViewAdapter(Context context, Component component, List<JsonElement> adapterData) {
        this.appCMSViewComponent = DaggerAppCMSViewComponent
                .builder()
                .build();
        this.adapterData = adapterData;
        this.context = context;
        this.component = component;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = appCMSViewComponent.viewCreator().generateComponent(context, component);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ComponentView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (ComponentView) itemView;
        }
    }
}
