package com.viewlift.tv.views.presenter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.viewlift.R;
import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.AppCMSUIKeyType;
import com.viewlift.models.data.appcms.ui.page.Component;
import com.viewlift.presenters.AppCMSPresenter;
import com.viewlift.tv.model.BrowseFragmentRowData;
import com.viewlift.tv.utility.Utils;
import com.viewlift.tv.views.customviews.HoverCard;

import java.util.List;
import java.util.Map;

/**
 * Created by nitin.tyagi on 6/29/2017.
 */

public class JumbotronPresenter extends CardPresenter {

    private static final String TAG = JumbotronPresenter.class.getCanonicalName();
    private final Component parentComponent;
    private Context mContext;
    private AppCMSPresenter mAppCMSPresenter;
    private boolean infoHover;


    public JumbotronPresenter(Context context, AppCMSPresenter appCMSPresenter, Component component,
                              Map<String, AppCMSUIKeyType> appCMSUIKeyTypeMap, boolean infoHover){
        super(context , appCMSPresenter, appCMSUIKeyTypeMap, infoHover);
        mContext = context;
        mAppCMSPresenter = appCMSPresenter;
        this.parentComponent = component;
        this.infoHover = infoHover;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
            //Log.d("Presenter" , " CardPresenter onCreateViewHolder******");
            final CustomFrameLayout frameLayout = new CustomFrameLayout(parent.getContext());
            FrameLayout.LayoutParams layoutParams;

            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT ,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            frameLayout.setLayoutParams(layoutParams);
            frameLayout.setFocusable(true);
          //  frameLayout.setBackgroundColor(ContextCompat.getColor(mContext , android.R.color.black));
        frameLayout.setBackgroundColor(Color.parseColor(mAppCMSPresenter.getAppBackgroundColor()));

        return new ViewHolder(frameLayout);
        }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
      //Log.d("Presenter" , " CardPresenter onBindViewHolder******");
        BrowseFragmentRowData rowData = (BrowseFragmentRowData)item;
        ContentDatum contentData = rowData.contentData;
        List<Component> componentList = rowData.uiComponentList;
        String blockName = rowData.blockName;
        CustomFrameLayout cardView = (CustomFrameLayout) viewHolder.view;
        createComponent(componentList , cardView , contentData);
        if(infoHover){
            ((HoverCard)cardView.hoverLayout).removeViews();
            ((HoverCard)cardView.hoverLayout).initViews();
             bindComponent(cardView , contentData , blockName, rowData.infoHover);
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    public void createComponent(List<Component> componentList , CustomFrameLayout parentLayout , ContentDatum contentData ){
        if(null != componentList && componentList.size() > 0) {
            for (Component component : componentList) {
                AppCMSUIKeyType componentType = mAppCMSPresenter.getJsonValueKeyMap().get(component.getType());
                if (componentType == null) {
                    componentType = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                }

                AppCMSUIKeyType componentKey = mAppCMSPresenter.getJsonValueKeyMap().get(component.getKey());
                if (componentKey == null) {
                    componentKey = AppCMSUIKeyType.PAGE_EMPTY_KEY;
                }

                switch (componentType) {
                    case PAGE_IMAGE_KEY:
                        ImageView imageView = new ImageView(parentLayout.getContext());
                        switch(componentKey){
                            case PAGE_CAROUSEL_IMAGE_KEY: {
                                Integer itemWidth = Integer.valueOf(component.getLayout().getTv().getWidth());
                                Integer itemHeight = Integer.valueOf(component.getLayout().getTv().getHeight());

                                FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(
                                        Utils.getViewXAxisAsPerScreen(mContext, itemWidth),
                                        Utils.getViewYAxisAsPerScreen(mContext, itemHeight));

                                imageView.setLayoutParams(parms);
                                imageView.setBackground(Utils.getTrayBorder(mContext, borderColor, component));
                                int gridImagePadding = Integer.valueOf(component.getLayout().getTv().getPadding());
                                imageView.setPadding(gridImagePadding, gridImagePadding, gridImagePadding, gridImagePadding);
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                Glide.with(mContext)
                                        .load(contentData.getGist().getVideoImageUrl())
                                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                                .error(ContextCompat.getDrawable(mContext, R.drawable.video_image_placeholder))
                                                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.video_image_placeholder)))
                                        .into(imageView);
                                parentLayout.addView(imageView);

                                if(null != parentLayout.hoverLayout) {
                                    FrameLayout.LayoutParams hoverParams = new FrameLayout.LayoutParams(
                                            Utils.getViewXAxisAsPerScreen(mContext, itemWidth - gridImagePadding*2),
                                            Utils.getViewYAxisAsPerScreen(mContext, itemHeight - gridImagePadding*2));

                                    hoverParams.setMargins(gridImagePadding, gridImagePadding, 0, 0);
                                    parentLayout.hoverLayout.setLayoutParams(hoverParams);
                                    ((HoverCard)parentLayout.hoverLayout).setCardHeight(itemHeight);
                                    ((HoverCard)parentLayout.hoverLayout).setCardWidth(itemWidth);
                                }

                                break;
                            }
                            /*case PAGE_VIDEO_HOVER_BACKGROUND_KEY: {
                                createComponentView(parentComponent, parentLayout);
                                break;
                            }*/
                        }
                   /* case PAGE_LABEL_KEY: {
                        createComponentView(parentComponent, parentLayout);
                        bindComponent(parentLayout, contentData, parentComponent.getBlockName(),
                                parentComponent.getSettings() != null && parentComponent.getSettings().isInfoHover());
                        break;
                    }*/
                }
            }
        }
    }
}
