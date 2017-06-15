package com.viewlift.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    protected Component component;
    protected AppCMSPresenter appCMSPresenter;
    protected Settings settings;
    protected ViewCreator viewCreator;
    protected Module moduleAPI;
    protected Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    protected List<ContentDatum> adapterData;
    protected CollectionGridItemView.OnClickHandler onClickHandler;
    protected int defaultWidth;
    protected int defaultHeight;
    protected boolean useMarginsAsPercentages;
    protected String defaultAction;

    public AppCMSViewAdapter(ViewCreator viewCreator,
                             AppCMSPresenter appCMSPresenter,
                             Settings settings,
                             Component component,
                             Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                             Module moduleAPI,
                             int defaultWidth,
                             int defaultHeight) {
        this.viewCreator = viewCreator;
        this.appCMSPresenter = appCMSPresenter;
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
        this.useMarginsAsPercentages = true;
        this.defaultAction = getDefaultAction();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CollectionGridItemView view = viewCreator.createCollectionGridItemView(parent.getContext(),
                component,
                appCMSPresenter,
                moduleAPI,
                settings,
                jsonValueKeyMap,
                defaultWidth,
                defaultHeight,
                useMarginsAsPercentages,
                true);
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

    protected void bindView(CollectionGridItemView itemView,
                          final ContentDatum data) throws IllegalArgumentException {
        if (onClickHandler == null) {
            onClickHandler = new CollectionGridItemView.OnClickHandler() {
                @Override
                public void click(Component childComponent, ContentDatum data) {
                    Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());
                    String permalink = data.getGist().getPermalink();
                    String action = childComponent.getAction();
                    String title = data.getGist().getTitle();
                    String hlsUrl = getHlsUrl(data);
                    Log.d(TAG, "Launching " + permalink + ":" + action);
                    if (!appCMSPresenter.launchButtonSelectedAction(permalink, action, title, hlsUrl)) {
                        Log.e(TAG, "Could not launch action: " +
                                " permalink: " +
                                permalink +
                                " action: " +
                                action +
                                " hlsUrl: " +
                                hlsUrl);
                    }
                }
            };
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String permalink = data.getGist().getPermalink();
                String title = data.getGist().getTitle();
                Log.d(TAG, "Launching " + permalink + ":" + defaultAction);
                if (!appCMSPresenter.launchButtonSelectedAction(permalink, defaultAction, title, null)) {
                    Log.e(TAG, "Could not launch action: " +
                            " permalink: " +
                            permalink +
                            " action: " +
                            defaultAction);
                }
            }
        });

        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(itemView.getContext(),
                    itemView.getChild(i),
                    data,
                    jsonValueKeyMap,
                    onClickHandler);
        }
    }

    private String getDefaultAction() {
        for (String jsonKey : jsonValueKeyMap.keySet()) {
            if (jsonValueKeyMap.get(jsonKey) == AppCMSUIKeyType.PAGE_PLAY_KEY) {
                return jsonKey;
            }
        }
        return null;
    }

    private String getHlsUrl(ContentDatum data) {
        if (data.getStreamingInfo() != null &&
                data.getStreamingInfo().getVideoAssets() != null &&
                data.getStreamingInfo().getVideoAssets().getHls() != null) {
            return data.getStreamingInfo().getVideoAssets().getHls();
        }
        return null;
    }
}
