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

import java.util.ArrayList;
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
    protected List<ContentDatum> adapterData;
    protected CollectionGridItemView.OnClickHandler onClickHandler;
    protected int defaultWidth;
    protected int defaultHeight;

    public AppCMSViewAdapter(Context context,
                             ViewCreator viewCreator,
                             AppCMSPresenter appCMSPresenter,
                             Settings settings,
                             Component component,
                             Map<AppCMSUIKeyType, String> jsonValueKeyMap,
                             Module moduleAPI,
                             int defaultWidth,
                             int defaultHeight) {
        this.viewCreator = viewCreator;
        this.appCMSPresenter = appCMSPresenter;
        this.context = context;
        this.component = component;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.moduleAPI = moduleAPI;
        if (moduleAPI != null && moduleAPI.getContentData() != null) {
            this.adapterData = moduleAPI.getContentData();
        } else {
            this.adapterData = new ArrayList<>();
        }
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CollectionGridItemView view = viewCreator.createCollectionGridItemView(context,
                        component,
                        appCMSPresenter,
                        moduleAPI,
                        settings,
                        ViewCreator.NOOP_ON_COMPONENT_LOADED,
                        jsonValueKeyMap,
                        defaultWidth,
                        defaultHeight);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (0 <= position && position < adapterData.size()) {
            bindView(holder.componentView, adapterData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return adapterData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }
    }

    private void bindView(CollectionGridItemView itemView,
                          final ContentDatum data) throws IllegalArgumentException {
        if (onClickHandler == null) {
            onClickHandler = new CollectionGridItemView.OnClickHandler() {
                @Override
                public void click(Component childComponent, ContentDatum data) {
                    String permalink = data.getGist().getPermalink();
                    String action = childComponent.getKey();
                    String title = data.getGist().getTitle();
                    Log.d(TAG, "Launching " + permalink + ":" + action);
                    if (!appCMSPresenter.launchButtonSelectedAction(permalink, action, title)) {
                        Log.e(TAG, "Could not launch action: " +
                                " permalink: " +
                                permalink +
                                " action: " +
                                action);
                    }
                }
            };
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String permalink = data.getGist().getPermalink();
                String action = jsonValueKeyMap.get(AppCMSUIKeyType.PAGE_PLAY_KEY);
                String title = data.getGist().getTitle();
                Log.d(TAG, "Launching " + permalink + ":" + action);
                if (!appCMSPresenter.launchButtonSelectedAction(permalink, action, title)) {
                    Log.e(TAG, "Could not launch action: " +
                            " permalink: " +
                            permalink +
                            " action: " +
                            action);
                }
            }
        });

        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(context,
                    itemView.getChild(i),
                    data,
                    jsonValueKeyMap,
                    onClickHandler);
        }
    }
}
