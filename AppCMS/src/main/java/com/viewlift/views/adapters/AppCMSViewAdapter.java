package com.viewlift.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.SubscriptionPlan;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    protected AppCMSUIKeyType viewTypeKey;
    protected boolean isSelected;
    protected int unselectedColor;
    protected int selectedColor;
    protected boolean isClickable;

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

        this.viewType = viewType;
        this.viewTypeKey = jsonValueKeyMap.get(viewType);
        if (this.viewTypeKey == null) {
            this.viewTypeKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
        }

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY) {
            if (appCMSPresenter.isUserLoggedIn()) {
                List<SubscriptionPlan> availableSubscriptionPlans =
                        appCMSPresenter.availablePlans();

                double subscriptionPrice = -1.0;

                try {
                    subscriptionPrice = Double.parseDouble(appCMSPresenter.getActiveSubscriptionPrice());
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse double value for subscription price");
                }

                if (subscriptionPrice >= 0.0) {
                    cullDataByAvailableUpgrades(availableSubscriptionPlans, subscriptionPrice);
                }
            }
        }

        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.useMarginsAsPercentages = true;
        this.defaultAction = getDefaultAction(context);

        this.isSelected = false;
        this.unselectedColor = ContextCompat.getColor(context, android.R.color.white);
        this.selectedColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                .getGeneral().getBlockTitleColor());
        this.isClickable = true;

        this.setHasStableIds(false);

        sortPlanPricesInDescendingOrder();
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

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY) {
            setBorder(view, unselectedColor);

            view.setOnClickListener(v -> {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View childView = parent.getChildAt(i);
                    setBorder(childView, unselectedColor);
                    if (childView instanceof CollectionGridItemView) {
                        deselectViewPlan((CollectionGridItemView) childView);
                    }
                }
                setBorder(v, selectedColor);
                if (v instanceof CollectionGridItemView) {
                    selectViewPlan((CollectionGridItemView) v);
                }
            });
        }

        return new ViewHolder(view);
    }

    private void selectViewPlan(CollectionGridItemView collectionGridItemView) {
        collectionGridItemView.setSelectable(true);
        for (View collectionGridChild : collectionGridItemView
                .getViewsToUpdateOnClickEvent()) {
            if (collectionGridChild instanceof Button) {
                Component childComponent = collectionGridItemView.matchComponentToView(collectionGridChild);
                ((TextView) collectionGridChild).setText(childComponent.getSelectedText());
                collectionGridChild.setBackgroundColor(selectedColor);
            }
        }
    }

    private void deselectViewPlan(CollectionGridItemView collectionGridItemView) {
        collectionGridItemView.setSelectable(false);
        for (View collectionGridChild : collectionGridItemView
                .getViewsToUpdateOnClickEvent()) {
            if (collectionGridChild instanceof Button) {
                Component childComponent = collectionGridItemView.matchComponentToView(collectionGridChild);
                ((TextView) collectionGridChild).setText(childComponent.getText());
                collectionGridChild.setBackgroundColor(ContextCompat.getColor(collectionGridItemView.getContext(),
                        R.color.disabledButtonColor));
            }
        }
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

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY) {
            int selectableIndex = -1;
            for (int i = 0; i < adapterData.size(); i++) {
                if (holder.componentView.isSelectable()) {
                    selectableIndex = i;
                }
            }

            if (selectableIndex == -1) {
                selectableIndex = 0;
            }

            if (selectableIndex == position) {
                holder.componentView.setSelectable(true);
                holder.componentView.performClick();
            } else {

            }
        }
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
        listView.setAdapter(null);
        adapterData = null;
        notifyDataSetChanged();
        adapterData = contentData;

        sortPlanPricesInDescendingOrder();

        notifyDataSetChanged();
        listView.setAdapter(this);
        listView.invalidate();

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY) {
            if (appCMSPresenter.isUserLoggedIn()) {
                List<SubscriptionPlan> availableSubscriptionPlans =
                        appCMSPresenter.availablePlans();

                double subscriptionPrice = -1.0;

                try {
                    subscriptionPrice = Double.parseDouble(appCMSPresenter.getActiveSubscriptionPrice());
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse double value for subscription price");
                }

                if (subscriptionPrice >= 0.0) {
                    cullDataByAvailableUpgrades(availableSubscriptionPlans, subscriptionPrice);
                }
            }
        }

        notifyDataSetChanged();
    }

    protected void bindView(CollectionGridItemView itemView,
                            final ContentDatum data) throws IllegalArgumentException {
        if (onClickHandler == null) {

            if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY) {
                onClickHandler = new CollectionGridItemView.OnClickHandler() {

                    @Override
                    public void click(CollectionGridItemView collectionGridItemView,
                                      Component childComponent,
                                      ContentDatum data) {
                        if (isClickable) {
                            if (collectionGridItemView.isSelectable()) {
                                Log.d(TAG, "Initiating signup and subscription: " +
                                    data.getIdentifier());
                                appCMSPresenter.initiateSignUpAndSubscription(data.getIdentifier(),
                                        data.getId(),
                                        data.getPlanDetails().get(0).getCountryCode(),
                                        data.getName(),
                                        data.getPlanDetails().get(0).getRecurringPaymentAmount(),
                                        data.getPlanDetails().get(0).getRecurringPaymentCurrencyCode(),
                                        data.getPlanDetails().get(0).getCountryCode(),
                                        data.getRenewable()
                                        );
                            } else {
                                collectionGridItemView.performClick();
                            }
                        }
                    }

                    @Override
                    public void play(Component childComponent, ContentDatum data) {
                        // NO-OP - Play is not implemented here
                    }
                };
            } else {
                onClickHandler = new CollectionGridItemView.OnClickHandler() {
                    @Override
                    public void click(CollectionGridItemView collectionGridItemView,
                                      Component childComponent,
                                      ContentDatum data) {
                        if (isClickable) {
                            if (data.getGist() != null) {
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
                    }

                    @Override
                    public void play(Component childComponent, ContentDatum data) {
                        if (isClickable) {
                            if (data.getGist() != null) {
                                Log.d(TAG, "Playing item: " + data.getGist().getTitle());
                                String filmId = data.getGist().getId();
                                String permaLink = data.getGist().getPermalink();
                                String title = data.getGist().getTitle();
                                List<String> relatedVideoIds = null;
                                if (data.getContentDetails() != null &&
                                        data.getContentDetails().getRelatedVideoIds() != null) {
                                    relatedVideoIds = data.getContentDetails().getRelatedVideoIds();
                                }
                                int currentPlayingIndex = -1;
                                if (relatedVideoIds == null) {
                                    currentPlayingIndex = 0;
                                }
                                if (!appCMSPresenter.launchVideoPlayer(data,
                                        currentPlayingIndex,
                                        relatedVideoIds,
                                        -1)) {
                                    Log.e(TAG, "Could not launch play action: " +
                                            " filmId: " +
                                            filmId +
                                            " permaLink: " +
                                            permaLink +
                                            " title: " +
                                            title);
                                }
                            }
                        }
                    }
                };
            }
        }

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY) {
            //
        } else {
            itemView.setOnClickListener(v -> {
                if (isClickable) {
                    String permalink = data.getGist().getPermalink();
                    String title = data.getGist().getTitle();
                    Log.d(TAG, "Launching " + permalink + ":" + defaultAction);
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
                            defaultAction,
                            title,
                            null,
                            null,
                            false,
                            currentPlayingIndex,
                            relatedVideoIds)) {
                        Log.e(TAG, "Could not launch action: " +
                                " permalink: " +
                                permalink +
                                " action: " +
                                defaultAction);
                    }
                }
            });
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

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
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

    private void setBorder(View itemView,
                           int color) {
        GradientDrawable planBorder = new GradientDrawable();
        planBorder.setShape(GradientDrawable.RECTANGLE);
        planBorder.setStroke(1, color);
        planBorder.setColor(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
        itemView.setBackground(planBorder);
    }

    private void sortPlanPricesInDescendingOrder() {
        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY && adapterData != null) {

            Collections.sort(adapterData,
                    (datum1, datum2) -> Double.compare(datum1.getPlanDetails().get(0)
                            .getRecurringPaymentAmount(), datum2.getPlanDetails().get(0)
                            .getRecurringPaymentAmount()));

            Collections.reverse(adapterData);
        }
    }

    private void cullDataByAvailableUpgrades(List<SubscriptionPlan> availableSubscriptionPlans,
                                             double existingSubscriptionPrice) {
        List<ContentDatum> updatedData = new ArrayList<>();
        for (ContentDatum contentDatum : adapterData) {
            if (availableSubscriptionPlans != null) {
                for (SubscriptionPlan subscriptionPlan : availableSubscriptionPlans) {
                    if (!TextUtils.isEmpty(contentDatum.getIdentifier()) &&
                            contentDatum.getIdentifier().equals(subscriptionPlan.getSku()) &&
                            (existingSubscriptionPrice < subscriptionPlan.getSubscriptionPrice())) {
                        updatedData.add(contentDatum);
                    }
                }
            } else if (contentDatum.getPlanDetails() != null &&
                    !contentDatum.getPlanDetails().isEmpty() &&
                    contentDatum.getPlanDetails().get(0) != null &&
                    existingSubscriptionPrice <
                            contentDatum.getPlanDetails().get(0).getRecurringPaymentAmount()) {
                updatedData.add(contentDatum);
            }
        }

        this.adapterData = updatedData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }
    }
}
