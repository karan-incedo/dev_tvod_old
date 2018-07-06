package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import java.util.List;
import java.util.Map;

/*
 * Created by viewlift on 5/5/17.
 */

public class AppCMSBenefitPlanPageAdapter extends RecyclerView.Adapter<AppCMSBenefitPlanPageAdapter.ViewHolder>
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
    private boolean isClickable;
    public static final int TYPE_FOOTER = 2;
    public static final int TYPE_ITEM = 1;

    public AppCMSBenefitPlanPageAdapter(Context context,
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
        this.settings = settings;
        this.appCMSPresenter = appCMSPresenter;
        this.parentLayout = parentLayout;
        this.useParentSize = useParentSize;
        this.component = component;
        this.jsonValueKeyMap = jsonValueKeyMap;
        this.moduleAPI = moduleAPI;

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
        appCMSPresenter.setSinglePlanFeatureAvailable(true);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
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
                    false, this.viewTypeKey);
            /*tablet landscape*/
            if (BaseView.isLandscape(mContext)) {
                FrameLayout.LayoutParams layPar = (FrameLayout.LayoutParams) view.getLayoutParams();
                layPar.setMargins(0, 25, 0, 0);
                view.setLayoutParams(layPar);
            } else
                /*tablet portrait*/
                if (BaseView.isTablet(mContext) && !BaseView.isLandscape(mContext)) {
                    FrameLayout.LayoutParams layPar = (FrameLayout.LayoutParams) view.getLayoutParams();
                    layPar.setMargins(0, 20, 0, 0);
                    view.setLayoutParams(layPar);
                } else
                    /*mobile*/
                    if (!BaseView.isTablet(mContext)) {
                        FrameLayout.LayoutParams layPar = (FrameLayout.LayoutParams) view.getLayoutParams();
                        layPar.setMargins(0, 45, 0, 0);
                        view.setLayoutParams(layPar);
                    }
            return new ViewHolder(view);
        } else {
            TextView termsView = new TextView(mContext);
            termsView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            termsView.setTextColor(appCMSPresenter.getGeneralTextColor());
            termsView.setTextSize(14f);
            termsView.setPadding(5, 35, 5, 15);
            termsView.setGravity(Gravity.CENTER);
            termsView.setText("See terms of use and privacy policy for more details");
            termsView.setLinkTextColor(ContextCompat.getColor(mContext, android.R.color.white));
            ClickableSpan tosClick = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    appCMSPresenter.navigatToTOSPage();
                }
            };
            ClickableSpan privacyClick = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    appCMSPresenter.navigateToPrivacyPolicy();
                }
            };
            appCMSPresenter.makeTextViewLinks(termsView, new String[]{
                    "terms of use", "privacy policy"}, new ClickableSpan[]{tosClick, privacyClick});
            FrameLayout.LayoutParams layPar = (FrameLayout.LayoutParams) termsView.getLayoutParams();
            layPar.setMargins(0, 0, 0, 8);
            termsView.setLayoutParams(layPar);
            return new ViewHolder(termsView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < settings.getItems().size()) {
            return TYPE_ITEM;
        } else {
            return TYPE_FOOTER;
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position < settings.getItems().size()) {
            for (int i = 0; i < holder.componentView.getNumberOfChildren(); i++) {
                if (holder.componentView.getChild(i) instanceof TextView) {
                    ((TextView) holder.componentView.getChild(i)).setText("");
                }
            }
            bindView(holder.componentView, null, position);
        }
    }


    @Override
    public int getItemCount() {
        if (settings.getItems() == null)
            return 0;
        return settings.getItems().size() + 1;
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


        for (int i = 0; i < itemView.getNumberOfChildren(); i++) {
            itemView.bindChild(itemView.getContext(),
                    itemView.getChild(i),
                    data,
                    jsonValueKeyMap,
                    onClickHandler,
                    componentViewType,
                    appCMSPresenter.getBrandPrimaryCtaColor(),
                    appCMSPresenter,
                    position, settings);
        }
    }


    public boolean isClickable() {
        return isClickable;
    }

    @Override
    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }


    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.componentView != null) {
            int childCount = holder.componentView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = holder.componentView.getChild(i);
                if (child instanceof ImageView) {
                    Glide.with(child.getContext()).clear(child);
                }
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CollectionGridItemView componentView;
        TextView termsView;

        public ViewHolder(TextView itemView) {
            super(itemView);
            this.termsView = itemView;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            this.componentView = (CollectionGridItemView) itemView;
        }
    }


}
