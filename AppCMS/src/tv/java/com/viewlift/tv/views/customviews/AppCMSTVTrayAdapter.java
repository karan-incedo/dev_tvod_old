package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anas.azeem on 9/7/2017.
 * Owned by ViewLift, NYC
 */

public class AppCMSTVTrayAdapter
        extends RecyclerView.Adapter<AppCMSTVTrayAdapter.ViewHolder>
        implements OnInternalEvent {

    private static final String TAG = AppCMSTVTrayAdapter.class.getCanonicalName();
    private final List<ContentDatum> adapterData;
    private final AppCMSPresenter appCMSPresenter;
    private final Map<String, AppCMSUIKeyType> jsonValueKeyMap;
    private final String viewType;
    private final TVViewCreator tvViewCreator;
    private final Context context;
    private final Module module;
    private final Layout parentLayout;
    private final Component component;
    private AppCMSUIKeyType viewTypeKey;
    protected String defaultAction;
    private TVCollectionGridItemView.OnClickHandler onClickHandler;
    private boolean isClickable;
    private List<OnInternalEvent> receivers;

    public AppCMSTVTrayAdapter(Context context,
                               List<ContentDatum> adapterData,
                               Component component,
                               Layout parentLayout,
                               AppCMSPresenter appCMSPresenter,
                               Map<String, AppCMSUIKeyType> jsonValueKeyMap,
                               String viewType,
                               TVViewCreator tvViewCreator,
                               Module moduleAPI) {
        this.context = context;
        this.adapterData = adapterData;
        this.component = component;
        this.parentLayout = parentLayout;
        this.appCMSPresenter = appCMSPresenter;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.defaultAction = getDefaultAction(context);
        this.viewType = viewType;
        this.tvViewCreator = tvViewCreator;
        this.module = moduleAPI;
        this.viewTypeKey = jsonValueKeyMap.get(viewType);
        if (this.viewTypeKey == null) {
            this.viewTypeKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }
        this.isClickable = true;
        this.receivers = new ArrayList<>();
    }

    private String getDefaultAction(Context context) {
        return context.getString(R.string.app_cms_action_videopage_key);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout parentLayout = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentLayout.setLayoutParams(params);

        TVViewCreator.ComponentViewResult componentViewResult =
                tvViewCreator.getComponentViewResult();
        TVCollectionGridItemView collectionGridItemView = new TVCollectionGridItemView(
                context,
                this.parentLayout,
                false,
                component,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Utils.getFocusColor(context, appCMSPresenter));


        List<OnInternalEvent> onInternalEvents = new ArrayList<>();

        for (int i = 0; i < component.getComponents().size(); i++) {
            Component childComponent = component.getComponents().get(i);
            tvViewCreator.createComponentView(context,
                    childComponent,
                    this.parentLayout,
                    module,
                    null,
                    childComponent.getSettings(),
                    jsonValueKeyMap,
                    appCMSPresenter,
                    false,
                    this.viewType);

            if (componentViewResult.onInternalEvent != null) {
                onInternalEvents.add(componentViewResult.onInternalEvent);
            }

            View componentView = componentViewResult.componentView;
            if (componentView != null) {
                TVCollectionGridItemView.ItemContainer itemContainer =
                        new TVCollectionGridItemView.ItemContainer.Builder()
                                .childView(componentView)
                                .component(childComponent)
                                .build();
                collectionGridItemView.addChild(itemContainer);
                collectionGridItemView.setComponentHasView(i, true);
                collectionGridItemView.setViewMarginsFromComponent(childComponent,
                        componentView,
                        collectionGridItemView.getLayout(),
                        collectionGridItemView.getChildrenContainer(),
                        jsonValueKeyMap,
                        false,
                        false,
                        this.viewType);
            } else {
                collectionGridItemView.setComponentHasView(i, false);
            }
        }
        return new ViewHolder(collectionGridItemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (0 <= position && position < adapterData.size()) {
            bindView(holder.componentView, adapterData.get(position));
        }
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    private String getHlsUrl(ContentDatum data) {
        if (data.getStreamingInfo() != null &&
                data.getStreamingInfo().getVideoAssets() != null &&
                data.getStreamingInfo().getVideoAssets().getHls() != null) {
            return data.getStreamingInfo().getVideoAssets().getHls();
        }
        return null;
    }

    protected void bindView(TVCollectionGridItemView itemView,
                            final ContentDatum data) throws IllegalArgumentException {
        if (onClickHandler == null) {
            onClickHandler = new TVCollectionGridItemView.OnClickHandler() {
                @Override
                public void click(TVCollectionGridItemView collectionGridItemView,
                                  Component childComponent,
                                  ContentDatum data) {
                    if (isClickable) {
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
                        List<String> relatedVideoIds = null;
                        if (data.getContentDetails() != null &&
                                data.getContentDetails().getRelatedVideoIds() != null) {
                            relatedVideoIds = data.getContentDetails().getRelatedVideoIds();
                        }
                        int currentPlayingIndex = -1;
                        if (relatedVideoIds == null) {
                            currentPlayingIndex = 0;
                        }
                        if (!appCMSPresenter.launchButtonSelectedAction(permalink,
                                action,
                                title,
                                extraData,
                                data,
                                false,
                                currentPlayingIndex,
                                relatedVideoIds)) {
                            Log.e(TAG, "Could not launch action: " + " permalink: " + permalink
                                    + " action: " + action + " hlsUrl: " + hlsUrl);
                        }
                    }
                }

                @Override
                public void play(Component childComponent, ContentDatum data) {
                }

                @Override
                public void delete(Component childComponent, ContentDatum data) {
                    Log.d(TAG, "Deleting watchlist item: " + data.getGist().getTitle());
                    appCMSPresenter.editWatchlist(data.getGist().getId(),
                            addToWatchlistResult -> {
                                adapterData.remove(data);
                                notifyDataSetChanged();
                            }, false);
                }
            };
        }

        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(itemView.getContext(),
                    itemView.getChild(i),
                    data,
                    jsonValueKeyMap,
                    onClickHandler,
                    viewTypeKey);
        }
    }

    @Override
    public int getItemCount() {
        return adapterData != null ? adapterData.size() : 0;
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TVCollectionGridItemView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (TVCollectionGridItemView) itemView;
        }
    }
}
