package com.viewlift.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.api.Module;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.android.AppCMSAndroidModules;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.models.data.appcms.ui.page.Layout;
import com.viewlift.models.data.appcms.ui.page.Settings;
import com.viewlift.presenters.AppCMSPresenter;
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
    public static final int TYPE_HEADER = 2;
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


            return new ViewHolder(view);
        } else {
            TextView emptyView = new TextView(mContext);
            emptyView.setTextColor(appCMSPresenter.getGeneralTextColor());
            emptyView.setTextSize(16f);
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setText("See terms of use and privacy policy for more details");
            emptyView.setLinkTextColor(Color.BLUE);
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
            appCMSPresenter.makeTextViewLinks(emptyView, new String[]{
                    "terms of use", "privacy policy"}, new ClickableSpan[]{tosClick, privacyClick});
            return new ViewHolder(emptyView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < settings.getItems().size()) {
            return TYPE_ITEM;
        } else {
            return TYPE_HEADER;
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
