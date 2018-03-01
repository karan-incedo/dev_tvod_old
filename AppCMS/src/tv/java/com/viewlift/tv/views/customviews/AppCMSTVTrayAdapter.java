package com.viewlift.tv.views.customviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.activity.AppCmsHomeActivity;
import com.viewlift.views.customviews.InternalEvent;
import com.viewlift.views.customviews.OnInternalEvent;

import java.util.ArrayList;
import java.util.Collections;
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
    private static final int ITEM_TYPE_DATA = 10001;
    private static final int ITEM_TYPE_NO_DATA = 10002;
    private boolean isWatchlist;
    private boolean isHistory;
    private List<ContentDatum> adapterData;
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
        this.defaultAction = getDefaultAction(context, component);
        this.viewType = viewType;
        this.tvViewCreator = tvViewCreator;
        this.module = moduleAPI;
        this.viewTypeKey = jsonValueKeyMap.get(viewType);
        if (this.viewTypeKey == null) {
            this.viewTypeKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }
        this.isClickable = true;
        this.receivers = new ArrayList<>();

        if (this.adapterData == null) {
            this.adapterData = new ArrayList<>();
        }

        if (null != jsonValueKeyMap.get(viewType)) {
            switch (jsonValueKeyMap.get(viewType)) {
                case PAGE_HISTORY_MODULE_KEY:
                    this.isHistory = true;
                    break;

                case PAGE_WATCHLIST_MODULE_KEY:
                    this.isWatchlist = true;
                    break;
                default:
                    break;
            }
        }
        sortData();
    }


    public void setContentData(List<ContentDatum> adapterData) {
        this.adapterData = adapterData;
        sortData();
        notifyDataSetChanged();
    }

    private String getDefaultAction(Context context, Component component) {
        if (null != component.getItemClickAction()) {
            return component.getItemClickAction();
        }
        return context.getString(R.string.app_cms_action_videopage_key);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_DATA) {
            TVCollectionGridItemView collectionGridItemView;
            FrameLayout parentLayout = new FrameLayout(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            parentLayout.setLayoutParams(params);

            TVViewCreator.ComponentViewResult componentViewResult =
                    tvViewCreator.getComponentViewResult();
            collectionGridItemView = new TVCollectionGridItemView(
                    context,
                    this.parentLayout,
                    false,
                    component,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Utils.getFocusColor(context, appCMSPresenter));

            List<OnInternalEvent> onInternalEvents = new ArrayList<>();

            for (int i = 0; i < component.getComponents().size(); i++) {
                Component childComponent = component.getComponents().get(i);
                if (null != childComponent) {
                    tvViewCreator.createComponentView(context,
                            childComponent,
                            this.parentLayout,
                            module,
                            null,
                            childComponent.getSettings(),
                            jsonValueKeyMap,
                            appCMSPresenter,
                            false,
                            this.viewType,
                            false);

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
            }
            return new ViewHolder(collectionGridItemView);
        } else {

            RelativeLayout relativeLayout = new RelativeLayout(context);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            relativeLayout.setLayoutParams(layoutParams);
            TextView textView = new TextView(context);
            if (this.viewType.equalsIgnoreCase(context.getString(R.string.app_cms_page_watchlist_module_key))) {
                textView.setText(context.getString(R.string.no_data_in_watchlist_text));
            } else {
                textView.setText(context.getString(R.string.no_data_in_history_text));
            }
            textView.setGravity(Gravity.CENTER);
            Component component1 = new Component();
            component1.setFontFamily(context.getString(R.string.app_cms_page_font_family_key));
            component1.setFontWeight(context.getString(R.string.app_cms_page_font_semibold_key));
            textView.setTypeface(Utils.getTypeFace(context, jsonValueKeyMap, component1));
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            textView.setLayoutParams(layoutParams);
            relativeLayout.addView(textView);
            return new ViewHolder(relativeLayout);
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (0 <= position && adapterData != null && position < adapterData.size()) {
            bindView(holder.componentView, adapterData.get(position), position);
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
                            final ContentDatum data, int position) throws IllegalArgumentException {
        if (onClickHandler == null) {
            onClickHandler = new TVCollectionGridItemView.OnClickHandler() {
                @Override
                public void click(TVCollectionGridItemView collectionGridItemView,
                                  Component childComponent,
                                  ContentDatum data) {
                    if (isClickable) {
                        //Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());
                        String permalink = data.getGist().getPermalink();
                        String action = defaultAction;
                        String title = data.getGist().getTitle();
                        String hlsUrl = getHlsUrl(data);
                        String[] extraData = new String[4];
                        extraData[0] = permalink;
                        extraData[1] = hlsUrl;
                        extraData[2] = data.getGist().getId();
                        //Log.d(TAG, "Launching " + permalink + ": " + action);
                        List<String> relatedVideoIds = null;
                        if (data.getContentDetails() != null &&
                                data.getContentDetails().getRelatedVideoIds() != null) {
                            relatedVideoIds = data.getContentDetails().getRelatedVideoIds();
                        }
                        int currentPlayingIndex = -1;
                        if (relatedVideoIds == null) {
                            currentPlayingIndex = 0;
                        }

                        if (defaultAction.equalsIgnoreCase(context.getString(R.string.app_cms_action_watchvideo_key))) {
                            play(childComponent, data);
                        } else if (!appCMSPresenter.launchTVButtonSelectedAction(permalink,
                                action/*"lectureDetailPage"*/,
                                title,
                                extraData,
                                data,
                                false, -1, null)) {
                         /*   Log.e(TAG, "Could not launch action: " + " permalink: " + permalink
                                    + " action: " + action + " hlsUrl: " + hlsUrl);  */
                        }
                    }
                }

                @Override
                public void play(Component childComponent, ContentDatum data) {
                    if (isClickable) {
                        //Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());
                        appCMSPresenter.launchTVVideoPlayer(
                                data,
                                -1,
                                null,
                                data.getGist().getWatchedTime());
                    }
                }

                @Override
                public void delete(Component childComponent, ContentDatum data) {
                    //Log.d(TAG, "Deleting watchlist item: " + data.getGist().getTitle());
                    if (appCMSPresenter.isNetworkConnected()) {
                        appCMSPresenter.editWatchlist(data.getGist().getId(),
                                addToWatchlistResult -> {
                                    adapterData.remove(data);
                                    View view = null;
                                    try {
                                        view = ((View) itemView.getParent().getParent()).findViewById(R.id.appcms_removeall);
                                    } catch (Exception e) {
                                        if (context instanceof AppCmsHomeActivity) {
                                            view = ((AppCmsHomeActivity) context).findViewById(R.id.appcms_removeall);
                                        }
                                    }
                                    if (view != null) {
                                        view.setFocusable(adapterData.size() != 0);
                                        view.setVisibility(adapterData.size() != 0 ? View.VISIBLE : View.INVISIBLE);
                                    }
                                    notifyDataSetChanged();
                                }, false);
                    } else {
                        appCMSPresenter.openErrorDialog(data.getGist().getId(),
                                true,
                                appCMSAddToWatchlistResult -> {
                                    adapterData.remove(data);
                                    View view = null;
                                    try {
                                        view = ((View) itemView.getParent().getParent()).findViewById(R.id.appcms_removeall);
                                    } catch (Exception e) {
                                        if (context instanceof AppCmsHomeActivity) {
                                            view = ((AppCmsHomeActivity) context).findViewById(R.id.appcms_removeall);
                                        }
                                    }
                                    if (view != null) {
                                        view.setFocusable(adapterData.size() != 0);
                                        view.setVisibility(adapterData.size() != 0 ? View.VISIBLE : View.INVISIBLE);
                                    }
                                    notifyDataSetChanged();
                                });
                    }
                }
            };
        }

        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(itemView.getContext(),
                    itemView.getChild(i),
                    data,
                    jsonValueKeyMap,
                    onClickHandler,
                    viewTypeKey,
                    position);
        }
    }

    @Override
    public int getItemCount() {
        return adapterData != null && adapterData.size() > 0 ? adapterData.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        return adapterData != null && adapterData.size() > 0 ? ITEM_TYPE_DATA : ITEM_TYPE_NO_DATA;
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
    public void setModuleId(String moduleId) {

    }

    @Override
    public String getModuleId() {
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TVCollectionGridItemView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView instanceof TVCollectionGridItemView)
                this.componentView = (TVCollectionGridItemView) itemView;
        }
    }

    private void sortData() {
        if (adapterData != null) {
            if (isWatchlist) {
                Collections.sort(adapterData, (o1, o2)
                        -> Long.compare(o2.getAddedDate(), o1.getAddedDate()));
            } else if (isHistory) {
                Collections.sort(adapterData, (o1, o2)
                        -> Long.compare(o1.getUpdateDate(), o2.getUpdateDate()));
                Collections.reverse(adapterData);
            }
        }
    }
}
