package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.api.SubscriptionPlan;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSActionPresenter;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * Created by viewlift on 5/5/17.
 */

public class AppCMSViewAdapter extends RecyclerView.Adapter<AppCMSViewAdapter.ViewHolder>
        implements AppCMSBaseAdapter {
    private static final String TAG = "AppCMSViewAdapter";

    private final String episodicContentType;
    private final String fullLengthFeatureType;

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
    private String defaultAction;
    private AppCMSUIKeyType viewTypeKey;
    private boolean isSelected;
    private int unselectedColor;
    private int selectedColor;
    private boolean isClickable;
    private String videoAction;
    private String draggableVideoAction;
    private String showAction;
    private MotionEvent lastTouchDownEvent;
    private String watchVideoAction;
    private String watchTrailerAction;
    private String watchTrailerQuailifier;

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
                             String viewType,
                             AppCMSAndroidModules appCMSAndroidModules) {
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

        this.componentViewType = viewType;
        this.viewTypeKey = jsonValueKeyMap.get(componentViewType);
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
                    //Log.e(TAG, "Failed to parse double value for subscription price");
                }
            }
        }

        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.useMarginsAsPercentages = true;
        this.defaultAction = getDefaultAction(context);
        this.videoAction = getVideoAction(context);
        this.draggableVideoAction = getDraggableVideoAction(context);
        this.showAction = getShowAction(context);

        this.isSelected = false;
        this.unselectedColor = ContextCompat.getColor(context, android.R.color.white);
        this.selectedColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                .getGeneral().getBlockTitleColor());
        this.isClickable = true;

        this.setHasStableIds(false);

        this.watchVideoAction = context.getString(R.string.app_cms_action_watchvideo_key);
        this.watchTrailerAction = context.getString(R.string.app_cms_action_watchtrailervideo_key);
        this.watchTrailerQuailifier = context.getString(R.string.app_cms_action_qualifier_watchvideo_key);

        this.appCMSAndroidModules = appCMSAndroidModules;

        this.episodicContentType = context.getString(R.string.app_cms_episodic_key_type);
        this.fullLengthFeatureType = context.getString(R.string.app_cms_full_length_feature_key_type);

        sortPlansByPriceInDescendingOrder();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CollectionGridItemView view = viewCreator.createCollectionGridItemView(parent.getContext(),
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
                useRoundedCorners() ||
                        component.getSettings().isRoundedCorners());

        if ("AC SelectPlan 02".equals(componentViewType)) {
            applyBgColorToChildren(view, selectedColor);
        }

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY) {
            if (!"AC SelectPlan 02".equals(componentViewType)) {
                setBorder(view, unselectedColor);
            }

            view.setOnClickListener(v -> {
                if (!"AC SelectPlan 02".equals(componentViewType)) {
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
                }
            });
        }

        return new ViewHolder(view);
    }

    private boolean useRoundedCorners() {
        return "AC SelectPlan 02".equals(componentViewType) ||
                "AC ContinueWatching 01".equals(componentViewType);
    }

    private void applyBgColorToChildren(ViewGroup viewGroup, int bgColor) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                if (child instanceof CardView) {
                    ((CardView) child).setUseCompatPadding(true);
                    ((CardView) child).setPreventCornerOverlap(false);
                    ((CardView) child).setCardBackgroundColor(bgColor);
                } else {
                    child.setBackgroundColor(bgColor);
                }
                applyBgColorToChildren((ViewGroup) child, bgColor);
            }
        }
    }

    private void selectViewPlan(CollectionGridItemView collectionGridItemView) {
        collectionGridItemView.setSelectable(true);
        for (View collectionGridChild : collectionGridItemView.getViewsToUpdateOnClickEvent()) {
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
                if (!"AC SelectPlan 02".equals(componentViewType)) {
                    holder.componentView.setSelectable(true);
                    holder.componentView.performClick();
                }
            } else {
                //
            }

            if ("AC SelectPlan 02".equals(componentViewType)) {
                holder.componentView.setSelectable(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (adapterData != null ? adapterData.size() : 0);
    }

    @Override
    public void resetData(RecyclerView listView) {
        notifyDataSetChanged();
    }

    @Override
    public void updateData(RecyclerView listView, List<ContentDatum> contentData) {
        listView.setAdapter(null);
        adapterData = null;
        notifyDataSetChanged();
        adapterData = contentData;

        sortPlansByPriceInDescendingOrder();

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
                    //Log.e(TAG, "Failed to parse double value for subscription price");
                }
            }
        }

        notifyDataSetChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
    void bindView(CollectionGridItemView itemView,
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
                                //Log.d(TAG, "Initiating signup and subscription: " +
//                                        data.getIdentifier());

                                double price = data.getPlanDetails().get(0).getStrikeThroughPrice();
                                if (price == 0) {
                                    price = data.getPlanDetails().get(0).getRecurringPaymentAmount();
                                }

                                double discountedPrice = data.getPlanDetails().get(0).getRecurringPaymentAmount();

                                boolean upgradesAvailable = false;
                                for (ContentDatum plan : adapterData) {
                                    if (plan != null &&
                                            plan.getPlanDetails() != null &&
                                            !plan.getPlanDetails().isEmpty() &&
                                            ((plan.getPlanDetails().get(0).getStrikeThroughPrice() != 0 &&
                                                    price < plan.getPlanDetails().get(0).getStrikeThroughPrice()) ||
                                                    (plan.getPlanDetails().get(0).getRecurringPaymentAmount() != 0 &&
                                                            price < plan.getPlanDetails().get(0).getRecurringPaymentAmount()))) {
                                        upgradesAvailable = true;
                                    }
                                }

                                appCMSPresenter.initiateSignUpAndSubscription(data.getIdentifier(),
                                        data.getId(),
                                        data.getPlanDetails().get(0).getCountryCode(),
                                        data.getName(),
                                        price,
                                        discountedPrice,
                                        data.getPlanDetails().get(0).getRecurringPaymentCurrencyCode(),
                                        data.getPlanDetails().get(0).getCountryCode(),
                                        data.getRenewable(),
                                        data.getRenewalCycleType(),
                                        upgradesAvailable);
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
                                //Log.d(TAG, "Clicked on item: " + data.getGist().getTitle());
                                String permalink = data.getGist().getPermalink();
                                String action = videoAction;

                                if (childComponent != null && !TextUtils.isEmpty(childComponent.getAction())) {
                                    action = childComponent.getAction();
                                }
                                String title = data.getGist().getTitle();
                                String hlsUrl = getHlsUrl(data);

                                @SuppressWarnings("MismatchedReadAndWriteOfArray")
                                String[] extraData = new String[3];
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

                                String contentType = "";

                                if (data.getGist() != null && data.getGist().getContentType() != null) {
                                    contentType = data.getGist().getContentType();
                                }

//                                if (!appCMSPresenter.isAppSVOD()) { // FIXME: Change to use T-Builder.
//                                    action = draggableVideoAction;
//                                } else if (contentType.equals(episodicContentType)) {
//                                    action = showAction;
//                                } else if (contentType.equals(fullLengthFeatureType)) {
//                                    action = videoAction;
//                                }

                                if (contentType.equals(episodicContentType)) {
                                    action = showAction;
                                } else if (contentType.equals(fullLengthFeatureType)) {
                                    action = videoAction;
                                }

                                if (data.getGist() == null ||
                                        data.getGist().getContentType() == null) {
                                    if (!appCMSPresenter.launchVideoPlayer(data,
                                            data.getGist().getId(),
                                            currentPlayingIndex,
                                            relatedVideoIds,
                                            -1,
                                            action)) {
                                        //Log.e(TAG, "Could not launch action: " +
//                                                " permalink: " +
//                                                permalink +
//                                                " action: " +
//                                                action);
                                    }
                                } else {
                                    AppCMSActionPresenter actionPresenter = new AppCMSActionPresenter();
                                    actionPresenter.setAction(action);

                                    if (appCMSPresenter.getCurrentActivity().getResources()
                                            .getBoolean(R.bool.video_detail_page_plays_video) &&
                                            !showAction.equals(action)) {
                                        if (!appCMSPresenter.launchVideoPlayer(data,
                                                data.getGist().getId(),
                                                currentPlayingIndex,
                                                relatedVideoIds,
                                                -1,
                                                action)) {
                                            //Log.e(TAG, "Could not launch action: " +
//                                                " permalink: " +
//                                                permalink +
//                                                " action: " +
//                                                action);
                                        }
                                    } else {
                                        if (!appCMSPresenter.launchButtonSelectedAction(permalink,
                                                actionPresenter,
                                                title,
                                                null,
                                                null,
                                                false,
                                                currentPlayingIndex,
                                                relatedVideoIds)) {
                                            //Log.e(TAG, "Could not launch action: " +
//                                                " permalink: " +
//                                                permalink +
//                                                " action: " +
//                                                action);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void play(Component childComponent, ContentDatum data) {
                        if (isClickable) {
                            if (data.getGist() != null) {
                                //Log.d(TAG, "Playing item: " + data.getGist().getTitle());
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
                                        data.getGist().getId(),
                                        currentPlayingIndex,
                                        relatedVideoIds,
                                        -1,
                                        null)) {
                                    //Log.e(TAG, "Could not launch play action: " +
//                                            " filmId: " +
//                                            filmId +
//                                            " permaLink: " +
//                                            permaLink +
//                                            " title: " +
//                                            title);
                                }
                            }
                        }
                    }
                };
            }
        }

        if ("AC SelectPlan 02".equals(componentViewType)) {
            itemView.setOnClickListener(v -> onClickHandler.click(itemView, component, data));
        }

        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY) {
            //
        } else {
            itemView.setOnTouchListener((View v, MotionEvent event) -> {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    lastTouchDownEvent = event;
                }

                return false;
            });

            itemView.setOnClickListener(v -> {
                if (isClickable && data != null && data.getGist() != null) {
                    if (v instanceof CollectionGridItemView) {
                        try {
                            int eventX = (int) lastTouchDownEvent.getX();
                            int eventY = (int) lastTouchDownEvent.getY();
                            ViewGroup childContainer = ((CollectionGridItemView) v).getChildrenContainer();
                            int childrenCount = childContainer.getChildCount();
                            for (int i = 0; i < childrenCount; i++) {
                                View childView = childContainer.getChildAt(i);
                                if (childView instanceof Button) {
                                    int[] childLocation = new int[2];
                                    childView.getLocationOnScreen(childLocation);
                                    int childX = childLocation[0] - 8;
                                    int childY = childLocation[1] - 8;
                                    int childWidth = childView.getWidth() + 8;
                                    int childHeight = childView.getHeight() + 8;
                                    if (childX <= eventX && eventX <= childX + childWidth) {
                                        if (childY <= eventY && eventY <= childY + childHeight) {
                                            childView.performClick();
                                            return;
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            //
                        }
                    }

                    String permalink = data.getGist().getPermalink();
                    String title = data.getGist().getTitle();
                    String action = component.getTrayClickAction();

                    String contentType = "";

                    if (data.getGist() != null && data.getGist().getContentType() != null) {
                        contentType = data.getGist().getContentType();
                    }

//                    if (action != draggableVideoAction) { // FIXME: HARDCODED - should rely on T-Builder.
//                        if (contentType.equals(episodicContentType)) {
//                            action = showAction;
//                        } else if (contentType.equals(fullLengthFeatureType)) {
//                            action = videoAction;
//                        }
//                    } else {
//                        action = draggableVideoAction;
//                    }

                    if (contentType.equals(episodicContentType)) {
                        action = showAction;
                    } else if (contentType.equals(fullLengthFeatureType)) {
                        action = videoAction;
                    }

                    //Log.d(TAG, "Launching " + permalink + ":" + action);
                    List<String> relatedVideoIds = null;
                    if (data.getContentDetails() != null &&
                            data.getContentDetails().getRelatedVideoIds() != null) {
                        relatedVideoIds = data.getContentDetails().getRelatedVideoIds();
                    }
                    int currentPlayingIndex = -1;
                    if (relatedVideoIds == null) {
                        currentPlayingIndex = 0;
                    }

                    if (data.getGist() == null ||
                            data.getGist().getContentType() == null) {
                        if (!appCMSPresenter.launchVideoPlayer(data,
                                data.getGist().getId(),
                                currentPlayingIndex,
                                relatedVideoIds,
                                -1,
                                action)) {
                            //Log.e(TAG, "Could not launch action: " +
//                                    " permalink: " +
//                                    permalink +
//                                    " action: " +
//                                    action);
                        }
                    } else {
                        AppCMSActionPresenter actionPresenter = new AppCMSActionPresenter();
                        actionPresenter.setAction(action);

                        if (appCMSPresenter.getCurrentActivity().getResources().getBoolean(R.bool.use_draggable_panel)) {
                            actionPresenter.setExtraScreenType(AppCMSPresenter.ExtraScreenType.DRAGGABLE_PANEL);
                        }

                        if (appCMSPresenter.getCurrentActivity().getResources().getBoolean(R.bool.video_detail_page_plays_video) &&
                                !showAction.equals(action)) {
                            if (!appCMSPresenter.launchVideoPlayer(data,
                                    data.getGist().getId(),
                                    currentPlayingIndex,
                                    relatedVideoIds,
                                    -1,
                                    action)) {
                                //Log.e(TAG, "Could not launch action: " +
//                                    " permalink: " +
//                                    permalink +
//                                    " action: " +
//                                    action);
                            }
                        } else {
                            if (!appCMSPresenter.launchButtonSelectedAction(permalink,
                                    actionPresenter,
                                    title,
                                    null,
                                    null,
                                    false,
                                    currentPlayingIndex,
                                    relatedVideoIds)) {
                                //Log.e(TAG, "Could not launch action: " +
//                                    " permalink: " +
//                                    permalink +
//                                    " action: " +
//                                    action);
                            }
                        }
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
                    componentViewType,
                    Color.parseColor(appCMSPresenter.getAppTextColor()),
                    appCMSPresenter);
        }
    }

    @Override
    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    private String getDefaultAction(Context context) {
        return context.getString(R.string.app_cms_action_videopage_key);
    }

    private String getVideoAction(Context context) {
        return context.getString(R.string.app_cms_action_detailvideopage_key);
    }

    private String getDraggableVideoAction(Context context) {
        return context.getString(R.string.app_cms_action_draggablevideopage_key);
    }

    private String getShowAction(Context context) {
        return context.getString(R.string.app_cms_action_showvideopage_key);
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

    private void sortPlansByPriceInDescendingOrder() {
        if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_KEY && adapterData != null) {

            Collections.sort(adapterData,
                    (datum1, datum2) -> {
                        if (datum1.getPlanDetails().get(0).getStrikeThroughPrice() == 0 &&
                                datum2.getPlanDetails().get(0).getStrikeThroughPrice() == 0) {
                            return Double.compare(datum2.getPlanDetails().get(0)
                                    .getRecurringPaymentAmount(), datum1.getPlanDetails().get(0)
                                    .getRecurringPaymentAmount());
                        }
                        return Double.compare(datum2.getPlanDetails().get(0)
                                .getStrikeThroughPrice(), datum1.getPlanDetails().get(0)
                                .getStrikeThroughPrice());
                    });
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        int childCount = holder.componentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = holder.componentView.getChild(i);
            if (child instanceof ImageView) {
                Glide.with(child.getContext()).clear(child);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }
    }
}
