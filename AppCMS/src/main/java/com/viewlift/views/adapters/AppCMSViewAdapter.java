package com.viewlift.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import snagfilms.com.air.appcms.R;

/**
 * Created by viewlift on 5/5/17.
 */

public class AppCMSViewAdapter extends RecyclerView.Adapter<AppCMSViewAdapter.ViewHolder>
        implements AppCMSBaseAdapter {
    private static final String TAG = "AppCMSViewAdapter";
    protected Layout parentLayout;
    protected boolean useParentSize;
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
    protected String viewType;

    public AppCMSViewAdapter(Context context,
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
                             String viewType) {
        this.viewCreator = viewCreator;
        this.appCMSPresenter = appCMSPresenter;
        this.parentLayout = parentLayout;
        this.useParentSize = useParentSize;
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
        this.defaultAction = getDefaultAction(context);

        this.viewType = viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CollectionGridItemView view = viewCreator.createCollectionGridItemView(parent.getContext(),
                parentLayout,
                useParentSize,
                component,
                appCMSPresenter,
                moduleAPI,
                settings,
                jsonValueKeyMap,
                defaultWidth,
                defaultHeight,
                useMarginsAsPercentages,
                true,
                this.viewType);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (0 <= position && position < adapterData.size()) {
            for (int i = 0; i < holder.componentView.getNumberOfChildren(); i++) {
                if (holder.componentView.getChild(i) instanceof TextView) {
                    ((TextView) holder.componentView.getChild(i)).setText("");
                }
            }
            bindView(holder.componentView, adapterData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return adapterData.size();
    }

    @Override
    public void resetData(RecyclerView listView) {

    }

    @Override
    public void updateData(RecyclerView listView, List<ContentDatum> contentData) {
        listView.setAdapter(null);
        adapterData = null;
        notifyDataSetChanged();
        adapterData = contentData;
        notifyDataSetChanged();
        listView.setAdapter(this);
        listView.invalidate();
    }

    protected void bindView(CollectionGridItemView itemView,
                            final ContentDatum data) throws IllegalArgumentException {
        if (onClickHandler == null) {
            onClickHandler = new CollectionGridItemView.OnClickHandler() {
                @Override
                public void click(Component childComponent, ContentDatum data) {
                    Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());
                    String permalink = data.getGist().getPermalink();
                    String action = defaultAction;
                    String title = data.getGist().getTitle();
                    String hlsUrl = getHlsUrl(data);
                    String[] extraData = new String[3];
                    extraData[0] = permalink;
                    extraData[1] = hlsUrl;
                    extraData[2] = data.getGist().getId();
                    Log.d(TAG, "Launching " + permalink + ": " + action);

                    if (!appCMSPresenter.launchButtonSelectedAction(permalink,
                            action,
                            title,
                            extraData,
                            data,
                            false)) {
                        Log.e(TAG, "Could not launch action: " + " permalink: " + permalink
                                + " action: " + action + " hlsUrl: " + hlsUrl);
                    }
                }

                @Override
                public void play(Component childComponent, ContentDatum data) {
                    Log.d(TAG, "Playing item: " + data.getGist().getTitle());
                    String filmId = data.getGist().getId();
                    String permaLink = data.getGist().getPermalink();
                    String title = data.getGist().getTitle();
                    if (!appCMSPresenter.launchVideoPlayer(filmId, permaLink, title, data)) {
                        Log.e(TAG, "Could not launch play action: " +
                                " filmId: " +
                                filmId +
                                " permaLink: " +
                                permaLink +
                                " title: " +
                                title);
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
                if (!appCMSPresenter.launchButtonSelectedAction(permalink,
                        defaultAction,
                        title,
                        null,
                        null,
                        false)) {
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

    private String getDefaultAction(Context context) {
        return context.getString(R.string.app_cms_action_videopage_key);
    }

    private String getHlsUrl(ContentDatum data) {
        if (data.getStreamingInfo() != null &&
                data.getStreamingInfo().getVideoAssets() != null &&
                data.getStreamingInfo().getVideoAssets().getHls() != null) {
            return data.getStreamingInfo().getVideoAssets().getHls();
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }
    }
}
