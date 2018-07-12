package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Fights;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * Created by viewlift on 5/5/17.
 */

public class AppCMSRosterAdapter extends RecyclerView.Adapter<AppCMSRosterAdapter.ViewHolder>
        implements AppCMSBaseAdapter, OnInternalEvent {
    private static final String TAG = "AppCMSFightSelectionAdapter";


    protected Context mContext;
    protected Layout parentLayout;
    protected Component component;
    protected AppCMSPresenter appCMSPresenter;
    protected Settings settings;
    protected ViewCreator viewCreator;
    protected Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    Module moduleAPI;
    List<ContentDatum> adapterData;
    CollectionGridItemView.OnClickHandler onClickHandler;
    int defaultWidth;
    int defaultHeight;
    boolean useMarginsAsPercentages;
    String componentViewType;
    AppCMSAndroidModules appCMSAndroidModules;
    private boolean useParentSize;
    private AppCMSUIKeyType viewTypeKey;
    private boolean isClickable;
    boolean emptyList = false;

    private List<OnInternalEvent> receivers;
    CollectionGridItemView view = null;
    private String moduleId;
    RecyclerView mRecyclerView;

    public AppCMSRosterAdapter(Context context,
                               ViewCreator viewCreator,
                               AppCMSPresenter appCMSPresenter,
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
        this.useParentSize = useParentSize;
        this.component = component;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.moduleAPI = moduleAPI;
        this.receivers = new ArrayList<>();
        if (moduleAPI != null && moduleAPI.getContentData() != null) {
            this.adapterData = moduleAPI.getContentData();
        } else {
            this.adapterData = new ArrayList<>();
        }
        if (this.adapterData.size() == 0) {
            emptyList = true;
        }

        this.componentViewType = viewType;
        this.viewTypeKey = jsonValueKeyMap.get(componentViewType);
        if (this.viewTypeKey == null) {
            this.viewTypeKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.useMarginsAsPercentages = true;
        this.isClickable = true;
        this.setHasStableIds(false);
        this.appCMSAndroidModules = appCMSAndroidModules;

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = viewCreator.createCollectionGridItemView(parent.getContext(),
                parentLayout,
                useParentSize,
                component,
                appCMSPresenter,
                moduleAPI,
                appCMSAndroidModules,
                settings,
                jsonValueKeyMap,
                defaultWidth,
                defaultHeight,
                useMarginsAsPercentages,
                true,
                this.componentViewType,
                false,
                false, viewTypeKey);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 5, 5, 5);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        bindView(holder.componentView, adapterData.get(position), position);

        holder.componentView.setBackgroundColor(Color.parseColor("#F1F1F1"));


    }


    @Override
    public int getItemCount() {

        return (adapterData != null ? adapterData.size() : 0);
    }

    @Override
    public void resetData(RecyclerView listView) {

    }

    @Override
    public void updateData(RecyclerView listView, List<ContentDatum> contentData) {
        if (contentData.size() == 0) {
            emptyList = true;
        } else {
            emptyList = false;
        }

        listView.setAdapter(null);
        adapterData = null;
        adapterData = contentData;
        listView.setAdapter(this);
        listView.invalidate();
        notifyDataSetChanged();

    }

    @SuppressLint("ClickableViewAccessibility")
    void bindView(CollectionGridItemView itemView,
                  final ContentDatum data, int position) throws IllegalArgumentException {
        if (onClickHandler == null) {

            onClickHandler = new CollectionGridItemView.OnClickHandler() {
                @Override
                public void click(CollectionGridItemView collectionGridItemView,
                                  Component childComponent,
                                  ContentDatum data, int clickPosition) {
                    if (isClickable) {
                        appCMSPresenter.navigateToPersonDetailsPage(data.getPlayersData().getData().getPermalink(), "", false);

//                        itemView.setBackgroundColor(Color.parseColor("#4B0502"));

//                        Fights fights = moduleAPI.getContentData().get(clickPosition).getFights();
                        notifyDataSetChanged();
//                        viewCreator.createFightStateRecorsView(mContext, appCMSPresenter, moduleAPI, component, jsonValueKeyMap, fights);
                    }
                }

                @Override
                public void play(Component childComponent, ContentDatum data) {
                }
            };

        }

        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(itemView.getContext(),
                    itemView.getChild(i),
                    data,
                    jsonValueKeyMap,
                    onClickHandler,
                    componentViewType,
                    appCMSPresenter.getBrandPrimaryCtaColor(), appCMSPresenter, position);
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;
        TextView emptyView;

        public ViewHolder(TextView itemView) {
            super(itemView);
            this.emptyView = itemView;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }

    }


    @Override
    public void addReceiver(OnInternalEvent e) {
        receivers.add(e);

    }

    @Override
    public void sendEvent(InternalEvent<?> event) {
        for (OnInternalEvent internalEvent : receivers) {
            internalEvent.receiveEvent(event);
        }
    }

    @Override
    public void receiveEvent(InternalEvent<?> event) {
        adapterData.clear();

        notifyDataSetChanged();

    }

    @Override
    public void cancel(boolean cancel) {

    }

    @Override
    public String getModuleId() {
        return moduleId;
    }

    @Override
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }


    @Override
    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

}
