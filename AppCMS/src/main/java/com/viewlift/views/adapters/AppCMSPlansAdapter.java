package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.views.customviews.BaseView;
import com.viewlift.views.customviews.CollectionGridItemView;
import com.viewlift.views.customviews.ViewCreator;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

/*
 * Created by viewlift on 5/5/17.
 */

public class AppCMSPlansAdapter extends RecyclerView.Adapter<AppCMSPlansAdapter.ViewHolder>
        implements AppCMSBaseAdapter {
    private static final String TAG = "AppCMSViewAdapter";
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
    private int selectedColor;
    private boolean isClickable;
    boolean singlePlanViewShown = false;
    boolean subscribeViewShown = false;

    public AppCMSPlansAdapter(Context context,
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
        this.mContext = context;
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
        this.defaultWidth = defaultWidth;
        this.defaultHeight = defaultHeight;
        this.useMarginsAsPercentages = true;
        this.selectedColor = Color.parseColor(appCMSPresenter.getAppCMSMain().getBrand()
                .getCta().getPrimary().getBackgroundColor());
        this.isClickable = true;
        this.setHasStableIds(false);
        this.appCMSAndroidModules = appCMSAndroidModules;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (adapterData.size() == 1 && !singlePlanViewShown && !appCMSPresenter.isSinglePlanFeatureAvailable()) {
            TextView singlePlanView = new TextView(mContext);
            singlePlanView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            singlePlanView.setTextColor(appCMSPresenter.getGeneralTextColor());
            singlePlanView.setGravity(Gravity.CENTER);
            singlePlanView = setSinglePlan(singlePlanView);
            return new ViewHolder(singlePlanView);
        }
        if (adapterData.size() == 1 && singlePlanViewShown && subscribeViewShown && !appCMSPresenter.isSinglePlanFeatureAvailable()) {
            TextView singlePlanView = new TextView(mContext);
            singlePlanView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            singlePlanView.setTextColor(appCMSPresenter.getGeneralTextColor());
            singlePlanView.setGravity(Gravity.RIGHT);
            if (moduleAPI.getDescription() != null)
                singlePlanView.setText(moduleAPI.getDescription());
            return new ViewHolder(singlePlanView);
        }

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
                useRoundedCorners(), this.viewTypeKey);

        applyBgColorToChildren(view, selectedColor);
        FrameLayout.LayoutParams layPar = (FrameLayout.LayoutParams) view.getLayoutParams();
        layPar.setMargins(0, 20, 0, 0);
        view.setLayoutParams(layPar);
        return new ViewHolder(view);
    }

    TextView setSinglePlan(TextView textView) {
        double recurringPaymentAmount = adapterData.get(0).getPlanDetails()
                .get(0).getRecurringPaymentAmount();
        String formattedRecurringPaymentAmount = mContext.getString(R.string.cost_with_fraction,
                recurringPaymentAmount);
        if (recurringPaymentAmount - (int) recurringPaymentAmount == 0) {
            formattedRecurringPaymentAmount = mContext.getString(R.string.cost_without_fraction,
                    recurringPaymentAmount);
        }
        Currency currency = null;
        if (adapterData.get(0).getPlanDetails() != null &&
                !adapterData.get(0).getPlanDetails().isEmpty() &&
                adapterData.get(0).getPlanDetails().get(0) != null &&
                adapterData.get(0).getPlanDetails().get(0).getRecurringPaymentCurrencyCode() != null) {
            try {
                currency = Currency.getInstance(adapterData.get(0).getPlanDetails().get(0).getRecurringPaymentCurrencyCode());
            } catch (Exception e) {
                //Log.e(TAG, "Could not parse locale");
            }
        }
        StringBuilder planAmt = new StringBuilder();
        if (currency != null) {
            if (adapterData.get(0).getPlanDetails().get(0).getRecurringPaymentCurrencyCode().contains("INR"))
                planAmt.append(mContext.getResources().getString(R.string.rupee_symbol));
            else
                planAmt.append(currency.getSymbol());
        }
        planAmt.append(formattedRecurringPaymentAmount);
        StringBuilder planDuration = new StringBuilder();

        if (adapterData.get(0).getRenewalCycleType().contains(mContext.getString(R.string.app_cms_plan_renewal_cycle_type_monthly))) {
//            planDuration.append(" ");
            planDuration.append(mContext.getString(R.string.forward_slash));
//            planDuration.append(" ");
            planDuration.append(mContext.getString(R.string.plan_type_month));
        }
        if (adapterData.get(0).getRenewalCycleType().contains(mContext.getString(R.string.app_cms_plan_renewal_cycle_type_yearly))) {
//            planDuration.append(" ");
            planDuration.append(mContext.getString(R.string.forward_slash));
//            planDuration.append(" ");
            planDuration.append(mContext.getString(R.string.plan_type_year));
        }
        if (adapterData.get(0).getRenewalCycleType().contains(mContext.getString(R.string.app_cms_plan_renewal_cycle_type_daily))) {
//            planDuration.append(" ");
            planDuration.append(mContext.getString(R.string.forward_slash));
//            planDuration.append(" ");
            planDuration.append(mContext.getString(R.string.plan_type_day));
        }
        planDuration.append("*");
        StringBuilder plan = new StringBuilder();
        plan.append(planAmt.toString());
        plan.append(planDuration.toString());
        Spannable text = new SpannableString(plan.toString());
        float durationFont = 2.1f;
        float priceFont = 3.0f;
        if (BaseView.isTablet(mContext)) {
            durationFont = 3.0f;
            priceFont = 4.0f;
        }
        text.setSpan(new RelativeSizeSpan(priceFont), 0, planAmt.toString().length() /*+ 1*/, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        text.setSpan(new StyleSpan(Typeface.BOLD), 0, planAmt.toString().length() + 1,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.splashbackgroundColor)), 0, planAmt.toString().length()/* + 1*/,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        text.setSpan(new RelativeSizeSpan(durationFont), planAmt.toString().length()/* + 1*/, plan.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(text, TextView.BufferType.SPANNABLE);
        return textView;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (0 <= position && position <= adapterData.size()) {
            if (adapterData.size() == 1) {
                if (singlePlanViewShown && position == 1) {
                    createView(holder, position - 1);
                    subscribeViewShown = true;
                }
                singlePlanViewShown = true;
            }
        }
        if (0 <= position && position < adapterData.size()) {
            if (adapterData.size() != 1) {
                createView(holder, position);
            }

        }
    }

    void createView(ViewHolder holder, final int position) {
        if (holder.componentView != null) {
            for (int i = 0; i < holder.componentView.getNumberOfChildren(); i++) {
                if (holder.componentView.getChild(i) instanceof TextView) {
                    ((TextView) holder.componentView.getChild(i)).setText("");
                }
            }
            bindView(holder.componentView, adapterData.get(position), position);
            holder.componentView.setSelectable(true);
        }
    }

    private boolean useRoundedCorners() {
        return mContext.getString(R.string.app_cms_page_subscription_selectionplan_02_key).equals(componentViewType);
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


    @Override
    public int getItemCount() {
        if (adapterData != null && adapterData.size() != 0 && !appCMSPresenter.isSinglePlanFeatureAvailable()) {
            if (adapterData.size() == 1) {
                return 3;
            } else {
                return adapterData.size();
            }
        }
        return 0;
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
        notifyDataSetChanged();
        listView.setAdapter(this);
        listView.invalidate();
        notifyDataSetChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
    void bindView(CollectionGridItemView itemView,
                  final ContentDatum data, int position) throws IllegalArgumentException {
        if (onClickHandler == null) {
            if (viewTypeKey == AppCMSUIKeyType.PAGE_SUBSCRIPTION_SELECTPLAN_02_KEY) {
                onClickHandler = new CollectionGridItemView.OnClickHandler() {
                    @Override
                    public void click(CollectionGridItemView collectionGridItemView,
                                      Component childComponent,
                                      ContentDatum data, int clickPosition) {
                        if (isClickable) {
                            subcriptionPlanClick(collectionGridItemView, data);
                        }
                    }

                    @Override
                    public void play(Component childComponent, ContentDatum data) {
                        // NO-OP - Play is not implemented here
                    }
                };
            }
        }

        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(itemView.getContext(),
                    itemView.getChild(i),
                    data,
                    jsonValueKeyMap,
                    onClickHandler,
                    componentViewType,
                    appCMSPresenter.getBrandPrimaryCtaColor(),
                    appCMSPresenter,
                    position, null);
        }
    }

    void subcriptionPlanClick(CollectionGridItemView collectionGridItemView, ContentDatum data) {
        if (collectionGridItemView.isSelectable()) {
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

    public boolean isClickable() {
        return isClickable;
    }

    @Override
    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;
        TextView singlePlanView;

        public ViewHolder(TextView itemView) {
            super(itemView);
            this.singlePlanView = itemView;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }
    }


}
